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

public class ShowConfigInfoPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "showConfigInfo";
	public static final ElementName EN_CONFIG_INFO = new ElementName ("ConfigInfo");
	public static final AttributeName AN_SHOW = new AttributeName ("show");
	public static final String buttonText = "Show Config Info File (full Path) when saving";
	
	public ShowConfigInfoPreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tShowConfigInfoFrame;

		setDecisionType (decisionType);
		tShowConfigInfoFrame = new JCheckBox ();
		setupCheckbox (this, tShowConfigInfoFrame, buttonText);
	}
	
	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tConfigInfo;
		boolean tShowConfigInfo;
		
		tShowConfigInfo = showConfigInfo ();
		tConfigInfo = aXMLDocument.createElement (EN_CONFIG_INFO);
		tConfigInfo.setAttribute (AN_SHOW, tShowConfigInfo);
		
		return tConfigInfo;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		parseBooleanPreference (aChildNode, AN_SHOW, checkBox);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		gameManager.updateAllFrames ();
	}

	public boolean showConfigInfo () {
		return checkBox.isSelected ();
	}
}
