package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.GetLoanEffect;
import ge18xx.round.action.effects.ShowFrameEffect;
import ge18xx.round.action.effects.UpdateLastRevenueEffect;
import ge18xx.toplevel.XMLFrame;
import geUtilities.XMLNode;

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

	public void addUpdateLastRevenueEffect (ActorI aActor, int aNewLastRevenue, int aOldLastRevenue) {
		UpdateLastRevenueEffect tUpdateLastRevenueEffect;

		tUpdateLastRevenueEffect = new UpdateLastRevenueEffect (aActor, aNewLastRevenue, aOldLastRevenue);
		addEffect (tUpdateLastRevenueEffect);
	}

	public void addShowFrameEffect (ActorI aActor, XMLFrame aXMLFrame) {
		ShowFrameEffect tShowFrameEffect;
		
		tShowFrameEffect = new ShowFrameEffect (aActor, aXMLFrame);
		addEffect (tShowFrameEffect);
	}
	
	public void addGetLoanEffect (ActorI aActor, boolean aOldLoanTaken, boolean aNewLoanTaken) {
		GetLoanEffect tGetLoanEffect;

		tGetLoanEffect = new GetLoanEffect (aActor, aOldLoanTaken, aNewLoanTaken);
		addEffect (tGetLoanEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " " + name + " set to state of " + getNewCorpState () + ".";

		return tSimpleActionReport;
	}

	public int getNewLastRevenue () {
		int tNewLastRevenue = 0;

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
		int tOldLastRevenue = 0;

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
