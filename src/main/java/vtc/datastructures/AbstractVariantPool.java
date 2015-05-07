/**
 * 
 */
package vtc.datastructures;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.broadinstitute.variant.vcf.VCFHeader;

/**
 * @author markebbert
 *
 */
public abstract class AbstractVariantPool implements VariantPool {
	private static ArrayList<String> usedPoolIDs = new ArrayList<String>();
	private static ArrayList<String> generatedPoolIDs = new ArrayList<String>();

	private String poolID;
	private File file;
	private boolean addChr;
	private boolean requireIndex;
	private boolean earlyVarAccess;
	private SamplePool samples;
	private VCFHeader header;
	
	private final VCFCodec vcfCodec = new VCFCodec();
	private FeatureReader<VariantContext> reader;
	private Iterator<VariantContext> varIter = null;
	private VariantContext currVar = null;
	private int currVarCount;
	
	private static Logger logger = Logger.getLogger(AbstractVariantPool.class);
	
	/****************************************************
	 * Constructors
	 */
	
	/**
	 * Create an empty VariantPool. Use this for building a
	 * VariantPool from scratch rather than reading from a file.
	 */
	public AbstractVariantPool(boolean addChr, String poolID){
		init(addChr, poolID);
	}

//	public AbstractVariantPool(AbstractVariantPool vp) throws IOException{
//		this(vp.getFile(), vp.getPoolID(), vp.addChr());
//	}
//	
//	public AbstractVariantPool(String filePath, String poolID, boolean addChr) throws IOException{
//		this(new File(filePath), poolID, addChr);
//	}
//	
//	public AbstractVariantPool(File file, String poolID, boolean addChr) throws IOException{
//		this(file, poolID, false, addChr);
//	}

	public AbstractVariantPool(File file, String poolID, boolean requireIndex, boolean addChr) throws IOException{
		logger.info("Creating new VariantPool from " + file.getName() + " with poolID " + poolID);
		this.init(file, poolID, requireIndex, addChr);
				
	}
	
	public AbstractVariantPool(String inputString, boolean requireIndex, boolean addChr) throws InvalidInputFileException, IOException{
		HashMap<String, String> fileAndPoolIDMap = this.parseInputString(inputString);
		this.init(new File(fileAndPoolIDMap.get("file")), fileAndPoolIDMap.get("poolID"), requireIndex, addChr);
	}

	protected void init(File file, String poolID, boolean requireIndex, boolean addChr) throws IOException{
		this.init(addChr, poolID);

		this.setFile(file);
	}
	
	protected void init(boolean addChr, String poolID){
		this.setPoolID(poolID);
		AbstractVariantPool.addPoolID(poolID);
		this.addChr(addChr);
		this.earlyVarAccess = false;
	}
	
	
	

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
			fileAndPoolIDMap.put("poolID", VariantPoolHeavy.generatePoolID(SupportedFileType.VCF));
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
	 * Open the VCF for reading
	 * 
	 * @param fileName
	 * @param requireIndex
	 * @throws IOException 
	 */
	private void openVCFForReading() throws IOException{
		if(this.reader == null){
			logger.info("Opening " + this.getFile().getPath() + " for reading...");
			reader = AbstractFeatureReader.getFeatureReader(this.getFile().getPath(), vcfCodec, this.requireIndex);
			this.setHeader((VCFHeader)reader.getHeader());
			varIter = reader.iterator();
			currVarCount = 0;
		}
	}

	
	
	
	/****************************************************
	 *  Getters
	 */
	
	
	public VCFHeader getHeader(){
		return this.header;
	}

	public String getPoolID(){
		return this.poolID;
	}
	
	public File getFile(){
		return this.file;
	}

	/**
	 * Return the list of all PoolIDs
	 * @return
	 */
	public static ArrayList<String> getAllPoolIDs(){
		return AbstractVariantPool.usedPoolIDs;
	}
	
	public TreeSet<String> getSamples(){
		
		/* If someone needs the samples before getting any variants, 
		 * get the first so samples are set. Then return the samples.
		 */
		if(this.samples == null){
			try {
				currVar = getNextVar();
				earlyVarAccess = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.samples.getSamples();
	}
	
	public boolean addChr(){
		return this.addChr;
	}
	
	public boolean requireIndex(){
		return this.requireIndex;
	}
	
	public VariantContext getNextVar() throws IOException{
		if(this.reader == null){
			openVCFForReading();
		}
		/* If earlyVarAccess == true, someone must have needed access to the
		 * VP's samples before reading any variants. Just return that var on
		 * the first time through here after that initial 'get.'
		 */
		if(earlyVarAccess && currVarCount == 1){
			earlyVarAccess = false;
			return currVar;
		}
		else if(varIter.hasNext()){
			currVar = varIter.next();
			
			if(currVarCount == 0){
				logger.info("Setting samples");
				SamplePool sp = new SamplePool();
				sp.addSamples(new TreeSet<String>(currVar.getSampleNames()));
				sp.setPoolID(this.getPoolID());
				this.setSamples(sp);
			}
			currVarCount += 1;
			return currVar;
		}
		this.reader.close();
		return null;
	}
	
	
	
	
	
	/****************************************************
	 *  Setters
	 */	
	
	public void setFile(File file){
		this.file = file;
	}
	
	public void setPoolID(String poolID){
		this.poolID = poolID;
	}
	
	public void setHeader(VCFHeader header){
		this.header = header;
	}
	
	protected void setSamples(SamplePool samples){
		this.samples = samples;
	}
	
	public void addSamples(SamplePool samples){
		initSamples();
		this.samples.addSamples(samples.getSamples());
	}
	
	public void addSamples(TreeSet<String> samples){
		initSamples();
		this.samples.addSamples(samples);
	}
	
	private void initSamples(){
		if(this.samples == null){
			this.samples = new SamplePool();
			this.samples.setPoolID(this.poolID); // VariantPools and SamplePools must have same ID
		}		
	}
	
	protected void addChr(boolean addChr){
		this.addChr = addChr;
	}
	
	protected void requireIndex(boolean requireIndex){
		this.requireIndex = requireIndex;
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
	
	/**
	 * Add poolID to the list of all poolIDs.
	 * @param poolID
	 */
	protected static void addPoolID(String poolID){
		usedPoolIDs.add(poolID);
	}
}

