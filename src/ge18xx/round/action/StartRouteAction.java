package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.StartRouteEffect;
import ge18xx.utilities.XMLNode;

public class StartRouteAction extends Action {
	public final static String NAME = "Start Route";

	public StartRouteAction () {
		super (NAME);
	}

	public StartRouteAction (String aName) {
		super (aName);
	}

	public StartRouteAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public StartRouteAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addStartRouteEffect (ActorI aActor, int aTrainIndex, MapCell aMapCell, Location aLocation) {
		StartRouteEffect tStartRouteEffect;

		tStartRouteEffect = new StartRouteEffect (aActor, aTrainIndex, aMapCell, aLocation);
		addEffect (tStartRouteEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " Started a Route for a Train.";
		
		return tSimpleActionReport;
	}

}
