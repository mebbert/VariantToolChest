/**
 * 
 */
package vtc.tools.varstats;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPoolHeavy;
import vtc.datastructures.VariantPoolLight;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author Kevin
 *
 */


public class VarStatsTest {

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(RED+"========================================================\n"
				+ "                       VarStatsTest                  \n"
				+ "========================================================\n"+RESET);
	}


	@Test
	public void CheckVarStatsValues() throws IOException {
		System.out.println(GREEN+"\nTest Basic Summary Values\n"+RESET);	
		
		
		String input1 = "target/test-classes/SummaryTests/TestBasicSummaryValues/input1.vcf";
		
		ArrayList<String> vcfArgs = new ArrayList<String>();
		TreeMap<String, VariantPoolLight> AllVPs = null;

		vcfArgs.add(input1);

		HashMap<String, VariantPoolSummary> summaries = new HashMap<String, VariantPoolSummary>();

		try {
			try {
				AllVPs = UtilityBelt.createLightVariantPools(vcfArgs, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			summaries = VariantPoolSummarizer.summarizeVariantPools(AllVPs);
			
			DecimalFormat df = new DecimalFormat("0.00");
			
			for (Entry<String, VariantPoolSummary> entry : summaries.entrySet()) {
			    VariantPoolSummary value = entry.getValue();

			    
			    assertEquals(10,value.getNumRecords());
			    assertEquals(9,value.getNumVarRecords());
			    assertEquals(9,value.getNumVars());
			    assertEquals(3,value.getNumSamples());
			    assertEquals(9,value.getNumSNVs());
//			    System.out.println(value.getGenoTiTv());
			    assertEquals(df.format(value.getTiTv()).equals("0.80"),true);
//			    System.out.println(value.getGenoTiTv());
			    assertEquals(df.format(value.getGenoTiTv()).equals("1.23"),true);
			    assertEquals(0,value.getNumMNVs());
			    assertEquals(0,value.getNumIndels());
			    assertEquals(0,value.getNumInsertions());
			    assertEquals(0,value.getNumDeletions());
			    assertEquals(-1,value.getSmallestDeletion());
			    assertEquals(-1,value.getSmallestInsertion());
			    assertEquals(-1.0==value.getAvgDeletionSize(),true);
			    assertEquals(-1.0==value.getAvgInsertionSize(),true);
			    assertEquals(-1,value.getLargestDeletion());
			    assertEquals(-1,value.getLargestInsertion());
			    assertEquals(0,value.getNumStructVars());
			    assertEquals(0,value.getNumStructIns());
			    assertEquals(0,value.getNumStructDels());
			    assertEquals(0,value.getNumMultiAlts());
			}

			
		} catch (InvalidInputFileException e) {
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			e.printStackTrace();
		}
		

	}
	
	
	@Test
	public void CheckIndels() throws IOException {
		System.out.println(GREEN+"\nTest Check InDel Values\n"+RESET);	
		
		
		String input1 = "target/test-classes/SummaryTests/CheckIndels/input1.vcf";
		
		ArrayList<String> vcfArgs = new ArrayList<String>();
		TreeMap<String, VariantPoolLight> AllVPs = null;

		vcfArgs.add(input1);

		HashMap<String, VariantPoolSummary> summaries = new HashMap<String, VariantPoolSummary>();

		try {
			try {
				AllVPs = UtilityBelt.createLightVariantPools(vcfArgs, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			summaries = VariantPoolSummarizer.summarizeVariantPools(AllVPs);
			
			DecimalFormat df = new DecimalFormat("0.00");
			
			for (Entry<String, VariantPoolSummary> entry : summaries.entrySet()) {
			    VariantPoolSummary value = entry.getValue();

			    
			    assertEquals(10,value.getNumRecords());
			    assertEquals(10,value.getNumVarRecords());
			    assertEquals(11,value.getNumVars());
			    assertEquals(3,value.getNumSamples());
			    assertEquals(1,value.getNumSNVs());
//			    System.out.println(value.getGenoTiTv());
			    assertEquals(String.valueOf(value.getTiTv()).equals("NaN"),true);
//			    System.out.println(value.getGenoTiTv());
			    assertEquals(String.valueOf(value.getGenoTiTv()).equals("NaN"),true);
			    assertEquals(0,value.getNumMNVs());
			    assertEquals(10,value.getNumIndels());
			    assertEquals(9,value.getNumInsertions());
			    assertEquals(1,value.getNumDeletions());
			    System.out.println(value.getSmallestDeletion());
			    assertEquals(1,value.getSmallestDeletion());
			    assertEquals(2,value.getSmallestInsertion());
//			    assert(1==Integer.valueOf(String.valueOf(value.getAvgDeletionSize())));
//			    assert(10==Integer.valueOf(String.valueOf(value.getAvgInsertionSize())));
//			    assert(1==value.getLargestDeletion());
//			    assert(70==value.getLargestInsertion());
//			    assertEquals(1,value.getNumStructVars());
//			    assertEquals(1,value.getNumStructIns());
//			    assertEquals(0,value.getNumStructDels());
//			    assertEquals(1,value.getNumMultiAlts());
			}

			
		} catch (InvalidInputFileException e) {
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			e.printStackTrace();
		}
		

	}
}
