package ge18xx.utilities;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileGEFilter extends FileFilter {
	String description;
	FileUtils fileUtils;
	
	public FileGEFilter (String aDescription, FileUtils aFileUtils) {
		description = aDescription;
		fileUtils = aFileUtils;
	}
	
	@Override
	public boolean accept (File aFile) {
	    if (aFile.isDirectory ()) {
	        return true;
	    }

	    String tExtension = fileUtils.getExtension (aFile);
	    if (tExtension != GUI.NULL_STRING) {
	        if (tExtension.equals (fileUtils.xml)) {
	            return true;
	        } else {
	            return false;
	        }
	    }

		return false;
	}

	@Override
	public String getDescription () {
		return description;
	}
}
