package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.GeneratedRevenueEffect;
import ge18xx.utilities.XMLNode;

public class OperatedTrainAction extends ChangeStateAction {
	public final static String NAME = "Operated Train";

	public OperatedTrainAction () {
		this (NAME);
	}

	public OperatedTrainAction (String aName) {
		super (aName);
	}

	public OperatedTrainAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public OperatedTrainAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addGeneratedRevenueEffect (ActorI aActor, int aRevenue, int aTrainCount) {
		GeneratedRevenueEffect tGeneratedRevenueEffect;

		tGeneratedRevenueEffect = new GeneratedRevenueEffect (aActor, aRevenue, aTrainCount);
		addEffect (tGeneratedRevenueEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		int tTrainCount;
		
		tTrainCount = getTrainCount ();
		tSimpleActionReport = actor.getName () + " generated " + Bank.formatCash (getRevenue ()) + 
				" from " + tTrainCount + " Train";
		if (tTrainCount > 1) {
			tSimpleActionReport += "s";
		}
		tSimpleActionReport += ".";
		
		return tSimpleActionReport;
	}

	public int getRevenue () {
		int tRevenue = 0;
		
		for (Effect tEffect : effects) {
			if (tRevenue == 0) {
				if (tEffect instanceof GeneratedRevenueEffect) {
					tRevenue = ((GeneratedRevenueEffect) tEffect).getRevenue ();
				}
			}
		}
		
		return tRevenue;
	}
	
	public int getTrainCount () {
		int tTrainCount = 0;
		
		for (Effect tEffect : effects) {
			if (tTrainCount == 0) {
				if (tEffect instanceof GeneratedRevenueEffect) {
					tTrainCount = ((GeneratedRevenueEffect) tEffect).getTrainCount ();
				}
			}
		}
		
		return tTrainCount;
	}
}
