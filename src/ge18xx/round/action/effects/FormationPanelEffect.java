package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class FormationPanelEffect extends Effect {
	public final static String NAME = "Formation Panel";

	public FormationPanelEffect () {
		this (NAME);
	}

	public FormationPanelEffect (String aName) {
		super (aName);
	}
	
	public FormationPanelEffect (ActorI aToActor) {
		super (NAME, aToActor);
	}
	
	public FormationPanelEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
	}

	public FormationPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	public void hideFormationPanel (RoundManager aRoundManager) {
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		
		tGameManager = aRoundManager.getGameManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		if (tTriggerClass != TriggerClass.NO_TRIGGER_CLASS) {
			tTriggerClass.hideFormationPanel ();
			tTriggerClass.triggeringHandleDone ();
		}
	}
	
	public void rebuildFormationPanel (RoundManager aRoundManager, int aCurrentPlayerIndex) {
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		
		tGameManager = aRoundManager.getGameManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		if (tTriggerClass != TriggerClass.NO_TRIGGER_CLASS) {
			tTriggerClass.rebuildFormationPanel (aCurrentPlayerIndex);
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
		int tPresidentPlayerIndex;
		ShareCompany tShareCompany;
		Player tPresident;
		PlayerManager tPlayerManager;
		
		tEffectApplied = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tPresident = (Player) tShareCompany.getPresident ();
			tPlayerManager = aRoundManager.getPlayerManager ();
			tPresidentPlayerIndex = tPlayerManager.getPlayerIndex (tPresident);
			rebuildFormationPanel (aRoundManager, tPresidentPlayerIndex);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		int tPresidentPlayerIndex;
		Player tPlayer;
		PlayerManager tPlayerManager;
		ShareCompany tShareCompany;
		Player tPresident;
		
		tPlayerManager = aRoundManager.getPlayerManager ();
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPresidentPlayerIndex = tPlayerManager.getPlayerIndex (tPlayer);
			rebuildFormationPanel (aRoundManager, tPresidentPlayerIndex);
			tEffectUndone = true;
		} else if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tPresident = (Player) tShareCompany.getPresident ();
			tPlayerManager = aRoundManager.getPlayerManager ();
			tPresidentPlayerIndex = tPlayerManager.getPlayerIndex (tPresident);
			rebuildFormationPanel (aRoundManager, tPresidentPlayerIndex);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
