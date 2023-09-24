package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class HideSpecialPanelEffect extends SpecialPanelEffect {
	public final static String NAME = "Hide Special Panel";

	public HideSpecialPanelEffect () {
		this (NAME);
	}

	public HideSpecialPanelEffect (String aName) {
		super (aName);
	}

	public HideSpecialPanelEffect (ActorI aFromActor, ActorI aToActor) {
		super (aFromActor, aToActor);
	}

	public HideSpecialPanelEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
	}

	public HideSpecialPanelEffect (String aName, ActorI aToActor, ActorI aFromActor) {
		super (aName, aToActor, aFromActor);
	}

	public HideSpecialPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName ());
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			hideSpecialPanel (aRoundManager);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPresident;
		PlayerManager tPlayerManager;
		int tCurrentPlayerIndex;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPresident = (Player) actor;
			tPlayerManager = aRoundManager.getPlayerManager ();
			tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (tPresident);
			rebuildSpecialPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}