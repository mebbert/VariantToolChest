package vtc.tools.varstats;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.ArrayList;
import java.util.List;

public class Association {
    /*
     * Variables
     */
    private String  chr;
    private String  id;
    private Integer pos;
    private String  ref;
    private String  alt;

    private long    caseRefCount    = 0;
    private long    caseAltCount    = 0;
    private long    controlRefCount = 0;
    private long    controlAltCount = 0;

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
        this.chr = chr;
        this.id = id;
        this.pos = pos;
        this.ref = ref;
        this.alt = alt;
    }

    Association(Integer RefCount, Integer AltCount, List<Allele> Alts, Allele Ref, VariantContext var, ArrayList<String> Case, ArrayList<String> Control) {

    }

    /*
     * setters
     */
    public void setChr(String chr) {
        this.chr = chr;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setCaseRefCount(long count) {
        caseRefCount = count;
    }

    public void setCaseAltCount(long c) {
        caseAltCount = c;
    }

    public void setControlRefCount(long c) {
        controlRefCount = c;
    }

    public void setControlAltCount(long c) {
        controlAltCount = c;
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
    
    public long getCaseRefCount(){
    	return this.caseRefCount;
    }
    
    public long getCaseAltCount(){
    	return this.caseAltCount;
    }
    
    public long getControlRefCount(){
    	return this.controlRefCount;
    }
    
    public long getControlAltCount(){
    	return this.controlAltCount;
    }

    /*
     * 
     * Functions
     */

    public void setCaseControlCounts(ArrayList<String> cases, ArrayList<String> controls, VariantContext vc, String alt) {
    	
    	/* Index '0' is the unexposed (ref) count. */
        long[] caseCounts = this.countAlleles(cases, vc, alt);
        this.setCaseRefCount(caseCounts[0]);
        this.setCaseAltCount(caseCounts[1]);

        long[] controlCounts = this.countAlleles(controls, vc, alt);
        this.setControlRefCount(controlCounts[0]);
        this.setControlAltCount(controlCounts[1]);
    }
    
    public long[] countAlleles(ArrayList<String> samples, VariantContext vc, String alt){
    	long[] counts = new long[2];
        int refcount = 0;
        int altcount = 0;
//        System.out.println("N Samples: " + samples.size());

        for (String s : samples) {
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
        String association = chr + '\t' + id + '\t' + pos + '\t' + ref + '\t'
        		+ alt + '\t' + caseRefCount + '\t' + caseAltCount + '\t'
        		+ controlRefCount + '\t' + controlAltCount + '\t' + or
        		+ '\t' + midp_pval_str + '\t' + fishers_pval_str + '\t' + chiSq_pval_str ;
        return association;
    }
}
