package vtc.tools.miscSetOperTests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class SamplesWithinSameFile {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n      Samples Within Same File Tests           \n"
				+ "____________________________________________\n"+RESET);	
	}
	
	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	
	/**
	 * @throws IOException 
	 */
	@Test
	public void test2sampleswithinsamefile() throws IOException {

		System.out.println(GREEN+"\ntest 2 samples in var1 and 1 in var2"+RESET);
		
		
		String in1 = "target/test-classes/MiscSetOperTests/SamplesWithinSameFile/input1.vcf";
		String in2 = "target/test-classes/MiscSetOperTests/SamplesWithinSameFile/input2.vcf";
		String A_acompb = "target/test-classes/MiscSetOperTests/SamplesWithinSameFile/Answer.AcompB.vcf";
		String O_acompb = "target/test-classes/OUTPUT/MiscSetOperTests/SamplesWithinSameFile/O_AcompB.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -c het_homo_alt -s out1=c[var1[NA00001,NA00002]:var2[NA00004]] -R "
					+ hgref + " -o " + O_acompb;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		MiscSetOperTest.test2files(A_acompb, O_acompb);
		System.out.println("AcompB done");
	}	
	
	/**
	 * @throws IOException 
	 */
	@Test
	public void testsampleswithinsamefile() throws IOException {

		System.out.println(GREEN+"\ntest 1 vs 2 other samples in same file"+RESET);
		
		
		String in1 = "target/test-classes/MiscSetOperTests/SamplesWithinSameFile/input1.vcf";
		String A_acompb = "target/test-classes/MiscSetOperTests/SamplesWithinSameFile/Answer.AcompB.vcf";
		String O_acompb = "target/test-classes/OUTPUT/MiscSetOperTests/SamplesWithinSameFile/O_AcompB2.vcf";

		String arguments = "SO -i var1=" + in1 + " -c het_homo_alt -s out1=c[var1[NA00001,NA00002]:var1[NA00003]] -R "
					+ hgref + " -o " + O_acompb;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		MiscSetOperTest.test2files(A_acompb, O_acompb);
		System.out.println("AcompB done");
	}	
	
}
