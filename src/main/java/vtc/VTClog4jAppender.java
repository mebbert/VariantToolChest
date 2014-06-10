/**
 * 
 */
package vtc;

import java.util.Random;

import org.apache.log4j.DailyRollingFileAppender;

/**
 * @author markebbert
 *
 */
public class VTClog4jAppender extends DailyRollingFileAppender {
	
	@Override
    public void  setFile(String fileName) {
        if (fileName.indexOf("%rnd") >= 0) {
            Random r = new Random();
            fileName = fileName.replaceAll("%rnd", Integer.toString(r.nextInt()));
        }
        super.setFile(fileName);
    }

}
