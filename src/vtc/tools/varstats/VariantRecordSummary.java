/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.List;

import org.broadinstitute.variant.variantcontext.Allele;

/**
 * @author markebbert
 *
 */
public class VariantRecordSummary {

	String chr;
	int position;
	Allele ref;
	ArrayList<Allele> alts;
	int snvCount, mnvCount, indelCount, insCount,
		delCount, structIndelCount, structInsCount,
		structDelCount, tiCount, tvCount, refGenotypeCount;
	String quality;
	ArrayList<Integer> altGenotypeCounts;
	Depth depth;
	

	/**
	 * @param chr
	 * @param position
	 * @param ref
	 * @param alts
	 */
	public VariantRecordSummary(String chr, int position, Allele ref,
			ArrayList<Allele> alts) {
		this.chr = chr;
		this.position = position;
		this.ref = ref;
		this.alts = alts;
	}

	/**
	 * @param snpCount
	 * @param mnpCount
	 * @param indelCount
	 * @param insCount
	 * @param delCount
	 * @param structIndelCount
	 * @param structInsCount
	 * @param structDelCount
	 * @param tiCount
	 * @param tvCount
	 */
	public VariantRecordSummary(String chr, int position, Allele ref, ArrayList<Allele> alts,
			int snpCount, int mnpCount, int indelCount,
			int insCount, int delCount, int structIndelCount,
			int structInsCount, int structDelCount, int tiCount, int tvCount) {
		this.chr = chr;
		this.position = position;
		this.ref = ref;
		this.alts = alts;
		this.snvCount = snpCount;
		this.mnvCount = mnpCount;
		this.indelCount = indelCount;
		this.insCount = insCount;
		this.delCount = delCount;
		this.structIndelCount = structIndelCount;
		this.structInsCount = structInsCount;
		this.structDelCount = structDelCount;
		this.tiCount = tiCount;
		this.tvCount = tvCount;
	}

	/**
	 * @return the snpCount
	 */
	public int getSnvCount() {
		return snvCount;
	}

	/**
	 * @param snpCount the snpCount to set
	 */
	public void setSnvCount(int snpCount) {
		this.snvCount = snpCount;
	}

	/**
	 * @return the mnpCount
	 */
	public int getMnvCount() {
		return mnvCount;
	}

	/**
	 * @param mnpCount the mnpCount to set
	 */
	public void setMnvCount(int mnpCount) {
		this.mnvCount = mnpCount;
	}

	/**
	 * @return the indelCount
	 */
	public int getIndelCount() {
		return indelCount;
	}

	/**
	 * @param indelCount the indelCount to set
	 */
	public void setIndelCount(int indelCount) {
		this.indelCount = indelCount;
	}

	/**
	 * @return the insCount
	 */
	public int getInsCount() {
		return insCount;
	}

	/**
	 * @param insCount the insCount to set
	 */
	public void setInsCount(int insCount) {
		this.insCount = insCount;
	}

	/**
	 * @return the delCount
	 */
	public int getDelCount() {
		return delCount;
	}

	/**
	 * @param delCount the delCount to set
	 */
	public void setDelCount(int delCount) {
		this.delCount = delCount;
	}

	/**
	 * @return the structIndelCount
	 */
	public int getStructIndelCount() {
		return structIndelCount;
	}

	/**
	 * @param structIndelCount the structIndelCount to set
	 */
	public void setStructIndelCount(int structIndelCount) {
		this.structIndelCount = structIndelCount;
	}

	/**
	 * @return the structInsCount
	 */
	public int getStructInsCount() {
		return structInsCount;
	}

	/**
	 * @param structInsCount the structInsCount to set
	 */
	public void setStructInsCount(int structInsCount) {
		this.structInsCount = structInsCount;
	}

	/**
	 * @return the structDelCount
	 */
	public int getStructDelCount() {
		return structDelCount;
	}

	/**
	 * @param structDelCount the structDelCount to set
	 */
	public void setStructDelCount(int structDelCount) {
		this.structDelCount = structDelCount;
	}

	/**
	 * @return the tiCount
	 */
	public int getTiCount() {
		return tiCount;
	}

	/**
	 * @param tiCount the tiCount to set
	 */
	public void setTiCount(int tiCount) {
		this.tiCount = tiCount;
	}

	/**
	 * @return the tvCount
	 */
	public int getTvCount() {
		return tvCount;
	}

	/**
	 * @param tvCount the tvCount to set
	 */
	public void setTvCount(int tvCount) {
		this.tvCount = tvCount;
	}

	/**
	 * @return the refGenotypeCount
	 */
	public int getRefGenotypeCount() {
		return refGenotypeCount;
	}

	/**
	 * @param refGenotypeCount the refGenotypeCount to set
	 */
	public void setRefGenotypeCount(int refGenotypeCount) {
		this.refGenotypeCount = refGenotypeCount;
	}

	/**
	 * @return the altGenotypeCounts
	 */
	public ArrayList<Integer> getAltGenotypeCounts() {
		return altGenotypeCounts;
	}

	/**
	 * @param altGenotypeCounts the altGenotypeCounts to set
	 */
	public void setAltGenotypeCounts(ArrayList<Integer> altGenotypeCounts) {
		this.altGenotypeCounts = altGenotypeCounts;
	}
	
	/**
	 * @return the quality
	 */
	public String getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}

	/**
	 * @return the depth
	 */
	public Depth getDepth() {
		return depth;
	}

	/**
	 * @param depth the depth to set
	 */
	public void setDepth(Depth depth) {
		this.depth = depth;
	}
	
	
	/**
	 * Create a comma-separated string
	 * with all alts
	 * 
	 * @param alts
	 * @return
	 */
	public String altToString(){
		
		StringBuilder altString = new StringBuilder();
		for(int i = 0; i < this.alts.size(); i++){
			if(i == 0){
				altString.append(this.alts.get(i));
			}
			else{
				altString.append("," + this.alts.get(i));
			}
		}
		return altString.toString();
	}
	
	
}
