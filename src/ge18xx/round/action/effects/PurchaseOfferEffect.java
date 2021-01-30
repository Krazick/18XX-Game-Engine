package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.PurchaseOfferFrame;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PurchaseOfferEffect extends CashInfoEffect {
	public final static String NAME = "Purchase Offer";
	final static AttributeName AN_ITEM_TYPE = new AttributeName ("itemType");
	final static AttributeName AN_ITEM_NAME = new AttributeName ("itemName");
	String itemType;
	String itemName;
	
	public PurchaseOfferEffect () {
		this (NAME);
	}

	public PurchaseOfferEffect (String aName) {
		super (aName);
	}
	
	public PurchaseOfferEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount, String aItemType, String aItemName) {
		super (aFromActor, aToActor, aCashAmount);
		setName (NAME);
		setItemType (aItemType);
		setItemName (aItemName);
	}

	public PurchaseOfferEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		String tItemType, tItemName;
		tItemType = aEffectNode.getThisAttribute (AN_ITEM_TYPE);
		tItemName = aEffectNode.getThisAttribute (AN_ITEM_NAME);
		setItemType (tItemType);
		setItemName (tItemName);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_ITEM_TYPE, getItemType ());
		tEffectElement.setAttribute (AN_ITEM_NAME, getItemName ());
	
		return tEffectElement;
	}

	public String getItemType () {
		return itemType;
	}
	
	public String getItemName () {
		return itemName;
	}
	
	public void setItemType (String aItemType) {
		itemType = aItemType;
	}
	
	public void setItemName (String aItemName) {
		itemName = aItemName;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " President of " + actor.getName () +
				" offers to buy " + getItemName () + " " + getItemType () + " for " +
				Bank.formatCash (cash) + 
				" from " +  toActor.getName () + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		PurchaseOfferFrame tPurchaseOfferFrame;
		String tClientUserName;
		String tActorName = "";
		
		tEffectApplied = true;
		tClientUserName = aRoundManager.getClientUserName ();
		if (toActor.isACorporation ()) {
			tActorName = ((Corporation) toActor).getPresidentName ();
		} else {
			tActorName = toActor.getName ();
			System.out.println ("To Actor is not a Corporation, probably a Player (Offer to buy a Private)");
		}
		System.out.println ("---- Offer to " + tActorName + " This client is " + tClientUserName);
		if (tClientUserName.equals (tActorName)) {
			System.out.println ("Ready to display Dialog to query Client if the Purchase offer should be Accepted or Rejected");
			tPurchaseOfferFrame = new PurchaseOfferFrame (this, aRoundManager);
			tPurchaseOfferFrame.setVisible (true);			
		}
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		// For a Purchase Offer, no actual change to the state of the game was Applied
		// Therefore there is nothing to undo.
		
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}
