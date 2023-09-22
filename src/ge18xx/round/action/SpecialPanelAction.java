package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.HideSpecialPanelEffect;
import ge18xx.round.action.effects.ShowSpecialPanelEffect;
import ge18xx.utilities.XMLNode;

public class SpecialPanelAction extends Action {
	public final static String NAME = "Show Special Panel";

	public SpecialPanelAction () {
		this (NAME);
	}

	public SpecialPanelAction (String aName) {
		super (aName);
	}

	public SpecialPanelAction (Action aAction) {
		super (aAction);
	}

	public SpecialPanelAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public SpecialPanelAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addHideSpecialPanelEffect (ActorI aFromActor, ActorI aToActor) {
		HideSpecialPanelEffect tHideSpecialPanelEffect;
		
		tHideSpecialPanelEffect = new HideSpecialPanelEffect (aFromActor, aToActor);
		addEffect (tHideSpecialPanelEffect);
	}

	public void addShowSpecialPanelEffect (ActorI aFromActor, ActorI aToActor) {
		ShowSpecialPanelEffect tShowSpecialPanelEffect;
		
		tShowSpecialPanelEffect = new ShowSpecialPanelEffect (aFromActor, aToActor);
		addEffect (tShowSpecialPanelEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " updated " + name;

		return tSimpleActionReport;
	}
}
