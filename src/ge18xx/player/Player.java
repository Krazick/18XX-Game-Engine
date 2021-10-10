package ge18xx.player;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.Logger;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.ShareCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.FakeBenefit;
import ge18xx.game.GameManager;
import ge18xx.game.Game_18XX;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.WinAuctionAction;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

//
//  Player.java
//  Game_18XX
//
//  Created by Mark Smith on 11/26/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class Player implements EscrowHolderI, PortfolioHolderLoaderI {
	public static final Player NO_PLAYER = null;
	public static final String NO_PLAYER_NAME = ">NO PLAYER<";
	public static final ElementName EN_PLAYER = new ElementName ("Player");
	public static final ElementName EN_PLAYERS = new ElementName ("Players");
	public static final ElementName EN_PLAYER_STATES = new ElementName ("PlayerStates");
	public static final AttributeName AN_CASH = new AttributeName ("cash");
	final static AttributeName AN_PLAYER_INDEX = new AttributeName ("playerIndex");
	final static AttributeName AN_PRIMARY_STATE = new AttributeName ("primaryState");
	final static AttributeName AN_AUCTION_STATE = new AttributeName ("auctionState");
	final static AttributeName AN_EXCHANGED_PREZ_SHARE = new AttributeName ("exchangePrezShare");
	final static AttributeName AN_SOLD_COMPANIES = new AttributeName ("soldCompanies");
	final static AttributeName AN_BOUGHT_SHARE = new AttributeName ("boughtShare");
	final static AttributeName AN_BID_SHARE = new AttributeName ("bidShare");
	final static AttributeName AN_TRIGGERED_AUCTION = new AttributeName ("triggeredAuction");
	public static final String SELL_LABEL = "Sell";
	public static final String BUY_LABEL = "Buy";
	public static final String BUY_AT_PAR_LABEL = "Buy at Par";
	public static final String BID_LABEL = "Bid";
	public static final String EXCHANGE_LABEL = "Exchange";
	public static final int OWN_ZERO_PERCENT = 0;
	public static final String NO_STOCK_TO_SELL = null;
	private final String DELIMITER = ",";
	static final AttributeName AN_NAME = new AttributeName ("name");
	/* These attributes are set once, and never change, but are needed for game use */
	PlayerManager playerManager;
	String name;
	PlayerFrame playerFrame;
	boolean gameHasPrivates;
	boolean gameHasCoals;
	boolean gameHasMinors;
	boolean gameHasShares;
	boolean boughtShare;
	boolean bidShare;
	boolean triggeredAuction;
	int certificateLimit;
	JLabel rfPlayerLabel;
	JLabel cashLabel;
	JPanel playerJPanel = null;
	Logger logger;
	Benefit benefitInUse;
	
	/* These attributes below change during the game, need to save/load them */
	int treasury;
	Escrows escrows;
	ActionStates primaryActionState;
	ActionStates auctionActionState;
	Portfolio portfolio;
	String exchangedPrezShare;
	SoldCompanies soldCompanies;
	
	public Player (String aName, boolean aPrivates, boolean aCoals, boolean aMinors, 
					boolean aShares, PlayerManager aPlayerManager, int aCertificateLimit) {
		String tFullTitle;
		GameManager tGameManager;
		Benefit tBenefitInUse;
		
		/* Save the Player Name -- ONCE */
		name = aName;
		
		/* Set Variables that change during the game, that must be saved/loaded */
		treasury = 0;
		portfolio = new Portfolio (this);
		clearAuctionActionState ();
		clearPrimaryActionState ();
		setBoughtShare (false);
		setBidShare (false);
		setTriggeredAuction (false);
		setExchangedPrezShare (NO_STOCK_TO_SELL);
		
		/* Set Non-Changing Values */
		setGameHasCompanies (aPrivates, aCoals, aMinors, aShares);
		setRFPlayerLabel (aName);
		setCertificateLimit (aCertificateLimit);
		
		playerManager = aPlayerManager;
		tGameManager = playerManager.getGameManager ();
		tFullTitle = tGameManager.createFrameTitle ("Player");
		playerFrame = new PlayerFrame (tFullTitle, this, tGameManager.getActiveGameName ());
		tGameManager.addNewFrame (playerFrame);
		playerFrame.setFrameToConfigDetails (tGameManager, XMLFrame.getVisibileOFF ());

		soldCompanies = new SoldCompanies ();
		escrows = new Escrows (this);
		logger = Game_18XX.getLogger();
		tBenefitInUse = new FakeBenefit ();
		setBenefitInUse (tBenefitInUse);
	}

	public void setBenefitInUse (Benefit aBenefitInUse) {
		benefitInUse = aBenefitInUse;
	}
	
	public Benefit getBenefitInUse () {
		return benefitInUse;
	}
	
	protected void addPrivateBenefitButtons (JPanel aButtonRow) {
		portfolio.configurePrivatePlayerBenefitButtons (aButtonRow);
	}

	public void completeBenefitUse () {
		
	}

	public void setTriggeredAuction (boolean aTriggeredAuction) {
		triggeredAuction = aTriggeredAuction;
	}
	
	public boolean getTriggeredAuction () {
		return triggeredAuction;
	}
	
	public void setBoughtShare (boolean aBoughtShare) {
		boughtShare = aBoughtShare;
		if (boughtShare) {
			primaryActionState = ActionStates.Bought;
		}
	}
	
	public void setBidShare (boolean aBidShare) {
		bidShare = aBidShare;
		if (bidShare) {
			setAuctionActionState (ActorI.ActionStates.AuctionRaise);
			primaryActionState = ActionStates.Bid;
		}
	}
	
	public void setExchangedPrezShare (String aExchangedShare) {
		exchangedPrezShare = aExchangedShare;
	}
	
	public boolean sells () {
		boolean tCanDoAction;
		
		tCanDoAction = false;
		if (primaryActionState == ActionStates.Pass){
			System.err.println ("Player has passed, can't Act");
		} else {
			tCanDoAction = true;
			primaryActionState = ActionStates.Sold;
		}
		
		return tCanDoAction;
	}
	
	public boolean acts () {
		boolean tCanDoAction;
		
		tCanDoAction = false;
		if (primaryActionState == ActionStates.Pass){
			System.err.println ("Player has passed, can't Act");
		} else {
			tCanDoAction = true;
			primaryActionState = ActionStates.Acted;
			playerFrame.setDoneButton ();
		}
		
		return tCanDoAction;
	}
	
	public void addCash (int aAmount) {
		treasury += aAmount;
		playerFrame.setCashLabel ();
	}
	
	public void addCertificate (Certificate aCertificate) {
		portfolio.addCertificate (aCertificate);
		playerFrame.updateCertificateInfo ();
	}
	
	public void addSoldCompanies (String aCompanyAbbrev) {
		soldCompanies.addSoldCompanies (aCompanyAbbrev);
	}

	public JPanel buildPortfolioJPanel (ItemListener aItemListener) {
		GameManager tGameManager;
		
		tGameManager = playerManager.getGameManager ();
		return portfolio.buildPortfolioJPanel (gameHasPrivates, gameHasCoals, gameHasMinors, 
				gameHasShares, SELL_LABEL, aItemListener, tGameManager);
	}

	public boolean atCertLimit () {
		boolean tAtCertLimit = false;
		int tCertificateCount, tCertificateLimit;
		
		tCertificateCount = getCertificateCount ();
		tCertificateLimit = getCertificateLimit ();
		if (tCertificateCount >= tCertificateLimit) {
			tAtCertLimit = true;
		}
		
		return tAtCertLimit;
	}
	
	public boolean canCompleteTurn () {
		boolean tCanCompleteTurn;
		String tShareCompanyThatExceeds;
		
		tCanCompleteTurn = true;
		if (exchangedPrezShare != NO_STOCK_TO_SELL) {
			tCanCompleteTurn = false;
		} else {
			tShareCompanyThatExceeds = exceedsAnyCorpShareLimit ();
			if (tShareCompanyThatExceeds != null) {
				tCanCompleteTurn = false;
			} else if (exceedsCertificateLimitBy () > 0) {
				tCanCompleteTurn = false;
			}
		}
		
		return tCanCompleteTurn;
	}

	public void clearAllSelections () {
		portfolio.clearSelections ();
	}
	
	public void clearAllSoldCompanies () {
		soldCompanies.clearAllSoldCompanies ();
	}
	
	public int exceedsCertificateLimitBy () {
		int tExceedsCertificateLimit;
		
		tExceedsCertificateLimit = getCertificateCount () - getCertificateLimit ();
		
		return tExceedsCertificateLimit;
	}
	
	public String exceedsAnyCorpShareLimit () {
		String tExceedsThisCorpLimit;
		String tShareCompanyAbbrev;
		int tCorporationCount;
		int tCorporationIndex;
		Corporation tCorporation;
		GameManager tGameManager;
		CorporationList tShareCompanies;
		
		tExceedsThisCorpLimit = null;
		tGameManager = playerManager.getGameManager ();
		tShareCompanies = tGameManager.getShareCompanies ();
		tCorporationCount = tShareCompanies.getCorporationCount ();
		for (tCorporationIndex = 0; (tCorporationIndex < tCorporationCount) && (tExceedsThisCorpLimit == null); tCorporationIndex++) {
			tCorporation = tShareCompanies.getCorporation (tCorporationIndex);
			tShareCompanyAbbrev = tCorporation.getAbbrev ();
			if (exceedsShareLimit (tShareCompanyAbbrev)) {
				tExceedsThisCorpLimit = tShareCompanyAbbrev;
			}
		}
		
		return tExceedsThisCorpLimit;
	}
	
	public boolean finishAuction (Certificate aCertificateToBuy, boolean aCreateNewAuctionAction) {
		boolean tNextShareHasBids;
		WinAuctionAction tWinAuctionAction;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		
		tRoundType = ActorI.ActionStates.AuctionRound;
		tRoundID = "1";

		tNextShareHasBids = playerManager.nextShareHasBids (aCertificateToBuy);

		tWinAuctionAction = new WinAuctionAction (tRoundType, tRoundID, this);
		tWinAuctionAction = (WinAuctionAction) playerManager.buyAction (this, aCertificateToBuy, 
				PlayerManager.STOCK_BUY_IN.AuctionRound, tWinAuctionAction);
		aCertificateToBuy.refundBids (tWinAuctionAction);
		tWinAuctionAction.addRemoveAllBidsEffect (this, aCertificateToBuy);
		tWinAuctionAction.addFinishAuctionEffect (this);
		
		playerManager.addAction (tWinAuctionAction);		
		playerManager.finishAuction (tNextShareHasBids, aCreateNewAuctionAction);
		
		return tNextShareHasBids;
	}
	
	public void clearAuctionActionState () {
		setAuctionActionState (ActionStates.NoAction);
	}

	public void clearPrimaryActionState () {
		primaryActionState = ActionStates.NoAction;
		setBoughtShare (false);
		setBidShare (false);
		if (playerManager != null) {
			playerManager.updateRFPlayerLabel (this);
		}
		if (playerFrame != null) {
			playerFrame.setPassButton ();
			playerFrame.updatePortfolioInfo ();
		}
	}
	
	public boolean exceedsShareLimit (String aCompanyAbbrev) {
		boolean tExceedsShareLimit;
		int tPlayerShareLimit;
		int tPlayerOwnedPercentage;
		
		tPlayerShareLimit = getShareLimit (aCompanyAbbrev);
		tPlayerOwnedPercentage = portfolio.getCertificatePercentageFor (aCompanyAbbrev);
		tExceedsShareLimit = (tPlayerOwnedPercentage > tPlayerShareLimit*10);
		
		return tExceedsShareLimit;
	}

	public boolean gameHasCoals () {
		return gameHasCoals;
	}
	
	public boolean gameHasMinors () {
		return gameHasMinors;
	}
	
	public boolean gameHasPrivates () {
		return gameHasPrivates;
	}
	
	public boolean gameHasShares () {
		return gameHasShares;
	}
	
	public ActionStates getAuctionActionState () {
		return auctionActionState;
	}

	public Bank getBank () {
		return playerManager.getBank ();
	}
	
	public BankPool getBankPool () {
		return playerManager.getBankPool ();
	}
	
	public int getCash () {
		return treasury;
	}
	
	public int getCertificateCount () {
		return portfolio.getCertificateCountAgainstLimit ();
	}
	
	public int getCertificateLimit () {
		return certificateLimit;
	}
	
	public int getCertificateTotalCount () {
		return portfolio.getCertificateTotalCount ();
	}
	
	public List<Certificate> getCertificatesToSell () {
		List<Certificate> tCertificatesToSell;
		
		tCertificatesToSell = portfolio.getCertificatesToSell ();
		
		return tCertificatesToSell;
	}
	
	public Certificate getCertificateToExchange () {
		return portfolio.getCertificateToExchange ();
	}
	
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;
		
		tCurrentHolder = playerManager.getCurrentHolder (aLoadedCertificate);
		
		return tCurrentHolder;
	}
	
	public void printAllEscrows () {
		escrows.printAllEscrows ();
	}
	
	public Escrow getEscrowMatching (String aEscrowName) {
		return escrows.getEscrowMatching (aEscrowName);
	}

	public Escrow getEscrowFor (Certificate aCertificate) {
		return escrows.getEscrowFor (aCertificate);
	}
	
	
	public Escrow getEscrowAt (int aEscrowIndex) {
		return escrows.getEscrowAt (aEscrowIndex);
	}
	
	public int getEscrowCount () {
		return escrows.getEscrowCount ();
	}
	
	public GameManager getGameManager () {
		return playerManager.getGameManager ();
	}
	
	public String getName () {
		return name;
	}
	
	public int getPercentOwnedOf (Corporation aCorporation) {
		int tPercentOwned;
	
		tPercentOwned = portfolio.getCertificatePercentageFor (aCorporation);
		
		return tPercentOwned;
	}
	
	public PlayerFrame getPlayerFrame () {
		return playerFrame;
	}
	
	public XMLElement getPlayerElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		
		tXMLElement = aXMLDocument.createElement (EN_PLAYER);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_PLAYER_INDEX, playerManager.getPlayerIndex (this));
		
		return tXMLElement;
	}
	
	public XMLElement getPlayerStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tXMLPortofolioElements, tXMLEscrows;
		String tCompaniesSold;
		
		tCompaniesSold = soldCompanies.toString (DELIMITER);
		tXMLElement = getPlayerElement (aXMLDocument);
		tXMLElement.setAttribute (AN_CASH, treasury);
		tXMLElement.setAttribute (AN_PRIMARY_STATE, primaryActionState.toString ());
		tXMLElement.setAttribute (AN_AUCTION_STATE, auctionActionState.toString ());
		tXMLElement.setAttribute (AN_EXCHANGED_PREZ_SHARE, exchangedPrezShare);
		tXMLElement.setAttribute (AN_BOUGHT_SHARE, boughtShare);
		tXMLElement.setAttribute (AN_BID_SHARE, bidShare);
		tXMLElement.setAttribute (AN_TRIGGERED_AUCTION, triggeredAuction);
		tXMLElement.setAttribute (AN_SOLD_COMPANIES, tCompaniesSold);
		tXMLPortofolioElements = portfolio.getElements (aXMLDocument);
		tXMLElement.appendChild (tXMLPortofolioElements);
		tXMLEscrows = escrows.getEscrowXML (aXMLDocument);
		tXMLElement.appendChild (tXMLEscrows);
		
		return tXMLElement;
	}
	
	public String getStateName () {
		return primaryActionState.toString ();
	}
	
	public Portfolio getPortfolio () {
		return portfolio;
	}
	
	public Player getPortfolioHolder () {
		return this;
	}
	
	public Certificate getPresidentCertificateFor (Corporation aCorporation) {
		return portfolio.getPresidentCertificate (aCorporation);
	}
	
	public Certificate getNonPresidentCertificateFor (Corporation aCorporation) {
		return portfolio.getNonPresidentCertificate (aCorporation);
	}
	
	public ActionStates getPrimaryActionState () {
		return primaryActionState;
	}
	
	public int getPortfolioValue () {
		return portfolio.getPortfolioValue ();
	}
	
	public JLabel getRFPlayerLabel () {
		return rfPlayerLabel;
	}
	
	public String getRFPlayerLabelText () {
		String tText;
		
		tText = ">>NO LABEL<<";
		if (rfPlayerLabel != null) {
			tText = rfPlayerLabel.getText ();
		}
		
		return tText;
	}

	public int getMustSellPercent (String aCompanyAbbrev) {
		int tMustSellPercent;
		ShareCompany tShareCompany;
		Player tPresidentOf;
		int tCurrentPlayerHasXPercent, tPresidentHasXPercent;
		
		tMustSellPercent = 0;
		tShareCompany = playerManager.getShareCompany (aCompanyAbbrev);
		if (tShareCompany != null) {
			if (tShareCompany.equals (CorporationList.NO_CORPORATION)) {
				System.err.println ("Share Company with abbrev " + aCompanyAbbrev + " could not be found");
			} else {
				tCurrentPlayerHasXPercent = getPercentOwnedOf (tShareCompany);
				tPresidentOf = (Player) tShareCompany.getPresident ();
				tPresidentHasXPercent = tPresidentOf.getPercentOwnedOf (tShareCompany);
				tMustSellPercent = 1 + tCurrentPlayerHasXPercent - tPresidentHasXPercent;
			}
		}
		
		return tMustSellPercent;
	}
	
	public int getShareLimit (String aCompanyAbbrev) {
		int tPlayerShareLimit;
		MarketCell tMarketCell;
		ShareCompany tShareCompany;
		GameManager tGameManager;

		tPlayerShareLimit = playerManager.getPlayerShareLimit ();
		tGameManager = playerManager.getGameManager();
		tShareCompany = tGameManager.getShareCompany (aCompanyAbbrev);
		if (tShareCompany != CorporationList.NO_CORPORATION) {
			tMarketCell = tShareCompany.getSharePriceMarketCell ();
			if (tMarketCell != Market.NO_MARKET_CELL) {
				if (tMarketCell.getExceedPlayerCorpShareLimit ()) {
					tPlayerShareLimit = 10;
				}
			}
		}
		
		return tPlayerShareLimit;
	}
	
	public boolean hasActionsToUndo () {
		boolean tActionsToUndo;
		
		if (playerManager == null) {
			tActionsToUndo = false;
		} else {
			tActionsToUndo = playerManager.hasActionsToUndo ();
		}
		
		return tActionsToUndo;
	}
	
	public boolean hasActed () {
		boolean tHasActed = true;

		// If the Primary Action State for the Player is NoAction or Pass, 
		// the Player has NOT Acted, otherwise the Player Has Acted
		
		if ((primaryActionState == ActionStates.NoAction) ||
			(primaryActionState == ActionStates.Pass)) {
			tHasActed = false;
		}
		
		return tHasActed; 
	}
	
	public boolean hasBoughtShare () {
		return boughtShare;
	}
	
	public boolean hasBid () {
		return bidShare;
	}
	
	public String hasExchangedShare () {
		return exchangedPrezShare;
	}
	
	public boolean hasLessThanPresident (String aCompanyAbbrev) {
		boolean tHasLessThanPresident;
		ShareCompany tShareCompany;
		Player tPresidentOf;
		int tCurrentPlayerHasXPercent, tPresidentHasXPercent;
		
		tHasLessThanPresident = true;
		tShareCompany = playerManager.getShareCompany (aCompanyAbbrev);
		if (tShareCompany.equals (CorporationList.NO_CORPORATION)) {
			System.err.println ("Share Company with abbrev " + aCompanyAbbrev + " could not be found");
		} else {
			tCurrentPlayerHasXPercent = getPercentOwnedOf (tShareCompany);
			tPresidentOf =  (Player) tShareCompany.getPresident ();
			tPresidentHasXPercent = tPresidentOf.getPercentOwnedOf (tShareCompany);
			tHasLessThanPresident = (tCurrentPlayerHasXPercent < tPresidentHasXPercent);
		}
		
		return tHasLessThanPresident;
	}

	public boolean hasMaxShares (String aCompanyAbbrev) {
		boolean tHasMaxShares;
		int tPlayerShareLimit;
		int tPlayerOwnedPercentage;
		
		tHasMaxShares = false;
		tPlayerShareLimit = getShareLimit (aCompanyAbbrev);

		if (tPlayerShareLimit < 10) {
			tPlayerOwnedPercentage = portfolio.getCertificatePercentageFor (aCompanyAbbrev);
			if (tPlayerOwnedPercentage >= tPlayerShareLimit*10) {
				tHasMaxShares = true;
			}
		}
		
		return tHasMaxShares;
	}
	
	public boolean hasMustBuyCertificate () {
		return playerManager.hasMustBuyCertificate ();
	}
	
	public boolean hasPassed () {
		return primaryActionState == ActionStates.Pass;
	}
	
	public boolean hasPassedInAuction () {
		return auctionActionState == ActionStates.AuctionPass;
	}
	
	public boolean hasSelectedPrezToExchange () {
		boolean tHasSelectedPrezToExchange;
		
		tHasSelectedPrezToExchange = portfolio.hasSelectedPrezToExchange ();
		
		return tHasSelectedPrezToExchange;
	}
	
	public boolean hasSelectedPrivateOrMinorToExchange () {
		boolean tHasSelectedPrivateOrMinorToExchange;
		
		tHasSelectedPrivateOrMinorToExchange = portfolio.hasSelectedPrivateOrMinorToExchange ();
		
		return tHasSelectedPrivateOrMinorToExchange;
	}

	public String getSelectedCompanyAbbrev () {
		String tSelectedCompanyAbbrev;
		
		tSelectedCompanyAbbrev = portfolio.getSelectedCompanyAbbrev ();
		
		return tSelectedCompanyAbbrev;
	}
	
	public int getSelectedPercent () {
		int tSelectedPercent;
		
		tSelectedPercent = portfolio.getSelectedPercent ();
		
		return tSelectedPercent;
	}
	
	public boolean hasSelectedSameStocksToSell () {
		boolean tHasSelectedSameStocksToSell;
	
		tHasSelectedSameStocksToSell = portfolio.AreAllSelectedStocksSameCorporation ();
		
		return tHasSelectedSameStocksToSell;
	}
	
	public boolean hasSelectedPrivateToBidOn () {
		boolean tHasSelectedPrivateToBidOn = false;;
		Bank tBank;
		StartPacketPortfolio tStartPacketPortfolio;
		
		tBank = getBank ();
		if (tBank != Bank.NO_BANK) {
			tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
			if (tStartPacketPortfolio != null) {
				tHasSelectedPrivateToBidOn = tStartPacketPortfolio.hasSelectedPrivateToBidOn ();
			}
		}
		
		return tHasSelectedPrivateToBidOn;
	}
	
	public boolean hasSelectedStockToBuy (Bank aBank) {
		boolean tHasSelectedStockToBuy;
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		StartPacketPortfolio tStartPacketPortfolio;
		
		tBank = playerManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tHasSelectedStockToBuy = tBankPortfolio.hasSelectedStockToBuy ();
		if (! tHasSelectedStockToBuy) {
			tBankPool = playerManager.getBankPool ();
			if (tBankPool != null) {
				tBankPoolPortfolio = tBankPool.getPortfolio ();
				tHasSelectedStockToBuy = tBankPoolPortfolio.hasSelectedStockToBuy ();
			}
		}
		if (! tHasSelectedStockToBuy) {
			tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
			if (tStartPacketPortfolio != null) {
				tHasSelectedStockToBuy = tStartPacketPortfolio.hasSelectedStockToBuy ();
			}
		}
		
		return tHasSelectedStockToBuy;
	}
	
	public int getCountSelectedCosToBuy () {
		int tCountSelectedCosToBuy = 0;
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		
		tBank = getBank ();
		if (hasSelectedStockToBuy (tBank)) {
			tBankPortfolio = tBank.getPortfolio ();
			tCountSelectedCosToBuy = tBankPortfolio.getCountSelectedCosToBuy ();
			tBankPool = playerManager.getBankPool ();
			tBankPoolPortfolio = tBankPool.getPortfolio ();
			tCountSelectedCosToBuy += tBankPoolPortfolio.getCountSelectedCosToBuy ();
		}
		
		return tCountSelectedCosToBuy;
	}
	
	public int getCostSelectedStockToBuy () {
		int tSelectedStockToBuyCost;
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		StartPacketPortfolio tStartPacketPortfolio;
		
		tSelectedStockToBuyCost = 0;
		tBank = getBank ();
		if (hasSelectedStockToBuy (tBank)) {
			tBankPortfolio = tBank.getPortfolio ();
			tSelectedStockToBuyCost = tBankPortfolio.getSelectedStockCost ();
			if (tSelectedStockToBuyCost == 0) {
				tBankPool = playerManager.getBankPool ();
				tBankPoolPortfolio = tBankPool.getPortfolio ();
				tSelectedStockToBuyCost = tBankPoolPortfolio.getSelectedStockCost ();
			}
			if (tSelectedStockToBuyCost == 0) {
				tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
				tSelectedStockToBuyCost = tStartPacketPortfolio.getSelectedStockCost ();
			}
		}
		
		return tSelectedStockToBuyCost;
	}
	
	public boolean hasSelectedStocksToSell () {
		boolean tHasSelectedStocksToSell;
		
		tHasSelectedStocksToSell = portfolio.hasSelectedStocksToSell ();
		
		return tHasSelectedStocksToSell;
	}
	
	public boolean hasShareCompanyStocks () {
		boolean tHasShareCompanyStocks;
		
		tHasShareCompanyStocks = portfolio.hasShareCompanyStocks ();
		
		return tHasShareCompanyStocks;
	}

	public boolean hasSoldCompany (String aCompanyAbbrev) {
		return soldCompanies.hasSoldCompany (aCompanyAbbrev);
	}

	public void hidePlayerFrame () {
		playerFrame.setVisible (false);
	}
	
	public boolean isBank () {
		return false;
	}
	
	public boolean isBankPool () {
		return false;
	}

	public boolean isCurrentPlayer () {
		Player tCurrentPlayer;
		
		tCurrentPlayer = playerManager.getCurrentPlayer ();
		
		return this.equals(tCurrentPlayer);
	}

	public boolean isPlayer () {
		return true;
	}
	
	public boolean isCompany () {
		return false;
	}
	
	public boolean isAPrivateCompany () {
		return false;
	}

	public boolean isPresidentOf (Corporation aCorporation) {
		return portfolio.containsPresidentShareOf (aCorporation);
	}
	
	public void loadState (XMLNode aPlayerNode) {
		String tState;
		String tSoldCompanies;
		XMLNodeList tXMLPortfolioNodeList;
		GenericActor tGenericActor;
		
		// Need to remove any Cash the Player has before setting it.
		treasury = aPlayerNode.getThisIntAttribute (Player.AN_CASH);
		tState = aPlayerNode.getThisAttribute (AN_PRIMARY_STATE);
		tGenericActor = new GenericActor ();
		primaryActionState = tGenericActor.getPlayerState (tState);
		tState = aPlayerNode.getThisAttribute (AN_AUCTION_STATE);
		auctionActionState = tGenericActor.getPlayerState (tState);
		exchangedPrezShare = aPlayerNode.getThisAttribute (AN_EXCHANGED_PREZ_SHARE);
		if (exchangedPrezShare.equals ("")) {
			exchangedPrezShare = NO_STOCK_TO_SELL;
		}
		tSoldCompanies = aPlayerNode.getThisAttribute (AN_SOLD_COMPANIES);
		soldCompanies.parse (DELIMITER, tSoldCompanies);
		tXMLPortfolioNodeList = new XMLNodeList (portfolioParsingRoutine);
		tXMLPortfolioNodeList.parseXMLNodeList (aPlayerNode, Portfolio.EN_PORTFOLIO);
		escrows.loadEscrowState (aPlayerNode);
	}
	
	ParsingRoutineI portfolioParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			portfolio.loadPortfolio (aChildNode);
		}
	};

	public void bidAction () {
		playerManager.bidAction (this);
	}
	
	public void buyAction () {
		boolean tNextShareHasBids;
		Certificate tCertificate = playerManager.getCertificateToBuy ();
		ActorI.ActionStates tRoundType;
		String tRoundID;
		BuyStockAction tBuyStockAction;
		boolean tCreateNewAuctionAction = true;
		
		tRoundType = ActorI.ActionStates.StockRound;
		tRoundID = playerManager.getStockRoundID ();

		tNextShareHasBids = playerManager.nextShareHasBids (tCertificate);
		
		tBuyStockAction = new BuyStockAction (tRoundType, tRoundID, this);
		tBuyStockAction = playerManager.buyAction (this, tCertificate, 
				PlayerManager.STOCK_BUY_IN.StockRound, tBuyStockAction);

		playerManager.addAction (tBuyStockAction);
		
		if (tNextShareHasBids) {
			setTriggeredAuction (true);	// Set the Triggered Auction Flag.
			playerManager.startAuctionRound (tCreateNewAuctionAction);
		}

		updateActionButtons ();
	}
	
	public void doneAction () {
		playerManager.doneAction (this);
		hidePlayerFrame ();
	}
	
	public void exchangeAction () {
		playerManager.exchangeAction (this);
	}
	
	public void exchangeCertificate (Certificate aCertificate) {
		playerManager.exchangeCertificate (this, aCertificate);
	}
	
	public void sellAction () {
		playerManager.sellAction (this);
	}
	
	public void undoAction () {
		playerManager.undoAction (this);
	}
	
	public void passAction () {
		playerManager.passAction (this);
		hidePlayerFrame ();
	}
	
	public void passAuctionAction () {
		playerManager.passAuctionAction (this);
	}
	
	public boolean passes () {
		boolean tCanPass;
		
		tCanPass = false;
		if (primaryActionState == ActionStates.NoAction) { 
			primaryActionState = ActionStates.Pass;
			tCanPass = true;
		} else {
			System.err.println ("Player has acted already, can't Pass");
		}
		
		return tCanPass;
	}
	
	public boolean canPassAuction () {
		boolean tCanPass;
		
		tCanPass = false;
		if (auctionActionState == ActionStates.NoAction) { 
			setAuctionActionState (ActionStates.AuctionPass);
			tCanPass = true;
		} else {
			System.err.println ("Player has acted in Auction already, can't Pass");			
		}
		
		return tCanPass;
	}
	
	public void printPlayerInfo () {
		printPlayerStateInfo ();
		System.out.println ("Bought Share State is " + boughtShare);
		System.out.println ("Bid on Share State is " + bidShare);
		
		escrows.printAllEscrows ();
		soldCompanies.printInfo ();
		portfolio.printPortfolioInfo ();
	}
	
	public void printPlayerStateInfo () {
		System.out.println ("Player " + name + " Cash " + Bank.formatCash (treasury));
		System.out.println ("Primary Action State [" + primaryActionState.toString () + "]");
		System.out.println ("Auction Action State [" + auctionActionState.toString () + "]");
	}
	
	public void replacePortfolioInfo (JPanel aPortfolioInfoContainer) {
		playerFrame.replacePortfolioInfo (aPortfolioInfoContainer);
	}
	
	public void setAuctionActionState (ActionStates aAuctionActionState) {
		auctionActionState = aAuctionActionState;
	}

	public void setCertificateLimit (int aCertificateLimit) {
		certificateLimit = aCertificateLimit;
	}
	
	public void setGameHasCoals (boolean aCoals) {
		gameHasCoals = aCoals;
	}
	
	public void setGameHasCompanies (boolean aPrivates, boolean aCoals, boolean aMinors, boolean aShares) {
		setGameHasPrivates (aPrivates);
		setGameHasCoals (aCoals);
		setGameHasMinors (aMinors);
		setGameHasShares (aShares);
	}
	
	public void setGameHasMinors (boolean aMinors) {
		gameHasMinors = aMinors;
	}
	
	public void setGameHasPrivates (boolean aPrivates) {
		gameHasPrivates = aPrivates;
	}

	public void setGameHasShares (boolean aShares) {
		gameHasShares = aShares;
	}

	public void setName (String aName) {
		name = aName;
	}
	
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		setPrimaryActionState (aPrimaryActionState);
	}
	
	public void setPrimaryActionState (ActionStates aPrimaryActionState) {
		primaryActionState = aPrimaryActionState;
		if (primaryActionState == ActionStates.NoAction) {
			playerFrame.setPassButton ();
		}
	}

	public void setRFPlayerLabel (String aText) {
		if (rfPlayerLabel == null) {
			rfPlayerLabel = new JLabel (aText);
		} else {
			rfPlayerLabel.setText (aText);
		}
	}
	
	public void showPlayerFrame () {
		Point tOffsetRoundFramePoint;
		
		if (! playerFrame.isLocationFixed ()) {
			tOffsetRoundFramePoint = getOffsetRoundFramePoint ();
			playerFrame.setLocation (tOffsetRoundFramePoint);
			playerFrame.setLocationFixed (true);
		}
		updatePlayerInfo ();

		playerFrame.setVisible (true);
	}
	
	public void refundEscrow (Certificate aCertificate, int aBidAmount, WinAuctionAction aWinAuctionAction) {
		escrows.refundEscrow (aCertificate, aBidAmount, aWinAuctionAction);
	}

	public void removeAllEscrows () {
		escrows.removeAllEscrows ();
	}
	
	public Escrow getMatchingEscrow (String aActorName) {
		return escrows.getMatchingEscrow (aActorName);
	}

	public Escrow getMatchingEscrow (Certificate aCertificate) {
		return escrows.getMatchingEscrow (aCertificate);
	}
	
	public Escrow addEmptyEscrow (String aName) {
		return escrows.addEmptyEscrow (aName);
	}
	
	public Escrow addEscrowInfo (Certificate aCertificate, int aAmount) {
		return  escrows.addEscrowInfo (aCertificate, aAmount);
	}
	
	public void removeEscrow (Escrow aEscrow) {
		escrows.removeEscrow (aEscrow, Escrows.ESCROW_EXACT_MATCH);
	}
	
	public void removeEscrow (Escrow aEscrow, boolean aMatchCriteria) {
		escrows.removeEscrow (aEscrow, aMatchCriteria);
	}
	
	public void raiseBid (Certificate aCertificate, int aRaise) {
		escrows.raiseBid (aCertificate, aRaise);
	}
	
	public void transferCashTo (CashHolderI aToHolder, int aAmount) {
		aToHolder.addCash (aAmount);
		addCash (-aAmount);
	}
	
	public void addSoldCompany (String aCompanyAbbrev) {
		soldCompanies.addSoldCompanies (aCompanyAbbrev);
	}
	
	public void undoSoldCompany (String aCompanyAbbrev) {
		soldCompanies.undoSoldCompany (aCompanyAbbrev);
	}
	
	public void undoClearSoldCompany (String aSoldCompanies) {
		soldCompanies.undoClearSoldCompany (DELIMITER, aSoldCompanies);
	}
	
	public void updateActionButtons () {
		playerFrame.updateActionButtons ();
	}

	public void updateBankBox () {
		GameManager tGameManager;
		
		tGameManager = playerManager.getGameManager ();
		playerFrame.fillBankBox (tGameManager);
	}
	
	public void updateCertificateInfo () {
		playerFrame.updateCertificateInfo ();
	}
	
	public void updateRoundWindow () {
		playerManager.updateRoundWindow ();
	}
	
	public void updatePlayerInfo () {
		playerFrame.setCashLabel ();
		updateCertificateInfo ();
		updatePortfolioInfo ();
		updateBankBox ();
		updateActionButtons ();
	}
	
	public void updatePortfolioInfo () {
		playerFrame.updatePortfolioInfo ();
		playerFrame.setPortfolioValueLabel();
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}
	
	public void buildPlayerLabel (int aPriorityPlayerIndex, int aPlayerIndex) {
		String tPlayerLabelText;
		String tPlayerState;
		
		tPlayerLabelText = getName ();
		if (aPriorityPlayerIndex == aPlayerIndex) {
			tPlayerLabelText += " [PRIORITY]";
		}
		if (hasPassed ()) {
			tPlayerLabelText += " [PASSED]";
		} else {
			tPlayerState = getStateName ();
			if (tPlayerState != "NoAction") {
				tPlayerLabelText += " [" + tPlayerState + "]";
			}
		}
		setRFPlayerLabel (tPlayerLabelText);
	}


	public void updatePlayerContainer () {
		playerManager.updateRFPlayerLabel (this);
	}
	
	public void updateCashLabel () {
		String tCashText;
		
		tCashText = "Cash: " + Bank.formatCash (getCash ());
		if (cashLabel == null) {
			cashLabel = new JLabel (tCashText);
		} else {
			cashLabel.setText (tCashText);
		}
	}
	
	public JPanel buildAPlayerJPanel (int aPriorityPlayerIndex, int aPlayerIndex) {
		Container tOwnershipContainer;
		JLabel tCertCountLabel;
		JLabel tTotalValueLabel;
		JLabel tSoldCompanies;
		JLabel tEscrowLabel;
		String tEscrowText;
		int tTotalEscrow, tEscrowCount;
		
		if (playerJPanel == null) {
			playerJPanel = new JPanel ();
			playerJPanel.setLayout (new BoxLayout (playerJPanel, BoxLayout.Y_AXIS));

		} else {
			playerJPanel.removeAll ();
		}
		buildPlayerLabel (aPriorityPlayerIndex, aPlayerIndex);
		playerJPanel.add (rfPlayerLabel);
		updateCashLabel ();
		playerJPanel.add (cashLabel);
		
		tEscrowCount = escrows.getEscrowCount ();
		tTotalEscrow = 0;
		if (tEscrowCount > 0) {
			tTotalEscrow = escrows.getTotalEscrow ();
			tEscrowText = tEscrowCount + " Bid";
			if (tEscrowCount > 1) {
				tEscrowText += "s";
			}
			tEscrowText += " totaling " + Bank.formatCash (tTotalEscrow);
			tEscrowLabel = new JLabel (tEscrowText);
			playerJPanel.add (tEscrowLabel);
		}
		
		tTotalValueLabel = new JLabel ("Total Value: " + Bank.formatCash (getCash () + getPortfolioValue () + tTotalEscrow));
		playerJPanel.add (tTotalValueLabel);

		tCertCountLabel = new JLabel (buildCertCountInfo ("Certificates "));
		playerJPanel.add (tCertCountLabel);
		tOwnershipContainer  = portfolio.buildOwnershipContainer ();
		if (tOwnershipContainer != null) {
			playerJPanel.add (tOwnershipContainer);
		}
		tSoldCompanies = soldCompanies.buildSoldCompaniesLabel ();
		if (tSoldCompanies != null) {
			playerJPanel.add (tSoldCompanies);
		}
		playerJPanel.add (Box.createHorizontalStrut (10));
		playerJPanel.repaint ();
		playerJPanel.revalidate ();
		
		return playerJPanel;
	}
	
	public String buildCertCountInfo (String aPrefix) {
		int tCertificateCount;
		int tCertificateLimit;
		int tCertificateTotalCount;
		String tLabel;
		
		tCertificateCount = getCertificateCount ();
		tCertificateLimit = getCertificateLimit ();
		tCertificateTotalCount = getCertificateTotalCount ();
		tLabel = aPrefix + tCertificateCount;
		if (tCertificateTotalCount > tCertificateCount) {
			tLabel += " (" + tCertificateTotalCount + ")";
		}
		tLabel += " of " + tCertificateLimit;
		
		return tLabel;
	}
	
	@Override
	public boolean isAPlayer () {
		return true;
	}

	public boolean isParPriceFrameActive () {
		boolean tIsParPriceFrameActive = false;
		
		tIsParPriceFrameActive = playerManager.isParPriceFrameActive ();
		
		return tIsParPriceFrameActive;
	}
	
	public boolean willSaleOverfillBankPool () {
		boolean tWillSaleOverfillBankPool = false;
		GameManager tGameManager;
		int tBankPoolLimit;
		BankPool tBankPool;
		Certificate tASelectedCertificate;
		int tSelectedCount, tBankPoolCount;
		Corporation tCorporation;
		
		if (hasSelectedStocksToSell ()) {
			tSelectedCount = portfolio.getCountOfCertificatesForSale ();
			tASelectedCertificate = portfolio.getSelectedStockToSell ();
			tCorporation = tASelectedCertificate.getCorporation ();
			tGameManager = playerManager.getGameManager ();
			tBankPoolLimit = tGameManager.getBankPoolShareLimit ();
			tBankPool = tGameManager.getBankPool ();
			tBankPoolCount = tBankPool.getCertificateCountFor (tCorporation);
			if ((tSelectedCount + tBankPoolCount) > tBankPoolLimit) {
				tWillSaleOverfillBankPool = true;
			}
		}
		
		return tWillSaleOverfillBankPool;
	}
	
	@Override
	public boolean isAStockRound () {
		return false;
	}

	@Override
	public boolean isAOperatingRound () {
		return false;
	}

	public void applyAuctionPass () {
		Escrow tCheapestEscrow = escrows.getCheapestEscrow ();
		Certificate tCertificate = tCheapestEscrow.getCertificate ();
		
		tCertificate.setAsPassForBidder (this);
		setAuctionActionState (ActorI.ActionStates.AuctionPass);
	}

	@Override
	public boolean isABank () {
		return false;
	}

	@Override
	public boolean isACorporation () {
		return false;
	}

	public Point getOffsetRoundFramePoint() {
		return playerManager.getOffsetRoundFramePoint ();
	}

	public boolean isAuctionRound () {
		return playerManager.isAuctionRound ();
	}

	public boolean isLastActionComplete () {
		return playerManager.isLastActionComplete ();
	}
}
