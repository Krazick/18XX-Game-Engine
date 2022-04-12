package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.ClearAllTrainsFromMapEffect;
import ge18xx.utilities.XMLNode;

public class DeclareBankruptcyAction extends Action {
	public final static String NAME = "Done";

	public DeclareBankruptcyAction () {
		this (NAME);
	}

	public DeclareBankruptcyAction (String aName) {
		super (aName);
	}

	public DeclareBankruptcyAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public DeclareBankruptcyAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState, ActorI.ActionStates aNewState) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aPreviousState, aNewState);
		addEffect (tChangeCorporationStatusEffect);
	}

	public void addClearTrainsFromMapEffect (Corporation aCorporation) {
		ClearAllTrainsFromMapEffect tClearTrainsFromMapEffect;
		
		tClearTrainsFromMapEffect = new ClearAllTrainsFromMapEffect (aCorporation);
		addEffect (tClearTrainsFromMapEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		
		tSimpleActionReport = actor.getName () + " Declared Bankruptcy.";
		
		return tSimpleActionReport;
	}

}
