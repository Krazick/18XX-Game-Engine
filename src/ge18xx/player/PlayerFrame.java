package ge18xx.player;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.StartPacketFrame;
import ge18xx.game.GameManager;
import ge18xx.toplevel.XMLFrame;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerFrame extends XMLFrame implements ActionListener, ItemListener {
	static final String DONE = "Done";
	static final String UNDO = "Undo";
	static final String PASS = "Pass";
	static final String BUY = "Buy";
	static final String BID = "Bid";
	static final String BUY_BID = "Buy-Bid";
	static final String SELL = "Sell";
	static final String EXCHANGE = "EXCHANGE";
	private static final long serialVersionUID = 1L;
	Container playerAndBankBox;
	Container bankBox;
	Container playerBox;
	Container actionButtonBox;
	JPanel playerInfoJPanel;
	JPanel portfolioInfoJPanel;
	Player player;
	JLabel playerCash;
	JLabel playerCertificateCount;
	JLabel playerPassed;
	JLabel playerAuctionPassed;
	JLabel playerBidAmount;
	JLabel playerPortfolioValue;
	JLabel playerTotalValue;
	JButton passActionButton;
	JButton buyBidActionButton;
	JButton sellActionButton;
	JButton exchangeActionButton;
	JButton undoActionButton;
	int portfolioInfoIndex;
	
	public PlayerFrame (String aFrameName, Player aPlayer, String aGameName) {
		super (aFrameName, aGameName);
		
		if (aPlayer != Player.NO_PLAYER) {
			player = aPlayer;
			
			playerAndBankBox = Box.createHorizontalBox ();
			bankBox = Box.createVerticalBox ();
			playerBox = Box.createVerticalBox ();
			playerInfoJPanel = new JPanel ();
			playerBox.add (Box.createVerticalStrut (10));
			playerInfoJPanel.setBorder (BorderFactory.createTitledBorder ("Information For " + player.getName ()));
			BoxLayout tLayout = new BoxLayout (playerInfoJPanel, BoxLayout.X_AXIS);
			playerInfoJPanel.setLayout (tLayout);
			playerInfoJPanel.setAlignmentX (CENTER_ALIGNMENT);
			
			addPlayerInfoJPanelLabel (null);
			playerCash = new JLabel ("");
			addPlayerInfoJPanelLabel (playerCash);
			setCashLabel ();
			
			playerCertificateCount = new JLabel ("");
			addPlayerInfoJPanelLabel (playerCertificateCount);
			setCertificateCountLabel ();
			
			playerPortfolioValue = new JLabel ("");
			addPlayerInfoJPanelLabel (playerPortfolioValue);
			setPortfolioValueLabel ();
			
			playerTotalValue = new JLabel ("");
			addPlayerInfoJPanelLabel (playerTotalValue);
			setTotalValueLabel ();
			
			playerBox.add (playerInfoJPanel);
			playerBox.add (Box.createVerticalStrut (10));
			
			createActionButtonBox ();
			playerBox.add (actionButtonBox);
			
			portfolioInfoIndex = 5;
			updatePortfolioInfo ();

			playerAndBankBox.add (Box.createHorizontalStrut(20));
			playerAndBankBox.add (playerBox);
			playerAndBankBox.add (Box.createHorizontalGlue ());
			playerAndBankBox.add (bankBox);
			playerAndBankBox.add(Box.createHorizontalStrut(20));
			add (playerAndBankBox);
			setSize (850, 900);
		}
	}
	
	private void addPlayerInfoJPanelLabel (JLabel aLabel) {
		Dimension tMinSize = new Dimension (10, 20);
		Dimension tPrefSize = new Dimension (15, 20);
		Dimension tMaxSize = new Dimension (Short.MAX_VALUE, 20);

		if (aLabel != null) {
			playerInfoJPanel.add (aLabel);
		}
		playerInfoJPanel.add (new Box.Filler (tMinSize, tPrefSize, tMaxSize));		
	}
	
	private JButton setupActionButton (String aButtonLabel, String aButtonAction) {
		JButton tActionButton;
		
		tActionButton = new JButton (aButtonLabel);
		tActionButton.setAlignmentX (CENTER_ALIGNMENT);
		tActionButton.setActionCommand (aButtonAction);
		tActionButton.addActionListener (this);
	
		return tActionButton;
	}

	private void createActionButtonBox () {
		actionButtonBox = Box.createHorizontalBox ();

		passActionButton = setupActionButton (PASS, PASS);
		buyBidActionButton = setupActionButton (BUY_BID, BUY_BID);
		sellActionButton = setupActionButton (SELL, SELL);
		exchangeActionButton = setupActionButton (EXCHANGE, EXCHANGE);
		undoActionButton = setupActionButton (UNDO, UNDO);
				
		actionButtonBox.add (passActionButton);
		actionButtonBox.add (buyBidActionButton);
		actionButtonBox.add (sellActionButton);
		actionButtonBox.add (exchangeActionButton);
		actionButtonBox.add (undoActionButton);
	}
	
	public void fillBankBox (GameManager aGameManager) {
		Bank tBank;
		BankPool tBankPool;
		JPanel tBPPortfolioJPanel;
		JPanel tBankPortfolioJPanel;
		JPanel tStartPacketPortfolioJPanel;
		
		tBank = player.getBank ();
		tBankPool = player.getBankPool ();
		
		bankBox.removeAll ();
		if (tBank.isStartPacketPortfolioEmpty ()) {
			tBPPortfolioJPanel = tBankPool.buildPortfolioInfoJPanel (this, player, aGameManager);
			
			bankBox.add (Box.createVerticalGlue ());
			bankBox.add (tBPPortfolioJPanel);
			bankBox.add (Box.createVerticalGlue ());
			
			tBankPortfolioJPanel = tBank.buildPortfolioInfoJPanel (this, player, aGameManager);
			bankBox.add (tBankPortfolioJPanel);
			bankBox.add (Box.createVerticalGlue ());
		} else {
			tStartPacketPortfolioJPanel = tBank.buildStartPacketInfoJPanel (this, player, aGameManager);
			bankBox.add (Box.createVerticalGlue ());
			bankBox.add (tStartPacketPortfolioJPanel);
			bankBox.add (Box.createVerticalGlue ());
		}
		bankBox.revalidate ();
		bankBox.repaint ();
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionText;
		
		if (PASS.equals (aEvent.getActionCommand ())) {
			player.passAction ();	
		}
		if (BUY_BID.equals (aEvent.getActionCommand ())) {
			tActionText = buyBidActionButton.getText();
			if (tActionText.equals(BUY.toUpperCase())) {
				player.buyAction ();
			} else {
				player.bidAction ();
			}
		}
		if (DONE.equals (aEvent.getActionCommand ())) {
			player.doneAction ();	
		}
		if (SELL.equals (aEvent.getActionCommand ())) {
			player.sellAction ();	
		}
		if (EXCHANGE.equals(aEvent.getActionCommand ())) {
			player.exchangeAction ();
		}
		if (UNDO.equals (aEvent.getActionCommand ())) {
			player.undoAction ();	
		}
		player.updateRoundWindow ();
	}
	
	public boolean canCompleteTurn () {
		return player.canCompleteTurn ();
	}
	
	public String getReasonForNotCompleting () {
		String tReason;
		String tCompanyAbbrev;
		int tPercentMustSell;
		int tExceedCount;
		
		tCompanyAbbrev = player.hasExchangedShare ();
		tReason = ">>NONE<<";
		if (tCompanyAbbrev != null) {
			tPercentMustSell = player.getMustSellPercent (tCompanyAbbrev);
			if (tPercentMustSell > 0) {
				tReason = "Must Sell at least " + tPercentMustSell + "% of " + tCompanyAbbrev + " before completing due to Exchange";
			}
		} else {
			tExceedCount = player.exceedsCertificateLimitBy ();
			if (tExceedCount > 0) {
				tReason = "Portfolio has " + tExceedCount + " too many certificates";
			} else {
				tCompanyAbbrev = player.exceedsAnyCorpShareLimit ();
				if (tCompanyAbbrev != null) {
					tReason = "Portfolio has too many share of " + tCompanyAbbrev;
				}
			}
		}
		
		return tReason;
	}

	public boolean hasActed () {
		return player.hasActed ();
	}
	
	public boolean hasActionsToUndo () {
		return player.hasActionsToUndo ();
	}
	
	public boolean hasMustBuyCertificate () {
		return player.hasMustBuyCertificate ();
	}
	
	public boolean hasSelectedPrivateOrMinorToExchange () {
		return player.hasSelectedPrivateOrMinorToExchange ();
	}
	
	public boolean hasSelectedPrivateToBidOn () {
		return player.hasSelectedPrivateToBidOn ();
	}
	
	public boolean hasSelectedSameStocksToSell () {
		boolean tHasSelectedSameStocksToSell;
		
		tHasSelectedSameStocksToSell = false;
		
		if (player.hasShareCompanyStocks ()) {
			tHasSelectedSameStocksToSell = player.hasSelectedSameStocksToSell ();
		}
		
		return tHasSelectedSameStocksToSell;
	}
	
	public boolean willSaleOverfillBankPool () {
		boolean tWillSaleOverfillBankPool = false;
		
		tWillSaleOverfillBankPool = player.willSaleOverfillBankPool ();

		return tWillSaleOverfillBankPool;
	}

	public boolean hasSelectedStocksToSell () {
		boolean tSelectedStocksToSell;
		
		tSelectedStocksToSell = false;
		
		// Note -- If Player has no Share Company Stocks, nothing to sell -- Only Share Company stocks can be sold
		if (player.hasShareCompanyStocks ()) {
			// need to examine to see if any shares are selected to sell from the player			
			tSelectedStocksToSell = player.hasSelectedStocksToSell ();
		}
		
		return tSelectedStocksToSell;
	}
	
	public boolean hasSelectedPrezToExchange () {
		return player.hasSelectedPrezToExchange ();
	}
	
	public boolean hasSelectedStocksToBuy () {
		boolean tSelectedStocksToBuy;
		
		tSelectedStocksToBuy = player.hasSelectedStockToBuy ();
		// Need to examine if any shares have been selected to Buy from Bank Pool or Bank
		
		return tSelectedStocksToBuy;
	}
	
	public int getCostSelectedStocksToBuy () {
		int tSelectedStocksToBuyCost;
		
		tSelectedStocksToBuyCost = player.getCostSelectedStockToBuy ();
		
		return tSelectedStocksToBuyCost;
	}
	
	public void itemStateChanged (ItemEvent aItemEvent) {
		// May not need to do anything except update Action Buttons.
		Portfolio tPortfolio;
		
		tPortfolio = player.getPortfolio ();
		tPortfolio.itemStateChanged (aItemEvent);
		updateActionButtons ();
	}

	public void replacePortfolioInfo (JPanel aPortfolioContainer) {
		if (portfolioInfoJPanel == null) {
			setPortfolioInfoContainer (aPortfolioContainer);
		} else {
			playerBox.remove (portfolioInfoIndex);
			playerBox.remove (portfolioInfoIndex - 1);
		}
		playerBox.add (aPortfolioContainer);
		playerBox.add (Box.createVerticalStrut (10));
		playerBox.validate ();
	}

	public void setCashLabel () {
		int tCashValue;
		
		tCashValue = 0;
		if (player != Player.NO_PLAYER) {
			tCashValue = player.getCash ();
		}
		playerCash.setText ("Cash: " + Bank.formatCash (tCashValue));
		setTotalValueLabel ();
	}
	
	public void setCertificateCountLabel () {
		String tLabel;
		
		if (player != Player.NO_PLAYER) {
			tLabel = player.buildCertCountInfo ("Certificate count: ");
			playerCertificateCount.setText (tLabel);
		}
	}

	public void setDoneButton () {
		setPassDoneButton (DONE, DONE);
	}
	
	public void setPassButton () {
		setPassDoneButton (PASS, PASS);
	}
	
	// TODO: Test if the Par Price Frame is UP, and disable the Done Button if so -- 
	// Must set that Par Price before allowing the Stock Action to be done.
	
	public void setPassDoneButton (String tLabel, String tAction) {
		passActionButton.setText (tLabel);
		passActionButton.setActionCommand (tAction);
		if (hasSelectedStocksToBuy ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText ("A Stock has been selected to be Bought");
		} else if (hasSelectedStocksToSell ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText ("At least one Stock has been selected to be Sold");
		} else if (hasSelectedPrezToExchange ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText ("A President Share has been selected to be Exchanged");
		} else if (hasSelectedPrivateToBidOn ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText ("A Private has been selected to be Bid On");
		} else if (hasSelectedPrivateOrMinorToExchange ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText ("A Private/Minor has been selected to be Exchanged");
		} else if (player.isParPriceFrameActive () ) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText ("A Share Company needs to have Par Price selected - Find the Par Price Frame");		
		} else if (hasMustBuyCertificate ()) {
			setCannotPass ();
		} else {
			passActionButton.setEnabled (true);
			passActionButton.setToolTipText ("");
		}
	}
	
	private void setCannotPass () {
		passActionButton.setEnabled (false);
		passActionButton.setToolTipText ("Must buy the Private where COST == DISCOUNT");
		disableAllStartPacketButtons ("Must buy the Private where COST == DISCOUNT");
	}
	
	public void setPortfolioInfoContainer (JPanel aPortfolioInfoContainer) {
		portfolioInfoJPanel = aPortfolioInfoContainer;
	}
	
	public void setPortfolioValueLabel () {
		int tPortfolioValue;
		
		tPortfolioValue = 0;
		if (player != Player.NO_PLAYER) {
			tPortfolioValue = player.getPortfolioValue ();
		}
		playerPortfolioValue.setText ("Portfolio Value: " + Bank.formatCash (tPortfolioValue));
		setTotalValueLabel ();
	}
	
	public void setTotalValueLabel () {
		int tPortfolioValue;
		int tCashValue;
		int tTotalValue;
		
		if (playerTotalValue != null) {
			tPortfolioValue = 0;
			tCashValue = 0;
			if (player != Player.NO_PLAYER) {
				tPortfolioValue = player.getPortfolioValue ();
				tCashValue = player.getCash ();
			}
			tTotalValue = tPortfolioValue + tCashValue;
			playerTotalValue.setText ("Total Value: " + Bank.formatCash (tTotalValue));
		}
	}
	
	public void updateActionButtons () {
		boolean tStocksToSell, tStocksToBuy, tActionsToUndo, tStocksToSellSame;
		boolean tPrezToExchange, tCanCompleteTurn, tPrivateOrMinorToExchange;
		boolean tStocksToSellOverfill, tMustBuy, tPrivateToBidOn;
		String tToolTip;
		
		tMustBuy = hasMustBuyCertificate ();
		tStocksToSell = hasSelectedStocksToSell ();
		tStocksToSellSame = hasSelectedSameStocksToSell ();
		tStocksToSellOverfill = willSaleOverfillBankPool ();
		tStocksToBuy = hasSelectedStocksToBuy ();
		tActionsToUndo = hasActionsToUndo ();
		tPrivateToBidOn = hasSelectedPrivateToBidOn ();
		tPrezToExchange = hasSelectedPrezToExchange ();
		tPrivateOrMinorToExchange = hasSelectedPrivateOrMinorToExchange ();
		tCanCompleteTurn = canCompleteTurn ();
		
		// If there is a Must Buy -- Cannot Do a Pass, or a Bid -- disable these
		if (tMustBuy) {
			setCannotPass ();
		} else {
			if (tCanCompleteTurn) {
				passActionButton.setEnabled (tCanCompleteTurn);
				passActionButton.setToolTipText ("Can complete player turn");
			} else {
				tToolTip = getReasonForNotCompleting ();
				if (">>NONE<<".equals (tToolTip)) {
					passActionButton.setEnabled (true);
					passActionButton.setToolTipText ("");
				} else {
					passActionButton.setEnabled (tCanCompleteTurn);
					passActionButton.setToolTipText (tToolTip);
				}
			}
		}
		if (tStocksToSell) {
			if (tStocksToSellOverfill) {
				sellActionButton.setEnabled (false);
				sellActionButton.setToolTipText ("Stocks selected to be Sold will Overfill BankPool");
			} else if (tStocksToSellSame) {
				sellActionButton.setEnabled (tStocksToSell);
				sellActionButton.setToolTipText ("Stocks have been selected to Sell");
			} else {
				sellActionButton.setEnabled (tStocksToSellSame);
				sellActionButton.setToolTipText ("Stocks selected to sell are different companies, sell one company stock at a time");
			}
		} else {
			sellActionButton.setEnabled (tStocksToSell);
			sellActionButton.setToolTipText ("No Stocks have been selected to Sell");
		}
		if (tStocksToBuy) {
			buyBidActionButton.setEnabled (tStocksToBuy);
			buyBidActionButton.setToolTipText ("Stock or Private have been selected to be Bought.");
			buyBidActionButton.setText ("BUY");
			disableAllStartPacketButtons ("Another Stock have been selected to be Bought.");
			enableSelectedButton ("Stock have been selected to be Bought.");
		}
		if (tPrivateToBidOn && (tStocksToBuy == false)) {
			buyBidActionButton.setEnabled (tPrivateToBidOn);
			buyBidActionButton.setToolTipText ("Private has been selected to Bid On");
			buyBidActionButton.setText ("BID");
			disableAllStartPacketButtons ("Another Private has been selected to Bid On");
			enableSelectedButton ("Private has been selected to Bid On");
		}
		if ((tStocksToBuy == false) && (tPrivateToBidOn == false)) {
			buyBidActionButton.setEnabled (tStocksToBuy);
			buyBidActionButton.setToolTipText ("No Stock or Private have been selected to be Bought or Private to Bid upon.");
			buyBidActionButton.setText ("Buy-Bid");
			enableAllStartPacketButtons ("No Stock or Private has been selected");
		}
		exchangeActionButton.setEnabled (tPrezToExchange || tPrivateOrMinorToExchange);
		if (tPrezToExchange) {
			exchangeActionButton.setToolTipText ("There is one President's Share Selected to Exchange");
		} else if (tPrivateOrMinorToExchange) {
			exchangeActionButton.setToolTipText ("There is one Private or Minor Share Selected to Exchange");
		} else {
			exchangeActionButton.setToolTipText ("There are no selected President's Share to Exchange");
		}
		undoActionButton.setEnabled (tActionsToUndo);
		if (tActionsToUndo) {
			undoActionButton.setToolTipText ("There are Actions that can be undone");
		} else {
			undoActionButton.setToolTipText ("No Actions to Undo");
		}
		
		if (hasActed ()) {
			setDoneButton ();
			disableAllStartPacketButtons ("Already acted");
		} else {
			setPassButton ();
		}
	}
	
	public void updateCertificateInfo () {
		setCertificateCountLabel ();
		updatePortfolioInfo ();
	}
	
	public void enableAllStartPacketButtons (String aToolTip) {
		StartPacketFrame tStartPacketFrame;
		Bank tBank;
		
		if (player.isCurrentPlayer ()) {
			tBank = player.getBank ();
			if (! tBank.isStartPacketPortfolioEmpty ()) {
				tStartPacketFrame = tBank.getStartPacketFrame ();
				tStartPacketFrame.enableAllCheckedButtons (aToolTip);
			}
		}
	}
	
	public void disableAllStartPacketButtons (String aToolTip) {
		StartPacketFrame tStartPacketFrame;
		Bank tBank;
		
		if (player.isCurrentPlayer ()) {
			tBank = player.getBank ();
			if (! tBank.isStartPacketPortfolioEmpty ()) {
				tStartPacketFrame = tBank.getStartPacketFrame ();
				tStartPacketFrame.disbleAllCheckedButtons (aToolTip);
			}
		}
	}
	
	public void enableSelectedButton (String aToolTip) {
		StartPacketFrame tStartPacketFrame;
		Bank tBank;
		
		if (player.isCurrentPlayer ()) {
			tBank = player.getBank ();
			if (! tBank.isStartPacketPortfolioEmpty ()) {
				tStartPacketFrame = tBank.getStartPacketFrame ();
				tStartPacketFrame.enableSelectedButton (aToolTip);
			}
		}
	}

	public void updatePortfolioInfo () {
		JPanel tPortfolioInfoJPanel;
		
		tPortfolioInfoJPanel = player.buildPortfolioJPanel (this);
		replacePortfolioInfo (tPortfolioInfoJPanel);
		setTotalValueLabel ();
	}
}
