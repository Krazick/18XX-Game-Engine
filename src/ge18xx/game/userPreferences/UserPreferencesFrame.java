package ge18xx.game.userPreferences;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class UserPreferencesFrame extends XMLFrame {
	public static final ElementName EN_USER_PREFERENCES = new ElementName ("UserPreferences");
	private static final long serialVersionUID = 1L;
	JTabbedPane tabbedPane;
	JPanel userPreferencesPanel;
	JPanel frameInfoPanel;
	JPanel colorsPanel;
	JScrollPane userPreferencesSPane;
	JScrollPane frameSPane;
	JScrollPane colorsSPane;
	PlayerOrderPreference playerOrderPreference;
	ClientNameInFramePreference clientNameInFramePreference;
	ShowConfigInfoPreference showConfigInfoPreference;
	
	public UserPreferencesFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		
		setupJTabbedPane (aGameManager);
	}
	
	private void setupJTabbedPane (GameManager aGameManager) {
		JPanel tColorsPanel;
		JLabel tColorsLabel;
		JPanel tUserPreferencesPanel;
		Point tFrameOffset;
		
		tabbedPane = new JTabbedPane ();
		
		userPreferencesSPane = new JScrollPane ();
		tUserPreferencesPanel = buildUserPreferences (aGameManager);
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
		
		tFrameOffset = gameManager.getOffsetGEFrame ();
		setLocation (tFrameOffset);
		setSize (500, 500);
	}
	
	public JPanel buildUserPreferences (GameManager aGameManager) {
		JPanel tUserPreferencesPanel;
		
		tUserPreferencesPanel = new JPanel ();
		playerOrderPreference = new PlayerOrderPreference (aGameManager);
		playerOrderPreference.buildUserPreferences (tUserPreferencesPanel);
		
		clientNameInFramePreference = new ClientNameInFramePreference (aGameManager);
		clientNameInFramePreference.buildUserPreferences (tUserPreferencesPanel);
		
		showConfigInfoPreference = new ShowConfigInfoPreference (aGameManager);
		showConfigInfoPreference.buildUserPreferences (tUserPreferencesPanel);
		
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
	
	public String getFirstPlayerName () {
		String tFirstPlayerName;
		
		tFirstPlayerName = playerOrderPreference.getFirstPlayerName ();
		
		return tFirstPlayerName;
	}
	
	public boolean showClientNameInFrameTitle () {
		return clientNameInFramePreference.showClientNameInFrameTitle ();
	}
	
	public boolean showConfigInfoFileInfo () {
		return showConfigInfoPreference.showConfigInfoFileInfo ();
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tPreferencesElement;
		XMLElement tPlayerOrderElement;
		XMLElement tClientNameElement;
		
		tPreferencesElement = aXMLDocument.createElement (EN_USER_PREFERENCES);
		tPlayerOrderElement = playerOrderPreference.createElement (aXMLDocument);
		tPreferencesElement.appendChild (tPlayerOrderElement);
		tClientNameElement = clientNameInFramePreference.createElement (aXMLDocument);
		tPreferencesElement.appendChild (tClientNameElement);
		
		return tPreferencesElement;
	}
	
	public void parsePreferences (XMLNode aPreferencesNode) {
		NodeList tChildren;
		XMLNode tChildNode;
		int tNodeCount;
		int tNodeIndex;
		String tNodeName;

		tChildren = aPreferencesNode.getChildNodes ();
		tNodeCount = tChildren.getLength ();
		try {
			for (tNodeIndex = 0; tNodeIndex < tNodeCount; tNodeIndex++) {
				tChildNode = new XMLNode (tChildren.item (tNodeIndex));
				tNodeName = tChildNode.getNodeName ();
				if (PlayerOrderPreference.EN_PLAYER_ORDER.equals (tNodeName)) {
					playerOrderPreference.parsePreference (tChildNode);
				}
				if (ClientNameInFramePreference.EN_CLIENT_NAME.equals (tNodeName)) {
					clientNameInFramePreference.parsePreference (tChildNode);
				}
				if (ShowConfigInfoPreference.EN_CONFIG_INFO.equals (tNodeName)) {
					showConfigInfoPreference.parsePreference (tChildNode);
				}
			}
		} catch (Exception tException) {
			System.err.println ("Caught Exception with message ");
			tException.printStackTrace ();
		}
	}
	
	@Override
	public void processWindowEvent (WindowEvent aWindowEvent) {
		super.processWindowEvent (aWindowEvent);
	}
	
	 public void windowClosing(WindowEvent e) {
		 gameManager.updateAllFrames ();
		 gameManager.saveConfig (true);
	 }
}
