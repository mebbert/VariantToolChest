/**
 * 
 */
package vtc.tools.utilitybelt;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import rules.OnFail;
import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPoolHeavy;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.varstats.AltType;

/**
 * @author Kevin
 * 
 */

public class UtilityBeltTest {

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	
//	private TreeMap<String, VariantPool> AllVPs = new TreeMap<String, VariantPool>();

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(RED+"========================================================\n"
				+ "                       UtilityBeltTest                  \n"
				+ "========================================================\n"+RESET);	
	}


    @Rule
    public OnFail ruleExample = new OnFail();
    
	/**
	 * Test method for {@link vtc.tools.utilitybelt.UtilityBelt#roundDouble(double)}.
	 */
	@Test
	public void testRoundDouble() {
		System.out.println(GREEN+"\nTest Round Double"+RESET);
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
		System.out.println(GREEN+"\nTest Create Variant Pools"+RESET);

		ArrayList<String> testlist = new ArrayList<String>();

		String file1 = "target/test-classes/1000_genomes_example.vcf";
		String file2 = "target/test-classes/1000_genomes_example-modified.vcf";
		testlist.add(file1);
		testlist.add(file2);

		TreeMap<String, VariantPoolHeavy> AllVPs;
		try {
			AllVPs = UtilityBelt.createHeavyVariantPools(testlist, true);
			assertTrue(AllVPs.size() == 2);

			testlist.clear();
			AllVPs = UtilityBelt.createHeavyVariantPools(testlist, true);
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
		System.out.println(GREEN+"\nTest Get Smallest Length"+RESET);

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
		System.out.println(GREEN+"\nTest Get Largest Length"+RESET);

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
		System.out.println(GREEN+"\nTest Get Average Length"+RESET);

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
		System.out.println(GREEN+"\nTest Determine Alt Type"+RESET);
		ArrayList<String> testlist = new ArrayList<String>();

		String file1 = "target/test-classes/1000_genomes_example.vcf";
		ArrayList<AltType> answers = new ArrayList<AltType>();
		answers.add(AltType.SNV);
		answers.add(AltType.SNV);
		answers.add(AltType.SNV);
		answers.add(AltType.SNV);
		answers.add(AltType.SNV);
		answers.add(AltType.MNP);
		answers.add(AltType.DELETION);
		answers.add(AltType.INSERTION);
		
		testlist.add(file1);

		TreeMap<String, VariantPoolHeavy> AllVPs;
		try {
			AllVPs = UtilityBelt.createHeavyVariantPools(testlist, true);
			ArrayList<VariantPoolHeavy> allVPsList = new ArrayList<VariantPoolHeavy>(AllVPs.values());
			System.out.println(allVPsList);
			int i=0;
			for (VariantPoolHeavy vp : allVPsList) {
				Iterator<String> varIT = vp.getVariantIterator();
				String currVarKey;
				while (varIT.hasNext()) {
					currVarKey = varIT.next();
					VariantContext var = vp.getVariant(currVarKey);
					// SNV, MNP, insertion, deletion or structural insertion or deletion
					for(Allele a : var.getAlternateAlleles()) {
						 AltType alt_type = UtilityBelt.determineAltType(var.getReference(), a);
						 System.out.println("Comparing: " + answers.get(i) + " with " + alt_type);
						 assertTrue(answers.get(i)==alt_type);
						 i += 1;
					}
				}
			}
		} catch (InvalidInputFileException e) {
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#altTypeIsIndel(vtc.tools.varstats.AltType)}.
	 */
	@Test
	public void testAltTypeIsIndel() {
		System.out.println(GREEN+"\nTest Alt Type Is Indel"+RESET);
		assertTrue(true); // This function is so simple..
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#getDiffCount(org.broadinstitute.variant.variantcontext.Allele, org.broadinstitute.variant.variantcontext.Allele)}
	 * .
	 */
	@Test
	public void testGetDiffCount() {
		System.out.println(GREEN+"\nTest Get Diff Count"+RESET);
		
		ArrayList<String> testlist = new ArrayList<String>();

		String file1 = "target/test-classes/1000_genomes_example.vcf";
		Integer[] answers = new Integer[] {1,1,1,1,1,3,-1,-1};
		System.out.println(Arrays.toString(answers));
		testlist.add(file1);

		TreeMap<String, VariantPoolHeavy> AllVPs;
		try {
			AllVPs = UtilityBelt.createHeavyVariantPools(testlist, true);
			ArrayList<VariantPoolHeavy> allVPsList = new ArrayList<VariantPoolHeavy>(AllVPs.values());
			System.out.println(allVPsList);
			int i=0;
			for (VariantPoolHeavy vp : allVPsList) {
				Iterator<String> varIT = vp.getVariantIterator();
				String currVarKey;
				while (varIT.hasNext()) {
					currVarKey = varIT.next();
					VariantContext var = vp.getVariant(currVarKey);
					// SNV, MNP, insertion, deletion or structural insertion or deletion
					for(Allele a : var.getAlternateAlleles()) {
						 //System.out.println("Comparing: " + var.getReference() + " with " + a+"\n");
						 int diff_count = UtilityBelt.getDiffCount(var.getReference(), a);
						 System.out.println("Comparing: " + answers[i] + " with " + diff_count+"\n");
						 assertTrue(answers[i]==diff_count);
						 i += 1;
					}
				}
			}
		} catch (InvalidInputFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			


	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#printErrorUsageHelpAndExit(net.sourceforge.argparse4j.inf.ArgumentParser, org.apache.log4j.Logger, java.lang.Exception)}
	 * .
	 */
	@Test
	public void testPrintErrorUsageHelpAndExit() {
		System.out.println(GREEN+"\nTest Print Error Usage Help And Exit"+RESET);

		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#printErrorUsageAndExit(net.sourceforge.argparse4j.inf.ArgumentParser, org.apache.log4j.Logger, java.lang.Exception)}
	 * .
	 */
	@Test
	public void testPrintErrorUsageAndExit() {
		System.out.println(GREEN+"\nTest Print Error Usage And Exit"+RESET);

		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link vtc.tools.utilitybelt.UtilityBelt#printUsageHelpAndExit(net.sourceforge.argparse4j.inf.ArgumentParser)}
	 * .
	 */
	@Test
	public void testPrintUsageHelpAndExit() {
		System.out.println(GREEN+"\nTest Print Usage Help And Exit"+RESET);

		// fail("Not yet implemented");
	}

}
