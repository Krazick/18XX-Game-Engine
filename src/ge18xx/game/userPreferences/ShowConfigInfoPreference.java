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
import geUtilities.xml.XMLNode;

public class ShowConfigInfoPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final ElementName EN_CONFIG_INFO = new ElementName ("ConfigInfo");
	public static final AttributeName AN_SHOW = new AttributeName ("show");
	public static final String buttonText = "Show Config Info File (full Path) when saving";
	JCheckBox showConfigInfoFrame;
	
	public ShowConfigInfoPreference (GameManager aGameManager) {
		super (aGameManager);
		showConfigInfoFrame = new JCheckBox ();
		setupCheckbox (this, showConfigInfoFrame, buttonText);
	}
	
	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (showConfigInfoFrame);
		super.buildUserPreferences (aUserPreferencesPanel);
	}
	
	public boolean showConfigInfoFileInfo () {
		return showConfigInfoFrame.isSelected ();
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tClientNameElement;
		boolean tShowClientNameInFrame;
		
		tShowClientNameInFrame = showConfigInfoFileInfo ();
		tClientNameElement = aXMLDocument.createElement (EN_CONFIG_INFO);
		tClientNameElement.setAttribute (AN_SHOW, tShowClientNameInFrame);
		
		return tClientNameElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		parseBooleanPreference (aChildNode, AN_SHOW, showConfigInfoFrame);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		gameManager.updateAllFrames ();
	}
}
