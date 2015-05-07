package vtc.tools.miscSetOperTests;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class NoCall {
	private static String hgref = UtilityBelt.getHGREF();

	private static String test_path = "/test_data/IntersectTests";
	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n      No Call Tests           \n"
				+ "______________________________________\n"+RESET);	
	}
	
	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	
	/**
	 * Test how the complement operator works for the exact option.
	 */
	@Test
	public void testNoCallComplement() {

		System.out.println(GREEN+"\ntest No Call complement"+RESET);
		
		
		String in1 = "target/test-classes/MiscSetOperTests/NoCall/input1.vcf";
		String in2 = "target/test-classes/MiscSetOperTests/NoCall/input2.vcf";
		String A_acompb = "target/test-classes/MiscSetOperTests/NoCall/Answer.AcompB.vcf";
		String O_acompb = "target/test-classes/OUTPUT/MiscSetOperTests/NoCall/O_AcompB.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -c het_homo_alt -s out1=c[var1:var2] -R "
					+ hgref + " -o " + O_acompb + " -r";

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		MiscSetOperTest.test2files(A_acompb, O_acompb);
		System.out.println("AcompB done");
	}	
	
	@Test
	public void testNoCallIntersect() {

		System.out.println(GREEN+"\ntest No Call intersect"+RESET);
		
		
		String in1 = "target/test-classes/MiscSetOperTests/NoCall/input1.vcf";
		String in2 = "target/test-classes/MiscSetOperTests/NoCall/input2.vcf";
		String A_intersect = "target/test-classes/MiscSetOperTests/NoCall/Answer.intersect.vcf";
		String O_intersect = "target/test-classes/OUTPUT/MiscSetOperTests/NoCall/O_intersect.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -g het_homo_alt -s out1=i[var1:var2] -R "
					+ hgref + " -o " + O_intersect + " -r";

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		MiscSetOperTest.test2files(A_intersect, O_intersect);
		System.out.println("AcompB done");
	}	
	
}
