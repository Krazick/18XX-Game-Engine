package ge18xx.round.action;

import ge18xx.company.TokenInfo.TokenType;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.effects.BenefitUsedEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.LayTokenEffect;
import ge18xx.round.action.effects.RemoveHomeEffect;
import ge18xx.tiles.Tile;
import ge18xx.utilities.GUI;
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
	
	public void addLayTokenEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex, 
			TokenType aTokenType, int aTokenIndex) {
		this.addLayTokenEffect (aActor, aMapCell, aTile, aRevenueCenterIndex, aTokenType, aTokenIndex, Benefit.NO_BENEFIT);
	}
	
	public void addLayTokenEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex, 
									TokenType aTokenType, int aTokenIndex, Benefit aBenefitInUse) {
		LayTokenEffect tLayTokenEffect;

		tLayTokenEffect = new LayTokenEffect (aActor, aMapCell, aTile, aRevenueCenterIndex, aTokenType, 
									aTokenIndex, aBenefitInUse);
		addEffect (tLayTokenEffect);
	}

	public void addRemoveHomeEffect (ActorI aActor, String aCorporationAbbrev, MapCell aHomeCity1, MapCell aHomeCity2,
									Location aHomeLocation1, Location aHomeLocation2) {
		RemoveHomeEffect tRemoveHomeEffect;

		tRemoveHomeEffect = new RemoveHomeEffect (aActor, aCorporationAbbrev, aHomeCity1, aHomeCity2, 
									aHomeLocation1, aHomeLocation2);
		addEffect (tRemoveHomeEffect);
	}

	public void addBenefitUsedEffect (ActorI aActor, Benefit aBenefit) {
		BenefitUsedEffect tBenefitUsedEffect;

		tBenefitUsedEffect = new BenefitUsedEffect (aActor, aBenefit);
		addEffect (tBenefitUsedEffect);
	}

	public String getMapCellID () {
		String tMapCellID = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (GUI.EMPTY_STRING.equals (tMapCellID)) {
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
