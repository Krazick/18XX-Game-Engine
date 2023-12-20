package ge18xx.round.action;

import ge18xx.game.GameManager;
import geUtilities.XMLNode;

public class SoldOutAdjustmentAction extends ChangeMarketCellAction {
	public final static String NAME = "Sold Out Adjustment";

	public SoldOutAdjustmentAction () {
		super ();
		setName (NAME);
		setChainToPrevious (true);
	}

	public SoldOutAdjustmentAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
		setChainToPrevious (true);
	}

	public SoldOutAdjustmentAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;
		String tStartCellCoordinates;
		String tNewCellCoordinates;

		tStartCellCoordinates = getStartCellCorodinates ();
		tNewCellCoordinates = getNewCellCoordinates ();
		tSimpleActionReport = actor.getName () + " was Sold Out.";
		if (tNewCellCoordinates.equals (tStartCellCoordinates)) {
			tSimpleActionReport += " However it is at the ceiling, so it has not gone up.";
		} else {
			tSimpleActionReport += " It has raised from " + tStartCellCoordinates + " to " + tNewCellCoordinates
					+ " on the Market.";
		}

		return tSimpleActionReport;
	}
}
