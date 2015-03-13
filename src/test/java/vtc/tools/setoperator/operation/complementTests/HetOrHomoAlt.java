package vtc.tools.setoperator.operation.complementTests;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class HetOrHomoAlt {
	private static String hgref = UtilityBelt.getHGREF();

	private static String test_path = "/test_data/IntersectTests";
	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Het or Homo Alt Tests                  \n"
				+ "________________________________\n"+RESET);	
	}
	
	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	

	/**
	 * Test how the complement operator works for homozygous Alternate.
	 */
	@Ignore
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

		ComplementOperationTest.test2files(answer, out);
	}
	
	/**
	 * Test how the complement operator works for homozygous Alternate.
	 */
	@Ignore
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

		ComplementOperationTest.test2files(answer, out);
	}	
	
}
