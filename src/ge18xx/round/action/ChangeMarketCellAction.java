package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.market.MarketCell;
import ge18xx.round.action.effects.ChangeMarketCellEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.utilities.XMLNode;

public class ChangeMarketCellAction extends TransferOwnershipAction {
	public final static String NAME = "Change Market Cell";
	
	public ChangeMarketCellAction () {
		super ();
		setName (NAME);
	}
	
	public ChangeMarketCellAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}
	
	public ChangeMarketCellAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addChangeMarketCellEffect (ActorI aActor, MarketCell aStartMarketCell, int aStartLocation, MarketCell aNewMarketCell, int aNewLocation) {
		ChangeMarketCellEffect tChangeMarketCellEffect;
		
		tChangeMarketCellEffect = new ChangeMarketCellEffect (aActor, aStartMarketCell, aStartLocation, aNewMarketCell, aNewLocation);
		addEffect (tChangeMarketCellEffect);
	}
	
	public String getNewCellCoordinates () {
		return getCellCoordinates (true);
	}
	
	public String getStartCellCorodinates () {
		return getCellCoordinates (false);
	}
	
	public String getCellCoordinates (boolean aNewCell) {
		String tCellCoordinates = "";
		ChangeMarketCellEffect tChangeMarketCellEffect;
		
		for (Effect tEffect : effects) {
			if (tCellCoordinates.equals ("")) {
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