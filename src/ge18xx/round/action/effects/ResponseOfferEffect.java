package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.ExchangeQueryFrame;
import ge18xx.company.PurchasePrivateOffer;
import ge18xx.company.PurchaseTrainOffer;
import ge18xx.company.QueryOffer;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ResponseOfferEffect extends ToEffect {
	public final static String NAME = "Response To Offer";
	final static AttributeName AN_RESPONSE = new AttributeName ("response");
	final static AttributeName AN_ITEM_TYPE = new AttributeName ("itemType");
	final static AttributeName AN_ITEM_NAME = new AttributeName ("itemName");
	boolean response;
	String itemType;
	String itemName;

	public ResponseOfferEffect () {
		this (NAME);
	}

	public ResponseOfferEffect (String aName) {
		super (aName);
	}

	public ResponseOfferEffect (ActorI aFromActor, ActorI aToActor, boolean aResponse, String aItemType,
			String aItemName) {
		super (NAME, aFromActor, aToActor);

		setResponse (aResponse);
		setItemType (aItemType);
		setItemName (aItemName);
	}

	public ResponseOfferEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		boolean tResponse;

		String tItemType, tItemName;
		tItemType = aEffectNode.getThisAttribute (AN_ITEM_TYPE);
		tItemName = aEffectNode.getThisAttribute (AN_ITEM_NAME);
		setItemType (tItemType);
		setItemName (tItemName);
		tResponse = aEffectNode.getThisBooleanAttribute (AN_RESPONSE);
		setResponse (tResponse);
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

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_RESPONSE, getResponse ());
		tEffectElement.setAttribute (AN_ITEM_TYPE, getItemType ());
		tEffectElement.setAttribute (AN_ITEM_NAME, getItemName ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tTextResponse;
		String tFullReport;
		String tToActorName = "NULL";
		String tWho;
		String tItem;
		String tDoes_DoesNot;
		String tActorName;
		String tPrezName;
		Corporation tCorporation;

		if (response) {
			tTextResponse = "Accepted";
			tDoes_DoesNot = " does ";
		} else {
			tTextResponse = "Rejected";
			tDoes_DoesNot = " does not ";
		}
		if (toActor != ActorI.NO_ACTOR) {
			tToActorName = getToActorName ();
		}
		if (actor.isACorporation ()) {
			tCorporation = (Corporation) actor;
			tActorName = actor.getName ();
			tPrezName = tCorporation.getPresidentName ();
			tWho = "President of " + tActorName + " (" + tPrezName + ")";
		} else {
			tWho = actor.getName ();
		}
		if ((itemType.equals (PurchasePrivateOffer.PRIVATE_TYPE)) ||
			(itemType.equals (PurchaseTrainOffer.TRAIN_TYPE))) {
			tItem = " to buy " + itemName + " " + itemType;
			tFullReport = REPORT_PREFIX + " The offer from " + tToActorName + tItem + " sent to " + tWho + " was "
					+ tTextResponse;
		} else if (itemType.equals (ExchangeQueryFrame.NAME)) {

			tItem = tDoes_DoesNot +  itemName;
			tFullReport = REPORT_PREFIX + tItem;
		} else {
			tFullReport = REPORT_PREFIX + " NOTHING TO REPORT.";
		}

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

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		String tClientUserName;
		String tToActorName = "";
		String tFromActorName = "";
		TrainCompany tTrainCompany = TrainCompany.NO_TRAIN_COMPANY;
		ShareCompany tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		ActorI.ActionStates tOldStatus;
		QueryOffer tQueryOffer;
		ActorI tToActor;
		ActorI tFromActor;
		Corporation tToCorporation;
		Corporation tFromCorporation;
		Player tFromPlayer;
		Player tToPlayer;

		tEffectApplied = false;

		// TODO: Major refactoring of this class... here on down, it is very busy
		tClientUserName = aRoundManager.getClientUserName ();
		tToActor = getToActor ();
		tFromActor = getActor ();
		if (tToActor.isACorporation ())  {
			tToCorporation = (Corporation) tToActor;
			tToActorName = tToCorporation.getPresidentName ();
		} else {
			tToActorName = tToActor.getName ();
		}
		if (tFromActor.isACorporation ()) {
			tFromCorporation = (Corporation) tFromActor;
			tFromActorName = tFromCorporation.getPresidentName ();
		} else if (tFromActor.isAPlayer ()) {
			tFromPlayer = (Player) tFromActor;
			tFromActorName = tFromPlayer.getName ();
		}
		if (tFromActorName.equals (tClientUserName)) {
			tEffectApplied = true;
		} else {
			tToPlayer = Player.NO_PLAYER;
			if (tToActor.isAPlayer ()) {
				tToPlayer = (Player) tToActor;
			} else if (getActor ().isACorporation ()) {
				tTrainCompany = (TrainCompany) tToActor;
			} else {
				tShareCompany = (ShareCompany) tToActor;
			}

			if (! tClientUserName.equals (tToActorName)) {
				// If the Client trying to apply the Effect is NOT the 'ToActor' Do Nothing is a good apply of the effect
				tEffectApplied = true;
			} else {
				if (tToPlayer != Player.NO_PLAYER) {
					tQueryOffer = tToPlayer.getQueryOffer ();
					tOldStatus = tQueryOffer.getOldStatus ();
					tToPlayer.setPrimaryActionState (tOldStatus);
					if (response) {
						tQueryOffer.setStatus (QueryOffer.ACCEPTED);
					} else {
						tQueryOffer.setStatus (QueryOffer.REJECTED);
					}
					tEffectApplied = true;
				} else  if (tTrainCompany != TrainCompany.NO_TRAIN_COMPANY) {
					if (tTrainCompany.getStatus ().equals (ActorI.ActionStates.WaitingResponse)) {
						tQueryOffer = tTrainCompany.getQueryOffer ();
						tOldStatus = tQueryOffer.getOldStatus ();
						tTrainCompany.resetStatus (tOldStatus);
						if (response) {
							tTrainCompany.setAcceptOffer ();
						} else {
							tTrainCompany.handleRejectOffer ();
						}
						tEffectApplied = true;
					} else {
						System.err.println ("Train Company " + tTrainCompany.getAbbrev ()
								+ " is not in Waiting Response State, it is in " + tTrainCompany.getStateName ());
					}
				} else if (tShareCompany != ShareCompany.NO_SHARE_COMPANY) {
					if (tShareCompany.getStatus ().equals (ActorI.ActionStates.WaitingResponse)) {
						tQueryOffer = tShareCompany.getQueryOffer ();
						if (tQueryOffer != QueryOffer.NO_QUERY_OFFER) {
							tOldStatus = tQueryOffer.getOldStatus ();
							tShareCompany.resetStatus (tOldStatus);
							if (response) {
								tShareCompany.setAcceptOffer ();
							} else {
								tShareCompany.handleRejectOfferPrivate ();
							}
							tEffectApplied = true;
						} else {
							// KLUDGE: This to allow get past bad load of Buy Offer
							tShareCompany.resetStatus (ActorI.ActionStates.BoughtTrain);
						}
					}
				} else {
					System.err.println ("To Actor " + tToActorName + " Not flagged as Corporation");
				}
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
