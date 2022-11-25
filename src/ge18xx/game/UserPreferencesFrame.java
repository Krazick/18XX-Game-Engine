package ge18xx.game;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import ge18xx.toplevel.XMLFrame;

public class UserPreferencesFrame extends XMLFrame {

	private static final long serialVersionUID = 1L;
	JTabbedPane tabbedPane;
	JPanel userPreferencesPanel;
	JPanel frameInfoPanel;
	JPanel colorsPanel;
	JScrollPane userPreferencesSPane;
	JScrollPane frameSPane;
	JScrollPane colorsSPane;
	ButtonGroup buttonGroup;
	JRadioButton playerOrderButtons [];
	
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
		JLabel tColorsLabel;
		JPanel tUserPreferencesPanel;
		
		tabbedPane = new JTabbedPane ();
		
		userPreferencesSPane = new JScrollPane ();
		tUserPreferencesPanel = buildUserPreferences ();
		setUserPrefencesPanel (tUserPreferencesPanel);
		
		frameSPane = new JScrollPane ();
		colorsSPane = new JScrollPane ();
		tabbedPane.setBounds (50, 50, 200, 200);
		tabbedPane.setBackground (Color.blue);
		tabbedPane.setForeground (Color.white);
		tabbedPane.add ("User Preferences", userPreferencesSPane);
		tabbedPane.add ("Frame Info", frameSPane);
		tabbedPane.add ("Colors", colorsSPane);
		
		tColorsPanel = new JPanel ();
		tColorsLabel = new JLabel ("User Color Choices");
		tColorsPanel.add (tColorsLabel);
		setColorsPanel (tColorsPanel);

		add (tabbedPane);
		
		setSize (500, 500);
	}
	
	public JPanel buildUserPreferences () {
		JPanel tUserPreferencesPanel;
		ButtonGroup buttonGroup;
		JLabel tPlayerOrderLabel;
		int tPlayerOrderIndex;
		
		tUserPreferencesPanel = new JPanel ();
		tPlayerOrderLabel = new JLabel ("Player Order in Round Frame");
		tUserPreferencesPanel.add (tPlayerOrderLabel);
		playerOrderButtons = new JRadioButton [4];
		playerOrderButtons [0] = new JRadioButton ("Client Player first");
		playerOrderButtons [0].setSelected (true);
		playerOrderButtons [1]  = new JRadioButton ("Priority Player at Start of Stock Round First"); 
		playerOrderButtons [2]  = new JRadioButton ("Priority Player always First"); 
		playerOrderButtons [3]  = new JRadioButton ("Current Player First"); 
		buttonGroup = new ButtonGroup ();
		for (tPlayerOrderIndex = 0; tPlayerOrderIndex < 4; tPlayerOrderIndex++) {
			buttonGroup.add (playerOrderButtons [tPlayerOrderIndex]);
			tUserPreferencesPanel.add (playerOrderButtons [tPlayerOrderIndex]);
		}
		
		return tUserPreferencesPanel;
	}
	
	public void setUserPrefencesPanel (JPanel aUserPreferencesPanel) {
		userPreferencesPanel = aUserPreferencesPanel;
		userPreferencesPanel.setLayout (new BoxLayout (userPreferencesPanel, BoxLayout.Y_AXIS));
		userPreferencesSPane.setViewportView (userPreferencesPanel);
	}
	
	public void setFrameInfoPanel (JPanel aFrameInfoPanel) {
		frameInfoPanel = aFrameInfoPanel;
		frameSPane.setViewportView (frameInfoPanel);
	}
	
	public void setColorsPanel (JPanel aColorsPanel) {
		colorsPanel = aColorsPanel;
		colorsSPane.setViewportView (colorsPanel);
	}
	
	public int getPlayerOrderPreference () {
		int tPlayerOrderIndex;
		int tSelectedPlayerOrder;
		
		tSelectedPlayerOrder = 0;
		for (tPlayerOrderIndex = 0; tPlayerOrderIndex < 4; tPlayerOrderIndex++) {
			if (playerOrderButtons [tPlayerOrderIndex].isSelected ()) {
				tSelectedPlayerOrder = tPlayerOrderIndex;
			}
		}

		return tSelectedPlayerOrder;
	}
}
