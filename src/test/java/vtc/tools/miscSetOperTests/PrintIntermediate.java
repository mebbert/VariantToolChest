package vtc.tools.miscSetOperTests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class PrintIntermediate {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n      Print Intermediate Tests           \n"
				+ "______________________________________\n"+RESET);	
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

		System.out.println(GREEN+"\ntest Same Sample Different Geno Compare"+RESET);
		
		
		String in1 = "target/test-classes/MiscSetOperTests/PrintIntermediate/input1.vcf";
		String in2 = "target/test-classes/MiscSetOperTests/PrintIntermediate/input2.vcf";
		String A_acompb = "target/test-classes/MiscSetOperTests/PrintIntermediate/Answer.AcompB.vcf";
		String A_intersect = "target/test-classes/MiscSetOperTests/PrintIntermediate/Answer.intersect.vcf";
		String O_acompb = "target/test-classes/OUTPUT/MiscSetOperTests/PrintIntermediate/out1.out.vcf";
		String O_intersect = "target/test-classes/OUTPUT/MiscSetOperTests/PrintIntermediate/inter.out.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -c het_homo_alt -g het_homo_alt -s out1=c[var1:var2] out2=i[out1:var2] -R "
					+ hgref + " -o " + O_intersect + " -I";

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		MiscSetOperTest.test2files(A_acompb, O_acompb);
		System.out.println("AcompB done");
		MiscSetOperTest.test2files(A_intersect, O_intersect);
		System.out.println("Intersect done");
	}	
	
	
	@Ignore
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

		MiscSetOperTest.test2files(answer, out);
	}	
	
	
	@Ignore
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

		MiscSetOperTest.test2files(answer, out);
	}	
	
	/**
	 * Test how the complement operator works for the exact option.
	 * @throws IOException 
	 */
	@Ignore
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

		MiscSetOperTest.test2files(answer, out);
	}	
	
}
