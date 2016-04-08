/**
 * 
 */
package vtc.datastructures;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

/**
 * @author markebbert
 *
 */
public interface VariantPool extends Pool{
	
	public VariantContext getNextVar() throws IOException;
	public TreeSet<String> getSamples();
	public VCFHeader getHeader();
	public String getPoolID();
	public File getFile();

}
