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
public enum SupportedSummaryTypes {

	INDIVIDUAL("Individual", "I", "Print individual summaries.",
			new ArrayList<String>(Arrays.asList(new String[]{"I", "Individual"}))),
    COMBINED("Combined", "C", "Print one combined summary.",
            new ArrayList<String>(Arrays.asList(new String[]{"C", "Combined"}))),
    SIDE_BY_SIDE("SideBySide", "S", "Print individual summaries side-by-side.",
			new ArrayList<String>(Arrays.asList(new String[]{"S", "SideBySide"}))),
    TABLE("Table", "T", "Print individual summaries in a single table.",
			new ArrayList<String>(Arrays.asList(new String[]{"T", "Table"}))),
	;
	
	private String name, shortCommand, briefDescription;
	private ArrayList<String> permittedCommands;
	private SupportedSummaryTypes(String name, String shortCommand, String briefDescription, ArrayList<String> permittedCommands){
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
	
	@Override
	public String toString(){
		return  getName() + " (" +
				getShortCommand() +
				") -- " + getBriefDescription() +
				".";
	}
}
