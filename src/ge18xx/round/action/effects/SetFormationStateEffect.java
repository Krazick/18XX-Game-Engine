package ge18xx.round.action.effects;

import ge18xx.company.formation.FormCGR;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLNode;

public class SetFormationStateEffect extends StateChangeEffect {
	public final static String NAME = "Set Formation State";

	public SetFormationStateEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
		super (aActor, aPreviousState, aNewState);
		setName (NAME);
	}

	public SetFormationStateEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;

		tEffectReport = REPORT_PREFIX + name;

		if (actor != ActorI.NO_ACTOR) {
			if (actor.isAPlayer ()) {
				tEffectReport = buildBasicReport (tEffectReport);
			} else {
				tEffectReport = buildBasicReport (tEffectReport)+ " ***";
			}
		} else {
			tEffectReport += " Actor within Effect is not defined";
		}

		return tEffectReport;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		int tCurrentPlayerIndex;
		FormCGR tFormCGR;
		Player tPlayer;

		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tGameManager = aRoundManager.getGameManager ();
			tPlayer = (Player) actor;
			tPlayer.resetPrimaryActionState (newState);

			tTriggerClass = tGameManager.getTriggerClass ();
			tTriggerClass.setFormationState (newState);
			if (newState != ActorI.ActionStates.FormationComplete) {
				tFormCGR = (FormCGR) tTriggerClass;
				tFormCGR.setFormationState (newState);
				tCurrentPlayerIndex = tFormCGR.getCurrentPlayerIndex ();
				tTriggerClass.rebuildFormationPanel (tCurrentPlayerIndex);
			}
			
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("Actor " + actor.getName () + " is not a Player.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		TriggerClass tTriggerClass;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tGameManager = aRoundManager.getGameManager ();
			tTriggerClass = tGameManager.getTriggerClass ();
			tTriggerClass.setFormationState (previousState);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("Actor " + actor.getName () + " is not a Player.");
		}

		return tEffectUndone;
	}
}
