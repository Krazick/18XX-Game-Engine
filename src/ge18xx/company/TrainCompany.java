package ge18xx.company;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.CashHolderI;
import ge18xx.player.Player;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.ShareHolders;
import ge18xx.round.OperatingRound;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.CashTransferAction;
import ge18xx.round.action.ClearATrainFromMapAction;
import ge18xx.round.action.ClearAllRoutesAction;
import ge18xx.round.action.FloatCompanyAction;
import ge18xx.round.action.LayTileAction;
import ge18xx.round.action.OperatedTrainsAction;
import ge18xx.round.action.PayFullDividendAction;
import ge18xx.round.action.PayNoDividendAction;
import ge18xx.round.action.PreparedCorporationAction;
import ge18xx.round.action.RemoveTileAction;
import ge18xx.round.action.SkipBaseTokenAction;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.train.TrainRevenueFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public abstract class TrainCompany extends Corporation implements CashHolderI, TrainHolderI {
	public static final ElementName EN_TRAIN_COMPANY = new ElementName ("TrainCompany");
	public static final AttributeName AN_TREASURY = new AttributeName ("treasury");
	public static final AttributeName AN_VALUE = new AttributeName ("value");
	public static final AttributeName AN_LAST_REVENUE = new AttributeName ("lastRevenue");
	public static final AttributeName AN_THIS_REVENUE = new AttributeName ("thisRevenue");
	static final AttributeName AN_CLOSE_ON_TRAIN_PURCHASE = new AttributeName ("closeOnTrainPurchase");
	static final AttributeName AN_BG_COLOR = new AttributeName ("bgColor");
	static final AttributeName AN_FG_COLOR = new AttributeName ("fgColor");
	static final AttributeName AN_HOME_COLOR = new AttributeName ("homeColor");
	static final AttributeName AN_COST = new AttributeName ("cost");
	static final AttributeName AN_MUST_BUY_TRAIN = new AttributeName ("mustBuyTrain");
	public static final String LAST_TRAIN_BOUGHT = "LAST TRAIN BOUGHT";
	public static final String DIVIDENDS_HANDLED = "DIVIDENDS HAVE BEEN HANDLED";
	public static final String BUY_LABEL = "Buy";
	public final static String NO_MONEY = "No money in the Treasury.";
	public final static String REVENUES_NOT_GENERATED = "Train Revenues have not been generated yet.";
	public final static String DIVIDENDS_ALREADY_HANDLED = "Dividend Payment already completed for the turn.";
	public final static String NO_TRAIN_SELECTED = "No train has been selected to be bought.";
	public final static String SELECT_SINGLE_TRAIN = "Must select a single Train to be bought.";
	public final static String OPERATED_NO_REVENUE = "Train Operated but no Revenue has been generated.";
	public static final TrainCompany NO_TRAIN_COMPANY = null;
	public static final int NO_REVENUE_GENERATED = -1;
	public static final String NO_REVENUE = "0";
	public static final JPanel NO_JPANEL = null;
	static final int NO_COST = 0;
	static final int NO_CASH = 0;
	static final float LEFT_ALIGNMENT = 0.0f;
	static final int INFINITE_PRICE = 99999;
	ArrayList<License> licenses;
	TrainPortfolio trainPortfolio;
	TrainRevenueFrame trainRevenueFrame;
	ForceBuyCouponFrame forceBuyCouponFrame;
	QueryOffer queryOffer;
	int closeOnTrainPurchase;
	int treasury;
	int thisRevenue;
	int lastRevenue;
	int value;
	boolean mustBuyTrain;
	boolean hasLaidTile;
	boolean isOperatingTrains;
	String bgColorName;
	String fgColorName;
	String homeColorName;
	Color bgColor;
	Color fgColor;
	Color homeColor;

	public TrainCompany () {
		this (Corporation.NO_ID, Corporation.NO_NAME);
	}

	public TrainCompany (int aID, String aName) {
		this (aID, aName, Corporation.NO_ABBREV, Color.white, Color.black, MapCell.NO_MAP_CELL, Location.NO_LOC,
				NO_COST, ActorI.ActionStates.Unowned, false);
	}

	public TrainCompany (int aID, String aName, String aAbbrev, Color aBgColor, Color aFgColor, MapCell aHomeCity1,
			Location aHomeLocation1, int aCost, ActorI.ActionStates aState, boolean aGovtRailway) {
		this (aID, aName, aAbbrev, aBgColor, aFgColor, aHomeCity1, aHomeLocation1, MapCell.NO_MAP_CELL, Location.NO_LOC,
				aCost, aState, aGovtRailway);
	}

	public TrainCompany (int aID, String aName, String aAbbrev, Color aBgColor, Color aFgColor, MapCell aHomeCity1,
			Location aHomeLocation1, MapCell aHomeCity2, Location aHomeLocation2, int aCost, ActorI.ActionStates aState,
			boolean aGovtRailway) {
		super (aID, aName, aAbbrev, aHomeCity1, aHomeLocation1, aHomeCity2, aHomeLocation2, aState, aGovtRailway);

		trainPortfolio = new TrainPortfolio (this);
		licenses = new ArrayList<License> ();
		bgColor = aBgColor;
		fgColor = aFgColor;
		setTreasury (NO_CASH);
		value = aCost;
		setThisRevenue (NO_REVENUE_GENERATED);
		setLastRevenue (NO_REVENUE_GENERATED);
		setIsOperatingTrains (false);
		if (aID != Corporation.NO_ID) {
			setupTrainRevenueFrame ();
			setCorporationFrame ();
		}
		setMustBuyTrain (false);
		setForceBuyCouponFrame (ForceBuyCouponFrame.NO_FRAME);
	}

	public void addLicense (License aLicense) {
		licenses.add (aLicense);
	}
	
	public boolean hasLicense (String aLicenseName) {
		boolean tHasLicense;
		
		tHasLicense = false;
		for (License tLicense : licenses) {
			if (tLicense.getName ().equals (aLicenseName)) {
				tHasLicense = true;
			}
		}
		
		return tHasLicense;
	}
	
	public boolean removeLicense (License aLicense) {
		boolean tLicenseRemoved;
		
		tLicenseRemoved = licenses.remove (aLicense);
		
		return tLicenseRemoved;
	}
	
	protected void setForceBuyCouponFrame (ForceBuyCouponFrame aFrame) {
		forceBuyCouponFrame = aFrame;
	}

	public TrainCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);

		String tColorName;
		boolean tMustBuyTrain;

		trainPortfolio = new TrainPortfolio (this);
		licenses = new ArrayList<License> ();
		tColorName = aChildNode.getThisAttribute (AN_BG_COLOR);
		bgColorName = tColorName;
		bgColor = translateColor (bgColorName);
		fgColorName = aChildNode.getThisAttribute (AN_FG_COLOR);
		fgColor = translateColor (fgColorName);
		homeColorName = aChildNode.getThisAttribute (AN_HOME_COLOR, bgColorName);
		homeColor = translateColor (homeColorName);
		value = aChildNode.getThisIntAttribute (AN_COST);
		lastRevenue = aChildNode.getThisIntAttribute (AN_LAST_REVENUE);
		thisRevenue = aChildNode.getThisIntAttribute (AN_THIS_REVENUE);
		tMustBuyTrain = aChildNode.getThisBooleanAttribute (AN_MUST_BUY_TRAIN);
		setMustBuyTrain (tMustBuyTrain);
		closeOnTrainPurchase = aChildNode.getThisIntAttribute (AN_CLOSE_ON_TRAIN_PURCHASE, NO_ID);
		setupTrainRevenueFrame ();
		setCorporationFrame ();
		setForceBuyCouponFrame (ForceBuyCouponFrame.NO_FRAME);
	}

	@Override
	public License getPortLicense () {
		License tFoundPortLicense;
		
		tFoundPortLicense = License.NO_LICENSE;
		for (License tLicense : licenses) {
			if (tLicense.isPortLicense ()) {
				tFoundPortLicense = tLicense;
			}
		}
		
		return tFoundPortLicense;
	}
	
	@Override
	public License getLicense (License.LicenseTypes aType) {
		License tFoundLicense;
		
		tFoundLicense = License.NO_LICENSE;
		for (License tLicense : licenses) {
			if (tLicense.isLicenseOfType (aType)) {
				tFoundLicense = tLicense;
			}
		}
		
		return tFoundLicense;
	}

	public void declareBankruptcy () {
		declareBankruptcyAction ();
	}

	public void setupTrainRevenueFrame () {
		String tRevenueFrameTitle;
		GameManager tGameManager;

		tGameManager = corporationList.getGameManager ();
		tRevenueFrameTitle = tGameManager.createFrameTitle ("Train Revenue for " + abbrev);
		trainRevenueFrame = new TrainRevenueFrame (this, tRevenueFrameTitle);
	}

	public TrainRevenueFrame getTrainRevenueFrame () {
		return trainRevenueFrame;
	}

	public void floatCompany (int aInitialTreasury) {
		int tRowIndex;
		FloatCompanyAction tFloatCompanyAction;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		OperatingRound tOperatingRound;
		Bank tBank;

		tBank = corporationList.getBank ();
		tOperatingRound = corporationList.getOperatingRound ();
		tRowIndex = corporationList.getRowIndex (this);
		tOldState = getStatus ();
		setStatus (ActorI.ActionStates.NotOperated);

		tNewState = getStatus ();
		tFloatCompanyAction = new FloatCompanyAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), this);
		tFloatCompanyAction.addChangeCorporationStatusEffect (this, tOldState, tNewState);
		tFloatCompanyAction.addCashTransferEffect (tBank, this, aInitialTreasury);
		tFloatCompanyAction.setChainToPrevious (true);

		tBank.transferCashTo (this, aInitialTreasury);
		corporationList.addDataElement (treasury, tRowIndex, 9);
		corporationList.addDataElement (getStatusName (), tRowIndex, 3);
		corporationList.addAction (tFloatCompanyAction);
	}

	/**
	 * Prepare the Corporation for operations.
	 *
	 */
	@Override
	public void prepareCorporation () {
		PreparedCorporationAction tPreparedCorporationAction;
		String tOperatingRoundID;
		ActorI.ActionStates tPreviousStatus;
		ActorI.ActionStates tNewStatus;
		int tCurrentRevenue;
		int tPreviousRevenue;
		ShareCompany tShareCompany;

		tPreviousStatus = getActionStatus ();
		updateStatus (ActorI.ActionStates.StartedOperations);
		tNewStatus = getActionStatus ();

		tOperatingRoundID = corporationList.getOperatingRoundID ();
		tPreparedCorporationAction = new PreparedCorporationAction (ActorI.ActionStates.OperatingRound,
				tOperatingRoundID, this);
		tPreparedCorporationAction.setChainToPrevious (true);
		tPreparedCorporationAction.addChangeCorporationStatusEffect (this, tPreviousStatus, tNewStatus);
		tCurrentRevenue = thisRevenue;
		tPreviousRevenue = lastRevenue;

		setLastRevenue (thisRevenue);
		setThisRevenue (NO_REVENUE_GENERATED);
		if (tCurrentRevenue != tPreviousRevenue) {
			tPreparedCorporationAction.addUpdateLastRevenueEffect (this, thisRevenue, lastRevenue);
		}
		if (isAShareCompany ()) {
			tShareCompany = (ShareCompany) this;
			if (tShareCompany.wasLoanTaken ()) {
				tShareCompany.setLoanTaken (false);
				tPreparedCorporationAction.addGetLoanEffect (this, true, false);
			}
		}
		tPreparedCorporationAction.addShowFrameEffect (this, corporationFrame);
		tPreparedCorporationAction.setChainToPrevious (true);
		addAction (tPreparedCorporationAction);
		setHasLaidTile (false);
		corporationList.updateRoundFrame ();
	}

	@Override
	public void loadStates (XMLNode aXMLNode) {
		int tThisRevenue, tLastRevenue;
		boolean tMustBuyTrain;

		tLastRevenue = aXMLNode.getThisIntAttribute (AN_LAST_REVENUE);
		tThisRevenue = aXMLNode.getThisIntAttribute (AN_THIS_REVENUE);
		setLastRevenue (tLastRevenue);
		setThisRevenue (tThisRevenue);
		tMustBuyTrain = aXMLNode.getThisBooleanAttribute (AN_MUST_BUY_TRAIN);
		setMustBuyTrain (tMustBuyTrain);
	}

	@Override
	public void addCash (int aAmount) {
		treasury += aAmount;
		updateListeners (CORPORATION_CASH_CHANGED + " by " + aAmount);
	}

	public void setTreasury (int aCash) {
		treasury = aCash;
	}

	@Override
	public int getCash () {
		return treasury;
	}

	public int getTreasury () {
		return getCash ();
	}

	public boolean noCash () {
		return (treasury == NO_CASH);
	}

	@Override
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;

		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		aCorporationList.addDataElement (getCash (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getBgColorName (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getBgColor (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getFgColorName (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getFgColor (), aRowIndex, tCurrentColumn++);

		return tCurrentColumn;
	}

	@Override
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;

		tCurrentColumn = super.addAllHeaders (aCorporationList, tCurrentColumn);
		aCorporationList.addHeader ("Treasury", tCurrentColumn++);
		aCorporationList.addHeader ("Bg Color Name", tCurrentColumn++);
		aCorporationList.addHeader ("Bg Color", tCurrentColumn++);
		aCorporationList.addHeader ("Fg Color Name", tCurrentColumn++);
		aCorporationList.addHeader ("Fg Color", tCurrentColumn++);

		return tCurrentColumn;
	}

	@Override
	public void addTrain (Train aTrain) {
		trainPortfolio.addTrain (aTrain);
	}

	@Override
	public void appendOtherElements (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		XMLElement tTrainPortfolioElements;
		XMLElement tPurchaseOfferElements;

		aXMLCorporationState.setAttribute (AN_LAST_REVENUE, getLastRevenue ());
		aXMLCorporationState.setAttribute (AN_THIS_REVENUE, getThisRevenue ());
		aXMLCorporationState.setAttribute (AN_MUST_BUY_TRAIN, mustBuyTrain ());
		if (! trainPortfolio.hasNoTrain ()) {
			tTrainPortfolioElements = trainPortfolio.getElements (aXMLDocument);
			aXMLCorporationState.appendChild (tTrainPortfolioElements);
		}
		super.appendOtherElements (aXMLCorporationState, aXMLDocument);
		if (queryOffer != QueryOffer.NO_QUERY_OFFER) {
			tPurchaseOfferElements = queryOffer.getElements (aXMLDocument);
			aXMLCorporationState.appendChild (tPurchaseOfferElements);
		}
	}

	@Override
	public boolean atTrainLimit () {
		int tTrainLimit;
		boolean tAtTrainLimit;

		tTrainLimit = getTrainLimit ();
		if (tTrainLimit == trainPortfolio.getTrainCount ()) {
			tAtTrainLimit = true;
		} else {
			tAtTrainLimit = false;
		}

		return tAtTrainLimit;
	}

	@Override
	public JPanel buildCorpInfoJPanel () {
		JPanel tCorpInfoJPanel;
		JLabel tCorpLabel;
		JLabel tBankPoolOwned;
		JLabel tEscrow;
		JLabel tStatus;
		JLabel tPresidentName;
		JLabel tTreasury;
		JLabel tTrainList;
		JLabel tThisRevenue;
		int tEscrowAmount;
		String tCorpName;
		
		tCorpName = getAbbrev ();
		tCorpInfoJPanel = new JPanel ();
		tCorpInfoJPanel.setLayout (new BoxLayout (tCorpInfoJPanel, BoxLayout.Y_AXIS));
		tCorpLabel = new JLabel (getAbbrev ());
		tEscrowAmount = 0;
		tStatus = new JLabel ("[" + getStatusName () + "]");
		if (isActive ()) {
			tCorpName += " " + buildPercentOwnedLabel ();
			tEscrow = new JLabel ();
			if (hasDestination ()) {
				if (hasReachedDestination ()) {
					tCorpName += " *";
				} else {
					tEscrowAmount = calculateEscrowToRelease ();
					if (tEscrowAmount > 0) {
						tEscrow.setText (tCorpName);
					}
				}
			}
			tCorpLabel.setText (tCorpName);
			tCorpInfoJPanel.add (tCorpLabel);

			if (! isAMinorCompany ()) {
				tBankPoolOwned =  new JLabel ("[" + getBankPoolPercentage () + " in Bank Pool]");
				tCorpInfoJPanel.add (tBankPoolOwned);
			}
			if (tEscrowAmount > 0) {
				tCorpInfoJPanel.add (tEscrow);
			}
			tCorpInfoJPanel.add ((tStatus));
			tPresidentName = new JLabel ("Prez: " + getPresidentName ());
			tCorpInfoJPanel.add (tPresidentName);
			tTreasury = new JLabel ("Treasury: " + Bank.formatCash (getCash ()));
			tCorpInfoJPanel.add (tTreasury);
			if (canOperate () || didOperate ()) {
				tTrainList = new JLabel (trainPortfolio.getTrainList ());
				tCorpInfoJPanel.add (tTrainList);
				tThisRevenue = new JLabel ("This Revenue: " + getFormattedThisRevenue ());
				tCorpInfoJPanel.add (tThisRevenue);
			}
		} else {
			tCorpInfoJPanel.add (tCorpLabel);
			tCorpInfoJPanel.add (tStatus);
		}

		return tCorpInfoJPanel;
	}
	
	public int calculateEscrowToRelease () {
		return 0;
	}

	public Border setupBorder (boolean aSamePresident) {
		Border tPanelBorder, tBackgroundBorder, tOuterBorder, tRaisedBevel;
		Border tLoweredBevel, tBevelBorder1, tBevelBorder2;
		Border tSamePrezBorder;

		tBackgroundBorder = setupBackgroundBorder (5);
		if (aSamePresident) {
			tSamePrezBorder = setupSamePrezBorder ();
			tRaisedBevel = BorderFactory.createBevelBorder (BevelBorder.RAISED, fgColor, bgColor);
			tLoweredBevel = BorderFactory.createBevelBorder (BevelBorder.LOWERED, fgColor, bgColor);
			tBevelBorder1 = BorderFactory.createCompoundBorder (tRaisedBevel, tLoweredBevel);
			tBevelBorder2 = BorderFactory.createCompoundBorder (tBevelBorder1, tSamePrezBorder);
			tPanelBorder = BorderFactory.createCompoundBorder (tSamePrezBorder, tBevelBorder2);
		} else {
			tOuterBorder = setupOuterBorder ();
			tPanelBorder = BorderFactory.createCompoundBorder (tOuterBorder, tBackgroundBorder);
		}

		return tPanelBorder;
	}

	private Border setupSamePrezBorder () {
		Border tOuterBorder;

		tOuterBorder = BorderFactory.createLineBorder (Color.CYAN, 2);

		return tOuterBorder;
	}

	private Border setupOuterBorder () {
		Border tOuterBorder;
		int tThickness;

		tThickness = 2;
		if (isSoldOut ()) {
			tThickness = 4;
		}
		tOuterBorder = BorderFactory.createLineBorder (bgColor, tThickness);

		return tOuterBorder;
	}

	private Border setupBackgroundBorder (int aWidth) {
		Border tBackgroundBorder;
		Color tBackgroundColor;

		tBackgroundColor = new Color (237, 237, 237);
		tBackgroundBorder = BorderFactory.createLineBorder (tBackgroundColor, aWidth);

		return tBackgroundBorder;
	}

	@Override
	public Border setupBorder () {
		Border tCorpBorder, tOuterBorder, tInnerBorder;

		tOuterBorder = setupOuterBorder ();
		tInnerBorder = setupBackgroundBorder (2);
		tCorpBorder = BorderFactory.createCompoundBorder (tOuterBorder, tInnerBorder);

		return tCorpBorder;
	}

	public JPanel buildPortfolioTrainsJPanel (CorporationFrame aCorporationFrame, GameManager aGameManager,
			boolean aFullTrainPortfolio, boolean aCanBuyTrain, String aDisableToolTipReason,
			Corporation aBuyingCorporation, int aTokenCount) {
		JPanel tTrainInfoJPanel;
		JPanel tCorpJPanel;
		Border tBorder;
		String tPresident, tBuyingPresident;
		String tActionLabel;

		tActionLabel = BUY_LABEL;
		tPresident = getPresidentName ();
		tBuyingPresident = aBuyingCorporation.getPresidentName ();
		tBorder = setupBorder (tPresident.equals (tBuyingPresident));

		tCorpJPanel = new JPanel ();
		tCorpJPanel.setLayout (new BoxLayout (tCorpJPanel, BoxLayout.Y_AXIS));
		tCorpJPanel.setAlignmentX (Component.LEFT_ALIGNMENT);
		tCorpJPanel.setBorder (tBorder);

		if (!isPlayerOwned ()) {
			tPresident = "Bank";
		}
		addLabel (tCorpJPanel, getAbbrev () + " " + buildPercentOwnedLabel ());
		addLabel (tCorpJPanel, "State: " + getStatusName ());
		addLabel (tCorpJPanel, "Treasury: " + Bank.formatCash (treasury));
		addLabel (tCorpJPanel, "Tokens: " + aTokenCount);
		addLabel (tCorpJPanel, "Prez: " + tPresident);
		if (canOperate ()) {
			addLabel (tCorpJPanel, "Revenue: " + getFormattedThisRevenue ());
		}

		if (trainPortfolio != TrainPortfolio.NO_TRAIN_PORTFOLIO) {
			tTrainInfoJPanel = trainPortfolio.buildPortfolioJPanel (aCorporationFrame, this, aGameManager, tActionLabel,
					aFullTrainPortfolio, aCanBuyTrain, aDisableToolTipReason);
			tCorpJPanel.add (tTrainInfoJPanel);
		}

		return tCorpJPanel;
	}

	private void addLabel (JPanel aCorpJPanel, String aString) {
		JLabel tLabel;

		tLabel = new JLabel (aString);
		aCorpJPanel.add (tLabel);
	}

	public JPanel buildLicenseInfoPanel () {
		JPanel tLicensePanel;
		JLabel tLicenseLabel;
		
		tLicensePanel = NO_JPANEL;
		if (licenses != License.NO_LICENSES) {
			if (licenses.size () > 0) {
				tLicensePanel = new JPanel ();
				for (License tLicense : licenses) {
					tLicenseLabel = new JLabel (tLicense.getLicenseName ());
					tLicensePanel.add (tLicenseLabel);
				}
			}
		}
		
		return tLicensePanel;
	}
	
	public JPanel buildCertPortfolioInfoJPanel (ItemListener aItemListener) {
		JPanel tCertPortfolioInfoJPanel;
		JPanel tTrainPortfolioInfoJPanel;
		JPanel tPortfolioInfoJPanel;
		JPanel tLicenseInfoPanel;
		GameManager tGameManager;
		JLabel tLabel;
		JLabel tBPPLabel;
		String tBankPoolPercent;

		tGameManager = corporationList.getGameManager ();
		if (trainPortfolio != TrainPortfolio.NO_TRAIN_PORTFOLIO) {
			tTrainPortfolioInfoJPanel = trainPortfolio.buildPortfolioJPanel (aItemListener, this, tGameManager, null,
					TrainPortfolio.FULL_TRAIN_PORTFOLIO, true, "");
		} else {
			tTrainPortfolioInfoJPanel = new JPanel ();
			tLabel = new JLabel (">>NO TRAINS<<");
			tTrainPortfolioInfoJPanel.add (tLabel);
		}

		tPortfolioInfoJPanel = buildPortfolioJPanel (aItemListener, tGameManager);
		tCertPortfolioInfoJPanel = new JPanel ();

		tBankPoolPercent = getBankPoolPercentage () + "% " + abbrev + " in Bank Pool";
		tBPPLabel = new JLabel (tBankPoolPercent);
		tCertPortfolioInfoJPanel.add (tBPPLabel);
		tCertPortfolioInfoJPanel.add (tTrainPortfolioInfoJPanel);
		tCertPortfolioInfoJPanel.add (tPortfolioInfoJPanel);
		tLicenseInfoPanel = buildLicenseInfoPanel ();
		if (tLicenseInfoPanel != NO_JPANEL) {
			tCertPortfolioInfoJPanel.add (tLicenseInfoPanel);
		}

		return tCertPortfolioInfoJPanel;
	}

	@Override
	public void forceBuyTrain () {
		Coupon tCheapestTrain;
		ForceBuyCouponFrame tForceBuyTrainFrame;
		ShareCompany tShareCompany;

		if (isAShareCompany ()) {
			tCheapestTrain = getCheapestBankTrain ();
			tShareCompany = (ShareCompany) this;
			tForceBuyTrainFrame = new ForceBuyCouponFrame (tShareCompany, tCheapestTrain);
			setForceBuyCouponFrame (tForceBuyTrainFrame);
			forceBuyCouponFrame.updateMainJPanel ();
			forceBuyCouponFrame.setVisible (true);
		}
	}

	@Override
	public boolean mustBuyTrain () {
		return mustBuyTrain;
	}

	@Override
	public boolean hasNoTrain () {
		return (trainPortfolio.hasNoTrain ());
	}

	public boolean hasLaidTile () {
		// If the Company Status is one of these
		// TileLaid, Tile2Laid, TileUpgraded, TileAndStationLaid
		return hasLaidTile;
	}

	@Override
	public boolean mustBuyTrainNow () {
		boolean tMustBuyTrainNow;

		if (mustBuyTrain && hasNoTrain ()) {
			tMustBuyTrainNow = true;
		} else {
			tMustBuyTrainNow = false;
		}

		return tMustBuyTrainNow;
	}

	@Override
	public void setMustBuyTrain (boolean aMustBuyTrain) {
		mustBuyTrain = aMustBuyTrain;
	}

	public void setHasLaidTile (boolean aHasLaidTile) {
		hasLaidTile = aHasLaidTile;
	}

	@Override
	public Coupon getCheapestBankTrain () {
		Coupon tCheapestTrain = Train.NO_TRAIN;
		Coupon tBankPoolTrain, tBankTrain;
		int tBankPoolTrainCost, tBankTrainCost;
		BankPool tBankPool;
		Bank tBank;

		tBankPool = corporationList.getBankPool ();
		tBankPoolTrain = tBankPool.getCheapestTrain ();
		tBank = corporationList.getBank ();
		tBankTrain = tBank.getCheapestTrain ();
		if (tBankPoolTrain != Train.NO_TRAIN) {
			tBankPoolTrainCost = tBankPoolTrain.getPrice ();
		} else {
			tBankPoolTrainCost = INFINITE_PRICE;
		}
		if (tBankTrain != Train.NO_TRAIN) {
			tBankTrainCost = tBankTrain.getPrice ();
		} else {
			tBankTrainCost = INFINITE_PRICE;
		}
		// TODO: Determine if BankPool Train and BankTrain cost is the same, which train
		// to buy?
		// Provide choice where to buy from?
		if (tBankPoolTrainCost < tBankTrainCost) {
			tCheapestTrain = tBankPoolTrain;
		} else {
			tCheapestTrain = tBankTrain;
		}

		return tCheapestTrain;
	}

	public void buyOtherTrain () {
		BuyTrainFrame tBuyTrainFrame;
		Train tTrainToBuy;
		TrainHolderI tTrainHolder;

		tTrainHolder = getSelectedTrainHolder ();
		tTrainToBuy = tTrainHolder.getSelectedTrain ();
		tBuyTrainFrame = new BuyTrainFrame (this, tTrainHolder, tTrainToBuy);
		tBuyTrainFrame.requestFocus ();
	}

	@Override
	public void buyTrain () {
		buyTrain (0);
	}

	public void buyTrain (int aNeededCash) {
		Train tUpgradingTrain;
		BankPool tBankPool;
		BuyTrainAction tBuyTrainAction;
		String tOperatingRoundID;

		if (isSelectedTrainInBank ()) {
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			addNeededCashTransferEffect (tBuyTrainAction, aNeededCash);
			tUpgradingTrain = getSelectedTrain ();
			if (tUpgradingTrain != Train.NO_TRAIN) {
				tBankPool = corporationList.getBankPool ();
				moveTrainToBankPool (tUpgradingTrain, tBankPool);
				tBuyTrainAction.addUpgradeTrainEffect (this, tUpgradingTrain, tBankPool);
			}
			buyBankTrain (tBuyTrainAction);
		} else {
			buyOtherTrain ();
		}
		clearAllTrainSelections ();
	}

	protected void addNeededCashTransferEffect (CashTransferAction aCashTransferAction, int aPresidentContribution) {
		CashHolderI tPresident;

		if (aPresidentContribution > 0) {
			tPresident = (CashHolderI) getPresident ();
			transferCashTo (tPresident, -aPresidentContribution);
			aCashTransferAction.addCashTransferEffect (tPresident, this, aPresidentContribution);
		}
	}
	
	public Train getSelectedBankTrain () {
		Train tTrain;
		TrainHolderI tTrainHolder;

		tTrainHolder = getSelectedTrainHolder ();
		tTrain = tTrainHolder.getSelectedTrain ();

		return tTrain;
	}

	public void buyBankTrain (BuyTrainAction aBuyTrainAction) {
		Train tTrain;
		Coupon tNextAvailableTrain;
		TrainHolderI tTrainHolder;
		CashHolderI tCashHolder;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		boolean tStatusUpdated;
		Bank tBank;
		GameManager tGameManager;
		boolean tFirstTrainOfType;

		tGameManager = getGameManager ();
		tTrain = getSelectedBankTrain ();
		tTrainHolder = getSelectedTrainHolder ();
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.BoughtTrain);
		tNewStatus = status;
		tCashHolder = tTrainHolder.getCashHolder ();
		aBuyTrainAction.addTransferTrainEffect (tTrainHolder, tTrain, this);
		aBuyTrainAction.addCashTransferEffect (this, tCashHolder, tTrain.getPrice ());
		if (tStatusUpdated) {
			aBuyTrainAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
		}
		if (closeOnTrainPurchase != NO_ID) {
			tGameManager.closeCompany (closeOnTrainPurchase, aBuyTrainAction);
		}
		tFirstTrainOfType = false;
		tNextAvailableTrain = Train.NO_TRAIN;
		if (!tTrainHolder.isABankPool ()) {
			if (tTrainHolder.isABank ()) {
				tBank = (Bank) tTrainHolder;
				tFirstTrainOfType = corporationList.isFirstTrainOfType (tTrain);
				tBank.makeTrainsAvailable (tTrain, aBuyTrainAction);
//				tNextAvailableTrain = tBank.getNextAvailableTrain ();
			}
		}
		addTrain (tTrain);
		transferCashTo (tCashHolder, tTrain.getPrice ());
		tTrainHolder.removeSelectedTrain ();
		if (tFirstTrainOfType) {
			corporationList.performPhaseChange (this, tTrain, aBuyTrainAction);
		}
		if (tTrainHolder.isABank ()) {
			tBank = (Bank) tTrainHolder;
			tNextAvailableTrain = tBank.getNextAvailableTrain ();
		}
		if (tNextAvailableTrain != Train.NO_TRAIN) {
			if (!tNextAvailableTrain.getName ().equals (tTrain.getName ())) {
				updateListeners (LAST_TRAIN_BOUGHT);
			}
		}
		addAction (aBuyTrainAction);
		updateInfo ();
	}

	@Override
	public boolean trainIsSelected () {
		boolean tTrainIsSelected = false;
		Coupon tTrain;
		TrainHolderI tTrainHolder;

		tTrainHolder = getSelectedTrainHolder ();
		if (tTrainHolder != TrainHolderI.NO_TRAIN_HOLDER) {
			tTrain = tTrainHolder.getSelectedTrain ();
			if (tTrain != Train.NO_TRAIN) {
				tTrainIsSelected = true;
			}
		}

		return tTrainIsSelected;
	}

	@Override
	public boolean allBasesHaveTiles () {
		boolean tAllBasesHaveTiles;
		
		tAllBasesHaveTiles = false;
		if (homeCity1 != null) {
			if (hasTwoBases ()) {
				if (homeCity2 != null) {
					if (homeCity1.isTileOnCell () && homeCity2.isTileOnCell ()) {
						tAllBasesHaveTiles = true;
					}
				}
			} else {
				tAllBasesHaveTiles = homeCity1.isTileOnCell ();
			}
		}
		return tAllBasesHaveTiles;
	}
	
	@Override
	public boolean canLayTile () {
		boolean tCanLayTile;

		tCanLayTile = false;
		if ((status == ActorI.ActionStates.StartedOperations) || (status == ActorI.ActionStates.StationLaid)) {
			tCanLayTile = true;
		}

		return tCanLayTile;
	}

	@Override
	public boolean canOperate () {
		boolean tCanOperate;

		if (didOperate () || isClosed () || ! isFormed ()) {
			tCanOperate = false;
		} else {
			if (isAMinorCompany () && isPlayerOwned ()) {
				if (isOperating ()) {
					tCanOperate = true;
				} else if ((status == ActorI.ActionStates.Owned) ||
					(status == ActorI.ActionStates.Operated) ||
					(status == ActorI.ActionStates.NotOperated)) {
					tCanOperate = true;
				} else {
					tCanOperate = false;
				}
			} else if ((status == ActorI.ActionStates.WillFloat) || 
				(status == ActorI.ActionStates.StartedOperations)) {
				tCanOperate = true;
			} else {
				tCanOperate = false;
			}
		}

		return tCanOperate;
	}

	@Override
	public boolean canOperateTrains () {
		boolean tOperateTrains;

		tOperateTrains = false;
		if ((status == ActorI.ActionStates.StartedOperations) || (status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.Tile2Laid) || (status == ActorI.ActionStates.TileUpgraded) ||
			(status == ActorI.ActionStates.TileAndStationLaid) || (status == ActorI.ActionStates.StationLaid)) {
			if (trainPortfolio.getTrainCount () > 0) {
				tOperateTrains = true;
			}
		}

		return tOperateTrains;
	}

	@Override
	public boolean canPayDividend () {
		boolean tCanPayDividend;

		tCanPayDividend = false;
		if (didOperateTrains ()) {
			if (thisRevenue > 0) {
				tCanPayDividend = true;
			}
		}

		return tCanPayDividend;
	}

	@Override
	public boolean didPayLoanInterest () {
		boolean tDidPayLoanInterst;

		tDidPayLoanInterst = false;
		if ((status == ActorI.ActionStates.HandledLoanInterest) || dividendsHandled ())  {
			tDidPayLoanInterst = true;
		}

		return tDidPayLoanInterst;
	}

	@Override
	public boolean didOperateTrains () {
		boolean tDidOperateTrains;

		if ((status == ActorI.ActionStates.OperatedTrain) ||
			(status == ActorI.ActionStates.HandledLoanInterest)) {
			tDidOperateTrains = true;
		} else {
			tDidOperateTrains = false;			
		}

		return tDidOperateTrains;
	}

	@Override
	public boolean didOperate () {
		return (status == ActorI.ActionStates.Operated);
	}

	public void discardExcessTrains (BankPool aBankPool, BuyTrainAction aBuyTrainAction) {
		int tTrainLimit, tTrainCount;
		Train tTrain;

		tTrainLimit = getTrainLimit ();
		tTrainCount = trainPortfolio.getTrainCount ();
		if (tTrainCount > tTrainLimit) {
			while (tTrainCount > tTrainLimit) {
				tTrain = trainPortfolio.getCheapestTrain ();
				moveTrainToBankPool (tTrain, aBankPool);
				aBuyTrainAction.addDiscardExcessTrainEffect (this, tTrain, aBankPool);
				tTrainCount = trainPortfolio.getTrainCount ();
			}
		}
	}

	public void moveTrainToBankPool (Train aTrain, BankPool aBankPool) {
		String tTrainName;

		tTrainName = aTrain.getName ();
		aBankPool.addTrain (aTrain);
		trainPortfolio.removeTrain (tTrainName);
	}

	@Override
	public boolean dividendsHandled () {
		boolean tDividendsHandled;

		tDividendsHandled = false;
		if ((status == ActorI.ActionStates.Closed) || (status == ActorI.ActionStates.Unformed) ||
			(status == ActorI.ActionStates.Unowned) || (status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.MayFloat) || (status == ActorI.ActionStates.WillFloat) ||
			(status == ActorI.ActionStates.HoldDividend) || (status == ActorI.ActionStates.HalfDividend) ||
			(status == ActorI.ActionStates.FullDividend) || (status == ActorI.ActionStates.BoughtTrain) ||
			(status == ActorI.ActionStates.Operated)) {
			tDividendsHandled = true;
		}

		return tDividendsHandled;
	}

	@Override
	public void enterPlaceTileMode () {
		corporationList.enterPlaceTileMode ();
	}

	@Override
	public void enterSelectRouteMode (RouteInformation aRouteInformation) {
		corporationList.enterSelectRouteMode (aRouteInformation);
	}

	@Override
	public void exitSelectRouteMode () {
		corporationList.exitSelectRouteMode ();
	}

	@Override
	public int fieldCount () {
		return super.fieldCount () + 5;
	}

	@Override
	public CashHolderI getCashHolder () {
		return this;
	}

	@Override
	public Color getBgColor () {
		return bgColor;
	}

	public String getBgColorName () {
		return bgColorName;
	}

	@Override
	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLCorporationState;

		tXMLCorporationState = aXMLDocument.createElement (EN_TRAIN_COMPANY);
		getCorporationStateElement (tXMLCorporationState, aXMLDocument);

		return tXMLCorporationState;
	}

	@Override
	public void getCorporationStateElement (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {

		aXMLCorporationState.setAttribute (AN_VALUE, getValue ());
		if (lastRevenue > 0) {
			aXMLCorporationState.setAttribute (AN_LAST_REVENUE, lastRevenue);
		}
		aXMLCorporationState.setAttribute (AN_TREASURY, getCash ());
		getLicensesElement (aXMLCorporationState, aXMLDocument);
		super.getCorporationStateElement (aXMLCorporationState, aXMLDocument);
	}

	public void getLicensesElement (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		XMLElement tXMLLicenses;
		XMLElement tLicenseElement;
		
		if (licenses != License.NO_LICENSES) {
			if (licenses.size () > 0) {
				tXMLLicenses = aXMLDocument.createElement (License.EN_LICENSES);
				for (License tLicense : licenses) {
					tLicenseElement = tLicense.createElement (aXMLDocument);
					tLicense.addAttributes (tLicenseElement);

					tXMLLicenses.appendChild (tLicenseElement);
				}
				aXMLCorporationState.appendChild (tXMLLicenses);
			}
		}
		
	}
	public double getDividendFor1Percent () {
		return thisRevenue / 100.0;
	}

	@Override
	public int getFullShareDividend () {
		return thisRevenue / 10;
	}

	@Override
	public int getHalfShareDividend () {
		int tHalfShare;

		/* Add 0.5 to round double up to next integer */
		tHalfShare = (int) (thisRevenue / 20.0 + 0.5);

		return tHalfShare;
	}

	@Override
	public Color getFgColor () {
		return fgColor;
	}

	public String getFgColorName () {
		return fgColorName;
	}

	@Override
	public String getFormattedThisRevenue () {
		String tFormattedRevenue;

		tFormattedRevenue = trainRevenueFrame.formatRevenue (thisRevenue);

		return tFormattedRevenue;
	}

	@Override
	public String getFormattedLastRevenue () {
		String tFormattedRevenue;

		tFormattedRevenue = trainRevenueFrame.formatRevenue (lastRevenue);

		return tFormattedRevenue;
	}

	@Override
	public int getThisRevenue () {
		return thisRevenue;
	}

	@Override
	public int getLastRevenue () {
		return lastRevenue;
	}

	@Override
	public int getLocalSelectedTrainCount () {
		int tSelectedTrainCount;

		tSelectedTrainCount = trainPortfolio.getSelectedCount ();

		return tSelectedTrainCount;
	}

	@Override
	public TrainHolderI getLocalSelectedTrainHolder () {
		TrainHolderI tTrainHolder = TrainHolderI.NO_TRAIN_HOLDER;
		Coupon tSelectedTrain;

		tSelectedTrain = trainPortfolio.getSelectedTrain ();
		if (tSelectedTrain != Train.NO_TRAIN) {
			tTrainHolder = this;
		}

		return tTrainHolder;
	}

	public boolean isSelectedTrainInBank () {
		boolean tIsSelectedTrainHolder = true;
		TrainHolderI tTrainHolder;

		tTrainHolder = getOtherSelectedTrainHolder ();
		if (tTrainHolder != TrainHolderI.NO_TRAIN_HOLDER) {
			tIsSelectedTrainHolder = false;
		}

		return tIsSelectedTrainHolder;
	}

	public boolean isSelectedTrainHolderTheBank () {
		boolean tIsSelectedTrainHolderTheBank;
		TrainHolderI tSelectedTrainHolder;
		Bank tBank;

		tBank = getBank ();

		tSelectedTrainHolder = getSelectedTrainHolder ();
		tIsSelectedTrainHolderTheBank = tSelectedTrainHolder.equals (tBank);

		return tIsSelectedTrainHolderTheBank;
	}

	@Override
	public boolean isATrainCompany () {
		return true;
	}

	public TrainHolderI getSelectedTrainHolder () {
		Bank tBank;
		BankPool tBankPool;
		TrainHolderI tTrainHolder;
		Coupon tTrain;

		tBank = getBank ();
		tBankPool = getBankPool ();
		tTrain = Train.NO_TRAIN;
		tTrainHolder = TrainHolderI.NO_TRAIN_HOLDER;
		if (tTrain == Train.NO_TRAIN) {
			tTrain = tBank.getSelectedTrain ();
			if (tTrain != Train.NO_TRAIN) {
				tTrainHolder = tBank;
			}
		}
		if (tTrain == Train.NO_TRAIN) {
			tTrain = tBankPool.getSelectedTrain ();
			if (tTrain != Train.NO_TRAIN) {
				tTrainHolder = tBankPool;
			}
		}

		if (tTrain == Train.NO_TRAIN) {
			tTrainHolder = getOtherSelectedTrainHolder ();
		}

		if (tTrain == Train.NO_TRAIN) {
			tTrain = trainPortfolio.getSelectedTrain ();
			if (tTrain != Train.NO_TRAIN) {
				tTrainHolder = this;
			}
		}

		return tTrainHolder;
	}

	@Override
	public Train getSelectedTrain () {
		return trainPortfolio.getSelectedTrain ();
	}

	@Override
	public String getStatusName () {
		String tStatus;

		tStatus = super.getStatusName ();
		if (tStatus.equals (ActorI.ActionStates.Owned.toString ())) {
			if (didOperate ()) {
				tStatus = ActorI.ActionStates.Operated.toString ();
			}
		}

		return tStatus;
	}

	/**
	 * Find the Train at the specified index in the Train Portfolio, and return it
	 *
	 * @param aIndex The Index for the Train to find
	 * @return The Train at the specified index in the Train Portfolio
	 */
	public Train getTrain (int aIndex) {
		return trainPortfolio.getTrainAt (aIndex);
	}

	/**
	 * Find the Train at the specified index in the Train Portfolio, and return it
	 *
	 * @param aName The name for the Train to find
	 * @return The Train at the specified index in the Train Portfolio
	 */
	@Override
	public Train getTrain (String aName) {
		return trainPortfolio.getTrain (aName);
	}

	@Override
	public int getTrainCount () {
		int tTrainCount;

		tTrainCount = 0;
		if (trainPortfolio != TrainPortfolio.NO_TRAIN_PORTFOLIO) {
			tTrainCount = trainPortfolio.getTrainCount ();
		}

		return tTrainCount;
	}

	@Override
	public int getTrainLimit () {
		int tTrainLimit;

		if (isAMinorCompany ()) {
			tTrainLimit = corporationList.getMinorTrainLimit ();
		} else {
			tTrainLimit = corporationList.getTrainLimit (govtRailway);
		}

		return tTrainLimit;
	}

	@Override
	public String getTrainNameAndQty (String aStatus) {
		return trainPortfolio.getTrainNameAndQty (aStatus);
	}

	@Override
	public TrainPortfolio getTrainPortfolio () {
		return trainPortfolio;
	}

	@Override
	public int getTrainQuantity (String aName) {
		return trainPortfolio.getTrainQuantity (aName);
	}

	public int getValue () {
		return value;
	}

	@Override
	public boolean hasTrainNamed (String aName) {
		return trainPortfolio.hasTrainNamed (aName);
	}

	@Override
	public boolean hasTrainOfType (Coupon aTrain) {
		return trainPortfolio.hasTrainNamed (aTrain.getName ());
	}

	@Override
	public void loadStatus (XMLNode aXMLNode) {
		Bank tBank;

		super.loadStatus (aXMLNode);
		addCash (-getCash ()); // Clear out any Cash here
		addCash (aXMLNode.getThisIntAttribute (AN_TREASURY));
		setLastRevenue (aXMLNode.getThisIntAttribute (AN_LAST_REVENUE));
		setMustBuyTrain (aXMLNode.getThisBooleanAttribute (AN_MUST_BUY_TRAIN));
		tBank = corporationList.getBank ();
		trainPortfolio.loadTrainPortfolio (aXMLNode, tBank);
		super.loadPortfolio (aXMLNode);
		loadLicenses (aXMLNode);
		// TODO: Build way to load a QueryOffer (PurchasePrivateOffer, PurchaseTrainOffer, ExchangePrivateQuery)
		// to load the QueryOffer Object here, and in the Player LoadState method
		// Probably store 'class' in the EN_QUERY_OFFER Element as attribute
		// Then just like an Action or Effect, use reflections to load it.
		// Can this be a generic method in 'QueryOffer' that both here and Player can call it?
	}

	public void loadLicenses (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (licensesParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, License.EN_LICENSES);
	}

	ParsingRoutineI licensesParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			loadLicense (aChildNode);
		}
	};

	public void loadLicense (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (licenseParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, License.EN_LICENSE);
	}

	ParsingRoutineI licenseParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			loadALicense (aChildNode);
		}
	};
	
	public void loadALicense (XMLNode aXMLNode) {
		License tLicense;
		PortLicense tPortLicense;
		
		tLicense = new License (aXMLNode);
		if (tLicense.isPortLicense ()) {
			tPortLicense = new PortLicense (tLicense.getName (), tLicense.getBenefitValue ());
			licenses.add (tPortLicense);
		} else {
			licenses.add (tLicense);
		}
	}

	@Override
	public void operateTrains () {
		GameManager tGameManager;
		Point tFrameOffset;

		tGameManager = corporationList.getGameManager ();
		tFrameOffset = tGameManager.getOffsetCorporationFrame ();
		trainPortfolio.clearCurrentRoutes ();
		trainRevenueFrame.operateTrains (tFrameOffset);
		setIsOperatingTrains (true);
	}

	/**
	 * Update all of the Train Indexes within the Company's portfolio
	 *
	 */
	public void updateTrainIndexes () {
		trainPortfolio.updateTrainIndexes ();
	}

	public void setIsOperatingTrains (boolean aIsOperatingTrains) {
		isOperatingTrains = aIsOperatingTrains;
	}

	public boolean isOperatingTrains () {
		return isOperatingTrains;
	}

	/**
	 * Clear the Specified Train from the Map
	 *
	 * @param aTrain the actual Train that should be cleared. Need to find index for the Train
	 */
	public void clearATrainFromMap (Coupon aTrain) {
		int tTrainCount, tTrainIndex;

		tTrainCount = getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (getTrain (tTrainIndex) == aTrain) {
				clearATrainFromMap (tTrainIndex, true);
			}
		}
	}

	/**
	 * Clear the Specified Train from the Map but not create an Action to send to others
	 *
	 * @param aTrainIndex The Train to clear from the Map, must Look for next index
	 *
	 */
	public void clearATrainFromMap (int aTrainIndex) {
		clearATrainFromMap (aTrainIndex, false);
	}

	/**
	 * Clear the Specified Train from the Map
	 *
	 * @param aTrainIndex The Train to clear from the Map, must Look for next index
	 * @param aCreateAction Flag to specify if Action should be created
	 *
	 */
	public void clearATrainFromMap (int aTrainIndex, boolean aCreateAction) {
		MapFrame tMapFrame;
		ClearATrainFromMapAction tClearATrainFromMapAction;
		String tOperatingRoundID;
		Train tTrain;

		tMapFrame = corporationList.getMapFrame ();
		tMapFrame.clearTrainFromMap (aTrainIndex + 1);
		tTrain = getTrain (aTrainIndex);
		trainRevenueFrame.clearRevenuesFromTrain (aTrainIndex, tTrain);
		if (aCreateAction) {
			tOperatingRoundID = getOperatingRoundID ();
			tClearATrainFromMapAction = new ClearATrainFromMapAction (ActorI.ActionStates.OperatingRound,
					tOperatingRoundID, this);
			tClearATrainFromMapAction.addClearATrainFromMapEffect (this, aTrainIndex);
			addAction (tClearATrainFromMapAction);
		}
	}

	/**
	 * Clear All trains from the Map
	 *
	 * @param aCreateAction Flag to specify if Action should be created
	 *
	 */
	@Override
	public void clearAllTrainsFromMap (boolean aCreateAction) {
		MapFrame tMapFrame;
		ClearAllRoutesAction tClearAllRoutesAction;
		String tOperatingRoundID;

		tMapFrame = corporationList.getMapFrame ();
		tMapFrame.clearAllTrainsFromMap ();

		if (aCreateAction) {
			tOperatingRoundID = getOperatingRoundID ();
			tClearAllRoutesAction = new ClearAllRoutesAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			tClearAllRoutesAction.addClearAllTrainsFromMapEffect (this);
			addAction (tClearAllRoutesAction);
		}

	}

	public void hideTrainRevenueFrame () {
		trainRevenueFrame.setVisible (false);
	}

	@Override
	public void skipBaseToken () {
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		boolean tStatusUpdated;
		String tOperatingRoundID;
		OperatingRound tOperatingRound;
		SkipBaseTokenAction tSkipBaseTokenAction;

		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.StationLaid);
		if (tStatusUpdated) {
			tNewStatus = status;
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tOperatingRound = corporationList.getOperatingRound ();
			tSkipBaseTokenAction = new SkipBaseTokenAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			tSkipBaseTokenAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatingRound.addAction (tSkipBaseTokenAction);
			setLastRevenue (thisRevenue);
			updateInfo ();
		} else {
			System.err.println ("Status has NOT been updated from " + status);
		}
	}

	@Override
	public void payNoDividend () {
		boolean tStatusUpdated;
		int tRevenueGenerated;
		String tOperatingRoundID;
		ShareCompany tShareCompany;
		Bank tBank;
		OperatingRound tOperatingRound;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		PayNoDividendAction tPayNoDividendAction;
		
		tRevenueGenerated = 0;
		if (thisRevenue != NO_REVENUE_GENERATED) {
			tRevenueGenerated = thisRevenue;
		}
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.HoldDividend);
		if (tStatusUpdated) {
			tNewStatus = status;
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tOperatingRound = corporationList.getOperatingRound ();
			tPayNoDividendAction = new PayNoDividendAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			if (tRevenueGenerated > 0) {
				tBank = corporationList.getBank ();
				// Pay the Dividend to the TrainCompany (this) and not the players
				tBank.transferCashTo (this, tRevenueGenerated);
				tPayNoDividendAction.addCashTransferEffect (tBank, this, tRevenueGenerated);
			}
			/*
			 * If a Share Company -- Adjust the Market Cell regardless of how much dividend
			 * is paid
			 */
			if (isAShareCompany ()) {
				tShareCompany = (ShareCompany) this;
				tShareCompany.payNoDividendAdjustment (tPayNoDividendAction);
			} 
			tPayNoDividendAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatingRound.addAction (tPayNoDividendAction);
			setLastRevenue (thisRevenue);
			updateInfo ();
			if (! isGovtRailway ()) {
				if (isAShareCompany ()) {
					tShareCompany = (ShareCompany) this;
					tShareCompany.handleCloseCorporation ();
				}
			}
		} else {
			System.err.println ("Status has NOT been updated from " + status);
		}
	}

	@Override
	public void payHalfDividend () {
		// TODO -- Implement Half Pay Dividend for 1870 GITHUB Issue GE # 169
	}
	
	@Override
	public void payFullDividend () {
		int tRevenueGenerated;
		PayFullDividendAction tPayFullDividendAction;
		String tOperatingRoundID;
		int tOperatingRoundPart2;
		ShareCompany tShareCompany;
		OperatingRound tOperatingRound;
		boolean tStatusUpdated;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;

		tRevenueGenerated = 0;
		if (thisRevenue != NO_REVENUE_GENERATED) {
			tRevenueGenerated = thisRevenue;
		}
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.FullDividend);
		if (tStatusUpdated) {
			tNewStatus = status;
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tOperatingRound = corporationList.getOperatingRound ();
			tOperatingRoundPart2 = tOperatingRound.getIDPart2 ();
			tPayFullDividendAction = new PayFullDividendAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			if (tRevenueGenerated > 0) {
				// Pay the Dividend to the Stock Holders not the TrainCompany (this)
				payShareHolders (tPayFullDividendAction, tOperatingRoundPart2);
			}
			// If a Share Company -- Adjust the Market Cell regardless of how much dividend
			// is paid
			if (isAShareCompany ()) {
				tShareCompany = (ShareCompany) this;
				tShareCompany.payFullDividendAdjustment (tPayFullDividendAction);
			}
			tPayFullDividendAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatingRound.addAction (tPayFullDividendAction);
			setLastRevenue (thisRevenue);
			updateInfo ();
		} else {
			System.err.println ("Status has NOT been updated from " + status);
		}
	}

	public void payShareHolders (PayFullDividendAction aPayFullDividendAction, int aOperatingRoundPart2) {
		ShareHolders tShareHolders;
		int tCertificateCount;
		int tCertificateIndex;
		int tShareHolderCount;
		int tShareHolderIndex;
		int tPercentage;
		int tDividendForShares;
		double tDividendFor1Percent;
		Certificate tCertificate;
		PortfolioHolderI tPortfolioHolder;
		TrainCompany tTrainCompany;
		Player tPlayer;
		CertificateHolderI tCertificateHolder;
		Bank tBank;

		tShareHolders = new ShareHolders ();
		tCertificateCount = corporationCertificates.getCertificateTotalCount ();
		for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
			tCertificate = corporationCertificates.getCertificate (tCertificateIndex);
			if (tCertificate != Certificate.NO_CERTIFICATE) {
				tCertificateHolder = tCertificate.getOwner ();
				tPortfolioHolder = tCertificateHolder.getPortfolioHolder ();
				tPercentage = tCertificate.getPercentage ();
				tShareHolders.addShareHolder (tPortfolioHolder, tPercentage);
			}
		}
		tBank = corporationList.getBank ();
		tDividendFor1Percent = getDividendFor1Percent ();
		tShareHolderCount = tShareHolders.getShareHolderCount ();
		if (tShareHolderCount > 0) {
			for (tShareHolderIndex = 0; tShareHolderIndex < tShareHolderCount; tShareHolderIndex++) {
				tPortfolioHolder = tShareHolders.getPortfolioHolder (tShareHolderIndex);
				tPercentage = tShareHolders.getPercentage (tShareHolderIndex);
				tDividendForShares = (int) (tDividendFor1Percent * tPercentage + 0.5);

				if (tPortfolioHolder.isAPlayer ()) {
					tPlayer = (Player) tPortfolioHolder;
					tBank.transferCashTo (tPlayer, tDividendForShares);
					tPlayer.addCashToDividends (tDividendForShares, aOperatingRoundPart2);
					tPlayer.updatePlayerJPanel ();
					aPayFullDividendAction.addPayCashDividendEffect (tBank, tPlayer, 
										tDividendForShares, aOperatingRoundPart2);
				} else if (tPortfolioHolder.isABankPool ()) {
					tBank.transferCashTo (this, tDividendForShares);
					aPayFullDividendAction.addCashTransferEffect (tBank, this, tDividendForShares);
				} else if (tPortfolioHolder.isATrainCompany ()) {
					tTrainCompany  = (TrainCompany) tPortfolioHolder;
					tBank.transferCashTo (tTrainCompany, tDividendForShares);
					tTrainCompany.addCashToDividends (tDividendForShares, aOperatingRoundPart2);
					tTrainCompany.updateCorporationFrame ();
					aPayFullDividendAction.addPayCashDividendEffect (tBank, tTrainCompany, 
										tDividendForShares, aOperatingRoundPart2);
				
				}
				// TODO: non-1830 Games Test if Portfolio Holder is Bank or Bank Pool -- and if
				// game states if these pay Corporation, pay those share there
			}
		}
	}
	
	@Override
	public boolean canBuyTrain () {
		boolean tCanBuyTrain;

		if (atTrainLimit ()) {
			tCanBuyTrain = false;
		} else if (noCash ()) {
			tCanBuyTrain = false;
		} else if ((status != ActorI.ActionStates.BoughtTrain) && 
					(status != ActorI.ActionStates.HoldDividend) && 
					(status != ActorI.ActionStates.HalfDividend) && 
					(status != ActorI.ActionStates.FullDividend)) {
			tCanBuyTrain = false;
		} else {
			tCanBuyTrain = true;
		}

		return tCanBuyTrain;
	}

	@Override
	public int getSelectedTrainCount () {
		Bank tBank;
		BankPool tBankPool;
		int tSelectedCount;

		tBank = getBank ();
		tBankPool = getBankPool ();
		tSelectedCount = tBank.getSelectedTrainCount () + tBankPool.getSelectedTrainCount ()
				+ super.getSelectedTrainCount ();

		return tSelectedCount;
	}

	@Override
	public String reasonForNoBuyTrain () {
		String tReasonForNoBuyTrain;
		int tSelectedTrainCount;

		tReasonForNoBuyTrain = NO_REASON;
		tSelectedTrainCount = getSelectedTrainCount ();
		if (dividendsHandled ()) {
			// If Dividend has been held, half paid, or full paid it is time to buy train
			if (noCash ()) {
				tReasonForNoBuyTrain = NO_MONEY;
			} else if (atTrainLimit ()) {
				tReasonForNoBuyTrain = "At Train Limit of " + getTrainLimit () + " for this Corporation/Phase.";
			}
			if (tSelectedTrainCount == 0) {
				tReasonForNoBuyTrain = NO_TRAIN_SELECTED;
			} else if (tSelectedTrainCount > 1) {
				tReasonForNoBuyTrain = SELECT_SINGLE_TRAIN;
			} else {
				tReasonForNoBuyTrain = GUI.NO_TOOL_TIP;
			}
		} else {
			tReasonForNoBuyTrain = CorporationFrame.DIVIDENDS_NOT_HANDLED;
		}

		return tReasonForNoBuyTrain;
	}

	@Override
	public String reasonForNoDividendOptions () {
		String tReason;

		tReason = commonReason ();
		if ((status == ActorI.ActionStates.NotOperated) || (status == ActorI.ActionStates.StartedOperations)
				|| (status == ActorI.ActionStates.TileLaid) || (status == ActorI.ActionStates.Tile2Laid)
				|| (status == ActorI.ActionStates.TileUpgraded) || (status == ActorI.ActionStates.TileAndStationLaid)
				|| (status == ActorI.ActionStates.StationLaid)) {
			tReason = REVENUES_NOT_GENERATED;
		} else if ((status == ActorI.ActionStates.HoldDividend) || (status == ActorI.ActionStates.HalfDividend)
				|| (status == ActorI.ActionStates.FullDividend) || (status == ActorI.ActionStates.BoughtTrain)
				|| (status == ActorI.ActionStates.Operated)) {
			tReason = DIVIDENDS_ALREADY_HANDLED;
		}

		return tReason;
	}

	@Override
	public String reasonForNoDividendPayment () {
		String tReason;

		tReason = NO_REASON;
		if ((status == ActorI.ActionStates.NotOperated) || (status == ActorI.ActionStates.StartedOperations)
				|| (status == ActorI.ActionStates.TileLaid) || (status == ActorI.ActionStates.Tile2Laid)
				|| (status == ActorI.ActionStates.TileUpgraded) || (status == ActorI.ActionStates.TileAndStationLaid)
				|| (status == ActorI.ActionStates.StationLaid)) {
			tReason = REVENUES_NOT_GENERATED;
		} else if ((status == ActorI.ActionStates.HoldDividend) || (status == ActorI.ActionStates.HalfDividend)
				|| (status == ActorI.ActionStates.FullDividend) || (status == ActorI.ActionStates.BoughtTrain)
				|| (status == ActorI.ActionStates.Operated)) {
			tReason = DIVIDENDS_ALREADY_HANDLED;
		} else if ((status == ActorI.ActionStates.OperatedTrain) && (thisRevenue <= 0)) {
			tReason = OPERATED_NO_REVENUE;
		}

		return tReason;
	}

	@Override
	public String reasonForNoTileLay () {
		String tReason;

		tReason = NO_REASON;
		if ((status == ActorI.ActionStates.TileLaid) || (status == ActorI.ActionStates.Tile2Laid)
				|| (status == ActorI.ActionStates.TileUpgraded) || (status == ActorI.ActionStates.TileAndStationLaid)) {
			tReason = "Already laid Tile this Turn";
		}
		if (NO_REASON.equals (tReason)) {
			tReason = commonReason ();
		}

		return tReason;
	}

	@Override
	public String reasonForNoTrainOperation () {
		String tReason;

		if (trainPortfolio.getTrainCount () == 0) {
			tReason = "There are no Trains in the Portfolio to Operate";
		} else {
			tReason = commonReason ();
		}

		return tReason;
	}

	@Override
	public boolean removeSelectedTrain () {
		return trainPortfolio.removeSelectedTrain ();
	}

	@Override
	public boolean removeTrain (String aName) {
		return trainPortfolio.removeTrain (aName);
	}

	public void setLastRevenue (int aRevenue) {
		lastRevenue = aRevenue;
		// If we have any Revenue then a Train has run, so MUST Buy a Train from now on
		if (lastRevenue > 0) {
			setMustBuyTrain (true);
		}
	}

	public void setThisRevenue (int aRevenue) {
		thisRevenue = aRevenue;
		// If we have any Revenue then a Train has run, so MUST Buy a Train from now on
		if (thisRevenue > 0) {
			setMustBuyTrain (true);
		}
	}

	public boolean anyTrainIsOperating () {
		return trainPortfolio.anyTrainIsOperating ();
	}

	public void setOperated () {
		if (!updateStatus (ActorI.ActionStates.Operated)) {
			System.err.println ("--> Failure to update State to Operated <--");
		}
	}

	@Override
	public void placeTileOnMapCell (MapCell aMapCell, Tile aTile, int aOrientation, Tile aPreviousTile,
			int aPreviousOrientation, String aPreviousTokens, String aPreviousBases) {
		RemoveTileAction tRemoveTileAction;
		LayTileAction tLayTileAction;
		String tOperatingRoundID;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		ActorI.ActionStates tTargetStatus;
		int tCostToLayTile;
		int tAllowedTileLays;
		Bank tBank;
		String tTokens;
		String tBases;

		tCurrentStatus = status;
		if (benefitInUse.changeState ()) {
			if (status == ActorI.ActionStates.TileLaid) {
				tTargetStatus = ActorI.ActionStates.Tile2Laid;
			} else if (status == ActorI.ActionStates.StationLaid) {
				tTargetStatus = ActorI.ActionStates.TileAndStationLaid;
			} else {
				tTargetStatus = ActorI.ActionStates.TileLaid;
			}
			updateStatus (tTargetStatus);
		}
		tNewStatus = status;
		tCostToLayTile = aMapCell.getCostToLayTile (aTile);
		tOperatingRoundID = corporationList.getOperatingRoundID ();
		tLayTileAction = new LayTileAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
		tAllowedTileLays = getAllowedTileLays ();
		System.out.println ("For the Company " + name + " (" + getAbbrev () + ") " + tAllowedTileLays + " Tile(s) can be laid.");
		if (aPreviousTile != Tile.NO_TILE) {
			tRemoveTileAction = new RemoveTileAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			tRemoveTileAction.addRemoveTileEffect (this, aMapCell, aPreviousTile, aPreviousOrientation, aPreviousTokens,
					aPreviousBases);
			tRemoveTileAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			addAction (tRemoveTileAction);
			tLayTileAction.setChainToPrevious (true);
		}
		tTokens = aTile.getPlacedTokens ();
		tBases = aTile.getCorporationBases ();
		tLayTileAction.addLayTileEffect (this, aMapCell, aTile, aOrientation, tTokens, tBases);
		addRemoveHomeEffect (tLayTileAction, aMapCell);
		if (tCurrentStatus != tNewStatus) {
			tLayTileAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
		}
		if (benefitInUse.isRealBenefit ()) {
			tLayTileAction.addBenefitUsedEffect (this, benefitInUse);
			if (!benefitInUse.isAExtraTilePlacement ()) {
				setTileLaid (tLayTileAction);
			}
		} else {
			setTileLaid (tLayTileAction);
		}
		if (tCostToLayTile > 0) {
			tBank = corporationList.getBank ();
			transferCashTo (tBank, tCostToLayTile);
			tLayTileAction.addCashTransferEffect (this, tBank, tCostToLayTile);
		}
		addAction (tLayTileAction);
		updateInfo ();
	}

	private void addRemoveHomeEffect (LayTileAction aLayTileAction, MapCell aSelectedMapCell) {
		MapCell tHomeMapCell1;
		MapCell tHomeMapCell2;
		Location tHomeLocation1;
		Location tHomeLocation2;
		
		if (isHomeTypeChoice ()) {
			tHomeMapCell1 = getHomeCity1 ();
			tHomeMapCell2 = getHomeCity2 ();
			tHomeLocation1 = getHomeLocation1 ();
			tHomeLocation2 = getHomeLocation2 ();
			if (aSelectedMapCell == tHomeMapCell1) {
				aLayTileAction.addRemoveHomeEffect (this, getAbbrev (), MapCell.NO_MAP_CELL,tHomeMapCell2, 
						Location.NO_LOC, tHomeLocation2);
			}
			if (aSelectedMapCell == tHomeMapCell2) {
				aLayTileAction.addRemoveHomeEffect (this, getAbbrev (), tHomeMapCell1, MapCell.NO_MAP_CELL, 
						tHomeLocation1, Location.NO_LOC);
			}
		}
	}

	private void setTileLaid (LayTileAction aLayTileAction) {
		setHasLaidTile (true);
		aLayTileAction.addSetHasLaidTileEffect (this, hasLaidTile);
	}

	@Override
	public void completeBenefitInUse (Corporation aOwningCompany) {
		benefitInUse.completeBenefitInUse (aOwningCompany);
	}

	public void trainsOperated (int aRevenue) {
		OperatedTrainsAction tOperatedTrainsAction;
		ActorI.ActionStates tCurrentStatus, tNewStatus;
		String tOperatingRoundID;
		OperatingRound tOperatingRound;
		int tTrainCount;

		tCurrentStatus = getStatus ();
		if (updateStatus (ActorI.ActionStates.OperatedTrain)) {
			tNewStatus = getStatus ();
			tTrainCount = getTrainCount ();
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tOperatedTrainsAction = new OperatedTrainsAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			tOperatedTrainsAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatedTrainsAction.addGeneratedRevenueEffect (this, aRevenue, tTrainCount);
			tOperatedTrainsAction.setChainToPrevious (true);
			tOperatingRound = corporationList.getOperatingRound ();
			tOperatingRound.addAction (tOperatedTrainsAction);
			tOperatingRound.updateRoundFrame ();
		} else {
			System.err.println ("--> Failure to update State to Operated Trains");
		}
		updateInfo ();
		setIsOperatingTrains (false);
	}

	@Override
	public void transferCashTo (CashHolderI aToHolder, int aAmount) {
		aToHolder.addCash (aAmount);
		addCash (-aAmount);
	}

	public Color translateColor (String aColorName) {
		int tRed;
		int tGreen;
		int tBlue;
		String rgbValues[] = new String [3];
		Color tColor;

		if (aColorName.equals ("Dark Green")) {
			tColor = new Color (34, 139, 34);
		} else if (aColorName.equals ("White")) {
			tColor = new Color (255, 255, 255);
		} else if (aColorName.equals ("Black")) {
			tColor = new Color (0, 0, 0);
		} else if (aColorName.equals ("Red")) {
			tColor = new Color (255, 0, 0);
		} else if (aColorName.equals ("Light Blue")) {
			tColor = new Color (173, 216, 230);
		} else if (aColorName.equals ("Yellow")) {
			tColor = new Color (255, 255, 0);
		} else if (aColorName.equals ("Deep Blue")) {
			tColor = new Color (0, 0, 255);
		} else if (aColorName.equals ("Light Green")) {
			tColor = new Color (124, 252, 0);
		} else if (aColorName.equals ("Orange")) {
			tColor = new Color (255, 165, 0);
		} else if (aColorName.equals ("Gold")) {
			tColor = new Color (255, 215, 0);
		} else if (aColorName.equals ("turquoise")) {
			tColor = new Color (64, 224, 208);
		} else if (aColorName.equals ("Yellow")) {
			tColor = Color.YELLOW;
		} else if (aColorName.equals ("Maroon")) {
			tColor = new Color (110, 39, 23);
		} else if (aColorName.equals ("Green")) {
			tColor = Color.GREEN;
		} else if (aColorName.equals ("Blue")) {
			tColor = Color.BLUE;
		} else {
			rgbValues = aColorName.split (",");
			tRed = Integer.valueOf (rgbValues [0]);
			tGreen = Integer.valueOf (rgbValues [1]);
			tBlue = Integer.valueOf (rgbValues [2]);
			tColor = new Color (tRed, tGreen, tBlue);
		}

		return tColor;
	}

	public void applyDiscount () {
		Coupon tSelectedTrainToUpgrade;
		Bank tBank;
		Train [] tAvailableTrains;
		String tDiscountAppliedTo = "";

		tBank = getBank ();
		tAvailableTrains = tBank.getAvailableTrains ();

		tSelectedTrainToUpgrade = getSelectedTrain ();
		for (Train tBankTrain : tAvailableTrains) {
			if (tBankTrain.canBeUpgradedFrom (tSelectedTrainToUpgrade.getName ())) {
				if (!(tDiscountAppliedTo.equals (tBankTrain.getName ()))) {
					tDiscountAppliedTo = tBankTrain.getName ();
					tBankTrain.applyDiscount (tSelectedTrainToUpgrade);
					corporationFrame.updateBankJPanel ();
				}
			}
		}
	}

	public boolean removeAllDiscounts () {
		Bank tBank;
		Train [] tAvailableTrains;
		boolean tADiscountRemoved = false;

		tBank = getBank ();
		tAvailableTrains = tBank.getAvailableTrains ();
		for (Train tBankTrain : tAvailableTrains) {
			tADiscountRemoved |= tBankTrain.removeDiscount ();
		}
		if (tADiscountRemoved) {
			corporationFrame.updateBankJPanel ();
		}

		return tADiscountRemoved;
	}

	public void clearAllTrainSelections () {
		Bank tBank;
		BankPool tBankPool;

		trainPortfolio.clearAllTrainSelections ();
		tBank = getBank ();
		tBank.clearSelections ();
		tBankPool = getBankPool ();
		tBankPool.clearSelections ();
	}

	public void setQueryOffer (QueryOffer aQueryOffer) {
		queryOffer = aQueryOffer;
	}

	public QueryOffer getQueryOffer () {
		return queryOffer;
	}

	public void handleRejectOffer () {
		corporationList.clearTrainSelections ();
		updateInfo ();
		queryOffer.setStatus (QueryOffer.REJECTED);
	}

	public void setAcceptOffer () {
		queryOffer.setStatus (QueryOffer.ACCEPTED);
	}

	public void doFinalTrainBuySteps (TrainCompany aOwningTrainCompany, Train aTrain, BuyTrainAction aBuyTrainAction) {
		ActorI.ActionStates tCurrentCorporationStatus;
		ActorI.ActionStates tNewCorporationStatus;
		TrainPortfolio tCompanyPortfolio, tOwningPortfolio;

		tCompanyPortfolio = getTrainPortfolio ();
		tOwningPortfolio = aOwningTrainCompany.getTrainPortfolio ();
		aTrain.clearCurrentRoute ();
		aTrain.clearPreviousRoute ();
		tCompanyPortfolio.addTrain (aTrain);
		tOwningPortfolio.removeSelectedTrain ();
		tCompanyPortfolio.clearSelections ();
		tOwningPortfolio.clearSelections ();
		tCurrentCorporationStatus = getStatus ();
		updateStatus (ActorI.ActionStates.BoughtTrain);
		tNewCorporationStatus = getStatus ();
		aBuyTrainAction.addTransferTrainEffect (aOwningTrainCompany, aTrain, this);
		if (tCurrentCorporationStatus != tNewCorporationStatus) {
			aBuyTrainAction.addChangeCorporationStatusEffect (this, tCurrentCorporationStatus, tNewCorporationStatus);
		}
		addAction (aBuyTrainAction);
	}

	public int getMaxTrainSize () {
		int tMaxTrainSize;

		tMaxTrainSize = trainPortfolio.getMaxTrainSize ();

		return tMaxTrainSize;
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		int tTrainCount;

		tTrainCount = trainPortfolio.getTrainCount ();
		if (tTrainCount > 0) {
			trainPortfolio.fixLoadedRoutes (aMapFrame);
		}
	}

//	public boolean startRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation,
//	Location aEndLocation, String aRoundID, int aPhase, TrainCompany aTrainCompany, TrainRevenueFrame aTrainRevenueFrame) {

	public boolean startRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation) {
		boolean tRouteStarted = false;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;

		showTrainRevenueFrameForOthers (aTrainIndex);
		tRoundID = corporationList.getOperatingRoundID ();
		tPhaseInfo = corporationList.getCurrentPhaseInfo ();
		tPhase = tPhaseInfo.getName ();
		tRouteStarted = trainPortfolio.startRouteInformation (aTrainIndex, aMapCell, aStartLocation, aEndLocation,
				tRoundID, tPhase, this, trainRevenueFrame);
		corporationList.repaintMapFrame ();

		return tRouteStarted;
	}

	public boolean extendRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation,
			Location aEndLocation) {
		boolean tRouteExtended = false;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;

		showTrainRevenueFrameForOthers (aTrainIndex);
		tRoundID = corporationList.getOperatingRoundID ();
		tPhaseInfo = corporationList.getCurrentPhaseInfo ();
		tPhase = tPhaseInfo.getName ();
		tRouteExtended = trainPortfolio.extendRouteInformation (aTrainIndex, aMapCell, aStartLocation, aEndLocation,
				tRoundID, tPhase, this, trainRevenueFrame);
		corporationList.repaintMapFrame ();

		return tRouteExtended;
	}

	public boolean removeRouteSegment (int aTrainIndex, MapCell aMapCell, int aSegmentIndex) {
		Train tTrain;
		RouteInformation tRouteInformation;
		boolean tRouteSegmentRemoved;
		
		tTrain = getTrain (aTrainIndex);
		tRouteInformation = tTrain.getCurrentRouteInformation ();
		tRouteSegmentRemoved = tRouteInformation.removeSegment (aSegmentIndex);
		corporationList.repaintMapFrame ();

		return tRouteSegmentRemoved;
	}
	
	public void showTrainRevenueFrameForOthers (int aTrainIndex) {
		Point tFrameOffset;
		GameManager tGameManager;

		tGameManager = corporationList.getGameManager ();
		tFrameOffset = tGameManager.getOffsetRoundFrame ();
		if (! trainRevenueFrame.isVisible ()) {
			trainRevenueFrame.setRevenueValues (this);
			trainRevenueFrame.setLocation (tFrameOffset);
		}
		trainRevenueFrame.updateInfo ();
		trainRevenueFrame.disableAll ();
		trainRevenueFrame.setVisible (true);
	}

	public boolean setNewEndPoint (int aTrainIndex, MapCell aMapCell, Location aStartLocation, Location aEndLocation) {
		boolean tNewEndPointSet = false;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;

		showTrainRevenueFrameForOthers (aTrainIndex);
		tRoundID = corporationList.getOperatingRoundID ();
		tPhaseInfo = corporationList.getCurrentPhaseInfo ();
		tPhase = tPhaseInfo.getName ();
		tNewEndPointSet = trainPortfolio.setNewEndPoint (aTrainIndex, aMapCell, aStartLocation, aEndLocation, tRoundID,
				tPhase, this, trainRevenueFrame);
		corporationList.repaintMapFrame ();

		return tNewEndPointSet;
	}

	public void closeTrainRevenueFrame () {
		// Need to clear the Frame Setup Flag for the Next Company Operating to be able
		// to update.
		trainRevenueFrame.setFrameSetup (false);
		trainRevenueFrame.setVisible (false);
	}

	@Override
	public void fillCorporationTrains (ButtonsInfoFrame aButtonsInfoFrame) {
		Corporation tCorporation;
		int tCorpCount, tCorpIndex;
		TrainPortfolio tTrainPortfolio;
		TrainCompany tTrainCompany;

		tCorpCount = corporationList.getCorporationCount ();
		if (tCorpCount > 0) {
			for (tCorpIndex = 0; tCorpIndex < tCorpCount; tCorpIndex++) {
				tCorporation = corporationList.getCorporation (tCorpIndex);
				// Only add Trains if a different corporation
				if (tCorporation.getID () != getID ()) {
					// Don't look in Corporations that are closed
					if (!tCorporation.isClosed ()) {
						// Only a Train Company will have a train for Purchase
						if (tCorporation.isATrainCompany ()) {
							// Only if a Train Company has at least one Train
							if (tCorporation.getTrainCount () > 0) {
								tTrainCompany = (TrainCompany) tCorporation;
								tTrainPortfolio = tTrainCompany.getTrainPortfolio ();
								aButtonsInfoFrame.fillWithCheckBoxes (tTrainPortfolio);
							}
						}
					}
				}
			}
		}
	}

	public boolean forceBuyEnoughCash () {
		boolean tForceBuyEnoughCash;

		tForceBuyEnoughCash = true;
		if (forceBuyCouponFrame != null) {
			tForceBuyEnoughCash = forceBuyCouponFrame.haveEnoughCash ();
		}

		return tForceBuyEnoughCash;
	}
	
	public boolean hasFloated () {
		return false;
	}
	
	public void setDestinationCapitalizationLevel () {
		
	}
}
