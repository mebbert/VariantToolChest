/**
 * 
 */
package vtc.tools.utilitybelt;

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
	 * Print the error to stdout and log. Then print the usage and help
	 * information and exit
	 * @param e
	 */
	public static void printErrorUsageHelpAndExit(ArgumentParser parser, Logger logger, Exception e){
		System.err.println(e.getMessage());
		logger.error(e.getMessage());
		printUsageHelpAndExit(parser);
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
