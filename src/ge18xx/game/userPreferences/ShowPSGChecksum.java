package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLNode;

public class ShowPSGChecksum extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "showPSGChecksum";
	public static final String buttonText = "Show PSG Checksum in Action Report Frame";
	JCheckBox showPSGChecksum;

	public ShowPSGChecksum (GameManager aGameManager) {
		super (aGameManager);
		setDecisionType (decisionType);
		showPSGChecksum = new JCheckBox ();
		setupCheckbox (this, showPSGChecksum, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (showPSGChecksum);
		super.buildUserPreferences (aUserPreferencesPanel);
	}
	
	public boolean showPSGChecksum () {
		return showPSGChecksum.isSelected ();
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;
		
		tChoice = parseBooleanPreference (aChildNode, AN_CHOICE, showPSGChecksum);
		setDecisionChoice (tChoice);
	}

	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		setDecisionChoice (showPSGChecksum.isSelected ());
		gameManager.updateAllFrames ();
	}
}
