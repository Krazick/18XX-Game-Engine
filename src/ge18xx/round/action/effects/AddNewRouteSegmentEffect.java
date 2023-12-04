package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class AddNewRouteSegmentEffect extends ChangeRouteEffect {
	public static final String NAME = "New Route Segment";

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

	public AddNewRouteSegmentEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation) {
		super (aActor, aTrainIndex, aMapCell, aStartLocation, aEndLocation);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Corporation tCompany;
		TrainCompany tTrainCompany;

		tEffectApplied = true;
		if (actor.isACorporation ()) {
			tCompany = (Corporation) actor;
			if (tCompany.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCompany;
				tEffectApplied = tTrainCompany.extendRouteInformation (trainIndex, mapCell, startLocation, endLocation);
			}
		} else {
			setApplyFailureReason ("Actor " + actor.getName () + " is not a Corporation.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		// Don't need to undo this effect, just accept
		return true;
	}
}
