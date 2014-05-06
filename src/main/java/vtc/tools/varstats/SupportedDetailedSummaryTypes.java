/**
 * 
 */
package vtc.tools.varstats;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author markebbert
 *
 */
public enum SupportedDetailedSummaryTypes {

	INDIVIDUAL("Individual", "I", "Print individual summaries.",
			new ArrayList<String>(Arrays.asList(new String[]{"I", "Individual"}))),
    COMBINED("Combined", "C", "Print one combined summary to 'unionedVP_detailed_summary.txt'.",
            new ArrayList<String>(Arrays.asList(new String[]{"C", "Combined"}))),
	;
	
	private String name, shortCommand, briefDescription;
	private ArrayList<String> permittedCommands;
	private SupportedDetailedSummaryTypes(String name, String shortCommand, String briefDescription, ArrayList<String> permittedCommands){
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
