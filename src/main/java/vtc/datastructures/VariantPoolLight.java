/**
 * 
 */
package vtc.datastructures;

import java.io.File;
import java.io.IOException;

/**
 * @author markebbert
 *
 */
public class VariantPoolLight extends AbstractVariantPool {

	/**
	 * @param addChr
	 * @param poolID
	 */
	public VariantPoolLight(boolean addChr, String poolID) {
		super(addChr, poolID);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param file
	 * @param poolID
	 * @param requireIndex
	 * @param addChr
	 * @throws IOException
	 */
	public VariantPoolLight(File file, String poolID, boolean requireIndex,
			boolean addChr) throws IOException {
		super(file, poolID, requireIndex, addChr);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inputString
	 * @param requireIndex
	 * @param addChr
	 * @throws InvalidInputFileException
	 * @throws IOException
	 */
	public VariantPoolLight(String inputString, boolean requireIndex,
			boolean addChr) throws InvalidInputFileException, IOException {
		super(inputString, requireIndex, addChr);
		// TODO Auto-generated constructor stub
	}

}
