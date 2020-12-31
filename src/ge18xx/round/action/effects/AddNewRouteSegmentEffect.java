package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class AddNewRouteSegmentEffect extends ChangeRouteEffect {
	public static final String NAME = "New Route Segment Effect";

	public AddNewRouteSegmentEffect () {
		super (NAME);
	}

	public AddNewRouteSegmentEffect (String aName) {
		super (aName);
	}

	public AddNewRouteSegmentEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public AddNewRouteSegmentEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	public AddNewRouteSegmentEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aStartLocation, Location aEndLocation) {
		super (aActor, aTrainIndex, aMapCell, aStartLocation, aEndLocation);
		setName (NAME);
	}
	

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApply;
		
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
