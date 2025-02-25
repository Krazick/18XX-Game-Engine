package ge18xx.player;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

//
//  PlayerManager.java
//  Game_18XX
//
//  Created by Mark Smith on 11/27/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TokenStack;
import ge18xx.company.TrainCompany;
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.Capitalization;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.market.MarketCell;
import ge18xx.round.AuctionRound;
import ge18xx.round.OperatingRound;
import ge18xx.round.Round;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.BidStockAction;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.ChangeRoundAction;
import ge18xx.round.action.ChangeStateAction;
import ge18xx.round.action.ClearRoundDividendsAction;
import ge18xx.round.action.DonePlayerAction;
import ge18xx.round.action.ExchangeStockAction;
import ge18xx.round.action.FormationRoundAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.PassAction;
import ge18xx.round.action.SellStockAction;
import ge18xx.round.action.SetPercentBoughtAction;
import ge18xx.round.action.StartStockAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.toplevel.PlayerInputFrame;
import geUtilities.GUI;
import geUtilities.MessageBean;
import geUtilities.ParsingRoutineI;
import geUtilities.ParsingRoutineIO;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.xml.XMLSaveGameI;

public class PlayerManager implements XMLSaveGameI {
	public static final String NO_PLAYER_NAME = null;
	public static final List<Player> NO_PLAYERS = null;
	public static final PlayerManager NO_PLAYER_MANAGER = null;
	public static final boolean AUCTION_BUY = false;
	public static final int BID_INCREMENT = 5;
	public static final int NO_PLAYER_INDEX = -1;
	public static final int CERTIFICATE_LIMIT_ZERO = 0;

	public enum STOCK_BUY_IN {
		StockRound, AuctionRound, OperatingRound, FormationRound, ContractBidRound
	} // Round a Stock Certificate was purchased

	GameManager gameManager;
	StockRound stockRound;
	AuctionRound auctionRound;
	ParPriceFrame parPriceFrame;
	List<Player> players;

	public PlayerManager (GameManager aGameManager) {
		players = new LinkedList<Player> ();
		gameManager = aGameManager;
		setStockRound (StockRound.NO_STOCK_ROUND);
	}

	private void addPlayer (String aName, int aCertificateLimit) {
		Player tPlayer;

		tPlayer = new Player (aName, this, aCertificateLimit);
		addPlayer (tPlayer);
	}

	public void addPlayer (Player aPlayer) {
		if (!players.contains (aPlayer)) {
			players.add (aPlayer);
		}
	}
	
	public List<Player> getPlayers () {
		return players;
	}
	
	public void addMessageBeans () {
		MessageBean tBean;
		
		for (Player tPlayer : players) {
			tBean = tPlayer.getMessageBean ();
			gameManager.addGoodBean (tBean);
		}
	}
	
	public void updatePlayerListeners (String aMessage) {
		for (Player tPlayer : players) {
			tPlayer.updateListeners (aMessage);
		}
	}
	
	public boolean noTouchPass () {
		return gameManager.noTouchPass ();
	}
	
	public String getPlayersInOrder () {
		String tPlayersInOrder;

		tPlayersInOrder = GUI.EMPTY_STRING;
		for (Player tPlayer : players) {
			if (tPlayersInOrder != GUI.EMPTY_STRING) {
				tPlayersInOrder += GUI.COMMA_SPACE;
			}
			tPlayersInOrder += tPlayer.getName ();
		}

		return tPlayersInOrder;
	}

	public Player getPlayerWhoTriggeredAuction () {
		Player tPlayer;

		tPlayer = Player.NO_PLAYER;
		for (Player tAPlayer : players) {
			if (tAPlayer.getTriggeredAuction ()) {
				tPlayer = tAPlayer;
			}
		}

		return tPlayer;
	}

	public ParPriceFrame getParPriceFrame () {
		return parPriceFrame;
	}

	public boolean canBeExchanged (Corporation aCorporation) {
		boolean tCanBeExchanged;
		Player tCurrentPresident;
		int tPercentOwned;
		int tPresidentPercent;
		Portfolio tPresidentsPortfolio;
		Certificate tPresidentCertificate;

		tCanBeExchanged = false;
		tCurrentPresident = (Player) aCorporation.getPresident ();
		tPresidentsPortfolio = tCurrentPresident.getPortfolio ();
		tPresidentCertificate = tPresidentsPortfolio.getPresidentCertificate (aCorporation);
		tPresidentPercent = tPresidentCertificate.getPercentage ();
		for (Player tPlayer : players) {
			if (tPlayer != tCurrentPresident) {
				tPercentOwned = tPlayer.getPercentOwnedOf (aCorporation);
				if (tPercentOwned >= tPresidentPercent) {
					tCanBeExchanged = true;
				}
			}
		}

		return tCanBeExchanged;
	}

	public void clearAllPlayerPasses (ChangeRoundAction aChangeRoundAction) {
		Player.ActionStates tOldState;
		Player.ActionStates tNewState;

		for (Player tPlayer : players) {
			tOldState = tPlayer.getPrimaryActionState ();

			tPlayer.clearPrimaryActionState ();
			tPlayer.clearPlayerFlags ();
			tNewState = tPlayer.getPrimaryActionState ();
			if (aChangeRoundAction != ChangeStateAction.NO_CHANGE_STATE_ACTION) {
				aChangeRoundAction.addStateChangeEffect (tPlayer, tOldState, tNewState);
			}
		}
		if (!gameManager.applyingAction ()) {
			aChangeRoundAction.setChainToPrevious (true);
		}
	}

	private String buildPreviousRoundID (String aOldRoundID, int aOldRoundID2) {
		String tPreviousRoundID;
		String [] tPreviousIDs;
		
		tPreviousIDs = aOldRoundID.split ("\\.");
		tPreviousRoundID = tPreviousIDs [0] + "." + aOldRoundID2;
		
		return tPreviousRoundID;
	}
	
	public void clearAllPlayerDividends (String aOldRoundID) {
		ClearRoundDividendsAction tClearRoundDividendsAction;
		String tOperatingRoundID;
		String tOldOperatingRoundID;
		int tORIndex;
		int tMaxORs;
		int tPreviousAmount;
		int tEffectCount;
		OperatingRound tOperatingRound;
		RoundManager tRoundManager;
		
		tRoundManager = gameManager.getRoundManager ();
		tOperatingRound = tRoundManager.getOperatingRound ();
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tClearRoundDividendsAction = new ClearRoundDividendsAction (ActorI.ActionStates.OperatingRound, 
							tOperatingRoundID, tOperatingRound);
		tMaxORs = gameManager.getMaxRounds ();
		for (Player tPlayer : players) {
			for (tORIndex = 1; tORIndex <= tMaxORs; tORIndex++) {
				tPreviousAmount = tPlayer.getRoundDividends (tORIndex);
				if (tPreviousAmount != 0) {
					tOldOperatingRoundID = buildPreviousRoundID (aOldRoundID, tORIndex);
					tClearRoundDividendsAction.addClearRoundDividendEffect (tPlayer, tPreviousAmount,
								tOldOperatingRoundID);
					tPlayer.clearRoundDividends (tOldOperatingRoundID);
				}
			}
		}
		tEffectCount = tClearRoundDividendsAction.getEffectCount ();
		if (tEffectCount > 0) {
			if (!gameManager.applyingAction ()) {
				tClearRoundDividendsAction.setChainToPrevious (true);
				addAction (tClearRoundDividendsAction);
			}
		}
	}
	
	public Player getPresidentFromPlayers (Corporation aCorporation) {
		Player tPlayerWhoIsPresident;
		
		tPlayerWhoIsPresident = Player.NO_PLAYER;
		for (Player tPlayer : players) {
			if (tPlayer.hasPresidentCertFor (aCorporation)) {
				tPlayerWhoIsPresident = tPlayer;
			}
		}
		
		return tPlayerWhoIsPresident;
	}
	

	public int getOperatingRoundCount () {
		return gameManager.getOperatingRoundCount ();
	}
	
	public void clearAllPlayerSelections () {
		for (Player tPlayer : players) {
			tPlayer.clearAllSelections ();
		}
	}

	public void clearAllAuctionStates () {
		for (Player tPlayer : players) {
			tPlayer.clearAuctionActionState ();
		}
	}

	public void clearAllSoldCompanies () {
		for (Player tPlayer : players) {
			tPlayer.clearAllSoldCompanies ();
		}
	}

	public void clearPlayerAuctionStateAt (int aPlayerIndex) {
		Player tPlayer;

		tPlayer = getPlayer (aPlayerIndex);
		if (tPlayer != Player.NO_PLAYER) {
			tPlayer.clearAuctionActionState ();
		}
	}

	public void clearPlayerPrimaryStateAt (int aPlayerIndex) {
		Player tPlayer;

		tPlayer = getPlayer (aPlayerIndex);
		if (tPlayer != Player.NO_PLAYER) {
			tPlayer.clearPrimaryActionState ();
			tPlayer.clearPlayerFlags ();
		}
	}

	/**
	 * Clear the Exchanged Shares for All Players
	 *
	 */
	public void clearAllExchangedShares () {
		for (Player tPlayer : players) {
			tPlayer.setExchangedPrezShare (Player.NO_STOCK_TO_SELL);
		}
	}
	
	/**
	 * Clear All Percent Bought for All Players
	 *
	 */
	public void clearAllPercentBought () {
		SetPercentBoughtAction tSetPercentBoughtAction;
		String tStockRoundID;
		Bank tBank;
		
		tStockRoundID = getStockRoundID ();
		tBank = getBank ();
		tSetPercentBoughtAction = new SetPercentBoughtAction (ActorI.ActionStates.StockRound, tStockRoundID, tBank);
		for (Player tPlayer : players) {
			tPlayer.clearAllPercentBought (tSetPercentBoughtAction);
		}
		if (tSetPercentBoughtAction.getEffectCount () > 0) {
			tSetPercentBoughtAction.setChainToPrevious (true);
			addAction (tSetPercentBoughtAction);
		}
	}

	public boolean didAnyoneBuy () {
		boolean tDidAnyoneBuy;

		tDidAnyoneBuy = false;

		for (Player tPlayer : players) {
			if (tPlayer.hasBoughtShare ()) {
				tDidAnyoneBuy = true;
			}
		}

		return tDidAnyoneBuy;
	}

	public Bank getBank () {
		Bank tBank;

		tBank = Bank.NO_BANK;
		if (stockRound != Round.NO_ROUND) {
			tBank = stockRound.getBank ();
		}

		return tBank;
	}

	public RoundManager getRoundManager () {
		return gameManager.getRoundManager ();
	}
		
	public BankPool getBankPool () {
		return stockRound.getBankPool ();
	}

	public Player getCurrentPlayer () {
		return stockRound.getCurrentPlayer ();
	}

	public String getCurrentPlayerName () {
		return stockRound.getCurrentPlayerName ();
	}

	public List<ActionStates> getPlayerAuctionStates () {
		List<ActionStates> aAuctionStates;
		ActorI.ActionStates tActionState;

		aAuctionStates = new LinkedList<> ();
		for (Player tPlayer : players) {
			tActionState = tPlayer.getAuctionActionState ();
			aAuctionStates.add (tActionState);
		}

		return aAuctionStates;
	}

	public void resetPlayerAuctionStates (List<ActionStates> aAuctionStates) {
		int tPlayerCount;
		int tStateCount;
		Player tPlayer;

		tPlayerCount = players.size ();
		tStateCount = aAuctionStates.size ();
		if (tPlayerCount == tStateCount) {
			for (int tIndex = 0; tIndex < tStateCount; tIndex++) {
				tPlayer = players.get (tIndex);
				tPlayer.setAuctionActionState (aAuctionStates.get (tIndex));
			}
		}
	}

	public GameManager getGameManager () {
		return gameManager;
	}

	public int getNextPlayerIndex (int aCurrentPlayerIndex) {
		int tNextPlayerIndex;

		tNextPlayerIndex = (aCurrentPlayerIndex + 1) % getPlayerCount ();

		return tNextPlayerIndex;
	}

	public PlayerFrame getCurrentPlayerFrame () {
		PlayerFrame tPlayerFrame;
		Player tCurrentPlayer;

		tCurrentPlayer = getCurrentPlayer ();
		tPlayerFrame = tCurrentPlayer.getPlayerFrame ();

		return tPlayerFrame;
	}

	private PlayerFrame getPlayerFrame (String aPlayerName) {
		PlayerFrame tPlayerFrame;
		Player tPlayer;

		tPlayer = getPlayer (aPlayerName);
		tPlayerFrame = tPlayer.getPlayerFrame ();

		return tPlayerFrame;
	}

	public Point getOffsetFrame (String aPlayerName) {
		Point tNewPoint;
		PlayerFrame tPlayerFrame;

		tPlayerFrame = getPlayerFrame (aPlayerName);
		tNewPoint = tPlayerFrame.getOffsetFrame ();

		return tNewPoint;
	}

	public Player getPlayer (String aName) {
		Player tFoundPlayer;

		tFoundPlayer = Player.NO_PLAYER;
		if (players != NO_PLAYERS) {
			if (aName != NO_PLAYER_NAME) {
				for (Player tPlayer : players) {
					if (tPlayer.getName ().equals (aName)) {
						tFoundPlayer = tPlayer;
					}
				}
			}
		}

		return tFoundPlayer;
	}

	public Player getPlayer (int aIndex) {
		Player tPlayer;

		tPlayer = Player.NO_PLAYER;
		if (players != NO_PLAYERS) {
			if (aIndex != NO_PLAYER_INDEX) {
				if (aIndex < getPlayerCount ()) {
					tPlayer = players.get (aIndex);
				}
			}
		}

		return tPlayer;
	}

	public int getPlayerCount () {
		return players.size ();
	}

	public void setPlayersToNoAction (ChangeStateAction aChangeStateAction) {
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		
		for (Player tPlayer : players) {
			tOldState = tPlayer.getPrimaryActionState ();
			tPlayer.resetPrimaryActionState (ActorI.ActionStates.NoAction);
			tNewState = tPlayer.getPrimaryActionState ();
			aChangeStateAction.addStateChangeEffect (tPlayer, tOldState, tNewState);
		}
	}
	
	public boolean isInCompanyFormationState () {
		Player tPlayer;
		boolean tIsCompanyFormationState;
		ActorI.ActionStates tActionState;
		GenericActor tGenericActor;
		
		tPlayer = getPlayer (0);
		tGenericActor = new GenericActor ();

		tActionState = tPlayer.getPrimaryActionState ();
		if (tGenericActor.isFormationRound (tActionState)) {
			tIsCompanyFormationState = true;
		} else {
			tIsCompanyFormationState = false;
		}
		
		return tIsCompanyFormationState;
	}
	
	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		XMLElement tXMLElement;
		String tType;
		
		tType = aEN_Type.toString ();
		if (tType.equals (Player.EN_PLAYERS.toString ())) {
			tXMLElement = addPlayerElements (aXMLDocument);
		} else if (tType.equals (Player.EN_PLAYER_STATES.toString ())) {
			tXMLElement = addPlayerStateElements (aXMLDocument);
		} else {
			tXMLElement = XMLElement.NO_XML_ELEMENT;
		}
		
		return tXMLElement;	
	}

	public XMLElement addPlayerElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tPlayerElement;

		tXMLElement = aXMLDocument.createElement (Player.EN_PLAYERS);
		for (Player tPlayer : players) {
			tPlayerElement = tPlayer.getPlayerElement (aXMLDocument);
			tXMLElement.appendChild (tPlayerElement);
		}

		return tXMLElement;
	}

	public XMLElement addPlayerStateElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tPlayerStateElement;

		tXMLElement = aXMLDocument.createElement (Player.EN_PLAYER_STATES);
		for (Player tPlayer : players) {
			tPlayerStateElement = tPlayer.getPlayerStateElement (aXMLDocument);
			tXMLElement.appendChild (tPlayerStateElement);
		}

		return tXMLElement;
	}

	public int getCurrentPlayerIndex () {
		int tPlayerIndex;
		Player tCurrentPlayer;
		
		tCurrentPlayer = getCurrentPlayer ();
		tPlayerIndex = getPlayerIndex (tCurrentPlayer);
		
		return tPlayerIndex;
	}
	
	public int getPlayerIndex (String aPlayerName) {
		int tPlayerIndex;
		int tPlayerCount;
		int tFoundPlayerIndex;
		Player tThisPlayer;

		tPlayerCount = getPlayerCount ();
		tFoundPlayerIndex = NO_PLAYER_INDEX;
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
			tThisPlayer = getPlayer (tPlayerIndex);
			if (aPlayerName.equals (tThisPlayer.getName ())) {
				tFoundPlayerIndex = tPlayerIndex;
			}
		}

		return tFoundPlayerIndex;
	}
	
	public int getPlayerIndex (Player aPlayer) {
		int tPlayerIndex;
		int tPlayerCount;
		int tFoundPlayerIndex;
		Player tThisPlayer;

		tPlayerCount = getPlayerCount ();
		tFoundPlayerIndex = NO_PLAYER_INDEX;
		for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
			tThisPlayer = getPlayer (tPlayerIndex);
			if (tThisPlayer == aPlayer) {
				tFoundPlayerIndex = tPlayerIndex;
			}
		}

		return tFoundPlayerIndex;
	}

	public int getPlayerShareLimit () {
		int tPlayerShareLimit;
		RoundManager tRoundManager;
		GameManager tGameManager;
		GameInfo tGameInfo;

		tRoundManager = stockRound.getRoundManager ();
		tGameManager = tRoundManager.getGameManager ();
		tGameInfo = tGameManager.getActiveGame ();
		tPlayerShareLimit = tGameInfo.getPlayerShareLimit ();

		return tPlayerShareLimit;
	}

	public ActorI.ActionStates getPlayerState (String aState) {
		GenericActor tGenericActor;

		tGenericActor = new GenericActor ();

		return tGenericActor.getPlayerState (aState);
	}

	public boolean hasActionsToUndo () {
		boolean tActionsToUndo;

		tActionsToUndo = stockRound.hasActionsToUndo ();

		return tActionsToUndo;
	}

	public boolean haveAllSharesBeenExchanged () {
		boolean tAllSharesBeenExchanged;

		tAllSharesBeenExchanged = true;
		for (Player tPlayer : players) {
			tAllSharesBeenExchanged = tAllSharesBeenExchanged && tPlayer.getSharesExchanged ();
		}

		return tAllSharesBeenExchanged;
	}

	public boolean haveAllPassed () {
		boolean tAllPassed;

		tAllPassed = true;
		for (Player tPlayer : players) {
			tAllPassed = tAllPassed && tPlayer.hasPassed ();
		}

		return tAllPassed;
	}

	public boolean haveAllPassedAuction () {
		boolean tAllPassed;

		tAllPassed = true;
		for (Player tPlayer : players) {
			tAllPassed = tAllPassed && tPlayer.hasPassedInAuction ();
		}

		return tAllPassed;
	}

	public void bidAction (Player aPlayer) {
		BidStockAction tBidStockAction;
		Player.ActionStates tOldState;
		Player.ActionStates tNewState;
		Player.ActionStates tOldAuctionState;
		Player.ActionStates tNewAuctionState;
		Escrow tEscrow;
		Certificate tCertificateToBidOn;
		int tCashValue;
		boolean tBidShare;

		tOldState = aPlayer.getPrimaryActionState ();
		tOldAuctionState = aPlayer.getAuctionActionState ();
		if (aPlayer.acts ()) {
			tCertificateToBidOn = stockRound.getCertificateToBidOn ();
			if (tCertificateToBidOn.isAPrivateCompany ()) {
				tBidStockAction = new BidStockAction (stockRound.getRoundState (), stockRound.getID (), aPlayer);
				tCashValue = tCertificateToBidOn.getHighestBid ();
				tCashValue += BID_INCREMENT;
				tBidShare = true;
				
				aPlayer.setBidShare (tBidShare);
				tBidStockAction.addBidShareEffect (aPlayer, tBidShare);
				tEscrow = aPlayer.addEscrowInfo (tCertificateToBidOn, tCashValue);
				tBidStockAction.addCashTransferEffect (aPlayer, tEscrow, tCashValue);
				tBidStockAction.addBidToCertificateEffect (aPlayer, tCertificateToBidOn, tCashValue);
				tBidStockAction.addEscrowToPlayerEffect (aPlayer, tEscrow);

				tNewState = aPlayer.getPrimaryActionState ();
				tNewAuctionState = aPlayer.getAuctionActionState ();
				tBidStockAction.addStateChangeEffect (aPlayer, tOldState, tNewState);
				tBidStockAction.addAuctionStateChangeEffect (aPlayer, tOldAuctionState, tNewAuctionState);
				addAction (tBidStockAction);
				// Need to set the default Auction Action State to a Raise for use in Action
				// Frame
				aPlayer.setAuctionActionState (ActorI.ActionStates.AuctionRaised);
				aPlayer.updatePlayerInfo ();
				stockRound.updateRFPlayerLabel (aPlayer);
			} else {
				System.err.println (aPlayer.getName () + " Not bidding on Private");
			}
		} else {
			System.err.println (aPlayer.getName () + " has Acted");
		}
	}

	public Certificate getCertificateToBuy () {
		return stockRound.getCertificateToBuy ();
	}

	public List<Certificate> getCertificatesToBuy () {
		List<Certificate> tCertificatesToBuy;

		tCertificatesToBuy = stockRound.getCertificatesToBuy ();

		return tCertificatesToBuy;
	}

	public String getStockRoundID () {
		return stockRound.getID ();
	}

	public boolean nextShareHasBids (Certificate aCertificateToBuy) {
		boolean tNextShareHasBids;
		Bank tBank;

		tNextShareHasBids = false;
		tBank = stockRound.getBank ();
		if (tBank.isInStartPacket (aCertificateToBuy)) {
			// Capture whether next share available has bids on it -- if So, after Sale,
			// need to go to Auction Round
			// For 1830 only Private Companies in the Start Packet can be bid upon, and
			// thereby auctioned.
			tNextShareHasBids = tBank.nextShareHasBids ();
		}

		return tNextShareHasBids;
	}

	public BuyStockAction buyAction (Player aPlayer, List<Certificate> aCertificatesToBuy, 
			STOCK_BUY_IN aRoundBuying, BuyStockAction aBuyStockAction) {
		BuyStockAction tBuyStockAction;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		Certificate tFreeCertificate;
		Certificate tCertificateToBuy;
		String tCompanyAbbrev;
		String tPriorBoughtShare;
		List<Certificate> tCertificatesToTransfer;
		ShareCompany tShareCompany;
		Portfolio tPlayerPortfolio;
		Portfolio tSourcePortfolio;
		Bank tBank;
		int tParPrice;
		Player tCurrentPresident;
		PortfolioHolderI tCurrentHolder;
		boolean tCanBuyStock;
		boolean tChainToPrevious;
//		SetParValueEffect tSetParValueEffect;
		
		tCanBuyStock = true;
		tChainToPrevious = false;
		// Get State before acting for saving in the Action Stack.
		tOldState = aPlayer.getPrimaryActionState ();

		// IMPORTANT --- Only want to check 'aPlayer.acts ()' on a StockRound
		// since it changes the Player's State
		if (aRoundBuying.equals (STOCK_BUY_IN.StockRound)) {
			tCanBuyStock = aPlayer.acts ();
		}
		if (tCanBuyStock) {
			tBank = stockRound.getBank ();
			tCertificateToBuy = aCertificatesToBuy.get (0);
			if (tCertificateToBuy.isAShareCompany ()) {
				tShareCompany = tCertificateToBuy.getShareCompany ();
				if (tShareCompany == ShareCompany.NO_SHARE_COMPANY) {
					tCurrentPresident = Player.NO_PLAYER;
				} else {
					tCurrentHolder = tShareCompany.getPresident ();
					if (tCurrentHolder == PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
						tCurrentPresident = Player.NO_PLAYER;
					} else if (tCurrentHolder.isAPlayer ()) {
						tCurrentPresident = (Player) tCurrentHolder;
					} else {
						tCurrentPresident = Player.NO_PLAYER;
					}
				}
				if (! tCertificateToBuy.hasParPrice ()) {
					tParPrice = tCertificateToBuy.getComboParValue ();
					if ((tParPrice > 0) && (tShareCompany != ShareCompany.NO_SHARE_COMPANY)) {
//						tSetParValueEffect = buildSetParPriceEffect (aPlayer, tCertificateToBuy, 
//								tShareCompany, tParPrice);
//						aBuyStockAction.addEffect (tSetParValueEffect);
						gameManager.setParPrice (tShareCompany, tParPrice);
						aBuyStockAction.addSetParValueEffect (aPlayer, tShareCompany, tParPrice);
					} else {
						System.err.println ("***Selected Par Price is " + tParPrice + 
											" or tShareCompany is NULL***");
					}
				} else {
					if (! gameManager.marketHasTokenFor (tShareCompany)) {
						gameManager.setParPriceToken (tShareCompany);
						tParPrice = tShareCompany.getParPrice ();
						aBuyStockAction.addSetParValueEffect (aPlayer, tShareCompany, tParPrice);
					}
				}
			} else {
				tCurrentPresident = Player.NO_PLAYER;
				tShareCompany = (ShareCompany) Corporation.NO_CORPORATION;
			}

			tFreeCertificate = tBank.getFreeCertificateWithThisCertificate (tCertificateToBuy);
			tSourcePortfolio = getSourcePortfolio (tCertificateToBuy);

			manageCashTransfer (aPlayer, aCertificatesToBuy, aBuyStockAction, tBank, tSourcePortfolio);

			tPlayerPortfolio = aPlayer.getPortfolio ();

			doFinalShareBuySteps (tPlayerPortfolio, tSourcePortfolio, aCertificatesToBuy, aBuyStockAction);
			/* If this Private include a Free Certificate, hand that over as well */
			if (tFreeCertificate != Certificate.NO_CERTIFICATE) {
				tCertificatesToTransfer = new LinkedList<> ();
				tCertificatesToTransfer.add (tFreeCertificate);
				doFinalShareBuySteps (tPlayerPortfolio, tSourcePortfolio, tCertificatesToTransfer, 
							aBuyStockAction);
				setFreeCertificatePrice (tFreeCertificate, aPlayer, aBuyStockAction);
			}

			handlePresidentialTransfer (aPlayer, aBuyStockAction, tShareCompany, tCurrentPresident);

			// Only want to change the Bought Share, and Primary Action State only if bought
			// during the Stock Round
			// (For an Auction, it does not change the player's Primary State)
			// (For a Operating Round, should not be here, since it is the Corporation that
			// is buying the Private, not a Player)
			if (STOCK_BUY_IN.StockRound.equals (aRoundBuying)) {
				tCompanyAbbrev = tCertificateToBuy.getCompanyAbbrev ();
				tPriorBoughtShare = aPlayer.boughtShare ();
				aPlayer.setBoughtShare (tCompanyAbbrev);
				aBuyStockAction.addBoughtShareEffect (aPlayer, tCompanyAbbrev, tPriorBoughtShare);
				tNewState = aPlayer.getPrimaryActionState ();
				aBuyStockAction.addStateChangeEffect (aPlayer, tOldState, tNewState);
				stockRound.updateRFPlayerLabel (aPlayer);
			}
			tBuyStockAction = aBuyStockAction;
			tBuyStockAction.setChainToPrevious (tChainToPrevious);
		} else {
			tBuyStockAction = BuyStockAction.NO_BUY_STOCK_ACTION;
		}
		aPlayer.updatePlayerInfo ();

		return tBuyStockAction;
	}

//	private SetParValueEffect buildSetParPriceEffect (Player aPlayer, Certificate aCertificate, 
//			ShareCompany aShareCompany, int aParPrice) {
//		SetParValueEffect tSetParValueEffect;
//
//		gameManager.setParPrice (aShareCompany, aParPrice);
//		parPriceFrame = buildParPriceFrame (aPlayer, aCertificate);
//		parPriceFrame.setParPriceFrameActive (false);
//		tSetParValueEffect = parPriceFrame.buildSetParValueEffect (aParPrice, aShareCompany);
//
//		return tSetParValueEffect;
//	}
	
	public void setFreeCertificatePrice (Certificate aFreeCertificate, Player aPlayer, 
				BuyStockAction aBuyStockAction) {
		ShareCompany tShareCompany;
		int tParPrice;
		
		/*
		 * If this Free Certificate is a President Share -- 
		 * Request a Par Price to be set
		 */
		if (aFreeCertificate.isPresidentShare ()) {
			if (! aFreeCertificate.hasParPrice ()) {
				handleBuildParPriceFrame (aPlayer, aFreeCertificate);
			} else {
				tShareCompany = aFreeCertificate.getShareCompany ();
				tParPrice = aFreeCertificate.getParPrice ();
				gameManager.setParPrice (tShareCompany, tParPrice);
				aBuyStockAction.addSetParValueEffect (aPlayer, tShareCompany, tParPrice);
			}
		}
	}
	
	private void manageCashTransfer (Player aPlayer, List<Certificate> aCertificatesToBuy,
			BuyStockAction aBuyStockAction, Bank aBank, Portfolio aSourcePortfolio) {
		CashHolderI tPayCashTo;
		int tCashValue;
		Certificate tCertificate;

		tCashValue = calculateCashToBuy (aCertificatesToBuy);
		tCertificate = aCertificatesToBuy.get (0);
		tPayCashTo = getPayCashTo (aBank, tCertificate, aSourcePortfolio);
		aPlayer.transferCashTo (tPayCashTo, tCashValue);
		aBuyStockAction.addCashTransferEffect (aPlayer, tPayCashTo, tCashValue);
	}

	public CashHolderI getPayCashTo (Bank aBank, Certificate aCertificate, Portfolio aSourcePortfolio) {
		CashHolderI tPayCashTo;
		int tCapitalizationLevel;
		int tSharesSold;
		ShareCompany tShareCompany;
		
		tShareCompany = aCertificate.getShareCompany ();
		if (aSourcePortfolio.isABankPool ()) {
			tPayCashTo = aBank;
		} else if (aCertificate.isAPrivateCompany ()) {
			tPayCashTo = aBank;
		} else if (aCertificate.isAMinorCompany ()) {
			tPayCashTo = aBank;
		} else if (! tShareCompany.hasFloated ()) {
			tPayCashTo = aBank;
		} else if (! tShareCompany.hasDestination ()) {
			tPayCashTo = aBank;
		} else if (tShareCompany.hasReachedDestination ()) {
			tCapitalizationLevel = getCapitalizationLevel (tShareCompany);
			if (tCapitalizationLevel == Capitalization.INCREMENTAL_10_MAX) {
				tPayCashTo = aBank;
			} else {
				tPayCashTo = tShareCompany;
			}
		} else {
			tSharesSold = tShareCompany.getPercentOwned ()/10 + 1;
			tCapitalizationLevel = getCapitalizationLevel (tShareCompany);
			if (tCapitalizationLevel == Capitalization.INCREMENTAL_10_MAX) {
				tPayCashTo = aBank;
			} else if (tCapitalizationLevel < tSharesSold){
				tPayCashTo = aBank;
			} else {
				tPayCashTo = tShareCompany;
			}
		}
		
		return tPayCashTo;
	}
	
	private int getCapitalizationLevel (ShareCompany aShareCompany) {
		int tCapitalizationLevel;
		int tSharesSold;

		tSharesSold = aShareCompany.getPercentOwned ()/10 + 1;
		tCapitalizationLevel = gameManager.getCapitalizationLevel (tSharesSold);

		return tCapitalizationLevel;
	}
	
	private Portfolio getSourcePortfolio (Certificate aCertificateToBuy) {
		Portfolio tSourcePortfolio;
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio;
		Portfolio tBankPoolPortfolio;

		tBank = getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tBankPool = stockRound.getBankPool ();
		tBankPoolPortfolio = tBankPool.getPortfolio ();

		if (tBank.isInStartPacket (aCertificateToBuy)) {
			tSourcePortfolio = tBank.getStartPacketPortfolio ();
		} else if (aCertificateToBuy.isOwnedByBank ()) {
			tSourcePortfolio = tBankPortfolio;
		} else {
			tSourcePortfolio = tBankPoolPortfolio;
		}
		// TODO: non-1830, need to check if certificate is being bought from another
		// player so a different source

		return tSourcePortfolio;
	}

	private int calculateCashToBuy (List<Certificate> aCertificatesToBuy) {
		int tCashToBuy;
		Bank tBank;

		tBank = getBank ();
		tCashToBuy = 0;
		for (Certificate tCertificateToBuy : aCertificatesToBuy) {
			if (tBank.isInStartPacket (tCertificateToBuy)) {
				tCashToBuy += tCertificateToBuy.getParValue ();
			} else if (tCertificateToBuy.isOwnedByBank ()) {
				tCashToBuy += tCertificateToBuy.getParValue ();
			} else {
				tCashToBuy += tCertificateToBuy.getValue ();
			}
		}

		return tCashToBuy;
	}

	public void handlePresidentialTransfer (Player aPlayer, TransferOwnershipAction aTransferOwnershipAction,
			TrainCompany aShareCompany, Player aCurrentPresident) {
		Player tNewPresident;

		// If we have a Current President, and the Current Player is Not the President,
		// Check to see if the Current Player now owns more and we have to Exchange
		// President Share

		if ((aCurrentPresident != Player.NO_PLAYER) && (aCurrentPresident != aPlayer)) {
			tNewPresident = findNewPresident (aShareCompany, aPlayer, aCurrentPresident);
			if ((tNewPresident != aCurrentPresident) && (tNewPresident != Player.NO_PLAYER)) {
				exchangePresidentCertificate (aShareCompany, aCurrentPresident, tNewPresident,
						aTransferOwnershipAction);
			}
		}
	}

	public void handlePresidentialTransfer (TransferOwnershipAction aTransferOwnershipAction,
			TrainCompany aShareCompany, Player aCurrentPresident) {
		Player tNewPresident;
		String tNotification;
		
		if (aCurrentPresident != Player.NO_PLAYER) {
			tNewPresident = findNewPresident (aShareCompany,aCurrentPresident);
			if ((tNewPresident != aCurrentPresident) && (tNewPresident != Player.NO_PLAYER)) {
				exchangePresidentCertificate (aShareCompany, aCurrentPresident, tNewPresident,
						aTransferOwnershipAction);
				tNotification = "New President of " + aShareCompany.getAbbrev () + " is " 
						+ tNewPresident.getName ();
				aTransferOwnershipAction.addSetNotificationEffect (aCurrentPresident, tNotification);
			}
		}
	}

	public Player findNewPresident (TrainCompany aShareCompany, Player aCurrentPresident) {
		Player tNewPresident;
		Player tNextPlayer;
		int tCurrentPlayerIndex;
		int tNextPlayerIndex;
		int tCurrentMaxPercentage;
		int tNextPercentOwned;
		int tCurrentPresidentPercentage;

		tCurrentPresidentPercentage = aCurrentPresident.getPercentOwnedOf (aShareCompany);
		tCurrentMaxPercentage = tCurrentPresidentPercentage;
		tNewPresident = Player.NO_PLAYER;
		
		tCurrentPlayerIndex = getPlayerIndex (aCurrentPresident);
		tNextPlayerIndex = getNextPlayerIndex (tCurrentPlayerIndex);
		while (tNextPlayerIndex != tCurrentPlayerIndex) {
			tNextPlayer = getPlayer (tNextPlayerIndex);
			tNextPercentOwned = tNextPlayer.getPercentOwnedOf (aShareCompany);
			if (tNextPercentOwned > tCurrentMaxPercentage) {
				tCurrentMaxPercentage = tNextPercentOwned;
				tNewPresident = tNextPlayer;
			}
			tNextPlayerIndex = getNextPlayerIndex (tNextPlayerIndex);
		}

		return tNewPresident;

	}
	
	public Player findNewPresident (TrainCompany aShareCompany, Player aCurrentPlayer, Player aCurrentPresident) {
		Player tNewPresident;
		Player tNextPlayer;
		int tCurrentPlayerIndex;
		int tNextPlayerIndex;
		int tCurrentMaxPercentage;
		int tNextPercentOwned;
		int tCurrentPresidentPercentage;

		tCurrentPresidentPercentage = aCurrentPresident.getPercentOwnedOf (aShareCompany);

		tNewPresident = Player.NO_PLAYER;
		tCurrentMaxPercentage = aCurrentPlayer.getPercentOwnedOf (aShareCompany);
		if (tCurrentMaxPercentage > tCurrentPresidentPercentage) {
			tNewPresident = aCurrentPlayer;
		} else {
			tCurrentPlayerIndex = getPlayerIndex (aCurrentPlayer);
			tNextPlayerIndex = getNextPlayerIndex (tCurrentPlayerIndex);
			while (tNextPlayerIndex != tCurrentPlayerIndex) {
				tNextPlayer = getPlayer (tNextPlayerIndex);
				tNextPercentOwned = tNextPlayer.getPercentOwnedOf (aShareCompany);
				if (tNextPercentOwned > tCurrentMaxPercentage) {
					tCurrentMaxPercentage = tNextPercentOwned;
					tNewPresident = tNextPlayer;
				}
				tNextPlayerIndex = getNextPlayerIndex (tNextPlayerIndex);
			}
		}

		return tNewPresident;
	}

	public ParPriceFrame buildParPriceFrame (Player aPlayer, Certificate aCertificate) {
		PlayerFrame tPlayerFrame;
		ParPriceFrame tParPriceFrame;

		tPlayerFrame = aPlayer.getPlayerFrame ();
		tParPriceFrame = new ParPriceFrame (tPlayerFrame, aPlayer, stockRound, aCertificate);

		return tParPriceFrame;
	}

//	private SetParValueEffect buildSetParPriceEffect (Player aPlayer, Certificate aCertificate, 
//			ShareCompany aShareCompany, int aParPrice) {
//		SetParValueEffect tSetParValueEffect;
//		
//		gameManager.setParPrice (aShareCompany, aParPrice);
//		parPriceFrame = buildParPriceFrame (aPlayer, aCertificate);
//		parPriceFrame.setParPriceFrameActive (false);
//		tSetParValueEffect = parPriceFrame.buildSetParValueEffect (aParPrice, aShareCompany);
//		
//		return tSetParValueEffect;
//	}

	private void handleBuildParPriceFrame (Player aPlayer, Certificate aCertificate) {
		PlayerFrame tPlayerFrame;

		parPriceFrame = buildParPriceFrame (aPlayer, aCertificate);
		parPriceFrame.setVisible (true);
		parPriceFrame.toFront ();
		tPlayerFrame = aPlayer.getPlayerFrame ();
		tPlayerFrame.setEnabled (false);
	}

	public void addAction (Action aAction) {
		if (aAction != Action.NO_ACTION) {
			stockRound.addAction (aAction);
		}
	}

	public String getPrivateAbbrevToAuction () {
		return gameManager.getPrivateAbbrevForAuction ();
	}

	private void doFinalShareBuySteps (Portfolio aToPortfolio, Portfolio aFromPortfolio,
			List<Certificate> aCertificatesToBuy, BuyStockAction aBuyStockAction) {
		ActorI.ActionStates tOldCorporationStatus;
		ActorI.ActionStates tNewCorporationStatus;

		for (Certificate tCertificate : aCertificatesToBuy) {
			transferOneCertificate (aToPortfolio, aFromPortfolio, tCertificate, aBuyStockAction);

			// Note, when Buying a Private, need to make the CheckBox invisible so it is not
			// added to the Explain List
			// Undo makes this visible again always.
			// If this is done for all Certs, especially Share Companies, the first will be
			// shown, but follow-on certs don't
			// show the Certificate Buy Button.
			// Should come up with a better way to fix this
			if (tCertificate.isAPrivateCompany ()) {
				tCertificate.hideFrameButton ();
			}
			tOldCorporationStatus = tCertificate.getCorporationStatus ();
			tCertificate.updateCorporationOwnership ();
			tNewCorporationStatus = tCertificate.getCorporationStatus ();
			if (tOldCorporationStatus != tNewCorporationStatus) {
				aBuyStockAction.addChangeCorporationStatusEffect (tCertificate.getCorporation (),
						tOldCorporationStatus, tNewCorporationStatus);
			}
		}
	}

	public void transferOneCertificate (Portfolio aToPortfolio, Portfolio aFromPortfolio, 
			Certificate aCertificate, BuyStockAction aBuyStockAction) {
		PortfolioHolderI tFromHolder;
		PortfolioHolderI tToHolder;
		boolean tTransferGood;
		StartPacketPortfolio tStartPacketPortfolio;

		tTransferGood = aToPortfolio.transferOneCertificateOwnership (aFromPortfolio, aCertificate);
		if (tTransferGood) {
			tFromHolder = aFromPortfolio.getHolder ();
			tToHolder = aToPortfolio.getHolder ();
			aBuyStockAction.addTransferOwnershipEffect (tFromHolder, aCertificate, tToHolder);
			
			if (aFromPortfolio instanceof StartPacketPortfolio) {
				tStartPacketPortfolio = (StartPacketPortfolio) aFromPortfolio;
				tStartPacketPortfolio.setCertificateFromStartPacketAvailability (aCertificate, aBuyStockAction);
			}
		}
	}

	public int getThisPlayerIndex (Player aPlayer) {
		int tThisPlayerIndex;
		int tPlayerIndex;
		Player tThisPlayer;

		tThisPlayerIndex = NO_PLAYER_INDEX;
		for (tPlayerIndex = 0; tPlayerIndex < players.size (); tPlayerIndex++) {
			tThisPlayer = players.get (tPlayerIndex);
			if (tThisPlayer.equals (aPlayer)) {
				tThisPlayerIndex = tPlayerIndex;
			}
		}

		return tThisPlayerIndex;
	}

	public int getPriorityPlayerIndex () {
		return stockRound.getPriorityIndex ();
	}
	
	public void doneAction (Player aPlayer) {
		int tNextPlayerIndex;
		int tCurrentPlayerIndex;
		int tOldPriorityPlayerIndex;
		int tThisPlayerIndex;
		Player tOldPriorityPlayer;
		DonePlayerAction tDonePlayerAction;

		tCurrentPlayerIndex = stockRound.getCurrentPlayerIndex ();
		tThisPlayerIndex = getThisPlayerIndex (aPlayer);
		if (tThisPlayerIndex != tCurrentPlayerIndex) {
			System.err.println (
					"----- CurrentPlayerIndex is " + tCurrentPlayerIndex + " This Player Index " 
					+ tThisPlayerIndex);
			stockRound.setCurrentPlayer (tThisPlayerIndex, true);
			tCurrentPlayerIndex = tThisPlayerIndex;
		}
		tNextPlayerIndex = stockRound.getNextPlayerIndex ();
		tOldPriorityPlayerIndex = getPriorityPlayerIndex ();
		tOldPriorityPlayer = getPlayer (tOldPriorityPlayerIndex);
		
		tDonePlayerAction = new DonePlayerAction (stockRound.getRoundState (), stockRound.getID (), aPlayer);
		tDonePlayerAction.addNewPriorityPlayerEffect (aPlayer, tOldPriorityPlayerIndex, tNextPlayerIndex);

		// If this Player Stock Action include exchanging out the President Share, clear
		// it, and add the Effect.
		if (aPlayer.hasExchangedShare () != Player.NO_STOCK_TO_SELL) {
			aPlayer.setExchangedPrezShare (Player.NO_STOCK_TO_SELL);
			tDonePlayerAction.addExchangePrezShareEffect (Player.NO_STOCK_TO_SELL);
		}

		aPlayer.updatePortfolioInfo ();
		stockRound.setPriorityPlayerIndex (tNextPlayerIndex);
		stockRound.updateRFPlayerLabel (tOldPriorityPlayer);
		moveToNextPlayer (tNextPlayerIndex, tDonePlayerAction);
		
		addAction (tDonePlayerAction);
		gameManager.resetRoundFrameBackgrounds ();
	}

	private void moveToNextPlayer (int aNextPlayerIndex, ChangeStateAction aChangeStateAction) {
		Player tNextPlayer;
		Player tCurrentPlayer;
		boolean tBidShare;
		int tCurrentPlayerIndex;
		
		tCurrentPlayerIndex = stockRound.getCurrentPlayerIndex ();
		tCurrentPlayer = getCurrentPlayer ();
		aChangeStateAction.addNewCurrentPlayerEffect (tCurrentPlayer, tCurrentPlayerIndex, aNextPlayerIndex);
		tNextPlayer = getPlayer (aNextPlayerIndex);
		
		tNextPlayer.setBoughtShare (Player.NO_SHARE_BOUGHT);
		aChangeStateAction.addBoughtShareEffect (tNextPlayer, Player.NO_SHARE_BOUGHT, tNextPlayer.boughtShare ());
		tBidShare = false;
		tNextPlayer.setBidShare (tBidShare);
		aChangeStateAction.addBidShareEffect (tNextPlayer, tBidShare);
		
		tNextPlayer.updatePortfolioInfo ();
		stockRound.setCurrentPlayer (aNextPlayerIndex, true, aChangeStateAction);
		stockRound.updateRFPlayerLabel (tNextPlayer);
	}

	public void exchangeAction (Player aPlayer) {
		Certificate tCertificate;

		tCertificate = aPlayer.getCertificateToExchange ();
		exchangeCertificate (aPlayer, tCertificate);
	}

	public void exchangeCertificate (Player aPlayer, Certificate aCertificate) {
		Player tNewPresident;
		Corporation tCorporation;
		ExchangeStockAction tExchangeStockAction;
		String tCorporationAbbrev;

		if (aCertificate != Certificate.NO_CERTIFICATE) {
			tCorporation = aCertificate.getCorporation ();
			if (tCorporation.isAShareCompany ()) {
				aPlayer.acts (); // Simply set the fact that the Player has acted. This does not require that he
									// has not
				tCorporationAbbrev = tCorporation.getAbbrev ();
				tNewPresident = findPlayerWithMost ((TrainCompany) tCorporation, aPlayer);
				aPlayer.setExchangedPrezShare (tCorporationAbbrev);

				tExchangeStockAction = new ExchangeStockAction (stockRound.getRoundState (), stockRound.getID (),
						aPlayer);
				if (gameManager.isOperatingRound ()) {
					tExchangeStockAction.setChainToPrevious (true);
				}
				tExchangeStockAction.addExchangePrezShareEffect (tCorporationAbbrev);
				exchangePresidentCertificate ((TrainCompany) tCorporation, aPlayer, tNewPresident,
						tExchangeStockAction);
				addAction (tExchangeStockAction);
			} else if (tCorporation.isAPrivateCompany ()) {
				handlePrivateExchange (aPlayer, aCertificate, tCorporation);
			} else {
				System.err.println ("Ready to Exchange a Minor Company for a Major");
			}
			aPlayer.updatePlayerInfo ();
		} else {
			System.err.println ("No Certificate selected to Exchange");
		}
	}

	private void handlePrivateExchange (Player aPlayer, Certificate aCertificate, Corporation aCorporation) {
		Certificate tCertificate;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		ExchangeStockAction tExchangeStockAction;
		PrivateCompany tPrivateCompany;
		Portfolio tBankPortfolio;
		Portfolio tPlayerPortfolio;
		Portfolio tClosedPortfolio;
		PortfolioHolderI tPortfolioHolder;
		Bank tBank;
		ActorI.ActionStates tOldCorporationStatus;
		ActorI.ActionStates tNewCorporationStatus;
		Player tCurrentPresident;
		ShareCompany tShareCompany;
		int tExchangeID;
		int tExchangePercentage;

		tPrivateCompany = (PrivateCompany) aCorporation;
		tExchangeID = tPrivateCompany.getExchangeID ();
		tExchangePercentage = tPrivateCompany.getExchangePercentage ();
		tShareCompany = (ShareCompany) gameManager.getCorporationByID (tExchangeID);
		tBank = stockRound.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tCertificate = tBankPortfolio.getCertificate (tShareCompany, tExchangePercentage);
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tPlayerPortfolio = aPlayer.getPortfolio ();
			tClosedPortfolio = tBank.getClosedPortfolio ();
			tExchangeStockAction = new ExchangeStockAction (stockRound.getRoundState (), 
							stockRound.getID (), aPlayer);
			tClosedPortfolio.transferOneCertificateOwnership (tPlayerPortfolio, aCertificate);
			tExchangeStockAction.addTransferOwnershipEffect (aPlayer, aCertificate, tBank);
			tPlayerPortfolio.transferOneCertificateOwnership (tBankPortfolio, tCertificate);
			tExchangeStockAction.addTransferOwnershipEffect (tBank, tCertificate, aPlayer);
			tOldState = tPrivateCompany.getActionStatus ();
			tPrivateCompany.close ();
			tNewState = tPrivateCompany.getActionStatus ();
			tExchangeStockAction.addCloseCorporationEffect (tPrivateCompany, tOldState, tNewState);
			tOldCorporationStatus = tCertificate.getCorporationStatus ();
			tCertificate.updateCorporationOwnership ();
			tNewCorporationStatus = tCertificate.getCorporationStatus ();
			if (tOldCorporationStatus != tNewCorporationStatus) {
				tExchangeStockAction.addChangeCorporationStatusEffect (tCertificate.getCorporation (),
						tOldCorporationStatus, tNewCorporationStatus);
			}
			tPortfolioHolder = tShareCompany.getPresident ();
			if (tPortfolioHolder != PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
				if (tPortfolioHolder.isAPlayer ()) {
					tCurrentPresident = (Player) tPortfolioHolder;
					handlePresidentialTransfer (aPlayer, tExchangeStockAction, tShareCompany, tCurrentPresident);
				}
			}
			addAction (tExchangeStockAction);
		}
	}

	public Player findPlayerWithMost (TrainCompany aTrainCompany, Player aCurrentPlayer) {
		Player tNewPresident;
		Player tNextPlayer;
		int tCurrentPlayerIndex;
		int tNextPlayerIndex;
		int tCurrentMaxPercentage;
		int tNextPercentOwned;

		tNewPresident = Player.NO_PLAYER;
		tCurrentMaxPercentage = 0;
		tCurrentPlayerIndex = getPlayerIndex (aCurrentPlayer);
		tNextPlayerIndex = getNextPlayerIndex (tCurrentPlayerIndex);
		while (tNextPlayerIndex != tCurrentPlayerIndex) {
			tNextPlayer = getPlayer (tNextPlayerIndex);
			tNextPercentOwned = tNextPlayer.getPercentOwnedOf (aTrainCompany);
			if (tNextPercentOwned > tCurrentMaxPercentage) {
				tCurrentMaxPercentage = tNextPercentOwned;
				tNewPresident = tNextPlayer;
			}
			tNextPlayerIndex = getNextPlayerIndex (tNextPlayerIndex);
		}

		return tNewPresident;
	}

	public ActorI getActor (String aActorName) {
		ActorI tActor;
		String tPlayerName;

		tActor = ActorI.NO_ACTOR;
		for (Player tPlayer : players) {
			if (tActor == ActorI.NO_ACTOR) {
				tPlayerName = tPlayer.getName ();
				if (aActorName.equals (tPlayerName)) {
					tActor = tPlayer;
				} else {
					// If the Actor's Name ends with this Player's Name with closing Square Bracket,
					// it could be '[#) Escrow for <PlayerName>]'
					// Ask the Player to fetch the matching name (if it exists)
					if (aActorName.endsWith (tPlayerName + "]")) {
						tActor = tPlayer.getMatchingEscrow (aActorName);
						// If the Player does not have the Escrow, it needs to be created and added to
						// the Player
						if (tActor == ActorI.NO_ACTOR) {
							tActor = tPlayer.addEmptyEscrow (aActorName);
						}
					}
				}

			}
		}

		return tActor;
	}

	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;

		tCurrentHolder = gameManager.getCurrentHolder (aLoadedCertificate);

		return tCurrentHolder;
	}

	public Escrow getEscrowMatching (String aEscrowName) {
		Escrow tEscrow;
		Escrow tFoundEscrow;

		tEscrow = Escrow.NO_ESCROW;
		for (Player tPlayer : players) {
			tFoundEscrow = tPlayer.getEscrowMatching (aEscrowName);
			if (tFoundEscrow != Escrow.NO_ESCROW) {
				tEscrow = tFoundEscrow;
			}
		}

		return tEscrow;
	}

	public ShareCompany getShareCompany (String aCompanyAbbrev) {
		return gameManager.getShareCompany (aCompanyAbbrev);
	}

	public Player getPresident (Corporation aCorporation) {
		return (Player) aCorporation.getPresident ();
	}

	public boolean hasMustBuyCertificate () {
		Bank tBank;
		
		tBank = getBank ();
		
		return tBank.hasMustBuyCertificate ();
	}

	public boolean loadPlayers (XMLNode aPlayersNode, GameInfo aActiveGame) {
		XMLNodeList tXMLNodeList;
		boolean tPlayersLoaded;
		int tPlayerCount;
		PlayerInputFrame tPlayerInputFrame;

		tXMLNodeList = new XMLNodeList (playerParsingRoutine, aActiveGame);
		tXMLNodeList.parseXMLNodeList (aPlayersNode, PlayerInputFrame.EN_PLAYER);
		tPlayerCount = getPlayerCount ();
		if (tPlayerCount > 1) {
			tPlayerInputFrame = gameManager.getPlayerInputFrame ();
			tPlayerInputFrame.removeAllPlayers ();
			tPlayersLoaded = true;
		} else {
			tPlayersLoaded = false;
		}

		return tPlayersLoaded;
	}

	public void setCertificateLimit (GameInfo aActiveGame, int aPlayerCount) {
		int tCertificateLimit;

		tCertificateLimit = aActiveGame.getCertificateLimit (aPlayerCount);
		for (Player tPlayer : players) {
			tPlayer.setCertificateLimit (tCertificateLimit);
		}
	}

	ParsingRoutineI playerParsingRoutine = new ParsingRoutineIO () {

		@Override
		public void foundItemMatchKey1 (XMLNode aPlayerNode, Object aGameInfo) {
			String tPlayerName;

			tPlayerName = aPlayerNode.getThisAttribute (Player.AN_NAME);
			addPlayer (tPlayerName, CERTIFICATE_LIMIT_ZERO);
		}
	};

	public boolean loadPlayerStates (XMLNode aPlayerStatesNodes) {
		XMLNodeList tXMLNodeList;
		boolean tPlayerStatesLoaded;

		tPlayerStatesLoaded = true;
		tXMLNodeList = new XMLNodeList (playerStateParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aPlayerStatesNodes, PlayerInputFrame.EN_PLAYER);

		return tPlayerStatesLoaded;
	}

	ParsingRoutineI playerStateParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aPlayerNode) {
			String tPlayerName;
			Player tPlayer;

			tPlayerName = aPlayerNode.getThisAttribute (Player.AN_NAME);
			tPlayer = (Player) getActor (tPlayerName);
			if (tPlayer != Player.NO_PLAYER) {
				tPlayer.loadState (aPlayerNode);
			}
		}
	};

	public void passAction (Player aPlayer) {
		PassAction tPassAction;
		Player.ActionStates tOldState;
		Player.ActionStates tNewState;
		boolean tHaveAllPassed;
		int tNextPlayerIndex;

		tPassAction = new PassAction (stockRound.getRoundState (), stockRound.getID (), aPlayer);
		// Get State before acting for saving in the Action Stack.
		tOldState = aPlayer.getPrimaryActionState ();

		if (aPlayer.passes ()) {
			tNewState = aPlayer.getPrimaryActionState ();
			aPlayer.updatePortfolioInfo ();

			tNextPlayerIndex = stockRound.getNextPlayerIndex ();
			
			tPassAction.addStateChangeEffect (aPlayer, tOldState, tNewState);
			stockRound.updateRFPlayerLabel (aPlayer);
			
			tHaveAllPassed = haveAllPassed ();
			if (! tHaveAllPassed) {
				moveToNextPlayer (tNextPlayerIndex, tPassAction);
			}
			addAction (tPassAction);
		} else {
			System.err.println ("Player has acted in this Stock Round, cannot Pass");
		}
	}

	public void applyDiscountIfMustSell (Round aRound, ChangeStateAction aChangeStateAction) {
		Certificate tCertificate;
		boolean tMustSell;
		int tOldDiscount;
		int tNewDiscount;
		String tCompanyName;
		Bank tBank;
		
		tBank = getBank ();
		tMustSell = tBank.hasMustSell ();
		if (tMustSell) {
			tCertificate = tBank.getMustSellCertificate ();
			tCompanyName = tCertificate.getCompanyAbbrev ();
			tOldDiscount = tCertificate.getDiscount ();
			tBank.applyDiscount ();
			tNewDiscount = tCertificate.getDiscount ();
			aChangeStateAction.addApplyDiscountEffect (aRound, tCompanyName, tOldDiscount, tNewDiscount);
		}
	}

	public void resetRoundFrameBackgrounds () {
		gameManager.resetRoundFrameBackgrounds ();
	}

	public void passAuctionAction (Player aPlayer) {
		int tNextPlayerIndex;

		if (aPlayer.canPassAuction ()) {
			tNextPlayerIndex = stockRound.getNextPlayerIndex ();
			stockRound.setCurrentPlayer (tNextPlayerIndex, false);
		} else {
			System.err.println ("Player has acted in this Auction Round, cannot pass");
		}
	}

	public void printAllPlayersInfo () {
		System.out.println ("==== All Players Information STARTED ====");	// PRINTLOG
		for (Player tPlayer : players) {
			tPlayer.printPlayerInfo ();
		}
		System.out.println ("==== All Players Information FINISHED ====");
	}

	public boolean isOperatingRound () {
		return gameManager.isOperatingRound ();
	}

	public void sellAction (Player aPlayer) {
		SellStockAction tSellStockAction;
		Player.ActionStates tOldState;
		Player.ActionStates tNewState;
		List<Certificate> tCertificatesToSell;
		Portfolio tBankPoolPortfolio;
		Portfolio tPlayerPortfolio;
		Bank tBank;
		BankPool tBankPool;
		MarketCell tMarketCell;
		MarketCell tNewMarketCell;
		MarketCell tStartMarketCell;
		Token tToken;
		ShareCompany tShareCompany;
		ShareCompany tCompanyBeingSold;
		String tCompanyAbbrev;
		String tExchangedShare;
		Player tCurrentPresident;
		Player tNewPresident;
		int tCashValue;
		int tSharesBeingSold;
		int tStartLocation;
		int tNewLocation;
		boolean tForceSell;
		boolean tNormalSale;

		// Get State before acting for saving in the Action Stack.
		tOldState = aPlayer.getPrimaryActionState ();
		if (isOperatingRound ()) {
			tForceSell = true;
			tNormalSale = false;
		} else if (aPlayer.sells ()) {
			tForceSell = false;
			tNormalSale = true;
		} else {
			tForceSell = false;
			tNormalSale = false;
		}

		if (tForceSell || tNormalSale) {
			tSellStockAction = new SellStockAction (stockRound.getRoundState (), stockRound.getID (), aPlayer);
			if (tForceSell) {
				aPlayer.setPrimaryActionState (tOldState);
				tSellStockAction.setChainToPrevious (true);
			}
			tNewState = aPlayer.getPrimaryActionState ();
			tCertificatesToSell = aPlayer.getCertificatesToSell ();

			tCashValue = 0;

			// Go through all Certificates to get total value before transferring or
			// adjusting stock price
			for (Certificate tCertificate : tCertificatesToSell) {
				tCashValue += tCertificate.getValue ();
			}
			tSharesBeingSold = tCertificatesToSell.size ();

			tBank = stockRound.getBank ();
			tBank.transferCashTo (aPlayer, tCashValue);
			tSellStockAction.addCashTransferEffect (tBank, aPlayer, tCashValue);

			tBankPool = stockRound.getBankPool ();
			tBankPoolPortfolio = tBankPool.getPortfolio ();
			tPlayerPortfolio = aPlayer.getPortfolio ();

			// This time transfer the Ownership of the stock certificates to the Bank
			// Portfolio, and adjust the Stock Price
			tStartMarketCell = MarketCell.NO_MARKET_CELL;
			tShareCompany = (ShareCompany) ActorI.NO_ACTOR;
			tStartLocation = TokenStack.NO_STACK_LOCATION;
			tNewLocation = TokenStack.NO_STACK_LOCATION;
			tNewMarketCell = tStartMarketCell;
			tCurrentPresident = Player.NO_PLAYER;
			tCompanyBeingSold = ShareCompany.NO_SHARE_COMPANY;
			for (Certificate tCertificate : tCertificatesToSell) {
				tShareCompany = (ShareCompany) tCertificate.getCorporation ();
				tCompanyBeingSold = tShareCompany;
				tCompanyAbbrev = tShareCompany.getAbbrev ();
				// Haven't found the President yet, so find the current President -- if known.
				if (tCurrentPresident == Player.NO_PLAYER) {
					tCurrentPresident = (Player) tShareCompany.getPresident ();
				}
				tMarketCell = tShareCompany.getSharePriceMarketCell ();
				if (tMarketCell != MarketCell.NO_MARKET_CELL) {
					// Save the Start Market Cell and Start Location (within token stack) of where
					// the Price is before changing anything
					if (tStartMarketCell == MarketCell.NO_MARKET_CELL) {
						tStartMarketCell = tMarketCell;
						tStartLocation = tStartMarketCell.getTokenLocation (tCompanyAbbrev);
					}
					tNewMarketCell = tMarketCell.getSellShareMarketCell (tSharesBeingSold);
					if (tMarketCell != tNewMarketCell) {
						tShareCompany.setSharePrice (tNewMarketCell);
						tToken = tMarketCell.getToken (tCompanyAbbrev);
						if (tToken != Token.NO_TOKEN) {
							tNewMarketCell.addTokenToBottom (tToken);
						}
						tNewMarketCell.redrawMarket ();
					}
					tNewLocation = tNewMarketCell.getTokenLocation (tCompanyAbbrev);
				}
				tBankPoolPortfolio.transferOneCertificateOwnership (tPlayerPortfolio, tCertificate);
				tSellStockAction.addTransferOwnershipEffect (aPlayer, tCertificate, tBankPool);
				if (tNormalSale) {
					aPlayer.addSoldCompanies (tCompanyAbbrev);
					tSellStockAction.addSoldCompanyEffect (aPlayer, tCompanyAbbrev);
				}
			}
			if (tCurrentPresident == aPlayer) {
				tNewPresident = findNewPresident (tShareCompany, aPlayer, tCurrentPresident);
				if ((tNewPresident != tCurrentPresident) && (tNewPresident != Player.NO_PLAYER)) {
					exchangePresidentCertificate (tShareCompany, tCurrentPresident, tNewPresident,
							tSellStockAction);
				}
			}
			tSellStockAction.addChangeMarketCellEffect (tShareCompany, tStartMarketCell, 
					tStartLocation, tNewMarketCell, tNewLocation);
			if (tNormalSale) {
				tSellStockAction.addStateChangeEffect (aPlayer, tOldState, tNewState);
			}
			tExchangedShare = aPlayer.hasExchangedShare ();
			if (tExchangedShare != Player.NO_STOCK_TO_SELL) {
				// Test if the current player has < shares than current President
				// If so, can clear Exchanged Share attribute.
				if (aPlayer.hasLessThanPresident (tExchangedShare)) {
					aPlayer.setExchangedPrezShare (Player.NO_STOCK_TO_SELL);
					tSellStockAction.addClearExchangePrezShareEffect (aPlayer, tExchangedShare);
				} else {
					System.err.println (aPlayer.getName () + " STILL owns more than the president");
				}
			}
			addAction (tSellStockAction);
			tCompanyBeingSold.handleCloseCorporation ();
			aPlayer.updatePlayerInfo ();
		}
	}
	
	public void exchangePresidentCertificate (TrainCompany aShareCompany, Player aOldPresident, 
			Player aNewPresident, TransferOwnershipAction aAction) {
		Certificate tPresidentCertificate;
		Certificate tCertificateOne;
		Portfolio tOldPresidentPortfolio;
		Portfolio tNewPresidentPortfolio;

		tOldPresidentPortfolio = aOldPresident.getPortfolio ();
		tNewPresidentPortfolio = aNewPresident.getPortfolio ();
		tPresidentCertificate = tOldPresidentPortfolio.getPresidentCertificate (aShareCompany);
		tCertificateOne = tNewPresidentPortfolio.getNonPresidentCertificate (aShareCompany);
		if (tCertificateOne.getPercentage () == tPresidentCertificate.getPercentage ()) {
			tNewPresidentPortfolio.transferOneCertificateOwnership (tOldPresidentPortfolio,
						tPresidentCertificate);
			aAction.addTransferOwnershipEffect (aOldPresident, tPresidentCertificate, aNewPresident);
			tOldPresidentPortfolio.transferOneCertificateOwnership (tNewPresidentPortfolio, tCertificateOne);
			aAction.addTransferOwnershipEffect (aNewPresident, tCertificateOne, aOldPresident);
		} else {
			tNewPresidentPortfolio.transferOneCertificateOwnership (tOldPresidentPortfolio,
						tPresidentCertificate);
			aAction.addTransferOwnershipEffect (aOldPresident, tPresidentCertificate, aNewPresident);
			tOldPresidentPortfolio.transferOneCertificateOwnership (tNewPresidentPortfolio, tCertificateOne);
			aAction.addTransferOwnershipEffect (aNewPresident, tCertificateOne, aOldPresident);
			tCertificateOne = tNewPresidentPortfolio.getNonPresidentCertificate (aShareCompany);
			tOldPresidentPortfolio.transferOneCertificateOwnership (tNewPresidentPortfolio, tCertificateOne);
			aAction.addTransferOwnershipEffect (aNewPresident, tCertificateOne, aOldPresident);
		}
	}

	public void removeAllEscrows () {
		for (Player tPlayer : players) {
			tPlayer.removeAllEscrows ();
		}
	}

	public void setStockRound (StockRound aStockRound) {
		stockRound = aStockRound;
	}

	public void setAuctionRound (AuctionRound aAuctionRound) {
		auctionRound = aAuctionRound;
	}

	public void hideAllPlayerFrames () {
		for (Player tPlayer : players) {
			tPlayer.hidePlayerFrame ();
		}
	}

	public void showPlayerFrame (int aPlayerIndex) {
		Player tPlayer;
		StartStockAction tStartStockAction;
		RoundManager tRoundManager;
		XMLFrame tXMLFrame;

		tPlayer = players.get (aPlayerIndex);
		if (! tPlayer.hasActed ()) {
			tStartStockAction = new StartStockAction (stockRound.getRoundState (), stockRound.getID (), tPlayer);
			tRoundManager = gameManager.getRoundManager ();
			tXMLFrame = tPlayer.getPlayerFrame ();
			tStartStockAction.addShowFrameEffect (stockRound, tXMLFrame);
			tStartStockAction.addStartStockEffect (tPlayer);
			tStartStockAction.setChainToPrevious (true);
			gameManager.addAction (tStartStockAction);
			tRoundManager.setPlayerDoingAction (true);
			tRoundManager.updateRoundFrame ();
		}
		tPlayer.showPlayerFrame ();
	}

	public void undoAction (Player aPlayer) {
		boolean tActionUndone;
//		Action tActionToUndo;
		Action tLastAction;
		Player tCurrentPlayer;

//		tActionToUndo = stockRound.getLastAction ();
		tActionUndone = stockRound.undoLastAction ();
		if (tActionUndone) {
			aPlayer.updatePlayerInfo ();
			tCurrentPlayer = getCurrentPlayer ();
			tCurrentPlayer.updatePlayerInfo ();
			tLastAction = stockRound.getLastAction ();
			if (tLastAction == Action.NO_ACTION) {
				tCurrentPlayer.hidePlayerFrame ();
			} else if (! gameManager.isNetworkGame ()) {
//				if (tActionToUndo instanceof StartStockAction) {
//					tCurrentPlayer.showPlayerFrame ();
//				}
			}
			gameManager.autoSaveGame (! GameManager.ADD_CHECKSUM);
		} else {
			System.err.println ("**** Undo Action failed ****");
		}
	}

	public void updateAllRFPlayerLabels () {
		for (Player tPlayer : players) {
			updateRFPlayerLabel (tPlayer);
		}
	}

	public void updateRFPlayerLabel (Player aPlayer) {
		stockRound.updateRFPlayerLabel (aPlayer);
	}

	public void updateAllPlayerFrames () {
		Player tCurrentPlayer = getCurrentPlayer ();

		tCurrentPlayer = getCurrentPlayer ();
		updateAllPlayerFrames (tCurrentPlayer);
	}

	public void bringPlayerFrameToFront () {
		Player tPlayer;

		tPlayer = getCurrentPlayer ();
		if (gameManager.isNetworkGame ()) {
			if (isNetworkAndIsThisClient (tPlayer.getName ())) {
				tPlayer.bringPlayerFrameToFront ();
			}
		} else {
			tPlayer.bringPlayerFrameToFront ();
		}
	}

	public void updateAllPlayerFrames (Player aCurrentPlayer) {
		for (Player tPlayer : players) {
			if (tPlayer != aCurrentPlayer) {
				tPlayer.updatePlayerInfo ();
			}
		}
		aCurrentPlayer.updatePlayerInfo ();
	}

	public void updateRoundWindow () {
		stockRound.updateStockRoundWindow ();
	}

	public boolean isParPriceFrameActive () {
		boolean tIsParPriceFrameActive = false;

		if (parPriceFrame != ParPriceFrame.NO_PAR_PRICE_FRAME) {
			tIsParPriceFrameActive = parPriceFrame.isParPriceFrameActive ();
		}

		return tIsParPriceFrameActive;
	}

	public boolean isNetworkGame () {
		return gameManager.isNetworkGame ();
	}

	public boolean isNetworkAndIsThisClient (String aPlayerName) {
		return gameManager.notIsNetworkAndIsThisClient (aPlayerName);
	}

	public String getClientUserName () {
		return gameManager.getClientUserName ();
	}

	public int getTotalPlayerCash () {
		int tTotalPlayerCash;

		tTotalPlayerCash = 0;
		for (Player tPlayer : players) {
			tTotalPlayerCash += tPlayer.getCash ();
		}

		return tTotalPlayerCash;
	}

	public Point getOffsetRoundFramePoint () {
		return gameManager.getOffsetRoundFrame ();
	}

	public boolean isAAuctionRound () {
		return gameManager.isAAuctionRound ();
	}

	public boolean isLastActionComplete () {
		return gameManager.isLastActionComplete ();
	}

	public boolean confirmBuyPresidentShare () {
		return gameManager.confirmBuyPresidentShare ();
	}

	public void fillPrivateCompanies (ButtonsInfoFrame aButtonsInfoFrame) {
		Portfolio tPortfolio;

		for (Player tPlayer : players) {
			tPortfolio = tPlayer.getPortfolio ();
			aButtonsInfoFrame.fillWithPrivateCheckBoxes (tPortfolio, tPlayer.getName ());
		}
	}
	
	public boolean allRepaymentsFinished () {
		boolean tAllRepaymentsFinished;
		
		tAllRepaymentsFinished = true;
		for (Player tPlayer : players) {
			tAllRepaymentsFinished = tAllRepaymentsFinished && tPlayer.getRepaymentFinished ();
		}
		
		return tAllRepaymentsFinished;
	}
	
	public void updateCertificateLimit (FormationRoundAction aFormationRoundAction) {
		int tOldCertificateLimit;
		int tNewCertificateLimit;
		Player tPlayerOne;
		
		tPlayerOne = players.get (0);
		tOldCertificateLimit = tPlayerOne.getCertificateLimit ();
		gameManager.updateCertificateLimit ();
		tNewCertificateLimit = tPlayerOne.getCertificateLimit ();
		for (Player tPlayer : players) {
			aFormationRoundAction.addUpdateCertificateLimitEffect (tPlayer, tOldCertificateLimit,
							tNewCertificateLimit);
		}
	}
}
