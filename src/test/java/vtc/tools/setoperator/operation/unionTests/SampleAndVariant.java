/**
 * 
 */
package vtc.tools.setoperator.operation.unionTests;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author Kevin
 * 
 */
public class SampleAndVariant {
	
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";


	private static String hgref = UtilityBelt.getHGREF();

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Sample and Variant Tests                  \n"
				+ "________________________________\n"+RESET);	
	}
	
	/**
	 * Test how the union operator works
	 * @throws IOException 
	 */
	@Test
	public void testUniqueSampleSetsDifferentVariantSets() throws IOException {

		System.out.println(GREEN+"\ntest Unique Sample Sets Different Variant Sets"+RESET);
		
		String in1 = "src/test/resources/test_data/UnionTests/testUniqueSampleSetsDifferentVariantSets/input1.vcf";
		String in2 = "src/test/resources/test_data/UnionTests/testUniqueSampleSetsDifferentVariantSets/input2.vcf";
		String answer = "src/test/resources/test_data/UnionTests/testUniqueSampleSetsDifferentVariantSets/Answer.vcf";
		String out = "src/test/resources/test_data/OUTPUT/union/testUniqueSampleSetsDifferentVariantSets.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		UnionOperationTest.test2files(answer, out);
	}

	/**
	 * Test how the union operator works
	 * @throws IOException 
	 */
	@Test
	public void testOverlappingSampleSameVariantSet() throws IOException {
		
		System.out.println(GREEN+"\ntest Overlapping Sample Same Variant Set"+RESET);
		
		String in1 = "target/test-classes/UnionTests/testOverlappingSampleSameVariantSet/input1.vcf";
		String in2 = "target/test-classes/UnionTests/testOverlappingSampleSameVariantSet/input2.vcf";
		String answer = "target/test-classes/UnionTests/testOverlappingSampleSameVariantSet/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/testOverlappingSampleSameVariantSet.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		UnionOperationTest.test2files(answer, out);

	}

	/**
	 * Test how the union operator works
	 * @throws IOException 
	 */
	@Test
	public void testDifferentAltsAdded() throws IOException {

		System.out.println(GREEN+"\ntest Different Alts Added"+RESET);

		String in1 = "target/test-classes/UnionTests/testDifferentAltsAdded/input1.vcf";
		String in2 = "target/test-classes/UnionTests/testDifferentAltsAdded/input2.vcf";
		String answer = "target/test-classes/UnionTests/testDifferentAltsAdded/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/testDifferentAltsAdded.vcf";

		String arguments = "SO -i var1=" + in2 + " var2=" + in1 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		UnionOperationTest.test2files(answer,out);

	}

	/**
	 * Test how the union operator works
	 * @throws IOException 
	 */
	@Test
	public void testOverlappingSamplesDifferentVariantSets() throws IOException {

		System.out.println(GREEN+"\ntest Overlapping Samples Different Variant Sets"+RESET);
		
		String in1 = "target/test-classes/UnionTests/testOverlappingSamplesDifferentVariantSets/input1.vcf";
		String in2 = "target/test-classes/UnionTests/testOverlappingSamplesDifferentVariantSets/input2.vcf";
		String answer = "target/test-classes/UnionTests/testOverlappingSamplesDifferentVariantSets/Answer.vcf";
		String out = "target/test-classes/OUTPUT/union/testOverlappingSamplesDifferentVariantSets.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -s u[var1:var2] -o " + out;
		
		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		UnionOperationTest.test2files(answer,out);

	}

}
