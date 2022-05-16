package ge18xx.company;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.GUI;

public class BuyItemFrame extends JFrame implements ChangeListener {
	protected static final String SET_BUY_PRICE_ACTION = "SetBuyPrice";
	protected static final String BUY_ACTION = "Buy";
	private static final long serialVersionUID = 1L;
	JButton doSetPriceButton;
	JButton doBuyButton;
	JPanel buyItemPanel;
	JPanel buttonPanel;
	JPanel rangePricePanel;
	JLabel description;
	JLabel range;
	JLabel buyerInfo;
	JLabel sellerInfo;
	int minPrice;
	int maxPrice;
	JTextField priceField;
	String itemName;

	
	public BuyItemFrame (String aTitle) {
		super (aTitle);
		description = new JLabel ("Description");
		range = new JLabel ("Range");
		buyerInfo = new JLabel ("Buyer info");
		sellerInfo = new JLabel ("Seller info");
		buildRangePricePanel ();
		buildButtonPanel ();
		buildBuyItemPanel ();
		add (buyItemPanel);
	}

	public void buildBuyItemPanel () {
		if (buyItemPanel == GUI.NO_PANEL) {
			buyItemPanel = new JPanel ();
			buyItemPanel.setLayout (new BoxLayout (buyItemPanel, BoxLayout.Y_AXIS));
			buyItemPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (Box.createVerticalStrut (10));
			description.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (description);
			rangePricePanel.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (rangePricePanel);
			buyerInfo.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (buyerInfo);
			buyItemPanel.add (Box.createVerticalStrut (10));
			sellerInfo.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (sellerInfo);
			buyItemPanel.add (Box.createVerticalStrut (10));
			buttonPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
			buyItemPanel.add (buttonPanel);
		}		
	}
	
	public void buildRangePricePanel () {
		JLabel tBuyPriceLabel;
		
		priceField = new JTextField ();
		priceField.setPreferredSize (new Dimension (20, 24));
		priceField.setMaximumSize (new Dimension (40, 24));
		priceField.setAlignmentX (Component.RIGHT_ALIGNMENT);
		priceField.setHorizontalAlignment (SwingConstants.RIGHT);
		priceField.setColumns (3); 
		
		tBuyPriceLabel = new JLabel ("Buy Price: ");
		
		rangePricePanel = new JPanel ();
		rangePricePanel.setLayout (new BoxLayout (rangePricePanel, BoxLayout.X_AXIS));
		rangePricePanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		rangePricePanel.add (range);
		rangePricePanel.add (Box.createHorizontalStrut (10));
		rangePricePanel.add (tBuyPriceLabel);
		rangePricePanel.add (Box.createHorizontalStrut (10));		
		rangePricePanel.add (priceField);
	}
	
	public void updateBuyItemPanel (String aItemName, String aDescription, 
					int aMinPrice, int aMaxPrice) {
		updateBuyItemPanel (aItemName, aDescription, aMinPrice, aMaxPrice, "", "");
	}
	
	public void updateBuyItemPanel (String aItemName, String aDescription, 
					int aMinPrice, int aMaxPrice, String aBuyerInfo, String aSellerInfo) {
		String tRange;
		
		if (validRange (aMinPrice, aMaxPrice)) {
			setItemName (aItemName);
			description.setText (aDescription);
			setMinPrice (aMinPrice);
			setMaxPrice (aMaxPrice);
			tRange = generateRange ();
			range.setText (tRange);
			updateBuyerInfo (aBuyerInfo);
			updateSellerInfo (aSellerInfo);
		}
	}
	
	public void setItemName (String aItemName) {
		itemName = aItemName;
	}
	
	public void updateBuyerInfo (String aBuyerInfo) {
		buyerInfo.setText (aBuyerInfo);
	}
	
	public void updateSellerInfo (String aSellerInfo) {
		sellerInfo.setText (aSellerInfo);
	}
	
	public void setMinPrice (int aMinPrice) {
		minPrice = aMinPrice;
	}
	
	public void setMaxPrice (int aMaxPrice) {
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
			tRange = "Price " + Bank.formatCash (minPrice);
		} else {
			tRange = "Range (" + Bank.formatCash (minPrice) + " to " + 
						Bank.formatCash (maxPrice) + ") ";
		}
		
		return tRange;
	}
	
	public void setBuyButtonText (String aBuyButtonText) {
		doBuyButton.setText (aBuyButtonText);
	}
	
	public void updateBuyButton (boolean aEnable, String aToolTip) {
		updateButton (doBuyButton, aEnable, aToolTip);
	}
	
	public void updateSetPriceButton (boolean aEnable, String aToolTip) {
		updateButton (doSetPriceButton, aEnable, aToolTip);
	}
	
	public void updateButton (JButton aButton, boolean aEnable, String aToolTip) {
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
	
	@Override
	public void stateChanged (ChangeEvent e) {
		// TODO Auto-generated method stub

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

		doSetPriceButton = buildButton ("Set Buy Price", SET_BUY_PRICE_ACTION);
		doBuyButton = buildButton (CorporationFrame.BUY_TRAIN, BUY_ACTION);
		buttonPanel.add (doSetPriceButton);
		buttonPanel.add (Box.createHorizontalStrut (10));
		buttonPanel.add (doBuyButton);
		buttonPanel.add (Box.createHorizontalStrut (10));
	}
	
	public JButton buildButton (String aButtonLabel, String aActionCommand) {
		JButton tActionButton;

		tActionButton = new JButton (aButtonLabel);
		tActionButton.setAlignmentX (CENTER_ALIGNMENT);
		tActionButton.setActionCommand (aActionCommand);

		return tActionButton;
	}

	public void setAllButtonListeners (ActionListener aActionListener) {
		setButtonListener (doSetPriceButton, aActionListener);
		setButtonListener (doBuyButton, aActionListener);
	}
	
	public void setButtonListener (JButton aButton, ActionListener aActionListener) {
		aButton.addActionListener (aActionListener);		
	}
	
	public void setDefaultPrice () {
		setPrice (1);
	}

	public void setPrice (int aPrice) {
		priceField.setText (aPrice + "");
	}

}
