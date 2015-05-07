package vtc.tools.setoperator.operation.complementTests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class Exact {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Exact Tests                  \n"
				+ "________________________________\n"+RESET);	
	}
	
	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	
	/**
	 * Test how the complement operator works for the exact option.
	 * @throws IOException 
	 */
	@Test
	public void testSameSampleDifferentGeno() throws IOException {

		System.out.println(GREEN+"\ntest Same Sample Different Geno"+RESET);
		
		
		String in1 = "target/test-classes/ComplementTests/Exact/SameSampleDifferentGeno/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Exact/SameSampleDifferentGeno/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Exact/SameSampleDifferentGeno/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Exact/SameSampleDifferentGeno.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c exact -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		ComplementOperationTest.test2files(answer, out);
	}	
	
	
	@Test
	public void testDifferentPosOrChr() throws IOException {

		System.out.println(GREEN+"\ntest Different Pos or Chr"+RESET);
		
		
		String in1 = "target/test-classes/ComplementTests/Exact/DifferentPosOrChr/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Exact/DifferentPosOrChr/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Exact/DifferentPosOrChr/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Exact/DifferentPosOrChr.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c exact -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		ComplementOperationTest.test2files(answer, out);
	}	
	
	
	@Test
	public void testRefOrAltDifferent() throws IOException {

		System.out.println(GREEN+"\ntest Ref or Alt Different"+RESET);
		
		
		String in1 = "target/test-classes/ComplementTests/Exact/RefOrAltDifferent/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Exact/RefOrAltDifferent/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Exact/RefOrAltDifferent/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Exact/RefOrAltDifferent.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c exact -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		ComplementOperationTest.test2files(answer, out);
	}	
	
	/**
	 * Test how the complement operator works for the exact option.
	 * @throws IOException 
	 */
	@Test
	public void testExactGenotypeTest() throws IOException {
		System.out.println(GREEN+"\ntest Exact Genotype Test"+RESET);

		// ///////////Test1//////////////
		String in1 = "target/test-classes/ComplementTests/Exact/ExactGenotypeTest/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Exact/ExactGenotypeTest/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Exact/ExactGenotypeTest/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Exact/ExactGenotypeTest.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c exact -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		ComplementOperationTest.test2files(answer, out);
	}	
	
}
