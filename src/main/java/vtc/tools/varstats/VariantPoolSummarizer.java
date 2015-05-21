/**
 * 
 */
package vtc.tools.varstats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.AbstractVariantPool;
import vtc.datastructures.VariantPoolHeavy;
import vtc.datastructures.VariantPoolLight;
import vtc.tools.setoperator.SetOperator;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.setoperator.operation.Operation;
import vtc.tools.setoperator.operation.OperationFactory;
import vtc.tools.setoperator.operation.UnionOperation;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author markebbert
 * 
 */
public class VariantPoolSummarizer {

	private static Logger logger = Logger
			.getLogger(VariantPoolSummarizer.class);

	private static PrintWriter detailedVariantRecordWriter;
	private static String detailedSummaryFile;

	public VariantPoolSummarizer() {
	}

	/**
	 * Provide a detailed summary for each VariantPool object
	 * 
	 * @param allVPs
	 * @param combined
	 * @return
	 * @throws InvalidOperationException
	 * @throws IOException
	 */
	public static HashMap<String, VariantPoolSummary> summarizeVariantPoolsDetailed(
			TreeMap<String, VariantPoolLight> allVPs)
			throws InvalidOperationException, IOException {

		HashMap<String, VariantPoolSummary> variantPoolSummaries = new HashMap<String, VariantPoolSummary>();
		VariantPoolSummary vps;
		for (VariantPoolLight vp : allVPs.values()) {
			vps = summarizeVariantPoolDetailed(vp);
			variantPoolSummaries.put(vp.getPoolID(), vps);
		}
		return variantPoolSummaries;
	}

	/**
	 * Provide a combined detailed summary of all VariantPool objects
	 * 
	 * @param allVPs
	 * @throws InvalidOperationException
	 * @throws IOException
	 */
	public static void summarizeVariantPoolsDetailedCombined(
			TreeMap<String, VariantPoolHeavy> allVPs)
			throws InvalidOperationException, IOException {

		ArrayList<VariantPoolHeavy> allVPsList = new ArrayList<VariantPoolHeavy>(
				allVPs.values());
		if (allVPsList.size() > 1) {
			String union = "", unionName = "combined";
			VariantPoolHeavy unionVP = null;
			SetOperator so = new SetOperator();

			/* Build the operation string */
			union = unionName + "=u[";
			for (int i = 0; i < allVPsList.size(); i++) {
				if (i == 0) {
					union += allVPsList.get(i).getPoolID();
				} else {
					union += ":" + allVPsList.get(i).getPoolID();
				}
			}
			union += "]";

			Operation op = OperationFactory.createOperation(union, allVPs);
			unionVP = so.performUnion((UnionOperation) op, allVPsList, true);

			// return summarizeVariantPoolDetailed(unionVP);
		} else if (allVPsList.size() == 1) {
			// return summarizeVariantPoolDetailed(allVPsList.get(0));
		} else {
			throw new RuntimeException(
					"ERROR: There were no VariantPools to summarize!");
		}
	}

	/**
	 * Get VariantRecordSummaries for the given VariantPool
	 * 
	 * @param vp
	 * @return
	 * @throws IOException
	 */
	public static <T extends AbstractVariantPool> VariantPoolSummary summarizeVariantPoolDetailed(
			T vp) throws IOException {
		// Iterator<String> varIT = vp.getVariantIterator();
		// String currVarKey;
		// ArrayList<VariantRecordSummary> summaries = new
		// ArrayList<VariantRecordSummary>();
		boolean printDetailedReport = true;
		// VariantPoolDetailedSummary vpds = new
		// VariantPoolDetailedSummary(summarizeVariantPool(vp,
		// printDetailedReport));
		VariantPoolSummary vps = summarizeVariantPool(vp, printDetailedReport);
		// VariantContext var = vp.getNextVar();
		// while(var != null){
		// vpds.addVariantRecordSummary(collectVariantStatistics(var));
		// var = vp.getNextVar();
		// }
		// return vpds;
		return vps;
	}

	/**
	 * Summarize statistics for a set of VariantPools.
	 * 
	 * @param allVPs
	 * @return HashMap<String, VariantPoolSummary>
	 * @throws IOException
	 */
	public static <T extends AbstractVariantPool> HashMap<String, VariantPoolSummary> summarizeVariantPools(
			TreeMap<String, T> allVPs) throws IOException {

		VariantPoolSummary vpSummary;
		HashMap<String, VariantPoolSummary> vpSummaries = new HashMap<String, VariantPoolSummary>();
		boolean printDetailedReport = false;
		for (T vp : allVPs.values()) {
			vpSummary = summarizeVariantPool(vp, printDetailedReport);
			vpSummary.setNumSamples(vp.getSamples().size());
			// vpSummaries.put(vp.getPoolID(), vpSummary);
			vpSummaries.put(vp.getPoolID()+"_"+vp.getFile().getName(), vpSummary);
//			vpSummaries.put(vp.getFile().getName(), vpSummary);
		}
		return vpSummaries;
	}

	/**
	 * Summarize statistics in a single VariantPool
	 * 
	 * @param vp
	 * @return
	 * @throws IOException
	 */
	public static <T extends AbstractVariantPool> VariantPoolSummary summarizeVariantPool(
			T vp, boolean printDetailed) throws IOException {

		// Iterator<String> varIT = vp.getVariantIterator();
		// String currVarKey;
		VariantRecordSummary vrs;
		int recordCount = 0, varRecordCount = 0, totalVarCount = 0, snvCount = 0, mnvCount = 0, structIndelCount = 0, structInsCount = 0, structDelCount = 0, sampleCount = 0, multiAltCount = 0, tiCount = 0, tvCount = 0,
				genoTiCount = 0, genoTvCount = 0, indelCount = 0, insCount = 0, delCount = 0, smallestIns = Integer.MAX_VALUE, smallestDel = Integer.MAX_VALUE, largestIns = 0, largestDel = 0, smallestStructIns = Integer.MAX_VALUE,
				smallestStructDel = Integer.MAX_VALUE, largestStructIns = 0, largestStructDel = 0, structInsSum = 0, insSum = 0, delSum = 0, structDelSum = 0;

		NumberFormat nf = NumberFormat.getInstance(Locale.US);

		if (printDetailed) {
			detailedSummaryFile = vp.getPoolID() + "_detailed_summary.txt";
		}

		VariantContext var = vp.getNextVar();
		while (var != null) {
			sampleCount = var.getNSamples();

			if (recordCount > 1 && recordCount % 10000 == 0)
				System.out.print("Parsed variant records: "
						+ nf.format(recordCount) + "\r");

			recordCount++; // count total num records
//				System.out.println(recordCount);

			vrs = collectVariantStatistics(var);
			if (var.isVariant()) {

				varRecordCount++; // count num records that are variants.
//				System.out.println("varrecord="+varRecordCount);

				if (var.getAlternateAlleles().size() > 1) {
					multiAltCount++;
				}

				/*
				 * Track total alts observed
				 */
				totalVarCount += var.getAlternateAlleles().size();

				// Count the different types of alternates for a single record

				snvCount += vrs.getSnvCount();
				mnvCount += vrs.getMnvCount();
				structIndelCount += vrs.getStructIndelCount();
				structInsCount += vrs.getStructInsCount();
				structDelCount += vrs.getStructDelCount();
				tiCount += vrs.getTiCount();
				tvCount += vrs.getTvCount();
				genoTiCount += vrs.getGenoTiCount();
				genoTvCount += vrs.getGenoTvCount();
				
				indelCount += vrs.getIndelCount();
				delCount += vrs.getDelCount();
				insCount += vrs.getInsCount();
				
				insSum += vrs.getInsSum();
				structInsSum += vrs.getStructInsSum();
				delSum += vrs.getDelSum();
				structDelSum += vrs.getStructDelSum();
				
				
				if(smallestIns > vrs.getSmallestIns())
					smallestIns = vrs.getSmallestIns();
				if(smallestDel > vrs.getSmallestDel())
					smallestDel = vrs.getSmallestDel();
				if(largestIns < vrs.getLargestIns())
					largestIns = vrs.getLargestIns();
				if(largestDel < vrs.getLargestDel())
					largestDel = vrs.getLargestDel();
				if(smallestStructIns > vrs.getSmallestStructIns())
					smallestStructIns = vrs.getSmallestStructIns();
				if(smallestStructDel > vrs.getSmallestStructDel())
					smallestStructDel = vrs.getSmallestStructDel();
				if(largestStructIns < vrs.getLargestStructIns())
					largestStructIns = vrs.getLargestStructIns();
				if(largestStructDel < vrs.getLargestStructDel())
					largestStructDel = vrs.getLargestStructDel();
				


			}

			if (printDetailed) {
				printVariantRecordSummaryToFile(vrs);
			}

			var = vp.getNextVar();
		}

		if (printDetailed) {
			detailedVariantRecordWriter.close();
		}

		return new VariantPoolSummary(recordCount, varRecordCount, sampleCount,
				totalVarCount, snvCount, mnvCount, structIndelCount,
				structInsCount, structDelCount, multiAltCount, tiCount,
				tvCount, genoTiCount, genoTvCount, /*allInsertions, allDeletions,*/ indelCount, insCount, delCount, insSum, delSum, structInsSum, structDelSum, smallestIns, smallestDel,
				largestIns, largestDel, smallestStructIns, smallestStructDel, largestStructIns, largestStructDel);
	}

	/**
	 * Count the different alternate types for a single record. i.e., count the
	 * number of SNVs, MNPs, insertions, deletions, structural insertions, and
	 * structural deletions in a given record.
	 * 
	 * @param var
	 * @return
	 */
	public static VariantRecordSummary collectVariantStatistics(
			VariantContext var) {

		Allele ref = var.getReference();
		List<Allele> alts = var.getAlternateAlleles();
		

		Integer snvCount = 0, mnvCount = 0, indelCount = 0, insCount = 0, delCount = 0, structIndelCount = 0, structInsCount = 0, structDelCount = 0, tiCount = 0, tvCount = 0;
		AltType type;
		VariantRecordSummary vrs = new VariantRecordSummary(var.getChr(),
				var.getStart(), var.getReference(), new TreeSet<Allele>(
						var.getAlternateAlleles()));
		for (Allele alt : alts) {
			
			type = UtilityBelt.determineAltType(ref, alt);

			if (type == AltType.SNV) {
				snvCount++;
				if (isTransition(ref.getBaseString(), alt.getBaseString())) {
					tiCount++;
				} else {
					tvCount++;
				}
			} else if (type == AltType.MNP) {
				mnvCount++;
			} else if (type == AltType.STRUCTURAL_INSERTION) {
				structIndelCount++;
				structInsCount++;

				int structInsLength = alt.length() - ref.length();

				if(vrs.getSmallestStructIns() > structInsLength)
					vrs.setSmallestStructIns(structInsLength);
				if(vrs.getLargestStructIns() < structInsLength)
					vrs.setLargestStructIns(structInsLength);

				vrs.setStructInsSum(structInsLength);
				
			} else if (type == AltType.STRUCTURAL_DELETION) {
				structIndelCount++;
				structDelCount++;

				int structDelLength = ref.length() - alt.length();

				if(vrs.getSmallestStructDel() > structDelLength)
					vrs.setSmallestStructDel(structDelLength);
				if(vrs.getLargestStructDel() < structDelLength)
					vrs.setLargestStructDel(structDelLength);

				vrs.setStructDelSum(structDelLength);
				
			} else if (type == AltType.INSERTION) {
				indelCount++;
				insCount++;
				
				int insLength = alt.length() - ref.length();

				if(vrs.getSmallestIns() > insLength)
					vrs.setSmallestIns(insLength);
				if(vrs.getLargestIns() < insLength)
					vrs.setLargestIns(insLength);

				vrs.setInsSum(insLength);
				
			} else if (type == AltType.DELETION) {
				indelCount++;
				delCount++;
				
				int delLength = ref.length() - alt.length();

				if(vrs.getSmallestDel() > delLength)
					vrs.setSmallestDel(delLength);
				if(vrs.getLargestDel() < delLength)
					vrs.setLargestDel(delLength);

				vrs.setDelSum(delLength);
				
			
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

		generateGenotypeStatsForVariantRecord(vrs, var, new ArrayList<String>(
				var.getSampleNamesOrderedByName()));
		return vrs;
	}

	/**
	 * Generate a detailed stat summary for a given variant.
	 * 
	 * @param var
	 * @param Samples
	 * @return
	 */
	private static void generateGenotypeStatsForVariantRecord(
			VariantRecordSummary vrs, VariantContext var,
			ArrayList<String> samples) {

		Allele ref = var.getReference();
		List<Allele> alts = new ArrayList<Allele>(var.getAlternateAlleles());

		int refGenoCount = 0, refSampleCount = 0, tmpRefGenoCount, altGenoCount, altSampleCount, tmpAltGenoCount, genoTiCount = 0, genoTvCount = 0, nSamples = 0, nSamplesWithCall = 0, nGenosCalled = 0, hetSampleCount, homoVarSampleCount;
		HashMap<Allele, Integer> altGenoCounts = new HashMap<Allele, Integer>(), altSampleCounts = new HashMap<Allele, Integer>(), hetSampleCounts = new HashMap<Allele, Integer>(), homoVarSampleCounts = new HashMap<Allele, Integer>();
		Iterator<Genotype> genoIT = var.getGenotypes().iterator();
		Genotype geno;
		int iterCount = 0;

		// Loop over all of the genotypes in the record
		while (genoIT.hasNext()) {
			nSamples++;
			geno = genoIT.next();

			if (!geno.isNoCall()) {
				/*
				 * Track the total number of samples with calls and the total
				 * number of genotypes
				 */
				nSamplesWithCall++;
				nGenosCalled += geno.getAlleles().size();
			}

			// Get the reference allele count
			tmpRefGenoCount = geno.countAllele(ref);
			refGenoCount += tmpRefGenoCount;

			// Increment refSampleCount if this genotype has at least one ref
			// allele
			if (tmpRefGenoCount > 0) {
				refSampleCount++;
			}

			// Get information from each alternate allele present in the Genotype 
			// for(int i = 0; i < alts.size(); i++){
			// for(Allele alt : alts){
			List<Allele> alleles = geno.getAlleles();
			for (Allele allele : alleles) {
				if (!allele.isReference() && !allele.isNoCall()) {
					if (iterCount == 0 || alts.contains(allele)) {
						alts.remove(allele);
						altGenoCount = 0;
						altSampleCount = 0;
						hetSampleCount = 0;
						homoVarSampleCount = 0;
						altGenoCounts.put(allele, altGenoCount);
						altSampleCounts.put(allele, altSampleCount);
						hetSampleCounts.put(allele, hetSampleCount);
						homoVarSampleCounts.put(allele, homoVarSampleCount);
					} else {
						altGenoCount = altGenoCounts.get(allele);
						altSampleCount = altSampleCounts.get(allele);
						hetSampleCount = hetSampleCounts.get(allele);
						homoVarSampleCount = homoVarSampleCounts.get(allele);
					}
					tmpAltGenoCount = geno.countAllele(allele);
					altGenoCount += tmpAltGenoCount;

					if (tmpAltGenoCount > 0) {
						altSampleCount++; // increment the number of samples
											// with the allele

						if (tmpAltGenoCount == 1) { // the sample is het for
													// this alt allele
							hetSampleCount++;
						} else if (tmpAltGenoCount == 2) { // the sample is homo
															// for this alt
															// allele
							homoVarSampleCount++;
						}
					}

					altGenoCounts.put(allele, altGenoCount);
					altSampleCounts.put(allele, altSampleCount);
					hetSampleCounts.put(allele, hetSampleCount);
					homoVarSampleCounts.put(allele, homoVarSampleCount);

					// Get ti/tv info too, but only for SNVs
					if (UtilityBelt.determineAltType(ref, allele) == AltType.SNV) {
						if (isTransition(ref.getBaseString(),
								allele.getBaseString())) {
							genoTiCount++;
						} else {
							genoTvCount++;
						}
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
		if (qual > 0)
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
	private static boolean isTransition(String ref, String alt) {
		// if(ref.length() > 1 || alt.length() > 1){
		// throw new
		// RuntimeException("Something is very wrong! Expected single nucleotide"
		// +
		// " reference and alternate! Got: " + ref + ">" + alt);
		// }

		char[] refArray = ref.toCharArray();
		char[] altArray = alt.toCharArray();

		for (int i = 0; i < refArray.length; i++) {
			if (refArray[i] != altArray[i]) {
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

	public static void printSummary(
			HashMap<String, VariantPoolSummary> vpSummaries,
			boolean PrintCombined) {
		Object[] keys = vpSummaries.keySet().toArray();
		VariantPoolSummary vps = new VariantPoolSummary();
		System.out.println(keys.length);
		for (Object o : keys) {
			if (PrintCombined == false) {
				PrintIndividualFiles(o.toString(), vpSummaries.get(o));
			} else {
				vps = VariantPoolSummary.addVariantPoolSummaries(vps,
						vpSummaries.get(o));
			}
		}
		if (PrintCombined == true) {
			PrintCombinedStats(keys, vps);
		}

	}

	private static void openDetailedFileForWriting(String fileName)
			throws FileNotFoundException {
		logger.info("Writing detailed summary to: " + fileName);

		String header = "Chr\tPos\tID\tRef\tAlt\tRef_allele_count\tAlt_allele_count"
				+ "\tRef_sample_count\tAlt_sample_count\tN_samples_with_call\tN_genos_called\tN_total_samples\t"
				+ "Alt_genotype_freq\tAlt_sample_freq\tMin_depth\tMax_depth\tAvg_depth\tQuality";

		detailedVariantRecordWriter = new PrintWriter(fileName);
		detailedVariantRecordWriter.println(header);
	}

	/**
	 * Print a single detailed summary to the given file
	 * 
	 * @param summary
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	private static void printVariantRecordSummaryToFile(
			VariantRecordSummary varSummary) throws FileNotFoundException {
		if (detailedVariantRecordWriter == null) {
			openDetailedFileForWriting(detailedSummaryFile);
		}
		detailedVariantRecordWriter.println(varSummary.toString());
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

	private static void PrintIndividualFiles(String file, VariantPoolSummary vps) {
		int length = vps.longest_length();
		String newLine = System.getProperty("line.separator");

		String title;
		title = "Summary of: " + file;

		length += 15 + file.length();

		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);
		// int LeftColumn = 15;
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
		 * double snvPercent = (double) vps.getNumSNVs() / (double)
		 * vps.getNumVars() * 100; double InDelsPercent = (double)
		 * vps.getInDels() / (double) vps.getNumVars() * 100; double
		 * StructPercent = (double) vps.getStructVars() / (double)
		 * vps.getNumVars() * 100;
		 */
		int LeftColumn = 15;

		String leftalignFormatint = "|%-" + LeftColumn + "s%"
				+ (length - LeftColumn) + "d |" + newLine;
		// String leftalignFormatd = "|%-" + LeftColumn + "s%" + (length -
		// LeftColumn) + ".2f |" + newLine;
		String rightalignFormati = "|%" + LeftColumn + "s%"
				+ (length - LeftColumn) + "s |" + newLine;
		String rightalignFormatf = "|%" + LeftColumn + "s%"
				+ (length - LeftColumn) + ".2f |" + newLine;
		// String rightalignFormats = "|%" + LeftColumn + "s%" + (length -
		// LeftColumn) + "s |" + newLine;
		// String leftalignFormats = " %-" + (length--) + "s" + newLine;
		// String leftAlignError = " %-" + length + "s" + newLine;

		System.out.format(s + newLine);
		System.out
				.format(leftalignFormatint, "TotalRecs:", vps.getNumRecords());
		System.out.format(leftalignFormatint, "TotalVarRecs:",
				vps.getNumVarRecords());
		System.out.format(leftalignFormatint, "TotalVars:", vps.getNumVars());
		System.out
				.format(leftalignFormatint, "N Samples:", vps.getNumSamples());
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "SNVs:      ",
				Integer.toString(vps.getNumSNVs()));
		System.out.format(rightalignFormatf, "Ti/Tv:", vps.getTiTv());
		System.out.format(rightalignFormatf, "(Geno)Ti/Tv:", vps.getGenoTiTv());
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "MNVs:      ",
				Integer.toString(vps.getNumMNVs()));
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "INDELs:    ",
				Integer.toString(vps.getIndelCount()));
		System.out.format(rightalignFormati, "INS:",
				Integer.toString(vps.getInsCount()));
		System.out.format(rightalignFormati, "DEL:",
				Integer.toString(vps.getDelCount()));

		System.out.format(rightalignFormati, "Sizes:    ", "");
		if (vps.getInsCount() > 0) {
			System.out
					.format(rightalignFormati, "smallINS:", UtilityBelt
							.roundDoubleToString(vps.getSmallestIns()));
			System.out.format(rightalignFormati, "largeINS:",
					UtilityBelt.roundDoubleToString(vps.getLargestIns()));
			System.out.format(rightalignFormati, "avgINS:",
					UtilityBelt.roundDoubleToString(vps.getAvgIns()));
		} else {
			System.out.format(rightalignFormati, "smallINS:", "NaN");
			System.out.format(rightalignFormati, "largeINS:", "NaN");
			System.out.format(rightalignFormati, "avgINS:", "NaN");
		}

		if (vps.getDelCount() > 0) {
			System.out.format(rightalignFormati, "smallDEL:",
					UtilityBelt.roundDoubleToString(vps.getSmallestDel()));
			System.out.format(rightalignFormati, "largeDEL:",
					UtilityBelt.roundDoubleToString(vps.getLargestDel()));
			System.out.format(rightalignFormati, "avgDEL:",
					UtilityBelt.roundDoubleToString(vps.getAvgDel()));
		} else {
			System.out.format(rightalignFormati, "smallDEL:", "NaN");
			System.out.format(rightalignFormati, "largeDEL:", "NaN");
			System.out.format(rightalignFormati, "avgDEL:", "NaN");
		}
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "StructVars:",
				Integer.toString(vps.getNumStructVars()));
		System.out.format(rightalignFormati, "StructINS:",
				Integer.toString(vps.getNumStructIns()));
		System.out.format(rightalignFormati, "StructDEL:",
				Integer.toString(vps.getNumStructDels()));
		System.out.format(rightalignFormati, "Sizes:    ", "");
		if(vps.getNumStructIns() > 0){
			System.out.format(rightalignFormati, "smallStructINS:",
				Integer.toString(vps.getSmallestStructIns()));
			System.out.format(rightalignFormati, "largeStructINS:",
				Integer.toString(vps.getLargestStructIns()));
			System.out.format(rightalignFormati, "avgStructINS:",
				UtilityBelt.roundDoubleToString(vps.getAvgStructIns()));
		}
		else{
			System.out.format(rightalignFormati, "smallStructINS:", "NaN");
			System.out.format(rightalignFormati, "largeStructINS:", "NaN");
			System.out.format(rightalignFormati, "avgStructINS:", "NaN");
		}
		if(vps.getAvgStructDel() > 0){
			System.out.format(rightalignFormati, "smallStructDEL:",
				Integer.toString(vps.getSmallestStructDel()));
			System.out.format(rightalignFormati, "largeStructDEL:",
				Integer.toString(vps.getLargestStructDel()));
			System.out.format(rightalignFormati, "avgStructDEL:",
				UtilityBelt.roundDoubleToString(vps.getAvgStructDel()));
		}
		else{
			System.out.format(rightalignFormati, "smallStructDEL:", "NaN");
			System.out.format(rightalignFormati, "largeStructDEL:", "NaN");
			System.out.format(rightalignFormati, "avgStructDEL:", "NaN");
		}
		System.out.format(s + newLine);
		System.out.format(leftalignFormatint, "MultiAlts:",
				vps.getNumMultiAlts());
		System.out.format(s + newLine);

		System.out.format(newLine + newLine);

	}

	public static void PrintSide_by_Side(
			HashMap<String, VariantPoolSummary> Summaries) {
		Object[] filenames = Summaries.keySet().toArray();
		int size = filenames.length;
		int length = -1;
		int namesize = -1;
		for (int i = 0; i < size; i++) {
			int temp = Summaries.get(filenames[i]).longest_length();
			if (length < temp)
				length = temp;
			if (namesize < filenames[i].toString().length())
				namesize = filenames[i].toString().length();
		}

		String newLine = System.getProperty("line.separator");

		String title;

		length += 15 + namesize;

		char[] chars = new char[length];
		Arrays.fill(chars, '-');
		String s = new String(chars);
		s = "+" + s + "+";

		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);

		int LeftColumn = 15;

		String leftalignFormats = " %-" + (length--) + "s" + "          ";
		String leftalignFormatint = "|%-" + LeftColumn + "s%"
				+ (length - LeftColumn) + "d |" + "          ";
		String rightalignFormati = "|%" + LeftColumn + "s%"
				+ (length - LeftColumn) + "s |" + "          ";
		String rightalignFormatf = "|%" + LeftColumn + "s%"
				+ (length - LeftColumn) + ".2f |" + "          ";

		for (int i = 0; i < size; i++)
			System.out.format(t + "         ");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(leftalignFormats, "");

		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			title = "Summary of: " + filenames[i];
			System.out.format(leftalignFormats, title);
			System.out.print(" ");
		}

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(leftalignFormats, "");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(t + "         ");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(s + "          ");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(leftalignFormatint, "TotalVars:",
					Summaries.get(filenames[i]).getNumVars());

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(leftalignFormatint, "Total Samples:", Summaries
					.get(filenames[i]).getNumSamples());

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(s + "          ");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "SNVs:      ",
					Integer.toString(Summaries.get(filenames[i]).getNumSNVs()));

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormatf, "Ti/Tv:",
					Summaries.get(filenames[i]).getTiTv());

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormatf, "(Geno)Ti/Tv:",
					Summaries.get(filenames[i]).getGenoTiTv());

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(s + "          ");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "MNVs:      ",
					Integer.toString(Summaries.get(filenames[i]).getNumMNVs()));

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(s + "          ");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "INDELs:    ", Integer
					.toString(Summaries.get(filenames[i]).getIndelCount()));

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "INS:", Integer
					.toString(Summaries.get(filenames[i]).getInsCount()));

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "DEL:", Integer
					.toString(Summaries.get(filenames[i]).getDelCount()));

		System.out.format(newLine);
		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "Sizes:    ", "");
		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getInsCount() > 0)
				System.out.format(
						rightalignFormati,
						"smallINS:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getSmallestIns()));
			else
				System.out.format(rightalignFormati, "smallINS:", "NaN");
		}
		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getInsCount() > 0)
				System.out.format(
						rightalignFormati,
						"largeINS:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getLargestIns()));
			else
				System.out.format(rightalignFormati, "largeINS:", "NaN");
		}

		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getInsCount() > 0)
				System.out.format(
						rightalignFormati,
						"avgINS:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getAvgIns()));
			else
				System.out.format(rightalignFormati, "avgINS:", "NaN");

		}
		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getDelCount() > 0)
				System.out.format(
						rightalignFormati,
						"smallDEL:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getSmallestDel()));
			else
				System.out.format(rightalignFormati, "smallDEL:", "NaN");

		}
		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getDelCount() > 0)
				System.out.format(
						rightalignFormati,
						"largeDEL:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getLargestDel()));
			else
				System.out.format(rightalignFormati, "largeDEL:", "NaN");

		}
		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getDelCount() > 0)
				System.out.format(
						rightalignFormati,
						"avgDEL:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getAvgDel()));
			else
				System.out.format(rightalignFormati, "avgDEL:", "NaN");
		}
		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(s + "          ");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "StructVars:", Integer
					.toString(Summaries.get(filenames[i]).getNumStructVars()));

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "StructINS:", Integer
					.toString(Summaries.get(filenames[i]).getNumStructIns()));

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "StructDEL:", Integer
					.toString(Summaries.get(filenames[i]).getNumStructDels()));

		System.out.format(newLine);
		for (int i = 0; i < size; i++)
			System.out.format(rightalignFormati, "Sizes:    ", "");
		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructIns() > 0)
				System.out.format(
						rightalignFormati,
						"smallStructINS:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getSmallestStructIns()));
			else
				System.out.format(rightalignFormati, "smallStructINS:", "NaN");

		}
		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructIns() > 0)
				System.out.format(
						rightalignFormati,
						"largeStructINS:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getLargestStructIns()));
			else
				System.out.format(rightalignFormati, "largeStructINS:", "NaN");

		}
		System.out.format(newLine);
		
		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructIns() > 0)
				System.out.format(
						rightalignFormati,
						"avgStructINS:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getAvgStructIns()));
			else
				System.out.format(rightalignFormati, "avgStructINS:", "NaN");

		}
		System.out.format(newLine);
		
		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructDels() > 0)
				System.out.format(
						rightalignFormati,
						"smallStructDEL:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getSmallestStructDel()));
			else
				System.out.format(rightalignFormati, "smallStructDEL:", "NaN");

		}
		System.out.format(newLine);

		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructDels() > 0)
				System.out.format(
						rightalignFormati,
						"largeStructDel:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getLargestStructDel()));
			else
				System.out.format(rightalignFormati, "largeStructDEL:", "NaN");

		}
		System.out.format(newLine);
		
		for (int i = 0; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructDels() > 0)
				System.out.format(
						rightalignFormati,
						"avgStructDEL:",
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getAvgStructDel()));
			else
				System.out.format(rightalignFormati, "avgStructDEL:", "NaN");

		}
		System.out.format(newLine);
		

		for (int i = 0; i < size; i++)
			System.out.format(s + "          ");

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(leftalignFormatint, "MultiAlts:",
					Summaries.get(filenames[i]).getNumMultiAlts());

		System.out.format(newLine);

		for (int i = 0; i < size; i++)
			System.out.format(s + "          ");

		System.out.format(newLine);

		System.out.format(newLine + newLine);

	}

	public static void Print_Columns(
			HashMap<String, VariantPoolSummary> Summaries) {
		Object[] filenames = Summaries.keySet().toArray();
		int size = filenames.length;
		int length = -1;
		int namesize = -1;
		for (int i = 0; i < size; i++) {
			int temp = Summaries.get(filenames[i]).longest_length();
			if (length < temp)
				length = temp;
			if (namesize < filenames[i].toString().length())
				namesize = filenames[i].toString().length();
		}

		String newLine = System.getProperty("line.separator");

		int column_size = 10 * size;
		/*
		 * for(int i=0;i<size;i++){ column_size +=
		 * 10+Summaries.get(filenames[i]).longest_length(); }
		 */
		if (column_size > length)
			length = 15 + column_size;
		else
			length += 15 + namesize;
		String title = "Summary of: " + filenames[0];

		length += 2;

		char[] chars = new char[length];
		Arrays.fill(chars, '-');
		String s = new String(chars);
		s = "+" + s + "+";

		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);

		int LeftColumn = 15;

		String r_int_align = "%10d";
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

		System.out.format(leftalignFormatint, "TotalVars:",
				Summaries.get(filenames[0]).getNumVars());
		for (int i = 1; i < size; i++)
			System.out.format(r_int_align, Summaries.get(filenames[i])
					.getNumVars());

		System.out.format(bar);
		System.out.format(newLine);

		System.out.format(leftalignFormatint, "Total Samples:",
				Summaries.get(filenames[0]).getNumSamples());
		for (int i = 1; i < size; i++)
			System.out.format(r_int_align, Summaries.get(filenames[i])
					.getNumSamples());

		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);

		System.out.format(rightalignFormati, "SNVs:      ",
				Integer.toString(Summaries.get(filenames[0]).getNumSNVs()));
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align,
					Integer.toString(Summaries.get(filenames[i]).getNumSNVs()));

		System.out.format(bar);
		System.out.format(newLine);

		System.out.format(rightalignFormatf, "Ti/Tv:",
				Summaries.get(filenames[0]).getTiTv());
		for (int i = 1; i < size; i++)
			System.out.format(r_float_align, Summaries.get(filenames[i])
					.getTiTv());

		System.out.format(bar);
		System.out.format(newLine);

		System.out.format(rightalignFormatf, "(Geno)Ti/Tv:",
				Summaries.get(filenames[0]).getGenoTiTv());
		for (int i = 1; i < size; i++)
			System.out.format(r_float_align, Summaries.get(filenames[i])
					.getGenoTiTv());

		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);

		System.out.format(rightalignFormati, "MNVs:      ",
				Integer.toString(Summaries.get(filenames[0]).getNumMNVs()));
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align,
					Integer.toString(Summaries.get(filenames[i]).getNumMNVs()));

		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);

		System.out.format(rightalignFormati, "INDELs:    ",
				Integer.toString(Summaries.get(filenames[0]).getIndelCount()));
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(
					filenames[i]).getIndelCount()));

		System.out.format(bar);
		System.out.format(newLine);

		System.out.format(rightalignFormati, "INS:", Integer.toString(Summaries
				.get(filenames[0]).getInsCount()));
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(
					filenames[i]).getInsCount()));

		System.out.format(bar);
		System.out.format(newLine);

		System.out
				.format(rightalignFormati, "DEL:", Integer.toString(Summaries
						.get(filenames[0]).getDelCount()));
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(
					filenames[i]).getDelCount()));

		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(rightalignFormati, "Sizes:    ", "");
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align, "");
		System.out.format(bar);
		System.out.format(newLine);

		if (Summaries.get(filenames[0]).getInsCount() > 0)
			System.out.format(rightalignFormati, "smallINS:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getSmallestIns()));
		else
			System.out.format(rightalignFormati, "smallINS:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getInsCount() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getSmallestIns()));
			else
				System.out.format(r_string_align, "NaN");
		}

		System.out.format(bar);
		System.out.format(newLine);

		if (Summaries.get(filenames[0]).getInsCount() > 0)
			System.out.format(rightalignFormati, "largeINS:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getLargestIns()));
		else
			System.out.format(rightalignFormati, "largeINS:", "NaN");
		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getInsCount() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getLargestIns()));
			else
				System.out.format(r_string_align, "NaN");
		}

		System.out.format(bar);
		System.out.format(newLine);

		if (Summaries.get(filenames[0]).getInsCount() > 0)
			System.out.format(rightalignFormati, "avgINS:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getAvgIns()));
		else
			System.out.format(rightalignFormati, "avgINS:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getInsCount() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getAvgIns()));
			else
				System.out.format(r_string_align, "NaN");
		}

		System.out.format(bar);
		System.out.format(newLine);

		if (Summaries.get(filenames[0]).getDelCount() > 0)
			System.out.format(rightalignFormati, "smallDEL:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getSmallestDel()));
		else
			System.out.format(rightalignFormati, "smallDEL:", "NaN");
		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getDelCount() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getSmallestDel()));
			else
				System.out.format(r_string_align, "NaN");
		}

		System.out.format(bar);
		System.out.format(newLine);

		if (Summaries.get(filenames[0]).getDelCount() > 0)
			System.out.format(rightalignFormati, "largeDEL:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getLargestDel()));
		else
			System.out.format(rightalignFormati, "largeDEL:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getDelCount() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getLargestDel()));
			else
				System.out.format(r_string_align, "NaN");
		}

		System.out.format(bar);
		System.out.format(newLine);

		if (Summaries.get(filenames[0]).getDelCount() > 0)
			System.out.format(rightalignFormati, "avgDEL:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getAvgDel()));
		else
			System.out.format(rightalignFormati, "avgDEL:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getDelCount() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getAvgDel()));
			else
				System.out.format(r_string_align, "NaN");
		}

		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);

		System.out.format(rightalignFormati, "StructVars:", Integer
				.toString(Summaries.get(filenames[0]).getNumStructVars()));
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(
					filenames[i]).getNumStructVars()));

		System.out.format(bar);
		System.out.format(newLine);

		System.out
				.format(rightalignFormati, "StructINS:",
						Integer.toString(Summaries.get(filenames[0])
								.getNumStructIns()));
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(
					filenames[i]).getNumStructIns()));

		System.out.format(bar);
		System.out.format(newLine);

		System.out.format(rightalignFormati, "StructDEL:", Integer
				.toString(Summaries.get(filenames[0]).getNumStructDels()));
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align, Integer.toString(Summaries.get(
					filenames[i]).getNumStructDels()));
		
		System.out.format(bar);
		System.out.format(newLine);
		
		System.out.format(rightalignFormati, "Sizes:    ", "");
		for (int i = 1; i < size; i++)
			System.out.format(r_string_align, "");
		System.out.format(bar);
		System.out.format(newLine);
		
		if (Summaries.get(filenames[0]).getNumStructIns() > 0)
			System.out.format(rightalignFormati, "smallStructINS:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getSmallestStructIns()));
		else
			System.out.format(rightalignFormati, "smallStructINS:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructIns() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getSmallestStructIns()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if (Summaries.get(filenames[0]).getNumStructIns() > 0)
			System.out.format(rightalignFormati, "largeStructINS:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getLargestStructIns()));
		else
			System.out.format(rightalignFormati, "largeStructINS:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructIns() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getLargestStructIns()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if (Summaries.get(filenames[0]).getNumStructIns() > 0)
			System.out.format(rightalignFormati, "avgStructINS:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getAvgStructIns()));
		else
			System.out.format(rightalignFormati, "avgStructINS:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructIns() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getAvgStructIns()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if (Summaries.get(filenames[0]).getNumStructDels() > 0)
			System.out.format(rightalignFormati, "smallStructDEL:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getSmallestStructDel()));
		else
			System.out.format(rightalignFormati, "smallStructDEL:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructDels() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getSmallestStructDel()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if (Summaries.get(filenames[0]).getNumStructDels() > 0)
			System.out.format(rightalignFormati, "largeStructDEL:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getLargestStructDel()));
		else
			System.out.format(rightalignFormati, "largeStructDEL:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructDels() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getLargestStructDel()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		System.out.format(bar);
		System.out.format(newLine);
		
		if (Summaries.get(filenames[0]).getNumStructDels() > 0)
			System.out.format(rightalignFormati, "avgStructDEL:", UtilityBelt
					.roundDoubleToString(Summaries.get(filenames[0])
							.getAvgStructDel()));
		else
			System.out.format(rightalignFormati, "avgStructDEL:", "NaN");

		for (int i = 1; i < size; i++) {
			if (Summaries.get(filenames[i]).getNumStructDels() > 0)
				System.out.format(
						r_string_align,
						UtilityBelt.roundDoubleToString(Summaries.get(
								filenames[i]).getAvgStructDel()));
			else
				System.out.format(r_string_align, "NaN");
		}
		
		
		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);

		System.out.format(leftalignFormatint, "MultiAlts:",
				Summaries.get(filenames[0]).getNumMultiAlts());
		for (int i = 1; i < size; i++)
			System.out.format(r_int_align, Summaries.get(filenames[i])
					.getNumMultiAlts());

		System.out.format(bar);
		System.out.format(newLine);
		System.out.format(s);
		System.out.format(newLine);
		System.out.format(newLine + newLine);

	}

}
