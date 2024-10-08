package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.AuctionPassEffect;
import geUtilities.xml.XMLNode;

public class AuctionPassAction extends AuctionStateChangeAction {
	public final static String NAME = "Auction Pass";

	public AuctionPassAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public AuctionPassAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addAuctionPassEffect (ActorI aActor, ActionStates aOldState, ActionStates aNewState) {
		AuctionPassEffect tAuctionPassEffect;

		tAuctionPassEffect = new AuctionPassEffect (aActor, aOldState, aNewState);
		addEffect (tAuctionPassEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " passed in the Auction.";

		return tSimpleActionReport;
	}
}
