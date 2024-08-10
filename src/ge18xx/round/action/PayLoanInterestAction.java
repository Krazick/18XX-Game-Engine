package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ReduceRevenueEffect;
import geUtilities.xml.XMLNode;

public class PayLoanInterestAction extends CashTransferAction {
	public final static String NAME = "Pay Loan Interest";

	public PayLoanInterestAction () {
		super (NAME);
	}

	public PayLoanInterestAction (String aName) {
		super (aName);
	}

	public PayLoanInterestAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public PayLoanInterestAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addReduceRevenueEffect (ActorI aActor, int aReduceRevenueBy) {
		ReduceRevenueEffect tReduceRevenueEffect;

		tReduceRevenueEffect = new ReduceRevenueEffect (aActor, aReduceRevenueBy);
		addEffect (tReduceRevenueEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + NAME + " on Goverment Loan of the amount " + 
								Bank.formatCash (getCashAmount ());

		return tSimpleActionReport;
	}
}
