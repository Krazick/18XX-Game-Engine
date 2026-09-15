package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ClientNameInFramePreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "DontBuyTrain";
	public static final ElementName EN_CLIENT_NAME = new ElementName ("ClientName");
	public static final AttributeName AN_IN_FRAME = new AttributeName ("inFrame");
	public static final String buttonText = "Show Client Name in Frame Titles (Network Games Only)";
	
	public ClientNameInFramePreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tClientNameInFrame;

		setDecisionType (decisionType);
		tClientNameInFrame = new JCheckBox ();
		setupCheckbox (this, tClientNameInFrame, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tClientNameElement;
		boolean tShowClientNameInFrame;
		
		tShowClientNameInFrame = showClientNameInFrameTitle ();
		tClientNameElement = aXMLDocument.createElement (EN_CLIENT_NAME);
		tClientNameElement.setAttribute (AN_IN_FRAME, tShowClientNameInFrame);
		
		return tClientNameElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_IN_FRAME, checkBox);
		setDecisionChoice (tChoice);
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		gameManager.updateAllFrames ();
	}
	
	public boolean showClientNameInFrameTitle () {
		return checkBox.isSelected ();
	}
}
