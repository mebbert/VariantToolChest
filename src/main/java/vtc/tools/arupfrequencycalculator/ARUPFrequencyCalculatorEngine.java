/**
 * 
 */
package vtc.tools.arupfrequencycalculator;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.log4j.Logger;
import org.broad.tribble.TribbleException;

import vtc.Engine;
import vtc.datastructures.InvalidInputFileException;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author markebbert
 *
 */
public class ARUPFrequencyCalculatorEngine implements Engine{
	
	private static Logger logger = Logger.getLogger(ARUPFrequencyCalculatorEngine.class);
	private static ArgumentParser parser;
	private Namespace parsedArgs;

	
	public ARUPFrequencyCalculatorEngine(String[] args) {
		setupArgParser(args);
	}
	
	private void setupArgParser(String[] args){

		parser = ArgumentParsers.newArgumentParser("ARUPFrequencyCalculator");
		parser.description("ARUP Frequency Calculator (AFC) will navigate ARUP result directories " +
				"and calculate test-specific variant frequencies.");
		parser.defaultHelp(true); // Add default values to help menu
		
		parser
			.addArgument("REF")
			.dest("REF")
			.type(String.class)
			.required(true)
			.help("Specify the reference genome location.");

		parser
			.addArgument("DIR")
			.dest("ROOTDIR")
			.type(String.class)
			.required(true)
			.help("Specify the root directory to traverse searching for ARUP result directories.");
		
		parser
			.addArgument("-e", "--excluded_samples-log")
			.dest("EXCLUDED")
			.setDefault("excluded_samples.txt")
			.type(String.class)
			.help("Specify the log file where all excluded sample names are recorded");
		
		parser
			.addArgument("-a", "--analysis-type-file")
			.dest("ANALYSIS_TYPE")
			.setDefault("analysis_types.txt")
			.type(String.class)
			.help("Specify a file with the analysis types to calculate frequencies for. This file should " +
					"be single values delimited by new lines. The values should specify expected values for " +
					"analysis.type in the sampleManifest files.");

		parser
			.addArgument("-n", "--ngs-address")
			.dest("NGS.WEB")
			.type(String.class)
			.help("Specify the web address for NGS.Web to post results to.");

		parser
			.addArgument("-u", "--union-vcfs")
			.dest("UNION")
			.action(Arguments.storeTrue())
			.help("Specify wether all of the VCFs should be unioned into one master VCF. If false, " +
					"look for vcfs containing ALL sites. i.e., the variant caller was set to emit " +
					"a call at every callable location. For GATK, the appropriate setting is " +
					"EMIT_ALL_SITES. Appropriate statistics will be aggregated for all samples " +
					"with the appropriate vcf.");
		
		try{
			parsedArgs = parser.parseArgs(args);
		} catch (ArgumentParserException e){
			parser.handleError(e);
			System.exit(1);
		}
	}
	
	/**
	 * Calculate frequencies for all samples/variants found in ROOTDIR
	 */
	public void calculateFrequencies(){
		
		// get args
		String rootDir = parsedArgs.getString("ROOTDIR");
		String log = parsedArgs.getString("EXCLUDED");
		String refPath = parsedArgs.getString("REF");
		String analTypeFilePath = parsedArgs.getString("ANALYSIS_TYPE");
		String ngsWebAddress = parsedArgs.getString("NGS.WEB");
		boolean union = parsedArgs.getBoolean("UNION");

		// read analysis type file
		try {
			ARUPFrequencyCalculator arupfc = new ARUPFrequencyCalculator();
			arupfc.calculateFrequencies(rootDir, analTypeFilePath, log, refPath, union, ngsWebAddress);

		} catch (IOException e) {
            UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (InvalidInputFileException e) {
            UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (InvalidOperationException e) {
            UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (URISyntaxException e) {
            UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (TribbleException e) {
            UtilityBelt.printErrorUsageAndExit(parser, logger, e);
        } catch (JSONException e) {
            UtilityBelt.printErrorUsageAndExit(parser, logger, e);
		}
		
		// loop over dirs and union vcfs

	}
	

	
	/**
	 * Given a new vcf, union it to the master for the given analysis type. Or, if
	 * this is the first vcf for the analysis type, make it the master.
	 * @param vcfPath
	 * @param analType
	 * @param sampleName
	 * @param refPath
	 * @throws IOException
	 */
//	private void unionVCFToMaster(String vcfPath, String analType, String sampleName, String refPath) throws IOException{
//		
//		/* If this is the first VCF for this analysis type, just make it the master
//		 * (i.e.) copy it to a new file as the master
//		 */
//		File master_vcf = new File("master_vcf-" + analType + "-" + CURR_DATE + ".vcf");
//		if(!master_vcf.exists()){
//			logger.info("Copying " + vcfPath + " to " + master_vcf.getAbsolutePath());
//			copyFileTo(vcfPath, master_vcf.getName());
//		}
//		else{ // master_vcf exists, so union to it
//			File tmpFile = new File(master_vcf + ".tmp");
//			File tmpFileIdx = new File(master_vcf + ".tmp.idx");
//			String args = "-ti varsA=" + master_vcf + " varsB=" + vcfPath + " -s tmp=u[varsA:varsB] -R "
//					+ refPath + " -o " + tmpFile.getName();
//			logger.info("Running SetOperator with the following args: " + args);
//			SetOperatorEngine soe = new SetOperatorEngine(args.split(" "));
//			soe.operate();
//
//			/* Copy the new tmp file to the master_vcf and then delete */
//			copyFileTo(tmpFile.getName(), master_vcf.getName());
//			tmpFile.delete();
//			tmpFileIdx.delete();
//		}
//	}
	

}
