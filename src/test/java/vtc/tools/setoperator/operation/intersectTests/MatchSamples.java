package vtc.tools.setoperator.operation.intersectTests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class MatchSamples {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Match Samples Tests                  \n"
				+ "________________________________\n"+RESET);	
	}
	
	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	
	/**
	 * Test how the intersect operator works for matching samples
	 * @throws IOException 
	 */
	@Test
	public void testMismatchingSamples() throws IOException {
		
		
		System.out.println(GREEN+"\ntest Mismatching Samples"+RESET);
		
		
		
		String in1 = "target/test-classes/IntersectTests/Match_Sample/MismatchingSamples/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Match_Sample/MismatchingSamples/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Match_Sample/MismatchingSamples/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Match_Sample/MismatchingSamples.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g match_sample -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer.toString(), out.toString());
		assert (true);
	}
	
	
}
