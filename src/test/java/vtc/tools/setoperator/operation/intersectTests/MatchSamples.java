package vtc.tools.setoperator.operation.intersectTests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import vtc.VTCEngine;
import vtc.tools.utilitybelt.UtilityBelt;

public class MatchSamples {
	private static String hgref = UtilityBelt.getHGREF();


	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(BLUE
				+ "\n    Match Samples Tests                  \n"
				+ "________________________________\n" + RESET);
	}

	
	@Test
	public void testMismatchingSamples() throws IOException {

		System.out.println(GREEN + "\ntest Mismatching Samples" + RESET);

		String in1 = "target/test-classes/IntersectTests/Match_Sample/MismatchingSamples/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Match_Sample/MismatchingSamples/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Match_Sample/MismatchingSamples/Answer.vcf";
		String StatAnswer = "target/test-classes/IntersectTests/Match_Sample/MismatchingSamples/StatAnswer.txt";
		String out = "target/test-classes/OUTPUT/intersect/Match_Sample/MismatchingSamples.vcf";
		String StatOut = "target/test-classes/OUTPUT/intersect/Match_Sample/out1_MatchSampleStats.txt";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R "
				+ hgref + " -g match_sample -s out1=i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer.toString(), out.toString());
		IntersectOperationTest.testMatchSampleFiles(StatAnswer, StatOut);
	}

	@Test
	public void testDifferentAlts() throws IOException {

		System.out.println(GREEN + "\ntest Different Alts" + RESET);

		String in1 = "target/test-classes/IntersectTests/Match_Sample/DifferentAlts/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Match_Sample/DifferentAlts/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Match_Sample/DifferentAlts/Answer.vcf";
		String StatAnswer = "target/test-classes/IntersectTests/Match_Sample/DifferentAlts/StatAnswer.txt";
		String out = "target/test-classes/OUTPUT/intersect/Match_Sample/DifferentAlts.vcf";
		String StatOut = "target/test-classes/OUTPUT/intersect/Match_Sample/out2_MatchSampleStats.txt";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R "
				+ hgref + " -g match_sample -s out2=i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer.toString(), out.toString());
		IntersectOperationTest.testMatchSampleFiles(StatAnswer, StatOut);
	}

	@Test
	public void testRefOrAltDifferent() throws IOException {

		System.out.println(GREEN + "\ntest when Ref or Alts are Different" + RESET);

		String in1 = "target/test-classes/IntersectTests/Match_Sample/RefOrAltDifferent/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Match_Sample/RefOrAltDifferent/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Match_Sample/RefOrAltDifferent/Answer.vcf";
		String StatAnswer = "target/test-classes/IntersectTests/Match_Sample/RefOrAltDifferent/StatAnswer.txt";
		String out = "target/test-classes/OUTPUT/intersect/Match_Sample/RefOrAltDifferent.vcf";
		String StatOut = "target/test-classes/OUTPUT/intersect/Match_Sample/out3_MatchSampleStats.txt";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R "
				+ hgref + " -g match_sample -s out3=i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer.toString(), out.toString());
		IntersectOperationTest.testMatchSampleFiles(StatAnswer, StatOut);
	}

	@Test
	public void testSameAltDifferentHet() throws IOException {

		System.out.println(GREEN + "\ntest Same alt but different het" + RESET);

		String in1 = "target/test-classes/IntersectTests/Match_Sample/SameAltDifferentHet/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Match_Sample/SameAltDifferentHet/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Match_Sample/SameAltDifferentHet/Answer.vcf";
		String StatAnswer = "target/test-classes/IntersectTests/Match_Sample/SameAltDifferentHet/StatAnswer.txt";
		String out = "target/test-classes/OUTPUT/intersect/Match_Sample/SameAltDifferentHet.vcf";
		String StatOut = "target/test-classes/OUTPUT/intersect/Match_Sample/out4_MatchSampleStats.txt";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R "
				+ hgref + " -g match_sample -s out4=i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer.toString(), out.toString());
		IntersectOperationTest.testMatchSampleFiles(StatAnswer, StatOut);
	}
	
	
	@Test
	public void testDifferentVarsSameSamples() throws IOException {

		System.out.println(GREEN + "\ntest Different Vars Same Samples" + RESET);

		String in1 = "target/test-classes/IntersectTests/Match_Sample/DifferentVarsSameSamples/input1.vcf";
		String in2 = "target/test-classes/IntersectTests/Match_Sample/DifferentVarsSameSamples/input2.vcf";
		String answer = "target/test-classes/IntersectTests/Match_Sample/DifferentVarsSameSamples/Answer.vcf";
		String StatAnswer = "target/test-classes/IntersectTests/Match_Sample/DifferentVarsSameSamples/StatAnswer.txt";
		String out = "target/test-classes/OUTPUT/intersect/Match_Sample/DifferentVarsSameSamples.vcf";
		String StatOut = "target/test-classes/OUTPUT/intersect/Match_Sample/out5_MatchSampleStats.txt";

		String arguments = "SO -i var1=" + in1 + " var2=" + in2 + " -R "
				+ hgref + " -g match_sample -s out5=i[var1:var2] -o " + out;

		String[] args = arguments.split(" ");
		VTCEngine.main(args);

		IntersectOperationTest.test2files(answer.toString(), out.toString());
		IntersectOperationTest.testMatchSampleFiles(StatAnswer, StatOut);
	}
	
}
