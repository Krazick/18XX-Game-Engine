package ge18xx.game;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import ge18xx.map.HexMap;
import ge18xx.market.Market;
import ge18xx.network.JGameClient;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Escrow;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.Action;
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
import ge18xx.toplevel.CoalCompaniesFrame;
import ge18xx.toplevel.CorporationTableFrame;
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
import ge18xx.train.TrainInfo;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.File18XXFilter;
import ge18xx.utilities.FileUtils;
import ge18xx.utilities.JFileMChooser;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import org.w3c.dom.NodeList;

public class GameManager extends Component implements NetworkGameSupport {
	private static final long serialVersionUID = 1L;
	public static final String NO_GAME_NAME = "<NONE>";
	public static final String NO_FILE_NAME = "<NONE>";
	public static final GameInfo NO_GAME = null;
	public static final XMLFrame NO_FRAME = null;
	public static final BankPool NO_BANK_POOL = null;
	public static final PlayerInputFrame NO_PLAYER_INPUT_FRAME = null;
	public static final int NO_BANK = 0;
	public static final PlayerManager NO_PLAYER_MANAGER = null;
	public static final PhaseManager NO_PHASE_MANAGER = null;
	public static final ElementName EN_CONFIG = new ElementName ("Config");
	public static final ElementName EN_SAVEGAMEDIR = new ElementName ("SaveGameDir");
	public static final ElementName EN_GAME = new ElementName ("Game");
	public static final ElementName EN_FRAMES = new ElementName ("Frames");
	public static final AttributeName AN_GAME_NAME = new AttributeName ("gameName");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_GE_VERSION = new AttributeName ("version");
	boolean gameChangedSinceSave;
	Game_18XX game18XXFrame;
	GameInfo activeGame;
	PlayerManager playerManager;
	RoundManager roundManager;
	PhaseManager phaseManager;
	BankPool bankPool;
	Bank bank;
	AuctionFrame auctionFrame;
	MapFrame mapFrame;
	MarketFrame marketFrame;
	CitiesFrame citiesFrame;
	PrivatesFrame privatesFrame;
	CoalCompaniesFrame coalCompaniesFrame;
	MinorCompaniesFrame minorCompaniesFrame;
	ShareCompaniesFrame shareCompaniesFrame;
	TileTrayFrame tileTrayFrame;
	AuditFrame auditFrame;
	TileDefinitionFrame tileDefinitionFrame;
	PlayerInputFrame playerInputFrame;
	JFileMChooser chooser;
	File saveFile;
	File autoSaveFile;
	String autoSaveFileName;
	JGameClient networkJGameClient;
	boolean notifyNetwork = false;
	String clientUserName;
	String gameID;
	Config configData;
	ArrayList<XMLFrame> configFrames;
	boolean gameStarted;
	boolean applyingNetworkAction = false;
	
	public GameManager () {
		gameID = "";		
	}
	
	public GameManager (Game_18XX aGame_18XX_Frame, String aClientUserName) {
		game18XXFrame = aGame_18XX_Frame;
		configFrames = new ArrayList<XMLFrame> ();
		setGame (NO_GAME);
		setBankPool (NO_BANK_POOL);
		setBank (NO_BANK);
		setPlayerManager (NO_PLAYER_MANAGER);
		setPhaseManager (NO_PHASE_MANAGER);
		setMapFrame (NO_FRAME);
		setCitiesFrame (NO_FRAME);
		setPrivatesFrame (NO_FRAME);
		setMinorCompaniesFrame (NO_FRAME);
		setShareCompaniesFrame (NO_FRAME);
		setTileTrayFrame (NO_FRAME);
		setTileDefinitionFrame (NO_FRAME);
		setPlayerInputFrame (NO_PLAYER_INPUT_FRAME);
		setAuditFrame (NO_FRAME);
		setClientUserName (aClientUserName);
		saveFile = null;
		autoSaveFile = null;
		gameStarted = false;
		gameID = "";
		loadConfig ();
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
		boolean tCanBeExchanged;

		tCanBeExchanged = playerManager.canBeExchanged (aCorporation);
		
		return tCanBeExchanged;
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
		coalCompaniesFrame.clearSelections ();
		minorCompaniesFrame.clearSelections ();
		shareCompaniesFrame.clearSelections ();	
	}
	
	public void clearAllPlayerSelections () {
		playerManager.clearAllPlayerSelections ();
	}
	
	public void closeCompany (int aCompanyID, TransferOwnershipAction aTransferOwnershipAction) {
		privatesFrame.closeCompany (aCompanyID, aTransferOwnershipAction);
	}
	
	public void createCities () {
		String tXMLCitiesName;
		String tActiveGameName;
		String tFullFrameTitle;
		CitiesFrame tCitiesFrame;
		
		if (gameIsStarted ()) {
			tActiveGameName = getActiveGameName ();
			tXMLCitiesName = getCitiesFileName ();
			tXMLCitiesName = getXMLBaseDirectory () + tXMLCitiesName;
			tFullFrameTitle = createFrameTitle ("Cities");
			tCitiesFrame = new CitiesFrame (tFullFrameTitle, tActiveGameName);
			setCitiesFrame (tCitiesFrame);
			try {
				tCitiesFrame.loadXML (tXMLCitiesName, tCitiesFrame.getCities ());
			} catch (Exception tException) {
				System.err.println (tException);
			}
		}
	}
	
	public void createCoalCompanies () {
		String tXMLCompaniesName;
		String tFullFrameTitle;
		CoalCompaniesFrame tCoalCompaniesFrame;
		
		if (gameIsStarted ()) {
			tXMLCompaniesName = getCompaniesFileName ();
			tXMLCompaniesName = getXMLBaseDirectory () + tXMLCompaniesName;
			tFullFrameTitle = createFrameTitle ("Coal Companies");

			tCoalCompaniesFrame = new CoalCompaniesFrame (tFullFrameTitle, roundManager);
			setCoalCompaniesFrame (tCoalCompaniesFrame);
			try {
				tCoalCompaniesFrame.loadXML (tXMLCompaniesName, tCoalCompaniesFrame.getCoalCompanies ());
			} catch (Exception tException) {
				System.err.println (tException);
			}
		}
	}

	private void setCoalCompaniesFrame (CoalCompaniesFrame aCoalCompaniesFrame) {
		coalCompaniesFrame = aCoalCompaniesFrame;
		if (aCoalCompaniesFrame != XMLFrame.NO_XML_FRAME) {
			coalCompaniesFrame = aCoalCompaniesFrame;
		}
	}

	public void setAuctionFrame (AuctionFrame aAuctionFrame) {
		auctionFrame = aAuctionFrame;
	}
	
	public String getPrivateAbbrevToAuction () {
		Certificate tCertificate = bank.getPrivateToAuction ();
		
		return tCertificate.getCompanyAbbrev ();
	}
	
	public void addPrivateToAuction () {
		Certificate tCertificate = bank.getPrivateToAuction ();
		
		auctionFrame.addPrivateToAuction (tCertificate);
	}
	
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
	
	public void createAuditFrame () {
		AuditFrame tAuditFrame;
		String tFullTitle;
		
		if (gameIsStarted ()) {
			tFullTitle = createFrameTitle ("Audit");
		
			tAuditFrame = new AuditFrame (tFullTitle, this);
			setAuditFrame (tAuditFrame);
		}
	}
	
	public void createMap () {
		String tXMLMapName;
		String tBaseDir;
		String tColorSchemeName;
		String tFullTitle;
		MapFrame tMapFrame;
		
		if (gameIsStarted ()) {
			tBaseDir = getXMLBaseDirectory ();
			tXMLMapName = getMapFileName ();
			tXMLMapName = tBaseDir + tXMLMapName;
			tFullTitle = createFrameTitle ("Map");
			tMapFrame = new MapFrame (tFullTitle, this);
			setMapFrame (tMapFrame);
			tMapFrame.setTileSet (tileTrayFrame.getTileSet ());
			try {
				tMapFrame.loadXML (tXMLMapName, tMapFrame.getMap ());
			} catch (Exception tException) {
				System.err.println (tException);
			}
			tColorSchemeName = tBaseDir + "Color Scheme.xml";
			try {
				tMapFrame.loadXMLColorScheme (tColorSchemeName, tMapFrame.getTerrain ());
				tMapFrame.loadXMLColorScheme (tColorSchemeName, tileTrayFrame.getTileType ());
			} catch (Exception tException) {
				System.err.println ("Problem Loading Color Scheme: " + tException);
			}
			
			tMapFrame.setCityInfo (citiesFrame.getCities ());
			tMapFrame.setCorporationList (privatesFrame.getPrivates (), CorporationList.TYPE_NAMES [0]);
			tMapFrame.setCorporationList (coalCompaniesFrame.getCoalCompanies (), CorporationList.TYPE_NAMES [1]);
			tMapFrame.setCorporationList (minorCompaniesFrame.getMinorCompanies (), CorporationList.TYPE_NAMES [2]);
			tMapFrame.setCorporationList (shareCompaniesFrame.getShareCompanies (), CorporationList.TYPE_NAMES [3]);
			tMapFrame.setHomeCities (shareCompaniesFrame.getShareCompanies ());
			tMapFrame.setHomeCities (minorCompaniesFrame.getMinorCompanies ());
			tMapFrame.setHomeCities (privatesFrame.getPrivates ());
		}	
	}
	
	public void createMarket () {
		String tXMLMarketName;
		String tFullTitle;
		MarketFrame tMarketFrame;
		
		if (gameIsStarted ()) {
			tXMLMarketName = getMarketFileName ();
			tXMLMarketName = getXMLBaseDirectory () + tXMLMarketName;
			tFullTitle = createFrameTitle ("Market");
			tMarketFrame = new MarketFrame (tFullTitle, this);
			setMarketFrame (tMarketFrame);
			try {
				tMarketFrame.loadXML (tXMLMarketName, tMarketFrame.getMarket ());
			} catch (Exception tException) {
				System.err.println (tException);
			}
		}
	}
	
	public void createMinorCompanies () {
		String tXMLCompaniesName;
		MinorCompaniesFrame tMinorCompaniesFrame;
		
		if (gameIsStarted ()) {
			tXMLCompaniesName = getCompaniesFileName ();
			tXMLCompaniesName = getXMLBaseDirectory () + tXMLCompaniesName;
			tMinorCompaniesFrame = new MinorCompaniesFrame (createFrameTitle ("Minor Companies"), roundManager);
			setMinorCompaniesFrame (tMinorCompaniesFrame);
			try {
				tMinorCompaniesFrame.loadXML (tXMLCompaniesName, tMinorCompaniesFrame.getMinorCompanies ());
			} catch (Exception tException) {
				System.err.println (tException);
			}
		}
	}
	
	public void createPrivateCompanies () {
		String tXMLCompaniesName;
		PrivatesFrame tPrivatesFrame;
		
		if (gameIsStarted ()) {
			tXMLCompaniesName = getCompaniesFileName ();
			tXMLCompaniesName = getXMLBaseDirectory () + tXMLCompaniesName;
			tPrivatesFrame = new PrivatesFrame (createFrameTitle ("Private Companies"), roundManager);
			setPrivatesFrame (tPrivatesFrame);
			try {
				tPrivatesFrame.loadXML (tXMLCompaniesName, tPrivatesFrame.getPrivates ());
			} catch (Exception tException) {
				System.err.println (tException);
			}
		}
	}
	
	public void createShareCompanies () {
		String tXMLCompaniesName;
		Integer [] tParValues;
		Market tMarket;
		ShareCompaniesFrame tShareCompaniesFrame;
		
		if (gameIsStarted ()) {
			tXMLCompaniesName = getCompaniesFileName ();
			tXMLCompaniesName = getXMLBaseDirectory () + tXMLCompaniesName;
			tShareCompaniesFrame = new ShareCompaniesFrame (createFrameTitle ("Share Companies"), roundManager);
			setShareCompaniesFrame (tShareCompaniesFrame);
			try {
				tShareCompaniesFrame.loadXML (tXMLCompaniesName, tShareCompaniesFrame.getShareCompanies ());
				tMarket = marketFrame.getMarket ();
				tShareCompaniesFrame.setMarket (tMarket);
				tShareCompaniesFrame.setStartCells ();
				tShareCompaniesFrame.updateCorpComboBox ();
				tParValues = tMarket.getAllStartCells ();
				tShareCompaniesFrame.updateParValuesComboBox (tParValues);
			} catch (Exception tException) {
				System.err.println (tException);
			}
		}
	}

	public Integer [] getAllStartCells () {
		Market tMarket;
		
		tMarket = marketFrame.getMarket ();
		
		return tMarket.getAllStartCells ();
	}
	
	public void createTileTray () {
		String tXMLTileTrayName, tXMLTileDefinitionName;
		String tActiveGameName, tBaseDirName;
		String tTileSets [] = {
				"Yellow", "Green", "Brown", "Grey", "Other"
			};
		TileTrayFrame tTileTrayFrame;
		TileDefinitionFrame tTileDefinitionFrame;
		
		if (gameIsStarted ()) {
			tActiveGameName = getActiveGameName ();
			tBaseDirName = getXMLBaseDirectory ();
			tXMLTileTrayName = getTileSetFileName ();
			tXMLTileTrayName = tBaseDirName + tXMLTileTrayName;
			tTileTrayFrame = new TileTrayFrame (createFrameTitle ("Tile Tray"), this);
			setTileTrayFrame (tTileTrayFrame);
			try {
				tTileTrayFrame.loadXML (tXMLTileTrayName, tTileTrayFrame.getTileSet ());
			} catch (Exception tException) {
				System.err.println (tException);
			}
			
			tTileDefinitionFrame = new TileDefinitionFrame (createFrameTitle ("Tile Definition"), tTileTrayFrame, tActiveGameName);
			setTileDefinitionFrame (tTileDefinitionFrame);
			for (String tTileSetName : tTileSets) {
				tXMLTileDefinitionName = tTileSetName + " Tile Definitions.xml";
				tXMLTileDefinitionName = tBaseDirName + "Tile XML Data/"+ tXMLTileDefinitionName;
				try {
					tTileDefinitionFrame.loadXML (tXMLTileDefinitionName, tTileDefinitionFrame.getTileSet ());
				} catch (Exception tException) {
					System.err.println (tException);
				}
				tTileTrayFrame.copyTileDefinitions (tileDefinitionFrame.getTileSet ());
			}
		}
	}
	
	public boolean doPartialCapitalization () {
		return phaseManager.doPartialCapitalization ();
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
	
	public boolean isPlaceTileMode() {
		return mapFrame.isPlaceTileMode ();
	}

	public boolean isPlaceTokenMode() {
		return mapFrame.isPlaceTokenMode ();
	}

	public void enterSelectRouteMode (RouteInformation aRouteInformation) {
		mapFrame.enterSelectRouteMode (aRouteInformation);		
	}

	public void exitSelectRouteMode (RouteInformation aRouteInformation) {
		mapFrame.exitSelectRouteMode (aRouteInformation);
	}
	
	public void fullOwnershipAdjustment () {
		marketFrame.fullOwnershipAdjustment (roundManager.getStockRound ());
	}
	
	public boolean gameChanged () {
		return gameChangedSinceSave;
	}
	
	public boolean gameHasPrivates () {
		boolean tGameHasPrivates;
		
		tGameHasPrivates = activeGame.hasPrivates ();
		
		return tGameHasPrivates;
	}

	public boolean gameIsSaved () {
		return (saveFile != null);
	}
	
	public boolean gameIsStarted () {
		return (activeGame != null);
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
		if (aActorName == null) {
			System.err.println ("Actor Name IS NULL<-----");
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
				tActor = coalCompaniesFrame.getActor (aActorName);
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
	
	public Portfolio getBankPoolPortfolio () {
		Portfolio tBankPoolPortfolio;
		
		tBankPoolPortfolio = bankPool.getPortfolio ();
		
		return tBankPoolPortfolio;
	}
	
	public String getCitiesFileName () {
		return getFileName ("cities");
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
			tCertificate = coalCompaniesFrame.getCertificate (aCompanyAbbrev, aPercentage, aPresidentShare);
		}
		if (tCertificate == Certificate.NO_CERTIFICATE) {
			tCertificate = shareCompaniesFrame.getCertificate (aCompanyAbbrev, aPercentage, aPresidentShare);
		}
		
		return tCertificate;
	}
	
	public CorporationList getCoalCompanies () {
		return coalCompaniesFrame.getCoalCompanies ();
	}
	
	public String getCompaniesFileName () {
		return getFileName ("companies");
	}

	public Corporation getCorporationByID (int aCorporationID) {
		Corporation tCorporation;
		
		tCorporation = privatesFrame.getCorporationByID (aCorporationID); 
		if (tCorporation == CorporationList.NO_CORPORATION) {
			tCorporation = minorCompaniesFrame.getCorporationByID (aCorporationID); 
		}
		if (tCorporation == CorporationList.NO_CORPORATION) {
			tCorporation = coalCompaniesFrame.getCorporationByID (aCorporationID); 
		}
		if (tCorporation == CorporationList.NO_CORPORATION) {
			tCorporation = shareCompaniesFrame.getCorporationByID (aCorporationID); 
		}
		
		return tCorporation;
	}
	
	public ActorI.ActionStates getCorporationState (String aCorpStateName) {
		return shareCompaniesFrame.getCorporationState (aCorpStateName);
	}
	
	public int getCountOfOpenPrivates () {
		return privatesFrame.getCountOfOpenPrivates ();
	}
	
	public int getCountOfPlayerOwnedPrivates () {
		return privatesFrame.getCountOfPlayerOwnedPrivates ();
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
		if (tSelectedCorporation instanceof PrivateCompany) {
			tSelectedPrivateCompany = (PrivateCompany) tSelectedCorporation;
		} else {
			tSelectedPrivateCompany = (PrivateCompany) CorporationList.NO_CORPORATION;
		}
		
		return tSelectedPrivateCompany;
	}

	public CorporationList getPrivates () {
		return privatesFrame.getPrivates ();
	}
	
	public CorporationList getShareCompanies () {
		return shareCompaniesFrame.getShareCompanies ();
	}
	
	public ShareCompany getShareCompany (String aCompanyAbbrev) {
		return shareCompaniesFrame.getShareCompany (aCompanyAbbrev);
	}
	
	public int getCountOfCoals () {
		int tCountOfCoals = 0;
		
		if (privatesFrame != null) {
			tCountOfCoals = coalCompaniesFrame.getCountOfCoals ();
		}
		
		return tCountOfCoals;
	}

	public int getCountOfMinors () {
		int tCountOfMinors = 0;
		
		if (minorCompaniesFrame != null) {
			tCountOfMinors = minorCompaniesFrame.getCountOfMinors ();
		}
		
		return tCountOfMinors;
	}

	public int getCountOfPrivates () {
		int tCountOfPrivates = 0;
		
		if (privatesFrame != null) {
			tCountOfPrivates = privatesFrame.getCountOfPrivates ();
		}
		
		return tCountOfPrivates;
	}
	
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;
		
		tCurrentHolder = bank.getCurrentHolder (aLoadedCertificate);
		if (tCurrentHolder == null) {
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
		
		if (activeGame == NO_GAME) {
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
		return getFileName ("map");
	}
	
	public Market getMarket () {
		return marketFrame.getMarket ();
	}
	
	public String getMarketFileName () {
		return getFileName ("market");
	}
	
	public MarketFrame getMarketFrame () {
		return marketFrame;
	}

	public CorporationList getMinorCompanies () {
		return minorCompaniesFrame.getMinorCompanies ();
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
		return getFileName ("tileSet");
	}

	public TileSet getTileSet () {
		return tileTrayFrame.getTileSet ();
	}
	
	public TileTrayFrame getTileTrayFrame () {
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

	public Train getTrain (String aTrainName) {
		return bank.getTrain (aTrainName);
	}
	
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
	
	public void initiateNetworkGame () {
		initiateGame (playerInputFrame.getSelectedGame ());
	}
	
	public void initiateGame (GameInfo aGameInfo) {
		setGame (aGameInfo);
		initiateGame ();
		setNotifyNetwork (true);	
		// For Normal Start, we need to Notify Clients of Actions
		// The Loading of a Save Game does it's own reset once the Load is complete
	}

	public void initiateGame () {
		CorporationList tPrivates;
		CorporationList tCoals;
		CorporationList tMinors;
		CorporationList tShares;
		PhaseManager tPhaseManager;
		PlayerManager tPlayerManager;
		
		if (activeGame != GameManager.NO_GAME) {
			game18XXFrame.initiateGame ();
			if (playerManager == GameManager.NO_PLAYER_MANAGER) {
				tPlayerManager = new PlayerManager (this);
				setPlayerManager (tPlayerManager);
			}
			roundManager = new RoundManager (this, playerManager);
			setupGamePieces ();
			setGameChanged (true);
			
			setupBank ();
			setupPlayers ();
			tPhaseManager = activeGame.getPhaseManager ();
			tPhaseManager.setCurrentPhase (PhaseManager.FIRST_PHASE);
			setPhaseManager (tPhaseManager);
			tPrivates = privatesFrame.getPrivates ();
			tCoals = coalCompaniesFrame.getCoalCompanies ();
			tMinors = minorCompaniesFrame.getMinorCompanies ();
			tShares = shareCompaniesFrame.getShareCompanies ();
			
			autoSaveFileName = constructAutoSaveFileName ("autoSaves");
			autoSaveFile = new File (autoSaveFileName);
			
			roundManager.initiateGame (tPrivates, tCoals, tMinors, tShares);
			if (! activeGame.isATestGame ()) {
				roundManager.showInitialFrames ();
			}

			gameStarted = true;
			createAuditFrame ();
			applyConfigSettings ();
		}
	}

	private String constructAutoSaveFileName (String tDirectoryName) {
		String tAutoSaveFileName = "";
		
		if (isNetworkGame ()) {
			tAutoSaveFileName = tDirectoryName + File.separator  + "network" + 
					File.separator + getGameName () + "." + getGameID () + "." + clientUserName;
		} else {
			tAutoSaveFileName = tDirectoryName + File.separator + getGameName () + "." + clientUserName;
	
		}
		tAutoSaveFileName += ".save" + FileUtils.xml;
		
		return tAutoSaveFileName;
	}
	
	private void loadCorporationsIntoBank (CorporationList aCorporationList) {
		Corporation tCorporation;
		Certificate tCertificate;
		int tCorporationCount, tCertificateCount;
		int tCorporationIndex, tCertificateIndex;
		
		tCorporationCount = aCorporationList.getRowCount ();
		for (tCorporationIndex = 0; tCorporationIndex < tCorporationCount; tCorporationIndex++) {
			tCorporation = aCorporationList.getCorporation (tCorporationIndex);
			tCertificateCount = tCorporation.getCorporationCertificateCount ();
			for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
				tCertificate = tCorporation.getCorporationCertificate (tCertificateIndex);
				bank.addCertificate (tCertificate);
			}
		}
	}

	private File getSelectedFile (File aDirectory, JFileMChooser aChooser, boolean aSaveFile) {
		File tSelectedFile = null;
		File18XXFilter tFileFilter = new File18XXFilter ();
		int tResult;
		boolean tNotChosenYet = true;
		File tDirectory = aDirectory;
		
		aChooser.addChoosableFileFilter (tFileFilter);
		aChooser.setAcceptAllFileFilterUsed (true);
		aChooser.setFileSelectionMode (JFileMChooser.FILES_AND_DIRECTORIES);
		
		while (tNotChosenYet) {
			aChooser.setCurrentDirectory (tDirectory);
			if (aSaveFile) {
				tResult = chooser.showSaveDialog (this);
			} else {
				tResult = chooser.showOpenDialog (this);
			}
			if (tResult == JFileMChooser.APPROVE_OPTION) {
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
		chooser.setFileSelectionMode (JFileMChooser.FILES_AND_DIRECTORIES);
		chooser.setMLocation (tNewPoint);
		saveFile = getSelectedFile (tSaveDirectory, chooser, false);
		
		if (saveFile != null) {
			tNewSaveDirectory = chooser.getCurrentDirectory ();
			if (!tSaveDirectory.equals (tNewSaveDirectory)) {
				configData.setSaveGameDirectory (tNewSaveDirectory.toString ());
			}
			List<ActionStates> auctionStates;
		
			setNotifyNetwork (false);
			if (loadXMLFile (saveFile)) {
				if (isNetworkGame ()) {
					networkJGameClient.setGameIDonServer (gameID, roundManager.getLastActionNumber (), activeGame.getGameName ());
				}
				/* Once a Game has been loaded, can enable both Save and Save As Menu Items */
				game18XXFrame.disableGameStartItems ();
				game18XXFrame.enableSaveMenuItem ();
				game18XXFrame.enableSaveAsMenuItem ();
				game18XXFrame.enableSaveConfigMenuItem ();
				setGameChanged (false);
				playerManager.updateAllRFPlayerLabels ();
				roundManager.updateAllCorporationsBox ();
				roundManager.setCurrentPlayerLabel ();
				if (roundManager.isAuctionRound ()) {
					// Save the Auction States since 'AddPrivateToAuction' will reset the Player Auction States Reset After adding the Private to Auction.
					auctionStates = playerManager.getPlayerAuctionStates ();
					addPrivateToAuction ();
					playerManager.resetPlayerAuctionStates (auctionStates);
				}
				bank.updateBankCashLabel ();
			}
			setNotifyNetwork (true);
		}
	}

	private void loadTrainsIntoBank () {
		int tTrainIndex, tTrainCount, tTrainQty, tTrainIndex2;
		TrainInfo tTrainInfo;
		Train tTrain, tNewTrain;

		tTrainCount = activeGame.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrainInfo = activeGame.getTrainInfo (tTrainIndex);
			tTrainQty = tTrainInfo.getQuantity ();
			tTrain = tTrainInfo.getTrain ();
			if (tTrainInfo.isStartPhase ()) {
				tTrain.setStatus (Train.AVAILABLE_FOR_PURCHASE);
			}
			for (tTrainIndex2 = 0; tTrainIndex2 < tTrainQty; tTrainIndex2++) {
				tNewTrain = new Train (tTrain);
				bank.addTrain (tNewTrain);
			}
		}
	}

	public boolean loadXMLFile (File aSaveGame) {
		boolean tXMLFileWasLoaded;
		
		if (aSaveGame != null) {
			try {
				XMLDocument tXMLDocument = new XMLDocument (aSaveGame);
				tXMLFileWasLoaded = loadXMLSavedGame (tXMLDocument);
			} catch (Exception aException) {
				System.err.println ("Oops, mucked up the XML Save Game File Access [" + aSaveGame.getName () + "].");
				System.err.println ("Exception Message [" + aException.getMessage () + "].");
				aException.printStackTrace ();
				tXMLFileWasLoaded = false;
			}
		} else {
			System.err.println ("No File Object for XML Save Game");
			tXMLFileWasLoaded = false;
		}
		
		return tXMLFileWasLoaded;
	}

	public boolean loadXMLSavedGame (XMLDocument aXMLDocument) throws IOException {
		boolean tLoadedSaveGame;
		XMLNode tXMLSaveGame, tChildNode;
		NodeList tChildren;
		int tChildrenCount, tIndex;
		String tChildName, tSaveGameName;
		GameSet tGameSet;
		boolean tGameIdentified = false, tPlayersLoaded = false, tGameInitiated = false;
		String tServerIP, tGameID;
		int tServerPort;
		
		tLoadedSaveGame = false;
		if (aXMLDocument.ValidDocument()) {
			playerManager = new PlayerManager (this); /* Create a new Player Manager - repeated openings, should not add players to an existing set */
			activeGame = null;
			
			tXMLSaveGame = aXMLDocument.getDocumentElement ();
			tChildren = tXMLSaveGame.getChildNodes ();
			tChildrenCount = tChildren.getLength ();
			for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
				if (tGameIdentified && tPlayersLoaded && ! tGameInitiated) {
					initiateGame ();
					tGameInitiated = true;
				}
				tChildNode = new XMLNode (tChildren.item (tIndex));
				tChildName = tChildNode.getNodeName ();
				if (JGameClient.EN_NETWORK_GAME.equals (tChildName)) {
					// <NetworkGame serverIP="192.168.1.9" serverPort="18300"/>
					tServerIP = tChildNode.getThisAttribute (JGameClient.AN_SERVER_IP);
					tServerPort = tChildNode.getThisIntAttribute (JGameClient.AN_SERVER_PORT);
					networkJGameClient = new JGameClient (GameSet.CHAT_TITLE + " (" + clientUserName + ")", this, tServerIP, tServerPort);
					setNetworkJGameClient (networkJGameClient);
					networkJGameClient.addLocalPlayer (clientUserName, true);
					networkJGameClient.addSPGameActivity ();
				}
				if (GameInfo.EN_GAME_INFO.equals (tChildName)) {
					tGameSet = playerInputFrame.getGameSet ();
					tSaveGameName = tChildNode.getThisAttribute (AN_NAME);
					activeGame = tGameSet.getGameByName (tSaveGameName);
					tGameID = tChildNode.getThisAttribute (GameInfo.AN_GAME_ID);
					setGameID (tGameID);
					activeGame.setGameID (tGameID);
					tGameIdentified = true;
				}
				if (PlayerInputFrame.EN_PLAYERS.equals (tChildName)) {
					tPlayersLoaded = playerManager.loadPlayers (tChildNode, activeGame);
				}
				if (PhaseManager.EN_PHASE.equals (tChildName)) {
					phaseManager.loadPhase (tChildNode);
				}
				if (tGameInitiated) {
					if (Action.EN_ACTIONS.equals (tChildName)) {
						roundManager.loadActions (tChildNode, this);
					}
					if (RoundManager.EN_ROUNDS.equals (tChildName)) {
						roundManager.loadRoundStates (tChildNode);
					}
					if (Player.EN_PLAYER_STATES.equals (tChildName)) {
						playerManager.loadPlayerStates (tChildNode);
					}
					if (Bank.EN_BANK_STATE.equals (tChildName)) {
						bank.loadBankState (tChildNode);
					}
					if (BankPool.EN_BANK_POOL_STATE.equals (tChildName)) {
						bankPool.loadBankPoolState (tChildNode);
					}
					if (Market.EN_MARKET.equals (tChildName)) {
						marketFrame.loadMarketTokens (tChildNode);
					}
					if (PrivatesFrame.EN_PRIVATES.equals (tChildName)) {
						privatesFrame.loadPrivatesStates (tChildNode);
						cleanupLoadedPrivates ();
					}
					if (MinorCompaniesFrame.EN_MINORS.equals (tChildName)) {
						minorCompaniesFrame.loadMinorsStates (tChildNode);
					}
					if (CoalCompaniesFrame.EN_COALS.equals (tChildName)) {
						coalCompaniesFrame.loadCoalsStates (tChildNode);
					}
					if (ShareCompaniesFrame.EN_SHARES.equals (tChildName)) {
						shareCompaniesFrame.loadSharesStates (tChildNode);
					}
					if (HexMap.EN_MAP.equals (tChildName)) {
						mapFrame.loadMapStates (tChildNode);
					}
				}
			}
			fixLoadedRoutes ();
			if ((activeGame != null) && (playerManager.getPlayerCount () > 0)) {
				tLoadedSaveGame = true;
			}
		}
		
		if (roundManager != null) {
			roundManager.updateRoundFrame ();
		}
		
		return tLoadedSaveGame;
	}
	
	private void fixLoadedRoutes () {
		if (minorCompaniesFrame != XMLFrame.NO_XML_FRAME) {
			minorCompaniesFrame.fixLoadedRoutes (mapFrame);
		}
		if (coalCompaniesFrame != XMLFrame.NO_XML_FRAME) {
			coalCompaniesFrame.fixLoadedRoutes (mapFrame);
		}
		if (shareCompaniesFrame != XMLFrame.NO_XML_FRAME) {
			shareCompaniesFrame.fixLoadedRoutes (mapFrame);
		}
	}
	
	private void cleanupLoadedPrivates () {
		// After Loading a Save Game, need to do some cleanup steps:
		// 1. If the Private is in a Closed State, close the Private (to remove from the Startup Packet)
		// 2. If All of the Privates are Owned, then No auctions to do, no escrows or bids need to be saved:
		//    a. Remove all Escrows entries added from all players
		//    b. Remove all Bids from Private Certificates
		privatesFrame.applyCloseToPrivates ();
		if (! privatesFrame.anyPrivatesUnowned ()) {
			privatesFrame.removeAllBids ();
			playerManager.removeAllEscrows ();
		}
	}

	public boolean mapVisible () {
		return mapFrame.isVisible ();
	}

	public void repaintMapFrame () {
		mapFrame.repaint();
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

	public void saveGame () {
		XMLDocument tXMLDocument;
		XMLElement tXMLElement, tSaveGameElement;
		
		tXMLDocument = new XMLDocument ();
		tSaveGameElement = tXMLDocument.createElement(EN_GAME);
		tSaveGameElement.setAttribute (AN_GE_VERSION, game18XXFrame.getGEVersion ());
		
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
		
		/* Save the Privates, Minors, Coals, and Share Company Information */
		appendCompanyFrameXML (tSaveGameElement, tXMLDocument, privatesFrame);
		appendCompanyFrameXML (tSaveGameElement, tXMLDocument, minorCompaniesFrame);
		appendCompanyFrameXML (tSaveGameElement, tXMLDocument, coalCompaniesFrame);
		appendCompanyFrameXML (tSaveGameElement, tXMLDocument, shareCompaniesFrame);

		/* Also need to save the Map Information */
		/* Save The Tile Placements, Orientations, and Token Placements */
		tXMLElement = mapFrame.getMapStateElements (tXMLDocument);
		tSaveGameElement.appendChild (tXMLElement);
		
		// Append Save Game Element to Document just before outputing it.
		tXMLDocument.appendChild (tSaveGameElement);
		
		tXMLDocument.outputXML (saveFile);
	}
	
	public void appendCompanyFrameXML (XMLElement aSaveGameElement, XMLDocument aXMLDocument, 
			CorporationTableFrame aCorporationTableFrame) {
		XMLElement tXMLElement;
		
		if (aCorporationTableFrame != null) {
			tXMLElement = aCorporationTableFrame.getCorporationStateElements (aXMLDocument);
			if (tXMLElement != null) {
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
		Point tNewPoint;
		
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
			File18XXFilter tFileFilter = new File18XXFilter ();
			tNewPoint = getOffsetGEFrame ();
			chooser = new JFileMChooser ();
			chooser.setMLocation (tNewPoint);
			chooser.setDialogTitle ("Save 18XX Game File");
			chooser.setCurrentDirectory (tSaveDirectory);
			chooser.addChoosableFileFilter (tFileFilter);
			chooser.setAcceptAllFileFilterUsed (true);
			chooser.setFileSelectionMode (JFileMChooser.FILES_AND_DIRECTORIES);
			chooser.setSelectedFile (new File("SaveGame." + FileUtils.xml));
			saveFile = getSelectedFile (tSaveDirectory, chooser, true);
			tSaveDirectory = chooser.getCurrentDirectory ();
			tNewSaveGameDir = tSaveDirectory.getAbsolutePath ();
			if (! tOriginalSaveGameDir.equals (tNewSaveGameDir)) {
				configData.setSaveGameDirectory (tNewSaveGameDir);
				saveConfig (true);
			}
			if (saveFile != null) {
				tFileName = saveFile.getName ();
				if (! tFileName.endsWith (FileUtils.xml)) {
					saveFile = new File (saveFile.getAbsoluteFile() + "." + FileUtils.xml);
				}
				/* Once a Game has been saved, can enable the Save Menu Item */
				saveGame ();
				setGameChanged (false);
				game18XXFrame.enableSaveMenuItem ();
			} else {
				System.err.println ("Cancel Save Game Action");
			}
		}
	}
	
	public void setBank (int aBank) {
		bank = new Bank (aBank, this);
	}
	
	public void setBankPool (BankPool aBankPool) {
		bankPool = aBankPool;
	}
	
	public void setCitiesFrame (XMLFrame aXMLFrame) {
		citiesFrame = (CitiesFrame) aXMLFrame;
		if (aXMLFrame != XMLFrame.NO_XML_FRAME) {
			addNewFrame (aXMLFrame);
		}
	}
	
	public void setGame (GameInfo aGame) {
		activeGame = aGame;
		if (activeGame != NO_GAME) {
			activeGame.setGameID (gameID);
		}
	}
	
	public void setGameChanged (boolean aFlag) {
		gameChangedSinceSave = aFlag;
	}
	
	public void setMapFrame (XMLFrame aXMLFrame) {
		mapFrame = (MapFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}
	
	public void setMarketFrame (XMLFrame aXMLFrame) {
		marketFrame = (MarketFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setMinorCompaniesFrame (XMLFrame aXMLFrame) {
		minorCompaniesFrame = (MinorCompaniesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setParPrice (ShareCompany aShareCompany, int aParPrice) {
		 marketFrame.setParPrice (aShareCompany, aParPrice);
		 playerManager.updateAllPlayerFrames ();
	}
	
	public void setPhaseManager (PhaseManager aPhaseManager) {
		phaseManager = aPhaseManager;
	}
	
	public void setPlayerInputFrame (PlayerInputFrame aPlayerInputFrame) {
		playerInputFrame = aPlayerInputFrame;
		addNewFrame (playerInputFrame);
	}
	
	public void setPlayerManager (PlayerManager aPlayerManager) {
		playerManager = aPlayerManager;
	}
	
	public void setPrivatesFrame (XMLFrame aXMLFrame) {
		privatesFrame = (PrivatesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setShareCompaniesFrame (XMLFrame aXMLFrame) {
		shareCompaniesFrame = (ShareCompaniesFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setTileTrayFrame (XMLFrame aXMLFrame) {
		tileTrayFrame = (TileTrayFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setAuditFrame (XMLFrame aXMLFrame) {
		auditFrame = (AuditFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}
	
	public void setTileDefinitionFrame (XMLFrame aXMLFrame) {
		tileDefinitionFrame = (TileDefinitionFrame) aXMLFrame;
		addNewFrame (aXMLFrame);
	}

	public void setupBank () {
		int tBankTotal;
		String tFormat;
		CorporationList tCorpList;
		
		bankPool = new BankPool (this);
		setBankPool (bankPool);
		tBankTotal = activeGame.getBankTotal ();
		tFormat = activeGame.getCurrencyFormat ();
		bank = new Bank (tBankTotal, this);
		bank.setFormat (tFormat);
		loadTrainsIntoBank ();
		tCorpList = privatesFrame.getPrivates ();
		loadCorporationsIntoBank (tCorpList);
		tCorpList = coalCompaniesFrame.getCoalCompanies ();
		loadCorporationsIntoBank (tCorpList);
		tCorpList = minorCompaniesFrame.getMinorCompanies ();
		loadCorporationsIntoBank (tCorpList);
		tCorpList = shareCompaniesFrame.getShareCompanies ();
		loadCorporationsIntoBank (tCorpList);
		bank.createStartPacket (this);
		setupOptions ();
	}
		
	public void setupGamePieces () {
		if (activeGame != GameManager.NO_GAME) {
			createMarket ();
			createShareCompanies ();
			createPrivateCompanies ();
			createCoalCompanies ();
			createMinorCompanies ();
			createCities ();
			createTileTray ();
			createMap ();
			
			tileTrayFrame.setTraySize ();
		}
	}
	
	public void setupLoadedPlayers () {
		int tPlayerCount;
		Player tPlayer;
		int tIndex;
		int tPlayerStartingCash;
		
		if (playerManager == GameManager.NO_PLAYER_MANAGER) {
			System.err.println ("No Player Manager loaded from Save Game");
		} else {
			tPlayerCount = playerManager.getPlayerCount ();
			tPlayerStartingCash = activeGame.getStartingCash (tPlayerCount);
			for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
				tPlayer = playerManager.getPlayer (tIndex);
				bank.transferCashTo (tPlayer, tPlayerStartingCash);
			}
		}
	}
	
	public void setupOptions () {
		activeGame.setupOptions (this);
	}
	
	public void setupPlayers () {
		Player tPlayer;
		PlayerManager tPlayerManager;
		String tPlayerName;
		int tIndex;
		int tPlayerStartingCash;
		int tPlayerCount;
		int tCertificateLimit;
		boolean tGameHasCoals = false;
		boolean tGameHasMinors = false;
		boolean tGameHasPrivates = false;
		boolean tGameHasShares = false;
		
		if (privatesFrame.getCountOfPrivates () > 0) {
			tGameHasPrivates = true;
		}
		if (coalCompaniesFrame.getCountOfCoals () > 0) {
			tGameHasCoals = true;
		}
		if (minorCompaniesFrame.getCountOfMinors () > 0) {
			tGameHasMinors = true;
		}
		if (shareCompaniesFrame.getCountOfShares () > 0) {
			tGameHasShares = true;
		}
		if (playerManager == GameManager.NO_PLAYER_MANAGER) {
			tPlayerManager = new PlayerManager (this);
			setPlayerManager (tPlayerManager);
		}
		tPlayerCount = playerInputFrame.getPlayerCount ();
		tPlayerStartingCash = activeGame.getStartingCash (tPlayerCount);
		tCertificateLimit = activeGame.getCertificateLimit (tPlayerCount);
		for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
			tPlayerName = playerInputFrame.getPlayerName (tIndex);
			tPlayer = new Player (tPlayerName, tGameHasPrivates, tGameHasCoals, tGameHasMinors, tGameHasShares, playerManager, tCertificateLimit);
			bank.transferCashTo (tPlayer, tPlayerStartingCash);
			playerManager.addPlayer (tPlayer);
		}
		
		if (roundManager != null) {	
			roundManager.updateAllCorporationsBox ();
		}
	}

	public void showCities () {
		showFrame (citiesFrame);
	}
	
	private void showFrame (JFrame aJFrame) {
		aJFrame.revalidate ();
		aJFrame.toFront ();
		aJFrame.setVisible (true);
	}
	
	public void setAuctionFrameLocation () {
		Point tNewPoint = getOffsetPlayerFrame ();
		
		auctionFrame.setLocation (tNewPoint);
	}
	
	public void showAuctionFrame () {
		auctionFrame.pack ();
		auctionFrame.repaint ();
		auctionFrame.setVisible (true);
		auctionFrame.toFront ();
	}

	public Point getOffsetGEFrame () {
		return game18XXFrame.getOffsetGEFrame ();
	}

	public Point getOffsetRoundFrame () {
		Point tNewPoint;
		
		tNewPoint = roundManager.getOffsetRoundFrame();
		
		return tNewPoint;
	}

	public Point getOffsetCorporationFrame () {
		CorporationFrame tCorporationFrame;
		Corporation tOperatingCorporation;
		Point tRoundFramePoint, tNewPoint;
		double tX, tY;
		int tNewX, tNewY;
		
		tOperatingCorporation = roundManager.getOperatingCompany ();
		tCorporationFrame = tOperatingCorporation.getCorporationFrame ();
		tRoundFramePoint = tCorporationFrame.getLocation ();
		tX = tRoundFramePoint.getX ();
		tY = tRoundFramePoint.getY ();
		tNewX = (int) tX + 100;
		tNewY = (int) tY + 100;
		tNewPoint = new Point (tNewX, tNewY);
		
		return tNewPoint;
	}
	
	public Point getOffsetPlayerFrame () {
		PlayerFrame tPlayerFrame;
		Player tPlayer;
		Point tPlayerFramePoint, tNewPoint;
		double tX, tY;
		int tNewX, tNewY;
		String tPlayerName;
		
		if (isNetworkGame ()) {
			tPlayerName = clientUserName;
		} else {
			tPlayerName = playerManager.getCurrentPlayer ().getName ();
		}
		tPlayer = playerManager.getPlayer (tPlayerName);
		tPlayerFrame = tPlayer.getPlayerFrame ();
		tPlayerFramePoint = tPlayerFrame.getLocation ();
		tX = tPlayerFramePoint.getX ();
		tY = tPlayerFramePoint.getY ();
		tNewX = (int) tX + 100;
		tNewY = (int) tY + 100;
		tNewPoint = new Point (tNewX, tNewY);
		
		return tNewPoint;
	}
	
	public void showMap () {
		showFrame (mapFrame);
	}
	
	public void showMarket () {
		marketFrame.pack ();
		showFrame (marketFrame);
	}
	
	public void showMinorCompanies () {
		minorCompaniesFrame.pack ();
		showFrame (minorCompaniesFrame);
	}
	
	public void showPrivateCompanies () {
		privatesFrame.pack ();
		showFrame (privatesFrame);
	}
	
	public void showShareCompanies () {
		shareCompaniesFrame.pack ();
		showFrame (shareCompaniesFrame);
	}
	
	public void showTileTray () {
		tileTrayFrame.pack ();
		showFrame (tileTrayFrame);
	}
	
	public void sendToReportFrame (String aReport) {
		roundManager.sendToReportFrame (aReport);
	}
	
	public void updateAllFrames () {
		Player tCurrentPlayer;
		StockRound tStockRound;
		
		roundManager.updateRoundFrame ();
		if (roundManager.getCurrentRoundType ().equals (ActorI.ActionStates.StockRound)) {
			tStockRound = roundManager.getStockRound ();
			tCurrentPlayer = tStockRound.getCurrentPlayer ();
			playerManager.updateAllPlayerFrames (tCurrentPlayer);
		}
	}

	// Get an Array of all Available Trains from Bank, and BankPool together
	public Train [] getBankAvailableTrains () {
		Train [] tBankAvailableTrains;
		Train [] tBankPoolAvailableTrains;
		Train [] tAvailableTrains;
		int tIndex = 0, tBankCount = 0, tBankPoolCount = 0;
		
		if (bank != null) {
			tBankAvailableTrains = bank.getAvailableTrains ();
			if (tBankAvailableTrains != null) {
				tBankCount = tBankAvailableTrains.length;
			}
		} else {
			tBankAvailableTrains = null;
		}
		if (bankPool != null)  {
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

	public boolean isOperatingRound () {
		return roundManager.isOperatingRound ();
	}

	public Corporation getOperatingCompany () {
		Corporation tShareCompany;
		
		tShareCompany = roundManager.getOperatingCompany ();
		
		return tShareCompany;
	}

	public boolean isUpgradeAllowed (String tTileColor) {
		return phaseManager.isUpgradeAllowed (tTileColor);
	}

	public int getBankPoolShareLimit () {
		return activeGame.getBankPoolShareLimit ();
	}

	public boolean isNetworkGame () {
		return (networkJGameClient != null);
	}
	
	public void setNetworkJGameClient (JGameClient aNetworkJGameClient) {
		networkJGameClient = aNetworkJGameClient;
		networkJGameClient.setFrameToConfigDetails (this);
	}
	
	public JGameClient getNetworkJGameClient () {
		return networkJGameClient;
	}

	public String getPlayersInOrder () {
		return playerInputFrame.getPlayersInOrder ();
	}
	
	public void randomizePlayerOrder () {
		playerInputFrame.randomizePlayerOrder ();
	}
	
	public void handleGameActivity (String aGameActivity) {
		XMLDocument tXMLGameActivity;

		XMLNode tXMLGameActivityNode;
		XMLNode tActionNode;
		NodeList tActionChildren;
		int tActionNodeCount, tActionIndex;
		String tANodeName;
		int tGameIndex;
		String tGameOptions, tBroadcast, tPlayerOrder;
		String tGameID;

		tXMLGameActivity = new XMLDocument ();
		tXMLGameActivity = tXMLGameActivity.ParseXMLString (aGameActivity);
		tXMLGameActivityNode = tXMLGameActivity.getDocumentElement ();
		tANodeName = tXMLGameActivityNode.getNodeName ();
		if (JGameClient.EN_GAME_ACTIVITY.equals (tANodeName)) {
			tActionChildren = tXMLGameActivityNode.getChildNodes ();
			tActionNodeCount = tActionChildren.getLength ();
			try {
				for (tActionIndex = 0; tActionIndex < tActionNodeCount; tActionIndex++) {
					tActionNode = new XMLNode (tActionChildren.item (tActionIndex));
					tANodeName = tActionNode.getNodeName ();
					if (JGameClient.EN_GAME_SELECTION.equals (tANodeName)) {
						tGameIndex = tActionNode.getThisIntAttribute (JGameClient.AN_GAME_INDEX);
						tGameOptions = tActionNode.getThisAttribute (JGameClient.AN_GAME_OPTIONS);
						tBroadcast = tActionNode.getThisAttribute (JGameClient.AN_BROADCAST_MESSAGE);
						tGameID = tActionNode.getThisAttribute (JGameClient.AN_GAME_ID);
						setGameID (tGameID);
						playerInputFrame.handleGameSelection (tGameIndex, tGameOptions, tBroadcast);
						networkJGameClient.updateReadyButton ("READY", true, "Hit when ready to play");
					} else if (JGameClient.EN_PLAYER_ORDER.equals (tANodeName)) {
						tPlayerOrder = tActionNode.getThisAttribute (JGameClient.AN_PLAYER_ORDER);
						tBroadcast = tActionNode.getThisAttribute (JGameClient.AN_BROADCAST_MESSAGE);
						playerInputFrame.handleResetPlayerOrder (tPlayerOrder, tBroadcast);
					} else if (Action.EN_ACTION.equals (tANodeName)){
						if (roundManager != null) {
							applyingNetworkAction = true;
							roundManager.handleNetworkAction (tActionNode);
							applyingNetworkAction = false;
						} else {
//							System.err.println ("Trying to handle a Server Game Activity, Node Named [" + tANodeName + "] no Round Manager created");
						}
					} else if ("#text".equals(tANodeName)){
						// If a #text Node, ignore -- it is empty
					} else {
						System.err.println ("Node Name is [" + tANodeName + "] which is Unrecognized");
					}
				}
			} catch (Exception tException) {
				System.err.println (tException.getMessage ());
				tException.printStackTrace ();
			}
		}
		
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
	
	public String getClientUserName () {
		return clientUserName;
	}
	
	public String getGameID () {
		return gameID;
	}
	
	public boolean isNetworkAndIsThisClient (String aClientName) {
		boolean tIsNetworkAndClient = true;
		
		if (isNetworkGame ()) {
			if (! clientUserName.equals (aClientName)) {
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
				System.err.println ("Oops, mucked up the Config File Access [" + tConfigFileName + "].");
				System.err.println ("Exception Message [" + tException.getMessage () + "].");
				tException.printStackTrace (System.err);
			}
			if (tXMLDocument != null) {
				XMLNode tXMLNode = tXMLDocument.getDocumentElement ();
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
	
	public void addNewFrame (XMLFrame aXMLFrame) {
		String tXMLFrameName;
		
		if (aXMLFrame != XMLFrame.NO_XML_FRAME) {
			tXMLFrameName = aXMLFrame.getTitle ();
			if (tXMLFrameName != null) {
				if (! frameIsPresent (tXMLFrameName)) {
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
		int tGameCount, tGameIndex;
		String tGameName;
		String tActiveGameName;
		
		tXMLDocument = new XMLDocument ();
		tConfigElement = tXMLDocument.createElement (EN_CONFIG);
		
		tSaveGameDirElement = tXMLDocument.createElement(EN_SAVEGAMEDIR);
		tSaveGameDirElement.setAttribute (AN_NAME, configData.getSaveGameDirectory ());
		tConfigElement.appendChild (tSaveGameDirElement);
		
		tFramesElement = tXMLDocument.createElement (EN_FRAMES);
		
		if (activeGame == null) {
			tActiveGameName = "NONE";
		} else {
			tActiveGameName = activeGame.getName ();
		}
		tFramesElement.setAttribute (AN_GAME_NAME, tActiveGameName);

		for (XMLFrame tXMLFrame : configFrames) {
			if (tXMLFrame != XMLFrame.NO_XML_FRAME) {
				// If the Height and Width are > 0, save it... otherwise it makes no sense since the 
				// Frame is not showing anything
				if ((tXMLFrame.getHeight () > 0) && (tXMLFrame.getWidth () > 0)) {
					tFrameElement = tXMLFrame.getXMLFrameElement (tXMLDocument);
					tFramesElement.appendChild (tFrameElement);
				}
			}
		}
		tConfigElement.appendChild (tFramesElement);
		tGameCount = configData.getGameFramesCount ();
		if (tGameCount > 0) {
			for (tGameIndex = 0; tGameIndex < tGameCount; tGameIndex++) {
				tGameName = configData.getGameFrameConfigFor (tGameIndex).getGameName ();
				if (!(tGameName.equals (getGameName ()))) {
					tFramesElement = configData.getXMLFramesElement (tXMLDocument, tGameIndex);
					tConfigElement.appendChild (tFramesElement);
				}
			}
		}

		tXMLDocument.appendChild (tConfigElement);
		
		return tXMLDocument;
	}

	@Override
	public int getSelectedGameIndex () {
		return playerInputFrame.getSelectedGameIndex ();
	}
	
	public void resetGameID (String aGameID) {
		if (gameID.equals ("")) {
			setGameID (aGameID);
		}
	}
	
	private void setGameID (String aGameID) {
		gameID = aGameID;
	}
	
	public void setSelectedGameIndex (int aGameIndex) {
		playerInputFrame.setSelectedGameIndex (aGameIndex);
	}
	
	public void appendToGameActivity (String aGameActivity) {
		networkJGameClient.appendToGameActivity (aGameActivity);
	}

	// TODO: Determine if this is needed still
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

	public void showRoundFrame () {
		roundManager.showRoundFrame ();
	}
	
	public void showActionReportFrame () {
		roundManager.showActionReportFrame ();
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
			tCompanies = shareCompaniesFrame.getShareCompanies ();
			tCorporation = tCompanies.getCorporation (0);
			tActorName = tCorporation.getAbbrev ();
		}
		auditFrame.setActorName (tActorName);
		fillAuditFrame (tActorName);
		auditFrame.setVisible (true);
	}
	
	public void fillAuditFrame (String aActorName) {
		roundManager.fillAuditFrame (auditFrame, aActorName);
	}
	
	public void showChatClient () {
		if (networkJGameClient != null) {
			networkJGameClient.setVisible (true);
		}
	}

	public int getCurrentPhase () {
		return phaseManager.getCurrentPhase ();
	}

	public int getTotalCash() {
		int tTotalCash = 0;
		int tBankCash, tAllPlayerCash, tAllCorpCash, tAllEscrows;
		int tAllCoalCash, tAllMinorCash, tAllShareCash;
		
		tBankCash = bank.getCash ();
		tAllEscrows = privatesFrame.getTotalEscrow ();
		tAllPlayerCash = playerManager.getTotalPlayerCash ();
		tAllCoalCash = coalCompaniesFrame.getTotalCorpCash ();
		tAllMinorCash = minorCompaniesFrame.getTotalCorpCash ();
		tAllShareCash = shareCompaniesFrame.getTotalCorpCash ();
		tAllCorpCash = tAllCoalCash + tAllMinorCash + tAllShareCash;
		
		tTotalCash = tBankCash + tAllPlayerCash + tAllEscrows + tAllCorpCash;
		
		return tTotalCash;
	}
	
	public Player getClientPlayer () {
		Player tPlayer = Player.NO_PLAYER;
		
		tPlayer = playerManager.getPlayer (clientUserName);
		return tPlayer;
	}

	public void hideAuctionFrame () {
		auctionFrame.hideAuctionFrame ();
		
	}

	public boolean isClientCurrentPlayer () {
		boolean tIsClientCurrentPlayer = false;
		Player tCurrentPlayer, tClientPlayer;
		
		tClientPlayer = getClientPlayer ();
		tCurrentPlayer = playerManager.getCurrentPlayer ();
		if (tClientPlayer.equals (tCurrentPlayer)) {
			tIsClientCurrentPlayer = true;
		}
		
		return tIsClientCurrentPlayer;
	}
	
	public PlayerFrame getCurrentPlayerFrame () {
		Player tCurrentPlayer;
		PlayerFrame tCurrentPlayerFrame;
		
		tCurrentPlayer = playerManager.getCurrentPlayer ();
		tCurrentPlayerFrame = tCurrentPlayer.getPlayerFrame ();
		
		return tCurrentPlayerFrame;
	}
	

	public boolean isAuctionRound () {
		return roundManager.isAuctionRound ();
	}

	public void showGEFrame() {
		game18XXFrame.setVisible (true);
		game18XXFrame.toFront ();
	}

	public String requestGameSupport (String aRequestGameSupport) {
		return networkJGameClient.requestGameSupport (gameID, aRequestGameSupport);
	}

	public boolean isLastActionComplete () {
		return roundManager.isLastActionComplete ();
	}
}
