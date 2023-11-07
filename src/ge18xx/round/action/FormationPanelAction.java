package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.RebuildFormationPanelEffect;
import ge18xx.round.action.effects.ShowFormationPanelEffect;
import ge18xx.utilities.XMLNode;

public class FormationPanelAction extends Action {
	public final static String NAME = "Show Formation Panel";

	public FormationPanelAction () {
		this (NAME);
	}

	public FormationPanelAction (String aName) {
		super (aName);
	}

	public FormationPanelAction (Action aAction) {
		super (aAction);
	}

	public FormationPanelAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public FormationPanelAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
//
//	public void addHideFormationPanelEffect (ActorI aFromActor, ActorI aToActor) {
//		HideFormationPanelEffect tHideFormationPanelEffect;
//		
//		tHideFormationPanelEffect = new HideFormationPanelEffect (aFromActor, aToActor);
//		addEffect (tHideFormationPanelEffect);
//	}

	public void addShowFormationPanelEffect (ActorI aFromActor) {
		ShowFormationPanelEffect tShowFormationPanelEffect;
		
		tShowFormationPanelEffect = new ShowFormationPanelEffect (aFromActor);
		addEffect (tShowFormationPanelEffect);
	}

	public void addRebuildFormationPanelEffect (ActorI aFromActor) {
		RebuildFormationPanelEffect tRebuildFormationPanelEffect;
		
		tRebuildFormationPanelEffect = new RebuildFormationPanelEffect (aFromActor);
		addEffect (tRebuildFormationPanelEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " updated " + name;

		return tSimpleActionReport;
	}
}
