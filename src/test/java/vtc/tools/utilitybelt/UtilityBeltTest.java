/**
 * 
 */
package vtc.tools.utilitybelt;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPool;
import vtc.tools.setoperator.operation.InvalidOperationException;

/**
 * @author Kevin
 * 
 */

public class UtilityBeltTest {

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	
	private TreeMap<String, VariantPool> AllVPs = new TreeMap<String, VariantPool>();

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(RED+"========================================================\n"
				+ "                       UtilityBeltTest                  \n"
				+ "========================================================\n"+RESET);	
	}


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ArrayList<String> testlist = new ArrayList<String>();

		String file1 = "target/test-classes/1000_genomes_example.vcf";
		String file2 = "target/test-classes/1000_genomes_example-modified.vcf";
		testlist.add(file1);
		testlist.add(file2);

		AllVPs = UtilityBelt.createVariantPools(testlist, true);
	}

	/**
	 * Test method for {@link vtc.tools.utilitybelt.UtilityBelt#roundDouble(double)}.
	 */
	@Test
	public void testRoundDouble() {
		double num = 1.1450239;
		String newnum = UtilityBelt.roundDoubleToString(num);
		assertTrue(newnum.equals("1.15"));
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#createVariantPools(java.util.ArrayList, boolean)}.
	 * 
	 * @throws InvalidOperationException
	 * @throws InvalidInputFileException
	 */
	@Test
	public void testCreateVariantPools() throws InvalidInputFileException, InvalidOperationException {

		ArrayList<String> testlist = new ArrayList<String>();

		String file1 = "target/test-classes/1000_genomes_example.vcf";
		String file2 = "target/test-classes/1000_genomes_example-modified.vcf";
		testlist.add(file1);
		testlist.add(file2);

		TreeMap<String, VariantPool> AllVPs;
		try {
			AllVPs = UtilityBelt.createVariantPools(testlist, true);
			assertTrue(AllVPs.size() == 2);

			testlist.clear();
			AllVPs = UtilityBelt.createVariantPools(testlist, true);
			assertTrue(AllVPs.isEmpty());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#getSmallestLength(java.util.TreeSet)}.
	 */
	@Test
	public void testGetSmallestLength() {
		int smallest = 0;
		TreeSet<String> al = new TreeSet<String>();

		// Common case
		al.clear();
		String[] al2 = { "A", "AT", "TAT ", "AAAA" };
		for (String i : al2) {
			al.add(i);
		}
		smallest = UtilityBelt.getSmallestLength(al);
		assertTrue(smallest == 1);

		// All same lengths
		al.clear();
		String[] al1 = { "ATC", "ATC", "TAT ", "AAA" };
		for (String i : al1) {
			al.add(i);
		}
		smallest = UtilityBelt.getSmallestLength(al);
		assertTrue(smallest == 3);

		// Empty string
		al.clear();
		String[] al3 = { "A", "T", "A", "" };
		for (String i : al3) {
			al.add(i);
		}
		smallest = UtilityBelt.getSmallestLength(al);
		assertTrue(smallest == 0);

		// empty input
		al.clear();
		String[] al4 = {};
		for (String i : al4) {
			al.add(i);
		}
		smallest = UtilityBelt.getSmallestLength(al);
		assertTrue(smallest == -1);

		// null input
		smallest = UtilityBelt.getSmallestLength(null);
		assertTrue(smallest == -1);

		// bad input
		al.clear();
		String[] al5 = { "1425", "...", "%", "2@" };
		for (String i : al5) {
			al.add(i);
		}
		smallest = UtilityBelt.getSmallestLength(al);
		assertTrue(smallest == 1);
	}

	/**
	 * Test method for {@link vtc.tools.utilitybelt.UtilityBelt#getLargestLength(java.util.TreeSet)}
	 * 
	 */
	@Test
	public void testGetLargestLength() {
		int largest = -1;
		TreeSet<String> al = new TreeSet<String>();

		// Common case
		al.clear();
		String[] al2 = { "A", "AT", "TAT ", "AAAAAATTTTCCCCC" };
		for (String i : al2) {
			al.add(i);
		}
		
		largest = UtilityBelt.getLargestLength(al);
		assertTrue("largest=" + largest, largest == 15);

		// All same lengths
		al.clear();
		String[] al1 = { "ATC", "ATC", "TAT", "AAA" };
		for (String i : al1) {
			al.add(i);
		}
		largest = UtilityBelt.getLargestLength(al);
		assertTrue("largest=" + largest, largest == 3);

		// Empty string
		al.clear();
		String[] al3 = { "A", "T", "A", "" };
		for (String i : al3) {
			al.add(i);
		}
		largest = UtilityBelt.getLargestLength(al);
		assertTrue(largest == 1);

		// empty input
		al.clear();
		String[] al4 = {};
		for (String i : al4) {
			al.add(i);
		}
		largest = UtilityBelt.getLargestLength(al);
		assertTrue(largest == -1);

		// null input
		largest = UtilityBelt.getLargestLength(null);
		assertTrue(largest == -1);

		// bad input
		al.clear();
		String[] al5 = { "1425", "...", "%", "2@" };
		for (String i : al5) {
			al.add(i);
		}
		largest = UtilityBelt.getLargestLength(al);
		assertTrue(largest == 4);
	}

	/**
	 * Test method for {@link vtc.tools.utilitybelt.UtilityBelt#getAverageLength(java.util.TreeSet)}
	 * .
	 */
	@Test
	public void testGetAverageLength() {
		double average = -1.0;
		TreeSet<String> al = new TreeSet<String>();

		// All same lengths
		al.clear();
		String[] al1 = { "ATC", "ATC", "TAT", "AAA" };
		for (String i : al1) {
			al.add(i);
		}
		average = UtilityBelt.getAverageLength(al);
		assertTrue("largest=" + average, average == 3);

		// Typical case
		al.clear();
		String[] al2 = { "A", "AT", "TAT", "AAAT", "ATTTA" };
		for (String i : al2) {
			al.add(i);
		}
		average = UtilityBelt.getAverageLength(al);
		assertTrue("largest=" + average, average == 3);

		// empty input
		al.clear();
		String[] al4 = {};
		for (String i : al4) {
			al.add(i);
		}
		average = UtilityBelt.getAverageLength(al);
		assertTrue(average == -1);

		// null input
		average = UtilityBelt.getAverageLength(null);
		assertTrue(average == -1);

	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#determineAltType(org.broadinstitute.variant.variantcontext.Allele, org.broadinstitute.variant.variantcontext.Allele)}
	 * .
	 */
	@Test
	public void testDetermineAltType() {

//		ArrayList<VariantPool> allVPsList = new ArrayList<VariantPool>(AllVPs.values());
//		for (VariantPool vp : allVPsList) {
//			Iterator<String> varIT = vp.getVariantIterator();
//			String currVarKey;
//			while (varIT.hasNext()) {
//				currVarKey = varIT.next();
//				VariantContext var = vp.getVariant(currVarKey);
//				for(Allele a : var.getAlternateAlleles()) {
//					UtilityBelt.determineAltType(var.getReference(), a);
//				}
//			}
//		}
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#altTypeIsIndel(vtc.tools.varstats.AltType)}.
	 */
	@Test
	public void testAltTypeIsIndel() {
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#getDiffCount(org.broadinstitute.variant.variantcontext.Allele, org.broadinstitute.variant.variantcontext.Allele)}
	 * .
	 */
	@Test
	public void testGetDiffCount() {
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#printErrorUsageHelpAndExit(net.sourceforge.argparse4j.inf.ArgumentParser, org.apache.log4j.Logger, java.lang.Exception)}
	 * .
	 */
	@Test
	public void testPrintErrorUsageHelpAndExit() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#printErrorUsageAndExit(net.sourceforge.argparse4j.inf.ArgumentParser, org.apache.log4j.Logger, java.lang.Exception)}
	 * .
	 */
	@Test
	public void testPrintErrorUsageAndExit() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#printUsageHelpAndExit(net.sourceforge.argparse4j.inf.ArgumentParser)}
	 * .
	 */
	@Test
	public void testPrintUsageHelpAndExit() {
		// fail("Not yet implemented");
	}

}
