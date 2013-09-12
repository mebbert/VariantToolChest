package vtc.tools.varstats;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;

public class VarStats {
	
	HashMap phenoInfo = new HashMap();
	
	
	private int TotalNumVars = 0;
	private int TotalNumSNVs = 0;
	private int TotalInDels = 0;
	private int TotalStructVars = 0;
	private int TotalNumHomoRef = 0;
	private int TotalNumHomoVar = 0;
	private int TotalNumHet = 0;
	private int TotalNumMultiAlts = 0;
	private double TotalQualScore = 0;
	private int TotalDepth = 0;
	private int TotalNumGeno = 0;
	private double TotalTiCount = 0;
	private double TotalTvCount = 0;
	private double TotalTiTv = 0;
	private double TotalGenoTiCount = 0;
	private double TotalGenoTvCount = 0;
	private double TotalGenoTiTv = 0;
	private float TotalFreqA = 0;
	private float TotalFreqT = 0;
	private float TotalFreqG = 0;
	private float TotalFreqC = 0;
	private float TotalFreqCount = 0;
	
	

	
	
	//constructors
	
	public VarStats(TreeMap<String, VariantPool> allVPs, ArrayList<Object> phenoArgs, boolean printMulti, boolean sum, boolean assoc) {
		if(sum)
			IterateAndCount(allVPs, printMulti);
		if(assoc)
			doAssociation(allVPs, phenoArgs);
	}
	
	public VarStats(){}
	
	//Functions
	
	
	
	private void doAssociation(TreeMap<String, VariantPool> AllVPs, ArrayList<Object> phenoArgs){
		if (phenoArgs != null) {
			// Make a structure to read in the phenotype information...
			phenoInfo = ParsePhenoFile(phenoArgs);
		}
		float freqA = 0;
		float freqT = 0;
		float freqC = 0;
		float freqG = 0;
		for (VariantPool VP : AllVPs.values()) {

			Iterator<String> it = VP.getIterator();
			String currVarKey;
			int Num_SNPS = 0;
			while (it.hasNext()) {
				currVarKey = it.next();
				VariantContext vc = VP.getVariant(currVarKey);
				// Its a SNP now calculate frequencies
				if (vc.isSNP()) {
					Num_SNPS++;
					System.out.println(vc.toString());
					System.out.println("---------------------------");
					String[] alleles = { "A", "T", "C", "G" };
					for (String all : alleles) {
						
						if (all.equals("A")) {
							freqA = (float) vc.getCalledChrCount(vc
									.getAllele(all))
									/ vc.getCalledChrCount();
						} else if (all.equals("T"))
							freqT = (float) vc.getCalledChrCount(vc
									.getAllele(all))
									/ vc.getCalledChrCount();
						else if (all.equals("C"))
							freqC = (float) vc.getCalledChrCount(vc
									.getAllele(all))
									/ vc.getCalledChrCount();
						else if (all.equals("G"))
							freqG = (float) vc.getCalledChrCount(vc
									.getAllele(all))
									/ vc.getCalledChrCount();
						
						
					}
					
					TotalFreqCount += (freqA + freqT + freqG + freqC);
					TotalFreqA += freqA;
					TotalFreqT += freqT;
					TotalFreqG += freqG;
					TotalFreqC += freqC;
					
					System.out.printf("frequency of A: %.2f\n", freqA);
					System.out.printf("frequency of T: %.2f\n", freqT);
					System.out.printf("frequency of C: %.2f\n", freqC);
					System.out.printf("frequency of G: %.2f\n", freqG);
				}
				System.out.printf("Number of SNPS %d\n", Num_SNPS);
			}
			TotalFreqA = TotalFreqA/TotalFreqCount;
			TotalFreqT = TotalFreqT/TotalFreqCount;
			TotalFreqG = TotalFreqG/TotalFreqCount;
			TotalFreqC = TotalFreqC/TotalFreqCount;
		}
		System.out.printf("Total frequency of A: %.2f\n", TotalFreqA);
		System.out.printf("Total frequency of T: %.2f\n", TotalFreqT);
		System.out.printf("Total frequency of C: %.2f\n", TotalFreqC);
		System.out.printf("Total frequency of G: %.2f\n", TotalFreqG);
		
	}
	
	private HashMap ParsePhenoFile(ArrayList<Object> phenofiles) {
		HashMap phenos = new HashMap();
		for (Object o : phenofiles) {
			// lets parse the phenotype file.
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(o.toString()));
				String line;
				while ((line = br.readLine()) != null){
					// process the line.
					String line1[] = line.split("\t");
					phenos.put(line1[0], line1[1]);
				}
				br.close();
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return phenos;
	}
	
	
	
	
	private void IterateAndCount (TreeMap<String, VariantPool> allVPs, boolean printMulti){
		

		for (VariantPool VP : allVPs.values()) {
			boolean PrintMulti = printMulti;
		
			int NumVars = 0;
			int NumSNVs = 0;
			int InDels = 0;
			int StructVars = 0;
			int NumHomoRef = 0;
			int NumHomoVar = 0;
			int NumMultiAlts = 0;
			int NumHet = 0;
			double QualScore = 0;
			double TiCount = 0;
			double TvCount = 0;
			int Depth = 0;
			int NumGeno = 0;
			double TiTv = 0;
			double GenoTiCount = 0;
			double GenoTvCount = 0;
			double GenoTiTv = 0;
		
			
			Iterator<String> it = VP.getIterator();
			String currVarKey;
			while (it.hasNext()) {
				
				currVarKey = it.next();
				NumVars++;
				TotalNumVars++;
				VariantContext var = VP.getVariant(currVarKey);
				List<String> names = var.getSampleNamesOrderedByName();
			
				List<Allele> Alts = var.getAlternateAlleles();
				Allele ref = var.getReference();
				
				if(var.isSNP()){
					NumSNVs++;
					TotalNumSNVs++;
					TiCount += isTransition(ref, Alts);
					TvCount += isTransversion(ref, Alts);
					double[] temp = testTiTv(var, names, ref);
					GenoTiCount += temp[0];
					GenoTvCount += temp[1];
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
				
				NumMultiAlts += AltCounter(Alts);
				
				QualScore += var.getPhredScaledQual();
				TotalQualScore += var.getPhredScaledQual();
				
				Depth += getDepth(var, names);
				TotalDepth += getDepth(var, names);
				NumGeno = names.size();
				TotalNumGeno = names.size();
			}		
			TotalTiCount += TiCount;
			TotalTvCount += TvCount;
			TotalGenoTiCount += GenoTiCount;
			TotalGenoTvCount += GenoTvCount;
			TiTv = TiCount/TvCount;
			GenoTiTv = GenoTiCount/GenoTvCount;
			Depth = Depth/(NumVars*NumGeno);
			QualScore = QualScore/NumVars;
			
			System.out.println("Total: " + NumVars + "\nSNPs: " + NumSNVs + "\nInDels: " + InDels + "\nStructInDels: " + StructVars + "\nNumHet: " +
					NumHet + "\nNumhomoRef: " + NumHomoRef + "\nNumHomoVar: " + NumHomoVar + "\nNumMulitAlts: " + NumMultiAlts + "\nQualScore: "
					+ QualScore + "\nDepth: " + Depth + "\nTi/Tv: " + TiTv + "\nGenoTiTv: " + GenoTiTv);
		}
		TotalTiTv = TotalTiCount/TotalTvCount;
		TotalGenoTiTv = TotalGenoTiCount/TotalGenoTvCount;
		TotalDepth = TotalDepth/(TotalNumVars*TotalNumGeno);
		TotalQualScore = TotalQualScore/TotalNumVars;
		
		System.out.println("Total: " + TotalNumVars + "\nTotalSNPs: " + TotalNumSNVs + "\nTotalInDels: " + TotalInDels + "\nTotalStructInDels: " +
				TotalStructVars + "\nTotalNumHet: " +
				TotalNumHet + "\nTotalNumhomoRef: " + 
				TotalNumHomoRef + "\nTotalNumHomoVar: " + 
				TotalNumHomoVar + "\nTotalNumMulitAlts: " + 
				TotalNumMultiAlts + "\nTotalQualScore: "
				+ TotalQualScore + "\nTotalDepth: " + 
				TotalDepth + "\nTotalTiTv: " + TotalTiTv
				+ "\nTotalGenoTiTv: " + TotalGenoTiTv);
	}
	
	
	
	private int getDepth(VariantContext var, List<String> names){
		
		int count = 0;
		for(String s : names){
			Genotype geno = var.getGenotypes().get(s);
			count += geno.getDP();
			
		}
		return count;
	}
	
	private double[] testTiTv(VariantContext var, List<String> names, Allele ref){
		double countTi = 0;
		double countTv = 0;
		for(String s : names){
			String refBase = ref.getBaseString();
			Genotype geno = var.getGenotypes().get(s);
			String gt = geno.getGenotypeString();
			String first = Character.toString(gt.charAt(0));
			String second = Character.toString(gt.charAt(2));
			countTi += checkTransition(refBase, first);
			countTi += checkTransition(refBase, second);
			countTv += checkTransversion(refBase, first);
			countTv += checkTransversion(refBase, second);
		}
		
		return new double[] {countTi, countTv};
	}
	
	
	
	private int checkTransition(String ref, String base){
		int count = 0;
		if(base.equals("G") && ref.equals("A")){
			count++;
		}
		else if(base.equals("A") && ref.equals("G")){
			count++;
		}
		else if(base.equals("T") && ref.equals("C")){
			count++;
		}
		else if(base.equals("C") && ref.equals("T")){
			count++;
		}
		return count;
	}
	
	private int isTransition(Allele ref, List<Allele> alts){
		String base;
		String refBase = ref.getBaseString();
		int count = 0;
		for(Allele a : alts){
			base = a.getBaseString();
			count += checkTransition(refBase, base);
		}
		TotalTiCount += count;
		return count;
	}
	
	private int checkTransversion(String ref, String base){
		int count = 0;
		if(base.equals("G") && ref.equals("C")){
			count++;
		}
		else if(base.equals("C") && ref.equals("G")){
			count++;
		}
		else if(base.equals("T") && ref.equals("A")){
			count++;
		}
		else if(base.equals("A") && ref.equals("T")){
			count++;
		}
		return count;
	}
	
	private int isTransversion(Allele ref, List<Allele> alts){
		String base;
		String refBase = ref.getBaseString();
		int count = 0;
		for(Allele a : alts){
			base = a.getBaseString();
			count += checkTransversion(refBase, base);
		}
		TotalTvCount += count;
		return count;
	}
	
	private int AltCounter(List <Allele> Alts){
		
		if(Alts.size() > 1){
			TotalNumMultiAlts++;
			return 1;
		}
		return 0;
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