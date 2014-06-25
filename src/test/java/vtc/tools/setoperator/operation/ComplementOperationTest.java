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
import org.junit.Test;

import vtc.datastructures.VariantPool;
import vtc.tools.setoperator.operation.IntersectOperationTest.Test_Params;
import vtc.tools.utilitybelt.UtilityBelt;

public class ComplementOperationTest {

	@Test
	public void test() {
		assertTrue(true);
	}

	

	private static String hgref = UtilityBelt.getHGREF();

	private static String test_path = "/test_data/IntersectTests";
	private static String output_path = "/test_data/OUTPUT";

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
