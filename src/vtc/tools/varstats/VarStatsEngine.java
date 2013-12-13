/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.HashMap;
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

    private static Logger         logger = Logger.getLogger(VarStatsEngine.class);

    private static ArgumentParser parser;

    private Namespace             parsedArgs;

    public VarStatsEngine(String[] args) {

        init(args);
    }

    private void init(String[] args) {

        parser = ArgumentParsers.newArgumentParser("VarStats");
        parser.description("Variant Stats will perform basic statistical analysis.");
        parser.defaultHelp(true); // Add default values to help menu
        ArgumentGroup Stats = parser.addArgumentGroup("statistical arguments");
        @SuppressWarnings("unused")
		ArgumentGroup output = parser.addArgumentGroup("output arguments");

        // MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup();

        Stats.addArgument("-i", "--input")
                .nargs("+")
                .dest("VCF")
                .required(true)
                .type(String.class)
                .help("Specify a VCF input file. Multiple files may be " + "specified at once. An ID may be provided for the input file " + "for use in --set-operation as follows: '--input " + "fId=input.vcf fId2=input2.vcf', where "
                        + "'fId' and 'fId2' are the new IDs. If IDs " + "are excluded, IDs will be assigned as 'v0', " + "'v1', etc. by default.");
        Stats.addArgument("-s", "--summary").dest("Summary").action(Arguments.storeTrue()).help("Prints summary statistics to the console");
        Stats.addArgument("-c", "--combined").dest("Combined").action(Arguments.storeTrue()).help("Prints summary statistics to the console for mulitple files in one block.  The default will print each file separately.");
        Stats.addArgument("-x", "--side_x_side").dest("Side_x_side").action(Arguments.storeTrue()).help("Prints summary statistics to the console for mulitple files side by side.  The default will print each file separately.");
        Stats.addArgument("-m", "--MultiColumns").dest("MultiColumns").action(Arguments.storeTrue()).help("Prints summary statistics to the console for mulitple files in multiple columns of the same table.  The default will print each file separately.");
        Stats.addArgument("-a", "--association").action(Arguments.storeTrue()).dest("association")
                .help("Performs an association test (also generates allele frequencies).  It only accepts one file. " + "Must include a phenotype file with columns (Sample IDs) and (Disease Status)           (-p PHENOTYPE_FILE).");
        Stats.addArgument("-p", "--pheno").nargs("+").dest("pheno").type(String.class).help("Allows for multiple pheno files.");

        
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

        ArrayList<Object> vcfArgs = new ArrayList<Object>(parsedArgs.getList("VCF"));
        ArrayList<Object> phenoArgs = (ArrayList<Object>) parsedArgs.getList("pheno");

        try {

            TreeMap<String, VariantPool> AllVPs;

            AllVPs = UtilityBelt.createVariantPools(vcfArgs, true);

            boolean sum = parsedArgs.getBoolean("Summary");
            boolean PrintMulti = parsedArgs.getBoolean("Combined");
            boolean assoc = parsedArgs.getBoolean("association");
            boolean side_by_side = parsedArgs.getBoolean("Side_x_side");
            boolean multi_column = parsedArgs.getBoolean("MultiColumns");
            
            
            HashMap<String, VariantPoolSummary> summaries = new HashMap<String, VariantPoolSummary>();
            
            if(sum){
            	summaries = VariantPoolSummarizer.summarizeVariantPools(AllVPs);
            	if(side_by_side)
            		VariantPoolSummarizer.PrintSide_by_Side(summaries);
            	else if(multi_column)
            		VariantPoolSummarizer.Print_Columns(summaries);
            	else
            		VariantPoolSummarizer.printSummary(summaries, PrintMulti);
            	
            }
            if(assoc){
            	VarStats vstat = new VarStats(AllVPs, phenoArgs);
            }
            
            
            
/*
            @SuppressWarnings("unused")
			VarStats vstat = new VarStats(AllVPs, phenoArgs, PrintMulti, sum, assoc);
*/
        } catch (InvalidInputFileException e) {
            UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
        } catch (Exception e) {
            logger.error("Caught unexpected exception, something is very wrong!");
            e.printStackTrace();
        }
    }
}