package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.xml.XMLNode;

public class TokenExchangeFinishedAction extends FormationPhaseAction {
	public final static String NAME = "Token Exchange Finished";

	public TokenExchangeFinishedAction () {
		this (NAME);
	}

	public TokenExchangeFinishedAction (String aName) {
		super (aName);
	}

	public TokenExchangeFinishedAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public TokenExchangeFinishedAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " has finished all Token Exchanges";

		return tSimpleActionReport;
	}
}
