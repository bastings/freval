package bastings.freval.util;

/**
 * This exception is thrown when a system argument was not specified
 */
public class ArgumentMissingException extends RuntimeException {

	private static final long serialVersionUID = -4878530960441469745L;

	public ArgumentMissingException(String s) {
		super(s);
	}
	
}
