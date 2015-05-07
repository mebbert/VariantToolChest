/**
 * 
 */
package vtc.tools.varstats;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.BeforeClass;
import org.junit.Test;

import vtc.datastructures.InvalidInputFileException;
import vtc.datastructures.VariantPoolHeavy;
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
	public void TestBasicSummaryValues() throws IOException {
		System.out.println(GREEN+"\nTest Basic Summary Values\n"+RESET);	
		
		
		String input1 = "target/test-classes/SummaryTests/TestBasicSummaryValues/input1.vcf";
		
		ArrayList<String> vcfArgs = new ArrayList<String>();
		TreeMap<String, VariantPoolHeavy> AllVPs = null;

		vcfArgs.add(input1);

		HashMap<String, VariantPoolSummary> summaries = new HashMap<String, VariantPoolSummary>();

		try {
			try {
				AllVPs = UtilityBelt.createHeavyVariantPools(vcfArgs, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			summaries = VariantPoolSummarizer.summarizeVariantPools(AllVPs);
			
			for (Entry<String, VariantPoolSummary> entry : summaries.entrySet()) {
			    VariantPoolSummary value = entry.getValue();

			    assertEquals(9,value.getNumVars());
			    assertEquals(3,value.getNumSamples());
			    assertEquals(9,value.getNumSNVs());
			   // assert(value.getTiTv()==1.0);
			    //assert(value.getGenoTiTv()==1.0);
			    assertEquals(0,value.getNumMNVs());
			    assertEquals(0,value.getNumIndels());
			    assertEquals(0,value.getNumInsertions());
			    assertEquals(0,value.getNumDeletions());
			    assert(-1.0==value.getSmallestDeletion());
			    assert(-1.0==value.getSmallestInsertion());
			    assert(-1.0==value.getAvgDeletionSize());
			    assert(-1.0==value.getAvgInsertionSize());
			    assert(-1.0==value.getLargestDeletion());
			    assert(-1.0==value.getLargestInsertion());
			    assertEquals(0,value.getNumStructVars());
			    assertEquals(0,value.getNumStructIns());
			    assertEquals(0,value.getNumStructDels());
			}

			
		} catch (InvalidInputFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}