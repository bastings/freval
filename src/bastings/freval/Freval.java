package bastings.freval;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import bastings.freval.evaluator.EvaluationInfo;
import bastings.freval.evaluator.Evaluator;
import bastings.freval.reader.PennReader;
import bastings.freval.util.*;

public class Freval {

	private PennReader gpr;
	private PennReader tpr;
	
	/**
	 *  read errors, skipped sentences, length mismatch, word mismatch
	 *  if this value reaches maxErrors (@see {@link Settings} 
	 *  the program will halt
	 */
	private int processingErrorCount = 0; 
	
	/**
	 * To store all Totals instances, which keep result totals
	 * for all sentences and if specified for sentences <= cutoff
	 */
	private List<Totals> totals;

	/**
	 * Fire up an instance of Freval that loads the specified tree files
	 * @param goldFilename
	 * @param testFilename
	 * @throws FileNotFoundException
	 */
	public Freval(String goldFilename, String testFilename) 
	throws FileNotFoundException {		

		gpr = new PennReader(goldFilename, "Gold");
		tpr = new PennReader(testFilename, "Test");
		
		initTotals();
		
	}

	/**
	 * Initializes the Totals objects to keep track of results
	 * There is always a Totals-all instance (without a cutoff length)
	 * CUt-off Total instances can be specified in the Properties file.
	 */
	private void initTotals() {
		totals = new ArrayList<Totals>(3);
		totals.add(new Totals()); // default "all" totals
		String[] cutOffLenghts = Settings.get("cutOffLengths").split("\\s+");
		for(String s : cutOffLenghts) {
			int cutoff = Integer.parseInt(s);
			totals.add(new Totals(cutoff));
		}
	}
	
	/**
	 * Starts evaluation procedure
	 */
	private void start() throws Exception {
		
		// print the columns
		System.out.println(String.format(
				"%4s    %4s    %4s    " +
				"%6s    %6s    " +
				"%8s    %8s    %8s    " +
				"%4s    %4s    " +
				"%4s    %6s    %6s",
				"ID", "Len", "N", 
				"R", "P",
				"Matched", "GoldFrag", "TestFrag",
				"XBkt", "Wrds",
				"TgOK", "TagAcc", "Status"));
		
		Node<NodeInfo> goldTree = null;
		Node<NodeInfo> testTree = null;

		while( true ) {

			try {
				
				goldTree = gpr.nextTree();
				testTree = tpr.nextTree();
				
			} catch(IOException e) {
				System.err.println(e.getMessage());
				processingErrorCount++;
				if (processingErrorCount > Settings.getInteger("maxErrors")) {
					throw new RuntimeException("Maximum number " +
							"of allowed errors exceeded. Halted.");
				}
				continue;
			}
			
			// stop evaluating when there are no more trees
			if (goldTree == null) {
				break;
			}
			
			// print treeID, length
			EvaluationInfo result = Evaluator.evaluate(goldTree, testTree);

			System.out.print(result);

			for(Totals t : totals) {
				t.addResult(result);
			}
			
		}

		// print summary
		for(Totals t : totals) {
			System.out.println(t);
		}

		System.err.println(String.format(
				"\n---\nFinished with %d processing errors",processingErrorCount));
	}

	/**
	 * Checks if all the arguments have been set
	 * @throws RuntimeException
	 */
	private static void checkSystemProperties() 
	throws ArgumentMissingException {

		if (System.getProperty("properties") == null) {
			throw new ArgumentMissingException(
					"Please specify the path to the properties file, " +
			"e.g. using -Dproperties=/path/to/file.");			
		}

		if (System.getProperty("gold") == null) {
			throw new ArgumentMissingException(
					"Please specify the path to the Gold trees, " +
			"e.g. using -Dgold=/path/to/file.");
		}

		if (System.getProperty("test") == null) {
			throw new ArgumentMissingException(
					"Please specify the path to the Test trees, " +
			"e.g. using -Dtest=/path/to/file.");
		}		

	}

	public static void main(String[] args) throws Exception {

		// print name  and version to error console
		System.err.print(Settings.progName);
		System.err.print(" ");
		System.err.println(Settings.version);

		try {

			checkSystemProperties();
			Settings.get("debug"); // make sure settings are loaded

			// create freval instance
			Freval freval = new Freval(
					System.getProperty("gold"),
					System.getProperty("test")
			);

			freval.start();

		} catch(FileNotFoundException e) {
			System.err.print("Error: ");
			System.err.println(e.getMessage());
			System.exit(1);			
		} catch (ArgumentMissingException e) {
			System.err.print("Error: ");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
