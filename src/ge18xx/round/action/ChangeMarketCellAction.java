package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.market.MarketCell;
import ge18xx.round.action.effects.ChangeMarketCellEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.GeneratedRevenueEffect;
import ge18xx.round.action.effects.ReturnTrainEffect;
import ge18xx.round.action.effects.UpdatePreviousRevenueEffect;
import ge18xx.train.Train;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class ChangeMarketCellAction extends TransferOwnershipAction {
	public static final String NAME = "Change Market Cell";

	public ChangeMarketCellAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ChangeMarketCellAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addChangeMarketCellEffect (ActorI aActor, MarketCell aStartMarketCell, int aStartLocation,
			MarketCell aNewMarketCell, int aNewLocation) {
		ChangeMarketCellEffect tChangeMarketCellEffect;

		tChangeMarketCellEffect = new ChangeMarketCellEffect (aActor, aStartMarketCell, aStartLocation, aNewMarketCell,
				aNewLocation);
		addEffect (tChangeMarketCellEffect);
	}

	public void addReturnTrainEffect (TrainCompany aTrainCompany, Train aTrain, Bank aBank) {
		ReturnTrainEffect tReturnTrainEffect;
		
		tReturnTrainEffect = new ReturnTrainEffect (aTrainCompany, aTrain, aBank);
		addEffect (tReturnTrainEffect);
	}

	public void addGeneratedThisRevenueEffect (ActorI aActor, int aOldThisRevenue, int aNewThisRevenue, int aTrainCount) {
		GeneratedRevenueEffect tGeneratedRevenueEffect;

		tGeneratedRevenueEffect = new GeneratedRevenueEffect (aActor, aOldThisRevenue, aNewThisRevenue, aTrainCount);
		addEffect (tGeneratedRevenueEffect);
	}
	
	public void addUpdatePreviousRevenueEffect (ActorI aActor, int aOldPreviousRevenue, int aNewPreviousRevenue) {
		UpdatePreviousRevenueEffect tUpdatePreviousRevenueEffect;

		tUpdatePreviousRevenueEffect = new UpdatePreviousRevenueEffect (aActor, aOldPreviousRevenue, aNewPreviousRevenue);
		addEffect (tUpdatePreviousRevenueEffect);
	}

	public String getNewCellCoordinates () {
		return getCellCoordinates (true);
	}

	public String getStartCellCorodinates () {
		return getCellCoordinates (false);
	}

	public String getCellCoordinates (boolean aNewCell) {
		String tCellCoordinates = GUI.EMPTY_STRING;
		ChangeMarketCellEffect tChangeMarketCellEffect;

		for (Effect tEffect : effects) {
			if (tCellCoordinates.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof ChangeMarketCellEffect) {
					tChangeMarketCellEffect = ((ChangeMarketCellEffect) tEffect);
					if (aNewCell) {
						tCellCoordinates = tChangeMarketCellEffect.getNewCellCoordinates ();
					} else {
						tCellCoordinates = tChangeMarketCellEffect.getStartCellCorodinates ();
					}
				}
			}
		}

		return tCellCoordinates;
	}
}