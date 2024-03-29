package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.HideFormationPanelEffect;
import ge18xx.round.action.effects.RebuildFormationPanelEffect;
import ge18xx.round.action.effects.SetFormationStateEffect;
import ge18xx.round.action.effects.UpdateToNextPlayerEffect;
import geUtilities.XMLNode;

public class FormationPhaseAction extends ChangeStateAction {
	public final static String NAME = "Formation Phase";

	public FormationPhaseAction () {
		this (NAME);
	}

	public FormationPhaseAction (String aName) {
		super (aName);
	}

	public FormationPhaseAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public FormationPhaseAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addUpdateToNextPlayerEffect (ActorI aFromActor, ActorI aToActor) {
		UpdateToNextPlayerEffect tUpdateToNextPlayerEffect;

		tUpdateToNextPlayerEffect = new UpdateToNextPlayerEffect (aFromActor, aToActor);
		addEffect (tUpdateToNextPlayerEffect);
	}

	public void addRebuildFormationPanelEffect (ActorI aFromActor) {
		RebuildFormationPanelEffect tRebuildFormationPanelEffect;
		
		tRebuildFormationPanelEffect = new RebuildFormationPanelEffect (aFromActor);
		addEffect (tRebuildFormationPanelEffect);
	}

	public void addSetFormationStateEffect (ActorI aFromActor, ActorI.ActionStates aOldFormationState,
							ActorI.ActionStates aNewFormationState) {
		SetFormationStateEffect tSetFormationStateEffect;
		
		tSetFormationStateEffect = new SetFormationStateEffect (aFromActor, aOldFormationState, aNewFormationState);
		addEffect (tSetFormationStateEffect);
	}
	
	public void addHideFormationPanelEffect (ActorI aActor) {
		HideFormationPanelEffect tHideFormationPanelEffect;
		
		tHideFormationPanelEffect = new HideFormationPanelEffect (aActor);
		addEffect (tHideFormationPanelEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " has finished all Loan Repayments";

		return tSimpleActionReport;
	}
}
