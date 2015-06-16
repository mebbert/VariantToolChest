/**
 * 
 */
package vtc.tools.varstats;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import vtc.VTCEngine;
import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPoolHeavy;
import vtc.datastructures.VariantPoolLight;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.utilitybelt.UtilityBelt;

/**
 * @author Mark
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
	
	@Test
	public void testIndDetailedSummary() throws IOException{	
		System.out.println(GREEN+"\nTest Individual Dedailed Summary\n"+RESET);	
		
		
		String input1 = "target/test-classes/OUTPUT/Varstats/IndVsCombined/input1.vcf";
		String indOut = "target/test-classes/OUTPUT/Varstats/IndVsCombined/indTest";
		String Answer = "target/test-classes/OUTPUT/Varstats/IndVsCombined/Answer_detailed_summary.txt";
		String arguments = "VS -i var1="+input1+" -d I -o "+indOut;
		String[] args = arguments.split(" ");
		VTCEngine.main(args);
		
	//	List<String> infiles = new ArrayList<String>();
	//	infiles.add(input1);
	//	String indID = "";
	//	try {
	//		
	//		TreeMap<String, VariantPoolLight> VPl = UtilityBelt.createLightVariantPools(infiles, true);
	//		indID = (String) VPl.keySet().toArray()[0];
	//		System.out.println(indID+"\t"+VPl.size());
	//		VariantPoolSummarizer.summarizeVariantPoolsDetailed(VPl, indOut);
	//		
	//	} catch (InvalidInputFileException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	} catch (InvalidOperationException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}

		Compare2DetailedSummaryFiles(Answer, indOut+"_var1_detailed_summary.txt");
//		Compare2DetailedSummaryFiles(Answer, indOut+"_"+indID+"_detailed_summary.txt");
	}
	
	@Test
	public void testIndDetailedCombinedSummaryOneFile() throws IOException{	
		System.out.println(GREEN+"\nTest Combined Detailed Summary on One File\n"+RESET);	
		
		
		String input1 = "target/test-classes/SummaryTests/CheckIndels/input1.vcf";
		String combinedOut = "target/test-classes/OUTPUT/Varstats/IndVsCombined/combTest";
		String Answer = "target/test-classes/OUTPUT/Varstats/IndVsCombined/Answer_detailed_summary.txt";
		
		List<String> infiles = new ArrayList<String>();
		infiles.add(input1);
		String combID = "";
		try {
				
			TreeMap<String,VariantPoolHeavy> VPs = UtilityBelt.createHeavyVariantPools(infiles,false);
			combID = (String) VPs.keySet().toArray()[0];
			System.out.println(combID);
			VariantPoolSummarizer.summarizeVariantPoolsDetailedCombined(VPs, combinedOut);
			
		} catch (InvalidInputFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Compare2DetailedSummaryFiles(combinedOut+"_"+combID+"_detailed_summary.txt", Answer);
	}
	
	
	@Test
	public void testCombinedSummary2Files() throws IOException{	
		System.out.println(GREEN+"\nTest Combined Detailed Summary on Two File\n"+RESET);	
		
		
		String input1 = "target/test-classes/SummaryTests/CheckIndels/input1.vcf";
		String input2 = "target/test-classes/OUTPUT/Varstats/IndVsCombined/input2.vcf";
		String combinedOut = "target/test-classes/OUTPUT/Varstats/IndVsCombined/combTest";
		String Answer = "target/test-classes/OUTPUT/Varstats/IndVsCombined/Answer_detailed_summary.txt";
		
		List<String> infiles = new ArrayList<String>();
		infiles.add(input1);
		String combID = "";
		try {
				
			TreeMap<String,VariantPoolHeavy> VPs = UtilityBelt.createHeavyVariantPools(infiles,false);
			combID = (String) VPs.keySet().toArray()[0];
			System.out.println(combID);
			VariantPoolSummarizer.summarizeVariantPoolsDetailedCombined(VPs, combinedOut);
			
		} catch (InvalidInputFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Compare2DetailedSummaryFiles(combinedOut+"_"+combID+"_detailed_summary.txt", Answer);
	}
	
	public static void Compare2DetailedSummaryFiles(String o1, String o2){
		File out1 = new File(o1);
		File out2 = new File(o2);
		
		try {
			Scanner file1 = new Scanner(out1);
			Scanner file2 = new Scanner(out2);
		
			while(file1.hasNext() && file2.hasNext()){
				String line1 = file1.next();
				String line2 = file2.next();
				Assert.assertTrue(line1.equals(line2));
			}
			
			if(file1.hasNext() || file2.hasNext())
				Assert.assertTrue(false);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
