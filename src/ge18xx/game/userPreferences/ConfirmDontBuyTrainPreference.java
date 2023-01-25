package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.utilities.XMLNode;

public class ConfirmDontBuyTrainPreference extends ConfirmDecisionPreference implements ItemListener {
	public final static String decisionType = "DontBuyTrain";
	JCheckBox confirmDontBuyTrain;

	public ConfirmDontBuyTrainPreference (GameManager aGameManager) {
		super (aGameManager);
		setDecisionType (decisionType);
		setupCheckbox ();
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

	private void setupCheckbox () {
		if (confirmDontBuyTrain == null) {
			confirmDontBuyTrain = new JCheckBox ("Provide don't buy Train confirmation Box");
			confirmDontBuyTrain.setSelected (false);
			confirmDontBuyTrain.addItemListener (this);
			confirmDontBuyTrain.addActionListener (this);
		}
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		setDecisionChoice (confirmDontBuyTrain.isSelected ());
	}

	public boolean showConfirmDontBuyTrain () {
		return confirmDontBuyTrain.isSelected ();
	}
}
