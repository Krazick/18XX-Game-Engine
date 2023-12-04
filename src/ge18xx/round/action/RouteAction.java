package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.AddNewRouteSegmentEffect;
import ge18xx.round.action.effects.SetNewEndPointEffect;
import geUtilities.XMLNode;

public class RouteAction extends Action {
	public static final String NAME = "Route";
	public static final RouteAction NO_ROUTE_ACTION = (RouteAction) Action.NO_ACTION;

	public RouteAction () {
		super ();
	}

	public RouteAction (String aName) {
		super (aName);
	}

	public RouteAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RouteAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " Updated a Route for a Train.";

		return tSimpleActionReport;
	}

	public void addNewRouteSegmentEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation) {
		AddNewRouteSegmentEffect tNewRouteSegmentEffect;

		tNewRouteSegmentEffect = new AddNewRouteSegmentEffect (aActor, aTrainIndex, aMapCell, aStartLocation,
				aEndLocation);
		addEffect (tNewRouteSegmentEffect);
	}

	public void addSetNewEndPointEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation) {
		SetNewEndPointEffect tSetNewEndPointEffect;

		tSetNewEndPointEffect = new SetNewEndPointEffect (aActor, aTrainIndex, aMapCell, aStartLocation, aEndLocation);
		addEffect (tSetNewEndPointEffect);
	}
}