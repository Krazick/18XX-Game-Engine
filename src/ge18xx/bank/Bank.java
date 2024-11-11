package ge18xx.bank;

import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.Coupon;
import ge18xx.company.LoadedCertificate;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.player.StartPacketPortfolio;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.train.Train;
import ge18xx.train.TrainInfo;
import ge18xx.train.TrainPortfolio;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.GUI;
import geUtilities.ParsingRoutine3I;
import geUtilities.ParsingRoutineI;

public class Bank extends GameBank implements CashHolderI {
	public static final AttributeName AN_BANK_CASH = new AttributeName ("cash");
	public static final ElementName EN_BANK_STATE = new ElementName ("Bank");
	public static final String BANK_LABEL_PREFIX = "Remaining Bank Cash ";
	public static final String NAME = "Bank";
	public static final String CLOSED = "Bank Closed";
	public static final String IPO = "Bank IPO";
	public static final Bank NO_BANK = null;
	public static final String NO_FORMAT = null;
	public static final int NO_BANK_CASH = 0;
	static DecimalFormat decimalFormat;
	static String format;
	private static Logger logger;
	boolean bankIsBroken;
	int treasury;
	JLabel bankCashLabel;
	Portfolio closedPortfolio;
	TrainPortfolio rustedTrainsPortfolio;
	StartPacketFrame startPacketFrame;

	public static String formatCash (int aCashAmount) {
		String tFormatted;

		if (format.equals (GUI.EMPTY_STRING)) {
			tFormatted = String.valueOf (aCashAmount);
		} else {
			tFormatted = decimalFormat.format (aCashAmount);
		}

		return tFormatted;
	}
	
	public static String formatCash (String aCashAmount) {
		String tFormatted;
		int tCashAmount;
		
		tCashAmount = Integer.parseInt (aCashAmount);
		tFormatted = formatCash (tCashAmount);
				
		return tFormatted;
	}
	
	private static void setStaticFormat (String aFormat) {
		format = aFormat;
		decimalFormat = new DecimalFormat (aFormat);
	}

	void setFormat (String aFormat) {
		setStaticFormat (aFormat);
	}

	ParsingRoutineI bankParsingRoutine = new ParsingRoutine3I () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			loadTrainPortfolio (aChildNode);
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aChildNode) {
			loadRustedTrainPortfolio (aChildNode);
		}
		
		@Override
		public void foundItemMatchKey3 (XMLNode aChildNode) {
			loadPortfolio (aChildNode);
		}
	};
	
	public Bank (int aTreasury, GameManager aGameManager) {
		super (NAME, aGameManager);

		Portfolio tClosedPortfolio;
		TrainPortfolio tRustedTrainsPortfolio;

		trainPortfolio.setPortfolioHolder (this);
		treasury = aTreasury;
		setStartPacketFrame (StartPacketFrame.NO_START_PACKET);
		setFormat (GUI.EMPTY_STRING);
		setBankCashLabel (null);
		tClosedPortfolio = new Portfolio (this);
		setClosedPortfolio (tClosedPortfolio);
		tRustedTrainsPortfolio = new TrainPortfolio (this);
		setRustedTrainsPortfolio (tRustedTrainsPortfolio);
		logger = LogManager.getLogger (Bank.class);
		setBankIsBroken (false);
	}

	@Override
	public void addCash (int aAmount) {
		treasury += aAmount;
		updateBankCashLabel ();
		if (aAmount < 0) {
			if (treasury < 0) {
				setBankIsBroken (true);
				gameManager.updateRoundFrame ();
			}
		}
		updateListeners (BANK_CASH_CHANGED + " by " + aAmount);
	}

	@Override
	public void transferCashTo (CashHolderI aToHolder, int aAmount) {
		aToHolder.addCash (aAmount);
		addCash (-aAmount);
		updateBankCashLabel ();
	}

	public void addClosedCertificate (Certificate aCertificate) {
		closedPortfolio.addCertificate (aCertificate);
	}

	public void addRustedTrain (Train aTrain) {
		rustedTrainsPortfolio.addTrain (aTrain);
	}

	public JPanel buildStartPacketInfoJPanel (ItemListener aItemListener, Player aPlayer) {
		JPanel tPortfolioJPanel;
		JPanel tStartPacketJPanel;
		BoxLayout tLayout;

		tStartPacketJPanel = new JPanel ();
		tStartPacketJPanel.setBorder (BorderFactory.createTitledBorder (startPacketFrame.getName ()));
		tLayout = new BoxLayout (tStartPacketJPanel, BoxLayout.Y_AXIS);
		tStartPacketJPanel.setLayout (tLayout);

		tPortfolioJPanel = startPacketFrame.buildStartPacketInfoJPanel (aItemListener, aPlayer, gameManager);
		tStartPacketJPanel.add (Box.createVerticalGlue ());
		tStartPacketJPanel.add (tPortfolioJPanel);
		tStartPacketJPanel.add (Box.createVerticalGlue ());

		return tStartPacketJPanel;
	}

	public void updateStartPacket () {
		startPacketFrame.updateStartPacket ();
	}
	
	public boolean canStartOperatingRound () {
		return startPacketFrame.noMustSellLeft ();
	}

	@Override
	public void clearSelections () {
		startPacketFrame.clearSelections ();
	}

	public void closeAllPrivates (BuyTrainAction aBuyTrainAction) {
		CorporationList tPrivateCorporations;

		tPrivateCorporations = gameManager.getPrivates ();

		tPrivateCorporations.closeAll (aBuyTrainAction);
	}

	public void createStartPacket (GameManager aGameManager) {
		String tXMLCompaniesName;
		String tActiveGameName;
		XMLDocument tXMLDocument;
		String tType;
		
		if (aGameManager.gameIsStarted ()) {
			tActiveGameName = aGameManager.getActiveGameName ();
			tXMLCompaniesName = aGameManager.getCompaniesFileName ();
			tXMLCompaniesName = aGameManager.getXMLBaseDirectory () + tXMLCompaniesName;
			startPacketFrame = new StartPacketFrame (tActiveGameName + StartPacketFrame.SPFRAME_SUFFIX, aGameManager);
			tType = Corporation.COMPANIES;
			tXMLDocument = aGameManager.readXMLfromURL (tActiveGameName, tType);
			try {
				startPacketFrame.loadXML (tXMLDocument, startPacketFrame);
			} catch (Exception tException) {
				logger.error ("Creating Start Packet Failure", tException);
			}
		}
		startPacketFrame.loadStartPacketWithCertificates (portfolio);
	}

	public void discardExcessTrains (BuyTrainAction aBuyTrainAction) {
		BankPool tBankPool;
		CorporationList tShareCorporationList;
		CorporationList tMinorCorporationList;

		tBankPool = gameManager.getBankPool ();

		tShareCorporationList = gameManager.getShareCompanies ();
		tShareCorporationList.discardExcessTrains (tBankPool, aBuyTrainAction);

		tMinorCorporationList = gameManager.getMinorCompanies ();
		tMinorCorporationList.discardExcessTrains (tBankPool, aBuyTrainAction);
	}

	public JLabel getBankCashLabel () {
		return bankCashLabel;
	}

	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (aEN_Type);
		tXMLElement.setAttribute (AN_BANK_CASH, treasury);
		getStateElements (aXMLDocument, tXMLElement);

		return tXMLElement;
	}

	@Override
	public int getCash () {
		return treasury;
	}

	@Override
	public CashHolderI getCashHolder () {
		return this;
	}

	@Override
	public Certificate getCertificateToBidOn () {
		Certificate tCertificateToBidOn;

		tCertificateToBidOn = portfolio.getCertificateToBidOn ();
		if (tCertificateToBidOn == Certificate.NO_CERTIFICATE) {
			tCertificateToBidOn = startPacketFrame.getCertificateToBidOn ();
		}

		return tCertificateToBidOn;
	}

	@Override
	public Certificate getCertificateToBuy () {
		Certificate tCertificateToBuy;

		tCertificateToBuy = super.getCertificateToBuy ();
		if (tCertificateToBuy == Certificate.NO_CERTIFICATE) {
			tCertificateToBuy = startPacketFrame.getCertificateToBuy ();
		}

		return tCertificateToBuy;
	}

	public Portfolio getClosedPortfolio () {
		return closedPortfolio;
	}

	/* Methods dealing with fetching from the startPacketFrame */
	
	@Override
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;

		tCurrentHolder = startPacketFrame.getCurrentHolder (aLoadedCertificate);
		if (tCurrentHolder == PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
			tCurrentHolder = super.getCurrentHolder (aLoadedCertificate);
		}

		return tCurrentHolder;
	}

	public void applyDiscount () {
		startPacketFrame.applyDiscount ();
	}

	public boolean availableShareHasBids () {
		return startPacketFrame.availableShareHasBids ();
	}

	public boolean isInStartPacket (Certificate aCertificate) {
		return startPacketFrame.hasThisCertificate (aCertificate);
	}

	public boolean isStartPacketPortfolioEmpty () {
		return startPacketFrame.isStartPacketPortfolioEmpty ();
	}

	public void setStartPacketFrame (StartPacketFrame aStartPacket) {
		startPacketFrame = aStartPacket;
	}

	public StartPacketFrame getStartPacketFrame () {
		return startPacketFrame;
	}

	public boolean hasMustSell () {
		boolean tHasMustSell;
		
		tHasMustSell = startPacketFrame.hasMustSell ();
		
		return tHasMustSell;
	}

	public Certificate getFreeCertificateWithThisCertificate (Certificate aThisCertificate) {
		return startPacketFrame.getFreeCertificateWithThisCertificate (aThisCertificate);
	}

	public Certificate getMustSellCertificate () {
		return startPacketFrame.getMustSellCertificate ();
	}
	
	public Certificate getMustBuyCertificate () {
		return startPacketFrame.getMustBuyCertificate ();
	}

	public Certificate getPrivateForAuction () {
		return startPacketFrame.getPrivateForAuction ();
	}

	public boolean hasMustBuyCertificate () {
		boolean tMustBuy;

		tMustBuy = false;
		if (startPacketFrame != StartPacketFrame.NO_START_PACKET) {
			tMustBuy = startPacketFrame.hasMustBuyCertificate ();
		}

		return tMustBuy;
	}

	public StartPacketPortfolio getStartPacketPortfolio () {
		StartPacketPortfolio tStartPacketPortfolio;

		if (startPacketFrame == StartPacketFrame.NO_START_PACKET) {
			tStartPacketPortfolio = StartPacketPortfolio.NO_START_PACKET;
		} else {
			tStartPacketPortfolio = startPacketFrame.getStartPacketPortfolio ();
		}

		return tStartPacketPortfolio;
	}

	public Certificate getMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		Certificate tCertificate;

		tCertificate = Certificate.NO_CERTIFICATE;
		if (startPacketFrame != XMLFrame.NO_XML_FRAME) {
			tCertificate = startPacketFrame.getMatchingCertificate (aAbbrev, aPercentage, aIsPresident);
		}
		if (tCertificate == Certificate.NO_CERTIFICATE) {
			tCertificate = portfolio.getCertificate (aAbbrev, aPercentage, aIsPresident);
		}

		return tCertificate;
	}

	public Coupon getNextAvailableTrain () {
		Coupon tTrain;

		tTrain = trainPortfolio.getNextAvailableTrain ();

		return tTrain;
	}

	/**
	 * Find a Train in the Rusted Pile
	 * 
	 * @param aName The Name of the train to find
	 * @return The Train found in the Rusted Pile that matches the Name provided
	 * 
	 */
	public Train getRustedTrain (String aName) {
		Train tTrain;

		tTrain = rustedTrainsPortfolio.getTrain (aName);

		return tTrain;
	}

	// Rusted Train Portfolio Methods
	public TrainPortfolio getRustedTrainPortfolio () {
		return rustedTrainsPortfolio;
	}

	// Call various Start Packet Frame methods

	public XMLElement getRustedTrainPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = rustedTrainsPortfolio.getRustedElements (aXMLDocument);

		return tXMLElement;
	}
	
	public XMLElement getClosedPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = closedPortfolio.getElements (aXMLDocument);

		return tXMLElement;
	}

	public void getStateElements (XMLDocument aXMLDocument, XMLElement aXMLElement) {
		XMLElement tTrainPortfolioElements;
		XMLElement tRustedTrainPortfolioElements;
		XMLElement tClosedPortfolioElements;

		tTrainPortfolioElements = getTrainPortfolioElements (aXMLDocument);
		aXMLElement.appendChild (tTrainPortfolioElements);
		tRustedTrainPortfolioElements = getRustedTrainPortfolioElements (aXMLDocument);
		aXMLElement.appendChild (tRustedTrainPortfolioElements);
		tClosedPortfolioElements = getClosedPortfolioElements (aXMLDocument);
		aXMLElement.appendChild (tClosedPortfolioElements);
	}

	public boolean isBroken () {
		return bankIsBroken;
	}

	public void loadBankState (XMLNode aBankNode) {
		XMLNodeList tXMLNodeList;

		treasury = aBankNode.getThisIntAttribute (AN_BANK_CASH);

		tXMLNodeList = new XMLNodeList (bankParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aBankNode, TrainPortfolio.EN_TRAIN_PORTFOLIO,
				TrainPortfolio.EN_RUSTED_TRAIN_PORTFOLIO, Portfolio.EN_PORTFOLIO);
	}

	public void loadCorporations (CorporationList aCorporationList) {
		Corporation tCorporation;
		Certificate tCertificate;
		int tCorporationCount;
		int tCertificateCount;
		int tCorporationIndex;
		int tCertificateIndex;

		tCorporationCount = aCorporationList.getRowCount ();
		for (tCorporationIndex = 0; tCorporationIndex < tCorporationCount; tCorporationIndex++) {
			tCorporation = aCorporationList.getCorporation (tCorporationIndex);
			tCertificateCount = tCorporation.getCorporationCertificateCount ();
			for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
				tCertificate = tCorporation.getCorporationCertificate (tCertificateIndex);
				if (tCertificate.onlyOwnedBy (Corporation.MINOR_COMPANY)) {
					tCorporation.addCertificate (tCertificate);
				} else {
					addCertificate (tCertificate);
				}
			}
		}
	}

	public void loadRustedTrainPortfolio (XMLNode aTrainPortfolioNode) {
		rustedTrainsPortfolio.loadTrainPortfolio (aTrainPortfolioNode, this);
	}

	@Override
	public void loadTrainPortfolio (XMLNode aTrainPortfolioNode) {
		trainPortfolio.loadTrainStatus (aTrainPortfolioNode);
	}

	@Override
	public void loadPortfolio (XMLNode aPortfolioNode) {
		closedPortfolio.loadPortfolio (aPortfolioNode); 
	}

	public void loadTrains (GameInfo aActiveGame) {
		int tTrainIndex;
		int tTrainCount;
		int tTrainQty;
		TrainInfo tTrainInfo;

		tTrainCount = aActiveGame.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrainInfo = aActiveGame.getTrainInfo (tTrainIndex);
			tTrainQty = tTrainInfo.getQuantity ();
			loadTrains (tTrainQty, tTrainInfo);
		}
	}

	public void loadTrains (int aQuantity, TrainInfo aTrainInfo) {
		int tTrainIndex;
		int tID;
		Train tTrain;
		Train tNewTrain;

		tTrain = aTrainInfo.getTrain ();
		if (aTrainInfo.isStartPhase ()) {
			tTrain.setStatus (Train.AVAILABLE_FOR_PURCHASE);
		}
		for (tTrainIndex = 0; tTrainIndex < aQuantity; tTrainIndex++) {
			tNewTrain = new Train (tTrain);
			tID = generateTrainID (tTrain.getOrder (), tTrainIndex);
			tNewTrain.setTrainID (tID);
			addTrain (tNewTrain);
		}
	}

	public int generateTrainID (int aMajorID, int aMinorID) {
		int tTrainID;
		
		tTrainID = (aMajorID + 1) * 100 + aMinorID;
		
		return tTrainID;
	}
	
	/* When a Train is bought, check for More Trains to make available */
	public void makeTrainsAvailable (Train aTrain, BuyTrainAction aBuyTrainAction) {
		int tTrainOrder;
		int tTrainOrderCount;
		int tNextTrainOrder;
		int tOldNextTrainStatus;
		int tNewNextTrainStatus;
		String tTrainName;
		Coupon tTrain;

		tTrainOrder = aTrain.getOrder ();
		tNextTrainOrder = aTrain.getOnFirstOrderAvailable ();
		/* Check for an "On First" Attribute set and if it is, then handle it */
		if (tNextTrainOrder != Train.NO_ORDER) {
			tOldNextTrainStatus = trainPortfolio.getTrainStatusForOrder (tNextTrainOrder);
			if (tOldNextTrainStatus == Train.NOT_AVAILABLE) {
				trainPortfolio.setTrainsAvailable (tNextTrainOrder);
				tNewNextTrainStatus = trainPortfolio.getTrainStatusForOrder (tNextTrainOrder);
				tTrain = trainPortfolio.getTrainOfOrder (tNextTrainOrder);
				tTrainName = tTrain.getName ();
				aBuyTrainAction.addTrainAvailableStatusEffect (this, tTrainName, tNextTrainOrder, tOldNextTrainStatus,
						tNewNextTrainStatus);
			}
		}
		/*
		 * Check for an "On Last" Attribute set and if it is, then examine if this is
		 * last train
		 */
		tNextTrainOrder = aTrain.getOnLastOrderAvailable ();
		if (tNextTrainOrder != Train.NO_ORDER) {
			tTrainOrderCount = trainPortfolio.countTrainsOfThisOrder (tTrainOrder);
			/* Last Train of this Order, need to make Trains of next Order available */
			if (tTrainOrderCount == 1) {
				tOldNextTrainStatus = trainPortfolio.getTrainStatusForOrder (tNextTrainOrder);
				trainPortfolio.setTrainsAvailable (tNextTrainOrder);
				tNewNextTrainStatus = trainPortfolio.getTrainStatusForOrder (tNextTrainOrder);
				tTrain = trainPortfolio.getTrainOfOrder (tNextTrainOrder);
				tTrainName = tTrain.getName ();
				aBuyTrainAction.addTrainAvailableStatusEffect (this, tTrainName, tNextTrainOrder, tOldNextTrainStatus,
						tNewNextTrainStatus);
			}
		}
	}

	public boolean nextShareHasBids () {
		return startPacketFrame.nextShareHasBids ();
	}

	public boolean firstCertificateHasBidders () {
		return startPacketFrame.firstCertificateHasBidders ();
	}
	
	public boolean removeRustedTrain (String aName) {
		return rustedTrainsPortfolio.removeTrain (aName);
	}

	public void rustAllTrainsNamed (String aTrainName, BuyTrainAction aBuyTrainAction) {
		BankPool tBankPool;
		CorporationList tShareCorporationList;
		CorporationList tMinorCorporationList;
		TrainPortfolio tTrainPortfolio;

		tBankPool = gameManager.getBankPool ();
		tTrainPortfolio = tBankPool.getTrainPortfolio ();
		tTrainPortfolio.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, tBankPool, this, aBuyTrainAction);
		trainPortfolio.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, this, this, aBuyTrainAction);

		tShareCorporationList = gameManager.getShareCompanies ();
		tShareCorporationList.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, this, aBuyTrainAction);

		tMinorCorporationList = gameManager.getMinorCompanies ();
		tMinorCorporationList.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, this, aBuyTrainAction);
	}

	private void setBankCashLabel (JLabel aBankCashLabel) {
		bankCashLabel = aBankCashLabel;
	}

	private void setBankIsBroken (boolean aBroken) {
		bankIsBroken = aBroken;
	}

	public void setClosedPortfolio (Portfolio aClosedPortfolio) {
		closedPortfolio = aClosedPortfolio;
	}

	public void setRustedTrainsPortfolio (TrainPortfolio aRustedTrainsPortfolio) {
		rustedTrainsPortfolio = aRustedTrainsPortfolio;
	}

	public void setup (GameInfo aActiveGame) {
		String tFormat;
		CorporationList tCorpList;

		tFormat = aActiveGame.getCurrencyFormat ();
		setFormat (tFormat);
		tCorpList = gameManager.getPrivates ();
		loadCorporations (tCorpList);
		tCorpList = gameManager.getMinorCompanies ();
		loadCorporations (tCorpList);
		tCorpList = gameManager.getShareCompanies ();
		loadCorporations (tCorpList);
		loadTrains (aActiveGame);
		createStartPacket (gameManager);
	}

	public void updateBankCashLabel () {
		String tBankLabel;

		tBankLabel = BANK_LABEL_PREFIX + Bank.formatCash (getCash ());
		if (bankCashLabel == GUI.NO_LABEL) {
			bankCashLabel = new JLabel (tBankLabel);
		} else {
			bankCashLabel.setText (tBankLabel);
		}
	}

	@Override
	public void updateListeners (String aMessage) {
		super.updateListeners (aMessage);
	}
	
	@Override
	public void printInfo () {
		System.out.println ("Bank Closed Portfolio Count " + closedPortfolio.getCertificateTotalCount ());
		closedPortfolio.printCompactPortfolioInfo ();
		
		System.out.println ("Bank Portfolio Count " + portfolio.getCertificateTotalCount ());
		super.printInfo ();
		rustedTrainsPortfolio.printNameAndQty (TrainPortfolio.RUSTED_TRAINS);
	}
}