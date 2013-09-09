package vtc.tools.varstats;

import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;

public class VarStats {
	
	private int NumVars;
	private int NumSNVs;
	private int InDels;
	private int StructVars;
	private int NumHomoRef;
	private int NumHomoVar;
	private int NumHet;
	private int NumMultiAlts;
	private double QualScore;
	
	//constructor
	
	public VarStats(TreeMap<String, VariantPool> allVPs) {
		
		IterateAndCount(allVPs);
		
	}
	
	//Functions
	
	private void IterateAndCount (TreeMap<String, VariantPool> allVPs){
		for (VariantPool VP : allVPs.values()) {
			NumVars = 0;
			NumSNVs = 0;
			InDels = 0;
			StructVars = 0;
			NumHomoRef = 0;
			NumHomoVar = 0;
			NumHet = 0;
			NumMultiAlts = 0;
			QualScore = 0;
			
			Iterator<String> it = VP.getIterator();
			String currVarKey;
			while (it.hasNext()) {
				
				currVarKey = it.next();
//				System.out.println(currVarKey);
				NumVars++;
				VariantContext var = VP.getVariant(currVarKey);
//				System.out.println(var);
				
				
				if(var.isSNP()){
					NumSNVs++;
				}
				else if(var.isIndel()){
					InDels++;
				}
				else if(var.isStructuralIndel()){
					StructVars++;
				}
			
				if(var.getHetCount()>0){
					NumHet = NumHet + var.getHetCount();
				}
			
				if(var.getHomRefCount()>0){
					NumHomoRef = NumHomoRef + var.getHetCount();
				}
				
				if(var.getHomVarCount()>0){
					NumHomoVar = NumHomoVar + var.getHetCount();
				}
				
				AltCounter(var);
				QualScores(var);
						
			}
			System.out.println("Total: " + NumVars + "\nSNPs: " + NumSNVs + "\nInDels: " + InDels + "\nStructInDels: " + StructVars + "\nNumHet: " +
					NumHet + "\nNumhomoRef: " + NumHomoRef + "\nNumHomoVar: " + NumHomoVar + "\nNumMulitAlts: " + NumMultiAlts + "\nQualScore: "
					+ QualScore);
		}
	}
	
	private void QualScores(VariantContext var){
		
		Object qs = var.getCommonInfo().getAttribute("QUAL");
		System.out.println(qs);
		if (qs != null){
			String score = qs.toString();
			Double temp = Double.valueOf(score);
			QualScore = QualScore + temp;
		}
		
	}
	
	private void DepthScores(){
	
	}
	
	private int TransitionTransversionScore(){
		return 0;
	}
	
	private void AltCounter(VariantContext var){
		List<Allele> Alts = var.getAlternateAlleles();
		if(Alts.size() > 1){
			NumMultiAlts++;
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