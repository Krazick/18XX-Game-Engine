package ge18xx.game.userPreferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

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

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		gameManager.updateAllFrames ();
	}
}
