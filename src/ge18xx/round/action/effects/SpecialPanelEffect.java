package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.company.special.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
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
		setName (NAME);
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
	
	public void rebuildSpecialPanel (RoundManager aRoundManager, Player aPresident) {
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		
		tGameManager = aRoundManager.getGameManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		if (tTriggerClass != TriggerClass.NO_TRIGGER_CLASS) {
			tTriggerClass.rebuildSpecialPanel (aPresident);
		}
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
		
		tEffectApplied = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tPresident = (Player) tShareCompany.getPresident ();
			rebuildSpecialPanel (aRoundManager, tPresident);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			rebuildSpecialPanel (aRoundManager, tPlayer);
			tEffectUndone = true;
		}

		return tEffectUndone;

	}
}
