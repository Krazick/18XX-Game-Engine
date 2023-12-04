package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.RotateTileEffect;
import ge18xx.tiles.Tile;
import geUtilities.XMLNode;

public class RotateTileAction extends LayTileAction {
	public final static String NAME = "Rotate Tile";

	public RotateTileAction () {
		this (NAME);
	}

	public RotateTileAction (String aName) {
		super (aName);
		setChainToPrevious (true);
	}

	public RotateTileAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
		setChainToPrevious (true);
	}

	public RotateTileAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
		setChainToPrevious (true);
	}

	public void addRotateTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aNewOrientation,
			int aPreviousOrientation, String aTokens, String aBases) {
		RotateTileEffect tRotateTileEffect;

		tRotateTileEffect = new RotateTileEffect (aActor, aMapCell, aTile, aNewOrientation, aPreviousOrientation,
				aTokens, aBases);
		addEffect (tRotateTileEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " rotate Tile " + getTileNumber () + " to Orientation of "
				+ getOrientation () + ".";

		return tSimpleActionReport;
	}
}
