package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ConfirmDontBuyTrainPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "DontBuyTrain";
	public static final ElementName EN_CONFIRM_DONT = new ElementName ("ConfirmDont");
	public static final AttributeName AN_BUY_TRAIN = new AttributeName ("buyTrain");
	public static final String buttonText = "Provide \"Do Not Buy Train\" Reminder Confirmation Box.";

	public ConfirmDontBuyTrainPreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tConfirmDontBuyTrain;

		setDecisionType (decisionType);
		tConfirmDontBuyTrain = new JCheckBox ();
		setupCheckbox (this, tConfirmDontBuyTrain, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tClientNameElement;
		boolean tConfirmDontBuyTrain;
		
		tConfirmDontBuyTrain = getConfirmDontBuyTrain ();
		tClientNameElement = aXMLDocument.createElement (EN_CONFIRM_DONT);
		tClientNameElement.setAttribute (AN_BUY_TRAIN, tConfirmDontBuyTrain);
		
		return tClientNameElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_BUY_TRAIN, checkBox);
		setDecisionChoice (tChoice);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		setDecisionChoice (checkBox.isSelected ());
	}

	public boolean getConfirmDontBuyTrain () {
		return checkBox.isSelected ();
	}
}
