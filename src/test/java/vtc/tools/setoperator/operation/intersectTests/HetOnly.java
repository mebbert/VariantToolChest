package vtc.tools.setoperator.operation.intersectTests;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class HetOnly {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Het Only Tests                  \n"
				+ "________________________________\n"+RESET);	
	}
	
	
	@Test	
	public void testHomoRef() throws IOException {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		System.out.println(GREEN+"\ntest Homo Ref"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Only/HomoRef/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Only/HomoRef/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Only/HomoRef/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Only/HomoRef.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	@Test	
	public void testHomoAlt() throws IOException {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		System.out.println(GREEN+"\ntest Homo Alt"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Only/HomoAlt/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Only/HomoAlt/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Only/HomoAlt/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Only/HomoAlt.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	@Test
	public void testMultipleAlts() throws IOException {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		System.out.println(GREEN+"\ntest Multiple Alts"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Only/MultipleAlts/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Only/MultipleAlts/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Only/MultipleAlts/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Only/MultipleAlts.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
		
	}
	
	@Test	
	public void testUniqueSamples() throws IOException {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		System.out.println(GREEN+"\ntest Unique Samples"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Only/UniqueSamples/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Only/UniqueSamples/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Only/UniqueSamples/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Only/UniqueSamples.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}
	
	@Test
	public void testSameSampleDifferentGeno() throws IOException {
		//String see = getClass().getResource("/MainConfig.xml").getFile();
		//System.out.println("got: " + see);
		
		System.out.println(GREEN+"\ntest Same Sample Different Geno"+RESET);
		
		String in1 = "target/test-classes/IntersectTests/Het_Only/SameSampleDifferentGeno/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Het_Only/SameSampleDifferentGeno/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Het_Only/SameSampleDifferentGeno/Answer.vcf";
		String out = "target/test-classes/OUTPUT/intersect/Het_Only/SameSampleDifferentGeno.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref + " -g het -s i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer, out);
	}

}
