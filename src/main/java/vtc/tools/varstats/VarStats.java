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

	    HashMap<String, String> phenoInfo = new HashMap<String, String>();

        if (phenoArgs != null) {
            // Make a structure to read in the phenotype information...
            phenoInfo = ParsePhenoFile(phenoArgs);
        }

        for (VariantPool vp : AllVPs.values()) {

        	logger.info("Collecting case-control status for samples in " + vp.getPoolID());
            ArrayList<Association> association = new ArrayList<Association>();

            String[] sampleList = vp.getSamples().toArray(new String[vp.getSamples().size()]);
            ArrayList<String> casePhenos = new ArrayList<String>();
            ArrayList<String> controlPhenos = new ArrayList<String>();
            for (String sample : sampleList) {
                if (phenoInfo.containsKey(sample)) {
                    String phenotype = phenoInfo.get(sample);
                    // System.out.println(o + phenotype);
                    if (phenotype.equals("2")) {
                        casePhenos.add(sample);

                    } else if (phenotype.equals("1"))
                        controlPhenos.add(sample);
                    // System.out.println(o + "'s pheno is " +phenoInfo.get(o));
                }
            }
            
            if(casePhenos.size() == 0 || controlPhenos.size() == 0){
            	logger.error("Missing either cases or controls in "
				    + vp.getPoolID() + ": "
            		+ casePhenos.size() + " cases, " + controlPhenos.size() +
            		" controls. Exiting...");
            	System.exit(1);
            }
            else{
            	logger.info("Counts in " + vp.getPoolID() + ": "
            		+ casePhenos.size() + " cases, " + controlPhenos.size() +
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

            	if(count > 1 && count % 1000 == 0) System.out.print("\rVariants tested: "
                		+ nf.format(count));

//                currVarKey = it.next();
//                VariantContext vc = vp.getVariant(currVarKey);
                OutFile = vp.getFile().getName();
                // Its a SNP now calculate frequencies
                Allele Ref = vc.getReference();
                List<Allele> Alts = vc.getAlternateAlleles();
                long caseRefCount, caseAltCount, ctrlRefCount, ctrlAltCount;
                double chiSq_pval, fisher_pval, midp_pval;

                for (Allele A : Alts) {

                    Association assoc = new Association(vc.getChr(), vc.getID(), vc.getStart(), vc.getReference().getBaseString(), A.getBaseString());
                    
                    /* Index '0' is the unexposed (ref) count. */
                    assoc.setCaseControlCounts(casePhenos, controlPhenos, vc, A.getBaseString());
                    
                    caseRefCount = assoc.getCaseRefCount();
                    caseAltCount = assoc.getCaseAltCount();
                    ctrlRefCount = assoc.getControlRefCount();
                    ctrlAltCount = assoc.getControlAltCount();


                    chiSq_pval = -1;
                    fisher_pval = -1;
                    midp_pval = -1;
                    if(caseRefCount !=0 && caseAltCount !=0 &&
                    		ctrlRefCount !=0 && ctrlAltCount !=0){
                    	ChiSquareTest test = new ChiSquareTest();
                   		chiSq_pval = test.chiSquareTestDataSetsComparison(
                   				new long[] {ctrlRefCount, ctrlAltCount},
                   				new long[] {caseRefCount, caseAltCount});
                   		

					    FishersExactTest fet = new FishersExactTest(
					    new ContingencyTable2x2((int)ctrlRefCount,
											    (int)caseRefCount,
											    (int)ctrlAltCount,
											    (int)caseAltCount),
											    H1.NOT_EQUAL);
					    fisher_pval = fet.getSP();
					    midp_pval = fet.getOneTailedMidP() * 2; // double to get 2-sided p-val
                    }


                    assoc.setMidP_pval(midp_pval);
				    assoc.setFishersExact_pval(fisher_pval);
                    assoc.setChiSq_pval(chiSq_pval);

                    double OR = assoc.calcOR(caseRefCount, caseAltCount,
                    		ctrlRefCount, ctrlAltCount);

                    assoc.setOR(OR);
                    association.add(assoc);

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
        for (String phenoFile : phenofiles) {
            // lets parse the phenotype file.
            BufferedReader br;
			br = new BufferedReader(new FileReader(phenoFile.toString()));
			String line;
			while ((line = br.readLine()) != null) {
			    // process the line.
			    String lineToks[] = line.split("\t");
			    
			    if(lineToks.length != 2){
					br.close();
				    throw new IOException("ERROR: Found " + lineToks.length +
						    " columns. Expected 2. Verify the file is tab-delimited.");
			    }

			  //  System.out.println(line);
			    phenos.put(lineToks[0], lineToks[1]);
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
            + "Alt" + '\t' + "CaseRefAlleleCount" + '\t' + "CaseAltAlleleCount" + '\t'
		    + "ControlRefAlleleCount" + '\t' + "ControlAltAlleleCount" + '\t' + "OR" + '\t'
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

	public void doPercentage(TreeMap<String, VariantPoolLight> allVPs) throws IOException {
		for(VariantPoolLight vp : allVPs.values()){
			VariantPoolPercents vpp = new VariantPoolPercents(vp);
		}
	}


}
