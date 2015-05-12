/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import vtc.datastructures.NaturalOrderComparator;

/**
 * @author markebbert
 *
 */
public class VariantPoolDetailedSummary extends VariantPoolSummary {
	private static Logger logger = Logger.getLogger(VariantPoolDetailedSummary.class);

	TreeMap<String, VariantRecordSummary> recordSummariesTree;
	HashMap<String, VariantRecordSummary> recordSummariesHash;
	HashMap<String, ArrayList<VariantRecordSummary>> recordSummariesHashChrPos;
	
	/**
	 * @param numVarRecords
	 * @param numVars
	 * @param numSNVs
	 * @param numMNVs
	 * @param numIndels
	 * @param numInsertions
	 * @param numDeletions
	 * @param smallestInsertion
	 * @param largestInsertion
	 * @param avgInsertionSize
	 * @param smallestDeletion
	 * @param largestDeletion
	 * @param avgDeletionSize
	 * @param numStructVars
	 * @param numStructIns
	 * @param numStructDels
	 * @param numMultiAlts
	 * @param tiCount
	 * @param tvCount
	 * @param tiTv
	 * @param genoTiCount
	 * @param genoTvCount
	 * @param genoTiTv
	 */
	public VariantPoolDetailedSummary(int numRecords, int numVarRecords, int numSamples, int numVars,
			int numSNVs, int numMNVs, int numStructVars, int numStructIns,
			int numStructDels, int numMultiAlts, double tiCount,
			double tvCount, double tiTv, double genoTiCount,
			double genoTvCount, double genoTiTv) {
		super(numRecords, numVarRecords, numSamples, numVars, numSNVs, numMNVs, numStructVars, numStructIns,
				numStructDels, numMultiAlts, tiCount, tvCount, genoTiCount, genoTvCount/*,
				allInsertions, allDeletions*/);
		init();
	}

	/**
	 * 
	 */
	public VariantPoolDetailedSummary(VariantPoolSummary vps) {
		super(vps.getNumRecords(), vps.getNumVarRecords(), vps.getNumSamples(), vps.getNumVars(), vps.getNumSNVs(), vps.getNumMNVs(),
				vps.getNumStructVars(), vps.getNumStructIns(), vps.getNumStructDels(), vps.getNumMultiAlts(),
				vps.getTiCount(), vps.getTvCount(), vps.getGenoTiCount(), vps.getGenoTvCount(), vps.getIndelCount(), vps.getInsCount(), vps.getDelCount(),vps.getInsSum(),
				vps.getDelSum(), vps.getStructInsSum(), vps.getStructDelSum(), vps.getSmallestIns(), vps.getSmallestDel(),
				vps.getLargestIns(), vps.getLargestDel(), vps.getSmallestStructIns(), vps.getSmallestStructDel(),
				vps.getLargestStructIns(), vps.getLargestStructDel());
		init();
	}
	
	private void init(){
		this.recordSummariesTree = new TreeMap<String, VariantRecordSummary>(new NaturalOrderComparator());
		this.recordSummariesHash = new HashMap<String, VariantRecordSummary>();
		this.recordSummariesHashChrPos = new HashMap<String, ArrayList<VariantRecordSummary>>();
	}
	
	/**
	 * Add a VariantRecordSummary
	 * 
	 * @param vrs
	 */
	public void addVariantRecordSummary(VariantRecordSummary vrs){
		String vrsKey = vrs.getChr() + ":" + vrs.getPosition() + ":" + vrs.getRef().getBaseString();
		String vrsKeyChrPos = vrs.getChr() + ":" + vrs.getPosition();
		this.recordSummariesTree.put(vrsKey, vrs);
		this.recordSummariesHash.put(vrsKey, vrs);
		
		/* Keep a list of all VariantRecordSummary objects at a given chr:pos. */
		ArrayList<VariantRecordSummary> tmpVrsList = this.recordSummariesHashChrPos.get(vrsKeyChrPos);
		if(tmpVrsList == null){
			ArrayList<VariantRecordSummary> vrsList = new ArrayList<VariantRecordSummary>();
			vrsList.add(vrs);
			this.recordSummariesHashChrPos.put(vrsKeyChrPos, vrsList);
		}
		else{
			tmpVrsList.add(vrs);
		}
	}
	
	/**
	 * Look up a VariantRecordSummary based on chromosome, position, and reference allele
	 * @param chr
	 * @param position
	 * @param ref
	 * @return
	 */
	public VariantRecordSummary getVariantRecordSummary(String chr, String position, String ref){
		return this.recordSummariesHash.get(chr + ":" + position + ":" + ref);
	}
	
	/**
	 * Look up a VariantRecordSummary based on chromosome, position, and reference allele.
	 * 'id' should be in the form of chr:pos:ref
	 * @param chr
	 * @param position
	 * @param ref
	 * @return
	 */
	public VariantRecordSummary getVariantRecordSummary(String id){
		return this.recordSummariesHash.get(id);
	}
	
	/**
	 * Look up a list of VariantRecordSummary objects based on chromosome and
	 * position
	 * 'chrPos' should be in the form of chr:pos
	 * @param chrPos
	 * @return
	 */
	public ArrayList<VariantRecordSummary> getVariantRecordSummariesAtLocus(String chrPos){
		return this.recordSummariesHashChrPos.get(chrPos);
	}
	
	/**
	 * Return the list of VariantRecordSummary
	 * @return ArrayList<VariantRecordSummary>
	 */
	public TreeMap<String, VariantRecordSummary> getVariantRecordSummaries(){
		return this.recordSummariesTree;
	}
	
	/**
	 * Add two VariantPoolDetailedSummary objects together.
	 * @param s1
	 * @param s2
	 * @return a new VariantPoolDetailedSummary object after adding s1 and s2 together
	 */
	public static VariantPoolDetailedSummary addVariantPoolDetailedSummaries(VariantPoolDetailedSummary s1,
			VariantPoolDetailedSummary s2){
		
		logger.info("Adding VariantPoolDetailedSummaries together.");
		
		/* Add all of the values specific to general VariantPoolSummary objects */
		VariantPoolDetailedSummary newVPDS = new VariantPoolDetailedSummary(VariantPoolSummary.addVariantPoolSummaries(s1, s2));
		VariantRecordSummary vrs, tmpVrs;
		
		/* Loop over VariantRecordSummary objects in s1 */
		for(String vrsKey : s1.getVariantRecordSummaries().keySet()){

			/* Get the corresponding VariantRecordSummary from both s1 and s2, if possible */
			vrs = s1.getVariantRecordSummary(vrsKey);
			tmpVrs = s2.getVariantRecordSummary(vrsKey);
			
			/* If s2, had the same VariantRecordSummary, add them together */
			if(tmpVrs != null){
				newVPDS.addVariantRecordSummary(VariantRecordSummary.addVariantRecordSummaries(vrs, tmpVrs));
			}
			
			/* s2 did not have the VariantRecordSummary. */
			else{
				
				/* If s2 had coverage at this locus (chr:pos) for any ref or alt allele,
				 * increment the number of samples that have ANY call at this locus.
				 */
				if(hasCoverageAtLocus(s2, vrs)){
					ArrayList<VariantRecordSummary> vrsList =
							s2.getVariantRecordSummariesAtLocus(vrs.getChr() + ":" + vrs.getPosition());
					if(vrsList.size() > 0){
                        vrs.setnSamplesWithCall(vrs.getnSamplesWithCall() + vrsList.get(0).getnSamplesWithCall());
					}
				}
				
				/* Also increment the total number of samples we've seen. This
				 * value includes those without coverage at this locus
				 */
				vrs.setnSamples(vrs.getnSamples() + 1);
				newVPDS.addVariantRecordSummary(vrs);
			}
			
		}
		
		for(String vrsKey : s2.getVariantRecordSummaries().keySet()){
			vrs = s2.getVariantRecordSummary(vrsKey);
			tmpVrs = newVPDS.getVariantRecordSummary(vrsKey);
			if(vrs.getPosition() == 189877298){
				logger.info("HERE");
			}
			if(tmpVrs != null){
				continue; // These were already combined in the above loop, so just continue
		}
			else{
				if(hasCoverageAtLocus(newVPDS, vrs)){
					ArrayList<VariantRecordSummary> vrsList =
							newVPDS.getVariantRecordSummariesAtLocus(vrs.getChr() + ":" + vrs.getPosition());
					if(vrsList.size() > 0){
						
						/* Set the number of samples with call to be those from another
						 * variant at the same location because this will have already
						 * been added to the other in the previous loop. 
						 */
                        vrs.setnSamplesWithCall(vrsList.get(0).getnSamplesWithCall());
                        vrs.setnSamples(vrsList.get(0).getnSamples());
					}
				}
				else{
                    vrs.setnSamples(vrs.getnSamples() + 1);
				}
				newVPDS.addVariantRecordSummary(vrs);
			}
		}
		
		
//		NaturalOrderComparator noc = new NaturalOrderComparator();
//		
//		ArrayList<VariantRecordSummary> s1VRSs = s1.getVariantRecordSummaries();
//		ArrayList<VariantRecordSummary> s2VRSs = s2.getVariantRecordSummaries();
//		VariantRecordSummary vrs1, vrs2;
//		int j = 0;
//		for(int i = 0; i < s1VRSs.size(); i++){
//			vrs1 = s1VRSs.get(i);
//			for(; j < s2VRSs.size(); j++){
//				vrs2 = s2VRSs.get(j);
//				
//				if(vrs1.getPosition() == 90702500 || vrs2.getPosition() == 90702500){
//					logger.info("HERE");
//				}
//
//                /* If variant records match by chr:pos:ref, add 
//                 * together and put in newVPDS
//                 */
//				if(vrs1.getID().equals(vrs2.getID())){ 
//					newVPDS.addVariantRecordSummary(VariantRecordSummary.addVariantRecordSummaries(vrs1, vrs2));
//				}
//				
//				/* If the two records have same chr and pos, but different
//				 * Ref, add both to the newVPDS
//				 */
//				else if(vrs1.getPosition() == vrs2.getPosition()
//						&& vrs1.getChr().equals(vrs2.getChr()) ){
//					
//					if(!hasCorrespondingRecord(s1, vrs2)){
//						if(hasCoverageAtLocus(s1, vrs2)){
//							vrs2.setnSamplesWithCall(vrs2.getnSamplesWithCall() + 1);
//						}
//						vrs2.setnSamples(vrs2.getnSamples() + 1);
//                        newVPDS.addVariantRecordSummary(vrs2);
//					}
//					
//					if(!hasCorrespondingRecord(s2, vrs1)){
//						if(hasCoverageAtLocus(s2, vrs1)){
//							vrs1.setnSamplesWithCall(vrs1.getnSamplesWithCall() + 1);
//						}
//						vrs1.setnSamples(vrs1.getnSamples() + 1);
//                        newVPDS.addVariantRecordSummary(vrs1);
//					}
//					break;
//				}
//			
//				/* If the chr:pos for vrs1 is < vrs2, break and
//				 * increment vrs1.
//				 */
//				else if(vrs1.getPosition() < vrs2.getPosition()
//						|| noc.compare(vrs1.getChr(), vrs2.getChr()) < 0){
//					
//					/* Since vrs2 has moved past vrs1's position, check if
//					 * s2 even has a corresponding record. If not, add
//					 * vrs1 to newVPDS
//					 */
//					if(!hasCorrespondingRecord(s2, vrs1)){
//						
//						/* Check if s2 was able to make calls at this
//						 * position by checking for any record at this
//						 * position. If so, increment the nSamplesWithCalls
//						 */
//						if(hasCoverageAtLocus(s2, vrs1)){
//							vrs1.setnSamplesWithCall(vrs1.getnSamplesWithCall() + 1);
//						}
//						
//						/* Always increment the total number of samples since this
//						 * measures how many calls were attempted for at this position.
//						 */
//						vrs1.setnSamples(vrs1.getnSamples() + 1);
//						newVPDS.addVariantRecordSummary(vrs1);
//					}
//                    break; // break to get next vrs1
//				}	
//
//				/* If the chr:pos for vrs1 is greater than vrs2, continue
//				 * iterating over s2
//				 */
//				else if(vrs1.getPosition() > vrs2.getPosition()
//						|| noc.compare(vrs1.getChr(), vrs2.getChr()) > 0){
//					
//					/* Check if s1 even has a corresponding record. If not,
//					 * add vrs2 to newVPDS
//					 */
//					if(!hasCorrespondingRecord(s1, vrs2)){
//						
//						/* Check if s1 was able to make calls at this
//						 * position by checking for any record at this
//						 * position. If so, increment the nSamplesWithCalls
//						 */
//						if(hasCoverageAtLocus(s1, vrs2)){
//							vrs2.setnSamplesWithCall(vrs2.getnSamplesWithCall() + 1);
//						}
//						
//						/* Always increment the total number of samples since this
//						 * measures how many calls were attempted for at this position.
//						 */
//						vrs2.setnSamples(vrs2.getnSamples() + 1);
//						newVPDS.addVariantRecordSummary(vrs2);
//					}
//					continue; // continue to get next vrs2
//				}
//
//				/* Must have missed an important case. Throw error. */
//				else{
//					throw new RuntimeException("ERROR: Unexpected situation when adding VariantPoolDetailedSummaries." +
//							" Looking at the two following VariantRecordSummaries: " + vrs1.getID() + " and "
//							+ vrs2.getID());
//				}
//			}
//		}
		
		/* Add all information on the variant record level. Determine which
		 * VariantPoolDetailedSummary has the fewest records. Loop over those
		 * records and add values together where both objects have a record
		 */
//		boolean s1Smallest = (s1.getNumVarRecords() < s2.getNumVarRecords()) ? true : false;
//		VariantRecordSummary tmp;
//		if(s1Smallest){
//            for(VariantRecordSummary vrs : s1.getVariantRecordSummaries()){
//            	tmp = s2.getVariantRecordSummary(vrs.getChr(), Integer.toString(vrs.getPosition()), vrs.getRef().getBaseString());
//            	if(tmp == null){
////            		vrs.setnSamples(vrs.getnSamples() + 1); // Increment the total number of samples
//            		newVPDS.addVariantRecordSummary(vrs);
//            		continue;
//            	}
//            	newVPDS.addVariantRecordSummary(VariantRecordSummary.addVariantRecordSummaries(vrs, tmp));
//            }
//		}
//		else{
//			for(VariantRecordSummary vrs : s2.getVariantRecordSummaries()){
//				tmp = s1.getVariantRecordSummary(vrs.getChr(), Integer.toString(vrs.getPosition()), vrs.getRef().getBaseString());
//				if(tmp == null){
////            		vrs.setnSamples(vrs.getnSamples() + 1);
//            		newVPDS.addVariantRecordSummary(vrs);
//					continue;
//				}
//				newVPDS.addVariantRecordSummary(VariantRecordSummary.addVariantRecordSummaries(vrs, tmp));
//			}
//		}
        return newVPDS;
	}
//	
//	/**
//	 * Check if vpsd has a record corresponding to the same chr:pos:ref as vrs
//	 * @param vpsd
//	 * @param vrs
//	 * @return
//	 */
//	private static boolean hasCorrespondingRecord(VariantPoolDetailedSummary vpsd, VariantRecordSummary vrs){
//
//		/* Check if vpsd has a corresponding record. */
//		if(vpsd.getVariantRecordSummary(vrs.getID()) == null){
//			return false;
//		}
//		return true;
//	}
	
	/**
	 * Check if vpsd has any callable locus at the same chr:pos as vrs. If so, we assume
	 * the sample had coverage at this locus. This includes indels of ANY size.
	 * 
	 * @param vpsd
	 * @param vrs
	 * @return
	 */
	private static boolean hasCoverageAtLocus(VariantPoolDetailedSummary vpsd, VariantRecordSummary vrs){
		
		/* Check if vpsd has any callable locus at the corresponding
		 * same chr:pos as vrs
		 */
		if(vpsd.getVariantRecordSummariesAtLocus(vrs.getChr() + ":" + vrs.getPosition()).size() > 0){
			return true;
		}
		return false;
	}
	
}
