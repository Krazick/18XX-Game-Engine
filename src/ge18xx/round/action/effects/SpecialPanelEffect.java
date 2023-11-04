package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.company.special.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class SpecialPanelEffect extends ToEffect {
	public final static String NAME = "Special Panel";

	public SpecialPanelEffect () {
		this (NAME);
	}

	public SpecialPanelEffect (String aName) {
		super (aName);
	}
	
	public SpecialPanelEffect (ActorI aToActor) {
		super (NAME, aToActor);
	}

	public SpecialPanelEffect (ActorI aFromActor, ActorI aToActor) {
		this (NAME, aToActor, aFromActor);
	}
	
	public SpecialPanelEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
	}
	
	public SpecialPanelEffect (String aName, ActorI aToActor, ActorI aFromActor) {
		super (aName, aToActor, aFromActor);
	}

	public SpecialPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	public void hideSpecialPanel (RoundManager aRoundManager) {
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		
		tGameManager = aRoundManager.getGameManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		if (tTriggerClass != TriggerClass.NO_TRIGGER_CLASS) {
			tTriggerClass.hideSpecialPanel ();
		}
	}
	
	public void rebuildFormationPanel (RoundManager aRoundManager, int aCurrentPlayerIndex) {
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		
		tGameManager = aRoundManager.getGameManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		if (tTriggerClass != TriggerClass.NO_TRIGGER_CLASS) {
			tTriggerClass.rebuildSpecialPanel (aCurrentPlayerIndex);
		}
	}

	public int getPlayerIndex (RoundManager aRoundManager, Player aPlayer) {
		PlayerManager tPlayerManager;
		int tPlayerIndex;
		
		tPlayerManager = aRoundManager.getPlayerManager ();
		tPlayerIndex = tPlayerManager.getPlayerIndex (aPlayer);
		
		return tPlayerIndex;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " is rebuilt.");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		ShareCompany tShareCompany;
		Player tPresident;
		PlayerManager tPlayerManager;
		int tCurrentPlayerIndex;
		
		tEffectApplied = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tPresident = (Player) tShareCompany.getPresident ();
			tPlayerManager = aRoundManager.getPlayerManager ();
			tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (tPresident);
			rebuildFormationPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;
		PlayerManager tPlayerManager;
		ShareCompany tShareCompany;
		Player tPresident;
		int tCurrentPlayerIndex;
		
		tPlayerManager = aRoundManager.getPlayerManager ();
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (tPlayer);
			rebuildFormationPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectUndone = true;
		} else if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tPresident = (Player) tShareCompany.getPresident ();
			tPlayerManager = aRoundManager.getPlayerManager ();
			tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (tPresident);
			rebuildFormationPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
