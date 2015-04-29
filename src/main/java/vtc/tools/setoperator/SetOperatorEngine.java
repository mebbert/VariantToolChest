/**
 * 
 */
package vtc.tools.setoperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import vtc.datastructures.VariantPoolHeavy;
import vtc.tools.setoperator.operation.ComplementOperation;
import vtc.tools.setoperator.operation.IntersectOperation;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.setoperator.operation.Operation;
import vtc.tools.setoperator.operation.OperationFactory;
import vtc.tools.setoperator.operation.UnionOperation;
import vtc.tools.utilitybelt.UtilityBelt;
import vtc.tools.varstats.VariantPoolSummarizer;
import vtc.tools.varstats.VariantPoolSummary;

/**
 * @author markebbert
 * 
 */
public class SetOperatorEngine implements Engine {

    private static Logger         logger = Logger.getLogger(SetOperatorEngine.class);

    private static ArgumentParser parser;
    
    private Namespace             parsedArgs;

    public SetOperatorEngine(String[] args) {
        init(args);
    }

    private void init(String[] args) {

        String exampleOper = "Here is an example intersect on two unions:\n" + "'-s out1=u[fId1[sId1,sId2]:fId2[sId3]]\n" + "out2=u[fId3[sId4,sId5]:fId4[sId6]] out3=i[out1,out2]'.";

        String operFormat = "oId=operator[input_id1[sample_id1,sample_id2,etc.]:input_id2[sample_id3," + "sample_id4,etc.]:etc.] ";
        String operDesc = "where 'oId' is a user-provided ID of the operation " + "(can be referenced in other operations), 'operator' " + "may be any of union ([uU]), intersect ([iI]), and "
                + "complement ([cC]), 'input_id' is a user-provided " + "file input ID (see --input), and 'sample_id' is a " + "sample ID within a file (or other variant set).";

        /*
         * Create arguments for the SetOperator
         */

        parser = ArgumentParsers.newArgumentParser("SetOperator");
        parser.description("Set Operator (SO) will perform various set math operations on sets of variants" + " (termed variant pools).");
        parser.defaultHelp(true); // Add default values to help menu
        MutuallyExclusiveGroup operation = parser.addMutuallyExclusiveGroup("required operation arguments").required(true);
        ArgumentGroup operationOptions = parser.addArgumentGroup("operation arguments");
        ArgumentGroup output = parser.addArgumentGroup("output arguments");

        // MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup();

        operation
        		.addArgument("--compare")
        		.dest("COMPARE")
        		.action(Arguments.storeTrue())
        		.help("Automagically perform intersect and complements " +
        				"between two input files.");
        operation
                .addArgument("-s", "--set-operation")
                .nargs("+")
                .dest("OP")
                .type(String.class)
                .help("Specify a set operation between one or more " +
                		"input files. Set operations are formatted as follows: " +
                		operFormat + operDesc + " If only file IDs (i.e. 'fId') are " +
                		"provided in the operation, all samples within those " +
                		"files will be used. If operation IDs (i.e. 'oId') are excluded, set " +
                		"IDs will be assigned as 's0', 's1', " + "etc. " + exampleOper +
                		" An 'fId' refers to a " + "file ID (see --input) and an 'sId' refers to a " +
                		"sample within a file (or other variant set).");

        operationOptions
                .addArgument("-i", "--input")
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

        operationOptions
        		.addArgument("-c", "--genotype-complement-type")
        		.dest("COMP_TYPE").type(String.class)
        		.setDefault(ComplementType.HET_OR_HOMO_ALT.getCommand())
                .help("Specify the type of complement to perform for a" +
                		" variant across samples (e.g. require all samples" +
                		" be heterozygous). Possible options are: " +
                		createComplementTypeCommandLineToString());

        operationOptions
                .addArgument("-g", "--genotype-intersect-type")
                .dest("INTER_TYPE")
                .type(String.class)
                .setDefault(IntersectType.HET_OR_HOMO_ALT.getCommand())
                .help("Specify the type of intersect to perform for a" +
                		" variant across samples (e.g. require all samples" +
                		" be heterozygous or require all samples be homozygous" +
                		" for the alternate allele). Possible options are: "
                        + createIntersectTypeCommandLineToString());
        
        operationOptions
        		.addArgument("-t", "--treat-sample-names-as-unique")
        		.dest("UNIQUE")
        		.action(Arguments.storeTrue())
        		.help("Force all sample names to be treated as unique when" +
        				" performing a UNION. Many" +
        				" VCFs use the same sample name. If this option" +
        				" is false, any samples with the same name will be" +
        				" treated as such (i.e., the variants will be unioned" +
        				" into a single sample in the result). Essentially, " +
        				" duplicate names will have the file name appended to" +
        				" the sample name.");

        output.addArgument("-o", "--out")
        		.dest("OUT").setDefault("variant_list.out.vcf")
        		.help("Specify the final output file name.");

        output.addArgument("-f", "--output-file-format")
        		.dest("FORMAT")
        		.type(String.class)
        		.setDefault(SupportedFileType.VCF.getName())
                .help("Specify the output file format. Possible options are: " +
        		createSupportedFileTypeCommandLineToString());

        output.addArgument("-R", "--reference-genome")
        		.dest("REF")
        		.type(String.class)
        		.help("Specify path to the reference genome associated with the data." +
        				" This is required if output format is 'VCF'");

        output.addArgument("-I", "--intermediate-files")
                .dest("INTERMEDIATE")
                .action(Arguments.storeTrue())
                .help("Print intermediate files such as when" +
                		" performing multiple set operations. Intermediate" +
                		" files will be named according to the --set-operation" +
                		" IDs (e.g. the user-provided IDs or \'s0.vcf\', \'s1.vcf\', etc.)");

        output.addArgument("-r", "--repair-header")
                .dest("REPAIR")
                .action(Arguments.storeTrue())
                .help("(EXPERIMENTAL) Add missing header lines to the VCF. This is useful to make the output VCF" +
                		" comply with requirements. INFO and FORMAT annotations must be specified" +
                		" in the VCF header to be valid. If a header line is missing, a dummy line will be inserted" +
                		" to satisfy requirements. This is not the recommended solution, but can be useful if" +
                		" necessary. If false, missing header lines will be ignored.");

        output.addArgument("-v", "--verbose")
        		.dest("VERBOSE")
        		.action(Arguments.storeTrue())
        		.help("Print useful information to 'debug.txt'.");

        output.addArgument("-a", "--add-chr")
        		.dest("CHR")
        		.action(Arguments.storeTrue())
        		.help("Add 'chr' to chromosome (e.g. 'chr20' instead of '20')");

        try {
            parsedArgs = parser.parseArgs(args);
//            logger.info(parsedArgs);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

    /**
     * Will run all operations provided to the constructor
     * 
     * @throws InvalidInputFileException
     * @throws InvalidOperationException
     * @throws ArgumentParserException
     */
    public void operate() {

        try {

            List<String> vcfArgs = null, operations = null;
            if (parsedArgs.getList("VCF") != null) {
                vcfArgs = parsedArgs.getList("VCF");
            } else {
                throw new RuntimeException("No input files!");
            }
            if (parsedArgs.getList("OP") != null) {
                operations = parsedArgs.getList("OP");
            }

            /* Collect and verify arguments */
            String intersectString = parsedArgs.getString("INTER_TYPE");
            IntersectType intersectType = getIntersectTypeByCommand(intersectString);
            if (intersectType == null) {
                throw new ArgumentParserException("Invalid intersect type specified: " + intersectString, parser);
            }
            System.out.println("Intersect type: " + intersectString);

            String complementString = parsedArgs.getString("COMP_TYPE");
            ComplementType complementType = getComplementTypeByCommand(complementString);
            if (complementType == null) {
                throw new ArgumentParserException("Invalid complement type specified: " + complementString, parser);
            }
            System.out.println("Complement type: " + complementString);

            String outFileName = parsedArgs.getString("OUT");
            File outFile = null;
            if (outFileName != null) {
                outFile = new File(outFileName);
            }

            SupportedFileType outputFormat = getSupportedFileTypeByName(parsedArgs.getString("FORMAT"));
            String refGenomeString = parsedArgs.getString("REF");

            File refGenome = null;
            if (outputFormat == SupportedFileType.VCF && refGenomeString == null) {
                throw new ArgumentParserException("No reference genome specified." +
                		" A reference genome must be provided if output format is VCF", parser);
            }
            else{
	            refGenome = new File(refGenomeString);
	            
	            if(!refGenome.exists()){
	            	throw new ArgumentParserException(refGenomeString + " not found.", parser);
	            }
            }

            boolean printIntermediateFiles = parsedArgs.getBoolean("INTERMEDIATE");
            boolean repairHeader = parsedArgs.getBoolean("REPAIR");
            boolean verbose = parsedArgs.getBoolean("VERBOSE");
            boolean addChr = parsedArgs.getBoolean("CHR");
            boolean compare = parsedArgs.getBoolean("COMPARE");
            boolean forceUniqueNames = parsedArgs.getBoolean("UNIQUE");

            if (compare) {
                if (vcfArgs.size() > 2) {
                    throw new InvalidOperationException("Error: cannot perform auto comparison on more " + "than two input files.");
                }
                performComparison(vcfArgs, verbose, addChr, complementType, intersectType,
                		outputFormat, outFile, refGenome, repairHeader, forceUniqueNames);
            } else {
                performOperations(vcfArgs, null, operations, verbose, addChr, complementType,
                		intersectType, printIntermediateFiles, outputFormat, outFile, refGenome, repairHeader, forceUniqueNames);
            }

        } catch (NumberFormatException e) {
        	InvalidInputFileException ie = new InvalidInputFileException("Java through a NumberFormatException. " +
        			"Expected numeric value." + " May be an invalid annotation value. The original" +
        					" error was: '" + e.getMessage() + "'");
            UtilityBelt.printErrorUsageAndExit(parser, logger, ie);
        } catch (ArgumentParserException e) {
            UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
        } catch (InvalidOperationException e) {
            UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
        } catch (InvalidInputFileException e) {
            UtilityBelt.printErrorUsageAndExit(parser, logger, e);
        } catch (FileNotFoundException e) {
            UtilityBelt.printErrorUsageAndExit(parser, logger, e);
        } catch (TribbleException e) {
            UtilityBelt.printErrorUsageAndExit(parser, logger, e);
        }  catch (Exception e) {
            logger.error("Caught unexpected exception, something is very wrong!");
            e.printStackTrace();
        }
    }

    /**
     * Given two input files, perform an intersect and both possible
     * complements. Then print out summaries for each.
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
    private void performComparison(List<String> vcfArgs, boolean verbose, boolean addChr,
    		ComplementType complementType, IntersectType intersectType, SupportedFileType outputFormat,
    		File outFile, File refGenome, boolean repairHeader, boolean forceUniqueNames)
            throws InvalidInputFileException, InvalidOperationException, IOException, URISyntaxException {

        TreeMap<String, VariantPoolHeavy> allVPs = UtilityBelt.createHeavyVariantPools(vcfArgs, addChr);
        ArrayList<String> allVPIDs = new ArrayList<String>(allVPs.keySet());

        /* create operations. Need to do an intersect and two complements */
        String complement2OperID = "BcompA";
        String intersect = "intersect=i[" + allVPIDs.get(0) + ":" + allVPIDs.get(1) + "]";
        String union = "union=u[" + allVPIDs.get(0) + ":" + allVPIDs.get(1) + "]";
        String complement1 = "AcompB=c[" + allVPIDs.get(0) + ":" + allVPIDs.get(1) + "]";
        String complement2 = complement2OperID + "=c[" + allVPIDs.get(1) + ":" + allVPIDs.get(0) + "]";

        /* prepare arguments for 'performOperations()' */
        ArrayList<String> operations = new ArrayList<String>();
        operations.add(intersect);
        operations.add(union);
        operations.add(complement1);
        operations.add(complement2);

        boolean printIntermediateFiles = true;
        String complement2OutPath = outFile.getCanonicalPath().substring(0, outFile.getCanonicalPath().lastIndexOf(File.separator) + 1);
        File complement2Outfile = new File(complement2OutPath + complement2OperID);

        /* perform the operations */
        TreeMap<String, VariantPoolHeavy> resultingVPs = performOperations(vcfArgs, allVPs, operations,
        		verbose, addChr, complementType, intersectType, printIntermediateFiles, outputFormat,
        		complement2Outfile, refGenome, repairHeader, forceUniqueNames);

        /* Print table showing results of intersect, union, and complements */
        printComparisonTable(resultingVPs);

        /* Print summary tables for each operation */
        HashMap<String, VariantPoolSummary> vpSummaries = VariantPoolSummarizer.summarizeVariantPools(resultingVPs);
//        VariantPoolSummarizer.printSummary(vpSummaries, false);
        VariantPoolSummarizer.PrintSide_by_Side(vpSummaries);
//        new VarStats(resultingVPs, null, false, true, false);
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
    private TreeMap<String, VariantPoolHeavy> performOperations(List<String> vcfArgs, TreeMap<String, VariantPoolHeavy> allVPs,
    		List<String> operations, boolean verbose, boolean addChr, ComplementType complementType,
    		IntersectType intersectType, boolean printIntermediateFiles, SupportedFileType outputFormat,
    		File outFile, File refGenome, boolean repairHeader, boolean forceUniqueNames)
    				throws InvalidInputFileException, InvalidOperationException, IOException, URISyntaxException {

        TreeMap<String, VariantPoolHeavy> resultingVPs = new TreeMap<String, VariantPoolHeavy>();
        
        if(allVPs == null){
	        allVPs = UtilityBelt.createHeavyVariantPools(vcfArgs, addChr);
        }

//        ArrayList<Operation> ops = UtilityBelt.createOperations(operations, allVPs);

        ArrayList<VariantPoolHeavy> associatedVPs;
        ArrayList<VCFHeader> associatedVPHeaders;
        VariantPoolHeavy result = null;
        Operator o;
        String intermediateOut, canonicalPath;
        VCFHeader header;
//        for (Operation op : ops) {
        for (String oper : operations) {
        	Operation op = OperationFactory.createOperation(oper, allVPs);
            SetOperator so = new SetOperator(verbose, addChr);
            associatedVPs = UtilityBelt.getAssociatedVariantPoolsAsArrayList(op, allVPs);
            result = null;

            o = op.getOperator();
            if (o == Operator.COMPLEMENT) {
            	System.out.println("\nPerforming complement...");
                result = so.performComplement((ComplementOperation) op, associatedVPs, complementType);
            } else if (o == Operator.INTERSECT) {
            	System.out.println("\nPerforming intersect...");
                result = so.performIntersect((IntersectOperation) op, associatedVPs, intersectType);
            } else if (o == Operator.UNION) {
            	System.out.println("\nPerforming union...");
                result = so.performUnion((UnionOperation)op, associatedVPs, forceUniqueNames);
            } else {
                throw new RuntimeException("Something is very wrong! Received an invalid operator: " + o);
            }

            if (result != null) {
                associatedVPHeaders = getHeaders(associatedVPs);

                /*
                 * Try to merge headers between the original VCFs and use for
                 * the resulting VariantPool header. If unsuccessful, emit
                 * warning and continue. A basic header will be generated when
                 * printed to file.
                 */
                try {
                    header = new VCFHeader(VCFUtils.smartMergeHeaders(associatedVPHeaders, true), result.getSamples());

                    /*
                     * If the resulting data has genotype data but the header
                     * does not specify such, add the appropriate format header
                     * line
                     */
                    if (result.hasGenotypeData() && !header.hasGenotypingData()) {
                        String s = "Resulting variant pool (" + op.getOperationID() + ") has genotype " + "data but the header does not include the appropriate line. Adding and continuing...";
                        logger.warn(s);
                        System.out.println(s);

                        header.addMetaDataLine(new VCFFormatHeaderLine("GT", 1, VCFHeaderLineType.String, "Genotype"));
                    }

                    result.setHeader(header);
                } catch (IllegalStateException e) {
                    String s = "Could not merge headers from VariantPools in operation: ";
                    String c = "Continuing...";
                    logger.warn(s + op.toString() + "\t" + c);
                    System.out.println("Warning: " + s + "\n" + c);
                }

                /*
                 * Add the resulting VariantPool to the list of VariantPools so
                 * it's available for future operations.
                 */
                allVPs.put(result.getPoolID(), result);
                resultingVPs.put(result.getPoolID(), result);

                /*
                 * If user asked to print intermediate files, print the
                 * resulting VariantPool to file.
                 */
                if (printIntermediateFiles) {
	            	System.out.println("\nPrinting intermediate file for " + op.getOperationID());
                    intermediateOut = op.getOperationID() + outputFormat.getDefaultExtension();
                    canonicalPath = outFile.getCanonicalPath();
                    VariantPoolHeavy.printVariantPool(intermediateOut,
                    		canonicalPath.substring(0, canonicalPath.lastIndexOf(File.separator) + 1),
                    		result, refGenome, outputFormat, repairHeader);

                    logger.info(result.getNumVarRecords() + " variants written for operation: '" + op.getOperationID() + "'");
                }
            } else {
                throw new RuntimeException("Something is very wrong! 'result' should not be null");
            }
        }

        /* Now print the final output file */
        if (result != null) {
            logger.info("Printing " + result.getPoolID() + " to file: " + outFile.getAbsolutePath());
            VariantPoolHeavy.printVariantPool(outFile.getAbsolutePath(), result, refGenome, outputFormat, repairHeader);

            logger.info(result.getNumVarRecords() + " variant record(s) written.");
        }
        return resultingVPs;
    }

    /**
     * Print the resulting comparison table
     * 
     * @param resultingVPs
     */
    private void printComparisonTable(TreeMap<String, VariantPoolHeavy> resultingVPs) {
        Iterator<String> it = resultingVPs.keySet().iterator();

        String poolID;
        int acompbCount = 0, bcompaCount = 0, intersectCount = 0, unionCount = 0,
        	acompbFuzCount = 0, bcompaFuzCount = 0, intersectFuzCount = 0, unionFuzCount = 0;
        VariantPoolHeavy result;
        while (it.hasNext()) {
            poolID = it.next();
            result = resultingVPs.get(poolID);
            if ("AcompB".equals(poolID)) {
                acompbCount = result.getNumVarRecords();
                acompbFuzCount = result.getPotentialMatchingIndelAlleles();
            } else if ("BcompA".equals(poolID)) {
                bcompaCount = result.getNumVarRecords();
                bcompaFuzCount = result.getPotentialMatchingIndelAlleles();
            } else if ("intersect".equals(poolID)) {
                intersectCount = result.getNumVarRecords();
                intersectFuzCount = result.getPotentialMatchingIndelAlleles();
            } else if ("union".equals(poolID)) {
                unionCount = result.getNumVarRecords();
                unionFuzCount = result.getPotentialMatchingIndelAlleles();
            }
        }

        String newLine = System.getProperty("line.separator");
        String leftAlignFormat = "| %-16s | %12d | %15d |" + newLine;
        // String centerAlignFormat = "| %14s | %10s | %13.2f%% | %8.0f |" +
        // newLine;
        System.out.format("\n=====================================================" + newLine);
        System.out.format("                                                     " + newLine);
        System.out.format("                Summary of comparison                " + newLine);
        System.out.format("                                                     " + newLine);
        System.out.format("=====================================================" + newLine + newLine);

        System.out.format("+------------------+--------------+-----------------+" + newLine);
        System.out.format("|   Variant Pool   |    n Vars    | n Fuzzy Matches |" + newLine);
        System.out.format("+------------------+--------------+-----------------+" + newLine);

        System.out.format(leftAlignFormat, "A - B", acompbCount, acompbFuzCount);
        System.out.format(leftAlignFormat, "B - A", bcompaCount, bcompaFuzCount);
        System.out.format(leftAlignFormat, "Intersect", intersectCount, intersectFuzCount);
        System.out.format(leftAlignFormat, "Union", unionCount, unionFuzCount);

        System.out.format("+------------------+--------------+-----------------+" + newLine);

    	int sum = acompbCount + bcompaCount + intersectCount;

        if (sum == unionCount) {
            System.out.format(" Status: OK! Counts are consistent " + newLine);
        } else {
            System.out.format(" Status: ERR! Counts inconsistent. Sum of  " + newLine +
            				  " " + acompbCount + " + " + bcompaCount + " + " + intersectCount +
            				  " = " + sum + ". Should be " + unionCount + "." + newLine + newLine +
            				  " There may have been separate record for the same" + newLine +
            				  " locus and ref. " + newLine +
            				  " Also verify intersect and complement options." + newLine);
        }

        System.out.format("+------------------+--------------+-----------------+" + newLine + newLine + newLine);

    }
    

    /**
     * Get VCFHeaders from the list of VariantPools.
     * 
     * @param vps
     * @return
     */
    private ArrayList<VCFHeader> getHeaders(ArrayList<VariantPoolHeavy> vps) {

        ArrayList<VCFHeader> headers = new ArrayList<VCFHeader>();
        for (VariantPoolHeavy vp : vps) {
            headers.add(vp.getHeader());
        }
        return headers;
    }

    /**
     * Build a string to dynamically provide allowable input options for
     * IntersectType
     * 
     * @return
     */
    private static String createIntersectTypeCommandLineToString() {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (IntersectType t : IntersectType.values()) {
            sb.append("\n" + Integer.toString(count) + ". " + t.toString());
            count++;
        }
        return sb.toString();
    }

    /**
     * Build a string to dynamically provide allowable input options for
     * IntersectType
     * 
     * @return
     */
    private static String createComplementTypeCommandLineToString() {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (ComplementType t : ComplementType.values()) {
            sb.append("\n" + Integer.toString(count) + ". " + t.toString());
            count++;
        }
        return sb.toString();
    }

    /**
     * Build a string to dynamically provide allowable input options for
     * IntersectType
     * 
     * @return
     */
    private static String createSupportedFileTypeCommandLineToString() {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (SupportedFileType t : SupportedFileType.values()) {
            sb.append("\n" + Integer.toString(count) + ". " + t.toString());
            count++;
        }
        return sb.toString();
    }

    /**
     * Get the IntersectType based on user input value
     * 
     * @param commandString
     * @return
     */
    private static IntersectType getIntersectTypeByCommand(String commandString) {
        for (IntersectType i : IntersectType.values()) {
            if (i.getCommand().equalsIgnoreCase(commandString)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Get the IntersectType based on user input value
     * 
     * @param commandString
     * @return
     */
    private static ComplementType getComplementTypeByCommand(String commandString) {
        for (ComplementType i : ComplementType.values()) {
            if (i.getCommand().equalsIgnoreCase(commandString)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Get SupportedFileType by name
     * 
     * @param name
     * @return
     */
    public SupportedFileType getSupportedFileTypeByName(String name) {
        for (SupportedFileType t : SupportedFileType.values()) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
