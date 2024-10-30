package ge18xx.round.action;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLNode;

public class ChangeFormationRoundStateAction extends FormationRoundAction {
	public final static String NAME = "Change Formation Phase State";

	public ChangeFormationRoundStateAction () {
		this (NAME);
	}

	public ChangeFormationRoundStateAction (String aName) {
		super (aName);
	}

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
			tSimpleActionReport = actor.getName () + " changed state of the Formation Phase from  "
					+ getOldState () + " to " + getNewState () + ".";
		} else {
			tSimpleActionReport = actor.getName () + " Formation Phase state remains [" + tOldState + "]";
		}

		return tSimpleActionReport;
	}
}
