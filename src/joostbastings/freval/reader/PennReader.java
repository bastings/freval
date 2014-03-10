package joostbastings.freval.reader;

import java.io.*;
import java.util.*;

import joostbastings.freval.Settings;
import joostbastings.freval.util.*;

/**
 * This class reads in trees in Penn-format, 
 * e.g. <tt>(TOP (LABEL terminal) (LABEL terminal))</tt>
 * The internal representation for the tree is a {@link DirectedGraph} JGraphT directed graph
 */
public class PennReader {

	private BufferedReader br;
	private int line = 0;
	
	/**
	 * The reader gets a name so messages that it prints
	 * make more sense (i.e. it knows if it is a 'Gold' reader
	 * or a 'Test' reader.
	 */
	private String name;

	/**
	 * Opens a stream with the specified filePath
	 * @param filePath
	 * @throws FileNotFoundException if {@link #openStream(String)} does so
	 */
	public PennReader(String filePath, String readerName) throws FileNotFoundException {
		br = openStream(filePath);
		name = readerName;
	}

	/**
	 * Opens a stream to the file specified by its filePath
	 * @param filePath
	 * @return stream to the file
	 * @throws FileNotFoundException
	 */
	private static BufferedReader openStream(String filePath) 
	throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(filePath))));
		return br;
	}

	/**
	 * Closes the stream
	 */
	protected void finalize() throws Throwable {
		br.close();
	}

	/**
	 * Reads a line form the tree-file and parses it
	 * @return Graph representing the tree
	 * @throws IOException
	 */
	public Node<NodeInfo> nextTree() throws IOException {
		
		line++; // starts at 0, so first line is 1
		String s = br.readLine();
		
		// we assume we are done if we encounter 
		// a line with whitespace, or if there are 
		// no more bytes
		if (s == null) {
			return null;
		} else if (s.isEmpty()) {
			return null;
		}
		
		// everything ok, we have a line to parse
		if (Settings.debug > 1) {
			//System.err.println("----------------------------------------");
			System.err.println(name + ": Reading line "+line);
		}
		Node<NodeInfo> tree = parse(s);
		tree.getUserObject().setTreeID(line);
		return tree;
	}
	
	/**
	 * Extract the next tree from the tree file
	 * @return Graph of the tree
	 */
	public Node<NodeInfo> parse(String s) 
	throws IOException {
		
		// read in a serialized tree character by character
		StringReader sr = new StringReader(s);

		// variable used in processing the sentence
		StringBuffer label = new StringBuffer();
		StringBuffer word  = new StringBuffer();

		// this holds nodes still being processed
		// e.g. they do not yet know where they end,
		// children are still being added
		Stack<Node<NodeInfo>> stack = 
			new Stack<Node<NodeInfo>>();
		
		// the tree
		Node<NodeInfo> tree = new Node<NodeInfo>(
				new NodeInfo("FREVALROOT"));
		stack.add(tree);

		int wordID = 0;
		int nodeID = 0;

		for(char c = (char) sr.read(); c != (char) -1; ) {

			// skip whitespace
			if (Character.isWhitespace(c)) {
				c = (char) sr.read();				
				continue;
			} else if (c == '(') { // open bracket			

				// collect label characters
				c = (char) sr.read();
				while (!Predicates.isTerminator(c)) {
					label.append(c);
					c = (char) sr.read();
				}

				// find terminal */
				if (Character.isWhitespace(c)) {

					// skip all further whitespace
					do {
						c = (char) sr.read();
					}
					while(Character.isWhitespace(c));

					// collect word characters
					while(!Predicates.isTerminator(c)) {
						word.append(c);
						c = (char) sr.read();
					}

					// output debug level 2 info
					if (Settings.debug > 4) {
						System.err.println(
								String.format("label=%s, word=%s, wid=%d",
										label, word, wordID));
					}

					// pre-terminal and terminal
					if (c == ')') { 
						
						// pre-terminal
						NodeInfo ni = new NodeInfo(label.toString());
						ni.setNodeID(nodeID);
						nodeID++;
						Node<NodeInfo> node = new Node<NodeInfo>(ni);

						// terminal
						NodeInfo tni = new NodeInfo(word.toString(), true);					
						tni.setTerminal(true);				
						tni.setNodeID(nodeID);
						nodeID++;
						tni.setWordID(wordID);	
						wordID++; // id for next terminal
						Node<NodeInfo> terminal = new Node<NodeInfo>(tni);
						
						// add terminal to node
						node.add(terminal);
						
						// add node to tree
						stack.lastElement().add(node);

						c = (char) sr.read();
						word = new StringBuffer();
						label = new StringBuffer();
						continue;
					} else if (c != '(') { // error
						throw new IOException(
						"More than two elements in a bracket");
					}
				}

				// otherwise non-terminal node
				NodeInfo ni = new NodeInfo(label.toString());
				ni.setNodeID(nodeID);
				nodeID++;

				// check if this node is root
				if (tree == null) {
					// create a new tree
					tree = new Node<NodeInfo>(ni);
				    stack.push(tree);
				} else { // not root, create node and add to parent
					Node<NodeInfo> node = 
						new Node<NodeInfo>(ni);
					stack.lastElement().add(node);
					stack.push(node); // add this node to the stack						
				}
				label = new StringBuffer();
				
			} else if (c == ')') // close bracket
			{
				if (stack.isEmpty()) {
					// note: this error was not there in evalb
					throw new IOException("Too many closing brackets!");
				} else {
					stack.pop();
					c = (char) sr.read();
				}
			} else // error
			{
				throw new IOException("Reading sentence");
			}
		}

		// whole string was read
		// only the root node should be in the stack now
		if (stack.size() != 1) {
			throw new IOException(
			"Bracketing is unbalanced (too many open brackets)");
		}
		
		return tree;
	}
}
