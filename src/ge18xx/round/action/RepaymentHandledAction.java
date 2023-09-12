package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.SetRepaymentHandledEffect;
import ge18xx.utilities.XMLNode;

public class RepaymentHandledAction extends ChangeStateAction {
	public final static String NAME = "RepayementHandledAction";

	public RepaymentHandledAction () {
		this (NAME);
	}

	public RepaymentHandledAction (String aName) {
		super (aName);
	}

	public RepaymentHandledAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RepaymentHandledAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addSetRepaymentHandledEffect (ActorI aActor, boolean aRepaymentHandled) {
		SetRepaymentHandledEffect tSetRepaymentHandledEffect;
		
		if (actor.isACorporation ()) {
			tSetRepaymentHandledEffect = new SetRepaymentHandledEffect (aActor, aRepaymentHandled);
			addEffect (tSetRepaymentHandledEffect);
		}
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		Corporation tCorporation;
		Player tPlayer;
		
		if (actor.isAShareCompany ()) {
			tCorporation = (Corporation) actor;
			tPlayer = (Player) tCorporation.getPresident ();
			tSimpleActionReport = tPlayer.getName () + " has handled Loan Repayment for " + 
					tCorporation.getAbbrev () + ".";
		} else {
			tSimpleActionReport = "The actor is not a Corporation";
		}

		return tSimpleActionReport;
	}
}