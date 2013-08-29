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

	VCF("VCF", "v", new ArrayList<String>(Arrays.asList(".vcf,.vcf.gz".split(","))));
	
	private String poolIDPrefix, name;
	private ArrayList<String> expectedExtensions;
	private SupportedFileType(String name, String poolIDPrefix, ArrayList<String> expectedExtensions){
		this.name = name;
		this.poolIDPrefix = poolIDPrefix;
		this.expectedExtensions = expectedExtensions;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getPoolIDPrefix(){
		return this.poolIDPrefix;
	}
	
	public ArrayList<String> getExpectedExtensions(){
		return this.expectedExtensions;
	}

	public String toString(){
		return getName();
	}
}
