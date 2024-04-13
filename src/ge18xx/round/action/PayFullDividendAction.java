package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.CashTransferEffect;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.PayCashDividendEffect;
import geUtilities.GUI;
import geUtilities.XMLNode;

public class PayFullDividendAction extends ChangeMarketCellAction {
	public final static String NAME = "Pay Full Dividend";

	public PayFullDividendAction () {
		super ();
		setName (NAME);
	}

	public PayFullDividendAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public PayFullDividendAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addPayCashDividendEffect (CashHolderI aFromCashHolder, CashHolderI aToCashHolder, 
						int aCashAmount, int aOperatingRoundID) {
		PayCashDividendEffect tPayCashDividendEffect;

		tPayCashDividendEffect = new PayCashDividendEffect (aFromCashHolder, aToCashHolder, 
						aCashAmount, aOperatingRoundID);
		addEffect (tPayCashDividendEffect);
	}
	
	@Override
	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aPreviousState, aNewState);
		addEffect (tChangeCorporationStatusEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;
		int tCashAmount;
		String tReceiverName;
		CashTransferEffect tCashTransferEffect;

		tSimpleActionReport = actor.getAbbrev () + " paid Full Dividend.";

		for (Effect tEffect : effects) {
			if (tEffect instanceof CashTransferEffect) {
				tCashTransferEffect = (CashTransferEffect) tEffect;
				tCashAmount = tCashTransferEffect.getCash ();
				tReceiverName = tCashTransferEffect.getToActor ().getAbbrev ();
				tSimpleActionReport += GUI.NEWLINE + tReceiverName + " received " + Bank.formatCash (tCashAmount) + ".";
			}
		}

		return tSimpleActionReport;
	}

	@Override
	public boolean applyAction (RoundManager aRoundManager) {
		boolean tActionApplied;
		GameManager tGameManager;
		TrainCompany tTrainCompany;

		tActionApplied = super.applyAction (aRoundManager);
		tGameManager = aRoundManager.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			tTrainCompany = (TrainCompany) tGameManager.getOperatingCompany ();
			tTrainCompany.hideTrainRevenueFrame ();
		}

		return tActionApplied;
	}
}
