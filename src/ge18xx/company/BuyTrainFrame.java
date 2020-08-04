package ge18xx.company;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.PurchaseOfferAction;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;

public class BuyTrainFrame extends JFrame implements ActionListener, ChangeListener, PropertyChangeListener {
	private static final String SET_BUY_PRICE_ACTION = "SetBuyPrice";
	private static final String BUY_ACTION = "BuyTrain";
	private static final long serialVersionUID = 1L;
	JButton doSetPriceButton;
	JButton doBuyButton;
	Train train;
	Corporation currentOwner;
	JPanel trainPanel;
	TrainCompany trainCompany;
	String operatingRoundID;
	GameManager gameManager;
	JTextField priceField;
	JLabel corporationTreasuryLabel;
	JLabel ownerTreasuryLabel;
	JLabel frameLabel;
	int buyingTrainCompanyTreasury, remainingTreasury;
	
	public BuyTrainFrame (TrainCompany aBuyingCompany, TrainHolderI aCurrentOwner, Train aSelectedTrain) {
		super ("Buy Train");

		trainCompany = aBuyingCompany;
		train = aSelectedTrain;
		if (aCurrentOwner instanceof TrainCompany) {
			currentOwner = (TrainCompany) aCurrentOwner;
		} else {
			currentOwner = CorporationList.NO_CORPORATION;
		}
		gameManager = trainCompany.getGameManager ();
		trainPanel = new JPanel ();
		trainPanel.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Choose Buy Price");
		frameLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		trainPanel.add (frameLabel);
		trainPanel.add (Box.createVerticalStrut (10));

		priceField = new JTextField ();
		priceField.setText ("0");
		priceField.setColumns (3); //get some space
		trainPanel.add (Box.createVerticalStrut (10));
		trainPanel.add (priceField);
		trainPanel.add (Box.createVerticalStrut (10));
		
		buyingTrainCompanyTreasury = trainCompany.getTreasury ();
		corporationTreasuryLabel = new JLabel ("Corporation");
		setCorporationTreasuryLabel ();
		trainPanel.add (corporationTreasuryLabel);
		
		ownerTreasuryLabel = new JLabel ("Owner");
		setOwnerTreasuryLabel ();
		trainPanel.add (ownerTreasuryLabel);
		trainPanel.add (Box.createVerticalStrut (10));
		
		doSetPriceButton = setActionButton ("Set Buy Price", SET_BUY_PRICE_ACTION);
		trainPanel.add (doSetPriceButton);
		doBuyButton = setActionButton ("Buy Train", BUY_ACTION);
		
		trainPanel.add (doBuyButton);
		add (trainPanel);

		pack ();
		setSize (500, 150);
		setVisible (false);
	}
	
	private int getPrice () {
		String tPrice;
		int tGetPrice;
		
		tPrice = priceField.getText ();
		if (tPrice.startsWith ("$")) {
			tPrice = tPrice.substring (1);
		}
		tPrice = tPrice.trim ();
		if (tPrice.equals ("")) {
			tGetPrice = 0;
		} else {
			try {
				tGetPrice = Integer.parseInt (tPrice);
			} catch (NumberFormatException eNFE) {
				tGetPrice = 0;
				priceField.setText ("0");
			}
		}
		
		return tGetPrice;
	}
	
	public void requestFocus () {
		priceField.requestFocus ();
	}
	
	private void setBuyButtonText () {
		doBuyButton.setText ("Buy Train for " + Bank.formatCash (getPrice ()));
	}
	
	private void setCorporationTreasuryLabel () {
		remainingTreasury = buyingTrainCompanyTreasury - getPrice ();
		corporationTreasuryLabel.setText (trainCompany.getName () + " will have " + 
				Bank.formatCash (remainingTreasury) + " after purchase.\n");		
	}
	
	public void setDefaultPrice () {
		setPrice (1);
	}
	
	public void setOwnerTreasuryLabel () {
		String tOwnerName = "NO OWNER";
		String tPresidentName = "NO PRESIDENT";
		int tTreasury = 0;
		String tLabel;
		
		if (train != TrainPortfolio.NO_TRAIN) {
			if (currentOwner != CorporationList.NO_CORPORATION) {
				tPresidentName = currentOwner.getPresidentName ();
				tOwnerName = currentOwner.getName ();
				tTreasury = currentOwner.getCash () + getPrice ();
			}
		}
		tLabel = "Prez: " + tPresidentName + " of " + tOwnerName + " Treasury After " + Bank.formatCash (tTreasury);
		ownerTreasuryLabel.setText (tLabel);
	}
	
	public void setPrice (int aPrice) {
		priceField.setText (aPrice + "");
		setBuyButtonText ();
	}
	
	public JButton setActionButton (String aButtonLabel, String aActionCommand) {
		JButton tActionButton;
		
		tActionButton = new JButton (aButtonLabel);
		tActionButton.setAlignmentX (CENTER_ALIGNMENT);
		tActionButton.setActionCommand (aActionCommand);
		tActionButton.addActionListener (this);
		
		return tActionButton;
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		int tPrice, tLowPrice, tHighPrice;
		boolean tGoodPrice;
		String tReasonForBad;
		String tActionCommand;
		
		tActionCommand = e.getActionCommand ();
		if (tActionCommand == SET_BUY_PRICE_ACTION) {
			tPrice = getPrice ();
			tLowPrice = 1;
			tHighPrice = buyingTrainCompanyTreasury;

			tGoodPrice = true;
			tReasonForBad = "Ready for Purchase";
			if (tPrice < tLowPrice) {
				tReasonForBad = "Must choose price > " + (tLowPrice - 1);
				tGoodPrice = false;
			}
			if (tPrice > tHighPrice) {
				tReasonForBad = "Must choose price < " + (tHighPrice + 1);
				tGoodPrice = false;
			}
			if (tPrice > buyingTrainCompanyTreasury) {
				tReasonForBad = "Company only has " + Bank.formatCash (buyingTrainCompanyTreasury) + " to spend.";
				tGoodPrice = false;
			}
			setBuyButtonText ();
			setCorporationTreasuryLabel ();
			setOwnerTreasuryLabel ();
			if (tGoodPrice) {
				doBuyButton.setEnabled (true);
			} else {
				System.out.println (tReasonForBad);
				doBuyButton.setEnabled (false);
				doBuyButton.setToolTipText (tReasonForBad);
			}
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
		
		if (train != TrainPortfolio.NO_TRAIN) {
			tCashValue = getPrice ();
			tOwningTrainCompany = (TrainCompany) (currentOwner);
			if (tOwningTrainCompany.getPresidentName ().equals (trainCompany.getPresidentName ())) {
				tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, 
						operatingRoundID, trainCompany);
				trainCompany.transferCashTo (tOwningTrainCompany, tCashValue);
				tBuyTrainAction.addCashTransferEffect (trainCompany, tOwningTrainCompany, tCashValue);
				trainCompany.doFinalTrainBuySteps (tOwningTrainCompany, train, tBuyTrainAction);
				tCorporationFrame = trainCompany.getCorporationFrame ();
				tCorporationFrame.updateInfo ();
			} else {
				makePurchaseOffer (tOwningTrainCompany);
			}
		}
	}
	
	public void makePurchaseOffer (TrainCompany aOwningTrainCompany) {
		PurchaseOfferAction tPurchaseOfferAction;
		PurchaseOffer tPurchaseOffer;
		ActorI.ActionStates tOldState, tNewState;
		
		tPurchaseOffer = new PurchaseOffer (train.getName (), train.getType (),
				train, CorporationList.NO_PRIVATE_COMPANY,
				trainCompany.getAbbrev (), aOwningTrainCompany.getAbbrev (), 
				getPrice (), trainCompany.getActionStatus ());
		tOldState = trainCompany.getStatus ();
		trainCompany.setPurchaseOffer (tPurchaseOffer);
		tPurchaseOfferAction = new PurchaseOfferAction (ActorI.ActionStates.OperatingRound,
				operatingRoundID, trainCompany);
		tPurchaseOfferAction.addPurchaseOfferEffect (trainCompany, aOwningTrainCompany, 
				getPrice (), train.getType (), train.getName ());
		
		trainCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		tNewState = trainCompany.getStatus ();
		tPurchaseOfferAction.addChangeCorporationStatusEffect (trainCompany, tOldState, tNewState);
		trainCompany.addAction (tPurchaseOfferAction);
		
		trainCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		// Set new Company State, Waiting for Reply to Purchase Offer
		// TODO -- All buttons should be disabled until the Response is received
	}
	
	public void doFinalTrainBuySteps (TrainCompany aOwningTrainCompany, 
			Train aTrain, BuyTrainAction aBuyTrainAction) {
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
		aBuyTrainAction.addTransferTrainEffect (aOwningTrainCompany, aTrain, trainCompany);
		if (tCurrentCorporationStatus != tNewCorporationStatus) {
			aBuyTrainAction.addChangeCorporationStatusEffect (trainCompany, 
					tCurrentCorporationStatus, tNewCorporationStatus);
		}
	}

	@Override
	public void stateChanged (ChangeEvent e) {
	    setCorporationTreasuryLabel ();
	}
	
	public void propertyChange (PropertyChangeEvent e) {
		setCorporationTreasuryLabel ();
	}
	
	public void updateInfo (Train aTrain) {
		int tLowPrice, tHighPrice;
		
		train = aTrain;
		setDefaultPrice ();
		tLowPrice = 1;
		tHighPrice = buyingTrainCompanyTreasury;
		buyingTrainCompanyTreasury = trainCompany.getTreasury ();
		operatingRoundID = trainCompany.getOperatingRoundID ();
		setCorporationTreasuryLabel ();
		setOwnerTreasuryLabel ();
		
		frameLabel.setText (trainCompany.getPresidentName () + ", choose Buy Price for " + 
				train.getName () + " from " +  currentOwner.getName () + 
				" Range [" + Bank.formatCash (tLowPrice) + " to " + Bank.formatCash (tHighPrice) + "]");
	}
}
