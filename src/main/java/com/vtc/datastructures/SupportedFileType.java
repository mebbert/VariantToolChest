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

	VCF("VCF", "v", ".out.vcf",
			new ArrayList<String>(Arrays.asList(".vcf,.vcf.gz".split(","))));
	
	private String poolIDPrefix, name;
	private ArrayList<String> expectedExtensions;
	private String defaultExtension;
	private SupportedFileType(String name, String poolIDPrefix, String defaultExtension,
			ArrayList<String> expectedExtensions){
		this.name = name;
		this.poolIDPrefix = poolIDPrefix;
		this.defaultExtension = defaultExtension;
		this.expectedExtensions = expectedExtensions;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getPoolIDPrefix(){
		return this.poolIDPrefix;
	}
	
	public String getDefaultExtension(){
		return this.defaultExtension;
	}
	
	public ArrayList<String> getExpectedExtensions(){
		return this.expectedExtensions;
	}

	public String toString(){
		return getName();
	}
}
