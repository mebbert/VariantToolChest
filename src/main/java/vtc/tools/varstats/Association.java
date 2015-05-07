package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.List;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;

public class Association {
    /*
     * Variables
     */
    private String  Chr;
    private String  Id;
    private Integer Pos;
    private String  Ref;
    private String  Alt;

    private long    CaseRefCount    = 0;
    private long    CaseAltCount    = 0;
    private long    ControlRefCount = 0;
    private long    ControlAltCount = 0;

    private double  OR              = 0;
    private double  chiSq_pval          = 0;
    private double fishersExact_pval = 0;
    private double midp_pval = 0;

    @SuppressWarnings("unused")
	private double  correctedChiSq_pval = 0;

    /*
     * Constructors
     */
    Association() {
    }

    Association(String chr, String id, Integer pos, String ref, String alt) {
        Chr = chr;
        Id = id;
        Pos = pos;
        Ref = ref;
        Alt = alt;
    }

    Association(Integer RefCount, Integer AltCount, List<Allele> Alts, Allele Ref, VariantContext var, ArrayList<String> Case, ArrayList<String> Control) {

    }

    /*
     * setters
     */
    public void setChr(String C) {
        Chr = C;
    }

    public void setId(String I) {
        Id = I;
    }

    public void setPos(Integer pos) {
        Pos = pos;
    }

    public void setRef(String R) {
        Ref = R;
    }

    public void setAlt(String A) {
        Alt = A;
    }

    public void setCaseRefCount(long c) {
        CaseRefCount = c;
    }

    public void setCaseAltCount(long c) {
        CaseAltCount = c;
    }

    public void setControlRefCount(long c) {
        ControlRefCount = c;
    }

    public void setControlAltCount(long c) {
        ControlAltCount = c;
    }

    public void setOR(double o) {
        OR = o;
    }

    public void setChiSq_pval(double p) {
        chiSq_pval = p;
    }

    public void setCorrectedChiSq_pval(double p) {
        correctedChiSq_pval = p;
    }
    
    public void setFishersExact_pval(double p){
    	fishersExact_pval = p;
    }
    
    public void setMidP_pval(double p){
    	midp_pval = p;
    }

    /*
     * 
     * Getters
     */

    /*
     * 
     * Functions
     */

    public long[] CaseControlCounts(ArrayList<String> CaseControl, VariantContext vc, String alt) {
        long[] counts = new long[2];
        int refcount = 0;
        int altcount = 0;
        // System.out.println(CaseControl.size() + '\n');
        for (String s : CaseControl) {
            String geno = vc.getGenotype(s).getGenotypeString();
            // System.out.println(s + "  " + geno + '\n');
            String[] genos = new String[2];
            if (geno.contains("/")) {
                genos = geno.split("/");

            } else if (geno.contains("|")) {
                genos = geno.split("|");

            }
            for (String g : genos) {
                // System.out.println(g);
                if (vc.getReference().getBaseString().equals(g))
                    refcount++;
                else if (alt.equals(g))
                    altcount++;
            }
        }
        // System.out.println(refcount + "\t" + altcount);
        counts[0] = refcount;
        counts[1] = altcount;

        return counts;
    }

    public double calcOR(double caseUnexposedAlleleCount, double caseExposedAlleleCount, double controlUnexposedAlleleCount, double controlExposedAlleleCount) {
        if (caseUnexposedAlleleCount == 0 || caseExposedAlleleCount == 0 || controlUnexposedAlleleCount == 0 || controlExposedAlleleCount == 0) {
            return -1;
        } else {
            double oddsRatio = (caseExposedAlleleCount * controlUnexposedAlleleCount) / (caseUnexposedAlleleCount * controlExposedAlleleCount);
            return oddsRatio;
        }
    }

    @Override
    public String toString() {
    	String chiSq_pval_str = "";
    	String fishers_pval_str = "";
    	String midp_pval_str = "";
    	if(chiSq_pval!=-1){
    		chiSq_pval_str = String.format("%.4g", chiSq_pval);
    		fishers_pval_str = String.format("%.4g", fishersExact_pval);
    		midp_pval_str = String.format("%.4g", midp_pval);
    	}
    	else{
    		chiSq_pval_str = "NA";
    		fishers_pval_str = "NA";
    		midp_pval_str = "NA";
    	}
    	
    	
        String or;
        if (OR == -1) {
            or = "NA";
        } else {
            or = String.format("%.4g", OR);
        }
        String association = Chr + '\t' + Id + '\t' + Pos + '\t' + Ref + '\t'
        		+ Alt + '\t' + CaseRefCount + '\t' + CaseAltCount + '\t'
        		+ ControlRefCount + '\t' + ControlAltCount + '\t' + or
        		+ '\t' + midp_pval_str + '\t' + fishers_pval_str + '\t' + chiSq_pval_str ;
        return association;
    }
}
