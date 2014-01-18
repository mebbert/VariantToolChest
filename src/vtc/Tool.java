/**
 * 
 */
package vtc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author markebbert
 *
 */
public enum Tool {

	SET_OPERATOR("SetOperator", "SO", "Perform set operations on variant sets",
			new ArrayList<String>(Arrays.asList(new String[]{"SO", "SetOperator"}))),
    VAR_STATS("VarStats", "VS", "Perform various statistical analyses",
            new ArrayList<String>(Arrays.asList(new String[]{"VS", "VarStats"}))),
    ARUP_FREQUENCY_CALCULATOR("ARUPFrequencyCalculator", "AFC", "Navigate ARUP result directories " +
			"and calculate test-specific variant frequencies",
			new ArrayList<String>(Arrays.asList(new String[]{"AFC", "ARUPFrequencyCalculator"})));
	
	private String name, shortCommand, briefDescription;
	private ArrayList<String> permittedCommands;
	private Tool(String name, String shortCommand, String briefDescription, ArrayList<String> permittedCommands){
		this.name = name;
		this.shortCommand = shortCommand;
		this.briefDescription = briefDescription;
		this.permittedCommands = permittedCommands;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getShortCommand(){
		return this.shortCommand;
	}
	
	public String getBriefDescription(){
		return this.briefDescription;
	}
	
	public ArrayList<String> getPermittedCommands(){
		return this.permittedCommands;
	}
	
	public boolean permittedCommandsContain(String command){
		for(String s : permittedCommands){
			if(s.equalsIgnoreCase(command)){
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		return  getName() + " (" +
				getShortCommand() +
				") -- " + getBriefDescription() +
				".";
	}
}
