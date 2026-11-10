package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import ge18xx.game.GameManager;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class PauseAfterAutoSavePreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "PauseAfterAutoSave";
	public static final ElementName EN_PAUSE_AFTER_AUTO_SAVE = new ElementName ("PauseAfterAutoSave");
	public static final AttributeName AN_PAUSE = new AttributeName ("pause");
	public static final String buttonText = "Pause after every Auto Save";

	public PauseAfterAutoSavePreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tPauseAfterAutoSave;

		setDecisionType (decisionType);
		tPauseAfterAutoSave = new JCheckBox ();
		setupCheckbox (this, tPauseAfterAutoSave, buttonText);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tPauseAfterAutoSaveElement;
		boolean tPauseAfterAutoSave;
		
		tPauseAfterAutoSave = pauseAfterAutoSave ();
		tPauseAfterAutoSaveElement = aXMLDocument.createElement (EN_PAUSE_AFTER_AUTO_SAVE);
		tPauseAfterAutoSaveElement.setAttribute (AN_PAUSE, tPauseAfterAutoSave);
		
		return tPauseAfterAutoSaveElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_PAUSE, checkBox);
		setDecisionChoice (tChoice);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aEvent) {
		gameManager.updateAllFrames ();
	}
	
	public boolean pauseAfterAutoSave () {
		return checkBox.isSelected ();
	}
}
