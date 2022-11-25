package ge18xx.game;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.NodeList;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.CorporationList;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TrainCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.variants.VariantEffect;
import ge18xx.map.HexMap;
import ge18xx.market.Market;
import ge18xx.network.GameSupportHandler;
import ge18xx.network.JGameClient;
import ge18xx.network.NetworkGameSupport;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Escrow;
import ge18xx.player.ParPriceFrame;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.round.RoundManager;
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
import ge18xx.toplevel.CorporationTableFrame;
import ge18xx.toplevel.FrameInfoFrame;
import ge18xx.toplevel.MapFrame;
import ge18xx.toplevel.MarketFrame;
import ge18xx.toplevel.MinorCompaniesFrame;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.toplevel.PrivatesFrame;
import ge18xx.toplevel.ShareCompaniesFrame;
import ge18xx.toplevel.TileDefinitionFrame;
import ge18xx.toplevel.TileTrayFrame;
import ge18xx.toplevel.XMLFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.FileGEFilter;
import ge18xx.utilities.FileUtils;
import ge18xx.utilities.JFileMChooser;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

// TODO -- Create an abstract 'GenericGameManager' Super Class that holds non-specific Game information like:
//   GameInfo, PlayerManager, configFrames, PlayerInputFrame, frameInfo, and the other non-specific
//   objects flagged below. Move the methods that use only these objects to the super-class
//
// Also create a 'NetworkGameManager' Sub-class that implements the NetworkGameSupport objects and
// methods into this sub-class

public class GameManager extends Component implements NetworkGameSupport {

	// Static Constants
	private static final long serialVersionUID = 1L;
	public static final ElementName EN_CONFIG = new ElementName ("Config");
	public static final ElementName EN_SAVEGAMEDIR = new ElementName ("SaveGameDir");
	public static final ElementName EN_GAME = new ElementName ("Game");
	public static final ElementName EN_FRAMES = new ElementName ("Frames");
	public static final AttributeName AN_GAME_NAME = new AttributeName ("gameName");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_GE_VERSION = new AttributeName ("version");
	public static final String NO_GAME_NAME = "<NONE>";
	public static final String UNSPECIFIED_GAME_NAME = "UNSPECIFIED";
	public static final String NO_FILE_NAME = "<NONE>";
	public static final String AUTO_SAVES_DIR = "autoSaves";
	public static final GameManager NO_GAME_MANAGER = null;
	public static final String EMPTY_GAME_ID = "";

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
	Config configData;
	boolean gameChangedSinceSave;
	boolean gameStarted;
	boolean gameEnding;
	Logger logger;
	String userDir;
	FileUtils fileUtils;
	FileGEFilter fileGEFilter;

	// 18XX Game Specific Objects
	GameInfo activeGame;
	Game_18XX game18XXFrame;
	PlayerManager playerManager;
	RoundManager roundManager;
	PhaseManager phaseManager;
	BankPool bankPool;
	Bank bank;

	// Various Frames the Game Manager tracks -- Consider adding to a "FrameManager"
	// Class
	// These Frames for Companies, have the CorporationList
	// TODO: Consider having an Array/List of CorporationLists, that tracks what
	// type of Company
	// it is, and has the Frame in the Corporation List
	//
	PrivatesFrame privatesFrame;
	MinorCompaniesFrame minorCompaniesFrame;
	ShareCompaniesFrame shareCompaniesFrame;
	UserPreferencesFrame userPreferencesFrame;
	
	AuctionFrame auctionFrame;
	MapFrame mapFrame;
	MarketFrame marketFrame;
	CitiesFrame citiesFrame;
	TileTrayFrame tileTrayFrame;
	AuditFrame auditFrame;
	TileDefinitionFrame tileDefinitionFrame;
	PlayerInputFrame playerInputFrame;
	FrameInfoFrame frameInfoFrame;

	// Other Frames include:
	// RoundFrame -- held by RoundManager
	// TrainRevenueFrame -- held by TrainCompany
	// ParPriceFrame
	// BuyTrainFrame
	// EmergencyBuyTrainFrame
	// BuyPrivateFrame (makeOffer for Private or Train)

	// Network Game Objects
	JGameClient networkJGameClient;
	SavedGames networkSavedGames;
	boolean notifyNetwork;
	boolean applyingNetworkAction;

	public GameManager () {
		fileUtils = new FileUtils ("18xx.");
		fileGEFilter = new FileGEFilter ("18XX Save Game - XML", fileUtils);
		setUserDir ();
		setDefaults ();
		setGame (GameInfo.NO_GAME_INFO);
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
		loadConfig ();
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
		return this.game18XXFrame.getJMenuBar ();
	}
	
	private void storeAllFrames (Game_18XX aGame_18XX_Frame) {
		configFrames = new ArrayList<XMLFrame> ();
		addNewFrame (aGame_18XX_Frame);
		game18XXFrame = aGame_18XX_Frame;
		setPlayerInputFrame (PlayerInputFrame.NO_PLAYER_INPUT_FRAME);
		setFrameInfoFrame (XMLFrame.NO_XML_FRAME);

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

	public void setLoadSavedFile (File aLoadSavedFile) {
		loadSavedFile = aLoadSavedFile;
	}

	public JPanel buildPrivatesForPurchaseJPanel (ItemListener aItemListener, int aAvailableCash) {
		JPanel tPrivatesJPanel;

		tPrivatesJPanel = privatesFrame.buildPrivatesForPurchaseJPanel (aItemListener, aAvailableCash);

		return tPrivatesJPanel;
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
			tCitiesFrame = new CitiesFrame (tFullFrameTitle, tActiveGameName);
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
			tShareCompany = (ShareCompany) Corporation.NO_CORPORATION;
		}
		if ((aSelectedParPrice > 0) && (tShareCompany != Corporation.NO_CORPORATION)) {
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

	public String createFrameTitle (String aBaseName) {
		String tFullTitle;

		tFullTitle = getActiveGameName () + " " + aBaseName + " Frame";
		if (isNetworkGame ()) {
			tFullTitle += " (" + clientUserName + ")";
		}

		return tFullTitle;
	}

	private void createUserPreferencesFrame () {
		UserPreferencesFrame tUserPreferencesFrame;
		String tFullTitle;

		if (gameIsStarted ()) {
			tFullTitle = createFrameTitle ("User Preferences");
			tUserPreferencesFrame = new UserPreferencesFrame (tFullTitle);
			setUserPreferencesFrame (tUserPreferencesFrame);
			game18XXFrame.enableUserPreferencesMenuItem ();
		}
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

	public ArrayList<XMLFrame> getConfigFrames () {
		return configFrames;
	}

	private void createFrameInfoFrame () {
		FrameInfoFrame tFrameInfoFrame;
		String tFullTitle;

		if (gameIsStarted ()) {
			tFullTitle = createFrameTitle ("Frame Info");

			tFrameInfoFrame = new FrameInfoFrame (tFullTitle, this);
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
			}
			loadColorScheme (tBaseDir, tMapFrame);

			tMapFrame.setCityInfo (citiesFrame.getCities ());
			tMapFrame.addCorporationList (privatesFrame, CorporationList.TYPE_NAMES [0]);
			tMapFrame.addCorporationList (minorCompaniesFrame, CorporationList.TYPE_NAMES [1]);
			tMapFrame.addCorporationList (shareCompaniesFrame, CorporationList.TYPE_NAMES [2]);
		}
	}

	public void loadColorScheme (String tBaseDir, MapFrame tMapFrame) {
		String tFullURL;
		XMLDocument tXMLDocument;
		
		tFullURL = game18XXFrame.getURLBase () + "Color-Scheme.xml";
		tXMLDocument = new XMLDocument (tFullURL);

		try {
			tMapFrame.loadXMLColorScheme (tXMLDocument, tMapFrame.getTerrain ());
			tMapFrame.loadXMLColorScheme (tXMLDocument, tileTrayFrame.getTileType ());
		} catch (Exception tException) {
			logger.error ("Problem Loading Color Scheme: " + tException);
		}
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
			} catch (Exception tException) {
				logger.error (tException);
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
			} catch (Exception tException) {
				logger.error (tException);
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
				tShareCompaniesFrame.loadXML (tXMLDocument, tShareCompaniesFrame.getCompanies ());
				tMarket = marketFrame.getMarket ();
				tShareCompaniesFrame.setMarket (tMarket);
				tShareCompaniesFrame.setStartCells ();
				tShareCompaniesFrame.updateCorpComboBox ();
				tParValues = tMarket.getAllStartCells ();
				tShareCompaniesFrame.updateParValuesComboBox (tParValues);
			} catch (Exception tException) {
				logger.error (tException);
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
		Train tTrain;

		tTrain = bank.getNextAvailableTrain ();
		tNextTrainName = tTrain.getName ();

		return phaseManager.getMinSharesToFloat (tNextTrainName);
	}

	public Integer [] getAllStartCells () {
		Market tMarket;

		tMarket = marketFrame.getMarket ();

		return tMarket.getAllStartCells ();
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
			tTileDefinitionFrame = new TileDefinitionFrame (tFrameTitle, tTileTrayFrame, tActiveGameName);
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

	public boolean doIncrementalCapitalization () {
		return phaseManager.doIncrementalCapitalization ();
	}

	public boolean canBuyPrivate () {
		return phaseManager.canBuyPrivate ();
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

	public void fullOwnershipAdjustment () {
		marketFrame.fullOwnershipAdjustment (roundManager.getStockRound ());
	}

	public boolean gameChanged () {
		return gameChangedSinceSave;
	}

	public boolean gameHasPrivates () {
		return activeGame.hasPrivates ();
	}

	public boolean gameHasLoans () {
		return activeGame.gameHasLoans ();
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

	public String getActiveGameName () {
		String tName;

		tName = NO_GAME_NAME;
		if (gameIsStarted ()) {
			tName = activeGame.getName ();
		}

		return tName;
	}

	public ActorI getActor (String aActorName) {
		return getActor (aActorName, false);
	}

	public ActorI getActor (String aActorName, boolean aLookingForPrivate) {
		ActorI tActor;

		tActor = ActorI.NO_ACTOR;
		if (aActorName == ActorI.NO_NAME) {
			logger.error ("Actor Name IS NULL<-----");
		} else {
			if (aActorName.equals (bank.getName ())) {
				tActor = bank;
			} else if (aActorName.equals (bankPool.getName ())) {
				tActor = bankPool;
			} else if (aActorName.equals (bank.getStartPacketFrameName ())) {
				tActor = bank.getStartPacketFrame ();
			} else {
				tActor = playerManager.getActor (aActorName);
			}

			if (tActor == ActorI.NO_ACTOR) {
				tActor = shareCompaniesFrame.getActor (aActorName);
			}

			if (aLookingForPrivate || (tActor == ActorI.NO_ACTOR)) {
				tActor = privatesFrame.getActor (aActorName);
			}

			if (tActor == ActorI.NO_ACTOR) {
				tActor = minorCompaniesFrame.getActor (aActorName);
			}

			if (tActor == ActorI.NO_ACTOR) {
				tActor = roundManager.getActor (aActorName);
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

	public String getCompaniesFileName () {
		return getFileName (File18XX.COMPANIES_TYPE);
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
		return privatesFrame.getCountOfOpenCompanies ();
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

	public CorporationList getPrivates () {
		return privatesFrame.getCompanies ();
	}

	public CorporationList getShareCompanies () {
		return shareCompaniesFrame.getCompanies ();
	}

	public ShareCompany getShareCompany (String aCompanyAbbrev) {
		return shareCompaniesFrame.getShareCompany (aCompanyAbbrev);
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

	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;

		tCurrentHolder = bank.getCurrentHolder (aLoadedCertificate);
		if (tCurrentHolder == PortfolioHolderI.NO_HOLDER) {
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

	public boolean hasMustBuyCertificate () {
		return bank.hasMustBuyCertificate ();
	}

	public boolean hasMustSell () {
		return bank.hasMustSell ();
	}

	public Certificate getMustSellCertificate () {
		return bank.getMustSellCertificate ();
	}

	@Override
	public GameInfo getSelectedGame () {
		return playerInputFrame.getSelectedGame ();
	}

	@Override
	public void initiateNetworkGame () {
		initiateGame (getSelectedGame ());
	}

	@Override
	public XMLElement getGameVariantsXMLElement () {
		XMLElement tGameVariantsXMLElement;
		XMLDocument tXMLDocument;

		tXMLDocument = new XMLDocument ();
		tGameVariantsXMLElement = activeGame.getGameVariantsXMLElement (tXMLDocument);

		return tGameVariantsXMLElement;
	}

	public void initiateGame (GameInfo aGameInfo) {
		setGame (aGameInfo);
		initiateGame ();
		setNotifyNetwork (true);
		// For Normal Start, we need to Notify Clients of Actions
		// The Loading of a Save Game does it's own reset once the Load is complete
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
			game18XXFrame.initiateGame ();
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
			removeInactiveCompanies ();
			playerInputFrame.randomizePlayerOrder ();
			setupPlayers ();
			tPrivates = getPrivates ();
			tMinors = getMinorCompanies ();
			tShares = getShareCompanies ();

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
			createUserPreferencesFrame ();
			applyConfigSettings ();
			createFrameInfoFrame ();
			setFrameBackgrounds ();
		}
	}

	private void setBank (int aInitialTreasury) {
		bank = new Bank (aInitialTreasury, this);
	}

	private void setBankPool (BankPool aBankPool) {
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
		String tAutoSaveFileName = "";

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
				tResult = chooser.showSaveDialog (this);
			} else {
				tResult = chooser.showOpenDialog (this);
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
		File tSaveDirectory, tNewSaveDirectory;
		String tSaveDirectoryPath;
		Point tNewPoint;

		tNewPoint = getOffsetGEFrame ();
		chooser = new JFileMChooser ();
		chooser.setDialogTitle ("Find Saved Game File to Load");
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
			System.out.println ("Need to Retrieve Actions for " + getGameID () + " from " + tNextActionNumber + " to "
					+ tLastNetworkAction);

			for (tActionNumber = tNextActionNumber; tActionNumber <= tLastNetworkAction; tActionNumber++) {
				tNextAction = networkJGameClient.fetchActionWithNumber (tActionNumber, getGameID ());
				System.out.println ("Provided Action [" + tNextAction + "]");
				handleNetworkAction (tNextAction);
			}
		} else {
			System.out.println ("Actions are Current at " + tLastNetworkAction);
		}

	}

	private boolean loadSavedXMLFile () {
		List<ActionStates> tAuctionStates;
		boolean tGoodLoad = false;
		int tLastActionNumber;
		String tGameName;

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
		setNotifyNetwork (true);

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

		if (aSaveGame != null) {
			try {
				XMLDocument tXMLDocument = new XMLDocument (aSaveGame);
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
		boolean tLoadedSaveGame;
		XMLNode tXMLSaveGame;
		NodeList tChildren;
		int tChildrenCount, tIndex;
		boolean tGameIdentified = false, tPlayersLoaded = false, tGameInitiated = false;

		tLoadedSaveGame = false;
		if (aXMLDocument.validDocument ()) {
			playerManager = new PlayerManager (this); /*
														 * Create a new Player Manager - repeated openings, should not
														 * add players to an existing set
														 */
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
					tGameIdentified = true;
				}
				if (playerManager.getPlayerCount () > 1) {
					tPlayersLoaded = true;
				}
			}
			fixLoadedRoutes ();
			if ((activeGame != GameInfo.NO_GAME_INFO) && (playerManager.getPlayerCount () > 1)) {
				tLoadedSaveGame = true;
			}
		}
		updateRoundFrame ();

		return tLoadedSaveGame;
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
			}
			if (PlayerInputFrame.EN_PLAYERS.equals (tChildName)) {
				playerManager.loadPlayers (tChildNode, activeGame);
			}
			if (PhaseManager.EN_PHASE.equals (tChildName)) {
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

	public void handleIfGameInitiated (XMLNode aChildNode, String aChildName) {
		if (Action.EN_ACTIONS.equals (aChildName)) {
			roundManager.loadActions (aChildNode, this);
		}
		if (RoundManager.EN_ROUNDS.equals (aChildName)) {
			roundManager.loadRoundStates (aChildNode);
		}
		if (Player.EN_PLAYER_STATES.equals (aChildName)) {
			playerManager.loadPlayerStates (aChildNode);
		}
		if (Bank.EN_BANK_STATE.equals (aChildName)) {
			bank.loadBankState (aChildNode);
		}
		if (BankPool.EN_BANK_POOL_STATE.equals (aChildName)) {
			bankPool.loadBankPoolState (aChildNode);
		}
		if (Market.EN_MARKET.equals (aChildName)) {
			marketFrame.loadMarketTokens (aChildNode);
		}
		if (PrivatesFrame.EN_PRIVATES.equals (aChildName)) {
			privatesFrame.loadStates (aChildNode);
			cleanupLoadedPrivates ();
		}
		if (MinorCompaniesFrame.EN_MINORS.equals (aChildName)) {
			minorCompaniesFrame.loadStates (aChildNode);
		}
		if (ShareCompaniesFrame.EN_SHARES.equals (aChildName)) {
			shareCompaniesFrame.loadStates (aChildNode);
		}
		if (HexMap.EN_MAP.equals (aChildName)) {
			mapFrame.loadMapStates (aChildNode);
		}
	}

	public void loadNetworkJGameClient (XMLNode tChildNode) {
		String tServerIP;
		int tServerPort;
		JGameClient tNetworkJGameClient;

		tServerIP = tChildNode.getThisAttribute (JGameClient.AN_SERVER_IP);
		tServerPort = tChildNode.getThisIntAttribute (JGameClient.AN_SERVER_PORT);
		tNetworkJGameClient = new JGameClient (GameSet.CHAT_TITLE + " (" + clientUserName + ")", this, tServerIP,
				tServerPort);
		tNetworkJGameClient.addLocalPlayer (clientUserName, true);
		tNetworkJGameClient.addSPGameActivity ();
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

	public void updateRoundFrameParPrices () {
		roundManager.updateParPrices ();
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

	public void saveGame () {
		XMLDocument tXMLDocument;
		XMLElement tXMLElement, tSaveGameElement;
		String tFullActionReport;

		tXMLDocument = new XMLDocument ();
		tSaveGameElement = tXMLDocument.createElement (EN_GAME);
		tSaveGameElement.setAttribute (AN_GE_VERSION, getGEVersion ());

		if (isNetworkGame ()) {
			tXMLElement = networkJGameClient.getNetworkElement (tXMLDocument);
			tSaveGameElement.appendChild (tXMLElement);
		}

		/* Save the Basic Game Information */
		tXMLElement = activeGame.getGameInfoElement (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the Player Names Information */
		tXMLElement = playerManager.getPlayerElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the Phase Index (Current Phase) */
		tXMLElement = phaseManager.getPhaseElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the Actions performed */
		tXMLElement = roundManager.getActionElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the Round Information, Stock and Operating */
		tXMLElement = roundManager.getRoundState (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the Player Status, Cash, Bids, and Portfolio */
		tXMLElement = playerManager.getPlayerStateElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the current Bank Balance, and Rusted Train Portfolio */
		tXMLElement = bank.getBankStateElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the Bank Pool Stock Portfolio and Train Portfolio */
		tXMLElement = bankPool.getBankPoolStateElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the Tokens on the Market */
		tXMLElement = marketFrame.getMarketStateElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		/* Save the Privates, Minors, and Share Company Information */
		appendCompanyFrameXML (tSaveGameElement, tXMLDocument, privatesFrame);
		appendCompanyFrameXML (tSaveGameElement, tXMLDocument, minorCompaniesFrame);
		appendCompanyFrameXML (tSaveGameElement, tXMLDocument, shareCompaniesFrame);

		/* Also need to save the Map Information */
		/* Save The Tile Placements, Orientations, and Token Placements */
		tXMLElement = mapFrame.getMapStateElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);

		// Append Save Game Element to Document just before outputing it.
		tXMLDocument.appendChild (tSaveGameElement);

		tXMLDocument.outputXML (saveFile);

		tFullActionReport = roundManager.getFullActionReport ();
		outputToFile (tFullActionReport, autoSaveActionReportFile);
	}

	public void outputToFile (String aReport, File aFile) {

		try {
			FileWriter tFWout = new FileWriter (aFile);
			tFWout.write (aReport);
			tFWout.close ();
		} catch (Exception tException) {
			System.err.println (tException);
			tException.printStackTrace ();
		}
	}

	public void appendCompanyFrameXML (XMLElement aSaveGameElement, XMLDocument aXMLDocument,
			CorporationTableFrame aCorporationTableFrame) {
		XMLElement tXMLElement;

		if (aCorporationTableFrame != CorporationTableFrame.NO_CORP_TABLE_FRAME) {
			tXMLElement = aCorporationTableFrame.getCorporationStateElements (aXMLDocument);
			if (tXMLElement != XMLElement.NO_XML_ELEMENT) {
				aSaveGameElement.appendChild (tXMLElement);
			}
		}
	}

	public void autoSaveGame () {
		File tPriorSave = saveFile;

		saveFile = autoSaveFile;
		if (gameStarted) {
			saveGame ();
		}
		saveFile = tPriorSave;
	}

	public void saveAGame (boolean aOverwriteFile) {
		if (saveFile == null) {
			aOverwriteFile = false;
		}
		if (aOverwriteFile) {
			saveGame ();
			setGameChanged (false);
		} else {

			File tSaveDirectory;
			String tFileName;
			String tOriginalSaveGameDir, tNewSaveGameDir;

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

	public void setGame (GameInfo aGame) {
		activeGame = aGame;
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

	private void setMinorCompaniesFrame (XMLFrame aXMLFrame) {
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

	private void setPrivatesFrame (XMLFrame aXMLFrame) {
		privatesFrame = (PrivatesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	private void setShareCompaniesFrame (XMLFrame aXMLFrame) {
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

	private void setUserPreferencesFrame (XMLFrame aXMLFrame) {
		userPreferencesFrame = (UserPreferencesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}
	
	private void setFrameInfoFrame (XMLFrame aXMLFrame) {
		frameInfoFrame = (FrameInfoFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	private void setTileDefinitionFrame (XMLFrame aXMLFrame) {
		tileDefinitionFrame = (TileDefinitionFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
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
		tPlayerStartingCash = activeGame.getStartingCash (tPlayerCount);
		tCertificateLimit = activeGame.getCertificateLimit (tPlayerCount);
		for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
			tPlayerName = playerInputFrame.getPlayerName (tIndex);
			tPlayer = new Player (tPlayerName, playerManager, tCertificateLimit);
			bank.transferCashTo (tPlayer, tPlayerStartingCash);
			playerManager.addPlayer (tPlayer);
		}

		if (roundManagerIsValid ()) {
			roundManager.updateAllCorporationsBox ();
		}
	}

	public void showCities () {
		citiesFrame.showFrame ();
	}

	public void setAuctionFrameLocation () {
		Point tNewPoint = getOffsetPlayerFrame ();

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
		tCorporationFrame = tOperatingCorporation.getCorporationFrame ();
		tNewPoint = tCorporationFrame.getOffsetFrame ();

		return tNewPoint;
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
		minorCompaniesFrame.showFrame ();
	}

	public void showPrivateCompanies () {
		privatesFrame.showFrame ();
	}

	public void showShareCompanies () {
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
		if (roundManager.getCurrentRoundType ().equals (ActorI.ActionStates.StockRound)) {
			updateAllPlayerFrames ();
		} else if (roundManager.getCurrentRoundType ().equals (ActorI.ActionStates.OperatingRound)) {
			roundManager.updateOperatingCorporationFrame ();
		}

	}

	public void updateAllPlayerFrames () {
		playerManager.updateAllPlayerFrames ();
	}

	public void updateRoundFrame () {
		if (roundManagerIsValid ()) {
			roundManager.updateRoundFrame ();
		}
	}

	// Get an Array of all Available Trains from Bank, and BankPool together
	public Train [] getBankAvailableTrains () {
		Train [] tBankAvailableTrains;
		Train [] tBankPoolAvailableTrains;
		Train [] tAvailableTrains;
		int tIndex = 0, tBankCount = 0, tBankPoolCount = 0;

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
		XMLNode tActionNode, tGSResponseNode;
		NodeList tActionChildren;
		String tGSRNodeName;
		String tActionNodeName;
		int tActionNodeCount, tActionIndex;

		tXMLNetworkAction = new XMLDocument ();
		tXMLNetworkAction = tXMLNetworkAction.ParseXMLString (aNetworkAction);
		tGSResponseNode = tXMLNetworkAction.getDocumentNode ();
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
		XMLNode tActionNode;
		NodeList tActionChildren;
		int tActionNodeCount, tActionIndex;
		String tActionNodeName;
		String tGANodeName;
		String tBroadcast;
		String tPlayerOrder;

		tXMLGameActivity = new XMLDocument ();
		tXMLGameActivity = tXMLGameActivity.ParseXMLString (aGameActivity);
		tXMLGameActivityNode = tXMLGameActivity.getDocumentNode ();
		tGANodeName = tXMLGameActivityNode.getNodeName ();
		if (JGameClient.EN_GAME_ACTIVITY.equals (tGANodeName)) {
			tActionChildren = tXMLGameActivityNode.getChildNodes ();
			tActionNodeCount = tActionChildren.getLength ();
			try {
				for (tActionIndex = 0; tActionIndex < tActionNodeCount; tActionIndex++) {
					tActionNode = new XMLNode (tActionChildren.item (tActionIndex));
					tActionNodeName = tActionNode.getNodeName ();
					if (Action.EN_ACTION.equals (tActionNodeName)) {
						sendNetworkAction (tActionNode);
					} else if (JGameClient.EN_GAME_SELECTION.equals (tActionNodeName)) {
						handleGameSelection (tActionNode);
					} else if (JGameClient.EN_PLAYER_ORDER.equals (tActionNodeName)) {
						tPlayerOrder = tActionNode.getThisAttribute (JGameClient.AN_PLAYER_ORDER);
						tBroadcast = tActionNode.getThisAttribute (JGameClient.AN_BROADCAST_MESSAGE);
						playerInputFrame.handleResetPlayerOrder (tPlayerOrder, tBroadcast);
					} else if (ActionManager.EN_REMOVE_ACTION.equals (tActionNodeName)) {
						// RemoveAction should be ignored
					} else if (XMLNode.XML_TEXT_TAG.equals (tActionNodeName)) {
						// If a #text Node, ignore -- it is empty
					} else {
						logger.error ("Node Name is [" + tActionNodeName + "] which is Unrecognized");
					}
				}
			} catch (Exception tException) {
				logger.error (tException.getMessage (), tException);
			}
		}
	}

	public void handleGameSelection (XMLNode aGameSelectionNode) {
		int tGameIndex;
		String tBroadcast;
		String tGameID;
		String tNodeName;
		int tIndex;
		int tChildCount;
		NodeList tChildren;
		XMLNode tChildNode;
		XMLNode tVariantEffectsNode;

		tGameIndex = aGameSelectionNode.getThisIntAttribute (JGameClient.AN_GAME_INDEX);
		tBroadcast = aGameSelectionNode.getThisAttribute (JGameClient.AN_BROADCAST_MESSAGE);
		tGameID = aGameSelectionNode.getThisAttribute (JGameClient.AN_GAME_ID);
		tChildren = aGameSelectionNode.getChildNodes ();
		tChildCount = tChildren.getLength ();
		tVariantEffectsNode = VariantEffect.NO_VARIANT_EFFECTS_NODE;
		for (tIndex = 0; tIndex < tChildCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tNodeName = tChildNode.getNodeName ();
			if (VariantEffect.EN_VARIANT_EFFECTS.equals (tNodeName)) {
				tVariantEffectsNode = tChildNode;
			}
		}

		setGameID (tGameID);
		playerInputFrame.handleGameSelection (tGameIndex, tVariantEffectsNode, tBroadcast);
		networkJGameClient.updateReadyButton ("READY", true, "Hit when ready to play");
	}

	public void setApplyingAction (boolean aApplyingAction) {
		applyingNetworkAction = aApplyingAction;
	}

	public boolean applyingAction () {
		return applyingNetworkAction;
	}

	@Override
	public void updatePlayerCountLabel () {
		playerInputFrame.updatePlayerCountLabel ();
	}

	@Override
	public void addNetworkPlayer (String aPlayerName) {
		if (notifyNetwork) {
			playerInputFrame.addNetworkPlayer (aPlayerName);
		}
	}

	@Override
	public void removeNetworkPlayer (String aPlayerName) {
		if (notifyNetwork) {
			playerInputFrame.removeNetworkPlayer (aPlayerName);
		}
	}

	@Override
	public void removeAllNetworkPlayers () {
		playerInputFrame.removeAllPlayers ();
	}

	public void setClientUserName (String aClientUserName) {
		clientUserName = aClientUserName;
	}

	public String getFirstPlayerName () {
		String tFirstPlayerName;
		
		tFirstPlayerName = clientUserName;
		if (userPreferencesFrame != XMLFrame.NO_XML_FRAME) {
			tFirstPlayerName = userPreferencesFrame.getFirstPlayerName (this);
		}
		
		return tFirstPlayerName;
	}
	
	@Override
	public String getClientUserName () {
		return clientUserName;
	}

	@Override
	public String getGameID () {
		return gameID;
	}

	public boolean isNetworkAndIsThisClient (String aClientName) {
		boolean tIsNetworkAndClient = true;

		if (isNetworkGame ()) {
			if (!clientUserName.equals (aClientName)) {
				tIsNetworkAndClient = false;
			}
		}

		return tIsNetworkAndClient;
	}

	public void loadConfig () {
		XMLDocument tXMLDocument = null;
		String tConfigFileName;
		File tConfigFile;

		tConfigFileName = getConfigFileName ();
		tConfigFile = new File (tConfigFileName);
		if (tConfigFile.exists ()) {
			try {
				tXMLDocument = new XMLDocument (tConfigFile);
			} catch (Exception tException) {
				logger.error ("Oops, mucked up the Config File Access [" + tConfigFileName + "].");
				logger.error ("Exception Message [" + tException.getMessage () + "].", tException);
			}
			if (tXMLDocument != XMLDocument.NO_XML_DOCUMENT) {
				@SuppressWarnings ("null")
				XMLNode tXMLNode = tXMLDocument.getDocumentNode ();
				configData = new Config (tXMLNode, this);
			} else {
				configData = new Config (this);
			}
		} else {
			configData = new Config (this);
		}
	}

	public void saveConfig (boolean aOverwriteFile) {
		XMLDocument tXMLDocument;
		File tConfigFile;

		tXMLDocument = createNewConfigDocument ();
		tConfigFile = getConfigFile ();
		tXMLDocument.outputXML (tConfigFile);
	}

	public String getConfigFileName () {
		return "ge18xx." + clientUserName + ".cfg.xml";
	}

	public File getConfigFile () {
		return new File (getConfigFileName ());
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
		boolean tFrameIsPresent = false;
		String tSpecificFrameName;

		if (aFrameName != null) {
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

	private void applyConfigSettings () {
		GameFrameConfig tGameFrameConfig;

		tGameFrameConfig = getGameFrameConfig ();
		if (tGameFrameConfig != Config.NO_GAME_FRAME) {
			for (XMLFrame tXMLFrame : configFrames) {
				tXMLFrame.setFrameToConfigDefaults (tGameFrameConfig, getVisibileConfig ());
			}
		}
	}

	public GameFrameConfig getGameFrameConfig () {
		GameFrameConfig tGameFrameConfig;
		String tGameName = getGameName ();

		tGameFrameConfig = configData.getGameFrameConfigFor (tGameName);

		return tGameFrameConfig;
	}

	public String getVisibileConfig () {
		return XMLFrame.getVisibileConfig ();
	}

	public XMLDocument createNewConfigDocument () {
		XMLElement tConfigElement, tFramesElement, tFrameElement, tSaveGameDirElement;
		XMLDocument tXMLDocument;
		String tActiveGameName;

		tXMLDocument = new XMLDocument ();
		tConfigElement = tXMLDocument.createElement (EN_CONFIG);

		tSaveGameDirElement = tXMLDocument.createElement (EN_SAVEGAMEDIR);
		tSaveGameDirElement.setAttribute (AN_NAME, configData.getSaveGameDirectory ());
		tConfigElement.appendChild (tSaveGameDirElement);

		tFramesElement = tXMLDocument.createElement (EN_FRAMES);

		if (activeGame == GameInfo.NO_GAME_INFO) {
			tActiveGameName = UNSPECIFIED_GAME_NAME;
		} else {
			tActiveGameName = activeGame.getName ();
		}
		tFramesElement.setAttribute (AN_GAME_NAME, tActiveGameName);

		for (XMLFrame tXMLFrame : configFrames) {
			if (tXMLFrame != XMLFrame.NO_XML_FRAME) {
				// If the Height and Width are > 0, save it... otherwise it makes no sense since
				// the
				// Frame is not showing anything
				if ((tXMLFrame.getHeight () > 0) && (tXMLFrame.getWidth () > 0)) {
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

	public void addOtherGameConfigs (String aActiveGameName, XMLElement tConfigElement, XMLDocument tXMLDocument) {
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

	private void setGameID (String aGameID) {
		gameID = aGameID;
	}

	@Override
	public void setSelectedGameIndex (int aGameIndex) {
		playerInputFrame.setSelectedGameIndex (aGameIndex);
	}

	public void appendToGameActivity (String aGameActivity) {
		networkJGameClient.appendToGameActivity (aGameActivity);
	}

	public JFrame getJFrameName (String aJFrameTitle) {
		JFrame tJFrame = null;

		if (aJFrameTitle != null) {
			if (aJFrameTitle.equals (auctionFrame.getTitle ())) {
				tJFrame = auctionFrame;
			}
		}

		return tJFrame;
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

	public void showUserPreferencesFrame () {
		userPreferencesFrame.setVisible (true);
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

	public void fillAuditFrame (String aActorName) {
		roundManager.fillAuditFrame (auditFrame, aActorName);
	}

	public void showChatClient () {
		if (networkJGameClient != JGameClient.NO_JGAME_CLIENT) {
			networkJGameClient.setVisible (true);
			addNewFrame (networkJGameClient);
		}
	}

	public int getCurrentPhase () {
		return phaseManager.getCurrentPhase ();
	}

	public int getTotalCash () {
		int tTotalCash = 0;
		int tBankCash, tAllPlayerCash, tAllCorpCash, tAllEscrows;
		int tAllMinorCash, tAllShareCash;

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
		boolean tIsClientCurrentPlayer = false;
		Player tCurrentPlayer, tClientPlayer;

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
		return networkJGameClient.requestGameSupport (gameID, aRequestGameSupport);
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

	@Override
	public void updateDisconnectButton () {
		game18XXFrame.updateDisconnectButton ();
	}

	public Image getIconImage () {
		return game18XXFrame.getIconImage ();
	}

	public void showFrameInfo () {
		if (frameInfoFrame != XMLFrame.NO_XML_FRAME) {
			System.out.println ("Ready to show Frame Info Frame");
			frameInfoFrame.setVisible (true);
		} else {
			System.out.println ("No Frame Info Setup yet");
		}
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
}
