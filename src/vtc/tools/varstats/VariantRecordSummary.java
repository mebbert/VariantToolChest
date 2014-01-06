/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.TreeSet;

import org.broadinstitute.variant.variantcontext.Allele;

import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author markebbert
 *
 */
public class VariantRecordSummary {

	private String chr, id;
	private int position;
	private Allele ref;
	private ArrayList<Allele> alts;
	private TreeSet<String> insertions, deletions;
	private int snvCount, mnvCount, indelCount, insCount,
		delCount, structIndelCount, structInsCount,
		structDelCount, tiCount, tvCount, genoTiCount,
		genoTvCount, refGenotypeCount, refSampleCount,
		nSamples, nSamplesWithCall;
	private String quality;
	private ArrayList<Integer> altGenotypeCounts, altSampleCounts;
	private Depth depth;
	

	/**
	 * @param chr
	 * @param position
	 * @param ref
	 * @param alts
	 */
	public VariantRecordSummary(String chr, int position, String id, Allele ref,
			ArrayList<Allele> alts) {
		this.chr = chr;
		this.position = position;
		this.id = id;
		this.ref = ref;
		this.alts = alts;
		init();
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
	public VariantRecordSummary(String chr, int position, String id, Allele ref, ArrayList<Allele> alts,
			int snpCount, int mnpCount, int indelCount,
			int insCount, int delCount, int structIndelCount,
			int structInsCount, int structDelCount, int tiCount, int tvCount) {
		this.chr = chr;
		this.position = position;
		this.id = id;
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
		init();
	}
	
	private void init(){
		this.insertions = new TreeSet<String>();
		this.deletions = new TreeSet<String>();
	}

	/**
	 * @return the chr
	 */
	public String getChr() {
		return chr;
	}

	/**
	 * @param chr the chr to set
	 */
	public void setChr(String chr) {
		this.chr = chr;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	
	public String getID() {
		return id;
	}
	
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * @return the ref
	 */
	public Allele getRef() {
		return ref;
	}

	/**
	 * @param ref the ref to set
	 */
	public void setRef(Allele ref) {
		this.ref = ref;
	}

	/**
	 * @return the alts
	 */
	public ArrayList<Allele> getAlts() {
		return alts;
	}

	/**
	 * @param alts the alts to set
	 */
	public void setAlts(ArrayList<Allele> alts) {
		this.alts = alts;
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
	public void setMnvCount(int mnvCount) {
		this.mnvCount = mnvCount;
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
	 * @return the genoTiCount
	 */
	public int getGenoTiCount() {
		return genoTiCount;
	}

	/**
	 * @param genoTiCount the genoTiCount to set
	 */
	public void setGenoTiCount(int genoTiCount) {
		this.genoTiCount = genoTiCount;
	}

	/**
	 * @return the genoTvCount
	 */
	public int getGenoTvCount() {
		return genoTvCount;
	}

	/**
	 * @param genoTvCount the genoTvCount to set
	 */
	public void setGenoTvCount(int genoTvCount) {
		this.genoTvCount = genoTvCount;
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
	 * @return the refSampleCount
	 */
	public int getRefSampleCount() {
		return refSampleCount;
	}

	/**
	 * @param refSampleCount the refSampleCount to set
	 */
	public void setRefSampleCount(int refSampleCount) {
		this.refSampleCount = refSampleCount;
	}

	/**
	 * @return the altSampleCounts
	 */
	public ArrayList<Integer> getAltSampleCounts() {
		return altSampleCounts;
	}

	/**
	 * @param altSampleCounts the altSampleCounts to set
	 */
	public void setAltSampleCounts(ArrayList<Integer> altSampleCounts) {
		this.altSampleCounts = altSampleCounts;
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
	
	public TreeSet<String> getInsertions(){
		return this.insertions;
	}
	
	public void addInsertion(String insertion){
		this.insertions.add(insertion);
	}
	
	/**
	 * Get the smallest insertion length
	 * @return
	 */
	public int getSmallestInsertionLength(){
		return UtilityBelt.getSmallestLength(this.insertions);
	}
	
	/**
	 * Get the largest insertion length
	 * @return
	 */
	public int getLargestInsertionLength(){
		return UtilityBelt.getLargestLength(this.insertions);
	}
	
	/**
	 * Get the average insertion length
	 * @return
	 */
	public double getAverageInsertionLength(){
		return UtilityBelt.getAverageLength(this.insertions);
	}
	
	public TreeSet<String> getDeletions(){
		return this.deletions;
	}
	
	public void addDeletion(String deletion){
		this.deletions.add(deletion);
	}
	
		/**
	 * Get the smallest deletion length
	 * @return
	 */
	public int getSmallestDeletionLength(){
		return UtilityBelt.getSmallestLength(this.deletions);
	}
	
	/**
	 * Get the largest deletion length
	 * @return
	 */
	public int getLargestDeletionLength(){
		return UtilityBelt.getLargestLength(this.deletions);
	}
	
	/**
	 * Get average deletion length
	 * @return
	 */
	public double getAverageDeletionLength(){
		return UtilityBelt.getAverageLength(this.deletions);
	}
	
	/**
	 * @return the nSamples
	 */
	public int getnSamples() {
		return nSamples;
	}

	/**
	 * @param nSamples the nSamples to set
	 */
	public void setnSamples(int nSamples) {
		this.nSamples = nSamples;
	}

	/**
	 * @return the nSamplesWithCall
	 */
	public int getnSamplesWithCall() {
		return nSamplesWithCall;
	}

	/**
	 * @param nSamplesWithCall the nSamplesWithCall to set
	 */
	public void setnSamplesWithCall(int nSamplesWithCall) {
		this.nSamplesWithCall = nSamplesWithCall;
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
				altString.append(",");
				altString.append(this.alts.get(i));
			}
		}
		return altString.toString();
	}
	
	public String altGenotypeCountToString(){
		StringBuilder altGenoCount = new StringBuilder();
		for(int i = 0; i < this.altGenotypeCounts.size(); i++){
			if(i == 0){
				altGenoCount.append(this.altGenotypeCounts.get(i));
			}
			else{
				altGenoCount.append(",");
				altGenoCount.append(this.altGenotypeCounts.get(i));
			}
		}
		return altGenoCount.toString();
	}
	
	public String altSampleCountToString(){
		StringBuilder altSampleCount = new StringBuilder();
		for(int i = 0; i < this.altSampleCounts.size(); i++){
			if(i == 0){
				altSampleCount.append(this.altSampleCounts.get(i));
			}
			else{
				altSampleCount.append(",");
				altSampleCount.append(this.altSampleCounts.get(i));
			}
		}
		return altSampleCount.toString();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getChr());
		sb.append("\t");
		sb.append(this.getPosition());
		sb.append("\t");
		sb.append(this.getID());
		sb.append("\t");
		sb.append(this.getRef().getBaseString());
		sb.append("\t");
		sb.append(this.altToString());
		sb.append("\t");
		sb.append(this.getRefGenotypeCount());
		sb.append("\t");
		sb.append(this.altGenotypeCountToString());
		sb.append("\t");
		sb.append(this.getRefSampleCount());
		sb.append("\t");
		sb.append(this.altSampleCountToString());
		sb.append("\t");
		sb.append(this.getnSamplesWithCall());
		sb.append("\t");
		sb.append(this.getnSamples());
		sb.append("\t");
		sb.append(this.getDepth().getMinDepth());
		sb.append("\t");
		sb.append(this.getDepth().getMaxDepth());
		sb.append("\t");
		sb.append(this.getDepth().getAvgDepth());
		sb.append("\t");
		sb.append(this.getQuality());
		return sb.toString();
	}
	
}
