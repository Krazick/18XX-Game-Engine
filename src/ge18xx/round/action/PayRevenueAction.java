package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.utilities.XMLNode;

public class PayRevenueAction extends CashTransferAction {
	public final static String NAME = "Pay Revenue";
	
	public PayRevenueAction () {
		super ();
		setName (NAME);
	}

	public PayRevenueAction (ActorI.ActionStates aRoundType, String aRoundID,
			ActorI aActor) {
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
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " paid revenue of " + 
				Bank.formatCash (getCashAmount ()) + 
				" to " + getToActorName () + ".";
		
		return tSimpleActionReport;
	}

}
