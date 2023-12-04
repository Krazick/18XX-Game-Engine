package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class SetNewEndPointEffect extends ChangeRouteEffect {
	public static final String NAME = "Set New End Point Effect";

	public SetNewEndPointEffect () {
		super (NAME);
	}

	public SetNewEndPointEffect (String aName) {
		super (aName);
	}

	public SetNewEndPointEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetNewEndPointEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	public SetNewEndPointEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation) {
		super (aActor, aTrainIndex, aMapCell, aStartLocation, aEndLocation);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Corporation tCompany;
		TrainCompany tTrainCompany;

		if (actor.isACorporation ()) {
			tCompany = (Corporation) actor;
			if (tCompany.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCompany;
				tEffectApplied = tTrainCompany.setNewEndPoint (trainIndex, mapCell, startLocation, endLocation);
			}
		}

		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = false;
		// TODO: Undo the setNewEndPoint Effect

		tEffectUndone = true;

		return tEffectUndone;
	}

}
