package joostbastings.freval.reader;

public class Predicates {

	/**
	 * Tells if the specified character is considered a
	 * terminator. That is, if it is a boundary for a word
	 * or a label in the Penn tree representation.
	 * @param c
	 * @return true if this is a terminator
	 */
	public static boolean isTerminator(Character c) {
		
		if ((Character.isWhitespace(c)) || 
				c.equals('(') || c.equals(')')) {
			return true;
		}
		
		return false;
	}
	
}
