package ge18xx.round.action.effects;

import java.util.List;

import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class UpdateToNextPlayerEffect extends ToFormationPanelEffect {
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

	public UpdateToNextPlayerEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " from " + actor.getName () + " to " + toActor.getName () + ".");
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		int tPlayerIndex;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayerIndex = updateToNextPlayer (aRoundManager);
			rebuildFormationPanel (aRoundManager, tPlayerIndex);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	public int updateToNextPlayer (RoundManager aRoundManager) {
		GameManager tGameManager;
		PlayerManager tPlayerManager;
		TriggerClass tTriggerClass;
		List<Player> tPlayers;
		int tNextPlayerIndex;
		
		tGameManager = aRoundManager.getGameManager ();
		tPlayerManager = tGameManager.getPlayerManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		tPlayers = tPlayerManager.getPlayers ();
		if (tTriggerClass != TriggerClass.NO_TRIGGER_CLASS) {
			tNextPlayerIndex = tTriggerClass.updateToNextPlayer (tPlayers);
		} else {
			tNextPlayerIndex = 0;
		}
		
		return tNextPlayerIndex;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		Player tPresident;
		int tPlayerIndex;
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPresident = (Player) actor;
			tPlayerIndex = getPlayerIndex (aRoundManager, tPresident);
			tGameManager = aRoundManager.getGameManager ();
			tTriggerClass = tGameManager.getTriggerClass ();
			tTriggerClass.setCurrentPlayerIndex (tPlayerIndex);
			rebuildFormationPanel (aRoundManager, tPlayerIndex);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
