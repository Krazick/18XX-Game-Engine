package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.GeneratedRevenueEffect;
import ge18xx.round.action.effects.GetLoanEffect;
import ge18xx.round.action.effects.UpdateLastRevenueEffect;
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

	public void addGeneratedThisRevenueEffect (ActorI aActor, int aNewThisRevenue, int aTrainCount,
							int aOldThisRevenue) {
		GeneratedRevenueEffect tGeneratedRevenueEffect;

		tGeneratedRevenueEffect = new GeneratedRevenueEffect (aActor, aNewThisRevenue, aTrainCount, 
									aOldThisRevenue);
		addEffect (tGeneratedRevenueEffect);
	}

	public void addUpdateLastRevenueEffect (ActorI aActor, int aNewLastRevenue, int aOldLastRevenue) {
		UpdateLastRevenueEffect tUpdateLastRevenueEffect;

		tUpdateLastRevenueEffect = new UpdateLastRevenueEffect (aActor, aNewLastRevenue, aOldLastRevenue);
		addEffect (tUpdateLastRevenueEffect);
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

	public int getNewLastRevenue () {
		int tNewLastRevenue;

		tNewLastRevenue = 0;
		for (Effect tEffect : effects) {
			if (tNewLastRevenue == 0) {
				if (tEffect instanceof UpdateLastRevenueEffect) {
					tNewLastRevenue = ((UpdateLastRevenueEffect) tEffect).getNewLastRevenue ();
				}
			}
		}

		return tNewLastRevenue;
	}

	public int getOldLastRevenue () {
		int tOldLastRevenue;

		tOldLastRevenue = 0;
		for (Effect tEffect : effects) {
			if (tOldLastRevenue == 0) {
				if (tEffect instanceof UpdateLastRevenueEffect) {
					tOldLastRevenue = ((UpdateLastRevenueEffect) tEffect).getOldLastRevenue ();
				}
			}
		}

		return tOldLastRevenue;
	}
}
