package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.XMLNode;

public class ConfirmBuyPresidentSharePreference extends TrueFalseDecisionPreference implements ItemListener {
	public final static String decisionType = "BuyPresidentShare";
	public final static String buttonText = "Provide Buy President Share confirmation Box";
	JCheckBox confirmBuyPresidentShare;

	public ConfirmBuyPresidentSharePreference (GameManager aGameManager) {
		super (aGameManager);
		setDecisionType (decisionType);
		confirmBuyPresidentShare = new JCheckBox ();
		setupCheckbox (this, confirmBuyPresidentShare, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (confirmBuyPresidentShare);
		super.buildUserPreferences (aUserPreferencesPanel);
	}
	
	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_CHOICE, confirmBuyPresidentShare);
		setDecisionChoice (tChoice);
	}

	@Override
	public void itemStateChanged (ItemEvent e) {
		setDecisionChoice (confirmBuyPresidentShare.isSelected ());
		
	}

	public boolean getConfirmBuyPresidentShare () {
		return confirmBuyPresidentShare.isSelected ();
	}
}
