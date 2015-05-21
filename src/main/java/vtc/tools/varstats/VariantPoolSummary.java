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
	private int indelCount;
	private int insCount;
	private int delCount;
	private int insSum, delSum, structDelSum, structInsSum;
	private int smallestIns, smallestDel, largestIns, largestDel, smallestStructIns, smallestStructDel, largestStructDel, largestStructIns;
	
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
			double tiCount, double tvCount, double genoTiCount, double genoTvCount) {
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
		this.indelCount = 0;
		this.insCount = 0;
		this.delCount = 0;
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
		this.indelCount = 0;
		this.insCount = 0;
		this.delCount = 0;
	}
	
	



	public VariantPoolSummary(int recordCount, int varRecordCount,
			int sampleCount, int totalVarCount, int snvCount, int mnvCount,
			int structIndelCount, int structInsCount, int structDelCount,
			int multiAltCount, double tiCount2, double tvCount2, double genoTiCount2,
			double genoTvCount2, int indelCount, int insCount,
			int delCount) {
		
		this.numRecords = recordCount;
		this.numSamples = sampleCount;
		this.numVarRecords = varRecordCount;
		this.numVars = totalVarCount;
		this.numSNVs = snvCount;
		this.numMNVs = mnvCount;
		this.numStructVars = structIndelCount;
		this.numStructIns = structInsCount;
		this.numStructDels = structDelCount;
		this.numMultiAlts = multiAltCount;
		this.tiCount = tiCount2;
		this.tvCount = tvCount2;
		this.genoTiCount = genoTiCount2;
		this.genoTvCount = genoTvCount2;
		this.indelCount = indelCount;
		this.insCount = insCount;
		this.delCount = delCount;
	}
	
	




	public VariantPoolSummary(int recordCount, int varRecordCount,
			int sampleCount, int totalVarCount, int snvCount, int mnvCount,
			int structIndelCount, int structInsCount, int structDelCount,
			int multiAltCount, double tiCount2, double tvCount2, double genoTiCount2,
			double genoTvCount2, int indelCount2, int insCount2,
			int delCount2, int insSum, int delSum, int structInsSum, int structDelSum,
			int smallestIns, int smallestDel, int largestIns, int largestDel,
			int smallestStructIns, int smallestStructDel, int largestStructIns,
			int largestStructDel) {
		
		this.numRecords = recordCount;
		this.numSamples = sampleCount;
		this.numVarRecords = varRecordCount;
		this.numVars = totalVarCount;
		this.numSNVs = snvCount;
		this.numMNVs = mnvCount;
		this.numStructVars = structIndelCount;
		this.numStructIns = structInsCount;
		this.numStructDels = structDelCount;
		this.numMultiAlts = multiAltCount;
		this.tiCount = tiCount2;
		this.tvCount = tvCount2;
		this.genoTiCount = genoTiCount2;
		this.genoTvCount = genoTvCount2;
		this.indelCount = indelCount2;
		this.insCount = insCount2;
		this.delCount = delCount2;
		this.insSum = insSum;
		this.delSum = delSum;
		this.structDelSum = structDelSum;
		this.structInsSum = structInsSum;
		this.smallestDel = smallestDel;
		this.smallestIns = smallestIns;
		this.largestDel = largestDel;
		this.largestIns = largestIns;
		this.largestStructDel = largestStructDel;
		this.largestStructIns = largestStructIns;
		this.smallestStructDel = smallestStructDel;
		this.smallestStructIns = smallestStructIns;
		
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
		Double TiTv = getTiCount()/getTvCount();
		if(TiTv.isInfinite() || TiTv == 0)
			return Double.NaN;
		return TiTv;
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
		Double TiTv = getGenoTiCount()/getGenoTvCount();
		if(TiTv.isInfinite() || TiTv == 0)
			return Double.NaN;
		return TiTv;
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
	 * @return the insSum
	 */
	public int getInsSum() {
		return insSum;
	}

	/**
	 * @param insSum the insSum to set
	 */
	public void setInsSum(int insSum) {
		this.insSum = insSum;
	}

	/**
	 * @return the delSum
	 */
	public int getDelSum() {
		return delSum;
	}

	/**
	 * @param delSum the delSum to set
	 */
	public void setDelSum(int delSum) {
		this.delSum = delSum;
	}

	/**
	 * @return the structDelSum
	 */
	public int getStructDelSum() {
		return structDelSum;
	}

	/**
	 * @param structDelSum the structDelSum to set
	 */
	public void setStructDelSum(int structDelSum) {
		this.structDelSum = structDelSum;
	}

	/**
	 * @return the structInsSum
	 */
	public int getStructInsSum() {
		return structInsSum;
	}

	/**
	 * @param structInsSum the structInsSum to set
	 */
	public void setStructInsSum(int structInsSum) {
		this.structInsSum = structInsSum;
	}

	/**
	 * @return the smallestIns
	 */
	public int getSmallestIns() {
		if(this.smallestIns!=Integer.MAX_VALUE)
			return smallestIns;
		return 0;
	}

	/**
	 * @param smallestIns the smallestIns to set
	 */
	public void setSmallestIns(int smallestIns) {
		this.smallestIns = smallestIns;
	}

	/**
	 * @return the smallestDel
	 */
	public int getSmallestDel() {
		if(this.smallestDel!=Integer.MAX_VALUE)
			return smallestDel;
		return 0;
	}

	/**
	 * @param smallestDel the smallestDel to set
	 */
	public void setSmallestDel(int smallestDel) {
		this.smallestDel = smallestDel;
	}

	/**
	 * @return the largestIns
	 */
	public int getLargestIns() {
		return largestIns;
	}

	/**
	 * @param largestIns the largestIns to set
	 */
	public void setLargestIns(int largestIns) {
		this.largestIns = largestIns;
	}

	/**
	 * @return the largestDel
	 */
	public int getLargestDel() {
		return largestDel;
	}

	/**
	 * @param largestDel the largestDel to set
	 */
	public void setLargestDel(int largestDel) {
		this.largestDel = largestDel;
	}

	/**
	 * @return the smallestStructIns
	 */
	public int getSmallestStructIns() {
		if(this.smallestStructIns!=Integer.MAX_VALUE)
			return smallestStructIns;
		return 0;
	}

	/**
	 * @param smallestStructIns the smallestStructIns to set
	 */
	public void setSmallestStructIns(int smallestStructIns) {
		this.smallestStructIns = smallestStructIns;
	}

	/**
	 * @return the smallestStructDel
	 */
	public int getSmallestStructDel() {
		if(this.smallestStructDel!=Integer.MAX_VALUE)
			return smallestStructDel;
		return 0;
	}

	/**
	 * @param smallestStructDel the smallestStructDel to set
	 */
	public void setSmallestStructDel(int smallestStructDel) {
		this.smallestStructDel = smallestStructDel;
	}

	/**
	 * @return the largestStructDel
	 */
	public int getLargestStructDel() {
		return largestStructDel;
	}

	/**
	 * @param largestStructDel the largestStructDel to set
	 */
	public void setLargestStructDel(int largestStructDel) {
		this.largestStructDel = largestStructDel;
	}

	/**
	 * @return the largestStructIns
	 */
	public int getLargestStructIns() {
		return largestStructIns;
	}

	/**
	 * @param largestStructIns the largestStructIns to set
	 */
	public void setLargestStructIns(int largestStructIns) {
		this.largestStructIns = largestStructIns;
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
        
        newVPS.setNumStructVars(vps1.getNumStructVars() + vps2.getNumStructVars());
        newVPS.setNumStructIns(vps1.getNumStructIns() + vps2.getNumStructIns());
        newVPS.setNumStructDels(vps1.getNumStructDels() + vps2.getNumStructDels());
        newVPS.setStructDelSum(vps1.getStructDelSum()+vps2.getStructDelSum());
        newVPS.setStructInsSum(vps1.getStructInsSum()+vps2.getStructInsSum());
        
        newVPS.setSmallestStructIns(getSmallestInDel(vps1.getSmallestStructIns(),vps2.getSmallestStructIns()));
        newVPS.setSmallestStructDel(getSmallestInDel(vps1.getSmallestStructDel(),vps2.getSmallestStructDel()));
        newVPS.setLargestStructIns(getLargestInDel(vps1.getLargestStructIns(),vps2.getLargestStructIns()));
        newVPS.setLargestStructDel(getLargestInDel(vps1.getLargestStructDel(),vps2.getLargestStructDel()));
        
        
        
        newVPS.setDelCount(vps1.getDelCount()+vps2.getDelCount());
        newVPS.setInsCount(vps1.getInsCount()+vps2.getInsCount());
        newVPS.setIndelCount(vps1.getIndelCount()+vps2.getIndelCount());
        newVPS.setDelSum(vps1.getDelSum()+vps2.getDelSum());
        newVPS.setInsSum(vps1.getInsSum()+vps2.getInsSum());

        newVPS.setSmallestIns(getSmallestInDel(vps1.getSmallestIns(),vps2.getSmallestIns()));
        newVPS.setSmallestDel(getSmallestInDel(vps1.getSmallestDel(),vps2.getSmallestDel()));
        newVPS.setLargestIns(getLargestInDel(vps1.getLargestIns(),vps2.getLargestIns()));
        newVPS.setLargestDel(getLargestInDel(vps1.getLargestDel(),vps2.getLargestDel()));

        newVPS.setNumMultiAlts(vps1.getNumMultiAlts() + vps2.getNumMultiAlts());
        
        newVPS.setTiCount(vps1.getTiCount() + vps2.getTiCount());
        newVPS.setTvCount(vps1.getTvCount() + vps2.getTvCount());
        
        newVPS.setGenoTiCount(vps1.getGenoTiCount() + vps2.getGenoTiCount());
        newVPS.setGenoTvCount(vps1.getGenoTvCount() + vps2.getGenoTvCount());
        
        return newVPS;
	}
	




	private static int getSmallestInDel(int vps1, int vps2) {
		if(vps1 > 0){
        	if(vps2 > 0){
        		if(vps1 < vps2){
        			return vps1;
        		}
        		else{
        			return vps2;
        		}
        	}
        	else{
        		return vps1;
        	}
        }
        
		return vps2;
	}

	private static int getLargestInDel(int vps1, int vps2) {
		if(vps1 > 0){
        	if(vps2 > 0){
        		if(vps1 > vps2){
        			return vps1;
        		}
        		else{
        			return vps2;
        		}
        	}
        	else{
        		return vps1;
        	}
        }
        
		return vps2;
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
		values.add(Integer.toString(this.getIndelCount()));
		values.add(Integer.toString(this.getInsCount()));
		values.add(Integer.toString(this.getDelCount()));
		values.add(Integer.toString(this.getSmallestIns()));
		values.add(Integer.toString(this.getLargestIns()));
		values.add(UtilityBelt.roundDoubleToString((this.getAvgIns())));
		values.add(Integer.toString(this.getSmallestDel()));
		values.add(Integer.toString(this.getLargestDel()));
		values.add(UtilityBelt.roundDoubleToString((this.getAvgDel())));
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




	public double getAvgDel() {
		if(this.delCount != 0)
			return this.delSum/this.delCount;
		return Double.NaN;
	}



	public double getAvgIns() {
		if(this.insCount!=0)
			return this.insSum/this.insCount;
		return Double.NaN;
	}

	public double getAvgStructIns() {
		if(this.numStructIns!=0)
			return this.structInsSum/this.numStructIns;
		return Double.NaN;
	}

	public double getAvgStructDel() {
		if(this.numStructDels!=0)
			return this.structDelSum/this.numStructDels;
		return Double.NaN;
	}
	
	
}
	
