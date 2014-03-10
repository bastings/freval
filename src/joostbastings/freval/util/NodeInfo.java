package joostbastings.freval.util;

public class NodeInfo {

	/**
	 * Construct a node
	 */
	public NodeInfo() {	
		isTerminal = false;
		isDeleted = false;
		isQuote = false;
		matches = false;
	}

	/**
	 * Construct nodeinfo with name
	 */
	public NodeInfo(String name) {
		this(name, false);
	}	

	/**
	 * Construct nodeinfo with name and terminal-specifier
	 */
	public NodeInfo(String name, boolean terminal) {
		this.isTerminal = terminal;
		this.setName(name);
	}

	/**
	 * Name of the node (Label or word)
	 */
	private String name;

	/**
	 * Index of left-most terminal that this node covers
	 */
	private int start = -1;

	/**
	 * Index of right-most terminal that this node covers
	 */
	private int end = -1;

	/**
	 * The ID of the word in this node (index in tree)
	 */
	private int wordID;

	/**
	 * This ID of this node in the tree
	 */
	private int nodeID;

	/**
	 * Tree ID, the line on which this tree was read
	 * only to be used in the ROOT node
	 */
	private int treeID;
	
	/**
	 * True if this a terminal node
	 */
	private boolean isTerminal;

	/**
	 * True if this node was deleted
	 */
	private boolean isDeleted;

	/**
	 * Is this node a Quote-node or not
	 */
	private boolean isQuote;

	/**
	 * Store if the node matches with a node in another tree
	 */
	private boolean matches;

	/**
	 * Store if the node bracket matches with a node in another tree
	 * E.g. covers the same word span, but the label might be different
	 */
	private boolean bracketMatches;

	/**
	 * Get the tree id for a Root Node
	 * @return the tree ID, which is meant to be the line number
	 * this tree was read from
	 */
	public int getTreeID() {
		return treeID;
	}

	/**
	 * Set the treeID, the line number this tree was read from
	 * @param treeID
	 */
	public void setTreeID(int treeID) {
		this.treeID = treeID;
	}

	/**
	 * Tell if the node matches
	 * @return true of the node is a match
	 */
	public boolean matches() {
		return matches;
	}

	/**
	 * Indicate that this node matches another one (or not)
	 * @param value
	 */
	public void setMatches(boolean value) {
		matches = value;
	}

	/**
	 * Tell if the node bracket matches (label does not matter)
	 * @return true if match
	 */
	public boolean bracketMatches() {
		return bracketMatches;
	}

	/**
	 * Indicate that this node bracket matches another one
	 * @param bracketMatches
	 */
	public void setBracketMatches(boolean bracketMatches) {
		this.bracketMatches = bracketMatches;
	}	
	
	/**
	 * Is this a terminal or not
	 * @return isTerminal
	 */
	public boolean isTerminal() {
		return isTerminal;
	}

	/**
	 * Indicate that this node is a terminal node (or not)
	 * @param value
	 */
	public void setTerminal(boolean value) {
		isTerminal = value;
	}

	/**
	 * Is this node was marked deleted
	 * @return deleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}	

	/**
	 * Set the node to deleted status
	 * @param value
	 */
	public void setDeleted(boolean value) {
		isDeleted = value;
	}

	/**
	 * Is this a quote or not
	 * @return
	 */
	public boolean isQuote() {
		return isQuote;
	}

	/**
	 * Indicate that this is a Quote-node (or not)
	 * @param value
	 */
	public void setQuote(boolean value) {
		isQuote = value;
	}

	/**
	 * Get the word ID
	 * @return word id
	 */
	public int getWordID() {
		return wordID;
	}

	/**
	 * Set the word ID
	 * @param value
	 */
	public void setWordID(int value) {
		wordID = value;
	}

	/**
	 * Get the node ID
	 * @return node id
	 */
	public int getNodeID() {
		return nodeID;
	}

	/**
	 * Set the node ID
	 * @param value
	 */
	public void setNodeID(int value) {
		nodeID = value;
	}	

	/**
	 * Return the index (word ID) of the first 
	 * terminal that this node covers
	 * @param value
	 */
	public int getStart() {
		return start;
	}	

	/**
	 * Set the start index (word ID) of this node
	 * defining the first terminal this node covers
	 * @param value
	 */
	public void setStart(int value) {
		start = value;
	}

	/**
	 * Return the index (word ID) of the last 
	 * terminal that this node covers
	 * @param value
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Set the end index (word ID) of this node
	 * defining the last terminal this node covers
	 * @param value
	 */
	public void setEnd(int value) {
		end = value;
	}

	/**
	 * Gets the name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name
	 */
	public void setName(String value) {
		name = value;
	}
	
	/**
	 * String representation
	 */
	public String toString() {
		String s = "";
		if (isDeleted) s += "[DELETED] ";		
		if (isQuote) s += "[QUOTE] ";
		if (isTerminal) s += "[WID=" + getWordID() + "]";
		s = s + getName();	
		return "("+start + ", " + s + ", " + end + ")" + 
			(matches() ? "" : " [NOMATCH]");

	}
	
	/**
	 * Make a copy of this info
	 * @return a copy / clone of this node info
	 */
	public NodeInfo clone() {
		NodeInfo info = new NodeInfo();
		info.setStart(start);
		info.setEnd(end);
		info.isDeleted = isDeleted;
		info.isQuote = isQuote;
		info.setName(name);
		info.setWordID(wordID);
		info.setTreeID(treeID);
		info.setTerminal(isTerminal);
		info.setMatches(matches);
		return info;
	}

}
