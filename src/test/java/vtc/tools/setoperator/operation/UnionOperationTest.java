/**
 * 
 */
package vtc.tools.setoperator.operation;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

/**
 * @author Kevin
 * 
 */
public class UnionOperationTest {
	
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";

	private static String hgref = UtilityBelt.getHGREF();

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(RED+"========================================================\n"
				+ "                       UnionOperationTest                  \n"
				+ "========================================================\n"+RESET);	
	}
	
	/**
	 * Test how the union operator works
	 */
	@Test
	public void testUniqueSampleSetsDifferentVariantSets() {

		System.out.println(GREEN+"\ntest Unique Sample Sets Different Variant Sets"+RESET);
		
		String in1 = "src/test/resources/test_data/UnionTests/testUniqueSampleSetsDifferentVariantSets/input1.vcf";
		String in2 = "src/test/resources/test_data/UnionTests/testUniqueSampleSetsDifferentVariantSets/input2.vcf";
		String answer = "src/test/resources/test_data/UnionTests/testUniqueSampleSetsDifferentVariantSets/Answer.vcf";
		String out = "src/test/resources/test_data/OUTPUT/union/testUniqueSampleSetsDifferentVariantSets.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}

	/**
	 * Test how the union operator works
	 */
	@Test
	public void testOverlappingSampleSameVariantSet() {
		
		System.out.println(GREEN+"\ntest Overlapping Sample Same Variant Set"+RESET);
		
		String in1 = "target/test-classes/UnionTests/testOverlappingSampleSameVariantSet/input1.vcf";
		String in2 = "target/test-classes/UnionTests/testOverlappingSampleSameVariantSet/input2.vcf";
		String answer = "target/test-classes/UnionTests/testOverlappingSampleSameVariantSet/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/testOverlappingSampleSameVariantSet.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}

	/**
	 * Test how the union operator works
	 */
	@Test
	public void testDifferentAltsAdded() {

		System.out.println(GREEN+"\ntest Different Alts Added"+RESET);

		String in1 = "target/test-classes/UnionTests/testDifferentAltsAdded/input1.vcf";
		String in2 = "target/test-classes/UnionTests/testDifferentAltsAdded/input2.vcf";
		String answer = "target/test-classes/UnionTests/testDifferentAltsAdded/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/testDifferentAltsAdded.vcf";

		String arguments = "SO -i var1=" + in2 + " var2=" + in1 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer,out);

	}

	/**
	 * Test how the union operator works
	 */
	@Test
	public void testOverlappingSamplesDifferentVariantSets() {

		System.out.println(GREEN+"\ntest Overlapping Samples Different Variant Sets"+RESET);
		
		String in1 = "target/test-classes/UnionTests/testOverlappingSamplesDifferentVariantSets/input1.vcf";
		String in2 = "target/test-classes/UnionTests/testOverlappingSamplesDifferentVariantSets/input2.vcf";
		String answer = "target/test-classes/UnionTests/testOverlappingSamplesDifferentVariantSets/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/testOverlappingSamplesDifferentVariantSets.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer,out);

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
//					System.out.println(curr_key_geno.toString());
//					System.out.println(curr_test_geno.toString());
					Assert.assertArrayEquals(curr_key_geno.getAlleles().toArray(), curr_test_geno.getAlleles().toArray());
					Assert.assertTrue(curr_key_geno.compareTo(curr_test_geno)==0);
					// Assert that the sample has a read depth
//					Assert.assertTrue(curr_test_geno.hasDP());
					// Assert that the sample has a genotype quality score
//					Assert.assertTrue(curr_test_geno.hasGQ());
					// Assert that the sample has a halpotype quality score
//					Assert.assertTrue(curr_test_geno.hasAnyAttribute("HQ"));
					
				}

				// assert that they have the same reference and alternate alleles....
				Assert.assertTrue("Ref-\nkey: " + var_key.getReference() + " test: " + var_key.getReference() + "\n", var_key.hasSameAllelesAs(var_test));
				Assert.assertTrue("Alt-\nkey: " + var_key.getAlternateAlleles().toString() + " test: " + var_key.getAlternateAlleles().toString() + "\n", var_key.hasSameAlternateAllelesAs(var_test));
			}
		}

	private void test2files_usingcmp(String answer, String out) {
		//Create key and program response answers

		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec("cmp " + answer + " " + out);
			p.waitFor();
			

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			assert(output==null);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
