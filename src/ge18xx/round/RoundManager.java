package ge18xx.round;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.apache.logging.log4j.Logger;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.GameBank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameInfo;
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
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.xml.XMLSaveGameI;
import geUtilities.GUI;
import geUtilities.ParsingRoutine2I;

public class RoundManager implements ActionListener, XMLSaveGameI {
	public static final ElementName EN_ROUNDS = new ElementName ("Rounds");
	public static final AttributeName AN_CURRENT_OR = new AttributeName ("currentOR");
	public static final AttributeName AN_OR_COUNT = new AttributeName ("maxOR");
	public static final AttributeName AN_ADDED_OR = new AttributeName ("addedOR");
	public static final AttributeName AN_ON_STOCK_ROUND = new AttributeName ("onStockRound");
	public static final AttributeName AN_CURRENT_ROUND_TYPE = new AttributeName ("currentRoundType");
	public static final RoundManager NO_ROUND_MANAGER = null;

	GameManager gameManager;
	PlayerManager playerManager;
	ActionManager actionManager;

	ActorI.ActionStates currentRoundState;
	RoundFrame roundFrame;
	Round currentRound;
	StockRound stockRound;
	OperatingRound operatingRound;
	AuctionRound auctionRound;
	FormationRound formationRound;
	ContractBidRound contractBidRound;
	
	Logger logger;
	int currentOR;
	int operatingRoundCount;
	boolean addedOR;
	String gameName;

	public RoundManager (GameManager aGameManager, PlayerManager aPlayerManager) {
		setManagers (aGameManager, aPlayerManager);
		setCurrentRound (Round.NO_ROUND);
		logger = gameManager.getLogger ();
	}

	public void setManagers (GameManager aGameManager, PlayerManager aPlayerManager) {
		if (gameManager == GameManager.NO_GAME_MANAGER) {
			setGameManager (aGameManager);
		}
		if (playerManager == PlayerManager.NO_PLAYER_MANAGER) {
			setPlayerManager (aPlayerManager);
		}
		if (actionManager == ActionManager.NO_ACTION_MANAGER) {
			setActionManager (new ActionManager (this));
		}
	}

	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}

	public void setPlayerManager (PlayerManager aPlayerManager) {
		playerManager = aPlayerManager;
	}

	public void setActionManager (ActionManager aActionManager) {
		actionManager = aActionManager;
	}

	public void setCurrentRound (Round aCurrentRound) {
		currentRound = aCurrentRound;
	}
	
	public Logger getLogger () {
		return logger;
	}

	public JMenuBar getJMenuBar () {
		return gameManager.getJMenuBar ();
	}
	
	public void initiateGame (CorporationList aPrivates, CorporationList aMinors, CorporationList aShares) {
		GameInfo tGameInfo;
		String tInitialRoundType;
		Round tInitialRound;
		
		tGameInfo = getGameInfo ();
		initiateRounds (aPrivates, aMinors, aShares);
		
		tInitialRoundType = tGameInfo.getInitialRoundType ();
		tInitialRound = getRoundByTypeName (tInitialRoundType);

		setCurrentRoundState (tInitialRound.getRoundState ());
		setCurrentRound (tInitialRound);
		setOtherRoundInfo ();
		tInitialRound.setIDPart1 (Round.START_ID1);
		tInitialRound.setIDPart2 (Round.START_ID2);
		setRoundToStockRound ();

		stockRound.setStartingPlayer ();
	}

	public boolean isFirstStockRound () {
		boolean tIsFirstStockRound;

		if (isStockRound ()) {
			tIsFirstStockRound = stockRound.isFirstRound ();
		} else {
			tIsFirstStockRound = false;
		}

		return tIsFirstStockRound;
	}

	public void showInitialFrames () {
		showFrame ();
		actionManager.showActionReportFrame ();
	}

	// Initialize each of the Round Types if they have not been set yet.
	
	public void initiateRounds (CorporationList aPrivates, CorporationList aMinors,
			CorporationList aShares) {
		if (stockRound == StockRound.NO_STOCK_ROUND) {
			setStockRound (new StockRound (this, playerManager));
		}
		if (auctionRound == AuctionRound.NO_AUCTION_ROUND) {
			setAuctionRound (new AuctionRound (this));
		}
		if (operatingRound == OperatingRound.NO_OPERATING_ROUND) {
			setOperatingRound (new OperatingRound (this, aPrivates, aMinors, aShares));
		}
		if (formationRound == FormationRound.NO_FORMATION_ROUND) {
			setFormationRound (new FormationRound (this));
		}
		if (contractBidRound == ContractBidRound.NO_CONTRACT_BID_ROUND) {
			setContractBidRound (new ContractBidRound (this));
		}
	}

	// Set each of the various Round Types
	public void setOperatingRound (OperatingRound aOperatingRound) {
		operatingRound = aOperatingRound;
	}

	public void setStockRound (StockRound aStockRound) {
		stockRound = aStockRound;
	}

	public void setAuctionRound (AuctionRound aAuctionRound) {
		auctionRound = aAuctionRound;
	}

	public void setFormationRound (FormationRound aFormationRound) {
		formationRound = aFormationRound;
	}

	public void setContractBidRound (ContractBidRound aContractBidRound) {
		contractBidRound = aContractBidRound;
	}
//
//	public void addPrivateToAuction () {
//		gameManager.addPrivateToAuction ();
//	}

	public void setAuctionFrameLocation () {
		gameManager.setAuctionFrameLocation ();
	}

	public void setOtherRoundInfo () {
		String tFullTitle;
		RoundFrame tRoundFrame;

		gameName = gameManager.getActiveGameName ();
		tFullTitle = gameManager.createFrameTitle (RoundFrame.BASE_TITLE);

		tRoundFrame = new RoundFrame (tFullTitle, this, gameManager);
		setRoundFrame (tRoundFrame);
		gameManager.addNewFrame (roundFrame);
		roundFrame.setFrameToConfigDetails (gameManager);
		setCurrentOR (0);
		setOperatingRoundCount (0);
		addedOR = false;
	}

	public RoundFrame getRoundFrame () {
		return roundFrame;
	}

	public void setRoundFrame (RoundFrame aRoundFrame) {
		roundFrame = aRoundFrame;
	}

	public void setCurrentRoundState (ActorI.ActionStates aNewRoundState) {
		currentRoundState = aNewRoundState;
	}

	public void addAction (Action aAction) {
	
		// If applying a Network Action, we do -NOT- Need to add the Action again. This
		// will double-up the Actions, and
		// During a Reload and Saved Network Game, this messes up the lastAction Number
		// locally.
		if (!gameManager.applyingAction ()) {
			actionManager.addAction (aAction);
		}
		gameManager.autoSaveGame ();
		gameManager.setGameChanged (true);
		
//		Time to Check and Handle a Round that will Interrupt. 
//		Setting the Current Round to the Round that is interrupting to
		checkAndHandleInterruption (aAction);
	}

	public void checkAndHandleInterruption (Action aAction) {
		Round tInterruptionRound;
		RoundType tCurrentRoundType;
		RoundType tInterruptionRoundType;
		boolean tIsInterrupting;
		boolean tInterruptionStarted;
		boolean tAfterAction;
		String tActionName;
		String tInterruptRoundName;

		tCurrentRoundType = currentRound.getRoundType ();
		tInterruptRoundName = tCurrentRoundType.getInterruptionRound ();
		
		if (tInterruptRoundName != GUI.NULL_STRING) {
			tInterruptionRound = getRoundByTypeName (tInterruptRoundName);
			tInterruptionRoundType = tInterruptionRound.getRoundType ();
			tActionName = aAction.getName ();
			tAfterAction = tInterruptionRoundType.getAfterActions ().contains (tActionName);
			if (tAfterAction) {
				tIsInterrupting = tInterruptionRound.isInterrupting ();
				tInterruptionStarted = tInterruptionRound.interruptionStarted ();
				if (tIsInterrupting && !tInterruptionStarted) {
					System.out.println ("Found Certificate with Bid, need to Start the Interruption to Auction Round");
					auctionRound.start ();
				}
			}
		}
	}
	
	/**
	 * Append Report String to Action Report Frame
	 *
	 * @param aReport String Text to append to the end of the Action Report Frame
	 *
	 */
	public void appendReport (String aReport) {
		actionManager.appendReport (aReport);
	}

	/**
	 * Append Error Report String to Action Report Frame as an Error
	 *
	 * @param aErrorReport String Text to append as an Error to the end of the Action Report Frame
	 *
	 */
	public void appendErrorReport (String aReport) {
		actionManager.appendErrorReport (aReport);
	}

	public String getFullActionReport () {
		return actionManager.getFullActionReport ();
	}

	public void addOR () {
		if (!addedOR) {
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

	public void declareBankuptcyAction (Corporation aCorporation) {
		clearAllPlayerSelections ();
		currentRoundState = ActorI.ActionStates.Bankrupt;
		updateRoundFrame ();
		roundFrame.toTheFront ();
		gameManager.resetRoundFrameBackgrounds ();
	}

	public void doneAction (Corporation aCorporation) {
		clearAllPlayerSelections ();
		gameManager.resetRoundFrameBackgrounds ();
		updateRoundFrame ();
		roundFrame.toTheFront ();
		if (operatingRoundIsDone ()) {
			endOperatingRound ();
		}
	}

	public void clearAllPlayerSelections () {
		gameManager.clearAllPlayerSelections ();
	}

	public boolean isPlaceTileMode () {
		return gameManager.isPlaceTileMode ();
	}

	public boolean isPlaceTokenMode () {
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
		roundFrame.updateAllCorporationsJPanel ();
	}

	public void setPlayerDoingAction (boolean aPlayerDoingAction) {
		roundFrame.setPlayerDoingAction (aPlayerDoingAction);
	}

	public void setButtonLabel (String aActionButtonLabel) {
		roundFrame.updateDoButtonText (aActionButtonLabel);
	}

	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		XMLElement tXMLElement;
		String tType;
		
		tType = aEN_Type.toString ();
		if (tType.equals (Action.EN_ACTIONS.toString ())) {
			tXMLElement = addActionElements (aXMLDocument);
		} else if (tType.equals (EN_ROUNDS.toString ())) {
			tXMLElement = addRoundState (aXMLDocument);
		} else {
			tXMLElement = XMLElement.NO_XML_ELEMENT;
		}
		
		return tXMLElement;
	}

	public XMLElement addActionElements (XMLDocument aXMLDocument) {
		return actionManager.getActionElements (aXMLDocument);		
	}
	
	public XMLElement addRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLStockElement;
		XMLElement tXMLOperatingElement;

		tXMLElement = aXMLDocument.createElement (EN_ROUNDS);
		tXMLElement.setAttribute (AN_CURRENT_OR, currentOR);
		tXMLElement.setAttribute (AN_OR_COUNT, operatingRoundCount);
		tXMLElement.setAttribute (AN_ADDED_OR, addedOR);
		tXMLElement.setAttribute (AN_CURRENT_ROUND_TYPE, getCurrentRoundState ().toString ());
		tXMLStockElement = stockRound.getRoundState (aXMLDocument);
		tXMLElement.appendChild (tXMLStockElement);

		tXMLOperatingElement = operatingRound.getRoundState (aXMLDocument);
		tXMLElement.appendChild (tXMLOperatingElement);

		return tXMLElement;
	}

	public ActionManager getActionManager () {
		return actionManager;
	}
	
	public void updatePlayerListeners (String aMessage) {
		playerManager.updatePlayerListeners (aMessage);
	}

	public ActorI getActor (String aActorName) {
		ActorI tActor;

		// TODO: Cycle through the different Round Type
		// If it matches the round by name, return the corresponding Round, given it is not NULL
		
		tActor = ActorI.NO_ACTOR;
		if (stockRound.isActor (aActorName)) {
			tActor = stockRound;
		} else if (operatingRound.isActor (aActorName)) {
			tActor = operatingRound;
		} else if (auctionRound.isActor (aActorName)) {
			tActor = auctionRound;
		} else if (formationRound.isActor (aActorName)) {
			tActor = formationRound;
		} else if (contractBidRound.isActor (aActorName)) {
			tActor = contractBidRound;
		}

		return tActor;
	}

	public Round getRoundByTypeName (String aActorName) {
		Round tRound;

		tRound = (Round) getActor (aActorName);

		return tRound;
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

	public List<Certificate> getCertificatesToBuy () {
		List<Certificate> tCertificatesToBuy;
		BankPool tBankPool;
		Bank tBank;
		Certificate tCertificateToBuy;

		tBankPool = getBankPool ();
		tCertificatesToBuy = tBankPool.getCertificatesToBuy ();
		tBank = getBank ();
		tCertificateToBuy = tBank.getCertificateToBuy ();
		if (tCertificateToBuy != Certificate.NO_CERTIFICATE) {
			tCertificatesToBuy.add (tCertificateToBuy);
		}

		return tCertificatesToBuy;
	}

	public int getCountOfSelectedPrivates () {
		return gameManager.getCountOfSelectedPrivates ();
	}

	public Escrow getEscrowMatching (String aEscrowName) {
		return gameManager.getEscrowMatching (aEscrowName);
	}

	public Round getCurrentRound () {
		Round tCurrentRound;
		
		if (currentRoundState == ActorI.ActionStates.StockRound) {
			tCurrentRound = stockRound;
		} else if (currentRoundState == ActorI.ActionStates.OperatingRound) {
			tCurrentRound = operatingRound;
		} else if (currentRoundState == ActorI.ActionStates.AuctionRound) {
			tCurrentRound = auctionRound;
		} else if (currentRoundState == ActorI.ActionStates.FormationRound) {
			tCurrentRound = formationRound;
		} else if (currentRoundState == ActorI.ActionStates.ContractBidRound) {
			tCurrentRound = contractBidRound;
		} else {
			System.err.println ("Current Round of " + currentRoundState.toString () + " NOT Recognized");
			tCurrentRound = Round.NO_ROUND;
		}
		
		return tCurrentRound;
	}
	
	public String getCurrentRoundOf () {
		return roundFrame.getCurrentRoundOf ();
	}

	public ActorI.ActionStates getCurrentRoundState () {
		return currentRoundState;
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

	public int getOperatingRoundCount () {
		return operatingRoundCount;
	}

	public OperatingRound getOperatingRound () {
		return operatingRound;
	}

	public String getOperatingOwnerName () {
		return operatingRound.getOperatingOwnerName ();
	}

	public String getOwnerWhoWillOperate () {
		return operatingRound.getOwnerWhoWillOperate ();
	}

	public int getActionCount () {
		return actionManager.getActionCount ();
	}
	
	public Action getLastAction () {
		return actionManager.getLastAction ();
	}

	public Action getLastAction (int aActionOffset) {
		return actionManager.getLastAction (aActionOffset);
	}

	public int getActionNumber () {
		return actionManager.getActionNumber ();
	}
	
	public Market getMarket () {
		return gameManager.getMarket ();
	}

	public GameInfo getGameInfo () {
		return gameManager.getActiveGame ();
	}
	
	/**
	 * Get the Minimum Number of Shares to Float the company
	 *
	 * @return return the Minimum Number of Shares
	 */
	public int getMinSharesToFloat () {
		return gameManager.getMinSharesToFloat ();
	}

	public PhaseInfo getCurrentPhaseInfo () {
		PhaseManager tPhaseManager;
		PhaseInfo tPhaseInfo;

		tPhaseManager = PhaseManager.NO_PHASE_MANAGER;
		if (gameManager != GameManager.NO_GAME_MANAGER) {
			tPhaseManager = gameManager.getPhaseManager ();
			tPhaseInfo = tPhaseManager.getCurrentPhaseInfo ();
		} else {
			tPhaseInfo = PhaseInfo.NO_PHASE_INFO;
		}

		return tPhaseInfo;
	}

	public int getCapitalizationLevel (int aSharesSold) {
		int tCapitalizationLevel;

		tCapitalizationLevel = gameManager.getCapitalizationLevel (aSharesSold);
		
		return tCapitalizationLevel;
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

	public PhaseManager getPhaseManager () {
		return gameManager.getPhaseManager ();
	}

	public PlayerManager getPlayerManager () {
		return stockRound.getPlayerManager ();
	}

	public RoundType getCurrentRoundType () {
		RoundType tCurrentRoundType;
		
		tCurrentRoundType = currentRound.getRoundType ();
		
		return tCurrentRoundType;
	}
	
	public String getRoundName () {
		String tRoundName;

		tRoundName = currentRoundState.toString ();

		return tRoundName;
	}

	public Benefit getBenefitWithName (String aPrivateAbbev, String aBenefitName) {
		Benefit tFoundBenefit;
		PrivateCompany tFoundPrivateCompany;
		
		tFoundPrivateCompany = gameManager.getPrivateCompany (aPrivateAbbev);
		tFoundBenefit = tFoundPrivateCompany.getBenefitNamed (aBenefitName);
		
		return tFoundBenefit;
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
		aRound.setIDPart1 (tIDPart1);

		return tIDPart1;
	}

	public boolean isAAuctionRound () {
		return (currentRoundState == ActorI.ActionStates.AuctionRound);
	}

	public boolean isOperatingRound () {
		return (currentRoundState == ActorI.ActionStates.OperatingRound);
	}

	public boolean isStockRound () {
		return (currentRoundState == ActorI.ActionStates.StockRound);
	}

	public boolean isFormationRound () {
		return (currentRoundState == ActorI.ActionStates.FormationRound);
	}

	public boolean isContractBidRound () {
		return (currentRoundState == ActorI.ActionStates.ContractBidRound);
	}

	public boolean isBankrupt () {
		return (currentRoundState == ActorI.ActionStates.Bankrupt);
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
		operatingRoundCount = aRoundStateNode.getThisIntAttribute (AN_OR_COUNT, 1);
		addedOR = aRoundStateNode.getThisBooleanAttribute (AN_ADDED_OR);
		tRoundType = aRoundStateNode.getThisAttribute (AN_CURRENT_ROUND_TYPE);
		tGenericActor = new GenericActor ();
		currentRoundState = tGenericActor.getRoundType (tRoundType);

		tXMLNodeList = new XMLNodeList (roundParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aRoundStateNode, StockRound.EN_STOCK_ROUND, OperatingRound.EN_OPERATING_ROUND);
	}

	ParsingRoutine2I roundParsingRoutine = new ParsingRoutine2I () {
		@Override
		public void foundItemMatchKey1 (XMLNode aRoundNode) {
			stockRound.loadRound (aRoundNode);
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aRoundNode) {
			operatingRound.loadRound (aRoundNode);
		}
	};

	public boolean canBuyPrivate () {
		return gameManager.canBuyPrivate ();
	}

	public boolean gameHasPrivates () {
		return gameManager.gameHasPrivates ();
	}

	public boolean gameHasLoans () {
		return gameManager.gameHasLoans ();
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
		System.out.println ("Round Manager Information Report");	// PRINTLOG
		System.out.println ("Currently this is a " + currentRoundState);
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
			if (stockRound != StockRound.NO_STOCK_ROUND) {
				tPlayerName = stockRound.getCurrentPlayerName ();
				if (roundFrame != RoundFrame.NO_ROUND_FRAME) {
					roundFrame.setPlayerDoingAction (false);
					roundFrame.setCurrentPlayerText (tPlayerName);
					roundFrame.toTheFront ();
				}
			}
		}
	}

	public void revalidateRoundFrame () {
		roundFrame.repaint ();
		roundFrame.revalidate ();
	}

	public void updateAllListenerPanels () {
		roundFrame.updateAllListenerPanels ();
	}
	
	// Functions to Change from one Round Type to another
	
	public void updateRoundFrame () {
		int tRoundID;

		if (roundFrame != RoundFrame.NO_ROUND_FRAME) {
			operatingRound.sortByOperatingOrder ();

			if (isStockRound ()) {
				updateAllCorporationsBox ();
				tRoundID = stockRound.getIDPart1 ();
				roundFrame.setStockRoundInfo (gameName, tRoundID);
			}
			if (isOperatingRound ()) {
				tRoundID = operatingRound.getIDPart1 ();
				roundFrame.setOperatingRound (gameName, tRoundID, currentOR, operatingRoundCount);
				updateOperatingCorporationFrame ();
				operatingRound.updateActionLabel ();
			}
			if (isAAuctionRound ()) {
				tRoundID = auctionRound.getIDPart1 ();

				roundFrame.setAuctionRound (gameName, tRoundID);
			}
			roundFrame.updateAll ();
		}
	}
	
	public void setRoundTypeTo (ActorI.ActionStates aRoundType) {
		setCurrentRoundState (aRoundType);
	}

	public void setRoundToAuctionRound () {
		setCurrentRoundState (ActorI.ActionStates.AuctionRound);
	}
	
	// May not need the next 3 methods anymore 8/27/2024
	public void setRoundToOperatingRound () {
		setCurrentRoundState (ActorI.ActionStates.OperatingRound);
	}

	public void setRoundToFormationRound () {
		setCurrentRoundState (ActorI.ActionStates.FormationRound);
	}

	public void setRoundToContractBidRound () {
		setCurrentRoundState (ActorI.ActionStates.ContractBidRound);
	}

	public void setRoundToOperatingRound (Round aCurrentRound, int aRoundIDPart1, int aRoundIDPart2) {
		String tOldOperatingRoundID;
		String tNewOperatingRoundID;
		boolean tCreateNewAction;

		tCreateNewAction = true;
		if (aRoundIDPart2 == Round.START_ID2) {
			setOperatingRoundCount ();
		}
		tOldOperatingRoundID = operatingRound.getID ();
		setCurrentOR (aRoundIDPart2);
		operatingRound.setID (aRoundIDPart1, currentOR);
		tNewOperatingRoundID = operatingRound.getID ();
		changeRound (aCurrentRound, ActorI.ActionStates.OperatingRound, operatingRound, tOldOperatingRoundID,
				tNewOperatingRoundID, tCreateNewAction);
		roundFrame.setOperatingRound (gameName, aRoundIDPart1, currentOR, operatingRoundCount);
		revalidateRoundFrame ();
	}

	public void setRoundToStockRound () {
		String tOldRoundID;
		String tNewRoundID;
		boolean tCreateNewAction;
		int tRoundIDPart1;
		
		tCreateNewAction = true;
		tOldRoundID = stockRound.getID ();
		
		incrementRoundIDPart1 (stockRound);

		tNewRoundID = stockRound.getID ();
		tRoundIDPart1 = stockRound.getIDPart1 ();
		changeRound (operatingRound, ActorI.ActionStates.StockRound, stockRound, tOldRoundID, tNewRoundID,
				tCreateNewAction);

		stockRound.clearAllPlayerPasses ();

		roundFrame.setStockRoundInfo (gameName, tRoundIDPart1);
	}

	public void setRoundToAuctionRound (boolean aCreateNewAuctionAction) {
		String tOldRoundID;
		String tNewRoundID;
		int tRoundID;

		tOldRoundID = auctionRound.getID ();
		tRoundID = incrementRoundIDPart1 (auctionRound);
		tNewRoundID = tRoundID + "";
		auctionRound.setID (tOldRoundID);
		changeRound (stockRound, ActorI.ActionStates.AuctionRound, auctionRound, tOldRoundID, tNewRoundID,
				aCreateNewAuctionAction);
		roundFrame.setAuctionRound (gameName, tRoundID);
	}

	public void changeRound (Round aCurrentRound, ActorI.ActionStates aNewRoundType, Round aNewRound,
								String aOldRoundID, String aNewRoundID, boolean aCreateNewAction) {
		ActorI.ActionStates tCurrentRoundType;
		ActorI.ActionStates tNewRoundType;
		ChangeRoundAction tChangeRoundAction;
		AuctionRound tAuctionRound;
		XMLFrame tAuctionFrame;
		String tRoundID;

		tRoundID = aCurrentRound.getID ();
		tCurrentRoundType = getCurrentRoundState ();
		setCurrentRoundState (aNewRoundType);
		setCurrentRound (aNewRound);
		tNewRoundType = getCurrentRoundState ();
		currentRound.resume ();
		if (!applyingAction ()) {
			if (aCreateNewAction) {
				if (!tRoundID.equals (Round.NO_ID_STRING)) {
					tChangeRoundAction = new ChangeRoundAction (tCurrentRoundType, tRoundID, aCurrentRound);
					tChangeRoundAction.addStateChangeEffect (aCurrentRound, tCurrentRoundType, tNewRoundType);
					if (! aOldRoundID.equals (aNewRoundID)) {
						tChangeRoundAction.addChangeRoundIDEffect (aNewRound, aOldRoundID, aNewRoundID);
					}
					if (tNewRoundType == ActorI.ActionStates.AuctionRound) {
						tAuctionRound = (AuctionRound) aNewRound;
						tAuctionFrame = tAuctionRound.getAuctionFrame ();
						tChangeRoundAction.addShowFrameEffect (aCurrentRound, tAuctionFrame);
					}
					tChangeRoundAction.setChainToPrevious (true);
					addAction (tChangeRoundAction);
				}
			}
		}
	}

	public void finishCurrentRound () {
		currentRound.finish ();
		roundFrame.updateAll ();
	}
	
	public void setOperatingRoundCount () {
		int tOperatingRoundsCount;
		PhaseInfo tCurrentPhase;
		PhaseManager tPhaseManager;

		tPhaseManager = gameManager.getPhaseManager ();
		tCurrentPhase = tPhaseManager.getCurrentPhaseInfo ();
		tOperatingRoundsCount = tCurrentPhase.getOperatingRoundsCount ();
		setOperatingRoundCount (tOperatingRoundsCount);
		revalidateRoundFrame ();
	}

	public void setOperatingRoundCount (int aORCount) {
		operatingRoundCount = aORCount;
	}

	// End of functions to deal with changing Rounds from one type to another
	
	public void resetOperatingRound (int aRoundIDPart1, int aRoundIDPart2) {
		setCurrentRoundState (ActorI.ActionStates.OperatingRound);
		roundFrame.setOperatingRound (gameName, aRoundIDPart1, currentOR, operatingRoundCount);
	}

	public void setStockRoundInfo (int aRoundIDPart1) {
		roundFrame.setStockRoundInfo (gameName, aRoundIDPart1);
	}

	// TODO -- Lowest Bidder needs to have the auctionState set to 'Bidder' like the
	// AuctionFrame Class/addPrivateToAuction Method does. 
	// Note for setting up this Auction, the individual Effects to set each Player state to are
	// NOT being created as independent Effects to be applied to remote clients. 
	// This whole Auction Setup should be revised so each separate Effect is documented
	// in an Action to be consistent with the whole game system. This will improve the Undo Functionality.
	
//	public void startAuctionRound (boolean aCreateNewAuctionAction) {
//		setRoundToAuctionRound (aCreateNewAuctionAction);
//		auctionRound.startAuctionRound ();
//		roundFrame.updatePassButton ();
//	}

	public void startRound (ActorI.ActionStates aRoundState) {
		Round tRound;
		
		tRound = this.getRoundByTypeName (aRoundState.toString ());
		tRound.start ();
	}
	
	public void updatePassButton () {
		roundFrame.updatePassButton ();		
	}
	
	public void startOperatingRound (Round aCurrentRound) {
		int tIDPart1;
		int tIDPart2;

		tIDPart1 = incrementRoundIDPart1 (operatingRound);
		tIDPart2 = Round.START_ID2;
		setRoundToOperatingRound (aCurrentRound, tIDPart1, tIDPart2);
		playerManager.clearAllPlayerDividends ();
		playerManager.clearAllPercentBought ();

		// If no Minor of Share company operates, the Operating Round failed to
		// start,
		// Revenues were paid by Private Companies
		// Need to simply restart Stock Round
		if (! operatingRound.startOperatingRound ()) {
			startStockRound ();
		}
	}

	public void endOperatingRound () {
		int tIDPart1;

		// If this is the LastOR, go back to Stock Round
		if (isLastOR ()) {
			startStockRound ();
		} else {
			// Otherwise, we need to go to another Operating Round, incrementing from the
			// current OR.
			tIDPart1 = operatingRound.getIDPart1 ();
			setRoundToOperatingRound (stockRound, tIDPart1, currentOR + 1);
			operatingRound.startOperatingRound ();
		}
	}

	public void startStockRound () {
		if (bankIsBroken ()) {
			System.out.println ("GAME OVER -- Bank is Broken, Don't do any more Stock Rounds");
		}
		setRoundToStockRound ();
		gameManager.bringMarketToFront ();
		stockRound.prepareStockRound ();
		roundFrame.updateAll ();
		updateAllListenerPanels ();
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

	public void prepareCorporation () {
		operatingRound.prepareCorporation ();
	}

	public void showCurrentCompanyFrame () {
		operatingRound.showCurrentCompanyFrame ();
	}

	public void showMap () {
		gameManager.showMap ();
	}

	public void bringMapToFront () {
		gameManager.bringMapToFront ();
	}

	public void showTileTray () {
		gameManager.showTileTray ();
	}

	public void bringTileTrayToFront () {
		gameManager.bringTileTrayToFront ();
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tEventAction;
		Object tEventSource;
		FastBuyButton tFastBuyButton;

		tEventAction = aEvent.getActionCommand ();
		if (RoundFrame.CORPORATION_ACTION.equals (tEventAction)) {
			if (!companyStartedOperating ()) {
				logger.info ("Corporation Action for Operation Round selected");
				prepareCorporation ();
			}
			showCurrentCompanyFrame ();
		}
		if (RoundFrame.PLAYER_ACTION.equals (tEventAction)) {
			showCurrentPlayerFrame ();
			roundFrame.updatePassButton ();
		}
		if (RoundFrame.PLAYER_AUCTION_ACTION.equals (tEventAction)) {
			showCurrentPlayerFrame ();
			showAuctionFrame ();
		}
		if (RoundFrame.SHOW_GE_FRAME_ACTION.equals (tEventAction)) {
			showGEFrame ();
		}
		if (RoundFrame.PASS_STOCK_ACTION.equals (tEventAction)) {
			passStockAction ();
			updateAllCorporationsBox ();
		}
		if (RoundFrame.BUY_STOCK_ACTION.equals (tEventAction)) {
			tEventSource = aEvent.getSource ();
			if (tEventSource instanceof FastBuyButton) {
				tFastBuyButton = (FastBuyButton) tEventSource;
				buyStockAction (tFastBuyButton);
			}
			updateAllCorporationsBox ();
		}
		if (isOperatingRound ()) {
			if (operatingRoundIsDone ()) {
				endOperatingRound ();
			}
		}
	}

	// Game Manager handles with Boolean Return

	public boolean canBuyTrainInPhase () {
		boolean tCanBuyTrainInPhase;

		tCanBuyTrainInPhase = gameManager.canBuyTrainInPhase ();

		return tCanBuyTrainInPhase;
	}

	public boolean bankIsBroken () {
		return gameManager.bankIsBroken ();
	}

	public boolean canStartOperatingRound () {
		return gameManager.canStartOperatingRound ();
	}

	public boolean isLoading () {
		return gameManager.gameIsStarted ();
	}

	public boolean isNetworkGame () {
		return gameManager.isNetworkGame ();
	}

	public boolean getNotifyNetwork () {
		return gameManager.getNotifyNetwork ();
	}

	public boolean applyingAction () {
		return gameManager.applyingAction ();
	}

	// Operating Round handles with Boolean Return
	
	public boolean operatingRoundIsDone () {
		return operatingRound.roundIsDone ();
	}

	public boolean companyStartedOperating () {
		return operatingRound.companyStartedOperating ();
	}

	// Action Manager handles with Boolean Return
	
	public boolean undoLastAction () {
		return actionManager.undoLastAction (this);
	}

	public boolean wasLastActionStartAuction () {
		return actionManager.wasLastActionStartAuction ();
	}

	public void updateAllCorporationsBox () {
		if (roundFrame != RoundFrame.NO_ROUND_FRAME) {
			roundFrame.updateAllCorporationsJPanel ();
		}
	}

	public void updateAllRFPlayers () {
		playerManager.updateAllRFPlayerLabels ();
	}

	public void updateRFPlayerLabel (Player aPlayer, int aPriorityPlayerIndex, int aPlayerIndex) {
		if (aPlayer != Player.NO_PLAYER) {
			aPlayer.buildAPlayerJPanel (aPriorityPlayerIndex, aPlayerIndex);
		}
	}

	public void updateAllFrames () {
		gameManager.updateAllFrames ();
	}

	public void sendToReportFrame (String aReport) {
		actionManager.appendBorderedReport (aReport);
	}

	public Corporation getCompanyByID (int tCorporationID) {
		CorporationList tShareCorporationList;
		CorporationList tMinorCorporationList;
		Corporation tCorporation;
		
		tShareCorporationList = operatingRound.getShareCompanies ();
		tCorporation = tShareCorporationList.getCorporationByID (tCorporationID);
		if (tCorporation == Corporation.NO_CORPORATION) {
			tMinorCorporationList = operatingRound.getMinorCompanies ();
			tCorporation = tMinorCorporationList.getCorporationByID (tCorporationID);
		}
		
		return tCorporation;
	}

	public Corporation getOperatingCompany () {
		return operatingRound.getOperatingCompany ();
	}

	public void updateOperatingCorporationFrame () {
		Corporation tOperatingCorporation;

		tOperatingCorporation = getOperatingCompany ();
		if (tOperatingCorporation != Corporation.NO_CORPORATION) {
			tOperatingCorporation.updateFrameInfo ();
		}
	}

	public void handleNetworkAction (XMLNode aXMLActionNode) {
		actionManager.handleNetworkAction (aXMLActionNode);
	}

	public void revalidateAuctionFrame () {
		gameManager.revalidateAuctionFrame ();
	}

	/**
	 * Get the name of the First Player to appear in Round Frame List
	 * This is based upon user Preference
	 * 
	 * @return Name of the player who should appear first in the Round Frame List
	 * 
	 */
	
	public String getFirstPlayerName () {
		return gameManager.getFirstPlayerName ();
	}

	public String getClientUserName () {
		return gameManager.getClientUserName ();
	}

	public void updateAuctionFrame () {
		gameManager.updateAuctionFrame ();
	}

	public void printAllPlayersInfo () {
		gameManager.printAllPlayersInfo ();
	}

	public void showRoundFrame () {
		roundFrame.setVisible (true);
	}

	public ShareCompany getShareCompany (String aCompanyAbbrev) {
		ShareCompany tShareCompany;

		tShareCompany = gameManager.getShareCompany (aCompanyAbbrev);

		return tShareCompany;
	}

	public CorporationList getShareCompanies () {
		CorporationList tShareCompanies;
		
		tShareCompanies = gameManager.getShareCompanies ();
		
		return tShareCompanies;
	}
	
	public void setParPrice (ShareCompany aShareCompany, int aParPrice) {
		gameManager.setParPrice (aShareCompany, aParPrice);
	}

	public void updateActionLabel (Corporation aCorporation) {
		String tDoActionLabel;
		
		tDoActionLabel = "DO THIS COMPANY";
		if (aCorporation.shouldOperate ()) {
			tDoActionLabel = aCorporation.getDoLabel ();
		}
		if (aCorporation.isOperating ()) {
			tDoActionLabel = aCorporation.getOperatingLabel ();
		}
		setButtonLabel (tDoActionLabel);
		if (isNetworkAndIsThisClient (aCorporation.getPresidentName ())) {
			enableActionButton (true);
		} else {
			enableActionButton (false);
		}
	}

	public boolean isNetworkAndIsThisClient (String aClientName) {
		return gameManager.notIsNetworkAndIsThisClient (aClientName);
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

	public void hideTrainRevenueFrame () {
	}

	public void showGEFrame () {
		gameManager.showGEFrame ();
	}

	public void showActionReportFrame () {
		actionManager.showActionReportFrame ();
	}

	public void resendLastActions () {
		actionManager.resendLastActions ();
	}

	public Point getOffsetRoundFrame () {
		Point tNewPoint;

		tNewPoint = roundFrame.getOffsetFrame ();

		return tNewPoint;
	}

	public MapFrame getMapFrame () {
		return gameManager.getMapFrame ();
	}

	public void passStockAction () {
		stockRound.passStockAction ();
	}

	public void buyStockAction (FastBuyButton aFastBuyButton) {
		Certificate tCertificate;
		List<Certificate> tCertificatesToBuy;
		Player tCurrentPlayer;

		tCertificate = aFastBuyButton.getCertificate ();

		tCertificatesToBuy = new LinkedList<> ();
		tCertificatesToBuy.add (tCertificate);
		tCurrentPlayer = gameManager.getCurrentPlayer ();
		tCurrentPlayer.buyAction (tCertificatesToBuy);
		tCurrentPlayer.doneAction ();
	}

	public int getLastActionIndex () {
		return actionManager.getLastActionIndex ();
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
		return actionManager.isLastActionComplete ();
	}

	public void setFrameBackgrounds () {
		roundFrame.setFrameBackgrounds ();
	}

	public void resetBackgrounds () {
		roundFrame.resetBackgrounds ();
	}

	public void setListenerPanels (boolean aListen) {
		roundFrame.setListenerPanels (aListen);
	}
	
	public CorporationList getMinors () {
		return gameManager.getMinors ();
	}

	public CorporationList getPrivates () {
		return gameManager.getPrivates ();
	}

	public void handleQueryBenefits () {

	}

	/* Method will the Next Certificate Available for Purchase/Bid that is in the Bank
	 * 
	 * @return the Certificate from the Bank
	 */
	public Certificate getNextCertificateWithBid () {
		Bank tBank;
		Certificate tCertificate;
		
		tBank = gameManager.getBank ();
		tCertificate = tBank.getPrivateForAuction ();
		
		return tCertificate;
	}
	
	public boolean firstCertificateHasBidders () {
		boolean tNextShareHasBids;
		Bank tBank;
		
		tBank = gameManager.getBank ();
		tNextShareHasBids = tBank.firstCertificateHasBidders ();
		
		return tNextShareHasBids;
	}
}