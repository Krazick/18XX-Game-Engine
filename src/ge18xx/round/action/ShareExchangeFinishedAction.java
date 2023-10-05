package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.utilities.XMLNode;

public class ShareExchangeFinishedAction extends FormationPhaseAction {
	public final static String NAME = "Share Exchange Finished";

	public ShareExchangeFinishedAction () {
		this (NAME);
	}

	public ShareExchangeFinishedAction (String aName) {
		super (aName);
	}

	public ShareExchangeFinishedAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ShareExchangeFinishedAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " has finished all Share Exchanges";

		return tSimpleActionReport;
	}
}
