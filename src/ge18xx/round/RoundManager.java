package ge18xx.round;

import java.awt.Point;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.GameBank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.market.Market;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.CashHolderI;
import ge18xx.player.Escrow;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.ChangeRoundAction;
import ge18xx.round.action.GenericActor;
import ge18xx.tiles.TileSet;
import ge18xx.toplevel.AuditFrame;
import ge18xx.toplevel.MapFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutine2I;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class RoundManager {
	public final static ElementName EN_ROUNDS = new ElementName ("Rounds");
	public final static AttributeName AN_CURRENT_OR = new AttributeName ("currentOR");
	public final static AttributeName AN_OR_COUNT = new AttributeName ("maxOR");
	public final static AttributeName AN_ADDED_OR = new AttributeName ("addedOR");
	public final static AttributeName AN_ON_STOCK_ROUND = new AttributeName ("onStockRound");
	public final static AttributeName AN_CURRENT_ROUND_TYPE = new AttributeName ("currentRoundType");
	public final static GameManager NO_GAME_MANAGER = null;
	public final static ActionManager NO_ACTION_MANAGER = null;
	public final static OperatingRound NO_OPERATING_ROUND = null;
	public final static StockRound NO_STOCK_ROUND = null;
	public final static AuctionRound NO_AUCTION_ROUND = null;
	public final static RoundFrame NO_ROUND_FRAME = null;
	
	GameManager gameManager;
	PlayerManager playerManager;
	StockRound stockRound;
	OperatingRound operatingRound;
	AuctionRound auctionRound;
	ActionManager actionManager;
	int currentOR;
	int operatingRoundCount;
	boolean addedOR;
	ActorI.ActionStates currentRoundType;
	RoundFrame roundFrame;
	String gameName;
	
	public RoundManager (GameManager aGameManager, PlayerManager aPlayerManager) {
		setManagers (aGameManager, aPlayerManager);
	}
	
	public void initiateGame (CorporationList aPrivates, CorporationList aCoals, 
							CorporationList aMinors, CorporationList aShares) {
		
		setRounds (aPrivates, aCoals, aMinors, aShares);
		setOtherRoundInfo ();
		setRoundToStockRound (1);
		
		stockRound.setStartingPlayer ();
	}

	public void showInitialFrames () {
		showFrame ();
		actionManager.showActionReportFrame ();
	}
	
	public void setManagers (GameManager aGameManager, PlayerManager aPlayerManager) {
		if (gameManager == NO_GAME_MANAGER) {
			gameManager = aGameManager;
		}
		if (playerManager == GameManager.NO_PLAYER_MANAGER) {
			playerManager = aPlayerManager;
		}
		if (actionManager == NO_ACTION_MANAGER) {
			setActionManager (new ActionManager (this));
		}
	}
	
	public void setActionManager (ActionManager aActionManager) {
		actionManager = aActionManager;
	}
	
	public void setRounds (CorporationList aPrivates, CorporationList aCoals, 
							CorporationList aMinors, CorporationList aShares) {
		
		if (stockRound == NO_STOCK_ROUND) {
			setStockRound (new StockRound (playerManager, this));
		}
		if (auctionRound == NO_AUCTION_ROUND) {
			setAuctionRound (new AuctionRound (playerManager, this));
			auctionRound.setAuctionRoundInAuctionFrame ();
		}
		if (operatingRound == NO_OPERATING_ROUND) {
			setOperatingRound (new OperatingRound (this, aPrivates, aCoals, aMinors, aShares));
		}
	}
	
	public void setOperatingRound (OperatingRound aOperatingRound) {
		operatingRound = aOperatingRound;
	}
	
	public void setStockRound (StockRound aStockRound) {
		stockRound = aStockRound;
	}
	
	public void setAuctionRound (AuctionRound aAuctionRound) {
		auctionRound = aAuctionRound;
	}
	
	public void addPrivateToAuction () {
		gameManager.addPrivateToAuction ();
	}

	public void setAuctionFrameLocation () {
		gameManager.setAuctionFrameLocation ();
	}
	
	public void setOtherRoundInfo () {
		String tFullTitle;
		
		gameName = gameManager.getActiveGameName ();
		tFullTitle = gameManager.createFrameTitle ("Round");
		setRoundFrame (new RoundFrame (tFullTitle, this, gameName));
		gameManager.addNewFrame (roundFrame);
		roundFrame.setFrameToConfigDetails (gameManager);
		setCurrentOR (0);
		setORCount (0);
		addedOR = false;
		setRoundType (ActorI.ActionStates.StockRound);
	}
	
	public RoundFrame getRoundFrame () {
		return roundFrame;
	}
	
	public void setRoundFrame (RoundFrame aRoundFrame) {
		roundFrame = aRoundFrame;
	}
	
	public void setRoundType (ActorI.ActionStates aNewRoundType) {
		currentRoundType = aNewRoundType;
	}
	
	public void addAction (Action aAction) {
		// If applying a Network Action, we do -NOT- Need to add the Action again. This will double-up the Actions, and
		// During a Reload and Saved Network Game, this messes up the lastAction Number locally.
		if (! gameManager.applyingAction ()) {
			actionManager.addAction (aAction);
		}
		// If this does NOT Chain to a Previous Action, Do an Auto save... Don't need extra overhead.
		if (! aAction.getChainToPrevious ()) {
			gameManager.autoSaveGame ();
		}
		gameManager.setGameChanged (true);
	}
	
	public void addOR () {
		if (addedOR == false) {
			operatingRoundCount++;
			addedOR = true; 
		}
	}
	
	public JPanel buildPrivatesForPurchaseJPanel (ItemListener aItemListener, int aAvailableCash) {
		JPanel tPrivatesJPanel;
		
		tPrivatesJPanel = gameManager.buildPrivatesForPurchaseJPanel (aItemListener, aAvailableCash);
		
		return tPrivatesJPanel;
	}

	public boolean canPayHalfDividend () {
		return gameManager.canPayHalfDividend ();
	}

	public void clearBankSelections () {
		gameManager.clearBankSelections ();
	}
	
	public void clearAllAuctionStates () {
		gameManager.clearAllAuctionStates ();
	}
	
	public void doneAction (Corporation aCorporation) {
		clearAllPlayerSelections ();
		updateRoundFrame ();
		if (operatingRoundIsDone ()) {
			endOperatingRound ();
		}
	}

	public void clearAllPlayerSelections () {
		gameManager.clearAllPlayerSelections ();
	}

	public boolean isPlaceTileMode() {
		return gameManager.isPlaceTileMode ();
	}

	public boolean isPlaceTokenMode() {
		return gameManager.isPlaceTokenMode ();
	}

	public void enterPlaceTileMode () {
		gameManager.enterPlaceTileMode ();
	}
	
	public void enterPlaceTokenMode () {
		gameManager.enterPlaceTokenMode ();
	}

	public void enterSelectRouteMode (RouteInformation aRouteInformation) {
		gameManager.enterSelectRouteMode (aRouteInformation);
	}

	public void exitSelectRouteMode () {
		gameManager.exitSelectRouteMode ();
	}
	
	public void fullOwnershipAdjustment () {
		gameManager.fullOwnershipAdjustment ();
		roundFrame.updateAllCorporationsBox ();
	}
	
	public void setActionButtonLabel (String aActionButtonLabel) {
		roundFrame.updateActionButtonText (aActionButtonLabel);
	}
	
	public XMLElement getActionElements (XMLDocument aXMLDocument) {
		return actionManager.getActionElements (aXMLDocument);
	}
	
	public ActionManager getActionManager () {
		return actionManager;
	}
	
	public ActorI getActor (String aActorName) {
		ActorI tActor;
		
		tActor = ActorI.NO_ACTOR;
		if (stockRound.getName ().equals (aActorName)) {
			tActor = stockRound;
		} else if (operatingRound.getName ().equals (aActorName)) {
			tActor = operatingRound;
		} else if (auctionRound.getName ().equals (aActorName)) {
			tActor = auctionRound;
		}
		
		return tActor;
	}
	
	public AuctionRound getAuctionRound () {
		return auctionRound;
	}
	
	public Bank getBank () {
		return gameManager.getBank ();
	}
	
	public BankPool getBankPool () {
		return gameManager.getBankPool ();
	}
	
	public Portfolio getBankPoolPortfolio () {
		return gameManager.getBankPoolPortfolio ();
	}

	public Certificate getCertificate (String aCompanyAbbrev, int aPercentage, boolean aPresidentShare) {
		return gameManager.getCertificate (aCompanyAbbrev, aPercentage, aPresidentShare);
	}
	
	public Certificate getCertificateToBidOn () {
		Certificate tCertificateToBidOn;
		GameBank tBank;
		
		tBank = getBank ();
		tCertificateToBidOn = tBank.getCertificateToBidOn ();

		return tCertificateToBidOn;
	}

	public Certificate getCertificateToBuy () {
		Certificate tCertificateToBuy;
		GameBank tBank;
		BankPool tBankPool;
		
		tBank = getBank ();
		tCertificateToBuy = tBank.getCertificateToBuy ();
		if (tCertificateToBuy == Certificate.NO_CERTIFICATE) {
			tBankPool = getBankPool ();
			tCertificateToBuy = tBankPool.getCertificateToBuy ();
		}
		
		return tCertificateToBuy;
	}
	
	public int getCountOfSelectedPrivates () {
		return gameManager.getCountOfSelectedPrivates ();
	}
	
	public Escrow getEscrowMatching (String aEscrowName) {
		return gameManager.getEscrowMatching (aEscrowName);
	}
	
	public int getCurrentOR () {
		return currentOR;
	}
		
	public ActorI.ActionStates getCurrentRoundType () {
		return currentRoundType;
	}
	
	public HexMap getGameMap () {
		return gameManager.getGameMap ();
	}
	
	public GameManager getGameManager () {
		return gameManager;
	}
	
	public String getGameName () {
		return gameName;
	}
	
	public Action getLastAction () {
		return actionManager.getLastAction ();
	}
	
	public Market getMarket () {
		return gameManager.getMarket ();
	}
	
	public int getOperatingRoundCount () {
		return operatingRoundCount;
	}
	
	public OperatingRound getOperatingRound () {
		return operatingRound;
	}
	
	public String getOperatingOwnerName () {
		return operatingRound.getOperatingOwnerName ();
	}
	
	public PhaseInfo getCurrentPhaseInfo () {
		PhaseManager tPhaseManager;
		PhaseInfo tPhaseInfo;
		
		tPhaseManager = null;
		if (gameManager != null) {
			tPhaseManager = gameManager.getPhaseManager ();
			tPhaseInfo = tPhaseManager.getCurrentPhaseInfo ();
		} else {
			tPhaseInfo = null;
		}
		
		return tPhaseInfo;
	}

	public int getOperatingRoundID1 () {
		return operatingRound.getIDPart1 ();
	}
	
	public int getOperatingRoundID2 () {
		return operatingRound.getIDPart2 ();
	}
	
	public String getOperatingRoundID () {
		return operatingRound.getID ();
	}
	
	public String getORType () {
		String tRoundType;
		
		tRoundType = ">>NO Operating Round Set<<";
		if (operatingRound != NO_OPERATING_ROUND) {
			tRoundType = operatingRound.getType ();
		}
		
		return tRoundType;
	}
	
	public PhaseManager getPhaseManager () {
		return gameManager.getPhaseManager ();
	}
	
	public PlayerManager getPlayerManager () {
		return stockRound.getPlayerManager ();
	}
	
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tXMLStockElement, tXMLOperatingElement;
		
		tXMLElement = aXMLDocument.createElement (EN_ROUNDS);
		tXMLElement.setAttribute (AN_CURRENT_OR, currentOR);
		tXMLElement.setAttribute (AN_OR_COUNT, operatingRoundCount);
		tXMLElement.setAttribute (AN_ADDED_OR, addedOR);
		tXMLElement.setAttribute (AN_CURRENT_ROUND_TYPE, getCurrentRoundType ().toString ());
		tXMLStockElement = stockRound.getRoundState (aXMLDocument);
		tXMLElement.appendChild (tXMLStockElement);
		
		tXMLOperatingElement = operatingRound.getRoundState (aXMLDocument);
		tXMLElement.appendChild (tXMLOperatingElement);
	
		return tXMLElement;
	}
		
	public String getSRType () {
		String tRoundType;
		
		tRoundType = ">>NO Stock Round Set<<";
		if (stockRound != NO_STOCK_ROUND) {
			tRoundType = stockRound.getType ();
		}
		
		return tRoundType;
	}
	
	public String getARType () {
		String tRoundType;
	
		tRoundType = ">>NO Auction Round Set<<";
		if (auctionRound != NO_AUCTION_ROUND) {
			tRoundType = auctionRound.getType ();
		}
	
		return tRoundType;
	}

	public Round getRoundByTypeName (String tActorName) {
		Round tRound;
		
		tRound = null;
		if (tActorName.equals (StockRound.NAME)) {
			tRound = getStockRound ();
		} else if (tActorName.equals (OperatingRound.NAME)) {
			tRound = getOperatingRound ();
		} else if (tActorName.equals (AuctionRound.NAME)) {
			tRound = getAuctionRound ();
		}
		
		return tRound;
	}
	
	public String getRoundType () {
		String tRoundType;
		
		if (currentRoundType == ActorI.ActionStates.StockRound) {
			tRoundType = getSRType ();
		} else if (currentRoundType == ActorI.ActionStates.OperatingRound) {
			tRoundType = getORType ();
		} else if (currentRoundType == ActorI.ActionStates.AuctionRound) {
			tRoundType = getARType ();
		} else {
			tRoundType = "UNKOWN";
		}
		
		return tRoundType;
	}
	
	public PrivateCompany getSelectedPrivateCompanyToBuy () {
		return gameManager.getSelectedPrivateCompanyToBuy ();
	}

	public StockRound getStockRound () {
		return stockRound;
	}
	
	public int getStockRoundID () {
		return stockRound.getIDPart1 ();
	}
	
	public TileSet getTileSet () {
		return gameManager.getTileSet ();
	}
	
	public int getMinorTrainLimit () {
		return gameManager.getMinorTrainLimit ();
	}

	public int getTrainLimit (boolean aGovtRailway) {
		return gameManager.getTrainLimit (aGovtRailway);
	}

	public boolean hasActionsToUndo () {
		return actionManager.hasActionsToUndo ();
	}
	
	public boolean hasAddedOR () {
		return addedOR;
	}
	
	public int incrementRoundIDPart1 (Round aRound) {
		int tIDPart1;
		
		tIDPart1 = aRound.getIDPart1 () + 1;
		
		return tIDPart1;
	}
	
	public void incrementStockRound () {
		int tIDPart1, tIDPart2;
		
		tIDPart1 = incrementRoundIDPart1 (stockRound);
		tIDPart2 = stockRound.getIDPart2 ();
		stockRound.setID (tIDPart1, tIDPart2);
		roundFrame.setFrameLabel (gameName, " " + tIDPart1);
	}

	public boolean isAuctionRound () {
		return (currentRoundType == ActorI.ActionStates.AuctionRound);
	}
	
	public boolean isOperatingRound () {
		return (currentRoundType == ActorI.ActionStates.OperatingRound);
	}
	
	public boolean isStockRound () {
		return (currentRoundType == ActorI.ActionStates.StockRound);
	}
	
	public boolean isLastOR () {
		return (currentOR == operatingRoundCount);
	}
	
	public void loadActions (XMLNode aActionsNode, GameManager aGameManager) {
		actionManager.loadActions (aActionsNode, aGameManager);
	}
	
	public void loadRoundStates (XMLNode aRoundStateNode) {
		XMLNodeList tXMLNodeList;
		String tRoundType;
		GenericActor tGenericActor;
		
		currentOR = aRoundStateNode.getThisIntAttribute (AN_CURRENT_OR);
		operatingRoundCount = aRoundStateNode.getThisIntAttribute (AN_OR_COUNT);
		addedOR = aRoundStateNode.getThisBooleanAttribute (AN_ADDED_OR);
		tRoundType = aRoundStateNode.getThisAttribute (AN_CURRENT_ROUND_TYPE);
		tGenericActor = new GenericActor ();
		currentRoundType = tGenericActor.getRoundType (tRoundType);
		
		updateRoundFrame ();
		
		tXMLNodeList = new XMLNodeList (roundParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aRoundStateNode, StockRound.EN_STOCK_ROUND, 
				OperatingRound.EN_OPERATING_ROUND);
	}
	
	ParsingRoutine2I roundParsingRoutine  = new ParsingRoutine2I ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aRoundNode) {
			stockRound.loadRound (aRoundNode);
		}
		
		public void foundItemMatchKey2 (XMLNode aRoundNode) {
			operatingRound.loadRound (aRoundNode);
		}
	};
	
	public boolean doPartialCapitalization () {
		return gameManager.doPartialCapitalization ();
	}

	public boolean canBuyPrivate () {
		return gameManager.canBuyPrivate ();
	}
	
	public boolean gameHasPrivates () {
		return gameManager.gameHasPrivates ();
	}

	public boolean mapVisible () {
		return gameManager.mapVisible ();
	}
	
	public void repaintMapFrame () {
		gameManager.repaintMapFrame ();
	}

	public boolean tileTrayVisible () {
		return gameManager.tileTrayVisible ();
	}
	
	public void performPhaseChange (TrainCompany aTrainCompany, Train aTrain, BuyTrainAction aBuyTrainAction) {
		gameManager.performPhaseChange (aTrainCompany, aTrain, aBuyTrainAction);
	}
	
	public void revalidateTileTrayFrame () {
		gameManager.repaintTileTrayFrame ();
	}

	public void printBriefActionReport () {
		actionManager.briefActionReport ();
	}
	
	public void printRoundInfo () {
		System.out.println ("Round Manager Information Report");
		System.out.println ("Currently this is a " + currentRoundType);
		System.out.println ("Current OR " + currentOR + " Max OR " + operatingRoundCount + " Added OR " + addedOR);
		stockRound.printRoundInfo ();
		operatingRound.printRoundInfo ();
	}
	
	public void setAddedOR (boolean aAddedOR) {
		addedOR = aAddedOR;
	}
	
	public void setCurrentOR (int aCurrentOR) {
		currentOR = aCurrentOR;
	}
	
	public void setCurrentPlayerLabel () {
		String tPlayerName;
		
		if (isStockRound ()) {
			if (stockRound != NO_STOCK_ROUND) {
				tPlayerName = stockRound.getCurrentPlayerName ();
				if (roundFrame != NO_ROUND_FRAME) {
					roundFrame.setCurrentPlayerText (tPlayerName);
				}
			}
		}
	}
	
	public void setRoundToOperatingRound (int aRoundIDPart1, int aRoundIDPart2) {
		String tOldOperatingRoundID, tNewOperatingRoundID;
		Round tCurrentRound;
		boolean tCreateNewAction = true;
		
		if (aRoundIDPart2 == 1) {
			setOperatingRoundCount ();
			tCurrentRound = stockRound;
		} else {
			tCurrentRound = operatingRound;
		}
		tOldOperatingRoundID = operatingRound.getID ();
		setCurrentOR (aRoundIDPart2);
		operatingRound.setID (aRoundIDPart1, currentOR);
		tNewOperatingRoundID = operatingRound.getID ();
		changeRound (tCurrentRound, ActorI.ActionStates.OperatingRound, operatingRound, 
				tOldOperatingRoundID, tNewOperatingRoundID, tCreateNewAction);		
		roundFrame.setOperatingRound (gameName, aRoundIDPart1, currentOR, operatingRoundCount);
		revalidateRoundFrame ();
	}

	public void setOperatingRoundCount () {
		int tOperatingRoundsCount;
		PhaseInfo tCurrentPhase;
		PhaseManager tPhaseManager;
		
		tPhaseManager = gameManager.getPhaseManager ();
		tCurrentPhase = tPhaseManager.getCurrentPhaseInfo ();
		tOperatingRoundsCount = tCurrentPhase.getOperatingRoundsCount ();
		setORCount (tOperatingRoundsCount);
		revalidateRoundFrame ();
	}
	
	public void setORCount (int aORCount) {
		operatingRoundCount = aORCount;
	}

	public void revalidateRoundFrame () {
		roundFrame.repaint ();
		roundFrame.revalidate ();	
	}
	
	public void changeRound (Round aCurrentRound, ActorI.ActionStates aNewRoundType, 
			Round aNewRound, String aOldRoundID, String aNewRoundID, boolean aCreateNewAction) {
		ActorI.ActionStates tCurrentRoundType, tNewRoundType;
		ChangeRoundAction tChangeRoundAction;
		String tRoundID;
		
		tRoundID = aCurrentRound.getID ();
		tCurrentRoundType = getCurrentRoundType ();
		setRoundType (aNewRoundType);
		tNewRoundType = getCurrentRoundType ();

		if (! applyingAction ()) {
			if (aCreateNewAction) {
				if (! tRoundID.equals ("0.0")) {
					tChangeRoundAction = new ChangeRoundAction (tCurrentRoundType, tRoundID, aCurrentRound);
					tChangeRoundAction.addStateChangeEffect (aCurrentRound, tCurrentRoundType, tNewRoundType);
					tChangeRoundAction.addChangeRoundIDEffect (aNewRound, aOldRoundID, aNewRoundID);
					tChangeRoundAction.setChainToPrevious (true);
					addAction (tChangeRoundAction);
				}
			}
		}
	}
	
	public void updateRoundFrame () {
		if (roundFrame != RoundManager.NO_ROUND_FRAME) {
			roundFrame.updatePhaseLabel ();
			PlayerManager tPlayerManager;
			
			if (isStockRound ()) {
				updateAllCorporationsBox ();
				roundFrame.setStockRound (gameName, stockRound.getIDPart1 ());
			}
			if (isOperatingRound ()) {
				roundFrame.setOperatingRound (gameName, operatingRound.getIDPart1 (), currentOR, operatingRoundCount);
				updateAllCorporationsBox ();
				updateOperatingCorporationFrame ();
				operatingRound.updateActionLabel ();
			}
			if (isAuctionRound ()) {
				roundFrame.setAuctionRound (gameName, auctionRound.getIDPart1 ());
			}
			roundFrame.updateAll ();
			tPlayerManager = gameManager.getPlayerManager ();
			tPlayerManager.updateAllRFPlayerLabels ();
			roundFrame.revalidate ();
		}
	}
	
	public void setRoundToStockRound (int aRoundIDPart1) {
		String tOldRoundID, tNewRoundID;
		boolean tCreateNewAction = true;
		
		tOldRoundID = stockRound.getID ();
		stockRound.setID (aRoundIDPart1, 1);
		tNewRoundID = stockRound.getID ();
		changeRound (operatingRound, ActorI.ActionStates.StockRound, stockRound, 
				tOldRoundID, tNewRoundID, tCreateNewAction);
		
		// Round Round ID 1, ONLY we don't want to save the Change State Action for the Player
		// since this has not been completely initialized at the start of the game and will prevent NULL Pointer Exception
		if (aRoundIDPart1 == 1) {
			stockRound.clearAllPlayerPasses (null);
		} else {
			stockRound.clearAllPlayerPasses ();
		}
		
		roundFrame.setStockRound (gameName, aRoundIDPart1);
	}

	public void setRoundToAuctionRound (boolean aCreateNewAuctionAction) {
		String tOldRoundID, tNewRoundID;
		
		tOldRoundID = auctionRound.getID ();
		tNewRoundID = incrementRoundIDPart1 (auctionRound) + "";
		auctionRound.setID (tOldRoundID);
		changeRound (stockRound, ActorI.ActionStates.AuctionRound, auctionRound, 
				tOldRoundID, tNewRoundID, aCreateNewAuctionAction);
		roundFrame.setAuctionRound (gameName, 1);
	}

	public void showFrame () {
		roundFrame.setVisible (true);
	}
	
	public void showAuctionFrame () {
		auctionRound.showAuctionFrame ();
	}
	
	public void showCurrentPlayerFrame () {
		stockRound.showCurrentPlayerFrame ();
	}
	
	public void showCurrentCompanyFrame () {
		operatingRound.showCurrentCompanyFrame ();
	}
	
	public void showMap () {
		gameManager.showMap ();
	}

	public void showTileTray () {
		gameManager.showTileTray ();
	}
	
	public void resetOperatingRound (int aRoundIDPart1, int aRoundIDPart2) {
		setRoundType (ActorI.ActionStates.OperatingRound);
		roundFrame.setOperatingRound (gameName, aRoundIDPart1, currentOR, operatingRoundCount);
	}
	
	public void resumeStockRound (int aRoundIDPart1) {
		setRoundType (ActorI.ActionStates.StockRound);
		roundFrame.setStockRound (gameName, aRoundIDPart1);
		roundFrame.enablePassButton ();
	}
	
	public void startAuctionRound (boolean aCreateNewAuctionAction) {
		setRoundToAuctionRound (aCreateNewAuctionAction);
		auctionRound.startAuctionRound ();
		roundFrame.disablePassButton ("In Auction Round, Can't Pass");
	}
	
	public void startOperatingRound () {
		int tIDPart1, tIDPart2;
		
		tIDPart1 = incrementRoundIDPart1 (operatingRound);
		tIDPart2 = 1;
		setRoundToOperatingRound (tIDPart1, tIDPart2);
		// If no Minor, Coal of Share company operates, the Operating Round failed to start, 
		// Revenues were paid by Private Companies
		// Need to simply restart Stock Round
		if (! operatingRound.startOperatingRound ()) {
			startStockRound ();
		} else {
			roundFrame.resetBackGround ();
			roundFrame.disablePassButton ("In Operating Round, Can't Pass");
		}
	}
	
	public void endOperatingRound () {
		// If this is the LastOR, go back to Stock Round
		if (isLastOR ()) {
			startStockRound ();
		} else {
			// Otherwise, we need to go to another Operating Round, incrementing from the current OR.
			int tIDPart1 = operatingRound.getIDPart1 ();
			setRoundToOperatingRound (tIDPart1, currentOR + 1);
			operatingRound.startOperatingRound ();
		}
	}
	
	public void startStockRound () {
		int tIDPart1;
		
		tIDPart1 = incrementRoundIDPart1 (stockRound);
		setRoundToStockRound (tIDPart1);
		stockRound.clearAllSoldCompanies ();
		stockRound.setCurrentPlayer (stockRound.getPriorityIndex ());
		roundFrame.fillPlayersContainer (stockRound);
		roundFrame.updatePassButton ();
	}
	
	public boolean applyingAction () {
		return gameManager.applyingAction ();
	}
	
	public boolean canStartOperatingRound () {
		return gameManager.canStartOperatingRound ();
	}
	
	public boolean stockRoundIsDone () {
		return stockRound.roundIsDone ();
	}
	
	public boolean operatingRoundIsDone () {
		return operatingRound.roundIsDone ();
	}
	
	public boolean undoLastAction () {
		return actionManager.undoLastAction (this);
	}

	public void updateCurrentCompanyFrame () {
		operatingRound.updateCurrentCompanyFrame ();
	}
	
	public void updateAllCorporationsBox () {
		if (roundFrame != NO_ROUND_FRAME) {
			roundFrame.updateAllCorporationsBox ();
		}
	}
	
	public void updateAllRFPlayers () {
		playerManager.updateAllRFPlayerLabels ();
	}
	
	public void updateRFPlayerLabel (Player aPlayer, int aPriorityPlayerIndex, int aPlayerIndex) {
		if (aPlayer != Player.NO_PLAYER) {
			aPlayer.buildAPlayerContainer (aPriorityPlayerIndex, aPlayerIndex);
		}
	}
	
	public void updateAllFrames () {
		gameManager.updateAllFrames ();
	}
	
	public boolean wasLastActionStartAuction () {
		return actionManager.wasLastActionStartAuction ();
	}
	
	public void sendToReportFrame (String aReport) {
		actionManager.sendToReportFrame (aReport);
	}

	public boolean canBuyTrainInPhase () {
		boolean tCanBuyTrainInPhase;
		
		tCanBuyTrainInPhase = gameManager.canBuyTrainInPhase ();
		
		return tCanBuyTrainInPhase;
	}

	public Corporation getOperatingCompany () {
		return operatingRound.getOperatingCompany ();
	}

	public void updateOperatingCorporationFrame () {
		Corporation tOperatingCorporation;
		
		tOperatingCorporation = getOperatingCompany ();
		if (tOperatingCorporation != null) {
			tOperatingCorporation.updateFrameInfo ();
		}
	}
	
	public void handleNetworkAction (XMLNode aXMLActionNode) {
		actionManager.handleNetworkAction (aXMLActionNode);
	}
	
	public void revalidateAuctionFrame () {
		gameManager.revalidateAuctionFrame ();
	}

	public String getClientUserName () {
		return gameManager.getClientUserName ();
	}

	public boolean isNetworkGame () {
		return gameManager.isNetworkGame ();
	}

	public void updateAuctionFrame () {
		gameManager.updateAuctionFrame ();
	}

	public void printAllPlayersInfo () {
		gameManager.printAllPlayersInfo ();
	}
	
	public boolean getNotifyNetwork () {
		return gameManager.getNotifyNetwork ();
	}
	
	public void showRoundFrame () {
		roundFrame.setVisible (true);
	}

	public ShareCompany getShareCompany (String aCompanyAbbrev) {
		ShareCompany tShareCompany;
		
		tShareCompany = gameManager.getShareCompany (aCompanyAbbrev);
		
		return tShareCompany;
	}

	public void setParPrice (ShareCompany aShareCompany, int aParPrice) {
		gameManager.setParPrice (aShareCompany, aParPrice);
	}

	public void updateActionLabel (ShareCompany aShareCompany) {
		String tDoActionLabel = "DO THIS COMPANY";
		
		if (aShareCompany.shouldOperate ()) {
			tDoActionLabel = aShareCompany.getDoActionLabel ();			
		}
		setActionButtonLabel (tDoActionLabel);
		if (isNetworkAndIsThisClient (aShareCompany.getPresidentName ())) {
			enableActionButton (true);
		} else {
			enableActionButton (false);
		}
	}

	public boolean isNetworkAndIsThisClient (String aClientName) {
		return gameManager.isNetworkAndIsThisClient (aClientName);
	}

	public void enableActionButton (boolean aEnableActionButton) {
		roundFrame.enableActionButton (aEnableActionButton);
	}
	
	public int getTotalCash () {
		return gameManager.getTotalCash ();
	}
	
	public void fillAuditFrame (AuditFrame aAuditFrame, String aActorName) {
		actionManager.fillAuditFrame (aAuditFrame, aActorName);
	}

	public void setActionNumber (int aActionNumber) {
		actionManager.setActionNumber (aActionNumber);
	}

	public void hideTrainRevenueFrame() {
		// TODO Auto-generated method stub
	}
	
	public void showGEFrame () {
		gameManager.showGEFrame ();
	}
	
	public void showActionReportFrame () {
		actionManager.showActionReportFrame ();
	}
	
	public Point getOffsetRoundFrame () {
		Point tRoundFramePoint, tNewPoint;
		double tX, tY;
		int tNewX, tNewY;
		
		tRoundFramePoint = roundFrame.getLocation ();
		tX = tRoundFramePoint.getX ();
		tY = tRoundFramePoint.getY ();
		tNewX = (int) tX + 100;
		tNewY = (int) tY + 100;
		tNewPoint = new Point (tNewX, tNewY);
		
		return tNewPoint;
	}

	public MapFrame getMapFrame() {
		return gameManager.getMapFrame ();
	}

	public void passStockAction () {
		stockRound.passStockAction ();
	}

	public int getLastActionNumber () {
		return actionManager.getLastActionNumber ();
	}

	public CashHolderI getCashHolderByName (String aCashHolderName) {
		CashHolderI tCashHolder;
		ActorI tBidderActorI;
		
		tBidderActorI = gameManager.getActor (aCashHolderName);
		tCashHolder = (CashHolderI) tBidderActorI;
		
		return tCashHolder;
	}

	public boolean isLastActionComplete () {
		// TODO Auto-generated method stub
		return actionManager.isLastActionComplete ();
	}
}