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
import vtc.tools.utilitybelt.UtilityBelt;
import vtc.tools.varstats.AltType;
import vtc.tools.varstats.Depth;
import vtc.tools.varstats.VariantPoolSummary;
import vtc.tools.varstats.VariantRecordSummary;

/**
 * @author markebbert
 *
 */
public class VariantPoolSummarizer {
    
    public VariantPoolSummarizer(){}
    
    /**
     * Summarize statistics for a set of VariantPools.
     * 
     * @param allVPs
     * @return HashMap<String, VariantPoolSummary>
     */
    public static HashMap<String, VariantPoolSummary> summarizeVariantPools(TreeMap<String, VariantPool> allVPs){
    	
    	VariantPoolSummary vpSummary;
    	HashMap<String, VariantPoolSummary> vpSummaries = new HashMap<String, VariantPoolSummary>();
    	for(VariantPool vp : allVPs.values()){
    		vpSummary = summarizeVariantPool(vp);
    		vpSummaries.put(vp.getPoolID(), vpSummary);
    	}
    	return vpSummaries;
    }
    
    /**
     * Summarize statistics in a single VariantPool
     * @param vp
     * @return
     */
    public static VariantPoolSummary summarizeVariantPool(VariantPool vp){
    	
		Iterator<String> varIT = vp.getVariantIterator();
		String currVarKey;
		VariantContext var;
		VariantRecordSummary vrs;
		ArrayList<Allele> allInsertions = new ArrayList<Allele>(), allDeletions = new ArrayList<Allele>();
    	int totalVarCount = 0, snvCount = 0, mnvCount = 0, indelCount = 0, insCount = 0,
    			delCount = 0, structIndelCount = 0, structInsCount = 0, structDelCount = 0,
    			multiAltCount = 0, tiCount = 0, tvCount = 0, genoTiCount = 0, genoTvCount = 0;
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
				vrs = collectVariantStatistics(var);
				
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
				genoTiCount += vrs.getGenoTiCount();
				genoTvCount += vrs.getGenoTvCount();
				
				// Keep track of all alternates that were insertions and deletions
				// to calculate shortest, longest, and average length
				allInsertions.addAll(vrs.getInsertions());
				allDeletions.addAll(vrs.getDeletions());

			}
		}
		int smallestIns = UtilityBelt.getSmallestLength(allInsertions);
		int longestIns = UtilityBelt.getLargestLength(allInsertions);
		double avgIns = UtilityBelt.getAverageLength(allInsertions);

		int smallestDel = UtilityBelt.getSmallestLength(allDeletions);
		int longestDel = UtilityBelt.getLargestLength(allDeletions);
		double avgDel = UtilityBelt.getAverageLength(allDeletions);
		
		double tiTv = (double)tiCount/(double)tvCount;
		double genoTiTv = (double)genoTiCount/(double)genoTvCount;

		return new VariantPoolSummary(totalVarCount, snvCount, mnvCount, indelCount, insCount, delCount,
				smallestIns, longestIns, avgIns, smallestDel, longestDel, avgDel,
				structIndelCount, structInsCount, structDelCount, multiAltCount,
				tiCount, tvCount, tiTv, genoTiCount, genoTvCount, genoTiTv);
    }
    
    /**
     * Count the different alternate types for a single record. i.e., count the
     * number of SNVs, MNPs, insertions, deletions, structural insertions, and
     * structural deletions in a given record.
     * 
     * @param var
     * @return
     */
    private static VariantRecordSummary collectVariantStatistics(VariantContext var){

    	Allele ref = var.getReference();
    	List<Allele> alts = var.getAlternateAlleles();
    	
    	Integer snvCount = 0, mnvCount = 0, indelCount = 0, insCount = 0,
    			delCount = 0, structIndelCount = 0, structInsCount = 0,
    			structDelCount = 0, tiCount = 0, tvCount = 0;
		AltType type;
		VariantRecordSummary vrs = new VariantRecordSummary(var.getChr(), var.getStart(),
				var.getReference(), new ArrayList<Allele>(var.getAlternateAlleles()));
    	for(Allele alt : alts){
    		type = UtilityBelt.determineAltType(ref, alt);
    		
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
    			mnvCount++;
    		}
    		else if(type == AltType.INSERTION){
    			indelCount++;
    			insCount++;
    			vrs.addInsertion(alt);
    		}
    		else if(type == AltType.DELETION){
    			indelCount++;
    			delCount++;
    			vrs.addDeletion(alt);
    		}
    		else if(type == AltType.STRUCTURAL_INSERTION){
    			structIndelCount++;
    			structInsCount++;
    			vrs.addInsertion(alt);
    		}
    		else if(type == AltType.STRUCTURAL_DELETION){
    			structIndelCount++;
    			structDelCount++;
    			vrs.addDeletion(alt);
    		}
    	}
    	
    	vrs.setSnvCount(snvCount);
    	vrs.setMnvCount(mnvCount);
    	vrs.setIndelCount(indelCount);
    	vrs.setInsCount(insCount);
    	vrs.setDelCount(delCount);
    	vrs.setStructIndelCount(structIndelCount);
    	vrs.setStructInsCount(structInsCount);
    	vrs.setStructDelCount(structDelCount);
    	vrs.setTiCount(tiCount);
    	vrs.setTvCount(tvCount);
    	
		generateGenotypeStatsForVariantRecord(vrs, var, new ArrayList<String>(var.getSampleNamesOrderedByName()));
		return vrs;
    }
    
    
	/**
	 * Generate a detailed stat summary for a given variant.
	 * 
	 * @param var
	 * @param Samples
	 * @return
	 */
	private static void generateGenotypeStatsForVariantRecord(VariantRecordSummary vrs, VariantContext var, ArrayList<String> samples) {

		Allele ref = var.getReference();
		List<Allele> alts = var.getAlternateAlleles();

		int refCount = 0, tmpAltCount, genoTiCount = 0, genoTvCount = 0;
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
				
				// Get ti/tv info too, but only vor SNVs
				if(UtilityBelt.determineAltType(ref, alts.get(i)) == AltType.SNV){
					if(isTransition(ref.getBaseString(), alts.get(i).getBaseString())){
						genoTiCount++;
					}
					else{
						genoTvCount++;
					}
				}
			}
		}
		
		vrs.setRefGenotypeCount(refCount);
		vrs.setAltGenotypeCounts(altCounts);
		vrs.setGenoTiCount(genoTiCount);
		vrs.setGenoTvCount(genoTvCount);
		
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
//		VariantPoolSummary vps = new VariantPoolSummary();
		for(Object o : keys){
			if(PrintCombined == false){
				PrintIndividualFiles(o.toString(), VPSummary.get(o));
			}
			else{
//				vps.addition(VPSummary.get(o));
			}
			
			
		}
		if(PrintCombined == true){
//			PrintCombinedStats(keys, vps);
		}
		
	}

	private static void PrintCombinedStats(Object[] keys, VariantPoolSummary vps) {
		// TODO Auto-generated method stub
		
	}

	private static void PrintIndividualFiles(String string,	VariantPoolSummary variantPoolSummary) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
