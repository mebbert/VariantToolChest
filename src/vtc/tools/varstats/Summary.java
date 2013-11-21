package vtc.tools.varstats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;
import vtc.tools.utilitybelt.UtilityBelt;

public class Summary {

	Summary() {
	}

	Summary(TreeMap<String, VariantPool> allVPs, boolean printMulti) {
		summaryMain(allVPs, printMulti);
	}

	private void summaryMain(TreeMap<String, VariantPool> allVPs, boolean printMulti) {
		ArrayList<String> files = new ArrayList<String>();
		NavigableSet<String> filenames = allVPs.descendingKeySet();
		Iterator<String> i = filenames.descendingIterator();
		while (i.hasNext()) {
			files.add(i.next());
		}

		int numsamples = 0;

		ArrayList<String> Files = new ArrayList<String>();

		VariantCalculator TotalVC = new VariantCalculator();
		
		int count = 0;
		for (VariantPool VP : allVPs.values()) {

			String FileName = VP.getFile().getName();
			Files.add(FileName);

			VariantCalculator VC = new VariantCalculator();
			
			ArrayList<String> varinfo = new ArrayList<String>();
			Object[] Samples = VP.getSamples().toArray();
			numsamples += Samples.length;
			Iterator<String> it = VP.getVariantIterator();
			String currVarKey;
			while (it.hasNext()) {
				currVarKey = it.next();

				VariantContext var = VP.getVariant(currVarKey);
				List<String> names = var.getSampleNamesOrderedByName();
				if (var.isVariant()) {
					VC.Calculator(var, names);

					String temp = getStatsforFile(var, Samples);
					varinfo.add(temp + '\n');

					// Concatenate all the info into a string.
					// add it to the varinfo arraylist.
				}
			}
			
			TotalVC.setNumVars(TotalVC.getNumVars()+VC.getNumVars());
			
			TotalVC.setNumSNVs(TotalVC.getNumSNVs()+VC.getNumSNVs());
			TotalVC.setTiCount(TotalVC.getTiCount()+VC.getTiCount());
			TotalVC.setTvCount(TotalVC.getTvCount()+VC.getTvCount());
			TotalVC.setGenoTiCount(TotalVC.getGenoTiCount()+VC.getGenoTiCount());
			TotalVC.setGenoTvCount(TotalVC.getGenoTvCount()+VC.getGenoTvCount());
			
			TotalVC.setInDels(TotalVC.getInDels()+VC.getInDels());
			TotalVC.setStructVars(TotalVC.getStructVars()+VC.getStructVars());
			
			TotalVC.setNumMultiAlts(TotalVC.getNumMultiAlts()+VC.getNumMultiAlts());

			VC.CalcTiTv();
			VC.CalcGenoTiTv();

			if (!printMulti) {
				printFiles(files, count, Files, VC, Samples.length, printMulti);
			}

			
			printSummaryFile(varinfo, FileName);
			varinfo.clear();
			count++;

		}
		
		TotalVC.CalcTiTv();
		TotalVC.CalcGenoTiTv();
		
		
		if (printMulti) {
			printFiles(files, count, Files, TotalVC, numsamples, printMulti);
		}

	}



	private String getStatsforFile(VariantContext var, Object[] Samples) {
		String temp = var.getChr() + '\t' + var.getStart() + '\t' + var.getID() + '\t' + var.getReference().getBaseString() + '\t';
		List<Allele> Alternates = var.getAlternateAlleles();
		temp += ALTs(Alternates) + '\t';
		int refcount = 0;
		String altcount = "";
		ArrayList<String> Genotypes = new ArrayList<String>();
		for (Object s : Samples) {
			String Geno = var.getGenotype((String) s).getGenotypeString();
			// System.out.println(Geno);

			String[] genos = new String[2];
			if (Geno.contains("/")) {
				genos = Geno.split("/");
				// System.out.println(genos[0] + " " + genos[1]);
				Genotypes.add(genos[0]);
				Genotypes.add(genos[1]);
			} else if (Geno.contains("|")) {
				genos = Geno.split("|");
				//System.out.println(genos[0]+" "+genos[1]+" "+genos[2]+" "+genos[3]);
				Genotypes.add(genos[1]);
				Genotypes.add(genos[3]);
			}
		}

		refcount = getrefcounts(Genotypes, var.getReference().getBaseString());
		altcount = getaltcounts(Genotypes, Alternates);
		//System.out.println(refcount + "  "+altcount);

		temp += Integer.toString(refcount) + "\t" + altcount;
		Depth d = new Depth();
		d.getDepths(var, new ArrayList<String>(Arrays.asList((String[])Samples)));
		temp += d.toString();
		double qual = var.getPhredScaledQual();
		if(qual>0)
			temp += "\t"+qual;
		else
			temp+="\t"+"NA";
		String depthError = d.getError();
		if(!depthError.isEmpty())
			temp += "\tIncorrect depth calls in samples: "+depthError+".";
		return temp;
	}



	private String getaltcounts(ArrayList<String> genotypes, List<Allele> alternates) {
		String temp = "";
		int pos = 0;
		for (Allele a : alternates) {
			int count = 0;
			String alt = a.getBaseString();
			for (String s : genotypes) {
				if (s.equals(alt)) {
					count++;
				}
			}
			if (pos == alternates.size() - 1)
				temp += Integer.toString(count);
			else
				temp += Integer.toString(count) + ",";
			pos++;
		}
		return temp;
	}

	private int getrefcounts(ArrayList<String> genotypes, String ref) {
		int count = 0;
		for (String s : genotypes) {
			if (s.equals(ref)) {
				count++;
			}
		}
		return count;
	}

	private String ALTs(List<Allele> Alternates) {
		String alts = "";
		int pos = 0;
		// System.out.println(Alternates.size());
		for (Allele A : Alternates) {

			if (pos == Alternates.size() - 1)
				alts += A.getBaseString();
			else
				alts += A.getBaseString() + ",";
			pos++;
		}
		return alts;
	}

	private void printSummaryFile(ArrayList<String> S, String OutFile) {
		String outfile = OutFile.substring(0, OutFile.lastIndexOf("."));
		OutFile = outfile + "_Summary.txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutFile));
			out.write("Chr" + '\t' + "Pos" + '\t' + "ID" + '\t' + "Ref" + '\t' + "Alts" + '\t' + "RefCount" + '\t' + "AltCount" + "\t" + "AvgDepth" + '\t' + "MinDepth" + '\t' + "MaxDepth" + '\t' + "Qual" + '\t'+"Errors"+'\n');
			for (String s : S) {
				out.write(s);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	private int FindLength(int NumVars, int SNPs, int InDels, int StructVars, int NumMultiAlts, double TiTv, double GenoTiTv, String title) {



		ArrayList<String> find = new ArrayList<String>();
		String numvars = Integer.toString(NumVars);
		find.add(numvars);
		String snps = Integer.toString(SNPs);
		find.add(snps);
		String indels = Integer.toString(InDels);
		find.add(indels);
		String structvars = Integer.toString(StructVars);
		find.add(structvars);
	
		String nummultialts = Integer.toString(NumMultiAlts);
		find.add(nummultialts);
		
		find.add(UtilityBelt.roundDouble(TiTv));
		find.add(UtilityBelt.roundDouble(GenoTiTv));
		find.add(title);

		int length = 0;
		for (String s : find) {
			if (s.length() > length)
				length = s.length();
			if (s.length() + 15 > title.length() && s != title)
				length = s.length() + 20;
		}
		return length;

	}
	
	
	private void printFiles(ArrayList<String> file, int count, ArrayList<String> FileName,
			VariantCalculator vc, int NumSamples, boolean printmulti) {

		String newLine = System.getProperty("line.separator");

		String title;

		if (printmulti)
			title = "Summary of " + file.get(0) + ": " + FileName.get(0);
		else
			title = "Summary of " + file.get(count) + ": " + FileName.get(count);

		int length = FindLength(vc.getNumVars(), vc.getNumSNVs(), vc.getInDels(), vc.getStructVars(),
				vc.getNumMultiAlts(), vc.getTiTv(), vc.getGenoTiTv(), title) + 5;
		

		char[] chars = new char[length + 1];
		Arrays.fill(chars, '-');
		String s = new String(chars);
		s = "+" + s + "+";

		char[] ch = new char[length + 3];
		Arrays.fill(ch, '=');
		String t = new String(ch);

		double snvPercent = (double) vc.getNumSNVs() / (double) vc.getNumVars() * 100;
		double InDelsPercent = (double) vc.getInDels() / (double) vc.getNumVars() * 100;
		double StructPercent = (double) vc.getStructVars() / (double) vc.getNumVars() * 100;

		int LeftColumn = 15;

		String leftalignFormatint = "|%-" + LeftColumn + "s%" + (length - LeftColumn) + "d |" + newLine;
		//String leftalignFormatd = "|%-" + LeftColumn + "s%" + (length - LeftColumn) + ".2f |" + newLine;
		String rightalignFormati = "|%" + LeftColumn + "s%" + (length - LeftColumn) + "s |" + newLine;
		String rightalignFormatf = "|%" + LeftColumn + "s%" + (length - LeftColumn) + ".2f |" + newLine;
		//String rightalignFormats = "|%" + LeftColumn + "s%" + (length - LeftColumn) + "s |" + newLine;
		String leftalignFormats = " %-" + (length--) + "s" + newLine;
		//String leftAlignError = " %-" + length + "s" + newLine;

		if (printmulti) {
			System.out.format(t + newLine);
			int pos = 0;

			System.out.format(leftalignFormats, "");
			for (String vpfile : file) {
				if (pos > 0)
					title = "           " + vpfile + ": " + FileName.get(pos);
				pos++;
				System.out.format(leftalignFormats, title);
			}
		} else {
			System.out.format(t + newLine);
			System.out.format(leftalignFormats, "");
			System.out.format(leftalignFormats, title);
		}
		System.out.format(leftalignFormats, "");
		System.out.format(t + newLine);
		System.out.format(newLine);
		System.out.format(s + newLine);
		System.out.format(leftalignFormatint, "TotalVars:", vc.getNumVars());
		System.out.format(leftalignFormatint, "Total Samples:", NumSamples);
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "SNVs:      ", Integer.toString(vc.getNumSNVs()) + " (" + UtilityBelt.roundDouble(snvPercent) + "%)");
		System.out.format(rightalignFormatf, "Ti/Tv:", vc.getTiTv());
		System.out.format(rightalignFormatf, "(Geno)Ti/Tv:", vc.getGenoTiTv());
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "INDELs:    ", Integer.toString(vc.getInDels()) + " (" + UtilityBelt.roundDouble(InDelsPercent) + "%)");
		System.out.format(s + newLine);
		System.out.format(rightalignFormati, "StructVars:", Integer.toString(vc.getStructVars()) + " (" + UtilityBelt.roundDouble(StructPercent) + "%)");
		System.out.format(s + newLine);
		System.out.format(leftalignFormatint, "MultiAlts:", vc.getNumMultiAlts());
		System.out.format(s + newLine);
	
		System.out.format(newLine + newLine);

	
	}
	
	
	
	
	
}
