package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class ClientNameInFramePreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final ElementName EN_CLIENT_NAME = new ElementName ("ClientName");
	public static final AttributeName AN_IN_FRAME = new AttributeName ("inFrame");
	public static final String buttonText = "Show Client Name in Frame Titles (Network Games Only)";
	JCheckBox clientNameInFrame;
	
	public ClientNameInFramePreference (GameManager aGameManager) {
		super (aGameManager);
		clientNameInFrame = new JCheckBox ();
		setupCheckbox (this, clientNameInFrame, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (clientNameInFrame);
		super.buildUserPreferences (aUserPreferencesPanel);
	}
	
	public boolean showClientNameInFrameTitle () {
		return clientNameInFrame.isSelected ();
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
		boolean tInFrame;
		
		tInFrame = aChildNode.getThisBooleanAttribute (AN_IN_FRAME);
		clientNameInFrame.setSelected (tInFrame);
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		gameManager.updateAllFrames ();
	}
}
