/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.log4j.Logger;

import vtc.Engine;
import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPool;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author kboehme1
 * 
 */
public class VarStatsEngine implements Engine {

	private static Logger logger = Logger.getLogger(VarStatsEngine.class);
	private static ArgumentParser parser;

	private Namespace parsedArgs;

	

	public VarStatsEngine(String[] args) {

		init(args);
	}

	private void init(String[] args) {

		parser = ArgumentParsers.newArgumentParser("VarStats");
		parser.description("Variant Stats will perform basic statistical analysis.");
		parser.defaultHelp(true); // Add default values to help menu
		ArgumentGroup Stats = parser.addArgumentGroup("statistical arguments");
		ArgumentGroup output = parser.addArgumentGroup("output arguments");

		// MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup();

		Stats.addArgument("-i", "--input")
				.nargs("+")
				.dest("VCF")
				.required(true)
				.type(String.class)
				.help("Specify a VCF input file. Multiple files may be "
						+ "specified at once. An ID may be provided for the input file "
						+ "for use in --set-operation as follows: '--input "
						+ "fId=input.vcf fId2=input2.vcf', where "
						+ "'fId' and 'fId2' are the new IDs. If IDs "
						+ "are excluded, IDs will be assigned as 'v0', "
						+ "'v1', etc. by default.");
		Stats.addArgument("-s", "--summary").dest("Summary")
				.action(Arguments.storeTrue())
				.help("Prints summary statistics to the console");
		Stats.addArgument("-c", "--combined")
				.dest("Combined")
				.action(Arguments.storeTrue())
				.help("Prints summary statistics to the console for mulitple files in one block.  The default is will print each file separately.");
		Stats.addArgument("-a", "--association")
				.action(Arguments.storeTrue()).dest("association")
				.help("Performs an association test (also generates allele frequencies).  "
						+ "Must include a phenotype file with columns (Sample IDs) and (Disease Status)           (-p PHENOTYPE_FILE).");
		Stats.addArgument("-p", "--pheno")
				.nargs("+")
				.dest("pheno")
				.type(String.class)
				.help("Allows for multiple pheno files.");
		
		output.addArgument("-o", "--out").nargs("?")
				.setDefault("variant_list.out.vcf")
				.help("Specify the final output file name.");

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
	 * 
	 * @throws InvalidInputFileException
	 * @throws ArgumentParserException
	 */
	public void doStats() {
		// lets have all the stats take place here for now..

		ArrayList<Object> vcfArgs = new ArrayList<Object>(
				parsedArgs.getList("VCF"));
		ArrayList<Object> phenoArgs = (ArrayList<Object>)
				parsedArgs.getList("pheno");

		try {

			TreeMap<String, VariantPool> AllVPs;
			
			/*if(parsedArgs.getBoolean("merger")){
				/*
				 * Get the list of VCF files (vcfArgs) and use the picard format for merging
				 * create a new arraylist with the newly merged file in it.
				 * create the variant pool and continue with the rest of the commands.
				 */
				
			/*	ArrayList<Object> newVCF = null; 
				newVCF.add("variants_merged.vcf");
				AllVPs = UtilityBelt.createVariantPools(newVCF);
			}
			else{*/
				AllVPs = UtilityBelt.createVariantPools(vcfArgs);
		//	}
			boolean sum = parsedArgs.getBoolean("Summary");
			boolean PrintMulti = parsedArgs.getBoolean("Combined");
			boolean assoc = parsedArgs.getBoolean("association");
			
			VarStats vstat = new VarStats(AllVPs, phenoArgs, PrintMulti, sum, assoc);
			
		} catch (InvalidInputFileException e) {
			UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (Exception e) {
			logger.error("Caught unexpected exception, something is very wrong!");
			e.printStackTrace();
		}
	}
}