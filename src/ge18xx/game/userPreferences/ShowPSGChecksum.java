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

public class ShowPSGChecksum extends TrueFalseDecisionPreference implements ItemListener {
	public static final ElementName EN_SHOW_PSG_CHECKSUM = new ElementName ("PSGChecksum");
	public static final AttributeName AN_SHOW_PSG_CHECKSUM = new AttributeName ("psgChecksum");
	public static final String buttonText = "Show PSG Checksum in Action Report Frame";
	JCheckBox showPSGChecksum;

	public ShowPSGChecksum (GameManager aGameManager) {
		super (aGameManager);
		showPSGChecksum = new JCheckBox ();
		setupCheckbox (this, showPSGChecksum, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (showPSGChecksum);
		super.buildUserPreferences (aUserPreferencesPanel);
	}
	
	public boolean showPSGChecksum () {
		return showPSGChecksum.isSelected ();
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tClientNameElement;
		boolean tShowClientNameInFrame;
		
		tShowClientNameInFrame = showPSGChecksum ();
		tClientNameElement = aXMLDocument.createElement (EN_SHOW_PSG_CHECKSUM);
		tClientNameElement.setAttribute (AN_SHOW_PSG_CHECKSUM, tShowClientNameInFrame);
		
		return tClientNameElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tInFrame;
		
		tInFrame = aChildNode.getThisBooleanAttribute (AN_SHOW_PSG_CHECKSUM);
		showPSGChecksum.setSelected (tInFrame);
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		gameManager.updateAllFrames ();
	}
}
