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
	public static final ElementName EN_HEX_SCALES = new ElementName ("HexScales");
	public static final AttributeName AN_SYNCHRONIZED = new AttributeName ("synchronized");
	public static final String buttonText = "Hex Scales for Map and Tile Tray stay synchronized";
	JCheckBox hexScalesSynchronized;

	public HexScalesSynchronizedPreference (GameManager aGameManager) {
		super (aGameManager);
		hexScalesSynchronized = new JCheckBox ();
		setupCheckbox (this, hexScalesSynchronized, buttonText);
	}
	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (hexScalesSynchronized);
		super.buildUserPreferences (aUserPreferencesPanel);
	}
	
	public boolean hexScalesSynchronized () {
		return hexScalesSynchronized.isSelected ();
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tHexScalesElement;
		boolean tSynchronized;
		
		tSynchronized = hexScalesSynchronized ();
		tHexScalesElement = aXMLDocument.createElement (EN_HEX_SCALES);
		tHexScalesElement.setAttribute (AN_SYNCHRONIZED, tSynchronized);
		
		return tHexScalesElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tSynchronized;
		
		tSynchronized = aChildNode.getThisBooleanAttribute (AN_SYNCHRONIZED);
		hexScalesSynchronized.setSelected (tSynchronized);
	}

	@Override
	public void itemStateChanged (ItemEvent e) {
		gameManager.updateAllFrames ();
	}
}
