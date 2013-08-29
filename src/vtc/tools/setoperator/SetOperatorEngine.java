/**
 * 
 */
package vtc.tools.setoperator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import net.sf.samtools.SAMSequenceDictionary;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.log4j.Logger;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriter;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriterFactory;

import vtc.Engine;
import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.SupportedFileType;
import vtc.datastructures.VariantPool;

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
		 * Create new options for the SetOperator
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
		
		operation.addArgument("-g", "--genotype-intersect-type").nargs(1).dest("TYPE").type(String.class)
				.setDefault(IntersectType.HET_OR_HOMO_ALT.getCommand())
				.help("Specify the type of intersect to perform for a" +
						" variant across samples (e.g. require all samples" +
						" be heterozygous or require all samples be homozygous" +
						" for the alternate allele). Possible options are: " + 
						createIntersectTypeCommandLineToString());
				
		output.addArgument("-o", "--out").nargs(1).setDefault("variant_list.out.vcf")
				.help("Specify the final output file name.");
		
		output.addArgument("-f", "--output-file-format").nargs(1).dest("FORMAT").type(String.class)
				.setDefault(SupportedFileType.VCF.getName())
				.help("Specify the output file format. Possible options are: " +
						createSupportedFileTypeCommandLineToString());
		
		output.addArgument("-R", "--reference-genome").nargs(1).dest("REF").type(String.class)
				.help("Specify path to the reference genome associated with the data." +
						" This is required if output format is 'VCF'");
		
		output.addArgument("-I", "--intermediate-files").dest("INTERMEDIATE").action(Arguments.storeTrue())
				.help("Print intermediate files such as when" +
                        " performing multiple set operations. Intermediate" +
                        " files will be named according to the --set-operation" +
                        " IDs (e.g. the user-provided IDs or \'s0.vcf\', \'s1.vcf\', etc.)");

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
			String intersectString = parsedArgs.getString("TYPE");
			IntersectType intersectType = getIntersectTypeByCommand(intersectString);
			if(intersectType == null){
				throw new ArgumentParserException("Invalid intersect type specified: " + intersectString, parser);
			}
			
			SupportedFileType outputFormat = getSupportedFileTypeByName(parsedArgs.getString("FORMAT"));
			String refGenome = parsedArgs.getString("REF");
			
			if(outputFormat == SupportedFileType.VCF && refGenome == null){
				throw new ArgumentParserException("No reference genome specified." +
						" A reference genome must be provided if output format is VCF", parser);
			}
			
			boolean printIntermediateFiles = parsedArgs.getBoolean("INTERMEDIATE");
	
	
			TreeMap<String, VariantPool> AllVPs = createVariantPools(vcfArgs);
			ArrayList<Operation> ops = createOperations(operations);
	
			ArrayList<VariantPool> associatedVPs;
			VariantPool result;
			Operator o;
			for(Operation op : ops){
				SetOperator so = new SetOperator();
				associatedVPs = getAssociatedVariantPoolsAsArrayList(op, AllVPs);
				result = null;
				
				o = op.getOperator();
				if(o == Operator.COMPLEMENT){
					result = so.performComplement(op, associatedVPs);
				}
				else if(o == Operator.INTERSECT){
					result = so.performIntersect(op, associatedVPs, intersectType);
				}
				else if(o == Operator.UNION){
					result = so.performUnion(op, associatedVPs);
				}
				else{
					throw new RuntimeException("Something is very wrong! Received an invalid operator: " + o);
				}
				
				if(result != null){
					AllVPs.put(result.getPoolID(), result);
					
					if(printIntermediateFiles){
//						printVariantPool(result, SupportedFileType.VCF);
					}
				}
				else{
					throw new RuntimeException("Something is very wrong! 'result' should not be null");
				}
			}
		} catch(ArgumentParserException e){
			printErrorUsageHelpAndExit(e);
		} catch (InvalidOperationException e) {
			printErrorUsageHelpAndExit(e);
		} catch (InvalidInputFileException e) {
			printErrorUsageHelpAndExit(e);
		} catch (Exception e) {
			logger.error("Caught unexpected exception, something is very wrong!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Will create VariantPool objects from command line-provided input file strings and return as ArrayList<VariantPool>
	 * @param inputFiles
	 * @return
	 * @throws InvalidInputFileException
	 */
	private TreeMap<String, VariantPool> createVariantPools(ArrayList<Object> inputFiles) throws InvalidInputFileException{
		
		TreeMap<String, VariantPool> vpMap = new TreeMap<String, VariantPool>();
		for(Object o : inputFiles){
			VariantPool vp = new VariantPool(o.toString(), false);
			vpMap.put(vp.getPoolID(), vp);
		}
		return vpMap;
	}
	
	/**
	 * Will create Operation objects from command line-provided operation strings and return as ArrayList<Operation>
	 * @param operations
	 * @return
	 * @throws InvalidOperationException
	 */
	private ArrayList<Operation> createOperations(ArrayList<Object> operations) throws InvalidOperationException{
		
		ArrayList<Operation> opList = new ArrayList<Operation>();
		for(Object o : operations){
			Operation op = new Operation(o.toString());
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
		Set<String> pids = op.getAllPoolIDs(); 
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
			if(i.getCommand().equals(commandString)){
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
			if(t.getName().equals(name)){
				return t;
			}
		}
		return null;
	}
	
	/**
	 * Print a VariantPool to file in the format specified by SupportedFileType. If fileType is
	 * VCF, we must have a SAMSequenceDictionary
	 * @param filePath
	 * @param vp
	 * @param refDict
	 * @param fileType
	 */
	private static void printVariantPool(String filePath, VariantPool vp, SAMSequenceDictionary refDict, SupportedFileType fileType){
		
		if(fileType == SupportedFileType.VCF){
			printVariantPoolToVCF(filePath, vp, refDict);
		}
	}
	
	private static void printVariantPoolToVCF(String filePath, VariantPool vp, SAMSequenceDictionary refDict){
		
		if(refDict == null){
			throw new RuntimeException("Received a 'null' SAMSequenceDictionary. Something is very wrong!");
		}
			VariantContextWriter writer = VariantContextWriterFactory.create(new File(filePath), refDict);
	}
	
	private static void printErrorUsageHelpAndExit(Exception e){
		System.err.println(e.getMessage());
		logger.error(e.getMessage());
		printUsageHelpAndExit();
	}
	
	private static void printUsageHelpAndExit(){
		parser.printUsage();
		parser.printHelp();
		System.exit(1);		
	}
}
