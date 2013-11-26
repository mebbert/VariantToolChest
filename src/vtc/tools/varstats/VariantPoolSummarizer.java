/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;
import vtc.tools.utilitybelt.UtilityBelt;

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
    		vpSummary.setNumSamples(vp.getSamples().size());
    		//vpSummaries.put(vp.getPoolID(), vpSummary);
    		vpSummaries.put(vp.getFile().getName(), vpSummary);
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
		TreeSet<String> allInsertions = new TreeSet<String>(), allDeletions = new TreeSet<String>();
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

		return new VariantPoolSummary(vp.getNumVarRecords(), totalVarCount, snvCount, mnvCount, indelCount, insCount, delCount,
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
    			vrs.addInsertion(alt.getBaseString());
    		}
    		else if(type == AltType.DELETION){
    			indelCount++;
    			delCount++;
    			vrs.addDeletion(ref.getBaseString()); // input the ref allele since that's what was deleted
    		}
    		else if(type == AltType.STRUCTURAL_INSERTION){
    			structIndelCount++;
    			structInsCount++;
    			vrs.addInsertion(alt.getBaseString());
    		}
    		else if(type == AltType.STRUCTURAL_DELETION){
    			structIndelCount++;
    			structDelCount++;
    			vrs.addDeletion(ref.getBaseString()); // input the ref allele since that's what was deleted
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
	
	public static void printSummary(HashMap<String, VariantPoolSummary> vpSummaries, boolean PrintCombined){
		Object[] keys = vpSummaries.keySet().toArray();
		VariantPoolSummary vps = new VariantPoolSummary();
		for(Object o : keys){
			if(PrintCombined == false){
				PrintIndividualFiles(o.toString(), vpSummaries.get(o));
			}
			else{
				vps.addition(vpSummaries.get(o));
			
			}
			double ti = vps.getTiTv();
			double tv = vps.getTvCount();
			vps.setTiTv(ti/tv);
			double genoti = vps.getGenoTiCount();
			double genotv = vps.getGenoTvCount();
			vps.setGenoTiTv(genoti/genotv);

			
		}
		if(PrintCombined == true){
			PrintCombinedStats(keys, vps);
		}
		
	}

	private static void PrintCombinedStats(Object[] keys, VariantPoolSummary vps) {
		int length = vps.longest_length();
		String newLine = System.getProperty("line.separator");

		String title;
		title = "Summary of: " + keys[0];
		
		length += 20;
		
		
		
		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);	
		String leftalignFormats = " %-" + (length--) + "s" + newLine;
		
		System.out.format(t + newLine);
		
		int pos = 0;

		System.out.format(leftalignFormats, "");
		for (Object vpfile : keys) {
			if (pos > 0)
				title = "            " + vpfile.toString();
			pos++;
			System.out.format(leftalignFormats, title);
		}
		System.out.format(leftalignFormats, "");
		System.out.format(t + newLine);
		System.out.format(newLine);
		printFiles(length, vps);
	}

	private static void PrintIndividualFiles(String file,	VariantPoolSummary vps) {
		int length = vps.longest_length();
		String newLine = System.getProperty("line.separator");

		String title;
		title = "Summary of: " + file;
	
		length += 15+file.length();
		
		
		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);
//		int LeftColumn = 15;
		String leftalignFormats = " %-" + (length--) + "s" + newLine;
		System.out.format(t + newLine);
		System.out.format(leftalignFormats, "");
		System.out.format(leftalignFormats, title);
		System.out.format(leftalignFormats, "");
		System.out.format(t + newLine);
		System.out.format(newLine);
		printFiles(length, vps);
	}
	
	private static void printFiles(int length, VariantPoolSummary vps) {

		String newLine = System.getProperty("line.separator");


		char[] chars = new char[length + 1];
		Arrays.fill(chars, '-');
		String s = new String(chars);
		s = "+" + s + "+";
/*
		double snvPercent = (double) vps.getNumSNVs() / (double) vps.getNumVars() * 100;
		double InDelsPercent = (double) vps.getInDels() / (double) vps.getNumVars() * 100;
		double StructPercent = (double) vps.getStructVars() / (double) vps.getNumVars() * 100;
*/
		int LeftColumn = 15;

		String leftalignFormatint = "|%-" + LeftColumn + "s%" + (length - LeftColumn) + "d |" + newLine;
		//String leftalignFormatd = "|%-" + LeftColumn + "s%" + (length - LeftColumn) + ".2f |" + newLine;
		String rightalignFormati = "|%" + LeftColumn + "s%" + (length - LeftColumn) + "s |" + newLine;
		String rightalignFormatf = "|%" + LeftColumn + "s%" + (length - LeftColumn) + ".2f |" + newLine;
		//String rightalignFormats = "|%" + LeftColumn + "s%" + (length - LeftColumn) + "s |" + newLine;
//		String leftalignFormats = " %-" + (length--) + "s" + newLine;
		//String leftAlignError = " %-" + length + "s" + newLine;

		
		System.out.format(s + newLine);
		System.out.format(leftalignFormatint, "TotalVars:", vps.getNumVars());
		System.out.format(leftalignFormatint, "Total Samples:", vps.getNumSamples());
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "SNVs:      ", Integer.toString(vps.getNumSNVs()));
		System.out.format(rightalignFormatf, "Ti/Tv:", vps.getTiTv());
		System.out.format(rightalignFormatf, "(Geno)Ti/Tv:", vps.getGenoTiTv());
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "MNVs:      ", Integer.toString(vps.getNumMNVs()));
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "INDELs:    ", Integer.toString(vps.getNumIndels()));
		System.out.format(rightalignFormati, "INS:", Integer.toString(vps.getNumInsertions()));
		System.out.format(rightalignFormati, "DEL:", Integer.toString(vps.getNumDeletions()));

		System.out.format(rightalignFormati, "Sizes:    ", "");
		System.out.format(rightalignFormati, "smallINS:", UtilityBelt.roundDouble(vps.getSmallestInsertion()));
		System.out.format(rightalignFormati, "largINS:", UtilityBelt.roundDouble(vps.getLargestInsertion()));
		System.out.format(rightalignFormati, "avgINS:", UtilityBelt.roundDouble(vps.getAvgInsertionSize()));
		System.out.format(rightalignFormati, "smallDEL:", UtilityBelt.roundDouble(vps.getSmallestDeletion()));
		System.out.format(rightalignFormati, "largeDEL:", UtilityBelt.roundDouble(vps.getLargestDeletion()));
		System.out.format(rightalignFormati, "avgDEL:", UtilityBelt.roundDouble(vps.getAvgDeletionSize()));
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "StructVars:", Integer.toString(vps.getNumStructVars()));
		System.out.format(rightalignFormati, "StructINS:", Integer.toString(vps.getNumStructIns()));
		System.out.format(rightalignFormati, "StructDEL:", Integer.toString(vps.getNumStructDels()));
		System.out.format(s + newLine);
		System.out.format(leftalignFormatint, "MultiAlts:", vps.getNumMultiAlts());
		System.out.format(s + newLine);
	
		System.out.format(newLine + newLine);

	
	}
	
	
	public static void PrintSide_by_Side(HashMap<String, VariantPoolSummary>  Summaries){
		Object[] filenames = Summaries.keySet().toArray();
		int size = filenames.length;
		int length = -1;
		int namesize = -1;
		for(int i = 0;i<size;i++){
			int temp= Summaries.get(filenames[i]).longest_length();
			if(length<temp)
				length = temp;
			if(namesize<filenames[i].toString().length())
				namesize = filenames[i].toString().length();
		}
		
		String newLine = System.getProperty("line.separator");
		
		
		String title;
		
		
		length += 15+namesize;
		
		

		char[] chars = new char[length + 1];
		Arrays.fill(chars, '-');
		String s = new String(chars);
		s = "+" + s + "+";
		
		
		
		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);
		
		int LeftColumn = 15;
		
		String leftalignFormats = " %-" + (length--) + "s" + "\t";
		String leftalignFormatint = "|%-" + LeftColumn + "s%" + (length - LeftColumn) + "d |" + "\t";
		String rightalignFormati = "|%" + LeftColumn + "s%" + (length - LeftColumn) + "s |" + "\t";
		String rightalignFormatf = "|%" + LeftColumn + "s%" + (length - LeftColumn) + ".2f |" + "\t";
		
		for(int i = 0; i<size;i++)
			System.out.format(t + "\t");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormats, "");

		System.out.format(newLine);
		
		for(int i = 0; i<size;i++){
			title = "Summary of: " + filenames[i];
			System.out.format(leftalignFormats, title);
		}
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormats, "");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(t + "\t");
		
		System.out.format(newLine);
		
		

		
		for(int i = 0; i<size;i++)
			System.out.format(s + "\t");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormatint, "TotalVars:", Summaries.get(filenames[i]).getNumVars());
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormatint, "Total Samples:", Summaries.get(filenames[i]).getNumSamples());
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "\t");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "SNVs:      ", Integer.toString(Summaries.get(filenames[i]).getNumSNVs()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormatf, "Ti/Tv:", Summaries.get(filenames[i]).getTiTv());
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormatf, "(Geno)Ti/Tv:", Summaries.get(filenames[i]).getGenoTiTv());
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "\t");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "MNVs:      ", Integer.toString(Summaries.get(filenames[i]).getNumMNVs()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "\t");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "INDELs:    ", Integer.toString(Summaries.get(filenames[i]).getNumIndels()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "INS:", Integer.toString(Summaries.get(filenames[i]).getNumInsertions()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "DEL:", Integer.toString(Summaries.get(filenames[i]).getNumDeletions()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "smallINS:", UtilityBelt.roundDouble(Summaries.get(filenames[i]).getSmallestInsertion()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "largINS:", UtilityBelt.roundDouble(Summaries.get(filenames[i]).getLargestInsertion()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "avgINS:", UtilityBelt.roundDouble(Summaries.get(filenames[i]).getAvgInsertionSize()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "smallDEL:", UtilityBelt.roundDouble(Summaries.get(filenames[i]).getSmallestDeletion()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "largeDEL:", UtilityBelt.roundDouble(Summaries.get(filenames[i]).getLargestDeletion()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "avgDEL:", UtilityBelt.roundDouble(Summaries.get(filenames[i]).getAvgDeletionSize()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "\t");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "StructVars:", Integer.toString(Summaries.get(filenames[i]).getNumStructVars()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "StructINS:", Integer.toString(Summaries.get(filenames[i]).getNumStructIns()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "StructDEL:", Integer.toString(Summaries.get(filenames[i]).getNumStructDels()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "\t");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormatint, "MultiAlts:", Summaries.get(filenames[i]).getNumMultiAlts());
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "\t");
		
		System.out.format(newLine);
	
		
		System.out.format(newLine + newLine);

		
		
		
	}
	
	
	
	
}
