package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class SetWaitStateEffect extends StateChangeEffect {
	public final static String NAME = "Set Wait State";
	ActorI toActor;

	public SetWaitStateEffect (ActorI aFromActor, ActorI aToActor, ActorI.ActionStates aOldState,
			ActorI.ActionStates aNewState) {
		super (aFromActor);

		setToActor (aToActor);
		setNewState (aNewState);
		setPreviousState (aOldState);
		setName (NAME);
	}

	public void setToActor (ActorI aToActor) {
		toActor = aToActor;
	}

	public ActorI getToActor () {
		return toActor;
	}

	public SetWaitStateEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tActorName;
		ActorI tActor;

		setName (NAME);
		tActorName = aEffectNode.getThisAttribute (ActorI.AN_TO_ACTOR_NAME);
		tActor = aGameManager.getActor (tActorName, false);
		if (tActor == ActorI.NO_ACTOR) {
			System.err.println ("No Actor Found -- Looking for [" + tActorName + "]");
		} else {
			setToActor (tActor);
		}
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (ActorI.AN_TO_ACTOR_NAME, toActor.getName ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;

		tEffectReport = REPORT_PREFIX + name;

		if (actor != ActorI.NO_ACTOR) {
			if (actor.isAPlayer ()) {
				tEffectReport = buildBasicReport (tEffectReport);
			}
		} else {
			tEffectReport += " Actor within Action is not defined";
		}

		return tEffectReport;
	}

	@Override
	public String buildBasicReport (String aEffectReport) {
		aEffectReport += " for " + toActor.getName () + " from " + previousState +
							" to " + newState + ".";

		return aEffectReport;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tToPlayer;
		StockRound tStockRound;

		tEffectApplied = false;
		if (aRoundManager.isNetworkGame ()) {
			if (toActor.isAPlayer ()) {
				tToPlayer = (Player) toActor;
				tStockRound = aRoundManager.getStockRound ();
				tToPlayer.resetPrimaryActionState (newState);
				tStockRound.updateRFPlayerLabel (tToPlayer);
				tEffectApplied = true;
			}
		} else {
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tToPlayer;
		StockRound tStockRound;

		tEffectUndone = false;
		if (aRoundManager.isNetworkGame ()) {
			if (toActor.isAPlayer ()) {
				tToPlayer = (Player) toActor;
				tStockRound = aRoundManager.getStockRound ();
				tToPlayer.resetPrimaryActionState (previousState);
				tStockRound.updateRFPlayerLabel (tToPlayer);
				tEffectUndone = true;
			} else {
				setUndoFailureReason ("The toActor is not a Player.");
			}
		} else {
			tEffectUndone = true;
		}

		return tEffectUndone;
	}

}
