package ge18xx.round.action;

import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.LayTokenEffect;
import ge18xx.tiles.Tile;
import ge18xx.utilities.XMLNode;

public class LayTokenAction extends ChangeMapAction {
	public final static String NAME = "Lay Token";
	
	public LayTokenAction () {
		super ();
		setName (NAME);
	}
	
	public LayTokenAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}
	
	public LayTokenAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addLayTokenEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex, Benefit aBenefitInUse) {
		LayTokenEffect tLayTokenEffect;

		tLayTokenEffect = new LayTokenEffect (aActor, aMapCell, aTile, aRevenueCenterIndex, aBenefitInUse);
		addEffect (tLayTokenEffect);
	}
	
	public String getMapCellID () {
		String tMapCellID = "";
		
		for (Effect tEffect : effects) {
			if ("".equals (tMapCellID)) {
				if (tEffect instanceof LayTokenEffect) {
					tMapCellID = ((LayTokenEffect) tEffect).getMapCellID ();
				}
			}
		}
		
		return tMapCellID;
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " laid a Token on Map Cell " + getMapCellID ();
		
		return tSimpleActionReport;

	}
}
