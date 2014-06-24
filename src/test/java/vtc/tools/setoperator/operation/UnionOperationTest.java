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

import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.Assert;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.datastructures.VariantPool;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author Kevin
 * 
 */
public class UnionOperationTest {

	private static String hgref = UtilityBelt.getHGREF();

	/**
	 * Test how the union operator works
	 */
	@Test
	public void testUnionOperation1() {

		String in1 = "src/test/resources/test_data/UnionTests/Test1/input1.vcf";
		String in2 = "src/test/resources/test_data/UnionTests/Test1/input2.vcf";
		String answer = "src/test/resources/test_data/UnionTests/Test1/Answer.vcf";
		String out = "src/test/resources/test_data/OUTPUT/union/u_test1_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);
	}

	/**
	 * Test how the union operator works
	 */
	@Test
	public void testUnionOperation2() {
		String in1 = "target/test-classes/UnionTests/Test2/input1.vcf";
		String in2 = "target/test-classes/UnionTests/Test2/input2.vcf";
		String answer = "target/test-classes/UnionTests/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/u_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer, out);

	}

	/**
	 * Test how the union operator works
	 */
	@Test
	public void testUnionOperation3() {
		String in1 = "target/test-classes/UnionTests/Test3/input1.vcf";
		String in2 = "target/test-classes/UnionTests/Test3/input2.vcf";
		String answer = "target/test-classes/UnionTests/Test3/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/u_test3_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer,out);

	}

	/**
	 * Test how the union operator works
	 */
	@Test
	public void testUnionOperation4() {
		String in1 = "target/test-classes/UnionTests/Test4/input1.vcf";
		String in2 = "target/test-classes/UnionTests/Test4/input2.vcf";
		String answer = "target/test-classes/UnionTests/Test4/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/u_test4_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		this.test2files(answer,out);

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

		//assertEquals(key_pool, test_pool);

		 //make sure they have same number of variants..
		 assertEquals(key_pool.getNumVarRecords(),test_pool.getNumVarRecords());
		
		 //make sure they have the same number of samples.
		 assertEquals(key_pool.getSamples().size(),test_pool.getSamples().size());
		
		 //Iterate over variants in the key and check if they are equal...
		 while(key_it.hasNext() && test_it.hasNext()){
			 			
			 currVarKey = key_it.next();
			 currVarTest = test_it.next();
			
			 var_key = key_pool.getVariant(currVarKey);
			 var_test = test_pool.getVariant(currVarTest);
			 
			 //assert that they have the same reference and alternate alleles....
			 Assert.assertTrue(var_key.hasSameAllelesAs(var_test));
			 Assert.assertTrue(var_key.hasSameAlternateAllelesAs(var_test));
			 Assert.assertTrue(0==var_key.getGenotype(0).compareTo(var_test.getGenotype(0)));
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
