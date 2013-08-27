/**
 * 
 */
package vtc.tools.setoperator;

import java.util.ArrayList;
import java.util.Iterator;

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

	
	/****************************************************
	 * Constructors
	 */
	
	public SetOperator(){
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
	
	public VariantPool performOperation(Operation op, ArrayList<VariantPool> variantPools, IntersectType type){
		
		Operator o = op.getOperator();
		if(o == Operator.COMPLEMENT){
			return performComplement(op, variantPools);
		}
		else if(o == Operator.INTERSECT){
			return performIntersect(op, variantPools, type);
		}
		else if(o == Operator.UNION){
			return performUnion(op, variantPools);
		}
		else{
			throw new RuntimeException("Something is very wrong! Received an invalid operator: " + o);
		}
	}
	
	
	
	
	
	/****************************************************
	 * Complement logic
	 */
	
	private VariantPool performComplement(Operation op, ArrayList<VariantPool> variantPools){
		
		return new VariantPool();
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
	 */
	private VariantPool performIntersect(Operation op, ArrayList<VariantPool> variantPools, IntersectType type){
		
		if(type == null){
			throw new RuntimeException("Received null IntersectType in \'performIntersect.\' Something is very wrong!");
		}

		/* TODO: Add logic to incorporate sample info in intersect. 
		 * Cases to consider:
		 * 	1. Report only variants where samples are:
		 * 		a. Homo ref
		 * 		b. Het
		 * 		c. Homo minor
		 * 		d. Any combination of the three?
		 * 		e. Must recognize difference between missing and homo ref
		 * 	2. Mode of inhertence aware
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 	1. If two or more single-sample pools with the SAME sample.
		 * 		a. Verify the genotypes are the same for the sample. Otherwise
		 * 			the variant should not be included
		 * 	2. If two or more single-sample pools with DIFFERENT samples.
		 * 		a. 
		 */
		
		// Get the smallest VariantPool
		VariantPool smallest = getSmallestVariantPool(variantPools);
		
		if(smallest == null){
			throw new RuntimeException("Unable to identify the smallest VariantPool. Something is very wrong.");
		}

		Iterator<String> it = smallest.getIterator();
		String currVarKey;
		VariantContext var;
		VariantContextBuilder vcBuilder;
		VariantPool intersection = new VariantPool();
		SamplePool sp;
		GenotypesContext gc;
		Iterator<Genotype> genoIt;
		Genotype geno;
		ArrayList<Genotype> genotypes;
		boolean intersects = true;

		// Iterate over the smallest VariantPool and lookup each variant in the other(s)
		while(it.hasNext()){
			currVarKey = it.next();
			
			/* See if all VariantPools contain this variant before interogating genotypes.
			 * This includes the VP we're iterating over, but lookup is O(n) + O(1), where n is the number
			 * of VariantPools and the O(1) is looking up in a Hash. Not a big deal.
			 * I believe verifying the var at least exists in all VPs first should save time over
			 * interrogating the genotypes along the way.
			 */

			if(allVariantPoolsContainVariant(variantPools, currVarKey)){

				genotypes = new ArrayList<Genotype>();
				vcBuilder = new VariantContextBuilder();
				for(VariantPool vp : variantPools){
					
					var = vp.getVariant(currVarKey);
					
					/* Start building the new VariantContext */
					vcBuilder.chr(var.getChr());
					vcBuilder.start(var.getStart());

					/* Get the SamplePool associated with this VariantPool and get the genotypes for this VariantContext
					 * Iterate over the genotypes and intersect.
					 */
					sp = op.getSamplePool(vp.getPoolID()); // SamplePool must have an associated VariantPool with identical poolID
					gc = var.getGenotypes(sp.getSamples());
					genoIt = gc.iterator();
					while(genoIt.hasNext()){
						geno = genoIt.next();
						if(!intersectsByGenotype(geno, type)){
							intersects = false;
							break;
						}
						genotypes.add(geno);
					}
				}

				// If all VariantPools contain var and they intersect by IntersectTypes, add it to the new pool
				if(intersects){
					vcBuilder.genotypes(genotypes);
					
					// Build the VariantContext and add to the VariantPool
					intersection.addVariant(vcBuilder.make());
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
	 * See if all VariantPools contain the variant of interest
	 * @param variantPools
	 * @param varKey
	 * @return true if all VariantPools contain the variant of interest. False, otherwise.
	 */
	private boolean allVariantPoolsContainVariant(ArrayList<VariantPool> variantPools, String varKey){
		VariantContext var;
		for(VariantPool vp : variantPools){
			var = vp.getVariant(varKey);
			if(var == null){
				return false;
			}
		}	
		return true;
	}
	
	
	/**
	 * Determine if the genotype matches the specified intersect type
	 * @param geno
	 * @param type
	 * @return True if the genotype matches the intersect type
	 */
	private boolean intersectsByGenotype(Genotype geno, IntersectType type){
		if(type == IntersectType.HOMOZYGOUS_REF){
			if(geno.isHomRef())
				return true;
		}
		else if(type == IntersectType.HOMOZYGOUS_ALT){
			if(geno.isHomVar())
				return true;
		}
		else if(type == IntersectType.HETEROZYGOUS){
			if(geno.isHet())
				return true;
		}
		else if(type == IntersectType.HET_OR_HOMO_ALT){
			if(geno.isHet() || geno.isHomVar())
				return true;
		}
		return false;
	}

	
	
	
	/****************************************************
	 * Union logic
	 */
	
	private VariantPool performUnion(Operation op, ArrayList<VariantPool> variantPools){

		return new VariantPool();
	}
}
