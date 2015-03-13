/**
 * 
 */
package vtc.datastructures;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vtc.tools.setoperator.operation.InvalidOperationException;

/**
 * @author markebbert
 *
 */
public class SamplePool implements Pool{

	private static TreeSet<String> usedPoolIDs = new TreeSet<String>();
	
	/* This poolID must match the poolID of its associated VariantPool object
	 * since all samples come from the variants anyway.
	 */
	private String poolID;
	private TreeSet<String> samples;
	private static TreeSet<String> definedVariantPoolIDs; // Keep track of all variantPools defined on the command line

	// Match on the expected pattern for a sample pool (e.g. f1[s1,s3] )
	private static Pattern samplePoolPattern = Pattern.compile("^(\\w+)(\\[(.+)\\])*$");

	
	/****************************************************
	 * Constructors
	 */
	
	public SamplePool(String pool, TreeMap<String, VariantPool> variantPools) throws InvalidOperationException{
		this();
		parseSamplePool(pool, variantPools);
	}
	
	public SamplePool(){
		this.samples = new TreeSet<String>();
		SamplePool.definedVariantPoolIDs = new TreeSet<String>();
	}
	
	
	
	/****************************************************
	 * Getters
	 */
	
	public TreeSet<String> getSamples(){
		return this.samples;
	}
	
	@Override
	public String getPoolID(){
		return this.poolID;
	}
	
	public static TreeSet<String> getAllPoolIDs(){
		return SamplePool.usedPoolIDs;
	}
	
	
	
	/****************************************************
	 * Setters
	 */

	public void setPoolID(String poolID){
		this.poolID = poolID;
	}
	
	public void addSamples(TreeSet<String> samples){
		this.samples.addAll(samples);
	}
	
	public void addSample(String sample){
		this.samples.add(sample);
	}
	
	
	/****************************************************
	 * Useful operations
	 */

	/**
	 * Parse a SamplePool string and populate this SamplePool
	 * @param pool
	 * @throws InvalidOperationException
	 */
	private void parseSamplePool(String pool, TreeMap<String, VariantPool> variantPools) throws InvalidOperationException{
		Matcher m = samplePoolPattern.matcher(pool);
		
		if(!m.find()){
			throw new InvalidOperationException("Invalid operation. Malformed sample pool. See help for more info: " + pool);
		}
		else{
			this.setPoolID(m.group(1));
			SamplePool.usedPoolIDs.add(this.getPoolID());
			
			/* Verify this poolID maps to an existing VariantPool poolID. */
			ArrayList<String> allVariantPoolIDs = VariantPool.getAllPoolIDs();
			if(!allVariantPoolIDs.contains(this.getPoolID())){
				throw new InvalidOperationException("Invalid sample pool ID. Sample pool " +
						"IDs must be defined as an input file or in a previous set operation: " + this.getPoolID());
			}

			/* Count the number of groups that were actually matched. The first group is always the entire
			 * input, so skip that by starting at i = 1. There should be either 1 or 3 groups if the defined
			 * SamplePool was valid.
			 */
			int groupCount = 0;
			for(int i = 1; i < 4; i++){
				if(m.start(i) != -1)
					groupCount++;
			}
			
			if(groupCount == 3){
				String[] samples = m.group(3).split(","); // split in comma and put sample names in map
				for (String s : samples){
					this.addSample(s);
				}
			}
			else if(groupCount == 1){
				this.addSamples(variantPools.get(this.getPoolID()).getSamples());
			}
			else{
				throw new InvalidOperationException("Could not parse input sample pool (" + pool + "). Something is very wrong!");
			}
		}
	}
	
}
