package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class HideFormationPanelEffect extends FormationPanelEffect {
	public final static String NAME = "Hide Special Panel";

	public HideFormationPanelEffect () {
		this (NAME);
	}

	public HideFormationPanelEffect (String aName) {
		super (aName);
	}
//
//	public HideFormationPanelEffect (ActorI aFromActor, ActorI aToActor) {
//		super (aFromActor, aToActor);
//	}

	public HideFormationPanelEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
	}

//	public HideFormationPanelEffect (String aName, ActorI aToActor, ActorI aFromActor) {
//		super (aName, aToActor, aFromActor);
//	}

	public HideFormationPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
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
			hideFormationPanel (aRoundManager);
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
			rebuildFormationPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
