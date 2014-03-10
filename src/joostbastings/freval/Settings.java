package joostbastings.freval;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

/**
 * Loads in properties from a file
 * No defaults are provided - a properties file must be used,
 * which can be specified using the system property 'properties'
 *
 */
public class Settings implements Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -6692235240768239391L;

	private final static String className = Settings.class.getName();
	private static File propertiesFile = null;
	private static Properties properties = new Properties();

	public static ArrayList<String> deleteLabels;
	public static ArrayList<String> deleteLabelsForLength;
	public static ArrayList<String> quoteLabels;
	public static ArrayList<String[]> equivalentLabels;
	public static ArrayList<String[]> equivalentWords;	

	public Settings() {}

	/**
	 * Program name
	 */
	public final static String progName = "Freval";

	/**
	 * Program version
	 */
	public final static String version = "1.0";	

	/**
	 * Convenience method to debug setting
	 */
	public static int debug;
	
	/**
	 * Loads properties from filename
	 *
	 * @param filename filename of the properties file
	 * @throws IOException
	 */
	public static void load(File f) throws IOException {
		System.err.println("Reading properties from: " + f.toString());

		BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(f));
		properties.load(bis);
		bis.close();

	}

	static {

		String propFilename = System.getProperty("properties");
		if (propFilename == null)
			throw new RuntimeException("Properties file path not specified. " +
			"Please specify the file path using -Dproperties=/path/to/file");
		else
		try {
			propertiesFile = new File(propFilename);			
			load(propertiesFile);
			processLoadedProperties();
		}
		catch (FileNotFoundException fnfe) {
			System.err.println(className + ": error: couldn't load settings");
			throw new RuntimeException(fnfe.getMessage());
		}
		catch (IOException ioe) {
			System.err.println(className + ": error: couldn't load settings");
			throw new RuntimeException(ioe.getMessage());
		}
	}

	/**
	 * Build some internal representations from the
	 * loaded properties
	 */
	private static void processLoadedProperties() {

		// set debug (for convenience)
		debug = Integer.parseInt(get("debug"));
		
		// populate delete labels
		deleteLabels = new ArrayList<String>(20);
		for(String s : get("deleteLabels").split(" ")) {
			deleteLabels.add(s);
		}

		// populate delete labels for length
		deleteLabelsForLength = new ArrayList<String>(5);
		for(String s : get("deleteLabelsForLength").split(" ")) {
			deleteLabelsForLength.add(s);
		}		

		// populate quote labels
		quoteLabels = new ArrayList<String>(10);
		for(String s : get("quoteLabels").split(" ")) {
			quoteLabels.add(s);
		}

		// populate equivalent labels
		equivalentLabels = new ArrayList<String[]>(5);
		for(String s : get("equivalentLabels").split(" ")) {
			String[] pair = s.split("===", 2);
			equivalentLabels.add(pair);
		}

		// populate equivalent words
		equivalentWords = new ArrayList<String[]>(5);
		for(String s : get("equivalentWords").split(" ")) {
			String[] pair = s.split("===", 2);
			equivalentWords.add(pair);
		}		

	}

	/**
	 * Gets the value of the specified property.
	 *
	 * @param name the name of the property to get
	 * @return the value of the specified property
	 */
	public static String get(String name) {
		return properties.getProperty(progName + "." + name);
	}	

	/**
	 * Returns the specified property as a double
	 *
	 * @param property
	 * @return value parsed as double
	 */
	public static double getDouble(String property) {
		return Double.parseDouble(get(property));
	}

	/**
	 * Returns the specified property as an integer
	 *
	 * @param property
	 * @return value parsed as an integer
	 */
	public static double getInteger(String property) {
		return Integer.parseInt(get(property));
	}

	/**
	 * Returns the property as a boolean
	 *
	 * @param property
	 * @return boolean value
	 */
	public static boolean getBoolean(String property) {
		return Boolean.parseBoolean(get(property));
	}	

	/**
	 * Prints the loaded properties
	 */
	public static void main(String [] args) {

		for( Entry<Object, Object> entry : Settings.properties.entrySet()) {
			System.out.println(entry.getKey() +": "+entry.getValue());
		}

	}	


}
