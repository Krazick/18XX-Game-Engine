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

public class HexScalesSynchronizedPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "HexScales";
	public static final ElementName EN_HEX_SCALES = new ElementName ("HexScales");
	public static final AttributeName AN_SYNCHRONIZED = new AttributeName ("synchronized");
	public static final String buttonText = "Hex Scales for Map and Tile Tray stay synchronized";

	public HexScalesSynchronizedPreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tHexScalesSynchronized;

		setDecisionType (decisionType);
		tHexScalesSynchronized = new JCheckBox ();
		setupCheckbox (this, tHexScalesSynchronized, buttonText);
	}
	
	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tHexScalesSynchronizedElement;
		boolean tHexScalesSynchronized;
		
		tHexScalesSynchronized = hexScalesSynchronized ();
		tHexScalesSynchronizedElement = aXMLDocument.createElement (EN_HEX_SCALES);
		tHexScalesSynchronizedElement.setAttribute (AN_SYNCHRONIZED, tHexScalesSynchronized);
		
		return tHexScalesSynchronizedElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_SYNCHRONIZED, checkBox);
		setDecisionChoice (tChoice);
	}

	@Override
	public void itemStateChanged (ItemEvent e) {
		gameManager.updateAllFrames ();
	}
	
	public boolean hexScalesSynchronized () {
		return checkBox.isSelected ();
	}
}
