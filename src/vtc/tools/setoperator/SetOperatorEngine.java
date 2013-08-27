/**
 * 
 */
package vtc.tools.setoperator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.log4j.Logger;

import vtc.Engine;
import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPool;

/**
 * @author markebbert
 *
 */
public class SetOperatorEngine implements Engine{

	Logger logger = Logger.getLogger(this.getClass());

	private Namespace parsedArgs;

	public SetOperatorEngine(String[] args){
		init(args);
	}

	private void init(String[] args){

		String exampleOper = "Here is an example intersect on two unions:\n" +
				"\'-s out1=u[fId1[sId1,sId2]:fId2[sId3]]\n" +
				"out2=u[fId3[sId4,sId5]:fId4[sId6]] out3=i[out1,out2]\'.";

		String operFormat = "<oper_id>=<operator>[<input_id1>[<sample_id1>,<sample_id2>,etc.]:<input_id2>[<sample_id3>," +
				"<sample_id4>,etc.]:etc.]\n";
		String operDesc = "where \'<oper_id>\' is a user provided ID of the operation\n" +
				"(can be referenced in other operations), \'<operator>\'\n" +
				"may be any of union ([uU]), intersect ([iI]), and\n" +
				"complement ([cC]), \'<input_id>\' is a user-provided\n" +
				"file input ID (see --input), and \'<sample_id>\' is a\n" +
				"sample ID within an input file.";

		/*
		 * Create new options for the SetOperator
		 */


		ArgumentParser parser = ArgumentParsers.newArgumentParser("SetOperator");
		parser.description("Set Operator (SO) will perform various set math operations on sets of variants" +
				"termed (variant pools).");
		parser.defaultHelp(true); // Add default values to help menu

//		MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup();

		parser.addArgument("-i", "--input").nargs("+").dest("VCF").required(true).type(String.class)
				.help("Specify a VCF input file. Multiple files may be " + 
				"specified at once. An ID may be provided for the input file " + 
				"for use in --set-operation as follows \"--input " + 
				"<fId>=<input.vcf> <fId2>=<input2.vcf>\", where " + 
				"\"<fId>\" and \"<fId2>\" are the new IDs. If IDs " + 
				"are excluded, IDs will be assigned as \"<i0>\", " +
				"\"<i1>\", etc. by default. The new ID may only " +
				"consist of letter, digits, and underscores.");

		parser.addArgument("-s", "--set-operation").nargs("+").dest("OPERATION").required(true).type(String.class)
				.help("Specify a set operation between one or more " +
					"input files. Set operations are formatted as follows: " +
					operFormat + operDesc +
					" If only file IDs (i.e. \"<fId>\") are " +
					"provided in the operation, all samples within those " +
					"files will be used. If operation IDs (i.e. <oper_id>) are excluded, set " +
					"operations will be given IDs as \"<s0>\", \"<s1>\", " +
					"etc. " + exampleOper + "An \"fId\" refers to a " +
					"file ID (see --input) and an \"sId\" refers to a " + 
					"sample within that file.");
				
		parser.addArgument("-o", "--out").nargs(1).setDefault("variant_list.out.vcf")
				.help("Specify the final output file name.");

		try {
			parsedArgs = parser.parseArgs(args);
			logger.info(parsedArgs);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
//		/* Help */ //		Option help = new Option("h", "help", false, "print this message" );
					//		
					//		/* Version */
					//		OptionBuilder.withLongOpt("version");
					//		OptionBuilder.withDescription("print verion");
					//		Option version = OptionBuilder.create();
					//
					//		/* Input files */
					//		OptionBuilder.withArgName("VCF");
					//		OptionBuilder.hasArgs();
					//		OptionBuilder.withLongOpt("input");
					//		OptionBuilder.isRequired();
					//		OptionBuilder.withValueSeparator();
					//		OptionBuilder.withDescription("Specify a VCF input file. Multiple files may be" +
					//				"specified at once. An ID may be provided for the input file" +
					//				"for use in --set-operation as follows \'--input" +
					//				"<fId>=<input.vcf> <fId2>=<input2.vcf>\', where" +
					//				"\'<fId>\' and \'<fId2>\' are the new IDs. If IDs" +
					//				"are excluded, IDs will be assigned as \'<i0>\'," +
					//				"\'<i1>\', etc. by default. The new ID may only" +
					//				"consist of letter, digits, and underscores.");
					//		Option input = OptionBuilder.create("i");	
					//									
					//		/* Set operations */
					//		OptionBuilder.withArgName("OPERATION");
					//		OptionBuilder.hasArgs();
					//		OptionBuilder.withLongOpt("set-operation");
					//		OptionBuilder.isRequired();
					//		OptionBuilder.withValueSeparator();
					//		OptionBuilder.withDescription("Specify a set operation between one or more input files." +
					//				"Set operations are formatted as follows: " +
					//				operFormat + operDesc + "If only file IDs (i.e. \'<fId>\') are " +
					//				"provided in the operation, all samples within those " +
					//				"files will be used. If operation IDs (i.e. <oper_id>) are excluded, set " +
					//				"operations will be given IDs as \'<s0>\', \'<s1>\', " +
					//				"etc. " + exampleOper + "An \'fId\' refers to a " + 
					//				"file ID (see --input) and an \'sId\' refers to a " + 
					//				"sample within that file.");
					//		Option setOper = OptionBuilder.create("s");
					//		
					//		/* Out file */
					//		Option out = new Option("-o", "--outfile", true, "Specify the final output file name.");
					//		
					//		
					//		Options options = new Options();
					//		options.addOption(help);
					//		options.addOption(version);
					//		options.addOption(input);
					//		options.addOption(setOper);
					//		options.addOption(out);
					//		
					//		
					//		CommandLineParser parser = new BasicParser();
					//		HelpFormatter formatter = new HelpFormatter();
					//		try {
					//			parser.parse(options, args);
					//		} catch (ParseException e) {
					//			String message = "Invalid arguments: " + e.getMessage() + "\nSee help for more details.";
					//			System.err.println(message);
					//			logger.error(message);
					//			formatter.printHelp("java -jar vtc.jar SO [OPTIONS] -i <file> [<file2> etc.] -s <operation>", options);
					//		}
	}

	/**
	 * Will run all operations provided to the constructor
	 * @throws InvalidInputFileException
	 * @throws InvalidOperationException
	 */
	public void operate() throws InvalidInputFileException, InvalidOperationException{
		
		@SuppressWarnings("unchecked")
		ArrayList<String> vcfArgs = (ArrayList<String>) parsedArgs.get("VCF");

		@SuppressWarnings("unchecked")
		ArrayList<String> operations = (ArrayList<String>) parsedArgs.get("OPERATION");

		logger.info(vcfArgs.toString());

		TreeMap<String, VariantPool> AllVPs = createVariantPools(vcfArgs);
		ArrayList<Operation> ops = createOperations(operations);

		ArrayList<VariantPool> associatedVPs;
		for(Operation op : ops){
			SetOperator so = new SetOperator();
			associatedVPs = getAssociatedVariantPoolsAsArrayList(op, AllVPs);
		}
	}
	
	/**
	 * Will create VariantPool objects from command line-provided input file strings and return as ArrayList<VariantPool>
	 * @param inputFiles
	 * @return
	 * @throws InvalidInputFileException
	 */
	private TreeMap<String, VariantPool> createVariantPools(ArrayList<String> inputFiles) throws InvalidInputFileException{
		
		TreeMap<String, VariantPool> vpMap = new TreeMap<String, VariantPool>();
		for(String s : inputFiles){
			VariantPool vp = new VariantPool(s);
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
	private ArrayList<Operation> createOperations(ArrayList<String> operations) throws InvalidOperationException{
		
		ArrayList<Operation> opList = new ArrayList<Operation>();
		for(String s : operations){
			Operation op = new Operation(s);
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
}
