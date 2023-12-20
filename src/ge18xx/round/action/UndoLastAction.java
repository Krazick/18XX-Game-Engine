package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.XMLNode;

public class UndoLastAction extends Action {
	public static final String NAME = "Undo Last";

	public UndoLastAction () {
		super (NAME);
	}

	public UndoLastAction (String aName) {
		super (aName);
	}

	public UndoLastAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
		setChainToPrevious (true);
	}

	public UndoLastAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public boolean applyAction (RoundManager aRoundManager) {
		boolean tActionApplied;
		ActionManager tActionManager;

		tActionApplied = true;
		tActionManager = aRoundManager.getActionManager ();
		tActionManager.undoLastAction (aRoundManager, false);

		return tActionApplied;
	}

	@Override
	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone;

		tActionUndone = true;
		System.out.println ("Performing Undo Action for UndoAction Action");

		return tActionUndone;
	}

}
