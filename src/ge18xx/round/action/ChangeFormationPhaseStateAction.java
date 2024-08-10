package ge18xx.round.action;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLNode;

public class ChangeFormationPhaseStateAction extends FormationPhaseAction {
	public final static String NAME = "Change Formation Phase State";

	public ChangeFormationPhaseStateAction () {
		this (NAME);
	}

	public ChangeFormationPhaseStateAction (String aName) {
		super (aName);
	}

	public ChangeFormationPhaseStateAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ChangeFormationPhaseStateAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;
		String tOldState, tNewState;

		tOldState = getOldState ();
		tNewState = getNewState ();
		if (!tNewState.equals (tOldState)) {
			tSimpleActionReport = actor.getName () + " changed state of the Formation Phase from  "
					+ getOldState () + " to " + getNewState () + ".";
		} else {
			tSimpleActionReport = actor.getName () + " Formation Phase state remains [" + tOldState + "]";
		}

		return tSimpleActionReport;
	}
}
