package vtc.tools.varstats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPoolLight;

public class VariantPoolPercents {

//	private TreeMap<String, String> VariantPercents;
	private File output;
	
	
	public VariantPoolPercents(VariantPoolLight vp) throws IOException{
		
		String filepath = vp.getFile().toString();
		if(filepath.contains(".vcf.gz"))
			filepath = filepath.replace(".vcf.gz", ".percent.txt");
		else
			filepath = filepath.replace(".vcf", ".percent.txt");
		this.output = new File(filepath);
		String files = "";
		try {
			files = getPercents(vp);
		} catch (VarStatsException e) {
			e.printStackTrace();
		}
		
		this.writeFile(files);
	
	}

	private void writeFile(String files) {
		try {
			FileWriter printer = new FileWriter(this.output);
			printer.append(files);
			printer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String getPercents(VariantPoolLight vp) throws VarStatsException, IOException{
		TreeSet<String> Samples = vp.getSamples();
		StringBuilder percentKey = new StringBuilder();
		percentKey.append("CHR\tPOS\tREF\tALT\tHOMO_ALT_percent\tHET_ALT_percent\tHET_percent\tHOMO_REF_percent\n");
		VariantContext vc;
		while((vc = vp.getNextVar()) != null){
			
			Iterable<Genotype> gts = vc.getGenotypesOrderedByName();
			
			int homoref = 0;
			int homoalt = 0;
			int het = 0;
			int hetalt = 0;
			
			for(Genotype gt : gts){
				if(gt.isHomRef()){
					homoref++;
					continue;
				}
				if(gt.isHomVar()){
					homoalt++;
					continue;
				}
				if(gt.isHet()){
					String genotype = gt.getGenotypeString(false);
					String[] types = genotype.split("[|/]");
					if(types[0].contains("*") || types[1].contains("*"))
						het++;
					else
						hetalt++;
				}
			}
			

			List<Allele> alts = vc.getAlternateAlleles();
			StringBuilder alt = new StringBuilder();
			if(alts.size() > 0){
				for(Allele a : alts)
					alt.append(a.toString()+",");
				alt.deleteCharAt(alt.length()-1);
			}
			
			double homoaltpercent = homoalt/Double.valueOf(Samples.size());
			double hetaltpercent = hetalt/Double.valueOf(Samples.size()); 
			double hetpercent = het/Double.valueOf(Samples.size());
			double homorefpercent = homoref/Double.valueOf(Samples.size());
			percentKey.append(vc.getChr()+"\t"+vc.getStart()+"\t"+vc.getReference().getBaseString()+"\t"+alt.toString()+"\t"+homoaltpercent+"\t"+hetaltpercent+"\t"+hetpercent+"\t"+homorefpercent+"\n");
		}
		return percentKey.toString();
	}

}
