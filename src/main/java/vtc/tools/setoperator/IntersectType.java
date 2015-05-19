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

	/* TODO: The purpose of Match sample is to only consider samples that are found in all VariantPools and only intersect
	 * if the genotype matches exactly, regardless of the genotype. e.g., if the sample is homozygous ref in both VariantPools,
	 * they intersect, but not if one is homo-ref and the other is het. We should also print a brief stats summary with concordance.
	 * Basically, the purpose is to compare the same samples across multiple sequencing runs.
	 */
	
	MATCH_SAMPLE("MATCH_SAMPLE", "match_sample", "All samples with the same name must have identical genotypes", ""),
//	PERFECT("PERFECT", "perfect", "Require all samples to have identical genotypes. Will also generate" +
//			" a file of 'imperfect' matches and 'complete mismatches'", ""),
	POS("POS", "pos", "Ignore samples when intersecting. Only consider chr, pos, ref", ""),
	ALT("ALT", "alt", "Ignore samples when intersecting. Only consider chr, pos, ref, alt", "");
	
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

	@Override
	public String toString(){
		return "'" + getCommand() + "' -- " +
				getBriefDescription() + ".";
	}
}
