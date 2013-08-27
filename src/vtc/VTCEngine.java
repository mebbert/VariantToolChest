/**
 * 
 */
package vtc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import vtc.datastructures.InvalidInputFileException;
import vtc.tools.setoperator.InvalidOperationException;
import vtc.tools.setoperator.SetOperatorEngine;

/**
 * @author markebbert
 *
 */
public class VTCEngine implements Engine{
	
	
	public VTCEngine(){
		return;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		VTCEngine vtc = new VTCEngine();
		
		ArgumentParser parser = vtc.instantiateArgParser();
		Namespace parsedArgs;

		try {
			/* args[0] is the argument for VTCEngine specifying the tool to create */
			String[] vtcArgs = new String[] {args[0]};
			
			/* Remove args[0] so we can pass the rest to the appropriate tool */
			String[] toolArgs = removeElement(args, 0);

			/* Parse first argument. Must specify the tool. Rest are passed to appropriate tool */
			parsedArgs = parser.parseArgs(vtcArgs);
			
			/* Get string specifying appropriate tool */
			String toolName = (String) parsedArgs.get("ToolName");
			Tool tool = vtc.getTool(toolName);
			if(tool == null){
				throw new ArgumentParserException("Invalid tool specified: " + toolName, parser);
			}
			
			/* Determine which tool was specified and call it */
			if(tool == Tool.SET_OPERATOR){
				SetOperatorEngine soe = new SetOperatorEngine(toolArgs);
				soe.operate();
			}
			
		} catch (ArgumentParserException e) {
			System.err.println(e.getMessage());
		} catch (InvalidInputFileException e) {
			System.err.println(e.getMessage());
		} catch (InvalidOperationException e) {
			System.err.println(e.getMessage());
		} finally{
			parser.printUsage();
			parser.printHelp();
			System.exit(1);
		}
	}

	/**
	 * Remove element from array 'orig' by copying to a new array without the element
	 * @param orig
	 * @param element
	 * @return a new String[] without element
	 */
	private static String[] removeElement(String[] orig, int element){
	    String[] n = new String[orig.length - 1];
	    System.arraycopy(orig, 0, n, 0, element );
	    System.arraycopy(orig, element+1, n, element, orig.length - element-1);
	    return n;
	}
	
	private ArgumentParser instantiateArgParser(){
		ArgumentParser parser = ArgumentParsers.newArgumentParser("VTC", false, "-");
		parser.description("Variant Tool Chest (VTC) was designed to facilitate various aspects" +
				" of variant analysis in a single set of uniform tool.");
		parser.usage("java -jar vtc.jar ToolName");
		
		parser.addArgument("ToolName").dest("ToolName")
				.help("Specify the tool to use. Available tools are: \n1. SetOperator (SO)\n2. Tool 2\n3. Tool 3");
		
		return parser;
	}
	
	/**
	 * Determine of 'tool' exists in the Variant Tool Chest (VTC). Names are not case-sensitive.
	 * @param tool
	 * @return
	 */
	private Tool getTool(String tool){
		for(Tool t : Tool.values()){
			if(t.permittedCommandsContain(tool)){
				return t;
			}
		}
		return null;
	}
	
}
