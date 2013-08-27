/**
 * 
 */
package vtc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author markebbert
 *
 */
public enum Tool {

	SET_OPERATOR("SetOperator", new ArrayList<String>(Arrays.asList(new String[]{"SO", "SetOperator"})));
	
	private String name;
	private ArrayList<String> permittedCommands;
	private Tool(String name, ArrayList<String> permittedCommands){
		this.permittedCommands = permittedCommands;
	}
	
	public String getName(){
		return this.name;
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
}
