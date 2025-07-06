package ge18xx.round.action.effects;

import ge18xx.company.formation.FormCGR;
import ge18xx.company.formation.FormCompany;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLNode;

public class RebuildFormationPanelEffect extends FormationPanelEffect {
	public final static String NAME = "Rebuild Formation Panel";

	public RebuildFormationPanelEffect (ActorI aFromActor) {
		super (NAME, aFromActor);
	}

	public RebuildFormationPanelEffect (String aName, ActorI aFromActor) {
		super (aName, aFromActor);
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
		FormCompany tFormCGR;
		GameManager tGameManager;
		
		tEffectApplied = false;
		
		tGameManager = aRoundManager.getGameManager ();
		tTriggerClass = tGameManager.getTriggerClass ();
		if (tTriggerClass instanceof FormCGR) {
			tFormCGR = (FormCompany) tTriggerClass;
			tCurrentPlayerIndex = tFormCGR.getCurrentPlayerIndex ();
			rebuildFormationPanel (aRoundManager, tCurrentPlayerIndex);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}
}
