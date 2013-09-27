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

    private double  PValue          = 0;

    private double  correctedPValue = 0;

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
     * Setters
     */
    public void SetChr(String C) {
        Chr = C;
    }

    public void SetId(String I) {
        Id = I;
    }

    public void SetPos(Integer pos) {
        Pos = pos;
    }

    public void SetRef(String R) {
        Ref = R;
    }

    public void SetAlt(String A) {
        Alt = A;
    }

    public void SetCaseRefCount(long c) {
        CaseRefCount = c;
    }

    public void SetCaseAltCount(long c) {
        CaseAltCount = c;
    }

    public void SetControlRefCount(long c) {
        ControlRefCount = c;
    }

    public void SetControlAltCount(long c) {
        ControlAltCount = c;
    }

    public void SetOR(double O) {
        OR = O;
    }

    public void SetPValue(double P) {
        PValue = P;
    }

    public void SetCorrectedPValue(double P) {
        correctedPValue = P;
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

    public double calcOR(long[] caseAlleleCount, long[] controlAlleleCount) {
        double oddsRatio = 0;

        return oddsRatio;
    }

    @Override
    public String toString() {
        String PVal = String.format("%.4g%n", PValue);
        String association = Chr + '\t' + Id + '\t' + Pos + '\t' + Ref + '\t' + Alt + '\t' + CaseRefCount + '\t' + CaseAltCount + '\t' + ControlRefCount + '\t' + ControlAltCount + '\t' + PVal;
        return association;
    }
}
