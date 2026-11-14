package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;

import geUtilities.xml.XMLNode;

public class PassAction extends ChangeStateAction {
	public static final String NAME = "Pass";

	public PassAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public PassAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " Passed.";

		return tSimpleActionReport;
	}

	@Override
	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone;
		Player tCurrentPlayer;
		PlayerManager tPlayerManager;

		tPlayerManager = aRoundManager.getPlayerManager ();
		tCurrentPlayer = tPlayerManager.getCurrentPlayer ();
		tCurrentPlayer.hidePlayerFrame ();

		tActionUndone = super.undoAction (aRoundManager);

		tCurrentPlayer = tPlayerManager.getCurrentPlayer ();
		tCurrentPlayer.showPlayerFrame ();

		return tActionUndone;
	}
}