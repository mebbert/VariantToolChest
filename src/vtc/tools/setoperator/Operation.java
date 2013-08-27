/**
 * 
 */
package vtc.tools.setoperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vtc.datastructures.SamplePool;

/**
 * @author markebbert
 *
 */
/**
 * @author markebbert
 *
 */
public class Operation {

//	private String operationPatternString =
//			"^((\\w+)=)?[iIuUcC]\\[(\\w+(\\[\\w+(,\\w+)*\\])?)(:\\w+(\\[\\w+(,\\w+)*\\])?)*\\]$";
//	private Pattern operationPattern;
	private String operationString;
	private String operID;
	private Operator operator;
	private static ArrayList<String> usedOperationIDs;
	private static ArrayList<String> generatedOperationIDs;
//	private Matcher matcher;
	private HashMap<String, SamplePool> samplePools;



	/****************************************************
	 * Constructors
	 */
	
	/**
	 * @param operationString
	 * @throws InvalidOperationException
	 */
	public Operation(String operationString) throws InvalidOperationException{
		parseOperation(operationString);
		this.operationString = operationString;
		init();
	}
	
	private void init(){
//		operationPattern = Pattern.compile(operationPatternString);
		usedOperationIDs = new ArrayList<String>();
		generatedOperationIDs = new ArrayList<String>();
	}
	
//	private boolean isValidOperation(String operation){
//		return operationPattern.matcher(operation).matches();
//	}
	
	
	/****************************************************
	 * Getters
	 */
	
	/**
	 * Get all sample pools involved in this operation
	 * @return
	 */
	public HashMap<String, SamplePool> getSamplePools(){
		return this.samplePools;
	}
	
	public SamplePool getSamplePool(String poolID){
		return this.samplePools.get(poolID);
	}
	

	/**
	 * Get all pool IDs involved in this operation. SamplePools and
	 * associated VariantPools must have matching pool IDs, so these
	 * pool IDs are useful for identifying all SamplePools and VariantPools
	 * involved in this operation.
	 * @return
	 */
	public Set<String> getAllPoolIDs(){
		return this.samplePools.keySet();
	}
	
	public Operator getOperator(){
		return this.operator;
	}


	
	
	/****************************************************
	 * Useful operations
	 */

	/**
	 * This method will parse and validate an operation string
	 * @param operation
	 * @throws InvalidOperationException
	 */
	private void parseOperation(String operation) throws InvalidOperationException{

		/* TODO: simplify the set operations to specify operators as '+', '-', and 'n'. Place
		 * operator between sample pools and drop outer brackets (e.g. out1=f1[s1,s3]+f2[s2,s5]+f3[s6,s7] ).
		 * I believe it's simplest to only allow two sample pools to be used in subtractions.
		 */
		// Split on the '=' in an operation (e.g. out1=i[f1[s1,s3]:f2[s2,s5]] )
		String[] operVals = operation.split("=");
		int operIndex;

		// If only one val after split, user didn't provide operID, so generate it
		if(operVals.length == 1){
			this.operID = Operation.generateOperationID();
			addOperationID(this.operID);
			operIndex = 0;
		}
		else if(operVals.length > 2){
			throw new InvalidOperationException("Invalid operation, see help for more info: " + operation);
		}
		else{
			this.operID = operVals[0];
			addOperationID(this.operID);
			operIndex = 1;
		}
		
		// Match on the expected pattern for an operation (i.e. everything after the '=' )
		Pattern p = Pattern.compile("^(\\w)\\[(.+)\\]$");
		Matcher m = p.matcher(operVals[operIndex]);
		
		// Something is wrong if there aren't two groups
		if(m.groupCount() != 2){
			throw new InvalidOperationException("Invalid operation, see help for more info: " + operation);
		}
		else{

			// Get the operator
			String op = m.group(1);
			this.operator = Operator.getOperator(op);
			if(this.operator == null){
				throw new InvalidOperationException("Invalid operator specified: " + op);
			}
			
			// Get the sample pools and split on ':' to get individual pools
			String[] samplePools = m.group(2).split(":");
			SamplePool sp;
			
			// Create the pools and store
			for(String s : samplePools){
				sp = new SamplePool(s);
				this.samplePools.put(sp.getPoolID(), sp);
			}
		}
		
		
	}
	
	/**
	 * Generate an operation ID if the user didn't provide one. IDs are generated
	 * as 's1', 's2', etc.
	 * @return
	 */
	private static String generateOperationID(){
		String id = "s" + generatedOperationIDs.size() + 1;
		generatedOperationIDs.add(id);
		return id;
	}
	
	private static void addOperationID(String operID){
		usedOperationIDs.add(operID);
	}
	
	public String toString(){
		return this.operationString;
	}
}
