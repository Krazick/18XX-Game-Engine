package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.effects.TransferTrainEffect;
import ge18xx.train.Train;
import geUtilities.xml.XMLNode;

public class TransferTrainAction extends TransferOwnershipAction {
	public final static String NAME = "Transfer Train";

	public TransferTrainAction () {
		this (NAME);
	}

	public TransferTrainAction (String aName) {
		super (aName);
	}

	public TransferTrainAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public TransferTrainAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addTransferTrainEffect (ActorI aFromActor, Train aTrain, ActorI aToActor) {
		TransferTrainEffect tBoughtTrainEffect;
	
		tBoughtTrainEffect = new TransferTrainEffect (aFromActor, aTrain, aToActor);
		addEffect (tBoughtTrainEffect);
	}
}
