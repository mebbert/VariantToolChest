package vtc.tools.setoperator.operation.complementTests;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class Alt {
	private static String hgref = UtilityBelt.getHGREF();

//	private static String test_path = "/test_data/IntersectTests";
//	private static String output_path = "/test_data/OUTPUT";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE+ "\n    Alt Tests                  \n"
				+ "________________________________\n"+RESET);	
	}
	
	@Test
	public void defaultTest(){
		assertTrue(true);
	}
	
	
	/**
	 * Test how the complement operator works for complement based on chr, pos, ref, alt.
	 */
	@Test
	public void testSameSampleDifferentNumberVars() {

		System.out.println(GREEN+"\ntest Same Sample Different Number Vars"+RESET);
		
		
		String in1 = "target/test-classes/ComplementTests/Alt/SameSampleDifferentNumberVars/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Alt/SameSampleDifferentNumberVars/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Alt/SameSampleDifferentNumberVars/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Alt/SameSampleDifferentNumberVars.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c alt -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		ComplementOperationTest.test2files(answer, out);
	}	
	
	
	/**
	 * Test how the complement operator works for complement based on chr, pos, ref, alt.
	 */
	@Test
	public void testUniqueSampleDifferentNumberVars() {

		System.out.println(GREEN+"\ntest Unique Sample Different Number Vars"+RESET);
		
		
		String in1 = "target/test-classes/ComplementTests/Alt/UniqueSampleDifferentNumberVars/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Alt/UniqueSampleDifferentNumberVars/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Alt/UniqueSampleDifferentNumberVars/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Alt/UniqueSampleDifferentNumberVars.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c alt -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		ComplementOperationTest.test2files(answer, out);
	}	
	
	@Test
	public void testRefOrAltDifferent() {

		System.out.println(GREEN+"\ntest Ref or Alt Different"+RESET);
		
		
		String in1 = "target/test-classes/ComplementTests/Alt/RefOrAltDifferent/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Alt/RefOrAltDifferent/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Alt/RefOrAltDifferent/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Alt/RefOrAltDifferent.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c alt -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		ComplementOperationTest.test2files(answer, out);
	}	
	
	
	@Test
	public void testDifferentPosOrChr() {

		System.out.println(GREEN+"\ntest Different Pos or Chr"+RESET);
		
		
		String in1 = "target/test-classes/ComplementTests/Alt/DifferentPosOrChr/input1.vcf";
		String in2 = "target/test-classes/ComplementTests/Alt/DifferentPosOrChr/input2.vcf";
		String answer = "target/test-classes/ComplementTests/Alt/DifferentPosOrChr/Answer.vcf";
		String out = "target/test-classes/OUTPUT/Complement/Alt/DifferentPosOrChr.vcf";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R " + hgref
				+ " -c alt -s c[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		ComplementOperationTest.test2files(answer, out);
	}	
	
	
	
	
	
}
