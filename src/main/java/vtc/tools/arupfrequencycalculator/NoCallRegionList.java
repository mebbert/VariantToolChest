/**
 * 
 */
package vtc.tools.arupfrequencycalculator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author markebbert
 *
 */
public class NoCallRegionList implements Iterator<NoCall> {
	
	private ArrayList<NoCall> noCallRegions;
	private int currIndex;

	public NoCallRegionList(ArrayList<NoCall> noCallRegions){
		this.noCallRegions = noCallRegions;
		currIndex = 0;
	}

	@Override
	public boolean hasNext() {
		if(currIndex < noCallRegions.size() - 1){
			return true;
		}
		return false;
	}

	@Override
	public NoCall next() {
		return noCallRegions.get(currIndex++);
	}
	
	/**
	 * Get the current NoCallRegion. If we've moved
	 * past the end, return null;
	 * @return
	 */
	public NoCall current() {
		try{
            return noCallRegions.get(currIndex);
		} catch (IndexOutOfBoundsException e){
			return null;
		}
	}

	@Override
	public void remove() {
		noCallRegions.remove(currIndex);
	}

}
