/**
 * 
 */
package vtc.tools.setoperator.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import vtc.datastructures.VariantPool;
import vtc.tools.setoperator.Operator;

/**
 * @author markebbert
 *
 */
/**
 * @author markebbert
 *
 */
public abstract class Operation {

//	private String operationPatternString =
//			"^((\\w+)=)?[iIuUcC]\\[(\\w+(\\[\\w+(,\\w+)*\\])?)(:\\w+(\\[\\w+(,\\w+)*\\])?)*\\]$";
//	private Pattern operationPattern;
	private String operationString;
	private String operID;
	private Operator operator;
	private static ArrayList<String> usedOperationIDs;
	private static ArrayList<String> generatedOperationIDs;
//	private Matcher matcher;
//	private HashMap<String, SamplePool> samplePools;



	/****************************************************
	 * Constructors
	 */
	
	/**
	 * @param operationString
	 * @throws InvalidOperationException
	 */
	public Operation(String operationString, TreeMap<String, VariantPool> variantPools) throws InvalidOperationException{
		init();
//		parseOperation(operationString, variantPools);
		this.operationString = operationString;
	}
	
	public Operation(String operationString, String operID, Operator op){
		init();
		this.operationString = operationString;
		
		if(operID == null){
			this.operID = generateOperationID();
		}
		else{
			this.operID = operID;
		}
		
		addOperationID(this.operID);

		this.operator = op;
	}
	
	private void init(){
		usedOperationIDs = new ArrayList<String>();
		generatedOperationIDs = new ArrayList<String>();
	}
	
//	private boolean isValidOperation(String operation){
//		return operationPattern.matcher(operation).matches();
//	}
	
	
	/****************************************************
	 * Getters
	 */
	
	public String getOperationID(){
		return this.operID;
	}
	
	/**
	 * Get all sample pools involved in this operation
	 * @return
	 */
//	public HashMap<String, SamplePool> getSamplePools(){
//		return this.samplePools;
//	}
//	
//	public SamplePool getSamplePool(String poolID){
//		return this.samplePools.get(poolID);
//	}
	

	/**
	 * Get all pool IDs involved in this operation. SamplePools and
	 * associated VariantPools must have matching pool IDs, so these
	 * pool IDs are useful for identifying all SamplePools and VariantPools
	 * involved in this operation.
	 * @return
	 */
	public abstract Collection<String> getAllPoolIDs();
	
	/**
	 * Get all pool IDs involved in this operation in the order provided. 
	 * This is important for complements to know which to subtract. SamplePools and
	 * associated VariantPools must have matching pool IDs, so these
	 * pool IDs are useful for identifying all SamplePools and VariantPools
	 * involved in this operation.
	 * @return
	 */
//	public ArrayList<String> getAllPoolIDsInOrder(){
//		return this.samplePoolIDsInOrder;
//	}
	
	public Operator getOperator(){
		return this.operator;
	}

	
	
	
	
	
	/****************************************************
	 * Setters
	 */
	
//	public abstract void addSamplePool(SamplePool sp);
	
//	private void setOperator(Operator op){
//		this.operator = op;
//	}
//	
//	private void setOperID(String operID){
//		this.operID = operID;
//		addOperationID(operID);
//	}
	
	
	
	
	
	
	/****************************************************
	 * Useful operations
	 */


	
	/**
	 * Generate an operation ID if the user didn't provide one. IDs are generated
	 * as 's1', 's2', etc.
	 * @return
	 */
	private static String generateOperationID(){
		String id = "s" + Integer.toString(generatedOperationIDs.size() + 1);
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
