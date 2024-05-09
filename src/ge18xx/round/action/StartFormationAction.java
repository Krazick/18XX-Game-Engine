package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.SetFormationStateEffect;
import ge18xx.round.action.effects.ShowFormationPanelEffect;
import ge18xx.round.action.effects.StartFormationEffect;
import geUtilities.XMLNode;

public class StartFormationAction extends ChangeStateAction {
	public final static String NAME = "Start Formation";
	public final static StartFormationAction NO_START_FORMATION_ACTION = null;

	public StartFormationAction () {
		this (NAME);
	}

	public StartFormationAction (String aName) {
		super (aName);
	}

	public StartFormationAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public StartFormationAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
//    <Effect actor="Mark" class="ge18xx.round.action.effects.ShowFormationPanelEffect" fromName="Mark" isAPrivate="false" name="Show Formation Panel" order="10"/>
//    <Effect actor="Mark" class="ge18xx.round.action.effects.SetFormationStateEffect" fromName="Mark" isAPrivate="false" name="Set Formation State" newState="Loan Repayment" order="11" previousState="No State"/>
//    <Effect actor="Mark" class="ge18xx.round.action.effects.StartFormationEffect" fromName="Mark" isAPrivate="false" name="Start Formation" order="12"/>
	
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
