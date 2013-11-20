/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;
import vtc.tools.varstats.AltType;
import vtc.tools.varstats.Depth;
import vtc.tools.varstats.VariantPoolSummary;
import vtc.tools.varstats.VariantRecordSummary;

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
    public static VariantPoolSummary summarizeVariantPool(VariantPool vp, boolean generateDetailedStats){
    	
		Iterator<String> varIT = vp.getVariantIterator();
		String currVarKey;
		VariantContext var;
		VariantRecordSummary vrs;
    	int totalVarCount = 0, snvCount = 0, mnvCount = 0, indelCount = 0, insCount = 0,
    			delCount = 0, structIndelCount = 0, structInsCount = 0, structDelCount = 0,
    			multiAltCount = 0, tiCount = 0, tvCount = 0;
		while (varIT.hasNext()) {
			currVarKey = varIT.next();

			var = vp.getVariant(currVarKey);
			if (var.isVariant()) {
				totalVarCount++; // Increment the total var counts by one for every record
				
				if(var.getAlternateAlleles().size() > 1){
					multiAltCount++;
				}

				/* Increment the total var count by the number of alts - 1. This
				 * will keep the total count equal to the number of counted alts
				 * when there are multiple alts in a single record.
				 */
				totalVarCount += var.getAlternateAlleles().size() - 1; 

				// Count the different types of alternates for a single record
				vrs = collectVariantStatistics(var, generateDetailedStats);
				
				snvCount += vrs.getSnvCount();
				mnvCount += vrs.getMnvCount();
				indelCount += vrs.getIndelCount();
				insCount += vrs.getInsCount();
				delCount += vrs.getDelCount();
				structIndelCount += vrs.getStructIndelCount();
				structInsCount += vrs.getStructInsCount();
				structDelCount += vrs.getStructDelCount();
				tiCount += vrs.getTiCount();
				tvCount += vrs.getTvCount();

			}
		}
		return new VariantPoolSummary(totalVarCount, snvCount, mnvCount, indelCount, insCount, delCount, 0, 0, 0, 0, 0, 0,
				structIndelCount, structInsCount, structDelCount, multiAltCount, tiCount, tvCount, 0, 0, 0, 0);
    }
    
    /**
     * Count the different alternate types for a single record. i.e., count the
     * number of SNVs, MNPs, insertions, deletions, structural insertions, and
     * structural deletions in a given record.
     * 
     * @param var
     * @return
     */
    private static VariantRecordSummary collectVariantStatistics(VariantContext var, boolean generateDetailedStats){

    	Allele ref = var.getReference();
    	List<Allele> alts = var.getAlternateAlleles();
    	
    	Integer snvCount = 0, mnpCount = 0, indelCount = 0, insCount = 0,
    			delCount = 0, structIndelCount = 0, structInsCount = 0,
    			structDelCount = 0, tiCount = 0, tvCount = 0;
		AltType type;
    	for(Allele alt : alts){
    		type = getAltType(ref, alt);
    		
    		if(type == AltType.SNV){
    			snvCount++;
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
    	
    	VariantRecordSummary vrs = new VariantRecordSummary(var.getChr(), var.getStart(), var.getReference(),
    			new ArrayList<Allele>(var.getAlternateAlleles()), snvCount, mnpCount, indelCount,
    			insCount, delCount, structIndelCount, structInsCount, structDelCount, tiCount,
    			tvCount);
    	
		if(generateDetailedStats){
			generateDetailedStatsForVariantRecord(vrs, var, (String[])var.getSampleNamesOrderedByName().toArray());
		}
		return vrs;
    }
    
    
	/**
	 * Generate a detailed stat summary for a given variant.
	 * 
	 * @param var
	 * @param Samples
	 * @return
	 */
	private static void generateDetailedStatsForVariantRecord(VariantRecordSummary vrs, VariantContext var, String[] samples) {

		Allele ref = var.getReference();
		List<Allele> alts = var.getAlternateAlleles();

		int refCount = 0, tmpAltCount;
		ArrayList<Integer> altCounts = new ArrayList<Integer>();
		Iterator<Genotype> genoIT = var.getGenotypes().iterator();
		Genotype geno;
		int iterCount = 0;
		
		// Loop over all of the genotypes in the record
		while(genoIT.hasNext()){
			geno = genoIT.next();
			
			// Get the reference allele count
			refCount += geno.countAllele(ref);

			// Get the count for each alternate allele
			for(int i = 0; i < alts.size(); i++){
				if(iterCount == 0){
					tmpAltCount = 0;
					altCounts.add(tmpAltCount);
				}
				else{
					tmpAltCount = altCounts.get(i);
				}
				tmpAltCount += geno.countAllele(alts.get(i));
				altCounts.set(i, tmpAltCount);
			}
		}
		
		vrs.setRefGenotypeCount(refCount);
		vrs.setAltGenotypeCounts(altCounts);
		
		Depth depth = new Depth();
		depth.getDepths(var, samples);
		
		vrs.setDepth(depth);
		
		double qual = var.getPhredScaledQual();
		if(qual>0)
			vrs.setQuality(Double.toString(qual));
		else
			vrs.setQuality("NA");
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
	
	public static void printSummary(HashMap<String, VariantPoolSummary> VPSummary, boolean PrintCombined){
		Object[] keys = VPSummary.keySet().toArray();
		VariantPoolSummary vps = new VariantPoolSummary();
		for(Object o : keys){
			if(PrintCombined == false){
				PrintIndividualFiles(o.toString(), VPSummary.get(o));
			}
			else{
				vps.addition(VPSummary.get(o));
			}
			
			
		}
		if(PrintCombined == true){
			PrintCombinedStats(keys, vps);
		}
		
	}

	private static void PrintCombinedStats(Object[] keys, VariantPoolSummary vps) {
		// TODO Auto-generated method stub
		
	}

	private static void PrintIndividualFiles(String string,	VariantPoolSummary variantPoolSummary) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
