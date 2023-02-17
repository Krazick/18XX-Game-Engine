package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ChangeRouteEffect extends Effect {
	final static AttributeName AN_TRAIN_INDEX = new AttributeName ("trainIndex");
	final static AttributeName AN_MAP_CELL_ID = new AttributeName ("mapCellID");
	final static AttributeName AN_START_LOCATION = new AttributeName ("start");
	final static AttributeName AN_END_LOCATION = new AttributeName ("end");

	public static final String NAME = "Change Route";
	int trainIndex;
	MapCell mapCell;
	Location startLocation;
	Location endLocation;

	public ChangeRouteEffect () {
		super ();
	}

	public ChangeRouteEffect (String aName) {
		super (aName);
	}

	public ChangeRouteEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public ChangeRouteEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tMapCellID;
		int tStartLocationInt, tEndLocationInt, tTrainIndex;
		Location tStartLocation, tEndLocation;
		MapCell tMapCell;
		HexMap tMap;

		tMap = aGameManager.getGameMap ();
		tMapCellID = aEffectNode.getThisAttribute (AN_MAP_CELL_ID);
		tMapCell = tMap.getMapCellForID (tMapCellID);
		setMapCell (tMapCell);

		tStartLocationInt = aEffectNode.getThisIntAttribute (AN_START_LOCATION);
		tStartLocation = new Location (tStartLocationInt);
		setStartLocation (tStartLocation);

		tEndLocationInt = aEffectNode.getThisIntAttribute (AN_END_LOCATION);
		tEndLocation = new Location (tEndLocationInt);
		setEndLocation (tEndLocation);

		tTrainIndex = aEffectNode.getThisIntAttribute (AN_TRAIN_INDEX);
		setTrainIndex (tTrainIndex);
	}

	public ChangeRouteEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell) {
		this (aActor, aTrainIndex, aMapCell, Location.NO_LOC, Location.NO_LOC);
	}
	
	public ChangeRouteEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation) {
		super (NAME, aActor);
		setTrainIndex (aTrainIndex);
		setMapCell (aMapCell);
		setStartLocation (aStartLocation);
		setEndLocation (aEndLocation);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_TRAIN_INDEX, trainIndex);
		tEffectElement.setAttribute (AN_MAP_CELL_ID, mapCell.getID ());
		if (startLocation != Location.NO_LOC) {
			tEffectElement.setAttribute (AN_START_LOCATION, startLocation.getLocation ());
		}
		if (endLocation != Location.NO_LOC) { 
			tEffectElement.setAttribute (AN_END_LOCATION, endLocation.getLocation ());
		}

		return tEffectElement;
	}

	public void setTrainIndex (int aTrainIndex) {
		trainIndex = aTrainIndex;
	}

	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}

	public void setStartLocation (Location aLocation) {
		startLocation = aLocation;
	}

	public void setEndLocation (Location aLocation) {
		endLocation = aLocation;
	}

	public int getTrainIndex () {
		return trainIndex;
	}

	public MapCell getMapCell () {
		return mapCell;
	}

	public Location getStartLocation () {
		return startLocation;
	}

	public Location getEndLocation () {
		return endLocation;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;

		tReport = REPORT_PREFIX + name + " for " + getActorName () + " for Train " + (getTrainIndex () + 1) +
				" on MapCell " + mapCell.getCellID () + " from Location " + startLocation.getLocation ();
		if (endLocation.getLocation () != Location.NO_LOCATION) {
			tReport += " to Location " + endLocation.getLocation ();
		}
		tReport += ".";

		return tReport;
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApply;

		tEffectApply = true;

		return tEffectApply;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		// Don't really need to undo this effect
		return true;
	}
}