package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.xml.XMLNode;

public class SkipBaseTileAction extends ChangeStateAction {
	public final static String NAME = "Skip Base Tile";

	public SkipBaseTileAction () {
		this (NAME);
	}

	public SkipBaseTileAction (String aName) {
		super (aName);
	}

	public SkipBaseTileAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public SkipBaseTileAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
}
