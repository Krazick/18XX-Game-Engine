package ge18xx.utilities;

import java.io.File;

public class FileUtils {
	private  String GE_extension;
	public String xml;
	
	public FileUtils (String aExtension) {
		GE_extension = aExtension;
		xml = aExtension + "xml";
	}
	
	/*
	 * Get the extension of a file.
	 */  
	public String getExtension (File aFile) {
	    String tExtension = null;
	    String s = aFile.getName ();
	    int i = s.lastIndexOf ('.' + GE_extension);
	
	    if ((i > 0) &&  (i < s.length () - 1)) {
	        tExtension = s.substring (i + 1).toLowerCase ();
	    }
	    
	    return tExtension;
	}
}
