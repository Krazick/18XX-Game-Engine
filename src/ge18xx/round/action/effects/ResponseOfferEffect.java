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
	boolean response;
	ActorI toActor;

	public ResponseOfferEffect () {
		this (NAME);
	}

	public ResponseOfferEffect (String aName) {
		super (aName);
	}

	public ResponseOfferEffect (ActorI aFromActor, ActorI aToActor, boolean aResponse) {
		super (NAME, aFromActor);
		
		setResponse (aResponse);
		setToActor (aToActor);
	}

	public ResponseOfferEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		boolean tResponse;
		ActorI tToActor;
		String tToActorName;
		
		tResponse = aEffectNode.getThisBooleanAttribute (AN_RESPONSE);
		setResponse (tResponse);
		tToActorName = aEffectNode.getThisAttribute (ActorI.AN_TO_ACTOR_NAME);
		tToActor = aGameManager.getActor (tToActorName);
		setToActor (tToActor);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tActorName;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_RESPONSE, getResponse ());
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
		
		if (response) {
			tTextResponse = "Accepted";
		} else {
			tTextResponse = "Rejected";
		}
		if (toActor != null) {
			tToActorName = toActor.getName ();
		}
		tFullReport = REPORT_PREFIX + name + " President of " + actor.getName () +
				" has " + tTextResponse + " the offer from " + tToActorName + ".";
		
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
		String tActorName = "";
		TrainCompany tTrainCompany = null;
		ActorI.ActionStates tOldStatus;
		PurchaseOffer tPurchaseOffer;
		
		tEffectApplied = false;
		System.out.println ("Ready to handle the Response to the Purchase offer");
		
		tClientUserName = aRoundManager.getClientUserName ();
		if (toActor.isACorporation ()) {
			tActorName = ((Corporation) toActor).getPresidentName ();
			tTrainCompany = (TrainCompany) toActor;
		} else {
			tActorName = toActor.getName ();
			System.out.println ("To Actor is not a Corporation, probably a Player (Offer to buy a Private)");
		}
		
		if (tClientUserName.equals (tActorName)) {
			if (tTrainCompany != null) {
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
				System.err.println ("To Actor " + tActorName + " Not flagged as Corporation");
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
