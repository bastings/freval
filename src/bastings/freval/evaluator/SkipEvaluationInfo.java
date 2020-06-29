package bastings.freval.evaluator;


/**
 * Holds an evaluation result for a skipped tree-pair evaluation
 * which means that the test tree was empty
 */
public class SkipEvaluationInfo extends EvaluationInfo {

	/**
	 * Construct a result which indicates
	 * the evaluation was skipped because of an empty
	 * test tree
	 * Note: no fragment result lists are initialized
	 * @param sentenceLength
	 */
	public SkipEvaluationInfo(int treeID, int sentenceLength) {
		this.setTreeID(treeID);
		this.setSentenceLength(sentenceLength);
		//this.setSkipped(true);
	}
	
	/**
	 * Prints this info
	 */
	public String toString() {

		return String.format("%4d    %4d    %4d    " +
					"%6.2f    %6.2f    %8d    %8d    %8d    " +
					"%4d    %4d    %4d    %6.2f    %6s\n", 
					this.getTreeID(), this.getSentenceLength(), 1,
					0.0, 0.0, 0, 0, 0, 0, 0, 0, 0.0, "Skip");
	}	
	
}
