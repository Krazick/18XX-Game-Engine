package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.train.Train;
import geUtilities.xml.XMLNode;

public class BorrowTrainEffect extends TransferTrainEffect {
	public final static String NAME = "Borrow Train";
	
	public BorrowTrainEffect () {
		super ();
		setName (NAME);
	}

	public BorrowTrainEffect (Bank aBank, Train aTrain, TrainCompany aTrainCompany) {
		super (aBank, aTrain, aTrainCompany);
	}

	public BorrowTrainEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = super.applyEffect (aRoundManager);
		if (tEffectApplied) {
			train.setBorrowed (true);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = super.undoEffect (aRoundManager);
		if (tEffectUndone) {
			train.setBorrowed (false);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
