package vtc.tools.miscSetOperTests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class Compare {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n      --Compare Tests           \n"
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
	public void testSameSampleDifferentGenoCompare() throws IOException {

		System.out.println(GREEN+"\ntest Same Sample Different Geno Compare"+RESET);
		
		
		String in1 = "target/test-classes/MiscSetOperTests/Compare/input1.vcf";
		String in2 = "target/test-classes/MiscSetOperTests/Compare/input2.vcf";
		String A_acompb = "target/test-classes/MiscSetOperTests/Compare/Answer.AcompB.vcf";
		String A_bcompa = "target/test-classes/MiscSetOperTests/Compare/Answer.BcompA.vcf";
		String A_intersect = "target/test-classes/MiscSetOperTests/Compare/Answer.intersect.vcf";
		String A_union = "target/test-classes/MiscSetOperTests/Compare/Answer.union.vcf";
		String O_acompb = "target/test-classes/OUTPUT/MiscSetOperTests/Compare/AcompB.out.vcf";
		String O_bcompa= "target/test-classes/OUTPUT/MiscSetOperTests/Compare/BcompA.out.vcf";
		String O_intersect = "target/test-classes/OUTPUT/MiscSetOperTests/Compare/intersect.out.vcf";
		String O_union = "target/test-classes/OUTPUT/MiscSetOperTests/Compare/union.out.vcf";

		String arguments = "SO --compare -i " + in1 + " " + in2 + " -c het_homo_alt -g het_homo_alt -R " + hgref + " -o " +
					"target/test-classes/OUTPUT/MiscSetOperTests/Compare/out.vcf";

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		File output = new File("target/test-classes/OUTPUT/MiscSetOperTests/Compare/");
		if(output.isDirectory()){
			String[] outputs = output.list();
//			for(String s : outputs)
//				System.out.println(s);
			
			// It is 8 not 4 because vtc writes an index file for each vcf.
			assertTrue(outputs.length==8);  
		}
		MiscSetOperTest.test2files(A_acompb, O_acompb);
		System.out.println("AcompB done");
		MiscSetOperTest.test2files(A_bcompa, O_bcompa);
		System.out.println("BcompA done");
		MiscSetOperTest.test2files(A_intersect, O_intersect);
		System.out.println("Intersect done");
		MiscSetOperTest.test2files(A_union, O_union);
		System.out.println("Union done");
	}

		
	
	
	
}
