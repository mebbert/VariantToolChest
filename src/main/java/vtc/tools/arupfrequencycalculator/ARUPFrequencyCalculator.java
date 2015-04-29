/**
 * 
 */
package vtc.tools.arupfrequencycalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;

import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.SupportedFileType;
import vtc.datastructures.VariantPoolHeavy;
import vtc.tools.setoperator.SetOperator;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.setoperator.operation.Operation;
import vtc.tools.setoperator.operation.OperationFactory;
import vtc.tools.setoperator.operation.UnionOperation;
import vtc.tools.utilitybelt.UtilityBelt;
import vtc.tools.varstats.VariantPoolDetailedSummary;
import vtc.tools.varstats.VariantPoolSummarizer;
import vtc.tools.varstats.VariantRecordSummary;

/**
 * @author markebbert
 *
 */
public class ARUPFrequencyCalculator {
	private static Logger logger = Logger.getLogger(ARUPFrequencyCalculator.class);

	private ArrayList<String> analTypes;
	private TreeMap<String, ArrayList<HashMap<String, String>>> sampleManifests
					= new TreeMap<String, ArrayList<HashMap<String, String>>>();
	private HashMap<String, File> masterVCFs = new HashMap<String, File>();
	
	private final static String VCF_KEY = "vcf.file";
	private final static String ANALYSIS_TYPE_KEY = "analysis.type";
	private final static String SAMPLE_NAME_KEY = "sample.name";
	private final static String ANALYSIS_START_KEY = "analysis.start.time";
	private final static String INCLUDE_KEY = "include.in.freq.calc";
	private final static String QC_KEY = "qc.json";

	private final static String CURR_DATE =
			new SimpleDateFormat("yyyyMMMdd").format(Calendar.getInstance().getTime());
	
	private FileWriter logFileWriter;
	
	
	public ARUPFrequencyCalculator(){
		return;
	}
	
	public void calculateFrequencies(String rootDir, String analTypeFilePath, String log, String refPath,
			boolean union, String ngsWebAddress) throws IOException, InvalidInputFileException,
			InvalidOperationException, URISyntaxException, JSONException{
		analTypes = readAnalysisTypeFile(analTypeFilePath);
		logFileWriter = new FileWriter(log);
		
		/* Get all of the sample manifests nested in rootDir */
		findSampleManifests(new File(rootDir), refPath);
		
		/* If union, join all VCFs to a master VCF for each analysis type
		 * and then calculate the frequencies for each master VCF
		 */
		if(union){
            boolean addChr = false;
            unionVCFs(addChr, new File(refPath));
            generateHomoRefCalls(new File(refPath));
		
            /* Calculate frequencies for each analysis type */
            Iterator<String> it = masterVCFs.keySet().iterator();
            String analType, freqsFileName, vcfFileName;
            VariantPoolDetailedSummary vpDetailedSummary;
            JSONObject analTypeFreqSummary;
            while(it.hasNext()){
                analType = it.next();		
                vpDetailedSummary = calculateMasterVCFFreqs(analType);
                vcfFileName = masterVCFs.get(analType).getAbsolutePath();
                freqsFileName = vcfFileName.substring(0, vcfFileName.lastIndexOf(".")) + "-freqs.txt";
                printDetailedVPSummaryToFile(vpDetailedSummary, freqsFileName);
                analTypeFreqSummary = buildNGSWebJSON(vpDetailedSummary, analType);
                postFreqsToNGSWeb(analTypeFreqSummary, ngsWebAddress);
            }
		}
		
		/* Loop over all sample manifests and calculate detailed variant pool
		 * summaries for each sample. Add summaries by analysis type.
		 */
		else{
			calculateAllSiteVCFFreqs();
		}

		logFileWriter.close();
	}
	
	private void calculateAllSiteVCFFreqs() throws InvalidInputFileException, IOException{
		logger.info("Calculating frequencies for all-site vcfs.");
		Iterator<String> manifestIT = sampleManifests.keySet().iterator();
		String freqsFileName, analType, origVCFFile, vcfFile, vcfFileArgs,
				masterAnalSummaryFileName, sampleName, path;
		ArrayList<HashMap<String, String>> manifestMaps;
		HashMap<String, String> manifestMap;
		boolean addChr = false, requireIndex = false;
		VariantPoolHeavy vp;
        VariantPoolDetailedSummary vpDetailedSummary, masterDetailedSummary;
        final VCFCodec vcfCodec = new VCFCodec();
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        String header = "Chr,Pos,Ref,Alt,Het_count,Homo_var_count,N_samples_with_coverage,N_total_samples";
        FileWriter fw;
        VariantRecordSummary vrs;
		while(manifestIT.hasNext()){
			analType = manifestIT.next();
			masterDetailedSummary = null;
			/* Loop over the manifestMaps for this analType */
			manifestMaps = sampleManifests.get(analType);
			if(manifestMaps == null){
				throw new RuntimeException("ERROR: should have had sampleManifests for "
							+ analType + " analyses.");
			}
			for(int i = 0; i < manifestMaps.size(); i++){
				manifestMap = manifestMaps.get(i);
			 
				origVCFFile = manifestMap.get(VCF_KEY);
				path = origVCFFile.substring(0, origVCFFile.lastIndexOf("/") + 1);
				sampleName = manifestMap.get(SAMPLE_NAME_KEY);
				vcfFile = path + sampleName + "_all_sites.vcf";
				
				/* If the '*_all_sites.vcf' file doesn't exist, see
				 * if the .gz file exists. If not, log that it didn't
				 * exist and continue on next loop.
				 */
				if(!new File(vcfFile).exists()){
					vcfFile += ".gz";

                    if(!new File(vcfFile).exists()){
                        logFileWriter.write(sampleName + "\t" + analType + "\tCould not find " + vcfFile + "\n");
                        continue;
                    }
				}
				
                freqsFileName = vcfFile.substring(0, vcfFile.lastIndexOf(".vcf")) + "-freqs.txt";
		        fw = new FileWriter(freqsFileName);
		        fw.write(header + "\n");
				
                logger.info("Parsing " + vcfFile + " ...");
                

                /* get A VCF Reader */
                FeatureReader<VariantContext> reader = AbstractFeatureReader.getFeatureReader(
                        vcfFile, vcfCodec, requireIndex);

                /* loop over each Variation */
                Iterator<VariantContext> it = null;
                it = reader.iterator();
                VariantContext vc;
                HashMap<String, String> vrsMap = new HashMap<String, String>();
                int count = 0;
                while ( it.hasNext() ) {
                    
                    if(count > 1 && count % 100000 == 0) System.out.print("Parsed variants: "
                                + nf.format(count) + "\r");
                    
                    vc = it.next();
                    vrs = VariantPoolSummarizer.collectVariantStatistics(vc);
                    vrsMap.put(vc.getChr() + ":" + vc.getStart() + ":" + vc.getReference(), vrs.getAlts().toString() + vrs.getHetSampleCounts().toString() + vrs.getHomoVarSampleCounts().toString());
                    fw.write(vrs.toStringSimpleByAlt() + "\n");
                    count++;
                }

                /* we're done */
                reader.close();
                fw.close();

				
				
				
				
				
				

//				logger.info("Parsing VCF: " + vcfFile);
//				vcfFileArgs = "vars=" + vcfFile;
//				vp = new VariantPool(vcfFileArgs, false, addChr);
//
//				logger.info("Summarizing VCF: " + vcfFile);
//                vpDetailedSummary = VariantPoolSummarizer.summarizeVariantPoolDetailed(vp);

//                printSimpleVPSummaryToFile(vpDetailedSummary, freqsFileName);
                
                /* Add the recent detailed summary to the master, or make it
                 * the master if this is the first one.
                 */
//                if(masterDetailedSummary != null){
//                    masterDetailedSummary = VariantPoolDetailedSummary.addVariantPoolDetailedSummaries(masterDetailedSummary, vpDetailedSummary);
//                }
//                else{
//                	masterDetailedSummary = vpDetailedSummary;
//                }
			}
//			if(masterDetailedSummary == null){
//                logger.warn("Could not find any all-site vcfs for the following analysis type: " + analType);
//			}
//			else{
//                masterAnalSummaryFileName = analType + "/" + CURR_DATE + "--master_summary--" + analType + ".txt";
//                printSimpleVPSummaryToFile(masterDetailedSummary, masterAnalSummaryFileName);
//			}
		}
	}
	
	/**
	 * Read the analysis type file and save analysis types into an ArrayList<String>
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String> readAnalysisTypeFile(String filePath) throws IOException{
		
		logger.info("Reading analysis Types");
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line, val;
		ArrayList<String> analysisTypes = new ArrayList<String>();
		while((line = br.readLine()) != null){
			// Just take whatever is on the line.
			val = line.replaceAll("\\s", ".");
			analysisTypes.add(val); // replace any white space with "."
			logger.info("Found " + val + " analysis type.");
		}
		br.close();
		return analysisTypes;
	}
	
	/**
	 * Recursively descend through the directory structure finding sample manifest
	 * files. If the analysis type for that sample was specified, and the sample
	 * is marked to be included, union it to the master.
	 * @param file
	 * @param refPath
	 * @throws IOException
	 */
	private void findSampleManifests(File file, String refPath) throws IOException{
//		logger.info("Descending into: " + file.getAbsolutePath());
		if(!file.exists()){
			logger.info("ERROR: File " + file.getAbsolutePath() + " does not exist!");
			return;
		}
		if(file.isDirectory()){ // Continue to descend through directories
			String[] children = file.list();
			for(String child : children){
				findSampleManifests(new File(file, child), refPath);
			}
		}
		else if(file.isFile()){ // This is a file. If it's a sample manifest, use it.
//			logger.info("File absolute path: " + file.getAbsolutePath());
//			logger.info("File name: " + file.getName());
			if(file.getName().equals("sampleManifest.txt")){
				
				/* Read the manifest into a HashMap */
				HashMap<String, String> manifestMap = readManifest(file);
				
				/* Get the analysis type (i.e., the pipeline template used). Replace whitespace with '.' */
				String analType = manifestMap.get(ANALYSIS_TYPE_KEY).replaceAll("\\s", ".");
				
				/* If a directory for this analysis type does not exist, create it. */
				File analDir = new File(analType);
				if(!analDir.exists()){
					analDir.mkdir();
				}
				
				/* Prepend the parent directory to the VCF path */
				manifestMap.put(VCF_KEY, file.getParent() + "/" + manifestMap.get(VCF_KEY));
				manifestMap.put(QC_KEY, file.getParent() + "/" + manifestMap.get(QC_KEY));
				
				/* Check whether this sample and analType should be included.
				 * If not, log the excluded sample and return.
				 */
				String include = manifestMap.get(INCLUDE_KEY);
				
				/* For now, if the sample doesn't have an include key, assume it's true */
				if(include == null){ include = "true"; }
				if(!analTypes.contains(analType) || !include.toLowerCase().equals("true")){
					String sampleName = manifestMap.get(SAMPLE_NAME_KEY);
					logFileWriter.write(sampleName + "\t" + analType + "\t" + include + "\n");
					return;
				}
				

				/* Collect the manifestMaps by analType */
				ArrayList<HashMap<String, String>> manifestList = sampleManifests.get(analType); 
				
				/* If sampleManifests doesn't already have a list of manifestMaps for
				 * this analType, create one and add it in.
				 */
				if(manifestList == null){
					manifestList = new ArrayList<HashMap<String, String>>();
					manifestList.add(manifestMap);
					sampleManifests.put(analType, manifestList);
				}
				
				/* Otherwise, just add the manifestMap to the existing list */
				else{
					manifestList.add(manifestMap);
				}
			}
		}
		
	}
	
	/**
	 * This method will loop over the sample manifests for each analysis type
	 * and union them into one master vcf for the analysis type.
	 * @param addChr
	 * @param refDict
	 * @throws IOException
	 * @throws InvalidInputFileException
	 * @throws InvalidOperationException
	 * @throws URISyntaxException
	 */
	private void unionVCFs(boolean addChr, File refDict)
			throws IOException, InvalidInputFileException, InvalidOperationException, URISyntaxException{
		logger.info("Unioning vcfs...");
		HashMap<String, String> manifestMap1, manifestMap2;
		String vcfFile1Args, vcfFile2Args, vcfFile1, vcfFile2;
		TreeMap<String, VariantPoolHeavy> allVPs;
		String opString = "masterVP=u[vars1:vars2]";
		Operation op;
		boolean forceUniqueNames = false;
		SetOperator so = new SetOperator();
		File master_vcf;
		VariantPoolHeavy masterVP = null, vp1, vp2;
		ArrayList<String> newSampleNames1, newSampleNames2;
        String sampleName1, sampleName2;

		/* Loop over the analysis types and corresponding sampleManifestMaps
		 * and union the VCFs to a master vcf
		 */
		Iterator<String> analTypeIT = sampleManifests.keySet().iterator();
		String analType;
		ArrayList<HashMap<String, String>> manifestMaps;
		while(analTypeIT.hasNext()){
			analType = analTypeIT.next();
			master_vcf = new File(analType + "/" + CURR_DATE + "--master_vcf--" + analType + ".vcf");
			masterVCFs.put(analType, master_vcf);
			
			/* Loop over the manifestMaps for this analType */
			manifestMaps = sampleManifests.get(analType);
			if(manifestMaps == null){
				throw new RuntimeException("ERROR: should have had sampleManifests for "
							+ analType + " analyses.");
			}
			if(manifestMaps.size() == 1){
				copyFileTo(manifestMaps.get(0).get(VCF_KEY), master_vcf.getAbsolutePath());
				continue;
			}
			for(int i = 0; i < manifestMaps.size(); i++){

				newSampleNames1 = new ArrayList<String>();
				newSampleNames2 = new ArrayList<String>();
				allVPs = new TreeMap<String, VariantPoolHeavy>();
				
				// If i == 0, union the first two to create the master
				if(i == 0){
					manifestMap1 = manifestMaps.get(i);
					manifestMap2 = manifestMaps.get(++i);
				
					vcfFile1 = manifestMap1.get(VCF_KEY);
					vcfFile2 = manifestMap2.get(VCF_KEY);

					sampleName1 = manifestMap1.get(SAMPLE_NAME_KEY)
							+ ":" + manifestMap1.get(ANALYSIS_START_KEY);
					newSampleNames1.add(sampleName1);

					sampleName2 = manifestMap2.get(SAMPLE_NAME_KEY)
							+ ":" + manifestMap2.get(ANALYSIS_START_KEY);
					newSampleNames2.add(sampleName2);
				
					vcfFile1Args = "vars1=" + vcfFile1;
					vcfFile2Args = "vars2=" + vcfFile2;
					
					vp1 = new VariantPoolHeavy(vcfFile1Args, false, addChr);
					vp1.changeSampleNames(newSampleNames1);

					vp2 = new VariantPoolHeavy(vcfFile2Args, false, addChr);
					vp2.changeSampleNames(newSampleNames2);
					
					allVPs.put(vp1.getPoolID(), vp1);
					allVPs.put(vp2.getPoolID(), vp2);
				
					op = OperationFactory.createOperation(opString, allVPs);
					masterVP = so.performUnion((UnionOperation)op, UtilityBelt.getAssociatedVariantPoolsAsArrayList(op, allVPs), forceUniqueNames);
				}
				else{
					manifestMap1 = manifestMaps.get(i);
					vcfFile1 = manifestMap1.get(VCF_KEY);
					sampleName1 = manifestMap1.get(SAMPLE_NAME_KEY)
							+ ":" + manifestMap1.get(ANALYSIS_START_KEY);
					newSampleNames1.add(sampleName1);
					vcfFile1Args = "vars1=" + vcfFile1;
					vcfFile2Args = "vars2=" + master_vcf.getAbsolutePath();
					
					vp1 = new VariantPoolHeavy(vcfFile1Args, false, addChr);
					vp1.changeSampleNames(newSampleNames1);
					vp2 = new VariantPoolHeavy(vcfFile2Args, false, addChr);
					
					allVPs.put(vp1.getPoolID(), vp1);
					allVPs.put(vp2.getPoolID(), vp2);

					op = OperationFactory.createOperation(opString, allVPs);
					masterVP = so.performUnion((UnionOperation)op, UtilityBelt.getAssociatedVariantPoolsAsArrayList(op, allVPs), forceUniqueNames);
					
				}
                logger.info("Printing " + masterVP.getPoolID() + " to file: " + master_vcf.getAbsolutePath());
                VariantPoolHeavy.printVariantPool(master_vcf.getAbsolutePath(), masterVP, refDict, SupportedFileType.VCF, false);
                logger.info(masterVP.getNumVarRecords() + " variant record(s) written.");
			}
		}
	}
	
	private void generateHomoRefCalls(File refDict)
			throws JSONException, InvalidInputFileException, URISyntaxException, IOException{
		logger.info("Converting No Calls to homozygous reference...");

		long startTime = System.nanoTime();
		

        Iterator<String> analTypeIT = sampleManifests.keySet().iterator();
		String analType, qcFile;
		ArrayList<HashMap<String, String>> manifestMaps;
		NoCallRegionList noCallRegions;
		HashMap<String, NoCallRegionList> noCallRegionsBySample
							= new HashMap<String, NoCallRegionList>();
		HashMap<String, String> manifestMap;
		File master_vcf;
		String sampleName;
		while(analTypeIT.hasNext()){
			analType = analTypeIT.next();
            master_vcf = masterVCFs.get(analType);
			
			/* Loop over the manifestMaps for this analType */
			manifestMaps = sampleManifests.get(analType);
			for(int i = 0; i < manifestMaps.size(); i++){	
				manifestMap = manifestMaps.get(i);
				qcFile = manifestMap.get(QC_KEY);
				sampleName = manifestMap.get(SAMPLE_NAME_KEY)
						+ ":" + manifestMap.get(ANALYSIS_START_KEY);

				noCallRegions = getNoCallRegions(new File(qcFile));
				noCallRegionsBySample.put(sampleName, noCallRegions);
			}
			
			logger.info("Converting for " + analType + " variants");
			generateHomoRefCalls(master_vcf, noCallRegionsBySample, refDict);
		}
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		long durMins = TimeUnit.NANOSECONDS.toMinutes(duration);
		logger.info("Done in " + durMins + " minutes.");
	}
	
	/**
	 * Convert no call genotypes to homozygous reference for samples that had coverage
	 * at the given location
	 * 
	 * @param master_vcf
	 * @param noCallRegionsBySample
	 * @throws InvalidInputFileException
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	private void generateHomoRefCalls(File master_vcf, HashMap<String, 
			NoCallRegionList> noCallRegionsBySample, File refDict) throws InvalidInputFileException, URISyntaxException, IOException{
		
		// get variant pool
		VariantPoolHeavy master = new VariantPoolHeavy(master_vcf.getAbsolutePath(), false, false);
		
		// loop over variants
		Iterator<String> it = master.getVariantIterator();
		String currVarKey;
		VariantContext var, newVar;
		GenotypesContext gc;
		Genotype geno;
		ArrayList<Allele> alleles;
		ArrayList<Genotype> newGenos;
		NoCallRegionList noCallRegionsForSample;
		while(it.hasNext()){
			newGenos = new ArrayList<Genotype>();
			currVarKey = it.next();
			var = master.getVariant(currVarKey);
			gc = var.getGenotypes();
			
			for(int i = 0; i < gc.size(); i++){
				geno = gc.get(i);
				
				/* If the genotype is a nocall, see if it had coverage */
				if(geno.isNoCall()){
					noCallRegionsForSample = noCallRegionsBySample.get(geno.getSampleName());
					if(noCallRegionsForSample == null 
							|| sampleHasCoverageAtLocation(noCallRegionsForSample, var)){
						 alleles = new ArrayList<Allele>();
						 alleles.add(var.getReference());
						 alleles.add(var.getReference());
						 geno = VariantPoolHeavy.changeAllelesForGenotype(geno, alleles);
					}
				}
				newGenos.add(geno);
			}
			newVar = VariantPoolHeavy.buildVariant(var, var.getAlleles(), newGenos);
			master.updateVariant(currVarKey, newVar);
		}

        logger.info("Printing " + master.getPoolID() + " to file: " + master_vcf.getAbsolutePath());
        VariantPoolHeavy.printVariantPool(master_vcf.getAbsolutePath(), master, refDict, SupportedFileType.VCF, false);
        logger.info(master.getNumVarRecords() + " variant record(s) written.");
	}
	
	/**
	 * Loop over the provided nocall regions for the sample and determine whether the
	 * variant overlaps with any. If so, return false. noCallRegions must be in 
	 * 
	 * @param noCallRegions
	 * @param var
	 * @return
	 */
	private boolean sampleHasCoverageAtLocation(NoCallRegionList noCallRegions, VariantContext var){
		
		String chr = var.getChr();
//		int varChr = Integer.parseInt(chr.replaceAll("\\w", ""));
		int varStart = var.getStart();
		int varEnd = var.getEnd();
		
		NoCall varNoCall = new NoCall(null, null, chr, varStart, varEnd, varEnd - varStart);
		
		/* For each no call region, check whether the variant overlaps */
		int comparison;
		NoCall nc;
		while(true){
			nc = noCallRegions.current();
			if(nc == null){
				return true;
			}
		
			comparison = nc.compareTo(varNoCall);
			
			if(comparison < 0){
				noCallRegions.next();
				continue; // Not to the right region yet
			}
			else if(comparison > 0){
				return true; // Passed the region. Must have coverage.
			}
			else{
				return false; // The two overlap. No coverage.
			}
		}
	}
	
	private NoCallRegionList getNoCallRegions(File qcFile) throws FileNotFoundException{
		logger.info("Getting no calls from: " + qcFile.getAbsolutePath());
		final String nocall_key = "nocalls";
		final String regions_key = "regions";
		final String gene_key = "gene";
		final String reason_key = "reason";
		final String chr_key = "chr";
		final String start_key = "start";
		final String end_key = "end";
		final String size_key = "size";
        JSONTokener jt = new JSONTokener(new BufferedReader(new FileReader(qcFile)));
        JSONObject currNoCallJSON;
        NoCall noCall;
        ArrayList<NoCall> noCallRegions = new ArrayList<NoCall>();
        String gene, reason, chr;
        Integer start, stop, size;

        /* If we fail to extract no call information from the json, just return
         * null. We will assume there was coverage and the sample was homo ref.
         */
        try{
            JSONObject noCalls = new JSONObject(jt).getJSONObject(nocall_key);
            JSONArray regions = noCalls.getJSONArray(regions_key);
            for(int j = 0; j < regions.length(); j++){
                currNoCallJSON = regions.getJSONObject(j);
                gene = currNoCallJSON.getString(gene_key);
                reason = currNoCallJSON.getString(reason_key);
                chr = currNoCallJSON.getString(chr_key);
                start = currNoCallJSON.getInt(start_key);
                stop = currNoCallJSON.getInt(end_key);
                size = currNoCallJSON.getInt(size_key);

                noCall = new NoCall(gene, reason, chr, start, stop, size);
                
    //            noCallRegions = noCallRegionsByChr.get(chr);
    //            if(noCallRegions == null){
    //            	noCallRegions = new LinkedList<NoCall>();
    //            	noCallRegionsByChr.put(chr, noCallRegions);
    //            }
                noCallRegions.add(noCall);
            }
        } catch(JSONException e){
        	logger.info("Unable to obtain nocalls from " + qcFile.getAbsolutePath() +
        			". Exception: " + e.getMessage());
        	return null;
        }
        
        /* Sort all of the NoCall lists */
//        Iterator<Integer> it = noCallRegionsByChr.keySet().iterator();
//        while(it.hasNext()){
//        	Collections.sort(noCallRegionsByChr.get(it.next()));
//        }
        Collections.sort(noCallRegions);
        return new NoCallRegionList(noCallRegions);
	}
	
	/**
	 * Calculate the frequencies for the given analType and return
	 * an ArrayList<VariantRecordSummary> giving a detailed summary
	 * of each variant record
	 * 
	 * @param analType
	 * @return
	 * @throws IOException
	 * @throws InvalidInputFileException
	 * @throws InvalidOperationException
	 */
	private VariantPoolDetailedSummary calculateMasterVCFFreqs(String analType)
			throws IOException, InvalidInputFileException, InvalidOperationException{
			
        logger.info(masterVCFs.get(analType));
        String arg = "all_samples=" + masterVCFs.get(analType);
        boolean requireIndex = false, addChr = false;
        VariantPoolHeavy vp = new VariantPoolHeavy(arg, requireIndex, addChr);
        VariantPoolDetailedSummary summary =
                VariantPoolSummarizer.summarizeVariantPoolDetailed(vp);
        return summary;
	}
	
	/**
	 * Print a detailed report of the summary to a file based on the master
	 * vcf for the given analysis type
	 * 
	 * @param summary
	 * @param analType
	 * @throws IOException
	 */
	private void printDetailedVPSummaryToFile(VariantPoolDetailedSummary summary, String fileName) throws IOException{
//        String master_vcf_file_name = masterVCFs.get(analType).getAbsolutePath();
        logger.info("Printing VCF summary to file" + fileName);
        
        String header = "Chr\tPos\tID\tRef\tAlt\tRef_allele_count\tAlt_allele_count" +
                "\tRef_sample_count\tAlt_sample_count\tN_samples_with_call\tN_genos_called\tN_total_samples\t" +
                "Alt_genotype_freq\tAlt_sample_freq\tMin_depth\tMax_depth\tAvg_depth\tQuality";
        FileWriter fw = new FileWriter(fileName);
        fw.write(header + "\n");
        VariantRecordSummary vrs;
        for(String vrsKey : summary.getVariantRecordSummaries().keySet()){
        	vrs = summary.getVariantRecordSummary(vrsKey);
            fw.write(vrs.toString() + "\n");
        }
        fw.close();
	}
	
	/**
	 * Print a simple summary to the given file name. A simple summary includes:
	 * chr, pos, ref, alt, het_count, homo_var_count, n_samples_with_call, and N_total_samples
	 * @param summary
	 * @param fileName
	 * @throws IOException
	 */
	private void printSimpleVPSummaryToFile(VariantPoolDetailedSummary summary, String fileName) throws IOException{

        String header = "Chr,Pos,Ref,Alt,Het_count,Homo_var_count,N_samples_with_coverage,N_total_samples";
        FileWriter fw = new FileWriter(fileName);
        fw.write(header + "\n");
        String line;
        VariantRecordSummary vrs;
        for(String vrsKey : summary.getVariantRecordSummaries().keySet()){
        	vrs = summary.getVariantRecordSummary(vrsKey);
        	line = vrs.toStringSimpleByAlt();
        	if(line != null){
                fw.write(line + "\n");
        	}
        }
        fw.close();
	}

	private JSONObject buildNGSWebJSON(VariantPoolDetailedSummary summary, String analType) throws JSONException{
        JSONObject summaryJSON = new JSONObject();
        JSONArray recordSummaries = new JSONArray();
        VariantRecordSummary vrs;
        for(String vrsKey : summary.getVariantRecordSummaries().keySet()){
        	vrs = summary.getVariantRecordSummary(vrsKey);
            recordSummaries.put(vrs.toJSON());
        }
        summaryJSON.put("dta", analType);
        summaryJSON.put("frequency.list", recordSummaries);
        return summaryJSON;
	}
	
	private boolean postFreqsToNGSWeb(JSONObject varFreqs, String ngsWebAddress) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String address = ngsWebAddress + "/Variant/UploadVariantFrequencies";
		logger.info("Posting to NGS.Web at: " + address);

	    try {
	        HttpPost request = new HttpPost(address);
	        StringEntity params =new StringEntity(varFreqs.toString());
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.addHeader("Accept", "text/plain");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
	        
	        logger.info("NGSWeb HTTP Response: " + response.toString());

	        // handle response here...
	    } finally {
	        httpClient.close();
	    }
	    return true;
	}
	
	/**
	 * Parse an ARUP sample manifest file and return a key -> value map
	 * 
	 * @param manifestFile
	 * @return
	 * @throws IOException
	 */
	private HashMap<String, String> readManifest(File manifestFile) throws IOException{
		logger.info("Reading " + manifestFile.getAbsolutePath());
		BufferedReader br = new BufferedReader(new FileReader(manifestFile));
		String line;
		String[] vals;
		HashMap<String, String> manifestMap = new HashMap<String, String>();
		while((line = br.readLine()) != null){
			vals = line.split("=");
			
			/* Require all lines to have 'key=value' format */
			if(vals.length != 2){
				br.close();
				throw new IOException("ERROR: encountered line without 'key=value' format in " +
						manifestFile.getAbsolutePath() + ": " + line);
			}
			
			manifestMap.put(vals[0], vals[1]);
		}
		br.close();
		return manifestMap;
	}

	/**
	 * Copy on file to a new destination
	 * 
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	private void copyFileTo(String srcFile, String destFile) throws IOException{
		FileInputStream src = new FileInputStream(srcFile);
		FileOutputStream dest = new FileOutputStream(destFile);
		dest.getChannel().transferFrom(src.getChannel(), 0, src.getChannel().size());
		src.close();
		dest.close();
	}
	
}
