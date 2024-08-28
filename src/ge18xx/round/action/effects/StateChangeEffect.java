package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.GenericActor;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class StateChangeEffect extends Effect {
	public static final AttributeName AN_PREVIOUS_STATE = new AttributeName ("previousState");
	public static final AttributeName AN_NEW_STATE = new AttributeName ("newState");
	public static final String NAME = "State Change";
	ActorI.ActionStates previousState;
	ActorI.ActionStates newState;

	public StateChangeEffect () {
		super (NAME);
		setPreviousState (ActorI.ActionStates.NoAction);
		setNewState (ActorI.ActionStates.NoAction);
	}

	public StateChangeEffect (ActorI aActor) {
		super (NAME, aActor);
	}

	public StateChangeEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
								ActorI.ActionStates aNewState) {
		super (NAME, aActor);
		setPreviousState (aPreviousState);
		setNewState (aNewState);
	}

	public StateChangeEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tPreviousStateName, tNewStateName;
		ActorI.ActionStates tPreviousState, tNewState;
		GenericActor tGenericActor;

		tPreviousStateName = aEffectNode.getThisAttribute (AN_PREVIOUS_STATE);
		tNewStateName = aEffectNode.getThisAttribute (AN_NEW_STATE);
		tGenericActor = new GenericActor ();
		tPreviousState = tGenericActor.getState (tPreviousStateName);
		tNewState = tGenericActor.getState (tNewStateName);
		setPreviousState (tPreviousState);
		setNewState (tNewState);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_PREVIOUS_STATE, previousState.toString ());
		tEffectElement.setAttribute (AN_NEW_STATE, newState.toString ());

		return tEffectElement;
	}

	public ActorI.ActionStates getNewState () {
		return newState;
	}

	public ActorI.ActionStates getPreviousState () {
		return previousState;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;

		tEffectReport = REPORT_PREFIX + name;

		if (actor != ActorI.NO_ACTOR) {
			if (actor.isAPlayer ()) {
				tEffectReport = buildBasicReport (tEffectReport);
			} else if (actor.isAOperatingRound () || actor.isAStockRound ()) {
				tEffectReport += " from " + previousState + " to " + newState + ".";
			} else if (actor.isACorporation ()) {
				tEffectReport = buildBasicReport (tEffectReport);
			} else {
				tEffectReport = buildBasicReport (tEffectReport)+ " ***";
			}
		} else {
			tEffectReport += " Actor within Action is not defined";
		}

		return tEffectReport;
	}

	public String buildBasicReport (String aEffectReport) {
		aEffectReport += " for " + actor.getName () + " from " + previousState +
							" to " + newState + ".";

		return aEffectReport;
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setNewState (ActorI.ActionStates aNewState) {
		newState = aNewState;
	}

	public void setPreviousState (ActorI.ActionStates aPreviousState) {
		previousState = aPreviousState;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		StockRound tStockRound;
		boolean tEffectApplied;
		boolean tNewAuctionAction;
		int tStockRoundID;

		tEffectApplied = false;
		tNewAuctionAction = false;
		if (actor.isAPlayer ()) {
			Player tPlayer = (Player) actor;
			tStockRound = aRoundManager.getStockRound ();
			actor.resetPrimaryActionState (newState);
			tStockRound.updateRFPlayerLabel (tPlayer);
			tEffectApplied = true;
		} else if (actor.isAStockRound ()) {
			if (newState == ActorI.ActionStates.AuctionRound) {
				aRoundManager.startAuctionRound (tNewAuctionAction);
				tEffectApplied = true;
			} else if (newState == ActorI.ActionStates.OperatingRound) {
				aRoundManager.startOperatingRound ();
				tEffectApplied = true;
			} else {
				setApplyFailureReason ("The Current State is a Stock Round, New state of " + newState.toString () +
						" is not allowed");
			}
		} else if (actor.isAAuctionRound ()) {
			if (newState == ActorI.ActionStates.StockRound) {
				tStockRoundID = aRoundManager.getStockRoundID ();
				aRoundManager.resumeStockRound (tStockRoundID);
				tEffectApplied = true;
			} else {
				setApplyFailureReason ("The Current State is a Auction Round, New state of " + newState.toString () +
						" is not allowed");
			}
		} else if (actor.isAOperatingRound ()) {
			if (newState == ActorI.ActionStates.StockRound) {
				aRoundManager.startStockRound ();
				tEffectApplied = true;
			} else {
				setApplyFailureReason ("The Current State is a Operating Round, New state of " + newState.toString () +
						" is not allowed");
			}
		} else if (actor.isABank ()) {
			setApplyFailureReason ("The Actor is a Bank, which does not have a State to Change");
		} else if (actor.isACorporation ()) {
			setApplyFailureReason ("The Actor is a Corporation, and should not have a State Change Effect");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		StockRound tStockRound;
		Player tPlayer;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			actor.resetPrimaryActionState (previousState);
			tEffectUndone = true;
			tStockRound = aRoundManager.getStockRound ();
			tStockRound.updateRFPlayerLabel (tPlayer);
		} else if (actor.isAOperatingRound ()) {
			if (previousState == ActorI.ActionStates.OperatingRound) {
				aRoundManager.setRoundType (previousState);
				tEffectUndone = true;
			} else if (previousState == ActorI.ActionStates.StockRound) {
				aRoundManager.setRoundType (previousState);
				tEffectUndone = true;
			} else if (previousState == ActorI.ActionStates.AuctionRound) {
				aRoundManager.setRoundType (previousState);
				tEffectUndone = true;
			} else {
				setUndoFailureReason ("The Actor is a Operating Round, and previous State is " + previousState.name ());
			}
		} else if (actor.isAStockRound ()) {
			if (previousState == ActorI.ActionStates.OperatingRound) {
				aRoundManager.setRoundType (previousState);
				tEffectUndone = true;
			} else if (previousState == ActorI.ActionStates.StockRound) {
				aRoundManager.setRoundType (previousState);
				tEffectUndone = true;
			} else if (previousState == ActorI.ActionStates.AuctionRound) {
				aRoundManager.setRoundType (previousState);
				tEffectUndone = true;
			} else {
				setUndoFailureReason ("The Actor is a Stock Round, and previous State is " + previousState.name ());
			}
		} else if (actor.isABank ()) {
			setUndoFailureReason ("The Actor is a Bank, which does not have a State to Change");
		} else if (actor.isACorporation ()) {
			setUndoFailureReason ("The Actor is a Corporation, and should not have a State Change Effect");
		} else {
			setUndoFailureReason ("The Actor is a " + actor.getName () + ". Don't know what to do.");
		}

		return tEffectUndone;
	}

	@Override
	public boolean wasNewStateAuction () {
		return (newState.equals (ActorI.ActionStates.AuctionRound));
	}

	@Override
	public boolean nullEffect () {
		boolean tNullEffect;

		if (previousState.equals (newState)) {
			tNullEffect = true;
		} else {
			tNullEffect = false;
		}

		return tNullEffect;
	}
}
