/**
 * 
 */
package vtc.tools.varstats;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

	private static Logger logger = Logger.getLogger(VarStatsEngine.class);
	private static ArgumentParser parser;

	private Namespace parsedArgs;

	HashMap phenoInfo = new HashMap();

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
		Stats.addArgument("-a", "--association")
				.nargs("+")
				.dest("pheno")
				.type(String.class)
				.help("Performs an association test (also generates allele frequencies).  "
						+ "Must include a phenotype file with columns (Sample IDs) and (Disease Status)           (-p PHENOTYPE_FILE).");

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
		ArrayList<Object> phenoArgs = (ArrayList<Object>) parsedArgs
				.getList("pheno");
		
		if (phenoArgs != null) {
			// Make a structure to read in the phenotype information...
			phenoInfo = ParsePhenoFile(phenoArgs);
		}

		try {
			
            if(parsedArgs.getBoolean("Summary")){
                TreeMap<String, VariantPool> AllVPs = UtilityBelt.createVariantPools(vcfArgs);
                VarStats vstat = new VarStats(AllVPs);
            }
			
			/* TODO: This is performed twice! One should be removed!
			 * 
			 */
			TreeMap<String, VariantPool> AllVPs = UtilityBelt.createVariantPools(vcfArgs);
			
			// OK we have the phenotype information (phenoInfo) and we have the
			// vcf (ALLVPs).
			for (VariantPool VP : AllVPs.values()) {
				
				Iterator<String> it = VP.getIterator();
				String currVarKey;
				while (it.hasNext()) {
					currVarKey = it.next();
					//VariantContext vc = AllVPs.get(currVarKey).getVariant(currVarKey); //TODO calculate allele frequencies here. This goes out of bounds
				}
			}	
		} catch (InvalidInputFileException e) {
			UtilityBelt.printErrorUsageHelpAndExit(parser, logger, e);
		} catch (Exception e) {
			logger.error("Caught unexpected exception, something is very wrong!");
			e.printStackTrace();
		}
	}

	private HashMap ParsePhenoFile(ArrayList<Object> phenofiles) {
		HashMap phenos = new HashMap();
		for (Object o : phenofiles) {
			// lets parse the phenotype file.
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(o.toString()));
				String line;
				while ((line = br.readLine()) != null) {
					// process the line.
					String line1[] = line.split("\t");
					phenos.put(line1[0], line1[1]);
				}
				br.close();
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return phenos;
	}
}