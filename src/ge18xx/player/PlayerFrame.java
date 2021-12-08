package ge18xx.player;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.StartPacketFrame;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerFrame extends XMLFrame implements ItemListener {
	public static final String STOCK_SELECTED_FOR_BID2 = "Another Private has been selected to be Bid On";
	public static final String STOCK_SELECTED_FOR_BUY = "A Stock has been selected to be Bought";
	public static final String STOCK_SELECTED_FOR_BUY2 = "Another Stock has been selected to be Bought";
	public static final String STOCK_SELECTED_FOR_SALE = "At least one Stock has been selected to be Sold";
	public static final String NO_STOCK_SELECTED_FOR_SALE = "No Stocks have been selected to Sell";
	public static final String NO_STOCK_SELECTED_FOR_SALE2 = "No Stock or Private have been selected to be Bought or Private to Bid upon.";
	public static final String STOCK_SELECTED_FOR_EXCHANGE = "A President Share has been selected to be Exchanged";
	public static final String STOCK_SELECTED_FOR_BID = "A Private has been selected to be Bid On";
	public static final String PRIVATE_SELECTED_FOR_EXCHANGE = "A Private/Minor has been selected to be Exchanged";
	public static final String STOCK_PAR_PRICE_NEEDS_SETTING = "A Share Company needs to have Par Price selected - Find the Par Price Frame";
	public static final String MUST_BUY_PRIVATE = "Must buy the Private where COST == DISCOUNT";
	public static final String ALREADY_VISIBLE = "PlayerFrame already Visible";
	public static final String EXCHANGE_PRIVATE = "Exchange Private Certificate for Share Certificate";
	static final String DONE = "Done";
	static final String UNDO = "Undo";
	static final String PASS = "Pass";
	static final String BUY = "Buy";
	static final String BID = "Bid";
	static final String BUY_BID = "Buy-Bid";
	static final String SELL = "Sell";
	static final String EXCHANGE = "Exchange";
	private static final long serialVersionUID = 1L;
	JPanel playerAndBankJPanel;
	JPanel bankJPanel;
	JPanel playerJPanel;
	JPanel actionButtonJPanel;
	JPanel playerInfoJPanel;
	JPanel portfolioInfoJPanel;
	JLabel playerCash;
	JLabel playerCertificateCount;
	JLabel playerPassed;
	JLabel playerAuctionPassed;
	JLabel playerBidAndEscrow;
	JLabel playerBidAmount;
	JLabel playerPortfolioLabel;
	JLabel playerTotalValue;
	JButton passActionButton;
	JButton buyBidActionButton;
	JButton sellActionButton;
	JButton exchangeActionButton;
	JButton undoActionButton;
	Player player;
	int portfolioInfoIndex;
	boolean locationFixed;
	
	public PlayerFrame (String aFrameName, Player aPlayer, String aGameName) {
		super (aFrameName, aGameName);
		
		if (aPlayer != Player.NO_PLAYER) {
			player = aPlayer;
			
			buildPlayerAndBankJPanel ();
			add (playerAndBankJPanel);
			setLocationFixed (false);
			setSize (850, 900);
		}
	}

	public void buildPlayerAndBankJPanel () {
		playerAndBankJPanel = new JPanel ();
		playerAndBankJPanel.setLayout (new BoxLayout (playerAndBankJPanel, BoxLayout.X_AXIS));
		
		bankJPanel = new JPanel ();
		bankJPanel.setLayout (new BoxLayout (bankJPanel, BoxLayout.Y_AXIS));
		playerAndBankJPanel.add (Box.createHorizontalStrut (20));
		buildPlayerJPanel ();
		playerAndBankJPanel.add (playerJPanel);
		playerAndBankJPanel.add (Box.createHorizontalGlue ());
		playerAndBankJPanel.add (bankJPanel);
		playerAndBankJPanel.add(Box.createHorizontalStrut (20));
	}

	public void buildPlayerJPanel () {
		playerJPanel = new JPanel ();
		playerJPanel.setLayout (new BoxLayout (playerJPanel, BoxLayout.Y_AXIS));
		playerJPanel.add (Box.createVerticalStrut (10));
		
		buildPlayerInfoPanel ();
		
		playerJPanel.add (playerInfoJPanel);
		playerJPanel.add (Box.createVerticalStrut (10));
		
		buildActionButtonJPanel ();
		playerJPanel.add (actionButtonJPanel);
		
		portfolioInfoIndex = 5;
		updatePortfolioInfo ();
	}

	private void buildPlayerInfoPanel () {
		playerInfoJPanel = new JPanel ();
		playerInfoJPanel.setBorder (BorderFactory.createTitledBorder ("Information For " + player.getName ()));
		playerInfoJPanel.setLayout (new BoxLayout (playerInfoJPanel, BoxLayout.X_AXIS));
		playerInfoJPanel.setAlignmentX (CENTER_ALIGNMENT);
		
		addPlayerInfoJPanelLabel (null);
		playerCash = new JLabel ("");
		addPlayerInfoJPanelLabel (playerCash);
		setCashLabel ();
		
		playerCertificateCount = new JLabel ("");
		addPlayerInfoJPanelLabel (playerCertificateCount);
		setCertificateCountLabel ();
		
		playerBidAndEscrow = new JLabel ("");
		updateBidAndEscrow ();
		playerInfoJPanel.add (playerBidAndEscrow);

		playerPortfolioLabel = new JLabel ("");
		addPlayerInfoJPanelLabel (playerPortfolioLabel);
		
		playerTotalValue = new JLabel ("");
		addPlayerInfoJPanelLabel (playerTotalValue);
	}
	
	private void updateBidAndEscrow () {
		int tBidCount, tEscrowTotal;
		String tBidCountText;
		
		tBidCount = player.getEscrowCount ();
		if (tBidCount > 0) {
			if (tBidCount == 1) {
				tBidCountText = "1 Bid";
			} else {
				tBidCountText = "Bids: " + tBidCount;
			}
			tEscrowTotal = player.getTotalEscrow ();
			playerBidAndEscrow.setText (tBidCountText + " Total Escrow: " + Bank.formatCash(tEscrowTotal) + " ");
		} else {
			playerBidAndEscrow.setText ("");
		}
	}
	
	public boolean isLocationFixed () {
		return locationFixed;
	}
	
	public void setLocationFixed (boolean aLocationFixed) {
		locationFixed = aLocationFixed;
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
		tActionButton.addActionListener (player);
	
		return tActionButton;
	}

	private void buildActionButtonJPanel () {
		actionButtonJPanel = new JPanel ();

		passActionButton = setupActionButton (PASS, PASS);
		buyBidActionButton = setupActionButton (BUY_BID, BUY_BID);
		sellActionButton = setupActionButton (SELL, SELL);
		exchangeActionButton = setupActionButton (EXCHANGE, EXCHANGE);
		undoActionButton = setupActionButton (UNDO, UNDO);
				
		actionButtonJPanel.add (passActionButton);
		actionButtonJPanel.add (buyBidActionButton);
		actionButtonJPanel.add (sellActionButton);
		actionButtonJPanel.add (exchangeActionButton);
		actionButtonJPanel.add (undoActionButton);
	}
	
	public void updateBankJPanel (GameManager aGameManager) {
		Bank tBank;
		BankPool tBankPool;
		JPanel tBPPortfolioJPanel;
		JPanel tBankPortfolioJPanel;
		JPanel tStartPacketPortfolioJPanel;
		
		tBank = player.getBank ();
		tBankPool = player.getBankPool ();
		
		bankJPanel.removeAll ();
		if (tBank.isStartPacketPortfolioEmpty ()) {
			tBPPortfolioJPanel = tBankPool.buildPortfolioInfoJPanel (this, player, aGameManager);
			
			bankJPanel.add (Box.createVerticalGlue ());
			bankJPanel.add (tBPPortfolioJPanel);
			bankJPanel.add (Box.createVerticalGlue ());
			
			tBankPortfolioJPanel = tBank.buildPortfolioInfoJPanel (this, player, aGameManager);
			bankJPanel.add (tBankPortfolioJPanel);
			bankJPanel.add (Box.createVerticalGlue ());
		} else {
			tStartPacketPortfolioJPanel = tBank.buildStartPacketInfoJPanel (this, player, aGameManager);
			bankJPanel.add (Box.createVerticalGlue ());
			bankJPanel.add (tStartPacketPortfolioJPanel);
			bankJPanel.add (Box.createVerticalGlue ());
		}
		bankJPanel.repaint ();
		bankJPanel.revalidate ();
	}
	
	public boolean isBuyAction () {
		boolean tIsBuyAction;
		
		tIsBuyAction = buyBidActionButton.getText().equals (BUY);
		
		return tIsBuyAction;
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
		if (tCompanyAbbrev != Corporation.NO_ABBREV) {
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
					tReason = "Portfolio has too many shares of " + tCompanyAbbrev;
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
	
	public boolean hasSelectedOneToExchange () {
		return player.hasSelectedOneToExchange ();
	}
	
	public boolean hasSelectedPrezToExchange () {
		return player.hasSelectedPrezToExchange ();
	}
	
	public boolean hasSelectedStocksToBuy () {
		boolean tSelectedStocksToBuy = false;
		Bank tBank;
		
		tBank = player.getBank ();
		if (tBank != Bank.NO_BANK) { 
			tSelectedStocksToBuy = player.hasSelectedStockToBuy (tBank);
		} else {
			System.err.println ("Player has failed to retrieve the Bank");
		}
		// Need to examine if any shares have been selected to Buy from Bank Pool or Bank
		
		return tSelectedStocksToBuy;
	}
	
	public int getCountSelectedCosToBuy () {
		return player.getCountSelectedCosToBuy ();
	}
	
	public int getCountSelectedCosToBid () {
		return player.getCountSelectedCosToBid ();
	}

	public int getCostSelectedStocksToBuy () {
		int tSelectedStocksToBuyCost;
		
		tSelectedStocksToBuyCost = player.getCostSelectedStockToBuy ();
		
		return tSelectedStocksToBuyCost;
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		// May not need to do anything except update Action Buttons.
		Portfolio tPortfolio;
		
		tPortfolio = player.getPortfolio ();
		tPortfolio.itemStateChanged (aItemEvent);
		updateActionButtons ();
	}

	public void replacePortfolioInfo (JPanel aPortfolioJPanel) {
		if (portfolioInfoJPanel == GUI.NO_PANEL) {
			setPortfolioInfoJPanel (aPortfolioJPanel);
		} else {
			playerJPanel.remove (portfolioInfoIndex);
			playerJPanel.remove (portfolioInfoIndex - 1);
		}
		playerJPanel.add (aPortfolioJPanel);
		playerJPanel.add (Box.createVerticalStrut (10));
		playerJPanel.validate ();
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
	
	public String getMustSellToolTip (Player aPlayer) {
		String tStock;
		String tToolTip;
		int tPercentage;
		
		// TODO: Test for Over Total Certificate Limit 
		// TODO: Test for Over specific Company Certificate Limit
		
		// Reason 1 from Exchange of President Share
		// Reason 2 from Company Share Price leaving Market Region that allowed excess certificates
		
		tStock = aPlayer.hasExchangedShare ();
		tPercentage = aPlayer.getMustSellPercent (tStock);
		tToolTip = "Must sell at least " + tPercentage + "% of " + tStock + " Share Company due to Exchange";
		
		return tToolTip;
	}
	
	public boolean mustSellStock () {
		boolean tMustSellStock = false;
		String tStockToSell;
		
		tStockToSell = player.hasExchangedShare ();
		if (tStockToSell != Player.NO_STOCK_TO_SELL) {
			tMustSellStock = true;
		}
		
		return tMustSellStock;
	}
	
	private void setCannotPass () {
		passActionButton.setEnabled (false);
		passActionButton.setToolTipText (MUST_BUY_PRIVATE);
		disableAllStartPacketButtons (MUST_BUY_PRIVATE);
		enableMustBuyPrivateButton ();
	}
	
	public void setPortfolioInfoJPanel (JPanel aPortfolioInfoJPanel) {
		portfolioInfoJPanel = aPortfolioInfoJPanel;
	}
	
	public void setPortfolioValueLabel () {
		int tPortfolioValue;
		
		tPortfolioValue = 0;
		if (player != Player.NO_PLAYER) {
			tPortfolioValue = player.getPortfolioValue ();
		}
		playerPortfolioLabel.setText ("Portfolio Value: " + Bank.formatCash (tPortfolioValue));
		setTotalValueLabel ();
	}
	
	public void setTotalValueLabel () {
		int tPortfolioValue;
		int tCashValue;
		int tTotalValue;
		int tEscrowValue;
		
		if (playerTotalValue != null) {
			tPortfolioValue = 0;
			tCashValue = 0;
			tEscrowValue = 0;
			if (player != Player.NO_PLAYER) {
				tPortfolioValue = player.getPortfolioValue ();
				tCashValue = player.getCash ();
				tEscrowValue = player.getTotalEscrow ();
			}
			tTotalValue = tPortfolioValue + tCashValue + tEscrowValue;
			playerTotalValue.setText ("Total Value: " + Bank.formatCash (tTotalValue));
		}
	}

	public void updatePlayerInfo (GameManager aGameManager) {
		setCashLabel ();
		updateCertificateInfo ();
		updatePortfolioInfo ();
		setPortfolioValueLabel();
		updateBankJPanel (aGameManager);
		updateActionButtons ();
	}
	
	public void updateActionButtons () {
		boolean tStocksToSell, tStocksToBuy, tActionsToUndo, tStocksToSellSame;
		boolean tPrezToExchange, tCanCompleteTurn, tPrivateOrMinorToExchange;
		boolean tStocksToSellOverfill, tMustBuy, tPrivateToBidOn;
		boolean tHasSelectedOneToExchange;
		
		tMustBuy = hasMustBuyCertificate ();
		tStocksToSell = hasSelectedStocksToSell ();
		tStocksToSellSame = hasSelectedSameStocksToSell ();
		tStocksToSellOverfill = willSaleOverfillBankPool ();
		tStocksToBuy = hasSelectedStocksToBuy ();
		tActionsToUndo = hasActionsToUndo ();
		tPrivateToBidOn = hasSelectedPrivateToBidOn ();
		tPrezToExchange = hasSelectedPrezToExchange ();
		tHasSelectedOneToExchange = hasSelectedOneToExchange ();
		tPrivateOrMinorToExchange = hasSelectedPrivateOrMinorToExchange ();
		tCanCompleteTurn = canCompleteTurn ();
		
		updatePassButton (tCanCompleteTurn, tMustBuy);
		updateSellButton (tStocksToSell, tStocksToSellSame, tStocksToSellOverfill, tPrezToExchange);
		updateBuyBidButton (tStocksToBuy, tPrivateToBidOn);
		updateExchangeButton (tPrezToExchange, tPrivateOrMinorToExchange, tHasSelectedOneToExchange);
		updateUndoButton (tActionsToUndo);
		
		if (hasActed ()) {
			setDoneButton ();
			disableAllStartPacketButtons ("Already acted");
		} else {
			setPassButton ();
		}
		player.addPrivateBenefitButtons (actionButtonJPanel);
	}
	
	public void setPassDoneButton (String tLabel, String tAction) {
		String tToolTip;
		
		passActionButton.setText (tLabel);
		passActionButton.setActionCommand (tAction);
		if (hasSelectedStocksToBuy ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText (STOCK_SELECTED_FOR_BUY);
		} else if (hasSelectedStocksToSell ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText (STOCK_SELECTED_FOR_SALE);
		} else if (hasSelectedPrezToExchange ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText (STOCK_SELECTED_FOR_EXCHANGE);
		} else if (hasSelectedPrivateToBidOn ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText (STOCK_SELECTED_FOR_BID);
		} else if (hasSelectedPrivateOrMinorToExchange ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText (PRIVATE_SELECTED_FOR_EXCHANGE);
		} else if (player.isParPriceFrameActive () ) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText (STOCK_PAR_PRICE_NEEDS_SETTING);
		} else if (player.isAuctionRound ()) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText ("Auction Round must complete first");	
		} else if (! player.isLastActionComplete () ) {
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText ("Last Action must be completed first");	
		} else if (mustSellStock ()) {
			tToolTip = getMustSellToolTip (player);
			passActionButton.setEnabled (false);
			passActionButton.setToolTipText (tToolTip);		
		} else if (hasMustBuyCertificate ()) {
			setCannotPass ();
		} else {
			passActionButton.setEnabled (true);
			passActionButton.setToolTipText ("");
		}
	}

	private void updatePassButton (boolean aCanCompleteTurn, boolean aMustBuy) {
		String tToolTip;
		
		// If there is a Must Buy -- Cannot Do a Pass, or a Bid -- disable these
		if (aMustBuy) {
			setCannotPass ();
		} else {
			if (aCanCompleteTurn) {
				passActionButton.setEnabled (aCanCompleteTurn);
				passActionButton.setToolTipText ("Can complete player turn");
			} else {
				tToolTip = getReasonForNotCompleting ();
				if (">>NONE<<".equals (tToolTip)) {
					passActionButton.setEnabled (true);
					passActionButton.setToolTipText ("");
				} else {
					passActionButton.setEnabled (aCanCompleteTurn);
					passActionButton.setToolTipText (tToolTip);
				}
			}
		}
	}

	private void updateSellButton (boolean aStocksToSell, boolean aStocksToSellSame, boolean aStocksToSellOverfill,
					boolean aPrezToExchange) {
		if (aStocksToSell) {
			if (aPrezToExchange) {
				sellActionButton.setEnabled (false);
				sellActionButton.setToolTipText ("Must Exchange President Share before selecting stock to sell");				
			} else if (aStocksToSellOverfill) {
				sellActionButton.setEnabled (false);
				sellActionButton.setToolTipText ("Stocks selected to be Sold will Overfill BankPool");
			} else if (aStocksToSellSame) {
				sellActionButton.setEnabled (aStocksToSell);
				sellActionButton.setToolTipText (STOCK_SELECTED_FOR_SALE);
			} else {
				sellActionButton.setEnabled (aStocksToSellSame);
				sellActionButton.setToolTipText ("Stocks selected to sell are different companies, sell one company stock at a time");
			}
		} else {
			sellActionButton.setEnabled (aStocksToSell);
			sellActionButton.setToolTipText (NO_STOCK_SELECTED_FOR_SALE);
		}
	}

	private void updateExchangeButton (boolean aPrezToExchange, boolean aPrivateOrMinorToExchange,
										boolean tHasSelectedOneToExchange) {
		boolean tCanBankHoldStock = false;
		
		if (tHasSelectedOneToExchange) {
			if (aPrezToExchange) {
				tCanBankHoldStock = canBankHoldStock ();
			}
			
			exchangeActionButton.setEnabled (tCanBankHoldStock || aPrivateOrMinorToExchange);
			if (! tCanBankHoldStock) {
				exchangeActionButton.setToolTipText ("The Bank Pool cannot hold minimum % of stock required to lose Presidency");			
			} else if (aPrezToExchange) {
				exchangeActionButton.setToolTipText ("There is one President's Share Selected to Exchange");
			} else if (aPrivateOrMinorToExchange) {
				exchangeActionButton.setToolTipText ("There is one Private or Minor Share Selected to Exchange");
			} else {
				exchangeActionButton.setToolTipText ("There are no selected President's Share to Exchange");
			}
		} else {
			exchangeActionButton.setEnabled (false);
			exchangeActionButton.setToolTipText ("Select only a single President Share to Exchange at a time");
		}
	}

	private boolean canBankHoldStock () {
		boolean tCanBankHoldStock = true;
		String tCompanyAbbrev;
		Certificate tCertificate;
		Corporation tCorporation;
		Player tNextPossiblePrez;
		int tCurrentPlayerPercent;
		int tNextPrezPercent;
		int tMustSellSharePercentage;
		int tSmallestSharePercentage;
		
		tCertificate = player.getCertificateToExchange ();
		tCorporation = tCertificate.getCorporation ();
		tCompanyAbbrev = tCertificate.getCompanyAbbrev ();
		tNextPossiblePrez = player.getNextPossiblePrez (tCompanyAbbrev);
		tCurrentPlayerPercent = player.getPercentOwnedOf (tCorporation);
		tNextPrezPercent = tNextPossiblePrez.getPercentOwnedOf (tCorporation);
		tSmallestSharePercentage = tCorporation.getSmallestSharePercentage ();
		tMustSellSharePercentage = tCurrentPlayerPercent - tNextPrezPercent + tSmallestSharePercentage; 
		tCanBankHoldStock = ! player.willOverfillBankPool(tMustSellSharePercentage, tCorporation);
		 
		return tCanBankHoldStock;
	}
	
	private void updateBuyBidButton (boolean aStocksToBuy, boolean aPrivateToBidOn) {
		int tCountSelectedCosToBuy;
		int tCountSelectedCosToBid;
		
		tCountSelectedCosToBuy = getCountSelectedCosToBuy ();
		tCountSelectedCosToBid = getCountSelectedCosToBid ();
		if (aStocksToBuy) {
			if ((tCountSelectedCosToBuy + tCountSelectedCosToBid) > 1) {
				buyBidActionButton.setEnabled (false);
				buyBidActionButton.setToolTipText ("Select only one Company's Stock to buy and/or bid on at a time");
				buyBidActionButton.setText (BUY);
				enableSelectedButton (STOCK_SELECTED_FOR_BUY);
			} else {
				buyBidActionButton.setEnabled (aStocksToBuy);
				buyBidActionButton.setToolTipText (STOCK_SELECTED_FOR_BUY);
				buyBidActionButton.setText (BUY);
				enableSelectedButton (STOCK_SELECTED_FOR_BUY);
			}
		} else {
			if (tCountSelectedCosToBid > 1) {
				buyBidActionButton.setEnabled (false);
				buyBidActionButton.setToolTipText ("Select only one Company's Stock to bid on at a time");
				buyBidActionButton.setText (BID);
				enableSelectedButton (STOCK_SELECTED_FOR_BID);
			} else if (aPrivateToBidOn) {
				buyBidActionButton.setEnabled (aPrivateToBidOn);
				buyBidActionButton.setToolTipText (STOCK_SELECTED_FOR_BID);
				buyBidActionButton.setText (BID);
				enableSelectedButton (STOCK_SELECTED_FOR_BID);
			} else {
				buyBidActionButton.setEnabled (aStocksToBuy);
				buyBidActionButton.setToolTipText (NO_STOCK_SELECTED_FOR_SALE2);
				buyBidActionButton.setText (BUY_BID);
				enableAllStartPacketButtons ("");
			}
		}
	}

	private void updateUndoButton (boolean tActionsToUndo) {
		GameManager tGameManager;
		
		tGameManager = player.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			undoActionButton.setEnabled (false);
			undoActionButton.setToolTipText ("Network Game - Undos are not allowed");
		} else {
			undoActionButton.setEnabled (tActionsToUndo);
			if (tActionsToUndo) {
				undoActionButton.setToolTipText ("There are Actions that can be undone");
			} else {
				undoActionButton.setToolTipText ("No Actions to Undo");
			}
		}
	}
	
	public void updateCertificateInfo () {
		setCertificateCountLabel ();
		updatePortfolioInfo ();
		updateBidAndEscrow ();
	}
	
	public void enableAllStartPacketButtons (String aToolTip) {
		StartPacketFrame tStartPacketFrame;
		Bank tBank;
		
		if (player.isCurrentPlayer ()) {
			tBank = player.getBank ();
			if (! tBank.isStartPacketPortfolioEmpty ()) {
				tStartPacketFrame = tBank.getStartPacketFrame ();
				tStartPacketFrame.enableAllCheckedButtons (aToolTip, player);
			}
		}
	}
	
	public void enableMustBuyPrivateButton () {
		StartPacketFrame tStartPacketFrame;
		Bank tBank;
		
		if (player.isCurrentPlayer ()) {
			tBank = player.getBank ();
			if (! tBank.isStartPacketPortfolioEmpty ()) {
				tStartPacketFrame = tBank.getStartPacketFrame ();
				tStartPacketFrame.enableMustBuyPrivateButton ();
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
