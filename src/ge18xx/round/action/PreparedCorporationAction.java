package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.GeneratedRevenueEffect;
import ge18xx.round.action.effects.GetLoanEffect;
import ge18xx.round.action.effects.UpdatePreviousRevenueEffect;
import geUtilities.xml.XMLNode;

public class PreparedCorporationAction extends ChangeStateAction {
	public final static String NAME = "Prepared Corporation";

	public PreparedCorporationAction () {
		this (NAME);
	}

	public PreparedCorporationAction (String aName) {
		super (aName);
		setName (NAME);
	}

	public PreparedCorporationAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public PreparedCorporationAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addGeneratedThisRevenueEffect (ActorI aActor, int aOldThisRevenue, int aThisRevenue, int aTrainCount) {
		GeneratedRevenueEffect tGeneratedRevenueEffect;

		tGeneratedRevenueEffect = new GeneratedRevenueEffect (aActor, aOldThisRevenue, aThisRevenue, aTrainCount);
		addEffect (tGeneratedRevenueEffect);
	}

	public void addUpdatePreviousRevenueEffect (ActorI aActor, int aNewPreviousRevenue, int aOldPreviousRevenue) {
		UpdatePreviousRevenueEffect tUpdatePreviousRevenueEffect;

		tUpdatePreviousRevenueEffect = new UpdatePreviousRevenueEffect (aActor, aNewPreviousRevenue, aOldPreviousRevenue);
		addEffect (tUpdatePreviousRevenueEffect);
	}

	public void addGetLoanEffect (ActorI aActor, boolean aOldLoanTaken, boolean aNewLoanTaken) {
		GetLoanEffect tGetLoanEffect;

		tGetLoanEffect = new GetLoanEffect (aActor, aOldLoanTaken, aNewLoanTaken);
		addEffect (tGetLoanEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " " + name + " set to state of " + getNewCorpState () + ".";

		return tSimpleActionReport;
	}

	public int getNewPreviousRevenue () {
		int tNewPreviousRevenue;

		tNewPreviousRevenue = 0;
		for (Effect tEffect : effects) {
			if (tNewPreviousRevenue == 0) {
				if (tEffect instanceof UpdatePreviousRevenueEffect) {
					tNewPreviousRevenue = ((UpdatePreviousRevenueEffect) tEffect).getNewPreviousRevenue ();
				}
			}
		}

		return tNewPreviousRevenue;
	}

	public int getOldPreviousRevenue () {
		int tOldPreviousRevenue;

		tOldPreviousRevenue = 0;
		for (Effect tEffect : effects) {
			if (tOldPreviousRevenue == 0) {
				if (tEffect instanceof UpdatePreviousRevenueEffect) {
					tOldPreviousRevenue = ((UpdatePreviousRevenueEffect) tEffect).getOldPreviousRevenue ();
				}
			}
		}

		return tOldPreviousRevenue;
	}
}
