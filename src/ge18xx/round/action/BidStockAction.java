package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.player.Escrow;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.AuctionStateChangeEffect;
import ge18xx.round.action.effects.BidShareEffect;
import ge18xx.round.action.effects.BidToCertificateEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.EscrowCashTransferEffect;
import ge18xx.round.action.effects.EscrowToPlayerEffect;
import ge18xx.utilities.XMLNode;

public class BidStockAction extends CashTransferAction {
	public final static String NAME = "Bid on Stock";

	public BidStockAction () {
		super ();
		setName (NAME);
	}

	public BidStockAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public BidStockAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addBidShareEffect (ActorI aActor) {
		BidShareEffect tBidShareEffect;

		tBidShareEffect = new BidShareEffect (aActor);
		addEffect (tBidShareEffect);
	}

	@Override
	public void addCashTransferEffect (CashHolderI aFromCashHolder, CashHolderI aToCashHolder, int aCashAmount) {
		EscrowCashTransferEffect tEscrowCashTransferEffect;

		tEscrowCashTransferEffect = new EscrowCashTransferEffect (aFromCashHolder, aToCashHolder, aCashAmount);
		addEffect (tEscrowCashTransferEffect);
	}

	public void addBidToCertificateEffect (ActorI aActor, Certificate aCertificate, int aBidAmount) {
		BidToCertificateEffect tBidToCertificateEffect;

		tBidToCertificateEffect = new BidToCertificateEffect (aActor, aCertificate, aBidAmount);
		addEffect (tBidToCertificateEffect);
	}

	public void addAuctionStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState,
			ActorI.ActionStates aNewState) {
		AuctionStateChangeEffect tAuctionStateChangeEffect;

		tAuctionStateChangeEffect = new AuctionStateChangeEffect (aActor, aOldState, aNewState);
		addEffect (tAuctionStateChangeEffect);
	}

	public void addEscrowToPlayerEffect (ActorI aActor, Escrow aEscrow) {
		EscrowToPlayerEffect tEscrowToPlayerEffect;

		tEscrowToPlayerEffect = new EscrowToPlayerEffect (aActor, aEscrow);
		addEffect (tEscrowToPlayerEffect);
	}

	public String getCompanyAbbrev () {
		String tCompanyAbbrev = "";

		for (Effect tEffect : effects) {
			if (tCompanyAbbrev.equals ("")) {
				if (tEffect instanceof BidToCertificateEffect) {
					tCompanyAbbrev = ((BidToCertificateEffect) tEffect).getCertificateName ();
				}
			}
		}

		return tCompanyAbbrev;
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " bid " + Bank.formatCash (getCashAmount ()) + " on "
				+ getCompanyAbbrev () + ".";

		return tSimpleActionReport;
	}
}
