package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;
import geUtilities.xml.XMLNode;

public class StartRouteEffect extends ChangeRouteEffect {
	public static final String NAME = "Start Route Effect";

	public StartRouteEffect () {
		super (NAME, ActorI.NO_ACTOR);
	}

	public StartRouteEffect (String aName) {
		super (aName, ActorI.NO_ACTOR);
	}

	public StartRouteEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public StartRouteEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation) {
		super (NAME, aActor);
		setTrainIndex (aTrainIndex);
		setMapCell (aMapCell);
		setStartLocation (aStartLocation);
		setEndLocation (aEndLocation);
	}

	public StartRouteEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tTrainCompany;
		Corporation tCompany;

		tEffectApplied = false;
		if (actor.isACorporation ()) {
			tCompany = (Corporation) actor;
			if (tCompany.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCompany;
				tEffectApplied = tTrainCompany.startRouteInformation (trainIndex, mapCell, startLocation, endLocation);
			}
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		TrainCompany tTrainCompany;
		Corporation tCompany;
		Train tTrain;

		if (actor.isACorporation ()) {
			tCompany = (Corporation) actor;
			if (tCompany.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCompany;
				tTrain = tTrainCompany.getTrain (trainIndex);
				tTrain.clearRouteInformation ();
				aRoundManager.repaintMapFrame ();
			}
		}

		return true;
	}
}
