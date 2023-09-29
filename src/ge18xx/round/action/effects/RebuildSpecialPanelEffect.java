package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class RebuildSpecialPanelEffect extends SpecialPanelEffect {
	public final static String NAME = "Rebuild Special Panel";

	public RebuildSpecialPanelEffect () {
		this (NAME);
	}

	public RebuildSpecialPanelEffect (String aName) {
		super (aName);
	}

	public RebuildSpecialPanelEffect (ActorI aFromActor) {
		super (NAME, aFromActor);
	}

	public RebuildSpecialPanelEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
	}

	public RebuildSpecialPanelEffect (String aName, ActorI aToActor, ActorI aFromActor) {
		super (aName, aToActor, aFromActor);
	}

	public RebuildSpecialPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
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
		Corporation tCorporation;
		
		tEffectApplied = false;
		tCorporation = aRoundManager.getOperatingCompany ();
		tPresident = (Player) tCorporation.getPresident ();
		tPlayerManager = aRoundManager.getPlayerManager ();
		tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (tPresident);
		rebuildSpecialPanel (aRoundManager, tCurrentPlayerIndex);
		tEffectApplied = true;

		return tEffectApplied;
	}
}
