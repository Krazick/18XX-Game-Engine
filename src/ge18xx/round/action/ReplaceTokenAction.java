package ge18xx.round.action;

import ge18xx.company.TokenInfo.TokenType;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.effects.ClearCorporationEffect;
import ge18xx.round.action.effects.RemoveTokenEffect;
import ge18xx.tiles.Tile;
import ge18xx.utilities.XMLNode;

public class ReplaceTokenAction extends LayTokenAction {
	public final static String NAME = "Replace Token";

	public ReplaceTokenAction () {
		super ();
		setName (NAME);
	}

	public ReplaceTokenAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ReplaceTokenAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " replaced Token on Map Cell " + getMapCellID ();

		return tSimpleActionReport;
	}
	
	public void addRemoveTokenEffect (ActorI aFromActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex,
			TokenType aTokenType, int aTokenIndex) {
		RemoveTokenEffect tRemoveTokenEffect;
		
		tRemoveTokenEffect = new RemoveTokenEffect (aFromActor, aMapCell, aTile, aRevenueCenterIndex,
				aTokenType, aTokenIndex);
		addEffect (tRemoveTokenEffect);
	}
	
	public void addClearCorporationEffect (ActorI aFromActor, MapCell aMapCell, Tile aTile, int aRevenueCenterIndex) {
		ClearCorporationEffect tClearCorporationEffect;
		
		tClearCorporationEffect = new ClearCorporationEffect (aFromActor, aMapCell, aTile, aRevenueCenterIndex);
		addEffect (tClearCorporationEffect);
	}
}
