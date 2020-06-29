package bastings.freval.util;

import java.util.ArrayList;
import java.util.List;

import bastings.freval.evaluator.DefaultEvaluationInfo;
import bastings.freval.evaluator.ErrorEvaluationInfo;
import bastings.freval.evaluator.EvaluationInfo;
import bastings.freval.evaluator.SkipEvaluationInfo;

/**
 * Holds the total results for either all sentences
 * or for sentences <= cutoffLength
 * 
 * When adding results, if the sentence is longer than
 * cutOffLength, the results will be discarded.
 */
public class Totals {

	/**
	 * The maximum length of sentences of which
	 * results will be added to these totals
	 */
	private int cutOffLength;

	private int sentCount;   // Sentence count
	private int errorCount;  // Error sentence count
	private int skipCount;   // Skipped sentence count
	private int completeMatchCount; // Complete match sent count
	private int wordCount;   // Total word count
	private int noCrossingCount;        // no crossing sent count
	private int twoOrLessCrossingCount; // 2 or less crossing s. count
	private int correctTagCount; // total correct tagging
	private int crossingCount; // total number of crossing

	/**
	 * We store the counts for each fragment size
	 * At start, we reserve space for size up to 70
	 * If larger fragments are found, the space will be extended
	 */
	private static int INITIAL_COUNTS_CAPACITY = 70;

	List<Integer> matchedFragmentCountByFragmentSize;
	List<Integer> goldFragmentCountByFragmentSize;
	List<Integer> testFragmentCountByFragmentSize;

	/**
	 * By default, don't use any cutoffLength (-1)
	 */
	public Totals() {
		this(-1);
	}

	/**
	 * Create an instance with specified cutoffLength
	 */
	public Totals(int cutOffLength) {

		this.cutOffLength = cutOffLength;

		// init fragment count containers
		this.matchedFragmentCountByFragmentSize = new
		ArrayList<Integer>(INITIAL_COUNTS_CAPACITY);
		this.goldFragmentCountByFragmentSize = new
		ArrayList<Integer>(INITIAL_COUNTS_CAPACITY);
		this.testFragmentCountByFragmentSize = new
		ArrayList<Integer>(INITIAL_COUNTS_CAPACITY);

		// var init
		this.sentCount = 0;
		this.errorCount = 0;
		this.skipCount = 0;
		this.completeMatchCount = 0;
		this.wordCount = 0;
		this.noCrossingCount = 0;
		this.twoOrLessCrossingCount = 0;
		this.correctTagCount = 0;

	}

	/**
	 * @return the cutoffLength
	 */
	public int getCutOffLength() {
		return cutOffLength;
	}

	/**
	 * @param cutoffLength the cutoffLength to set
	 */
	public void setCutOffLength(int cutOffLength) {
		this.cutOffLength = cutOffLength;
	}

	/**
	 * Add an Error-result to the totals, if the sentence length
	 * is <= cut-off length, or if the length does not matter
	 * @param sentenceLength
	 * @param message reason for the error
	 */
	public void addError(int sentenceLength, String message) {
		if ((cutOffLength == -1) || 
				(sentenceLength <= cutOffLength)) {	
			this.sentCount++;
			this.errorCount++;
		}
	}

	/**
	 * Add a Skipped-sentence result to the totals,
	 * if the sentenceLength is <= cut-off length, or if the
	 * length does not matter
	 * @param sentenceLength
	 */
	public void addSkipped(int sentenceLength) {
		if ((cutOffLength == -1) || 
				(sentenceLength <= cutOffLength)) {
			this.sentCount++;
			this.skipCount++;
		}
	}

	/**
	 * Adds an Error-result to the total error count, when the sentence length
	 * is small enough (or when it does not mater)
	 * @param result
	 */
	public void addResult(ErrorEvaluationInfo result) {
		if ((cutOffLength == -1) || 
				(result.getSentenceLength() <= cutOffLength)) {

			this.sentCount++;
			this.errorCount++;
		}
		
		// only print this once
		if (cutOffLength == -1) {
			System.err.println(result.getTreeID() + " : " + 
					result.getMessage());
		}
	}

	/**
	 * Adds a skipped result to the skipped sentence count, 
	 * when the sentence length is small enough (or when it does not mater)
	 * @param result
	 */
	public void addResult(SkipEvaluationInfo result) {

		if ((cutOffLength == -1) || 
				(result.getSentenceLength() <= cutOffLength)) {

			this.sentCount++;
			this.skipCount++;
		}
	}

	/**
	 * Adds a default result to the totals, when the sentence length
	 * is small enough (or when it does not mater)
	 * @param result
	 */
	public void addResult(DefaultEvaluationInfo result) {

		if ((cutOffLength == -1) || 
				(result.getSentenceLength() <= cutOffLength)) {

			this.sentCount++;

			while (this.goldFragmentCountByFragmentSize.size() < 
					result.goldFragmentCountByFragmentSize.size()) {
				this.goldFragmentCountByFragmentSize.add(0);
				this.testFragmentCountByFragmentSize.add(0);
				this.matchedFragmentCountByFragmentSize.add(0);
			}

			for(int i = 0; i < result.goldFragmentCountByFragmentSize.size(); 
			i++) {
				this.goldFragmentCountByFragmentSize.set(i, 
						this.goldFragmentCountByFragmentSize.get(i) + result.
						goldFragmentCountByFragmentSize.get(i));
				this.testFragmentCountByFragmentSize.set(i, 
						this.testFragmentCountByFragmentSize.get(i) + result.
						testFragmentCountByFragmentSize.get(i));
				this.matchedFragmentCountByFragmentSize.set(i, 
						this.matchedFragmentCountByFragmentSize.get(i) + result.
						matchedFragmentCountByFragmentSize.get(i));				
			}
			
			// check for complete match
			if(result.goldFragmentCountByFragmentSize.get(0) == 
				result.testFragmentCountByFragmentSize.get(0) &&
				result.testFragmentCountByFragmentSize.get(0) ==
					result.matchedFragmentCountByFragmentSize.get(0)) {
				completeMatchCount++;
			}

			// update total crossing counts
			crossingCount += result.getCrossing();

			if(result.getCrossing()==0){
				noCrossingCount++;
			}

			if(result.getCrossing() <= 2){
				twoOrLessCrossingCount++;
			}

			// word count
			wordCount += result.getGoldWordCount();

			// update total correct tag
			correctTagCount += result.getCorrectTag();
			
		}

	}

	/**
	 * General addResult methods, identifies which subclass
	 * we deal with and delegates to the appropriate function
	 * @param result
	 */
	public void addResult(EvaluationInfo result) {
		if (result instanceof DefaultEvaluationInfo) {
			this.addResult( (DefaultEvaluationInfo) result);
		} else if (result instanceof ErrorEvaluationInfo) {
			this.addResult( (ErrorEvaluationInfo) result);
		} else if (result instanceof SkipEvaluationInfo) {
			this.addResult( (SkipEvaluationInfo) result);
		}
		else {
			throw new RuntimeException(
			"Unknown result! Can not add to totals.");
		}

	}

	public String toString() {

		StringBuffer sb = new StringBuffer();

		int validSentCount = sentCount - errorCount - skipCount;

		// accumulative precision and recall
		// the final score for the parser in Treeval
		// using alpha's weights
		double accWeighedP = 0.0;
		double accWeighedR = 0.0;

		sb.append("\n");
		sb.append("\n=== Summary ");

		// print appropriate header
		if (cutOffLength == -1) // header for all sentence lengths
		{
			sb.append("(All)");
		} else // header for cut-off statistics
		{
			sb.append(String.format("(len<=%d)", cutOffLength));
		}
		sb.append(" ===\n\n");

		sb.append(String.format(
				"Number of sentence        = %6d\n", sentCount));
		sb.append(String.format(
				"Number of Error sentence  = %6d\n", errorCount));
		sb.append(String.format(
				"Number of Skip  sentence  = %6d\n", skipCount));
		sb.append(String.format(
				"Number of Valid sentence  = %6d\n", validSentCount));
		sb.append(String.format(
				"Complete match            = %6.2f\n", 
				(validSentCount > 0 ? 100.0 * 
						completeMatchCount / (double) validSentCount : 0.0)));
		sb.append(String.format(
				"Average crossing          = %6.2f\n", 
				(validSentCount > 0 ?
						crossingCount / (double) validSentCount : 0.0)));	
		sb.append(String.format(
				"No crossing               = %6.2f\n", 
				(validSentCount > 0 ? 100.0 * 
						noCrossingCount  / (double) validSentCount : 0.0)));		
		sb.append(String.format(
				"2 or less crossing        = %6.2f\n", 
				(validSentCount > 0 ? 100.0 * 
						twoOrLessCrossingCount  / 
						(double) validSentCount : 0.0)));	
		sb.append(String.format(
				"Tagging accuracy          = %6.2f\n", 
				(wordCount > 0 ? 100.0 * 
						correctTagCount / (double) wordCount : 0.0)));

		sb.append("\n");

		sb.append(String.format(
				"%4s    %6s    %6s    %6s    %8s    " +
				"%8s    %8s    %8s\n",
				"N", "R", "P", "F1", "a(n)", 
				"Matched", "GoldFrag", "TestFrag"));		

		// print recall, precision, F1 for each N (fragment size)
		for(int i = 0; i < matchedFragmentCountByFragmentSize.size(); i++) {

			double r = 0.0;
			double p = 0.0;
			double f = 0.0;
			double alpha = 0.0;

			if (goldFragmentCountByFragmentSize.get(i) > 0 &&
					testFragmentCountByFragmentSize.get(i) > 0) {

				// recall
				r = (goldFragmentCountByFragmentSize.get(i) > 0 ?
						100.0 * matchedFragmentCountByFragmentSize.get(i) /
						(double) goldFragmentCountByFragmentSize.get(i) : 0.0);

				// precision
				p = (testFragmentCountByFragmentSize.get(i) > 0 ?
						100.0 * matchedFragmentCountByFragmentSize.get(i) /
						(double) testFragmentCountByFragmentSize.get(i) : 0.0);

				// F1 measure
				f = 2 * p * r / (p + r);

				// alpha
				// weight for interpolation of precision & recall for the 
				// various N's
				// uniform distribution for now
				alpha = 1.0 / (double)matchedFragmentCountByFragmentSize.size();

				// add this N's recall and precision to the accumulative score over all
				// N's with alpha as weight
				accWeighedR += alpha * r;
				accWeighedP += alpha * p;

				sb.append(String.format(
						"%4d    %6.2f    %6.2f    %6.2f    %1.6f    " +
						"%8d    %8d    %8d\n", (i+1), r, p, f, alpha,
						matchedFragmentCountByFragmentSize.get(i),
						goldFragmentCountByFragmentSize.get(i),
						testFragmentCountByFragmentSize.get(i)));					

			}
		}

		sb.append("------------------------------------------------------" +
		"----------------------------\n");
		sb.append(String.format(
				"        %6.2f    %6.2f    %6.2f\n",
				accWeighedR, accWeighedP, 
				2 * accWeighedP * accWeighedR / (accWeighedP + accWeighedR))
		);		


		return sb.toString();
	}


}
