package ge18xx.round.action;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLNode;

public class TransferTrainAction extends TransferOwnershipAction {
	public final static String NAME = "Transfer Train";

	public TransferTrainAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public TransferTrainAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
}
