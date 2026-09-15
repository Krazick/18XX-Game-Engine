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

public class ConfirmBuyPresidentSharePreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "BuyPresidentShare";
	public static final ElementName EN_CONFIRM_BUY = new ElementName ("ConfirmBuy");
	public static final AttributeName AN_PRESIDENT_SHARE = new AttributeName ("presidentShare");
	public static final String buttonText = "Provide Buy President Share confirmation Box";

	public ConfirmBuyPresidentSharePreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tConfirmBuyPresidentShare;

		setDecisionType (decisionType);
		tConfirmBuyPresidentShare = new JCheckBox ();
		setupCheckbox (this, tConfirmBuyPresidentShare, buttonText);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tClientNameElement;
		boolean tConfirmBuyPresidentShare;
		
		tConfirmBuyPresidentShare = getConfirmBuyPresidentShare ();
		tClientNameElement = aXMLDocument.createElement (EN_CONFIRM_BUY);
		tClientNameElement.setAttribute (AN_PRESIDENT_SHARE, tConfirmBuyPresidentShare);
		
		return tClientNameElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_PRESIDENT_SHARE, checkBox);
		setDecisionChoice (tChoice);
	}

	@Override
	public void itemStateChanged (ItemEvent e) {
		setDecisionChoice (checkBox.isSelected ());
	}

	public boolean getConfirmBuyPresidentShare () {
		return checkBox.isSelected ();
	}
}
