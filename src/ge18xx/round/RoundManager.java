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
import ge18xx.game.GameManager;
import ge18xx.game.XMLSaveGameI;
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
	StockRound stockRound;
	OperatingRound operatingRound;
	AuctionRound auctionRound;
	ActionManager actionManager;
	ActorI.ActionStates currentRoundType;
	RoundFrame roundFrame;
	Logger logger;
	int currentOR;
	int operatingRoundCount;
	boolean addedOR;
	String gameName;

	public RoundManager (GameManager aGameManager, PlayerManager aPlayerManager) {
		setManagers (aGameManager, aPlayerManager);
		logger = gameManager.getLogger ();
	}

	public Logger getLogger () {
		return logger;
	}

	public JMenuBar getJMenuBar () {
		return gameManager.getJMenuBar ();
	}
	
	public void initiateGame (CorporationList aPrivates, CorporationList aMinors,
			CorporationList aShares) {

		setRounds (aPrivates, aMinors, aShares);
		setOtherRoundInfo ();
		setRoundToStockRound (1);

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

	public void setRounds (CorporationList aPrivates, CorporationList aMinors,
			CorporationList aShares) {

		if (stockRound == StockRound.NO_STOCK_ROUND) {
			setStockRound (new StockRound (playerManager, this));
		}
		if (auctionRound == AuctionRound.NO_AUCTION_ROUND) {
			setAuctionRound (new AuctionRound (this));
			auctionRound.setAuctionRoundInAuctionFrame ();
		}
		if (operatingRound == OperatingRound.NO_OPERATING_ROUND) {
			setOperatingRound (new OperatingRound (this, aPrivates, aMinors, aShares));
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
		RoundFrame tRoundFrame;

		gameName = gameManager.getActiveGameName ();
		tFullTitle = gameManager.createFrameTitle (RoundFrame.BASE_TITLE);

		tRoundFrame = new RoundFrame (tFullTitle, this, gameManager);
		setRoundFrame (tRoundFrame);
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
		// If applying a Network Action, we do -NOT- Need to add the Action again. This
		// will double-up the Actions, and
		// During a Reload and Saved Network Game, this messes up the lastAction Number
		// locally.
		if (!gameManager.applyingAction ()) {
			actionManager.addAction (aAction);
		}
		gameManager.autoSaveGame ();
		gameManager.setGameChanged (true);
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
		currentRoundType = ActorI.ActionStates.Bankrupt;
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
		tXMLElement.setAttribute (AN_CURRENT_ROUND_TYPE, getCurrentRoundType ().toString ());
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

		tActor = ActorI.NO_ACTOR;
		if (stockRound.isActor (aActorName)) {
			tActor = stockRound;
		} else if (operatingRound.isActor (aActorName)) {
			tActor = operatingRound;
		} else if (auctionRound.isActor (aActorName)) {
			tActor = auctionRound;
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

	public String getCurrentRoundOf () {
		return roundFrame.getCurrentRoundOf ();
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

	public String getARType () {
		String tRoundType;

		tRoundType = ">>NO Auction Round Set<<";
		if (auctionRound != AuctionRound.NO_AUCTION_ROUND) {
			tRoundType = auctionRound.getType ();
		}

		return tRoundType;
	}

	public String getORType () {
		String tRoundType;

		tRoundType = ">>NO Operating Round Set<<";
		if (operatingRound != OperatingRound.NO_OPERATING_ROUND) {
			tRoundType = operatingRound.getType ();
		}

		return tRoundType;
	}

	public String getSRType () {
		String tRoundType;

		tRoundType = ">>NO Stock Round Set<<";
		if (stockRound != StockRound.NO_STOCK_ROUND) {
			tRoundType = stockRound.getType ();
		}

		return tRoundType;
	}

	public String getRoundType () {
		String tRoundType;

		if (currentRoundType == ActorI.ActionStates.StockRound) {
			tRoundType = getSRType ();
		} else if (currentRoundType == ActorI.ActionStates.OperatingRound) {
			tRoundType = getORType ();
		} else if (currentRoundType == ActorI.ActionStates.AuctionRound) {
			tRoundType = getARType ();
		} else if (currentRoundType == ActorI.ActionStates.Bankrupt) {
			tRoundType = "BANKRUPT";
		} else {
			tRoundType = "UNKOWN";
		}

		return tRoundType;
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

	public void incrementStockRound () {
		int tIDPart1;
		int tIDPart2;

		tIDPart1 = incrementRoundIDPart1 (stockRound);
		tIDPart2 = stockRound.getIDPart2 ();
		stockRound.setID (tIDPart1, tIDPart2);
		roundFrame.setFrameLabel (gameName, " " + tIDPart1);
	}

	public boolean isAAuctionRound () {
		return (currentRoundType == ActorI.ActionStates.AuctionRound);
	}

	public boolean isOperatingRound () {
		return (currentRoundType == ActorI.ActionStates.OperatingRound);
	}

	public boolean isStockRound () {
		return (currentRoundType == ActorI.ActionStates.StockRound);
	}

	public boolean isBankrupt () {
		return (currentRoundType == ActorI.ActionStates.Bankrupt);
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

	public void setRoundToOperatingRound () {
		setRoundType (ActorI.ActionStates.OperatingRound);
	}

	public void setRoundToStockRound () {
		setRoundType (ActorI.ActionStates.StockRound);
	}

	public void setRoundToOperatingRound (int aRoundIDPart1, int aRoundIDPart2) {
		String tOldOperatingRoundID;
		String tNewOperatingRoundID;
		Round tCurrentRound;
		boolean tCreateNewAction;

		tCreateNewAction = true;
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
		changeRound (tCurrentRound, ActorI.ActionStates.OperatingRound, operatingRound, tOldOperatingRoundID,
				tNewOperatingRoundID, tCreateNewAction);
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

	public void changeRound (Round aCurrentRound, ActorI.ActionStates aNewRoundType, Round aNewRound,
								String aOldRoundID, String aNewRoundID, boolean aCreateNewAction) {
		ActorI.ActionStates tCurrentRoundType;
		ActorI.ActionStates tNewRoundType;
		ChangeRoundAction tChangeRoundAction;
		AuctionRound tAuctionRound;
		XMLFrame tAuctionFrame;
		String tRoundID;

		tRoundID = aCurrentRound.getID ();
		tCurrentRoundType = getCurrentRoundType ();
		setRoundType (aNewRoundType);
		tNewRoundType = getCurrentRoundType ();

		if (!applyingAction ()) {
			if (aCreateNewAction) {
				if (!tRoundID.equals ("0.0")) {
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

	public void updateAllListenerPanels () {
		roundFrame.updateAllListenerPanels ();
	}
	
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

	public void setRoundToStockRound (int aRoundIDPart1) {
		String tOldRoundID;
		String tNewRoundID;
		boolean tCreateNewAction = true;

		tOldRoundID = stockRound.getID ();
		stockRound.setID (aRoundIDPart1, 1);
		tNewRoundID = stockRound.getID ();
		changeRound (operatingRound, ActorI.ActionStates.StockRound, stockRound, tOldRoundID, tNewRoundID,
				tCreateNewAction);

		stockRound.clearAllPlayerPasses ();

		roundFrame.setStockRoundInfo (gameName, aRoundIDPart1);
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

	public void showFrame () {
		roundFrame.setVisible (true);
	}

	public void showAuctionFrame () {
		auctionRound.showAuctionFrame ();
	}

	public void showCurrentPlayerFrame () {
		stockRound.showCurrentPlayerFrame ();
	}

	public boolean companyStartedOperating () {
		return operatingRound.companyStartedOperating ();
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

	public void resetOperatingRound (int aRoundIDPart1, int aRoundIDPart2) {
		setRoundType (ActorI.ActionStates.OperatingRound);
		roundFrame.setOperatingRound (gameName, aRoundIDPart1, currentOR, operatingRoundCount);
	}

	public void resumeStockRound (int aRoundIDPart1) {
		setRoundType (ActorI.ActionStates.StockRound);
		roundFrame.setStockRoundInfo (gameName, aRoundIDPart1);
	}

	public void startAuctionRound (boolean aCreateNewAuctionAction) {
		setRoundToAuctionRound (aCreateNewAuctionAction);
		auctionRound.startAuctionRound ();
		roundFrame.updatePassButton ();
	}

	public void startOperatingRound () {
		int tIDPart1;
		int tIDPart2;

		tIDPart1 = incrementRoundIDPart1 (operatingRound);
		tIDPart2 = 1;
		setRoundToOperatingRound (tIDPart1, tIDPart2);
		playerManager.clearAllPlayerDividends ();
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
			setRoundToOperatingRound (tIDPart1, currentOR + 1);
			operatingRound.startOperatingRound ();
		}
	}

	public void startStockRound () {
		int tIDPart1;

		if (bankIsBroken ()) {
			System.out.println ("GAME OVER -- Bank is Broken, Don't do any more Stock Rounds");
		}
		tIDPart1 = incrementRoundIDPart1 (stockRound);
		setRoundToStockRound (tIDPart1);
		gameManager.bringMarketToFront ();
		stockRound.prepareStockRound ();
		roundFrame.updateAll ();
		updateAllListenerPanels ();
	}

	public boolean bankIsBroken () {
		return gameManager.bankIsBroken ();
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

	public boolean applyingAction () {
		return gameManager.applyingAction ();
	}

	public boolean canStartOperatingRound () {
		return gameManager.canStartOperatingRound ();
	}

	public boolean operatingRoundIsDone () {
		return operatingRound.roundIsDone ();
	}

	public boolean undoLastAction () {
		return actionManager.undoLastAction (this);
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

	public boolean wasLastActionStartAuction () {
		return actionManager.wasLastActionStartAuction ();
	}

	public void sendToReportFrame (String aReport) {
		actionManager.appendBorderedReport (aReport);
	}

	public boolean canBuyTrainInPhase () {
		boolean tCanBuyTrainInPhase;

		tCanBuyTrainInPhase = gameManager.canBuyTrainInPhase ();

		return tCanBuyTrainInPhase;
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

	public CorporationList getShareCompanies () {
		CorporationList tShareCompanies;
		
		tShareCompanies = gameManager.getShareCompanies ();
		
		return tShareCompanies;
	}
	
	public void setParPrice (ShareCompany aShareCompany, int aParPrice) {
		gameManager.setParPrice (aShareCompany, aParPrice);
	}

	public void updateActionLabel (TrainCompany aTrainCompany) {
		String tDoActionLabel;
		
		tDoActionLabel = "DO THIS COMPANY";
		if (aTrainCompany.shouldOperate ()) {
			tDoActionLabel = aTrainCompany.getDoLabel ();
		}
		if (aTrainCompany.isOperating ()) {
			tDoActionLabel = aTrainCompany.getOperatingLabel ();
		}
		setButtonLabel (tDoActionLabel);
		if (isNetworkAndIsThisClient (aTrainCompany.getPresidentName ())) {
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

	public boolean isLoading () {
		return gameManager.gameIsStarted ();
	}

	public void handleQueryBenefits () {

	}
}