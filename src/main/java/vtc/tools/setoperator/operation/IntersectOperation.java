/**
 * 
 */
package vtc.tools.setoperator.operation;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import vtc.datastructures.SamplePool;
import vtc.datastructures.VariantPoolHeavy;
import vtc.tools.setoperator.Operator;

/**
 * @author markebbert
 *
 */
/**
 * @author markebbert
 *
 */
public class IntersectOperation extends Operation {

	private HashMap<String, SamplePool> samplePools;



	/****************************************************
	 * Constructors
	 */
	
	/**
	 * @param operationString
	 * @throws InvalidOperationException
	 */
	public IntersectOperation(String operationString, TreeMap<String, VariantPoolHeavy> variantPools) throws InvalidOperationException{
		super(operationString, variantPools);
		samplePools = new HashMap<String, SamplePool>();
	}
	
	public IntersectOperation(String operationString, String operID, Operator op, HashMap<String, SamplePool> samplePools){
		super(operationString, operID, op);
		this.samplePools = samplePools;
	}
	
	
	
	
	
	/****************************************************
	 * Getters
	 */
	
//	public String getOperationID(){
//		return this.operID;
//	}
	
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
	
//	public Operator getOperator(){
//		return this.operator;
//	}
	
	
	
	
	
	
	/****************************************************
	 * Setters
	 */
	
	public void addSamplePool(SamplePool sp){
		this.samplePools.put(sp.getPoolID(), sp);
	}
	
	


	
	
	/****************************************************
	 * Useful operations
	 */

	/**
	 * This method will parse and validate an operation string
	 * @param operation
	 * @throws InvalidOperationException
	 */
//	private void parseOperation(String operation, TreeMap<String, VariantPool> variantPools) throws InvalidOperationException{
//
//		/* TODO: simplify the set operations to specify operators as '+', '-', and 'n'. Place
//		 * operator between sample pools and drop outer brackets (e.g. out1=f1[s1,s3]+f2[s2,s5]+f3[s6,s7] ).
//		 * I believe it's simplest to only allow two sample pools to be used in subtractions.
//		 */
//		// Split on the '=' in an operation (e.g. out1=i[f1[s1,s3]:f2[s2,s5]] )
//		String[] operVals = operation.split("=");
//		int operIndex;
//
//		// If only one val after split, user didn't provide operID, so generate it
//		if(operVals.length == 1){
//			this.operID = IntersectOperation.generateOperationID();
//			addOperationID(this.operID);
//			operIndex = 0;
//		}
//		else if(operVals.length > 2){
//			throw new InvalidOperationException("Invalid operation, see help for more info: " + operation);
//		}
//		else{
//			this.operID = operVals[0];
//			addOperationID(this.operID);
//			operIndex = 1;
//		}
//		
//		// Match on the expected pattern for an operation (i.e. everything after the '=' )
//		Pattern p = Pattern.compile("^(\\w)\\[(.+)\\]$");
//		Matcher m = p.matcher(operVals[operIndex]);
//		
//		// Something is wrong if there aren't two groups
//		if(!m.find() || m.groupCount() != 2){
//			throw new InvalidOperationException("Invalid operation, see help for more info: " + operation);
//		}
//		else{
//
//			// Get the operator
//			String op = m.group(1);
//			this.operator = Operator.getOperator(op);
//			if(this.operator == null){
//				throw new InvalidOperationException("Invalid operator specified: " + op);
//			}
//			
//			// Get the sample pools and split on ':' to get individual pools
//			String[] samplePools = m.group(2).split(":");
//			SamplePool sp;
//			
//			// Create the pools and store
//			for(String s : samplePools){
//				sp = new SamplePool(s, variantPools);
//				this.samplePools.put(sp.getPoolID(), sp);
//				this.samplePoolIDsInOrder.add(sp.getPoolID());
//			}
//		}
//	}
//	
//	/**
//	 * Generate an operation ID if the user didn't provide one. IDs are generated
//	 * as 's1', 's2', etc.
//	 * @return
//	 */
//	private static String generateOperationID(){
//		String id = "s" + Integer.toString(generatedOperationIDs.size() + 1);
//		generatedOperationIDs.add(id);
//		return id;
//	}
//	
//	private static void addOperationID(String operID){
//		usedOperationIDs.add(operID);
//	}
//	
//	public String toString(){
//		return this.operationString;
//	}
}
