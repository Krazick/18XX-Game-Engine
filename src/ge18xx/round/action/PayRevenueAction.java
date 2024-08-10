package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.action.effects.PayCashRevenueEffect;
import geUtilities.xml.XMLNode;

public class PayRevenueAction extends CashTransferAction {
	public final static String NAME = "Pay Revenue";

	public PayRevenueAction () {
		super ();
		setName (NAME);
	}

	public PayRevenueAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
		setChainToPrevious (true);
	}

	public PayRevenueAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " paid revenue of " + Bank.formatCash (getCashAmount ()) + " to "
				+ getToActorName () + ".";

		return tSimpleActionReport;
	}

	public void addPayCashRevenueEffect (CashHolderI aFromCashHolder, CashHolderI aToCashHolder, 
						int aCashAmount, int aOperatingRoundID) {
		PayCashRevenueEffect tPayCashRevenueEffect;

		tPayCashRevenueEffect = new PayCashRevenueEffect (aFromCashHolder, aToCashHolder, 
						aCashAmount, aOperatingRoundID);
		addEffect (tPayCashRevenueEffect);
	}
}
