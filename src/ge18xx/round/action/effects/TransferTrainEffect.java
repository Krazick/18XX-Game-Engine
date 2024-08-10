package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Corporation;
import ge18xx.company.Coupon;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class TransferTrainEffect extends ToEffect {
	public static final AttributeName AN_TRAIN_NAME = new AttributeName ("trainName");
	public static final AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	public static final String NAME = "Transfer Train";
	Train train;
	String trainName;	// Need to save in case the Train is not found where expected

	public TransferTrainEffect () {
		super ();
		setName (NAME);
		setTrain (Train.NO_TRAIN);
		setToActor (ActorI.NO_ACTOR);
	}

	public TransferTrainEffect (ActorI aFromActor, Train aTrain, ActorI aToActor) {
		super (NAME, aFromActor, aToActor);
		setTrain (aTrain);
		setToActor (aToActor);
	}

	public TransferTrainEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tFromActorName;
		ActorI tFromActor;
		String tTrainName;
		Train tTrain;
		Corporation tCorporation;
		TrainCompany tTrainCompany;

		tFromActorName = aEffectNode.getThisAttribute (ActorI.AN_FROM_ACTOR_NAME);
		tFromActor = aGameManager.getActor (tFromActorName);
		setActor (tFromActor);

		tTrainName = aEffectNode.getThisAttribute (AN_TRAIN_NAME);
		trainName = tTrainName;			// Save it for Post Parse if needed
		setTrain (null);
		if (tFromActor.isABankPool ()) {
			tTrain = aGameManager.getBankPoolTrain (tTrainName);
			if (tTrain == Train.NO_TRAIN) {
				tTrain = aGameManager.getTrain (tTrainName);
			}
			setTrain (tTrain);
		} else if (tFromActor.isABank ()) {
			tTrain = aGameManager.getTrain (tTrainName);
			setTrain (tTrain);
		} else if (tFromActor.isACorporation ()) {
			tCorporation = (Corporation) tFromActor;
			if (tCorporation.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCorporation;
				tTrain = tTrainCompany.getTrain (tTrainName);
				if (tTrain != Train.NO_TRAIN) {
					setTrain (tTrain);
				} else {
					tTrain = aGameManager.getTrain (tTrainName);
					if (tTrain != Train.NO_TRAIN) {
						setTrain (tTrain);
					} else {
						System.err.println (
								"Can't find [" + tTrainName + "] in Train Portfolio for " + tTrainCompany.getAbbrev ());
					}
				}
			} else {
				System.err.println ("From Actor " + tFromActorName + " is not a Train Company");
			}
		} else {
			System.err.println ("From Actor " + tFromActorName + " is not a Corporation");
		}
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_TRAIN_NAME, getTrainName ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " named " + getTrainName () + " from " + getActorName () + " to "
				+ getToActorName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public Coupon getTrain () {
		return train;
	}

	public String getTrainName () {
		String tTrainName = Train.NO_TRAIN_NAME;

		if (train != Train.NO_TRAIN) {
			tTrainName = train.getName ();
		}

		return tTrainName;
	}

	public void setTrain (Train aTrain) {
		train = aTrain;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainHolderI tFromHolder;
		TrainPortfolio tToTrainPortfolio;

		tEffectApplied = false;
		tToTrainPortfolio = getToTrainPortfolio ();
		train.clearCurrentRoute ();
		train.clearPreviousRoute ();
		train.clearSelection ();
		tToTrainPortfolio.addTrain (train);
		tToTrainPortfolio.clearAllTrainSelections ();
		tFromHolder = (TrainHolderI) getActor ();
		tFromHolder.removeTrain (getTrainName ());
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainHolderI tFromHolder;
		TrainPortfolio tToTrainPortfolio;

		tEffectUndone = false;
		tToTrainPortfolio = getToTrainPortfolio ();
		tToTrainPortfolio.removeTrain (getTrainName ());
		tFromHolder = (TrainHolderI) getActor ();
		train.clearCurrentRoute ();
		train.clearPreviousRoute ();
		train.clearSelection ();
		tToTrainPortfolio.clearAllTrainSelections ();
		tFromHolder.addTrain (train);

		tEffectUndone = true;

		return tEffectUndone;
	}

	private TrainPortfolio getToTrainPortfolio () {
		TrainHolderI tToHolder;
		TrainPortfolio tToTrainPortfolio;
		Bank tBank;
		BankPool tBankPool;

		tToHolder = (TrainHolderI) getToActor ();
		tToTrainPortfolio = tToHolder.getTrainPortfolio ();

		// If the Actor is the Bank, confirm the Bank has the Train,
		// If not, the Train has been rusted, so get the Rusted Portfolio
		if (isToActor (Bank.NAME)) {
			if (tToHolder.hasTrainNamed (getTrainName ())) {
				tBank = (Bank) getToActor ();
				tToTrainPortfolio = tBank.getRustedTrainPortfolio ();
			}
			/*
			 * If the ToActor is the BankPool -- the Train was either in excess of limit due
			 * to Phase Change , or the Company had been closed, and need to get from the
			 * Bank Pool Portfolio
			 */
		} else if (isToActor (BankPool.NAME)) {
			if (tToHolder.hasTrainNamed (getTrainName ())) {
				tBankPool = (BankPool) getToActor ();
				tToTrainPortfolio = tBankPool.getTrainPortfolio ();
			}
		}

		return tToTrainPortfolio;
	}
}
