package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLNode;

public class ShowFormationPanelEffect extends RebuildFormationPanelEffect {
	public static final String NAME = "Show Formation Panel";
	
	public ShowFormationPanelEffect (ActorI aFromActor) {
		super (NAME, aFromActor);
	}

	public ShowFormationPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName ());
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		int tCurrentPlayerIndex;
		boolean tEffectApplied;
		Player tPresident;
		PlayerManager tPlayerManager;
		PhaseManager tPhaseManager;
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
			tPhaseManager = aRoundManager.getPhaseManager ();
			tPhaseManager.handleTriggerClass ();
			rebuildFormationPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectApplied = true;
		}
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = false;
		hideFormationPanel (aRoundManager);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
