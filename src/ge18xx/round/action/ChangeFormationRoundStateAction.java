package ge18xx.round.action;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLNode;

public class ChangeFormationRoundStateAction extends FormationRoundAction {
	public final static String NAME = "Change Formation Round State";

	public ChangeFormationRoundStateAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ChangeFormationRoundStateAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;
		String tOldState;
		String tNewState;

		tOldState = getOldState ();
		tNewState = getNewState ();
		if (!tNewState.equals (tOldState)) {
			tSimpleActionReport = actor.getName () + " changed state of the Formation Round from  "
					+ getOldState () + " to " + getNewState () + ".";
		} else {
			tSimpleActionReport = actor.getName () + " Formation Round state remains [" + tOldState + "]";
		}

		return tSimpleActionReport;
	}
}
