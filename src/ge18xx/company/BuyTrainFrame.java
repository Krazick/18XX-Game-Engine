package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ge18xx.bank.Bank;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.PurchaseOfferAction;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;

public class BuyTrainFrame extends BuyItemFrame implements ActionListener {
	private static final String ITEM_NAME = "Train";
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
		String tBuyToolTip;
		boolean tEnableBuyButton;
		
		tActionCommand = e.getActionCommand ();
		if (tActionCommand.equals (SET_BUY_PRICE_ACTION)) {
			tBuyToolTip = getBuyToolTip ();
			tEnableBuyButton = priceIsGood ();
			updateBuyButton (tEnableBuyButton, tBuyToolTip);
			setBuyButtonText (currentOwner);
			updateSetPriceButton (false, "Price Field has not changed");
			
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
		BuyTrainAction tBuyTrainAction;
		CorporationFrame tCorporationFrame;
		String tOperatingRoundID;

		if (train != Train.NO_TRAIN) {
			tCashValue = getPrice ();
			tOwningTrainCompany = (TrainCompany) (currentOwner);
			if (needToMakeOffer (tOwningTrainCompany, trainCompany)) {
				if (makePurchaseOffer (tOwningTrainCompany)) {
					tCorporationFrame = trainCompany.getCorporationFrame ();
					tCorporationFrame.waitForResponse ();
				}
			} else {
				tOperatingRoundID = trainCompany.getOperatingRoundID ();
				tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
						trainCompany);
				trainCompany.transferCashTo (tOwningTrainCompany, tCashValue);
				tBuyTrainAction.addCashTransferEffect (trainCompany, tOwningTrainCompany, tCashValue);
				trainCompany.doFinalTrainBuySteps (tOwningTrainCompany, train, tBuyTrainAction);
				tCorporationFrame = trainCompany.getCorporationFrame ();
				tCorporationFrame.updateInfo ();
			}
		}
	}

	public boolean makePurchaseOffer (TrainCompany aOwningTrainCompany) {
		PurchaseOfferAction tPurchaseOfferAction;
		PurchaseOffer tPurchaseOffer;
		ActorI.ActionStates tOldState, tNewState;
		boolean tOfferMade = true;
		String tOperatingRoundID;

		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		tPurchaseOffer = new PurchaseOffer (train.getName (), train.getType (), train,
				PrivateCompany.NO_PRIVATE_COMPANY, trainCompany.getAbbrev (), aOwningTrainCompany.getAbbrev (),
				getPrice (), trainCompany.getActionStatus ());
		tOldState = trainCompany.getStatus ();
		trainCompany.setPurchaseOffer (tPurchaseOffer);
		tPurchaseOfferAction = new PurchaseOfferAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
				trainCompany);
		tPurchaseOfferAction.addPurchaseOfferEffect (trainCompany, aOwningTrainCompany, getPrice (), train.getType (),
				train.getName ());

		trainCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		tNewState = trainCompany.getStatus ();
		tPurchaseOfferAction.addChangeCorporationStatusEffect (trainCompany, tOldState, tNewState);
		trainCompany.addAction (tPurchaseOfferAction);

		return tOfferMade;
	}

	public void doFinalTrainBuySteps (TrainCompany aOwningTrainCompany, Train aTrain, BuyTrainAction aBuyTrainAction) {
		ActorI.ActionStates tCurrentCorporationStatus, tNewCorporationStatus;
		TrainPortfolio tCompanyPortfolio, tOwningPortfolio;

		tCompanyPortfolio = trainCompany.getTrainPortfolio ();
		tOwningPortfolio = aOwningTrainCompany.getTrainPortfolio ();
		tCompanyPortfolio.addTrain (aTrain);
		tOwningPortfolio.removeSelectedTrain ();
		tCompanyPortfolio.clearSelections ();
		tOwningPortfolio.clearSelections ();
		tCurrentCorporationStatus = trainCompany.getStatus ();
		trainCompany.updateStatus (ActorI.ActionStates.BoughtTrain);
		tNewCorporationStatus = trainCompany.getStatus ();
		// TODO: possible issue when Buying a Train Between Companies, not saving the
		// train properly in the Effect
		aBuyTrainAction.addTransferTrainEffect (aOwningTrainCompany, aTrain, trainCompany);
		if (tCurrentCorporationStatus != tNewCorporationStatus) {
			aBuyTrainAction.addChangeCorporationStatusEffect (trainCompany, tCurrentCorporationStatus,
					tNewCorporationStatus);
		}
	}

	public void updateInfo () {
		int tLowPrice, tHighPrice;
		String tDescription;
		
		setDefaultPrice ();
		tLowPrice = 1;
		tHighPrice = trainCompany.getTreasury ();
		tDescription = trainCompany.getPresidentName () + ", Choose Buy Price for " + 
				train.getName () + " " + ITEM_NAME + " from " + currentOwner.getName ();
		updateBuyItemPanel (ITEM_NAME, tDescription, tLowPrice, tHighPrice);
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
