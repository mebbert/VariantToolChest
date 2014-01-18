/**
 * 
 */
package vtc.tools.arupfrequencycalculator;


/**
 * @author markebbert
 *
 */
public class NoCall implements Comparable<NoCall>{

	private String gene;
	private String reason;
	private String chr;
	private int start;
	private int end;
	private int size;
	
	
	public NoCall(){
		return;
	}

	/**
	 * @param gene
	 * @param reason
	 * @param chr
	 * @param start
	 * @param end
	 * @param size
	 */
	public NoCall(String gene, String reason, String chr, int start, int end,
			int size) {
		this.gene = gene;
		this.reason = reason;
		this.chr = chr;
		this.start = start;
		this.end = end;
		this.size = size;
	}
	
	
	
	
	/**
	 * @return the gene
	 */
	public String getGene() {
		return gene;
	}
	/**
	 * @param gene the gene to set
	 */
	public void setGene(String gene) {
		this.gene = gene;
	}
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
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
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	public Integer isInt(String s){
		try{
            return Integer.parseInt(s);
		} catch(NumberFormatException e){
			return null;
		}
	}

	@Override
	public int compareTo(NoCall nc) {
		Integer thisChr = isInt(this.getChr());
		Integer ncChr = isInt(nc.getChr());
		Integer thisStart = this.getStart();
		Integer ncStart = nc.getStart();
		Integer thisEnd = this.getEnd();
		Integer ncEnd = nc.getEnd();
		int chrStringCompare;
		
		if(thisChr != null && ncChr != null){ // They're both ints
            if(thisChr > ncChr){
                return 1;
            }
            else if(thisChr < ncChr){
                return -1;
            }
            else if(thisChr == ncChr){
            	if(positionsOverlap(thisStart, thisEnd, ncStart, ncEnd)){
                    /* Two nocall regions overlap. Consider them equal. This is directed
                     * at comparing a nocall variant to a no call region
                     */
            		return 0; 
            	}
                else if(thisStart > ncStart){
                    return 1;
                }
                else if(thisStart < ncStart){
                    return -1;
                }
            }
		}
		else if(thisChr != null && ncChr == null){ // thisChr is an int and ncChr is a string
			return -1;
		}
		else if(thisChr == null && ncChr != null){ // this Chr is a string and ncChr is an int
			return 1;
		}
		else{ // They're both strings (e.g. X, Y, or MT)
			chrStringCompare = this.getChr().compareTo(nc.getChr());
			if(chrStringCompare < 0 || chrStringCompare > 0){ // They're different chr strings
				return chrStringCompare;
			}
			else{ // They're the same chr string
				if(positionsOverlap(thisStart, thisEnd, ncStart, ncEnd)){
					return 0;
				}
                else if(thisStart > ncStart){
                    return 1;
                }
                else if(thisStart < ncStart){
                    return -1;
                }
			}
		}
		throw new RuntimeException("ERROR: Did not know how to handle two no call regions: "
				+ this.getChr() + ":" + this.getStart()
				+ ":" + this.getEnd() + " & " + nc.getChr() + ":" + nc.getStart()
				+ ":" + nc.getEnd());
	}
	
	private boolean positionsOverlap(int start1, int end1, int start2, int end2){
    	if(start1 >= start2 && start1 <= end2
    			|| end1 >= start2 && end1 <= end2){
            /* Two nocall regions overlap. Consider them equal. This is directed
             * at comparing a nocall variant to a no call region
             */
    		return true; 
    	}
    	return false;
	}
	
	public String toString(){
		return this.gene + ":" + this.chr + ":" + this.start +
				":" + this.end + "\tsize: " + this.size +
				"\treason: " + this.reason;
	}
	
	
}