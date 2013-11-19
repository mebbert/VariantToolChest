/**
 * 
 */
package vtc.tools.utilitybelt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import net.sourceforge.argparse4j.inf.ArgumentParser;

import org.apache.log4j.Logger;

import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPool;

/**
 * @author markebbert
 *
 */
public class UtilityBelt {

	public UtilityBelt(){
		return;
	}
	
	 public static String roundDouble(double d) {
	        DecimalFormat df = new DecimalFormat("#.##");
	        return String.valueOf(df.format(d));
	    }
	
	
	/**
	 * Will create VariantPool objects from command line-provided input file strings and return as ArrayList<VariantPool>
	 * @param inputFiles
	 * @return
	 * @throws InvalidInputFileException
	 */
	public static TreeMap<String, VariantPool> createVariantPools(ArrayList<Object> inputFiles, boolean addChr) throws InvalidInputFileException{
		
		TreeMap<String, VariantPool> vpMap = new TreeMap<String, VariantPool>();
		for(Object o : inputFiles){
			VariantPool vp = new VariantPool(o.toString(), false, addChr);
			vpMap.put(vp.getPoolID(), vp);
		}
		return vpMap;
	}
	
	
	/**
	 * Print the error. Then print the usage and help
	 * information and exit
	 * @param e
	 */
	public static void printErrorUsageHelpAndExit(ArgumentParser parser, Logger logger, Exception e){
		System.err.println("\nERROR: " + e.getMessage() + "\n");
//		logger.error(e.getMessage());
		printUsageHelpAndExit(parser);
	}
	
	/**
	 * Print the error. Then print the usage
	 * information and exit
	 * @param e
	 */
	public static void printErrorUsageAndExit(ArgumentParser parser, Logger logger, Exception e){
		System.err.println("\nERROR: " + e.getMessage() + "\n");
		parser.printUsage();
		System.exit(1);
	}
	
	/**
	 * Print only the usage and help information and exit.
	 */
	public static void printUsageHelpAndExit(ArgumentParser parser){
		parser.printUsage();
		parser.printHelp();
		System.exit(1);		
	}
	
}
