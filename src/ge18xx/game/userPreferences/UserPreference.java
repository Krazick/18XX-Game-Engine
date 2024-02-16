package ge18xx.game.userPreferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public abstract class UserPreference implements ActionListener {
	GameManager gameManager;

	public UserPreference (GameManager aGameManager) {
		setGameManager (aGameManager);
	}
	
	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}
	
	public abstract void buildUserPreferences (JPanel aUserPreferencesPanel);
	
	public abstract XMLElement createElement (XMLDocument aXMLDocument);
	
	public abstract void parsePreference (XMLNode aChildNode);
	
	public boolean parseBooleanPreference (XMLNode aChildNode, AttributeName aAttributeName, JCheckBox aCheckBox) {
		boolean tBooleanAttribute;
		
		tBooleanAttribute = aChildNode.getThisBooleanAttribute (aAttributeName);
		aCheckBox.setSelected (tBooleanAttribute);
		
		return tBooleanAttribute;
	}

	public void appendNewElement (XMLElement aXMLElement, XMLDocument aXMLDocument) {
		XMLElement tUserPreferenceElement;
		
		tUserPreferenceElement = createElement (aXMLDocument);
		aXMLElement.appendChild (tUserPreferenceElement);
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		gameManager.updateAllFrames ();
	}
}
