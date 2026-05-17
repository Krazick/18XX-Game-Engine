package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.xml.XMLNode;

public class ShareExchangeFinishedAction extends FormationRoundAction {
	public static final String NAME = "Share Exchange Finished";

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
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " has finished all Share Exchanges";

		return tSimpleActionReport;
	}
}
