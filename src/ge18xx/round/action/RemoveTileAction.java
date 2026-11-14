package ge18xx.round.action;

import ge18xx.company.MinorCompany;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ChangeTileEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.RemoveTileEffect;
import ge18xx.tiles.Tile;
import geUtilities.xml.XMLNode;
import genericMap.Cell;

public class RemoveTileAction extends ChangeMapAction {
	public static final String NAME = "Remove Tile";

	public RemoveTileAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RemoveTileAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addRemoveTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, int aOrientation,
			String aPreviousTokens, String aPreviousBases) {
		ChangeTileEffect tTileRemoveEffect;

		tTileRemoveEffect = new RemoveTileEffect (aActor, aMapCell, aTile, aOrientation, aPreviousTokens,
				aPreviousBases);
		addEffect (tTileRemoveEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;
		String tActorAbbrev;
			
		tActorAbbrev = actor.getAbbrev ();
		if (actor.isAMinorCompany ()) {
			tActorAbbrev = MinorCompany.MINOR_COMPANY + " " + tActorAbbrev;
		}
		tSimpleActionReport = tActorAbbrev + " Removed Tile " + getTileNumber () + 
				" with Orientation of " + getOrientation () + " from MapCell " + 
				getMapCellID () + ".";

		return tSimpleActionReport;
	}
	
	public String getMapCellID () {
		String tMapCellID;
	
		tMapCellID = Cell.NO_ID;
		for (Effect tEffect : effects) {
			if (tMapCellID == Cell.NO_ID) {
				if (tEffect instanceof RemoveTileEffect) {
					tMapCellID = ((RemoveTileEffect) tEffect).getMapCellID ();
				}
			}
		}
	
		return tMapCellID;
	}
	
	public int getTileNumber () {
		int tTileNumber = -1;
	
		for (Effect tEffect : effects) {
			if (tTileNumber == -1) {
				if (tEffect instanceof RemoveTileEffect) {
					tTileNumber = ((RemoveTileEffect) tEffect).getTileNumber ();
				}
			}
		}
	
		return tTileNumber;
	}
	
	public int getOrientation () {
		int tOrientation = -1;
	
		for (Effect tEffect : effects) {
			if (tOrientation == -1) {
				if (tEffect instanceof RemoveTileEffect) {
					tOrientation = ((RemoveTileEffect) tEffect).getOrientation ();
				}
			}
		}
	
		return tOrientation;
	}
}
