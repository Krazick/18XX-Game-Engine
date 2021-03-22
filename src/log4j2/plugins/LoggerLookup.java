package log4j2.plugins;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin (name = "userlookup", category = StrLookup.CATEGORY)
public class LoggerLookup implements StrLookup {
	private static String userName = "NONAME";
	
	public static void setUserName (String aUserName) {
		userName = aUserName;
	}
	
    @Override
    public String lookup (String key) {
        return userName;
    }

    @Override
    public  String lookup (LogEvent event, String key) {
        return this.lookup (key);
    }
}