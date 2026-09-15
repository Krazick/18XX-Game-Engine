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

public class ShowEscrowPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "AlwaysShowEscrow";
	public static final ElementName EN_SHOW_ESCROW = new ElementName ("ShowEscrow");
	public static final AttributeName AN_FOR_COMPANIES = new AttributeName ("forCompanies");
	public static final String buttonText = "Always Show the Escrow for Companies for games with a Destination.";

	public ShowEscrowPreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tAlwaysShowEscrow;

		setDecisionType (decisionType);
		tAlwaysShowEscrow = new JCheckBox ();
		setupCheckbox (this, tAlwaysShowEscrow, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tShowEscrowElement
		;
		boolean tShowEscrow;
		
		tShowEscrow = getAlwaysShowEscrow ();
		tShowEscrowElement = aXMLDocument.createElement (EN_SHOW_ESCROW);
		tShowEscrowElement.setAttribute (AN_FOR_COMPANIES, tShowEscrow);
		
		return tShowEscrowElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_FOR_COMPANIES, checkBox);
		setDecisionChoice (tChoice);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		setDecisionChoice (checkBox.isSelected ());
	}

	public boolean getAlwaysShowEscrow () {
		return checkBox.isSelected ();
	}
}
