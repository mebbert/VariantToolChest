/**
 * 
 */
package vtc.tools.setoperator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
	 * Useful operations
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
	
	
	
	
	
	/****************************************************
	 * Complement logic
	 */
	
	public VariantPool performComplement(Operation op, ArrayList<VariantPool> variantPools){
		
		return null;
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
	public VariantPool performIntersect(Operation op, ArrayList<VariantPool> variantPools, IntersectType type) throws InvalidOperationException{
		
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
		boolean intersects, allVPsContainVarAtLoc;




		// Iterate over the smallest VariantPool and lookup each variant in the other(s)
		while(it.hasNext()){
			currVarKey = it.next();
			
			/* See if all VariantPools contain this variant before interogating genotypes.
			 * This includes the VP we're iterating over, but lookup is O(n) + O(1), where n is the number
			 * of VariantPools and the O(1) is looking up in a Hash. Not a big deal.
			 * I believe verifying the var at least exists in all VPs first should save time over
			 * interrogating the genotypes along the way.
			 */
			allVPsContainVarAtLoc = allVariantPoolsContainVariant(variantPools, currVarKey, op.getOperationID());
			if(allVPsContainVarAtLoc){

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
			boolean noRef = true;
			if(geno.isHet()){
				for(Allele a : geno.getAlleles()){
					if(a.isReference()){
						noRef = false;
						return true;
					}
				}
			}
			if(!geno.isHet() || noRef){
				if(this.verbose){
					String s = "is not Heterozygous containing a Ref allele.";
					emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
				}
			}
		}
		else if(type == IntersectType.HET_OR_HOMO_ALT){
			if(geno.isHet() || geno.isHomVar())
				return true;
			else{
				if(this.verbose){
					String s = "is Homo Ref containing a Ref allele.";
					emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
				}
			}
		}
		else if(type == IntersectType.ALT){
			/* If IntersectType.ALT, always return true because
			 * the user doesn't care about genotype.
			 */
			return true;
		}
		else if(type == IntersectType.POS){
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
	
	public VariantPool performUnion(Operation op, ArrayList<VariantPool> variantPools){

		String currVarKey;
		VariantContext var, var2;
		ArrayList<String> processedVarKeys = new ArrayList<String>();
		Iterator<String> it;
		ArrayList<Genotype> genotypes;
		LinkedHashSet<Allele> alleles;
		Allele ref;
		boolean add;
		
		VariantPool union = new VariantPool();
		union.setFile(new File(op.getOperationID()));
		union.setPoolID(op.getOperationID());

		/* Add all samples from each VariantPool involved in the intersection */
		for(VariantPool vp : variantPools){
			union.addSamples(vp.getSamples());
		}
		
		/* Loop over variantPools */
		for(VariantPool vp : variantPools){
			it = vp.getIterator();
			
			/* Iterate over each variant in this pool */
			while(it.hasNext()){
				currVarKey = it.next();
				genotypes = new ArrayList<Genotype>();
				alleles = new LinkedHashSet<Allele>();
				
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
					ref = var.getReference();
					add = true;
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

							/* Check that refs match, otherwise omit */
							if(!ref.equals(var2.getReference(), true)){
								String s = "reference alleles do not match between variant pools. Do the reference builds match?";
								emitExcludedVariantWarning(s, currVarKey, op.getOperationID(), null);
								break;
							}
							
							if(hasMatchingSampleWithDifferentGenotype(var, var2, currVarKey, op.getOperationID())){
								break;
							}
							
							genotypes.addAll(var2.getGenotypes());
							alleles.addAll(var2.getAlleles());
							if(!ref.equals(var2.getReference())){
								String s = "reference alleles do not match between variant pools. Do the reference builds match?";
								emitExcludedVariantWarning(s, currVarKey, op.getOperationID(), null);
							}
						}
						else{
							genotypes.addAll(generateNoCallGenotypesForSamples(vp.getSamples(), vp2.getSamples()));
						}
						union.addVariant(buildVariant(var, alleles, genotypes));
					}
				}
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
	
//	private Genotype buildGenotype(TreeSet<String> allAlts, Genotype geno){
//
//		ArrayList<Allele> alleles = new ArrayList<Allele>(geno.getAlleles());
//		GenotypeBuilder gb = new GenotypeBuilder(geno.getSampleName());
//
//		for(Allele a : alleles){
//			if(a.isReference()){
//			}
//		}
//	}
	
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
	 * Add 'chr' to chromosome if user requests
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
