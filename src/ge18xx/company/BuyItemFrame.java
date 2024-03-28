package ge18xx.company;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.PurchaseOfferAction;

import geUtilities.GUI;
import swingDelays.KButton;

public class BuyItemFrame extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	public static final String SET_BUY_PRICE_ACTION = "SetBuyPrice";
	public static final String BUY_ACTION = "Buy";
	KButton doSetPriceButton;
	KButton doBuyButton;
	JPanel buyItemPanel;
	JPanel buttonPanel;
	JPanel rangePricePanel;
	JLabel description;
	JLabel range;
	JLabel buyerInfo;
	JLabel sellerInfo;
	JLabel buyPriceLabel;
	JTextField priceField;
	int minPrice;
	int maxPrice;
	TrainCompany trainCompany;
	String itemName;
	protected ActorI currentOwner;

	public BuyItemFrame (String aTitle, TrainCompany aTrainCompany) {
		super (aTitle);
		trainCompany = aTrainCompany;
		description = new JLabel ("Description");
		range = new JLabel ("Range");
		buyerInfo = new JLabel ("Buyer info");
		sellerInfo = new JLabel ("Seller info");

		buildPriceRangePanel ();
		buildButtonPanel ();
		buildBuyItemPanel ();
		add (buyItemPanel);

		pack ();
		setSize (520, 170);
		setVisible (false);
	}

	private void buildBuyItemPanel () {
		if (buyItemPanel == GUI.NO_PANEL) {
			buyItemPanel = new JPanel ();
			buyItemPanel.setLayout (new BoxLayout (buyItemPanel, BoxLayout.Y_AXIS));
			buyItemPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (Box.createVerticalStrut (10));
			description.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (description);
			buyItemPanel.add (Box.createVerticalStrut (10));
			rangePricePanel.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (rangePricePanel);
			buyItemPanel.add (Box.createVerticalStrut (10));
			buyerInfo.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (buyerInfo);
			buyItemPanel.add (Box.createVerticalStrut (10));
			sellerInfo.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (sellerInfo);
			buyItemPanel.add (Box.createVerticalStrut (10));
			buttonPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (buttonPanel);
			buyItemPanel.add (Box.createVerticalStrut (10));
		}
	}

	private void buildPriceRangePanel () {
		buildPriceField ();

		rangePricePanel = new JPanel ();
		rangePricePanel.setLayout (new BoxLayout (rangePricePanel, BoxLayout.X_AXIS));
		rangePricePanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		rangePricePanel.add (range);
		buyPriceLabel = new JLabel ("Buy Price: ");
		rangePricePanel.add (Box.createHorizontalStrut (10));
		rangePricePanel.add (buyPriceLabel);
		rangePricePanel.add (Box.createHorizontalStrut (10));
		rangePricePanel.add (priceField);
	}

	@Override
	public void requestFocus () {
		priceField.requestFocus ();
	}

	public void setCurrentOwner (ActorI aCurrentOwner) {
		currentOwner = aCurrentOwner;
	}

	public void setItemName (String aItemName) {
		itemName = aItemName;
	}

	protected void updateBuyerInfo () {
		String tBuyerInfo;
		int tRemainingCash;

		tRemainingCash = trainCompany.getTreasury () - getPrice ();
		tBuyerInfo = trainCompany.getName () + " will have " + Bank.formatCash (tRemainingCash) +
				" after purchase.";
		updateBuyerInfo (tBuyerInfo);
	}

	protected void updateBuyerInfo (String aBuyerInfo) {
		buyerInfo.setText (aBuyerInfo);
	}

	protected void updateSellerInfo (String aSellerInfo) {
		sellerInfo.setText (aSellerInfo);
	}

	protected void setMinPrice (int aMinPrice) {
		minPrice = aMinPrice;
	}

	protected void setMaxPrice (int aMaxPrice) {
		maxPrice = aMaxPrice;
	}

	public boolean validRange (int aMinPrice, int aMaxPrice) {
		boolean tValidPrice;

		if (aMinPrice < 1) {
			tValidPrice = false;
		} else if (aMaxPrice < aMinPrice) {
			tValidPrice = false;
		} else {
			tValidPrice = true;
		}

		return tValidPrice;
	}

	public boolean fixedPrice () {
		return (minPrice == maxPrice);
	}

	public String generateRange () {
		String tRange;

		if (fixedPrice ()) {
			tRange = "Fixed Price " + Bank.formatCash (minPrice);
		} else {
			tRange = "Range (" + Bank.formatCash (minPrice) + " to " +
						Bank.formatCash (maxPrice) + ") ";
		}

		return tRange;
	}

	protected void setBuyButtonText (ActorI aCurrentOwner) {
		String tBuyButtonText;
		String tPrefix;

		if (samePresident (aCurrentOwner, trainCompany)) {
			tPrefix = "Buy ";
		} else {
			tPrefix = "Offer to Buy ";
		}
		tBuyButtonText = tPrefix + itemName + " for " + Bank.formatCash (getPrice ());
		setBuyButtonText (tBuyButtonText);
	}

	public void setBuyButtonText (String aBuyButtonText) {
		doBuyButton.setText (aBuyButtonText);
	}

	public void updateBuyButton (boolean aEnable, String aToolTip) {
		int tTrainPrice;
		int tCompanyTreasury;
		
		tTrainPrice = getPrice ();
		tCompanyTreasury = trainCompany.getTreasury ();
		if (tTrainPrice > tCompanyTreasury) {
			updateButton (doBuyButton, false, trainCompany.getAbbrev () + " does not have enough cash to buy the Train.");
		} else {
			updateButton (doBuyButton, aEnable, aToolTip);
		}
	}

	public void updateSetPriceButton (boolean aEnable, String aToolTip) {
		boolean tVisible;
		
		if (fixedPrice ()) {
			tVisible = false;
		} else {
			tVisible = true;
		}
		if (doSetPriceButton != GUI.NO_BUTTON) {
			if (priceIsGood ()) {
				updateButton (doSetPriceButton, aEnable, aToolTip);
			} else {
				updateButton (doSetPriceButton, false, getBuyToolTip ());
			}
		}
		doSetPriceButton.setVisible (tVisible);
	}

	protected void setPriceField (boolean aVisible, boolean aEnabled, String aToolTip) {
		buyPriceLabel.setVisible (aVisible);
		priceField.setVisible (aVisible);
		priceField.setEnabled (aEnabled);
		priceField.setToolTipText (aToolTip);
	}

	public void updateButton (KButton aButton, boolean aEnable, String aToolTip) {
		aButton.setEnabled (aEnable);
		aButton.setToolTipText (aToolTip);
	}

	protected int getPrice () {
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

	protected boolean samePresident (ActorI aOwningActor, TrainCompany aBuyingTrainCompany) {
		boolean tSamePresident;
		String tPresidentName, tOwningPresidentName;
		TrainCompany tOwningTrainCompany;
		Player tOwningPlayer;

		tSamePresident = false;
		tPresidentName = aBuyingTrainCompany.getPresidentName ();
		if (aOwningActor.isATrainCompany ()) {
			tOwningTrainCompany = (TrainCompany) aOwningActor;
			tOwningPresidentName = tOwningTrainCompany.getPresidentName ();
			if (tOwningPresidentName.equals (tPresidentName)) {
				tSamePresident = true;
			}
		} else if (aOwningActor.isAPlayer ()) {
			tOwningPlayer = (Player) aOwningActor;
			tOwningPresidentName = tOwningPlayer.getName ();
			tPresidentName = aBuyingTrainCompany.getPresidentName ();
			if (tOwningPresidentName.equals (tPresidentName)) {
				tSamePresident = true;
			}
		}

		return tSamePresident;
	}

	protected void sendPurchaseOffer (ActorI aItemOwner, QueryOffer aQueryOffer) {
		String tOperatingRoundID;
		PurchaseOfferAction tPurchaseOfferAction;
		String tItemType;
		String tItemName;
		ActorI.ActionStates tOldState, tNewState;

		tOldState = trainCompany.getStatus ();
		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		aQueryOffer = trainCompany.getQueryOffer ();
		tItemType = aQueryOffer.getItemType ();
		tItemName = aQueryOffer.getItemName ();
		tPurchaseOfferAction = new PurchaseOfferAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
				trainCompany);
		tPurchaseOfferAction.addPurchaseOfferEffect (trainCompany, aItemOwner, getPrice (), tItemType, tItemName);

		trainCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		tNewState = trainCompany.getStatus ();
		tPurchaseOfferAction.addChangeCorporationStatusEffect (trainCompany, tOldState, tNewState);
		trainCompany.addAction (tPurchaseOfferAction);
	}

	protected boolean needToMakeOffer (ActorI aOwningActor, TrainCompany aBuyingCompany) {
		boolean tNeedToMakeOffer = true;
		GameManager tGameManager;

		tGameManager = aBuyingCompany.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			if (samePresident (aOwningActor, aBuyingCompany)) {
				tNeedToMakeOffer = false;
			}
		} else {
			tNeedToMakeOffer = false;
		}

		return tNeedToMakeOffer;
	}

	protected boolean priceIsGood () {
		boolean tGoodPrice;
		int tPrice;

		tPrice = getPrice ();
		tGoodPrice = true;
		if (tPrice < minPrice) {
			tGoodPrice = false;
		}
		if (tPrice > maxPrice) {
			tGoodPrice = false;
		}

		return tGoodPrice;
	}

	protected String getBuyToolTip () {
		String tBuyToolTip;
		int tPrice;

		tPrice = getPrice ();
		tBuyToolTip = "Ready for Purchase";
		if (tPrice < minPrice) {
			tBuyToolTip = "Must choose price > " + (minPrice - 1);
		}
		if (tPrice > maxPrice) {
			tBuyToolTip = "Must choose price < " + (maxPrice + 1);
		}

		return tBuyToolTip;
	}

	private void buildButtonPanel () {
		buttonPanel = new JPanel ();
		buttonPanel.setLayout (new BoxLayout (buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentY (Component.CENTER_ALIGNMENT);

		doBuyButton = buildButton (CorporationFrame.BUY_TRAIN, BUY_ACTION);
		doSetPriceButton = buildButton ("Set Buy Price", SET_BUY_PRICE_ACTION);
		updateSetPriceButton (false, "Price Field has not changed");
		buttonPanel.add (doSetPriceButton);
		buttonPanel.add (Box.createHorizontalStrut (10));

		buttonPanel.add (doBuyButton);
		buttonPanel.add (Box.createHorizontalStrut (10));
	}

	public KButton buildButton (String aButtonLabel, String aActionCommand) {
		KButton tActionButton;

		tActionButton = new KButton (aButtonLabel);
		tActionButton.setAlignmentX (CENTER_ALIGNMENT);
		tActionButton.setActionCommand (aActionCommand);

		return tActionButton;
	}

	public void setAllButtonListeners (ActionListener aActionListener) {
		if (doSetPriceButton != GUI.NO_BUTTON) {
			setButtonListener (doSetPriceButton, aActionListener);
		}
		if (doBuyButton != GUI.NO_BUTTON) {
			setButtonListener (doBuyButton, aActionListener);
		}
	}

	public void setButtonListener (KButton aButton, ActionListener aActionListener) {
		aButton.addActionListener (aActionListener);
	}

	protected void setDefaultPrice () {
		setPrice (1);
	}

	public void setPrice (int aPrice) {
		priceField.setText (aPrice + "");
	}

	private void buildPriceField () {
		priceField = new JTextField ();
		priceField.setPreferredSize (new Dimension (20, 24));
		priceField.setMaximumSize (new Dimension (40, 24));
		priceField.setAlignmentX (Component.RIGHT_ALIGNMENT);
		priceField.setHorizontalAlignment (SwingConstants.RIGHT);
		priceField.setColumns (3);
		priceField.addKeyListener (this);
	}

	@Override
	public void keyTyped (KeyEvent aKeyEvent) {
		handleKeyEvent (aKeyEvent);
	}

	@Override
	public void keyPressed (KeyEvent aKeyEvent) {
		handleKeyEvent (aKeyEvent);
	}

	@Override
	public void keyReleased (KeyEvent aKeyEvent) {
		handleKeyEvent (aKeyEvent);
	}

	private void handleKeyEvent (KeyEvent aKeyEvent) {
		int tEventID;

		tEventID = aKeyEvent.getID ();
		if (tEventID == KeyEvent.KEY_RELEASED) {
			updateBuyButton (false, "Price Field has changed");
			updateSetPriceButton (true, "Price Field has changed");
		}
	}

	protected int getCurrentOwnerCash () {
		TrainCompany tTrainCompany;
		Player tPlayer;
		int tCurrentOwnerCash;

		tCurrentOwnerCash = 0;
		if (currentOwner.isAPlayer ()) {
			tPlayer = (Player) currentOwner;
			tCurrentOwnerCash = tPlayer.getCash ();
		} else if (currentOwner.isATrainCompany ()) {
			tTrainCompany = (TrainCompany) currentOwner;
			tCurrentOwnerCash = tTrainCompany.getCash ();
		}

		return tCurrentOwnerCash;
	}

	protected void setFrameLocation () {
		Point tNewPoint;
		GameManager tGameManager;

		tGameManager = trainCompany.getGameManager ();
		tNewPoint = tGameManager.getOffsetCorporationFrame ();
		setLocation (tNewPoint);
		setVisible (true);
	}

	protected void updateButtons () {
		String tBuyToolTip;
		boolean tEnableBuyButton;

		tBuyToolTip = getBuyToolTip ();
		tEnableBuyButton = priceIsGood ();
		updateBuyButton (tEnableBuyButton, tBuyToolTip);
		setBuyButtonText (currentOwner);
		updateSetPriceButton (false, "Price Field has not changed");
	}

	protected void updateInfo (String aItemType, int aLowPrice, int aHighPrice, String aDescription) {
		updateBuyItemPanel (aItemType, aDescription, aLowPrice, aHighPrice);
		updateBuyerInfo ();
		setBuyButtonText (currentOwner);
		updateButtons ();
		setFrameLocation ();
	}

	public void updateBuyItemPanel (String aItemName, String aDescription,
					int aMinPrice, int aMaxPrice) {
		String tRange;

		if (validRange (aMinPrice, aMaxPrice)) {
			setItemName (aItemName);
			description.setText (aDescription);
			setMinPrice (aMinPrice);
			setMaxPrice (aMaxPrice);
			tRange = generateRange ();
			range.setText (tRange);
		}
	}
}
