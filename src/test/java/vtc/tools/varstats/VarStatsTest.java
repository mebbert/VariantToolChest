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
			    assertEquals(0,value.getIndelCount());
			    assertEquals(0,value.getInsCount());
			    assertEquals(0,value.getDelCount());
			    assertEquals(0,value.getSmallestDel());
			    assertEquals(0,value.getSmallestIns());
			    assertEquals(0,(int)value.getAvgDel());
			    assertEquals(0,(int)value.getAvgIns());
			    assertEquals(0,value.getLargestDel());
			    assertEquals(0,value.getLargestIns());
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
			    assertEquals(9,value.getIndelCount());
			    assertEquals(8,value.getInsCount());
			    assertEquals(1,value.getDelCount());
//			    System.out.println(value.getSmallestDel());
//			    System.out.println(value.getNumStructVars());
//			    System.out.println(value.getIndelCount());
			    assertEquals(1,value.getSmallestDel());
			    assertEquals(1,value.getSmallestIns());
			    assertEquals(UtilityBelt.roundDoubleToString(value.getAvgDel()).equals("1"),true);
			    assertEquals(UtilityBelt.roundDoubleToString(value.getAvgIns()).equals("2"),true);
			    assertEquals(1,value.getLargestDel());
			    assertEquals(5,value.getLargestIns());
			    assertEquals(1,value.getNumStructVars());
			    assertEquals(1,value.getNumStructIns());
			    assertEquals(0,value.getNumStructDels());
			    assertEquals(0,value.getSmallestStructDel());
			    assertEquals(0,value.getLargestStructDel());
			    assertEquals(192,value.getSmallestStructIns());
			    assertEquals(192,value.getLargestStructIns());
			    assertEquals(192,(int)value.getAvgStructIns());
			    assertEquals(0,(int)value.getAvgStructDel());
			    assertEquals(1,value.getNumMultiAlts());
			}

			
		} catch (InvalidInputFileException e) {
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			e.printStackTrace();
		}
		

	}
	
	
	@Test
	public void TestNoCallGeno() throws IOException {
		System.out.println(GREEN+"\nTest No Call Genotypes\n"+RESET);	
		
		
		String input1 = "target/test-classes/SummaryTests/TestNoCallGeno/input1.vcf";
		
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
			    assertEquals(df.format(value.getGenoTiTv()).equals("1.17"),true);
			    assertEquals(0,value.getNumMNVs());
			    assertEquals(0,value.getIndelCount());
			    assertEquals(0,value.getInsCount());
			    assertEquals(0,value.getDelCount());
//			    System.out.println(value.getSmallestDel());
//			    System.out.println(value.getNumStructVars());
//			    System.out.println(value.getIndelCount());
			    assertEquals(0,value.getSmallestDel());
			    assertEquals(0,value.getSmallestIns());
			    assertEquals(0,(int)value.getAvgDel());
			    assertEquals(0,(int)value.getAvgIns());
			    assertEquals(0,value.getLargestDel());
			    assertEquals(0,value.getLargestIns());
			    assertEquals(0,value.getNumStructVars());
			    assertEquals(0,value.getNumStructIns());
			    assertEquals(0,value.getNumStructDels());
			    assertEquals(0,value.getSmallestStructDel());
			    assertEquals(0,value.getLargestStructDel());
			    assertEquals(0,value.getSmallestStructIns());
			    assertEquals(0,value.getLargestStructIns());
			    assertEquals(0,(int)value.getAvgStructIns());
			    assertEquals(0,(int)value.getAvgStructDel());
			    assertEquals(0,value.getNumMultiAlts());
			}

			
		} catch (InvalidInputFileException e) {
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			e.printStackTrace();
		}
		

	}
	
	
	
	@Test
	public void TestMNVsAndMulitAlts() throws IOException {
		System.out.println(GREEN+"\nTest MNVs and Multi Alts\n"+RESET);	
		
		
		String input1 = "target/test-classes/SummaryTests/TestMNVsAndMultiAlts/input1.vcf";
		
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
			    assertEquals(11,value.getNumVars());
			    assertEquals(3,value.getNumSamples());
			    assertEquals(5,value.getNumSNVs());
//			    System.out.println(value.getGenoTiTv());
			    assertEquals(df.format(value.getTiTv()).equals("0.67"),true);
//			    System.out.println(value.getGenoTiTv());
			    assertEquals(df.format(value.getGenoTiTv()).equals("0.78"),true);
			    assertEquals(3,value.getNumMNVs());
			    assertEquals(3,value.getIndelCount());
			    assertEquals(1,value.getInsCount());
			    assertEquals(2,value.getDelCount());
//			    System.out.println(value.getSmallestDel());
//			    System.out.println(value.getNumStructVars());
//			    System.out.println(value.getIndelCount());
			    assertEquals(1,value.getSmallestDel());
			    assertEquals(1,value.getSmallestIns());
			    assertEquals(1,(int)value.getAvgDel());
			    assertEquals(1,(int)value.getAvgIns());
			    assertEquals(1,value.getLargestDel());
			    assertEquals(1,value.getLargestIns());
			    assertEquals(0,value.getNumStructVars());
			    assertEquals(0,value.getNumStructIns());
			    assertEquals(0,value.getNumStructDels());
			    assertEquals(0,value.getSmallestStructDel());
			    assertEquals(0,value.getLargestStructDel());
			    assertEquals(0,value.getSmallestStructIns());
			    assertEquals(0,value.getLargestStructIns());
			    assertEquals(0,(int)value.getAvgStructIns());
			    assertEquals(0,(int)value.getAvgStructDel());
			    assertEquals(2,value.getNumMultiAlts());
			}

			
		} catch (InvalidInputFileException e) {
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			e.printStackTrace();
		}
		

	}
}
