package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.XMLNode;

public class ExtendRouteAction extends ClearAllRoutesAction {
	public static final String NAME = "Extend Route";

	public ExtendRouteAction () {
		super (NAME);
	}

	public ExtendRouteAction (String aName) {
		super (aName);
	}

	public ExtendRouteAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ExtendRouteAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " Extended a Route for a Train.";

		return tSimpleActionReport;
	}
}
