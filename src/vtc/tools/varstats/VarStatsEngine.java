/**
 * 
 */
package vtc.tools.varstats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
        ArgumentGroup summary = parser.addArgumentGroup("Summary arguments");
        ArgumentGroup assoc = parser.addArgumentGroup("Association arguments");

        // MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup();

        parser.addArgument("-i", "--input")
                .nargs("+")
                .dest("VCF")
                .required(true)
                .type(String.class)
                .help("Specify a VCF input file. Multiple files may be " +
                		"specified at once. An ID may be provided for the input file " +
                		"for use in --set-operation as follows: '--input " +
                		"fId=input.vcf fId2=input2.vcf', where " +
                		"'fId' and 'fId2' are the new IDs. If IDs " +
                		"are excluded, IDs will be assigned as 'v0', " +
                		"'v1', etc. by default.");
        summary.addArgument("-s", "--summary")
		.dest("SUMMARY")
		.type(String.class)
		.setDefault(SupportedSummaryTypes.TABLE.getName())
        .help("Specify the summary format. Possible options are: " +
                createSupportedSummaryTypeString());
//        Stats.addArgument("-s", "--summary").dest("Summary").action(Arguments.storeTrue()).help("Prints summary statistics to the console");
//        Stats.addArgument("-c", "--combined").dest("Combined").action(Arguments.storeTrue()).help("Prints summary statistics to the console for mulitple files in one block.  The default will print each file separately.");
//        Stats.addArgument("-x", "--side_x_side").dest("Side_x_side").action(Arguments.storeTrue()).help("Prints summary statistics to the console for mulitple files side by side.  The default will print each file separately.");
//        Stats.addArgument("-m", "--MultiColumns").dest("MultiColumns").action(Arguments.storeTrue()).help("Prints summary statistics to the console for mulitple files in multiple columns of the same table.  The default will print each file separately.");
        summary.addArgument("-d", "--detailed-summary")
        		.dest("DETAILED")
        		.type(String.class)
        		.setDefault(SupportedDetailedSummaryTypes.INDIVIDUAL.getName())
        		.help("Prints detailed summary statistics to file. Possible opteions are: "
        				+ createSupportedDetailedSummaryTypeString());
        assoc.addArgument("-a", "--association").action(Arguments.storeTrue()).dest("association")
                .help("Performs an association test (also generates allele frequencies).  It only accepts one file. " + "Must include a phenotype file with columns (Sample IDs) and (Disease Status)           (-p PHENOTYPE_FILE).");
        assoc.addArgument("-p", "--pheno").nargs("+").dest("pheno").type(String.class).help("Allows for multiple pheno files.");

        
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
            boolean sum = false, detailedSummary = false;
            SupportedSummaryTypes  summaryType = null;
            SupportedDetailedSummaryTypes detSumType = null;

            String sumTypeString = parsedArgs.getString("SUMMARY");
            if(sumTypeString != null){
                summaryType = getSupportedSummaryTypeByCommand(sumTypeString);
                sum = true;
                if (summaryType == null) {
                    throw new ArgumentParserException("Invalid summary type specified: " + sumTypeString, parser);
                }
            }

            String detailedSumType = parsedArgs.getString("DETAILED");
            if(detailedSumType != null){
                detSumType = getSupportedDetailedSummaryTypeByCommand(detailedSumType);
                detailedSummary = true;
                if(detSumType == null){
                    throw new ArgumentParserException("Invalid detailed summary type specified: " + detailedSumType, parser);
                }
            }

//            boolean combined = parsedArgs.getBoolean("Combined");
            boolean assoc = parsedArgs.getBoolean("association");
//            boolean side_by_side = parsedArgs.getBoolean("Side_x_side");
//            boolean multi_column = parsedArgs.getBoolean("MultiColumns");
            
            HashMap<String, VariantPoolSummary> summaries = new HashMap<String, VariantPoolSummary>();
            
            AllVPs = UtilityBelt.createVariantPools(vcfArgs, true);
            if(sum){
            	summaries = VariantPoolSummarizer.summarizeVariantPools(AllVPs);
            	if(summaryType == SupportedSummaryTypes.SIDE_BY_SIDE)
            		VariantPoolSummarizer.PrintSide_by_Side(summaries);
            	else if(summaryType == SupportedSummaryTypes.TABLE)
            		VariantPoolSummarizer.Print_Columns(summaries);
            	else if(summaryType == SupportedSummaryTypes.COMBINED){
            		VariantPoolSummarizer.printSummary(summaries, true);
            	}
            	else if(summaryType == SupportedSummaryTypes.INDIVIDUAL){
            		VariantPoolSummarizer.printSummary(summaries, false);
            	}
            	
            }
            if(detailedSummary){
            	// generate detailed summary
            	
            	if(detSumType == SupportedDetailedSummaryTypes.COMBINED){
                    VariantPoolDetailedSummary summary =
                            VariantPoolSummarizer.summarizeVariantPoolsDetailedCombined(AllVPs);
                    String fileName = "unionedVP_detailed_summary.txt";
                    printDetailedSummaryToFile(summary, fileName);
            	}
            	else if(detSumType == SupportedDetailedSummaryTypes.INDIVIDUAL){
                    HashMap<String, VariantPoolDetailedSummary> detailedSummaries =
                            VariantPoolSummarizer.summarizeVariantPoolsDetailed(AllVPs);
                    printDetailedSummariesToFile(detailedSummaries);
            	}
            }
            if(assoc){
            	new VarStats(AllVPs, phenoArgs);
            }
            
        } catch (InvalidInputFileException e) {
            UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
        } catch (Exception e) {
            logger.error("Caught unexpected exception, something is very wrong!");
            e.printStackTrace();
        }
    }
    
	private void printDetailedSummariesToFile(HashMap<String, VariantPoolDetailedSummary> detailedSummaries) throws IOException{
        
        Iterator<String> summaryIT = detailedSummaries.keySet().iterator();
        String vpID, fileName;
        VariantPoolDetailedSummary summary;
        while(summaryIT.hasNext()){
            vpID = summaryIT.next();
            fileName = vpID + "_detailed_summary.txt";
            summary = detailedSummaries.get(vpID);
            printDetailedSummaryToFile(summary, fileName);
        }
	}
	
	private static String createSupportedDetailedSummaryTypeString(){
		StringBuilder sb = new StringBuilder();
		int count = 1;
		for(SupportedDetailedSummaryTypes t : SupportedDetailedSummaryTypes.values()){
			sb.append("\n" + Integer.toString(count) + ". " + t.toString());
			count++;
		}
		return sb.toString();
	}

	private static String createSupportedSummaryTypeString(){
		StringBuilder sb = new StringBuilder();
		int count = 1;
		for(SupportedSummaryTypes t : SupportedSummaryTypes.values()){
			sb.append("\n" + Integer.toString(count) + ". " + t.toString());
			count++;
		}
		return sb.toString();
	}

    private static SupportedSummaryTypes getSupportedSummaryTypeByCommand(String commandString) {
        for (SupportedSummaryTypes s : SupportedSummaryTypes.values()) {
            if (s.permittedCommandsContain(commandString)) {
                return s;
            }
        }
        return null;
    }	

    private static SupportedDetailedSummaryTypes getSupportedDetailedSummaryTypeByCommand(String commandString) {
        for (SupportedDetailedSummaryTypes s : SupportedDetailedSummaryTypes.values()) {
            if (s.permittedCommandsContain(commandString)) {
                return s;
            }
        }
        return null;
    }
    
    /**
     * Print a single detailed summary to the given file
     * 
     * @param summary
     * @param fileName
     * @throws FileNotFoundException
     */
    private void printDetailedSummaryToFile(VariantPoolDetailedSummary summary, String fileName) throws FileNotFoundException{
        logger.info("Writing detailed summary to: " + fileName);
        String header = "Chr\tPos\tID\tRef\tAlt\tRef_allele_count\tAlt_allele_count" +
                "\tRef_sample_count\tAlt_sample_count\tN_samples_with_call\tN_genos_called\tN_total_samples\t" +
                "Alt_genotype_freq\tAlt_sample_freq\tMin_depth\tMax_depth\tAvg_depth\tQuality";
        PrintWriter writer = new PrintWriter(fileName);
        writer.println(header);
        VariantRecordSummary vrs;
        for(String vrsKey : summary.getVariantRecordSummaries().keySet()){
        	vrs = summary.getVariantRecordSummary(vrsKey);
            writer.println(vrs.toString());
        }
        writer.close();   	
    }
}