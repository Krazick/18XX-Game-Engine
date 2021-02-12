package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.AuctionPassEffect;
import ge18xx.round.action.effects.AuctionStateChangeEffect;
import ge18xx.round.action.effects.NewCurrentBidderEffect;
import ge18xx.utilities.XMLNode;

public class AuctionPassAction extends ChangeStateAction {
	public final static String NAME = "Auction Pass";
	
	// When the AuctionPassAction ends the Auction need to have:
	// TODO: Need to add a Refund Escrow Effect so that the Escrow can be restored on an UNDO
	// (One or more, with Player Name, Certificate Name, and Last Bid/Escrow Value)
	// TODO: Need to add a Remove Bidder Effect so that can record Bidder can be restored on UNDO
	// (One or more, with Player Name, Certificate Name, and Last Bid/Escrow Value)
	// TODO: Remove Bidder for Winning Bidder
	
	public AuctionPassAction () {
		super ();
		setName (NAME);
	}
	
	public AuctionPassAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}
	
	public AuctionPassAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addNewCurrentBidderEffect (ActorI aPlayer, int aCurrentBidderIndex, int aNextBidderIndex) {
		NewCurrentBidderEffect tNewCurrentBidderEffect;

		tNewCurrentBidderEffect = new NewCurrentBidderEffect (aPlayer, aCurrentBidderIndex, aNextBidderIndex);
		addEffect (tNewCurrentBidderEffect);
	}
	
	public void addAuctionStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState, 
			ActorI.ActionStates aNewState) {
		AuctionStateChangeEffect tAuctionStateChangeEffect;

		tAuctionStateChangeEffect = new AuctionStateChangeEffect (aActor, aOldState, aNewState);
		addEffect (tAuctionStateChangeEffect);
	}

	public void addAuctionPassEffect (ActorI aActor, ActionStates aOldState, ActionStates aNewState) {
		AuctionPassEffect tAuctionPassEffect;

		tAuctionPassEffect = new AuctionPassEffect (aActor, aOldState, aNewState);
		addEffect (tAuctionPassEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " passed in the Auction.";
		
		return tSimpleActionReport;
	}
}
