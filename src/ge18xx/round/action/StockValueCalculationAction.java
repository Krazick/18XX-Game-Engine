package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.XMLNode;

public class StockValueCalculationAction extends FormationPhaseAction {
	public final static String NAME = "Stock Value Calculation Finished";

	public StockValueCalculationAction () {
		this (NAME);
	}

	public StockValueCalculationAction (String aName) {
		super (aName);
	}

	public StockValueCalculationAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public StockValueCalculationAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " has finished Stock Value Calculation";

		return tSimpleActionReport;
	}

}
