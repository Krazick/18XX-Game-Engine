package ge18xx.game.userPreferences;

import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

// User Preference to allow Confirmation of Decisions before the action is completed
// Examples:
//    Buy the President Share of a Stock (in case it was chosen by accident)
//    Perform 'DONE' during Company Operation if train does not have a Train -- not required if company MUST buy a Train

public class ConfirmDecisionPreference extends UserPreference {
	public static final ElementName EN_CONFIRM_DECISION = new ElementName ("ConfirmDecision");
	public static final AttributeName AN_TYPE= new AttributeName ("type");
	public static final AttributeName AN_CHOICE= new AttributeName ("choice");
	public String decisionType;
	boolean decisionChoice;
	JCheckBox checkBox;

	public ConfirmDecisionPreference (GameManager aGameManager) {
		super (aGameManager);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		aUserPreferencesPanel.add (Box.createVerticalStrut (5));
	}

	public void setDecisionType (String aDecisionType) {
		decisionType = aDecisionType;
	}
	
	public String getDecisionType () {
		return decisionType;
	}
	
	public void setDecisionChoice (boolean aDecisionChoice) {
		decisionChoice = aDecisionChoice;
	}
	
	public boolean getDecisionChoice () {
		return decisionChoice;
	}
	
	protected void setupCheckbox (ItemListener aItemListener, JCheckBox aCheckBox, String aButtonText) {
		checkBox = aCheckBox;
		checkBox.setSelected (false);
		checkBox.addItemListener (aItemListener);
		checkBox.addActionListener (this);
		checkBox.setText (aButtonText);	
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tConfirmDecisionElement;
		
		tConfirmDecisionElement = aXMLDocument.createElement (EN_CONFIRM_DECISION);
		tConfirmDecisionElement.setAttribute (AN_TYPE, decisionType);
		tConfirmDecisionElement.setAttribute (AN_CHOICE, decisionChoice);
		
		return tConfirmDecisionElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		
	}
}
