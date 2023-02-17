package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.RemoveRouteSegmentEffect;
import ge18xx.utilities.XMLNode;

public class RemoveRouteSegmentsAction extends RouteAction {
	public static final String NAME = "Remove Route Segment";

	public RemoveRouteSegmentsAction () {
		super ();
	}

	public RemoveRouteSegmentsAction (String aName) {
		super (aName);
	}

	public RemoveRouteSegmentsAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RemoveRouteSegmentsAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " " + NAME + "s for a Train.";

		return tSimpleActionReport;
	}

	public void addRemoveRouteSegmentEffect (ActorI aActor, int aSegmentIndex, int aTrainIndex, MapCell aMapCell) {
		RemoveRouteSegmentEffect tRemoveRouteSegmentEffect;

		tRemoveRouteSegmentEffect = new RemoveRouteSegmentEffect (aActor, aSegmentIndex, aTrainIndex, aMapCell);
		addEffect (tRemoveRouteSegmentEffect);
	}
}
