package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.XMLNode;

public class SkipBaseTokenAction extends ChangeStateAction {
	public final static String NAME = "Skip Base Token";

	public SkipBaseTokenAction () {
		this (NAME);
	}

	public SkipBaseTokenAction (String aName) {
		super (aName);
	}

	public SkipBaseTokenAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public SkipBaseTokenAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
}
