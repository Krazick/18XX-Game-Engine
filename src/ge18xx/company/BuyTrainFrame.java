package ge18xx.company;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
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
	JPanel offerButtonPanel;
	JPanel offerPricePanel;
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
			currentOwner = Corporation.NO_CORPORATION;
		}
		gameManager = trainCompany.getGameManager ();
		trainPanel = new JPanel ();
		trainPanel.setBorder (new EmptyBorder (10, 10, 10, 10));
		trainPanel.setLayout (new BoxLayout (trainPanel, BoxLayout.Y_AXIS));
		trainPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		
		trainPanel.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Choose Buy Price");
		frameLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		trainPanel.add (frameLabel);
		trainPanel.add (Box.createVerticalStrut (10));

		buyingTrainCompanyTreasury = trainCompany.getTreasury ();
		setOfferTopPanel ();
		trainPanel.add (offerPricePanel);
		trainPanel.add (Box.createVerticalStrut (10));
		
		ownerTreasuryLabel = new JLabel ("Owner");
		setOwnerTreasuryLabel ();
		ownerTreasuryLabel.setAlignmentX (CENTER_ALIGNMENT);
		trainPanel.add (ownerTreasuryLabel);
		trainPanel.add (Box.createVerticalStrut (10));
		
		setOfferButtonPanel ();
		trainPanel.add (offerButtonPanel);
		add (trainPanel);

		pack ();
		setSize (520, 170);
		setVisible (false);
	}
	
	private void setOfferTopPanel () {		
		priceField = new JTextField ();
		corporationTreasuryLabel = new JLabel ("Corporation");
		setCorporationTreasuryLabel ();
		offerPricePanel = new JPanel ();
		offerPricePanel.add (Box.createVerticalStrut (10));
		offerPricePanel.setLayout (new BoxLayout (offerPricePanel, BoxLayout.X_AXIS));
		offerPricePanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		
		priceField.setText ("0");
		priceField.setPreferredSize ( new Dimension ( 80, 24 ) );
		priceField.setMaximumSize ( new Dimension ( 100, 24 ) );
		priceField.setAlignmentX (Component.RIGHT_ALIGNMENT);
		priceField.setColumns (4); //get some space
		
		offerPricePanel.add (priceField);
		offerPricePanel.add (Box.createHorizontalStrut (10));
		offerPricePanel.add (corporationTreasuryLabel);
		offerPricePanel.add (Box.createHorizontalStrut (10));
	}

	private void setOfferButtonPanel () {
		offerButtonPanel = new JPanel ();
		offerButtonPanel.setLayout (new BoxLayout (offerButtonPanel, BoxLayout.X_AXIS));
		offerButtonPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		
		doSetPriceButton = setActionButton ("Set Buy Price", SET_BUY_PRICE_ACTION);
		doBuyButton = setActionButton ("Buy Train", BUY_ACTION);
		offerButtonPanel.add (doSetPriceButton);
		offerButtonPanel.add (Box.createHorizontalStrut (10));
		offerButtonPanel.add (doBuyButton);
		offerButtonPanel.add (Box.createHorizontalStrut (10));
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
	
	@Override
	public void requestFocus () {
		priceField.requestFocus ();
	}
	
	private void setBuyButtonText () {
		if (corporationsHaveSamePresident ()) {
			doBuyButton.setText ("Buy Train for " + Bank.formatCash (getPrice ()));
		} else {
			doBuyButton.setText ("Offer to Buy Train for " + Bank.formatCash (getPrice ()));
		}
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
		
		if (train != Train.NO_TRAIN) {
			if (currentOwner != Corporation.NO_CORPORATION) {
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
				doBuyButton.setEnabled (false);
				doBuyButton.setToolTipText (tReasonForBad);
			}
		}
		if (tActionCommand == BUY_ACTION) {
			buyTrain ();
			setVisible (false);
		}
	}

	private boolean samePresident (TrainCompany aOwningTrainCompany) {
		boolean tSamePresident = false;
		String tPresidentName, tOwningPresidentName;
		
		tOwningPresidentName = aOwningTrainCompany.getPresidentName ();
		tPresidentName = trainCompany.getPresidentName ();
		if (tOwningPresidentName.equals (tPresidentName)) {
			tSamePresident = true;
		}
		
		return tSamePresident;
	}
	
	private boolean needToMakeOffer (TrainCompany aOwningTrainCompany) {
		boolean tNeedToMakeOffer = true;
		
		if (gameManager.isNetworkGame ()) {
			if (samePresident (aOwningTrainCompany)) {
				tNeedToMakeOffer = false;
			}
		} else {
			tNeedToMakeOffer = false;
		}
		
		return tNeedToMakeOffer;
	}
	
	private void buyTrain () {
		TrainCompany tOwningTrainCompany;
		int tCashValue;
		BuyTrainAction tBuyTrainAction;
		CorporationFrame tCorporationFrame;
		
		if (train != Train.NO_TRAIN) {
			tCashValue = getPrice ();
			tOwningTrainCompany = (TrainCompany) (currentOwner);
			if (needToMakeOffer (tOwningTrainCompany)) {
				if (makePurchaseOffer (tOwningTrainCompany)) {
					tCorporationFrame = trainCompany.getCorporationFrame ();
					tCorporationFrame.waitForResponse ();
				}
			} else {
				tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, 
						operatingRoundID, trainCompany);
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
		
		tPurchaseOffer = new PurchaseOffer (train.getName (), train.getType (),
				train, PrivateCompany.NO_PRIVATE_COMPANY,
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
		
		return tOfferMade;
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
		// TODO: possible issue when Buying a Train Between Companies, not saving the train properly in the Effect
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
	
	@Override
	public void propertyChange (PropertyChangeEvent e) {
		setCorporationTreasuryLabel ();
	}
	
	private boolean corporationsHaveSamePresident () {
		boolean tCorporationsHaveSamePresident = false;
		String tTrainPresidentName;
		String tOwnerPresidentName;
		
		if ((trainCompany != Corporation.NO_ACTOR) &&
			(currentOwner != Corporation.NO_ACTOR)) {
			tTrainPresidentName = trainCompany.getPresidentName ();
			tOwnerPresidentName = currentOwner.getPresidentName ();
			if (tTrainPresidentName.equals (tOwnerPresidentName)) {
				tCorporationsHaveSamePresident = true;
			}
		}
		
		return tCorporationsHaveSamePresident;
	}
	
	public void updateInfo (Train aTrain) {
		int tLowPrice, tHighPrice;
		Point tNewPoint;
		
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
		frameLabel.setAlignmentX (CENTER_ALIGNMENT);
		tNewPoint = gameManager.getOffsetCorporationFrame ();
		setLocation (tNewPoint);
	}
}
