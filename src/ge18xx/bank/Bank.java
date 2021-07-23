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
import ge18xx.company.CorporationList;
import ge18xx.company.LoadedCertificate;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.player.StartPacketPortfolio;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.train.Train;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutine2I;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class Bank extends GameBank implements CashHolderI {
	private static final String BANK_LABEL_PREFIX = "Remaining Bank Cash ";
	public static final ElementName EN_BANK_STATE = new ElementName ("Bank");
	private static final AttributeName AN_BANK_CASH = new AttributeName ("cash");
	public static final StartPacketFrame NO_START_PACKET = null;
	public static final String NAME = "Bank";
	public static final Bank NO_BANK = null;
	int treasury;
	static String format;
	static DecimalFormat decimalFormat;
	JLabel bankCashLabel;
	StartPacketFrame startPacketFrame;
	Portfolio closedPortfolio;
	TrainPortfolio rustedTrainsPortfolio;
	private static Logger logger;
	
	public Bank () {
		this (0, null);
	}
		
	public Bank (int aTreasury, GameManager aGameManager) {
		super (NAME, aGameManager);
		treasury = aTreasury;
		setStartPacketFrame (NO_START_PACKET);
		setFormat ("");
		setBankCashLabel (null);
		closedPortfolio = new Portfolio (this);
		rustedTrainsPortfolio = new TrainPortfolio (this);
		logger = LogManager.getLogger (Bank.class);
	}
	
	public void addCash (int aAmount) {
		treasury += aAmount;
		updateBankCashLabel ();
	}
	
	public void addClosedCertificate (Certificate aCertificate) {
		closedPortfolio.addCertificate (aCertificate);
	}
	
	public void addRustedTrain (Train aTrain) {
		rustedTrainsPortfolio.addTrain(aTrain);
	}
	
	public JPanel buildStartPacketInfoJPanel (ItemListener aItemListener, Player aPlayer, GameManager aGameManager) {
		JPanel tPortfolioJPanel;
		JPanel tStartPacketJPanel;
		BoxLayout tLayout;
		
		tStartPacketJPanel = new JPanel ();
		tStartPacketJPanel.setBorder (BorderFactory.createTitledBorder (startPacketFrame.getName ()));
		tLayout = new BoxLayout (tStartPacketJPanel, BoxLayout.Y_AXIS);
		tStartPacketJPanel.setLayout (tLayout);
	
		tPortfolioJPanel = startPacketFrame.buildStartPacketInfoJPanel (aItemListener, aPlayer, 
				aGameManager);
		tStartPacketJPanel.add (Box.createVerticalGlue ());
		tStartPacketJPanel.add (tPortfolioJPanel);
		tStartPacketJPanel.add (Box.createVerticalGlue ());
	
		return tStartPacketJPanel;
	}
	
	public boolean canStartOperatingRound () {
		return startPacketFrame.noMustSellLeft ();
	}
	
	public void applyDiscount () {
		startPacketFrame.applyDiscount ();
	}
	
	public boolean hasMustBuyCertificate () {
		boolean tMustBuy = false;
		
		if (startPacketFrame != null) {
			tMustBuy = startPacketFrame.hasMustBuyCertificate ();
		}
		
		return tMustBuy;
	}
	
	public boolean hasMustSell () {
		return startPacketFrame.hasMustSell ();
	}
	
	public Certificate getMustSellCertificate () {
		return startPacketFrame.getMustSellCertificate ();
	}
	
	public void clearSelections () {
		startPacketFrame.clearSelections ();
	}
	
	public void createStartPacket (GameManager aGameManager) {
		String tXMLCompaniesName;
		String tActiveGameName;
		
		if (aGameManager.gameIsStarted ()) {
			tActiveGameName = aGameManager.getActiveGameName ();
			tXMLCompaniesName = aGameManager.getCompaniesFileName ();
			tXMLCompaniesName = aGameManager.getXMLBaseDirectory () + tXMLCompaniesName;
			startPacketFrame = new StartPacketFrame (tActiveGameName + " Start Packet Frame", aGameManager);
			try {
				startPacketFrame.loadXML (tXMLCompaniesName, startPacketFrame);
			} catch (Exception tException) {
				logger.error ("Creating Start Packet Failure");
				logger.error (tException);
			}
		}
		startPacketFrame.loadStartPacketWithCertificates (portfolio);
	}

	public void discardExcessTrains (BuyTrainAction aBuyTrainAction) {
		BankPool tBankPool;
		CorporationList tShareCorporationList, tMinorCorporationList, tCoalCorporationList;
		
		tBankPool = gameManager.getBankPool ();
		
		tShareCorporationList = gameManager.getShareCompanies ();
		tShareCorporationList.discardExcessTrains (tBankPool, aBuyTrainAction);
		
		tMinorCorporationList = gameManager.getMinorCompanies ();
		tMinorCorporationList.discardExcessTrains (tBankPool, aBuyTrainAction);
		
		tCoalCorporationList = gameManager.getCoalCompanies ();
		tCoalCorporationList.discardExcessTrains (tBankPool, aBuyTrainAction);
		
	}
	
	public static String formatCash (int aCashAmount) {
		String tFormatted;
		
		if (format != null) {
			if (format.equals ("")) {
				tFormatted = String.valueOf (aCashAmount);
			} else {
				tFormatted = decimalFormat.format (aCashAmount);
			}
		} else {
			tFormatted = "" + aCashAmount;
		}
			
		return tFormatted;
	}
	
	public JLabel getBankCashLabel () {
		return bankCashLabel;
	}
	
	public XMLElement getBankStateElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tTrainPortfolioElements, tRustedTrainPortfolioElements;
		
		tXMLElement = aXMLDocument.createElement (EN_BANK_STATE);
		tXMLElement.setAttribute (AN_BANK_CASH, treasury);
		tTrainPortfolioElements = getTrainPortfolioElements (aXMLDocument);
		tXMLElement.appendChild (tTrainPortfolioElements);
		tRustedTrainPortfolioElements = getRustedTrainPortfolioElements (aXMLDocument);
		tXMLElement.appendChild (tRustedTrainPortfolioElements);

		return tXMLElement;
	}

	public int getCash () {
		return treasury;
	}
	
	@Override
	public CashHolderI getCashHolder() {
		return (CashHolderI) this;
	}

	public Certificate getCertificateToBidOn() {
		Certificate tCertificateToBidOn;
		
		tCertificateToBidOn = portfolio.getCertificateToBidOn ();
		if (tCertificateToBidOn == Certificate.NO_CERTIFICATE) {
			tCertificateToBidOn = startPacketFrame.getCertificateToBidOn ();
		}
		
		return tCertificateToBidOn;
	}

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
	
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;
		
		tCurrentHolder = startPacketFrame.getCurrentHolder (aLoadedCertificate);
		if (tCurrentHolder == null) {
			tCurrentHolder = super.getCurrentHolder (aLoadedCertificate);
		}

		return tCurrentHolder;
	}
	
	public Certificate getFreeCertificateWithThisCertificate (Certificate aThisCertificate) {
		return startPacketFrame.getFreeCertificateWithThisCertificate (aThisCertificate);
	}

	public TrainPortfolio getRustedTrainPortfolio () {
		return rustedTrainsPortfolio;
	}

	public XMLElement getRustedTrainPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		
		tXMLElement = rustedTrainsPortfolio.getRustedElements (aXMLDocument);
		
		return tXMLElement;
	}

	public StartPacketFrame getStartPacketFrame () {
		return startPacketFrame;
	}
	
	public String getStartPacketFrameName () {
		return startPacketFrame.getName ();
	}

	public StartPacketPortfolio getStartPacketPortfolio () {
		StartPacketPortfolio tStartPacketPortfolio;
		
		if (startPacketFrame == null) {
			tStartPacketPortfolio = null;
		} else {
			tStartPacketPortfolio = startPacketFrame.getStartPacketPortfolio ();
		}
		
		return tStartPacketPortfolio;
	}
	
	public boolean isBank () {
		return true;
	}
	
	public boolean isInStartPacket (Certificate aCertificate) {	
		return startPacketFrame.hasThisCertificate (aCertificate);
	}
	
	public boolean isStartPacketPortfolioEmpty () {
		return startPacketFrame.isStartPacketPortfolioEmpty ();
	}
	
	public void loadBankState (XMLNode aBankNode) {
		XMLNodeList tXMLNodeList;
		
		treasury = aBankNode.getThisIntAttribute (AN_BANK_CASH);
		
		tXMLNodeList = new XMLNodeList (bankParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aBankNode, TrainPortfolio.EN_TRAIN_PORTFOLIO, 
				TrainPortfolio.EN_RUSTED_TRAIN_PORTFOLIO);
	}
	
	ParsingRoutineI bankParsingRoutine  = new ParsingRoutine2I ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			loadTrainPortfolio (aChildNode);
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aChildNode) {
			loadRustedTrainPortfolio (aChildNode);
		}
	};
	
	public void loadRustedTrainPortfolio (XMLNode aTrainPortfolioNode)  {
		rustedTrainsPortfolio.loadTrainPortfolio (aTrainPortfolioNode, this);
	}
	
	public void loadTrainPortfolio (XMLNode aTrainPortfolioNode) {
		trainPortfolio.loadTrainStatus (aTrainPortfolioNode);
	}

	/* When a Train is bought, check for More Trains to make available */
	public void makeTrainsAvailable (Train aTrain, BuyTrainAction aBuyTrainAction) {
		int tTrainOrder, tTrainOrderCount;
		int tNextTrainOrder;
		int tOldNextTrainStatus, tNewNextTrainStatus;
		String tTrainName;
		Train tTrain;
		
		trainPortfolio = getTrainPortfolio ();
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
				aBuyTrainAction.addTrainAvailableStatusEffect (this, tTrainName, tNextTrainOrder, 
						tOldNextTrainStatus, tNewNextTrainStatus);			
			}
		}
		/* Check for an "On Last" Attribute set and if it is, then examine if this is last train */
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
				aBuyTrainAction.addTrainAvailableStatusEffect (this, tTrainName, tNextTrainOrder, 
						tOldNextTrainStatus, tNewNextTrainStatus);			
			}
		}
		
	}
	
	public boolean availableShareHasBids () {
		return startPacketFrame.availableShareHasBids ();
	}
	
	public boolean nextShareHasBids () {
		return startPacketFrame.nextShareHasBids ();
	}
	
	public Certificate getPrivateToAuction () {
		return startPacketFrame.getPrivateToAuction ();
	}
	
	public void printStartPacket () {
		startPacketFrame.printStartPacket ();
	}
	
	public void closeAllPrivates (BuyTrainAction aBuyTrainAction) {
		CorporationList tPrivateCorporations;
		
		tPrivateCorporations = gameManager.getPrivates ();
		
		tPrivateCorporations.closeAll (aBuyTrainAction);
	}
	
	public void rustAllTrainsNamed (String aTrainName, BuyTrainAction aBuyTrainAction) {
		BankPool tBankPool;
		CorporationList tShareCorporationList, tMinorCorporationList, tCoalCorporationList;
		TrainPortfolio tTrainPortfolio;
		
		tBankPool = gameManager.getBankPool ();
		tTrainPortfolio = tBankPool.getTrainPortfolio ();
		tTrainPortfolio.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, tBankPool,
					this, aBuyTrainAction);
		trainPortfolio.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, this,
				this, aBuyTrainAction);
		
		tShareCorporationList = gameManager.getShareCompanies ();
		tShareCorporationList.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, this, aBuyTrainAction);
		
		tMinorCorporationList = gameManager.getMinorCompanies ();
		tMinorCorporationList.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, this, aBuyTrainAction);
		
		tCoalCorporationList = gameManager.getCoalCompanies ();
		tCoalCorporationList.rustAllTrainsNamed (aTrainName, rustedTrainsPortfolio, this, aBuyTrainAction);
	}
	
	public void setBankCashLabel (JLabel aBankCashLabel) {
		bankCashLabel = aBankCashLabel;
	}
	
	public static void setStaticFormat (String aFormat) {
		format = aFormat;
		decimalFormat = new DecimalFormat (aFormat);
	}

	public void setFormat (String aFormat) {
		setStaticFormat (aFormat);
	}
	
	public void setStartPacketFrame (StartPacketFrame aStartPacket) {
		startPacketFrame = aStartPacket;
	}

	public void transferCashTo (CashHolderI aToHolder, int aAmount) {
		aToHolder.addCash (aAmount);
		addCash (-aAmount);
		updateBankCashLabel ();
	}
	
	public void updateBankCashLabel () {
		if (bankCashLabel == null) {
			bankCashLabel = new JLabel (BANK_LABEL_PREFIX + Bank.formatCash (getCash ()));
		} else {
			bankCashLabel.setText (BANK_LABEL_PREFIX + Bank.formatCash (getCash ()));
		}
	}

	public void clearAllTrainSelections () {
		trainPortfolio.clearAllTrainSelections ();
	}

	public Certificate getMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		Certificate tCertificate = Certificate.NO_CERTIFICATE;
		
		if (startPacketFrame != null) {
			tCertificate = startPacketFrame.getMatchingCertificate (aAbbrev, aPercentage, aIsPresident);
		}
		if (tCertificate == Certificate.NO_CERTIFICATE) {
			tCertificate = portfolio.getCertificate(aAbbrev, aPercentage, aIsPresident);
		}
		
		return tCertificate;
	}
}