package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class RemoveDestinationEffect extends ChangeMapEffect {
	public final static String NAME = "Remove Destination";
	Location location;

	public RemoveDestinationEffect () {
	}

	public RemoveDestinationEffect (String aName) {
		super (aName);
	}

	public RemoveDestinationEffect (ActorI aActor, MapCell aMapCell, Location aLocation) {
		super (aActor, aMapCell);
		setLocation (aLocation);
	}

	public RemoveDestinationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		Location tLocation;
		int tLocationInt;
		
		tLocationInt = aEffectNode.getThisIntAttribute (Location.AN_LOCATION);
		tLocation = new Location (tLocationInt);
		setLocation (tLocation);
	}
	
	public void setLocation (Location aLocation) {
		location = aLocation;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (Location.AN_LOCATION, location.getLocation ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " by " + actor.getName () + " from MapCell " + mapCellID + " at Location " + 
					location.toString () + ".");
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameManager tGameManager;
		HexMap tHexMap;
		MapCell tMapCell;
		
		tEffectApplied = false;
		tGameManager = aRoundManager.getGameManager ();
		tHexMap = tGameManager.getGameMap ();
		tMapCell = tHexMap.getMapCellForID (mapCellID);
		tMapCell.removeDestination (location, actor.getAbbrev ());
		tEffectApplied = true;
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		ShareCompany tShareCompany;
		HexMap tHexMap;
		MapCell tMapCell;
		
		tEffectUndone = false;
		if (actor.isAShareCompany ()) {
			tGameManager = aRoundManager.getGameManager ();
			tHexMap = tGameManager.getGameMap ();
			tMapCell = tHexMap.getMapCellForID (mapCellID);
			tShareCompany = (ShareCompany) actor;
			tMapCell.replaceDestination (location, tShareCompany);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("The actor " + actor.getName () + " is not a Share Company");
		}
		
		return tEffectUndone;

	}
}
