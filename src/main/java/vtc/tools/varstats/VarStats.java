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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import jsc.contingencytables.ContingencyTable2x2;
import jsc.contingencytables.FishersExactTest;
import jsc.tests.H1;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.log4j.Logger;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.datastructures.VariantPool;
import vtc.datastructures.VariantPoolLight;

public class VarStats {

    @SuppressWarnings("unused")
	private static final String MaxInsertion = null;

    HashMap<String, String>     phenoInfo    = new HashMap<String, String>();
    
    private static Logger logger = Logger.getLogger(VarStats.class);

    // constructors

    public VarStats(TreeMap<String, VariantPoolLight> allVPs, List<String> phenoArgs/*, boolean printMulti, boolean sum, boolean assoc*/) throws IOException {
       /* if (sum)
            IterateAndCount(allVPs, printMulti);
        if (assoc)*/
            doAssociation(allVPs, phenoArgs);
    }

    public VarStats() {
    }

    // Functions

    @SuppressWarnings("unused")
	public void doAssociation(TreeMap<String, VariantPoolLight> AllVPs, List<String> phenoArgs) throws IOException {

        if (phenoArgs != null) {
            // Make a structure to read in the phenotype information...
            phenoInfo = ParsePhenoFile(phenoArgs);
        }

        for (VariantPool vp : AllVPs.values()) {

        	logger.info("Collecting case-control status for samples in " + vp.getPoolID());
            ArrayList<Association> association = new ArrayList<Association>();

            Object[] SampleList = vp.getSamples().toArray();
            ArrayList<String> CasePhenos = new ArrayList<String>();
            ArrayList<String> ControlPhenos = new ArrayList<String>();
            for (Object o : SampleList) {
                if (phenoInfo.containsKey(o)) {
                    String phenotype = phenoInfo.get(o);
                    // System.out.println(o + phenotype);
                    if (phenotype.equals("2")) {
                        CasePhenos.add((String) o);

                    } else if (phenotype.equals("1"))
                        ControlPhenos.add((String) o);
                    // System.out.println(o + "'s pheno is " +phenoInfo.get(o));
                }
            }
            
            if(CasePhenos.size() == 0 || ControlPhenos.size() == 0){
            	logger.error("Missing either cases or controls in "
				    + vp.getPoolID() + ": "
            		+ CasePhenos.size() + " cases, " + ControlPhenos.size() +
            		" controls. Exiting...");
            	System.exit(1);
            }
            else{
            	logger.info("Counts in " + vp.getPoolID() + ": "
            		+ CasePhenos.size() + " cases, " + ControlPhenos.size() +
            		" controls.");
            }


        	logger.info("Testing association for all variants in " + vp.getPoolID());
        	NumberFormat nf = NumberFormat.getInstance(Locale.US);
//            Iterator<String> it = VP.getVariantIterator();
//            String currVarKey;
            int count = 0;
            String OutFile = "temp.txt";
            VariantContext vc;
            while ((vc = vp.getNextVar()) != null) {

            	if(count > 1 && count % 1000 == 0) System.out.print("Variants tested: "
                		+ nf.format(count) + "\r");

//                currVarKey = it.next();
//                VariantContext vc = vp.getVariant(currVarKey);
                OutFile = vp.getFile().getName();
                // Its a SNP now calculate frequencies
                Allele Ref = vc.getReference();
                List<Allele> Alts = vc.getAlternateAlleles();
                long caseRefCount, caseAltCount, ctrlRefCount, ctrlAltCount;
                double chiSq_pval, fisher_pval, midp_pval;

                for (Allele A : Alts) {

                    Association Assoc = new Association(vc.getChr(), vc.getID(), vc.getStart(), vc.getReference().getBaseString(), A.getBaseString());
                    
                    /* Index '0' is the unexposed (ref) count. */
                    long[] CaseAlleleCount = Assoc.countAlleles(CasePhenos, vc, A.getBaseString());
                    long[] ControlAlleleCount = Assoc.countAlleles(ControlPhenos, vc, A.getBaseString());
                    
                    caseRefCount = CaseAlleleCount[0];
                    caseAltCount = CaseAlleleCount[1];
                    ctrlRefCount = ControlAlleleCount[0];
                    ctrlAltCount = ControlAlleleCount[1];

                    Assoc.setCaseRefCount(caseRefCount);
                    Assoc.setCaseAltCount(caseAltCount);
                    Assoc.setControlRefCount(ctrlRefCount);
                    Assoc.setControlAltCount(ctrlAltCount);

                    chiSq_pval = -1;
                    fisher_pval = -1;
                    midp_pval = -1;
                    if(caseRefCount !=0 && caseAltCount !=0 &&
                    		ctrlRefCount !=0 && ctrlAltCount !=0){
                    	ChiSquareTest test = new ChiSquareTest();
                   		chiSq_pval = test.chiSquareTestDataSetsComparison(ControlAlleleCount, CaseAlleleCount);
                   		

					    FishersExactTest fet = new FishersExactTest(
					    new ContingencyTable2x2((int)ctrlRefCount,
											    (int)caseRefCount,
											    (int)ctrlAltCount,
											    (int)caseAltCount),
											    H1.NOT_EQUAL);
					    fisher_pval = fet.getSP();
					    midp_pval = fet.getOneTailedMidP() * 2; // double to get 2-sided p-val
                    }


                    Assoc.setMidP_pval(midp_pval);
				    Assoc.setFishersExact_pval(fisher_pval);
                    Assoc.setChiSq_pval(chiSq_pval);

                    double OR = Assoc.calcOR(caseRefCount, caseAltCount,
                    		ctrlRefCount, ctrlAltCount);

                    Assoc.setOR(OR);
                    association.add(Assoc);

                }
                count++;
                vc = vp.getNextVar();
            }

        	logger.info("Printing association results for " + vp.getPoolID());
            printToFile(association, OutFile);

        }

    }

    private HashMap<String, String> ParsePhenoFile(List<String> phenofiles) throws IOException {
        HashMap<String, String> phenos = new HashMap<String, String>();
        for (Object o : phenofiles) {
            // lets parse the phenotype file.
            BufferedReader br;
			br = new BufferedReader(new FileReader(o.toString()));
			String line;
			while ((line = br.readLine()) != null) {
			    // process the line.
			    String line1[] = line.split("\t");
			    
			    if(line1.length != 2){
					br.close();
				    throw new IOException("ERROR: Found " + line1.length +
						    " columns. Expected 2. Verify the file is tab-delimited.");
			    }

			  //  System.out.println(line);
			    phenos.put(line1[0], line1[1]);
			}
			br.close();

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
            out.write("Chr" + '\t' + "ID" + '\t' + "Pos" + '\t' + "Ref" + '\t'
            + "Alt" + '\t' + "CaseRefCount" + '\t' + "CaseAltCount" + '\t'
		    + "ControlRefCount" + '\t' + "ControlAltCount" + '\t' + "OR" + '\t'
            + "MidP pval (2-sided)" + "\t"
		    + "Fisher Exact pval (2-sided)" + '\t' + "ChiSq pval (2-sided)"
            + '\n');
            for (Association a : A) {
                out.write(a.toString() + "\n");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	

}
