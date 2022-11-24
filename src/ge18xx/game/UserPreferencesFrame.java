package ge18xx.game;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import ge18xx.toplevel.XMLFrame;

public class UserPreferencesFrame extends XMLFrame {

	private static final long serialVersionUID = 1L;
	JTabbedPane tabbedPane;
	JPanel userPreferencesPanel;
	JPanel frameInfoPanel;
	JPanel colorsPanel;
	
	public UserPreferencesFrame (String aFrameName) {
		super (aFrameName);
		
		setupJTabbedPane ();
	}

	public UserPreferencesFrame (String aFrameName, String aGameName) {
		super (aFrameName, aGameName);
		
		setupJTabbedPane ();
	}
	
	private void setupJTabbedPane () {
		JPanel tColorsPanel;
		tabbedPane = new JTabbedPane ();
		userPreferencesPanel = new JPanel ();
		
		JTextArea ta = new JTextArea (200, 200);

		userPreferencesPanel.add (ta);
		
		tColorsPanel = new JPanel ();
		tabbedPane.setBounds (50, 50, 200, 200);
		tabbedPane.setBackground (Color.blue);
		tabbedPane.setForeground (Color.white);
		tabbedPane.add ("User Preferences", userPreferencesPanel);
		setColorsPanel (tColorsPanel);

		add (tabbedPane);
		
		setSize (500, 500);
	}
	
	public void setFrameInfoPanel (JPanel aFrameInfoPanel) {
		frameInfoPanel = aFrameInfoPanel;
		tabbedPane.add ("Frame Info", frameInfoPanel);
	}
	
	public void setColorsPanel (JPanel aColorsPanel) {
		colorsPanel = aColorsPanel;
		tabbedPane.add ("Colors", colorsPanel);
	}
}
