package ge18xx.player;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.logging.log4j.Logger;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.QueryOffer;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.FakeBenefit;
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.GameManager;
import ge18xx.market.MarketCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.SetPercentBoughtAction;
import ge18xx.round.action.SetWaitStateAction;
import ge18xx.round.action.WinAuctionAction;
import ge18xx.toplevel.AuctionFrame;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.GUI;
import geUtilities.MessageBean;
import geUtilities.ParsingRoutineI;
import geUtilities.ParsingRoutineIO;

//
//  Player.java
//  Game_18XX
//
//  Created by Mark Smith on 11/26/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class Player implements ActionListener, EscrowHolderI, PortfolioHolderLoaderI {
	public static final ElementName EN_PLAYER = new ElementName ("Player");
	public static final ElementName EN_PLAYERS = new ElementName ("Players");
	public static final ElementName EN_PLAYER_STATES = new ElementName ("PlayerStates");
	public static final AttributeName AN_CASH = new AttributeName ("cash");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_PLAYER_INDEX = new AttributeName ("playerIndex");
	public static final AttributeName AN_PRIMARY_STATE = new AttributeName ("primaryState");
	public static final AttributeName AN_AUCTION_STATE = new AttributeName ("auctionState");
	public static final AttributeName AN_EXCHANGED_PREZ_SHARE = new AttributeName ("exchangePrezShare");
	public static final AttributeName AN_SOLD_COMPANIES = new AttributeName ("soldCompanies");
	public static final AttributeName AN_BOUGHT_SHARE = new AttributeName ("boughtShare");
	public static final AttributeName AN_BID_SHARE = new AttributeName ("bidShare");
	public static final AttributeName AN_TRIGGERED_AUCTION = new AttributeName ("triggeredAuction");
	public static final AttributeName AN_CERTIFICATE_LIMIT = new AttributeName ("certificateLimit");
	public static final String NO_PLAYER_NAME = GUI.EMPTY_STRING;
	public static final String NO_PLAYER_NAME_LABEL = ">NO PLAYER<";
	public static final String SELL_LABEL = "Sell";
	public static final String BUY_LABEL = "Buy";
	public static final String BUY_AT_PAR_LABEL = "Buy at Par";
	public static final String BID_LABEL = "Bid";
	public static final String BUY_BID_LABEL = "Buy-Bid";
	public static final String EXCHANGE_LABEL = "Exchange";
	public static final String NO_STOCK_TO_SELL = null;
	public static final String NO_SHARE_BOUGHT = null;
	public static final String DELIMITER = ",";
	public static final int OWN_ZERO_PERCENT = 0;
	public static final Player NO_PLAYER = null;
	
	// TODO Should not need to store these in this class, fetch from Game Manager if
	// needed
	boolean gameHasPrivates;
	boolean gameHasMinors;
	boolean gameHasShares;
	//-------------
	/*
	 * These attributes are set once, and never change, but are needed for game use
	 */
	PlayerManager playerManager;
	String name;
	String boughtShare;
	PlayerFrame playerFrame;
	
	int certificateLimit;
	JLabel rfPlayerLabel;
	JLabel cashLabel;
	JPanel playerJPanel;
	Logger logger;

	/* These attributes below change during the game, need to save/load them */
	String exchangedPrezShare;
	boolean bidShare;
	boolean triggeredAuction;
	int treasury;
	AllPercentBought allPercentBought;
	RoundDividends roundDividends;
	Benefit benefitInUse;
	Escrows escrows;
	QueryOffer queryOffer;
	Portfolio portfolio;
	ActionStates primaryActionState;
	ActionStates auctionActionState;
	SoldCompanies soldCompanies;
	MessageBean bean;
	ActorI.ActorTypes actorType = ActorI.ActorTypes.Player;

	public Player (String aName, PlayerManager aPlayerManager, int aCertificateLimit) {
		GameManager tGameManager;
		MessageBean tBean;
		String tActorType;
		int tMaxRounds;
		
		tGameManager = aPlayerManager.getGameManager ();
		tActorType = actorType.toString () + " " + aName;
		tMaxRounds = tGameManager.getMaxRounds ();
		roundDividends = new RoundDividends (tMaxRounds);
		tBean = new MessageBean (tActorType);
		setMessageBean (tBean);
		playerJPanel = GUI.NO_PANEL;
		buildPlayer (aName, aPlayerManager, aCertificateLimit, tGameManager);
		setGameHasCompanies (tGameManager);
	}

	public void setGameHasCompanies (GameManager aGameManager) {
		boolean tHasPrivates;
		boolean tHasMinors;
		boolean tHasShares;

		tHasPrivates = aGameManager.gameHasPrivates ();
		tHasMinors = aGameManager.gameHasMinors ();
		tHasShares = aGameManager.gameHasShares ();
		setGameHasPrivates (tHasPrivates);
		setGameHasMinors (tHasMinors);
		setGameHasShares (tHasShares);
	}

	private void setMessageBean (MessageBean aBean) {
		bean = aBean;
	}
	
	public MessageBean getMessageBean () {
		return bean;
	}
	
	private void buildPlayer (String aName, PlayerManager aPlayerManager, int aCertificateLimit,
			GameManager aGameManager) {
		Benefit tBenefitInUse;
		Portfolio tPortfolio;

		/* Save the Player Name -- ONCE */
		name = aName;
		logger = aGameManager.getLogger ();

		/* Set Variables that change during the game, that must be saved/loaded */
		treasury = 0;
		tPortfolio = new Portfolio (this);
		setPortfolio (tPortfolio);
		clearAuctionActionState ();
		clearPrimaryActionState ();
		clearPlayerFlags ();
		setTriggeredAuction (false);
		setExchangedPrezShare (NO_STOCK_TO_SELL);
		setRFPlayerLabel (aName);
		setCertificateLimit (aCertificateLimit);
		allPercentBought = new AllPercentBought ();
		soldCompanies = new SoldCompanies ();
		escrows = new Escrows (this);
		tBenefitInUse = new FakeBenefit ();
		setBenefitInUse (tBenefitInUse);
		setQueryOffer (QueryOffer.NO_QUERY_OFFER);
		playerManager = aPlayerManager;
		buildPlayerFrame (aGameManager);
	}

	public void setPortfolio (Portfolio aPortfolio) {
		portfolio = aPortfolio;
	}
	
	private void buildPlayerFrame (GameManager aGameManager) {
		String tFullTitle;

		tFullTitle = aGameManager.createFrameTitle ("Player");
		playerFrame = new PlayerFrame (tFullTitle, this, aGameManager);
		aGameManager.addNewFrame (playerFrame);
		playerFrame.setFrameToConfigDetails (aGameManager, XMLFrame.getVisibileOFF ());
	}
	
	public int getRoundDividends (int aRoundID) {
		return roundDividends.getDividends (aRoundID);
	}
	
	public void clearRoundDividends () {
		roundDividends.clear ();
	}
	
	public void clearAllPercentBought (SetPercentBoughtAction aSetPercentBoughtAction) {
		allPercentBought.clear (aSetPercentBoughtAction, this);
	}

	@Override
	public void clearRoundDividends (String aRoundID) {
		roundDividends.clear (aRoundID);
	}

	public void addPercentBought (String aAbbrev, int aPercent) {
		allPercentBought.addPercentBought (aAbbrev, aPercent);
	}
	
	public void setPercentBought (String aAbbrev, int aPercent) {
		allPercentBought.setPercentBought (aAbbrev, aPercent);
		if (aPercent == AllPercentBought.ZERO_PERCENT) {
			allPercentBought.removeZeroPercents ();
		}
	}
	
	public int getPercentBought (String aAbbrev) {
		int tPercentBought;
		
		tPercentBought = allPercentBought.getPercentFor (aAbbrev);
		
		return tPercentBought;
	}
	
	public boolean noTouchPass () {
		boolean tNoTouchPass;
		
		tNoTouchPass = playerManager.noTouchPass ();
		
		return tNoTouchPass;
	}
	
	public void setBenefitInUse (Benefit aBenefitInUse) {
		benefitInUse = aBenefitInUse;
	}

	public Benefit getBenefitInUse () {
		return benefitInUse;
	}

	protected void updatePrivateBenefitButtons (JPanel aButtonRow) {
		portfolio.configurePrivatePlayerBenefitButtons (aButtonRow);
	}

	@Override
	public void completeBenefitInUse () {

	}

	public void setTriggeredAuction (boolean aTriggeredAuction) {
		triggeredAuction = aTriggeredAuction;
	}

	public boolean getTriggeredAuction () {
		return triggeredAuction;
	}

	public void setBoughtShare (String aBoughtShare) {
		boughtShare = aBoughtShare;
		if (hasBoughtShare ()) {
			setPrimaryActionState (ActionStates.Bought);
		}
	}

	public void setBidShare (boolean aBidShare) {
		bidShare = aBidShare;
		if (bidShare) {
			setAuctionActionState (ActorI.ActionStates.AuctionRaised);
			setPrimaryActionState (ActionStates.Bid);
		}
	}

	public boolean getBidShare () {
		return bidShare;
	}
	
	public void setExchangedPrezShare (String aExchangedShare) {
		exchangedPrezShare = aExchangedShare;
	}

	public boolean sells () {
		boolean tCanDoAction;

		tCanDoAction = false;
		if (playerManager.isOperatingRound ()) {

		} else if (primaryActionState == ActionStates.Pass) {
			System.err.println ("Player has passed, can't Act");
		} else {
			tCanDoAction = true;
			setPrimaryActionState (ActionStates.Sold);
		}

		return tCanDoAction;
	}

	public boolean acts () {
		boolean tCanDoAction;

		tCanDoAction = false;
		if (playerManager.isOperatingRound ()) {

		} else if (primaryActionState == ActionStates.Pass) {
			System.err.println ("Player has passed, can't Act");
		} else {
			tCanDoAction = true;
			setPrimaryActionState (ActionStates.Acted);
			playerFrame.setDoneButton ();
		}

		return tCanDoAction;
	}

	@Override
	public void addCash (int aAmount) {
		treasury += aAmount;
		playerFrame.setCashLabel ();
		updateListeners (PLAYER_CASH_CHANGED + " by " + aAmount);
	}

	@Override
	public void addCashToDividends (int aAmount, String aOperatingRoundID) {
		roundDividends.addDividend (aOperatingRoundID, aAmount);
	}
	
	@Override
	public void addCertificate (Certificate aCertificate) {
		portfolio.addCertificate (aCertificate);
		playerFrame.updateCertificateInfo ();
		updateListeners (PLAYER_PORTFOLIO_CHANGED + " by adding " + aCertificate.getCompanyAbbrev ());
	}

	public void bringPlayerFrameToFront () {
		playerFrame.updateButtons ();
		playerFrame.toTheFront ();
	}

	public void addSoldCompanies (String aCompanyAbbrev) {
		soldCompanies.addSoldCompanies (aCompanyAbbrev);
	}

	public JPanel buildPortfolioJPanel (ItemListener aItemListener) {
		GameManager tGameManager;
		JPanel tPortfolioPanel;
		
		tGameManager = playerManager.getGameManager ();
		tPortfolioPanel =  portfolio.buildPortfolioJPanel (gameHasPrivates, gameHasMinors, gameHasShares,
					SELL_LABEL, aItemListener, tGameManager);
		
		return tPortfolioPanel;
	}

	public boolean atCertLimit () {
		boolean tAtCertLimit;
		int tCertificateCount;

		tAtCertLimit = false;
		tCertificateCount = getCertificateCount ();
		if (tCertificateCount >= certificateLimit) {
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
			if (tShareCompanyThatExceeds != NO_STOCK_TO_SELL) {
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

		tExceedsCertificateLimit = getCertificateCount () - certificateLimit;

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

		tExceedsThisCorpLimit = NO_STOCK_TO_SELL;
		tGameManager = playerManager.getGameManager ();
		tShareCompanies = tGameManager.getShareCompanies ();
		tCorporationCount = tShareCompanies.getCorporationCount ();
		for (tCorporationIndex = 0; (tCorporationIndex < tCorporationCount)
				&& (tExceedsThisCorpLimit == NO_STOCK_TO_SELL); tCorporationIndex++) {
			tCorporation = tShareCompanies.getCorporation (tCorporationIndex);
			tShareCompanyAbbrev = tCorporation.getAbbrev ();
			if (exceedsShareLimit (tShareCompanyAbbrev)) {
				tExceedsThisCorpLimit = tShareCompanyAbbrev;
			}
		}

		return tExceedsThisCorpLimit;
	}

	public boolean finishAuction (Certificate aCertificateToBuy, AuctionFrame aAuctionFrame, 
					boolean aCreateNewAuctionAction) {
		boolean tNextShareHasBids;
		String tRoundID;
		Bidders tBidders;
		WinAuctionAction tWinAuctionAction;
		ActorI.ActionStates tRoundType;
		RoundManager tRoundManager;

		List<Certificate> tCertificatesToBuy;
		Certificate tFreeCertificate;
		Bank tBank;

		tRoundType = ActorI.ActionStates.AuctionRound;
		tRoundID = "1";

		tNextShareHasBids = playerManager.nextShareHasBids (aCertificateToBuy);

		tWinAuctionAction = new WinAuctionAction (tRoundType, tRoundID, this);
		tCertificatesToBuy = new LinkedList<> ();
		tCertificatesToBuy.add (aCertificateToBuy);

		tBank = playerManager.getBank ();
		tFreeCertificate = tBank.getFreeCertificateWithThisCertificate (aCertificateToBuy);
		tWinAuctionAction = (WinAuctionAction) playerManager.buyAction (this, tCertificatesToBuy,
				PlayerManager.STOCK_BUY_IN.AuctionRound, tWinAuctionAction);
		aCertificateToBuy.refundBids (tWinAuctionAction);
		
		tBidders = aCertificateToBuy.getBidders ();
		aCertificateToBuy.removeAllBidders ();
		tWinAuctionAction.addRemoveBiddersEffect (this, aCertificateToBuy, tBidders);
		
		tWinAuctionAction.addFinishAuctionEffect (this);
		if (mustSetParPrice (tFreeCertificate)) {
			setAllWaitStateEffects (tWinAuctionAction);
		}
		
		playerManager.addAction (tWinAuctionAction);
		playerManager.updateAllPlayerFrames ();
		tRoundManager = playerManager.getRoundManager ();
		tRoundManager.finishCurrentRound ();
		
		return tNextShareHasBids;
	}
	
	private boolean mustSetParPrice (Certificate aFreeCertificate) {
		boolean tMustSetParPrice;

		tMustSetParPrice = false;

		if (aFreeCertificate != Certificate.NO_CERTIFICATE) {
			if (aFreeCertificate.isPresidentShare ()) {
				if (aFreeCertificate.isAShareCompany ()) {
					if (! aFreeCertificate.hasParPrice ()) {
						tMustSetParPrice = true;
					}
				}
			}
		}

		return tMustSetParPrice;
	}

	public void setAllWaitStateEffects (SetWaitStateAction aWaitStateAction) {
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		int tPlayerCount;
		int tPlayerIndex;
		String tPlayerName;
		String tActingPlayerName;
		String tCurrentPlayerName;
		Player tPlayer;
		Player tCurrentPlayer;

		tNewState = ActorI.ActionStates.WaitState;
		tPlayerCount = playerManager.getPlayerCount ();
		tCurrentPlayer = playerManager.getCurrentPlayer ();
		tCurrentPlayerName = tCurrentPlayer.getName ();
		tActingPlayerName = getName ();
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
			tPlayer = playerManager.getPlayer (tPlayerIndex);
			tPlayerName = tPlayer.getName ();
			if (tActingPlayerName.equals (tPlayerName)) {
				// If this is the player that needs to answer the query, No Wait State is needed
			} else if (tCurrentPlayerName.equals (tPlayerName)) {
				// If this is the player generating the query, No Wait State is needed, will be set to 'Wait to Respond'
			} else {
				// All other players are put into a Wait State
				tOldState = tPlayer.getPrimaryActionState ();
				tPlayer.resetPrimaryActionState (tNewState);
				aWaitStateAction.addSetWaitStateEffect (this, tPlayer, tOldState, tNewState);
			}
		}
		aWaitStateAction.setChainToPrevious (true);
	}

	public void clearAuctionActionState () {
		setAuctionActionState (ActionStates.NoAction);
	}

	public void clearPrimaryActionState () {
		setPrimaryActionState (ActionStates.NoAction);
	}
	
	public void clearPlayerFlags () {
		setBoughtShare (NO_SHARE_BOUGHT);
		setBidShare (false);
		if (playerManager != PlayerManager.NO_PLAYER_MANAGER) {
			playerManager.updateRFPlayerLabel (this);
			playerManager.updateRoundWindow ();
		}
		if (playerFrame != XMLFrame.NO_XML_FRAME) {
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
		tExceedsShareLimit = (tPlayerOwnedPercentage > tPlayerShareLimit * 10);

		return tExceedsShareLimit;
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

	@Override
	public Bank getBank () {
		return playerManager.getBank ();
	}

	public BankPool getBankPool () {
		return playerManager.getBankPool ();
	}

	@Override
	public int getCash () {
		return treasury;
	}

	public int getCertificateCount () {
		int tCertificateCount;

		if (portfolio == Portfolio.NO_PORTFOLIO) {
			tCertificateCount = 0;
		} else {
			tCertificateCount = portfolio.getCertificateCountAgainstLimit ();
		}

		return tCertificateCount;
	}

	public int getCertificateLimit () {
		return certificateLimit;
	}

	public int getCertificateTotalCount () {
		int tCertificateCount;

		if (portfolio == Portfolio.NO_PORTFOLIO) {
			tCertificateCount = 0;
		} else {
			tCertificateCount = portfolio.getCertificateTotalCount ();
		}
		return tCertificateCount;
	}

	public List<Certificate> getCertificatesToSell () {
		List<Certificate> tCertificatesToSell;

		tCertificatesToSell = portfolio.getCertificatesToSell ();

		return tCertificatesToSell;
	}

	public List<Certificate> getCertificatesToBuy () {
		List<Certificate> tCertificatesToBuy;

		tCertificatesToBuy = playerManager.getCertificatesToBuy ();

		return tCertificatesToBuy;
	}

	public Certificate getCertificateToExchange () {
		return portfolio.getCertificateToExchange ();
	}
	
	public boolean hasPresidentCertFor (Corporation aCorporation) {
		boolean tPlayerWhoIsPresident;
		
		tPlayerWhoIsPresident = portfolio.hasPresidentCertFor (aCorporation);
		
		return tPlayerWhoIsPresident;
	}
	

	@Override
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;

		tCurrentHolder = playerManager.getCurrentHolder (aLoadedCertificate);

		return tCurrentHolder;
	}

	@Override
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
		int tEscrowCount;

		tEscrowCount = 0;
		if (escrows != Escrows.NO_ESCROWS) {
			tEscrowCount = escrows.getEscrowCount ();
		}

		return tEscrowCount;
	}

	public int getTotalEscrow () {
		int tTotalEscrow;

		tTotalEscrow = 0;
		if (escrows != Escrows.NO_ESCROWS) {
			tTotalEscrow = escrows.getTotalEscrow ();
		}

		return tTotalEscrow;
	}

	public String getAllDividends () {
		String tAllDividends;
		int tOperatingRoundCount;
		
		tOperatingRoundCount = playerManager.getOperatingRoundCount ();
		tAllDividends = roundDividends.getAllRoundsDividends (tOperatingRoundCount);
		
		return tAllDividends;
	}
	
	@Override
	public GameManager getGameManager () {
		return playerManager.getGameManager ();
	}

	@Override
	public String getName () {
		return name;
	}

	@Override
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
		XMLElement tXMLElement;
		XMLElement tXMLPortofolioElements;
		XMLElement tXMLEscrows;
		XMLElement tXMLQueryOfferElements;
		XMLElement tXMLAllPercentBoughtElements;
		String tCompaniesSold;
		int tOperatingRoundCount;

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
		tXMLElement.setAttribute (AN_CERTIFICATE_LIMIT, certificateLimit);
		tOperatingRoundCount = playerManager.getOperatingRoundCount ();
		roundDividends.addDividendAttribute (tXMLElement, tOperatingRoundCount);
		
		tXMLAllPercentBoughtElements = allPercentBought.getElements (aXMLDocument);
		tXMLElement.appendChild (tXMLAllPercentBoughtElements);
		tXMLPortofolioElements = portfolio.getElements (aXMLDocument);
		tXMLElement.appendChild (tXMLPortofolioElements);
		tXMLEscrows = escrows.getEscrowXML (aXMLDocument);
		tXMLElement.appendChild (tXMLEscrows);
		if (queryOffer != QueryOffer.NO_QUERY_OFFER) {
			tXMLQueryOfferElements = queryOffer.getElements (aXMLDocument);
			tXMLElement.appendChild (tXMLQueryOfferElements);
		}

		return tXMLElement;
	}

	@Override
	public String getStateName () {
		return primaryActionState.toString ();
	}

	@Override
	public Portfolio getPortfolio () {
		return portfolio;
	}

	@Override
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
		if (rfPlayerLabel != GUI.NO_LABEL) {
			tText = rfPlayerLabel.getText ();
		}

		return tText;
	}

	public int getMustSellPercent (String aCompanyAbbrev) {
		PortfolioHolderI tPresident;
		ShareCompany tShareCompany;
		Player tPresidentPlayer;
		int tCurrentPlayerHasXPercent;
		int tPresidentHasXPercent;
		int tMustSellPercent;

		tMustSellPercent = 0;
		tShareCompany = playerManager.getShareCompany (aCompanyAbbrev);
		if (tShareCompany == Corporation.NO_CORPORATION) {
			System.err.println ("Share Company with abbrev " + aCompanyAbbrev + " could not be found");
		} else {
			tCurrentPlayerHasXPercent = getPercentOwnedOf (tShareCompany);
			tPresident = tShareCompany.getPresident ();
			if (tPresident != Player.NO_PLAYER) {
				if (tPresident.isAPlayer ()) {
					tPresidentPlayer = (Player) tPresident;
					tPresidentHasXPercent = tPresidentPlayer.getPercentOwnedOf (tShareCompany);
					tMustSellPercent = 1 + tCurrentPlayerHasXPercent - tPresidentHasXPercent;
				}
			}
		}

		return tMustSellPercent;
	}

	public Player getNextPossiblePrez (String aCompanyAbbrev) {
		TrainCompany tShareCompany;
		Player tNextPossiblePrez;

		tNextPossiblePrez = NO_PLAYER;
		tShareCompany = playerManager.getShareCompany (aCompanyAbbrev);
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tNextPossiblePrez = playerManager.findPlayerWithMost (tShareCompany, this);
		}

		return tNextPossiblePrez;
	}

	public int getShareLimit (String aCompanyAbbrev) {
		MarketCell tMarketCell;
		ShareCompany tShareCompany;
		GameManager tGameManager;
		int tPlayerShareLimit;

		tPlayerShareLimit = playerManager.getPlayerShareLimit ();
		tGameManager = getGameManager ();
		tShareCompany = tGameManager.getShareCompany (aCompanyAbbrev);
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tMarketCell = tShareCompany.getSharePriceMarketCell ();
			if (tMarketCell != MarketCell.NO_MARKET_CELL) {
				if (tMarketCell.getExceedPlayerCorpShareLimit ()) {
					tPlayerShareLimit = 10;
				}
			}
		}

		return tPlayerShareLimit;
	}

	public boolean hasActionsToUndo () {
		boolean tActionsToUndo;

		if (playerManager == PlayerManager.NO_PLAYER_MANAGER) {
			tActionsToUndo = false;
		} else {
			tActionsToUndo = playerManager.hasActionsToUndo ();
		}

		return tActionsToUndo;
	}

	public boolean hasActed () {
		boolean tHasActed;

		// If the Primary Action State for the Player is NoAction or Pass,
		// the Player has NOT Acted, otherwise the Player Has Acted

		if ((primaryActionState == ActionStates.NoAction) || 
			(primaryActionState == ActionStates.Pass)) {
			tHasActed = false;
		} else {
			tHasActed = true;
		}

		return tHasActed;
	}

	public boolean hasBoughtShare () {
		boolean tHasBoughtShare;

		tHasBoughtShare = true;
		if (boughtShare == NO_SHARE_BOUGHT) {
			tHasBoughtShare = false;
		}

		return tHasBoughtShare;
	}

	public String boughtShare () {
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
		int tCurrentPlayerHasXPercent;
		int tPresidentHasXPercent;

		tHasLessThanPresident = true;
		tShareCompany = playerManager.getShareCompany (aCompanyAbbrev);
		if (tShareCompany != Corporation.NO_CORPORATION) {
			tCurrentPlayerHasXPercent = getPercentOwnedOf (tShareCompany);
			tPresidentOf = (Player) tShareCompany.getPresident ();
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
			if (tPlayerOwnedPercentage >= tPlayerShareLimit * 10) {
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
		return auctionActionState == ActionStates.AuctionPassed;
	}

	public boolean hasSelectedOneToExchange () {
		return portfolio.hasSelectedOneToExchange ();
	}

	public boolean hasSelectedPrezToExchange () {
		boolean tHasSelectedPrezToExchange;

		tHasSelectedPrezToExchange = portfolio.hasSelectedPrezToExchange ();

		return tHasSelectedPrezToExchange;
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

	public boolean canSellSelectedStocks () {
		boolean tCanSellSelectedStocks;
		String tSelectedAbbrev;
		int tSelectedPercent;
		int tPercentCanSell;
		int tPercentBought;
		int tPercentOwned;
		
		tSelectedAbbrev = portfolio.getSelectedAbbrevToSell ();
		tSelectedPercent = getSelectedPercent ();
		tPercentBought = allPercentBought.getPercentFor (tSelectedAbbrev);
		tPercentOwned = portfolio.getCertificatePercentageFor (tSelectedAbbrev);
		tPercentCanSell = tPercentOwned - tPercentBought;
		if (tPercentCanSell >= tSelectedPercent) {
			tCanSellSelectedStocks = true;
		} else {
			tCanSellSelectedStocks = false;
		}
		
		return tCanSellSelectedStocks;
	}
	
	public Certificate getMustBuyCertificate () {
		Certificate tMustBuyCertificate = Certificate.NO_CERTIFICATE;
		Bank tBank;
		
		tBank = getBank ();
		if (tBank != Bank.NO_BANK) {
			tMustBuyCertificate = tBank.getMustBuyCertificate ();
		}
		
		return tMustBuyCertificate;
	}
	
	public boolean hasSelectedPrivateToBidOn () {
		StartPacketPortfolio tStartPacketPortfolio;
		Bank tBank;
		boolean tHasSelectedPrivateToBidOn;

		tHasSelectedPrivateToBidOn = false;
		tBank = getBank ();
		if (tBank != Bank.NO_BANK) {
			tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
			if (tStartPacketPortfolio != Portfolio.NO_PORTFOLIO) {
				tHasSelectedPrivateToBidOn = tStartPacketPortfolio.hasSelectedPrivateToBidOn ();
			}
		}

		return tHasSelectedPrivateToBidOn;
	}

	public boolean hasSelectedStockToBuy () {
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		StartPacketPortfolio tStartPacketPortfolio;
		boolean tHasSelectedStockToBuy;

		tBank = playerManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tHasSelectedStockToBuy = tBankPortfolio.hasSelectedStockToBuy ();
		if (!tHasSelectedStockToBuy) {
			tBankPool = playerManager.getBankPool ();
			if (tBankPool != BankPool.NO_BANK_POOL) {
				tBankPoolPortfolio = tBankPool.getPortfolio ();
				tHasSelectedStockToBuy = tBankPoolPortfolio.hasSelectedStockToBuy ();
			}
		}
		if (!tHasSelectedStockToBuy) {
			tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
			if (tStartPacketPortfolio != StartPacketPortfolio.NO_START_PACKET) {
				tHasSelectedStockToBuy = tStartPacketPortfolio.hasSelectedStockToBuy ();
			}
		}

		return tHasSelectedStockToBuy;
	}

	public boolean hasSelectedStockToBid (Bank aBank) {
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		StartPacketPortfolio tStartPacketPortfolio;
		boolean tHasSelectedStockToBid;

		tBank = playerManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tHasSelectedStockToBid = tBankPortfolio.hasSelectedStockToBid ();
		if (!tHasSelectedStockToBid) {
			tBankPool = playerManager.getBankPool ();
			if (tBankPool != BankPool.NO_BANK_POOL) {
				tBankPoolPortfolio = tBankPool.getPortfolio ();
				tHasSelectedStockToBid = tBankPoolPortfolio.hasSelectedStockToBid ();
			}
		}
		if (!tHasSelectedStockToBid) {
			tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
			if (tStartPacketPortfolio != StartPacketPortfolio.NO_START_PACKET) {
				tHasSelectedStockToBid = tStartPacketPortfolio.hasSelectedStockToBid ();
			}
		}

		return tHasSelectedStockToBid;
	}

	public int getCountSelectedCosToBuy () {
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		StartPacketPortfolio tStartPacketPortfolio;
		int tCountSelectedCosToBuy;

		tCountSelectedCosToBuy = 0;
		tBank = getBank ();
		if (hasSelectedStockToBuy ()) {
			tBankPortfolio = tBank.getPortfolio ();
			tCountSelectedCosToBuy = tBankPortfolio.getCountSelectedCosToBuy ();
			tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
			tCountSelectedCosToBuy += tStartPacketPortfolio.getCountSelectedCosToBuy ();
			tBankPool = playerManager.getBankPool ();
			tBankPoolPortfolio = tBankPool.getPortfolio ();
			tCountSelectedCosToBuy += tBankPoolPortfolio.getCountSelectedCosToBuy ();
		}

		return tCountSelectedCosToBuy;
	}

	public int getCountSelectedCosToBid () {
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		StartPacketPortfolio tStartPacketPortfolio;
		int tCountSelectedCosToBid;

		tCountSelectedCosToBid = 0;
		tBank = getBank ();
		if (hasSelectedStockToBid (tBank)) {
			tBankPortfolio = tBank.getPortfolio ();
			tCountSelectedCosToBid = tBankPortfolio.getCountSelectedCosToBid ();
			tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
			tCountSelectedCosToBid += tStartPacketPortfolio.getCountSelectedCosToBid ();
			tBankPool = playerManager.getBankPool ();
			tBankPoolPortfolio = tBankPool.getPortfolio ();
			tCountSelectedCosToBid += tBankPoolPortfolio.getCountSelectedCosToBid ();
		}

		return tCountSelectedCosToBid;
	}

	public int getCountSelectedCertificatesToBuy () {
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		StartPacketPortfolio tStartPacketPortfolio;
		int tCountSelectedCertificatesToBuy;

		tCountSelectedCertificatesToBuy = 0;
		tBank = getBank ();
		if (hasSelectedStockToBuy ()) {
			tBankPortfolio = tBank.getPortfolio ();
			tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
			tCountSelectedCertificatesToBuy = tBankPortfolio.getCountSelectedCertificatesToBuy ();
			tCountSelectedCertificatesToBuy += tStartPacketPortfolio.getCountSelectedCertificatesToBuy ();
			tBankPool = playerManager.getBankPool ();
			tBankPoolPortfolio = tBankPool.getPortfolio ();
			tCountSelectedCertificatesToBuy += tBankPoolPortfolio.getCountSelectedCertificatesToBuy ();
		}

		return tCountSelectedCertificatesToBuy;

	}

	public Certificate getSelectedCertificateToBuy () {
		Certificate tCertificate;
		BankPool tBankPool;

		tCertificate = Certificate.NO_CERTIFICATE;
		tBankPool = getBankPool ();
		if (hasSelectedStockToBuy ()) {
			tCertificate = tBankPool.getCertificateToBuy ();
		}

		return tCertificate;
	}

	public int getCostSelectedStockToBuy () {
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		StartPacketPortfolio tStartPacketPortfolio;
		int tSelectedStockToBuyCost;

		tSelectedStockToBuyCost = 0;
		tBank = getBank ();
		if (hasSelectedStockToBuy ()) {
			tBankPortfolio = tBank.getPortfolio ();
			tSelectedStockToBuyCost = tBankPortfolio.getSelectedStocksCost ();
			if (tSelectedStockToBuyCost == 0) {
				tBankPool = playerManager.getBankPool ();
				tBankPoolPortfolio = tBankPool.getPortfolio ();
				tSelectedStockToBuyCost = tBankPoolPortfolio.getSelectedStocksCost ();
			}
			if (tSelectedStockToBuyCost == 0) {
				tStartPacketPortfolio = tBank.getStartPacketPortfolio ();
				tSelectedStockToBuyCost = tStartPacketPortfolio.getSelectedStocksCost ();
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
		playerFrame.hideFrame ();
	}

	public boolean isWaiting () {
		return (primaryActionState == ActorI.ActionStates.WaitState);
	}

	public boolean isCurrentPlayer () {
		Player tCurrentPlayer;
		boolean tIsCurrentPlayer;
		
		tCurrentPlayer = playerManager.getCurrentPlayer ();
		tIsCurrentPlayer = this.equals (tCurrentPlayer);
		
		return tIsCurrentPlayer;
	}

	public boolean isPresidentOf (Corporation aCorporation) {
		return portfolio.containsPresidentShareOf (aCorporation);
	}

	public void loadState (XMLNode aPlayerNode) {
		String tState;
		String tSoldCompanies;
		String tExchangedPrezShare;
		XMLNodeList tXMLAllPercentBoughtNodeList;
		XMLNodeList tXMLPortfolioNodeList;
		XMLNodeList tXMLQueryOfferNodeList;
		GenericActor tGenericActor;
		GameManager tGameManager;
		int tCertificateLimit;
		ActorI.ActionStates tAuctionActionState;
		// Need to remove any Cash the Player has before setting it.

		treasury = aPlayerNode.getThisIntAttribute (Player.AN_CASH);
		boughtShare = aPlayerNode.getThisAttribute (AN_BOUGHT_SHARE, NO_SHARE_BOUGHT);
		tState = aPlayerNode.getThisAttribute (AN_PRIMARY_STATE);
		tGenericActor = new GenericActor ();
		clearPrimaryActionState ();
		clearPlayerFlags ();
		setPrimaryActionState (tGenericActor.getPlayerState (tState));
		if (primaryActionState == ActorI.ActionStates.NoState) {
			setPrimaryActionState (tGenericActor.getPlayerFormationState (tState));
		}
		roundDividends.parseDividendAtribute (aPlayerNode);
		tState = aPlayerNode.getThisAttribute (AN_AUCTION_STATE);
		tAuctionActionState = tGenericActor.getPlayerState (tState);
		setAuctionActionState (tAuctionActionState);
		tExchangedPrezShare = aPlayerNode.getThisAttribute (AN_EXCHANGED_PREZ_SHARE);
		if (tExchangedPrezShare.equals (GUI.EMPTY_STRING)) {
			setExchangedPrezShare (NO_STOCK_TO_SELL);
		} else {
			setExchangedPrezShare (tExchangedPrezShare);
		}
		tSoldCompanies = aPlayerNode.getThisAttribute (AN_SOLD_COMPANIES);
		soldCompanies.parse (DELIMITER, tSoldCompanies);
		tCertificateLimit = aPlayerNode.getThisIntAttribute (AN_CERTIFICATE_LIMIT);
		setCertificateLimit (tCertificateLimit);
		tXMLAllPercentBoughtNodeList = new XMLNodeList (AllPercentBoughtParsingRoutine);
		tXMLAllPercentBoughtNodeList.parseXMLNodeList (aPlayerNode, AllPercentBought.EN_ALL_PERCENT_BOUGHT);
	
		tXMLPortfolioNodeList = new XMLNodeList (portfolioParsingRoutine);
		tXMLPortfolioNodeList.parseXMLNodeList (aPlayerNode, Portfolio.EN_PORTFOLIO);
		
		escrows.loadEscrowState (aPlayerNode);
		
		tGameManager = getGameManager ();
		tXMLQueryOfferNodeList = new XMLNodeList (queryParsingRoutine, tGameManager);
		tXMLQueryOfferNodeList.parseXMLNodeList (aPlayerNode, QueryOffer.EN_QUERY_OFFER);
		// TODO: Build way to load a QueryOffer (PurchasePrivateOffer, PurchaseTrainOffer, ExchangePrivateQuery)
		// to load the QueryOffer Object here, and in the Train Company LoadStatus method
		// Probably store 'class' in the EN_QUERY_OFFER Element as attribute
		// Then just like an Action or Effect, use reflections to load it.
		// Can this be a generic method in 'QueryOffer' that both here and Train Company can call it?
	}
	
	ParsingRoutineI queryParsingRoutine = new ParsingRoutineIO () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode, Object aMetaObject) {
			QueryOffer tQueryOffer;
			Class<?> tQueryOfferToLoad;
			Constructor<?> tQueryOfferConstructor;
			String tClassName;
			GameManager tGameManager;

			// Use Reflections to identify the OptionEffect to create, and call the
			// constructor with the XMLNode and Game Manager
			tClassName = aChildNode.getThisAttribute (QueryOffer.AN_CLASS_NAME);
			try {
				if (aMetaObject instanceof GameManager) {
					tGameManager = (GameManager) aMetaObject;
					tQueryOfferToLoad = Class.forName (tClassName);
					tQueryOfferConstructor = tQueryOfferToLoad.getConstructor (aChildNode.getClass (),
							tGameManager.getClass ());
					tQueryOffer = (QueryOffer) tQueryOfferConstructor.newInstance (aChildNode, tGameManager);
					setQueryOffer (tQueryOffer);
				}
			} catch (Exception eException) {
				eException.printStackTrace();
			}
		}
	};

	ParsingRoutineI portfolioParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			portfolio.loadPortfolio (aChildNode);
		}
	};

	ParsingRoutineI AllPercentBoughtParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			allPercentBought.loadAllPercentBought (aChildNode);
		}
	};

	public void bidAction () {
		playerManager.bidAction (this);
		updateListeners (PLAYER_BID_CHANGED );
	}

	// TODO: Move the BuyAction Methods over the Player Manager

	/**
	 * Buy the Certificate the player manager has identified, and handle if next share has bids to handle
	 * Then handle the Auctions if necessary.
	 *
	 */
	public void buyAction () {
		List<Certificate> tCertificatesToBuy;
		boolean tConfirmBuyShare;
		
		tConfirmBuyShare = confirmBuyShareAction ();
		if (tConfirmBuyShare) {	
			tCertificatesToBuy = playerManager.getCertificatesToBuy ();
	
			buyAction (tCertificatesToBuy);
	
		}
		playerFrame.updateButtons ();
		updateListeners (PLAYER_PORTFOLIO_CHANGED + " - BOUGHT");
	}
	
	public List<Benefit> getOwnerTypeBenefits () {
		List<Benefit> tOwnerTypeBenefits;
		
		tOwnerTypeBenefits = portfolio.getOwnerTypeBenefits ();

		return tOwnerTypeBenefits;
	}
	
	public boolean confirmBuyShareAction () {
		Certificate tCertificate;
		List<Certificate> tCertificatesToBuy;
		boolean tConfirmedBuyShareAction;
		int tResponse;
		int tCertificateCount;		
		
		tCertificatesToBuy = playerManager.getCertificatesToBuy ();
		tCertificateCount = tCertificatesToBuy.size ();
		if (tCertificateCount == 1) {
			tCertificate = tCertificatesToBuy.get (0);
			if (tCertificate.isAShareCompany ()) {
				if ((playerManager.confirmBuyPresidentShare ()) && (tCertificate.isPresidentShare ())) {
					tResponse = JOptionPane.showConfirmDialog (playerFrame,
							"You have chosen to buy the President Share of  " + tCertificate.getCompanyAbbrev () + 
							"\nAre you sure you want to buy this President Share?", 
							"Confirm Buy", JOptionPane.YES_NO_OPTION);
					if (tResponse == JOptionPane.YES_OPTION) {
						tConfirmedBuyShareAction = true;
					} else {
						tConfirmedBuyShareAction = false;
					}
				} else {
					tConfirmedBuyShareAction = true;
				}
			} else {
				tConfirmedBuyShareAction = true;				
			}
		} else if (tCertificateCount > 1) {
			// TODO -- present a confirmation for buy X (where X > 1) Confirmation Dialog for buying more than 1 share of 
			// the particular company. -- May not be worth the trouble
			tConfirmedBuyShareAction = true;
		} else {
			tConfirmedBuyShareAction = false;
		}

		return tConfirmedBuyShareAction;
	}
	
	/**
	 * Buy the Certificates in the list of Certificates provided, creating the appropriate Action.
	 *
	 * @param aCertificatesToBuy the List of Certificates from the Player Manager
	 *
	 */
	public void buyAction (List<Certificate> aCertificatesToBuy) {
		ActorI.ActionStates tRoundType;
		BuyStockAction tBuyStockAction;
		String tRoundID;
		
		tRoundType = ActorI.ActionStates.StockRound;
		tRoundID = playerManager.getStockRoundID ();
		
		tBuyStockAction = new BuyStockAction (tRoundType, tRoundID, this);
		updatePercentBought (aCertificatesToBuy, tBuyStockAction);
		
		tBuyStockAction = playerManager.buyAction (this, aCertificatesToBuy,
				PlayerManager.STOCK_BUY_IN.StockRound, tBuyStockAction);
		sortShareCompanies ();
		
		playerManager.addAction (tBuyStockAction);
	}

	public void sortShareCompanies () {
		GameManager tGameManager;
		CorporationList tCorporationList;
		
		tGameManager = getGameManager ();
		tCorporationList = tGameManager.getShareCompanies ();
		tCorporationList.sortByOperatingOrder ();
		tGameManager.updateRoundFrame ();
	}

	public void updatePercentBought (List<Certificate> aCertificatesToBuy, BuyStockAction aBuyStockAction) {
		String tAbbrev;
		int tPercentBought;
		int tPreviousPercent;
		int tNewPercent;

		tPercentBought = 0;
		tAbbrev = GUI.EMPTY_STRING;
		for (Certificate tCertificateBought : aCertificatesToBuy) {
			if (tCertificateBought.isAShareCompany ()) {
				tPercentBought += tCertificateBought.getPercentage ();
				tAbbrev = tCertificateBought.getCompanyAbbrev ();
			}
		}
		if (tPercentBought > 0) {
			tPreviousPercent = allPercentBought.getPercentFor (tAbbrev);
			allPercentBought.addPercentBought (tAbbrev, tPercentBought);
			tNewPercent = allPercentBought.getPercentFor (tAbbrev);
			aBuyStockAction.addSetPercentBoughtEffect (this, tAbbrev, tPreviousPercent, tNewPercent);
		}
	}
	
	public void doneAction () {
		playerManager.doneAction (this);
		updateListeners (PLAYER_STATUS_CHANGED + " - DONE");
		hidePlayerFrame ();
	}

	public void exchangeAction () {
		playerManager.exchangeAction (this);
		updateListeners (PLAYER_PORTFOLIO_CHANGED + " - EXCHANGED");
	}

	public void exchangeCertificate (Certificate aCertificate) {
		playerManager.exchangeCertificate (this, aCertificate);
		updateListeners (PLAYER_PORTFOLIO_CHANGED + " - EXCHANGED");
	}

	public void sellAction () {
		playerManager.sellAction (this);
		updateListeners (PLAYER_PORTFOLIO_CHANGED + " - SOLD");
	}

	public void undoAction () {
		playerManager.undoAction (this);
	}

	public void passAction () {
		playerManager.resetRoundFrameBackgrounds ();
		playerManager.passAction (this);
		hidePlayerFrame ();
		updateListeners (PLAYER_STATUS_CHANGED + " - PASSED");
	}

	public void passAuctionAction () {
		playerManager.passAuctionAction (this);
		updateListeners (PLAYER_STATUS_CHANGED + " - PASSED AUCTION");
	}

	public boolean passes () {
		boolean tCanPass;
		boolean tCanChangeState;
		
		tCanPass = false;
		tCanChangeState = primaryActionState.canChangeState (ActionStates.Pass);
		if (tCanChangeState) {
			clearPrimaryActionState ();
			clearPlayerFlags ();
			setPrimaryActionState (ActionStates.Pass);
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
			setAuctionActionState (ActionStates.AuctionPassed);
			tCanPass = true;
		} else {
			System.err.println ("Player has acted in Auction already, can't Pass");
		}

		return tCanPass;
	}

	public void printPlayerInfo () {
		printPlayerStateInfo ();
		System.out.println ("Bought Share State is " + boughtShare);		// PRINTLOG
		System.out.println ("Bid on Share State is " + bidShare);

		escrows.printAllEscrows ();
		soldCompanies.printInfo ();
		portfolio.printPortfolioInfo ();
	}

	public void printPlayerStateInfo () {
		System.out.println ("Player " + name + " cash " + Bank.formatCash (treasury));	// PRINTLOG
		System.out.println ("Primary Action State [" + primaryActionState.toString () + "]");
		System.out.println ("Auction Action State [" + auctionActionState.toString () + "]");
		System.out.println ("Player Certificate Limit " + certificateLimit);
	}

	@Override
	public void replacePortfolioInfo (JPanel aPortfolioInfoJPanel) {
		playerFrame.replacePortfolioInfo (aPortfolioInfoJPanel);
	}

	public void setAuctionActionState (ActionStates aAuctionActionState) {
		auctionActionState = aAuctionActionState;
	}

	public void setCertificateLimit (int aCertificateLimit) {
		certificateLimit = aCertificateLimit;
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

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		setPrimaryActionState (aPrimaryActionState);
		playerFrame.updateButtons ();
	}

	public void setPrimaryActionState (ActionStates aPrimaryActionState) {
		primaryActionState = aPrimaryActionState;
	}

	public void setRFPlayerLabel (String aText) {
		if (rfPlayerLabel == GUI.NO_LABEL) {
			rfPlayerLabel = new JLabel (aText);
		} else {
			rfPlayerLabel.setText (aText);
		}
	}

	public void showPlayerFrame () {
		Point tOffsetRoundFramePoint;

		if (playerManager.isNetworkAndIsThisClient (name) || 
			! playerManager.isNetworkGame ()) {
			if (!playerFrame.isLocationFixed ()) {
				tOffsetRoundFramePoint = getOffsetRoundFramePoint ();
				playerFrame.setLocation (tOffsetRoundFramePoint);
				playerFrame.setLocationFixed (true);
			}
			updatePlayerInfo ();

			playerFrame.setVisible (true);
			playerFrame.setEnabled (true);
		}
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

	@Override
	public Escrow addEscrowInfo (Certificate aCertificate, int aAmount) {
		return escrows.addEscrowInfo (aCertificate, aAmount);
	}

	@Override
	public void addEscrow (Escrow aEscrow) {
		escrows.addEscrow (aEscrow);
	}
	
	@Override
	public boolean removeEscrow (Escrow aEscrow, boolean aMatchCriteria) {
		return escrows.removeEscrow (aEscrow, aMatchCriteria);
	}

	public void raiseBid (Certificate aCertificate, int aRaise) {
		escrows.raiseBid (aCertificate, aRaise);
	}

	@Override
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

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		RoundManager tRoundManager;
		boolean tInterrupted;

		tActionCommand = aEvent.getActionCommand ();

		if (PlayerFrame.PASS.equals (tActionCommand)) {
			passAction ();
		}
		if (Player.BUY_BID_LABEL.equals (tActionCommand)) {
			if (playerFrame.isBuyAction ()) {
				buyAction ();
			} else {
				bidAction ();
			}
		}
		if (Player.BUY_LABEL.equals (tActionCommand)) {
				buyAction ();
		}
		if (PlayerFrame.DONE.equals (tActionCommand)) {
			doneAction ();
		}
		if (SELL_LABEL.equals (tActionCommand)) {
			sellAction ();
		}
		if (EXCHANGE_LABEL.equals (tActionCommand)) {
			exchangeAction ();
		}
		if (PlayerFrame.UNDO.equals (tActionCommand)) {
			undoAction ();
		}
		if (ButtonsInfoFrame.EXPLAIN.equals (tActionCommand)) {
			playerFrame.handleExplainButtons ();
		}
		
		tRoundManager = playerManager.getRoundManager ();
		tInterrupted = tRoundManager.checkAndHandleInterruption ();
		if (!tInterrupted) {
			tRoundManager.checkAndHandleRoundEnds ();
		}
		playerManager.updateRoundWindow ();
	}

	public void updatePlayerJPanel () {
		playerManager.updateRFPlayerLabel (this);
	}

	public void updatePortfolioInfo () {
		playerFrame.updatePortfolioInfo ();
		playerFrame.setPortfolioValueLabel ();
	}

	public void updatePlayerInfo () {
		GameManager tGameManager;

		tGameManager = playerManager.getGameManager ();

		playerFrame.updatePlayerInfo (tGameManager);
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}

	public void buildPlayerLabel (int aPriorityPlayerIndex, int aPlayerIndex) {
		String tPlayerLabelText;

		tPlayerLabelText = getName ();
		if (aPriorityPlayerIndex == aPlayerIndex) {
			tPlayerLabelText += " [PRIORITY]";
		}
		
		tPlayerLabelText = buildNameState (tPlayerLabelText);
		setRFPlayerLabel (tPlayerLabelText);
	}

	public String buildNameState () {
		return buildNameState (name);
	}
	
	public String buildNameState (String aPlayerName) {
		String tPlayerLabel;
		
		tPlayerLabel = aPlayerName + " [" + getStateName () + "]";

		return tPlayerLabel;
	}
	
	public void updateCashLabel () {
		String tCashText;

		tCashText = Bank.formatCash (Bank.CASH_LABEL, treasury); 
		if (cashLabel == GUI.NO_LABEL) {
			cashLabel = new JLabel (tCashText);
		} else {
			cashLabel.setText (tCashText);
		}
	}

	public int getTotalValue () {
		return (getCash () + getPortfolioValue () + escrows.getTotalEscrow ());
	}

	public JPanel buildAPlayerJPanel (int aPriorityPlayerIndex, int aPlayerIndex) {
		if (playerJPanel == GUI.NO_PANEL) {
			playerJPanel = new JPanel ();
			playerJPanel.setLayout (new BoxLayout (playerJPanel, BoxLayout.Y_AXIS));
		} else {
			playerJPanel.removeAll ();
		}
		updateAPlayerJPanel (aPriorityPlayerIndex, aPlayerIndex);

		return playerJPanel;
	}

	private void updateAPlayerJPanel (int aPriorityPlayerIndex, int aPlayerIndex) {
		JPanel tOwnershipPanel;
		JLabel tCertCountLabel;
		JLabel tTotalValueLabel;
		JLabel tDividendsLabel;
		JLabel tSoldCompanies;
		GameManager tGameManager;

		tGameManager = playerManager.getGameManager ();
		buildPlayerLabel (aPriorityPlayerIndex, aPlayerIndex);
		playerJPanel.add (rfPlayerLabel);
		updateCashLabel ();
		playerJPanel.add (cashLabel);

		if (escrows.getEscrowCount () > 0) {
			playerJPanel.add (escrows.getEscrowLabel ());
		}

		tTotalValueLabel = new JLabel ("Total Value: " + Bank.formatCash (getTotalValue ()));
		playerJPanel.add (tTotalValueLabel);

		tDividendsLabel = new JLabel ("Dividends: " + getAllDividends ());
		playerJPanel.add (tDividendsLabel);
		
		tCertCountLabel = new JLabel (buildCertCountInfo ("Certificates "));
		playerJPanel.add (tCertCountLabel);
		
		tOwnershipPanel = portfolio.buildOwnershipPanel (tGameManager);
		if (tOwnershipPanel != GUI.NO_PANEL) {
			playerJPanel.add (tOwnershipPanel);
		}
		tSoldCompanies = soldCompanies.buildSoldCompaniesLabel ();
		if (tSoldCompanies != SoldCompanies.NO_SOLD_COMPANIES) {
			playerJPanel.add (tSoldCompanies);
		}
		playerJPanel.add (Box.createHorizontalStrut (10));
		playerJPanel.repaint ();
		playerJPanel.revalidate ();
	}

	public String buildCertCountInfo (String aPrefix) {
		int tCertificateCount;
		int tCertificateLimit;
		int tCertificateTotalCount;
		String tLabel;

		tCertificateCount = getCertificateCount ();
		tCertificateLimit = certificateLimit;
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
		Certificate tASelectedCertificate;
		Corporation tCorporation;
		boolean tWillSaleOverfillBankPool;
		int tSelectedPercentage;
		
		tWillSaleOverfillBankPool = false;
		if (hasSelectedStocksToSell ()) {
			tSelectedPercentage = portfolio.getPercentOfCertificatesForSale ();
			tASelectedCertificate = portfolio.getSelectedStockToSell ();
			tCorporation = tASelectedCertificate.getCorporation ();
			tWillSaleOverfillBankPool = willOverfillBankPool (tSelectedPercentage, tCorporation);
		}

		return tWillSaleOverfillBankPool;
	}

	public boolean willOverfillBankPool (int aPercentage, Corporation aCorporation) {
		GameManager tGameManager;
		BankPool tBankPool;
		boolean tWillSaleOverfillBankPool;
		int tBankPoolPercentage;
		int tBankPoolPercentageLimit;

		tWillSaleOverfillBankPool = false;
		tGameManager = playerManager.getGameManager ();
		tBankPoolPercentageLimit = tGameManager.getBankPoolPercentageLimit ();
		tBankPool = tGameManager.getBankPool ();
		tBankPoolPercentage = tBankPool.getCertificatePercentageFor (aCorporation);
		if ((aPercentage + tBankPoolPercentage) > tBankPoolPercentageLimit) {
			tWillSaleOverfillBankPool = true;
		}

		return tWillSaleOverfillBankPool;
	}

	public void applyAuctionPass () {
		Escrow tCheapestEscrow;
		Certificate tCertificate;

		tCheapestEscrow = escrows.getCheapestEscrow ();
		tCertificate = tCheapestEscrow.getCertificate ();
		tCertificate.setAsPassForBidder (this);
		setAuctionActionState (ActorI.ActionStates.AuctionPassed);
	}

	public Point getOffsetRoundFramePoint () {
		return playerManager.getOffsetRoundFramePoint ();
	}

	public boolean isLastActionComplete () {
		return playerManager.isLastActionComplete ();
	}

	public String getClientName () {
		String tClientName;

		tClientName = playerManager.getClientUserName ();

		return tClientName;
	}

	public void setQueryOffer (QueryOffer aQueryOffer) {
		queryOffer = aQueryOffer;
	}

	public QueryOffer getQueryOffer () {
		return queryOffer;
	}

	@Override
	public void updateInfo () {
		playerManager.updateRoundWindow ();
	}

	public Certificate getNextFastBuyCertificate (int aFastBuyIndex) {
		Certificate tCertificate;

		tCertificate = portfolio.getNextFastBuyCertificate (aFastBuyIndex, this);

		return tCertificate;
	}

	@Override
	public void updateListeners (String aMessage) {
		bean.addMessage (aMessage);
	}

	// Methods to test if Player has completed various Formation States
	
	public boolean getRepaymentFinished () {
		boolean tRepaymemtFinished;
		
		if (primaryActionState == ActionStates.LoanRepayment) {
			tRepaymemtFinished = true;
		} else if (primaryActionState == ActionStates.FormationComplete) {
			tRepaymemtFinished = true;
		} else {
			tRepaymemtFinished = false;
		}
		
		return tRepaymemtFinished;
	}
		
	public boolean getSharesExchanged () {
		boolean tSharesExchanged;
		
		if (primaryActionState == ActionStates.ShareExchange) {
			tSharesExchanged = true;
		} else {
			tSharesExchanged = false;
		}
		
		return tSharesExchanged;
	}
	
	public boolean getTokensExchanged () {
		boolean tTokensExchanged;
		
		if (primaryActionState == ActionStates.TokenExchange) {
			tTokensExchanged = true;
		} else {
			tTokensExchanged = false;
		}
		
		return tTokensExchanged;
	}
	
	public boolean getAssetsCollected () {
		boolean tAssetsCollected;
		
		if (primaryActionState == ActionStates.AssetCollection) {
			tAssetsCollected = true;
		} else {
			tAssetsCollected = false;
		}
		
		return tAssetsCollected;
	}
	
	public boolean getStockValueCalculated () {
		boolean tStockValueCalculated;
		
		if (primaryActionState == ActionStates.StockValueCalculation) {
			tStockValueCalculated = true;
		} else {
			tStockValueCalculated = false;
		}
		
		return tStockValueCalculated;
	}
}
