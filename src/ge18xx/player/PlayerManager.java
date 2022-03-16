package ge18xx.player;

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
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.market.MarketCell;
import ge18xx.round.AuctionRound;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BidStockAction;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.ChangeStateAction;
import ge18xx.round.action.DonePlayerAction;
import ge18xx.round.action.ExchangeStockAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.PassAction;
import ge18xx.round.action.SellStockAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.ParsingRoutineIO;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.util.List;
import java.awt.Point;
import java.util.LinkedList;

public class PlayerManager {
	final AttributeName AN_NAME = new AttributeName ("name");
	public static final int BID_INCREMENT = 5;
	public final static int NO_PLAYER_INDEX = -1;
	public final static String NO_PLAYER_NAME = null;
	public final static PlayerManager NO_PLAYER_MANAGER = null;
	public enum STOCK_BUY_IN { StockRound, AuctionRound, OperatingRound }; // Round a Stock Certificate was purchased
	public final static boolean AUCTION_BUY = false;
	public final static List<Player> players = new LinkedList<Player> ();
	private final static List<Player> NO_PLAYERS = null;
	GameManager gameManager;
	StockRound stockRound;
	AuctionRound auctionRound;
	ParPriceFrame parPriceFrame;
	
	public PlayerManager (GameManager aGameManager) {
		gameManager = aGameManager;
		setStockRound (StockRound.NO_STOCK_ROUND);
	}
	
	
	public void addPlayer (String aName, boolean aPrivates, boolean aCoals, boolean aMinors, boolean aShares, int aCertificateLimit) {
		Player tPlayer;
		
		tPlayer = new Player (aName, aPrivates, aCoals, aMinors, aShares, this, aCertificateLimit);
		addPlayer (tPlayer);
	}
	
	public void addPlayer (Player aPlayer) {
		if (! players.contains (aPlayer)) {
			players.add (aPlayer);
		}
	}

    public String getPlayersInOrder () {
    	String tPlayersInOrder = "";
 
    	for (Player tPlayer : players) {
    		if (tPlayersInOrder != "") {
    			tPlayersInOrder += ", ";
    		}
    		tPlayersInOrder += tPlayer.getName ();
    	}

    	return tPlayersInOrder;
    }

	public Player getPlayerWhoTriggeredAuction () {
		Player tPlayer = Player.NO_PLAYER;
		
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
		int tPercentOwned, tPresidentPercent;
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
	
	public void clearAllPlayerPasses () {
		Player.ActionStates tOldState, tNewState;
		ChangeStateAction tChangeStateAction;
		
		tChangeStateAction = new ChangeStateAction (stockRound.getRoundType (), stockRound.getID (), stockRound);
		for (Player tPlayer : players) {
			tOldState = tPlayer.getPrimaryActionState ();

			tPlayer.clearPrimaryActionState ();
			
			tNewState = tPlayer.getPrimaryActionState ();
			if (tChangeStateAction != ChangeStateAction.NO_CHANGE_STATE_ACTION) {
				tChangeStateAction.addStateChangeEffect (tPlayer, tOldState, tNewState);
			}
		}
		if (! gameManager.applyingAction ()) {
			tChangeStateAction.setChainToPrevious (true);
			addAction (tChangeStateAction);
		}
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
		Bank tBank = Bank.NO_BANK;
		
		if (stockRound != StockRound.NO_ROUND) {
			tBank = stockRound.getBank ();
		}
		
		return tBank;
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
		List<ActionStates> aAuctionStates = new LinkedList<ActionStates> ();
		ActorI.ActionStates tActionState;
		
		for (Player tPlayer : players) {
			tActionState = tPlayer.getAuctionActionState ();
			aAuctionStates.add (tActionState);
		}
		
		return aAuctionStates;
	}

	public void resetPlayerAuctionStates (List<ActionStates> aAuctionStates) {
		int tPlayerCount = players.size ();
		int tStateCount = aAuctionStates.size ();
		Player tPlayer;
		
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
				if (aIndex < getPlayerCount () ) {
					tPlayer = players.get (aIndex);
				}
			}
		}
		
		return tPlayer;
	}
	
	public int getPlayerCount () {
		return players.size ();
	}
	
	public XMLElement getPlayerElements (XMLDocument tXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tPlayerElement;
		
		tXMLElement = tXMLDocument.createElement (Player.EN_PLAYERS);
		for (Player tPlayer : players) {
			tPlayerElement = tPlayer.getPlayerElement (tXMLDocument);
			tXMLElement.appendChild (tPlayerElement);
		}
		
		return tXMLElement;
	}
	
	public XMLElement getPlayerStateElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tPlayerStateElement;
		
		tXMLElement = aXMLDocument.createElement (Player.EN_PLAYER_STATES);
		for (Player tPlayer : players) {
			tPlayerStateElement = tPlayer.getPlayerStateElement (aXMLDocument);
			tXMLElement.appendChild (tPlayerStateElement);
		}
		
		return tXMLElement;
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
		tGameManager = tRoundManager.getGameManager();
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
		Player.ActionStates tOldState, tNewState, tOldAuctionState, tNewAuctionState;
		Escrow tEscrow;
		Certificate tCertificateToBidOn;
		int tCashValue;
		
		tOldState = aPlayer.getPrimaryActionState();
		tOldAuctionState = aPlayer.getAuctionActionState();
		if (aPlayer.acts ()) {
			tCertificateToBidOn = stockRound.getCertificateToBidOn ();
			if (tCertificateToBidOn.isPrivateCompany ()) {
				tBidStockAction = new BidStockAction (stockRound.getRoundType (), stockRound.getID (), aPlayer);
				tCashValue = tCertificateToBidOn.getHighestBid ();
				tCashValue += BID_INCREMENT;
				aPlayer.setBidShare (true);
				tBidStockAction.addBidShareEffect (aPlayer);
				tEscrow = aPlayer.addEscrowInfo (tCertificateToBidOn, tCashValue);
				tBidStockAction.addCashTransferEffect (aPlayer, tEscrow, tCashValue);
				tBidStockAction.addBidToCertificateEffect (aPlayer, tCertificateToBidOn, tCashValue);
				tBidStockAction.addEscrowToPlayerEffect (aPlayer, tEscrow);
				
				tNewState = aPlayer.getPrimaryActionState ();
				tNewAuctionState = aPlayer.getAuctionActionState ();
				tBidStockAction.addStateChangeEffect (aPlayer, tOldState, tNewState);
				tBidStockAction.addAuctionStateChangeEffect (aPlayer, tOldAuctionState, tNewAuctionState);
				addAction (tBidStockAction);
				// Need to set the default Auction Action State to a Raise for use in Action Frame
				aPlayer.setAuctionActionState (ActorI.ActionStates.AuctionRaise);
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
		boolean tNextShareHasBids = false;
		Bank tBank;
		
		tBank = stockRound.getBank ();
		if (tBank.isInStartPacket (aCertificateToBuy)) {
			// Capture whether next share available has bids on it -- if So, after Sale, need to go to Auction Round
			// For 1830 only Private Companies in the Start Packet can be bid upon, and thereby auctioned.
			tNextShareHasBids = tBank.nextShareHasBids ();
		}
		
		return tNextShareHasBids;
	}
	
	public void finishAuction (boolean aNextShareHasBids, boolean aCreateNewAuctionAction) {
		if (aNextShareHasBids) {
			startAuctionRound (aCreateNewAuctionAction);
		} else {
			resumeStockRound ();
		}
		updateAllPlayerFrames ();
	}
	
	public BuyStockAction buyAction (Player aPlayer, List<Certificate> aCertificatesToBuy, 
			STOCK_BUY_IN aRoundBuying, BuyStockAction aBuyStockAction) {
		BuyStockAction tBuyStockAction;
		Player.ActionStates tOldState, tNewState;
		Certificate tFreeCertificate;
		Certificate tCertificateToBuy;
		List<Certificate> tCertificatesToTransfer;
		ShareCompany tShareCompany;
		Portfolio tPlayerPortfolio;
		Portfolio tSourcePortfolio;
		Bank tBank;
		int tCashValue;
		int tParPrice;
		Player tCurrentPresident;
		PortfolioHolderI tCurrentHolder;
		boolean tCanBuyStock = true;
		boolean tChainToPrevious = false;
		
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
			if (tCertificateToBuy.isShareCompany ()) {
				tShareCompany = tCertificateToBuy.getShareCompany ();
				if (tShareCompany == ShareCompany.NO_SHARE_COMPANY) {
					tCurrentPresident = Player.NO_PLAYER;
				} else {
					tCurrentHolder = tShareCompany.getPresident ();
					if (tCurrentHolder == Portfolio.NO_HOLDER) {
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
						handleSetParPrice (aPlayer, tCertificateToBuy, tShareCompany, tParPrice);
						tChainToPrevious = true;
					} else {
						System.err.println ("***Selected Par Price is " + tParPrice + " or tShareCompany is NULL***");
					}
				}
			} else {
				tCurrentPresident = Player.NO_PLAYER;
				tShareCompany = (ShareCompany) Corporation.NO_CORPORATION;
			}

			tFreeCertificate = tBank.getFreeCertificateWithThisCertificate (tCertificateToBuy);
			tCashValue = calculateCashToBuy (aCertificatesToBuy);
			tSourcePortfolio = getSourcePortfolio (tCertificateToBuy);
			
			aPlayer.transferCashTo (tBank, tCashValue);
			aBuyStockAction.addCashTransferEffect (aPlayer, tBank, tCashValue);
			tPlayerPortfolio = aPlayer.getPortfolio ();

			doFinalShareBuySteps (tPlayerPortfolio, tSourcePortfolio, aCertificatesToBuy, aBuyStockAction);
			/* If this Private include a Free Certificate, hand that over as well */
			if (tFreeCertificate != Certificate.NO_CERTIFICATE) {
				tCertificatesToTransfer = new LinkedList<Certificate> ();
				tCertificatesToTransfer.add (tFreeCertificate);
				doFinalShareBuySteps (tPlayerPortfolio, tSourcePortfolio, tCertificatesToTransfer, aBuyStockAction);
				/* If this Free Certificate is a President Share -- Request a Par Price to be set */
				if (tFreeCertificate.isPresidentShare ()) {
					if (tFreeCertificate.hasParPrice ()) {
						System.err.println ("Par Price already set.");
					} else {
						handleSetParPrice (aPlayer, tFreeCertificate);
					}
				}
			}
			
			handlePresidentialTransfer (aPlayer, aBuyStockAction, tShareCompany, tCurrentPresident);

			// Only want to change the Bought Share, and Primary Action State only if bought during the Stock Round
			// (For an Auction, it does not change the player's Primary State)
			// (For a Operating Round, should not be here, since it is the Corporation that is buying the Private, not a Player)
			if (STOCK_BUY_IN.StockRound.equals (aRoundBuying)) {
				aPlayer.setBoughtShare (true);
				aBuyStockAction.addBoughtShareEffect (aPlayer);
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

	private Portfolio getSourcePortfolio (Certificate aCertificateToBuy) {
		Portfolio tSourcePortfolio;
		Bank tBank;
		BankPool tBankPool;
		Portfolio tBankPortfolio, tBankPoolPortfolio;
		
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
		// TODO: non-1830, need to check if certificate is being bought from another player so a different source
		
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
	
	private void handlePresidentialTransfer (Player aPlayer, TransferOwnershipAction aTransferOwnershipAction, 
			ShareCompany aShareCompany, Player aCurrentPresident) {
		Player tNewPresident;
		
		// TODO: Extract method out to test and handle Presidential Exchange
		// Use this method to handle Private Exchange that tests and handles Presidential Exchange
		
		// If we have a Current President, and the Current Player is Not the President, 
		// Check to see if the Current Player now owns more and we have to Exchange President Share
		
		if ((aCurrentPresident != Player.NO_PLAYER) && (aCurrentPresident != aPlayer)) {
			tNewPresident = findNewPresident (aShareCompany, aPlayer, aCurrentPresident);
			if ((tNewPresident != aCurrentPresident) && (tNewPresident != Player.NO_PLAYER)) {
				exchangePresidentCertificate (aShareCompany, aCurrentPresident, tNewPresident, aTransferOwnershipAction);
			}
		}
	}
	
	private void handleSetParPrice (Player aPlayer, Certificate aCertificate, ShareCompany aShareCompany, int aParPrice) {
		PlayerFrame tPlayerFrame;
		
		gameManager.setParPrice (aShareCompany, aParPrice);
		tPlayerFrame = aPlayer.getPlayerFrame ();
		parPriceFrame = new ParPriceFrame (tPlayerFrame, aPlayer, stockRound, aCertificate);
		parPriceFrame.setParPriceFrameActive (false);
		parPriceFrame.setParValueAction (aParPrice, aShareCompany, false);
	}

	private void handleSetParPrice (Player aPlayer, Certificate aCertificate) {
		PlayerFrame tPlayerFrame;
		
		tPlayerFrame = aPlayer.getPlayerFrame ();
		parPriceFrame = new ParPriceFrame (tPlayerFrame, aPlayer, stockRound, aCertificate);
		parPriceFrame.setVisible (true);
		parPriceFrame.toFront ();
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
		ActorI.ActionStates tCurrentCorporationStatus, tNewCorporationStatus;
		
		for (Certificate tCertificate : aCertificatesToBuy) {
			transferOneCertificate (aToPortfolio, aFromPortfolio, tCertificate, aBuyStockAction);
			
			// Note, when Buying a Private, need to make the CheckBox invisible so it is not added to the Explain List
			// Undo makes this visible again always.
			// If this is done for all Certs, especially Share Companies, the first will be shown, but follow-on certs don't 
			// show the Certificate Buy Button.
			// Should come up with a better way to fix this
			if (tCertificate.isPrivateCompany ()) {
				tCertificate.clearFrameButton ();
			}
			tCurrentCorporationStatus = tCertificate.getCorporationStatus ();
			tCertificate.updateCorporationOwnership ();
			tNewCorporationStatus = tCertificate.getCorporationStatus ();
			if (tCurrentCorporationStatus != tNewCorporationStatus) {
				aBuyStockAction.addStateChangeEffect (tCertificate.getCorporation (), 
						tCurrentCorporationStatus, tNewCorporationStatus);
			}
		}
	}
	
	public void transferOneCertificate (Portfolio aToPortfolio, Portfolio aFromPortfolio, Certificate aCertificate,
			BuyStockAction aBuyStockAction) {
		PortfolioHolderI tFromHolder;
		PortfolioHolderI tToHolder;
		
		aToPortfolio.transferOneCertificateOwnership (aFromPortfolio, aCertificate);
		tFromHolder = aFromPortfolio.getHolder ();
		tToHolder = aToPortfolio.getHolder ();
		aBuyStockAction.addTransferOwnershipEffect (tFromHolder, aCertificate,  tToHolder);
	}
	
	public int getThisPlayerIndex (Player aPlayer) {
		int tThisPlayerIndex = -1;
		int tPlayerIndex;
		Player tThisPlayer;
		
		for (tPlayerIndex = 0; tPlayerIndex < players.size (); tPlayerIndex++) {
			tThisPlayer = players.get(tPlayerIndex);
			if (tThisPlayer.equals (aPlayer)) {
				tThisPlayerIndex = tPlayerIndex;
			}
		}
		
		return tThisPlayerIndex;
	}
	
	public void doneAction (Player aPlayer) {
		int tNextPlayerIndex, tCurrentPlayerIndex;
		int tOldPriorityPlayerIndex, tThisPlayerIndex;
		Player tOldPriorityPlayer;
		DonePlayerAction tDonePlayerAction;
		
		tCurrentPlayerIndex = stockRound.getCurrentPlayerIndex ();
		tThisPlayerIndex = getThisPlayerIndex (aPlayer);
		if (tThisPlayerIndex != tCurrentPlayerIndex) {
			System.err.println ("----- CurrentPlayerIndex is " + tCurrentPlayerIndex + " This Player Index " + tThisPlayerIndex);
			stockRound.setCurrentPlayer (tThisPlayerIndex, true);
			tCurrentPlayerIndex = tThisPlayerIndex;
		}
		tNextPlayerIndex = stockRound.getNextPlayerIndex ();
		tOldPriorityPlayerIndex = stockRound.getPriorityIndex ();
		tOldPriorityPlayer = getPlayer (tOldPriorityPlayerIndex);
		tDonePlayerAction = new DonePlayerAction (stockRound.getRoundType (), stockRound.getID (), aPlayer);
		tDonePlayerAction.addNewCurrentPlayerEffect (aPlayer, tCurrentPlayerIndex, tNextPlayerIndex);
		tDonePlayerAction.addNewPriorityPlayerEffect (aPlayer, tOldPriorityPlayerIndex, tNextPlayerIndex);
		
		// If this Player Stock Action include exchanging out the President Share, clear it, and add the Effect.
		if (aPlayer.hasExchangedShare () != Player.NO_STOCK_TO_SELL) {
			aPlayer.setExchangedPrezShare (Player.NO_STOCK_TO_SELL);
			tDonePlayerAction.addExchangePrezShareEffect (Player.NO_STOCK_TO_SELL);
		}

		aPlayer.updatePortfolioInfo ();
		stockRound.setPriorityPlayer (tNextPlayerIndex);
		stockRound.updateRFPlayerLabel (tOldPriorityPlayer);
		
		addAction (tDonePlayerAction);
		moveToNextPlayer (tNextPlayerIndex);
		gameManager.resetRoundFrameBackgrounds ();
	}
	
	private void moveToNextPlayer (int aNextPlayerIndex) {
		Player tNextPlayer;
		
		tNextPlayer = getPlayer (aNextPlayerIndex);
		tNextPlayer.setBoughtShare (false);
		tNextPlayer.setBidShare (false);
		tNextPlayer.updatePortfolioInfo ();
		stockRound.setCurrentPlayer (aNextPlayerIndex, true);
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
			if (tCorporation.isShareCompany ()) {
				aPlayer.acts (); // Simply set the fact that the Player has acted. This does not require that he has not
				tCorporationAbbrev = tCorporation.getAbbrev ();
				tNewPresident = findPlayerWithMost ((ShareCompany) tCorporation, aPlayer);
				aPlayer.setExchangedPrezShare (tCorporationAbbrev);
				
				tExchangeStockAction = new ExchangeStockAction (stockRound.getRoundType (),stockRound.getID (), aPlayer);
				tExchangeStockAction.addExchangePrezShareEffect (tCorporationAbbrev);
				exchangePresidentCertificate ((ShareCompany) tCorporation, aPlayer, tNewPresident, tExchangeStockAction);
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
		Certificate tNewCertificate;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		ExchangeStockAction tExchangeStockAction;
		int tExchangeID;
		int tExchangePercentage;
		PrivateCompany tPrivateCompany;
		Portfolio tBankPortfolio;
		Portfolio tPlayerPortfolio;
		Portfolio tClosedPortfolio;
		Bank tBank;
		ActorI.ActionStates tCurrentCorporationStatus;
		ActorI.ActionStates tNewCorporationStatus;
		Player tCurrentPresident;
		ShareCompany tShareCompany;
		
		tPrivateCompany = (PrivateCompany) aCorporation;
		tExchangeID = tPrivateCompany.getExchangeID ();
		tExchangePercentage = tPrivateCompany.getExchangePercentage ();
		tShareCompany = (ShareCompany) gameManager.getCorporationByID (tExchangeID);
		tBank = stockRound.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tNewCertificate = tBankPortfolio.getCertificate (tShareCompany, tExchangePercentage);
		if (tNewCertificate != Certificate.NO_CERTIFICATE) {
			tPlayerPortfolio = aPlayer.getPortfolio ();
			tClosedPortfolio = tBank.getClosedPortfolio ();
			tExchangeStockAction = new ExchangeStockAction (stockRound.getRoundType (),stockRound.getID (), aPlayer);
			tExchangeStockAction.addExchangeShareEffect (tPrivateCompany.getAbbrev (), tShareCompany.getAbbrev ());
			tClosedPortfolio.transferOneCertificateOwnership (tPlayerPortfolio, aCertificate);
			tExchangeStockAction.addTransferOwnershipEffect (aPlayer, aCertificate, tBank);
			tPlayerPortfolio.transferOneCertificateOwnership (tBankPortfolio, tNewCertificate);
			tExchangeStockAction.addTransferOwnershipEffect (tBank, tNewCertificate, aPlayer);
			tOldState = tPrivateCompany.getActionStatus ();
			tPrivateCompany.close ();
			tNewState = tPrivateCompany.getActionStatus ();
			tExchangeStockAction.addCloseCorporationEffect (tPrivateCompany, tOldState, tNewState);
			tCurrentCorporationStatus = tNewCertificate.getCorporationStatus ();
			tNewCertificate.updateCorporationOwnership ();
			tNewCorporationStatus = tNewCertificate.getCorporationStatus ();
			if (tCurrentCorporationStatus != tNewCorporationStatus) {
				tExchangeStockAction.addStateChangeEffect (tNewCertificate.getCorporation (), 
						tCurrentCorporationStatus, tNewCorporationStatus);
			}
			tCurrentPresident = (Player) tShareCompany.getPresident ();
			handlePresidentialTransfer (aPlayer, tExchangeStockAction, tShareCompany, tCurrentPresident);

			// TODO -- 
			addAction (tExchangeStockAction);
		}
	}
	
	public Player findPlayerWithMost (ShareCompany aShareCompany, Player aCurrentPlayer) {
		Player tNewPresident, tNextPlayer;
		int tCurrentPlayerIndex, tNextPlayerIndex;
		int tCurrentMaxPercentage, tNextPercentOwned;
		
		tNewPresident = Player.NO_PLAYER;
		tCurrentMaxPercentage = 0;
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
		
		return tNewPresident;
	}
	
	public Player findNewPresident (ShareCompany aShareCompany, Player aCurrentPlayer, Player aCurrentPresident) {
		Player tNewPresident, tNextPlayer;
		int tCurrentPlayerIndex, tNextPlayerIndex;
		int tCurrentMaxPercentage, tNextPercentOwned, tCurrentPresidentPercentage;
		
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
	
	public ActorI getActor (String aActorName) {
		ActorI tActor;
		
		tActor = ActorI.NO_ACTOR;
		for (Player tPlayer : players) {
			if (tActor == ActorI.NO_ACTOR) {
				if (aActorName.equals (tPlayer.getName ())) {
					tActor = tPlayer;
				} else {
					// If the Actor's Name ends with this Player's Name, it could be '#) Escrow for <PlayerName>' 
					// Ask the Player to fetch the matching name (if it exists)
					if (aActorName.endsWith (tPlayer.getName ())) {
						tActor = tPlayer.getMatchingEscrow (aActorName);
						// If the Player does not have the Escrow, it needs to be created and added to the Player
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
		Escrow tEscrow = Escrow.NO_ESCROW;
		Escrow tFoundEscrow;
		
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
		return gameManager.hasMustBuyCertificate ();
	}
	
	public boolean loadPlayers (XMLNode aPlayersNode, GameInfo aActiveGame) {
		XMLNodeList tXMLNodeList;
		boolean tPlayersLoaded;
		int tPlayerCount;
		PlayerInputFrame tPlayerInputFrame;
		
		tXMLNodeList = new XMLNodeList (playerParsingRoutine, aActiveGame);
		tXMLNodeList.parseXMLNodeList (aPlayersNode, PlayerInputFrame.EN_PLAYER);

		// If we have more than 1 player, we have loaded players. Can have 2 to N Players
		tPlayerCount = getPlayerCount ();
		if (tPlayerCount > 1) {
			// Clear out Players from the PlayerInputFrame (to make sure we get rid of the Client User Name 
			// Who may, or may not have been in the Save Game.
			tPlayerInputFrame = gameManager.getPlayerInputFrame ();
			tPlayerInputFrame.removeAllPlayers ();
			tPlayersLoaded = true;
			setCertificateLimit (aActiveGame, tPlayerCount);
		} else {
			tPlayersLoaded = false;
		}
		
		return tPlayersLoaded;
	}

	private void setCertificateLimit (GameInfo aActiveGame, int aPlayerCount) {
		int tCertificateLimit;
		
		tCertificateLimit = aActiveGame.getCertificateLimit (aPlayerCount);
		for (Player tPlayer : players) {
			tPlayer.setCertificateLimit (tCertificateLimit);
		}
	}
	
	ParsingRoutineI playerParsingRoutine  = new ParsingRoutineIO ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aPlayerNode) {
			
		}
		
		@Override
		public void foundItemMatchKey1 (XMLNode aPlayerNode, Object aGameInfo) {
			String tPlayerName;
			boolean tGameHasCoals;
			boolean tGameHasMinors;
			boolean tGameHasPrivates;
			boolean tGameHasShares;
			GameInfo tActiveGame;
			
			tActiveGame = (GameInfo) aGameInfo;
			tGameHasPrivates = tActiveGame.hasPrivates ();
			tGameHasCoals = tActiveGame.hasCoals ();
			tGameHasMinors = tActiveGame.hasMinors ();
			tGameHasShares = tActiveGame.hasShares ();
			tPlayerName = aPlayerNode.getThisAttribute (AN_NAME);
			addPlayer (tPlayerName, tGameHasPrivates, tGameHasCoals, tGameHasMinors, 
					tGameHasShares, 0);
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
	
	ParsingRoutineI playerStateParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aPlayerNode) {
			String tPlayerName;
			Player tPlayer;
			
			tPlayerName = aPlayerNode.getThisAttribute (AN_NAME);
			tPlayer = (Player) getActor (tPlayerName);
			if (tPlayer != Player.NO_PLAYER) {
				tPlayer.loadState (aPlayerNode);
			}
		}
	};

	public void passAction (Player aPlayer) {
		int tNextPlayerIndex, tCurrentPlayerIndex;
		PassAction tPassAction;
		Player.ActionStates tOldState, tNewState;
		boolean tMustSell, tHaveAllPassed;
		Certificate tCertificate;
		int tOldDiscount = 0, tNewDiscount = 0;
		String tCompanyName = "";
		
		tPassAction = new PassAction (stockRound.getRoundType (), stockRound.getID (), aPlayer);
		// Get State before acting for saving in the Action Stack.
		tOldState = aPlayer.getPrimaryActionState ();
	
		if (aPlayer.passes ()) {
			tNewState = aPlayer.getPrimaryActionState ();
			aPlayer.updatePortfolioInfo ();
			
			tCurrentPlayerIndex = stockRound.getCurrentPlayerIndex ();
			tNextPlayerIndex = stockRound.getNextPlayerIndex ();
			tCertificate = gameManager.getMustSellCertificate ();
			tHaveAllPassed = haveAllPassed ();
			if (tHaveAllPassed) {
				// Test result -- if True, continue 
				// If False -- clear all Pass Flags, and move to Next Player, continuing Stock Round
				if (! stockRound.canStartOperatingRound ()) {
					tMustSell = gameManager.hasMustSell ();
					if (tMustSell) {
						tCompanyName = tCertificate.getCompanyAbbrev ();
						tOldDiscount = tCertificate.getDiscount ();
						gameManager.applyDiscount ();
						tNewDiscount = tCertificate.getDiscount ();
						tPassAction.addApplyDiscountEffect (aPlayer, tCompanyName, tOldDiscount, tNewDiscount);
					}
				};
			}
			tPassAction.addStateChangeEffect (aPlayer, tOldState, tNewState);
			tPassAction.addNewCurrentPlayerEffect (aPlayer, tCurrentPlayerIndex, tNextPlayerIndex);
			addAction (tPassAction);
			stockRound.updateRFPlayerLabel (aPlayer);
			
			if (tHaveAllPassed) {
				if (! stockRound.startOperatingRound ()) {
					clearAllPlayerPasses ();
					moveToNextPlayer (tNextPlayerIndex);									
				}
			} else {
				moveToNextPlayer (tNextPlayerIndex);
			}
			gameManager.resetRoundFrameBackgrounds ();
		} else {
			System.err.println ("Player has acted in this Stock Round, cannot Pass");
		}
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
		System.out.println ("==== All Players Information STARTED ====");
		for (Player tPlayer : players) {
			tPlayer.printPlayerInfo ();
		}
		System.out.println ("==== All Players Information FINISHED ====");
	}
	
	public void sellAction (Player aPlayer) {
		SellStockAction tSellStockAction;
		Player.ActionStates tOldState, tNewState;
		List<Certificate> tCertificatesToSell;
		Portfolio tBankPoolPortfolio, tPlayerPortfolio;
		Bank tBank;
		BankPool tBankPool;
		int tCashValue, tSharesBeingSold;
		MarketCell tMarketCell, tNewMarketCell;
		MarketCell tStartMarketCell;
		int tStartLocation, tNewLocation;
		Token tToken;
		ShareCompany tShareCompany;
		String tCompanyAbbrev;
		String tExchangedShare;
		Player tCurrentPresident, tNewPresident;
		boolean tForceSell, tNormalSale;
		
		if (gameManager.isOperatingRound ()) {
			tForceSell = true;
			tNormalSale = false;
		} else if (aPlayer.sells ()) {
			tForceSell = false;
			tNormalSale = true;
		} else {
			tForceSell = false;
			tNormalSale = false;
		}
		// Get State before acting for saving in the Action Stack.
		tOldState = aPlayer.getPrimaryActionState ();
		
		if (tForceSell || tNormalSale) {
			tSellStockAction = new SellStockAction (stockRound.getRoundType (), stockRound.getID (), aPlayer);
			if (tForceSell) {
				aPlayer.setPrimaryActionState (tOldState);
			}
			tNewState = aPlayer.getPrimaryActionState ();
			tCertificatesToSell = aPlayer.getCertificatesToSell ();
			
			tCashValue = 0;
			
			// Go through all Certificates to get total value before transferring or adjusting stock price
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
			
			// This time transfer the Ownership of the stock certificates to the Bank Portfolio, and adjust the Stock Price
			tStartMarketCell = MarketCell.NO_MARKET_CELL;
			tShareCompany = (ShareCompany) Corporation.NO_ACTOR;
			tStartLocation = TokenStack.NO_STACK_LOCATION;
			tNewLocation = TokenStack.NO_STACK_LOCATION;
			tNewMarketCell = tStartMarketCell;
			tCurrentPresident = Player.NO_PLAYER;
			for (Certificate tCertificate : tCertificatesToSell) {
				tShareCompany = (ShareCompany) tCertificate.getCorporation ();
				tCompanyAbbrev = tShareCompany.getAbbrev ();
				// Haven't found the President yet, so find the current President -- if known.
				if (tCurrentPresident == Player.NO_PLAYER) {
					tCurrentPresident =  (Player) tShareCompany.getPresident ();
				}
				tMarketCell = tShareCompany.getSharePriceMarketCell ();
				if (tMarketCell != MarketCell.NO_MARKET_CELL) {
					// Save the Start Market Cell and Start Location (within token stack) of where the Price is before changing anything
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
				System.out.println ("Current President " + tCurrentPresident.getName () + " is Selling Stock, need to check for change");
				tNewPresident = findNewPresident (tShareCompany, aPlayer, tCurrentPresident);
				if ((tNewPresident != tCurrentPresident) && (tNewPresident != Player.NO_PLAYER)) {
					exchangePresidentCertificate (tShareCompany, tCurrentPresident, tNewPresident, tSellStockAction);
				}
			}
			tSellStockAction.addChangeMarketCellEffect (tShareCompany, tStartMarketCell, tStartLocation, tNewMarketCell, tNewLocation);
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
			aPlayer.updatePlayerInfo ();
		}
	}

	public void exchangePresidentCertificate (ShareCompany aShareCompany, Player aOldPresident, Player aNewPresident, TransferOwnershipAction aAction) {
		Certificate tPresidentCertificate, tCertificateOne;
		Portfolio tOldPresidentPortfolio, tNewPresidentPortfolio;
		
		tOldPresidentPortfolio = aOldPresident.getPortfolio ();
		tNewPresidentPortfolio = aNewPresident.getPortfolio ();
		tPresidentCertificate = tOldPresidentPortfolio.getPresidentCertificate (aShareCompany);
		tCertificateOne = tNewPresidentPortfolio.getNonPresidentCertificate (aShareCompany);
		if (tCertificateOne.getPercentage () == tPresidentCertificate.getPercentage ()) {
			tNewPresidentPortfolio.transferOneCertificateOwnership (tOldPresidentPortfolio, tPresidentCertificate);
			aAction.addTransferOwnershipEffect (aOldPresident, tPresidentCertificate, aNewPresident);
			tOldPresidentPortfolio.transferOneCertificateOwnership (tNewPresidentPortfolio, tCertificateOne);
			aAction.addTransferOwnershipEffect (aNewPresident, tCertificateOne, aOldPresident);
		} else {
			tNewPresidentPortfolio.transferOneCertificateOwnership (tOldPresidentPortfolio, tPresidentCertificate);
			aAction.addTransferOwnershipEffect (aOldPresident, tPresidentCertificate, aNewPresident);
			tOldPresidentPortfolio.transferOneCertificateOwnership (tNewPresidentPortfolio, tCertificateOne);
			aAction.addTransferOwnershipEffect (aNewPresident, tCertificateOne, aOldPresident);
			tCertificateOne = tNewPresidentPortfolio.getNonPresidentCertificate (aShareCompany);
			tOldPresidentPortfolio.transferOneCertificateOwnership (tNewPresidentPortfolio, tCertificateOne);
			aAction.addTransferOwnershipEffect (aNewPresident, tCertificateOne, aOldPresident);
		}
	}
	
	public void resumeStockRound () {
		stockRound.resumeStockRound ();
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
		
		tPlayer = players.get (aPlayerIndex);
		tPlayer.showPlayerFrame ();
	}
	
	public void startAuctionRound (boolean aCreateNewAuctionAction) {
		stockRound.startAuctionRound (aCreateNewAuctionAction);
	}
	
	public void undoAction (Player aPlayer) {
		boolean tActionUndone;
		Action tActionToUndo;
		Action tLastActionDone;
		Player tCurrentPlayer;
		String tLastActionActor, tUndoneActionActor;
		
		tActionToUndo = stockRound.getLastAction ();
		tUndoneActionActor = tActionToUndo.getActorName ();
		tActionUndone = stockRound.undoLastAction ();
		if (tActionUndone) {
			aPlayer.updatePlayerInfo ();
			if ((tActionToUndo instanceof PassAction) || (tActionToUndo instanceof DonePlayerAction)) {
				aPlayer.hidePlayerFrame ();
				
				tCurrentPlayer = getCurrentPlayer ();
				tCurrentPlayer.showPlayerFrame ();
				tCurrentPlayer.updatePlayerInfo ();
			}
			tLastActionDone = stockRound.getLastAction ();
			if (tLastActionDone != Action.NO_ACTION) {
				tLastActionActor = tLastActionDone.getActorName ();
				if (! tLastActionActor.equals (tUndoneActionActor)) {
					aPlayer.hidePlayerFrame ();
				}
			}
			System.out.println ("Last Action undone successfully");
		} else {
			System.err.println ("**** Undo Action failed ****");
		}
		stockRound.printBriefActionReport ();
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

	public boolean isNetworkAndIsThisClient (String aPlayerName) {
		return gameManager.isNetworkAndIsThisClient (aPlayerName);
	}
	
	public String getClientUserName () {
		return gameManager.getClientUserName ();
	}
	
	public int getTotalPlayerCash () {
		int tTotalPlayerCash = 0;
		
		for (Player tPlayer : players) {
			tTotalPlayerCash += tPlayer.getCash ();
		}
		
		return tTotalPlayerCash;
	}

	public Point getOffsetRoundFramePoint () {
		return gameManager.getOffsetRoundFrame ();
	}
	
	public boolean isAuctionRound () {
		return gameManager.isAuctionRound ();
	}

	public boolean isLastActionComplete () {
		return gameManager.isLastActionComplete ();
	}
	
	public void fillPrivateCompanies (ButtonsInfoFrame aButtonsInfoFrame) {
		Portfolio tPortfolio;
		
		for (Player tPlayer : players) {
			tPortfolio = tPlayer.getPortfolio ();
			aButtonsInfoFrame.fillWithPrivateCheckBoxes (tPortfolio, tPlayer.getName ());
		}
	}
}
