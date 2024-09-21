package ge18xx.game;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.NodeList;

import checksum.Checksum;
import checksum.ChecksumCalc;
//import checksum.Checksums;

import ge18xx.toplevel.ChecksumAuditFrame;
import ge18xx.toplevel.Checksums;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.StartPacketFrame;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.CorporationList;
import ge18xx.company.Coupon;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.MinorCompany;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TrainCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.formation.FormationPhase;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.userPreferences.UserPreferencesFrame;
import ge18xx.map.HexMap;
import ge18xx.market.Market;
import ge18xx.network.GameSupportHandler;
import ge18xx.network.JGameClient;
import ge18xx.network.NetworkGameSupportTier2;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Bidder;
import ge18xx.player.Bidders;
import ge18xx.player.Escrow;
import ge18xx.player.ParPriceFrame;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.toplevel.AuctionFrame;
import ge18xx.toplevel.AuditFrame;

import ge18xx.toplevel.CitiesFrame;
import ge18xx.toplevel.MapFrame;
import ge18xx.toplevel.MarketFrame;
import ge18xx.toplevel.MinorCompaniesFrame;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.toplevel.PrivatesFrame;
import ge18xx.toplevel.ShareCompaniesFrame;
import ge18xx.toplevel.TileDefinitionFrame;
import ge18xx.toplevel.TileTrayFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.game.GameManager;
import geUtilities.GameFrameConfig;
import geUtilities.FileGEFilter;
import geUtilities.FileUtils;
import geUtilities.FrameInfoSupport;
import geUtilities.GUI;
import geUtilities.JFileMChooser;
import geUtilities.MessageBean;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.GameEngineManager;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLSaveGameI;

// TODO -- Create an abstract 'GameEngineManager' Super Class that holds non-specific Game information like:
//   GameInfo, PlayerManager, configFrames, PlayerInputFrame, frameInfo, and the other non-specific
//   objects flagged below. Move the methods that use only these objects to the super-class
//
// Also create a 'NetworkGameManager' Sub-class that implements the NetworkGameSupport objects and
// methods into this sub-class

public class GameManager extends GameEngineManager implements NetworkGameSupportTier2 {

	// Static Constants
	public static final ElementName EN_CONFIG = new ElementName ("Config");
	public static final ElementName EN_SAVEGAMEDIR = new ElementName ("SaveGameDir");
	public static final ElementName EN_GAME = new ElementName ("Game");
	public static final ElementName EN_FRAMES = new ElementName ("Frames");
	public static final AttributeName AN_GAME_NAME = new AttributeName ("gameName");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_GE_VERSION = new AttributeName ("version");
	public static final GameManager NO_GAME_MANAGER = null;
	public static final boolean ADD_CHECKSUM = true;
	public static final String NO_GAME_NAME = "<NONE>";
	public static final String UNSPECIFIED_GAME_NAME = "UNSPECIFIED";
	public static final String NO_FILE_NAME = "<NONE>";
	public static final String AUTO_SAVES_DIR = "autoSaves";
	public static final String EMPTY_GAME_ID = GUI.EMPTY_STRING;

	// Generic (non-game specific objects)
	ArrayList<XMLFrame> configFrames;
	JFileMChooser chooser;
	File saveFile;
	File autoSaveFile;
	File autoSaveActionReportFile;
	File loadSavedFile;
	String autoSaveFileName;
	String autoSaveActionReportFileName;
	String clientUserName;
	String gameID;
	String userDir;
	boolean gameChangedSinceSave;
	boolean gameStarted;
	boolean gameEnding;
	Config configData;
	Logger logger;
	FileUtils fileUtils;
	FileGEFilter fileGEFilter;
	Checksums checksums;

	// 18XX Game Specific Objects
	GameInfo activeGame;
	Game_18XX game18XXFrame;
	PlayerManager playerManager;
	RoundManager roundManager;
	PhaseManager phaseManager;
	BankPool bankPool;
	Bank bank;
	TriggerClass triggerClass;
	FormationPhase formationPhase;
	ColorPalette biddersPalette;

	// Various Frames the Game Manager tracks -- 
	// Consider adding to a "FrameManager" Class
	// These Frames for Companies, have the CorporationList
	//
	// Consider having an Array/List of CorporationLists, that tracks what
	// type of Company it is, and has the Frame in the Corporation List
	//
	PrivatesFrame privatesFrame;
	MinorCompaniesFrame minorCompaniesFrame;
	ShareCompaniesFrame shareCompaniesFrame;
	UserPreferencesFrame userPreferencesFrame;
	
	AuctionFrame auctionFrame;
	AuditFrame auditFrame;
	ChecksumAuditFrame checksumAuditFrame;
	CitiesFrame citiesFrame;
	FrameInfoSupport frameInfoFrame;
	MapFrame mapFrame;
	MarketFrame marketFrame;
	PlayerInputFrame playerInputFrame;
	TileDefinitionFrame tileDefinitionFrame;
	TileTrayFrame tileTrayFrame;

	// Other Frames include:
		// RoundFrame 				-- held by RoundManager
		// TrainRevenueFrame 		-- held by TrainCompany
		// ParPriceFrame				-- held by PlayerManager
		// BuyItemFrame extends to:
			// BuyPrivateFrame			-- held by ShareCompany, in BuyPrivate Method
			// BuyTrainFrame				-- held by TrainCompany, in BuyOtherTrain Method
		// ForceBuyCouponFrame	extends to:
			// ForceBuyTrainFrame		-- held by TrainCompany, in ForceBuyTrain Method
			// Loan Interest Payment		-- held by ShareCompany, in ForceBuyCoupon Method
			// Loan Redemption Payment	-- held by ShareCompany, in ForceBuyCoupon Method

	// Network Game Objects
	JGameClient networkJGameClient;		// Extends XMLFrame
	SavedGames networkSavedGames;
	boolean notifyNetwork;
	boolean applyingNetworkAction;
	List<MessageBean> allBeans;

	public GameManager () {
		fileUtils = new FileUtils ("18xx.");
		fileGEFilter = new FileGEFilter ("18XX Save Game - XML", fileUtils);
		setUserDir ();
		setDefaults ();
		setGameInfo (GameInfo.NO_GAME_INFO);
	}

	public GameManager (String aClientUserName) {
		this ();
		setClientUserName (aClientUserName);
	}

	public GameManager (Game_18XX aGame_18XX_Frame, String aClientUserName) {
		this (aClientUserName);
		storeAllFrames (aGame_18XX_Frame);
		logger = game18XXFrame.getLogger ();

		setBankPool (BankPool.NO_BANK_POOL);
		setBank (Bank.NO_BANK_CASH);
		setPlayerManager (PlayerManager.NO_PLAYER_MANAGER);
		setPhaseManager (PhaseManager.NO_PHASE_MANAGER);
	}

	@Override
	public String getEnvironmentVersionInfo () {
		return game18XXFrame.getEnvironmentVersionInfo ();
	}

	@Override
	public FileUtils getFileUtils () {
		return fileUtils;
	}

	@Override
	public Logger getLogger () {
		return logger;
	}

	public JMenuBar getJMenuBar () {
		return game18XXFrame.getJMenuBar ();
	}
	
	private void storeAllFrames (Game_18XX aGame_18XX_Frame) {
		configFrames = new ArrayList<XMLFrame> ();
		addNewFrame (aGame_18XX_Frame);
		game18XXFrame = aGame_18XX_Frame;
		setPlayerInputFrame (PlayerInputFrame.NO_PLAYER_INPUT_FRAME);

		setMapFrame (XMLFrame.NO_XML_FRAME);
		setCitiesFrame (XMLFrame.NO_XML_FRAME);
		setPrivatesFrame (XMLFrame.NO_XML_FRAME);
		setMinorCompaniesFrame (XMLFrame.NO_XML_FRAME);
		setShareCompaniesFrame (XMLFrame.NO_XML_FRAME);
		setTileTrayFrame (XMLFrame.NO_XML_FRAME);
		setTileDefinitionFrame (XMLFrame.NO_XML_FRAME);
		setAuditFrame (XMLFrame.NO_XML_FRAME);
		setUserPreferencesFrame (XMLFrame.NO_XML_FRAME);
	}

	private void setUserDir () {
		userDir = System.getProperty ("user.dir");
	}

	private void setDefaults () {
		setLoadSavedFile (null);
		saveFile = null;
		autoSaveFile = null;
		autoSaveActionReportFileName = null;
		gameStarted = false;
		gameEnding = false;
		notifyNetwork = false;
		applyingNetworkAction = false;
		gameID = EMPTY_GAME_ID;
	}

	public String getUserDir () {
		return userDir;
	}

	public void setTriggerClass (TriggerClass aTriggerClass) {
		triggerClass = aTriggerClass;
	}
	
	public TriggerClass getTriggerClass () {
		return triggerClass;
	}
	
	public void setLoadSavedFile (File aLoadSavedFile) {
		loadSavedFile = aLoadSavedFile;
	}

	public JPanel buildPrivatesForPurchaseJPanel (ItemListener aItemListener, int aAvailableCash) {
		JPanel tPrivatesJPanel;

		tPrivatesJPanel = privatesFrame.buildPrivatesForPurchaseJPanel (aItemListener, aAvailableCash);

		return tPrivatesJPanel;
	}

	public boolean noTouchPass () {
		return activeGame.noTouchPass ();
	}
	
	public boolean canSellPresidentShare () {
		return activeGame.canSellPresidentShare ();
	}

	public boolean canBeExchanged (Corporation aCorporation) {
		return playerManager.canBeExchanged (aCorporation);
	}

	public boolean canPayHalfDividend () {
		return activeGame.canPayHalfDividend ();
	}

	public boolean canStartOperatingRound () {
		return bank.canStartOperatingRound ();
	}

	public void applyDiscount () {
		bank.applyDiscount ();
	}

	public void clearAllAuctionStates () {
		playerManager.clearAllAuctionStates ();
	}

	public void clearBankSelections () {
		bank.clearSelections ();
		bankPool.clearSelections ();
	}

	public void clearCorpSelections () {
		privatesFrame.clearSelections ();
		minorCompaniesFrame.clearSelections ();
		shareCompaniesFrame.clearSelections ();
	}

	public void clearAllPlayerSelections () {
		playerManager.clearAllPlayerSelections ();
	}

	public void closeCompany (int aCompanyID, TransferOwnershipAction aTransferOwnershipAction) {
		privatesFrame.closeCompany (aCompanyID, aTransferOwnershipAction);
	}

	private void createCities () {
		String tXMLCitiesName;
		String tActiveGameName;
		String tFullFrameTitle;
		CitiesFrame tCitiesFrame;
		XMLDocument tXMLDocument;
		String tType;

		if (gameIsStarted ()) {
			tType = "Cities";
			tActiveGameName = getActiveGameName ();
			tXMLCitiesName = getCitiesFileName ();
			tXMLCitiesName = getXMLBaseDirectory () + tXMLCitiesName;
			tFullFrameTitle = createFrameTitle (tType);
			tCitiesFrame = new CitiesFrame (tFullFrameTitle, this);
			setCitiesFrame (tCitiesFrame);
			tXMLDocument = readXMLfromURL (tActiveGameName, tType);
			try {
				tCitiesFrame.loadXML (tXMLDocument, tCitiesFrame.getCities ());
			} catch (Exception tException) {
				logger.error (tException);
			}
		}
	}

	public XMLDocument readXMLfromURL (String tActiveGameName, String tType) {
		XMLDocument tXMLDocument;
		String tFullURL;
		
		tFullURL = constructFullURL (tActiveGameName, tType);
		tXMLDocument = new XMLDocument (tFullURL);
		
		return tXMLDocument;
	}

	private String constructFullURL (String aActiveGameName, String aType) {
		String tFullURL;
		String tURLBase;
		
		tURLBase = game18XXFrame.getURLBase ();
		tFullURL = tURLBase + aActiveGameName + "/" + aActiveGameName + "-" + aType + ".xml";
		
		return tFullURL;
	}

	public void setAuctionFrame (AuctionFrame aAuctionFrame) {
		auctionFrame = aAuctionFrame;
	}

	public String getPrivateAbbrevForAuction () {
		Certificate tCertificate = bank.getPrivateForAuction ();

		return tCertificate.getCompanyAbbrev ();
	}

	public ParPriceFrame buildParPriceFrame (Certificate aCertificate) {
		Player tCurrentPlayer;

		tCurrentPlayer = getCurrentPlayer ();

		return playerManager.buildParPriceFrame (tCurrentPlayer, aCertificate);
	}

	public boolean handleSetParPrice (Certificate aCertificate, int aSelectedParPrice, ParPriceFrame aParPriceFrame) {
		Corporation tCorporation;
		ShareCompany tShareCompany;
		boolean tParPriceSet;

		tCorporation = aCertificate.getCorporation ();
		if (tCorporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) tCorporation;
		} else {
			tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		}
		if ((aSelectedParPrice > 0) && (tShareCompany != ShareCompany.NO_SHARE_COMPANY)) {
			tParPriceSet = true;
			setParPrice (tShareCompany, aSelectedParPrice);
			aParPriceFrame.setParValueAction (aSelectedParPrice, tShareCompany, true);
		} else {
			tParPriceSet = false;
		}

		return tParPriceSet;
	}

	public void addPrivateToAuction () {
		Certificate tCertificate;
		Certificate tFreeCertificate;

		tCertificate = bank.getPrivateForAuction ();
		tFreeCertificate = bank.getFreeCertificateWithThisCertificate (tCertificate);
		auctionFrame.addPrivateToAuction (tCertificate, tFreeCertificate);
	}

	@Override
	public boolean gameStarted () {
		return gameStarted;
	}

	@Override
	public String createFrameTitle (String aBaseName) {
		String tFullTitle;

		tFullTitle = getActiveGameName () + " " + aBaseName + " Frame";
		if (userPreferencesFrame != XMLFrame.NO_XML_FRAME) {
			if (userPreferencesFrame.showClientNameInFrameTitle ()) {
				if (isNetworkGame ()) {
					tFullTitle += " (" + clientUserName + ")";
				}
			}
		}
		
		return tFullTitle;
	}

	public void createUserPreferencesFrame () {
		UserPreferencesFrame tUserPreferencesFrame;
		String tFullTitle;

		tFullTitle = "User Preferences Frame";
		tUserPreferencesFrame = new UserPreferencesFrame (tFullTitle, this);
		setUserPreferencesFrame (tUserPreferencesFrame);
		game18XXFrame.enableUserPreferencesMenuItem ();
		loadConfig ();
	}
	
	private void createAuditFrame () {
		AuditFrame tAuditFrame;
		String tFullTitle;

		if (gameIsStarted ()) {
			tFullTitle = createFrameTitle ("Audit");
			tAuditFrame = new AuditFrame (tFullTitle, this);
			setAuditFrame (tAuditFrame);
		}
	}
	
	private void createChecksumAuditFrame () {
		ChecksumAuditFrame tChecksumAuditFrame;
		String tFullTitle;

		if (gameIsStarted ()) {
			tFullTitle = createFrameTitle ("Checksum Audit");
			tChecksumAuditFrame = new ChecksumAuditFrame (tFullTitle, this);
			setChecksumAuditFrame (tChecksumAuditFrame);
		}
	}

	public ArrayList<XMLFrame> getConfigFrames () {
		return configFrames;
	}

	private void createFrameInfoFrame () {
		FrameInfoSupport tFrameInfoFrame;
		String tFullTitle;

		if (gameIsStarted ()) {
			tFullTitle = createFrameTitle ("Frame Info");

			tFrameInfoFrame = new FrameInfoSupport (tFullTitle, this, configFrames);
			setFrameInfoFrame (tFrameInfoFrame);
			userPreferencesFrame.setFrameInfoPanel (tFrameInfoFrame.getFrameInfoPanel ());
		}
	}

	private void createMap () {
		String tBaseDir;
		String tFullTitle;
		MapFrame tMapFrame;
		String tActiveGameName;
		String tType;
		XMLDocument tXMLDocument;

		if (gameIsStarted ()) {
			tType = "Map";
			tActiveGameName = getActiveGameName ();
			tBaseDir = getXMLBaseDirectory ();
			tFullTitle = createFrameTitle (tType);
			tMapFrame = new MapFrame (tFullTitle, this);
			setMapFrame (tMapFrame);
			tMapFrame.setTileSet (tileTrayFrame.getTileSet ());
			tXMLDocument = readXMLfromURL (tActiveGameName, tType);
			try {
				tMapFrame.loadXML (tXMLDocument, tMapFrame.getMap ());
			} catch (Exception tException) {
				logger.error (tException);
				tException.printStackTrace ();
			}
			loadColorScheme (tBaseDir, tMapFrame);

			tMapFrame.setCityInfo (citiesFrame.getCities ());
			tMapFrame.addCorporationList (privatesFrame, CorporationList.TYPE_NAMES [0]);
			tMapFrame.addCorporationList (minorCompaniesFrame, CorporationList.TYPE_NAMES [1]);
			tMapFrame.addCorporationList (shareCompaniesFrame, CorporationList.TYPE_NAMES [2]);
			tMapFrame.setDestinationCorpIDs (shareCompaniesFrame);
		}
	}

	public void loadColorScheme (String aBaseDir, MapFrame aMapFrame) {
		String tFullURL;
		XMLDocument tXMLDocument;
		
		tFullURL = game18XXFrame.getURLBase () + "Color-Scheme.xml";
		tXMLDocument = new XMLDocument (tFullURL);

		try {
			aMapFrame.loadXMLColorScheme (tXMLDocument, aMapFrame.getTerrain ());
			aMapFrame.loadXMLColorScheme (tXMLDocument, tileTrayFrame.getTileType ());
			
			biddersPalette = new ColorPalette (Bidders.EN_BIDDERS, Bidder.EN_BIDDER);
			biddersPalette.loadXML (tXMLDocument, biddersPalette);
			
		} catch (Exception tException) {
			logger.error ("Problem Loading Color Scheme: " + tException);
			tException.printStackTrace ();
		}
	}

	public ColorPalette getBiddersPalette () {
		return biddersPalette;
	}

	private void createMarket () {
		String tFullTitle;
		MarketFrame tMarketFrame;
		String tActiveGameName;
		String tType;
		XMLDocument tXMLDocument;
		
		if (gameIsStarted ()) {
			tType = "Market";
			tActiveGameName = getActiveGameName ();
			tFullTitle = createFrameTitle (tType);
			tMarketFrame = new MarketFrame (tFullTitle, this);
			setMarketFrame (tMarketFrame);
			tXMLDocument = readXMLfromURL (tActiveGameName, tType);
			try {
				tMarketFrame.loadXML (tXMLDocument, tMarketFrame.getMarket ());
			} catch (Exception tException) {
				logger.error (tException);
				tException.printStackTrace ();
			}
		}
	}

	private void createMinorCompanies () {
		String tFullTitle;
		MinorCompaniesFrame tMinorCompaniesFrame;
		String tActiveGameName;
		String tType;
		XMLDocument tXMLDocument;

		if (gameIsStarted ()) {
			tType = Corporation.COMPANIES;
			tActiveGameName = getActiveGameName ();
			tFullTitle = createFrameTitle (tType);
			tMinorCompaniesFrame = new MinorCompaniesFrame (tFullTitle, roundManager);
			setMinorCompaniesFrame (tMinorCompaniesFrame);
			tXMLDocument = readXMLfromURL (tActiveGameName, tType);
			try {
				tMinorCompaniesFrame.loadXML (tXMLDocument, tMinorCompaniesFrame.getCompanies ());
				tMinorCompaniesFrame.fillCertificateInfo (this);
			} catch (Exception tException) {
				logger.error (tException);
				tException.printStackTrace ();
			}
		}
	}

	private void createPrivateCompanies () {
		String tXMLCompaniesName;
		String tFullTitle;
		PrivatesFrame tPrivatesFrame;
		String tActiveGameName;
		String tType;
		XMLDocument tXMLDocument;

		if (gameIsStarted ()) {
			tType = Corporation.COMPANIES;
			tActiveGameName = getActiveGameName ();
			tXMLCompaniesName = getCompaniesFileName ();
			tXMLCompaniesName = getXMLBaseDirectory () + tXMLCompaniesName;
			tFullTitle = createFrameTitle (tType);
			tPrivatesFrame = new PrivatesFrame (tFullTitle, roundManager);
			setPrivatesFrame (tPrivatesFrame);
			tXMLDocument = readXMLfromURL (tActiveGameName, tType);
			try {
				tPrivatesFrame.loadXML (tXMLDocument, tPrivatesFrame.getCompanies ());
				tPrivatesFrame.fillCertificateInfo (this);
			} catch (Exception tException) {
				logger.error (tException);
				tException.printStackTrace ();
			}
		}
	}

	private void createShareCompanies () {
		String tXMLCompaniesName;
		String tFullTitle;
		Integer [] tParValues;
		Market tMarket;
		ShareCompaniesFrame tShareCompaniesFrame;
		String tActiveGameName;
		String tType;
		XMLDocument tXMLDocument;
		int tLoanAmount;
		int tLoanInterest;
		CorporationList tCorporationList;

		if (gameIsStarted ()) {
			tType =  Corporation.COMPANIES;
			tActiveGameName = getActiveGameName ();
			tXMLCompaniesName = getCompaniesFileName ();
			tXMLCompaniesName = getXMLBaseDirectory () + tXMLCompaniesName;
			tFullTitle = createFrameTitle (tType);
			tShareCompaniesFrame = new ShareCompaniesFrame (tFullTitle, roundManager);
			setShareCompaniesFrame (tShareCompaniesFrame);
			tXMLDocument = readXMLfromURL (tActiveGameName, tType);
			try {
				tCorporationList = tShareCompaniesFrame.getCompanies ();
				tShareCompaniesFrame.loadXML (tXMLDocument, tCorporationList);
				tMarket = marketFrame.getMarket ();
				tLoanAmount = activeGame.getLoanAmount ();
				tLoanInterest = activeGame.getLoanInterest ();
				tShareCompaniesFrame.setLoanInfo (tLoanAmount, tLoanInterest);
				tShareCompaniesFrame.setMarket (tMarket);
				tShareCompaniesFrame.setStartCells ();
				tShareCompaniesFrame.updateCorpComboBox ();
				tParValues = tMarket.getAllStartCells ();
				tShareCompaniesFrame.updateParValuesComboBox (tParValues);
				tShareCompaniesFrame.fillCertificateInfo (this);
			} catch (Exception tException) {
				logger.error (tException);
				tException.printStackTrace ();
			}
		}
	}

	/**
	 * Get the Minimum Number of Shares that must be sold to allow a Company to
	 * Float
	 *
	 * @return the Minimum Number of Shares needed to float
	 */
	public int getMinSharesToFloat () {
		String tNextTrainName;
		Coupon tTrain;
		int tMinSharesToFloat;

		tTrain = bank.getNextAvailableTrain ();
		if (tTrain == Train.NO_TRAIN) {
			tMinSharesToFloat = PhaseInfo.STANDARD_MIN_SHARES;
		} else {
			tNextTrainName = tTrain.getName ();
			tMinSharesToFloat = phaseManager.getMinSharesToFloat (tNextTrainName);
		}
		
		return tMinSharesToFloat;
	}

	public Integer [] getAllStartCells () {
		Market tMarket;

		tMarket = marketFrame.getMarket ();

		return tMarket.getAllStartCells ();
	}

	public int getMaxRounds () {
		return activeGame.getMaxRounds ();
	}
		
	private void createTileTray () {
		String tActiveGameName;
		TileTrayFrame tTileTrayFrame;
		TileDefinitionFrame tTileDefinitionFrame;
		String tFrameTitle;
		String tType;
		XMLDocument tXMLDocument;
		String tURLBase;

		if (gameIsStarted ()) {
			tActiveGameName = getActiveGameName ();
			tFrameTitle = createFrameTitle (TileTrayFrame.BASE_TITLE);
			tTileTrayFrame = new TileTrayFrame (tFrameTitle, this);
			setTileTrayFrame (tTileTrayFrame);
			
			tType = "TileSet";
			tXMLDocument = readXMLfromURL (tActiveGameName, tType);
			tTileTrayFrame.loadTileTrayFrame (tXMLDocument);

			tFrameTitle = createFrameTitle (TileDefinitionFrame.BASE_TITLE);
			tTileDefinitionFrame = new TileDefinitionFrame (tFrameTitle, tTileTrayFrame, this);
			setTileDefinitionFrame (tTileDefinitionFrame);
			
			tURLBase = game18XXFrame.getURLBase ();
			tTileDefinitionFrame.loadAllTileDefinitions (tURLBase, tTileTrayFrame);
		}
	}

	/**
	 * Load a specific tile Number with a Quantity from the specified Set, into the Tile Tray Frame.
	 *
	 * @param aSetName The Name of the Tile Set (from the list of Tile Types that have specific XML Definition Files)
	 * @param aTileNumber The Tile Number to load
	 * @param aQuantity The Quantity to Add to the Tile Tray.
	 *
	 */
	public void loadATileFromASet (String aSetName, int aTileNumber, int aQuantity) {
		String tBaseDirName;
		String tURLBase;
		
		tBaseDirName = getXMLBaseDirectory ();
		tURLBase = game18XXFrame.getURLBase ();
		tileDefinitionFrame.loadATileFromASet (tURLBase, tBaseDirName, tileTrayFrame, aTileNumber, aSetName, aQuantity);
	}

	public boolean canBuyPrivate () {
		return phaseManager.canBuyPrivate ();
	}

	public boolean gameHasLoans () {
		return activeGame.gameHasLoans ();
	}
	
	public boolean loansAllowed () {
		boolean tLoansAllowed;
		
		tLoansAllowed = false;
		if (phaseManager != PhaseManager.NO_PHASE_MANAGER) {
			tLoansAllowed = phaseManager.loansAllowed ();
		}
		
		return tLoansAllowed;
	}
	
	public void enterPlaceTileMode () {
		mapFrame.togglePlaceTileMode ();
	}

	public void enterPlaceTokenMode () {
		mapFrame.togglePlaceTokenMode ();
	}

	public boolean isPlaceTileMode () {
		return mapFrame.isPlaceTileMode ();
	}

	public boolean isPlaceTokenMode () {
		return mapFrame.isPlaceTokenMode ();
	}

	public void enterSelectRouteMode (RouteInformation aRouteInformation) {
		mapFrame.enterSelectRouteMode (aRouteInformation);
	}

	public void exitSelectRouteMode () {
		mapFrame.exitSelectRouteMode ();
	}

	public StockRound getStockRound () {
		return roundManager.getStockRound ();
	}
	
	public void fullOwnershipAdjustment () {
		marketFrame.fullOwnershipAdjustment (getStockRound ());
	}

	public boolean gameChanged () {
		return gameChangedSinceSave;
	}

	public boolean gameHasPrivates () {
		return activeGame.hasPrivates ();
	}

	public boolean gameHasMinors () {
		return activeGame.hasMinors ();
	}

	public boolean gameHasShares () {
		return activeGame.hasShares ();
	}

	public boolean gameIsSaved () {
		return (saveFile != null);
	}

	public boolean gameIsStarted () {
		return (activeGame != GameInfo.NO_GAME_INFO);
	}

	public GameInfo getActiveGame () {
		return activeGame;
	}

	@Override
	public String getActiveGameName () {
		String tName;

		tName = "";
		if (gameIsStarted ()) {
			tName = activeGame.getName ();
		}

		return tName;
	}

	public ActorI getActor (String aActorName) {
		ActorI tFoundActor;
		
		tFoundActor = getActor (aActorName, false);
		
		return tFoundActor;
	}

	public ActorI getActor (String aActorName, boolean aLookingForPrivate) {
		ActorI tActor;
		String tBankName;
		String tBankPoolName;
		String tStartPacketFrameName;
		StartPacketFrame tStartPacketFrame;
		
		tActor = ActorI.NO_ACTOR;
		tBankName = bank.getName ();
		tBankPoolName = bankPool.getName ();
		tStartPacketFrame = bank.getStartPacketFrame ();
		tStartPacketFrameName = tStartPacketFrame.getName ();
		if (aActorName == ActorI.NO_NAME) {
			logger.error ("Actor Name IS NULL<-----");
		} else {
			if (aActorName.equals (tBankName)) {
				tActor = bank;
			} else if (aActorName.equals (tBankPoolName)) {
				tActor = bankPool;
			} else if (aActorName.equals (tStartPacketFrameName)) {
				tActor = bank.getStartPacketFrame ();
			} else {
				if (playerManager != PlayerManager.NO_PLAYER_MANAGER) {
					tActor = playerManager.getActor (aActorName);
				}
			}

			if (tActor == ActorI.NO_ACTOR) {
				if (shareCompaniesFrame != ShareCompaniesFrame.NO_SHARES_FRAME) {
					tActor = shareCompaniesFrame.getActor (aActorName);
				}
			}

			if (aLookingForPrivate || (tActor == ActorI.NO_ACTOR)) {
				if (privatesFrame != PrivatesFrame.NO_PRIVATES_FRAME) {
					tActor = privatesFrame.getActor (aActorName);
				}
			}

			if (tActor == ActorI.NO_ACTOR) {
				if (minorCompaniesFrame != MinorCompaniesFrame.NO_MINORS_FRAME) { 
					tActor = minorCompaniesFrame.getActor (aActorName);
				}
			}

			if (tActor == ActorI.NO_ACTOR) {
				if (roundManager != RoundManager.NO_ROUND_MANAGER) {
					tActor = roundManager.getActor (aActorName);
				}
			}
		}

		return tActor;
	}

	public Bank getBank () {
		return bank;
	}

	public BankPool getBankPool () {
		return bankPool;
	}

	public Portfolio getBankPortfolio () {
		return bank.getPortfolio ();
	}

	public Portfolio getBankPoolPortfolio () {
		Portfolio tBankPoolPortfolio;

		tBankPoolPortfolio = bankPool.getPortfolio ();

		return tBankPoolPortfolio;
	}

	public String getCitiesFileName () {
		return getFileName (File18XX.CITIES_TYPE);
	}

	public CitiesFrame getCitiesFrame () {
		return citiesFrame;
	}

	public Certificate getCertificate (String aCompanyAbbrev, int aPercentage, boolean aPresidentShare) {
		Certificate tCertificate;

		tCertificate = privatesFrame.getCertificate (aCompanyAbbrev, aPercentage, aPresidentShare);
		if (tCertificate == Certificate.NO_CERTIFICATE) {
			tCertificate = minorCompaniesFrame.getCertificate (aCompanyAbbrev, aPercentage, aPresidentShare);
		}
		if (tCertificate == Certificate.NO_CERTIFICATE) {
			tCertificate = shareCompaniesFrame.getCertificate (aCompanyAbbrev, aPercentage, aPresidentShare);
		}

		return tCertificate;
	}

	public int getFormingCompanyId () {
		return phaseManager.getFormingCompanyId ();
	}
	
	public String getCompaniesFileName () {
		return getFileName (File18XX.COMPANIES_TYPE);
	}

	public Corporation getCorporationByName (String aCorporationName) {
		Corporation tCorporation;

		tCorporation = privatesFrame.getCorporationByAbbrev (aCorporationName);
		if (tCorporation == Corporation.NO_CORPORATION) {
			tCorporation = getActingCorporationByName (aCorporationName);
		}

		return tCorporation;
	}

	public Corporation getCorporationByAbbrev (String aCorporationAbbrev) {
		Corporation tCorporation;

		tCorporation = minorCompaniesFrame.getCorporationByAbbrev (aCorporationAbbrev);
		if (tCorporation == Corporation.NO_CORPORATION) {
			tCorporation = shareCompaniesFrame.getCorporationByAbbrev (aCorporationAbbrev);
		}

		return tCorporation;
	}

	public Corporation getActingCorporationByName (String aCorporationName) {
		Corporation tCorporation;

		tCorporation = minorCompaniesFrame.getCorporationByName (aCorporationName);
		if (tCorporation == Corporation.NO_CORPORATION) {
			tCorporation = shareCompaniesFrame.getCorporationByName (aCorporationName);
		}

		return tCorporation;
	}

	public Player getPresidentFromPlayers (Corporation aCorporation) {
		Player tPlayerWhoIsPresident;
		
		tPlayerWhoIsPresident = playerManager.getPresidentFromPlayers (aCorporation);
		
		return tPlayerWhoIsPresident;
	}
	
	public Corporation getCorporationByID (int aCorporationID) {
		Corporation tCorporation;

		tCorporation = privatesFrame.getCorporationByID (aCorporationID);
		if (tCorporation == Corporation.NO_CORPORATION) {
			tCorporation = minorCompaniesFrame.getCorporationByID (aCorporationID);
		}
		if (tCorporation == Corporation.NO_CORPORATION) {
			tCorporation = shareCompaniesFrame.getCorporationByID (aCorporationID);
		}

		return tCorporation;
	}

	public ActorI.ActionStates getCorporationState (String aCorpStateName) {
		return shareCompaniesFrame.getCorporationState (aCorpStateName);
	}

	public int getCountOfOpenPrivates () {
		return privatesFrame.getCountOfOpen ();
	}

	public int getCountOfPlayerOwnedPrivates () {
		return privatesFrame.getCountOfPlayerOwnedCompanies ();
	}

	public int getCountOfSelectedPrivates () {
		return privatesFrame.getCountOfSelectedCertificates ();
	}

	public String getCurrentOffBoard () {
		return phaseManager.getCurrentOffBoard ();
	}

	public Escrow getEscrowMatching (String aEscrowName) {
		return playerManager.getEscrowMatching (aEscrowName);
	}

	public PrivateCompany getSelectedPrivateCompanyToBuy () {
		Corporation tSelectedCorporation;
		PrivateCompany tSelectedPrivateCompany;

		tSelectedCorporation = privatesFrame.getSelectedCorporation ();
		if (tSelectedCorporation == Corporation.NO_CORPORATION) {
			tSelectedPrivateCompany = PrivateCompany.NO_PRIVATE_COMPANY;
		} else {
			if (tSelectedCorporation.isAPrivateCompany ()) {
				tSelectedPrivateCompany = (PrivateCompany) tSelectedCorporation;
			} else {
				tSelectedPrivateCompany = PrivateCompany.NO_PRIVATE_COMPANY;
			}
		}

		return tSelectedPrivateCompany;
	}

	public CorporationList getMinors () {
		return minorCompaniesFrame.getCompanies ();
	}

	public CorporationList getPrivates () {
		return privatesFrame.getCompanies ();
	}

	public CorporationList getShareCompanies () {
		return shareCompaniesFrame.getCompanies ();
	}

	public ShareCompany getShareCompany (String aCompanyAbbrev) {
		return shareCompaniesFrame.getShareCompany (aCompanyAbbrev);
	}

	public ShareCompany getShareCompanyByID (int aCompanyID) {
		return shareCompaniesFrame.getShareCompanyByID (aCompanyID);
	}

	public MinorCompany getMinorCompany (String aCompanyAbbrev) {
		return minorCompaniesFrame.getMinorCompany (aCompanyAbbrev);
	}

	public PrivateCompany getPrivateCompany (String aCompanyAbbrev) {
		return privatesFrame.getPrivateCompany (aCompanyAbbrev);
	}

	public int getCountOfMinors () {
		int tCountOfMinors = 0;

		if (minorCompaniesFrame != MinorCompaniesFrame.NO_MINORS_FRAME) {
			tCountOfMinors = minorCompaniesFrame.getCountOfCompanies ();
		}

		return tCountOfMinors;
	}

	public int getCountOfPrivates () {
		int tCountOfPrivates = 0;

		if (privatesFrame != PrivatesFrame.NO_PRIVATES_FRAME) {
			tCountOfPrivates = privatesFrame.getCountOfCompanies ();
		}

		return tCountOfPrivates;
	}

	public int getCountOfShares () {
		int tCountOfShares = 0;

		if (shareCompaniesFrame != ShareCompaniesFrame.NO_SHARES_FRAME) {
			tCountOfShares = shareCompaniesFrame.getCountOfCompanies ();
		}

		return tCountOfShares;
	}

	public int getCountOfCanOperate () {
		int tCountOfShares = 0;

		if (shareCompaniesFrame != ShareCompaniesFrame.NO_SHARES_FRAME) {
			tCountOfShares = shareCompaniesFrame.getCountOfOperatingCompanies ();
		}

		return tCountOfShares;
	}

	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;

		tCurrentHolder = bank.getCurrentHolder (aLoadedCertificate);
		if (tCurrentHolder == PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
			tCurrentHolder = bankPool.getCurrentHolder (aLoadedCertificate);
		}

		return tCurrentHolder;
	}

	public String getFileName (String aType) {
		String tFileName;

		tFileName = NO_FILE_NAME;
		if (gameIsStarted ()) {
			tFileName = activeGame.getFileNameFor (aType);
		}

		return tFileName;
	}

	public String getGameName () {
		String tGameName;

		if (activeGame == GameInfo.NO_GAME_INFO) {
			tGameName = NO_GAME_NAME;
		} else {
			tGameName = activeGame.getGameName ();
		}

		return tGameName;
	}

	public AuctionFrame getAuctionFrame () {
		return auctionFrame;
	}

	public AuditFrame getAuditFrame () {
		return auditFrame;
	}

	public ChecksumAuditFrame getChecksumAuditFrame () {
		return checksumAuditFrame;
	}
	
	public MapFrame getMapFrame () {
		return mapFrame;
	}

	public HexMap getGameMap () {
		return mapFrame.getMap ();
	}

	public String getMapFileName () {
		return getFileName (File18XX.MAP_TYPE);
	}

	public Market getMarket () {
		return marketFrame.getMarket ();
	}

	public String getMarketFileName () {
		return getFileName (File18XX.MARKET_TYPE);
	}

	public MarketFrame getMarketFrame () {
		return marketFrame;
	}

	public CorporationList getMinorCompanies () {
		return minorCompaniesFrame.getCompanies ();
	}

	public MinorCompaniesFrame getMinorCompaniesFrame () {
		return minorCompaniesFrame;
	}

	public PhaseManager getPhaseManager () {
		return phaseManager;
	}

	@Override
	public PlayerInputFrame getPlayerInputFrame () {
		return playerInputFrame;
	}

	public PlayerManager getPlayerManager () {
		return playerManager;
	}

	public Player getCurrentPlayer () {
		return playerManager.getCurrentPlayer ();
	}

	public ActorI.ActionStates getPlayerState (String aState) {
		return playerManager.getPlayerState (aState);
	}

	public PrivatesFrame getPrivatesFrame () {
		return privatesFrame;
	}

	public RoundManager getRoundManager () {
		return roundManager;
	}

	public ActorI.ActionStates getRoundType (String aRoundTypeString) {
		GenericActor tGenericActor;

		tGenericActor = new GenericActor ();

		return tGenericActor.getRoundType (aRoundTypeString);
	}

	public ShareCompaniesFrame getShareCompaniesFrame () {
		return shareCompaniesFrame;
	}

	public Tile getTile (int aTileNumber) {
		return tileTrayFrame.getTile (aTileNumber);
	}

	public TileDefinitionFrame getTileDefinitionFrame () {
		return tileDefinitionFrame;
	}

	public String getTileSetFileName () {
		return getFileName (File18XX.TILE_SET_TYPE);
	}

	public TileSet getTileSet () {
		return tileTrayFrame.getTileSet ();
	}

	public XMLFrame getTileTrayFrame () {
		return tileTrayFrame;
	}

	public Token getToken (String aCompanyAbbrev) {
		return shareCompaniesFrame.getToken (aCompanyAbbrev);
	}

	public int getMinorTrainLimit () {
		return phaseManager.getMinorTrainLimit ();
	}

	public int getTrainLimit (boolean aGovtRailway) {
		return phaseManager.getTrainLimit (aGovtRailway);
	}

	public int getMinorTileLays () {
		return phaseManager.getMinorTileLays ();
	}
	
	public int getMajorTileLays () {
		return phaseManager.getMajorTileLays ();
	}
	
	public Train getBankPoolTrain (String aTrainName) {
		return bankPool.getTrain (aTrainName);
	}

	public Train getTrain (String aTrainName) {
		return bank.getTrain (aTrainName);
	}

	@Override
	public String getXMLBaseDirectory () {
		String aBaseDirectory;

		aBaseDirectory = "18XX XML Data" + File.separator;

		return aBaseDirectory;
	}

	public void checkForDestinationsReached () {
		shareCompaniesFrame.checkForDestinationsReached ();
	}

	public boolean hasDestinations () {
		return shareCompaniesFrame.hasDestinations ();
	}
	
	public boolean hasLicenses() {
		return activeGame.gameHasLicenses ();
	}
	
	public boolean hasMustBuyCertificate () {
		return bank.hasMustBuyCertificate ();
	}

	public boolean hasMustSell () {
		StartPacketFrame tStartPacketFrame;
		boolean tHasMustSell;
		
		tStartPacketFrame = bank.getStartPacketFrame ();
		tHasMustSell = tStartPacketFrame.hasMustSell ();
		
		return tHasMustSell;
//		return bank.hasMustSell ();
	}

	public Certificate getMustSellCertificate () {
		return bank.getMustSellCertificate ();
	}

	@Override
	public GameInfo getSelectedGame () {
		return playerInputFrame.getSelectedGame ();
	}

	@Override
	public void initiateNetworkGame (String aGameID) {
		GameInfo tSelectedGame;
		
		tSelectedGame = getSelectedGame ();
		tSelectedGame.setGameID (aGameID);
		initiateGame (tSelectedGame);
	}

	@Override
	public XMLElement getGameVariantsXMLElement () {
		XMLElement tGameVariantsXMLElement;
		XMLDocument tXMLDocument;

		tXMLDocument = new XMLDocument ();
		tGameVariantsXMLElement = activeGame.getGameVariantsXMLElement (tXMLDocument);

		return tGameVariantsXMLElement;
	}

	public int getCapitalizationLevel (int aSharesSold) {
		int tCapitalizationLevel;
		Coupon tNextTrain;
		String tNextTrainName;
		
		tNextTrain = bank.getNextAvailableTrain ();
		tNextTrainName = tNextTrain.getName ();
		tCapitalizationLevel = activeGame.getCapitalizationLevel (aSharesSold, tNextTrainName);
		
		return tCapitalizationLevel;
	}

	public void initiateGame (GameInfo aGameInfo) {
		setGameID (aGameInfo.getGameID ());
		setGameInfo (aGameInfo);
		initiateGame ();
		activateCommunications ();
		// For Normal Start, we need to Notify Clients of Actions
		// The Loading of a Save Game does it's own reset once the Load is complete
	}

	public void activateCommunications () {
		setNotifyNetwork (true);
		roundManager.setListenerPanels (true);
		activateAllBeans (true);
		flushAllBeanMessages ();
	}

	public void removeInactiveCompanies () {
		CorporationList tPrivates;
		CorporationList tMinors;
		CorporationList tShares;

		tPrivates = getPrivates ();
		tPrivates.removeInactiveCompanies ();
		tMinors = getMinorCompanies ();
		tMinors.removeInactiveCompanies ();
		tShares = getShareCompanies ();
		tShares.removeInactiveCompanies ();

	}

	public void initiateGame () {
		CorporationList tPrivates;
		CorporationList tMinors;
		CorporationList tShares;
		PhaseManager tPhaseManager;
		PlayerManager tPlayerManager;

		if (activeGame != GameInfo.NO_GAME_INFO) {
			createUserPreferencesFrame ();
			if (playerManager == PlayerManager.NO_PLAYER_MANAGER) {
				tPlayerManager = new PlayerManager (this);
				setPlayerManager (tPlayerManager);
			}
			roundManager = new RoundManager (this, playerManager);
			setupGamePieces ();
			setGameChanged (true);
			setupBank ();
			tPhaseManager = activeGame.getPhaseManager ();
			tPhaseManager.setCurrentPhase (PhaseManager.FIRST_PHASE);
			setPhaseManager (tPhaseManager);
			activeGame.setupVariants ();
			activeGame.applyActiveVariantEffects (this);
			activeGame.setGameManager (this);
			removeInactiveCompanies ();
			playerInputFrame.randomizePlayerOrder ();
			setupPlayers ();
			tPrivates = getPrivates ();
			tMinors = getMinorCompanies ();
			tShares = getShareCompanies ();
			collectAllBeans ();
			autoSaveFileName = constructAutoSaveFileName (AUTO_SAVES_DIR);
			autoSaveActionReportFileName = constructASARFileName (AUTO_SAVES_DIR, ".action.txt");
			autoSaveFile = new File (autoSaveFileName);
			autoSaveActionReportFile = new File (autoSaveActionReportFileName);
			roundManager.initiateGame (tPrivates, tMinors, tShares);
			if (!activeGame.isATestGame ()) {
				roundManager.showInitialFrames ();
			}

			logger.info ("Game has started with AutoSave Name " + autoSaveFileName);
			
			gameStarted = true;
			createAuditFrame ();
			createChecksumAuditFrame ();
			applyConfigSettings ();
			createFrameInfoFrame ();
			setFrameBackgrounds ();
			game18XXFrame.initiateGame ();
		}
	}

	private void collectAllBeans () {
		MessageBean tBean;
		
		allBeans = new LinkedList<MessageBean> ();
		tBean = bank.getMessageBean ();
		addGoodBean (tBean);
		tBean = bankPool.getMessageBean ();
		addGoodBean (tBean);
		playerManager.addMessageBeans ();
		shareCompaniesFrame.addMessageBeans ();
		minorCompaniesFrame.addMessageBeans ();
		privatesFrame.addMessageBeans ();
	}
	
	public void addGoodBean (MessageBean aBean) {
		if (aBean != MessageBean.NO_BEAN) {
			allBeans.add (aBean);
		}
	}
	
	public void activateAllBeans (boolean aActiveFlag) {
		for (MessageBean tBean : allBeans) {
			tBean.setActive (aActiveFlag);
		}
	}
	
	public void flushAllBeanMessages () {
		for (MessageBean tBean : allBeans) {
			tBean.flushMessages ();
		}
	}
	
	public void sendAllBeanMessages () {
		for (MessageBean tBean : allBeans) {
			tBean.sendMessages ();
		}
	}

	public void setBank (int aInitialTreasury) {
		bank = new Bank (aInitialTreasury, this);
	}

	public void setBank (Bank aBank) {
		bank = aBank;
	}
	
	public void setBankPool (BankPool aBankPool) {
		bankPool = aBankPool;
	}

	private void setupBank () {
		int tBankTotal;

		bankPool = new BankPool (this);
		tBankTotal = getBankStartingCash ();
		setBank (tBankTotal);
		bank.setup (activeGame);
	}

	public int getBankStartingCash () {
		int tBankStartingCash;
		int tPlayerCount;
		int tPlayerStartingCash;
		int tTotalPlayerCash;

		tBankStartingCash = activeGame.getBankTotal ();
		tPlayerCount = playerManager.getPlayerCount ();
		tPlayerStartingCash = activeGame.getStartingCash (tPlayerCount);
		tTotalPlayerCash = tPlayerCount * tPlayerStartingCash;
		tBankStartingCash -= tTotalPlayerCash;

		return tBankStartingCash;
	}

	private String constructAutoSaveFileName (String aDirectoryName) {
		String tAutoSaveFileName;

		tAutoSaveFileName = constructASARFileName (aDirectoryName, ".save" + fileUtils.xml);

		return tAutoSaveFileName;
	}

	private String constructASARFileName (String aDirectoryName, String aSuffix) {
		String tAutoSaveFileName;

		if (isNetworkGame ()) {
			tAutoSaveFileName = constructAutoSaveNetworkDir (aDirectoryName) + getGameName () + "." + getGameID () + "."
					+ clientUserName;
		} else {
			tAutoSaveFileName = aDirectoryName + File.separator + getGameName () + "." + clientUserName;
		}
		tAutoSaveFileName += aSuffix;

		return tAutoSaveFileName;
	}

	private String constructAutoSaveNetworkDir (String tDirectoryName) {
		String tASNDir;

		tASNDir = tDirectoryName + File.separator + "network" + File.separator;

		return tASNDir;
	}

	private File getSelectedFile (File aDirectory, JFileMChooser aChooser, boolean aSaveFile) {
		File tSelectedFile = null;
		int tResult;
		boolean tNotChosenYet = true;
		File tDirectory = aDirectory;

		aChooser.addChoosableFileFilter (fileGEFilter);
		aChooser.setAcceptAllFileFilterUsed (true);
		aChooser.setFileSelectionMode (JFileChooser.FILES_AND_DIRECTORIES);

		while (tNotChosenYet) {
			aChooser.setCurrentDirectory (tDirectory);
			if (aSaveFile) {
				tResult = chooser.showSaveDialog (null);
			} else {
				tResult = chooser.showOpenDialog (null);
			}
			if (tResult == JFileChooser.APPROVE_OPTION) {
				tSelectedFile = chooser.getSelectedFile ();
				if (tSelectedFile.isDirectory ()) {
					tDirectory = tSelectedFile;
				} else {
					tNotChosenYet = false;
				}
			} else {
				tSelectedFile = null;
				tNotChosenYet = false;
			}
		}

		return tSelectedFile;
	}

	public int getStartingCash () {
		int tStartingCash;

		tStartingCash = activeGame.getStartingCash (playerManager.getPlayerCount ());

		return tStartingCash;
	}

	public void loadSavedGame () {
		File tSaveDirectory;
		File tNewSaveDirectory;
		String tSaveDirectoryPath;
		Point tNewPoint;

		tNewPoint = getOffsetGEFrame ();
		chooser = new JFileMChooser ();
		chooser.setDialogTitle ("Find Saved Game File to Load");
		createUserPreferencesFrame ();
		tSaveDirectoryPath = configData.getSaveGameDirectory ();
		if (tSaveDirectoryPath == null) {
			tSaveDirectoryPath = "";
			configData.setSaveGameDirectory (tSaveDirectoryPath);
		}
		tSaveDirectory = new File (tSaveDirectoryPath);
		chooser.setCurrentDirectory (tSaveDirectory);
		chooser.setAcceptAllFileFilterUsed (true);
		chooser.setFileSelectionMode (JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMLocation (tNewPoint);
		setLoadSavedFile (getSelectedFile (tSaveDirectory, chooser, false));

		if (loadSavedFile != null) {
			tNewSaveDirectory = chooser.getCurrentDirectory ();
			if (!tSaveDirectory.equals (tNewSaveDirectory)) {
				configData.setSaveGameDirectory (tNewSaveDirectory.toString ());
			}
			loadSavedXMLFile ();
			if (isOperatingRound ()) {
				playerManager.hideAllPlayerFrames ();
			}
		} else {
			logger.info ("No Saved File selected.");
		}
	}

	@Override
	public void loadAutoSavedGame (String aSavedGameFile) {
		String tAutoSavesDir;
		String tFullAutoSaveFilePath;
		File tAutoSaveFile;

		tAutoSavesDir = constructAutoSaveNetworkDir (AUTO_SAVES_DIR);
		tFullAutoSaveFilePath = tAutoSavesDir + File.separator + aSavedGameFile;
		tAutoSaveFile = new File (tFullAutoSaveFilePath);
		setLoadSavedFile (tAutoSaveFile);
		loadSavedXMLFile ();
		handleMissedActions ();
		updateRoundFrame ();
		playerManager.hideAllPlayerFrames ();
	}

	private void handleMissedActions () {
		int tLastLocalAction;
		int tLastNetworkAction;
		int tNextActionNumber;
		int tActionNumber;
		String tNextAction;

		tLastLocalAction = roundManager.getLastActionNumber ();
		tLastNetworkAction = networkJGameClient.getAutoSavedLastAction ();
		if (tLastNetworkAction > tLastLocalAction) {
			tNextActionNumber = tLastLocalAction + 1;
			for (tActionNumber = tNextActionNumber; tActionNumber <= tLastNetworkAction; tActionNumber++) {
				tNextAction = networkJGameClient.fetchActionWithNumber (tActionNumber, getGameID ());
				handleNetworkAction (tNextAction);
			}
		}
	}

	public void printDateTime (String aLabel) {
		Date tNow;
		String tDateTime;
		
		tNow = new Date ();
		tDateTime = GUI.formatDateTime (aLabel, tNow);
		System.out.println (tDateTime);
	}
	
	private boolean loadSavedXMLFile () {
		List<ActionStates> tAuctionStates;
		boolean tGoodLoad;
		int tLastActionNumber;
		String tGameName;
		
		tGoodLoad = false;
		setNotifyNetwork (false);
		if (loadXMLFile (loadSavedFile)) {
			if (isNetworkGame ()) {
				tLastActionNumber = roundManager.getLastActionNumber ();
				tGameName = activeGame.getGameName ();
				networkJGameClient.setGameIDonServer (gameID, tLastActionNumber,tGameName);
			}
			/* Once a Game has been loaded, can enable both Save and Save As Menu Items */
			game18XXFrame.disableGameStartItems ();
			game18XXFrame.enableSaveMenuItems ();
			setGameChanged (false);
			playerManager.updateAllRFPlayerLabels ();
			roundManager.updateAllCorporationsBox ();
			roundManager.setCurrentPlayerLabel ();
			if (roundManager.isAAuctionRound ()) {
				// Save the Auction States since 'AddPrivateToAuction' will reset the Player
				// Auction States Reset After adding the Private to Auction.
				tAuctionStates = playerManager.getPlayerAuctionStates ();
				addPrivateToAuction ();
				playerManager.resetPlayerAuctionStates (tAuctionStates);
			}
			bank.updateBankCashLabel ();
			tGoodLoad = true;
			logger.info ("Load of file " + loadSavedFile.getName () + " Succeeded." + " Players ["
					+ playerManager.getPlayersInOrder () + "]");
		}
		activateCommunications ();

		return tGoodLoad;
	}

	@Override
	public void parseNetworkSavedGames (String aNetworkSavedGames) {
		String tAutoSavesDir;

		networkSavedGames = new SavedGames (aNetworkSavedGames, this);
		tAutoSavesDir = constructAutoSaveNetworkDir (AUTO_SAVES_DIR);
		networkSavedGames.setAllLocalAutoSaveFound (tAutoSavesDir);
		// TODO Have this method call build the Panel in this package (maybe part of
		// SavedGames?)
		// Call the JGameClient to set the networkSavedGamesPanel that is built.
		networkJGameClient.buildNetworkSGPanel (networkSavedGames);
	}

	public boolean loadXMLFile (File aSaveGame) {
		boolean tXMLFileWasLoaded;
		XMLDocument tXMLDocument;
		
		if (aSaveGame != null) {
			try {
				tXMLDocument = new XMLDocument (aSaveGame);
				tXMLFileWasLoaded = loadXMLSavedGame (tXMLDocument);
			} catch (Exception eException) {
				logger.error ("Oops, mucked up the XML Save Game File Access [" + aSaveGame.getName () + "].");
				logger.error ("Exception Message [" + eException.getMessage () + "].", eException);
				tXMLFileWasLoaded = false;
			}
		} else {
			logger.error ("No File Object for XML Save Game");
			tXMLFileWasLoaded = false;
		}

		return tXMLFileWasLoaded;
	}

	public boolean loadXMLSavedGame (XMLDocument aXMLDocument) throws IOException {
		XMLNode tXMLSaveGame;
		NodeList tChildren;
		int tChildrenCount;
		int tIndex;
		boolean tLoadedSaveGame;
		boolean tGameIdentified;
		boolean tPlayersLoaded;
		boolean tGameInitiated;

		tGameIdentified = false;
		tPlayersLoaded = false;
		tGameInitiated = false;
		tLoadedSaveGame = false;
		if (aXMLDocument.validDocument ()) {
			playerManager = new PlayerManager (this); 
			/** Create a new Player Manager - repeated openings, should not add players to an existing set */
			
			activeGame = null;

			tXMLSaveGame = aXMLDocument.getDocumentNode ();
			tChildren = tXMLSaveGame.getChildNodes ();
			tChildrenCount = tChildren.getLength ();
			for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
				if (tGameIdentified && tPlayersLoaded && !tGameInitiated) {
					initiateGame ();
					tGameInitiated = true;
				}
				parseChildNode (tChildren, tIndex, tGameInitiated);
				if (gameID != null) {
					if (gameID != EMPTY_GAME_ID) {
						tGameIdentified = true;
					}
				}
				if (playerManager.getPlayerCount () > 1) {
					tPlayersLoaded = true;
				}
			}
			if (isInCompanyFormationState ()) {
				prepareFormationPhase ();
				showFormationPhaseFrame ();
			}
			clearClosedCorporations ();
			fixLoadedRoutes ();
			updateStartPacket ();
			if ((activeGame != GameInfo.NO_GAME_INFO) && (playerManager.getPlayerCount () > 1)) {
				tLoadedSaveGame = true;
			}
		}
		updateRoundFrame ();

		return tLoadedSaveGame;
	}

	public boolean isInCompanyFormationState () {
		boolean tIsInCompanyFormationState;
		
		if (formationPhase == FormationPhase.NO_FORMATION_PHASE) {
			tIsInCompanyFormationState = false;
		} else if (playerManager.isInCompanyFormationState ()) {
			tIsInCompanyFormationState = true;
		} else {
			tIsInCompanyFormationState = false;			
		}
		
		return tIsInCompanyFormationState;
	}
	
	private void clearClosedCorporations () {
		CorporationList tShareCompanies;
		
		tShareCompanies = getShareCompanies ();
		tShareCompanies.clearClosedCorporations ();
	}
	
	private void parseChildNode (NodeList aChildren, int aIndex, boolean aGameInitiated) {
		XMLNode tChildNode;
		String tChildName;

		tChildNode = new XMLNode (aChildren.item (aIndex));
		tChildName = tChildNode.getNodeName ();
		if (!tChildName.equals (XMLNode.XML_TEXT_TAG)) {
			if (JGameClient.EN_NETWORK_GAME.equals (tChildName)) {
				if (networkJGameClient == JGameClient.NO_JGAME_CLIENT) {
					loadNetworkJGameClient (tChildNode);
				}
			}
			if (GameInfo.EN_GAME_INFO.equals (tChildName)) {
				loadGameInfo (tChildNode);
			} else if (PlayerInputFrame.EN_PLAYERS.equals (tChildName)) {
				playerManager.loadPlayers (tChildNode, activeGame);
			} else if (PhaseManager.EN_PHASE.equals (tChildName)) {
				phaseManager.loadPhase (tChildNode);
			}
			if (aGameInitiated) {
				handleIfGameInitiated (tChildNode, tChildName);
			}
		}
	}

	private void loadGameInfo (XMLNode aGameInfoNode) {
		String tSaveGameName;
		GameSet tGameSet;
		String tGameID;

		tGameSet = playerInputFrame.getGameSet ();
		tSaveGameName = aGameInfoNode.getThisAttribute (AN_NAME);
		activeGame = tGameSet.getGameByName (tSaveGameName);
		tGameID = aGameInfoNode.getThisAttribute (GameInfo.AN_GAME_ID);
		setGameID (tGameID);
		activeGame.setGameID (tGameID);
		activeGame.loadVariantEffects (aGameInfoNode);
	}

	@Override
	public GameSet getGameSet () {
		GameSet tGameSet;
		
		tGameSet = playerInputFrame.getGameSet ();
		
		return tGameSet;
	}
	
	public void handleIfGameInitiated (XMLNode aChildNode, String aChildName) {
		FormationPhase tFormationPhase;
		String tFormationState;
		
		if (Action.EN_ACTIONS.equals (aChildName)) {
			roundManager.loadActions (aChildNode, this);
		} else  if (RoundManager.EN_ROUNDS.equals (aChildName)) {
			roundManager.loadRoundStates (aChildNode);
		} else if (Player.EN_PLAYER_STATES.equals (aChildName)) {
			playerManager.loadPlayerStates (aChildNode);
		} else if (Bank.EN_BANK_STATE.equals (aChildName)) {
			bank.loadBankState (aChildNode);
		} else if (BankPool.EN_BANK_POOL_STATE.equals (aChildName)) {
			bankPool.loadBankPoolState (aChildNode);
		} else if (Market.EN_MARKET.equals (aChildName)) {
			marketFrame.loadMarketTokens (aChildNode);
		} else if (PrivatesFrame.EN_PRIVATES.equals (aChildName)) {
			privatesFrame.loadStates (aChildNode);
			cleanupLoadedPrivates ();
		} else if (MinorCompaniesFrame.EN_MINORS.equals (aChildName)) {
			minorCompaniesFrame.loadStates (aChildNode);
		} else if (ShareCompaniesFrame.EN_SHARES.equals (aChildName)) {
			shareCompaniesFrame.loadStates (aChildNode);
		} else if (HexMap.EN_MAP.equals (aChildName)) {
			mapFrame.loadMapStates (aChildNode);
		} else if (FormationPhase.EN_FORMATION_PHASE.equals (aChildName)) {
			// If the Formation Phase is in a NoState, the game was undone, saved, and reloaded 
			// It might be better to clear the formationPhase so it is not saved.
			tFormationState = aChildNode.getThisAttribute (FormationPhase.AN_FORMATION_STATE);
			if (tFormationState.equals (ActionStates.FormationComplete.toString ())) {
				// If the State is FormationComplete, Don't need to Show it.
				setFormationPhase (FormationPhase.NO_FORMATION_PHASE);
			} else if (tFormationState.equals (ActionStates.NoState.toString ())) {
				// If the State is NO State, Don't need to show it
				setFormationPhase (FormationPhase.NO_FORMATION_PHASE);
			} else {
				// Otherwise game saved in middle of Formation, need to show it.
				tFormationPhase = new FormationPhase (aChildNode, this);
				setFormationPhase (tFormationPhase);
			}
		}
	}

	public void loadNetworkJGameClient (XMLNode tChildNode) {
		String tServerIP;
		String tChatTitle;
		int tServerPort;
		JGameClient tNetworkJGameClient;

		tServerIP = tChildNode.getThisAttribute (JGameClient.AN_SERVER_IP);
		tServerPort = tChildNode.getThisIntAttribute (JGameClient.AN_SERVER_PORT);
		tChatTitle = createFrameTitle (JGameClient.BASE_TITLE);
		tNetworkJGameClient = new JGameClient (tChatTitle, this, tServerIP, tServerPort);
		tNetworkJGameClient.setVisible (true);
		tNetworkJGameClient.addLocalPlayer (clientUserName, true);
		setNetworkJGameClient (tNetworkJGameClient);
	}

	private void fixLoadedRoutes () {
		if (minorCompaniesFrame != XMLFrame.NO_XML_FRAME) {
			minorCompaniesFrame.fixLoadedRoutes (mapFrame);
		}
		if (shareCompaniesFrame != XMLFrame.NO_XML_FRAME) {
			shareCompaniesFrame.fixLoadedRoutes (mapFrame);
		}
	}

	private void cleanupLoadedPrivates () {
		// After Loading a Save Game, need to do some cleanup steps:
		// 1. If the Private is in a Closed State, close the Private (to remove from the
		// Startup Packet)
		// 2. If All of the Privates are Owned, then No auctions to do, no escrows or
		// bids need to be saved:
		// a. Remove all Escrows entries added from all players
		// b. Remove all Bids from Private Certificates
		privatesFrame.applyCloseToPrivates ();
		if (!privatesFrame.anyPrivatesUnowned ()) {
			privatesFrame.removeAllBids ();
			playerManager.removeAllEscrows ();
		}
	}

	public void updateStartPacket () {
		bank.updateStartPacket ();
	}
	
	public boolean mapVisible () {
		return mapFrame.isVisible ();
	}

	public void repaintMapFrame () {
		mapFrame.repaint ();
	}

	public void notifyMapFrame () {
		mapFrame.updatePutTileButton ();
	}

	public void performPhaseChange (TrainCompany aTrainCompany, Train aTrain, BuyTrainAction aBuyTrainAction) {
		phaseManager.performPhaseChange (aTrainCompany, aTrain, aBuyTrainAction, bank);
		repaintTileTrayFrame ();
	}

	public void repaintTileTrayFrame () {
		tileTrayFrame.repaint ();
	}

	public boolean tileTrayVisible () {
		return tileTrayFrame.isVisible ();
	}

	@Override
	public String getGEVersion () {
		return game18XXFrame.getGEVersion ();
	}

	public Checksums getChecksums () {
		return checksums;
	}
	
	public void addElements (XMLSaveGameI aXMLSaveGame, XMLDocument aXMLDocument, XMLElement aSaveGameElement, 
							ElementName aEN_TYPE) {
		XMLElement tXMLElement;
		
		if (aXMLSaveGame != XMLSaveGameI.NO_XML_SAVE_GAME) {
			tXMLElement = aXMLSaveGame.addElements (aXMLDocument, aEN_TYPE);
			if (tXMLElement != XMLElement.NO_XML_ELEMENT) {
				aSaveGameElement.appendChild (tXMLElement);
			}
		}
	}

	public void saveGame () {
		saveGame (ADD_CHECKSUM);
	}
	
	public void saveGame (boolean aAddChecksum) {
		XMLDocument tXMLDocument;
		XMLElement tSaveGameElement;
		String tFullActionReport;

		tXMLDocument = new XMLDocument ();
		tSaveGameElement = tXMLDocument.createElement (EN_GAME);
		tSaveGameElement.setAttribute (AN_GE_VERSION, getGEVersion ());

		if (isNetworkGame ()) {
			addElements (networkJGameClient, tXMLDocument, tSaveGameElement, JGameClient.EN_NETWORK_GAME);
			if (checksums == Checksums.NO_CHECKSUMS) {
				checksums = new Checksums ();
			}
		}

		/* Save the Basic Game Information */
		addElements (activeGame, tXMLDocument, tSaveGameElement, GameInfo.EN_GAME_INFO);

		/* Save the Player Names Information */
		addElements (playerManager, tXMLDocument, tSaveGameElement, Player.EN_PLAYERS);

		/* Save the Phase Index (Current Phase) */
		addElements (phaseManager, tXMLDocument, tSaveGameElement, PhaseManager.EN_PHASE);

		/* Save the Actions performed */
		addElements (roundManager, tXMLDocument, tSaveGameElement, Action.EN_ACTIONS);

		/* Save the Round Information, Stock and Operating */
		addElements (roundManager, tXMLDocument, tSaveGameElement, RoundManager.EN_ROUNDS);

		/* Save the Player Status, Cash, Bids, and Portfolio */
		addElements (playerManager, tXMLDocument, tSaveGameElement, Player.EN_PLAYER_STATES);

		/* Save the current Bank Balance, and Rusted Train Portfolio */
		addElements (bank, tXMLDocument, tSaveGameElement, Bank.EN_BANK_STATE);

		/* Save the Bank Pool Stock Portfolio and Train Portfolio */
		addElements (bankPool, tXMLDocument, tSaveGameElement, BankPool.EN_BANK_POOL_STATE);

		/* Save the Tokens on the Market */
		addElements (marketFrame, tXMLDocument, tSaveGameElement, Market.EN_MARKET);

		/* Save the Privates, Minors, and Share Company Information */
		addElements (privatesFrame, tXMLDocument, tSaveGameElement, PrivatesFrame.EN_PRIVATES);
		addElements (minorCompaniesFrame, tXMLDocument, tSaveGameElement, MinorCompaniesFrame.EN_MINORS);
		addElements (shareCompaniesFrame, tXMLDocument, tSaveGameElement, ShareCompaniesFrame.EN_SHARES);

		/* Also need to save the Map Information */
		/* Save The Tile Placements, Orientations, and Token Placements */
		addElements (mapFrame, tXMLDocument, tSaveGameElement, HexMap.EN_MAP);
		
		/* Save the FormationPhase */
		if (formationPhase != FormationPhase.NO_FORMATION_PHASE) {
			addElements (formationPhase, tXMLDocument, tSaveGameElement, FormationPhase.EN_FORMATION_PHASE);
		}
		
		// Append Save Game Element to Document just before outputing it.
		tXMLDocument.appendChild (tSaveGameElement);
		if (isNetworkGame ()) {
			if (aAddChecksum) {
				addChecksum (tXMLDocument);
			}
		}
		
		tXMLDocument.outputXML (saveFile);

		tFullActionReport = roundManager.getFullActionReport ();
		outputToFile (tFullActionReport, autoSaveActionReportFile);
	}

	public void addChecksum (XMLDocument aXMLDocument) {
		addChecksum (EN_GAME, aXMLDocument);
	}
	
	public void addChecksum (ElementName aEN_Name, XMLDocument aXMLDocument) {
		Checksum tChecksum;
		XMLElement tChecksumXMLElement;
		ActionManager tActionManager;
		String tChecksumValue;
		String tGameID;
		String tClientName;
		String tNodeName;
		String tXMLChecksum;
		int tActionIndex;
		int tPlayerIndex;
		int tPlayerCount;
//		int tFoundIndex;
		
		tChecksumValue = aXMLDocument.MD5 (ChecksumCalc.STRIP_WHITESPACE);
		
		tGameID = getGameID ();
		tClientName = getClientUserName ();
		tNodeName = aEN_Name.getString ();
		tActionIndex = roundManager.getLastActionIndex ();
		tPlayerIndex = playerManager.getPlayerIndex (clientUserName);
		tPlayerCount = playerManager.getPlayerCount ();
		tChecksum = new Checksum (tGameID, tNodeName, tClientName, tPlayerCount, tActionIndex);
		tChecksum.addClientChecksum (tPlayerIndex, tChecksumValue);
//		tFoundIndex = checksums.findIndexFor (tActionIndex);
		checksums.add (tChecksum);
		checksumAuditFrame.addRow (tChecksum);

		tChecksumXMLElement = tChecksum.addElements (aXMLDocument, Checksum.EN_CHECKSUM);
		tXMLChecksum = tChecksumXMLElement.toXMLString ();
		
		tActionManager = roundManager.getActionManager ();
		tActionManager.sendGameActivity (tXMLChecksum, true);
	}
	
	public void addAdditionalChecksum (XMLNode aChecksumNode) {
		int tActionIndex;
		int tPlayerIndex;
		int tChecksumIndex;
		String tChecksumValue;
		String tGameID;
		String tClientName;
		String tNodeName;
		Checksum tChecksum;
		
		tActionIndex = aChecksumNode.getThisIntAttribute (Checksum.AN_ACTION_INDEX);
		tPlayerIndex = aChecksumNode.getThisIntAttribute (Checksum.AN_PLAYER_INDEX);
		tChecksumValue = aChecksumNode.getThisAttribute (Checksum.AN_CHECKSUM);
		tGameID = aChecksumNode.getThisAttribute (Checksum.AN_GAME_ID);
		tClientName = aChecksumNode.getThisAttribute (Checksum.AN_CLIENT_NAME);
		tNodeName = aChecksumNode.getThisAttribute (Checksum.AN_NODE_NAME);
		
		tChecksumIndex = checksums.findIndexFor (tActionIndex);
		tChecksum = checksums.get (tChecksumIndex);
		if (tChecksum != Checksum.NO_CHECKSUM) {
			if (tGameID.equals (tChecksum.getGameID ())) {
				if (tActionIndex == tChecksum.getActionIndex ()) {
					if (tNodeName.equals (tChecksum.getNodeName ())) {
						if (tClientName.equals (tChecksum.getClientName ())) {
							System.err.println ("Client Name " + tNodeName + " does match");
						} else {
							tChecksum.addClientChecksum (tPlayerIndex, tChecksumValue);
							checksumAuditFrame.refreshAuditTable ();
						}
					} else {
						System.err.println ("Node Name " + tNodeName + " does not match");
					}
				} else {
					System.err.println ("Action Index " + tActionIndex + " does not match");
				}
			} else {
				System.err.println ("Game ID " + tGameID + " does not match");
			}
		} else {
			System.err.println ("Could not find Checksum with Action Index " + tActionIndex);			
		}
	}
	
	public void removeChecksumFor (int aActionIndex) {
		if (checksums != Checksums.NO_CHECKSUMS) {
			checksums.removeActionIndex (aActionIndex);
			checksumAuditFrame.refreshAuditTable ();
		}
	}
	
	/* Update to use the method in the File Utils */
	public void outputToFile (String aReport, File aFile) {
		FileWriter tFWout;
		
		try {
			tFWout = new FileWriter (aFile);
			tFWout.write (aReport);
			tFWout.close ();
		} catch (Exception tException) {
			System.err.println (tException);
			tException.printStackTrace ();
		}
	}

	public void autoSaveGame () {
		autoSaveGame (ADD_CHECKSUM);
	}
	
	public void autoSaveGame (boolean aAddChecksum) {
		File tPriorSave;

		tPriorSave = saveFile;
		saveFile = autoSaveFile;
		if (gameStarted) {
			saveGame (aAddChecksum);
		}
		saveFile = tPriorSave;
	}

	public void saveAGame (boolean aOverwriteFile) {
		File tSaveDirectory;
		String tFileName;
		String tOriginalSaveGameDir;
		String tNewSaveGameDir;
		
		if (saveFile == null) {
			aOverwriteFile = false;
		}
		if (aOverwriteFile) {
			saveGame ();
			setGameChanged (false);
		} else {
			tOriginalSaveGameDir = configData.getSaveGameDirectory ();
			tSaveDirectory = new File (tOriginalSaveGameDir);
			setupChooser (tSaveDirectory);
			saveFile = getSelectedFile (tSaveDirectory, chooser, true);
			tNewSaveGameDir = tSaveDirectory.getAbsolutePath ();
			if (!tOriginalSaveGameDir.equals (tNewSaveGameDir)) {
				configData.setSaveGameDirectory (tNewSaveGameDir);
				saveConfig (true);
			}
			if (saveFile != null) {
				tFileName = saveFile.getName ();
				if (!tFileName.endsWith (fileUtils.xml)) {
					saveFile = new File (saveFile.getAbsoluteFile () + "." + fileUtils.xml);
				}
				saveGame ();
				setGameChanged (false);
				/* Once a Game has been saved, can enable the Save Menu Item */
				game18XXFrame.enableSaveMenuItem ();
			} else {
				logger.error ("Cancel Save Game Action");
			}
		}
	}

	private void setupChooser (File aSaveDirectory) {
		Point tNewPoint;

		tNewPoint = getOffsetGEFrame ();
		chooser = new JFileMChooser ();
		chooser.setMLocation (tNewPoint);
		chooser.setDialogTitle ("Save 18XX Game File");
		chooser.setCurrentDirectory (aSaveDirectory);
		chooser.addChoosableFileFilter (fileGEFilter);
		chooser.setAcceptAllFileFilterUsed (true);
		chooser.setFileSelectionMode (JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setSelectedFile (new File ("SaveGame." + fileUtils.xml));
	}

	public void setCitiesFrame (XMLFrame aXMLFrame) {
		citiesFrame = (CitiesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setGameInfo (GameInfo aGameInfo) {
		activeGame = aGameInfo;
		if (activeGame != GameInfo.NO_GAME_INFO) {
			activeGame.setGameID (gameID);
		}
	}

	public void setGameChanged (boolean aFlag) {
		gameChangedSinceSave = aFlag;
	}

	public boolean marketHasTokenFor (ShareCompany aShareCompany) {
		return marketFrame.marketHasTokenFor (aShareCompany);
	}

	public void setParPriceToken (ShareCompany aShareCompany) {
		marketFrame.setSharePriceToken (aShareCompany);
		updateAllPlayerFrames ();
	}

	public void setParPrice (ShareCompany aShareCompany, int aParPrice) {
		marketFrame.setParPrice (aShareCompany, aParPrice);
		updateAllPlayerFrames ();
	}

	public void setPhaseManager (PhaseManager aPhaseManager) {
		phaseManager = aPhaseManager;
	}

	public void setPlayerManager (PlayerManager aPlayerManager) {
		playerManager = aPlayerManager;
	}

	public void setMapFrame (XMLFrame aXMLFrame) {
		mapFrame = (MapFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	private void setMarketFrame (XMLFrame aXMLFrame) {
		marketFrame = (MarketFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setMinorCompaniesFrame (XMLFrame aXMLFrame) {
		minorCompaniesFrame = (MinorCompaniesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setPlayerInputFrame (PlayerInputFrame aPlayerInputFrame) {
		playerInputFrame = aPlayerInputFrame;
		addNewFrame (playerInputFrame);
	}

	public boolean hasTestGraphs () {
		return activeGame.hasTestGraphs ();
	}

	public void setPrivatesFrame (XMLFrame aXMLFrame) {
		privatesFrame = (PrivatesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setShareCompaniesFrame (XMLFrame aXMLFrame) {
		shareCompaniesFrame = (ShareCompaniesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	private void setTileTrayFrame (XMLFrame aXMLFrame) {
		tileTrayFrame = (TileTrayFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	private void setAuditFrame (XMLFrame aXMLFrame) {
		auditFrame = (AuditFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	private void setChecksumAuditFrame (XMLFrame aXMLFrame) {
		checksumAuditFrame = (ChecksumAuditFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}
	
	private void setUserPreferencesFrame (XMLFrame aXMLFrame) {
		userPreferencesFrame = (UserPreferencesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	private void setTileDefinitionFrame (XMLFrame aXMLFrame) {
		tileDefinitionFrame = (TileDefinitionFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}
	
	private void setFrameInfoFrame (FrameInfoSupport aFrameInfoFrame) {
		frameInfoFrame = aFrameInfoFrame;
	}

	private void setupGamePieces () {
		if (activeGame != GameInfo.NO_GAME_INFO) {
			createMarket ();
			createShareCompanies ();
			createPrivateCompanies ();
			createMinorCompanies ();
			createCities ();
			createTileTray ();
			createMap ();
		}
	}

	private void setupPlayers () {
		Player tPlayer;
		PlayerManager tPlayerManager;
		String tPlayerName;
		int tIndex;
		int tPlayerStartingCash;
		int tPlayerCount;
		int tCertificateLimit;

		if (playerManager == PlayerManager.NO_PLAYER_MANAGER) {
			tPlayerManager = new PlayerManager (this);
			setPlayerManager (tPlayerManager);
		}
		
		tPlayerCount = playerInputFrame.getPlayerCount ();
		if (tPlayerCount == 0) {
			tPlayerCount = playerManager.getPlayerCount ();
			playerManager.setCertificateLimit (activeGame, tPlayerCount);
		} else {
			tPlayerStartingCash = activeGame.getStartingCash (tPlayerCount);
			tCertificateLimit = activeGame.getCertificateLimit (tPlayerCount);
			for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
				tPlayerName = playerInputFrame.getPlayerName (tIndex);
				tPlayer = new Player (tPlayerName, playerManager, tCertificateLimit);
				bank.transferCashTo (tPlayer, tPlayerStartingCash);
				playerManager.addPlayer (tPlayer);
			}
		}

		if (roundManagerIsValid ()) {
			roundManager.updateAllCorporationsBox ();
		}
	}
	
	public void updateCertificateLimit () {
		int tCertificateLimit;
		int tPlayerCount;
		int tIndex;
		Player tPlayer;
		
		tPlayerCount = playerManager.getPlayerCount ();
		tCertificateLimit = activeGame.getCertificateLimit (tPlayerCount);
		for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
			tPlayer = playerManager.getPlayer (tIndex);
			tPlayer.setCertificateLimit (tCertificateLimit);
			playerManager.updateRFPlayerLabel (tPlayer);
		}
	}
	
	public void updatePlayerListeners (String aMessage) {
		playerManager.updatePlayerListeners (aMessage);
	}
	
	public void showCities () {
		citiesFrame.showFrame ();
	}

	public void setAuctionFrameLocation () {
		Point tNewPoint;

		tNewPoint = getOffsetPlayerFrame ();
		auctionFrame.setLocation (tNewPoint);
	}

	@Override
	public Point getOffsetGEFrame () {
		return game18XXFrame.getOffsetGEFrame ();
	}

	public Point getOffsetRoundFrame () {
		return roundManager.getOffsetRoundFrame ();
	}

	public Point getOffsetCorporationFrame () {
		CorporationFrame tCorporationFrame;
		Corporation tOperatingCorporation;
		Point tNewPoint;

		tOperatingCorporation = roundManager.getOperatingCompany ();
		if (tOperatingCorporation == Corporation.NO_CORPORATION) {
			tNewPoint = roundManager.getOffsetRoundFrame ();
		} else {
			tCorporationFrame = tOperatingCorporation.getCorporationFrame ();
			tNewPoint = tCorporationFrame.getOffsetFrame ();
		}
		
		return tNewPoint;
	}
	
	public boolean isStartPacketPortfolioEmpty () {
		return bank.isStartPacketPortfolioEmpty ();
	}

	public Point getOffsetPlayerFrame () {
		Point tNewPoint;
		String tPlayerName;

		if (isNetworkGame ()) {
			tPlayerName = clientUserName;
		} else {
			tPlayerName = playerManager.getCurrentPlayerName ();
		}
		tNewPoint = playerManager.getOffsetFrame (tPlayerName);

		return tNewPoint;
	}

	public PlayerFrame getCurrentPlayerFrame () {
		return playerManager.getCurrentPlayerFrame ();
	}

	public void showMap () {
		mapFrame.showFrame ();
	}

	public void showAuctionFrame () {
		auctionFrame.showFrame ();
	}

	public void bringMarketToFront () {
		marketFrame.toTheFront ();
	}

	public void showMarket () {
		marketFrame.showFrame ();
	}

	public void showMinorCompanies () {
		minorCompaniesFrame.setLocation (getOffsetRoundFrame ());
		minorCompaniesFrame.setSize (1250, 300);
		minorCompaniesFrame.showFrame ();
	}

	public void showPrivateCompanies () {
		privatesFrame.setLocation (getOffsetRoundFrame ());
		privatesFrame.setSize (1250, 300);
		privatesFrame.showFrame ();
	}

	public void showShareCompanies () {
		shareCompaniesFrame.setLocation (getOffsetRoundFrame ());
		shareCompaniesFrame.setSize (1250, 300);
		shareCompaniesFrame.showFrame ();
	}

	public void showTileTray () {
		tileTrayFrame.showFrame ();
	}

	public void bringMapToFront () {
		mapFrame.toTheFront ();
	}

	public void bringPlayerFrameToFront () {
		playerManager.bringPlayerFrameToFront ();
	}

	public void bringTileTrayToFront () {
		tileTrayFrame.toTheFront ();
	}

	public void sendToReportFrame (String aReport) {
		roundManager.sendToReportFrame (aReport);
	}

	public void updateAllFrames () {
		updateRoundFrame ();
		if (roundManagerIsValid () ) {
			if (roundManager.getCurrentRoundType ().equals (ActorI.ActionStates.StockRound)) {
				updateAllPlayerFrames ();
			} else if (roundManager.getCurrentRoundType ().equals (ActorI.ActionStates.OperatingRound)) {
				roundManager.updateOperatingCorporationFrame ();
			}
		}
		if (mapFrame != MapFrame.NO_XML_FRAME) {
			mapFrame.updateFrame ();
		}
		if (marketFrame != MarketFrame.NO_XML_FRAME) {
			marketFrame.updateFrame ();
		}
		if (tileTrayFrame != TileTrayFrame.NO_XML_FRAME) {
			tileTrayFrame.updateFrame ();
		}
		if (networkJGameClient != JGameClient.NO_XML_FRAME) {
			networkJGameClient.updateFrame ();
		}
	}

	public void updateAllPlayerFrames () {
		playerManager.updateAllPlayerFrames ();
	}

	public void updateRoundFrame () {
		if (roundManagerIsValid ()) {
			roundManager.updateRoundFrame ();
			roundManager.updateAllListenerPanels ();
			roundManager.setListenerPanels (true);
		}
	}

	// Get an Array of all Available Trains from Bank, and BankPool together
	public Train [] getBankAvailableTrains () {
		Train [] tBankAvailableTrains;
		Train [] tBankPoolAvailableTrains;
		Train [] tAvailableTrains;
		int tIndex = 0;
		int tBankCount = 0;
		int tBankPoolCount = 0;

		if (bank != Bank.NO_BANK) {
			tBankAvailableTrains = bank.getAvailableTrains ();
			if (tBankAvailableTrains != null) {
				tBankCount = tBankAvailableTrains.length;
			}
		} else {
			tBankAvailableTrains = null;
		}
		if (bankPool != BankPool.NO_BANK_POOL) {
			tBankPoolAvailableTrains = bankPool.getAvailableTrains ();
			if (tBankPoolAvailableTrains != null) {
				tBankPoolCount = tBankPoolAvailableTrains.length;
			}
		} else {
			tBankPoolAvailableTrains = null;
		}
		tAvailableTrains = new Train [tBankCount + tBankPoolCount];
		if (tBankAvailableTrains != null) {
			for (Train tTrain : tBankAvailableTrains) {
				tAvailableTrains [tIndex++] = tTrain;
			}
		}
		if (tBankPoolAvailableTrains != null) {
			for (Train tTrain : tBankPoolAvailableTrains) {
				tAvailableTrains [tIndex++] = tTrain;
			}
		}

		return tAvailableTrains;
	}

	public boolean canBuyTrainInPhase () {
		boolean tCanBuyTrainInPhase;

		tCanBuyTrainInPhase = phaseManager.canBuyTrainInPhase ();

		return tCanBuyTrainInPhase;
	}

	public boolean roundManagerIsValid () {
		return (roundManager != RoundManager.NO_ROUND_MANAGER);
	}

	public String getOperatingRoundID () {
		String tOperatingRoundID;

		if (roundManagerIsValid ()) {
			tOperatingRoundID = roundManager.getOperatingRoundID ();
		} else {
			tOperatingRoundID = ">>NONE<<";
		}

		return tOperatingRoundID;
	}

	public String getStockRoundID () {
		String tStockRoundID;

		if (roundManagerIsValid ()) {
			tStockRoundID = roundManager.getStockRoundID () + "";
		} else {
			tStockRoundID = ">>NONE<<";
		}

		return tStockRoundID;
	}

	public boolean isOperatingRound () {
		boolean tIsOperatingRound;

		if (roundManagerIsValid ()) {
			tIsOperatingRound = roundManager.isOperatingRound ();
		} else {
			tIsOperatingRound = false;
		}

		return tIsOperatingRound;
	}

	public boolean isStockRound () {
		boolean tIsStockRound;

		if (roundManagerIsValid ()) {
			tIsStockRound = roundManager.isStockRound ();
		} else {
			tIsStockRound = false;
		}

		return tIsStockRound;
	}

	public Corporation getOperatingCompany () {
		Corporation tOperatingCompany;

		if (isStockRound ()) {
			tOperatingCompany = Corporation.NO_CORPORATION;
		} else {
			tOperatingCompany = roundManager.getOperatingCompany ();
		}

		return tOperatingCompany;
	}

	public void addAction (Action aAction) {
		roundManager.addAction (aAction);
	}

	public boolean isUpgradeAllowed (String tTileColor) {
		return phaseManager.isUpgradeAllowed (tTileColor);
	}

	public int getBankPoolShareLimit () {
		return activeGame.getBankPoolShareLimit () * 10;
	}

	public int getBankPoolPercentageLimit () {
		return activeGame.getBankPoolShareLimit () * 10;
	}

	/** 
	 *  Determine if the active game is a Network Game
	 *  
	 * @return True if the networkJGameClient is Defined, thereby a Network Game
	 * 
	 */
	public boolean isNetworkGame () {
		return (networkJGameClient != JGameClient.NO_JGAME_CLIENT);
	}

	public void setNetworkJGameClient (JGameClient aNetworkJGameClient) {
		networkJGameClient = aNetworkJGameClient;
		networkJGameClient.setFrameToConfigDetails (this);
	}

	@Override
	public JGameClient getNetworkJGameClient () {
		return networkJGameClient;
	}

	@Override
	public String getPlayersInOrder () {
		return playerInputFrame.getPlayersInOrder ();
	}

	@Override
	public String getGameID () {
		return gameID;
	}

	public boolean shouldRandomize () {
		boolean tShouldRandomize;

		if (activeGame != GameInfo.NO_GAME_INFO) {
			tShouldRandomize = activeGame.randomizeStartOrder ();
		} else {
			tShouldRandomize = true;
		}

		return tShouldRandomize;
	}

	@Override
	public void randomizePlayerOrder () {
		playerInputFrame.randomizePlayerOrder ();
	}

	public void handleNetworkAction (String aNetworkAction) {
		XMLDocument tXMLNetworkAction;
		XMLNode tActionNode;
		XMLNode tGSResponseNode;
		NodeList tActionChildren;
		String tGSRNodeName;
		String tActionNodeName;
		int tActionNodeCount;
		int tActionIndex;

		tXMLNetworkAction = new XMLDocument ();
		tXMLNetworkAction = tXMLNetworkAction.parseXMLString (aNetworkAction);
		tGSResponseNode = tXMLNetworkAction.getDocumentNode ();
		if (tGSResponseNode != XMLNode.NO_NODE) {
			tGSRNodeName = tGSResponseNode.getNodeName ();
			if (GameSupportHandler.GS_RESPONSE_TAG.equals (tGSRNodeName)) {
				tActionChildren = tGSResponseNode.getChildNodes ();
				tActionNodeCount = tActionChildren.getLength ();
				for (tActionIndex = 0; tActionIndex < tActionNodeCount; tActionIndex++) {
					tActionNode = new XMLNode (tActionChildren.item (tActionIndex));
					tActionNodeName = tActionNode.getNodeName ();
					if (Action.EN_ACTION.equals (tActionNodeName)) {
						sendNetworkAction (tActionNode);
					}
				}
			}
		}
	}

	private void sendNetworkAction (XMLNode aActionNode) {
		if (roundManagerIsValid ()) {
			setApplyingAction (true);
			roundManager.handleNetworkAction (aActionNode);
			setApplyingAction (false);
		} else {
			logger.error ("Trying to handle a Server Game Activity, Node Named [" + aActionNode
					+ "] no Round Manager set yet");
		}
	}

	@Override
	public void handleGameActivity (String aGameActivity) {
		XMLDocument tXMLGameActivity;
		XMLNode tXMLGameActivityNode;
		XMLNode tActivityNode;
		NodeList tActivityChildren;
		int tActivityNodeCount;
		int tActivityIndex;
		String tActivityNodeName;
		String tGANodeName;
		String tBroadcast;
		String tPlayerOrder;

		tXMLGameActivity = new XMLDocument ();
		tXMLGameActivity = tXMLGameActivity.parseXMLString (aGameActivity);
		tXMLGameActivityNode = tXMLGameActivity.getDocumentNode ();
		tGANodeName = tXMLGameActivityNode.getNodeName ();
		if (JGameClient.EN_GAME_ACTIVITY.equals (tGANodeName)) {
			tActivityChildren = tXMLGameActivityNode.getChildNodes ();
			tActivityNodeCount = tActivityChildren.getLength ();
			try {
				for (tActivityIndex = 0; tActivityIndex < tActivityNodeCount; tActivityIndex++) {
					tActivityNode = new XMLNode (tActivityChildren.item (tActivityIndex));
					tActivityNodeName = tActivityNode.getNodeName ();
					if (Action.EN_ACTION.equals (tActivityNodeName)) {
						sendNetworkAction (tActivityNode);
					} else if (Checksum.EN_CHECKSUM.equals (tActivityNodeName)) {
						addAdditionalChecksum (tActivityNode);
					} else if (JGameClient.EN_GAME_SELECTION.equals (tActivityNodeName)) {
						networkJGameClient.handleGameSelection (tActivityNode, playerInputFrame);
					} else if (JGameClient.EN_PLAYER_ORDER.equals (tActivityNodeName)) {
						tPlayerOrder = tActivityNode.getThisAttribute (JGameClient.AN_PLAYER_ORDER);
						tBroadcast = tActivityNode.getThisAttribute (JGameClient.AN_BROADCAST_MESSAGE);
						playerInputFrame.handleResetPlayerOrder (tPlayerOrder, tBroadcast);
					} else if (ActionManager.EN_REMOVE_ACTION.equals (tActivityNodeName)) {
						// RemoveAction should be ignored
					} else if (XMLNode.XML_TEXT_TAG.equals (tActivityNodeName)) {
						// If a #text Node, ignore -- it is empty
					} else {
						logger.error ("Node Name is [" + tActivityNodeName + "] which is Unrecognized");
					}
				}
			} catch (Exception tException) {
				logger.error (tException.getMessage (), tException);
			}
		}
	}
	
	public void setApplyingAction (boolean aApplyingAction) {
		applyingNetworkAction = aApplyingAction;
	}

	public boolean applyingAction () {
		return applyingNetworkAction;
	}

	@Override
	public void updatePlayerCountLabel () {
		if (validPlayerInputFrame ()) {
			playerInputFrame.updatePlayerCountLabel ();
		}
	}

	@Override
	public void addNetworkPlayer (String aPlayerName) {
		if (notifyNetwork) {
			if (validPlayerInputFrame ()) {
				playerInputFrame.addNetworkPlayer (aPlayerName);
			}
		}
	}

	@Override
	public void removeNetworkPlayer (String aPlayerName) {
		if (notifyNetwork) {
			if (validPlayerInputFrame ()) {
				playerInputFrame.removeNetworkPlayer (aPlayerName);
			}
		}
	}

	@Override
	public void removeAllNetworkPlayers () {
		if (validPlayerInputFrame ()) {
			playerInputFrame.removeAllPlayers ();
		}
	}

	public boolean validPlayerInputFrame () {
		boolean tValidPlayerInputFrame;
		
		if (playerInputFrame == PlayerInputFrame.NO_PLAYER_INPUT_FRAME) {
			tValidPlayerInputFrame = false;
		} else {
			tValidPlayerInputFrame = true;
		}
		
		return tValidPlayerInputFrame;
	}
	
	public void setClientUserName (String aClientUserName) {
		clientUserName = aClientUserName;
	}

	public String getFirstPlayerName () {
		String tFirstPlayerName;
		
		tFirstPlayerName = clientUserName;
		if (userPreferencesFrame != XMLFrame.NO_XML_FRAME) {
			tFirstPlayerName = userPreferencesFrame.getFirstPlayerName ();
		}
		
		return tFirstPlayerName;
	}
	
	@Override
	public String getClientUserName () {
		return clientUserName;
	}

	// TODO: This method per the logic is backwards....
	// This should be 'isNetworkAndIsThisClient', and
	// Return TRUE if it is a Network Game, AND the Client Name is what is provided
	// There are 11 methods calling this and each needs to be tested when this is reversed.
	public boolean notIsNetworkAndIsThisClient (String aClientName) {
		boolean tIsNetworkAndClient;

		tIsNetworkAndClient = true;
		if (isNetworkGame ()) {
			if (!clientUserName.equals (aClientName)) {
				tIsNetworkAndClient = false;
			}
		}

		return tIsNetworkAndClient;
	}

	@Override
	public void addNewFrame (XMLFrame aXMLFrame) {
		String tXMLFrameName;

		if (aXMLFrame != XMLFrame.NO_XML_FRAME) {
			tXMLFrameName = aXMLFrame.getTitle ();
			if (tXMLFrameName != null) {
				if (!frameIsPresent (tXMLFrameName)) {
					configFrames.add (aXMLFrame);
				}
			}
		}
	}

	public boolean frameIsPresent (String aFrameName) {
		boolean tFrameIsPresent;
		String tSpecificFrameName;

		tFrameIsPresent = false;
		if (aFrameName != GUI.NULL_STRING) {
			for (XMLFrame tSpecificFrame : configFrames) {
				if (tSpecificFrame != XMLFrame.NO_XML_FRAME) {
					tSpecificFrameName = tSpecificFrame.getTitle ();
					if (aFrameName.equals (tSpecificFrameName)) {
						tFrameIsPresent = true;
					}
				}
			}
		}

		return tFrameIsPresent;
	}

	//  Configuration File Methods
	
	public void loadConfig () {
		XMLDocument tXMLDocument;
		XMLNode tXMLNode;
		String tConfigFileName;
		File tConfigFile;
		Config tConfigData;

		tXMLDocument = XMLDocument.NO_XML_DOCUMENT;
		tConfigFileName = game18XXFrame.getConfigFileName (clientUserName);
		tConfigFile = new File (tConfigFileName);
		showConfigFileInfo (tConfigFile, "Loading Config File from");
		
		if (tConfigFile.exists ()) {
			try {
				tXMLDocument = new XMLDocument (tConfigFile);
			} catch (Exception tException) {
				logger.error ("Oops, mucked up the Config File Access [" + tConfigFileName + "].");
				logger.error ("Exception Message [" + tException.getMessage () + "].", tException);
			}
			if (tXMLDocument != XMLDocument.NO_XML_DOCUMENT) {
				tXMLNode = tXMLDocument.getDocumentNode ();
				tConfigData = new Config (tXMLNode, this);
			} else {
				tConfigData = new Config (this);
			}
		} else {
			tConfigData = new Config (this);
		}
		setConfigData (tConfigData);
	}

	public void setConfigData (Config aConfigData) {
		configData = aConfigData;
	}
	
	public void saveConfig (boolean aOverwriteFile) {
		XMLDocument tXMLDocument;
		File tConfigFile;

		tXMLDocument = createNewConfigDocument ();
		tConfigFile = getConfigFile ();
		tXMLDocument.outputXML (tConfigFile);
		showConfigFileInfo (tConfigFile, "Config File Saved");
	}

	public File getConfigFile () {
		return new File (game18XXFrame.getConfigFileName (clientUserName));
	}

	private void applyConfigSettings () {
		GameFrameConfig tGameFrameConfig;

		tGameFrameConfig = getGameFrameConfig ();
		if (tGameFrameConfig != GameFrameConfig.NO_GAME_FRAME) {
			for (XMLFrame tXMLFrame : configFrames) {
				tXMLFrame.setFrameToConfigDefaults (tGameFrameConfig, getVisibileConfig ());
			}
		}
	}

	@Override
	public GameFrameConfig getGameFrameConfig () {
		GameFrameConfig tGameFrameConfig;
		String tGameName;

		tGameName = getGameName ();
		tGameFrameConfig = configData.getGameFrameConfigFor (tGameName);

		return tGameFrameConfig;
	}

	public String getVisibileConfig () {
		return XMLFrame.getVisibileConfig ();
	}

	public XMLDocument createNewConfigDocument () {
		XMLElement tConfigElement;
		XMLElement tFramesElement;
		XMLElement tFrameElement;
		XMLElement tSaveGameDirElement;
		XMLElement tPreferencesElement;
		XMLDocument tXMLDocument;
		String tActiveGameName;
		int tHeight;
		int tWidth;

		tXMLDocument = new XMLDocument ();
		tConfigElement = tXMLDocument.createElement (EN_CONFIG);

		tSaveGameDirElement = tXMLDocument.createElement (EN_SAVEGAMEDIR);
		tSaveGameDirElement.setAttribute (AN_NAME, configData.getSaveGameDirectory ());
		tConfigElement.appendChild (tSaveGameDirElement);

		tPreferencesElement = userPreferencesFrame.createElement (tXMLDocument);
		tConfigElement.appendChild (tPreferencesElement);
		
		tFramesElement = tXMLDocument.createElement (EN_FRAMES);

		if (activeGame == GameInfo.NO_GAME_INFO) {
			tActiveGameName = UNSPECIFIED_GAME_NAME;
		} else {
			tActiveGameName = activeGame.getName ();
		}
		tFramesElement.setAttribute (AN_GAME_NAME, tActiveGameName);

		for (XMLFrame tXMLFrame : configFrames) {
			if (tXMLFrame != XMLFrame.NO_XML_FRAME) {
				tHeight = tXMLFrame.getHeight ();
				tWidth = tXMLFrame.getWidth ();
				// If the Height and Width are > 0, save it... otherwise it makes no sense since
				// the Frame is not showing anything
				if ((tHeight > 0) && (tWidth > 0)) {
					tFrameElement = tXMLFrame.getXMLFrameElement (tXMLDocument);
					tFramesElement.appendChild (tFrameElement);
				}
			}
		}
		tConfigElement.appendChild (tFramesElement);
		addOtherGameConfigs (tActiveGameName, tConfigElement, tXMLDocument);

		tXMLDocument.appendChild (tConfigElement);

		return tXMLDocument;
	}

	public void addOtherGameConfigs (String aActiveGameName, XMLElement tConfigElement, 
									XMLDocument tXMLDocument) {
		XMLElement tFramesElement;
		int tGameCount;
		int tGameIndex;
		String tGameName;
		GameFrameConfig tGameFrameConfig;
		
		tGameCount = configData.getGameFramesCount ();
		if (tGameCount > 0) {
			for (tGameIndex = 0; tGameIndex < tGameCount; tGameIndex++) {
				tGameFrameConfig = configData.getGameFrameConfigFor (tGameIndex);
				tGameName = tGameFrameConfig.getGameName ();
				if (tGameName.equals (aActiveGameName)) {
					// Don't add since this was already added
				} else if (tGameName.equals (UNSPECIFIED_GAME_NAME) || tGameName.equals ("NONE")) {
					// Don't add since the Unspecified Game Name doesn't need to be saved
				} else {
					tFramesElement = configData.getXMLFramesElement (tXMLDocument, tGameIndex);
					tConfigElement.appendChild (tFramesElement);
				}
			}
		}
	}

	public void showConfigFileInfo (File aConfigFile, String aTitle) {
		XMLFrame tFrame;
		String tConfigurationFilePath;
		
		if (userPreferencesFrame.showConfigInfoFileInfo ()) {
			if (roundManager == RoundManager.NO_ROUND_MANAGER) {
				tFrame = game18XXFrame;
			} else {
				tFrame = roundManager.getRoundFrame ();
			}
			tConfigurationFilePath = aConfigFile.getAbsolutePath ();
			JOptionPane.showMessageDialog (tFrame, "Path " + tConfigurationFilePath, 
											aTitle, JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	//  End Configuration File Methods
	
	public boolean confirmDontBuyTrain () {
		return userPreferencesFrame.confirmDontBuyTrain ();
	}
	
	public boolean confirmBuyPresidentShare () {
		return userPreferencesFrame.confirmBuyPresidentShare ();
	}
	
	public boolean getAlwaysShowEscrow () {
		return userPreferencesFrame.getAlwaysShowEscrowPreference ();
	}

	@Override
	public int getSelectedGameIndex () {
		return playerInputFrame.getSelectedGameIndex ();
	}

	@Override
	public void resetGameID (String aGameID) {
		if (gameID.equals (EMPTY_GAME_ID)) {
			setGameID (aGameID);
		}
	}

	@Override
	public void setGameID (String aGameID) {
		gameID = aGameID;
	}

	@Override
	public void setSelectedGameIndex (int aGameIndex) {
		playerInputFrame.setSelectedGameIndex (aGameIndex);
	}

	public void appendToGameActivity (String aGameActivity) {
		networkJGameClient.appendToGameActivity (aGameActivity);
	}

	public XMLFrame getXMLFrameNamed (String aXMLFrameTitle) {
		XMLFrame tXMLFrameFound;
		String tFrameTitle;

		tXMLFrameFound = XMLFrame.NO_XML_FRAME;
		if (aXMLFrameTitle != GUI.NULL_STRING) {
			for (XMLFrame tXMLFrame : configFrames) {
				tFrameTitle = tXMLFrame.getTitle ();
				if (tFrameTitle.startsWith (aXMLFrameTitle)) {
					tXMLFrameFound = tXMLFrame;
				}
			}
		}

		return tXMLFrameFound;
	}

	public void revalidateAuctionFrame () {
		auctionFrame.revalidate ();
	}

	public void updateAuctionFrame () {
		auctionFrame.updateAuctionFrame ();
	}

	public void finishAuction (boolean aCreateNewAuctionAction) {
		updateAllFrames ();
		auctionFrame.setVisible (false);
		playerManager.finishAuction (bank.availableShareHasBids (), aCreateNewAuctionAction);
	}

	public void printAllPlayersInfo () {
		playerManager.printAllPlayersInfo ();
	}

	public void setNotifyNetwork (boolean aNotifyNetwork) {
		notifyNetwork = aNotifyNetwork;
	}

	public boolean getNotifyNetwork () {
		return notifyNetwork;
	}

	public boolean operateBeforeSale () {
		return activeGame.operateBeforeSale ();
	}

	public boolean isFirstStockRound () {
		return roundManager.isFirstStockRound ();
	}

	public void showRoundFrame () {
		roundManager.showRoundFrame ();
	}

	public void showActionReportFrame () {
		roundManager.showActionReportFrame ();
	}

	public void resendLastActions () {
		roundManager.resendLastActions ();
	}
	
	public void showUserPreferencesFrame () {
		userPreferencesFrame.setVisible (true);
	}
	
	public UserPreferencesFrame getUserPreferencesFrame () {
		return userPreferencesFrame;
	}
	
	public void showAuditFrame () {
		String tActorName;
		CorporationList tCompanies;
		Corporation tCorporation;
		Player tPlayer;

		if (playerManager.getPlayerCount () > 0) {
			tPlayer = playerManager.getPlayer (0);
			tActorName = tPlayer.getName ();
		} else {
			tCompanies = shareCompaniesFrame.getCompanies ();
			tCorporation = tCompanies.getCorporation (0);
			tActorName = tCorporation.getAbbrev ();
		}
		auditFrame.setActorName (tActorName);
		auditFrame.refreshAuditTable (true);
		auditFrame.setVisible (true);
	}
	
	public void showChecksumAuditFrame () {
		checksumAuditFrame.refreshAuditTable ();
		checksumAuditFrame.setVisible (true);
	}

	public boolean triggerPanelExists () {
		boolean tTriggerPanelExists;
		
		if (formationPhase == FormationPhase.NO_FORMATION_PHASE) {
			tTriggerPanelExists = false;
		} else {
			tTriggerPanelExists = true;
		}
		
		return tTriggerPanelExists;
	}
	
	public boolean hasTriggerClass () {
		boolean tHasTriggerClass;
		
		if (phaseManager != PhaseManager.NO_PHASE_MANAGER) {
			tHasTriggerClass = phaseManager.hasTriggerClass ();
		} else {
			tHasTriggerClass = false;
		}
	
		return tHasTriggerClass;
	}

	public void showFormationPhaseFrame () {
		if (hasTriggerClass ()) {
			prepareFormationPhase ();
			if (formationPhase != FormationPhase.NO_FORMATION_PHASE) {
				formationPhase.rebuildFormationPanel ();
			} else {
				System.err.println ("Formation Phase not available to be shown");
			}
		}
	}

	public void prepareFormationPhase () {
		FormationPhase tFormationPhase;
		
		tFormationPhase = FormationPhase.NO_FORMATION_PHASE;
		if (triggerClass == TriggerClass.NO_TRIGGER_CLASS) {
			tFormationPhase = new FormationPhase (this);
			tFormationPhase.showFormationFrame ();
		} else if (triggerClass instanceof FormationPhase) {
			tFormationPhase = (FormationPhase) triggerClass;
		}
		setFormationPhase (tFormationPhase);
	}

	public void setFormationPhase (FormationPhase aFormationPhase) {
		formationPhase = aFormationPhase;
	}
	
	public FormationPhase getFormationPhase () {
		return formationPhase;
	}
	
	public void fillChecksumAuditFrame () {
		int tChecksumIndex;
		int tChecksumCount;
		Checksum tChecksum;
		
		if (checksums != Checksums.NO_CHECKSUMS) { 
			tChecksumCount = checksums.size ();
			for (tChecksumIndex = 0; tChecksumIndex < tChecksumCount; tChecksumIndex++) {
				tChecksum = checksums.get (tChecksumIndex);
				checksumAuditFrame.addRow (tChecksum);
			}
		}
	}

	public void fillAuditFrame (String aActorName) {
		roundManager.fillAuditFrame (auditFrame, aActorName);
	}

	public void showChatClient () {
		if (networkJGameClient != JGameClient.NO_JGAME_CLIENT) {
			networkJGameClient.setVisible (true);
			addNewFrame (networkJGameClient);
		}
	}

	public int getOperatingRoundCount () {
		return roundManager.getOperatingRoundCount ();
	}
	
	public int getCurrentPhase () {
		return phaseManager.getCurrentPhase ();
	}

	public int getTotalCash () {
		int tTotalCash;
		int tBankCash;
		int tAllPlayerCash;
		int tAllCorpCash;
		int tAllEscrows;
		int tAllMinorCash;
		int tAllShareCash;

		tTotalCash = 0;
		tBankCash = bank.getCash ();
		tAllEscrows = privatesFrame.getTotalEscrow ();
		tAllPlayerCash = playerManager.getTotalPlayerCash ();
		tAllMinorCash = minorCompaniesFrame.getTotalCorpCash ();
		tAllShareCash = shareCompaniesFrame.getTotalCorpCash ();
		tAllCorpCash = tAllMinorCash + tAllShareCash;

		tTotalCash = tBankCash + tAllPlayerCash + tAllEscrows + tAllCorpCash;

		return tTotalCash;
	}

	public void hideAuctionFrame () {
		auctionFrame.hideAuctionFrame ();
	}

	public void setFrameBackgrounds () {
		roundManager.setFrameBackgrounds ();
	}

	public void resetRoundFrameBackgrounds () {
		roundManager.resetBackgrounds ();
	}

	public boolean isClientCurrentPlayer () {
		boolean tIsClientCurrentPlayer;
		Player tCurrentPlayer;
		Player tClientPlayer;

		tIsClientCurrentPlayer = false;
		tClientPlayer = playerManager.getPlayer (clientUserName);
		tCurrentPlayer = playerManager.getCurrentPlayer ();
		if (tClientPlayer.equals (tCurrentPlayer)) {
			tIsClientCurrentPlayer = true;
		}

		return tIsClientCurrentPlayer;
	}

	public boolean isAAuctionRound () {
		return roundManager.isAAuctionRound ();
	}

	public void showGEFrame () {
		game18XXFrame.setVisible (true);
		game18XXFrame.toFront ();
	}

	public String requestGameSupport (String aRequestGameSupport) {
		String tGameSupportResponse;
		
		tGameSupportResponse = networkJGameClient.requestGameSupport (gameID, aRequestGameSupport);
		
		return tGameSupportResponse;
	}

	public boolean isLastActionComplete () {
		return roundManager.isLastActionComplete ();
	}

	public boolean bankIsBroken () {
		return bank.isBroken ();
	}

	public void declareBankruptcy (String aCompanyAbbrev) {
		System.out.println ("Game Manager set to Bankruptcy Declared by " + aCompanyAbbrev);
	}

	public void disconnect () {
		if (isNetworkGame ()) {
			networkJGameClient.disconnect ();
		}
	}

	public boolean isConnected () {
		boolean tIsConnected = false;

		if (isNetworkGame ()) {
			tIsConnected = networkJGameClient.isConnected ();
		}

		return tIsConnected;
	}

	/**
	 * Get the default Alert Color
	 * 
	 * @return the Default Alert Color
	 */
	public Color getAlertColor () {
		Color tAlertColor;

		tAlertColor = Color.ORANGE;
		
		return tAlertColor;
	}

	public Color getDefaultColor () {
		Color tDefaultColor;
		
		tDefaultColor = shareCompaniesFrame.getBackground ();
		
		return tDefaultColor;
	}

	@Override
	public void updateDisconnectButton () {
		game18XXFrame.updateDisconnectButton ();
	}

	@Override
	public Image getIconImage () {
		return game18XXFrame.getIconImage ();
	}

	public Benefit findBenefit (String aBenefitName) {
		Benefit tBenefit;

		tBenefit = privatesFrame.findBenefit (aBenefitName);

		return tBenefit;
	}

	public Action getLastAction (int aActionOffset) {
		Action tLastAction;

		tLastAction = roundManager.getLastAction (aActionOffset);

		return tLastAction;
	}
	
	public int getActionNumber () {
		return roundManager.getActionNumber ();
	}
}
