package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLNode;

public class ShowConfigInfoPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "showConfigInfo";
	public static final String buttonText = "Show Config Info File (full Path) when saving";
	JCheckBox showConfigInfoFrame;
	
	public ShowConfigInfoPreference (GameManager aGameManager) {
		super (aGameManager);
		setDecisionType (decisionType);
		showConfigInfoFrame = new JCheckBox ();
		setupCheckbox (this, showConfigInfoFrame, buttonText);
	}
	
	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (showConfigInfoFrame);
		super.buildUserPreferences (aUserPreferencesPanel);
	}
	
	public boolean showConfigInfoFileInfo () {
		return showConfigInfoFrame.isSelected ();
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		parseBooleanPreference (aChildNode, AN_CHOICE, showConfigInfoFrame);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		gameManager.updateAllFrames ();
	}
}
