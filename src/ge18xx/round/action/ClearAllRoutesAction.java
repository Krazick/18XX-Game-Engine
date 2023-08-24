package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ClearAllTrainsFromMapEffect;
import ge18xx.utilities.XMLNode;

public class ClearAllRoutesAction extends RouteAction {
	public static final String NAME = "Clear All Routes";

	public ClearAllRoutesAction () {
		super (NAME);
	}

	public ClearAllRoutesAction (String aName) {
		super (aName);
	}

	public ClearAllRoutesAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ClearAllRoutesAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addClearAllTrainsFromMapEffect (Corporation aCorporation) {
		ClearAllTrainsFromMapEffect tClearAllTrainsFromMapEffect;

		tClearAllTrainsFromMapEffect = new ClearAllTrainsFromMapEffect (aCorporation);
		addEffect (tClearAllTrainsFromMapEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " Cleared all Routes from the Map.";

		return tSimpleActionReport;
	}
}
