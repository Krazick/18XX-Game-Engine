package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.train.Train;
import geUtilities.XMLNode;

public class ReturnTrainEffect extends BorrowTrainEffect {
	public final static String NAME = "Return Train";

	public ReturnTrainEffect () {
		super ();
		setName (NAME);
	}

	public ReturnTrainEffect (TrainCompany aTrainCompany, Train aTrain, Bank aBank) {
		super (aBank, aTrain, aTrainCompany);
		setName (NAME);
	}

	public ReturnTrainEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = super.undoEffect (aRoundManager);
		if (tEffectApplied) {
			train.setBorrowed (false);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = super.applyEffect (aRoundManager);
		if (tEffectUndone) {
			train.setBorrowed (true);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
