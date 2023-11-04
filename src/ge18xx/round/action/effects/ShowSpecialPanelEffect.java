package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class ShowSpecialPanelEffect extends RebuildFormationPanelEffect {
	public final static String NAME = "Show Special Panel";
	
	public ShowSpecialPanelEffect () {
		this (NAME);
	}

	public ShowSpecialPanelEffect (String aName) {
		super (aName);
	}

	public ShowSpecialPanelEffect (ActorI aFromActor) {
		super (NAME, aFromActor);
	}

	public ShowSpecialPanelEffect (ActorI aFromActor, ActorI aToActor) {
		super (NAME, aFromActor, aToActor);
	}

	public ShowSpecialPanelEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
	}

	public ShowSpecialPanelEffect (String aName, ActorI aToActor, ActorI aFromActor) {
		super (aName, aToActor, aFromActor);
	}

	public ShowSpecialPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName ());
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPresident;
		PlayerManager tPlayerManager;
		int tCurrentPlayerIndex;
		ShareCompany tShareCompany;
		
		tEffectApplied = false;
		tPlayerManager = aRoundManager.getPlayerManager ();
		tCurrentPlayerIndex = 0;
		tPresident = Player.NO_PLAYER;
		if (actor.isAPlayer ()) {
			tPresident = (Player) actor;
		} else if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tPresident = (Player) tShareCompany.getPresident ();
		}

		if (tPresident != Player.NO_PLAYER) {
			tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (tPresident);
			rebuildFormationPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectApplied = true;
		}
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = false;
		hideSpecialPanel (aRoundManager);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
