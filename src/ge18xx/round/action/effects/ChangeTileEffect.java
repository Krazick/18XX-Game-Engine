package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ChangeTileEffect extends ChangeMapEffect {
	public static final String NAME = "Change Tile";
	protected static final AttributeName AN_TILE_ORIENTATION = new AttributeName ("tileOrientation");
	int tileNumber;
	int orientation;

	public ChangeTileEffect () {
		this (NAME);
	}

	public ChangeTileEffect (String aName) {
		super (aName);
	}
	
	public ChangeTileEffect (ActorI aActor, MapCell aMapCell, Tile aTile, 
			int aOrientation) {
		super (aActor, aMapCell);
		setName (NAME);
		setTileNumber (aTile.getNumber ());
		setOrientation (aOrientation);
	}

	public ChangeTileEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		int tTileNumber, tOrientation;

		tOrientation = aEffectNode.getThisIntAttribute (AN_TILE_ORIENTATION);
		tTileNumber = aEffectNode.getThisIntAttribute (Tile.AN_TILE_NUMBER);
		
		setTileNumber (tTileNumber);
		setOrientation (tOrientation);
	}

	public int getTileNumber () {
		return tileNumber;
	}

	public int getOrientation () {
		return orientation;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (Tile.AN_TILE_NUMBER, tileNumber);
		tEffectElement.setAttribute (AN_TILE_ORIENTATION, orientation);
	
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " #" + tileNumber + " with orientation " + orientation +
				" by " + actor.getName () + " on MapCell " + mapCellID);
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setTileNumber (int aTileNumber) {
		tileNumber = aTileNumber;
	}

	public void setOrientation (int aOrientation) {
		orientation = aOrientation;
	}
}