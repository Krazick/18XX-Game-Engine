package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.utilities.XMLNode;

public class ConfirmBuyPresidentSharePreference extends ConfirmDecisionPreference implements ItemListener {
	public final static String decisionType = "BuyPresidentShare";
	JCheckBox confirmBuyPresidentShare;

	public ConfirmBuyPresidentSharePreference (GameManager aGameManager) {
		super (aGameManager);
		setDecisionType (decisionType);
		setupCheckbox ();
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

	private void setupCheckbox () {
		if (confirmBuyPresidentShare == null) {
			confirmBuyPresidentShare = new JCheckBox ("Provide Buy President Share confirmation Box");
			confirmBuyPresidentShare.setSelected (false);
			confirmBuyPresidentShare.addItemListener (this);
			confirmBuyPresidentShare.addActionListener (this);
		}
	}

	@Override
	public void itemStateChanged (ItemEvent e) {
		setDecisionChoice (confirmBuyPresidentShare.isSelected ());
		
	}

	public boolean getConfirmBuyPresidentShare () {
		return confirmBuyPresidentShare.isSelected ();
	}
}
