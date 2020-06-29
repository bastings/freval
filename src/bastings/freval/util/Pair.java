package bastings.freval.util;

public class Pair<A, B> {
	
	/**
	 * The first object in this pair, type A
	 */
	private A first;
	
	/**
	 * The second object in this pair, type B
	 */
	private B second;
	
	/**
	 * Construct this pair with first and second
	 */
	public Pair(A first, B second) {
		this.setFirst(first);
		this.setSecond(second);
	}

	/**
	 * @return the first
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(A first) {
		this.first = first;
	}

	/**
	 * @return the second
	 */
	public B getSecond() {
		return second;
	}

	/**
	 * @param second the second to set
	 */
	public void setSecond(B second) {
		this.second = second;
	}

}
