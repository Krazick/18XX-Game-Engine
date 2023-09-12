package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.RepaymentFinishedEffect;
import ge18xx.round.action.effects.UpdateToNextPlayerEffect;
import ge18xx.utilities.XMLNode;

public class RepaymentFinishedAction extends Action {
	public final static String NAME = "Repayment Finished";

	public RepaymentFinishedAction () {
		this (NAME);
	}

	public RepaymentFinishedAction (String aName) {
		super (aName);
	}

	public RepaymentFinishedAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RepaymentFinishedAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addRepaymentFinishedEffect (Player aPlayer, boolean aRepaymentFinished) {
		RepaymentFinishedEffect tRepaymentFinishedEffect;

		tRepaymentFinishedEffect = new RepaymentFinishedEffect (aPlayer, aRepaymentFinished);
		addEffect (tRepaymentFinishedEffect);
	}

	public void addUpdateToNextPlayerEffect (ActorI aFromActor, ActorI aToActor) {
		UpdateToNextPlayerEffect tUpdateToNextPlayerEffect;

		tUpdateToNextPlayerEffect = new UpdateToNextPlayerEffect (aFromActor, aToActor);
		addEffect (tUpdateToNextPlayerEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " has finished all Loan Repayments";

		return tSimpleActionReport;
	}

}
