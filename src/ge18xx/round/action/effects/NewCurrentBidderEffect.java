package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.AuctionRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

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
		tAuctionRound = aRoundManager.getAuctionRound ();
		tAuctionRound.setPrevBidderJPanelColor (previousPlayerIndex);
		tAuctionRound.setNewBidderJPanelColor (newPlayerIndex);
		tAuctionRound.updateBidderJPanels ();
		tAuctionRound.showAuctionFrame ();

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		AuctionRound tAuctionRound;

		tEffectUndone = true;
		tAuctionRound = aRoundManager.getAuctionRound ();
		tAuctionRound.setNewBidderJPanelColor (previousPlayerIndex);
		tAuctionRound.setPrevBidderJPanelColor (newPlayerIndex);

		return tEffectUndone;
	}
}