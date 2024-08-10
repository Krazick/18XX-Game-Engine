package ge18xx.round.action.effects;

import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.MapFrame;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class ChangeMapEffect extends Effect {
	public final static String NAME = "Change Map";
	String mapCellID;

	public ChangeMapEffect () {
		this (NAME);
	}

	public ChangeMapEffect (String aName) {
		super (aName);
	}

	public ChangeMapEffect (ActorI aActor, MapCell aMapCell) {
		this (aActor, aMapCell, NO_BENEFIT_IN_USE);
	}

	public ChangeMapEffect (ActorI aActor, MapCell aMapCell, Benefit aBenefitInUse) {
		super (NAME, aActor, aBenefitInUse);
		setMapCellID (aMapCell);
	}

	public ChangeMapEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tMapCellID;

		tMapCellID = aEffectNode.getThisAttribute (MapCell.AN_MAP_CELL_ID);
		setMapCellID (tMapCellID);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (MapCell.AN_MAP_CELL_ID, mapCellID);

		return tEffectElement;
	}

	public MapFrame getMapFrame (RoundManager aRoundManager) {
		return aRoundManager.getMapFrame ();
	}

	public HexMap getMap (RoundManager aRoundManager) {
		return aRoundManager.getGameMap ();
	}

	public String getMapCellID () {
		return mapCellID;
	}

	public MapCell getMapCell (HexMap aGameMap) {
		return aGameMap.getMapCellForID (mapCellID);
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " by " + actor.getName () + " on MapCell " + mapCellID + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setMapCellID (MapCell aMapCell) {
		mapCellID = aMapCell.getCellID ();
	}

	public void setMapCellID (String aMapCell) {
		mapCellID = aMapCell;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = true;

		return tEffectUndone;
	}
}
