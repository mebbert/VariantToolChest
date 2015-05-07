package vtc.tools.setoperator.operation.intersectTests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class HomoRef {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Homo Ref Tests                  \n"
				+ "________________________________\n"+RESET);	
	}

	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	
	/**
	 * Test how the intersect operator works for homozygous reference.
	 * @throws IOException 
	 */
	@Ignore
	public void testNoHetOrNoHomoAlt() throws IOException {

		System.out.println(GREEN+"\ntest No Het or No Homo Alt"+RESET);
		
		
		String in1 = "target/test-classes/IntersectTests/Homo_Ref/NoHetOrNoHomoAlt/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Ref/NoHetOrNoHomoAlt/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Ref/NoHetOrNoHomoAlt/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Ref/NoHetOrNoHomoAlt.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_ref -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	
	
	@Test
	public void testBaseTest() throws IOException {

		System.out.println(GREEN+"\ntest Base Test"+RESET);
		
		
		String in1 = "target/test-classes/IntersectTests/Homo_Ref/BaseTest/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Ref/BaseTest/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Ref/BaseTest/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Ref/BaseTest.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_ref -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	@Ignore
	public void testIntersectOperation_HomoRef_Test2() throws IOException {
		
		
		
		
		String in1 = "target/test-classes/IntersectTests/Homo_Ref/Test2/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Ref/Test2/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Ref/Test2/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Ref/i_test2_out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g homo_ref -s i[var1:var2] -o "
				+ out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);

	}
	
}
