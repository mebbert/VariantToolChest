package vtc.tools.miscSetOperTests;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class RepairHeader {
	private static String hgref = UtilityBelt.getHGREF();

	private static String test_path = "/test_data/IntersectTests";
	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n      Repair Header Tests           \n"
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
	public void testOneInfoOneFormatMissing() {

		System.out.println(GREEN+"\ntest 1 info and one format and both filter headings missing"+RESET);
		
		
		String in1 = "target/test-classes/MiscSetOperTests/RepairHeader/input1.vcf";
		String in2 = "target/test-classes/MiscSetOperTests/RepairHeader/input2.vcf";
		String A_acompb = "target/test-classes/MiscSetOperTests/RepairHeader/Answer.AcompB.vcf";
		String O_acompb = "target/test-classes/OUTPUT/MiscSetOperTests/RepairHeader/O_AcompB.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -c het_homo_alt -g het_homo_alt -s out1=c[var1:var2] -R "
					+ hgref + " -o " + O_acompb + " -r";

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		MiscSetOperTest.test2files(A_acompb, O_acompb);
		System.out.println("AcompB done");
	}	
	
	
}
