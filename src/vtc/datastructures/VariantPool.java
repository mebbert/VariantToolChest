package vtc.datastructures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.SAMSequenceDictionary;

import org.apache.log4j.Logger;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.variant.variantcontext.writer.Options;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriter;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriterFactory;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.broadinstitute.variant.vcf.VCFFormatHeaderLine;
import org.broadinstitute.variant.vcf.VCFHeader;
import org.broadinstitute.variant.vcf.VCFHeaderLine;
import org.broadinstitute.variant.vcf.VCFHeaderLineCount;
import org.broadinstitute.variant.vcf.VCFHeaderLineType;
import org.broadinstitute.variant.vcf.VCFHeaderVersion;
import org.broadinstitute.variant.vcf.VCFInfoHeaderLine;
import org.broadinstitute.variant.vcf.VCFUtils;


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
	
	private static Logger logger = Logger.getLogger(VariantPool.class);

	private HashMap<String, VariantContext> hMap;
	private TreeMap<String, VariantContext> tMap;
	private TreeSet<String> contigs;
	private SamplePool samples;
	private String poolID;
	private File file;
	private VCFHeader header;
	private Boolean hasGenotypeData;
	private boolean addChr;

	
	/****************************************************
	 * Constructors
	 */
	
	/**
	 * Create an empty VariantPool. Use this for building a
	 * VariantPool from scratch rather than reading from a file.
	 */
	public VariantPool(boolean addChr){
		init(addChr);
	}

	public VariantPool(VariantPool vp){
		this(vp.file, vp.poolID, vp.addChr());
	}
	
	public VariantPool(String filePath, String poolID, boolean addChr){
		this(new File(filePath), poolID, addChr);
	}
	
	public VariantPool(File file, String poolID, boolean addChr){
		this(file, poolID, false, addChr);
	}

	public VariantPool(File file, String poolID, boolean requireIndex, boolean addChr){
		logger.info("Creating new VariantPool from " + file.getName() + " with poolID " + poolID);
		init(file, poolID, requireIndex, addChr);
				
	}
	
	public VariantPool(String inputString, boolean requireIndex, boolean addChr) throws InvalidInputFileException{
		HashMap<String, String> fileAndPoolIDMap = this.parseInputString(inputString);
		init(new File(fileAndPoolIDMap.get("file")), fileAndPoolIDMap.get("poolID"), requireIndex, addChr);
	}

	private void init(File file, String poolID, boolean requireIndex, boolean addChr){
		init(addChr);

		this.setFile(file);
		this.setPoolID(poolID);
		addPoolID(this.poolID);
		this.parseVCF(file.getPath(), requireIndex);
	}
	
	private void init(boolean addChr){
		this.tMap = new TreeMap<String, VariantContext>(new NaturalOrderComparator());
		this.hMap = new HashMap<String, VariantContext>();
		this.contigs = new TreeSet<String>();
		this.addChr = addChr;
	}
	
	
	
	
	/****************************************************
	 *  Getters
	 */
	
	public boolean addChr(){
		return this.addChr;
	}

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
	public VariantContext getVariant(String chr, int pos, String ref){
		return getVariant(chr + ":" + Integer.toString(pos) + ":" + ref);
	}

	/**
	 * Get variant by key ('chr:pos:ref')
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
	
	public TreeSet<String> getContigs(){
		return this.contigs;
	}
	
	/**
	 * Return an Iterator<String> object to an ordered key set. The
	 * keys are formatted as 'chr:pos' and ordered 'naturally'.
	 * @return
	 */
	public Iterator<String> getIterator(){
		return this.tMap.keySet().iterator();
	}	
	
	public TreeSet<String> getSamples(){
		return this.samples.getSamples();
	}
	
	public boolean hasGenotypeData(){

		if(this.hasGenotypeData == null){
			Iterator<String> it = this.getIterator();

			if(!it.hasNext()){
				/* The set is empty. Return false. */
				return false;
			}

			VariantContext vc = this.getVariant(it.next());
			if(vc.hasGenotypes()){
				this.hasGenotypeData = true;
			}
			else{
				this.hasGenotypeData = false;
			}
		}
		return this.hasGenotypeData;
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
	
	public void setHeader(VCFHeader header){
		this.header = header;
	}
	
	public void setSamples(SamplePool samples){
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
	
	
	
	
	/****************************************************
	 * Useful operations
	 */
	
	/**
	 * Add a contig value. This will keep track of all contigs
	 * in this VariantPool.
	 * @param contig
	 */
	private void addContig(String contig){
		this.contigs.add(contig);
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
	 * @param union
	 */
	public void addVariant(VariantContext v, boolean union){
		String currChr = v.getChr();
		String newChr = generateChrString(currChr);
		
		if(!currChr.equals(newChr)){
			currChr = newChr;
			v = buildNewVariantWithChr(newChr, v);
		}

		this.addContig(currChr);
		String chrPosRef = currChr + ":" + Integer.toString(v.getStart()) + ":" + v.getReference();
		
		/* If a variant already exists with this chr:pos:ref,
		 * ignore subsequent variants and emit warning. 
		 */
		if(hMap.containsKey(chrPosRef) && !union ){
			/* TODO: Determine how to handle VCFs with multiple records
			 * at the same location. Sometimes people represent multiple
			 * alts on different lines. e.g.:
			 * 
			 * chr2 111 C A
			 * chr2 111 C T
			 * 
			 * Or when there's an INDEL. e.g.:
			 * 
			 * chr2 110 CC C (Can be represented as chr2 111 C .)
			 * chr2 111 C  A
			 */
//			VariantContext existingVar = hMap.get(chrPosRef);
//			VariantContext newVar = combineVariants(v, existingVar);
//			
//			/* Add the new variant */
//			hMap.put(chrPosRef, newVar);
//			tMap.put(chrPosRef, newVar);
			
			try{
				throw new VariantPoolException("Found separate variant records with the same Chr, pos, and ref. Ignoring " +
					"subsequent variants at: " + v.getChr() + ":" + v.getStart());
			} catch (VariantPoolException e){
				logger.error(e.getMessage());
			}
		}
		else{
			hMap.put(chrPosRef, v);
			tMap.put(chrPosRef, v);
		}
	}
	
	/**
	 * Add or remove 'chr' to chromosome if user requests
	 * @param chr
	 * @return
	 */
	private String generateChrString(String chr){
		if(this.addChr()){
			if(!chr.toLowerCase().startsWith("chr")){
				return "chr" + chr;
			}
		}
		else if(chr.toLowerCase().startsWith("chr")){
			return chr.substring(3);
		}
		return chr;
	}
	
	/**
	 * Build a new variant with the updated 'chr'
	 * 
	 * @param chr
	 * @param var
	 * @return
	 */
	private VariantContext buildNewVariantWithChr(String chr, VariantContext var){
		
		VariantContextBuilder vcBuilder = new VariantContextBuilder();
		vcBuilder.alleles(var.getAlleles());
		vcBuilder.attributes(var.getAttributes());
		vcBuilder.chr(chr);
		vcBuilder.filters(var.getFilters());
		vcBuilder.genotypes(var.getGenotypes());
		vcBuilder.id(var.getID());
		vcBuilder.log10PError(var.getLog10PError());
		vcBuilder.source(var.getSource());
		vcBuilder.start(var.getStart());
		vcBuilder.stop(var.getEnd());
		return vcBuilder.make();
	}
	
	/**
	 * Combine variants with the same chr, pos, and ref found in the same VariantPool
	 * 
	 * @param var
	 * @param alleles
	 * @param genos
	 * @return
	 */
//	private VariantContext combineVariants(VariantContext var1, VariantContext var2){
//		/* Start building the new VariantContext */
//		VariantContextBuilder vcBuilder = new VariantContextBuilder();
//		vcBuilder.chr(var1.getChr());
//		vcBuilder.start(var1.getStart());
//		vcBuilder.stop(var1.getEnd());
//		
//		TreeSet<Allele> allAlleles = new TreeSet<Allele>();
//		allAlleles.addAll(var1.getAlleles());
//		allAlleles.addAll(var2.getAlleles());
//		vcBuilder.alleles(allAlleles);
//		
//		ArrayList<Genotype> genos = new ArrayList<Genotype>();
//		genos.addAll(var1.getGenotypes());
//		genos.addAll(var2.getGenotypes());
//		vcBuilder.genotypes(genos);
//		
//		/* TODO: Figure out how to approach attributes (i.e. INFO). */
////		vcBuilder.attributes(var.getAttributes());
//		return vcBuilder.make();
//	}

	/**
	 * Read a vcf and add VariantContext objects to the pool. 
	 * 
	 * @param filename
	 * @param requireIndex
	 * @return
	 */
	private void parseVCF(String filename, boolean requireIndex){
		logger.info("Parsing " + filename + " ...");

		/** latest VCF specification */
		final VCFCodec vcfCodec = new VCFCodec();

		/* get A VCF Reader */
		FeatureReader<VariantContext> reader = AbstractFeatureReader.getFeatureReader(
				filename, vcfCodec, requireIndex);

		/* read the header */
		this.setHeader((VCFHeader)reader.getHeader());

		/** loop over each Variation */
		Iterator<VariantContext> it = null;
		try {
			it = reader.iterator();
			VariantContext vc;
			int count = 0;
			while ( it.hasNext() ) {
				
				if(count > 1 && count % 10000 == 0) System.out.print("Parsed variants: " + count + "\r");

				/* get next variation and save it */
				vc = it.next();
				
				/* Create a new SamplePool and add it to the VariantPool */
				if(count == 0){
					SamplePool sp = new SamplePool();
					sp.addSamples(new TreeSet<String>(vc.getSampleNames()));
					sp.setPoolID(this.getPoolID());
					this.setSamples(sp);
				}
				
				this.addVariant(vc, false);
				count++;
			}

			/* we're done */
			reader.close();

		} catch (IOException e) {
			// TODO: add logger, print error, and throw exception
		}
	}
	
	/**
	 * Generate a basic header for the VCF
	 * 
	 * @param refDict
	 */
	public void generateBasicHeader(SAMSequenceDictionary refDict, Set<String> sampleNames){
		LinkedHashSet<VCFHeaderLine> headerLines = new LinkedHashSet<VCFHeaderLine>();
		
		/* Add the 'fileFormat' header line (must be first) */
		headerLines.add(new VCFHeaderLine(VCFHeaderVersion.VCF4_1.getFormatString(),
				VCFHeaderVersion.VCF4_1.getVersionString()));
		
		/* Format field must have at least one value. The Genotype in this case. */
		headerLines.add(new VCFFormatHeaderLine("GT", 1, VCFHeaderLineType.String, "Genotype"));
		
		/* Create contig header lines */
		headerLines.addAll(VCFUtils.makeContigHeaderLines(refDict, null));
		
		this.setHeader(new VCFHeader(headerLines, sampleNames));
	}
	
	
	/**
	 * Print a VariantPool to file in the format specified by SupportedFileType. If fileType is
	 * VCF, we must have a SAMSequenceDictionary. If 'repairHeader' is true, create and add missing
	 * header lines.
	 * 
	 * @param filePath
	 * @param vp
	 * @param refDict
	 * @param fileType
	 * @param repairHeader
	 * @throws URISyntaxException 
	 * @throws FileNotFoundException 
	 */
	public static void printVariantPool(String file, VariantPool vp, String refDict,
			SupportedFileType fileType, boolean repairHeader) throws URISyntaxException, FileNotFoundException{
		printVariantPool(file, null, vp, refDict, fileType, repairHeader);
	}
	
	/**
	 * Print a VariantPool to file in the format specified by SupportedFileType. If fileType is
	 * VCF, we must have a SAMSequenceDictionary. If 'repairHeader' is true, create and add missing
	 * header lines.
	 * 
	 * @param fileName
	 * @param outputDirectory
	 * @param vp
	 * @param refDict
	 * @param fileType
	 * @param repairHeader
	 * @throws URISyntaxException
	 * @throws FileNotFoundException
	 */
	public static void printVariantPool(String fileName, String outputDirectory,
			VariantPool vp, String refDict, SupportedFileType fileType, boolean repairHeader) throws URISyntaxException, FileNotFoundException{
		
		File file;
		String normalizedPath;
		
		/* If a specific output directory is specified, normalize the path and prepend it to the fileName */
		if(outputDirectory != null){
			normalizedPath = new URI(outputDirectory).normalize().getPath();
			file = new File(normalizedPath + fileName);
		}
		else{
			file = new File(fileName);
		}

		if(fileType == SupportedFileType.VCF){
			printVariantPoolToVCF(file, vp, refDict, repairHeader);
		}
	}
	
	/**
	 * Print a VariantPool to a file in VCF format. Must have a SAMSequenceDictionary for a reference file. If
	 * 'repairHeader' is true, create and add missing header lines.
	 * 
	 * @param file
	 * @param vp
	 * @param refDict
	 * @param repairHeader
	 * @throws FileNotFoundException
	 */
	private static void printVariantPoolToVCF(File file, VariantPool vp, String refDict, boolean repairHeader) throws FileNotFoundException{
		
		if(refDict == null){
			throw new RuntimeException("Received a 'null' SAMSequenceDictionary. Something is very wrong!");
		}

		EnumSet<Options> es;
		if(repairHeader){
			es = EnumSet.of(Options.INDEX_ON_THE_FLY);
		}
		else{
			es = EnumSet.of(Options.INDEX_ON_THE_FLY, Options.ALLOW_MISSING_FIELDS_IN_HEADER);
		}
		SAMSequenceDictionary dict = new IndexedFastaSequenceFile( new File(refDict)).getSequenceDictionary();
		VariantContextWriter writer = VariantContextWriterFactory.create(file, dict, es);
		
		if(vp.getHeader() == null){
			vp.generateBasicHeader(dict, vp.getSamples());
		}
		
		writer.writeHeader(vp.getHeader());
		
		boolean rewrite = false;
		Iterator<String> it = vp.getIterator();
		VariantContext vc;
		while(it.hasNext()){
			vc = vp.getVariant(it.next());
			
			/* Write variant to file. 'writer' will throw an IllegalStateException
			 * if a variant has annotations that are not in the header. If this
			 * happens, determine what's missing and add a dummy header
			 * line (assuming the user chooses this option).
			 */
			try{
				writer.add(vc);
			} catch (IllegalStateException e){
				
				/* An example of the expected message is:
				 * "Key AC found in VariantContext field INFO at 20:13566260
				 * but this key isn't defined in the VCFHeader.  We require
				 * all VCFs to have complete VCF headers by default."
				 * 
				 * check if the exception's message appears to match and add
				 * missing line if it does.
				 */
				if(repairHeader && e.getMessage().contains("found in VariantContext field")){
					addMissingHeaderLineAndWriteVariant(e, vp, writer, vc);
					
					/* There should now be dummy header lines added for all
					 * missing values, but since
					 * we've already started writing to the file, we need to 
					 * stop, close and rewrite.
					 */
					rewrite = true;
					break;
				}
				
				// else throw the error because something is wrong
				throw e;
			}
		}
		
		writer.close();
		if(rewrite){
			printVariantPoolToVCF(file, vp, refDict, repairHeader);
		}
	}
	
	/**
	 * Recursively add missing header line(s) to VariantPool and attempt to write the variant
	 * to file. 
	 * 
	 * TODO: It would be WAY better to check whether the header has all necessary lines BEFORE
	 * trying to write anything. This is approach is just clunky. We should also get a list of
	 * any standardized annotations with descriptions and use those were possible rather than
	 * giving a dummy description.
	 * 
	 * @param e
	 * @param vp
	 */
	private static void addMissingHeaderLineAndWriteVariant(Exception e, VariantPool vp,
			VariantContextWriter writer, VariantContext vc){
		
		/* Extract the missing 'Key' and 'field */
		Pattern missingKeyPattern = Pattern.compile("^Key (\\w+) found in VariantContext field (\\w+)");
		Matcher m = missingKeyPattern.matcher(e.getMessage());
		m.find();
		
		/* Something's wrong if there are no matches */
		if(m.groupCount() == 0){
			throw new RuntimeException("Could not determine the missing annotation. Something is very wrong!" +
					"Original message:" + e.getMessage());
		}
		String missingKey = m.group(1);
		String lineType = m.group(2);
		String description = "This is a dummy description";
		
		String message = "Variant pool (" + vp.getPoolID() + ") missing header line with key '" +
				missingKey + "' and type '" + lineType + ". Creating and adding dummy line to header.";

		logger.warn(message);
		System.out.println("Warning: " + message);
		
		/* Create missing line based on the type */
		if("INFO".equals(lineType)){
			vp.getHeader().addMetaDataLine(new VCFInfoHeaderLine(missingKey, VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, description));
		}
		else if("FORMAT".equals(lineType)){
			vp.getHeader().addMetaDataLine(new VCFFormatHeaderLine(missingKey, 1, VCFHeaderLineType.String, description));
		}
		else{
			throw new RuntimeException("Could not determine missing header line type." +
					"Something is very wrong! Original message: " + e.getMessage());
		}
		
		/* Attempt to write to file again */
		try{
			writer.add(vc);
		} catch (IllegalStateException ex){
			if(ex.getMessage().contains("found in VariantContext field")){
				addMissingHeaderLineAndWriteVariant(ex, vp, writer, vc);
			}
		}
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
	private static void addPoolID(String poolID){
		usedPoolIDs.add(poolID);
	}
	
	
	
}
