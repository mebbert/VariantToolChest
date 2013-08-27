/**
 * 
 */
package vtc.datastructures;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author markebbert
 *
 */
public enum SupportedFileType {

	VCF(new ArrayList<String>(Arrays.asList(".vcf,.vcf.gz".split(","))), "v");
	
	private String poolIDPrefix;
	private ArrayList<String> expectedExtensions;
	private SupportedFileType(ArrayList<String> expectedExtensions, String poolIDPrefix){
		this.expectedExtensions = expectedExtensions;
		this.poolIDPrefix = poolIDPrefix;
	}
	
	public ArrayList<String> getExpectedExtensions(){
		return this.expectedExtensions;
	}
	
	public String getPoolIDPrefix(){
		return this.poolIDPrefix;
	}
}
