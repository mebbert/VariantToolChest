/**
 * 
 */
package vtc.tools.setoperator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

import vtc.datastructures.SamplePool;
import vtc.datastructures.VariantPool;
import vtc.tools.setoperator.operation.ComplementOperation;
import vtc.tools.setoperator.operation.IntersectOperation;
import vtc.tools.setoperator.operation.InvalidOperationException;
import vtc.tools.setoperator.operation.Operation;

/**
 * @author markebbert
 *
 */
public class SetOperator {

	private static Logger logger = Logger.getLogger(SetOperator.class);
	private boolean verbose;
	private boolean addChr;

	
	/****************************************************
	 * Constructors
	 */
	
	public SetOperator(boolean verbose, boolean addChr){
		this.verbose = verbose;
		this.addChr = addChr;
	}
	
//	public SetOperator(Operation op, HashMap<String, VariantPool> variantPools){
//		this.operation = op;
//		this.variantPools = variantPools;
//	}
	
	
	
	/****************************************************
	 * Getters
	 */
	
	public boolean addChr(){
		return addChr;
	}
//	public Operation getOperation(){
//		return this.operation;
//	}
//	
//	public HashMap<String, VariantPool> getVariantPools(){
//		return this.variantPools;
//	}
	
	
	
	
	
	/****************************************************
	 * Complement logic
	 */
	
	/**
	 * Perform complement across all specified VariantPools. If more than two
	 * VariantPools are specified, perform complements in order. For example,
	 * if 'A-B-C' is specified, subtract B from A and then C from the previous
	 * result.
	 * @param op
	 * @param variantPools
	 * @return
	 * @throws InvalidOperationException 
	 */
	public VariantPool performComplement(ComplementOperation op,
			ArrayList<VariantPool> variantPools, ComplementType type) throws InvalidOperationException{
		
		/* Get VariantPool IDs in order provided to the operation so
		 * we know which VariantPool to subtract from which
		 */
		ArrayList<String> vPoolIDsInOrder = op.getAllPoolIDs();
		
		/* Loop over the IDs in order and put in queue
		 * 
		 */
		LinkedList<VariantPool> vpQueue = new LinkedList<VariantPool>();
		for(String vpID : vPoolIDsInOrder){
			for(VariantPool vp : variantPools){
				if(vp.getPoolID().equals(vpID)){
					vpQueue.add(vp);
				}
			}
		}
		
		/* Perform complement of first two VariantPools
		 * 
		 */
		VariantPool vp1 = vpQueue.pop();
		VariantPool vp2 = vpQueue.pop();
		VariantPool complement = performAComplementB(op.getOperationID(), vp1, vp2, type);
		
		/* If more VariantPools specified, take previous result and
		 * subtract the next VariantPool from it.
		 */
		while(vpQueue.peekFirst() != null){
			complement = performAComplementB(op.getOperationID(), complement, vpQueue.pop(), type);
		}

		return complement;
	}
	
	/**
	 * Perform A complement B (A - B)
	 * TODO: Write good description
	 * 
	 * @param vp1
	 * @param vp2
	 * @return
	 * @throws InvalidOperationException 
	 */
	private VariantPool performAComplementB(String operationID, VariantPool vp1,
			VariantPool vp2, ComplementType type) throws InvalidOperationException{
		
		VariantPool complement = new VariantPool();
		complement.setFile(new File(operationID));
		complement.setPoolID(operationID);	
		complement.addSamples(vp1.getSamples());
		
		Iterator<String> it = vp1.getIterator();
		String currVarKey;
		LinkedHashSet<Allele> allAlleles;
		VariantContext var1 = null, var2 = null;
		boolean keep = false;
		
		/* Iterate over variants in vp1. If found in vp2,
		 * subtract from vp1
		 */
		while(it.hasNext()){
			keep = false;
			allAlleles = new LinkedHashSet<Allele>();
			
			currVarKey = it.next();
			
			/* Check if variant found in vp2 */
			var2 = vp2.getVariant(currVarKey);
			if(var2 != null){
				var1 = vp1.getVariant(currVarKey);
				
				if(type == ComplementType.ALT){
					ArrayList<VariantPool> vps = new ArrayList<VariantPool>();
					vps.add(vp1);
					vps.add(vp2);
					if(!allVariantPoolsContainVariant(vps, currVarKey, operationID)){
						keep = true;
					}
				}
				else if(!subtractByGenotype(var1.getGenotypes(), var2.getGenotypes(), type)){
					keep = true;
				}
			}
			else{
				/* Not found in vp2, so add to complement */
				keep = true;
			}
			
			if(keep){
				var1 = vp1.getVariant(currVarKey);
				allAlleles.addAll(var1.getAlternateAlleles());
	
				/* Build the VariantContext and add to the VariantPool */
				complement.addVariant(buildVariant(var1,
						new LinkedHashSet<Allele>(var1.getAlleles()),
						new ArrayList<Genotype>(var1.getGenotypes())));			
			}
		}
		
		return complement;
	}
	
	/**
	 * Determine whether a variant should be subtracted by genotype
	 * @param gc1
	 * @param gc2
	 * @param type
	 * @return
	 * @throws InvalidOperationException 
	 */
	private boolean subtractByGenotype(GenotypesContext gc1, GenotypesContext gc2, ComplementType type) throws InvalidOperationException{
		
		/* TODO: Add 'verbose' information */
		
		
		if(type == ComplementType.HET_OR_HOMO_ALT){

			if(genotypesHetOrHomoAlt(gc1) && genotypesHetOrHomoAlt(gc2)){
				return true;
			}
			return false;
		}
		else if(type == ComplementType.EXACT){
			if(allGenotypesExact(gc1, gc2)){
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Iterate over GenotypesContext and verify all Genotypes meet ComplementType
	 * requirements
	 * @param gc
	 * @return
	 */
	private boolean genotypesHetOrHomoAlt(GenotypesContext gc){
		Iterator<Genotype> genoIT = gc.iterator();
		Genotype geno;
		
		/* Iterate over gc1 and verify they are all het or homo var */
		while(genoIT.hasNext()){
			geno = genoIT.next();
			if(!(geno.isHet() && genoContainsRefAllele(geno)) && !geno.isHomVar()){
				return false;
			}
		}	
		return true;
	}
	
	/**
	 * Check whether all genotypes in both GenotypesContext objects have identical genotypes
	 * @param gc1
	 * @param gc2
	 * @return
	 * @throws InvalidOperationException
	 */
	private boolean allGenotypesExact(GenotypesContext gc1, GenotypesContext gc2) throws InvalidOperationException{
		
		Iterator<Genotype> genoIT = gc1.iterator();
		Genotype firstGeno, currGeno;
		
		if(genoIT.hasNext()){
			firstGeno = genoIT.next();
		}
		else{
			throw new InvalidOperationException("No sample information for variant");
		}

		while(genoIT.hasNext()){
			currGeno = genoIT.next();
			
			if(!currGeno.sameGenotype(firstGeno)){
				return false;
			}
		}
		
		/* Same for gc2 */
		genoIT = gc2.iterator();
		while(genoIT.hasNext()){
			currGeno = genoIT.next();
			if(!currGeno.sameGenotype(firstGeno)){
				return false;
			}
		}
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	/****************************************************
	 * Intersect logic
	 */
	
	/**
	 * TODO: Write a descriptive description of what intersection is based on
	 * 
	 * @param op
	 * @param variantPools
	 * @param type
	 * @return A VariantPool with all variants that intersect, including only the samples of interest.
	 * @throws InvalidOperationException 
	 */
	public VariantPool performIntersect(IntersectOperation op, ArrayList<VariantPool> variantPools, IntersectType type) throws InvalidOperationException{
		
		if(type == null){
			throw new RuntimeException("Received null IntersectType in \'performIntersect.\' Something is very wrong!");
		}
	
		// Get the smallest VariantPool
		VariantPool smallest = getSmallestVariantPool(variantPools);
		
		if(smallest == null){
			throw new RuntimeException("Unable to identify the smallest VariantPool. Something is very wrong.");
		}

		VariantPool intersection = new VariantPool();
		intersection.setFile(new File(op.getOperationID()));
		intersection.setPoolID(op.getOperationID());

		/* Add all samples from each VariantPool involved in the intersection */
		for(VariantPool vp : variantPools){
			intersection.addSamples(vp.getSamples());
		}

		Iterator<String> it = smallest.getIterator();
		String currVarKey;
		VariantContext var = null;
		SamplePool sp;
		GenotypesContext gc;
		Iterator<Genotype> genoIt;
		Genotype geno;
		ArrayList<Genotype> genotypes;
		LinkedHashSet<Allele> allAlleles;
		HashMap<String, Genotype> sampleGenotypes;
		boolean intersects, allVPsContainVar;



		// Iterate over the smallest VariantPool and lookup each variant in the other(s)
		while(it.hasNext()){
			currVarKey = it.next();
			
			/* See if all VariantPools contain this variant before interogating genotypes.
			 * This includes the VP we're iterating over, but lookup is O(n) + O(1), where n is the number
			 * of VariantPools and the O(1) is looking up in a Hash. Not a big deal.
			 * I believe verifying the var at least exists in all VPs first should save time over
			 * interrogating the genotypes along the way.
			 */
			allVPsContainVar = allVariantPoolsContainVariant(variantPools, currVarKey, op.getOperationID());
			if(allVPsContainVar){

				intersects = true;
				genotypes = new ArrayList<Genotype>();
				allAlleles = new LinkedHashSet<Allele>();
				sampleGenotypes = new HashMap<String, Genotype>();
				for(VariantPool vp : variantPools){
					
					var = vp.getVariant(currVarKey);
					allAlleles.addAll(var.getAlternateAlleles());
					

					/* Get the SamplePool associated with this VariantPool and get the genotypes for this VariantContext
					 * Iterate over the genotypes and intersect.
					 */
					sp = op.getSamplePool(vp.getPoolID()); // SamplePool must have an associated VariantPool with identical poolID
					gc = var.getGenotypes(sp.getSamples());

					
					/* Check if any samples from the SamplePool were missing in the file. If so,
					 * throw error and let user know which samples were missing.
					 */
					if(!gc.containsSamples(sp.getSamples())){
						throwMissingSamplesError(gc, sp, vp, op);
					}

					/* Iterate over the sample genotypes in this GenotypeContext
					 * and determine if they intersect by genotype
					 */
					genoIt = gc.iterator();
					while(genoIt.hasNext()){
						geno = genoIt.next();
						if(!intersectsByType(geno, type, sampleGenotypes, currVarKey, op.getOperationID())){
							intersects = false;
							break;
						}
						sampleGenotypes.put(geno.getSampleName(), geno);
						genotypes.add(geno);
					}

					if(!intersects)
						break;
				}

				// If all VariantPools contain var and they intersect by IntersectTypes, add it to the new pool
				if(intersects && var != null){
					
					/* add Ref allele */
					allAlleles.add(var.getReference());

					// Build the VariantContext and add to the VariantPool
					intersection.addVariant(buildVariant(var, allAlleles, genotypes));
				}
			}
		}
		return intersection;
	}
	
	/**
	 * Determine which VariantPool is smallest to iterate over. Smallest refers only to 
	 * the number of variants and not an associated file size.
	 * @param variantPools
	 * @return The smallest VariantPool (i.e. the one with the fewest variants)
	 */
	private VariantPool getSmallestVariantPool(ArrayList<VariantPool> variantPools){
		VariantPool smallest = null;
		int currSize, currSmallest = -1;
		for(VariantPool vp : variantPools){
			currSize = vp.getCount();
			if(currSize < currSmallest || currSmallest == -1){
				currSmallest = currSize;
				smallest = vp;
			}
		}
		return smallest;
	}
	
	/**
	 * See if all VariantPools contain a variant at the same location. All VariantPools must have the same reference
	 * allele and at least one alt allele in common.
	 * @param variantPools
	 * @param varKey
	 * @return true if all VariantPools contain the variant of interest. False, otherwise.
	 * @throws InvalidOperationException 
	 */
	private boolean allVariantPoolsContainVariant(ArrayList<VariantPool> variantPools, String varKey, String operationID) throws InvalidOperationException{
		VariantContext var;
		Allele ref = null; 
		ArrayList<Allele> alts = null;
		int count = 0;
		boolean commonAlt;
		for(VariantPool vp : variantPools){
			var = vp.getVariant(varKey);
			if(var == null){
				return false;
			}
			/* Track whether the reference and alt alleles are the same across all
			 * VariantPools. If ref is not identical, ignore the variant, emit warning,
			 * and continue. Alts must have at least one in common
			 */
			if(count == 0){
				ref = var.getReference();
				alts = new ArrayList<Allele>(var.getAlternateAlleles());
			}
			else{
				if(!ref.equals(var.getReference(), true)){
					String s = "reference alleles do not match between variant pools. Do the reference builds match?";
					emitExcludedVariantWarning(s, varKey, operationID, null);
					return false;
				}
				else{
					/* Make sure there is at least one alt allele in common with
					 * the alleles from the first VariantPool
					 */
					commonAlt = false;
					for(Allele a : var.getAlternateAlleles()){
						if(alts.contains(a)){
							/* Found one that matches. Break and continue */
							commonAlt = true;
							break;
						}
					}
					
					/* If we didn't find common alt, exclude variant */
					if(!commonAlt){
						String s = "alternate alleles do not overlap between variant pools.";
						emitExcludedVariantWarning(s, varKey, operationID, null);
						return false;
					}
				}
			}
			count++;
		}	
		return true;
	}
	
	
	/**
	 * Determine if the genotype matches the specified intersect type
	 * @param geno
	 * @param type
	 * @return True if the genotype matches the intersect type
	 */
	private boolean intersectsByType(Genotype geno, IntersectType type, HashMap<String, Genotype> sampleGenotypes,
			String currVarKey, String operationID){
		
		/* If any sample is found in multiple VariantPools and the sample's 
		 * genotype is not identical, return false
		 */
		Genotype sg = sampleGenotypes.get(geno.getSampleName());
		if(sg != null && !sg.sameGenotype(geno)){
			if(this.verbose){
				String s = "exists in multiple variant pools but the genotype did not match.";
				emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
			}
			return false;
		}
		else if(type == IntersectType.MATCH_SAMPLE){
			return true;
		}

		if(type == IntersectType.HOMOZYGOUS_REF){
			if(geno.isHomRef())
				return true;
			else{
				if(this.verbose){
					String s = "is not Homo Ref.";
					emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
				}
			}
		}
		else if(type == IntersectType.HOMOZYGOUS_ALT){
			/* Genotype must consist of only alternate alleles,
			 * even if they're different alleles.
			 */
			if(geno.isHomVar())
				return true;
			else{
				if(this.verbose){
					String s = "is not Homo Alt.";
					emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
				}
			}
		}
		else if(type == IntersectType.HETEROZYGOUS){
			/* Intersecting on HETEROZYGOUS assumes that there is
			 * both a ref and alt allele. i.e. having two different
			 * alternate alleles (e.g. 1/2) does not qualify in this logic.
			 */
			if(geno.isHet() && genoContainsRefAllele(geno)){
					return true;
			}
			else{
				if(this.verbose){
					String s = "is not Heterozygous containing a Ref allele.";
					emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
				}
			}
		}
		else if(type == IntersectType.HET_OR_HOMO_ALT){
			if(geno.isHomVar() || (geno.isHet() && genoContainsRefAllele(geno)))
				return true;
			else{
				if(this.verbose){
					String s = "is Homo Ref containing a Ref allele.";
					emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
				}
			}
		}
		else if(type == IntersectType.ALT){
			/* TODO: Create test case for this. I don't think this is working.
			 * Should be placed in performIntersect
			 */
			/* If IntersectType.ALT, always return true because
			 * the user doesn't care about genotype.
			 */
			return true;
		}
		else if(type == IntersectType.POS){
			 /* TODO: Create test case for this. I don't think this is working.
			  * Should be placed in performIntersect
			  */
			/* If IntersectType.POS, always return true because
			 * the user doesn't care about genotype.
			 */
			return true;
		}
		return false;
	}

	
	
	
	
	
	
	
	
	
	
	/****************************************************
	 * Union logic
	 */
	
	/**
	 * Perform union between VariantPools
	 * @param op
	 * @param variantPools
	 * @return
	 */
	public VariantPool performUnion(Operation op, ArrayList<VariantPool> variantPools){
		
		/* TODO: Only perform union on samples specified in operation!
		 * TODO: Add verbose information
		 */

		String currVarKey;
		VariantContext var, var2;
		HashSet<String> processedVarKeys = new HashSet<String>();
		Iterator<String> it;
		ArrayList<Genotype> genotypes;
		LinkedHashSet<Allele> alleles;
//		Allele ref;
		
		VariantPool union = new VariantPool();
		union.setFile(new File(op.getOperationID()));
		union.setPoolID(op.getOperationID());

		/* Add all samples from each VariantPool involved in the intersection */
		for(VariantPool vp : variantPools){
			union.addSamples(vp.getSamples());
		}
		
		/* Loop over variantPools */
		for(VariantPool vp : variantPools){
			logger.info("Processing variant pool '" + vp.getPoolID() + "'...");
			int nVars = vp.getCount();
			it = vp.getIterator();
			
			/* Iterate over each variant in this pool */
			int count = 0;
			while(it.hasNext()){
				currVarKey = it.next();
				genotypes = new ArrayList<Genotype>();
				alleles = new LinkedHashSet<Allele>();
				
				if(count > 1 && count % 10000 == 0) logger.info("Processed " + count + " of " + nVars + " variants...");
				
				/* Track each variant that we've processed
				 * so we don't process it in subsequent VariantPools
				 */
				if(!processedVarKeys.contains(currVarKey)){
					processedVarKeys.add(currVarKey);
	
					/* Get variant and loop over the other VariantPools
					 * and add the samples to the new VariantPool
					 */
					var = vp.getVariant(currVarKey);
					genotypes.addAll(var.getGenotypes());
					alleles.addAll(var.getAlleles());
//					ref = var.getReference();
					for(VariantPool vp2 : variantPools){
						
						/* Skip this VariantPool if it's the same as vp */
						if(vp2.getPoolID().equals(vp.getPoolID())){
							continue;
						}
						
						/* Get the variant from this VariantPool. If exists,
						 * add genotypes. Otherwise, create NO_CALL genotypes
						 */
						var2 = vp2.getVariant(currVarKey);
						if(var2 != null){

							/* TODO: Verify that this is unnecessary. We will assume they are using the
							 * same build. Since variants are stored by 'chr:pos:ref', anything at this
							 * point must match!
							 */
							
							/* Check that refs match, otherwise omit */
//							if(!ref.equals(var2.getReference(), true)){
//								String s = "reference alleles do not match between variant pools. Do the reference builds match?";
//								emitExcludedVariantWarning(s, currVarKey, op.getOperationID(), null);
//								break;
//							}
							
							if(hasMatchingSampleWithDifferentGenotype(var, var2, currVarKey, op.getOperationID())){
								break;
							}
							
							genotypes.addAll(var2.getGenotypes());
							alleles.addAll(var2.getAlleles());						}
						else{
							genotypes.addAll(generateNoCallGenotypesForSamples(vp.getSamples(), vp2.getSamples()));
						}
						union.addVariant(buildVariant(var, alleles, genotypes));
					}
				}
				count++;
			}
		}
		return union;
	}
	
	
	/**
	 * Check if var1 and var2 have an overlapping sample with different genotypes. If so,
	 * return true.
	 * @param var1
	 * @param var2
	 * @param varKey
	 * @param operationID
	 * @return
	 */
	private boolean hasMatchingSampleWithDifferentGenotype(VariantContext var1, VariantContext var2, String varKey, String operationID){
		for(String sampleName : var2.getSampleNames()){
			if(var1.getSampleNames().contains(sampleName)){
				if(!var1.getGenotype(sampleName).sameGenotype(var2.getGenotype(sampleName))){
					String s = "encountered in multiple variant pools but the genotypes" +
							" do not match.";
					emitExcludedVariantWarning(s, varKey, operationID, sampleName);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Generate NO_CALL genotypes for samples that we don't have data for.
	 * @param samples
	 * @return
	 */
	private ArrayList<Genotype> generateNoCallGenotypesForSamples(TreeSet<String> var1Samples, TreeSet<String> var2Samples){
		
		ArrayList<Genotype> genotypes = new ArrayList<Genotype>();
		ArrayList<Allele> alleles;
		Genotype g;

		for(String s : var2Samples){
			
			/* If var2 has the same samples, don't
			 * overwrite genotypes from var1.
			 */
			if(!var1Samples.contains(s)){
				alleles = new ArrayList<Allele>();
				
				/* Create to NO_Call alleles */
				alleles.add(Allele.create(Allele.NO_CALL_STRING));
				alleles.add(Allele.create(Allele.NO_CALL_STRING));
				g = new GenotypeBuilder(s, alleles).make();
				genotypes.add(g);
			}
		}
		return genotypes;
	}
	
	
	
	
	
	
	
	
	
	/****************************************************
	 * Useful operations
	 */
	
	
	/**
	 * Build a new variant from an original and add all alleles and genotypes
	 * 
	 * @param var
	 * @param alleles
	 * @param genos
	 * @return
	 */
	private VariantContext buildVariant(VariantContext var, LinkedHashSet<Allele> alleles, ArrayList<Genotype> genos){
		/* Start building the new VariantContext */
		VariantContextBuilder vcBuilder = new VariantContextBuilder();
		vcBuilder.chr(generateChrString(var.getChr()));
		vcBuilder.start(var.getStart());
		vcBuilder.stop(var.getEnd());
		vcBuilder.alleles(alleles);
		vcBuilder.genotypes(genos);
		
		/* TODO: Figure out how to approach attributes (i.e. INFO). */
//		vcBuilder.attributes(var.getAttributes());
		return vcBuilder.make();
	}

	
	/**
	 * Add or remove 'chr' to chromosome if user requests
	 * @param chr
	 * @return
	 */
	private String generateChrString(String chr){
		if(this.addChr()){
			if(!chr.toLowerCase().startsWith("chr")){
				return "chr" + chr;
			}
		}
		else if(chr.toLowerCase().startsWith("chr")){
			return chr.substring(3);
		}
		return chr;
	}
	
	/**
	 * Iterate over alleles in the genotype and verify if any
	 * are the Ref allele
	 * @param geno
	 * @return
	 */
	private boolean genoContainsRefAllele(Genotype geno){
		for(Allele a : geno.getAlleles()){
			if(a.isReference()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Throw an invalidOperationException specifying which samples are missing that were
	 * specified in the operation
	 * @param gc
	 * @param sp
	 * @param vp
	 * @param op
	 * @throws InvalidOperationException
	 */
	private void throwMissingSamplesError(GenotypesContext gc, SamplePool sp, VariantPool vp, Operation op) throws InvalidOperationException{
		ArrayList<String> missing = getMissingSamples(gc, sp);
		StringBuilder sb = new StringBuilder();
		String delim = "";
	    for (String i : missing) {
	        sb.append(delim).append(i);
	        delim = ", ";
	    }
		throw new InvalidOperationException("The following sample names do not exist " +
				"in the variant pool '" +
				sp.getPoolID() + "' (" + vp.getFile().getName() +
				") as specified in '" +
				op.toString() + "': " + sb.toString());
	}
		
	/**
	 * Determine which samples are missing
	 * @param gc
	 * @param sp
	 * @return
	 */
	private ArrayList<String> getMissingSamples(GenotypesContext gc, SamplePool sp){
		Iterator<String> sampIT = sp.getSamples().iterator();
		String samp;
		ArrayList<String> missingSamps = new ArrayList<String>();
		while(sampIT.hasNext()){
			samp = sampIT.next();
			if(!gc.containsSample(samp)){
				missingSamps.add(samp);
			}
		}
		return missingSamps;
	}
	
	/**
	 * Emit a warning why a variant was excluded in set operation
	 * 
	 * @param reason
	 * @param varKey
	 * @param operationID
	 * @param sampleName
	 */
	private void emitExcludedVariantWarning(String reason, String varKey, String operationID, String sampleName){
		String message;
		if(sampleName == null){
			message = "Variant at (chr:pos) " + varKey + " in operation " + operationID + " excluded because " + reason;
		}
		else{
			message = "Variant at (chr:pos) " + varKey + " in operation " + operationID + " excluded " +
							"because sample " + sampleName + " " + reason;
		}
		logger.warn(message);
		System.out.println("Warning: " + message);	
	}
}
