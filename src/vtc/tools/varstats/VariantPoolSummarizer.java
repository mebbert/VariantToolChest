/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;

/**
 * @author markebbert
 *
 */
public class VariantPoolSummarizer {
    private final static int MAX_ALLELE_SIZE_FOR_NON_SV = 150;
    
    
    /**
     * Summarize statistics for a set of VariantPools.
     * 
     * @param allVPs
     * @return
     */
    public static HashMap<String, VariantPoolSummary> summarizeVariantPools(TreeMap<String, VariantPool> allVPs,
    		boolean generateDetailedStatistics){
    	
    	VariantPoolSummary vpSummary;
    	HashMap<String, VariantPoolSummary> vpSummaries = new HashMap<String, VariantPoolSummary>();
    	for(VariantPool vp : allVPs.values()){
    		vpSummary = summarizeVariantPool(vp, generateDetailedStatistics);
    		vpSummaries.put(vp.getPoolID(), vpSummary);
    	}
    	return vpSummaries;
    }
    
    /**
     * Summarize statistics in a single VariantPool
     * @param vp
     * @return
     */
    public static VariantPoolSummary summarizeVariantPool(VariantPool vp, boolean generateDetailedStatistics){
    	
		Iterator<String> varIT = vp.getVariantIterator();
		ArrayList<String> detailedStats = new ArrayList<String>();
		String currVarKey;
		VariantContext var;
		HashMap<AltType, Integer> altTypeCounts;
    	int totalVarCount = 0, snpCount = 0, mnpCount = 0, indelCount = 0, insCount = 0,
    			delCount = 0, structIndelCount = 0, structInsCount = 0, structDelCount = 0;
		while (varIT.hasNext()) {
			currVarKey = varIT.next();

			var = vp.getVariant(currVarKey);
			if (var.isVariant()) {
				totalVarCount++; // Increment the total var counts by one for every record

				/* Increment the total var count by the number of alts - 1. This
				 * will keep the total count equal to the number of counted alts
				 * when there are multiple alts in a single record.
				 */
				totalVarCount += var.getAlternateAlleles().size() - 1; 

				// Count the different types of alternates for a single record
				altTypeCounts = collectVariantStatistics(var);
				
				snpCount += altTypeCounts.get(AltType.SNV);
				mnpCount += altTypeCounts.get(AltType.MNP);
				indelCount += altTypeCounts.get(AltType.INDEL);
				insCount += altTypeCounts.get(AltType.INSERTION);
				delCount += altTypeCounts.get(AltType.DELETION);
				structIndelCount += altTypeCounts.get(AltType.STRUCTURAL_INDEL);
				structInsCount += altTypeCounts.get(AltType.STRUCTURAL_INSERTION);
				structDelCount += altTypeCounts.get(AltType.STRUCTURAL_DELETION);
				

				if(generateDetailedStatistics){
					String temp = generateDetailedStatsForVariantRecord(var, (String[])vp.getSamples().toArray());
					detailedStats.add(temp);
				}
			}
		}
		return new VariantPoolSummary(totalVarCount, snpCount, mnpCount, indelCount, insCount, delCount, 0, 0, 0, 0, 0, 0,
				structIndelCount, structInsCount, structDelCount, 0, 0, 0, 0, 0, 0, 0);
    }
    
	/**
	 * Generate a detailed stat summary for a given variant.
	 * 
	 * @param var
	 * @param Samples
	 * @return
	 */
	private static String generateDetailedStatsForVariantRecord(VariantContext var, String[] samples) {
		String temp = var.getChr() + '\t' + var.getStart() + '\t' +
				var.getID() + '\t' + var.getReference().getBaseString() + '\t';
		Allele ref = var.getReference();
		List<Allele> alts = var.getAlternateAlleles();

		temp += generateAltString(alts) + '\t';
		
		int refCount = 0, tmpAltCount, i = 0;
		String altCounts = "";
		Iterator<Genotype> genoIT = var.getGenotypes().iterator();
		Genotype geno;
		for(Allele alt : alts){
			tmpAltCount = 0;
			while(genoIT.hasNext()){
				geno = genoIT.next();
				refCount += geno.countAllele(ref);
				tmpAltCount += geno.countAllele(alt);
			}
			if(i == 0){
				altCounts = altCounts + tmpAltCount;
			}
			else{
				altCounts = altCounts + "," + tmpAltCount;
			}
			i++;
		}
		
		temp += Integer.toString(refCount) + "\t" + altCounts;
		
		Depth depth = new Depth();
		depth.getDepths(var, samples);
		
		temp += depth.toString();

		double qual = var.getPhredScaledQual();
		if(qual>0)
			temp += "\t"+qual;
		else
			temp += "\t"+"NA";
		String depthError = depth.getError();
		if(!depthError.isEmpty())
			temp += "\tIncorrect depth calls in samples: "+depthError+".";
		return temp;

	}
    
    /**
     * Count the different alternate types for a single record. i.e., count the
     * number of SNVs, MNPs, insertions, deletions, structural insertions, and
     * structural deletions in a given record.
     * 
     * @param var
     * @return
     */
    private static HashMap<AltType, Integer> collectVariantStatistics(VariantContext var){

    	Allele ref = var.getReference();
    	List<Allele> alts = var.getAlternateAlleles();
    	
    	Integer snpCount = 0, mnpCount = 0, indelCount = 0, insCount = 0,
    			delCount = 0, structIndelCount = 0, structInsCount = 0,
    			structDelCount = 0, tiCount = 0, tvCount = 0;
		AltType type;
    	for(Allele alt : alts){
    		type = getAltType(ref, alt);
    		
    		if(type == AltType.SNV){
    			snpCount++;
    			if(isTransition(ref.getBaseString(), alt.getBaseString())){
    				tiCount++;
    			}
    			else{
    				tvCount++;
    			}
    		}
    		else if(type == AltType.MNP){
    			mnpCount++;
    		}
    		else if(type == AltType.INSERTION){
    			indelCount++;
    			insCount++;
    		}
    		else if(type == AltType.DELETION){
    			indelCount++;
    			delCount++;
    		}
    		else if(type == AltType.STRUCTURAL_INSERTION){
    			structIndelCount++;
    			structInsCount++;
    		}
    		else if(type == AltType.STRUCTURAL_DELETION){
    			structIndelCount++;
    			structDelCount++;
    		}
    	}
    	
    	HashMap<AltType, Integer> typeCounts = new HashMap<AltType, Integer>();
    	typeCounts.put(AltType.SNV, snpCount);
    	typeCounts.put(AltType.MNP, mnpCount);
    	typeCounts.put(AltType.INDEL, indelCount);
    	typeCounts.put(AltType.INSERTION, insCount);
    	typeCounts.put(AltType.DELETION, delCount);
    	typeCounts.put(AltType.STRUCTURAL_INDEL, structIndelCount);
    	typeCounts.put(AltType.STRUCTURAL_INSERTION, structInsCount);
    	typeCounts.put(AltType.STRUCTURAL_DELETION, structDelCount);
    	
    	return typeCounts;
    }
    
	/**
	 * Determine whether the alt is a SNV, MNP, insertion, deletion
	 * or structural insertion or deletion.
	 * 
	 * @param ref
	 * @param alt
	 * @return
	 */
	private static AltType getAltType(Allele ref, Allele alt){
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
	 * Count the differences between a ref and alt
	 * 
	 * @param ref
	 * @param alt
	 * @return
	 */
	private static int getDiffCount(Allele ref, Allele alt){
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
	 * Given a list of alt alleles, create a comma-separated string
	 * with all alts
	 * 
	 * @param alts
	 * @return
	 */
	private static String generateAltString(List<Allele> alts){
		
		StringBuilder altString = new StringBuilder();
		for(int i = 0; i < alts.size(); i++){
			if(i == 0){
				altString.append(alts.get(i));
			}
			else{
				altString.append("," + alts.get(i));
			}
		}
		return altString.toString();
	}
	
	/**
	 * Determine if the SNV is a transition.
	 * 
	 * @param ref
	 * @param alt
	 * @return
	 */
	private static boolean isTransition(String ref, String alt){
		if(ref.length() > 1 || alt.length() > 1){
	        throw new RuntimeException("Something is very wrong! Expected single nucleotide" +
	        		" reference and alternate! Got: " + ref + ">" + alt);
		}
			
		if (alt.equals("G") && ref.equals("A")) {
			return true;
		} else if (alt.equals("A") && ref.equals("G")) {
			return true;
		} else if (alt.equals("T") && ref.equals("C")) {
			return true;
		} else if (alt.equals("C") && ref.equals("T")) {
			return true;
		}
		return false;
	}
}
