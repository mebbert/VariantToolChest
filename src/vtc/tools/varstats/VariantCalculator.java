package vtc.tools.varstats;

import java.util.List;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

public class VariantCalculator {
	

	private int NumVars;
	private int QVars;
	private int DPVars;
	private int NumSNVs;
	private int InDels;
	private int Insertions;
	private int Deletions;
	private double minInsertion;
	private double maxInsertion;
	private double avgInsertion;
	private double minDeletion;
	private double maxDeletion;
	private double avgDeletion;
	private int StructVars;
	private int NumHomoRef;
	private int NumHomoVar;
	private int NumMultiAlts;
	private int NumHet;
	private double QualScore;
	private double MaxQScore;
	private double MinQScore;
	private double TiCount;
	private double TvCount;
	private double Depth;
	private int MaxDepth;
	private int MinDepth;
	private int NumGeno;
	private double TiTv;
	private double GenoTiCount;
	private double GenoTvCount;
	private double GenoTiTv;
	
	
	
	VariantCalculator(){
		NumVars = 0;
		QVars = 0;
		DPVars = 0;
		NumSNVs = 0;
		InDels = 0;
		Insertions = 0;
		Deletions = 0;
		minInsertion = 0;
		maxInsertion = 0;
		avgInsertion = 0;
		minDeletion = 0;
		maxDeletion = 0;
		avgDeletion = 0;
		StructVars = 0;
		NumHomoRef = 0;
		NumHomoVar = 0;
		NumMultiAlts = 0;
		NumHet = 0;
		QualScore = 0;
		MaxQScore = -1;
		MinQScore = Integer.MAX_VALUE;
		TiCount = 0;
		TvCount = 0;
		Depth = 0;
		MaxDepth = -1;
		MinDepth = Integer.MAX_VALUE;
		NumGeno = 0;
		TiTv = 0;
		GenoTiCount = 0;
		GenoTvCount = 0;
		GenoTiTv = 0;
	}
	
	
	public void Calculator(VariantContext var, List<String> names){
		NumVars++;

		List<Allele> Alts = var.getAlternateAlleles();
		Allele ref = var.getReference();

		if (var.isSNP()) {
			NumSNVs++;
			
			TiCount += isTransition(ref, Alts);
			TvCount += isTransversion(ref, Alts);
			double[] temp = testTiTv(var, names, ref);
			GenoTiCount += temp[0];
			GenoTvCount += temp[1];
		} else if (var.isIndel()) {
			List<Integer> indelLengths = var.getIndelLengths();
			for (int length : indelLengths) {
				InDels++;
				
				if (length > 0) {
					Insertions++;
					// System.out.println("Insertion");
				} else if (length < 0) {
					Deletions++;
					// System.out.println("Deletion");

				}
			}
		} else if (var.isStructuralIndel()) {
			StructVars++;
			
		}
/*
		if (var.getHetCount() > 0) {
			NumHet = NumHet + var.getHetCount();
		}

		if (var.getHomRefCount() > 0) {
			NumHomoRef = NumHomoRef + var.getHetCount();
		}

		if (var.getHomVarCount() > 0) {
			NumHomoVar = NumHomoVar + var.getHetCount();
		}
*/
		NumMultiAlts += AltCounter(Alts);

	
	}
	
	

	private double[] testTiTv(VariantContext var, List<String> names, Allele ref) {
		double countTi = 0;
		double countTv = 0;
		for (String s : names) {
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
		return new double[] { countTi, countTv };
	}

	
	private int checkTransition(String ref, String base) {
		int count = 0;
		if (base.equals("G") && ref.equals("A")) {
			count++;
		} else if (base.equals("A") && ref.equals("G")) {
			count++;
		} else if (base.equals("T") && ref.equals("C")) {
			count++;
		} else if (base.equals("C") && ref.equals("T")) {
			count++;
		}
		return count;
	}

	
	private int isTransition(Allele ref, List<Allele> alts) {
		String base;
		String refBase = ref.getBaseString();
		int count = 0;
		for (Allele a : alts) {
			base = a.getBaseString();
			count += checkTransition(refBase, base);
		}
		return count;
	}

	
	private int checkTransversion(String ref, String base) {
		int count = 0;
		if (base.equals("G") && ref.equals("C")) {
			count++;
		} else if (base.equals("C") && ref.equals("G")) {
			count++;
		} else if (base.equals("T") && ref.equals("A")) {
			count++;
		} else if (base.equals("A") && ref.equals("T")) {
			count++;
		}
		return count;
	}

	
	private int isTransversion(Allele ref, List<Allele> alts) {
		String base;
		String refBase = ref.getBaseString();
		int count = 0;
		for (Allele a : alts) {
			base = a.getBaseString();
			count += checkTransversion(refBase, base);
		}
		return count;
	}
	
	
	private int AltCounter(List<Allele> Alts) {
		if (Alts.size() > 1) {
			return 1;
		}
		return 0;
	}
	
	public void CalcTiTv() {
		TiTv = TiCount/TvCount;
		
	}


	public void CalcGenoTiTv() {
		GenoTiTv = GenoTiCount/GenoTvCount;
		
	}
	
	
	/**
	 * @return the numVars
	 */
	public int getNumVars() {
		return NumVars;
	}

	/**
	 * @param numVars the numVars to set
	 */
	public void setNumVars(int numVars) {
		NumVars = numVars;
	}

	/**
	 * @return the qVars
	 */
	public int getQVars() {
		return QVars;
	}

	/**
	 * @param qVars the qVars to set
	 */
	public void setQVars(int qVars) {
		QVars = qVars;
	}

	/**
	 * @return the dPVars
	 */
	public int getDPVars() {
		return DPVars;
	}

	/**
	 * @param dPVars the dPVars to set
	 */
	public void setDPVars(int dPVars) {
		DPVars = dPVars;
	}

	/**
	 * @return the numSNVs
	 */
	public int getNumSNVs() {
		return NumSNVs;
	}

	/**
	 * @param numSNVs the numSNVs to set
	 */
	public void setNumSNVs(int numSNVs) {
		NumSNVs = numSNVs;
	}

	/**
	 * @return the inDels
	 */
	public int getInDels() {
		return InDels;
	}

	/**
	 * @param inDels the inDels to set
	 */
	public void setInDels(int inDels) {
		InDels = inDels;
	}

	/**
	 * @return the insertions
	 */
	public int getInsertions() {
		return Insertions;
	}

	/**
	 * @param insertions the insertions to set
	 */
	public void setInsertions(int insertions) {
		Insertions = insertions;
	}

	/**
	 * @return the deletions
	 */
	public int getDeletions() {
		return Deletions;
	}

	/**
	 * @param deletions the deletions to set
	 */
	public void setDeletions(int deletions) {
		Deletions = deletions;
	}

	/**
	 * @return the minInsertion
	 */
	public double getMinInsertion() {
		return minInsertion;
	}

	/**
	 * @param minInsertion the minInsertion to set
	 */
	public void setMinInsertion(double minInsertion) {
		this.minInsertion = minInsertion;
	}

	/**
	 * @return the maxInsertion
	 */
	public double getMaxInsertion() {
		return maxInsertion;
	}

	/**
	 * @param maxInsertion the maxInsertion to set
	 */
	public void setMaxInsertion(double maxInsertion) {
		this.maxInsertion = maxInsertion;
	}

	/**
	 * @return the avgInsertion
	 */
	public double getAvgInsertion() {
		return avgInsertion;
	}

	/**
	 * @param avgInsertion the avgInsertion to set
	 */
	public void setAvgInsertion(double avgInsertion) {
		this.avgInsertion = avgInsertion;
	}

	/**
	 * @return the minDeletion
	 */
	public double getMinDeletion() {
		return minDeletion;
	}

	/**
	 * @param minDeletion the minDeletion to set
	 */
	public void setMinDeletion(double minDeletion) {
		this.minDeletion = minDeletion;
	}

	/**
	 * @return the maxDeletion
	 */
	public double getMaxDeletion() {
		return maxDeletion;
	}

	/**
	 * @param maxDeletion the maxDeletion to set
	 */
	public void setMaxDeletion(double maxDeletion) {
		this.maxDeletion = maxDeletion;
	}

	/**
	 * @return the avgDeletion
	 */
	public double getAvgDeletion() {
		return avgDeletion;
	}

	/**
	 * @param avgDeletion the avgDeletion to set
	 */
	public void setAvgDeletion(double avgDeletion) {
		this.avgDeletion = avgDeletion;
	}

	/**
	 * @return the structVars
	 */
	public int getStructVars() {
		return StructVars;
	}

	/**
	 * @param structVars the structVars to set
	 */
	public void setStructVars(int structVars) {
		StructVars = structVars;
	}

	/**
	 * @return the numHomoRef
	 */
	public int getNumHomoRef() {
		return NumHomoRef;
	}

	/**
	 * @param numHomoRef the numHomoRef to set
	 */
	public void setNumHomoRef(int numHomoRef) {
		NumHomoRef = numHomoRef;
	}

	/**
	 * @return the numHomoVar
	 */
	public int getNumHomoVar() {
		return NumHomoVar;
	}

	/**
	 * @param numHomoVar the numHomoVar to set
	 */
	public void setNumHomoVar(int numHomoVar) {
		NumHomoVar = numHomoVar;
	}

	/**
	 * @return the numMultiAlts
	 */
	public int getNumMultiAlts() {
		return NumMultiAlts;
	}

	/**
	 * @param numMultiAlts the numMultiAlts to set
	 */
	public void setNumMultiAlts(int numMultiAlts) {
		NumMultiAlts = numMultiAlts;
	}

	/**
	 * @return the numHet
	 */
	public int getNumHet() {
		return NumHet;
	}

	/**
	 * @param numHet the numHet to set
	 */
	public void setNumHet(int numHet) {
		NumHet = numHet;
	}

	/**
	 * @return the qualScore
	 */
	public double getQualScore() {
		return QualScore;
	}

	/**
	 * @param qualScore the qualScore to set
	 */
	public void setQualScore(double qualScore) {
		QualScore = qualScore;
	}

	/**
	 * @return the maxQScore
	 */
	public double getMaxQScore() {
		return MaxQScore;
	}

	/**
	 * @param maxQScore the maxQScore to set
	 */
	public void setMaxQScore(double maxQScore) {
		MaxQScore = maxQScore;
	}

	/**
	 * @return the minQScore
	 */
	public double getMinQScore() {
		return MinQScore;
	}

	/**
	 * @param minQScore the minQScore to set
	 */
	public void setMinQScore(double minQScore) {
		MinQScore = minQScore;
	}

	/**
	 * @return the tiCount
	 */
	public double getTiCount() {
		return TiCount;
	}

	/**
	 * @param tiCount the tiCount to set
	 */
	public void setTiCount(double tiCount) {
		TiCount = tiCount;
	}

	/**
	 * @return the tvCount
	 */
	public double getTvCount() {
		return TvCount;
	}

	/**
	 * @param tvCount the tvCount to set
	 */
	public void setTvCount(double tvCount) {
		TvCount = tvCount;
	}

	/**
	 * @return the depth
	 */
	public double getDepth() {
		return Depth;
	}

	/**
	 * @param depth the depth to set
	 */
	public void setDepth(double depth) {
		Depth = depth;
	}

	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return MaxDepth;
	}

	/**
	 * @param maxDepth the maxDepth to set
	 */
	public void setMaxDepth(int maxDepth) {
		MaxDepth = maxDepth;
	}

	/**
	 * @return the minDepth
	 */
	public int getMinDepth() {
		return MinDepth;
	}

	/**
	 * @param minDepth the minDepth to set
	 */
	public void setMinDepth(int minDepth) {
		MinDepth = minDepth;
	}

	/**
	 * @return the numGeno
	 */
	public int getNumGeno() {
		return NumGeno;
	}

	/**
	 * @param numGeno the numGeno to set
	 */
	public void setNumGeno(int numGeno) {
		NumGeno = numGeno;
	}

	/**
	 * @return the tiTv
	 */
	public double getTiTv() {
		return TiTv;
	}

	/**
	 * @param tiTv the tiTv to set
	 */
	public void setTiTv(double tiTv) {
		TiTv = tiTv;
	}

	/**
	 * @return the genoTiCount
	 */
	public double getGenoTiCount() {
		return GenoTiCount;
	}

	/**
	 * @param genoTiCount the genoTiCount to set
	 */
	public void setGenoTiCount(double genoTiCount) {
		GenoTiCount = genoTiCount;
	}

	/**
	 * @return the genoTvCount
	 */
	public double getGenoTvCount() {
		return GenoTvCount;
	}

	/**
	 * @param genoTvCount the genoTvCount to set
	 */
	public void setGenoTvCount(double genoTvCount) {
		GenoTvCount = genoTvCount;
	}

	/**
	 * @return the genoTiTv
	 */
	public double getGenoTiTv() {
		return GenoTiTv;
	}

	/**
	 * @param genoTiTv the genoTiTv to set
	 */
	public void setGenoTiTv(double genoTiTv) {
		GenoTiTv = genoTiTv;
	}





	
	
}
