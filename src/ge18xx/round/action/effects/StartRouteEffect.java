package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class StartRouteEffect extends Effect {
	public final static String NAME = "Start Route Effect";
	int trainIndex;
	MapCell mapCell;
	Location location;
	
	public StartRouteEffect () {
		super (NAME, ActorI.NO_ACTOR);
	}

	public StartRouteEffect (String aName) {
		super (aName, ActorI.NO_ACTOR);
	}

	public StartRouteEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public StartRouteEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aLocation) {
		super (NAME, aActor);
		setTrainIndex (aTrainIndex);
		setMapCell (aMapCell);
		setLocation (aLocation);
	}
	
	public StartRouteEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		// TODO Auto-generated constructor stub
	}

	public void setTrainIndex (int aTrainIndex) {
		trainIndex = aTrainIndex;
	}
	
	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public void setLocation (Location aLocation) {
		location = aLocation;
	}
	
	public int getTrainIndex () {
		return trainIndex;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	public Location getLocation () {
		return location;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + getActorName () + " start Route for Train " + getTrainIndex () +
				" on MapCell " + mapCell.getCellID() + " Location " + location.getLocation () +".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApply;
		
		// The creation of the Escrow when the Action is Parsed 
		// means we don't have to apply the effect of the Action.
		tEffectApply = true;
		
		return tEffectApply;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		tEffectUndone = false;
// TODO: Undo the Start Route Effect
		
		tEffectUndone = true;
		
		return tEffectUndone;
	}

}
