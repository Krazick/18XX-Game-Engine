package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.AuctionRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class NewCurrentBidderEffect extends ChangePlayerEffect {
	public final static String NAME = "New Current Bidder";
	
	public NewCurrentBidderEffect (ActorI aActor, int aPreviousBidder, int aNewBidder) {
		super (aActor, aPreviousBidder, aNewBidder);
		setName (NAME);
	}

	public NewCurrentBidderEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		AuctionRound tAuctionRound;

		tEffectApplied = true;
		System.out.println ("Need to change the the Current Bidder to new");
		tAuctionRound = aRoundManager.getAuctionRound ();
		tAuctionRound.setCurrentBidderIndexOnly (newPlayerIndex);

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		AuctionRound tAuctionRound;

		tEffectUndone = true;
		System.out.println ("Need to change the the Current Bidder to previous");
		tAuctionRound = aRoundManager.getAuctionRound ();
		tAuctionRound.setCurrentBidderIndexOnly (previousPlayerIndex);

		return tEffectUndone;
	}
}