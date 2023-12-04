package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.XMLNode;

public class ConfirmDontBuyTrainPreference extends TrueFalseDecisionPreference implements ItemListener {
	public final static String decisionType = "DontBuyTrain";
	public final static String buttonText = "Provide \"Do Not Buy Train\" Reminder Confirmation Box.";
	JCheckBox confirmDontBuyTrain;

	public ConfirmDontBuyTrainPreference (GameManager aGameManager) {
		super (aGameManager);
		setDecisionType (decisionType);
		confirmDontBuyTrain = new JCheckBox ();
		setupCheckbox (this, confirmDontBuyTrain, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (confirmDontBuyTrain);
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_CHOICE, confirmDontBuyTrain);
		setDecisionChoice (tChoice);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		setDecisionChoice (confirmDontBuyTrain.isSelected ());
	}

	public boolean getConfirmDontBuyTrain () {
		return confirmDontBuyTrain.isSelected ();
	}
}
