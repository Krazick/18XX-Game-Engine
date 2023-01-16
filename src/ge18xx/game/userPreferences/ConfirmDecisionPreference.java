package ge18xx.game.userPreferences;

import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

// User Preference to allow Confirmation of Decisions before the action is completed
// Examples:
//    Buy the President Share of a Stock (in case it was chosen by accident)
//    Perform 'DONE' during Company Operation if train does not have a Train -- not required if company MUST buy a Train

public class ConfirmDecisionPreference extends UserPreference {

	public ConfirmDecisionPreference (GameManager aGameManager) {
		super (aGameManager);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		return null;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		
	}
}
