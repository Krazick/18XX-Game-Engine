package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Escrow;
import ge18xx.player.EscrowHolderI;
import ge18xx.player.PlayerFrame;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.FinishAuctionEffect;
import ge18xx.round.action.effects.RefundEscrowEffect;
import ge18xx.round.action.effects.RemoveAllBids;
import ge18xx.round.action.effects.RemoveEscrowEffect;
import ge18xx.round.action.effects.StateChangeEffect;
import ge18xx.round.action.effects.TransferOwnershipEffect;
import geUtilities.GUI;
import geUtilities.XMLNode;

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

	public void addRefundEscrowEffect (ActorI aActor, EscrowHolderI aEscrowHolder, int aBidAmount) {
		RefundEscrowEffect tRefundEscrowEffect;

		tRefundEscrowEffect = new RefundEscrowEffect (aActor, aEscrowHolder, aBidAmount);
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
	
	@Override
	public void addStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState, ActorI.ActionStates aNewState) {
		StateChangeEffect tStateChangeEffect;

		if (actor.isACorporation ()) {
			addChangeCorporationStatusEffect (aActor, aOldState, aNewState);
		} else {
			tStateChangeEffect = new StateChangeEffect (aActor, aOldState, aNewState);
			addEffect (tStateChangeEffect);
		}
	}

	public void addFinishAuctionEffect (ActorI aActor) {
		FinishAuctionEffect tFinishAuctionEffect;

		tFinishAuctionEffect = new FinishAuctionEffect (aActor);
		addEffect (tFinishAuctionEffect);
	}

	@Override
	public int getEffectCredit (String aActorName) {
		int tCredit = 0;
		RefundEscrowEffect tRefundEscrowEffect;

		for (Effect tEffect : effects) {
			if (tCredit == 0) {
				if (tEffect instanceof RefundEscrowEffect) {
					tRefundEscrowEffect = (RefundEscrowEffect) tEffect;

					tCredit = tRefundEscrowEffect.getEffectCredit (aActorName);
				}
			}
		}

		return tCredit;
	}

	@Override
	public String getSimpleActionReport (String aActorName) {
		String tRefundReport;
		String tActorName;
		String tToActorName;
		RefundEscrowEffect tRefundEscrowEffect;
		int tCredit;

		tRefundReport = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			tActorName = tEffect.getActorName ();
			tToActorName = tEffect.getToActorName ();
			if ((aActorName.equals (tActorName)) || (aActorName.equals (tToActorName))) {
				if ((tEffect instanceof RefundEscrowEffect)) {
					tRefundEscrowEffect = (RefundEscrowEffect) tEffect;
					tCredit = tRefundEscrowEffect.getEffectCredit (aActorName);
					tRefundReport = "Refund " + Bank.formatCash (tCredit) + " to " + aActorName;
				}
			}
		}

		return tRefundReport;
	}

	@Override
	public String getAuctionWinner () {
		String aAuctionWinner;
		TransferOwnershipEffect tTransferOwnershipEffect;

		aAuctionWinner = ActorI.NO_NAME;
		for (Effect tEffect : effects) {
			if (tEffect instanceof TransferOwnershipEffect) {
				tTransferOwnershipEffect = (TransferOwnershipEffect) tEffect;
				aAuctionWinner = tTransferOwnershipEffect.getToActorName ();
			}
		}

		return aAuctionWinner;
	}

	@Override
	public boolean applyAction (RoundManager aRoundManager) {
		boolean tActionApplied;
		GameManager tGameManager;
		PlayerFrame tPlayerFrame;

		tActionApplied = super.applyAction (aRoundManager);
		tGameManager = aRoundManager.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			tGameManager.hideAuctionFrame ();
			if (tGameManager.isClientCurrentPlayer ()) {
				tPlayerFrame = tGameManager.getCurrentPlayerFrame ();
				tPlayerFrame.setDoneButton ();
			}
		}

		return tActionApplied;
	}
}