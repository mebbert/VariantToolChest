/**
 * 
 */
package vtc.tools.varstats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;
import vtc.tools.setoperator.SetOperator;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.setoperator.operation.Operation;
import vtc.tools.setoperator.operation.OperationFactory;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author markebbert
 *
 */
public class VariantPoolSummarizer {
    
    private static Logger logger = Logger.getLogger(VariantPoolSummarizer.class);

    public VariantPoolSummarizer(){}
    
    /**
     * Provide a detailed summary for each VariantPool object
     * @param allVPs
     * @param combined
     * @return
     * @throws InvalidOperationException 
     */
    public static HashMap<String, VariantPoolDetailedSummary> summarizeVariantPoolsDetailed(TreeMap<String, VariantPool> allVPs)
    		throws InvalidOperationException{

    	HashMap<String, VariantPoolDetailedSummary> detailedVariantPoolSummaries =
    			new HashMap<String, VariantPoolDetailedSummary>();
    	
        VariantPoolDetailedSummary vpsd;
        for(VariantPool vp : allVPs.values()){
            vpsd = summarizeVariantPoolDetailed(vp);
            detailedVariantPoolSummaries.put(vp.getPoolID(), vpsd);
        }
    	return detailedVariantPoolSummaries;
    }
    
    /**
     * Provide a combined detailed summary of all VariantPool objects
     * 
     * @param allVPs
     * @throws InvalidOperationException 
     */
    public static VariantPoolDetailedSummary summarizeVariantPoolsDetailedCombined(TreeMap<String, VariantPool> allVPs) throws InvalidOperationException{

    	ArrayList<VariantPool> allVPsList = new ArrayList<VariantPool>(allVPs.values());
    	if(allVPsList.size() > 1){
    		String union = "", unionName = "combined";
    		VariantPool unionVP = null;
    		SetOperator so = new SetOperator();
    		
    		/* Build the operation string */
			union = unionName + "=u[";
    		for(int i = 0; i < allVPsList.size(); i++){
    			if(i == 0){
    				union += allVPsList.get(i).getPoolID();
    			}
    			else{
    				union += ":" + allVPsList.get(i).getPoolID();
    			}
    		}
            union += "]";
    			
    		Operation op = OperationFactory.createOperation(union, allVPs);
    		unionVP = so.performUnion(op, allVPsList, true);
    		
    		return summarizeVariantPoolDetailed(unionVP);
    	}   	
    	else if(allVPsList.size() == 1){
    		return summarizeVariantPoolDetailed(allVPsList.get(0));
    	}
    	else{
    		throw new RuntimeException("ERROR: There were no VariantPools to summarize!");
    	}
    }
    
    /**
     * Get VariantRecordSummaries for the given VariantPool
     * 
     * @param vp
     * @return
     */
    public static VariantPoolDetailedSummary summarizeVariantPoolDetailed(VariantPool vp){
    	Iterator<String> varIT = vp.getVariantIterator();
    	String currVarKey;
//    	ArrayList<VariantRecordSummary> summaries = new ArrayList<VariantRecordSummary>();
    	VariantPoolDetailedSummary vpds = new VariantPoolDetailedSummary(summarizeVariantPool(vp));
    	while(varIT.hasNext()){
    		currVarKey = varIT.next();
//    		summaries.add(collectVariantStatistics(vp.getVariant(currVarKey)));
    		vpds.addVariantRecordSummary(collectVariantStatistics(vp.getVariant(currVarKey)));
    	}
    	return vpds;
    }
    
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
    	int totalVarCount = 0, snvCount = 0, mnvCount = 0, structIndelCount = 0, structInsCount = 0, structDelCount = 0,
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
		
		return new VariantPoolSummary(vp.getNumVarRecords(), totalVarCount, snvCount, mnvCount,
				structIndelCount, structInsCount, structDelCount, multiAltCount,
				tiCount, tvCount, genoTiCount, genoTvCount, allInsertions, allDeletions);
    }
    
    /**
     * Count the different alternate types for a single record. i.e., count the
     * number of SNVs, MNPs, insertions, deletions, structural insertions, and
     * structural deletions in a given record.
     * 
     * @param var
     * @return
     */
    public static VariantRecordSummary collectVariantStatistics(VariantContext var){

    	Allele ref = var.getReference();
    	List<Allele> alts = var.getAlternateAlleles();
    	
    	Integer snvCount = 0, mnvCount = 0, indelCount = 0, insCount = 0,
    			delCount = 0, structIndelCount = 0, structInsCount = 0,
    			structDelCount = 0, tiCount = 0, tvCount = 0;
		AltType type;
		VariantRecordSummary vrs = new VariantRecordSummary(var.getChr(), var.getStart(),
				var.getReference(), new TreeSet<Allele>(var.getAlternateAlleles()));
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

		int refGenoCount = 0, refSampleCount = 0, tmpRefGenoCount,
				altGenoCount, altSampleCount, tmpAltGenoCount,
				genoTiCount = 0, genoTvCount = 0, nSamples = 0,
				nSamplesWithCall = 0, nGenosCalled = 0, hetSampleCount,
				homoVarSampleCount;
		HashMap<Allele, Integer> altGenoCounts = new HashMap<Allele, Integer>(),
				altSampleCounts = new HashMap<Allele, Integer>(),
				hetSampleCounts = new HashMap<Allele, Integer>(),
				homoVarSampleCounts = new HashMap<Allele, Integer>();
		Iterator<Genotype> genoIT = var.getGenotypes().iterator();
		Genotype geno;
		int iterCount = 0;
		
		// Loop over all of the genotypes in the record
		while(genoIT.hasNext()){
			nSamples++;
			geno = genoIT.next();
			
			if(!geno.isNoCall()){
				/* Track the total number of samples with calls
				 * and the total number of genotypes
				 */
				nSamplesWithCall++;
				nGenosCalled += geno.getAlleles().size();
			}
			
			// Get the reference allele count
			tmpRefGenoCount = geno.countAllele(ref);
			refGenoCount += tmpRefGenoCount;
			
			// Increment refSampleCount if this genotype has at least one ref allele
			if(tmpRefGenoCount > 0){
				refSampleCount++;
			}

			// Get the count for each alternate allele
//			for(int i = 0; i < alts.size(); i++){
			for(Allele alt : alts){
				if(iterCount == 0){
					altGenoCount = 0;
					altSampleCount = 0;
					hetSampleCount = 0;
					homoVarSampleCount = 0;
					altGenoCounts.put(alt, altGenoCount);
					altSampleCounts.put(alt, altSampleCount);
					hetSampleCounts.put(alt, hetSampleCount);
					homoVarSampleCounts.put(alt, homoVarSampleCount);
				}
				else{
					altGenoCount = altGenoCounts.get(alt);
					altSampleCount = altSampleCounts.get(alt);
					hetSampleCount = hetSampleCounts.get(alt);
					homoVarSampleCount = homoVarSampleCounts.get(alt);
				}
				tmpAltGenoCount = geno.countAllele(alt);
				altGenoCount += tmpAltGenoCount;
				
				if(tmpAltGenoCount > 0){
					altSampleCount++; // increment the number of samples with the allele
					
					if(tmpAltGenoCount == 1){ // the sample is het for this alt allele
						hetSampleCount++;
					}
					else if(tmpAltGenoCount == 2){ // the sample is homo for this alt allele
						homoVarSampleCount++;
					}
				}

				altGenoCounts.put(alt, altGenoCount);
				altSampleCounts.put(alt, altSampleCount);
				hetSampleCounts.put(alt, hetSampleCount);
				homoVarSampleCounts.put(alt, homoVarSampleCount);
				
				// Get ti/tv info too, but only vor SNVs
				if(UtilityBelt.determineAltType(ref, alt) == AltType.SNV){
					if(isTransition(ref.getBaseString(), alt.getBaseString())){
						genoTiCount++;
					}
					else{
						genoTvCount++;
					}
				}
			}
			iterCount++;
		}
		
		vrs.setRefGenotypeCount(refGenoCount);
		vrs.setRefSampleCount(refSampleCount);
		vrs.setAltGenotypeCounts(altGenoCounts);
		vrs.setAltSampleCounts(altSampleCounts);
		vrs.setGenoTiCount(genoTiCount);
		vrs.setGenoTvCount(genoTvCount);
		vrs.setnSamples(nSamples);
		vrs.setnSamplesWithCall(nSamplesWithCall);
		vrs.setnGenosCalled(nGenosCalled);
		vrs.setHetSampleCounts(hetSampleCounts);
		vrs.setHomoVarSampleCounts(homoVarSampleCounts);
		
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
//		if(ref.length() > 1 || alt.length() > 1){
//	        throw new RuntimeException("Something is very wrong! Expected single nucleotide" +
//	        		" reference and alternate! Got: " + ref + ">" + alt);
//		}
			
		char[] refArray = ref.toCharArray();
		char[] altArray = alt.toCharArray();
		
		for(int i = 0; i < refArray.length; i++){
			if(refArray[i] != altArray[i]){
				if (alt.equals("G") && ref.equals("A")) {
					return true;
				} else if (alt.equals("A") && ref.equals("G")) {
					return true;
				} else if (alt.equals("T") && ref.equals("C")) {
					return true;
				} else if (alt.equals("C") && ref.equals("T")) {
					return true;
				}
			}
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
				vps = VariantPoolSummary.addVariantPoolSummaries(vps, vpSummaries.get(o));
			}
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
		if(vps.getNumInsertions()>0){
			System.out.format(rightalignFormati, "smallINS:", UtilityBelt.roundDoubleToString(vps.getSmallestInsertion()));
			System.out.format(rightalignFormati, "largeINS:", UtilityBelt.roundDoubleToString(vps.getLargestInsertion()));
			System.out.format(rightalignFormati, "avgINS:", UtilityBelt.roundDoubleToString(vps.getAvgInsertionSize()));
		}
		else{
			System.out.format(rightalignFormati, "smallINS:", "NaN");
			System.out.format(rightalignFormati, "largeINS:", "NaN");
			System.out.format(rightalignFormati, "avgINS:", "NaN");
		}
		
		if(vps.getNumDeletions()>0){
			System.out.format(rightalignFormati, "smallDEL:", UtilityBelt.roundDoubleToString(vps.getSmallestDeletion()));
			System.out.format(rightalignFormati, "largeDEL:", UtilityBelt.roundDoubleToString(vps.getLargestDeletion()));
			System.out.format(rightalignFormati, "avgDEL:", UtilityBelt.roundDoubleToString(vps.getAvgDeletionSize()));
		}
		else{
			System.out.format(rightalignFormati, "smallDEL:", "NaN");
			System.out.format(rightalignFormati, "largeDEL:", "NaN");
			System.out.format(rightalignFormati, "avgDEL:", "NaN");
		}
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
		
		

		char[] chars = new char[length];
		Arrays.fill(chars, '-');
		String s = new String(chars);
		s = "+" + s + "+";
		
		
		
		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);
		
		int LeftColumn = 15;
		
		String leftalignFormats = " %-" + (length--) + "s" + "          ";
		String leftalignFormatint = "|%-" + LeftColumn + "s%" + (length - LeftColumn) + "d |" + "          ";
		String rightalignFormati = "|%" + LeftColumn + "s%" + (length - LeftColumn) + "s |" + "          ";
		String rightalignFormatf = "|%" + LeftColumn + "s%" + (length - LeftColumn) + ".2f |" + "          ";
		
		for(int i = 0; i<size;i++)
			System.out.format(t + "         ");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormats, "");

		System.out.format(newLine);
		
		for(int i = 0; i<size;i++){
			title = "Summary of: " + filenames[i];
			System.out.format(leftalignFormats, title);
			System.out.print(" ");
		}
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormats, "");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(t + "         ");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "          ");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormatint, "TotalVars:", Summaries.get(filenames[i]).getNumVars());
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormatint, "Total Samples:", Summaries.get(filenames[i]).getNumSamples());
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "          ");
		
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
			System.out.format(s + "          ");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(rightalignFormati, "MNVs:      ", Integer.toString(Summaries.get(filenames[i]).getNumMNVs()));
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "          ");
		
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
		
		for(int i = 0; i<size;i++){
			if(Summaries.get(filenames[i]).getNumInsertions()>0)
				System.out.format(rightalignFormati, "smallINS:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getSmallestInsertion()));
			else
				System.out.format(rightalignFormati, "smallINS:", "NaN");
		}
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++){
			if(Summaries.get(filenames[i]).getNumInsertions()>0)
				System.out.format(rightalignFormati, "largeINS:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getLargestInsertion()));
			else
				System.out.format(rightalignFormati, "largeINS:", "NaN");
		}
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++){
			if(Summaries.get(filenames[i]).getNumInsertions()>0)
				System.out.format(rightalignFormati, "avgINS:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getAvgInsertionSize()));
			else
				System.out.format(rightalignFormati, "avgINS:", "NaN");

		}
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++){
			if(Summaries.get(filenames[i]).getNumDeletions()>0)
				System.out.format(rightalignFormati, "smallDEL:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getSmallestDeletion()));
			else
				System.out.format(rightalignFormati, "smallDEL:", "NaN");

		}
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++){
			if(Summaries.get(filenames[i]).getNumDeletions()>0)
				System.out.format(rightalignFormati, "largeDEL:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getLargestDeletion()));
			else
				System.out.format(rightalignFormati, "largeDEL:", "NaN");

		}
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++){
			if(Summaries.get(filenames[i]).getNumDeletions()>0)
				System.out.format(rightalignFormati, "avgDEL:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getAvgDeletionSize()));
			else
				System.out.format(rightalignFormati, "avgDEL:", "NaN");
		}
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "          ");
		
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
			System.out.format(s + "          ");
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(leftalignFormatint, "MultiAlts:", Summaries.get(filenames[i]).getNumMultiAlts());
		
		System.out.format(newLine);
		
		for(int i = 0; i<size;i++)
			System.out.format(s + "          ");
		
		System.out.format(newLine);
	
		
		System.out.format(newLine + newLine);

		
		
		
	}
	
	
	public static void Print_Columns(HashMap<String, VariantPoolSummary>  Summaries){
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
		
		int column_size = 10*size;
		/*
		for(int i=0;i<size;i++){
			column_size += 10+Summaries.get(filenames[i]).longest_length();
		}*/
		if(column_size > length)
			length = 15+column_size;
		else
			length += 15+namesize;
		String title="Summary of: " + filenames[0];
		
	
		length+=2;
		

		char[] chars = new char[length];
		Arrays.fill(chars, '-');
		String s = new String(chars);
		s = "+" + s + "+";
		
		
		
		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);
		
		int LeftColumn = 15;
		
		
		
		
		String r_int_align="%10d";
		String bar = " |";
		String r_string_align = "%10s";
		String r_float_align = "%10.2f";
		
		String leftalignFormats = " %-" + (length--) + "s" + newLine;
		String leftalignFormatint = "|%-" + LeftColumn + "s %10d";
		String rightalignFormati = "|%" + LeftColumn + "s %10s";
		String rightalignFormatf = "|%" + LeftColumn + "s %10.2f";
		
		
		
		
		System.out.format(t + newLine);
		
		int pos = 0;

		System.out.format(leftalignFormats, "");
		for (Object vpfile : filenames) {
			if (pos > 0)
				title = "            " + vpfile.toString();
			pos++;
			System.out.format(leftalignFormats, title);
		}
		System.out.format(leftalignFormats, "");
		System.out.format(t + newLine);
		System.out.format(newLine);
		
		
		
		
		
		
		System.out.format(s);
		
		System.out.format(newLine);
		
		System.out.format(leftalignFormatint, "TotalVars:", Summaries.get(filenames[0]).getNumVars());
		for(int i = 1; i<size;i++)
			System.out.format(r_int_align, Summaries.get(filenames[i]).getNumVars());

		System.out.format(bar);
		System.out.format(newLine);
		
		System.out.format(leftalignFormatint, "Total Samples:", Summaries.get(filenames[0]).getNumSamples());
		for(int i = 1; i<size;i++)
			System.out.format(r_int_align, Summaries.get(filenames[i]).getNumSamples());
		
		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "SNVs:      ", Integer.toString(Summaries.get(filenames[0]).getNumSNVs()));
		for(int i = 1; i<size;i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(filenames[i]).getNumSNVs()));
		
		System.out.format(bar);
		System.out.format(newLine);
		
		System.out.format(rightalignFormatf, "Ti/Tv:", Summaries.get(filenames[0]).getTiTv());
		for(int i = 1; i<size;i++)
			System.out.format(r_float_align, Summaries.get(filenames[i]).getTiTv());
		
		System.out.format(bar);
		System.out.format(newLine);
		
		System.out.format(rightalignFormatf, "(Geno)Ti/Tv:", Summaries.get(filenames[0]).getGenoTiTv());
		for(int i = 1; i<size;i++)
			System.out.format(r_float_align, Summaries.get(filenames[i]).getGenoTiTv());
		
		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "MNVs:      ", Integer.toString(Summaries.get(filenames[0]).getNumMNVs()));
		for(int i = 1; i<size;i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(filenames[i]).getNumMNVs()));
		
		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "INDELs:    ", Integer.toString(Summaries.get(filenames[0]).getNumIndels()));
		for(int i = 1; i<size;i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(filenames[i]).getNumIndels()));
		
		System.out.format(bar);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "INS:", Integer.toString(Summaries.get(filenames[0]).getNumInsertions()));
		for(int i = 1; i<size;i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(filenames[i]).getNumInsertions()));
		
		System.out.format(bar);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "DEL:", Integer.toString(Summaries.get(filenames[0]).getNumDeletions()));
		for(int i = 1; i<size;i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(filenames[i]).getNumDeletions()));
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if(Summaries.get(filenames[0]).getNumInsertions()>0)
			System.out.format(rightalignFormati, "smallINS:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[0]).getSmallestInsertion()));
		else
			System.out.format(rightalignFormati, "smallINS:", "NaN");
		
		for(int i = 1; i<size;i++){
			if(Summaries.get(filenames[i]).getNumInsertions()>0)
				System.out.format(r_string_align, UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getSmallestInsertion()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if(Summaries.get(filenames[0]).getNumInsertions()>0)
			System.out.format(rightalignFormati, "largeINS:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[0]).getLargestInsertion()));
		else
			System.out.format(rightalignFormati, "largeINS:", "NaN");
		for(int i = 1; i<size;i++){
			if(Summaries.get(filenames[i]).getNumInsertions()>0)
				System.out.format(r_string_align, UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getLargestInsertion()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if(Summaries.get(filenames[0]).getNumInsertions()>0)
			System.out.format(rightalignFormati, "avgINS:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[0]).getAvgInsertionSize()));
		else
			System.out.format(rightalignFormati, "avgINS:", "NaN");
		
		for(int i = 1; i<size;i++){
			if(Summaries.get(filenames[i]).getNumInsertions()>0)
				System.out.format(r_string_align, UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getAvgInsertionSize()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if(Summaries.get(filenames[0]).getNumDeletions()>0)
			System.out.format(rightalignFormati, "smallDEL:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[0]).getSmallestDeletion()));
		else
			System.out.format(rightalignFormati, "smallDEL:", "NaN");
		for(int i = 1; i<size;i++){
			if(Summaries.get(filenames[i]).getNumDeletions()>0)
				System.out.format(r_string_align,UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getSmallestDeletion()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if(Summaries.get(filenames[0]).getNumDeletions()>0)
			System.out.format(rightalignFormati, "largeDEL:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[0]).getLargestDeletion()));
		else
			System.out.format(rightalignFormati, "largeDEL:", "NaN");
		
		for(int i = 1; i<size;i++){
			if(Summaries.get(filenames[i]).getNumDeletions()>0)
				System.out.format(r_string_align, UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getLargestDeletion()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if(Summaries.get(filenames[0]).getNumDeletions()>0)
			System.out.format(rightalignFormati, "avgDEL:", UtilityBelt.roundDoubleToString(Summaries.get(filenames[0]).getAvgDeletionSize()));
		else
			System.out.format(rightalignFormati, "avgDEL:", "NaN");
		
		for(int i = 1; i<size;i++){
			if(Summaries.get(filenames[i]).getNumDeletions()>0)
				System.out.format(r_string_align, UtilityBelt.roundDoubleToString(Summaries.get(filenames[i]).getAvgDeletionSize()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "StructVars:", Integer.toString(Summaries.get(filenames[0]).getNumStructVars()));
		for(int i = 1; i<size;i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(filenames[i]).getNumStructVars()));
		
		System.out.format(bar);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "StructINS:", Integer.toString(Summaries.get(filenames[0]).getNumStructIns()));
		for(int i = 1; i<size;i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(filenames[i]).getNumStructIns()));
		
		System.out.format(bar);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "StructDEL:", Integer.toString(Summaries.get(filenames[0]).getNumStructDels()));
		for(int i = 1; i<size;i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(filenames[i]).getNumStructDels()));
		
		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);
		
		System.out.format(leftalignFormatint, "MultiAlts:", Summaries.get(filenames[0]).getNumMultiAlts());
		for(int i = 1; i<size;i++)
			System.out.format(r_int_align, Summaries.get(filenames[i]).getNumMultiAlts());
		
		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);
		System.out.format(newLine + newLine);

		
		
		
	}	
	
}

