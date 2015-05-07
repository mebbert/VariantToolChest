/**
 * 
 */
package vtc.datastructures;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFHeader;

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
