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
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;

public class VarStats {

    @SuppressWarnings("unused")
	private static final String MaxInsertion = null;

    HashMap<String, String>     phenoInfo    = new HashMap<String, String>();

    // constructors

    public VarStats(TreeMap<String, VariantPool> allVPs, ArrayList<Object> phenoArgs/*, boolean printMulti, boolean sum, boolean assoc*/) {
       /* if (sum)
            IterateAndCount(allVPs, printMulti);
        if (assoc)*/
            doAssociation(allVPs, phenoArgs);
    }

    public VarStats() {
    }

    // Functions

    @SuppressWarnings("unused")
	public void doAssociation(TreeMap<String, VariantPool> AllVPs, ArrayList<Object> phenoArgs) {

        if (phenoArgs != null) {
            // Make a structure to read in the phenotype information...
            phenoInfo = ParsePhenoFile(phenoArgs);
        }

        for (VariantPool VP : AllVPs.values()) {

            ArrayList<Association> association = new ArrayList<Association>();

            Object[] SampleList = VP.getSamples().toArray();
            ArrayList<String> CasePhenos = new ArrayList<String>();
            ArrayList<String> ControlPhenos = new ArrayList<String>();
            for (Object o : SampleList) {
                if (phenoInfo.containsKey(o)) {
                    String phenotype = phenoInfo.get(o);
                    // System.out.println(o + phenotype);
                    if (phenotype.equals("1")) {
                        CasePhenos.add((String) o);

                    } else if (phenotype.equals("2"))
                        ControlPhenos.add((String) o);
                    // System.out.println(o + "'s pheno is " +phenoInfo.get(o));
                }
            }

            Iterator<String> it = VP.getVariantIterator();
            String currVarKey;
            int Num_SNPS = 0;
            String OutFile = "temp.txt";
            while (it.hasNext()) {

                currVarKey = it.next();
                VariantContext vc = VP.getVariant(currVarKey);
                OutFile = VP.getFile().getName();
                // Its a SNP now calculate frequencies
                Allele Ref = vc.getReference();
                List<Allele> Alts = vc.getAlternateAlleles();

                for (Allele A : Alts) {

                    Association Assoc = new Association(vc.getChr(), vc.getID(), vc.getStart(), vc.getReference().getBaseString(), A.getBaseString());
                    long[] CaseAlleleCount = Assoc.CaseControlCounts(CasePhenos, vc, A.getBaseString());
                    long[] ControlAlleleCount = Assoc.CaseControlCounts(ControlPhenos, vc, A.getBaseString());
                    Assoc.SetCaseRefCount(CaseAlleleCount[0]);
                    Assoc.SetCaseAltCount(CaseAlleleCount[1]);
                    Assoc.SetControlRefCount(ControlAlleleCount[0]);
                    Assoc.SetControlAltCount(ControlAlleleCount[1]);
                   	ChiSquareTest test = new ChiSquareTest();
                   	double PVal = test.chiSquareTestDataSetsComparison(ControlAlleleCount, CaseAlleleCount);
                    
                    Assoc.SetPValue(PVal);

                    double OR = Assoc.calcOR(CaseAlleleCount, ControlAlleleCount);
                    Assoc.SetOR(OR);

                    association.add(Assoc);

                }

            }
            printToFile(association, OutFile);

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
                while ((line = br.readLine()) != null) {
                    // process the line.
                    String line1[] = line.split("\t");
                    System.out.println(line);
                    phenos.put(line1[0], line1[1]);
                }
                br.close();
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return phenos;
    }

    HashMap<String, String> MakePhenoList(Object[] SampleList, HashMap<String, String> phenoInfo) {
        HashMap<String, String> Phenos = new HashMap<String, String>();

        return Phenos;
    }

    private void printToFile(ArrayList<Association> A, String OutFile) {
        String outfile = OutFile.substring(0, OutFile.lastIndexOf("."));
        OutFile = outfile + "_Assoc.txt";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(OutFile));
            out.write("Chr" + '\t' + "ID" + '\t' + "Pos" + '\t' + "Ref" + '\t' + "Alt" + '\t' + "CaseRefCount" + '\t' + "CaseAltCount" + '\t' + "ControlRefCount" + '\t' + "ControlAltCount" + '\t' + "OR" + '\t' + "P-Value" + '\n');
            for (Association a : A) {
                out.write(a.toString());
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 
     * 
     * 
     * Association functions above, Summary functions below
     */

	private void IterateAndCount(TreeMap<String, VariantPool> allVPs, boolean printMulti) {

        Summary s = new Summary(allVPs, printMulti);

    }

}
