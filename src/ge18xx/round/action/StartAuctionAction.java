package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.SetTriggeredAuctionEffect;
import geUtilities.xml.XMLNode;

public class StartAuctionAction extends Action {
	public final static String NAME = "Start Auction";

	public StartAuctionAction () {
		super ();
		setName (NAME);
	}

	public StartAuctionAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public StartAuctionAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " start Auction Round Action.";

		return tSimpleActionReport;
	}
	
	public void addSetTriggeredAuctionEffect (ActorI aActor, boolean tTriggeredAuction) {
		SetTriggeredAuctionEffect tSetTriggeredAuctionEffect;

		tSetTriggeredAuctionEffect = new SetTriggeredAuctionEffect (aActor, tTriggeredAuction);
		addEffect (tSetTriggeredAuctionEffect);
	}
}
