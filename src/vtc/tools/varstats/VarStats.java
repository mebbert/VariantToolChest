package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;

public class VarStats {
	
	private int TotalNumVars = 0;
	private int TotalNumSNVs = 0;
	private int TotalInDels = 0;
	private int TotalStructVars = 0;
	private int TotalNumHomoRef = 0;
	private int TotalNumHomoVar = 0;
	private int TotalNumHet = 0;
	private int TotalNumMultiAlts = 0;
	private double TotalQualScore = 0;
	private double TotalTiCount = 0;
	private double TotalTvCount = 0;
	
	private int NumVars = 0;
	private int NumSNVs = 0;
	private int InDels = 0;
	private int StructVars = 0;
	private int NumHomoRef = 0;
	private int NumHomoVar = 0;
	private int NumHet = 0;
	private int NumMultiAlts = 0;
	private double QualScore = 0;
	private double TiCount = 0;
	private double TvCount = 0;
	
	boolean PrintMulti;
	boolean PrintSingle;
	
	//constructor
	
	public VarStats(TreeMap<String, VariantPool> allVPs, boolean printMulti, boolean printSingle) {
		
		IterateAndCount(allVPs, printMulti, printSingle);
		
	}
	
	//Functions
	
	private void IterateAndCount (TreeMap<String, VariantPool> allVPs, boolean printMulti, boolean printSingle){

		for (VariantPool VP : allVPs.values()) {
			boolean PrintMulti = printMulti;
			boolean PrintSingle = printSingle;
			if(PrintSingle == true){
				NumVars = 0;
				NumSNVs = 0;
				InDels = 0;
				StructVars = 0;
				NumHomoRef = 0;
				NumHomoVar = 0;
				NumHet = 0;
				NumMultiAlts = 0;
				QualScore = 0;
				TiCount = 0;
				TvCount = 0;
			}
			
			Iterator<String> it = VP.getIterator();
			String currVarKey;
			while (it.hasNext()) {
				
				currVarKey = it.next();
//				System.out.println(currVarKey);
				NumVars++;
				TotalNumVars++;
				VariantContext var = VP.getVariant(currVarKey);
//				System.out.println(var);
				List<Allele> Alts = var.getAlternateAlleles();
				Allele ref = var.getReference();
				
				if(var.isSNP()){
					NumSNVs++;
					TotalNumSNVs++;
					TiCount += isTransition(ref, Alts);
					TvCount += isTransversion(ref, Alts);
				}
				else if(var.isIndel()){
					InDels++;
					TotalInDels++;
				}
				else if(var.isStructuralIndel()){
					StructVars++;
					TotalStructVars++;
				}
			
				if(var.getHetCount()>0){
					NumHet = NumHet + var.getHetCount();
					TotalNumHet = TotalNumHet + var.getHetCount();
				}
			
				if(var.getHomRefCount()>0){
					NumHomoRef = NumHomoRef + var.getHetCount();
					TotalNumHomoRef = TotalNumHomoRef + var.getHetCount();
				}
				
				if(var.getHomVarCount()>0){
					NumHomoVar = NumHomoVar + var.getHetCount();
					TotalNumHomoVar = TotalNumHomoVar + var.getHetCount();
				}
				
				AltCounter(Alts);
				
						
			}
			System.out.println("Total: " + NumVars + "\nSNPs: " + NumSNVs + "\nInDels: " + InDels + "\nStructInDels: " + StructVars + "\nNumHet: " +
					NumHet + "\nNumhomoRef: " + NumHomoRef + "\nNumHomoVar: " + NumHomoVar + "\nNumMulitAlts: " + NumMultiAlts + "\nQualScore: "
					+ QualScore);
		}
		System.out.println("Total: " + TotalNumVars + "\nTotalSNPs: " + TotalNumSNVs + "\nTotalInDels: " + TotalInDels + "\nTotalStructInDels: " +
				TotalStructVars + "\nTotalNumHet: " +
				TotalNumHet + "\nTotalNumhomoRef: " + 
				TotalNumHomoRef + "\nTotalNumHomoVar: " + 
				TotalNumHomoVar + "\nTotalNumMulitAlts: " + 
				TotalNumMultiAlts + "\nTotalQualScore: "
				+ TotalQualScore);
	}
	
	private void QualScoreCounter (){
		
	}
	
	private void QualScoreAvg (){
		
	}
	
	private void DepthScoresCounter (){
		
	}
	
	private void DepthScoresAvg (){
		
	}
	
	private int isTransition(Allele ref, List<Allele> alts){
		String base;
		String refBase = ref.getBaseString();
		int count = 0;
		for(Allele a : alts){
			base = a.getBaseString();
			if(base.equals("G") && refBase.equals("A")){
				count++;
			}
			else if(base.equals("A") && refBase.equals("G")){
				count++;
			}
			else if(base.equals("T") && refBase.equals("C")){
				count++;
			}
			else if(base.equals("C") && refBase.equals("T")){
				count++;
			}
		}
		TotalTiCount += count;
		return count;
	}
	
	private int isTransversion(Allele ref, List<Allele> alts){
		String base;
		String refBase = ref.getBaseString();
		int count = 0;
		for(Allele a : alts){
			base = a.getBaseString();
			if(base.equals("G") && refBase.equals("C")){
				count++;
			}
			else if(base.equals("C") && refBase.equals("G")){
				count++;
			}
			else if(base.equals("T") && refBase.equals("A")){
				count++;
			}
			else if(base.equals("A") && refBase.equals("T")){
				count++;
			}
		}
		TotalTvCount += count;
		return count;
	}
	
	private void AltCounter(List <Allele> Alts){
		
		if(Alts.size() > 1){
			NumMultiAlts++;
			TotalNumMultiAlts++;
		}
	}
	
	private void print1file(){
		
	}
	private void print2files (){
		
	}
	
	
	
	/************************************************
	 * 	Summary Stats  
	 * 
	 * Filename
	 * 	total variants:
	 * 		SNVs:
	 * 		InDels:	
	 * 		Structural Variants:
	 * 	Transition/transversion:
	 * 	Mean/min/max for qual and depth
	 * 	# of hetero.
	 * 	# of Homo.
	 * 	# of vars with miltiple alts -> variant context
	 * 
	 */
	
	/************************************************
	*	Association Test
	*	Lets just get all the variants calculated to be able to perform
	*	an association test.
	*/
	
	
}