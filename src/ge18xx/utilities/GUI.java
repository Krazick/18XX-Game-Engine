package ge18xx.utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public interface GUI {
	public static final String NO_TOOL_TIP = "";
	public static final String NULL_STRING = null;
	public static final String EMPTY_STRING = "";
	public static final JPanel NO_PANEL = null;
	public static final JLabel NO_LABEL = null;
	public static final JButton NO_BUTTON = null;
	public static final JCheckBox NO_CHECK_BOX = null;
	public static final JComboBox<String> NO_COMBO_BOX = null;
	public static final JComponent NO_JCOMPONENT = null;
	public static final ButtonGroup NO_BUTTON_GROUP = null;
	public static final JScrollPane NO_SCROLL_PANE = null;
	public static final Color NO_COLOR = null;
	public static final String SPLIT = ";";
	public static final String NEWLINE = "\n";
	public static final Color defaultColor = UIManager.getColor ("Panel.background");
	
	public static int getNumberOfDisplays () {
		int tNumberOfDisplays;
		GraphicsEnvironment tLocalGraphicsEnvironment;
		
		tLocalGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment ();
		tNumberOfDisplays = tLocalGraphicsEnvironment.getScreenDevices ().length;
		
		return tNumberOfDisplays;
	}
	
	public static Dimension getDefaultScreenSize () {
		GraphicsDevice tGraphicsDevice;
		Dimension tDimension;
		
		tGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment ().getDefaultScreenDevice ();
		tDimension = getSizeOfDevice (tGraphicsDevice);
		
		return tDimension;
	}
	
	public static Dimension getScreenSize (int aDeviceIndex) {
		GraphicsDevice tGraphicsDevice;
		GraphicsDevice [] tGraphicsDevices;
		Dimension tDimension;
		
		tGraphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment ().getScreenDevices ();
		if ((aDeviceIndex > 0) && (aDeviceIndex < tGraphicsDevices.length)) {
			tGraphicsDevice = tGraphicsDevices [aDeviceIndex];
			tDimension = getSizeOfDevice (tGraphicsDevice);
		} else {
			tDimension = null;
		}
		
		return tDimension;
	}

	public static Dimension getSizeOfDevice (GraphicsDevice tGraphicsDevice) {
		Dimension tDimension;
		int tWidth;
		int tHeight;
		
		tWidth = tGraphicsDevice.getDisplayMode ().getWidth ();
		tHeight = tGraphicsDevice.getDisplayMode ().getHeight ();
		tDimension = new Dimension (tWidth, tHeight);
		
		return tDimension;
	}
	
	public static Color makeTransparent(Color aSource, int aAlpha) {
		Color tTransparent;
		
		tTransparent = new Color(aSource.getRed(), aSource.getGreen(), aSource.getBlue(), aAlpha);
		
		return tTransparent;
	}
}