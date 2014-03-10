package joostbastings.freval.util;

import java.util.LinkedList;

public class Fragment extends LinkedList<Node<NodeInfo>> {
	
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 4961034538622618596L;
	
	/**
	 * Indicates if EVERY node in this fragment matches, or not
	 */
	private boolean matches;

	/**
	 * @return the matches
	 */
	public boolean matches() {
		return matches;
	}

	/**
	 * @param matches the matches to set
	 */
	public void setMatches(boolean matches) {
		this.matches = matches;
	}
	
	@Override
	/**
	 * Shallow copy of this fragment
	 * e.g. references to the original elements are the same
	 * but matches variable is unique for the cloned fragment
	 * as is the _list_ of elements itself
	 */
	public Fragment clone() {
		Fragment f = new Fragment();
		f.setMatches(matches);
		f.addAll(this);
		return f;
	}
	
	/**
	 * String representation
	 */
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("( ");
		for(Node<NodeInfo> node : this) {
			sb.append(node.getUserObject().getNodeID());
			sb.append(" ");
		}
		sb.append(")");
		return sb.toString();
	}
	
}
