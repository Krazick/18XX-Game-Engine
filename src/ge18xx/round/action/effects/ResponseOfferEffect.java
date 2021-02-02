package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.PurchaseOffer;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

//TODO: Refactor TransferTrainEffect, TransferOwnershipEffect, ResponseToOfferEffect, and CashTransferEffect
//to extend a new SuperClass "ToEffect" to hold the "toActor" and methods setToActor, getToActor, getToActorName

public class ResponseOfferEffect extends Effect {
	public final static String NAME = "Response To Offer";
	final static AttributeName AN_RESPONSE = new AttributeName ("response");
	final static AttributeName AN_ITEM_TYPE = new AttributeName ("itemType");
	final static AttributeName AN_ITEM_NAME = new AttributeName ("itemName");
	boolean response;
	ActorI toActor;
	String itemType;
	String itemName;

	public ResponseOfferEffect () {
		this (NAME);
	}

	public ResponseOfferEffect (String aName) {
		super (aName);
	}

	public ResponseOfferEffect (ActorI aFromActor, ActorI aToActor, boolean aResponse, 
				String aItemType, String aItemName) {
		super (NAME, aFromActor);
		
		setResponse (aResponse);
		setToActor (aToActor);
		setItemType (aItemType);
		setItemName (aItemName);
	}

	public ResponseOfferEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		boolean tResponse;
		ActorI tToActor;
		String tToActorName;
		
		String tItemType, tItemName;
		tItemType = aEffectNode.getThisAttribute (AN_ITEM_TYPE);
		tItemName = aEffectNode.getThisAttribute (AN_ITEM_NAME);
		setItemType (tItemType);
		setItemName (tItemName);
		tResponse = aEffectNode.getThisBooleanAttribute (AN_RESPONSE);
		setResponse (tResponse);
		tToActorName = aEffectNode.getThisAttribute (ActorI.AN_TO_ACTOR_NAME);
		tToActor = aGameManager.getActor (tToActorName);
		setToActor (tToActor);
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
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tActorName;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_RESPONSE, getResponse ());
		tEffectElement.setAttribute (AN_ITEM_TYPE, getItemType ());
		tEffectElement.setAttribute (AN_ITEM_NAME, getItemName ());
		if (toActor.isACorporation ()) {
			tActorName = ((Corporation) toActor).getAbbrev ();
		} else {
			tActorName = toActor.getName ();
		}
		tEffectElement.setAttribute (ActorI.AN_TO_ACTOR_NAME, tActorName);
	
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tTextResponse;
		String tFullReport, tToActorName = "NULL";
		String tWho, tItem;
		Corporation tCorporation;
		
		if (response) {
			tTextResponse = "Accepted";
		} else {
			tTextResponse = "Rejected";
		}
		if (toActor != null) {
			tToActorName = toActor.getName ();
		}
		if (actor.isACorporation ()) {
			tCorporation = (Corporation) actor;
			tWho = " President of " + actor.getName () + " (" + tCorporation.getPresidentName () + ")";
		} else {
			tWho = actor.getName ();
		}
		tItem = " to buy " + itemName + " " + itemType;
		tFullReport = REPORT_PREFIX + " The offer from " + tToActorName + tItem + " sent to " + 
				tWho + " was " + tTextResponse;
		return tFullReport;
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setResponse (boolean aResponse) {
		response = aResponse;
	}
	
	public boolean getResponse () {
		return response;
	}
	
	public void setToActor (ActorI aToActor) {
		toActor = aToActor;
	}
	
	public ActorI getToActor () {
		return toActor;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		String tClientUserName;
		String tToActorName = "";
		TrainCompany tTrainCompany = TrainCompany.NO_TRAIN_COMPANY;
		ActorI.ActionStates tOldStatus;
		PurchaseOffer tPurchaseOffer;
		
		tEffectApplied = false;
		System.out.println ("Ready to handle the Response to the Purchase offer");
		
		tClientUserName = aRoundManager.getClientUserName ();
		tToActorName = ((Corporation) toActor).getPresidentName ();
		if (actor.isACorporation ()) {
			tTrainCompany = (TrainCompany) toActor;
		} else {
			System.out.println ("Actor is not a Corporation [" + tToActorName + 
					"], probably a Player (Offer to buy a Private)");
		}
		
		if (tClientUserName.equals (tToActorName)) {
			if (tTrainCompany != TrainCompany.NO_TRAIN_COMPANY) {
				if (tTrainCompany.getStatus ().equals (ActorI.ActionStates.WaitingResponse)) {
					tPurchaseOffer = tTrainCompany.getPurchaseOffer ();
					tOldStatus = tPurchaseOffer.getOldStatus ();
					tTrainCompany.resetStatus (tOldStatus);
					if (response) {
						System.out.println ("Offer was Accepted");
						tTrainCompany.handleAcceptOffer (aRoundManager);
					} else {
						System.out.println ("Offer was Rejected");
						tTrainCompany.handleRejectOffer (aRoundManager);
					}			
					tEffectApplied = true;
				} else {
					System.err.println ("Train Company " + tTrainCompany.getAbbrev () + 
							" is not in Waiting Response State, it is in " + tTrainCompany.getStateName ());
				}	
			} else {
				System.err.println ("To Actor " + tToActorName + " Not flagged as Corporation");
			}
		}
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		// For a Offer Response, no actual change to the state of the game was Applied
		// Therefore there is nothing to undo.
		
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}
