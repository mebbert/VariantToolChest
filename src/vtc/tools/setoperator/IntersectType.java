/**
 * 
 */
package vtc.tools.setoperator;

/**
 * @author markebbert
 *
 */
public enum IntersectType {

	HOMOZYGOUS_REF("HOMOZYGOUS_REF", "homo_ref", "All samples must be homo-ref", ""),
	HOMOZYGOUS_ALT("HOMOZYGOUS_ALT", "homo_alt", "All samples must be homo-alt", ""),
	HETEROZYGOUS("HETEROZYGOUS", "het", "All samples must be het", ""),
	HET_OR_HOMO_ALT("HET_OR_HOMO_ALT", "het_homo_alt", "All samples must be het or homo-alt", ""),
	NONE("NONE", "none", "Ignore samples when intersecting. Only consider chr, pos, ref, alt", "");
	
	private String name, command, briefDescription, fullDescription;
	private IntersectType(String name, String command, String briefDescription, String fullDescription){
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
