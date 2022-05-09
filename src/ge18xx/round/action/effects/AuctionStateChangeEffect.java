package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.utilities.XMLNode;

public class AuctionStateChangeEffect extends StateChangeEffect {
	public final static String NAME = "Auction State Change";

	public AuctionStateChangeEffect () {
		super ();
		setName (NAME);
	}

	public AuctionStateChangeEffect (ActorI aActor, ActionStates aPreviousState, ActionStates aNewState) {
		super (aActor, aPreviousState, aNewState);
		setName (NAME);
	}

	public AuctionStateChangeEffect (XMLNode aEffectNode, GameManager aGameManager) {
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
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;

		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setAuctionActionState (newState);
			aRoundManager.updateAuctionFrame ();
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setAuctionActionState (previousState);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}