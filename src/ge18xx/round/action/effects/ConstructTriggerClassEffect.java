package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.FormationRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLNode;

public class ConstructTriggerClassEffect extends Effect {
	public final static String NAME = "Construct Trigger Formation Class";

	public ConstructTriggerClassEffect (ActorI aActor) {
		super (NAME, aActor);
	}

	public ConstructTriggerClassEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameManager tGameManager;
		FormationRound tFormationRound;
		TriggerClass tTriggerFormationClass;
		ShareCompany tTriggeringShareCompany;
		String tTriggerClassName;
		
		tEffectApplied = false;
		if (actor.isACorporation ()) {
			tTriggeringShareCompany = (ShareCompany) actor;
			tTriggerClassName = tTriggeringShareCompany.getTriggerClassName ();
			tFormationRound = aRoundManager.getFormationRound ();
			tFormationRound.constructFormationClass (tTriggerClassName);
			tTriggerFormationClass = tFormationRound.getTriggerFormationClass ();
			tTriggerFormationClass.setTriggeringShareCompany (tTriggeringShareCompany);
			tGameManager = aRoundManager.getGameManager ();
			tGameManager.setTriggerFormation (tTriggerFormationClass);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("Actor " + actor.getName () + " is not a Corporation.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		FormationRound tFormationRound;

		tEffectUndone = false;
		if (actor.isACorporation ()) {
			tFormationRound = aRoundManager.getFormationRound ();
			tFormationRound.setTriggerFormationClass (TriggerClass.NO_TRIGGER_CLASS);
			tGameManager = aRoundManager.getGameManager ();
			tGameManager.setTriggerFormation (TriggerClass.NO_TRIGGER_CLASS);

			tEffectUndone = true;
		} else {
			setUndoFailureReason ("Actor " + actor.getName () + " is not a Corporation.");
		}

		return tEffectUndone;
	}
}
