package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.BorrowTrainEffect;
import ge18xx.train.Train;
import geUtilities.xml.XMLNode;

public class BorrowTrainAction extends TransferTrainAction {
	public final static String NAME = "Borrow Train";

	public BorrowTrainAction () {
		this (NAME);
	}

	public BorrowTrainAction (String aName) {
		super (aName);
	}

	public BorrowTrainAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public BorrowTrainAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addBorrowTrainEffect (Bank aBank, Train aTrain, TrainCompany aTrainCompany) {
		BorrowTrainEffect tBorrowTrainEffect;
	
		tBorrowTrainEffect = new BorrowTrainEffect (aBank, aTrain, aTrainCompany);
		addEffect (tBorrowTrainEffect);
	}
}
