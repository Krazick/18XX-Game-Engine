package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.SyncActionNumberEffect;
import geUtilities.XMLNode;

public class SyncActionNumber extends Action {
	public final static String NAME = "Sync Action Number";

	public SyncActionNumber () {
		this (NAME);
	}

	public SyncActionNumber (String aName) {
		super (aName);
	}

	public SyncActionNumber (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public SyncActionNumber (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addSyncActionNumberEffect (ActorI aPlayer, int aNewActionNumber) {
		SyncActionNumberEffect tSyncActionNumberEffect;

		tSyncActionNumberEffect = new SyncActionNumberEffect (aPlayer, aNewActionNumber);
		addEffect (tSyncActionNumberEffect);
	}

}
