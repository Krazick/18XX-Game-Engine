package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.StartStockEffect;
import geUtilities.XMLNode;

public class StartStockAction extends Action {
	public final static String NAME = "Start Stock";

	public StartStockAction () {
		super ();
		setName (NAME);
	}

	public StartStockAction (Action aAction) {
		super (aAction);
		setName (NAME);
	}

	public StartStockAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public StartStockAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " started Stock Action.";

		return tSimpleActionReport;
	}

	public void addStartStockEffect (ActorI aActor) {
		StartStockEffect tStartStockEffect;

		tStartStockEffect = new StartStockEffect (NAME, aActor);
		addEffect (tStartStockEffect);
	}
}
