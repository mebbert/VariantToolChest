package vtc.tools.setoperator.operation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.datastructures.VariantPool;
import vtc.tools.utilitybelt.UtilityBelt;

public class ComplementOperationTest {


	private static String hgref = UtilityBelt.getHGREF();

	@BeforeClass
	public static void setUpClass(){
		System.out.println();
		System.out.println("==========================================================================");
		System.out.println("                             Complement");
		System.out.println("==========================================================================");
		System.out.println();
	}
	
	
	
	@Test
	public void test() {
		assertTrue(true);
	}
	
	
	/**
	 * Test how the complement operator works for homozygous Alternate.
	 */
	@Test
	public void testComplementOperation_HetOrHomoAlt_Test1() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/ComplementTests/Het_or_Homo_Alt/Test1/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Het_or_Homo_Alt/Test1/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Het_or_Homo_Alt/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Het_or_Homo_Alt/c_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c het_homo_alt -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}
	
	/**
	 * Test how the complement operator works for homozygous Alternate.
	 */
	@Test
	public void testComplementOperation_HetOrHomoAlt_Test2() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/ComplementTests/Het_or_Homo_Alt/Test2/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Het_or_Homo_Alt/Test2/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Het_or_Homo_Alt/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Het_or_Homo_Alt/c_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c het_homo_alt -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}	
	
	/**
	 * Test how the complement operator works for the exact option.
	 */
	@Test
	public void testComplementOperation_Exact_Test1() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/ComplementTests/Exact/Test1/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Exact/Test1/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Exact/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Exact/c_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c exact -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}	
	
	/**
	 * Test how the complement operator works for the exact option.
	 */
	@Test
	public void testComplementOperation_Exact_Test2() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/ComplementTests/Exact/Test2/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Exact/Test2/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Exact/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Exact/c_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c exact -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}	
	
	
	/**
	 * Test how the complement operator works for complement based on chr, pos, ref, alt.
	 */
	@Test
	public void testComplementOperation_Alt_Test1() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/ComplementTests/Alt/Test1/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Alt/Test1/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Alt/Test1/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Alt/c_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c alt -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}	
	
	
	/**
	 * Test how the complement operator works for complement based on chr, pos, ref, alt.
	 */
	@Test
	public void testComplementOperation_Alt_Test2() {

		// ///////////Test1//////////////
		String in1 = "target/test-classes/ComplementTests/Alt/Test2/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Alt/Test2/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Alt/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Alt/c_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c alt -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
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
				// Assert that the sample has a halpotype quality score
				Assert.assertTrue(curr_test_geno.hasAnyAttribute("HQ"));
					
			}

			// assert that they have the same reference and alternate alleles....
			Assert.assertTrue("Ref-\nkey: " + var_key.getReference() + " test: " + var_key.getReference() + "\n", var_key.hasSameAllelesAs(var_test));
			Assert.assertTrue("Alt-\nkey: " + var_key.getAlternateAlleles().toString() + " test: " + var_key.getAlternateAlleles().toString() + "\n", var_key.hasSameAlternateAllelesAs(var_test));
				
		}
	}
	
	
	
}
