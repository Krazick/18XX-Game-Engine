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

public class ShowPSGChecksumPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "showPSGChecksum";
	public static final ElementName EN_SHOW_PSG = new ElementName ("showPSG");
	public static final AttributeName AN_SHOW_CHECKSUM = new AttributeName ("showChecksum");
	public static final String buttonText = "Show PSG Checksum in Action Report Frame";

	public ShowPSGChecksumPreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tShowPSGChecksum;

		setDecisionType (decisionType);
		tShowPSGChecksum = new JCheckBox ();
		setupCheckbox (this, tShowPSGChecksum, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tShowPSGChecksumElement;
		boolean tShowPSGChecksum;
		
		tShowPSGChecksum = showPSGChecksum ();
		tShowPSGChecksumElement = aXMLDocument.createElement (EN_SHOW_PSG);
		tShowPSGChecksumElement.setAttribute (AN_SHOW_CHECKSUM, tShowPSGChecksum);
		
		return tShowPSGChecksumElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;
		
		tChoice = parseBooleanPreference (aChildNode, AN_SHOW_CHECKSUM, checkBox);
		setDecisionChoice (tChoice);
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		setDecisionChoice (checkBox.isSelected ());
		gameManager.updateAllFrames ();
	}
	
	public boolean showPSGChecksum () {
		return checkBox.isSelected ();
	}
}
