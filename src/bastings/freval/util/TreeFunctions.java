package bastings.freval.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import bastings.freval.Settings;

public class TreeFunctions {

	/**
	 * Returns true if the specified label is a 
	 * label indicated for deletion by {@link Settings#deleteLabels}
	 * @param label
	 * @return if this is a delete label
	 */
	public static boolean isDeleteLabel(String label) {

		for (String dl : Settings.deleteLabels) {
			if (label.equals(dl)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if the specified labels are equal as defined by the
	 * settings' equivalentLabels
	 * @see {@link Settings#equivalentLabels}
	 * @param label1
	 * @param label2
	 * @return true iff labels are equal
	 */
	public static boolean areEqualLabels(String label1, String label2) {

		if (label1.equals(label2)) {
			return true;
		}

		for (String[] equal : Settings.equivalentLabels) {
			if ((label1.equals(equal[0]) && label2.equals(equal[1])) ||
					(label1.equals(equal[1]) && label2.equals(equal[0])) ) {
				return true;
			}
		}

		return false;
	}	

	/**
	 * Returns whether the specified label is to be 
	 * deleted when computing the length of the sentence
	 * (the yield of the tree)
	 * @param label
	 * @return is this a delete label or not
	 */
	public static boolean isDeleteLabelForLength(String label) {

		for (String dll : Settings.deleteLabelsForLength) {
			if (label.equals(dll)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns whether the specified label and word make a Quote Term together
	 * @param label node label
	 * @param word the terminal below the node of the specified label
	 * @return whether this is a quote term
	 */
	public static boolean isQuoteTerm(String label, String word) {

		for(String ql : Settings.quoteLabels) {
			if (label.equals(ql)) {
				if (word.equals("'") 
						|| word.equals("\"") 
						|| word.equals("/")) {
					return true;
				}
			}
		}

		return false;
	}	

	/**
	 * Returns the number of leafs not counting the leafs
	 * where the parent has a label that is positive for
	 * isDeleteLabelForLength
	 * @return
	 */
	static public int getOfficialSentenceLength(Node<NodeInfo> root) {
		if (root == null) return 0;
		Enumeration<Node<NodeInfo>> en = root.depthFirstEnumeration();
		int count = 0;

		while ( en.hasMoreElements() ) {
			Node<NodeInfo> node = en.nextElement();
			NodeInfo info = node.getUserObject();
			if (info.isTerminal() && !TreeFunctions.isDeleteLabelForLength(
					node.getParent().getUserObject().
					getName())) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Returns the number of leafs with parents that have NOT been deleted
	 * @return
	 */
	static public int getRealLeafCount(Node<NodeInfo> root) {
		if (root == null) return 0;
		Enumeration<Node<NodeInfo>> en = root.depthFirstEnumeration();
		int count = 0;
		while ( en.hasMoreElements() ) {
			Node<NodeInfo> node = en.nextElement();
			if (node.isLeaf()) {
				if (!node.getParent().getUserObject().isDeleted()) {
					count++;
				}
			}
		}
		return count;
	}	

	/**
	 * Compares the leafs of 2 trees
	 * Assume: the amount of leafs are the same
	 * @return the first pair of leafs that mismatch, as "(a|b)" or null if no
	 * mismatch was found
	 */
	public static String firstLeafMismatch(
			Node<NodeInfo> goldTree, Node<NodeInfo> testTree) {

		// paranoid check to be sure the amount of leafs are the same
		// yields cannot be identical if leaf count differs, so
		// return unknown mismatch, because if we continue
		// we are not sure to find a mismatch
		if (getRealLeafCount(goldTree) != getRealLeafCount(testTree)) {
			return "(?????|?????)";
		}

		// check for word unmatch
		Enumeration<Node<NodeInfo>> goldEnum = goldTree.depthFirstEnumeration();
		Enumeration<Node<NodeInfo>> testEnum = testTree.depthFirstEnumeration();

		// for each leaf in goldTree that is not deleted
		while (goldEnum.hasMoreElements()) {

			Node<NodeInfo> gn = goldEnum.nextElement();
			if (gn.isLeaf() && !gn.getParent().getUserObject().isDeleted()) {

				// find the next leaf in the test tree
				// to compare the current gold leaf with
				// and stop looping after it was found
				while ( testEnum.hasMoreElements()) {
					Node<NodeInfo> tn = testEnum.nextElement();
					if (tn.isLeaf() && 
							!tn.getParent().getUserObject().isDeleted()) {
						if(!gn.getUserObject().getName().equals(
								tn.getUserObject().getName())) {
							return String.format("(%s|%s)",
									gn.getUserObject().getName(),
									tn.getUserObject().getName());	
						}
						break; // break out of test leaf search
					}
				}
			}
		} // end while

		// if we reach here, all the leafs matched (no mismatch)
		return null;
	}

	/**
	 * Sets nodes to isDeleted() when the Settings indicate they should be;
	 * Also sets nodes to isQuote() when the Settings indicate so;
	 * 
	 * <b>Note: this function modifies labels if indicated by Settings
	 * (Freval.adjustLabels)</b>
	 * 
	 * <b>Note: nodes that do not cover any word span (anymore) are deleted,
	 * that is, a node with word span <x, x> is marked for deletion</b>
* TODO 
	 * 
	 * @param tree
	 */
	public static void identifyDeleteAndQuoteNodes(Node<NodeInfo> tree) {

		// get a depth-first enumeration
		Enumeration<Node<NodeInfo>> en = tree.depthFirstEnumeration();

		while (en.hasMoreElements()) {

			Node<NodeInfo> node = en.nextElement();

			if (node.isRoot()) { // root
				continue; // no need to process our own root element
			}

			if (!node.isLeaf()) { // non-terminal

				String label = node.getUserObject().getName();

				// delete nodes covering nothing
				//if (node.getUserObject().getStart() == 
				//	node.getUserObject().getEnd()) {
				//	node.getUserObject().setDeleted(true);
				//}
				// FIXME delete those somewhere else

				// modify label (e.g. "NP-SUBJ" becomes "NP")
				// Note: in ParseEval/evalb labels are modified before
				// they are matched with deleteLabels, we do the same
				node.getUserObject().setName(simplifyLabel(label));				

				// identify delete label
				if (isDeleteLabel(label)) {
					node.getUserObject().setDeleted(true);
				}

			} else { // leaf / terminal / word

				String word  = node.getUserObject().getName();
				String parentLabel = node.getParent().getUserObject().getName();
				
				// identify Quote-node and terminal
				if (isQuoteTerm(parentLabel, word)) {
					// Note: we only indicate a quote at the parent node
					node.getParent().getUserObject().setQuote(true);
				}

				// if pre-terminal parent deleted, also delete the terminal
				if(isDeleteLabel(parentLabel)) {
					node.getUserObject().setDeleted(true);
				}
			}
		}

	}

	/**
	 * Identify nodes with empty word spans (start word index== end word index)
	 * Nodes found get the isDeleted marker
	 */
	public static void identifyEmptySpanNodes(Node<NodeInfo> tree) {
		
		Enumeration<Node<NodeInfo>> en = tree.depthFirstEnumeration();
		
		while(en.hasMoreElements()) {
			
			Node<NodeInfo> node = en.nextElement();
			if (node.getUserObject().getStart() == node.getUserObject()
					.getEnd()) {
				node.getUserObject().setDeleted(true);
				if (Settings.debug > 4) {
					System.err.println(String.format(
							"  EMPTY-SPAN-NODE start=%d, end=%d, node=%s", 
							node.getUserObject().getStart(), 
							node.getUserObject().getEnd(), 
							node.getUserObject()));
				}
			}
	
		}
		
	}

	/**
	 * Returns the part of the node label before a possible - (dash)
	 * e.g. "NP-SUBJ" becomes "NP"
	 * <b>Note: hard-coded exception for "-NONE-", which is returned as-is</b>
	 * @param label
	 * @return simplified label
	 */
	public static String simplifyLabel(String label) {
		if ((!label.equals("-NONE-")) && 
				(Settings.getBoolean("adjustLabels"))) {
			return label.split("[-=]", 2)[0];
		}
		else {
			return label;
		}		
	}

	/**
	 * Prints a tree with every node on a single line, indented
	 * to reflect the level of the node in the tree
	 * @param stream 
	 * @param tree
	 */
	public static void printIndentedTree(PrintStream stream, Node<NodeInfo> tree) {

		// create an enumeration in pre-order
		Enumeration<Node<NodeInfo>> en = tree.preorderEnumeration();

		//System.err.println(name + ": Tree parsed as:");
		while (en.hasMoreElements()) {
			Node<NodeInfo> n = en.nextElement();
			stream.print("  ");
			stream.println(n);
		}

	}

	/**
	 * Fixes quotes, which means if a quote-node was deleted in the gold tree,
	 * but not in the test tree, the node is reinserted into gold.
	 * And vice versa.
	 * @param goldTree
	 * @param testTree
	 */
	public static void fixQuotes(Node<NodeInfo> goldTree, 
			Node<NodeInfo> testTree) {

		// get a depth-first enumeration
		Enumeration<Node<NodeInfo>> goldEn = goldTree.depthFirstEnumeration();

		// the number of undeleted, non-quote terminals that we have seen
		// consistent with "wid" in evalb
		int realGoldTerminalCount = 0;

		// iterate over quote-nodes in the gold tree
		while (goldEn.hasMoreElements()) {

			Node<NodeInfo> goldNode = goldEn.nextElement();

			// skip over non-quote nodes, but count the actual
			// terminals we have seen so far in the gold enumeration
			if (!goldNode.getUserObject().isQuote()) {
				if (goldNode.isLeaf() &&
						!goldNode.getParent().getUserObject().isDeleted()
						&& !goldNode.getParent().getUserObject().isQuote()) {
					realGoldTerminalCount++;
				}
				continue;
			}

			// get a depth-first enumeration of test nodes
			Enumeration<Node<NodeInfo>> testEn =
				testTree.depthFirstEnumeration();	

			int realTestTerminalCount = 0;			

			while (testEn.hasMoreElements()) {

				Node<NodeInfo> testNode = testEn.nextElement();

				// skip over non-quote nodes, but count the actual
				// terminals we have seen so far in the test enumeration
				if (!testNode.getUserObject().isQuote()) {
					if (testNode.isLeaf() &&
							!testNode.getParent().getUserObject().isDeleted()
							&& !testNode.getParent().getUserObject().isQuote()) {
						realTestTerminalCount++; 
					}		
					continue;
				}

				// if both nodes follow the same terminal
				// but have different labels

				if (realGoldTerminalCount == realTestTerminalCount &&
						!goldNode.getUserObject().getName().equals(
								testNode.getUserObject().getName())) {

					// check if goldNode is deleted while testNode is not
					// if so, UNdelete goldNode
					if (goldNode.getUserObject().isDeleted() &&
							!testNode.getUserObject().isDeleted()) {
						goldNode.getUserObject().setDeleted(false);
						goldNode.getFirstChild().getUserObject().
						setDeleted(false);
						System.err.println(String.format(
								"Gold: [fixQuotes]: restored %s (wid=%d) " +
								"counterpart was: %s (wid=%d)",
								goldNode.getUserObject(), realGoldTerminalCount, 
								testNode.getUserObject(), realTestTerminalCount));				
					}

					// vice versa
					if (!goldNode.getUserObject().isDeleted() && 
							testNode.getUserObject().isDeleted()) {
						testNode.getUserObject().setDeleted(false);
						testNode.getFirstChild().getUserObject().
						setDeleted(false);
						System.err.println(String.format(
								"Test: [fixQuotes]: restored %s (wid=%d) " +
								"counterpart was: %s (wid=%d)",
								testNode.getUserObject(), realTestTerminalCount,
								goldNode.getUserObject(), realGoldTerminalCount));					
					}	
				}
			}
		}
	}

	/**
	 * Returns the specified tree without the nodes that had
	 * an isDeleted marker in their user object
	 * 
	 * <b>Note: Children of deleted nodes remain and are moved up
	 * to take the place of their parent; except for pre-terminals which
	 * are deleted together with their terminal.</b>
	 * 
	 * @returns tree without deleted nodes
	 */
	public static Node<NodeInfo> getWithoutDeleted(Node<NodeInfo> tree) {

		Node<NodeInfo> newTree = tree.getRoot().clone();
		newTree.setUserObject(tree.getUserObject());

		Enumeration<Node<NodeInfo>> children = tree.children();

		while(children.hasMoreElements()) {
			Node<NodeInfo> child = children.nextElement();
			addNormalToRoot(child, newTree);
		}

		return newTree;
	}
	
	/**
	 * Adds the node (first argument) to the second node (second argument, parent)
	 * if the node is not marked as isDeleted. If the node is marked as such,
	 * its children are added to the parent instead. 
	 * 
	 * @param node
	 * @param parent
	 */
	public static void addNormalToRoot(Node<NodeInfo> node, 
			Node<NodeInfo> parent) {

		if (!node.getUserObject().isDeleted()) {
			Node<NodeInfo> newNode = node.clone();
			newNode.setUserObject(node.getUserObject());
			parent.add(newNode);
			parent = newNode; // change parent to the new node
		}

		// parent is either the node's parent or the node itself
		// i.e. if the node was deleted, we just skip it
		// and add its children to the parent and not the node
		Enumeration<Node<NodeInfo>> children = node.children();
		while(children.hasMoreElements()) {
			addNormalToRoot(children.nextElement(), parent);
		}		

	}

	/**
	 * Removes all leafs from the specified tree
	 * @param tree
	 */
	public static void removeLeafs(Node<NodeInfo> tree) {

		Enumeration<Node<NodeInfo>> en = tree.preorderEnumeration();

		// remember what nodes to delete
		List<Node<NodeInfo>> deleteList = new ArrayList<Node<NodeInfo>>();

		// find leafs
		while(en.hasMoreElements()) {
			Node<NodeInfo> node = en.nextElement();
			if (node.isLeaf()) {
				deleteList.add(node);
			}
		}

		// delete every node in the list
		for( Node<NodeInfo> node : deleteList) {
			node.removeFromParent();
		}
	}	

	/**
	 * Find out which nodes in the Gold tree have a match in the Test tree
	 * Each of the matching nodes gets 'true' when matches() is called
	 * on their NodeInfo
	 * @param goldTree
	 * @param testTree
	 */
	public static void findMatchingNodes(Node<NodeInfo> goldTree,
			Node<NodeInfo> testTree) {

		Enumeration<Node<NodeInfo>> goldEnum = goldTree.preorderEnumeration();

		// remember which test nodes we have seen do match
//		int testTreeNodeCount = 0;
//		Enumeration<Node<NodeInfo>> bmTestEnum = testTree.preorderEnumeration();
//		while (bmTestEnum.hasMoreElements()) testTreeNodeCount++;
//		int[] testBracketMatch = new int[testTreeNodeCount]; 
		
		// for each gold node
		while(goldEnum.hasMoreElements()) {

			Node<NodeInfo> goldNode = goldEnum.nextElement();
			NodeInfo goldInfo = goldNode.getUserObject();

			Enumeration<Node<NodeInfo>> testEnum = 
				testTree.preorderEnumeration();

			// for each test node
			while(testEnum.hasMoreElements()) {

				Node<NodeInfo> testNode = testEnum.nextElement();
				NodeInfo testInfo = testNode.getUserObject();

				// compare the two nodes
				// do they cover the same terminals?
				if ((testInfo.matches() == false) && // only match a node once
						goldInfo.getStart() == testInfo.getStart() &&
						goldInfo.getEnd() == testInfo.getEnd()) {
					
					// for debug purposes we want to remember if we ever had
					// a bracket match for a node
					goldInfo.setBracketMatches(true);
					testInfo.setBracketMatches(true);
					
					// if we don't care about the label,
					// or if it matches, we have a hit
					if (!Settings.getBoolean("labeled") || areEqualLabels(
							goldInfo.getName(), testInfo.getName()) ) {
						goldInfo.setMatches(true);
						testInfo.setMatches(true);
						break;
					} else { // no match because of label 
						if (Settings.debug > 0) { // evalb/legacy debug output
							System.err.println(String.format(
									"  LABEL[%d-%d]: %s", 
									goldInfo.getStart(), 
									goldInfo.getEnd() - 1, // strange but evalb does this
									goldInfo.getName()));
						}
					}
				}

			} // while test
			
			// legacy debug info: gold bracket without test bracket
			if (Settings.debug > 1 && !goldInfo.bracketMatches() && !goldInfo.matches()) { 
				System.err.println(String.format(
						"  BRACKET[%d-%d]: %s", 
						goldInfo.getStart(), 
						goldInfo.getEnd() - 1,
						goldInfo.getName()));
			}			
			
		} // while gold
		
		// legacy debug info
		// test brackets without gold brackets (EXTRA)
		Enumeration<Node<NodeInfo>> testEnum = testTree.preorderEnumeration();
		while(testEnum.hasMoreElements()) {
			Node<NodeInfo> testNode = testEnum.nextElement();
			NodeInfo testInfo = testNode.getUserObject();
			if (Settings.debug > 1 && !testInfo.matches() && !testInfo.bracketMatches()) {
				System.err.println(String.format(
						"  EXTRA[%d-%d]: %s", 
						testInfo.getStart(), 
						testInfo.getEnd() - 1,
						testInfo.getName()));
			}
		}
		
	} // end findMatchingNodes


	/**
	 * Sets start and end variables for nodes in a tree
	 * 
	 * Start indicates the first terminal this node covers MINUS 1
	 * End indicates the last terminal this node covers
	 * e.g. covering terminal 1 means start 0 and end 1
	 * 
	 * Start and end values make easy comparison possible between nodes
	 * 
	 * @param tree
	 */
	public static void setStartEnd(Node<NodeInfo> tree) {

		// make the all the terminals have an evalb-style word id
		// i.e. starting with 0 at the first terminal,
		// and only IDs for terminals we will evaluate / not deleted
		resetWordIDs(tree);

		// reset start and end for every node
		setStartEndNode(tree);

	}

	private static Pair<Integer, Integer> setStartEndNode(Node<NodeInfo> node) {

		// terminals have start=wordid and end wordid+1
		// we also store those values in the terminal
		if(node.isLeaf() && node.getUserObject().isTerminal()) {
			node.getUserObject().setStart(node.getUserObject().getWordID());
			node.getUserObject().setEnd(node.getUserObject().getWordID()+1);
			return new Pair<Integer, Integer>(
					node.getUserObject().getStart(),
					node.getUserObject().getEnd());
		}

		int start = -1;
		int end = -1;

		Enumeration<Node<NodeInfo>> children = node.children();

		while(children.hasMoreElements()) {
			Node<NodeInfo> child = children.nextElement();
			Pair<Integer, Integer> result = setStartEndNode(child);
			
			// only update if not yet set
			if (start == -1) {
				start = result.getFirst();
			}
			// it is possible that there is a wordless constituent
			// as a second child, and thus it has no word index
			// in that case, keep the word index we already have 
			// (e.g. from the first child)
			if (result.getSecond() != -1) {
				end = result.getSecond();
			}
		}

		// set start and end for this node
		node.getUserObject().setStart(start);
		node.getUserObject().setEnd(end);

		// return the result (to the parent, probably)
		return new Pair<Integer, Integer>(start, end);

	}

	/**
	 * Sets consecutive word IDs (starting from 0) to all leafs (=words)
	 * @param tree
	 */
	public static void resetWordIDs(Node<NodeInfo> tree) {
		Enumeration<Node<NodeInfo>> en = tree.depthFirstEnumeration();

		int wid = 0;

		while(en.hasMoreElements()) {

			Node<NodeInfo> node = en.nextElement();

			if (node.isLeaf() && !node.getUserObject().isDeleted()
					&& node.getUserObject().isTerminal()) {
				// set word id - first word gets wid=0
				node.getUserObject().setWordID(wid);
				wid++;
			}

		}
	}

	/**
	 * Sets Node IDs (starting from 0) to all Nodes in pre-order fashion
	 * @param tree
	 */
	public static void resetNodeIDs(Node<NodeInfo> tree) {

		Enumeration<Node<NodeInfo>> en = tree.preorderEnumeration();

		int nid = 0;

		while(en.hasMoreElements()) {
			en.nextElement().getUserObject().setNodeID(nid);
			nid++;
		}
		
	}	

	/**
	 * Calculates the number of crossing brackets
	 * e.g. number of cases when neither bracket is contained in the other
	 * @param tree
	 * @return
	 */
	public static int getCrossing(Node<NodeInfo> goldTree, 
			Node<NodeInfo> testTree) {

		int crossing = 0;

		Enumeration<Node<NodeInfo>> testEn = testTree.preorderEnumeration();

		while(testEn.hasMoreElements()) {

			Node<NodeInfo> tn = testEn.nextElement();
			NodeInfo ti = tn.getUserObject();

			Enumeration<Node<NodeInfo>> goldEn = goldTree.preorderEnumeration();

			while(goldEn.hasMoreElements()) {

				Node<NodeInfo> gn = goldEn.nextElement();
				NodeInfo gi = gn.getUserObject();

				if ((gi.getStart() < ti.getStart() &&
						gi.getEnd()   > ti.getStart() &&
						gi.getEnd()   < ti.getEnd()) ||
						(gi.getStart() > ti.getStart() &&
								gi.getStart() < ti.getEnd() &&
								gi.getEnd()   > ti.getEnd())) {

					crossing++;

					if (Settings.debug > 1) {
						System.err.println(String.format("  CROSSING[%d-%d]: ",
								gi.getStart(),
								gi.getEnd()-1));
					}		

					break;

				}

			}


		}

		return crossing;

	}

	/**
	 * Get the number of correctly tagged terminals
	 * <b>Note: we assume there are no deleted terminals in the tree
	 * All terminals are evaluated here.</b>
	 * @param goldTree
	 * @param testTree
	 * @return number of correct tags
	 */
	public static int getCorrectTagCount(Node<NodeInfo> goldTree,
			Node<NodeInfo> testTree) {

		int correctTag = 0;

		Enumeration<Node<NodeInfo>> goldEn = goldTree.preorderEnumeration();
		Enumeration<Node<NodeInfo>> testEn = testTree.preorderEnumeration();

		// ASSUME: both have the same number of terminals
		while(goldEn.hasMoreElements()) {

			// skip over non-terminal nodes
			Node<NodeInfo> gn = goldEn.nextElement();
			if (!gn.getUserObject().isTerminal()) {
				continue;
			}

			// now we have in gn a gold terminal
			// find the corresponding terminal in test
			Node<NodeInfo> tn = testEn.nextElement();

			while(!tn.getUserObject().isTerminal() && 
					testEn.hasMoreElements()) {
				tn = testEn.nextElement();
			}

			if(areEqualLabels(
					gn.getParent().getUserObject().getName(), 
					tn.getParent().getUserObject().getName())) {
				correctTag++;
			}
		}

		return correctTag;
	}

}
