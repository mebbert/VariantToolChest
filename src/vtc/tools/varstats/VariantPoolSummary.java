/**
 * 
 */
package vtc.tools.varstats;

/**
 * @author markebbert
 *
 */
public class VariantPoolSummary {

	private int numVars;
	private int numSNVs;
	private int numMNVs;
	private int numIndels;
	private int numInsertions;
	private int numDeletions;
	private double smallestInsertion;
	private double largestInsertion;
	private double avgInsertionSize;
	private double smallestDeletion;
	private double largestDeletion;
	private double avgDeletionSize;
	private int numStructVars;
	private int numStructIns;
	private int numStructDels;
	private int numMultiAlts;
	private double tiCount;
	private double tvCount;
	private double tiTv;
	private double genoTiCount;
	private double genoTvCount;
	private double genoTiTv;
	
	
	/**
	 * @param numVars
	 * @param numSNVs
	 * @param numMNVs
	 * @param numIndels
	 * @param numInsertions
	 * @param numDeletions
	 * @param smallestInsertion
	 * @param largestInsertion
	 * @param avgInsertionSize
	 * @param smallestDeletion
	 * @param largestDeletion
	 * @param avgDeletionSize
	 * @param numStructVars
	 * @param numMultiAlts
	 * @param tiCount
	 * @param tvCount
	 * @param tiTv
	 * @param genoTiCount
	 * @param genoTvCount
	 * @param genoTiTv
	 */
	public VariantPoolSummary(int numVars, int numSNVs, int numMNVs, int numIndels,
			int numInsertions, int numDeletions, double smallestInsertion,
			double largestInsertion, double avgInsertionSize,
			double smallestDeletion, double largestDeletion,
			double avgDeletionSize, int numStructVars, int numStructIns, int numStructDels, int numMultiAlts,
			double tiCount, double tvCount, double tiTv, double genoTiCount,
			double genoTvCount, double genoTiTv) {
		this.numVars = numVars;
		this.numSNVs = numSNVs;
		this.numMNVs = numMNVs;
		this.numIndels = numIndels;
		this.numInsertions = numInsertions;
		this.numDeletions = numDeletions;
		this.smallestInsertion = smallestInsertion;
		this.largestInsertion = largestInsertion;
		this.avgInsertionSize = avgInsertionSize;
		this.smallestDeletion = smallestDeletion;
		this.largestDeletion = largestDeletion;
		this.avgDeletionSize = avgDeletionSize;
		this.numStructVars = numStructVars;
		this.numStructIns = numStructIns;
		this.numStructDels = numStructDels;
		this.numMultiAlts = numMultiAlts;
		this.tiCount = tiCount;
		this.tvCount = tvCount;
		this.tiTv = tiTv;
		this.genoTiCount = genoTiCount;
		this.genoTvCount = genoTvCount;
		this.genoTiTv = genoTiTv;
	}
	
	
	/**
	 * @return the numVars
	 */
	public int getNumVars() {
		return numVars;
	}

	/**
	 * @param numVars the numVars to set
	 */
	public void setNumVars(int numVars) {
		this.numVars = numVars;
	}

	/**
	 * @return the numSNVs
	 */
	public int getNumSNVs() {
		return numSNVs;
	}

	/**
	 * @param numSNVs the numSNVs to set
	 */
	public void setNumSNVs(int numSNVs) {
		this.numSNVs = numSNVs;
	}

	/**
	 * @return the numSNVs
	 */
	public int getNumMNVs() {
		return numMNVs;
	}

	/**
	 * @param numSNVs the numSNVs to set
	 */
	public void setNumMNVs(int numMNVs) {
		this.numMNVs = numMNVs;
	}

	/**
	 * @return the numIndels
	 */
	public int getNumIndels() {
		return numIndels;
	}

	/**
	 * @param numIndels the numIndels to set
	 */
	public void setNumIndels(int numIndels) {
		this.numIndels = numIndels;
	}

	/**
	 * @return the numInsertions
	 */
	public int getNumInsertions() {
		return numInsertions;
	}

	/**
	 * @param numInsertions the numInsertions to set
	 */
	public void setNumInsertions(int numInsertions) {
		this.numInsertions = numInsertions;
	}

	/**
	 * @return the numDeletions
	 */
	public int getNumDeletions() {
		return numDeletions;
	}

	/**
	 * @param numDeletions the numDeletions to set
	 */
	public void setNumDeletions(int numDeletions) {
		this.numDeletions = numDeletions;
	}

	/**
	 * @return the smallestInsertion
	 */
	public double getSmallestInsertion() {
		return smallestInsertion;
	}

	/**
	 * @param smallestInsertion the smallestInsertion to set
	 */
	public void setSmallestInsertion(double smallestInsertion) {
		this.smallestInsertion = smallestInsertion;
	}

	/**
	 * @return the largestInsertion
	 */
	public double getLargestInsertion() {
		return largestInsertion;
	}

	/**
	 * @param largestInsertion the largestInsertion to set
	 */
	public void setLargestInsertion(double largestInsertion) {
		this.largestInsertion = largestInsertion;
	}

	/**
	 * @return the avgInsertionSize
	 */
	public double getAvgInsertionSize() {
		return avgInsertionSize;
	}

	/**
	 * @param avgInsertionSize the avgInsertionSize to set
	 */
	public void setAvgInsertionSize(double avgInsertionSize) {
		this.avgInsertionSize = avgInsertionSize;
	}

	/**
	 * @return the smallestDeletion
	 */
	public double getSmallestDeletion() {
		return smallestDeletion;
	}

	/**
	 * @param smallestDeletion the smallestDeletion to set
	 */
	public void setSmallestDeletion(double smallestDeletion) {
		this.smallestDeletion = smallestDeletion;
	}

	/**
	 * @return the largestDeletion
	 */
	public double getLargestDeletion() {
		return largestDeletion;
	}

	/**
	 * @param largestDeletion the largestDeletion to set
	 */
	public void setLargestDeletion(double largestDeletion) {
		this.largestDeletion = largestDeletion;
	}

	/**
	 * @return the avgDeletionSize
	 */
	public double getAvgDeletionSize() {
		return avgDeletionSize;
	}

	/**
	 * @param avgDeletionSize the avgDeletionSize to set
	 */
	public void setAvgDeletionSize(double avgDeletionSize) {
		this.avgDeletionSize = avgDeletionSize;
	}

	/**
	 * @return the numStructVars
	 */
	public int getNumStructVars() {
		return numStructVars;
	}

	/**
	 * @param numStructVars the numStructVars to set
	 */
	public void setNumStructVars(int numStructVars) {
		this.numStructVars = numStructVars;
	}

	/**
	 * @return the numStructIns
	 */
	public int getNumStructIns() {
		return numStructIns;
	}

	/**
	 * @param numStructIns the numStructIns to set
	 */
	public void setNumStructIns(int numStructIns) {
		this.numStructIns = numStructIns;
	}

	/**
	 * @return the numStructIns
	 */
	public int getNumStructDels() {
		return numStructDels;
	}

	/**
	 * @param numStructIns the numStructIns to set
	 */
	public void setNumStructDels(int numStructDels) {
		this.numStructDels = numStructDels;
	}

	/**
	 * @return the numMultiAlts
	 */
	public int getNumMultiAlts() {
		return numMultiAlts;
	}

	/**
	 * @param numMultiAlts the numMultiAlts to set
	 */
	public void setNumMultiAlts(int numMultiAlts) {
		this.numMultiAlts = numMultiAlts;
	}

	/**
	 * @return the tiCount
	 */
	public double getTiCount() {
		return tiCount;
	}

	/**
	 * @param tiCount the tiCount to set
	 */
	public void setTiCount(double tiCount) {
		this.tiCount = tiCount;
	}

	/**
	 * @return the tvCount
	 */
	public double getTvCount() {
		return tvCount;
	}

	/**
	 * @param tvCount the tvCount to set
	 */
	public void setTvCount(double tvCount) {
		this.tvCount = tvCount;
	}

	/**
	 * @return the tiTv
	 */
	public double getTiTv() {
		return tiTv;
	}

	/**
	 * @param tiTv the tiTv to set
	 */
	public void setTiTv(double tiTv) {
		this.tiTv = tiTv;
	}

	/**
	 * @return the genoTiCount
	 */
	public double getGenoTiCount() {
		return genoTiCount;
	}

	/**
	 * @param genoTiCount the genoTiCount to set
	 */
	public void setGenoTiCount(double genoTiCount) {
		this.genoTiCount = genoTiCount;
	}

	/**
	 * @return the genoTvCount
	 */
	public double getGenoTvCount() {
		return genoTvCount;
	}

	/**
	 * @param genoTvCount the genoTvCount to set
	 */
	public void setGenoTvCount(double genoTvCount) {
		this.genoTvCount = genoTvCount;
	}

	/**
	 * @return the genoTiTv
	 */
	public double getGenoTiTv() {
		return genoTiTv;
	}

	/**
	 * @param genoTiTv the genoTiTv to set
	 */
	public void setGenoTiTv(double genoTiTv) {
		this.genoTiTv = genoTiTv;
	}

}
