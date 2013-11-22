/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;

import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author markebbert
 *
 */
public class VariantPoolSummary {

	private int numSamples;
	private int numVarRecords;
	private int numVars;
	private int numSNVs;
	private int numMNVs;
	private int numIndels;
	private int numInsertions;
	private int numDeletions;
	private int smallestInsertion;
	private int largestInsertion;
	private double avgInsertionSize;
	private int smallestDeletion;
	private int largestDeletion;
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
	public VariantPoolSummary(int numVarRecords, int numVars, int numSNVs, int numMNVs, int numIndels,
			int numInsertions, int numDeletions, int smallestInsertion,
			int largestInsertion, double avgInsertionSize,
			int smallestDeletion, int largestDeletion,
			double avgDeletionSize, int numStructVars, int numStructIns, int numStructDels, int numMultiAlts,
			double tiCount, double tvCount, double tiTv, double genoTiCount,
			double genoTvCount, double genoTiTv) {
		this.numVarRecords = numVarRecords;
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
	
	
	public VariantPoolSummary() {
		numSamples = 0;
		numVars = 0;
		numSNVs = 0;
		numMNVs = 0;	
		numIndels = 0;
		numInsertions = 0;
		numDeletions = 0;
		smallestInsertion = 0;
		largestInsertion = 0;
		avgInsertionSize = 0;
		smallestDeletion = 0;
		largestDeletion = 0;
		avgDeletionSize = 0;
		numStructVars = 0;
		numStructIns = 0;
		numStructDels = 0;
		numMultiAlts = 0;
		tiCount = 0;
		tvCount = 0;
		genoTiCount = 0;
		genoTvCount = 0;
	}
	
	
	/**
	 * @return the numSamples
	 */
	public int getNumSamples() {
		return numSamples;
	}


	/**
	 * @param numSamples the numSamples to set
	 */
	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}

	/**
	 * @return the numVarRecords
	 */
	public int getNumVarRecords() {
		return numVarRecords;
	}


	/**
	 * @param numVarRecords the numVarRecords to set
	 */
	public void setNumVarRecords(int numVarRecords) {
		this.numVarRecords = numVarRecords;
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
	public void setSmallestInsertion(int smallestInsertion) {
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
	public void setLargestInsertion(int largestInsertion) {
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
	public void setSmallestDeletion(int smallestDeletion) {
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
	public void setLargestDeletion(int largestDeletion) {
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


	public void addition(VariantPoolSummary vps) {
		numSamples += vps.getNumSamples();
		numVars += vps.getNumVars();
		numSNVs += vps.getNumSNVs();
		numMNVs += vps.getNumMNVs();	
		numIndels += vps.getNumIndels();
		numInsertions += vps.getNumInsertions();
		numDeletions += vps.getNumDeletions();
		smallestInsertion += vps.getSmallestInsertion();
		largestInsertion += vps.getLargestInsertion();
		avgInsertionSize += vps.getAvgInsertionSize();
		smallestDeletion += vps.getSmallestDeletion();
		largestDeletion += vps.getLargestDeletion();
		avgDeletionSize += vps.getAvgDeletionSize();
		numStructVars += vps.getNumStructVars();
		numStructIns += vps.getNumStructIns();
		numStructDels += vps.getNumStructDels();
		numMultiAlts += vps.getNumMultiAlts();
		tiCount += vps.getTiCount();
		tvCount += vps.getTvCount();
		genoTiCount += vps.getGenoTiCount();
		genoTvCount += vps.getGenoTvCount();
		
	}
	
	public int longest_length(){
		int length = 0;
		ArrayList<String> values = new ArrayList<String>();
		values.add(Integer.toString(numSamples));
		values.add(Integer.toString(numVars));
		values.add(Integer.toString(numSNVs));
		values.add(Integer.toString(numMNVs));
		values.add(Integer.toString(numIndels));
		values.add(Integer.toString(numInsertions));
		values.add(Integer.toString(numDeletions));
		values.add(Integer.toString(smallestInsertion));
		values.add(Integer.toString(largestInsertion));
		values.add(UtilityBelt.roundDouble((avgInsertionSize)));
		values.add(Integer.toString(smallestDeletion));
		values.add(Integer.toString(largestDeletion));
		values.add(UtilityBelt.roundDouble((avgDeletionSize)));
		values.add(Integer.toString(numStructVars));
		values.add(Integer.toString(numStructIns));
		values.add(Integer.toString(numStructDels));
		values.add(Integer.toString(numMultiAlts));
		values.add(UtilityBelt.roundDouble((tiTv)));
		values.add(UtilityBelt.roundDouble((genoTiTv)));
		for(String s : values){
			if(s.length()>length)
				length = s.length();
		}
		return length;
	}

}
	
