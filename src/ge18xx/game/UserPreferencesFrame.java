package ge18xx.game;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	PlayerOrderPreference playerOrderPreference;
	
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
		
		tUserPreferencesPanel = new JPanel ();
		playerOrderPreference = new PlayerOrderPreference ();
		playerOrderPreference.buildUserPreferences (tUserPreferencesPanel);
		
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
		int tSelectedPlayerOrder;
		
		tSelectedPlayerOrder = playerOrderPreference.getPlayerOrderPreference ();

		return tSelectedPlayerOrder;
	}
	
	public String getFirstPlayerName (GameManager aGameManager) {
		String tFirstPlayerName;
		
		tFirstPlayerName = playerOrderPreference.getFirstPlayerName (aGameManager);
		
		return tFirstPlayerName;
	}
}
