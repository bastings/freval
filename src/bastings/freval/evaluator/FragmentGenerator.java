package bastings.freval.evaluator;

import java.util.ArrayList;
import java.util.List;

import bastings.freval.util.*;

/**
 * A class with functions that generate fragments
 * 
 * 
 * TODO move functions to this class
 * This functionality currently is found in Evaluator.java
 *
 */
public class FragmentGenerator {


	/**
	 * Generates a list of fragments of size N+1 from a fragment
	 * of size N
	 * 
	 * If the specified fragment completely matches, the new fragments
	 * are checked for complete match as well. 
	 * 
	 * If the specified fragment does not completely match,
	 * the new fragments will never completely match so there is no check.
	 *
	 * @param f
	 * @return
	 */
	public static List<Fragment> generate(Fragment f) {
		
		List<Fragment> generatedFragments = new ArrayList<Fragment>();
		
		
		
		return generatedFragments;
		
	}

}
