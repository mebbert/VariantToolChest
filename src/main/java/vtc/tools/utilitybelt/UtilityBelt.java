/**
 * 
 */
package vtc.tools.utilitybelt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sourceforge.argparse4j.inf.ArgumentParser;

import org.apache.log4j.Logger;
import org.broadinstitute.variant.variantcontext.Allele;

import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPoolHeavy;
import vtc.datastructures.VariantPoolLight;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.setoperator.operation.Operation;
import vtc.tools.setoperator.operation.OperationFactory;
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
	
	 public static String roundDoubleToString(double d) {
	        DecimalFormat df = new DecimalFormat("#.##");
	        return String.valueOf(df.format(d));
    }
	 
    /**
     * @param unrounded
     * @param precision
     * @param roundingMode
     * @return
     */
    public static double round(double unrounded, int precision, int roundingMode)
    {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }
	
	
	/**
	 * Will create VariantPoolHeavy objects from command line-provided input file strings and return as ArrayList<VariantPool>
	 * @param inputFiles
	 * @return
	 * @throws InvalidInputFileException
	 * @throws InvalidOperationException 
	 * @throws IOException 
	 */
	public static TreeMap<String, VariantPoolHeavy> createHeavyVariantPools(List<String> inputFiles, boolean addChr) throws InvalidInputFileException, InvalidOperationException, IOException{
		
		TreeMap<String, VariantPoolHeavy> vpMap = new TreeMap<String, VariantPoolHeavy>();
		for(Object o : inputFiles){
			VariantPoolHeavy vp = new VariantPoolHeavy(o.toString(), false, addChr);
			vpMap.put(vp.getPoolID(), vp);
		}
		return vpMap;
	}
	
	
	/**
	 * Will create VariantPoolLight objects from command line-provided input file strings and return as ArrayList<VariantPool>
	 * @param inputFiles
	 * @return
	 * @throws InvalidInputFileException
	 * @throws InvalidOperationException 
	 * @throws IOException 
	 */
	public static TreeMap<String, VariantPoolLight> createLightVariantPools(List<String> inputFiles, boolean addChr) throws InvalidInputFileException, InvalidOperationException, IOException{
		
		TreeMap<String, VariantPoolLight> vpMap = new TreeMap<String, VariantPoolLight>();
		for(Object o : inputFiles){
			VariantPoolLight vp = new VariantPoolLight(o.toString(), false, addChr);
			vpMap.put(vp.getPoolID(), vp);
		}
		return vpMap;
	}
	

    /**
     * Extract only VariantPool objects associated with the Operation provided
     * as an ArrayList<VariantPool>.
     * 
     * @param op
     * @param vps
     * @return
     */
    public static ArrayList<VariantPoolHeavy> getAssociatedVariantPoolsAsArrayList(Operation op, TreeMap<String, VariantPoolHeavy> vps) {

        /*
         * Get all pool IDs associated with this Operation. Note: All SamplePool
         * objects have a pool ID that matches a VariantPool pool ID.
         */
        Collection<String> pids = op.getAllPoolIDs();
        ArrayList<VariantPoolHeavy> vpList = new ArrayList<VariantPoolHeavy>();
        Iterator<String> it = vps.keySet().iterator();
        String pid;

        /*
         * Iterate over the VP TreeMap and add any VP associated with Operation
         * op to the vpList and return vpList
         */
        while (it.hasNext()) {
            pid = it.next();
            if (pids.contains(pid)) {
                vpList.add(vps.get(pid));
            }
        }
        return vpList;
    }
	
	/**
	 * Determine the smallest length of alleles
	 * 
	 * @param alleles
	 * @return the smallest length or -1 if alleles are empty
	 */
	public static int getSmallestLength(TreeSet<String> alleles){
		if (alleles == null)
			return -1;
					
		if(alleles.size() > 0){
            return alleles.first().length(); // Since this is a TreeSet, it should just be the first
		}
		return -1;
//		int currSize, smallest = -1;
//		for(String al : alleles){
//			currSize = al.length();
//			if(smallest == -1){
//				smallest = currSize;
//			}
//			else if(currSize < smallest){
//				smallest = currSize;
//			}
//		}
//		return smallest;	
	}
	
	/**
	 * Determine largest length of alleles
	 * @param alleles
	 * @return the largest length or -1 if alleles are empty
	 */
	public static int getLargestLength(TreeSet<String> alleles){
		if (alleles == null || alleles.size() == 0)
			return -1;
		
		int max_length = 0;
		for (String al : alleles) {
			if (al.length() > max_length) {
				max_length = al.length();
			}
		}
		return max_length;
	}
	
	/**
	 * Get average length of alleles.
	 * 
	 * @param alleles
	 * @return Returns -1 if alleles is empty
	 */
	public static double getAverageLength(TreeSet<String> alleles){
		if (alleles == null || alleles.size() == 0 )
			return -1.0;
					
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
		if (ref == null || alt == null){
			throw new RuntimeException("Reference or alternate alleles are null.");
		}
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
		if(refNucs.length() != altNucs.length()){
			System.err.println("Error: Running getDiffCount on two alleles of different lengths.");
			return -1;
		}
		
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
	
    /**
     * Will create Operation objects from command line-provided operation
     * strings and return as ArrayList<Operation>
     * 
     * @param operations
     * @return
     * @throws InvalidOperationException
     */
    public static ArrayList<Operation> createOperations(ArrayList<Object> operations, TreeMap<String, VariantPoolHeavy> variantPools) throws InvalidOperationException {

        ArrayList<Operation> opList = new ArrayList<Operation>();
        for (Object o : operations) {
            Operation op = OperationFactory.createOperation(o.toString(), variantPools);
            opList.add(op);
        }
        return opList;
    }
    
    
    
    /**
     * Gets the specified property from the vtc.VTC.properties file.
     * @param prop
     * @return The property desired.
     */
    public static Object getProperty(String prop){
    	File refFile = new File("src/main/java/vtc/VTC.properties");
		try{
			if(refFile.canRead()){
				BufferedReader br = new BufferedReader(new FileReader(refFile));
				String line = br.readLine();
				while(line != null){
					line = line.replaceAll("\\s+", "");
					String[] keyAndVal = line.split("=");
					if(keyAndVal[0].equals(prop)){
						br.close();
						return keyAndVal[1];
					}
					line = br.readLine();
				}
				br.close();
				throw new IOException("'fasta_ref' not found in VTC.properties");
			}
			throw new IOException(refFile.getAbsolutePath() + "is not a valid file path");
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
    }
    
    /**
	 * Get the absolute pathway to the reference FASTA file
	 * @return Human Genome (hg) reference pathway
	 */
	public static String getHGREF() {
		return (String) getProperty("fasta_ref");
	}
    
}
