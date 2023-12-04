package ge18xx.game.userPreferences;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.toplevel.XMLFrame;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class UserPreferencesFrame extends XMLFrame {
	public static final ElementName EN_USER_PREFERENCES = new ElementName ("UserPreferences");
	private static final long serialVersionUID = 1L;
	private static final int PlayerOrderIndex = 0;
	private static final int ClientNameIndex = 1;
	private static final int ShowConfigIndex = 2;
	private static final int ConfirmDontBuyTrainIndex = 3;
	private static final int ConfirmBuyPresidentShareIndex = 4;
	private static final int AlwaysShowEscrow = 5;
	JTabbedPane tabbedPane;
	JPanel userPreferencesPanel;
	JPanel frameInfoPanel;
	JPanel colorsPanel;
	JScrollPane userPreferencesSPane;
	JScrollPane frameSPane;
	JScrollPane colorsSPane;
	List <UserPreference> userPreferences;

	
	public UserPreferencesFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		userPreferences = new LinkedList <UserPreference> ();
		setupJTabbedPane (aGameManager);
	}
	
	private void setupJTabbedPane (GameManager aGameManager) {
		JPanel tColorsPanel;
		JLabel tColorsLabel;
		Point tFrameOffset;
		
		tabbedPane = new JTabbedPane ();
		
		userPreferencesSPane = new JScrollPane ();
		buildUserPreferences (aGameManager);
		
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
	
	public void buildUserPreferences (GameManager aGameManager) {
		JPanel tUserPreferencesPanel;
		UserPreference tUserPreference;
		
		tUserPreferencesPanel = new JPanel ();
		setUserPrefencesPanel (tUserPreferencesPanel);
		
		tUserPreference = new PlayerOrderPreference (aGameManager);
		buildUserPreferences (tUserPreference);

		tUserPreference = new ClientNameInFramePreference (aGameManager);
		buildUserPreferences (tUserPreference);

		tUserPreference = new ShowConfigInfoPreference (aGameManager);
		buildUserPreferences (tUserPreference);
		
		tUserPreference = new ConfirmDontBuyTrainPreference (aGameManager);
		buildUserPreferences (tUserPreference);
		
		tUserPreference = new ConfirmBuyPresidentSharePreference (aGameManager);
		buildUserPreferences (tUserPreference);
		
		tUserPreference = new ShowEscrowPreference (aGameManager);
		buildUserPreferences (tUserPreference);
	}
	
	public void buildUserPreferences (UserPreference aUserPreference) {
		aUserPreference.buildUserPreferences (userPreferencesPanel);
		userPreferences.add (aUserPreference);
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
		PlayerOrderPreference tPlayerOrderPreference;
		
		tPlayerOrderPreference = (PlayerOrderPreference) userPreferences.get (PlayerOrderIndex);
		tSelectedPlayerOrder = tPlayerOrderPreference.getPlayerOrderPreference ();

		return tSelectedPlayerOrder;
	}
	
	public String getFirstPlayerName () {
		String tFirstPlayerName;
		PlayerOrderPreference tPlayerOrderPreference;
		
		tPlayerOrderPreference = (PlayerOrderPreference) userPreferences.get (PlayerOrderIndex);
		tFirstPlayerName = tPlayerOrderPreference.getFirstPlayerName ();
		
		return tFirstPlayerName;
	}
	
	public boolean showClientNameInFrameTitle () {
		ClientNameInFramePreference tClientNameInFramePreference;
		
		tClientNameInFramePreference = (ClientNameInFramePreference) userPreferences.get (ClientNameIndex);
		
		return tClientNameInFramePreference.showClientNameInFrameTitle ();
	}
	
	public boolean showConfigInfoFileInfo () {
		ShowConfigInfoPreference tShowConfigInfoPreference;
		
		tShowConfigInfoPreference = (ShowConfigInfoPreference) userPreferences.get (ShowConfigIndex);
		
		return tShowConfigInfoPreference.showConfigInfoFileInfo ();
	}

	public boolean confirmDontBuyTrain () {
		ConfirmDontBuyTrainPreference tConfirmDontBuyTrainPreference;
		
		tConfirmDontBuyTrainPreference = (ConfirmDontBuyTrainPreference) userPreferences.get (ConfirmDontBuyTrainIndex);
		
		return tConfirmDontBuyTrainPreference.getConfirmDontBuyTrain ();
	}

	public boolean confirmBuyPresidentShare () {
		ConfirmBuyPresidentSharePreference tConfirmBuyPresidentSharePreference;
		
		tConfirmBuyPresidentSharePreference = (ConfirmBuyPresidentSharePreference) userPreferences.get (ConfirmBuyPresidentShareIndex);
		
		return tConfirmBuyPresidentSharePreference.getConfirmBuyPresidentShare ();
	}

	public boolean getAlwaysShowEscrowPreference () {
		ShowEscrowPreference tShowEscrowPreference;
		
		tShowEscrowPreference = (ShowEscrowPreference) userPreferences.get (AlwaysShowEscrow);
		
		return tShowEscrowPreference.getAlwaysShowEscrow ();
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tPreferencesElement;
		
		tPreferencesElement = aXMLDocument.createElement (EN_USER_PREFERENCES);
		
		for (UserPreference tUserPreference : userPreferences) {
			tUserPreference.appendNewElement (tPreferencesElement, aXMLDocument);
		}

		return tPreferencesElement;
	}
	
	public void parsePreferences (XMLNode aPreferencesNode) {
		NodeList tChildren;
		XMLNode tChildNode;
		int tNodeCount;
		int tNodeIndex;
		String tNodeName;
		PlayerOrderPreference tPlayerOrderPreference;
		ClientNameInFramePreference tClientNameInFramePreference;
		ShowConfigInfoPreference tShowConfigInfoPreference;
		TrueFalseDecisionPreference tConfirmDecisionPreference;
		
		tChildren = aPreferencesNode.getChildNodes ();
		tNodeCount = tChildren.getLength ();
		try {
			for (tNodeIndex = 0; tNodeIndex < tNodeCount; tNodeIndex++) {
				tChildNode = new XMLNode (tChildren.item (tNodeIndex));
				tNodeName = tChildNode.getNodeName ();
				if (PlayerOrderPreference.EN_PLAYER_ORDER.equals (tNodeName)) {
					tPlayerOrderPreference = (PlayerOrderPreference) userPreferences.get (PlayerOrderIndex);
					tPlayerOrderPreference.parsePreference (tChildNode);
				}
				if (ClientNameInFramePreference.EN_CLIENT_NAME.equals (tNodeName)) {
					tClientNameInFramePreference = (ClientNameInFramePreference) userPreferences.get (ClientNameIndex);
					tClientNameInFramePreference.parsePreference (tChildNode);
				}
				if (ShowConfigInfoPreference.EN_CONFIG_INFO.equals (tNodeName)) {
					tShowConfigInfoPreference = (ShowConfigInfoPreference) userPreferences.get (ShowConfigIndex);
					tShowConfigInfoPreference.parsePreference (tChildNode);
				}
				if (TrueFalseDecisionPreference.EN_CONFIRM_DECISION.equals (tNodeName)) {
					tConfirmDecisionPreference = (TrueFalseDecisionPreference) userPreferences.get (ConfirmDontBuyTrainIndex);
					tConfirmDecisionPreference.parsePreference (tChildNode);
				}
				if (TrueFalseDecisionPreference.EN_CONFIRM_DECISION.equals (tNodeName)) {
					tConfirmDecisionPreference = (TrueFalseDecisionPreference) userPreferences.get (ConfirmBuyPresidentShareIndex);
					tConfirmDecisionPreference.parsePreference (tChildNode);
				}
				if (TrueFalseDecisionPreference.EN_CONFIRM_DECISION.equals (tNodeName)) {
					tConfirmDecisionPreference = (TrueFalseDecisionPreference) userPreferences.get (AlwaysShowEscrow);
					tConfirmDecisionPreference.parsePreference (tChildNode);
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
