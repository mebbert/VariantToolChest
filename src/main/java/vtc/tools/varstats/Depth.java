package vtc.tools.varstats;

import java.util.ArrayList;

import org.broadinstitute.variant.variantcontext.VariantContext;

import vtc.tools.utilitybelt.UtilityBelt;

public class Depth {
	
	
	private double avgDepth = 0;
	private int minDepth = Integer.MAX_VALUE;
	private int maxDepth = -1;
	private String error = "";
	
	
	public Depth(){}
	
	public double getAvgDepth(){
		return avgDepth;
	}
	
	public String getMinDepth(){
		if(minDepth < Integer.MAX_VALUE){
			return new Integer(minDepth).toString();
		}
		return "NA";
	}
	
	public String getMaxDepth(){
		if(maxDepth >= 0){
			return new Integer(maxDepth).toString();
		}
		return "NA";
	}
	
	public String getError(){
		return error;
	}
	
	
	public void getDepths(VariantContext var, ArrayList<String> samples) {
		
		int numSamples = 0;
		for(String s : samples){
			
			int depth = var.getGenotypes().get(s).getDP();
			if(depth>0){
				numSamples++;
				avgDepth+=depth;
				if(depth>maxDepth)
					maxDepth=depth;
				if(depth<minDepth)
					minDepth=depth;
			}
			else{
				if(error.isEmpty())
					error += s;
				else
					error += ", "+s;
			}
		}
		if(numSamples != 0)
			avgDepth /= numSamples;
		//System.out.println(avgDepth);
		
				
	}
	

	@Override
	public String toString(){
		String temp  = "";
		
		if(minDepth == Integer.MAX_VALUE || maxDepth == -1)
			temp = "\t"+"NA"+"\t"+"NA"+"\t"+"NA";
		else
			temp = "\t"+UtilityBelt.roundDoubleToString(avgDepth)+"\t"+Integer.toString(minDepth)+"\t"+Integer.toString(maxDepth);
		return temp;
	}

}
