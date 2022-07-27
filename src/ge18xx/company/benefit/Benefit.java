package ge18xx.company.benefit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public abstract class Benefit implements ActionListener {
	public final static ElementName EN_BENEFITS = new ElementName ("Benefits");
	public final static ElementName EN_BENEFIT = new ElementName ("Benefit");
	public final static AttributeName AN_CLASS = new AttributeName ("class");
	public final static AttributeName AN_USED = new AttributeName ("used");
	public final static AttributeName AN_NAME = new AttributeName ("name");
	public final static AttributeName AN_CLOSE_ON_USE = new AttributeName ("closeOnUse");
	public final static AttributeName AN_PASSIVE = new AttributeName ("passive");
	public final static AttributeName AN_ACTOR_TYPE = new AttributeName ("actorType");
	public final static JPanel NO_BUTTON_PANEL = GUI.NO_PANEL;
	public final static Benefit NO_BENEFIT = null;
	public final static String NO_BENEFIT_NAME = null;
	public final static String NAME = "ABSTRACT";

	ActorI.ActorTypes actorType;
	boolean closeOnUse;
	boolean used;
	boolean passive;
	JButton button;
	JPanel buttonPanel;
	PrivateCompany privateCompany;
	Benefit previousBenefitInUse;
	String name;

	public Benefit () {
		setName (NAME);
		setCloseOnUse (false);
		setPassive (true);
		setActorType (ActorI.ActorTypes.NO_TYPE.toString ());
		setDefaults ();
	}

	public Benefit (XMLNode aXMLNode) {
		boolean tClose;
		boolean tPassive;
		String tActorType;

		tActorType = aXMLNode.getThisAttribute (AN_ACTOR_TYPE);
		tClose = aXMLNode.getThisBooleanAttribute (AN_CLOSE_ON_USE);
		tPassive = aXMLNode.getThisBooleanAttribute (AN_PASSIVE);
		setCloseOnUse (tClose);
		setPassive (tPassive);
		setActorType (tActorType);
		setDefaults ();
	}

	public void setName (String aName) {
		name = aName;
	}

	public String getBaseName () {
		return name;
	}

	public String getName () {
		return name;
	}

	private void setDefaults () {
		setUsed (false);
		setButton (GUI.NO_BUTTON);
		setButtonPanel (NO_BUTTON_PANEL);
		setPrivateCompany (PrivateCompany.NO_PRIVATE_COMPANY);
		setPreviousBenefitInUse (NO_BENEFIT);
	}

	protected void setPreviousBenefitInUse (Benefit aPreviousBenefitInUse) {
		previousBenefitInUse = aPreviousBenefitInUse;
	}

	public Benefit getPreviousBenefitInUse () {
		return previousBenefitInUse;
	}

	protected void capturePreviousBenefitInUse (Corporation aCorporation, Benefit aNewBenefit) {
		Benefit tPreviousBenefitInUse;

		tPreviousBenefitInUse = aCorporation.getBenefitInUse ();
		setPreviousBenefitInUse (tPreviousBenefitInUse);
		aCorporation.setBenefitInUse (aNewBenefit);
	}

	protected void setButtonPanel (JPanel aButtonPanel) {
		buttonPanel = aButtonPanel;
	}

	protected JPanel getButtonPanel () {
		return buttonPanel;
	}

	public void removeButton (JPanel aButtonRow) {
		if (hasButton ()) {
			removeButton ();
		}
	}

	protected void removeButton () {
		if (buttonPanel != NO_BUTTON_PANEL) {
			if (button != GUI.NO_BUTTON) {
				buttonPanel.remove (button);
				setButton (GUI.NO_BUTTON);
			}
		}
	}

	public void updateButton () {
		// Must update Button for Non-Passive Benefits by Overriding this Method
	}

	public boolean isAExtraTilePlacement () {
		return false;
	}

	public void enableButton () {
		button.setEnabled (true);
	}

	public void disableButton () {
		button.setEnabled (false);
	}

	public void hideButton () {
		button.setVisible (false);
	}

	public void setToolTip (String aToolTip) {
		button.setToolTipText (aToolTip);
	}

	public void setCorporation (Corporation aCorporation) {
		privateCompany = (PrivateCompany) aCorporation;
	}

	public void setPrivateCompany (PrivateCompany aPrivateCompany) {
		privateCompany = aPrivateCompany;
	}

	public PrivateCompany getPrivateCompany () {
		return privateCompany;
	}

	protected ShareCompany getOwningCompany () {
		ShareCompany tShareCompany = (ShareCompany) Corporation.NO_CORPORATION;
		ActorI tOwner;

		tOwner = privateCompany.getOwner ();
		if (tOwner.isACorporation ()) {
			tShareCompany = (ShareCompany) tOwner;
		}

		return tShareCompany;
	}

	private void setActorType (String aActorType) {
		actorType = ActorI.ActorTypes.fromString (aActorType);
	}

	public void setButton (JButton aButton) {
		button = aButton;
	}

	public JButton getButton () {
		return button;
	}

	public boolean hasButton () {
		boolean tHasButton;

		if (button == GUI.NO_BUTTON) {
			tHasButton = false;
		} else {
			tHasButton = true;
		}

		return tHasButton;
	}

	private void setPassive (boolean aPassive) {
		passive = aPassive;
	}

	public void undoUse () {
		setUsed (false);
		if (shouldConfigure ()) {
			configure (privateCompany, buttonPanel);
		}
	}

	public void setUsed (boolean aUsed) {
		used = aUsed;
	}

	private void setCloseOnUse (boolean aCloseOnUse) {
		closeOnUse = aCloseOnUse;
	}

	public boolean used () {
		return used;
	}

	public boolean closeOnUse () {
		return closeOnUse;
	}

	public boolean passive () {
		return passive;
	}

	public boolean isActiveCompanyBenefit () {
		boolean tIsActiveCompanyBenefit = false;

		if (isACompanyBenefit () && (!passive)) {
			tIsActiveCompanyBenefit = true;
		}

		return tIsActiveCompanyBenefit;
	}

	public boolean isActivePlayerBenefit () {
		boolean tIsActivePlayerBenefit = false;

		if (isAPlayerBenefit () && (!passive)) {
			tIsActivePlayerBenefit = true;
		}

		return tIsActivePlayerBenefit;
	}

	public abstract int getCost ();

	public boolean isACompanyBenefit () {
		boolean tIsACompanyBenefit = false;

		if (actorType.compareTo (ActorI.ActorTypes.ShareCompany) == 0) {
			tIsACompanyBenefit = true;
		}

		return tIsACompanyBenefit;
	}

	public boolean isAPlayerBenefit () {
		boolean tIsAPlayerBenefit = false;

		if (actorType.compareTo (ActorI.ActorTypes.Player) == 0) {
			tIsAPlayerBenefit = true;
		}

		return tIsAPlayerBenefit;
	}

	public boolean shouldConfigure () {
		boolean tShouldConfigure;

		tShouldConfigure = true;
		if (privateCompany.isClosed ()) {
			tShouldConfigure = false;
		} else {
			if (used || passive) {
				tShouldConfigure = false;
			}
	
			if (isAPlayerBenefit ()) {
				if ((!privateCompany.isPlayerOwned ())) {
					tShouldConfigure = false;
				}
			}
		}

		return tShouldConfigure;
	}

	public abstract String getNewButtonLabel ();

	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		setPrivateCompany (aPrivateCompany);
		// Should have sub-class override to configure for the type of Benefit
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
	}

	public boolean realBenefit () {
		return true;
	}

	public void abortUse () {

	}

	public void completeBenefitInUse () {
		setUsed (true);
		removeButton ();
	}

	public boolean changeState () {
		return true;
	}

	protected XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLBenefitElement;

		tXMLBenefitElement = aXMLDocument.createElement (EN_BENEFIT);
		tXMLBenefitElement.setAttribute (AN_USED, used);
		tXMLBenefitElement.setAttribute (AN_NAME, getBaseName ());

		return tXMLBenefitElement;
	}

	protected Benefit findMatchedBenefit (XMLNode aBenefitNode) {
		Benefit tMatchedBenefit = NO_BENEFIT;
		String tBenefitNodeName;

		tBenefitNodeName = aBenefitNode.getThisAttribute (AN_NAME);
		if (tBenefitNodeName.equals (getBaseName ())) {
			tMatchedBenefit = this;
		}

		return tMatchedBenefit;
	}

	public void updateState (XMLNode aBenefitNode) {
		boolean tUsedState;

		tUsedState = aBenefitNode.getThisBooleanAttribute (AN_USED);
		setUsed (tUsedState);
	}
}
