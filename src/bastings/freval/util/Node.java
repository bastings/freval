package bastings.freval.util;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This Node class is a generic extension of {@link DefaultMutableTreeNode}
 * (so it implements {@link TreeNode})
 * which makes sure previously returned Object types are now of type E
 * so they do not need to be casted to their correct type anymore
 * 
 * It also returns Enumerations with type Node in contrast to the
 * original Enumeration without a type.
 * 
 */
public class Node<E> extends DefaultMutableTreeNode {

	/**
	 * Version UID for serialization
	 */
	private static final long serialVersionUID = 4355061655930820227L;

	/**
	 * Default constructor, calls parent
	 */
	public Node() {
		super();
	}
	
	/** 
	 * Constructor with user object
	 */
	public Node(Object o) {
		super(o);
	}
	
	@SuppressWarnings("unchecked")	
	/**
	 * Returns the parent as a Node object
	 * @return parent
	 */
	public Node<E> getParent() {
		return (Node<E>) super.getParent();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * Returns the user object using the generic type of this class
	 * @return user object
	 */
	public E getUserObject() {
		return (E) super.getUserObject();
	}

	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getRoot() {
		return (Node<E>) super.getRoot();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getFirstChild() {
		return (Node<E>) super.getFirstChild();
	}		

	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getLastChild() {
		return (Node<E>) super.getLastChild();
	}	
	
	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getFirstLeaf() {
		return (Node<E>) super.getFirstLeaf();
	}		

	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getLastLeaf() {
		return (Node<E>) super.getLastLeaf();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getNextLeaf() {
		return (Node<E>) super.getNextLeaf();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getPreviousLeaf() {
		return (Node<E>) super.getPreviousLeaf();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getNextNode() {
		return (Node<E>) super.getNextNode();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getPreviousNode() {
		return (Node<E>) super.getPreviousNode();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getNextSibling() {
		return (Node<E>) super.getNextSibling();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Node<E> getPreviousSibling() {
		return (Node<E>) super.getPreviousSibling();
	}	
	
	@SuppressWarnings("unchecked")
	@Override	
	public Enumeration<Node<E>> children() {
		return (Enumeration<Node<E>>) super.children();
	}	
	
	@SuppressWarnings("unchecked")
	@Override	
	public Enumeration<Node<E>> pathFromAncestorEnumeration(
			TreeNode ancestor) {
		return (Enumeration<Node<E>>) super.
			pathFromAncestorEnumeration(ancestor);
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Enumeration<Node<E>> postorderEnumeration() {
		return (Enumeration<Node<E>>) super.postorderEnumeration();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Enumeration<Node<E>> preorderEnumeration() {
		return (Enumeration<Node<E>>) super.preorderEnumeration();
	}	
	
	@SuppressWarnings("unchecked")
	@Override	
	public Enumeration<Node<E>> depthFirstEnumeration() {
		return (Enumeration<Node<E>>) super.depthFirstEnumeration();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public Enumeration<Node<E>> breadthFirstEnumeration() {
		return (Enumeration<Node<E>>) super.breadthFirstEnumeration();
	}	
	
	@Override	
	public String toString() {
		StringBuffer s = new StringBuffer();
		for(int i=0; i<this.getLevel(); i++) {
			s.append("  ");
		}
		s.append(userObject.toString());
		//s.append(" PARENT: ");
		//s.append(this.getParent());
		return s.toString();
	}
	
	@Override
	/**
	 * Shallow copy of the node, without any parent or children but
	 * with the same user object!
	 * @returns node
	 */
	public Node<E> clone() {
		Node<E> node = new Node<E>();
		node.setUserObject(this.getUserObject());
		return node;
	}
	
}
