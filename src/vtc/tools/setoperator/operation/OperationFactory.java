/**
 * 
 */
package vtc.tools.setoperator.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vtc.datastructures.SamplePool;
import vtc.datastructures.VariantPool;
import vtc.tools.setoperator.Operator;

/**
 * @author markebbert
 *
 */
public class OperationFactory {

	
	
	/**
	 * Will create the appropriate Operation object
	 * @param operation
	 * @param variantPools
	 * @return
	 * @throws InvalidOperationException
	 */
	public static Operation createOperation(String operation, TreeMap<String, VariantPool> variantPools) throws InvalidOperationException{
		return parseOperation(operation, variantPools);
	}
	
	
	
	/**
	 * This method will parse and validate an operation string and create the appropriate
	 * Operation object
	 * @param operation
	 * @throws InvalidOperationException
	 */
	private static Operation parseOperation(String operation, TreeMap<String, VariantPool> variantPools) throws InvalidOperationException{

		/* TODO: simplify the set operations to specify operators as '+', '-', and 'n'. Place
		 * operator between sample pools and drop outer brackets (e.g. out1=f1[s1,s3]+f2[s2,s5]+f3[s6,s7] ).
		 * I believe it's simplest to only allow two sample pools to be used in subtractions.
		 */
		// Split on the '=' in an operation (e.g. out1=i[f1[s1,s3]:f2[s2,s5]] )
		String[] operVals = operation.split("=");
		String operID;
		Operator operator;
		ArrayList<SamplePool> samplePoolList = new ArrayList<SamplePool>();
		int operIndex;

		// If only one val after split, user didn't provide operID, so generate it
		if(operVals.length == 1){
			operID = Operation.generateOperationID();
			operIndex = 0;
		}
		else if(operVals.length > 2){
			throw new InvalidOperationException("Invalid operation, see help for more info: " + operation);
		}
		else{
			operID = operVals[0];
			operIndex = 1;
		}
		
		// Match on the expected pattern for an operation (i.e. everything after the '=' )
		Pattern p = Pattern.compile("^(\\w)\\[(.+)\\]$");
		Matcher m = p.matcher(operVals[operIndex]);
		
		// Something is wrong if there aren't two groups
		if(!m.find() || m.groupCount() != 2){
			throw new InvalidOperationException("Invalid operation, see help for more info: " + operation);
		}
		else{

			// Get the operator
			String op = m.group(1);
			if(op == null){
				throw new InvalidOperationException("Invalid operator specified: " + op);
			}
			operator = Operator.getOperator(op);
			
			// Get the sample pools and split on ':' to get individual pools
			String[] samplePools = m.group(2).split(":");
			SamplePool sp;
			
			// Create the pools and store
			for(String s : samplePools){
				sp = new SamplePool(s, variantPools);
				samplePoolList.add(sp);
			}
		}
		return createProperOperation(operation, operID, operator, samplePoolList);
	}
	
	/**
	 * This method actually creates the Operation object
	 * @param operationString
	 * @param operID
	 * @param op
	 * @param samplePools
	 * @return
	 * @throws InvalidOperationException
	 */
	private static Operation createProperOperation(String operationString, String operID, Operator op, ArrayList<SamplePool> samplePools) throws InvalidOperationException{
		if(op == Operator.INTERSECT){
			
			HashMap<String, SamplePool> samplePoolMap = new HashMap<String, SamplePool>();
			for(SamplePool sp : samplePools){
				if(samplePoolMap.containsKey(sp.getPoolID())){
					throw new InvalidOperationException("Intersect operations cannot specify the same file ID twice.");
				}
				samplePoolMap.put(sp.getPoolID(), sp);
			}
			return new IntersectOperation(operationString, operID, op, samplePoolMap);
		}	
		else if(op == Operator.UNION){
				
			HashMap<String, SamplePool> samplePoolMap = new HashMap<String, SamplePool>();
			for(SamplePool sp : samplePools){
				if(samplePoolMap.containsKey(sp.getPoolID())){
					throw new InvalidOperationException("Union operations cannot specify the same file ID twice.");
				}
				samplePoolMap.put(sp.getPoolID(), sp);
			}
			return new UnionOperation(operationString, operID, op, samplePoolMap);		
		}
		else if(op == Operator.COMPLEMENT){
			return new ComplementOperation(operationString, operID, op, samplePools);
		}
		return null;
	}
}
