package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.SpecialPanelEffect;
import ge18xx.utilities.XMLNode;

public class SpecialPanelAction extends Action {
	public final static String NAME = "Special Panel";

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

	public void addSpecialPanelEffect (ActorI aFromActor, ActorI aToActor) {
		SpecialPanelEffect tSpecialPanelEffect;
		
		tSpecialPanelEffect = new SpecialPanelEffect (aFromActor, aToActor);
		addEffect (tSpecialPanelEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " updated " + name;

		return tSimpleActionReport;
	}
}
