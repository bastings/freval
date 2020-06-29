package bastings.freval.evaluator;


/**
 * Holds an evaluation result which consists
 * of an error, and holds the reason (message)
 * for that error and the sentence length of the gold
 * tree's sentence
 * 
 * <b>Note: an evaluation error means a word mismatch or 
 * a leaf-count mismatch between the gold and test tree
 * The two trees are supposed to have the exact same
 * terminals</b>
 */
public class ErrorEvaluationInfo extends EvaluationInfo {

	public ErrorEvaluationInfo() {
		
	}
	
	/**
	 * A description of the error
	 */
	private String message;
	
	/**
	 * Construct a result which is an error or skipped
	 * no fragment result lists are initialized
	 * @param isError
	 * @param isSkipped
	 */
	public ErrorEvaluationInfo(int treeID, int sentenceLength, String message) {
		this.setTreeID(treeID);
		this.setSentenceLength(sentenceLength);
		//this.setError(true);
		this.setMessage(message);
	}

	/**
	 * @return the error message
	 * (description of this error)
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the error message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Prints this info
	 */
	public String toString() {

		return String.format("%4d    %4d    %4d    " +
					"%6.2f    %6.2f    %8d    %8d    %8d    " +
					"%4d    %4d    %4d    %6.2f    %6s\n", 
					this.getTreeID(), this.getSentenceLength(), 1,
					0.0, 0.0, 0, 0, 0, 0, 0, 0, 0.0, "Error");
	}	
		
	
}
