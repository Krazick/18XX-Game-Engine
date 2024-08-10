package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.RebuildFormationPanelEffect;
import geUtilities.xml.XMLNode;

public class RedeemLoanAction extends GetLoanAction {
	public final static String NAME = "Redeem Loan";

	public RedeemLoanAction () {
		super (NAME);
	}

	public RedeemLoanAction (String aName) {
		super (aName);
	}

	public RedeemLoanAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RedeemLoanAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	/**
	 * This method is to override the AddGetLoanEffect method, since when redeeming a Loan should never be able to
	 * add the 'GetLoanEffect' to the RedeemLoanAction. The name is CORRECT.
	 *
	 */
	@Override
	public void addGetLoanEffect (ActorI aActor, boolean aOldLoanTaken, boolean aNewLoanTaken) {
		System.err.println ("Cannot add a Loan Effect when Redeeming a Loan");
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " redeemed a Goverment Loan of the amount " + 
								Bank.formatCash (getCashAmount ());

		return tSimpleActionReport;
	}
	
	@Override
	public void addRebuildFormationPanelEffect (ActorI aFromActor) {
		RebuildFormationPanelEffect tRebuildFormationPanelEffect;
		
		tRebuildFormationPanelEffect = new RebuildFormationPanelEffect (aFromActor);
		addEffect (tRebuildFormationPanelEffect);
	}
}
