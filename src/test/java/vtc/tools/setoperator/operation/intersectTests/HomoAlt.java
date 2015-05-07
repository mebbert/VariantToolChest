package vtc.tools.setoperator.operation.intersectTests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class HomoAlt {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Homo Alt Tests                  \n"
				+ "________________________________\n"+RESET);	
	}
	
	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	
	/**
	 * Test how the intersect operator works for homozygous Alternate.
	 * @throws IOException 
	 */
	@Test
	public void testHomoRef() throws IOException {

		System.out.println(GREEN+"\ntest Homo Ref"+RESET);
		
		
		
		String in1 = "target/test-classes/IntersectTests/Homo_Alt/HomoRef/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Alt/HomoRef/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Alt/HomoRef/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Alt/HomoRef.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	@Test
	public void testDifferentAlts() throws IOException {

		System.out.println(GREEN+"\ntest Different Alts"+RESET);
		
		
		
		String in1 = "target/test-classes/IntersectTests/Homo_Alt/DifferentAlts/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Alt/DifferentAlts/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Alt/DifferentAlts/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Alt/DifferentAlts.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}

	@Test
	public void testUniqueSamples() throws IOException {

		System.out.println(GREEN+"\ntest Unique Samples"+RESET);
		
		
		
		String in1 = "target/test-classes/IntersectTests/Homo_Alt/UniqueSamples/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Alt/UniqueSamples/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Alt/UniqueSamples/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Alt/UniqueSamples.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	
	@Test
	public void testSameSampleDifferentGeno() throws IOException {

		System.out.println(GREEN+"\ntest Same Sample Different Geno"+RESET);
		
		
		
		String in1 = "target/test-classes/IntersectTests/Homo_Alt/SameSampleDifferentGeno/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Alt/SameSampleDifferentGeno/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Alt/SameSampleDifferentGeno/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Alt/SameSampleDifferentGeno.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	
	@Test
	public void testRefOrAltDifferent() throws IOException {

		System.out.println(GREEN+"\ntest Ref or Alt Different"+RESET);
		
		
		
		String in1 = "target/test-classes/IntersectTests/Homo_Alt/RefOrAltDifferent/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Homo_Alt/RefOrAltDifferent/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Homo_Alt/RefOrAltDifferent/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Homo_Alt/RefOrAltDifferent.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -g homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	

	
	
}
