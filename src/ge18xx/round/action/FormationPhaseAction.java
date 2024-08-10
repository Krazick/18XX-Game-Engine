package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.HideFormationPanelEffect;
import ge18xx.round.action.effects.SetFormationStateEffect;
import ge18xx.round.action.effects.ShowFormationPanelEffect;
import ge18xx.round.action.effects.UpdateCertificateLimitEffect;
import geUtilities.xml.XMLNode;

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
	
	public void addShowFormationPanelEffect (ActorI aActor) {
		ShowFormationPanelEffect tShowFormationPanelEffect;
		
		tShowFormationPanelEffect = new ShowFormationPanelEffect (aActor);
		addEffect (tShowFormationPanelEffect);
	}
	
	public void addUpdateCertificateLimitEffect (ActorI aActor, int aOldCertificateLimit, int aNewCertificateLimit) {
		UpdateCertificateLimitEffect tUpdateCertificateLimitEffect;

		tUpdateCertificateLimitEffect = new UpdateCertificateLimitEffect (aActor, aOldCertificateLimit,
						aNewCertificateLimit);
		addEffect (tUpdateCertificateLimitEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " has finished all Loan Repayments";

		return tSimpleActionReport;
	}
}
