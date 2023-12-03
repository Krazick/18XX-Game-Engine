package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.StartFormationEffect;
import ge18xx.utilities.XMLNode;

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
	
	public void addStartFormationEffect (ActorI aActor, Corporation aFormingCorporation) {
		StartFormationEffect tStartFormationEffect;

			tStartFormationEffect = new StartFormationEffect (aActor, aFormingCorporation);
			addEffect (tStartFormationEffect);
	}
}
