package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.utilities.XMLNode;

public class ClearRouteAction extends RouteAction {
	public static final String NAME = "Clear Route";

	public ClearRouteAction () {
		super (NAME);
	}

	public ClearRouteAction (String aName) {
		super (aName);
	}

	public ClearRouteAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ClearRouteAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " Cleared a Route from the Map.";
		
		return tSimpleActionReport;
	}

}
