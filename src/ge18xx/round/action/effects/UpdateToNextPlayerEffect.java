package ge18xx.round.action.effects;

import java.util.List;

import ge18xx.company.special.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class UpdateToNextPlayerEffect extends SpecialPanelEffect {
	public final static String NAME = "Update to Next Player";

	public UpdateToNextPlayerEffect () {
		this (NAME);
	}

	public UpdateToNextPlayerEffect (String aName) {
		super (aName);
	}
	
	public UpdateToNextPlayerEffect (ActorI aFromActor, ActorI aToActor) {
		super (NAME, aFromActor, aToActor);
	}

	public UpdateToNextPlayerEffect (String aName, ActorI aFromActor, ActorI aToActor) {
		super (aName, aFromActor, aToActor);
	}

	public UpdateToNextPlayerEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			updateToNextPlayer (aRoundManager);
			rebuildSpecialPanel (aRoundManager, tPlayer);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	public boolean updateToNextPlayer (RoundManager aRoundManager) {
		GameManager tGameManager;
		PlayerManager tPlayerManager;
		TriggerClass tTriggerClass;
		List<Player> tPlayers;
		boolean tSuccess;
		
		tGameManager = aRoundManager.getGameManager ();
		tPlayerManager = tGameManager.getPlayerManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		tPlayers = tPlayerManager.getPlayers ();
		if (tTriggerClass != TriggerClass.NO_TRIGGER_CLASS) {
			tSuccess = tTriggerClass.updateToNextPlayer (tPlayers);
		} else {
			tSuccess = false;
		}
		
		return tSuccess;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPresident;
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPresident = (Player) actor;
			tPresident.setRepaymentFinished (false);
			rebuildSpecialPanel (aRoundManager, tPresident);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}

}
