package ge18xx.round.action;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLNode;

public class CloseCompanyAction extends TransferOwnershipAction {
	public final static String NAME = "Close Company";

	public CloseCompanyAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public CloseCompanyAction (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
}
