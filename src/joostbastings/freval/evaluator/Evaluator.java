package joostbastings.freval.evaluator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import joostbastings.freval.Settings;
import joostbastings.freval.util.*;

/**
 * Calculates the Freval-score given 2 trees
 */
public class Evaluator {

	public static EvaluationInfo evaluate(
			Node<NodeInfo> goldTree, Node<NodeInfo> testTree) {

		// calc corrected sentence length
		// i.e. the length without terminals that have a parent
		// that has a delete-label for length
		final int sentenceLength = 
			TreeFunctions.getOfficialSentenceLength(goldTree);

		// get the tree ID ("sentence number") from the gold root
		int treeID = goldTree.getUserObject().getTreeID();

		// check if the testTree is not null
		if (testTree == null) {
			return new SkipEvaluationInfo(treeID, sentenceLength);
		}

		// identify delete nodes, identify quote nodes and quote terminals
		TreeFunctions.identifyDeleteAndQuoteNodes(goldTree);
		TreeFunctions.identifyDeleteAndQuoteNodes(testTree);

		// get real word counts (e.g. we do NOT count deleted words)
		int goldWordCount = TreeFunctions.getRealLeafCount(goldTree);
		int testWordCount = TreeFunctions.getRealLeafCount(testTree);

		// check for sentence length unmatch
		// we count the number of terminals that were not deleted
		if (goldWordCount != testWordCount) {

			// reintroduce deleted quotes to try to make lengths equal
			TreeFunctions.fixQuotes(goldTree, testTree);

			// if still length mismatch after quote fixing, return error
			goldWordCount = TreeFunctions.getRealLeafCount(goldTree);
			testWordCount = TreeFunctions.getRealLeafCount(testTree);			
			if (goldWordCount != testWordCount) {
				return new ErrorEvaluationInfo(treeID, sentenceLength, 
						String.format("Length unmatch (%d|%d)", 
								TreeFunctions.getRealLeafCount(goldTree), 
								TreeFunctions.getRealLeafCount(testTree)));
			}
		}

		// check for words unmatch
		String result = TreeFunctions.firstLeafMismatch(goldTree, testTree);
		if (result != null) {			
			return new ErrorEvaluationInfo(treeID, sentenceLength, 
					String.format("Words unmatch %s", result));			
		}

		// remove from the tree all nodes marked as isDeleted
		// children stay and are moved up!	
		goldTree = TreeFunctions.getWithoutDeleted(goldTree);
		testTree = TreeFunctions.getWithoutDeleted(testTree);	

		// set start and end for every node
		TreeFunctions.setStartEnd(goldTree);
		TreeFunctions.setStartEnd(testTree);
		
		// identify nodes with empty spans (start wordid == end wordid)
		TreeFunctions.identifyEmptySpanNodes(goldTree);
		TreeFunctions.identifyEmptySpanNodes(testTree);
		
		// possibly delete the empty span nodes identified above
		goldTree = TreeFunctions.getWithoutDeleted(goldTree);
		testTree = TreeFunctions.getWithoutDeleted(testTree);		
		
		// calculate correct tags (before we remove the leafs!)
		int correctTag = TreeFunctions.getCorrectTagCount(goldTree, testTree);

		// remove leafs (terminals), they are not part of evaluation
		// remove pre-terminals (POS), they are also not part of evaluation
		TreeFunctions.removeLeafs(goldTree);
		TreeFunctions.removeLeafs(goldTree);
		TreeFunctions.removeLeafs(testTree);
		TreeFunctions.removeLeafs(testTree);

		// give every node an ID, useful to print Fragments (which is next)
		TreeFunctions.resetNodeIDs(goldTree);
		TreeFunctions.resetNodeIDs(testTree);

		// set the "matches" variable for each node
		// so we know which ones match
		TreeFunctions.findMatchingNodes(goldTree, testTree);

		// print the tree -  debug output level 2
		if (Settings.debug > 1) {
			System.err.println("Gold tree (as evaluated):");
			TreeFunctions.printIndentedTree(System.err, goldTree);
			System.err.println("Test tree (as evaluated):");
			TreeFunctions.printIndentedTree(System.err, testTree);
		}		
		
		// create initial fragments
		List<Fragment> goldFragments = fragmentListFromTree(goldTree);
		List<Fragment> testFragments = fragmentListFromTree(testTree);

		// run fragment generator, and keep track of counts
		
		DefaultEvaluationInfo evalInfo = new DefaultEvaluationInfo(treeID, 
				sentenceLength);

		// get number of crossing brackets in TEST tree
		int crossing = TreeFunctions.getCrossing(goldTree, testTree);

		evalInfo.setCrossing(crossing);
		evalInfo.setCorrectTag(correctTag);
		evalInfo.setGoldWordCount(goldWordCount);

		boolean isGold = true;

		calculate(goldFragments, evalInfo, isGold);
		calculate(testFragments, evalInfo, !isGold);

		// return the evaluation result
		return evalInfo;

	}

	/**
	 * Calculate the evaluation result
	 * @param list
	 * @param evalInfo
	 */
	private static void calculate(List<Fragment> fragments, 
			DefaultEvaluationInfo evalInfo, boolean isGold) {

		for(Fragment f : fragments) {

			// add the fragment to the test/gold fragment count
			// and add matching fragments to the match count for this sentence			
			int size = f.size();

			if (isGold) { // gold fragment
				evalInfo.incrementGold(size);
				if( f.matches()) {
					evalInfo.incrementMatched(size);
				}
			} else { // test fragment
				evalInfo.incrementTest(size);
			}
			
			// if this is the largest fragment we want to generate,
			// do not extend it any further
			if (Settings.getInteger("fragmentSizeLimit") != 0 &&
					Settings.getInteger("fragmentSizeLimit") == size) {
				continue;
			}

			// generate new fragments (with size N+1)
			// from current fragment (with size N)
			List<Fragment> newFragments = extend(f);

			// recursively calculate for those new fragments
			// before we continue with the current list of fragments
			calculate(newFragments, evalInfo, isGold);

		}
		
	}

	/**
	 * Extend a fragment to create one or more fragments 1 node larger
	 * We either extend by 
	 * 
	 * 	(1) adding a child of the deepest node in the
	 *      fragment, or
	 *  
	 * 	(2) by adding a node on the same level as the deepest, right-most, node,
	 *      where the node added must be to the right of that right-most deepest node 
	 *      in the tree
	 *      
	 *  
	 *  <b>ASSUME: pre-order sorting of the Fragment.
	 *  The result will then keep that sorting.</b>
	 * 
	 * @param f
	 * @return
	 */
	private static List<Fragment> extend(Fragment f) {

		List<Fragment> newFragments = new ArrayList<Fragment>();

		/*
		 * extension type 1
		 * 
		 * for each node having the max level in the fragment, 
		 * add a child
		 */

		// get the max level and the right-most max level node (for type 2 ext.)
		int maxLevel = -1;
		Node<NodeInfo> rmmlNode = null; // right-most max-level node
		int rmmlNodeIndex = -1;
		
		// FIXME changed to: level >= maxLevel, was: >
		for(int i = 0; i < f.size(); i++) {
			
			int level = f.get(i).getLevel();
			if (level >= maxLevel) {
				maxLevel = level;
				rmmlNode = f.get(i);
				rmmlNodeIndex = i;
			}
		}

		// for each max level node
		for(int i = 0; i < f.size(); i++) {

			Node<NodeInfo> node = f.get(i);

			if (node.getLevel() == maxLevel) {

				// extend with child(s)
				Enumeration<Node<NodeInfo>> children = node.children();

				while(children.hasMoreElements()) {
					
					Node<NodeInfo> child = children.nextElement();
					Fragment newF = f.clone();
					
					// if the added node does not match, the whole fragment 
					// also does not
					if (f.matches()) {
						if (!child.getUserObject().matches()) {
							newF.setMatches(false);
						}
					}
					
					newF.add(i + 1, child); // add AFTER parent in fragment
					newFragments.add(newF);

					// level 4 debug - fragment output
					if(Settings.debug > 3) {
						System.out.println(String.format(
								"fragment treeID=%d type=1 size=%d maxlevelnode=%d(%s) " +
								"added=%d(%s) list=%s",
								newF.getFirst().getRoot().getUserObject().getTreeID(),
								newF.size(), 
								node.getUserObject().getNodeID(),
								node.getUserObject().getName(), 
								child.getUserObject().getNodeID(),
								child.getUserObject().getName(), 
								newF)
						);						
					}
				}
			}
		}

		/* 
		 * extension type 2
		 * 
		 * for the RIGHT MOST max-level node, add nodes *to the right of it*
		 * that have the *same* level (and are connected to the fragment)
		 * 
		 * Note that the added node can be a sibling of the max-level node
		 * on the left, but also a node in a different branch but on the
		 * same level
		 */
		
		// this extension does not work if the maxlevel node is 'root' 
		// of the fragment
		// because it has no parent (so return the result of type 1)
		if(f.size() == 1) {
			return newFragments;
		}
		
		// for each node
		for(int i = 0; i < f.size(); i++) {
			
			Node<NodeInfo> node = f.get(i);
			
			// which is on max level -1 (so parents of possible extensions)
			if(node.getLevel() == maxLevel - 1) {
			
				// iterate over the children (potential extensions to the frag!)
				Enumeration<Node<NodeInfo>> en = node.children();
				
				while(en.hasMoreElements()) {
					
					Node<NodeInfo> child = en.nextElement();
					
					// only add in this situation:
					// the child we add is on the same level as the
					// right-most max-level node,
					// and it is to the right of that node (higher child NodeID)
					if (child.getUserObject().getNodeID() > 
					rmmlNode.getUserObject().getNodeID()) {

						// copy the fragment, and add the new node
						Fragment newF = f.clone();
						
						// if the added node does not match, the whole fragment 
						// also does not
						if (f.matches()) {
							if (!child.getUserObject().matches()) {
								newF.setMatches(false);
							}
						}
							
						// add either 
						//   (1) if sibling of rmmlNode: AFTER rmmlNode
						//   or else (2) AFTER parent (=i) in fragment
						newF.add(Math.max(i, rmmlNodeIndex) + 1, child);
						newFragments.add(newF);

						// level 4 debug - fragment output
						if(Settings.debug > 3) {
							System.out.println(String.format(
									"fragment treeID=%d type=2 size=%d " +
									"rmmlnode=%d(%s) added=%d(%s) list=%s",
									newF.getFirst().getRoot().getUserObject().getTreeID(),
									newF.size(), 
									rmmlNode.getUserObject().getNodeID(), 
									rmmlNode.getUserObject().getName(), 
									child.getUserObject().getNodeID(), 
									child.getUserObject().getName(), 
									newF)
							);						
						}
					}					
					
				}
			}
		}
		
		return newFragments;

	}

	/**
	 * Initialize a list of fragments from a tree
	 * @param tree
	 * @return
	 */
	private static List<Fragment> fragmentListFromTree(Node<NodeInfo> tree) {

		List<Fragment> list = new ArrayList<Fragment>();

		Enumeration<Node<NodeInfo>> en = tree.preorderEnumeration();

		while(en.hasMoreElements()) {

			Node<NodeInfo> node = en.nextElement();

			// skip our own root element
			if (node.isRoot()) {
				continue;
			}

			Fragment f = new Fragment();
			f.setMatches(node.getUserObject().matches());
			f.add(node);
			list.add(f);
		}

		return list;

	}

}
