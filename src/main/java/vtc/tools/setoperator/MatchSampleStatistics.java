package vtc.tools.setoperator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import vtc.tools.utilitybelt.UtilityBelt;

public class MatchSampleStatistics {

	
	private Integer match;
	private Integer mismatch;
	private Integer partialMatch;
	
	
	
	public MatchSampleStatistics(){
		this.match = 0;
		this.mismatch = 0;
		this.partialMatch = 0;
	}
	
	
	
	/**
	 * @return the match
	 */
	public Integer getMatch() {
		return match;
	}
	/**
	 * @param match the match to set
	 */
	public void addMatch() {
		this.match += 1;
	}
	/**
	 * @return the mismatch
	 */
	public Integer getMismatch() {
		return mismatch;
	}
	/**
	 * @param mismatch the mismatch to set
	 */
	public void addMismatch() {
		this.mismatch += 1;
	}
	/**
	 * @return the partialMatch
	 */
	public Integer getPartialMatch() {
		return partialMatch;
	}
	/**
	 * @param partialMatch the partialMatch to set
	 */
	public void addPartialMatch() {
		this.partialMatch += 1;
	}
	
	public Integer getTotal(){
		return this.match + this.mismatch + this.partialMatch;
	}
	
	public Double getMatchPercentage(){
		Integer total = this.getTotal();
		if(total == 0)
			return Double.NaN;
		return UtilityBelt.round((Double.valueOf(this.match)/total * 100),1,BigDecimal.ROUND_HALF_UP);
	}
	
	public Double getMismatchPercentage(){
		Integer total = this.getTotal();
		if(total == 0)
			return Double.NaN;
		return UtilityBelt.round((Double.valueOf(this.mismatch)/total * 100),1,BigDecimal.ROUND_HALF_UP);
	}
	
	public Double getPartialmatchPercentage(){
		Integer total = this.getTotal();
		if(total == 0)
			return Double.NaN;
	
		return UtilityBelt.round((Double.valueOf(this.partialMatch)/total * 100),1,BigDecimal.ROUND_HALF_UP);
	}
	
	public Boolean isEmpty(){
		if(match == 0 && mismatch == 0 && partialMatch == 0)
			return true;
		return false;
	}
	
	public void clear(){
		this.match = 0;
		this.mismatch = 0;
		this.partialMatch = 0;
	}
	
	public String toString(){
		
		double match = this.getMatchPercentage();
		double mismatch = this.getMismatchPercentage();
		double partialmatch = this.getPartialmatchPercentage();
		StringBuilder sb = new StringBuilder(String.valueOf(this.match)+"\t");
		if(match == 0.0)
			sb.append("NA\t"+String.valueOf(this.mismatch)+"\t");
		else
			sb.append(String.valueOf(match)+"\t"+String.valueOf(this.mismatch)+"\t");
		
		if(mismatch == 0.0)
			sb.append("NA\t"+String.valueOf(this.partialMatch)+"\t");
		else
			sb.append(String.valueOf(mismatch)+"\t"+String.valueOf(this.partialMatch)+"\t");
		
		if(partialmatch == 0.0)
			sb.append("NA\t"+String.valueOf(this.getTotal()));
		else
			sb.append(String.valueOf(partialmatch)+"\t"+String.valueOf(this.getTotal()));
		
				
		return sb.toString();
	}
	
}
