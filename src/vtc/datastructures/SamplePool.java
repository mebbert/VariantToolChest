/**
 * 
 */
package vtc.datastructures;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vtc.tools.setoperator.InvalidOperationException;

/**
 * @author markebbert
 *
 */
public class SamplePool implements Pool{

	private static ArrayList<String> usedPoolIDs;
	
	/* This poolID must match the poolID of its associated VariantPool object
	 * since all samples come from the variants anyway.
	 */
	private String poolID;
	private TreeSet<String> samples;
	private Pattern samplePoolPattern;
	
	/****************************************************
	 * Constructors
	 */
	
	public SamplePool(String pool) throws InvalidOperationException{
		init();
		parseSamplePool(pool);
	}
	
	private void init(){
		// Match on the expected pattern for a sample pool (e.g. f1[s1,s3] )
		this.samplePoolPattern = Pattern.compile("^(\\w)\\[(.+)\\]$");
		SamplePool.usedPoolIDs = new ArrayList<String>();
		this.samples = new TreeSet<String>();
	}
	
	
	/****************************************************
	 * Getters
	 */
	
	public TreeSet<String> getSamples(){
		return this.samples;
	}
	
	public String getPoolID(){
		return this.poolID;
	}
	
	public static ArrayList<String> getAllPoolIDs(){
		return SamplePool.usedPoolIDs;
	}
	
	
	/****************************************************
	 * Useful operations
	 */
	private void parseSamplePool(String pool) throws InvalidOperationException{
		Matcher m = samplePoolPattern.matcher(pool);
		
		if(m.groupCount() != 2){
			throw new InvalidOperationException("Invalid operation. Malformed sample pool. See help for more info: " + pool);
		}
		else{
			this.poolID = m.group(1);
			SamplePool.usedPoolIDs.add(this.poolID);
			
			ArrayList<String> allVariantPoolIDs = VariantPool.getAllPoolIDs();
			if(!allVariantPoolIDs.contains(this.poolID)){
				throw new InvalidOperationException("Invalid sample pool ID. Sample pool " +
						"IDs must be defined in an input file or in a previous set operation: " + this.poolID);
			}

			String[] samples = m.group(2).split(","); // split in comma and put sample names in map
			for (String s : samples){
				this.samples.add(s);
			}
		}
	}
	
}
