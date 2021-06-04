package ge18xx.company.benefit;

import javax.swing.JPanel;

import ge18xx.company.PrivateCompany;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLNode;

public class Benefit {
	public final static ElementName EN_BENEFITS = new ElementName ("Benefits");
	public final static ElementName EN_BENEFIT = new ElementName ("Benefit");
	public final static AttributeName AN_CLASS = new AttributeName ("class");
	public final static AttributeName AN_CLOSE_ON_USE = new AttributeName ("closeOnUse");
	public final static AttributeName AN_PASSIVE = new AttributeName ("passive");
	public final static AttributeName AN_ACTOR_TYPE = new AttributeName ("actorType");
	
	ActorI.ActorTypes actorType;
	boolean closeOnUse;
	boolean used;
	boolean passive;
		
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
		setUsed (false);
	}
	
	private void setActorType (String aActorType) {
		actorType = ActorI.ActorTypes.fromString (aActorType);
	}
	
	private void setPassive (boolean aPassive) {
		passive = aPassive;
	}
	
	private void setUsed (boolean aUsed) {
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
	
	public boolean isPlayerBenefit () {
		boolean tIsPlayerBenefit = false;
		
		if (actorType.compareTo (ActorI.ActorTypes.Player) == 0) {
			tIsPlayerBenefit = true;
		}
		
		return tIsPlayerBenefit;
	}
	
	public boolean shouldConfigure (PrivateCompany aPrivateCompany) {
		boolean tShouldConfigure = true;
		
		if (used || passive) {
			tShouldConfigure = false;
		}
		
		if ((! aPrivateCompany.isPlayerOwned ()) && isPlayerBenefit ()) {
			tShouldConfigure = false;
		}
		
		return tShouldConfigure;
	}
	
	public String getNewButtonLabel (PrivateCompany aPrivateCompany) {
		// Should have sub-class override to build label for the type of Benefit
		String tNewButtonText = "";
		
		return tNewButtonText;
	}
	
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		// Should have sub-class override to configure for the type of Benefit
	}
}
