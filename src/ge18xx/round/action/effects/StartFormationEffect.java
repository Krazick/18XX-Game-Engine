package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.formation.FormationPhase;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class StartFormationEffect extends Effect {
	public static final String NAME = "Start Formation";
	Corporation formingCorporation;
	
	public StartFormationEffect () {
		this (NAME);
	}

	public StartFormationEffect (String aName) {
		super (aName);
	}
	
	public StartFormationEffect (ActorI aActor, Corporation aFormingCorporation) {
		super (NAME, aActor);
		setFormingCorporation (aFormingCorporation);
	}

	public StartFormationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	public void setFormingCorporation (Corporation aFormingCorporation) {
		formingCorporation = aFormingCorporation;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		String tFormingCompanyName;
		String tActorName;
		
		if (formingCorporation == Corporation.NO_CORPORATION) {
			tFormingCompanyName = "NO Formation Corporation";
		} else {
			tFormingCompanyName = formingCorporation.getName ();
		}
		tActorName = actor.getName ();
		tReport = REPORT_PREFIX + name + " for " + tFormingCompanyName + " by " + tActorName + ".";
		
		return tReport;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		tEffectApplied = true;
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tGameManager = aRoundManager.getGameManager ();
			tGameManager.setFormationPhase (FormationPhase.NO_FORMATION_PHASE);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
