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

import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.datastructures.VariantPool;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author Kevin
 * 
 */
public class IntersectOperationTest {
	
	private static String hgref = UtilityBelt.getHGREF();

	private static String test_path = "/test_data/IntersectTests";
	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	
	private ArrayList<ArrayList<Test_Params>> mytests;

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(RED+"========================================================\n"
				+ "                       IntersectOperationTest                  \n"
				+ "========================================================\n"+RESET);	
	}

	/**
	 * Test how the intersect operator works for Heterozygous only
	 */
	@Test
	public void testHetOnlyHomoRef() {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		System.out.println(GREEN+"\ntest Het Only Homo Ref"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Only/HomoRef/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Only/HomoRef/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Only/HomoRef/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Only/HomoRef.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}
	
	@Test
	public void testHetOnlyMultipleAlts() {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		System.out.println(GREEN+"\ntest Het Only Multiple Alts"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Only/MultipleAlts/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Only/MultipleAlts/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Only/MultipleAlts/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Only/MultipleAlts.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
		
	}
	
	
	/**
	 * Test how the intersect operator works for Heterozygous and homozygous alternate 
	 */
	@Test
	public void testIntersectOperation_HetorHomoAlt_Test1() {

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
	}
	
	@Test
	public void testIntersectOperation_HetorHomoAlt_Test2() {

		// /////////Test2///////////
		String in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test2/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test2/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/i_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het_homo_alt -s i[var1:var2] -o "
				+ out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}
	
	@Ignore
	public void testIntersectOperation_HetorHomoAlt_Test3() {

		// /////////Test3///////////
		String in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test3/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test3/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/Test3/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/i_test3_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het_homo_alt -s i[var1:var2] -o "
				+ out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}

	/**
	 * Test how the intersect operator works for homozygous reference.
	 */
	@Ignore
	public void testIntersectOperation_HomoRef_Test1() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/IntersectTests/Homo_Ref/Test1/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Ref/Test1/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Ref/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Ref/i_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_ref -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}
	
	@Ignore
	public void testIntersectOperation_HomoRef_Test2() {
		// /////////Test2///////////
		String in1 = "target/test-classes/IntersectTests/Homo_Ref/Test2/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Ref/Test2/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Ref/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Ref/i_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g homo_ref -s i[var1:var2] -o "
				+ out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}
	
	/**
	 * Test how the intersect operator works for chr, pos, ref.
	 */
	@Test
	public void testIntersectOperation_Pos_Test1() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/IntersectTests/Pos/Test1/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Pos/Test1/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Pos/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Pos/i_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g pos -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}
	
	@Ignore
	public void testIntersectOperation_Pos_Test2() {
		
		// /////////Test2///////////
		String in1 = "target/test-classes/IntersectTests/Pos/Test2/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Pos/Test2/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Pos/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Pos/i_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g pos -s i[var1:var2] -o "
				+ out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}
	
	
	/**
	 * Test how the intersect operator works for chr, pos, ref, alt.
	 */
	@Ignore
	public void testIntersectOperation_Alt_Test1() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/IntersectTests/Alt/Test1/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Alt/Test1/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Alt/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Alt/i_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}
	
	@Ignore
	public void testIntersectOperation_Alt_Test2() {

		// /////////Test2///////////
		String in1 = "target/test-classes/IntersectTests/Alt/Test2/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Alt/Test2/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Alt/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Alt/i_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g alt -s i[var1:var2] -o "
				+ out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}
	
	
	/**
	 * Test how the intersect operator works for homozygous Alternate.
	 */
	@Test
	public void testIntersectOperation_HomoAlt_Test1() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/IntersectTests/Homo_Alt/Test1/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Alt/Test1/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Alt/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Alt/i_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}
	
	@Ignore
	public void testIntersectOperation_HomoAlt_Test2() {
		
		// /////////Test2///////////
		String in1 = "target/test-classes/IntersectTests/Homo_Alt/Test2/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Alt/Test2/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Alt/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Alt/i_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g homo_alt -s i[var1:var2] -o "
				+ out;

		String [] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}
	
	
	
	/**
	 * Test how the intersect operator works for matching samples
	 */
	@Ignore
	public void testIntersectOperation_MatchSamples() {
		
		String in1 = "target/test-classes/IntersectTests/Match_Sample/Test1/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Match_Sample/Test1/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Match_Sample/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Match_Sample/i_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g match_sample -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer.toString(), out.toString());
		assert (true);
	}

	// Compare the answer file with the output file
	private void test2files(String answer, String out) {
		// Create key and program response answers
		VariantPool key_pool = null;
		VariantPool test_pool = null;
		try {
			key_pool = new VariantPool(new File(answer), "1", false);
			test_pool = new VariantPool(new File(out), "2", false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String currVarKey;
		String currVarTest;

		Iterator<String> key_it = key_pool.getVariantIterator();
		Iterator<String> test_it = test_pool.getVariantIterator();

		VariantContext var_key;
		VariantContext var_test;

		// Make sure they have same number of variants..
		assertEquals(key_pool.getNumVarRecords(), test_pool.getNumVarRecords());

		// Make sure they have the same number of samples.
		assertEquals(key_pool.getSamples().size(), test_pool.getSamples().size());

		// Iterate over variants in the key and check if they are equal...
		while (key_it.hasNext() && test_it.hasNext()) {

			currVarKey = key_it.next();
			currVarTest = test_it.next();

			var_key = key_pool.getVariant(currVarKey);
			var_test = test_pool.getVariant(currVarTest);
			
			GenotypesContext key_genos = var_key.getGenotypes();	
			GenotypesContext test_genos = var_test.getGenotypes();
			
			Iterator<Genotype> key_geno_it = key_genos.iterator();
			Iterator<Genotype> test_geno_it = test_genos.iterator();
			
			// Iterate through the genotypes for the samples in this variant context
			while(key_geno_it.hasNext() && test_geno_it.hasNext()){
				Genotype curr_key_geno = key_geno_it.next();
				Genotype curr_test_geno = test_geno_it.next();
				
				// Assert that the genotypes are the same
				Assert.assertTrue(curr_key_geno.compareTo(curr_test_geno)==0);
				// Assert that the sample has a read depth
				Assert.assertTrue(curr_test_geno.hasDP());
				// Assert that the sample has a genotype quality score
				Assert.assertTrue(curr_test_geno.hasGQ());

			}

			// assert that they have the same reference and alternate alleles....
			Assert.assertTrue("Ref-\nkey: " + var_key.getReference() + " test: " + var_test.getReference() + "\n", var_key.hasSameAllelesAs(var_test));
			Assert.assertTrue("Alt-\nkey: " + var_key.getAlternateAlleles().toString() + " test: " + var_test.getAlternateAlleles().toString() + "\n", var_key.hasSameAlternateAllelesAs(var_test));
			
			
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
	 * 
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
