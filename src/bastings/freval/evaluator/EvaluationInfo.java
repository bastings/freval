package joostbastings.freval.evaluator;

import joostbastings.freval.util.TreeFunctions;

/**
 * Abstract class for the result of an evaluation of a test tree
 * with respect to a gold tree
 * @see {@link DefaultEvaluationInfo}, {@link ErrorEvaluationInfo}
 * 
 */
public abstract class EvaluationInfo {
	
	/**
	 * Holds the ID of the tree, so we know what tree this result
	 * belongs to
	 */
	private int treeID;
	
	/**
	 * This is the "corrected" sentence length
	 * i.e. the number of terminals without a parent pre-terminal
	 * that is a deleteLabelForLength, @see {@link TreeFunctions}
	 */
	private int sentenceLength;
	
	/**
	 * @return the treeID
	 */
	public int getTreeID() {
		return treeID;
	}

	/**
	 * @param treeID the treeID to set
	 */
	public void setTreeID(int treeID) {
		this.treeID = treeID;
	}

	/**
	 * @return the sentenceLength
	 */
	public int getSentenceLength() {
		return sentenceLength;
	}

	/**
	 * @param sentenceLength the sentenceLength to set
	 */
	public void setSentenceLength(int sentenceLength) {
		this.sentenceLength = sentenceLength;
	}

}
