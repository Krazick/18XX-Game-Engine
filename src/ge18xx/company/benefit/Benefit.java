package ge18xx.company.benefit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.effects.BenefitUsedEffect;
import ge18xx.round.action.effects.Effect;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import swingTweaks.KButton;

public abstract class Benefit implements ActionListener {
	public static final ElementName EN_BENEFITS = new ElementName ("Benefits");
	public static final ElementName EN_BENEFIT = new ElementName ("Benefit");
	public static final AttributeName AN_CLASS = new AttributeName ("class");
	public static final AttributeName AN_USED = new AttributeName ("used");
	public static final AttributeName AN_ALL_ACTORS = new AttributeName ("allActors");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_CLOSE_ON_USE = new AttributeName ("closeOnUse");
	public static final AttributeName AN_CLOSE_ON_ALL_USED = new AttributeName ("closeOnAllUsed");
	public static final AttributeName AN_PASSIVE = new AttributeName ("passive");
	public static final AttributeName AN_ACTOR_TYPE = new AttributeName ("actorType");
	public static final AttributeName AN_OWNER_TYPE = new AttributeName ("ownerType");
	public static final JPanel NO_BUTTON_PANEL = GUI.NO_PANEL;
	public static final Benefit NO_BENEFIT = null;
	public static final String NO_BENEFIT_NAME = GUI.NULL_STRING;
	public static final String NO_BUTTON_LABEL = GUI.NULL_STRING;
	public static final String NAME = "ABSTRACT";
	public static final Benefit FAKE_BENEFIT = new FakeBenefit (); 

	ActorI.ActorTypes actorType;
	ActorI.ActorTypes ownerType;
	boolean closeOnUse;
	boolean closeOnAllUsed;
	boolean used;
	boolean passive;
	boolean allActors;
	KButton button;
	JPanel buttonPanel;
	protected PrivateCompany privateCompany;
	Benefit previousBenefitInUse;
	String name;
	ArrayList<Effect> additionalEffects;
	
	@Override
	public String toString () {
		String tString;
		
		tString = "Private " + privateCompany.getAbbrev () + " Benefit " + name + " Actor " + actorType;
				
		return tString;
	}
	
	public Benefit () {
		setName (NAME);
		setCloseOnUse (false);
		setPassive (true);
		setUsed (false);
		setActorType (ActorI.ActorTypes.NO_TYPE.toString ());
		setAllActors (false);
		setDefaults ();
		setAdditionalEffects ();
	}

	public Benefit (XMLNode aXMLNode) {
		boolean tCloseOnUse;
		boolean tCloseOnAllUsed;
		boolean tPassive;
		boolean tUsed;
		boolean tAllActors;
		String tActorType;
		String tOwnerType;

		tActorType = aXMLNode.getThisAttribute (AN_ACTOR_TYPE);
		tOwnerType = aXMLNode.getThisAttribute (AN_OWNER_TYPE);
		tCloseOnUse = aXMLNode.getThisBooleanAttribute (AN_CLOSE_ON_USE);
		tCloseOnAllUsed = aXMLNode.getThisBooleanAttribute (AN_CLOSE_ON_ALL_USED);
		tPassive = aXMLNode.getThisBooleanAttribute (AN_PASSIVE);
		tUsed = aXMLNode.getThisBooleanAttribute (AN_USED);
		tAllActors = aXMLNode.getThisBooleanAttribute (AN_ALL_ACTORS);
		setUsed (tUsed);
		setCloseOnUse (tCloseOnUse);
		setCloseOnAllUsed (tCloseOnAllUsed);
		setPassive (tPassive);
		setActorType (tActorType);
		setAllActors (tAllActors);
		setOwnerType (tOwnerType);
		setDefaults ();
		setAdditionalEffects ();
	}

	public void setAdditionalEffects () {
		additionalEffects = new ArrayList<Effect> ();
	}
	
	public void clearAdditionalEffects () {
		setAdditionalEffects ();
	}
	
	public boolean isOwnerTypeBenefit () {
		boolean tOwnerTypeBenefit;
		
		if (ownerType == ActorI.ActorTypes.NO_TYPE) {
			tOwnerTypeBenefit = false;
		} else {
			tOwnerTypeBenefit = true;
		}
		
		return tOwnerTypeBenefit;
	}
	
	public boolean isOwnerTypeAPlayer () {
		boolean tOwnerTypeIsAPlayer;
		
		if (ownerType == ActorI.ActorTypes.Player) {
			tOwnerTypeIsAPlayer = true;
		} else {
			tOwnerTypeIsAPlayer = false;
		}
		
		return tOwnerTypeIsAPlayer;
	}
	
	public boolean isOwnerTypeAShareCompany () {
		boolean tOwnerTypeIsAShareCompany;
		
		if (ownerType == ActorI.ActorTypes.ShareCompany) {
			tOwnerTypeIsAShareCompany = true;
		} else {
			tOwnerTypeIsAShareCompany = false;
		}
		
		return tOwnerTypeIsAShareCompany;
	}
	
	public void addAdditionalEffect (Effect aEffect) {
		if (! additionalEffects.contains (aEffect)) {
			additionalEffects.add (aEffect);
		}
	}
	
	public void addAdditionalEffects (Action aAction) {
		for (Effect tEffect : additionalEffects) {
			aAction.addEffect (tEffect);
		}
	}
	
	public void setAllActors (boolean aAllActors) {
		allActors = aAllActors;
	}
	
	public boolean getAllActors () {
		return allActors;
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

	protected void updateButton () {
		// Must update Button for Non-Passive Benefits by Overriding this Method
	}

	public boolean isAExtraTilePlacement () {
		return false;
	}
	
	protected void enableButton () {
		if (hasButton ()) {
			button.setEnabled (true);
		}
	}

	protected void disableButton () {
		if (hasButton ()) {
			button.setEnabled (false);
		}
	}

	protected void showButton () {
		if (hasButton ()) {
			button.setVisible (true);
		}
	}

	protected void hideButton () {
		if (hasButton ()) {
			button.setVisible (false);
		}
	}

	protected void setToolTip (String aToolTip) {
		if (hasButton ()) {
			button.setToolTipText (aToolTip);
		}
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

	protected TrainCompany getOwningCompany () {
		TrainCompany tTrainCompany;
		ActorI tOwner;

		tTrainCompany = (TrainCompany) Corporation.NO_CORPORATION;
		tOwner = privateCompany.getOwner ();
		if (tOwner != ActorI.NO_ACTOR) {
			if (tOwner.isACorporation ()) {
				tTrainCompany = (TrainCompany) tOwner;
			} else if (tOwner.isAPlayer ()) {
				
			}
		}

		return tTrainCompany;
	}

	private void setOwnerType (String aActorType) {
		ownerType = ActorI.ActorTypes.fromString (aActorType);
	}

	private void setActorType (String aActorType) {
		actorType = ActorI.ActorTypes.fromString (aActorType);
	}

	public void setButton (KButton aButton) {
		button = aButton;
	}

	public KButton getButton () {
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
		showButton ();
	}

	public void setUsed (boolean aUsed) {
		used = aUsed;
	}

	private void setCloseOnUse (boolean aCloseOnUse) {
		closeOnUse = aCloseOnUse;
	}

	private void setCloseOnAllUsed (boolean aCloseOnAllUsed) {
		closeOnAllUsed = aCloseOnAllUsed;
	}

	public boolean used () {
		return used;
	}

	public boolean closeOnUse () {
		return closeOnUse;
	}

	public boolean closeOnAllUsed () {
		return closeOnAllUsed;
	}

	public boolean passive () {
		return passive;
	}

	public boolean isPassiveCompanyBenefit () {
		boolean tIsPassiveCompanyBenefit;

		if (isACompanyBenefit () && (passive)) {
			tIsPassiveCompanyBenefit = true;
		} else {
			tIsPassiveCompanyBenefit = false;
		}

		return tIsPassiveCompanyBenefit;
	}

	public boolean isPassivePlayerBenefit () {
		boolean tIsPassivePlayerBenefit;

		if (isAPlayerBenefit () && (passive)) {
			tIsPassivePlayerBenefit = true;
		} else {
			tIsPassivePlayerBenefit = false;
		}

		return tIsPassivePlayerBenefit;
	}

	public boolean isAllActorsCompanyBenefit () {
		boolean tIsAllActorsCompanyBenefit;

		if (allActors && (!passive)) {
			tIsAllActorsCompanyBenefit = true;
		} else {
			tIsAllActorsCompanyBenefit = false;
		}

		return tIsAllActorsCompanyBenefit;
	}

	public boolean isActiveCompanyBenefit () {
		boolean tIsActiveCompanyBenefit;

		if (isACompanyBenefit () && (!passive)) {
			tIsActiveCompanyBenefit = true;
		} else {
			tIsActiveCompanyBenefit = false;
		}

		return tIsActiveCompanyBenefit;
	}

	public boolean isActivePlayerBenefit () {
		boolean tIsActivePlayerBenefit;

		if (isAPlayerBenefit () && (!passive)) {
			tIsActivePlayerBenefit = true;
		} else {
			tIsActivePlayerBenefit = false;
		}

		return tIsActivePlayerBenefit;
	}

	public abstract int getCost ();

	public boolean isACompanyBenefit () {
		boolean tIsACompanyBenefit;

		if (actorType.compareTo (ActorI.ActorTypes.ShareCompany) == 0) {
			tIsACompanyBenefit = true;
		} else {
			tIsACompanyBenefit = false;
		}

		return tIsACompanyBenefit;
	}

	public boolean isAPlayerBenefit () {
		boolean tIsAPlayerBenefit;

		if (actorType.compareTo (ActorI.ActorTypes.Player) == 0) {
			tIsAPlayerBenefit = true;
		} else {
			tIsAPlayerBenefit = false;
		}

		return tIsAPlayerBenefit;
	}

	public boolean shouldConfigure () {
		boolean tShouldConfigure;

		tShouldConfigure = true;

		// If the Benefit is Passive, NEVER Configure
		
		if (passive) {
			tShouldConfigure = false;
		} else if (allActors) {
			// If it is an AllActors Benefit, and it is a Player Benefit. Don't Configure
			// If it is NOT a Player Benefit (ie Company Benefit) Then always Configure
			if (isAPlayerBenefit ()) {
				tShouldConfigure = false;
			}
		} else {
			if (privateCompany.isClosed ()) {
				tShouldConfigure = false;
			} else {
				if (used) {
					tShouldConfigure = false;
				}
	
				if (isAPlayerBenefit ()) {
					if ((!privateCompany.isPlayerOwned ())) {
						tShouldConfigure = false;
					}
				}
			}
		}

		return tShouldConfigure;
	}

	public abstract String getNewButtonLabel ();

	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		setPrivateCompany (aPrivateCompany);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
	}

	public boolean isRealBenefit () {
		return true;
	}

	public void abortUse () {

	}

	public void completeBenefitInUse (Corporation aOwningCompany) {
		BenefitUsedEffect tBenefitUsedEffect;

		setUsed (true);
		tBenefitUsedEffect = new BenefitUsedEffect (aOwningCompany, this);
		addAdditionalEffect (tBenefitUsedEffect);
		hideButton ();
	}

	public boolean changeState () {
		return true;
	}

	protected XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLBenefitElement;

		tXMLBenefitElement = aXMLDocument.createElement (EN_BENEFIT);
		tXMLBenefitElement.setAttribute (AN_USED, used);
		tXMLBenefitElement.setAttribute (AN_NAME, getBaseName ());
		tXMLBenefitElement.setAttribute (AN_CLOSE_ON_USE, closeOnUse);
		tXMLBenefitElement.setAttribute (AN_CLOSE_ON_ALL_USED, closeOnAllUsed);

		return tXMLBenefitElement;
	}

	protected Benefit findMatchedBenefit (XMLNode aBenefitNode) {
		Benefit tMatchedBenefit;
		String tBenefitNodeName;

		tBenefitNodeName = aBenefitNode.getThisAttribute (AN_NAME);
		if (tBenefitNodeName.equals (getBaseName ())) {
			tMatchedBenefit = this;
		} else {
			tMatchedBenefit = NO_BENEFIT;
		}

		return tMatchedBenefit;
	}

	public void updateState (XMLNode aBenefitNode) {
		boolean tUsedState;
		boolean tCloseOnUse;
		boolean tCloseOnAllUsed;
		
		tUsedState = aBenefitNode.getThisBooleanAttribute (AN_USED);
		setUsed (tUsedState);
		tCloseOnUse = aBenefitNode.getThisBooleanAttribute (AN_CLOSE_ON_USE);
		setCloseOnUse (tCloseOnUse);
		tCloseOnAllUsed = aBenefitNode.getThisBooleanAttribute (AN_CLOSE_ON_ALL_USED);
		setCloseOnAllUsed (tCloseOnAllUsed);
	}
	
	public ShareCompany getOperatingCompany () {
		ShareCompany tOperatingCompany;
		Corporation tCorporation;
		GameManager tGameManager;

		tGameManager = privateCompany.getGameManager ();
		tCorporation = tGameManager.getOperatingCompany ();
		if (tCorporation != Corporation.NO_CORPORATION) {
			if (tCorporation.isAShareCompany ()) {
				tOperatingCompany = (ShareCompany) tCorporation;
			} else {
				tOperatingCompany = ShareCompany.NO_SHARE_COMPANY;
			}
		} else {
			tOperatingCompany = ShareCompany.NO_SHARE_COMPANY;
		}
		
		return tOperatingCompany;
	}
	
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
	
		tBenefitLabel = new JLabel ("NO BENEFIT INFO");

		return tBenefitLabel;
	}
}
