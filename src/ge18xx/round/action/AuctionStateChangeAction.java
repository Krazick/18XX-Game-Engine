package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.AuctionStateChangeEffect;
import ge18xx.round.action.effects.NewCurrentBidderEffect;
import geUtilities.xml.XMLNode;

public class AuctionStateChangeAction extends CashTransferAction {
	public final static String NAME = "Auction State Change";

	public AuctionStateChangeAction () {
	}

	public AuctionStateChangeAction (String aName) {
		super (aName);
	}

	public AuctionStateChangeAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
	}

	public AuctionStateChangeAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
	}
	
	public void addAuctionStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState,
			ActorI.ActionStates aNewState) {
		AuctionStateChangeEffect tAuctionStateChangeEffect;

		tAuctionStateChangeEffect = new AuctionStateChangeEffect (aActor, aOldState, aNewState);
		addEffect (tAuctionStateChangeEffect);
	}

	public void addNewCurrentBidderEffect (ActorI aPlayer, int aCurrentBidderIndex, int aNextBidderIndex) {
		NewCurrentBidderEffect tNewCurrentBidderEffect;
	
		tNewCurrentBidderEffect = new NewCurrentBidderEffect (aPlayer, aCurrentBidderIndex, aNextBidderIndex);
		addEffect (tNewCurrentBidderEffect);
	}
}
