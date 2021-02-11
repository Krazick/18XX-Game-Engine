package ge18xx.round.action.effects;

import ge18xx.bank.StartPacketFrame;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.GenericActor;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLDocument;

public class StateChangeEffect extends Effect {
	public final static String NAME = "State Change";
	final static AttributeName AN_PREVIOUS_STATE = new AttributeName ("previousState");
	final static AttributeName AN_NEW_STATE = new AttributeName ("newState");
	ActorI.ActionStates previousState;
	ActorI.ActionStates newState;
	
	public StateChangeEffect () {
		super (NAME);
		setPreviousState (ActorI.ActionStates.NoAction);
		setNewState (ActorI.ActionStates.NoAction);
	}

	public StateChangeEffect (ActorI aActor, ActorI.ActionStates aPreviousState, ActorI.ActionStates aNewState) {
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
				tEffectReport += " for " + actor.getName () + " from " + previousState + 
						" to " + newState + ".";
			} else if (actor.isAOperatingRound () || actor.isAStockRound ()) {
				tEffectReport += " from " + previousState +  " to " + newState + ".";
			} else if (actor.isACorporation ()) {
				tEffectReport += " for " + actor.getName () + " from " + previousState + 
						" to " + newState + ".";
			} else {
				tEffectReport += " for " + actor.getName () + " from " + previousState + 
						" to " + newState + ". ***";
			}
		} else {
			tEffectReport += " Actor within Action is not defined";
		}
		
		return tEffectReport;
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
		boolean tEffectApplied;
		boolean tNewAuctionAction = false;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			Player tPlayer = (Player) actor;
			StockRound tStockRound = aRoundManager.getStockRound ();
			actor.resetPrimaryActionState (newState);
			tStockRound.updateRFPlayerLabel (tPlayer);
		} else if (actor.isAStockRound ()) {
			if (newState == ActorI.ActionStates.AuctionRound) {
				aRoundManager.startAuctionRound (tNewAuctionAction);
			} else if (newState == ActorI.ActionStates.OperatingRound) {
				actor.resetPrimaryActionState (newState);
				aRoundManager.setOperatingRoundCount ();
			}
		} else if (actor.isAOperatingRound ()) {
			if (newState == ActorI.ActionStates.StockRound) {
				aRoundManager.startStockRound ();
			}
		} else if (actor.isABank ()) {
			if (actor instanceof StartPacketFrame) {
				
			}
		} else if (actor.isACorporation ()) {
			
		}
		tEffectApplied = true;
		
		return tEffectApplied;

	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		tEffectUndone = false;
		System.out.println ("--- Actor " + actor.getName () + " State " + actor.getStateName ()+ " BEFORE UNDO");
		actor.resetPrimaryActionState (previousState);
		System.out.println ("--- Actor " + actor.getName () + " State " + actor.getStateName ()+ " AFTER UNDO");
		tEffectUndone = true;
		if (actor.isAPlayer () ) {
			Player tPlayer = (Player) actor;
			StockRound tStockRound = aRoundManager.getStockRound ();
			tStockRound.updateRFPlayerLabel (tPlayer);
		}
		
		return tEffectUndone;
	}

	public boolean wasNewStateAuction () {
		return (newState.equals (ActorI.ActionStates.AuctionRound));
	}
	
	@Override
	public boolean nullEffect () {
		boolean tNullEffect = false;
		
		if (previousState.equals (newState)) {
			tNullEffect = true;
		}
		
		return tNullEffect;
	}
}
