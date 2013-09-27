/**
 * 
 */
package vtc.tools.setoperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.log4j.Logger;
import org.broad.tribble.TribbleException;
import org.broadinstitute.variant.vcf.VCFFormatHeaderLine;
import org.broadinstitute.variant.vcf.VCFHeader;
import org.broadinstitute.variant.vcf.VCFHeaderLineType;
import org.broadinstitute.variant.vcf.VCFUtils;

import vtc.Engine;
import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.SupportedFileType;
import vtc.datastructures.VariantPool;
import vtc.tools.setoperator.operation.ComplementOperation;
import vtc.tools.setoperator.operation.IntersectOperation;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.setoperator.operation.Operation;
import vtc.tools.setoperator.operation.OperationFactory;
import vtc.tools.utilitybelt.UtilityBelt;
import vtc.tools.varstats.VarStats;

/**
 * @author markebbert
 *
 */
public class SetOperatorEngine implements Engine{

	private static Logger logger = Logger.getLogger(SetOperatorEngine.class);
	private static ArgumentParser parser;

	private Namespace parsedArgs;

	public SetOperatorEngine(String[] args){
		init(args);
	}

	private void init(String[] args){

		String exampleOper = "Here is an example intersect on two unions:\n" +
				"'-s out1=u[fId1[sId1,sId2]:fId2[sId3]]\n" +
				"out2=u[fId3[sId4,sId5]:fId4[sId6]] out3=i[out1,out2]'.";

		String operFormat = "oId=operator[input_id1[sample_id1,sample_id2,etc.]:input_id2[sample_id3," +
				"sample_id4,etc.]:etc.] ";
		String operDesc = "where 'oId' is a user-provided ID of the operation " +
				"(can be referenced in other operations), 'operator' " +
				"may be any of union ([uU]), intersect ([iI]), and " +
				"complement ([cC]), 'input_id' is a user-provided " +
				"file input ID (see --input), and 'sample_id' is a " +
				"sample ID within a file (or other variant set).";


		/*
		 * Create arguments for the SetOperator
		 */

		parser = ArgumentParsers.newArgumentParser("SetOperator");
		parser.description("Set Operator (SO) will perform various set math operations on sets of variants" +
				" (termed variant pools).");
		parser.defaultHelp(true); // Add default values to help menu
		MutuallyExclusiveGroup operation = parser.addMutuallyExclusiveGroup("required operation arguments")
				.required(true);
		ArgumentGroup operationOptions = parser.addArgumentGroup("operation arguments");
		ArgumentGroup output = parser.addArgumentGroup("output arguments");

//		MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup();


		operation.addArgument("--compare").dest("COMPARE").action(Arguments.storeTrue())
				.help("Automagically perform intersect and complements " +
					"between two input files.");
		operation.addArgument("-s", "--set-operation").nargs("+").dest("OP").type(String.class)
				.help("Specify a set operation between one or more " +
					"input files. Set operations are formatted as follows: " +
					operFormat + operDesc +
					" If only file IDs (i.e. 'fId') are " +
					"provided in the operation, all samples within those " +
					"files will be used. If operation IDs (i.e. 'oId') are excluded, set " +
					"IDs will be assigned as 's0', 's1', " +
					"etc. " + exampleOper + " An 'fId' refers to a " +
					"file ID (see --input) and an 'sId' refers to a " + 
					"sample within a file (or other variant set).");

		operationOptions.addArgument("-i", "--input").nargs("+").dest("VCF").required(true).type(String.class)
				.help("Specify a VCF input file. Multiple files may be " + 
				"specified at once. An ID may be provided for the input file " + 
				"for use in --set-operation as follows: '--input " + 
				"fId=input.vcf fId2=input2.vcf', where " + 
				"'fId' and 'fId2' are the new IDs. If IDs " + 
				"are excluded, IDs will be assigned as 'v0', " +
				"'v1', etc. by default.");
	
		operationOptions.addArgument("-g", "--genotype-intersect-type").dest("INTER_TYPE").type(String.class)
				.setDefault(IntersectType.HET_OR_HOMO_ALT.getCommand())
				.help("Specify the type of intersect to perform for a" +
						" variant across samples (e.g. require all samples" +
						" be heterozygous or require all samples be homozygous" +
						" for the alternate allele). Possible options are: " + 
						createIntersectTypeCommandLineToString());
				
		
		operationOptions.addArgument("-c", "--genotype-complement-type").dest("COMP_TYPE").type(String.class)
				.setDefault(IntersectType.HET_OR_HOMO_ALT.getCommand())
				.help("Specify the type of complement to perform for a" +
						" variant across samples (e.g. require all samples" +
						" be heterozygous). Possible options are: " + 
						createComplementTypeCommandLineToString());

		output.addArgument("-o", "--out").dest("OUT").setDefault("variant_list.out.vcf")
				.help("Specify the final output file name.");
		
		output.addArgument("-f", "--output-file-format").dest("FORMAT").type(String.class)
				.setDefault(SupportedFileType.VCF.getName())
				.help("Specify the output file format. Possible options are: " +
						createSupportedFileTypeCommandLineToString());
		
		output.addArgument("-R", "--reference-genome").dest("REF").type(String.class)
				.help("Specify path to the reference genome associated with the data." +
						" This is required if output format is 'VCF'");
		
		output.addArgument("-I", "--intermediate-files").dest("INTERMEDIATE").action(Arguments.storeTrue())
				.help("Print intermediate files such as when" +
                        " performing multiple set operations. Intermediate" +
                        " files will be named according to the --set-operation" +
                        " IDs (e.g. the user-provided IDs or \'s0.vcf\', \'s1.vcf\', etc.)");
		
		output.addArgument("-r", "--repair-header").dest("REPAIR").action(Arguments.storeTrue())
				.help("(EXPERIMENTAL) Add missing header lines to the VCF. This is useful to make the output VCF" +
						" comply with requirements. INFO and FORMAT annotations must be specified" +
						" in the VCF header to be valid. If a header line is missing, a dummy line will be inserted" +
						" to satisfy requirements. This is not the recommended solution, but can be useful if" +
						" necessary. If false, missing header lines will be ignored.");
		
		output.addArgument("-v", "--verbose").dest("VERBOSE").action(Arguments.storeTrue())
				.help("Print useful information to 'debug.txt'.");
		
		output.addArgument("-a", "--add-chr").dest("CHR").action(Arguments.storeTrue())
				.help("Add 'chr' to chromosome (e.g. 'chr20' instead of '20'");

		try {
			parsedArgs = parser.parseArgs(args);
			logger.info(parsedArgs);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
	}

	/**
	 * Will run all operations provided to the constructor
	 * @throws InvalidInputFileException
	 * @throws InvalidOperationException
	 * @throws ArgumentParserException 
	 */
	public void operate() {
		

		try{
			
			ArrayList<Object> vcfArgs = null, operations = null;
			if(parsedArgs.getList("VCF") != null){
				 vcfArgs = new ArrayList<Object>(parsedArgs.getList("VCF"));
			}
			else{
				throw new RuntimeException("No input files!");
			}
			if(parsedArgs.getList("OP") != null){
				operations = new ArrayList<Object>(parsedArgs.getList("OP"));
			}


			/* Collect and verify arguments */
			String intersectString = parsedArgs.getString("INTER_TYPE");
			IntersectType intersectType = getIntersectTypeByCommand(intersectString);
			if(intersectType == null){
				throw new ArgumentParserException("Invalid intersect type specified: " + intersectString, parser);
			}
			
			String complementString = parsedArgs.getString("COMP_TYPE");
			ComplementType complementType = getComplementTypeByCommand(complementString);
			if(complementType == null){
				throw new ArgumentParserException("Invalid complement type specified: " + complementString, parser);
			}
			

			String outFileName = parsedArgs.getString("OUT");
			File outFile = null;
			if(outFileName != null){
				outFile = new File(outFileName);
			}
			
			SupportedFileType outputFormat = getSupportedFileTypeByName(parsedArgs.getString("FORMAT"));
			String refGenome = parsedArgs.getString("REF");
			
			if(outputFormat == SupportedFileType.VCF && refGenome == null){
				throw new ArgumentParserException("No reference genome specified." +
						" A reference genome must be provided if output format is VCF", parser);
			}
			
			boolean printIntermediateFiles = parsedArgs.getBoolean("INTERMEDIATE");
			boolean repairHeader = parsedArgs.getBoolean("REPAIR");
			boolean verbose = parsedArgs.getBoolean("VERBOSE");
			boolean chr = parsedArgs.getBoolean("CHR");
			boolean compare = parsedArgs.getBoolean("COMPARE");
	
	
			if(compare){
				if(vcfArgs.size() > 2){
					throw new InvalidOperationException("Error: cannot perform auto comparison on more " +
							"than two input files.");
				}
				performComparison(vcfArgs, verbose, chr, complementType, intersectType, outputFormat,
						outFile, refGenome, repairHeader);
			}
			else{
				performOperations(vcfArgs, operations, verbose, chr, complementType,
					intersectType, printIntermediateFiles, outputFormat, outFile, refGenome, repairHeader);
			}
			
			
		} catch(ArgumentParserException e){
			UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (InvalidOperationException e) {
			UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (InvalidInputFileException e) {
			UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (FileNotFoundException e) {
			UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (TribbleException e) {
			UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (Exception e) {
			logger.error("Caught unexpected exception, something is very wrong!");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Given two input files, perform an intersect and both possible complements. Then
	 * print out summaries for each.
	 * 
	 * @param vcfArgs
	 * @param verbose
	 * @param chr
	 * @param complementType
	 * @param intersectType
	 * @param outputFormat
	 * @param outFile
	 * @param refGenome
	 * @param repairHeader
	 * @throws InvalidInputFileException
	 * @throws InvalidOperationException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void performComparison(ArrayList<Object> vcfArgs, boolean verbose, boolean chr,
			ComplementType complementType, IntersectType intersectType,
			SupportedFileType outputFormat, File outFile, String refGenome, boolean repairHeader)
					throws InvalidInputFileException, InvalidOperationException, IOException, URISyntaxException{

		TreeMap<String, VariantPool> allVPs = UtilityBelt.createVariantPools(vcfArgs);
		ArrayList<String> allVPIDs = new ArrayList<String>(allVPs.keySet());
	
		/* create operations. Need to do an intersect and two complements */
		String complement2OperID = "BcompA";
		String intersect = "intersect=i[" + allVPIDs.get(0) + ":" + allVPIDs.get(1) + "]";
		String union = "union=u[" + allVPIDs.get(0) + ":" + allVPIDs.get(1) + "]";
		String complement1 = "AcompB=c[" + allVPIDs.get(0) + ":" + allVPIDs.get(1) + "]";
		String complement2 = complement2OperID + "=c[" + allVPIDs.get(1) + ":" + allVPIDs.get(0) + "]";
		
		/* prepare arguments for 'performOperations()' */
		ArrayList<Object> operations = new ArrayList<Object>();
		operations.add(intersect);
		operations.add(union);
		operations.add(complement1);
		operations.add(complement2);
		
		boolean printIntermediateFiles = true;
		String complement2OutPath = outFile.getCanonicalPath()
				.substring(0,outFile.getCanonicalPath().lastIndexOf(File.separator) + 1);
		File complement2Outfile = new File(complement2OutPath + complement2OperID);
		
		/* perform the operations */
		TreeMap<String, VariantPool> resultingVPs = performOperations(vcfArgs, operations, verbose, chr,
				complementType, intersectType, printIntermediateFiles,
				outputFormat, complement2Outfile, refGenome, repairHeader);
		
		/* Print table showing results of intersect, union, and complements */
		printComparisonTable(resultingVPs);

		/* Print summary tables for each operation */
		new VarStats(resultingVPs, null, false, true, false, null);
	}
	
	
	/**
	 * Perform operations defined on the command line
	 * 
	 * @param vcfArgs
	 * @param operations
	 * @param verbose
	 * @param chr
	 * @param complementType
	 * @param intersectType
	 * @param printIntermediateFiles
	 * @param outputFormat
	 * @param outFile
	 * @param refGenome
	 * @param repairHeader
	 * @return
	 * @throws InvalidInputFileException
	 * @throws InvalidOperationException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private TreeMap<String, VariantPool> performOperations(ArrayList<Object> vcfArgs, ArrayList<Object> operations,
			boolean verbose, boolean chr, ComplementType complementType, IntersectType intersectType,
			boolean printIntermediateFiles, SupportedFileType outputFormat, File outFile, String refGenome,
			boolean repairHeader) throws InvalidInputFileException, InvalidOperationException, IOException, URISyntaxException{
		
		
		TreeMap<String, VariantPool> resultingVPs = new TreeMap<String, VariantPool>();
		TreeMap<String, VariantPool> allVPs = UtilityBelt.createVariantPools(vcfArgs);
		ArrayList<Operation> ops = createOperations(operations, allVPs);

		ArrayList<VariantPool> associatedVPs;
		ArrayList<VCFHeader> associatedVPHeaders;
		VariantPool result = null;
		Operator o;
		String intermediateOut, canonicalPath;
		VCFHeader header;
		for(Operation op : ops){
			SetOperator so = new SetOperator(verbose, chr);
			associatedVPs = getAssociatedVariantPoolsAsArrayList(op, allVPs);
			result = null;
			
			o = op.getOperator();
			if(o == Operator.COMPLEMENT){
				result = so.performComplement((ComplementOperation)op, associatedVPs, complementType);
			}
			else if(o == Operator.INTERSECT){
				result = so.performIntersect((IntersectOperation)op, associatedVPs, intersectType);
			}
			else if(o == Operator.UNION){
				result = so.performUnion(op, associatedVPs);
			}
			else{
				throw new RuntimeException("Something is very wrong! Received an invalid operator: " + o);
			}
			
			if(result != null){
				associatedVPHeaders = getHeaders(associatedVPs);
				
				/* Try to merge headers between the original VCFs and use for the resulting
				 * VariantPool header. If unsuccessful, emit warning and continue. A basic
				 * header will be generated when printed to file.
				 */
				try{
					header = new VCFHeader(VCFUtils.smartMergeHeaders(associatedVPHeaders, true), result.getSamples());
					
					/* If the resulting data has genotype data but the header does not specify
					 * such, add the appropriate format header line
					 */
					if(result.hasGenotypeData() && !header.hasGenotypingData()){
						String s = "Resulting variant pool (" + op.getOperationID() + ") has genotype " +
								"data but the header does not include the appropriate line. Adding and continuing...";
						logger.warn(s);
						System.out.println(s);

						header.addMetaDataLine(new VCFFormatHeaderLine("GT", 1,
								VCFHeaderLineType.String, "Genotype"));
					}

					result.setHeader(header);
				} catch (IllegalStateException e){
					String s = "Could not merge headers from VariantPools in operation: ";
					String c = "Continuing...";
					logger.warn(s + op.toString() + "\t" + c);
					System.out.println("Warning: " + s + "\n" + c);
				}
				
				/* Add the resulting VariantPool to the list of VariantPools
				 * so it's available for future operations.
				 */
				allVPs.put(result.getPoolID(), result);
				resultingVPs.put(result.getPoolID(), result);
				
				/* If user asked to print intermediate files, print the resulting
				 * VariantPool to file.
				 */
				if(printIntermediateFiles){
					intermediateOut = op.getOperationID() + outputFormat.getDefaultExtension();
					canonicalPath = outFile.getCanonicalPath();
					VariantPool.printVariantPool(intermediateOut,
							canonicalPath.substring(0,canonicalPath.lastIndexOf(File.separator) + 1),
							result, refGenome, outputFormat, repairHeader);
					
					logger.info(result.getCount() + " variants written for operation: '" + op.getOperationID() + "'");
				}
			}
			else{
				throw new RuntimeException("Something is very wrong! 'result' should not be null");
			}
		}
		
		/* Now print the final output file */
		if(result != null){
			logger.info("Printing " + result.getPoolID() + " to file: " + outFile.getAbsolutePath());
			VariantPool.printVariantPool(outFile.getAbsolutePath(), result, refGenome, outputFormat, repairHeader);
			
			logger.info(result.getCount() + " variant(s) written.");
		}
		return resultingVPs;
	}
	
	
	private void printComparisonTable(TreeMap<String, VariantPool> resultingVPs){
		Iterator<String> it = resultingVPs.keySet().iterator();
		
		String poolID;
		int acompbCount = 0, bcompaCount = 0, intersectCount = 0, unionCount = 0;
		while(it.hasNext()){
			poolID = it.next();
			
			if("AcompB".equals(poolID)){
				acompbCount = resultingVPs.get(poolID).getCount();
			}
			else if("BcompA".equals(poolID)){
				bcompaCount = resultingVPs.get(poolID).getCount();
			}
			else if("intersect".equals(poolID)){
				intersectCount = resultingVPs.get(poolID).getCount();
			}
			else if("union".equals(poolID)){
				unionCount = resultingVPs.get(poolID).getCount();
			}
		}
		
		
		String newLine = System.getProperty("line.separator");
		String leftAlignFormat = "| %-16s | %12d |" + newLine;
//		String centerAlignFormat = "| %14s | %10s | %13.2f%% | %8.0f |" + newLine;
		System.out.format("===================================" + newLine);
		System.out.format("                                   " + newLine);
		System.out.format("       Summary of comparison       " + newLine);
		System.out.format("                                   " + newLine);
		System.out.format("===================================" + newLine + newLine);
		

		System.out.format("+------------------+--------------+" + newLine);
		System.out.format("|   Variant Pool   |    n Vars    |" + newLine);
		System.out.format("+------------------+--------------+" + newLine);

		System.out.format(leftAlignFormat, "A - B", acompbCount);
		System.out.format(leftAlignFormat, "B - A", bcompaCount);
		System.out.format(leftAlignFormat, "Intersect", intersectCount);
		System.out.format(leftAlignFormat, "Union", unionCount);
	
		System.out.format("+------------------+--------------+" + newLine);
		
		if(acompbCount + bcompaCount + intersectCount == unionCount){
			System.out.format(" Status: OK! Counts are consistent "+ newLine);
		}
		else{
			System.out.format(" Status: ERR! Counts inconsistent  "+ newLine);
		}

		System.out.format("+------------------+--------------+" + newLine + newLine + newLine);
		
	}
	

	/**
	 * Will create Operation objects from command line-provided operation strings and return as ArrayList<Operation>
	 * @param operations
	 * @return
	 * @throws InvalidOperationException
	 */
	private ArrayList<Operation> createOperations(ArrayList<Object> operations, TreeMap<String, VariantPool> variantPools) throws InvalidOperationException{
		
		ArrayList<Operation> opList = new ArrayList<Operation>();
		for(Object o : operations){
			Operation op = OperationFactory.createOperation(o.toString(), variantPools);
			opList.add(op);
		}
		return opList;
	}
	
	/**
	 * Extract only VariantPool objects associated with the Operation provided as an ArrayList<VariantPool>. 
	 * @param op
	 * @param vps
	 * @return
	 */
	private ArrayList<VariantPool> getAssociatedVariantPoolsAsArrayList(Operation op, TreeMap<String, VariantPool> vps){

		/* Get all pool IDs associated with this Operation. Note: All SamplePool objects
		 * have a pool ID that matches a VariantPool pool ID.
		 */
		Collection<String> pids = op.getAllPoolIDs(); 
		ArrayList<VariantPool> vpList = new ArrayList<VariantPool>();
		Iterator<String> it = vps.keySet().iterator();
		String pid;

		/* Iterate over the VP TreeMap and add any VP associated with Operation op to
		 * the vpList and return vpList
		 */
		while(it.hasNext()){
			pid = it.next();
			if(pids.contains(pid)){
				vpList.add(vps.get(pid));
			}
		}
		return vpList;
	}
	
	/**
	 * Get VCFHeaders from the list of VariantPools.
	 * @param vps
	 * @return
	 */
	private ArrayList<VCFHeader> getHeaders(ArrayList<VariantPool> vps){
		
		ArrayList<VCFHeader> headers = new ArrayList<VCFHeader>();
		for(VariantPool vp : vps){
			headers.add(vp.getHeader());
		}
		return headers;
	}
	
	/**
	 * Build a string to dynamically provide allowable input options for
	 * IntersectType
	 * @return
	 */
	private static String createIntersectTypeCommandLineToString(){
		StringBuilder sb = new StringBuilder();
		int count = 1;
		for(IntersectType t : IntersectType.values()){
			sb.append("\n" + Integer.toString(count) + ". " + t.toString());
			count++;
		}
		return sb.toString();
	}
	
	/**
	 * Build a string to dynamically provide allowable input options for
	 * IntersectType
	 * @return
	 */
	private static String createComplementTypeCommandLineToString(){
		StringBuilder sb = new StringBuilder();
		int count = 1;
		for(ComplementType t : ComplementType.values()){
			sb.append("\n" + Integer.toString(count) + ". " + t.toString());
			count++;
		}
		return sb.toString();
	}

	/**
	 * Build a string to dynamically provide allowable input options for
	 * IntersectType
	 * @return
	 */
	private static String createSupportedFileTypeCommandLineToString(){
		StringBuilder sb = new StringBuilder();
		int count = 1;
		for(SupportedFileType t : SupportedFileType.values()){
			sb.append("\n" + Integer.toString(count) + ". " + t.toString());
			count++;
		}
		return sb.toString();
	}
	
	/**
	 * Get the IntersectType based on user input value
	 * @param commandString
	 * @return
	 */
	private static IntersectType getIntersectTypeByCommand(String commandString){
		for(IntersectType i : IntersectType.values()){
			if(i.getCommand().equalsIgnoreCase(commandString)){
				return i;
			}
		}
		return null;
	}
	
	/**
	 * Get the IntersectType based on user input value
	 * @param commandString
	 * @return
	 */
	private static ComplementType getComplementTypeByCommand(String commandString){
		for(ComplementType i : ComplementType.values()){
			if(i.getCommand().equalsIgnoreCase(commandString)){
				return i;
			}
		}
		return null;
	}
	
	/**
	 * Get SupportedFileType by name
	 * @param name
	 * @return
	 */
	public SupportedFileType getSupportedFileTypeByName(String name){
		for (SupportedFileType t : SupportedFileType.values()){
			if(t.getName().equalsIgnoreCase(name)){
				return t;
			}
		}
		return null;
	}
}
