package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ge18xx.bank.Bank;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
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
			setCurrentOwner (aCurrentOwner);
		} else {
			setCurrentOwner (Corporation.NO_CORPORATION);
		}
		updateInfo ();
		setSize (520, 190);
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
	
	protected QueryOffer buildPurchaseOffer (ActorI aItemOwner, Train aTrain, String aFromActorName) {
		PurchaseTrainOffer tPurchaseTrainOffer;
		QueryOffer tQueryOffer;
		ActorI.ActionStates tOldState;
		String tItemName;
		String tOwnerName;

		tOldState = trainCompany.getStatus ();
		tOwnerName = aItemOwner.getName ();
		tItemName = aTrain.getName ();
		tPurchaseTrainOffer = new PurchaseTrainOffer (tItemName, aTrain, aFromActorName, tOwnerName, getPrice (), tOldState);
		tQueryOffer = tPurchaseTrainOffer;
		trainCompany.setQueryOffer (tPurchaseTrainOffer);

		return tQueryOffer;
	}

	private void buyTrain () {
		TrainCompany tOwningTrainCompany;
		int tCashValue;
		QueryOffer tQueryOffer;
		String tBuyingOwnerName;

		if (train != Train.NO_TRAIN) {
			tBuyingOwnerName = trainCompany.getPresidentName ();
			tCashValue = getPrice ();
			tOwningTrainCompany = (TrainCompany) (currentOwner);
			if (needToMakeOffer (tOwningTrainCompany, trainCompany)) {
				tQueryOffer = buildPurchaseOffer (tOwningTrainCompany, train, tBuyingOwnerName);
				sendPurchaseOffer (tOwningTrainCompany, tQueryOffer);
				setVisible (false);
				trainCompany.waitForResponse ();

				// Once a Response is received, examine for Accept or Reject of the Purchase Offer
				// If Accept, perform the Buy Train
				if (tQueryOffer.wasAccepted ()) {
					buyTrain (tOwningTrainCompany, tCashValue, true);
				} else {
					// TODO: Notify with Dialog the Offer was Rejected
					System.out.println ("Purchase Offer for Train was Rejected");
				}
			} else {
				buyTrain (tOwningTrainCompany, tCashValue, false);
			}
		}
	}

	private void buyTrain (TrainCompany aOwningTrainCompany, int aCashValue, boolean aChainToPrevious) {
		BuyTrainAction tBuyTrainAction;
		String tOperatingRoundID;

		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, trainCompany);
		tBuyTrainAction.setChainToPrevious (aChainToPrevious);
		tBuyTrainAction.addCashTransferEffect (trainCompany, aOwningTrainCompany, aCashValue);
		trainCompany.transferCashTo (aOwningTrainCompany, aCashValue);
		trainCompany.doFinalTrainBuySteps (aOwningTrainCompany, train, tBuyTrainAction);
		trainCompany.updateInfo ();
	}
	
	private void updateInfo () {
		int tLowPrice;
		int tHighPrice;
		String tDescription;
		String tPresidentName;
		String tTrainName;
		String tOwnerName;
		String tPriceChoice;
		String tToolTip;
		boolean tVisible;
		boolean tEnabled;
		boolean tFixedPrice;
		Corporation tCurrentCompanyOwner;

		tPresidentName = trainCompany.getPresidentName ();
		tTrainName = train.getName ();
		tOwnerName = currentOwner.getName ();
		
		tFixedPrice = false;
		tLowPrice = 1;
		tHighPrice = trainCompany.getTreasury ();
		tPriceChoice = ", Choose Buy Price for ";
		tVisible = true;
		tEnabled = true;
		tToolTip = trainCompany.getAbbrev () + " can choose the price to pay for the Train.";
		if (currentOwner.isATrainCompany ()) {
			tCurrentCompanyOwner = (Corporation) currentOwner;
			if (tCurrentCompanyOwner.mustPayFullPrice () ) {
				tLowPrice = train.getPrice ();
				tHighPrice = train.getPrice ();
				tPriceChoice = " Buy Price for ";
				tVisible = false;
				tEnabled = false;
				tToolTip = trainCompany.getAbbrev () + " must pay full face value of the Train.";	
				tFixedPrice = true;
			}
		} 
		if (!tFixedPrice) {
			if (trainCompany.mustPayFullPrice ()) {
				tLowPrice = train.getPrice ();
				tHighPrice = train.getPrice ();
				tPriceChoice = " Buy Price for ";
				tVisible = false;
				tEnabled = false;
				tToolTip = trainCompany.getAbbrev () + " must pay full face value of the Train.";
			}
		}
		setPriceField (tVisible, tEnabled, tToolTip);
		setPrice (tLowPrice);
		tDescription = tPresidentName + tPriceChoice + tTrainName + " " + PurchaseTrainOffer.TRAIN_TYPE + 
				" from " + tOwnerName;

		updateSellerInfo ();
		updateInfo (PurchaseTrainOffer.TRAIN_TYPE, tLowPrice, tHighPrice, tDescription);
	}

	private void updateSellerInfo () {
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
