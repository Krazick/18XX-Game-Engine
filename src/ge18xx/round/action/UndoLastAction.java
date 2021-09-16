package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.utilities.XMLNode;

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

	public boolean applyAction (RoundManager aRoundManager) {
		boolean tActionApplied;
		ActionManager tActionManager;
//		int tCurrentActionNumber;
		
		tActionApplied = true;
		tActionManager = aRoundManager.getActionManager ();
		tActionManager.undoLastAction (aRoundManager, false);
//		tCurrentActionNumber = tActionManager.getActionNumber ();
//		System.out.println ("Applying Undo Last Action - from Remote Client " + tCurrentActionNumber);

		return tActionApplied;
	}
	
	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone = true;
		
		System.out.println ("Performing Undo Action for UndoAction Action");
		
		return tActionUndone;
	}

}
