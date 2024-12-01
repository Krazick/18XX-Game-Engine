package ge18xx.round.action;

import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.GeneratedRevenueEffect;
import ge18xx.round.action.effects.UpdatePreviousRevenueEffect;
import geUtilities.xml.XMLNode;

public class PayNoDividendAction extends ChangeMarketCellAction {
	public final static String NAME = "Pay No Dividend";

	public PayNoDividendAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public PayNoDividendAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aPreviousState, aNewState);
		addEffect (tChangeCorporationStatusEffect);
	}
	
	public void addUpdatePreviousRevenueEffect (ActorI aActor, int aNewPreviousRevenue, int aOldPreviousRevenue) {
		UpdatePreviousRevenueEffect tUpdatePreviousRevenueEffect;

		tUpdatePreviousRevenueEffect = new UpdatePreviousRevenueEffect (aActor, aNewPreviousRevenue, aOldPreviousRevenue);
		addEffect (tUpdatePreviousRevenueEffect);
	}

	public void addGeneratedThisRevenueEffect (ActorI aActor, int aOldThisRevenue, int aNewThisRevenue, int aTrainCount) {
		GeneratedRevenueEffect tGeneratedRevenueEffect;

		tGeneratedRevenueEffect = new GeneratedRevenueEffect (aActor, aOldThisRevenue, aNewThisRevenue, aTrainCount);
		addEffect (tGeneratedRevenueEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " paid NO Dividend.";

		return tSimpleActionReport;
	}

	@Override
	public boolean applyAction (RoundManager aRoundManager) {
		boolean tActionApplied;
		GameManager tGameManager;
		TrainCompany tTrainCompany;

		tActionApplied = super.applyAction (aRoundManager);
		tGameManager = aRoundManager.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			tTrainCompany = (TrainCompany) tGameManager.getOperatingCompany ();
			tTrainCompany.hideTrainRevenueFrame ();
		}

		return tActionApplied;
	}
}
