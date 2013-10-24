package vtc.tools.varstats;

import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.tools.utilitybelt.UtilityBelt;

public class Depth {
	
	
	private double avgDepth = 0;
	private int minDepth = Integer.MAX_VALUE;
	private int maxDepth = -1;
	private String error = "";
	
	
	Depth(){};
	
	public double getAvgDepth(){
		return avgDepth;
	}
	
	public int getMinDepth(){
		return minDepth;
	}
	
	public int getMaxDepth(){
		return maxDepth;
	}
	
	public String getError(){
		return error;
	}
	
	
	public void getDepths(VariantContext var, Object[] samples) {
		
		int numSamples = 0;
		for(Object s : samples){
			
			int depth = var.getGenotypes().get((String) s).getDP();
			if(depth>0){
				numSamples++;
				avgDepth+=(double)depth;
				if(depth>maxDepth)
					maxDepth=depth;
				if(depth<minDepth)
					minDepth=depth;
			}
			else{
				if(error.isEmpty())
					error += (String) s;
				else
					error += ", "+(String) s;
			}
		}
		if(numSamples != 0)
			avgDepth /= numSamples;
		//System.out.println(avgDepth);
		
				
	}
	

	public String toString(){
		String temp  = "";
		
		if(minDepth == Integer.MAX_VALUE || maxDepth == -1)
			temp = "\t"+"NA"+"\t"+"NA"+"\t"+"NA";
		else
			temp = "\t"+UtilityBelt.roundDouble(avgDepth)+"\t"+Integer.toString(minDepth)+"\t"+Integer.toString(maxDepth);
		return temp;
	}

}
