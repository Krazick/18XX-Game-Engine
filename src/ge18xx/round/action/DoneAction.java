package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.NewActingCorpEffect;
import ge18xx.utilities.XMLNode;

public class DoneAction extends Action {
	public final static String NAME = "Done";

	public DoneAction() {
		this (NAME);
	}

	public DoneAction (String aName) {
		super (aName);
	}
	
	public DoneAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public DoneAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addNewActingCorpEffect (Corporation aCorporation) {
		NewActingCorpEffect tNewActingCorpEffect;
		
		tNewActingCorpEffect = new NewActingCorpEffect (aCorporation);
		addEffect (tNewActingCorpEffect);
	}
	
	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState, ActorI.ActionStates aNewState) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aPreviousState, aNewState);
		addEffect (tChangeCorporationStatusEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " completed Operations.";
		
		return tSimpleActionReport;
	}
}
