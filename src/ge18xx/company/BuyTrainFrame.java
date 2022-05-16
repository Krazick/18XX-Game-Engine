package ge18xx.company;

//import java.awt.Component;
//import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

//import javax.swing.Box;
//import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.PurchaseOfferAction;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;

public class BuyTrainFrame extends BuyItemFrame implements ActionListener, PropertyChangeListener {
	private static final String ITEM_NAME = "Train";
	private static final long serialVersionUID = 1L;
	JButton doSetPriceButton;
	JButton doBuyButton;
	Train train;
	Corporation currentOwner;
	JPanel trainPanel;
	JPanel offerButtonPanel;
	JPanel offerPricePanel;
	TrainCompany trainCompany;
	String operatingRoundID;
	JLabel corporationTreasuryLabel;
	JLabel ownerTreasuryLabel;
	JLabel frameLabel;
	int buyingTrainCompanyTreasury, remainingTreasury;

	public BuyTrainFrame (TrainCompany aBuyingCompany, TrainHolderI aCurrentOwner, Train aSelectedTrain) {
		super (CorporationFrame.BUY_TRAIN);

		setAllButtonListeners (this);
		trainCompany = aBuyingCompany;
		train = aSelectedTrain;
		if (aCurrentOwner.isATrainCompany ()) {
			currentOwner = (TrainCompany) aCurrentOwner;
		} else {
			currentOwner = Corporation.NO_CORPORATION;
		}

		pack ();
		setSize (520, 170);
		setVisible (false);
	}

	@Override
	public void requestFocus () {
		priceField.requestFocus ();
	}

	private void setBuyButtonText () {
		String tBuyButtonText;
		
		if (samePresident (currentOwner, trainCompany)) {
			tBuyButtonText = "Buy Train for " + Bank.formatCash (getPrice ());
		} else {
			tBuyButtonText = "Offer to Buy Train for " + Bank.formatCash (getPrice ());
		}
		setBuyButtonText (tBuyButtonText);
	}

	private void setCorporationTreasuryLabel () {
		String tBuyerInfo;
		
		remainingTreasury = buyingTrainCompanyTreasury - getPrice ();
		tBuyerInfo = trainCompany.getName () + " will have " + Bank.formatCash (remainingTreasury) + 
				" after purchase.";
		updateBuyerInfo (tBuyerInfo);
	}

	public void setOwnerTreasuryLabel () {
		String tOwnerName = "NO OWNER";
		int tTreasury = 0;
		String tSellerInfo;
		
		if (train != Train.NO_TRAIN) {
			if (currentOwner != Corporation.NO_CORPORATION) {
				tOwnerName = currentOwner.getName ();
				tTreasury = currentOwner.getCash () + getPrice ();
			}
		}
		tSellerInfo =  tOwnerName + " Treasury will have " + Bank.formatCash (tTreasury) + 
						" after purchase.";
		updateSellerInfo (tSellerInfo);
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
			setBuyButtonText ();
			
			setCorporationTreasuryLabel ();
			setOwnerTreasuryLabel ();
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

		if (train != Train.NO_TRAIN) {
			tCashValue = getPrice ();
			tOwningTrainCompany = (TrainCompany) (currentOwner);
			if (needToMakeOffer (tOwningTrainCompany, trainCompany)) {
				if (makePurchaseOffer (tOwningTrainCompany)) {
					tCorporationFrame = trainCompany.getCorporationFrame ();
					tCorporationFrame.waitForResponse ();
				}
			} else {
				tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, operatingRoundID,
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

		tPurchaseOffer = new PurchaseOffer (train.getName (), train.getType (), train,
				PrivateCompany.NO_PRIVATE_COMPANY, trainCompany.getAbbrev (), aOwningTrainCompany.getAbbrev (),
				getPrice (), trainCompany.getActionStatus ());
		tOldState = trainCompany.getStatus ();
		trainCompany.setPurchaseOffer (tPurchaseOffer);
		tPurchaseOfferAction = new PurchaseOfferAction (ActorI.ActionStates.OperatingRound, operatingRoundID,
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

	@Override
	public void stateChanged (ChangeEvent e) {
		setCorporationTreasuryLabel ();
	}

	@Override
	public void propertyChange (PropertyChangeEvent e) {
		setCorporationTreasuryLabel ();
	}

	public void updateInfo (Train aTrain) {
		int tLowPrice, tHighPrice;
		Point tNewPoint;
		String tDescription;
		GameManager tGameManager;
		
		train = aTrain;
		setDefaultPrice ();
		buyingTrainCompanyTreasury = trainCompany.getTreasury ();
		tLowPrice = 1;
		tHighPrice = buyingTrainCompanyTreasury;
		tDescription = trainCompany.getPresidentName () + ", Choose Buy Price for " + 
				train.getName () + " " + ITEM_NAME + " from " + currentOwner.getName ();
		updateBuyItemPanel (ITEM_NAME, tDescription, tLowPrice, tHighPrice);
		operatingRoundID = trainCompany.getOperatingRoundID ();
		setCorporationTreasuryLabel ();
		setOwnerTreasuryLabel ();
		
		tGameManager = trainCompany.getGameManager ();
		tNewPoint = tGameManager.getOffsetCorporationFrame ();
		setLocation (tNewPoint);
	}
}
