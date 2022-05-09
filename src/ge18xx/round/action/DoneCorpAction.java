package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.ClearAllTrainsFromMapEffect;
import ge18xx.round.action.effects.EndCorpActionsEffect;
import ge18xx.utilities.XMLNode;

public class DoneCorpAction extends Action {
	public final static String NAME = "Done";

	public DoneCorpAction () {
		this (NAME);
	}

	public DoneCorpAction (String aName) {
		super (aName);
	}

	public DoneCorpAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public DoneCorpAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addNewActingCorpEffect (Corporation aCorporation) {
		EndCorpActionsEffect tEndCorpActionsEffect;

		tEndCorpActionsEffect = new EndCorpActionsEffect (aCorporation);
		addEffect (tEndCorpActionsEffect);
	}

	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
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

		tSimpleActionReport = actor.getName () + " completed Operations.";

		return tSimpleActionReport;
	}
}
