package vtc.tools.setoperator.operation.intersectTests;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class Alt {
	private static String hgref = UtilityBelt.getHGREF();

	private static String test_path = "/test_data/IntersectTests";
	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Alt Tests                  \n"
				+ "________________________________\n"+RESET);	
	}
	
	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	
	/**
	 * Test how the intersect operator works for chr, pos, ref, alt.
	 */
	@Test
	public void testDifferentPosAndChr() {

		System.out.println(GREEN+"\ntest Different Pos and Chr"+RESET);
		
		
		
		String in1 = "target/test-classes/IntersectTests/Alt/DifferentPosAndChr/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Alt/DifferentPosAndChr/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Alt/DifferentPosAndChr/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Alt/DifferentPosAndChr.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	@Test
	public void testRefOrAltDifferent() {

		System.out.println(GREEN+"\ntest Ref or Alt Different"+RESET);
		
		
		String in1 = "target/test-classes/IntersectTests/Alt/RefOrAltDifferent/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Alt/RefOrAltDifferent/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Alt/RefOrAltDifferent/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Alt/RefOrAltDifferent.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g alt -s i[var1:var2] -o "
				+ out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);

	}
	
}
