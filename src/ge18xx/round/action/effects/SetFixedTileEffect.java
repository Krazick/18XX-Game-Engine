package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetFixedTileEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set Fixed Tile";
	final static AttributeName AN_PREVIOUS_STATE = new AttributeName ("previousState");
	final static AttributeName AN_TILE = new AttributeName ("fixedTile");
	final static AttributeName AN_MAP_CELL_ID = new AttributeName ("mapCellID");
	String mapCellID;
	boolean previousState;
	
	public SetFixedTileEffect (ActorI aActor, MapCell aMapCell, boolean aFixedTile, 
			boolean aPreviousState) {
		this (NAME, aActor, aFixedTile);
		
		String tMapCellID;
		
		tMapCellID = aMapCell.getID ();
		setMapCellID (tMapCellID);
		setPreviousState (aPreviousState);
	}

	public SetFixedTileEffect (String aName, ActorI aActor, boolean aBooleanFlag) {
		super (aName, aActor, aBooleanFlag);
	}

	public SetFixedTileEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_TILE);
		
		String tMapCellID;
		boolean tPreviousState;
		
		tMapCellID = aEffectNode.getThisAttribute (AN_MAP_CELL_ID);
		setMapCellID (tMapCellID);
		
		tPreviousState = aEffectNode.getThisBooleanAttribute (AN_PREVIOUS_STATE);
		setPreviousState (tPreviousState);
	}
	
	public void setPreviousState (boolean aPreviousState) {
		previousState = aPreviousState;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_TILE);
		tEffectElement.setAttribute (AN_MAP_CELL_ID, mapCellID);

		return tEffectElement;
	}

	public void setMapCellID (String aMapCellID) {
		mapCellID = aMapCellID;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for (" + mapCellID + ") to " + getBooleanFlag () + 
				". Previous State " + previousState + ".");
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameManager tGameManager;
		MapCell tMapCell;
		HexMap tHexMap;
		
		tEffectApplied = false;
		tGameManager = aRoundManager.getGameManager ();
		tHexMap = tGameManager.getGameMap ();
		tMapCell = tHexMap.getMapCellForID (mapCellID);
		tMapCell.setFixedTileFlag (booleanFlag);
		tEffectApplied = true;
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameManager tGameManager;
		MapCell tMapCell;
		HexMap tHexMap;
		
		tEffectApplied = false;
		tGameManager = aRoundManager.getGameManager ();
		tHexMap = tGameManager.getGameMap ();
		tMapCell = tHexMap.getMapCellForID (mapCellID);
		tMapCell.setFixedTileFlag (previousState);
		tEffectApplied = true;
		
		return tEffectApplied;
	}
}
