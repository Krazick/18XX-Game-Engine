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
import ge18xx.company.TokenCompany;
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
import geUtilities.ParsingRoutine3I;

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
	
	public XMLFrame getXMLFrameNamed (String aXMLFrameTitle) {
		XMLFrame tXMLFrameFound;
		
		tXMLFrameFound = XMLFrame.NO_XML_FRAME;
		
		if (formationRound != Round.NO_ROUND) {
			tXMLFrameFound = formationRound.getXMLFrameNamed (aXMLFrameTitle);
		} else if (contractBidRound != Round.NO_ROUND) {
			tXMLFrameFound = contractBidRound.getXMLFrameNamed (aXMLFrameTitle);
		};

		return tXMLFrameFound;
	}
	
	public void initiateGame () {
		GameInfo tGameInfo;
		String tInitialRoundType;
		Round tInitialRound;
		
		tGameInfo = getGameInfo ();
		
		tInitialRoundType = tGameInfo.getInitialRoundType ();
		tInitialRound = getRoundByTypeName (tInitialRoundType);

		setCurrentRound (tInitialRound);
		setOtherRoundInfo ();
		tInitialRound.setIDPart1 (Round.START_ID1);
		tInitialRound.setIDPart2 (Round.START_ID2);
		
		tInitialRound.start ();
		tInitialRound.setStartingPlayer ();
	}

	public boolean isFirstStockRound () {
		boolean tIsFirstStockRound;

		if (isAStockRound ()) {
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

	public void setAuctionRound (AuctionRound aAuctionRound) {
		auctionRound = aAuctionRound;
	}

	public void setContractBidRound (ContractBidRound aContractBidRound) {
		contractBidRound = aContractBidRound;
	}

	public void setFormationRound (FormationRound aFormationRound) {
		formationRound = aFormationRound;
	}
	public void setOperatingRound (OperatingRound aOperatingRound) {
		operatingRound = aOperatingRound;
	}

	public void setStockRound (StockRound aStockRound) {
		stockRound = aStockRound;
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
		setOperatingRoundCount (0);
		addedOR = false;
	}

	public RoundFrame getRoundFrame () {
		return roundFrame;
	}

	public void setRoundFrame (RoundFrame aRoundFrame) {
		roundFrame = aRoundFrame;
	}

	public void addAction (Action aAction) {
	
		// If applying a Network Action, we do -NOT- Need to add the Action again. This
		// will double-up the Actions, and
		// During a Reload and Saved Network Game, this messes up the lastAction Number
		// locally.
		if (! applyingAction ()) {
			actionManager.addAction (aAction);
		}
		gameManager.autoSaveGame ();
		gameManager.setGameChanged (true);
	}

	public boolean checkAndHandleInterruption () {
		boolean tHandledInterruption;
		Action tLastAction;
		
		tLastAction = getLastAction ();
		if (tLastAction != Action.NO_ACTION) {
			tHandledInterruption = checkAndHandleInterruption (tLastAction);
		} else {
			tHandledInterruption = false;
		}
		
		return tHandledInterruption;
	}
	
	public boolean checkAndHandleInterruption (Action aAction) {
		Round tInterruptionRound;
		RoundType tCurrentRoundType;
		boolean tHandledInterruption;
		boolean tShouldInterrupt;
		String tActionName;
		String tInterruptRoundName;

		tCurrentRoundType = currentRound.getRoundType ();
		tInterruptRoundName = tCurrentRoundType.getInterruptionRound ();
		tHandledInterruption = false;
		if (tInterruptRoundName != GUI.NULL_STRING) {
			tActionName = aAction.getName ();
			tInterruptionRound = getRoundByTypeName (tInterruptRoundName);
			tShouldInterrupt = shouldInterrupt (tInterruptionRound, tActionName);
			if (tShouldInterrupt) {
				tInterruptionRound = getRoundByTypeName (tInterruptRoundName);
				tHandledInterruption = handleInterruption (tInterruptionRound);
			}
		}
		
		return tHandledInterruption;
	}

	public boolean shouldInterrupt (Round aInterruptionRound, String aActionName) {
		boolean tShouldInterrupt;
		boolean tInterruptsAfterAction;
		RoundType tInterruptionRoundType;
		String tInterruptsAfterActions;
		String tInterruptsCondition;

		tShouldInterrupt = false;
		tInterruptionRoundType = aInterruptionRound.getRoundType ();
		tInterruptsAfterActions = tInterruptionRoundType.getInterruptsAfterActions ();
		if (tInterruptsAfterActions != GUI.NULL_STRING) {
			tInterruptsAfterAction = tInterruptsAfterActions.contains (aActionName);
			if (tInterruptsAfterAction) {
				tInterruptsCondition = tInterruptionRoundType.getInterruptsCondition ();
				if (tInterruptsCondition == RoundType.NO_INTERRUPTS_CONDITION) {
					tShouldInterrupt = true;
				} else if (tInterruptsCondition.equals (RoundType.ALL_PLAYERS_PASSED)) {
					if (playerManager.haveAllPassed ()) {
						tShouldInterrupt = true;
					}
				}
			}
		}
		
		return tShouldInterrupt;
	}
	
	public boolean handleInterruption (Round aInterruptionRound) {
		boolean tIsInterrupting;
		boolean tInterruptionStarted;
		boolean tHandledInterruption;
		
		tIsInterrupting = aInterruptionRound.isInterrupting ();
		tInterruptionStarted = aInterruptionRound.interruptionStarted ();
		if (tIsInterrupting && !tInterruptionStarted) {
			aInterruptionRound.start ();
			tHandledInterruption = aInterruptionRound.interruptionStarted ();
		} else {
			tHandledInterruption = false;
		}
		
		return tHandledInterruption;
	}

	public boolean checkAndHandleInteruptionRoundEnds () {
		Action tLastAction;
		boolean tCurrentRoundEnds;

		tLastAction = getLastAction ();
		tCurrentRoundEnds = checkAndHandleRoundEnds (tLastAction);
		if (tCurrentRoundEnds) {
			tCurrentRoundEnds = checkAndHandleRoundEnds ();
		}
		
		return tCurrentRoundEnds;
	}

	public boolean checkAndHandleRoundEnds () {
		Action tLastAction;
		boolean tCurrentRoundEnds;
		boolean tCheckForEnding;
		
		tCheckForEnding = true;
		tCurrentRoundEnds = false;
		// Need to loop if the Operating Round only does automatic Private Pay Revenues, and no player 
		// interaction when the Round Starts, and then immediately Ends and will go to a different Round.
		while (tCheckForEnding) {
			tLastAction = getLastAction ();
			tCurrentRoundEnds = checkAndHandleRoundEnds (tLastAction);
			if (tCurrentRoundEnds) {
				startNextRound ();
			} else {
				tCheckForEnding = false;
			}
		}
		
		return tCurrentRoundEnds;
	}
	
	public boolean checkAndHandleRoundEnds (Action aAction) {
		boolean tCurrentRoundEnds;
		boolean tEndsAfterAction;
		RoundType tCurrentRoundType;
		String tActionName;
		String tEndsAfterActions;

		tCurrentRoundEnds = false;
		tCurrentRoundType = currentRound.getRoundType ();
		if (aAction != Action.NO_ACTION) {
			tActionName = aAction.getName ();
			tEndsAfterActions = tCurrentRoundType.getEndsAfterActions ();
			if (tEndsAfterActions != GUI.NULL_STRING) {
				tEndsAfterAction = tEndsAfterActions.contains (tActionName);
				if (tEndsAfterAction) {
					if (currentRound.ends ()) {
						currentRound.finish ();
						tCurrentRoundEnds = true;
					}
				}
			}
		}
		
		return tCurrentRoundEnds;
	}
	
	public void startNextRound () {
		Round tNextRound;
		
		tNextRound = currentRound.getNextRound ();
		tNextRound.start ();
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
		// TODO: Need to create a Bankrupt "Round" to hold this state 
//		setCurrentRoundState (ActorI.ActionStates.Bankrupt);
		updateRoundFrame ();
		roundFrame.toTheFront ();
		gameManager.resetRoundFrameBackgrounds ();
	}

	public void doneAction () {
		clearAllPlayerSelections ();
		gameManager.resetRoundFrameBackgrounds ();
		updateRoundFrame ();
		roundFrame.toTheFront ();
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

		tXMLElement = addRoundState (aXMLDocument, aEN_Type);
		
		return tXMLElement;
	}
	
	public XMLElement addRoundState (XMLDocument aXMLDocument, ElementName aEN_Type) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (aEN_Type);
		tXMLElement.setAttribute (AN_CURRENT_OR, currentOR);
		tXMLElement.setAttribute (AN_OR_COUNT, operatingRoundCount);
		tXMLElement.setAttribute (AN_ADDED_OR, addedOR);
		tXMLElement.setAttribute (AN_CURRENT_ROUND_TYPE, getCurrentRoundState ().toString ());
		
		getRoundState (aXMLDocument, tXMLElement, stockRound);
		getRoundState (aXMLDocument, tXMLElement, operatingRound);
		getRoundState (aXMLDocument, tXMLElement, auctionRound);
		getRoundState (aXMLDocument, tXMLElement, contractBidRound);
		getRoundState (aXMLDocument, tXMLElement, formationRound);
				
		return tXMLElement;
	}

	public void getRoundState (XMLDocument aXMLDocument, XMLElement aXMLElement, Round aRound) {
		XMLElement tXMLElement;
		
		if (gameManager.gameHasRoundType (aRound.getName ())) {
			tXMLElement = aRound.getRoundState (aXMLDocument);
			aXMLElement.appendChild (tXMLElement);
		}
	}

	public ActionManager getActionManager () {
		return actionManager;
	}
	
	public void updatePlayerListeners (String aMessage) {
		playerManager.updatePlayerListeners (aMessage);
	}
	
	public Round getRoundByTypeName (String aActorName) {
		Round tRound;

		tRound = (Round) getActor (aActorName);

		return tRound;
	}

	public ActorI getActor (String aActorName) {
		ActorI tActor;
		
		tActor = ActorI.NO_ACTOR;
		if (isRoundActor (stockRound, aActorName)) {
			tActor = stockRound;
		} else if (isRoundActor (operatingRound, aActorName)) {
			tActor = operatingRound;
		} else if (isRoundActor (auctionRound, aActorName)) {
			tActor = auctionRound;
		} else if (isRoundActor (formationRound, aActorName)) {
			tActor = formationRound;
		} else if (isRoundActor (contractBidRound, aActorName)) {
			tActor = contractBidRound;
		}

		return tActor;
	}

	public boolean isRoundActor (Round aRound, String aActorName) {
		boolean tIsRoundActor;
		
		if (aRound == Round.NO_ROUND) {
			tIsRoundActor = false;
		} else {
			if (aRound.isActor (aActorName)) {
				tIsRoundActor = true;
			} else {
				tIsRoundActor = false;
			}
		}
		
		return tIsRoundActor;
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
		return currentRound;
	}
	
	public String getCurrentRoundOf () {
		return roundFrame.getCurrentRoundOf ();
	}

	public ActorI.ActionStates getCurrentRoundState () {
		return currentRound.getRoundState ();
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

	public FormationRound getFormationRound () {
		return formationRound;
	}

	public String getOperatingOwnerName () {
		return operatingRound.getOperatingOwnerName ();
	}

	public String getOwnerWhoWillOperate () {
		TrainCompany tTrainCompany;
		String tPresidentName;

		tTrainCompany = operatingRound.getNextOperatingCompany ();
		if (tTrainCompany != TrainCompany.NO_TRAIN_COMPANY) {
			tPresidentName = tTrainCompany.getPresidentName ();
		} else {
			tPresidentName = Player.NO_NAME;
		}
		
		return tPresidentName;
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

		tRoundName = currentRound.getName ();

		return tRoundName;
	}

	public Benefit getBenefitWithName (String aPrivateAbbev, String aBenefitName) {
		Benefit tFoundBenefit;
		PrivateCompany tFoundPrivateCompany;
		
		tFoundPrivateCompany = gameManager.getPrivateCompany (aPrivateAbbev);
		tFoundBenefit = tFoundPrivateCompany.getBenefitNamed (aBenefitName);
		
		return tFoundBenefit;
	}
	
	public Corporation getSelectedPrivateCompanyToBuy () {
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

	public Train getTrain (int aTrainID) {
		Corporation tCorporation;
		TrainCompany tTrainCompany;
		Train tTrain;
		
		tCorporation = getOperatingCompany ();
		if (tCorporation.isATrainCompany ()) {
			tTrainCompany = (TrainCompany) tCorporation;
			tTrain = tTrainCompany.getTrainByID (aTrainID);
		} else {
			tTrain = Train.NO_TRAIN;
		}
		
		return tTrain;
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

	public boolean isAAuctionRound () {
		return (currentRound.getRoundState () == ActorI.ActionStates.AuctionRound);
	}

	public boolean isAContractBidRound () {
		return (currentRound.getRoundState () == ActorI.ActionStates.ContractBidRound);
	}

	public boolean isAOperatingRound () {
		return (currentRound.getRoundState () == ActorI.ActionStates.OperatingRound);
	}

	public boolean isAStockRound () {
		return (currentRound.getRoundState () == ActorI.ActionStates.StockRound);
	}

	public boolean isAFormationRound () {
		return (currentRound.getRoundState () == ActorI.ActionStates.FormationRound);
	}

	public boolean isBankrupt () {
		return (currentRound.getRoundState () == ActorI.ActionStates.Bankrupt);
	}

	public boolean isLastOR () {
		return (currentOR == operatingRoundCount);
	}

	public void loadActions (XMLNode aActionsNode, GameManager aGameManager) {
		actionManager.loadActions (aActionsNode, aGameManager);
	}

	public void loadRoundStates (XMLNode aRoundStateNode) {
		XMLNodeList tXMLNodeListA;
		XMLNodeList tXMLNodeListB;
		String tCurrentRoundName;
		Round tCurrentRound;
		boolean tAddedOR;
		int tCurrentOR;
		int tOperatingRoundCount;

		tCurrentOR = aRoundStateNode.getThisIntAttribute (AN_CURRENT_OR);
		tOperatingRoundCount = aRoundStateNode.getThisIntAttribute (AN_OR_COUNT, 1);
		tAddedOR = aRoundStateNode.getThisBooleanAttribute (AN_ADDED_OR);
		tCurrentRoundName = aRoundStateNode.getThisAttribute (AN_CURRENT_ROUND_TYPE);
		
		tCurrentRound = getRoundByTypeName (tCurrentRoundName);
		setCurrentRound (tCurrentRound);
		setCurrentOR (tCurrentOR);
		setOperatingRoundCount (tOperatingRoundCount);
		setAddedOR (tAddedOR);
		
		tXMLNodeListA = new XMLNodeList (roundParsingRoutineA);
		tXMLNodeListA.parseXMLNodeList (aRoundStateNode, Round.EN_STOCK_ROUND, Round.EN_OPERATING_ROUND);
		
		tXMLNodeListB = new XMLNodeList (roundParsingRoutineB);
		tXMLNodeListB.parseXMLNodeList (aRoundStateNode, Round.EN_AUCTION_ROUND, Round.EN_FORMATION_ROUND,
								Round.EN_CONTRACT_BID_ROUND);
	}

	ParsingRoutine2I roundParsingRoutineA = new ParsingRoutine2I () {
		@Override
		public void foundItemMatchKey1 (XMLNode aRoundNode) {
			stockRound.loadRound (aRoundNode);
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aRoundNode) {
			operatingRound.loadRound (aRoundNode);
		}
	};
	
	ParsingRoutine3I roundParsingRoutineB = new ParsingRoutine3I () {
		@Override
		public void foundItemMatchKey1 (XMLNode aRoundNode) {
			auctionRound.loadRound (aRoundNode);
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aRoundNode) {
			formationRound.loadRound (aRoundNode);
		}
		
		@Override
		public void foundItemMatchKey3 (XMLNode aRoundNode) {
			contractBidRound.loadRound (aRoundNode);
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
		System.out.println ("Currently this is a " + currentRound.getRoundState ());
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

	public int getCurrentOR () {
		return currentOR;
	}
	
	public void setCurrentPlayerLabel () {
		String tPlayerName;

		if (isAStockRound ()) {
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
		String tRoundID;
		int tORRoundID;
		int tARRoundID;
		int tFRRoundID;

		if (roundFrame != RoundFrame.NO_ROUND_FRAME) {
			operatingRound.sortByOperatingOrder ();

			if (isAStockRound ()) {
				updateAllCorporationsBox ();
				tRoundID = stockRound.getID ();
				roundFrame.setStockRoundInfo (gameName, tRoundID);
			} else if (isAOperatingRound ()) {
				tORRoundID = operatingRound.getIDPart1 ();
				roundFrame.setOperatingRound (gameName, tORRoundID, currentOR, operatingRoundCount);
				updateOperatingCorporationFrame ();
				operatingRound.updateActionLabel ();
			} else if (isAAuctionRound ()) {
				tARRoundID = auctionRound.getIDPart1 ();
				roundFrame.setAuctionRound (gameName, tARRoundID);
			} else if (isAFormationRound ()) {
				tFRRoundID = formationRound.getIDPart1 ();
				roundFrame.setFormationRound (gameName, tFRRoundID);
			}
			roundFrame.updateAll ();
		}
	}

	public void changeRound (Round aCurrentRound, ActorI.ActionStates aNewRoundState, Round aNewRound,
						String aOldRoundID, String aNewRoundID, ChangeRoundAction aChangeRoundAction) {
		ActorI.ActionStates tCurrentRoundState;
		ActorI.ActionStates tNewRoundState;

		tCurrentRoundState = getCurrentRoundState ();
		setCurrentRound (aNewRound);
		tNewRoundState = getCurrentRoundState ();
		currentRound.resume ();
		aChangeRoundAction.addStateChangeEffect (aCurrentRound, tCurrentRoundState, tNewRoundState);
		if (! aOldRoundID.equals (aNewRoundID)) {
			aChangeRoundAction.addChangeRoundIDEffect (aNewRound, aOldRoundID, aNewRoundID);
		}
		aChangeRoundAction.setChainToPrevious (true);
		roundFrame.updateAll ();
	}

	public void finishCurrentRound () {
		Action tLastAction;
		
		currentRound.finish ();
		roundFrame.updateAll ();
		if (! applyingAction ()) {
			tLastAction = getLastAction ();
			checkAndHandleInterruption (tLastAction);
		}
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
		roundFrame.setOperatingRound (gameName, aRoundIDPart1, currentOR, operatingRoundCount);
	}

	public void setStockRoundInfo (int aRoundIDPart1) {
		String tRoundID;
		
		tRoundID = aRoundIDPart1 + GUI.EMPTY_STRING;
		roundFrame.setStockRoundInfo (gameName, tRoundID);
	}

	public void startRound (ActorI.ActionStates aRoundState) {
		Round tRound;
		String tRoundStateName;
		
		tRoundStateName = aRoundState.toString ();
		tRound = getRoundByTypeName (tRoundStateName);
		tRound.start ();
	}
	
	public void updatePassButton () {
		roundFrame.updatePassButton ();		
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
		Player tPlayer;

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
			tPlayer = playerManager.getCurrentPlayer ();
			tPlayer.passAction ();

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
		checkAndHandleRoundEnds ();
	}

	// Game Manager handles with boolean Return

	public boolean canBuyTrainInPhase () {
		boolean tCanBuyTrainInPhase;

		tCanBuyTrainInPhase = gameManager.canBuyTrainInPhase ();

		return tCanBuyTrainInPhase;
	}

	public boolean bankIsBroken () {
		return gameManager.bankIsBroken ();
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

	// Operating Round handles with boolean Return
	
	public boolean companyStartedOperating () {
		return operatingRound.companyStartedOperating ();
	}

	// Action Manager handles with boolean Return
	
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

	public TokenCompany getTokenCompany (String aCompanyAbbrev) {
		TokenCompany tTokenCompany;

		tTokenCompany = gameManager.getShareCompany (aCompanyAbbrev);
		if (tTokenCompany == TokenCompany.NO_TOKEN_COMPANY) {
			tTokenCompany = gameManager.getMinorCompany (aCompanyAbbrev);
		}
		
		return tTokenCompany;
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
		if (roundFrame != XMLFrame.NO_XML_FRAME) {
			roundFrame.setListenerPanels (aListen);
		}
	}
	
	public CorporationList getMinors () {
		return gameManager.getMinors ();
	}

	public CorporationList getPrivates () {
		return gameManager.getPrivates ();
	}

	/* Method will get the Next Certificate Available for Purchase/Bid that is in the Bank
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