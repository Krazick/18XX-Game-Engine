package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class FinishAuctionEffect extends Effect {
	public final static String NAME = "Finish Auction";

	public FinishAuctionEffect () {
		this (NAME);
	}

	public FinishAuctionEffect (String aName) {
		super (aName);
	}

	public FinishAuctionEffect (ActorI aActor) {
		this (NAME, aActor);
	}
	
	public FinishAuctionEffect (String aName, ActorI aActor) {
		super (aName, aActor);	
	}

	public FinishAuctionEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied = false;
		
		GameManager tGameManager = aRoundManager.getGameManager ();
		tGameManager.finishAuction (false);
		tEffectApplied = true;
		
		return tEffectApplied;
	}
}
