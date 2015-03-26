package rules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class OnFail extends TestWatcher {
	
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";

	
    @Override
    protected void failed(Throwable e, Description description) {
    	 BufferedReader br;
		try {
			
			br = new BufferedReader(new FileReader("src/test/java/rules/.failure.txt"));
	    	 String line = null;
	    	 while ((line = br.readLine()) != null) {
	    	   System.out.println(ANSI_CYAN+line);
	    	 }
	    	 System.out.println(ANSI_RESET+"");
	    	 Thread.sleep(2000);
	    	 
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    }
}