package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

// TODO: Refactor TransferTrainEffect, TransferOwnershipEffect, ResponseToOfferEffect, and CashTransferEffect
// to extend a new SuperClass "ToEffect" to hold the "toActor" and methods setToActor, getToActor, getToActorName

public class TransferTrainEffect extends Effect {
	final static AttributeName AN_TRAIN_NAME = new AttributeName ("trainName");
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	public final static String NAME = "Transfer Train";
	ActorI toActor;
	Train train;
	
	public TransferTrainEffect () {
		super ();
		setName (NAME);
		setTrain (Train.NO_TRAIN);
		setToActor (ActorI.NO_ACTOR);
	}

	public TransferTrainEffect (ActorI aFromActor, Train aTrain, ActorI aToActor) {
		super (NAME, aFromActor);
		setTrain (aTrain);
		setToActor (aToActor);
	}

	public TransferTrainEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tFromActorName;
		String tToActorName;
		ActorI tFromActor, tToActor;
		String tTrainName;
		Train tTrain;
		Corporation tCorporation;
		TrainCompany tTrainCompany;
		
		tFromActorName = aEffectNode.getThisAttribute (ActorI.AN_FROM_ACTOR_NAME);
		tToActorName = aEffectNode.getThisAttribute (ActorI.AN_TO_ACTOR_NAME);
		tFromActor = aGameManager.getActor (tFromActorName);
		tToActor = aGameManager.getActor (tToActorName);
		setActor (tFromActor);
		setToActor (tToActor);
		
		tTrainName = aEffectNode.getThisAttribute (AN_TRAIN_NAME);
		setTrain (null);
		if (tFromActor.isABank ()) {
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
						System.err.println ("Can't find [" + tTrainName + "] in Train Portfolio for " + tTrainCompany.getAbbrev ());
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
		tEffectElement.setAttribute (ActorI.AN_TO_ACTOR_NAME, getToActorName ());
	
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " named " + getTrainName () + " from " + 
				getActorName () + " to " + getToActorName () + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public ActorI getToActor () {
		return toActor;
	}

	@Override
	public String getToActorName () {
		String tToActorName = ActorI.NO_NAME;
		
		if (toActor != ActorI.NO_ACTOR) {
			tToActorName = toActor.getName ();
		}
		
		return tToActorName;
	}
	
	public Train getTrain () {
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
	
	public void setToActor (ActorI aToActor) {
		toActor = aToActor;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainHolderI tToHolder, tFromHolder;
		TrainPortfolio tToTrainPortfolio;
		Bank tBank;
		BankPool tBankPool;

		tEffectApplied = false;
		tToHolder = (TrainHolderI) toActor;
		
		// Consider moving this statement as a final "Else" clause below... 
		tToTrainPortfolio = tToHolder.getTrainPortfolio ();
		
		// TODO: When the new "ToEffect" is created, also create a new "isActor" Method that receives an Actor's Name and does
		// the comparison of the toActor's Name
		// TODO: Also move this If/Else If Block to a new method "getToTrainPortfolio" to get from Bank (Rusted) or BankPool Train Portfolio
		
		/* If the ToActor is the Bank -- the Train was Rusted */
		if (toActor.getName ().equals (Bank.NAME)){
			if (tToHolder.hasTrainNamed (getTrainName ())) {
				tBank = (Bank) toActor;
				tToTrainPortfolio = tBank.getRustedTrainPortfolio ();
			}
		/* If the ToActor is the BankPool -- the Train is either in excess of limit, 
		 * or the Company had been closed, and need to send it to the Bank Pool
		 */
		} else if (toActor.getName ().equals (BankPool.NAME)) {
			if (tToHolder.hasTrainNamed (train.getName ())) {
				tBankPool = (BankPool) toActor;
				tToTrainPortfolio = tBankPool.getTrainPortfolio ();
			}
		}

		tToTrainPortfolio.addTrain (train);
		tFromHolder = (TrainHolderI) actor;
		tFromHolder.removeTrain (getTrainName ());
		tEffectApplied = true;
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainHolderI tToHolder, tFromHolder;
		TrainPortfolio tToTrainPortfolio;
		Bank tBank;
		BankPool tBankPool;
		
		tEffectUndone = false;
		tToHolder = (TrainHolderI) toActor;
		tToTrainPortfolio = tToHolder.getTrainPortfolio ();

		/* If the ToActor is the Bank -- the Train was Rusted, need to "un-rust" the Train */
		
		// TODO: When the new "ToEffect" is created, also create a new "isActor" Method that receives an Actor's Name and does
		// the comparison of the toActor's Name
		// TODO: Also move this If/Else If Block to a new method "getToTrainPortfolio" to get from Bank (Rusted) or BankPool Train Portfolio
		
		if (toActor.getName ().equals (Bank.NAME)){
			if (tToHolder.hasTrainNamed (getTrainName ())) {
				tBank = (Bank) toActor;
				tToTrainPortfolio = tBank.getRustedTrainPortfolio ();
			}
		/* If the ToActor is the BankPool -- the Train was either in excess of limit, or the Company had 
		 * been closed, and need to get from the Bank Pool
		 */
		} else if (toActor.getName ().equals (BankPool.NAME)) {
			if (tToHolder.hasTrainNamed (getTrainName ())) {
				tBankPool = (BankPool) toActor;
				tToTrainPortfolio = tBankPool.getTrainPortfolio ();
			}
		}

		tToTrainPortfolio.removeTrain (getTrainName ());
		tFromHolder = (TrainHolderI) actor;
		tFromHolder.addTrain (train);
		
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}
