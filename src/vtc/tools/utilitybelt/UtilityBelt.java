/**
 * 
 */
package vtc.tools.utilitybelt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sourceforge.argparse4j.inf.ArgumentParser;

import org.apache.log4j.Logger;
import org.broadinstitute.variant.variantcontext.Allele;

import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPool;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.varstats.AltType;

/**
 * @author markebbert
 *
 */
public class UtilityBelt {
    private final static int MAX_ALLELE_SIZE_FOR_NON_SV = 150;

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
	 * @throws InvalidOperationException 
	 */
	public static TreeMap<String, VariantPool> createVariantPools(ArrayList<Object> inputFiles, boolean addChr) throws InvalidInputFileException, InvalidOperationException{
		
		TreeMap<String, VariantPool> vpMap = new TreeMap<String, VariantPool>();
		for(Object o : inputFiles){
			VariantPool vp = new VariantPool(o.toString(), false, addChr);
			vpMap.put(vp.getPoolID(), vp);
		}
		return vpMap;
	}
	
	/**
	 * Determine the smallest length of alleles
	 * 
	 * @param alleles
	 * @return
	 */
	public static int getSmallestLength(TreeSet<String> alleles){
		int currSize, smallest = -1;
		for(String al : alleles){
			currSize = al.length();
			if(smallest == -1){
				smallest = currSize;
			}
			else if(currSize < smallest){
				smallest = currSize;
			}
		}
		return smallest;	
	}
	
	/**
	 * Determine largest length of alleles
	 * @param alleles
	 * @return
	 */
	public static int getLargestLength(TreeSet<String> alleles){
		int currSize, largest = -1;
		for(String al : alleles){
			currSize = al.length();
			if(largest == -1){
				largest = currSize;
			}
			else if(currSize > largest){
				largest = currSize;
			}
		}
		return largest;
	}
	
	/**
	 * Get average length of alleles.
	 * 
	 * @param alleles
	 * @return Returns -1 if alleles is empty
	 */
	public static double getAverageLength(TreeSet<String> alleles){
		if(alleles.size() == 0){
			return -1;
		}

		int cumLength = 0;
		for(String al : alleles){
			cumLength += al.length();
		}
		
		return cumLength / alleles.size();
	}
	

	/**
	 * Determine whether the alt is a SNV, MNP, insertion, deletion
	 * or structural insertion or deletion.
	 * 
	 * @param ref
	 * @param alt
	 * @return
	 */
	public static AltType determineAltType(Allele ref, Allele alt){
		if(ref.length() == 1 && alt.length() == 1 && !ref.equals(alt, true)){
			return AltType.SNV;
		}
		else if(ref.length() > 1 && ref.length() == alt.length()){
			int diffCount = getDiffCount(ref, alt);
			if(diffCount == 0){
                throw new RuntimeException("Something is very wrong! Expected differences in variant record. Ref: " +
											ref + " Alt: " + alt);
			}
			else if(diffCount == 1){
				return AltType.SNV;
			}
			else{
				return AltType.MNP;
			}
		}
		else if(ref.length() > alt.length()){ // Deletion
			if(ref.length() > MAX_ALLELE_SIZE_FOR_NON_SV){
				return AltType.STRUCTURAL_DELETION;
			}
			return AltType.DELETION;
		}
		else if(ref.length() < alt.length()){
			if(alt.length() > MAX_ALLELE_SIZE_FOR_NON_SV){
				return AltType.STRUCTURAL_INSERTION;
			}
			return AltType.INSERTION;
		}
        throw new RuntimeException("Something is very wrong! Could not determine variant type! Ref: " + ref + " Alt: " + alt);
	}
	
	/**
	 * Determine if the AltType is a subclass if INDEL
	 * @param type
	 * @return
	 */
	public static boolean altTypeIsIndel(AltType type){
		if(type == AltType.INSERTION || type == AltType.DELETION
				|| type == AltType.STRUCTURAL_INSERTION || type == AltType.STRUCTURAL_DELETION){
			return true;
		}
		return false;
	}
	
	/**
	 * Count the differences between a ref and alt
	 * 
	 * @param ref
	 * @param alt
	 * @return
	 */
	public static int getDiffCount(Allele ref, Allele alt){
		String refNucs = ref.getBaseString();
		String altNucs = alt.getBaseString();
		int count = 0;
		for(int i = 0; i < refNucs.length(); i++){
			if(refNucs.charAt(i) != altNucs.charAt(i)){
				count++;
			}
		}
		return count;
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
