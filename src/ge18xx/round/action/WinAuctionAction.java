package ge18xx.round.action;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Escrow;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.FinishAuctionEffect;
import ge18xx.round.action.effects.RefundEscrowEffect;
import ge18xx.round.action.effects.RemoveAllBids;
import ge18xx.round.action.effects.RemoveEscrowEffect;
import ge18xx.utilities.XMLNode;

public class WinAuctionAction extends BuyStockAction {
	public final static String NAME = "Win Auction";

	public WinAuctionAction () {
		super ();
		setName (NAME);
	}

	public WinAuctionAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public WinAuctionAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addRefundEscrowEffect (ActorI aActor, Player aPlayer, int aBidAmount) {
		RefundEscrowEffect tRefundEscrowEffect;

		tRefundEscrowEffect = new RefundEscrowEffect (aActor, aPlayer, aBidAmount);
		addEffect (tRefundEscrowEffect);
	}

	public void addRemoveEscrowEffect (ActorI aActor, Escrow aEscrow) {
		RemoveEscrowEffect tRemoveEscrowEffect;

		tRemoveEscrowEffect = new RemoveEscrowEffect (aActor, aEscrow);
		addEffect (tRemoveEscrowEffect);
	}
	
	public void addRemoveAllBidsEffect (ActorI aActor, Certificate aCertificate) {
		RemoveAllBids tRemoveAllBids;

		tRemoveAllBids = new RemoveAllBids (aActor, aCertificate);
		addEffect (tRemoveAllBids);
	}
	
	public void addFinishAuctionEffect (ActorI aActor) {
		FinishAuctionEffect tFinishAuctionEffect;

		tFinishAuctionEffect = new FinishAuctionEffect (aActor);
		addEffect (tFinishAuctionEffect);
	}
}