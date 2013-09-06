/**
 * 
 */
package vtc.tools.setoperator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
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

	
	/****************************************************
	 * Constructors
	 */
	
	public SetOperator(boolean verbose){
		this.verbose = verbose;
		return;
	}
	
//	public SetOperator(Operation op, HashMap<String, VariantPool> variantPools){
//		this.operation = op;
//		this.variantPools = variantPools;
//	}
	
	
	
	/****************************************************
	 * Getters
	 */
	
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

		/* Add all samples from each VariantPool involved in the intersection */
		for(VariantPool vp : variantPools){
			intersection.addSamples(vp.getSamples());
		}


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
//			else{
//				if(this.verbose){
//					String s = "Variant at (chr:pos) " + currVarKey + " in operation " + op.getOperationID() + " excluded " +
//							"because it was not present in all variant pools.";
//					logger.warn(s);
//					System.out.println(s);
//				}
//			}
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
					String s = "Variant at (chr:pos) " + varKey + " in operation " + operationID +
							" excluded because reference alleles" +
							" do not match between variant pools. Do the reference builds match?";
					logger.warn(s);
					System.out.println("Warning: " + s);
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
						String s = "Variant at (chr:pos) " + varKey + " in operation " + operationID +
								" excluded because alternate alleles" +
								" do not overlap between variant pools.";
						logger.warn(s);
						System.out.println("Warning: " + s);
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
				String s = "Variant at (chr:pos) " + currVarKey + " in operation " + operationID + " excluded " +
						"because sample " + geno.getSampleName() + " exists in multiple " +
						"variant pools but the genotype did not match.";
				logger.warn(s);
				System.out.println(s);
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
					String s = "Variant at (chr:pos) " + currVarKey + " in operation " + operationID + " excluded " +
							"because sample " + geno.getSampleName() + " is not Homo Ref.";
					logger.warn(s);
					System.out.println(s);
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
					String s = "Variant at (chr:pos) " + currVarKey + " in operation " + operationID + " excluded " +
							"because sample " + geno.getSampleName() + " is not Homo Alt.";
					logger.warn(s);
					System.out.println(s);
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
					String s = "Variant at (chr:pos) " + currVarKey + " in operation " + operationID + " excluded " +
							"because sample " + geno.getSampleName() + " is not Heterozygous" +
							" containing a Ref allele.";
					logger.warn(s);
					System.out.println(s);
				}
			}
		}
		else if(type == IntersectType.HET_OR_HOMO_ALT){
			if(geno.isHet() || geno.isHomVar())
				return true;
			else{
				if(this.verbose){
					String s = "Variant at (chr:pos) " + currVarKey + " in operation " + operationID + " excluded " +
							"because sample " + geno.getSampleName() + " is Homo Ref" +
							" containing a Ref allele.";
					logger.warn(s);
					System.out.println(s);
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

		return null;
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
		vcBuilder.chr(var.getChr());
		vcBuilder.start(var.getStart());
		vcBuilder.stop(var.getEnd());
		vcBuilder.alleles(alleles);
		vcBuilder.genotypes(genos);
		
		/* TODO: Figure out how to approach attributes (i.e. INFO). */
//		vcBuilder.attributes(var.getAttributes());
		return vcBuilder.make();
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
}
