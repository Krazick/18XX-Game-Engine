package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.effects.ExchangePrezShareEffect;
import ge18xx.round.action.effects.NewPriorityPlayerEffect;
import ge18xx.utilities.XMLNode;

public class DonePlayerAction extends ChangeStateAction {
	public final static String NAME = "Done Player";

	public DonePlayerAction () {
		super ();
		setName (NAME);
	}

	public DonePlayerAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public DonePlayerAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addNewPriorityPlayerEffect (ActorI aPlayer, int aPriorityPlayerIndex, int aNextPlayerIndex) {
		NewPriorityPlayerEffect tNewPriorityPlayerEffect;

		tNewPriorityPlayerEffect = new NewPriorityPlayerEffect (aPlayer, aPriorityPlayerIndex, aNextPlayerIndex);
		addEffect (tNewPriorityPlayerEffect);
	}

	public void addExchangePrezShareEffect (String aCorporationAbbrev) {
		ExchangePrezShareEffect tExchangePrezShareEffect;

		tExchangePrezShareEffect = new ExchangePrezShareEffect (getActor (), aCorporationAbbrev);
		addEffect (tExchangePrezShareEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " is Done.";

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
