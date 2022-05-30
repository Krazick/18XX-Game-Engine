package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ge18xx.bank.Bank;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.PurchaseOfferAction;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;

public class BuyTrainFrame extends BuyItemFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	Train train;

	public BuyTrainFrame (TrainCompany aTrainCompany, TrainHolderI aCurrentOwner, Train aSelectedTrain) {
		super (CorporationFrame.BUY_TRAIN, aTrainCompany);

		setAllButtonListeners (this);
		train = aSelectedTrain;
		if (aCurrentOwner.isATrainCompany ()) {
			setCurrentOwner ((TrainCompany) aCurrentOwner);
		} else {
			setCurrentOwner (Corporation.NO_CORPORATION);
		}
		updateInfo ();
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		String tActionCommand;
		
		tActionCommand = e.getActionCommand ();
		if (tActionCommand.equals (SET_BUY_PRICE_ACTION)) {
			updateButtons ();
			updateBuyerInfo ();
			updateSellerInfo ();
		}
		if (tActionCommand == BUY_ACTION) {
			buyTrain ();
			setVisible (false);
		}
	}

	private void buyTrain () {
		TrainCompany tOwningTrainCompany;
		int tCashValue;
		CorporationFrame tCorporationFrame;
		PurchaseOffer tPurchaseOffer;
		
		if (train != Train.NO_TRAIN) {
			tCashValue = getPrice ();
			tOwningTrainCompany = (TrainCompany) (currentOwner);
			if (needToMakeOffer (tOwningTrainCompany, trainCompany)) {
				tPurchaseOffer = makePurchaseOffer (tOwningTrainCompany);
				
				tCorporationFrame = trainCompany.getCorporationFrame ();
				// TODO Disable Buttons on BuyItemFrame, add Status Message (Waiting for Response to offer)
				tCorporationFrame.waitForResponse ();
				System.out.println ("Response Received for Buy Train");
				// Once a Response is received, examine for Accept or Reject of the Purchase Offer
				// If Accept, perform the Buy Train
				if (tPurchaseOffer.wasAccepted ()) {
					buyTrain (tOwningTrainCompany, tCashValue, true);	
				} else {
					System.out.println ("Purchase Offer was Rejected");
				}
			
			} else {
				buyTrain (tOwningTrainCompany, tCashValue, false);
			}
		}
	}

	private void buyTrain (TrainCompany aOwningTrainCompany, int aCashValue, boolean aChainToPrevious) {
		BuyTrainAction tBuyTrainAction;
		CorporationFrame tCorporationFrame;
		String tOperatingRoundID;
		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
				trainCompany);
		tBuyTrainAction.setChainToPrevious (aChainToPrevious);
		trainCompany.transferCashTo (aOwningTrainCompany, aCashValue);
		tBuyTrainAction.addCashTransferEffect (trainCompany, aOwningTrainCompany, aCashValue);
		trainCompany.doFinalTrainBuySteps (aOwningTrainCompany, train, tBuyTrainAction);
		tCorporationFrame = trainCompany.getCorporationFrame ();
		tCorporationFrame.updateInfo ();
	}

	// TODO: Look at pushing this up to BuyItemFrame
	public PurchaseOffer makePurchaseOffer (TrainCompany aOwningTrainCompany) {
		PurchaseOfferAction tPurchaseOfferAction;
		PurchaseOffer tPurchaseOffer;
		ActorI.ActionStates tOldState, tNewState;
		String tOperatingRoundID;

		tPurchaseOffer = new PurchaseOffer (train.getName (), train.getType (), train,
				PrivateCompany.NO_PRIVATE_COMPANY, trainCompany.getAbbrev (), aOwningTrainCompany.getAbbrev (),
				getPrice (), trainCompany.getStatus ());
		
		tOldState = trainCompany.getStatus ();
		trainCompany.setPurchaseOffer (tPurchaseOffer);
		
		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		tPurchaseOfferAction = new PurchaseOfferAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
				trainCompany);
		
		tPurchaseOfferAction.addPurchaseOfferEffect (trainCompany, aOwningTrainCompany, getPrice (), 
				PurchaseOffer.TRAIN_TYPE, train.getName ());

		trainCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		tNewState = trainCompany.getStatus ();
		tPurchaseOfferAction.addChangeCorporationStatusEffect (trainCompany, tOldState, tNewState);
		trainCompany.addAction (tPurchaseOfferAction);

		return tPurchaseOffer;
	}

	public void updateInfo () {
		int tLowPrice, tHighPrice;
		String tDescription;
		
		setDefaultPrice ();
		tLowPrice = 1;
		tHighPrice = trainCompany.getTreasury ();
		tDescription = trainCompany.getPresidentName () + ", Choose Buy Price for " + 
				train.getName () + " " + PurchaseOffer.TRAIN_TYPE + " from " + currentOwner.getName ();
		updateBuyItemPanel (PurchaseOffer.TRAIN_TYPE, tDescription, tLowPrice, tHighPrice);
		updateBuyerInfo ();
		updateSellerInfo ();
		setBuyButtonText (currentOwner);
		
		setFrameLocation ();
	}

	protected void updateSellerInfo () {
		String tOwnerName = "NO OWNER";
		int tTreasury = 0;
		String tSellerInfo;
		
		if (currentOwner != Corporation.NO_CORPORATION) {
			tOwnerName = currentOwner.getName ();
			tTreasury = getCurrentOwnerCash () + getPrice ();
		}
		tSellerInfo =  tOwnerName + " Treasury will have " + Bank.formatCash (tTreasury) + 
						" after purchase.";
		updateSellerInfo (tSellerInfo);
	}
}
