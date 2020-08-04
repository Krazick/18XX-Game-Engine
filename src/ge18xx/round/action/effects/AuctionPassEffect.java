package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.utilities.XMLNode;

public class AuctionPassEffect extends AuctionStateChangeEffect {
	public final static String NAME = "Auction Pass";

	public AuctionPassEffect () {
		super ();
		setName (NAME);
	}

	public AuctionPassEffect (ActorI aActor, ActionStates aPreviousState, ActionStates aNewState) {
		super (aActor, aPreviousState, aNewState);
		setName (NAME);
	}

	public AuctionPassEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			Player tPlayer = (Player) actor;
			tPlayer.applyAuctionPass ();
			tEffectApplied = true;
			aRoundManager.updateAuctionFrame ();
		}

		return tEffectApplied;
	}
}