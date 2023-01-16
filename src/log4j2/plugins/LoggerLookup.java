package log4j2.plugins;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin (name = "loggerlookup", category = StrLookup.CATEGORY)
public class LoggerLookup implements StrLookup {
	private static String userName = "NONAME";
	private static String appName = "NOAPP";
	public static Logger logger;
	String environmentVersionInfo;

	public static void setAppName (String aAppName) {
		appName = aAppName;
	}

	public static void setUserName (String aUserName) {
		userName = aUserName;
	}

	public String getLookupValue (String key) {
		String tLookupValue = "NONE";

		if (key.equals ("userName")) {
			tLookupValue = userName;
		} else if (key.equals ("appName")) {
			tLookupValue = appName;
		}

		return tLookupValue;
	}

	@Override
	public String lookup (String key) {
		return getLookupValue (key);
	}

	@Override
	public String lookup (LogEvent event, String key) {
		return this.lookup (key);
	}

	public Logger getLogger () {
		return logger;
	}

	public static Logger getLoggerX () {
		return logger;
	}

	public void setupLogger (String aUserName, String aAppName, String aAppVersion, String aConfigDir,
			Class<?> aClass) {
		String tXMLConfigFile;

		LoggerLookup.setUserName (aUserName);
		LoggerLookup.setAppName (aAppName);
		tXMLConfigFile = aConfigDir + File.separator + "log4j2.xml";
		System.setProperty ("log4j2.configurationFile", tXMLConfigFile);
//		System.setProperty ("log4j2.debug", "");
		logger = LogManager.getLogger (aClass);
		logBasicInfo (aUserName, aAppName, aAppVersion);
	}

	private void logBasicInfo (String aUserName, String aAppName, String aAppVersion) {
		String tJavaVersion = System.getProperty ("java.version");
		String tOSName = System.getProperty ("os.name");
		String tOSVersion = System.getProperty ("os.version");
		String tLog4JVersion;
		String tEnvironmentVersionInfo;

		tLog4JVersion = getLog4JVersion ();
		logger.info ("Application: " + aAppName + ", Version " + aAppVersion + " Client " + aUserName);
		tEnvironmentVersionInfo = "Java Version " + tJavaVersion + " OS Name " + tOSName + " OS Version " + tOSVersion +" Log4J2 Version " + tLog4JVersion;
		logger.info (tEnvironmentVersionInfo);
		setVersionInfo (tEnvironmentVersionInfo);
	}

	public void setVersionInfo (String aEnvironmentVersionInfo) {
		environmentVersionInfo = aEnvironmentVersionInfo;
	}
	
	private String getLog4JVersion () {
		String tLog4JVersion = "NOT FOUND";

		try {
			tLog4JVersion = org.apache.logging.log4j.LogManager.class.getPackage ().getImplementationVersion ();
		} catch (Exception e) {
			System.err.println ("Exception Thrown trying to get Log4J Version -- OOPS");
		}

		return tLog4JVersion;
	}

}