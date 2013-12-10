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
import java.util.List;
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
import vtc.tools.utilitybelt.UtilityBelt;
import vtc.tools.varstats.AltType;

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
	
	private boolean verbose(){
		return this.verbose;
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
		
		VariantPool complement = new VariantPool(addChr());
		complement.setFile(new File(operationID));
		complement.setPoolID(operationID);	
		complement.addSamples(vp1.getSamples());
		
		Iterator<String> it = vp1.getVariantIterator();
		String currVarKey;
		LinkedHashSet<Allele> allAlleles;
		VariantContext var1 = null, var2 = null;
		boolean keep = false;
		
		/* Track the number of indels that may be the same
		 * but aligned differently.
		 * 
		 * potentialMatchingIndelAlleles: The number of alleles that
		 * may overlap. This will count all alternate alleles in
		 * a record
		 * 
		 * potentialMatchinIndelRecords: The number of variant
		 * records (i.e. lines) in a VariantPool that may overlap
		 */
		int potentialMatchingIndelAlleles = 0;
		int potentialMatchingIndelRecords = 0;
		
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
					else{
						if(verbose()){
							String s = "Not all variant pools contained variant.";
							emitExcludedVariantWarning(s, currVarKey, operationID, null);
						}
					}
				}
				else if(!subtractByGenotype(var1.getGenotypes(), var2.getGenotypes(), type, currVarKey, operationID)){
					keep = true;
				}
			}
			else{
				/* Not found in vp2, so add to complement */
				keep = true;
				
				/* If this variant is an indel, check if there are
				 * overlapping indels that may match but align differently.
				 */
				var1 = vp1.getVariant(currVarKey);
				if(var1.isIndel() || var1.isMixed()){ // At least one alternate is an indel
//					System.out.println("var: " + var1.getChr() + ":" + var1.getStart() + ":"
//						+ var1.getReference() + ":" + var1.getAlternateAlleles());
					int matches = vp2.getOverlappingIndelAlleleCount(var1);
					if(matches > 0){
						potentialMatchingIndelAlleles += matches;
						potentialMatchingIndelRecords++;
					}
				}
			}
			
			if(keep){
				var1 = vp1.getVariant(currVarKey);
				allAlleles.addAll(var1.getAlternateAlleles());
	
				/* Build the VariantContext and add to the VariantPool */
				complement.addVariant(buildVariant(var1,
						new LinkedHashSet<Allele>(var1.getAlleles()),
						new ArrayList<Genotype>(var1.getGenotypes())), false);			
			}
		}
		
		complement.setPotentialMatchingIndelAlleles(potentialMatchingIndelAlleles);
		complement.setPotentialMatchingIndelRecords(potentialMatchingIndelRecords);
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
	private boolean subtractByGenotype(GenotypesContext gc1, GenotypesContext gc2,
			ComplementType type, String currVarKey, String operationID) throws InvalidOperationException{
		
		if(type == ComplementType.HET_OR_HOMO_ALT){

			if(genotypesHetOrHomoAlt(gc1) && genotypesHetOrHomoAlt(gc2)){
				return true;
			}
			if(verbose()){
				String s = "Not all genotypes were het or homo alt.";
				emitExcludedVariantWarning(s, currVarKey, operationID, null);
			}
			return false;
		}
		else if(type == ComplementType.EXACT){
			if(allGenotypesExact(gc1, gc2)){
				return true;
			}
			if(verbose()){
				String s = "Not all genotypes were identical.";
				emitExcludedVariantWarning(s, currVarKey, operationID, null);
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
	public VariantPool performIntersect(IntersectOperation op,
			ArrayList<VariantPool> variantPools, IntersectType type) throws InvalidOperationException{
		
		if(type == null){
			throw new RuntimeException("Received null IntersectType in \'performIntersect.\' Something is very wrong!");
		}
	
		// Get the smallest VariantPool
		VariantPool smallest = getSmallestVariantPool(variantPools);
		
		if(smallest == null){
			throw new RuntimeException("Unable to identify the smallest VariantPool. Something is very wrong.");
		}

		VariantPool intersection = new VariantPool(addChr());
		intersection.setFile(new File(op.getOperationID()));
		intersection.setPoolID(op.getOperationID());

		/* Add all samples from each VariantPool involved in the intersection */
		for(VariantPool vp : variantPools){
			intersection.addSamples(vp.getSamples());
		}

		Iterator<String> it = smallest.getVariantIterator();
		String currVarKey;
		ArrayList<VariantContext> fuzzyVars;
		ArrayList<Genotype> genotypes, fuzzyGenos;
		VariantContext var = null, tmpVar, smallestVar;
		SamplePool sp;
		GenotypesContext gc;
		LinkedHashSet<Allele> allAlleles;
		HashMap<String, Genotype> sampleGenotypes;
		boolean intersects, fuzzyIntersects, allVPsContainVar;
		int potentialMatchingIndelAlleles = 0;
		int potentialMatchingIndelRecords = 0;



		// Iterate over the smallest VariantPool and lookup each variant in the other(s)
		while(it.hasNext()){
			currVarKey = it.next();
			
			var = null;
			intersects = true;
			genotypes = new ArrayList<Genotype>();
			allAlleles = new LinkedHashSet<Allele>();
			
			/* If intersect type is POS, only check that */
			if(type == IntersectType.POS){
				
				smallestVar = smallest.getVariant(currVarKey);
				for(VariantPool vp : variantPools){
					var = vp.getVariant(currVarKey);
					if(var == null || !var.getReference().equals(smallestVar.getReference(), true)){
						if(verbose()){
							String s = "not all variant pools have variant at position " + smallestVar.getStart()
									+ " with reference " + smallestVar.getReference();
							emitExcludedVariantWarning(s, currVarKey, op.getOperationID(), null);
						}
						intersects = false;
						break;
					}
					allAlleles.addAll(var.getAlternateAlleles());
					
					/* Check that the genotypes exist. If they don't create 'NO_CALL' genotypes */
					genotypes.addAll(getCorrectGenotypes(var, vp.getSamples()));
				}
			}
			else{
				/* See if all VariantPools contain this variant before interogating genotypes.
				 * This includes the VP we're iterating over, but lookup is O(n) + O(1), where n is the number
				 * of VariantPools and the O(1) is looking up in a Hash. Not a big deal.
				 * I believe verifying the var at least exists in all VPs first should save time over
				 * interrogating the genotypes along the way.
				 */
				allVPsContainVar = allVariantPoolsContainVariant(variantPools, currVarKey, op.getOperationID());
				if(allVPsContainVar){
	
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
						genotypes = intersectsByGenotype(gc, var, sampleGenotypes, type, currVarKey, op.getOperationID());
						if(genotypes == null){
							intersects = false;
							break;
						}
					}

				}
				else{
					/* If we're here, not all VariantPools had the variant
					 * associated with currVarKey. If it's an indel, check
					 * to see if there are fuzzy matches in all VariantPools
					 * and then see if they intersect by genotype. Getting
					 * tmpVar from "smallest" since it's the VariantPool
					 * that the Iterator originated from. It must have
					 * currVarKey.
					 */
					tmpVar = smallest.getVariant(currVarKey);
					if(tmpVar.isIndel() || tmpVar.isMixed()){
						fuzzyVars = allVariantPoolsContainINDELFuzzyMatching(variantPools, tmpVar, currVarKey);
						if(fuzzyVars != null){
							sampleGenotypes = new HashMap<String, Genotype>();
							fuzzyIntersects = true;
							int count = 0;
							for(VariantContext fuzzyVar : fuzzyVars){
								fuzzyVar = fuzzyVars.get(count);
								fuzzyGenos = intersectsByGenotype(fuzzyVar.getGenotypes(), fuzzyVar,
										sampleGenotypes, type, currVarKey, op.getOperationID());
								if(fuzzyGenos == null){
									fuzzyIntersects = false;
									break;
								}
								count++;
							}
							if(fuzzyIntersects){
								potentialMatchingIndelRecords++;
								
								/* Count the number of same-size indels in this variant. */
								for(Allele alt : tmpVar.getAlternateAlleles()){
									if(UtilityBelt.altTypeIsIndel(UtilityBelt.determineAltType(tmpVar.getReference(), alt))){
										potentialMatchingIndelAlleles++;
									}
								}
							}
						}
					}
				}
			}

			// If all VariantPools contain var and they intersect by IntersectTypes, add it to the new pool
			if(intersects && var != null){
				
				/* add Ref allele */
				allAlleles.add(var.getReference());

				// Build the VariantContext and add to the VariantPool
				intersection.addVariant(buildVariant(var, allAlleles, genotypes), false);
			}
		}
		intersection.setPotentialMatchingIndelRecords(potentialMatchingIndelRecords);
		intersection.setPotentialMatchingIndelAlleles(potentialMatchingIndelAlleles);
		return intersection;
	}
	
	/**
	 * Determine if this variant intersects by genotype.
	 * @param gc
	 * @param var
	 * @param sampleGenotypes
	 * @param type
	 * @param currVarKey
	 * @param operID
	 * @return
	 */
	private ArrayList<Genotype> intersectsByGenotype(GenotypesContext gc,
			VariantContext var, HashMap<String, Genotype> sampleGenotypes,
			IntersectType type, String currVarKey, String operID){
		Iterator<Genotype> genoIt = gc.iterator();
		Genotype geno, correctGeno;
		ArrayList<Genotype> genotypes = new ArrayList<Genotype>();
		
		/* Iterate over the sample genotypes in this GenotypeContext
		 * and determine if they intersect by genotype
		 */
		while(genoIt.hasNext()){
			geno = genoIt.next();
			if(!geno.isAvailable() && type != IntersectType.ALT){
				String s = "Sample is missing genotypes! Cannot intersect by" +
						"genotypes for position " + var.getStart();
				emitExcludedVariantWarning(s, currVarKey, operID, null);
			}
			else if(!intersectsByType(geno, type, sampleGenotypes, currVarKey, operID)){
				return null;
			}
			correctGeno = getCorrectGenotype(var, geno.getSampleName());
			genotypes.add(correctGeno);
			sampleGenotypes.put(geno.getSampleName(), correctGeno);
		}
		return genotypes;
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
			currSize = vp.getNumVarRecords();
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
					if(verbose()){
						String s = "reference alleles do not match between variant pools. Do the reference builds match?";
						emitExcludedVariantWarning(s, varKey, operationID, null);
					}
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
						if(verbose()){
							String s = "alternate alleles do not overlap between variant pools.";
							emitExcludedVariantWarning(s, varKey, operationID, null);
						}
						return false;
					}
				}
			}
			count++;
		}	
		return true;
	}
	
	
	/**
	 * Check if all VariantPools have a potential indel match.
	 * @param variantPools
	 * @param var
	 * @param varKey
	 * @return An ArrayList<VariantContext> with the match from each VariantPool, or null if
	 * any didn't have a match.
	 */
	private ArrayList<VariantContext> allVariantPoolsContainINDELFuzzyMatching(ArrayList<VariantPool> variantPools,
			VariantContext var, String varKey){
		
		VariantContext tmpVar;
		ArrayList<VariantContext> matches = new ArrayList<VariantContext>();
		int indelLength;
		for(VariantPool vp : variantPools){

			tmpVar = vp.getVariant(varKey);

			/* if tmpVar != null, just continue. It matched perfectly */
			if(tmpVar != null){
				matches.add(tmpVar);
				continue;
			}
			
			Allele ref = var.getReference();
			List<Allele> alts = var.getAlternateAlleles();
			for(Allele alt : alts){
				AltType type = UtilityBelt.determineAltType(ref, alt);
				/* Make sure this alt is an indel. If the variant is mixed,
				 * we'll wind up looking at SNVs too.
				 */
				if(!UtilityBelt.altTypeIsIndel(type)){ continue; }
				indelLength = ref.length() > alt.length() ? ref.length() : alt.length(); // length is the longer of the two
				tmpVar = vp.getOverlappingIndel(var.getChr(), var.getStart(), indelLength, type);
				if(tmpVar != null){
					matches.add(tmpVar);
					break;
				}
			}
			if(tmpVar == null){
				/* This VariantPool didn't have a potential match */
				return null;
			}
		}
		return matches;
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
			if(verbose()){
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
				if(verbose()){
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
				if(verbose()){
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
				if(verbose()){
					String s = "is not Heterozygous containing a Ref allele.";
					emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
				}
			}
		}
		else if(type == IntersectType.HET_OR_HOMO_ALT){
			if(geno.isHomVar() || (geno.isHet() && genoContainsRefAllele(geno)))
				return true;
			else{
				if(verbose()){
					String s = "is not Homo Alt or Het containing a Ref allele.";
					emitExcludedVariantWarning(s, currVarKey, operationID, geno.getSampleName());
				}
			}
		}
		else if(type == IntersectType.ALT){
			/* TODO: Create test case for this.
			 */
			/* If IntersectType.ALT, always return true because
			 * the user doesn't care about genotype.
			 */
			return true;
		}
//		else if(type == IntersectType.POS){
//			 /* TODO: Create test case for this.
//			  */
//			/* If IntersectType.POS, always return true because
//			 * the user doesn't care about genotype.
//			 */
//			return true;
//		}
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
		HashMap<Integer, String> fuzzyMatches = new HashMap<Integer, String>();
		Iterator<String> it;
		ArrayList<Genotype> genotypes;
		LinkedHashSet<Allele> alleles;
		int potentialMatchingIndelAlleles = 0;
		int potentialMatchingIndelRecords = 0;
		
		VariantPool union = new VariantPool(addChr());
		union.setFile(new File(op.getOperationID()));
		union.setPoolID(op.getOperationID());

		/* Add all samples from each VariantPool involved in the intersection */
		for(VariantPool vp : variantPools){
			union.addSamples(vp.getSamples());
		}
		
		/* Loop over variantPools */
		for(VariantPool vp : variantPools){
			logger.info("Processing variant pool '" + vp.getPoolID() + "'...");
			int nVars = vp.getNumVarRecords();
			it = vp.getVariantIterator();
			
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

					/* Check that the genotypes exist. If they don't create 'NO_CALL' genotypes */
					genotypes.addAll(getCorrectGenotypes(var, vp.getSamples()));
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
							
							/* Check that the genotypes exist. If they don't create 'NO_CALL' genotypes */
							genotypes.addAll(getCorrectGenotypes(var2, vp2.getSamples()));
							alleles.addAll(var2.getAlleles());
						}
						else{
							genotypes.addAll(generateNoCallGenotypesForSamples(vp.getSamples(), vp2.getSamples()));
							
							/* If var1 is an INDEL, check if there is a fuzzy match */
							if(!varOverlapsFuzzyMatch(var, fuzzyMatches)
									&& (var.isIndel() || var.isMixed())){ // At least one alternate is an indel
								int matches = vp2.getOverlappingIndelAlleleCount(var);
								if(matches > 0){
									/* track the vars that had a fuzzy match. The position
									 * is the key and 'chr:varLength' is the value.
									 */
									fuzzyMatches.put(var.getStart(), var.getChr() +
											":" + (var.getEnd() - var.getStart()));
									potentialMatchingIndelAlleles += matches;
									potentialMatchingIndelRecords++;
								}
							}
						}
						union.addVariant(buildVariant(var, alleles, genotypes), true);
					}
				}
				count++;
			}
		}
		union.setPotentialMatchingIndelAlleles(potentialMatchingIndelAlleles);
		union.setPotentialMatchingIndelRecords(potentialMatchingIndelRecords);
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
	 * Generate NO_CALL genotypes for samples that we don't have data for. And verify
	 * we don't overwrite an existing genotype. Probably only useful for unions where
	 * we might get the same sample in multiple variant pools.
	 * @param samples
	 * @return
	 */
	private ArrayList<Genotype> generateNoCallGenotypesForSamples(TreeSet<String> var1Samples, TreeSet<String> var2Samples){
		
		ArrayList<Genotype> genotypes = new ArrayList<Genotype>();
		for(String s : var2Samples){
			
			/* If var2 has the same samples, don't
			 * overwrite genotypes from var1.
			 */
			if(!var1Samples.contains(s)){
				genotypes.add(generateNoCallGenotypeForSample(s));
			}
		}
		return genotypes;
	}
	
	/**
	 * Test whether a similar variant is recorded in fuzzyMatches. fuzzyMatches uses
	 * the variant position as the key and 'chr:varLength' as the value. If there is
	 * a variant within the length of var and of the same size, consider them fuzzy
	 * matches.
	 * 
	 * @param var
	 * @param fuzzyMatches
	 * @return
	 */
	private boolean varOverlapsFuzzyMatch(VariantContext var, HashMap<Integer, String> fuzzyMatches){
		int indelLength = var.getEnd() - var.getStart();
		String indelLengthString = Integer.toString(indelLength);
		int pos = var.getStart();
		String[] chrAndLengthArray;
		String chrAndLength;
		for(int i = pos - indelLength; i <= pos + indelLength; i++){
			chrAndLength = fuzzyMatches.get(i);
			if(chrAndLength != null){
				chrAndLengthArray = chrAndLength.split(":");
				if(chrAndLengthArray[0].equals(var.getChr()) && chrAndLengthArray[1].equals(indelLengthString)){
					return true;
				}
			}
		}
		return false;
	}

	
	
	
	
	
	
	
	
	/****************************************************
	 * Useful operations
	 */
	
	/**
	 * Determine whether a specific sample has a genotype for the given variant. If
	 * missing, create NO_CALL genotype. Otherwise just return its genotype.
	 * @param var
	 * @param sample
	 * @return
	 */
	private Genotype getCorrectGenotype(VariantContext var, String sample){
		
		/* Check that the genotypes exist. If they don't create 'NO_CALL' genotypes */
		if(var.getGenotypes().size() > 0 && !var.getGenotypes().get(0).isAvailable()){
			return generateNoCallGenotypeForSample(sample);
		}
		return var.getGenotype(sample);
	}
	
	/**
	 * Determine whether the variant has genotypes (not missing). If missing, create NO_CALL
	 * genotypes. Otherwise just return the existing genotypes.
	 * @param var
	 * @param samples
	 * @return
	 */
	private ArrayList<Genotype> getCorrectGenotypes(VariantContext var, TreeSet<String> samples){

		/* Check that the genotypes exist. If they don't create 'NO_CALL' genotypes */
		if(var.getGenotypes().size() > 0 && !var.getGenotypes().get(0).isAvailable()){
			return generateNoCallGenotypesForSamples(samples);
		}
		return new ArrayList<Genotype>(var.getGenotypes());
	}
	
	/**
	 * Generate NO_CALL genotypes for samples that we don't have data for.
	 * @param samples
	 * @return
	 */
	private ArrayList<Genotype> generateNoCallGenotypesForSamples(TreeSet<String> varSamples){
		
		ArrayList<Genotype> genotypes = new ArrayList<Genotype>();
		for(String s : varSamples){
			genotypes.add(generateNoCallGenotypeForSample(s));
		}
		return genotypes;
	}
	
	/**
	 * Generate NO_CALL genotype for a single sample
	 * @param sample
	 * @return
	 */
	private Genotype generateNoCallGenotypeForSample(String sample){
		
		ArrayList<Allele> alleles = new ArrayList<Allele>();
		alleles.add(Allele.NO_CALL);
		alleles.add(Allele.NO_CALL);
		return new GenotypeBuilder(sample, alleles).make();
	}
	
	
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
							"because sample '" + sampleName + "' " + reason;
		}
		logger.warn(message);
		System.out.println("Warning: " + message);	
	}
}
