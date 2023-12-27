package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.ClearAllTrainsFromMapEffect;
import ge18xx.round.action.effects.EndCorpActionsEffect;
import ge18xx.round.action.effects.SetFormationStateEffect;
import ge18xx.round.action.effects.ShowFormationPanelEffect;
import ge18xx.round.action.effects.StartFormationEffect;
import geUtilities.XMLNode;

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
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " completed Operations.";

		return tSimpleActionReport;
	}
	
	public void addShowFormationPanelEffect (ActorI aFromActor) {
		ShowFormationPanelEffect tShowFormationPanelEffect;
		
		tShowFormationPanelEffect = new ShowFormationPanelEffect (aFromActor);
		addEffect (tShowFormationPanelEffect);
	}

	public void addSetFormationStateEffect (ActorI aFromActor, ActorI.ActionStates aOldFormationState,
							ActorI.ActionStates aNewFormationState) {
		SetFormationStateEffect tSetFormationStateEffect;
		
		tSetFormationStateEffect = new SetFormationStateEffect (aFromActor, aOldFormationState, aNewFormationState);
		addEffect (tSetFormationStateEffect);
	}
	
	public void addStartFormationEffect (ActorI aActor, Corporation aFormingCorporation) {
		StartFormationEffect tStartFormationEffect;

			tStartFormationEffect = new StartFormationEffect (aActor, aFormingCorporation);
			addEffect (tStartFormationEffect);
	}
}
