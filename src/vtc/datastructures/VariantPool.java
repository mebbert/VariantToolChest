package vtc.datastructures;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.variantcontext.VariantContext;
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

	public VariantPool(){
		return;
	}
	
	public VariantPool(String inputString) throws InvalidInputFileException{
		this.parseInputString(inputString);
	}

	public VariantPool(VariantPool vp){
		this.tMap = new TreeMap<String, VariantContext>(vp.tMap);
		this.hMap = new HashMap<String, VariantContext>(vp.hMap);
		this.poolID = new String(vp.poolID);
		this.file = new File(vp.file.getPath());

		addPoolID(vp.getPoolID());
	}
	
	public VariantPool(String filePath, String poolID){
		this(new File(filePath), poolID);
	}
	
	public VariantPool(File file, String poolID){
		this(file, false, poolID);
	}

	public VariantPool(File file, boolean requireIndex, String poolID){
		logger.info("Creating new VariantPool from " + file.getName() + " with poolID " + poolID);
		this.tMap = new TreeMap<String, VariantContext>();
		this.hMap = new HashMap<String, VariantContext>();
		this.file = file;
		this.poolID = poolID;
		this.parseVCF(file.getPath(), requireIndex);
				
		addPoolID(this.poolID);
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
	
	
	
	
	/****************************************************
	 * Useful operations
	 */
	
	/**
	 * This method will parse an input string from the command line. e.g.
	 * f1=/path/to/file.vcf or the same without the 'f1='
	 * @param inputString
	 * @throws InvalidInputFileException 
	 */
	private void parseInputString(String inputString) throws InvalidInputFileException{
		/* Loop over all input files, extract any user-specified pool IDs and file names,
		 * and create associated VariantPool objects
		 */
		String[] inputVals = inputString.split("=");
		
		if(inputVals.length == 1){
			this.file = new File(inputVals[0]);
			this.poolID = VariantPool.generatePoolID(SupportedFileType.VCF);
		}
		else if(inputVals.length > 2 || inputVals.length < 1){
			throw new InvalidInputFileException("Invalid input file specified: " + inputString);
		}
		else{
			this.poolID = inputVals[0];
			this.file = new File(inputVals[1]);
		}
	}
	
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
	 * This method will generate a poolID for files that the user does not define one
	 * 
	 * @param fileType
	 * @return
	 */
	public static String generatePoolID(SupportedFileType fileType){
		String id = fileType.getPoolIDPrefix() + generatedPoolIDs.size() + 1; 
		generatedPoolIDs.add(id);
		return id;
	}
	
	private static void addPoolID(String poolID){
		usedPoolIDs.add(poolID);
	}
	
	
	
}
