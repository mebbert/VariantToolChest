package vtc.tools.setoperator.operation.intersectTests;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;


public class HetOrHomoAlt {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

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
	public void testHomoRef() {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		System.out.println(GREEN+"\ntest Homo Ref"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/HomoRef/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/HomoRef/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/HomoRef/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/HomoRef.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het_homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	

	
	@Test
	public void testMultipleAlts() {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		System.out.println(GREEN+"\ntest Multiple Alts"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/MultipleAlts/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/MultipleAlts/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/MultipleAlts/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/MultipleAlts.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het_homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
		
	}
	
	@Test	
	public void testUniqueSamples() {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		System.out.println(GREEN+"\ntest Unique Samples"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/UniqueSamples/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/UniqueSamples/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/UniqueSamples/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/UniqueSamples.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het_homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	@Test
	public void testSameSampleDifferentGeno() {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		System.out.println(GREEN+"\ntest Same Sample Different Geno"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/SameSampleDifferentGeno/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/SameSampleDifferentGeno/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/SameSampleDifferentGeno/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/SameSampleDifferentGeno.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het_homo_alt -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}	
	
	
	
	@Test
	public void testRefOrAltDifferent() {

		System.out.println(GREEN+"\ntest Ref or Alt Different"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/RefOrAltDifferent/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/RefOrAltDifferent/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Or_Homo_Alt/RefOrAltDifferent/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Or_Homo_Alt/RefOrAltDifferent.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het_homo_alt -s i[var1:var2] -o "
				+ out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);

	}
	

}
