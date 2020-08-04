package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ChangeTileEffect;
import ge18xx.round.action.effects.RemoveTileEffect;
import ge18xx.tiles.Tile;
import ge18xx.utilities.XMLNode;

public class RemoveTileAction extends ChangeMapAction {
	public final static String NAME = "Remove Tile";
	

	public RemoveTileAction () {
		super ();
		setName (NAME);
	}

	public RemoveTileAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RemoveTileAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addTileRemoveEffect (ActorI aActor, MapCell aMapCell, Tile aTile, 
			int aOrientation, String aPreviousTokens, String aPreviousBases) {
		ChangeTileEffect tTileRemoveEffect;

		tTileRemoveEffect = new RemoveTileEffect (aActor, aMapCell, aTile, 
				aOrientation, aPreviousTokens, aPreviousBases);
		addEffect (tTileRemoveEffect);
	}

}
