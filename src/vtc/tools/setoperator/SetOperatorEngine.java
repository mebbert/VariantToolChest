/**
 * 
 */
package vtc.tools.setoperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
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
		ArgumentGroup operation = parser.addArgumentGroup("operation arguments");
		ArgumentGroup output = parser.addArgumentGroup("output arguments");

//		MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup();

		operation.addArgument("-i", "--input").nargs("+").dest("VCF").required(true).type(String.class)
				.help("Specify a VCF input file. Multiple files may be " + 
				"specified at once. An ID may be provided for the input file " + 
				"for use in --set-operation as follows: '--input " + 
				"fId=input.vcf fId2=input2.vcf', where " + 
				"'fId' and 'fId2' are the new IDs. If IDs " + 
				"are excluded, IDs will be assigned as 'v0', " +
				"'v1', etc. by default.");

		operation.addArgument("-s", "--set-operation").nargs("+").dest("OP").required(true).type(String.class)
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
		
		operation.addArgument("-g", "--genotype-intersect-type").dest("INTER_TYPE").type(String.class)
				.setDefault(IntersectType.HET_OR_HOMO_ALT.getCommand())
				.help("Specify the type of intersect to perform for a" +
						" variant across samples (e.g. require all samples" +
						" be heterozygous or require all samples be homozygous" +
						" for the alternate allele). Possible options are: " + 
						createIntersectTypeCommandLineToString());
				
		
		operation.addArgument("-c", "--genotype-complement-type").dest("COMP_TYPE").type(String.class)
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
		
		ArrayList<Object> vcfArgs = new ArrayList<Object>(parsedArgs.getList("VCF"));
		ArrayList<Object> operations = new ArrayList<Object>(parsedArgs.getList("OP"));

		try{

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
//	
//	/**
//	 * Will create VariantPool objects from command line-provided input file strings and return as ArrayList<VariantPool>
//	 * @param inputFiles
//	 * @return
//	 * @throws InvalidInputFileException
//	 */
//	private TreeMap<String, VariantPool> createVariantPools(ArrayList<Object> inputFiles) throws InvalidInputFileException{
//		
//		TreeMap<String, VariantPool> vpMap = new TreeMap<String, VariantPool>();
//		for(Object o : inputFiles){
//			VariantPool vp = new VariantPool(o.toString(), false);
//			vpMap.put(vp.getPoolID(), vp);
//		}
//		return vpMap;
//	}
	
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

	
//	/**
//	 * Print the error to stdout and log. Then print the usage and help
//	 * information and exit
//	 * @param e
//	 */
//	private static void printErrorUsageHelpAndExit(Exception e){
//		System.err.println(e.getMessage());
//		logger.error(e.getMessage());
//		printUsageHelpAndExit();
//	}
//	
//	/**
//	 * Print only the usage and help information and exit.
//	 */
//	private static void printUsageHelpAndExit(){
//		parser.printUsage();
//		parser.printHelp();
//		System.exit(1);		
//	}
}
