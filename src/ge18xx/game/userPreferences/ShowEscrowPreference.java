package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLNode;

public class ShowEscrowPreference extends TrueFalseDecisionPreference implements ItemListener {
	public final static String decisionType = "AlwaysShowEscrow";
	public final static String buttonText = "Always Show the Escrow for Companies for games with a Destination.";
	JCheckBox alwaysShowEscrow;

	public ShowEscrowPreference (GameManager aGameManager) {
		super (aGameManager);
		setDecisionType (decisionType);
		alwaysShowEscrow = new JCheckBox ();
		setupCheckbox (this, alwaysShowEscrow, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (alwaysShowEscrow);
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_CHOICE, alwaysShowEscrow);
		setDecisionChoice (tChoice);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		setDecisionChoice (alwaysShowEscrow.isSelected ());
	}

	public boolean getAlwaysShowEscrow () {
		return alwaysShowEscrow.isSelected ();
	}
}
