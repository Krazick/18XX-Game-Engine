package ge18xx.utilities;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class File18XXFilter extends FileFilter {

	@Override
	public boolean accept (File aFile) {
	    if (aFile.isDirectory ()) {
	        return true;
	    }

	    String tExtension = FileUtils.getExtension (aFile);
	    if (tExtension != null) {
	        if (tExtension.equals (FileUtils.xml)) {
	            return true;
	        } else {
	            return false;
	        }
	    }

		return false;
	}

	@Override
	public String getDescription() {
		return "18XX Save Game - XML";
	}

}
