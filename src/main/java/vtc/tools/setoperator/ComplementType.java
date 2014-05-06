/**
 * 
 */
package vtc.tools.setoperator;

/**
 * @author markebbert
 *
 */
public enum ComplementType {

	HET_OR_HOMO_ALT("HET_OR_HOMO_ALT", "het_homo_alt", "All samples must be het or homo-alt for subtraction", ""),
	EXACT("EXACT", "exact", "Require genotypes to be identical across samples for subtraction", ""),
	ALT("ALT", "alt", "Ignore samples when subtracting. Only consider chr, pos, ref, alt", "");
	
	private String name, command, briefDescription, fullDescription;
	private ComplementType(String name, String command, String briefDescription, String fullDescription){
		this.name = name;
		this.command = command;
		this.briefDescription = briefDescription;
		this.fullDescription = fullDescription;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getCommand(){
		return this.command;
	}
	
	public String getBriefDescription(){
		return this.briefDescription;
	}
	
	public String getFullDescription(){
		return this.fullDescription;
	}

	public String toString(){
		return "'" + getCommand() + "' -- " +
				getBriefDescription() + ".";
	}
}
