/**
 * 
 */
package vtc.tools.setoperator;

/**
 * @author markebbert
 *
 */
public enum Operator {

	INTERSECT("iI", "Performs intersection"),
	COMPLEMENT("cC", "Performs complement (i.e. substraction)"),
	UNION("uU", "Performs union");

	private String allowedSymbols;
	private String description;
	private Operator(String allowedSymbols, String description){
		this.allowedSymbols = allowedSymbols;
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public static Operator getOperator(String operString){

		if(Operator.INTERSECT.allowedSymbols.contains(operString)){
			return Operator.INTERSECT;
		}
		else if(Operator.COMPLEMENT.allowedSymbols.contains(operString)){
			return Operator.COMPLEMENT;
		}
		else if(Operator.UNION.allowedSymbols.contains(operString)){
			return Operator.UNION;
		}
		return null;
	}
}
