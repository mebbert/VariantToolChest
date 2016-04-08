package vtc.tools.setoperator.operation.complementTests;

import static org.junit.Assert.assertEquals;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import vtc.datastructures.VariantPoolHeavy;



@RunWith(Suite.class)
@Suite.SuiteClasses({
	Alt.class,
	Exact.class,
	HetOrHomoAlt.class
})
public class ComplementOperationTest {
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(RED+"\n==========================================================================\n"
				+ "                       ComplementOperationTest                  \n"
				+ "==========================================================================\n"+RESET);	
	}
	
	
	public static void test2files(String answer, String out) throws IOException {
		// Create key and program response answers
		VariantPoolHeavy answer_pool = null;
		VariantPoolHeavy test_pool = null;
		try {
			answer_pool = new VariantPoolHeavy(new File(answer), "1", false);
			test_pool = new VariantPoolHeavy(new File(out), "2", false);
		} catch (IOException e) {
			e.printStackTrace();
		}

//		String currVarAnswerKey;
//		String currVarTestKey;

//		Iterator<String> answer_it = answer_pool.getVariantIterator();
//		Iterator<String> test_it = test_pool.getVariantIterator();

		VariantContext answer_var;
		VariantContext test_var;

		// Make sure they have same number of variants..
		assertEquals(answer_pool.getNumVarRecords(), test_pool.getNumVarRecords());

		// Make sure they have the same number of samples.
		assertEquals(answer_pool.getSamples().size(), test_pool.getSamples().size());

		// Iterate over variants in the key and check if they are equal...
//		while (answer_it.hasNext() && test_it.hasNext()) {
		while((answer_var = answer_pool.getNextVar()) != null
				&& (test_var = test_pool.getNextVar()) != null){

//			currVarAnswerKey = answer_it.next();
//			currVarTestKey = test_it.next();

//			answer_var = answer_pool.getVariant(currVarAnswerKey);
//			test_var = test_pool.getVariant(currVarTestKey);
			
			GenotypesContext key_genos = answer_var.getGenotypes();	
			GenotypesContext test_genos = test_var.getGenotypes();
			
			Iterator<Genotype> key_geno_it = key_genos.iterator();
			Iterator<Genotype> test_geno_it = test_genos.iterator();
			
			// Iterate through the genotypes for the samples in this variant context
			while(key_geno_it.hasNext() && test_geno_it.hasNext()){
				Genotype curr_key_geno = key_geno_it.next();
				Genotype curr_test_geno = test_geno_it.next();
				
				// Assert that the genotypes are the same
				Assert.assertTrue(curr_key_geno.compareTo(curr_test_geno)==0);
				Assert.assertArrayEquals(curr_key_geno.getAlleles().toArray(), curr_test_geno.getAlleles().toArray());
				// Assert that the sample has a read depth
//				Assert.assertTrue(curr_test_geno.hasDP());
				// Assert that the sample has a genotype quality score
//				Assert.assertTrue(curr_test_geno.hasGQ());

			}

			// assert that they have the same reference and alternate alleles....
			Assert.assertTrue("Ref-\nkey: " + answer_var.getReference() + " test: " + test_var.getReference() + "\n", answer_var.hasSameAllelesAs(test_var));
			Assert.assertTrue("Alt-\nkey: " + answer_var.getAlternateAlleles().toString() + " test: " + test_var.getAlternateAlleles().toString() + "\n", answer_var.hasSameAlternateAllelesAs(test_var));
			
			
		}
		
		
	}

}
