package ge18xx.round.action.effects;

import ge18xx.company.special.FormationPhase;
import ge18xx.company.special.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class RebuildFormationPanelEffect extends FormationPanelEffect {
	public final static String NAME = "Rebuild Special Panel";

	public RebuildFormationPanelEffect () {
		this (NAME);
	}

	public RebuildFormationPanelEffect (String aName) {
		super (aName);
	}

	public RebuildFormationPanelEffect (ActorI aFromActor) {
		super (NAME, aFromActor);
	}

	public RebuildFormationPanelEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
	}

	public RebuildFormationPanelEffect (String aName, ActorI aToActor, ActorI aFromActor) {
		super (aName, aToActor, aFromActor);
	}

	public RebuildFormationPanelEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName ());
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		int tCurrentPlayerIndex;
		TriggerClass tTriggerClass;
		FormationPhase tFormationPhase;
		GameManager tGameManager;
		
		tEffectApplied = false;
		
		tGameManager = aRoundManager.getGameManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		if (tTriggerClass instanceof FormationPhase) {
			tFormationPhase = (FormationPhase) tTriggerClass;
			tCurrentPlayerIndex = tFormationPhase.getCurrentPlayerIndex ();
			rebuildFormationPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}
}
