package ge18xx.company.benefit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.PrivateCompany;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
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
	public final static JButton NO_BUTTON = null;
	public final static JPanel NO_BUTTON_PANEL = null;
	public final static Benefit NO_BENEFIT = null;
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

	public void setName (String aName) {
		name = aName;
	}
	
	public String getBaseName () {
		return name;
	}
	
	public String getName () {
		return name;
	}
	
	private void setDefaults() {
		setUsed (false);
		setButton (NO_BUTTON);
		setButtonPanel (NO_BUTTON_PANEL);
		setPrivateCompany (CorporationList.NO_PRIVATE_COMPANY);
		setPreviousBenefitInUse (NO_BENEFIT);
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
	
	protected void removeButton () {
		if (buttonPanel != NO_BUTTON_PANEL) {
			if (button != NO_BUTTON) {
				buttonPanel.remove (button);
				setButton (NO_BUTTON);
			}
		}
	}
	
	public void updateButton () {
		
	}
	
	public void enableButton () {
		button.setEnabled (true);
	}
	
	public void disableButton () {
		button.setEnabled (false);
	}
	
	public void setToolTip (String aToolTip) {
		button.setToolTipText (aToolTip);
	}
	
	public void setPrivateCompany (PrivateCompany aPrivateCompany) {
		privateCompany = aPrivateCompany;
	}
	
	public PrivateCompany getPrivateCompany () {
		return privateCompany;
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
		boolean tHasButton = false;
		
		if (button != NO_BUTTON) {
			tHasButton = true;
		}
		
		return tHasButton;
	}
	
	private void setPassive (boolean aPassive) {
		passive = aPassive;
	}
	
	protected void setUsed (boolean aUsed) {
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
	
	public abstract int getCost ();
	
	public boolean isPlayerBenefit () {
		boolean tIsPlayerBenefit = false;
		
		if (actorType.compareTo (ActorI.ActorTypes.Player) == 0) {
			tIsPlayerBenefit = true;
		}
		
		return tIsPlayerBenefit;
	}
	
	public boolean shouldConfigure () {
		boolean tShouldConfigure = true;
		
		if (used || passive) {
			tShouldConfigure = false;
		}
		
		if ((! privateCompany.isPlayerOwned ()) && isPlayerBenefit ()) {
			tShouldConfigure = false;
		}
		
		return tShouldConfigure;
	}
	
	public String getNewButtonLabel () {
		// Should have sub-class override to build label for the type of Benefit
		String tNewButtonText = "";
		
		return tNewButtonText;
	}
	
	public void removeButton (JPanel aButtonRow) {
		if (hasButton ()) {
			aButtonRow.remove (button);
		}
	}
	
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		setPrivateCompany (aPrivateCompany);
		// Should have sub-class override to configure for the type of Benefit
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
	}
	
	public boolean realBenefit () {
		return false;
	}
	
	public void abortUse () {
		
	}
	
	public void completeBenefitUse () {
		
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
}
