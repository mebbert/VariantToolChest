/**
 * 
 */
package vtc.tools.setoperator.operation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.Assert;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.datastructures.VariantPool;

/**
 * @author Kevin
 * 
 */
public class IntersectOperationTest {

	private static String hgref = "C:\\Users\\Kevin\\Desktop\\Kauwe_Lab\\human_g1k_v37.fasta";

	private static String test_path = "/test_data/IntersectTests";
	private static String output_path = "/test_data/OUTPUT";

	private ArrayList<ArrayList<Test_Params>> mytests;

	/**
	 * This will get the tests and also set up the output folders.
	 * 
	 * @throws java.lang.Exception
	 */
	// @Before
	// public void setUp() throws Exception {
	// // This will make it so it will automatically generate and run tests for all folders
	// // within the specific test.
	//
	// // List the directories.
	// File folder = new File(test_path);
	// File[] types_of_tests = folder.listFiles(); // These are the het_only, het_or_homo, etc...
	//
	// for (int i = 0; i < types_of_tests.length; i++) {
	// ArrayList<Test_Params> test_group = new ArrayList<Test_Params>();
	//
	// if (types_of_tests[i].isDirectory()) {
	// //lets create the output folder for this test group..
	// File
	// File specific_test = new File(test_path + "/" + types_of_tests[i].getName());
	// File[] test_list = specific_test.listFiles(); // these are the test1, test2, test3,
	//
	// Test_Params param = new Test_Params();
	// for (int j = 0; j < test_list.length; j++) {
	// param.setInputfiles((ArrayList<File>) Arrays.asList(listFilesMatching(new
	// File(test_list[j].getPath()), "input\\d+\\.vcf"))); //get all the input files..
	// param.setAnwser(listFilesMatching(new File(test_list[j].getPath()),
	// "input\\d+\\.vcf")[0].getPath());
	//
	// param.setOutfile(outfile);
	// test_group.add(param);
	// }
	// }
	// mytests.add(test_group);
	// }
	// }

	/**
	 * Test how the intersect operator works
	 */
	@Test
	public void testIntersectOperation_HetOnly() {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		String in1 = "target/test-classes/IntersectTests/Het_Only/Test1/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Only/Test1/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Only/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Only/i_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}

	/**
	 * Test how the intersect operator works
	 */
	@Test
	public void testIntersectOperation_HetorHomoAlt() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test1/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test1/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/i_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g het_homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

		// /////////Test2///////////
		in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test2/input1.vcf";
		in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test2/input2.vcf";
		answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test2/Answer.vcf";
		out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/i_test2_out.vcf";

		arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het_homo_alt -s i[var1:var2] -o "
				+ out;

		args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}

	/**
	 * Test how the intersect operator works
	 */
	@Test
	public void test() {
		//ClassLoader loader = Thread.currentThread().getContextClassLoader();
		//Resource resource = appContext.getResource("classpath:com/mkyong/common/testing.txt");		System.out.println(file);
	}
	
	/**
	 * Test how the intersect operator works
	 */
	@Test
	public void testIntersectOperation_MatchSamples() {
		
		URL in1 = this.getClass().getResource("");
		URL in2 = this.getClass().getResource("");
		URL answer = this.getClass().getResource("");
		URL out = this.getClass().getResource("");

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g match_sample -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer.toString(), out.toString());
		assert (true);
	}

	private void test2files(String answer, String out) {
		// Create key and program response answers
		VariantPool key_pool = null;
		VariantPool test_pool = null;
		try {
			key_pool = new VariantPool(new File(answer), "1", false);
			test_pool = new VariantPool(new File(out), "2", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String currVarKey;
		String currVarTest;

		Iterator<String> key_it = key_pool.getVariantIterator();
		Iterator<String> test_it = test_pool.getVariantIterator();

		VariantContext var_key;
		VariantContext var_test;

		// assertEquals(key_pool, test_pool);

		// make sure they have same number of variants..
		assertEquals(key_pool.getNumVarRecords(), test_pool.getNumVarRecords());

		// make sure they have the same number of samples.
		assertEquals(key_pool.getSamples().size(), test_pool.getSamples().size());

		// Iterate over variants in the key and check if they are equal...
		while (key_it.hasNext() && test_it.hasNext()) {

			currVarKey = key_it.next();
			currVarTest = test_it.next();

			var_key = key_pool.getVariant(currVarKey);
			var_test = test_pool.getVariant(currVarTest);

			// assert that they have the same reference and alternate alleles....
			Assert.assertTrue("Ref-\nkey: " + var_key.getReference() + " test: " + var_key.getReference() + "\n", var_key.hasSameAllelesAs(var_test));
			Assert.assertTrue("Alt-\nkey: " + var_key.getAlternateAlleles().toString() + " test: " + var_key.getAlternateAlleles().toString() + "\n", var_key.hasSameAlternateAllelesAs(var_test));

		}
	}

	public static File[] listFilesMatching(File root, String regex) {
		if (!root.isDirectory()) {
			throw new IllegalArgumentException(root + " is no directory.");
		}
		final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!
		return root.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return p.matcher(file.getName()).matches();
			}
		});
	}

	/**
	 * @author Kevin
	 * 
	 */
	public class Test_Params {
		private String anwser;
		private ArrayList<File> inputfiles;
		private String outfile;
		private ArrayList<String> params;

		/**
		 * @return the anwser
		 */
		public String getAnwser() {
			return anwser;
		}

		/**
		 * @param anwser
		 *            the anwser to set
		 */
		public void setAnwser(String anwser) {
			this.anwser = anwser;
		}

		/**
		 * @return the inputfiles
		 */
		public ArrayList<File> getInputfiles() {
			return inputfiles;
		}

		/**
		 * @param inputfiles
		 *            the inputfiles to set
		 */
		public void setInputfiles(ArrayList<File> inputfiles) {
			this.inputfiles = inputfiles;
		}

		/**
		 * @return the outfile
		 */
		public String getOutfile() {
			return outfile;
		}

		/**
		 * @param outfile
		 *            the outfile to set
		 */
		public void setOutfile(String outfile) {
			this.outfile = outfile;
		}

		/**
		 * @return the params
		 */
		public ArrayList<String> getParams() {
			return params;
		}

		/**
		 * @param params
		 *            the params to set
		 */
		public void setParams(ArrayList<String> params) {
			this.params = params;
		}

	}
}
