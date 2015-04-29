/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.TreeSet;

import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author markebbert
 *
 */
public class VariantPoolSummary {

	private int numSamples;
	private int numRecords;
	private int numVarRecords;
	private int numVars;
	private int numSNVs;
	private int numMNVs;
	private int numStructVars;
	private int numStructIns;
	private int numStructDels;
	private int numMultiAlts;
	private int numHets;
	private int numHomos;
	private double tiCount;
	private double tvCount;
	private double genoTiCount;
	private double genoTvCount;
	TreeSet<String> allInsertions, allDeletions;
	
	
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
	 */
	public VariantPoolSummary(int numRecords, int numVarRecords, int numSamples, int numVars, int numSNVs, int numMNVs,
			int numStructVars, int numStructIns, int numStructDels, int numMultiAlts,
			double tiCount, double tvCount, double genoTiCount, double genoTvCount,
			TreeSet<String> allInsertions, TreeSet<String> allDeletions) {
		this.numRecords = numRecords;
		this.numSamples = numSamples;
		this.numVarRecords = numVarRecords;
		this.numVars = numVars;
		this.numSNVs = numSNVs;
		this.numMNVs = numMNVs;
		this.numStructVars = numStructVars;
		this.numStructIns = numStructIns;
		this.numStructDels = numStructDels;
		this.numMultiAlts = numMultiAlts;
		this.tiCount = tiCount;
		this.tvCount = tvCount;
		this.genoTiCount = genoTiCount;
		this.genoTvCount = genoTvCount;
		this.allInsertions = allInsertions;
		this.allDeletions = allDeletions;
	}
	
	
	
	
	public VariantPoolSummary() {
		numSamples = 0;
		numRecords = 0;
		numVars = 0;
		numSNVs = 0;
		numMNVs = 0;
		numStructVars = 0;
		numStructIns = 0;
		numStructDels = 0;
		numMultiAlts = 0;
		tiCount = 0;
		tvCount = 0;
		genoTiCount = 0;
		genoTvCount = 0;
		this.allInsertions = new TreeSet<String>();
		this.allDeletions = new TreeSet<String>();
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

	public int getNumRecords(){
		return numRecords;
	}
	
	public void setNumRecords(int numRecords){
		this.numRecords = numRecords;
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
		return this.allDeletions.size() + this.allInsertions.size();
	}

	/**
	 * @return the numInsertions
	 */
	public int getNumInsertions() {
		return this.allInsertions.size();
	}

	/**
	 * @return the numDeletions
	 */
	public int getNumDeletions() {
		return this.allDeletions.size();
	}
	
	/**
	 * @return the numHets
	 */
	public int getNumHets() {
		return numHets;
	}

	/**
	 * @param numHets the numHets to set
	 */
	public void setNumHets(int numHets) {
		this.numHets = numHets;
	}

	/**
	 * @return the numHomos
	 */
	public int getNumHomos() {
		return numHomos;
	}

	/**
	 * @param numHomos the numHomos to set
	 */
	public void setNumHomos(int numHomos) {
		this.numHomos = numHomos;
	}

	/**
	 * @return the smallestInsertion
	 */
	public int getSmallestInsertion() {
		return UtilityBelt.getSmallestLength(this.allInsertions);
	}

	/**
	 * @return the largestInsertion
	 */
	public int getLargestInsertion() {
		return UtilityBelt.getLargestLength(this.allInsertions);
	}

	/**
	 * @return the avgInsertionSize
	 */
	public double getAvgInsertionSize() {
		return UtilityBelt.getAverageLength(this.allInsertions);
	}

	/**
	 * @return the smallestDeletion
	 */
	public int getSmallestDeletion() {
		return UtilityBelt.getSmallestLength(this.allDeletions);
	}

	/**
	 * @return the largestDeletion
	 */
	public int getLargestDeletion() {
		return UtilityBelt.getLargestLength(this.allDeletions);
	}

	/**
	 * @return the avgDeletionSize
	 */
	public double getAvgDeletionSize() {
		return UtilityBelt.getAverageLength(this.allDeletions);
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
		return getTiCount()/getTvCount();
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
		return getGenoTiCount()/getGenoTvCount();
	}
	
	/**
	 * @return the allInsertions
	 */
	public TreeSet<String> getAllInsertions() {
		return allInsertions;
	}

	/**
	 * @param allInsertions the allInsertions to set
	 */
	public void setAllInsertions(TreeSet<String> allInsertions) {
		this.allInsertions = allInsertions;
	}
	
	/**
	 * @param newInsertions
	 */
	public void addInsertions(TreeSet<String> newInsertions){
		if(newInsertions != null){
            this.allInsertions.addAll(newInsertions);
		}
	}

	/**
	 * @return the allDeletions
	 */
	public TreeSet<String> getAllDeletions() {
		return allDeletions;
	}

	/**
	 * @param allDeletions the allDeletions to set
	 */
	public void setAllDeletions(TreeSet<String> allDeletions) {
		this.allDeletions = allDeletions;
	}
	
	/**
	 * @param newDeletions
	 */
	public void addDeletions(TreeSet<String> newDeletions){
		if(newDeletions != null){
            this.allDeletions.addAll(newDeletions);
		}
	}

	/**
	 * Add two VariantPoolSummaryObjects together
	 * 
	 * @param vps1
	 * @param vps2
	 * @return a new VariantPoolSummary after adding vps1 and vps2 together.
	 */
	public static VariantPoolSummary addVariantPoolSummaries(VariantPoolSummary vps1, VariantPoolSummary vps2) {
		VariantPoolSummary newVPS = new VariantPoolSummary();
		newVPS.setNumSamples(vps1.getNumSamples() + vps2.getNumSamples());
		newVPS.setNumRecords(vps1.getNumRecords() + vps2.getNumRecords());
        newVPS.setNumVars(vps1.getNumVars() + vps2.getNumVars());
        newVPS.setNumSNVs(vps1.getNumSNVs() + vps2.getNumSNVs());
        newVPS.setNumMNVs(vps1.getNumMNVs() + vps2.getNumMNVs());	
        
        /* give newVPS all insertions and deletions from both VariantPoolSummary objects */
        newVPS.setAllDeletions(vps1.getAllDeletions());
        newVPS.addDeletions(vps2.getAllDeletions());
        newVPS.setAllInsertions(vps1.getAllInsertions());
        newVPS.addInsertions(vps2.getAllInsertions());
        
        newVPS.setNumStructVars(vps1.getNumStructVars() + vps2.getNumStructVars());
        newVPS.setNumStructIns(vps1.getNumStructIns() + vps2.getNumStructIns());
        newVPS.setNumStructDels(vps1.getNumStructDels() + vps2.getNumStructDels());
        newVPS.setNumMultiAlts(vps1.getNumMultiAlts() + vps2.getNumMultiAlts());
        newVPS.setTiCount(vps1.getTiCount() + vps2.getTiCount());
        newVPS.setTvCount(vps1.getTvCount() + vps2.getTvCount());
        newVPS.setGenoTiCount(vps1.getGenoTiCount() + vps2.getGenoTiCount());
        newVPS.setGenoTvCount(vps1.getGenoTvCount() + vps2.getGenoTvCount());
        return newVPS;
	}
	
	/**
	 * Get the longest value that will be printed in the summary tables. This
	 * will allow the table to be formatted with the proper width.
	 * 
	 * @return the longest value
	 */
	public int longest_length(){
		int length = 0;
		ArrayList<String> values = new ArrayList<String>();
		values.add(Integer.toString(this.getNumSamples()));
		values.add(Integer.toString(this.getNumRecords()));
		values.add(Integer.toString(this.getNumVars()));
		values.add(Integer.toString(this.getNumSNVs()));
		values.add(Integer.toString(this.getNumMNVs()));
		values.add(Integer.toString(this.getNumIndels()));
		values.add(Integer.toString(this.getNumInsertions()));
		values.add(Integer.toString(this.getNumDeletions()));
		values.add(Integer.toString(this.getSmallestInsertion()));
		values.add(Integer.toString(this.getLargestInsertion()));
		values.add(UtilityBelt.roundDoubleToString((this.getAvgInsertionSize())));
		values.add(Integer.toString(this.getSmallestDeletion()));
		values.add(Integer.toString(this.getLargestDeletion()));
		values.add(UtilityBelt.roundDoubleToString((this.getAvgDeletionSize())));
		values.add(Integer.toString(this.getNumStructVars()));
		values.add(Integer.toString(this.getNumStructIns()));
		values.add(Integer.toString(this.getNumStructDels()));
		values.add(Integer.toString(this.getNumMultiAlts()));
		values.add(UtilityBelt.roundDoubleToString((this.getGenoTiTv())));
		values.add(UtilityBelt.roundDoubleToString((this.getGenoTiTv())));
		for(String s : values){
			if(s.length()>length)
				length = s.length();
		}
		return length;
	}

}
	
