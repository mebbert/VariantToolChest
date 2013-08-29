package vtc.datastructures;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import net.sf.samtools.SAMSequenceDictionary;

import org.apache.log4j.Logger;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriter;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriterFactory;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.broadinstitute.variant.vcf.VCFHeader;


/**
 * @author markebbert
 *
 *	VariantPool is designed to handle a pool of variants of class VariantContext and any
 *	necessary operations on those pools. Pools may be thought of as variants from files
 *	such as VCFs or resultant pools after performing set operations between other
 *	VariantPool objects.
 */
/**
 * @author markebbert
 *
 */
public class VariantPool implements Pool{
	
	private static ArrayList<String> usedPoolIDs = new ArrayList<String>();
	private static ArrayList<String> generatedPoolIDs = new ArrayList<String>();
	
	private Logger logger = Logger.getLogger(this.getClass());

	private HashMap<String, VariantContext> hMap;
	private TreeMap<String, VariantContext> tMap;
	private String poolID;
	private File file;
	private VCFHeader header;

	
	/****************************************************
	 * Constructors
	 */
	
	/**
	 * Create an empty VariantPool. Use this for building a
	 * VariantPool from scratch rather than reading from a file.
	 */
	public VariantPool(){
		init();
	}

	public VariantPool(VariantPool vp){
		this(vp.file, vp.poolID);
	}
	
	public VariantPool(String filePath, String poolID){
		this(new File(filePath), poolID);
	}
	
	public VariantPool(File file, String poolID){
		this(file, poolID, false);
	}

	public VariantPool(File file, String poolID, boolean requireIndex){
		logger.info("Creating new VariantPool from " + file.getName() + " with poolID " + poolID);
		init(file, poolID, requireIndex);
				
	}
	
	public VariantPool(String inputString, boolean requireIndex) throws InvalidInputFileException{
		HashMap<String, String> fileAndPoolIDMap = this.parseInputString(inputString);
		init(new File(fileAndPoolIDMap.get("file")), fileAndPoolIDMap.get("poolID"), requireIndex);
	}

	private void init(File file, String poolID, boolean requireIndex){
		init();

		this.file = file;
		this.poolID = poolID;
		addPoolID(this.poolID);
		this.parseVCF(file.getPath(), requireIndex);
	}
	
	private void init(){
		this.tMap = new TreeMap<String, VariantContext>();
		this.hMap = new HashMap<String, VariantContext>();
	}
	
	
	
	
	/****************************************************
	 *  Getters
	 */
	
	public String getPoolID(){
		return this.poolID;
	}
	
	public File getFile(){
		return this.file;
	}
	
	public static ArrayList<String> getAllPoolIDs(){
		return VariantPool.usedPoolIDs;
	}
	
	/**
	 * Get variant by chromosome and position
	 * @param chr
	 * @param pos
	 * @return Either a VariantContext object or null
	 */
	public VariantContext getVariant(String chr, int pos){
		return getVariant(chr + ":" + Integer.toString(pos));
	}

	/**
	 * Get variant by key ('chr:pos')
	 * @param key
	 * @return Either a VariantContext object or null
	 */
	public VariantContext getVariant(String key){
		return this.hMap.get(key);
	}

	
	public VCFHeader getHeader(){
		return this.header;
	}
	
	/**
	 * Return the number of variants in this pool
	 * @return
	 */
	public int getCount(){
		return hMap.size();
	}
	
	/**
	 * Return an Iterator<String> object to an ordered key set. The
	 * keys are formatted as 'chr:pos' and ordered 'naturally'.
	 * @return
	 */
	public Iterator<String> getIterator(){
		return this.tMap.keySet().iterator();
	}	
	
	
	
	
	/****************************************************
	 * Setters
	 */
	
	public void setFile(File file){
		this.file = file;
	}
	
	public void setPoolID(String poolID){
		this.poolID = poolID;
	}
	
	
	
	
	/****************************************************
	 * Useful operations
	 */

	/**
	 * This method will parse an input string from the command line. e.g.
	 * f1=/path/to/file.vcf or the same without the 'f1='
	 * @param inputString
	 * @return A HashMap<String, String> with the poolID and file string. Keys are 'poolID' and
	 * 'file', respectively
	 * @throws InvalidInputFileException
	 */
	private HashMap<String, String> parseInputString(String inputString) throws InvalidInputFileException{
		/* Loop over all input files, extract any user-specified pool IDs and file names,
		 * and create associated VariantPool objects
		 */
		String[] inputVals = inputString.split("=");
		HashMap<String, String> fileAndPoolIDMap = new HashMap<String, String>(2); // only need size 2
		
		if(inputVals.length == 1){
			fileAndPoolIDMap.put("file", inputVals[0]);
			fileAndPoolIDMap.put("poolID", VariantPool.generatePoolID(SupportedFileType.VCF));
		}
		else if(inputVals.length > 2 || inputVals.length < 1){
			throw new InvalidInputFileException("Invalid input file specified: " + inputString);
		}
		else{
			fileAndPoolIDMap.put("poolID", inputVals[0]);
			fileAndPoolIDMap.put("file", inputVals[1]);
		}
		return fileAndPoolIDMap;
	}
	
	/**
	 * Add a VariantContext object to the pool
	 * @param v
	 */
	public void addVariant(VariantContext v){
		String chrPos = new String(v.getChr() + ":" + Integer.toString(v.getStart()));
		hMap.put(chrPos, v);
		tMap.put(chrPos, v);
	}

	/**
	 * Read a vcf and add VariantContext objects to the pool. Variants will be validated
	 * before they're added.
	 * 
	 * @param filename
	 * @param requireIndex
	 * @return
	 */
	private void parseVCF(String filename, boolean requireIndex){
		logger.info("Parsing file...");

		/** latest VCF specification */
		final VCFCodec vcfCodec = new VCFCodec();

		/* get A VCF Reader */
		FeatureReader<VariantContext> reader = AbstractFeatureReader.getFeatureReader(
				filename, vcfCodec, requireIndex);

		/* read the header */
		this.header = (VCFHeader)reader.getHeader();

		/** loop over each Variation */
		Iterator<VariantContext> it = null;
		try {
			it = reader.iterator();

			int count = 0;
			while ( it.hasNext() ) {
				
				if(count % 10000 == 0) logger.info("Parsed " + count + " variants so far...");

				/* get next variation and save it */
				VariantContext vc = it.next();
				
				/* Fully decode the variant. This should verify the variant is valid */
				// TODO: Make sure this really validates the variant
				// TODO: Determine if this is too much upfront overhead. Probably very costly.
					// Decoding was very costly on /Users/markebbert/BYU/total.hg19.SNPs.capture.vcf! 4GB was not enough.
				// TODO: Figure out what the 'lenientDecoding' actually does. Should we be 'lenient'?
//				vc = vc.fullyDecode(header, false);

				this.addVariant(vc);
				count++;
			}

			/* we're done */
			reader.close();

		} catch (IOException e) {
			// TODO: add logger, print error, and throw exception
		}
	}
	
	
	/**
	 * Print a VariantPool to file in the format specified by SupportedFileType. If fileType is
	 * VCF, we must have a SAMSequenceDictionary
	 * @param filePath
	 * @param vp
	 * @param refDict
	 * @param fileType
	 */
	public static void printVariantPool(String filePath, VariantPool vp, SAMSequenceDictionary refDict, SupportedFileType fileType){
		
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
	
	/**
	 * This method will generate a poolID for files that the user does not define one
	 * 
	 * @param fileType
	 * @return
	 */
	public static String generatePoolID(SupportedFileType fileType){
		String id = fileType.getPoolIDPrefix() + Integer.toString(generatedPoolIDs.size() + 1); 
		generatedPoolIDs.add(id);
		return id;
	}
	
	private static void addPoolID(String poolID){
		usedPoolIDs.add(poolID);
	}
	
	
	
}
