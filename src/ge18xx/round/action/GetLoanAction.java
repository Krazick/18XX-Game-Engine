package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.GetLoanEffect;
import ge18xx.round.action.effects.UpdateLoanCountEffect;
import ge18xx.utilities.XMLNode;

public class GetLoanAction extends CashTransferAction {
	public final static String NAME = "Get Loan";

	public GetLoanAction () {
		super (NAME);
	}

	public GetLoanAction (String aName) {
		super (aName);
	}

	public GetLoanAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public GetLoanAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addGetLoanEffect (ActorI aActor, boolean aOldLoanTaken, boolean aNewLoanTaken) {
		GetLoanEffect tGetLoanEffect;

		tGetLoanEffect = new GetLoanEffect (aActor, aOldLoanTaken, aNewLoanTaken);
		addEffect (tGetLoanEffect);
	}

	public void addUpdateLoanCountEffect (ActorI aActor, int aOldLoanCount, int aNewLoanCount) {
		UpdateLoanCountEffect tUpdateLoanCountEffect;
		
		tUpdateLoanCountEffect = new UpdateLoanCountEffect (aActor, aOldLoanCount, aNewLoanCount);
		addEffect (tUpdateLoanCountEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " obtained a Goverment Loan of the amount " + Bank.formatCash (getCashAmount ());

		return tSimpleActionReport;
	}

}
