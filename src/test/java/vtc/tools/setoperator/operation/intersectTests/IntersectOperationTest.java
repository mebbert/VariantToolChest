package vtc.tools.setoperator.operation.intersectTests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import vtc.datastructures.VariantPoolHeavy;



@RunWith(Suite.class)
@Suite.SuiteClasses({
   HetOnly.class,
   HetOrHomoAlt.class,
   HomoRef.class,
   Pos.class,
   Alt.class,
   HomoAlt.class,
//   MatchSamples.class
})
public class IntersectOperationTest {
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(RED+"\n==========================================================================\n"
				+ "                       IntersectOperationTest                  \n"
				+ "==========================================================================\n"+RESET);	
	}
	
	
	public static void test2files(String answer, String out) {
		// Create key and program response answers
		VariantPoolHeavy key_pool = null;
		VariantPoolHeavy test_pool = null;
		try {
			test_pool = new VariantPoolHeavy(new File(out), "2", false);
			key_pool = new VariantPoolHeavy(new File(answer), "1", false);
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
		Assert.assertArrayEquals(key_pool.getSamples().toArray(),test_pool.getSamples().toArray());

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
				Assert.assertArrayEquals(curr_key_geno.getAlleles().toArray(), curr_test_geno.getAlleles().toArray());
				//PRINT THE ARRAYS HERE TO DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				
				// Assert that the sample has a read depth
//				Assert.assertTrue(curr_test_geno.hasDP());
				// Assert that the sample has a genotype quality score
//				Assert.assertTrue(curr_test_geno.hasGQ());

			}

			// assert that they have the same reference and alternate alleles....
			Assert.assertTrue("Ref-\nkey: " + var_key.getReference() + " test: " + var_test.getReference() + "\n", var_key.hasSameAllelesAs(var_test));
			Assert.assertTrue("Alt-\nkey: " + var_key.getAlternateAlleles().toString() + " test: " + var_test.getAlternateAlleles().toString() + "\n", var_key.hasSameAlternateAllelesAs(var_test));
			
			
		}
		
		
	}

}
