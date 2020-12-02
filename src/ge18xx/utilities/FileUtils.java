package ge18xx.utilities;

import java.io.File;

public class FileUtils {
	private final static String ext18xx = "18xx.";
	public final static String xml = ext18xx + "xml";
	
	/*
	 * Get the extension of a file.
	 */  
	public static String getExtension (File aFile) {
	    String tExtension = null;
	    String s = aFile.getName ();
	    int i = s.lastIndexOf ('.' + ext18xx);
	
	    if ((i > 0) &&  (i < s.length () - 1)) {
	        tExtension = s.substring (i + 1).toLowerCase ();
	    }
	    
	    return tExtension;
	}
}
