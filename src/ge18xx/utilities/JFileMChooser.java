package ge18xx.utilities;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Point;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class JFileMChooser extends JFileChooser {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JDialog jDialog;
	Point newLocation;

	public JFileMChooser () {
	}

	public JFileMChooser (String currentDirectoryPath) {
		super (currentDirectoryPath);
	}

	public JFileMChooser (File currentDirectory) {
		super (currentDirectory);
	}

	public JFileMChooser (FileSystemView fsv) {
		super (fsv);
	}

	public JFileMChooser (File currentDirectory, FileSystemView fsv) {
		super (currentDirectory, fsv);
	}

	public JFileMChooser (String currentDirectoryPath, FileSystemView fsv) {
		super (currentDirectoryPath, fsv);
	}

	public void setMLocation (Point aNewLocation) {
		newLocation = aNewLocation;
	}

	public void setMLocation (int aX, int aY) {
		Point tNewPoint;

		tNewPoint = new Point (aX, aY);
		setMLocation (tNewPoint);
	}

	@Override
	public JDialog createDialog (Component parent) throws HeadlessException {
		jDialog = super.createDialog (parent);
		jDialog.setLocation (newLocation);
		return jDialog;
	}
}
