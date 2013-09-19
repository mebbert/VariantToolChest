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
	



package vtc.tools.varstats;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.SamplePool;
import vtc.datastructures.VariantPool;

public class VarStats {
	

	HashMap<String, String> phenoInfo = new HashMap<String, String>();

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
						} 
						else if (all.equals("T")){
							freqT = (float) vc.getCalledChrCount(vc
									.getAllele(all))
									/ vc.getCalledChrCount();
						}
						else if (all.equals("C")){
							freqC = (float) vc.getCalledChrCount(vc
									.getAllele(all))
									/ vc.getCalledChrCount();
						}
						else if (all.equals("G")){
							freqG = (float) vc.getCalledChrCount(vc
									.getAllele(all))
									/ vc.getCalledChrCount();
						}		
						
					}
					
					
					System.out.printf("frequency of A: %.2f\n", freqA);
					System.out.printf("frequency of T: %.2f\n", freqT);
					System.out.printf("frequency of C: %.2f\n", freqC);
					System.out.printf("frequency of G: %.2f\n", freqG);
				}
				System.out.printf("Number of SNPS %d\n", Num_SNPS);
			}
			
		}
		
		
	}
	
	private HashMap<String, String> ParsePhenoFile(ArrayList<Object> phenofiles) {
		HashMap<String, String> phenos = new HashMap<String, String>();
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
		
		ArrayList<String> files = new ArrayList<String>();
		NavigableSet<String> filenames = allVPs.descendingKeySet();
		Iterator<String> i = filenames.descendingIterator();
		while(i.hasNext()){
			files.add(i.next());
		}
		

		int TotalNumVars = 0;
		int TotalNumSNVs = 0;
		int TotalInDels = 0;
		int TotalStructVars = 0;
		int TotalNumHomoRef = 0;
		int TotalNumHomoVar = 0;
		int TotalNumHet = 0;
		int TotalNumMultiAlts = 0;
		double TotalQualScore = 0;
		double TotalMaxQScore = 0;
		double TotalMinQScore = Integer.MAX_VALUE;
		double TotalDepth = 0;
		int TotalMaxDepth = 0;
		int TotalMinDepth = Integer.MAX_VALUE;
		int TotalNumGeno = 0;
		double TotalTiCount = 0;
		double TotalTvCount = 0;
		double TotalTiTv = 0;
		double TotalGenoTiCount = 0;
		double TotalGenoTvCount = 0;
		double TotalGenoTiTv = 0;
		
		ArrayList<String> Files = new ArrayList<String>();
		
		int count = 0;
		for (VariantPool VP : allVPs.values()) {
			
			int NumVars = 0;
			int NumSNVs = 0;
			int InDels = 0;
			int StructVars = 0;
			int NumHomoRef = 0;
			int NumHomoVar = 0;
			int NumMultiAlts = 0;
			int NumHet = 0;
			double QualScore = 0;
			double MaxQScore = 0;
			double MinQScore = Integer.MAX_VALUE;
			double TiCount = 0;
			double TvCount = 0;
			double Depth = 0;
			int MaxDepth = 0;
			int MinDepth = Integer.MAX_VALUE;
			int NumGeno = 0;
			double TiTv = 0;
			double GenoTiCount = 0;
			double GenoTvCount = 0;
			double GenoTiTv = 0;
			String FileName = VP.getFile().getName();
			Files.add(FileName);
			
			
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
			
//				if(var.getHetCount()>0){
//					NumHet = NumHet + var.getHetCount();
//					TotalNumHet = TotalNumHet + var.getHetCount();
//				}
//			
//				if(var.getHomRefCount()>0){
//					NumHomoRef = NumHomoRef + var.getHetCount();
//					TotalNumHomoRef = TotalNumHomoRef + var.getHetCount();
//				}
//				
//				if(var.getHomVarCount()>0){
//					NumHomoVar = NumHomoVar + var.getHetCount();
//					TotalNumHomoVar = TotalNumHomoVar + var.getHetCount();
//				}
				
				NumMultiAlts += AltCounter(Alts);
				
				
				double tempQualScore = var.getPhredScaledQual();
				if(tempQualScore>MaxQScore)
					MaxQScore = tempQualScore;
				if(tempQualScore<MinQScore)
					MinQScore = tempQualScore;
				QualScore += tempQualScore;
				TotalQualScore += tempQualScore;
				
				int TempDepth = getDepth(var, names);
				if(TempDepth > MaxDepth)
					MaxDepth = TempDepth;
				if(TempDepth < MinDepth)
					MinDepth = TempDepth;
				Depth += TempDepth;
				TotalDepth += TempDepth;
				NumGeno = names.size();
				TotalNumGeno = names.size();
			}		
			TotalNumMultiAlts += NumMultiAlts;
			if(TotalMinQScore > MinQScore)
				TotalMinQScore = MinQScore;
			if(TotalMaxQScore < MaxQScore)
				TotalMaxQScore = MaxQScore;
			if(TotalMinDepth > MinDepth)
				TotalMinDepth = MinDepth;
			if(TotalMaxDepth < MaxDepth)
				TotalMaxDepth = MaxDepth;
			TotalTiCount += TiCount;
			TotalTvCount += TvCount;
			TotalGenoTiCount += GenoTiCount;
			TotalGenoTvCount += GenoTvCount;
			TiTv = TiCount/TvCount;
			GenoTiTv = GenoTiCount/GenoTvCount;
			Depth = Depth/(NumVars);
			QualScore = QualScore/NumVars;
			
			if(!printMulti){
				printFiles(files, count, Files, NumVars, NumSNVs, InDels, StructVars, NumHet, NumHomoRef, NumHomoVar, NumMultiAlts, QualScore,
					Depth, TiTv, GenoTiTv, MinDepth, MaxDepth, MinQScore, MaxQScore, printMulti);
			}
			
//			System.out.println("Total: " + NumVars + "\nSNPs: " + NumSNVs + "\nInDels: " + InDels + "\nStructInDels: " + StructVars + "\nNumHet: " +
//					NumHet + "\nNumhomoRef: " + NumHomoRef + "\nNumHomoVar: " + NumHomoVar + "\nNumMulitAlts: " + NumMultiAlts + "\nQualScore: "
//					+ QualScore + "\nDepth: " + Depth + "\nTi/Tv: " + TiTv + "\nGenoTiTv: " + GenoTiTv);
		count++;
		}
		TotalTiTv = TotalTiCount/TotalTvCount;
		TotalGenoTiTv = TotalGenoTiCount/TotalGenoTvCount;
		TotalDepth = TotalDepth/(TotalNumVars);
		TotalQualScore = TotalQualScore/TotalNumVars;
		
		if(printMulti){
			printFiles(files, count, Files, TotalNumVars, TotalNumSNVs, TotalInDels, TotalStructVars, TotalNumHet, TotalNumHomoRef,
					TotalNumHomoVar, TotalNumMultiAlts,TotalQualScore,TotalDepth, TotalTiTv, TotalGenoTiTv, TotalMinDepth,
					TotalMaxDepth, TotalMinQScore, TotalMaxQScore, printMulti);
		}
		
		
		
//		System.out.println("Total: " + TotalNumVars + "\nTotalSNPs: " + TotalNumSNVs + "\nTotalInDels: " + TotalInDels + "\nTotalStructInDels: " +
//				TotalStructVars + "\nTotalNumHet: " +
//				TotalNumHet + "\nTotalNumhomoRef: " + 
//				TotalNumHomoRef + "\nTotalNumHomoVar: " + 
//				TotalNumHomoVar + "\nTotalNumMulitAlts: " + 
//				TotalNumMultiAlts + "\nTotalQualScore: "
//				+ TotalQualScore + "\nTotalDepth: " + 
//				TotalDepth + "\nTotalTiTv: " + TotalTiTv
//				+ "\nTotalGenoTiTv: " + TotalGenoTiTv);
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
	
		return count;
	}
	
	private int AltCounter(List <Allele> Alts){
		
		if(Alts.size() > 1){
			
			return 1;
		}
		return 0;
	}
	
	String roundDouble(double d){
		DecimalFormat df = new DecimalFormat("#.##");
		return String.valueOf(df.format(d));
	}
	
	
	private int FindLength(int NumVars, int SNPs, int InDels, int StructVars, int NumHets, int NumHomoRef,
			int NumHomoVar, int NumMultiAlts, double QualScore, double Depth, double TiTv, double GenoTiTv,
			int MinDepth, int MaxDepth, double MinQScore, double MaxQScore, String title){
		
		ArrayList<String> find = new ArrayList<String>();
		String numvars = Integer.toString(NumVars);
		find.add(numvars);
		String snps = Integer.toString(SNPs);
		find.add(snps);
		String indels = Integer.toString(InDels);
		find.add(indels);
		String structvars = Integer.toString(StructVars);
		find.add(structvars);
		String numhets = Integer.toString(NumHets);
		find.add(numhets);
		String numhomoref = Integer.toString(NumHomoRef);
		find.add(numhomoref);
		String numhomovar = Integer.toString(NumHomoVar);
		find.add(numhomovar);
		String nummultialts = Integer.toString(NumMultiAlts);
		find.add(nummultialts);
		String mindepth = Integer.toString(MinDepth);
		find.add(mindepth);
		String maxdepth = Integer.toString(MaxDepth);
		find.add(maxdepth);
		find.add(roundDouble(QualScore));
		find.add(roundDouble(MinQScore));
		find.add(roundDouble(MaxQScore));
		find.add(roundDouble(Depth));
		find.add(roundDouble(TiTv));
		find.add(roundDouble(GenoTiTv));
		find.add(title);
		
		int length = 0;
		for(String s: find){
			if(s.length() > length)
				length = s.length();
			if(s.length()+15 > title.length() && s!=title)
				length = s.length()+15;
		}
		return length;
		
		
	}
	

	
	
	
	
	private void printFiles (ArrayList<String> file, int count, ArrayList<String> FileName, int NumVars, int SNPs, int InDels, int StructVars, int NumHets, int NumHomoRef,
			int NumHomoVar, int NumMultiAlts, double QualScore, double Depth, double TiTv, double GenoTiTv, int MinDepth, int MaxDepth,
			double MinQScore, double MaxQScore, boolean printmulti)
	{
		
		String newLine = System.getProperty("line.separator");
		
		String title;
		
		
		if(printmulti)
			title = "Summary of "+file.get(0)+": "+FileName.get(0);
		else
			title = "Summary of "+file.get(count)+": "+FileName.get(count);
		
		int length = FindLength(NumVars, SNPs, InDels, StructVars, NumHets, NumHomoRef,
			NumHomoVar, NumMultiAlts, QualScore, Depth, TiTv, GenoTiTv, MinDepth, MaxDepth, MinQScore, MaxQScore, title)+5;
		
		
		
		char[] chars = new char[length+1];
		Arrays.fill(chars, '-');
		String s = new String(chars);
		s = "+"+s+"+";
		
		char[] ch = new char[length+3];
		Arrays.fill(ch, '=');
		String t = new String(ch);
		
		
//		System.out.println(file);
		
		int LeftColumn = 15;
		
		
		String leftalignFormatint = "|%-"+LeftColumn+"s%"+(length-LeftColumn)+"d |" + newLine;
		String leftalignFormatd = "|%-"+LeftColumn+"s%"+(length-LeftColumn)+".2f |" + newLine;
		String rightalignFormati = "|%"+LeftColumn+"s%"+(length-LeftColumn)+"d |" + newLine;
		String rightalignFormatf = "|%"+LeftColumn+"s%"+(length-LeftColumn)+".2f |" + newLine;
		String leftalignFormats = " %-"+(length--)+"s" + newLine;
		
		
		if(printmulti){
			System.out.format(t + newLine);
			int pos = 0;
		
			System.out.format(leftalignFormats, "");
			for(String vpfile : file){
				if(pos>0)
					title = "           "+vpfile+": "+FileName.get(pos);
				pos++;
			System.out.format(leftalignFormats, title);
			}
		}
		else{
			System.out.format(t + newLine);
			System.out.format(leftalignFormats, "");
			System.out.format(leftalignFormats, title);
		}
		System.out.format(leftalignFormats, "");
		System.out.format(t + newLine);
		System.out.format(newLine);
		System.out.format(s + newLine);
		System.out.format(leftalignFormatint, "TotalVars:", NumVars);
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "SNVs:      ", SNPs);
		System.out.format(rightalignFormatf, "Ti/Tv:", TiTv);
		System.out.format(rightalignFormatf, "(Geno)Ti/Tv:", GenoTiTv);
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "INDEls:    ", InDels);
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "StructVars:", StructVars);
		System.out.format(s + newLine);
//		System.out.format(leftalignFormatint, "Hets:", NumHets);
//		System.out.format(s + newLine);
//		System.out.format(leftalignFormatint, "HomoRef:", NumHomoRef);
//		System.out.format(s + newLine);
//		System.out.format(leftalignFormatint, "HomoVar:", NumHomoVar);
//		System.out.format(s + newLine);
		System.out.format(leftalignFormatint, "MultiAlts:", NumMultiAlts);
		System.out.format(s + newLine);
		System.out.format(leftalignFormatd, "AvgQualScore:", QualScore);
		System.out.format(rightalignFormatf, "MinQualScore:", MinQScore);
		System.out.format(rightalignFormatf, "MaxQualScore:", MaxQScore);
		System.out.format(s + newLine);
		System.out.format(leftalignFormatd, "AvgDepth:", Depth);
		System.out.format(rightalignFormati, "MinDepth:", MinDepth);
		System.out.format(rightalignFormati, "MaxDepth:", MaxDepth);
		System.out.format(s + newLine);
		
		System.out.format(newLine + newLine);		
	}
	
}
