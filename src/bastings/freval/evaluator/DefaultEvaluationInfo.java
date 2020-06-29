package bastings.freval.evaluator;

import java.util.ArrayList;
import java.util.List;

import bastings.freval.util.Totals;

/**
 * The default implementation of Evaluation Info
 * Instances of this class are expected to contain
 * fragment counts (and should not be errors or skipped,
 * for those @see {@link ErrorEvaluationInfo} and 
 * {@link SkipEvaluationInfo})
 *
 */
public class DefaultEvaluationInfo extends EvaluationInfo {

	/**
	 * We store the counts for each fragment size
	 * At start, we reserve space for size up to 70
	 * If larger fragments are found, the space will be extended
	 * @see {@link Totals} 
	 */
	private static int INITIAL_COUNTS_CAPACITY = 70;

	/**
	 * This holds the number of matched fragments for every fragment size
	 * <b>Note: fragment size 1 is at index 0!</b>
	 */
	public List<Integer> matchedFragmentCountByFragmentSize;

	/**
	 * This holds the number of gold fragments for every fragment size
	 * <b>Note: fragment size 1 is at index 0!</b>
	 */	
	public List<Integer> goldFragmentCountByFragmentSize;

	/**
	 * This holds the number of test fragments for every fragment size
	 * <b>Note: fragment size 1 is at index 0!</b>
	 */	
	public List<Integer> testFragmentCountByFragmentSize;

	/**
	 * Number of crossing brackets in test tree compared to gold tree
	 */
	private int crossing = -1;

	/**
	 * Number of correct tags (on terminals)
	 */
	private int correctTag = -1;

	/**
	 * Number of words without counting those that were deleted
	 * before evaluation
	 */
	private int goldWordCount = 0;

	/**
	 * Number of words without counting those that were deleted
	 * before evaluation
	 */
	private int testWordCount = 0;	

	/**
	 * Constructor, initializes fragment lists
	 */
	public DefaultEvaluationInfo(int treeID, int sentenceLength) {
		this.setTreeID(treeID);
		this.setSentenceLength(sentenceLength);
		this.matchedFragmentCountByFragmentSize = new ArrayList<Integer>
		(INITIAL_COUNTS_CAPACITY);
		this.goldFragmentCountByFragmentSize = new ArrayList<Integer>
		(INITIAL_COUNTS_CAPACITY);
		this.testFragmentCountByFragmentSize = new ArrayList<Integer>
		(INITIAL_COUNTS_CAPACITY);
	}

	/**
	 * Increment the number of gold fragments with size fragmentSize
	 * Note that we store fragment size 1 in index 0
	 * @param fragmentSize
	 */
	public void incrementGold(int fragmentSize) {

		// check for resize
		if (goldFragmentCountByFragmentSize.size() < fragmentSize) {
			goldFragmentCountByFragmentSize.add(0);
			testFragmentCountByFragmentSize.add(0);
			matchedFragmentCountByFragmentSize.add(0);
		}

		goldFragmentCountByFragmentSize.set(fragmentSize - 1, 
				goldFragmentCountByFragmentSize.get(fragmentSize - 1) + 1);
	}

	/**
	 * Increment the number of test fragments with size fragmentSize
	 * Note that we store fragment size 1 in index 0
	 * @param fragmentSize
	 */
	public void incrementTest(int fragmentSize) {

		// check for resize
		if (testFragmentCountByFragmentSize.size() < fragmentSize) {
			goldFragmentCountByFragmentSize.add(0);
			testFragmentCountByFragmentSize.add(0);
			matchedFragmentCountByFragmentSize.add(0);
		}

		testFragmentCountByFragmentSize.set(fragmentSize - 1, 
				testFragmentCountByFragmentSize.get(fragmentSize-1) + 1);
	}	

	/**
	 * Increment the number of matched fragments with size fragmentSize
	 * Note that we store fragment size 1 in index 0
	 * @param fragmentSize
	 */
	public void incrementMatched(int fragmentSize) {

		// check for resize
		if (matchedFragmentCountByFragmentSize.size() < fragmentSize) {
			goldFragmentCountByFragmentSize.add(0);
			testFragmentCountByFragmentSize.add(0);
			matchedFragmentCountByFragmentSize.add(0);
		}

		matchedFragmentCountByFragmentSize.set(fragmentSize - 1, 
				matchedFragmentCountByFragmentSize.get(fragmentSize-1) + 1);
	}	

	/**
	 * @return the crossing
	 */
	public int getCrossing() {
		return crossing;
	}

	/**
	 * @param crossing the crossing to set
	 */
	public void setCrossing(int crossing) {
		this.crossing = crossing;
	}

	/**
	 * @return the correctTag
	 */
	public int getCorrectTag() {
		return correctTag;
	}

	/**
	 * @param correctTag the correctTag to set
	 */
	public void setCorrectTag(int correctTag) {
		this.correctTag = correctTag;
	}

	/**
	 * @return the goldWordCount
	 */
	public int getGoldWordCount() {
		return goldWordCount;
	}

	/**
	 * @param goldWordCount the goldWordCount to set
	 */
	public void setGoldWordCount(int goldWordCount) {
		this.goldWordCount = goldWordCount;
	}

	/**
	 * @return the testWordCount
	 */
	public int getTestWordCount() {
		return testWordCount;
	}

	/**
	 * @param testWordCount the testWordCount to set
	 */
	public void setTestWordCount(int testWordCount) {
		this.testWordCount = testWordCount;
	}

	/**
	 * Prints this info
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < goldFragmentCountByFragmentSize.size(); i++) {

			int match = matchedFragmentCountByFragmentSize.get(i);
			int gold = goldFragmentCountByFragmentSize.get(i);
			int test = testFragmentCountByFragmentSize.get(i);

			double recall = (gold > 0 ? 100 * match / (double) gold : 0.0);
			double precision = (test > 0 ? 100 * match / (double) test : 0.0);
			double taggingAccuracy = (getGoldWordCount() == 0 ? 
					0.0 : 
						100.0 * getCorrectTag() / (double) getGoldWordCount());

			sb.append(String.format("%4d    %4d    %4d    " +
					"%6.2f    %6.2f    %8d    %8d    %8d    " +
					"%4d    %4d    %4d    %6.2f    %6s\n", 
					this.getTreeID(), this.getSentenceLength(), i+1,
					recall, precision, match, gold, test,
					getCrossing(), getGoldWordCount(), getCorrectTag(), 
					taggingAccuracy, "OK"));
		}

		return sb.toString();
	}

}
