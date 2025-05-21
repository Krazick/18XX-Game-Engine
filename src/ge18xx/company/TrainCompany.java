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
import ge18xx.center.Revenue;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.Capitalization;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.CashHolderI;
import ge18xx.player.Player;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.ShareHolders;
import ge18xx.round.FormationRound;
import ge18xx.round.OperatingRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BorrowTrainAction;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.CashTransferAction;
import ge18xx.round.action.ChangeMarketCellAction;
import ge18xx.round.action.ClearATrainFromMapAction;
import ge18xx.round.action.ClearAllRoutesAction;
import ge18xx.round.action.FloatCompanyAction;
import ge18xx.round.action.LayTileAction;
import ge18xx.round.action.OperatedTrainsAction;
import ge18xx.round.action.PayFullDividendAction;
import ge18xx.round.action.PayNoDividendAction;
import ge18xx.round.action.PreparedAction;
import ge18xx.round.action.PreparedCorporationAction;
import ge18xx.round.action.RemoveTileAction;
import ge18xx.round.action.SkipBaseTokenAction;
import ge18xx.round.action.effects.Effect;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.train.TrainRevenueFrame;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

public abstract class TrainCompany extends Corporation implements CashHolderI, TrainHolderI {
	public static final ElementName EN_TRAIN_COMPANY = new ElementName ("TrainCompany");
	public static final AttributeName AN_TREASURY = new AttributeName ("treasury");
	public static final AttributeName AN_VALUE = new AttributeName ("value");
	public static final AttributeName AN_PREVIOUS_REVENUE = new AttributeName ("previousRevenue");
	public static final AttributeName AN_THIS_REVENUE = new AttributeName ("thisRevenue");
	public static final AttributeName AN_CLOSE_ON_TRAIN_PURCHASE = new AttributeName ("closeOnTrainPurchase");
	public static final AttributeName AN_BG_COLOR = new AttributeName ("bgColor");
	public static final AttributeName AN_FG_COLOR = new AttributeName ("fgColor");
	public static final AttributeName AN_HOME_COLOR = new AttributeName ("homeColor");
	public static final AttributeName AN_COST = new AttributeName ("cost");
	public static final AttributeName AN_MUST_BUY_TRAIN = new AttributeName ("mustBuyTrain");
	public static final AttributeName AN_MUST_PAY_FULL_PRICE = new AttributeName ("mustPayFullPrice");
	public static final AttributeName AN_GOVT_RAILWAY = new AttributeName ("govtRailway");
	public static final AttributeName AN_CAN_BORROW_TRAIN = new AttributeName ("canBorrowTrain");
	public static final AttributeName AN_ONLY_PERMANENT_TRAIN = new AttributeName ("onlyPermanentTrain");
	public static final TrainCompany NO_TRAIN_COMPANY = null;
	public static final String LAST_TRAIN_BOUGHT = "LAST TRAIN BOUGHT";
	public static final String DIVIDENDS_HANDLED = "DIVIDENDS HAVE BEEN HANDLED";
	public static final String BUY_LABEL = "Buy";
	public static final String NO_MONEY = "No money in the Treasury.";
	public static final String REVENUES_NOT_GENERATED = "Train Revenues have not been generated yet.";
	public static final String DIVIDENDS_ALREADY_HANDLED = "Dividend Payment already completed for the turn.";
	public static final String NO_TRAIN_SELECTED = "No train has been selected to be bought.";
	public static final String SELECT_SINGLE_TRAIN = "Must select a single Train to be bought.";
	public static final String OPERATED_NO_REVENUE = "Train Operated but no Revenue has been generated.";
	public static final int NO_REVENUE_GENERATED = 0;
	public static final int NO_COST = 0;
	public static final int NO_CASH = 0;
	public static final int INFINITE_PRICE = 99999;
	public static final float LEFT_ALIGNMENT = 0.0f;
	ArrayList<License> licenses;
	TrainPortfolio trainPortfolio;
	TrainRevenueFrame trainRevenueFrame;
	ForceBuyCouponFrame forceBuyCouponFrame;
	QueryOffer queryOffer;
	int closeOnTrainPurchase;
	int treasury;
	int thisRevenue;
	int previousRevenue;
	int value;
	boolean onlyPermanentTrain;
	boolean canBorrowTrain;
	boolean mustPayFullPrice;
	boolean mustBuyTrain;
	boolean hasLaidTile;
	boolean isOperatingTrains;
	String bgColorName;
	String fgColorName;
	String homeColorName;
	Color bgColor;
	Color fgColor;
	Color homeColor;

	public TrainCompany (int aID, String aName) {
		this (aID, aName, Corporation.NO_ABBREV, Color.white, Color.black, MapCell.NO_MAP_CELL, Location.NO_LOC,
				NO_COST, ActorI.ActionStates.Unowned, false);
	}

	public TrainCompany (int aID, String aName, String aAbbrev, Color aBgColor, Color aFgColor, 
			MapCell aHomeCity1, Location aHomeLocation1, int aCost, ActorI.ActionStates aState, 
			boolean aGovtRailway) {
		this (aID, aName, aAbbrev, aBgColor, aFgColor, aHomeCity1, aHomeLocation1, 
				MapCell.NO_MAP_CELL, Location.NO_LOC, aCost, aState, aGovtRailway);
	}

	public TrainCompany (int aID, String aName, String aAbbrev, Color aBgColor, Color aFgColor, 
			MapCell aHomeCity1, Location aHomeLocation1, MapCell aHomeCity2, Location aHomeLocation2, 
			int aCost, ActorI.ActionStates aState, boolean aGovtRailway) {
		super (aID, aName, aAbbrev, aHomeCity1, aHomeLocation1, aHomeCity2, aHomeLocation2, aState, aGovtRailway);

		trainPortfolio = new TrainPortfolio (this);
		licenses = new ArrayList<License> ();
		bgColor = aBgColor;
		fgColor = aFgColor;
		setTreasury (NO_CASH);
		value = aCost;
		setThisRevenue (NO_REVENUE_GENERATED);
		setPreviousRevenue (NO_REVENUE_GENERATED);
		setIsOperatingTrains (false);
		if (aID != Corporation.NO_ID) {
			setupTrainRevenueFrame ();
			setCorporationFrame ();
		}
		setMustBuyTrain (false);
		setMustPayFullPrice (false);
		setCanBorrowTrain (false);
		setOnlyPermanentTrain (false);
		setForceBuyCouponFrame (ForceBuyCouponFrame.NO_FRAME);
	}

	public void addLicense (License aLicense) {
		licenses.add (aLicense);
	}
	
	public boolean hasLicense (License aLicense) {
		return hasLicense (aLicense.getName ());
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
	
	public String getLicenses () {
		String tLicenses;
		
		tLicenses = GUI.EMPTY_STRING;
		for (License tLicense : licenses) {
			if (!tLicenses.equals (GUI.EMPTY_STRING)) {
				tLicenses += GUI.COMMA_SPACE;
			}
			tLicenses += tLicense.getName ();
		}
		if (tLicenses.equals (GUI.EMPTY_STRING)) {
			tLicenses = " ";
		}
		
		return tLicenses;
	}
	
	public int getLicenseCount () {
		return licenses.size ();
	}
	
	public License getLicenseAt (int aIndex) {
		return licenses.get (aIndex);
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
		boolean tCanBorrowTrain;
		boolean tMustBuyTrain;
		boolean tMustPayFullPrice;
		boolean tGovtRailway;
		boolean tOnlyPermanentTrain;
		int tPreviousRevenue;
		int tThisRevenue;

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
		tPreviousRevenue = aChildNode.getThisIntAttribute (AN_PREVIOUS_REVENUE);
		tThisRevenue = aChildNode.getThisIntAttribute (AN_THIS_REVENUE);
		tGovtRailway = aChildNode.getThisBooleanAttribute (AN_GOVT_RAILWAY);
		setGovtRailway (tGovtRailway);
		tCanBorrowTrain = aChildNode.getThisBooleanAttribute (AN_CAN_BORROW_TRAIN);
		setCanBorrowTrain (tCanBorrowTrain);
		tOnlyPermanentTrain = aChildNode.getThisBooleanAttribute (AN_ONLY_PERMANENT_TRAIN);
		setOnlyPermanentTrain (tOnlyPermanentTrain);
		tMustBuyTrain = aChildNode.getThisBooleanAttribute (AN_MUST_BUY_TRAIN);
		setMustBuyTrain (tMustBuyTrain);
		tMustPayFullPrice = aChildNode.getThisBooleanAttribute (AN_MUST_PAY_FULL_PRICE);
		setMustPayFullPrice (tMustPayFullPrice);
		closeOnTrainPurchase = aChildNode.getThisIntAttribute (AN_CLOSE_ON_TRAIN_PURCHASE, NO_ID);
		
		setThisRevenue (tThisRevenue);
		setPreviousRevenue (tPreviousRevenue);
		setupTrainRevenueFrame ();
		setCorporationFrame ();
		setForceBuyCouponFrame (ForceBuyCouponFrame.NO_FRAME);
	}
	
	@Override
	public void setCorporationFrame () {
		String tFullTitle;
		GameManager tGameManager;
		boolean tIsNetworkGame;
		CorporationFrame tCorporationFrame;

		tGameManager = corporationList.getGameManager ();
		tIsNetworkGame = tGameManager.isNetworkGame ();
		tFullTitle = tGameManager.createFrameTitle ("Corporation");
		tCorporationFrame = new CorporationFrame (tFullTitle, this, tIsNetworkGame, tGameManager);
		setCorporationFrame (tCorporationFrame);
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

	public void floatCompany () {
		int tRowIndex;
		int tInitialTreasury;
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
		tFloatCompanyAction = new FloatCompanyAction (tOperatingRound.getRoundState (), 
				tOperatingRound.getID (), this);
		tFloatCompanyAction.addChangeCorporationStatusEffect (this, tOldState, tNewState);
		if (hasDestination ()) {
			handleCapitalization (tFloatCompanyAction);
		}
		tInitialTreasury = calculateStartingTreasury ();
		tFloatCompanyAction.addCashTransferEffect (tBank, this, tInitialTreasury);
		tFloatCompanyAction.setChainToPrevious (true);

		
		tBank.transferCashTo (this, tInitialTreasury);
		corporationList.addDataElement (treasury, tRowIndex, 9);
		corporationList.addDataElement (getStatusName (), tRowIndex, 3);
		corporationList.addAction (tFloatCompanyAction);
	}

	// Share Company will Override this method
	public void handleCapitalization (FloatCompanyAction aFloatCompanyAction) {
		
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
		ShareCompany tShareCompany;

		tPreviousStatus = getActionStatus ();
		updateStatus (ActorI.ActionStates.StartedOperations);
		tNewStatus = getActionStatus ();

		tOperatingRoundID = getOperatingRoundID ();
		tPreparedCorporationAction = new PreparedCorporationAction (ActorI.ActionStates.OperatingRound,
				tOperatingRoundID, this);
		tPreparedCorporationAction.setChainToPrevious (true);
		tPreparedCorporationAction.addChangeCorporationStatusEffect (this, tPreviousStatus, tNewStatus);
		updateThisRevenue (tPreparedCorporationAction);
		
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

	public void updateThisRevenue (PreparedCorporationAction tPreparedCorporationAction) {
		int tOldThisRevenue;
		int tTrainCount;
		
		tTrainCount = getTrainCount ();
		tOldThisRevenue = thisRevenue;
		setThisRevenue (NO_REVENUE_GENERATED);
		tPreparedCorporationAction.addGeneratedThisRevenueEffect (this, tOldThisRevenue, thisRevenue,
				tTrainCount);
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
		int tCurrentColumn;

		tCurrentColumn = aStartColumn;
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
		int tCurrentColumn;

		tCurrentColumn = aStartColumn;
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

		aXMLCorporationState.setAttribute (AN_PREVIOUS_REVENUE, getPreviousRevenue ());
		aXMLCorporationState.setAttribute (AN_THIS_REVENUE, getThisRevenue ());
		aXMLCorporationState.setAttribute (AN_MUST_BUY_TRAIN, mustBuyTrain ());
		aXMLCorporationState.setAttribute (AN_MUST_PAY_FULL_PRICE, mustPayFullPrice ());
		aXMLCorporationState.setAttribute (AN_CAN_BORROW_TRAIN, canBorrowTrain ());
		aXMLCorporationState.setAttribute (AN_ONLY_PERMANENT_TRAIN, onlyPermanentTrain ());
		if (! hasNoTrains ()) {
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

	public String getDestinationCityName () {
		return GUI.EMPTY_STRING;
	}
	
	public int getDestinationCapitalizationLevel () {
		return Capitalization.INCREMENTAL_0_MAX;
	}

	public int getCapitalizationLevel () {
		int tCapitalizationAmount;
		int tSharesSold;

		tSharesSold = getSharesSold ();
		tCapitalizationAmount = super.getGameCapitalizationLevel (tSharesSold);

		return tCapitalizationAmount;
	}

	public JLabel buildEscrowLabel () {
		JLabel tEscrowLabel;
		GameManager tGameManager;
		String tPrefix;
		int tEscrowAmount;
		int tCapitalizationLevel;

		tGameManager = getGameManager ();
		tPrefix = "Escrow: ";
		if (hasDestination ()) {
			if (hasReachedDestination ()) {
				tEscrowLabel = new JLabel (tPrefix + "All Paid");
			} else {
				tCapitalizationLevel = getDestinationCapitalizationLevel ();
				if (tCapitalizationLevel > Capitalization.INCREMENTAL_0_MAX) {
					tEscrowAmount = calculateEscrowWithheld ();
					if (tEscrowAmount > 0) {
						tEscrowLabel = new JLabel (tPrefix + Bank.formatCash (tEscrowAmount));
					} else if (tGameManager.getAlwaysShowEscrow ()) {
						if (hasReachedDestination ()) {
							tEscrowLabel = new JLabel (tPrefix + "All Paid");
						} else {
							tEscrowLabel = new JLabel (tPrefix + "No Escrow");
						}
					} else {
						tEscrowLabel = GUI.NO_LABEL;
					}
				} else {
					tEscrowLabel = GUI.NO_LABEL;
				}
			}
		} else {
			tEscrowLabel = GUI.NO_LABEL;
		}
		
		return tEscrowLabel;
	}
	
	public int calculateEscrowWithheld () {
		return 0;
	}
	
	@Override
	public boolean hasReachedDestination () {
		return false;
	}

	public String destinationMapCellID () {
		return GUI.EMPTY_STRING;
	}
	
	@Override
	public JPanel buildCorpInfoJPanel () {
		JPanel tCorpInfoJPanel;
		JLabel tCorpLabel;
		JLabel tPercentOwned;
		JLabel tEscrowLabel;
		JLabel tStatus;
		JLabel tTrainList;
		JLabel tThisRevenue;
		BankPool tBankPool;
		String tBankPoolText;
		
		tCorpInfoJPanel = new JPanel ();
		tCorpInfoJPanel.setLayout (new BoxLayout (tCorpInfoJPanel, BoxLayout.Y_AXIS));
		tCorpLabel = buildCorpNameLabel ();
		tCorpInfoJPanel.add (tCorpLabel);
		tStatus = new JLabel ("[" + getStatusName () + "]");
		tCorpInfoJPanel.add (tStatus);
		if (isActive ()) {
			tPercentOwned = new JLabel (buildPercentOwned ());
			tCorpInfoJPanel.add (tPercentOwned);
			tEscrowLabel = buildEscrowLabel ();
			if (tEscrowLabel != GUI.NO_LABEL) {
				tCorpInfoJPanel.add (tEscrowLabel);
			}
			if (! isAMinorCompany ()) {
				tBankPool = getBankPool ();
				tBankPoolText = "[" + getBankPoolPercentage () + "% in " + tBankPool.getName () + "]";
				addLabel (tCorpInfoJPanel, tBankPoolText);
			}			
			addLabel (tCorpInfoJPanel, "Prez: " + getPresidentName ());
			addLabel (tCorpInfoJPanel, "Treasury: ", treasury);
			if (canOperate () || didOperate ()) {
				tTrainList = new JLabel (trainPortfolio.getTrainList ());
				tCorpInfoJPanel.add (tTrainList);
				tThisRevenue = new JLabel ("This " + Revenue.LABEL + getFormattedThisRevenue ());
				tCorpInfoJPanel.add (tThisRevenue);
			}
		}

		return tCorpInfoJPanel;
	}

	public JLabel buildCorpNameLabel () {
		JLabel tCorpLabel;
		String tCorpAbbrev;
		
		tCorpAbbrev = getAbbrev ();
		if (isGovtRailway ()) {
			tCorpAbbrev += " [Gov't]";
		} else {
			if (hasDestination ()) {
				if (hasReachedDestination ()) {
					tCorpAbbrev += "*";
				} else {
					tCorpAbbrev += " (" + destinationMapCellID () + ")";
				}
			}
		}
		tCorpLabel = new JLabel (tCorpAbbrev);
		
		return tCorpLabel;
	}

	public Border setupBorder (boolean aSamePresident) {
		Border tPanelBorder;
		Border tBackgroundBorder;
		Border tOuterBorder;
		Border tRaisedBevel;
		Border tLoweredBevel;
		Border tBevelBorder1;
		Border tBevelBorder2;
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
			tOuterBorder = setupOuterBorder (bgColor);
			tPanelBorder = BorderFactory.createCompoundBorder (tOuterBorder, tBackgroundBorder);
		}

		return tPanelBorder;
	}

	private Border setupSamePrezBorder () {
		Border tOuterBorder;

		tOuterBorder = BorderFactory.createLineBorder (Color.CYAN, 2);

		return tOuterBorder;
	}

	@Override
	public Border setupBorder () {
		Border tCorpBorder;
		Border tOuterBorder;
		Border tInnerBorder;

		tOuterBorder = setupOuterBorder (bgColor);
		tInnerBorder = setupBackgroundBorder (2);
		tCorpBorder = BorderFactory.createCompoundBorder (tOuterBorder, tInnerBorder);

		return tCorpBorder;
	}

	public JPanel buildPortfolioTrainsJPanel (CorporationFrame aCorporationFrame, GameManager aGameManager,
			boolean aFullTrainPortfolio, Corporation aBuyingCorporation, int aTokenCount) {
		JPanel tTrainInfoJPanel;
		JPanel tCorpJPanel;
		Border tBorder;
		String tPresident;
		String tBuyingPresident;
		String tActionLabel;
		JLabel tEscrow;

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
		addLabel (tCorpJPanel, getAbbrev () + " " + buildPercentOwned ());
		addLabel (tCorpJPanel, "State: " + getStatusName ());
		addLabel (tCorpJPanel, "Treasury: " + Bank.formatCash (treasury));
		if (hasDestination ()) {
			tEscrow = buildEscrowLabel ();
			if (tEscrow != GUI.NO_LABEL) {
				addLabel (tCorpJPanel, tEscrow);
			}
		}

		addLabel (tCorpJPanel, "Tokens: " + aTokenCount);
		addLabel (tCorpJPanel, "Prez: " + tPresident);
		if (canOperate ()) {
			addLabel (tCorpJPanel, Revenue.LABEL + getFormattedThisRevenue ());
		}

		if (trainPortfolio != TrainPortfolio.NO_TRAIN_PORTFOLIO) {
			tTrainInfoJPanel = trainPortfolio.buildPortfolioJPanel (aCorporationFrame, this, aGameManager, 
					tActionLabel, aFullTrainPortfolio);
			tCorpJPanel.add (tTrainInfoJPanel);
		}

		return tCorpJPanel;
	}

	private void addLabel (JPanel aCorpJPanel, String aString, int aCash) {
		addLabel (aCorpJPanel, aString + Bank.formatCash (aCash));
	}

	private void addLabel (JPanel aCorpJPanel, String aString) {
		JLabel tLabel;

		tLabel = new JLabel (aString);
		aCorpJPanel.add (tLabel);
	}

	private void addLabel (JPanel aCorpJPanel, JLabel aLabel) {
		aCorpJPanel.add (aLabel);
	}
	
	public boolean hasAnyLicense () {
		boolean tHasAnyLicense;
		
		tHasAnyLicense = false;
		if (licenses != License.NO_LICENSES) {
			if (licenses.size () > 0) {
				tHasAnyLicense = true;
			}
		}
		
		return tHasAnyLicense;
	}
	
	public JPanel buildLicenseInfoPanel () {
		JPanel tLicensePanel;
		JLabel tLicenseLabel;
		
		tLicensePanel = GUI.NO_PANEL;
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
		JPanel tCompanyInfoPanel;
		GameManager tGameManager;
		JLabel tLabel;
		JLabel tBPPLabel;
		JLabel tDestination;
		JLabel tEscrowWithheld;
		String tBankPoolPercent;
		String tDestinationPrefix;

		tGameManager = corporationList.getGameManager ();
		if (trainPortfolio != TrainPortfolio.NO_TRAIN_PORTFOLIO) {
			tTrainPortfolioInfoJPanel = trainPortfolio.buildPortfolioJPanel (aItemListener, 
					this, tGameManager, null, TrainPortfolio.FULL_TRAIN_PORTFOLIO);
		} else {
			tTrainPortfolioInfoJPanel = new JPanel ();
			tLabel = new JLabel (">>NO TRAINS<<");
			tTrainPortfolioInfoJPanel.add (tLabel);
		}

		tPortfolioInfoJPanel = buildPortfolioJPanel (aItemListener, tGameManager);
		tCertPortfolioInfoJPanel = new JPanel ();

		tCompanyInfoPanel = new JPanel ();
		tCompanyInfoPanel.setLayout (new BoxLayout (tCompanyInfoPanel, BoxLayout.Y_AXIS));
		tCompanyInfoPanel.setAlignmentY (LEFT_ALIGNMENT);
		tBankPoolPercent = getBankPoolPercentage () + "% " + abbrev + " in Bank Pool";
		if (hasDestination ()) {
			if (hasReachedDestination ()) { 
				tDestinationPrefix = "Destination reached ";
			} else {
				tDestinationPrefix = "Destination: ";
			}
			tDestination = new JLabel (tDestinationPrefix + getDestinationCityName ());
			tCompanyInfoPanel.add (tDestination);
			tEscrowWithheld = buildEscrowLabel ();
			if (tEscrowWithheld != GUI.NO_LABEL) {
				tCompanyInfoPanel.add (tEscrowWithheld);
			}
		}
		tBPPLabel = new JLabel (tBankPoolPercent);
		tCompanyInfoPanel.add (tBPPLabel);
		
		tCertPortfolioInfoJPanel.add (tCompanyInfoPanel);
		tCertPortfolioInfoJPanel.add (tTrainPortfolioInfoJPanel);
		tCertPortfolioInfoJPanel.add (tPortfolioInfoJPanel);
		tLicenseInfoPanel = buildLicenseInfoPanel ();
		if (tLicenseInfoPanel != GUI.NO_PANEL) {
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
	public boolean onlyPermanentTrain () {
		return onlyPermanentTrain;
	}

	@Override
	public boolean canBorrowTrain () {
		return canBorrowTrain;
	}

	@Override
	public boolean mustBuyTrain () {
		return mustBuyTrain;
	}

	@Override
	public boolean mustPayFullPrice () {
		return mustPayFullPrice;
	}

	@Override
	public boolean hasNoTrains () {
		return (trainPortfolio.isEmpty ());
	}

	public boolean hasLaidTile () {
		// If the Company Status is one of these
		// TileLaid, Tile2Laid, TileUpgraded, TileAndStationLaid
		return hasLaidTile;
	}

	@Override
	public boolean canBorrowTrainNow () {
		boolean tCanBorrowTrainNow;
		
		tCanBorrowTrainNow = false;
		if (canBorrowTrain && hasNoTrains ()) {
			tCanBorrowTrainNow = true;
		}
		
		return tCanBorrowTrainNow;
	}
	
	@Override
	public boolean hasBorrowedTrain () {
		boolean tHasBorrowedTrain;
		
		tHasBorrowedTrain = trainPortfolio.hasBorrowedTrain ();
		
		return tHasBorrowedTrain;
	}
	
	@Override
	public boolean mustBuyTrainNow () {
		boolean tMustBuyTrainNow;

		if (mustBuyTrain && hasNoTrains ()) {
			if (isGovtRailway ()) {
				if (hasPermanentTrain ()) {
					tMustBuyTrainNow = false;
				} else if (canBuyPermanentTrain ()) {
					tMustBuyTrainNow = true;
				} else {
					tMustBuyTrainNow = false;
				}
			} else {
				tMustBuyTrainNow = true;
			}
		} else {
			tMustBuyTrainNow = false;
		}

		return tMustBuyTrainNow;
	}
	
	public boolean canBuyPermanentTrain () {
		boolean tCanBuyPermanentTrain;
		Coupon tTrain;
		int tPrice;
		
		tTrain = getCheapestPermanentBankTrain ();
		tPrice = tTrain.getPrice ();
		if (this.treasury >= tPrice) {
			tCanBuyPermanentTrain = true;
		} else {
			tCanBuyPermanentTrain = false;
		}
		
		return tCanBuyPermanentTrain;
	}
	
	public void setGovtRailway (boolean aGovtRailway) {
		govtRailway = aGovtRailway;
	}

	@Override
	public boolean isGovtRailway () {
		return govtRailway;
	}

	public void setOnlyPermanentTrain (boolean aOnlyPermanentTrain) {
		onlyPermanentTrain = aOnlyPermanentTrain;
	}
	
	public void setCanBorrowTrain (boolean aCanBorrowTrain) {
		canBorrowTrain = aCanBorrowTrain;
	}

	public void setMustPayFullPrice (boolean aMustPayFullPrice) {
		mustPayFullPrice = aMustPayFullPrice;
	}
	
	@Override
	public void setMustBuyTrain (boolean aMustBuyTrain) {
		mustBuyTrain = aMustBuyTrain;
	}

	public void setHasLaidTile (boolean aHasLaidTile) {
		hasLaidTile = aHasLaidTile;
	}
	
	@Override
	public Coupon getCheapestPermanentBankTrain () {
		Coupon tCheapestPermanentTrain;
		Coupon tBankPoolPermanentTrain;
		Coupon tBankPermanentTrain;
		int tBankPoolTrainCost;
		int tBankTrainCost;
		BankPool tBankPool;
		Bank tBank;

		tCheapestPermanentTrain = Train.NO_TRAIN;
		tBankPool = corporationList.getBankPool ();
		tBankPoolPermanentTrain = tBankPool.getCheapestPermanentTrain ();
		tBank = corporationList.getBank ();
		tBankPermanentTrain = tBank.getCheapestPermanentTrain ();
		if (tBankPoolPermanentTrain != Train.NO_TRAIN) {
			tBankPoolTrainCost = tBankPoolPermanentTrain.getPrice ();
		} else {
			tBankPoolTrainCost = INFINITE_PRICE;
		}
		if (tBankPermanentTrain != Train.NO_TRAIN) {
			tBankTrainCost = tBankPermanentTrain.getPrice ();
		} else {
			tBankTrainCost = INFINITE_PRICE;
		}
		// TODO: Determine if BankPool Train and BankTrain cost is the same, which train
		// to buy?
		// Provide choice where to buy from?
		if (tBankPoolTrainCost < tBankTrainCost) {
			tCheapestPermanentTrain = tBankPoolPermanentTrain;
		} else {
			tCheapestPermanentTrain = tBankPermanentTrain;
		}

		return tCheapestPermanentTrain;
	}

	@Override
	public Coupon getCheapestBankTrain () {
		Coupon tCheapestTrain;
		Coupon tBankPoolTrain;
		Coupon tBankTrain;
		int tBankPoolTrainCost;
		int tBankTrainCost;
		BankPool tBankPool;
		Bank tBank;

		tCheapestTrain = Train.NO_TRAIN;
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

		if (isSelectedTrainHolderTheBank ()) {
			tOperatingRoundID = getOperatingRoundID ();
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
		boolean tFirstTrainOfType;
		boolean tStatusUpdated;
		Bank tBank;
		BankPool tBankPool;
		GameManager tGameManager;

		tGameManager = getGameManager ();
		tTrain = getSelectedBankTrain ();
		tTrainHolder = getSelectedTrainHolder ();
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.BoughtTrain);
		tNewStatus = status;
		tCashHolder = tTrainHolder.getCashHolder ();
		aBuyTrainAction.addTransferTrainEffect (tTrainHolder, tTrain, this);
		aBuyTrainAction.addCashTransferEffect (this, tCashHolder, tTrain.getPrice ());
		tFirstTrainOfType = false;
		tNextAvailableTrain = Train.NO_TRAIN;
		if (!tTrainHolder.isABankPool ()) {
			if (tTrainHolder.isABank ()) {
				tBank = (Bank) tTrainHolder;
				tFirstTrainOfType = corporationList.isFirstTrainOfType (tTrain);
				tBank.makeTrainsAvailable (tTrain, aBuyTrainAction);
			}
		}
		if (tStatusUpdated) {
			aBuyTrainAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
		}
		if (closeOnTrainPurchase != NO_ID) {
			tGameManager.closeCompany (closeOnTrainPurchase, aBuyTrainAction);
		}

		addTrain (tTrain);
		transferCashTo (tCashHolder, tTrain.getPrice ());
		tTrainHolder.removeSelectedTrain ();
		if (tFirstTrainOfType) {
			corporationList.performPhaseChange (this, tTrain, aBuyTrainAction);
			handleTriggerClass (aBuyTrainAction);
		}
		if (tTrainHolder.isABankPool ()) {
			tBankPool = (BankPool) tTrainHolder;
			tNextAvailableTrain = tBankPool.getNextAvailableTrain ();
		} else if (tTrainHolder.isABank ()) {
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

	// if The Phase has a TriggerClass, it needs to be called
	public void handleTriggerClass (BuyTrainAction aBuyTrainAction) {
		TriggerClass tTriggerFormationClass;
		FormationRound tFormationRound;
		GameManager tGameManager;
		RoundManager tRoundManager;
		String tTriggerClassName;
		ShareCompany tOperatingCorporation;
		
		tTriggerClassName = getTriggerClassName ();
		if (hasTriggerClass (tTriggerClassName)) {
			// When a TriggerClass Exists need to capture the current Operating Company
			// so that after current action is completed, the Formation Round can start
			// and have which class, and from that which player actually triggered
			// the Formation Round.
			tGameManager = getGameManager ();
			tTriggerFormationClass = tGameManager.getTriggerFormation ();
			if (tTriggerFormationClass == TriggerClass.NO_TRIGGER_CLASS) {
				tRoundManager = tGameManager.getRoundManager ();
				tFormationRound = tRoundManager.getFormationRound ();
				tFormationRound.constructFormationClass (tTriggerClassName);
				tTriggerFormationClass = tFormationRound.getTriggerFormationClass ();
				tGameManager.setTriggerFormation (tTriggerFormationClass);
				aBuyTrainAction.addConstructTriggerClassEffect (this);
			}
			tOperatingCorporation = tGameManager.getOperatingShareCompany ();
			tTriggerFormationClass.setTriggeringShareCompany (tOperatingCorporation);
		}
	}
	
	public PreparedAction createPreparedFormationAction () {
		String tPreparedFormationActionXML;
		XMLDocument tXMLDocument;
		XMLDocument tAXMLDocument;
		XMLNode tXMLNode;
		PreparedAction tPreparedAction;
		GameManager tGameManager;
		String tXMLNodeName;
		PhaseInfo tCurrentPhaseInfo;
		PhaseManager tPhaseManager;

		tGameManager = getGameManager ();
		tPhaseManager = tGameManager.getPhaseManager ();
		tCurrentPhaseInfo = tPhaseManager.getCurrentPhaseInfo ();
		tPreparedFormationActionXML = tCurrentPhaseInfo.getPreparedActionXML ();
		tAXMLDocument = new XMLDocument ();
		tXMLDocument = tAXMLDocument.parseXMLString (tPreparedFormationActionXML);
		tPreparedAction = PreparedAction.NO_PREPARED_ACTION;
		if (tXMLDocument.validDocument ()) {
			tXMLNode = tXMLDocument.getDocumentNode ();
			tXMLNodeName = tXMLNode.getNodeName ();
			if (PreparedAction.EN_PREPARED_ACTION.equals (tXMLNodeName)) {
				tPreparedAction = new PreparedAction (tXMLNode, tGameManager);
				fillPlaceHolders (tGameManager, tPreparedAction);
			}
		}
		
		return tPreparedAction;
	}
	
	public void fillPlaceHolders (GameManager aGameManager, PreparedAction aPreparedAction) {
		Corporation tOperatingCorporation;
		Player tOperatingCorpPresident;
		Action tPreparedAction;
		Effect tEffect;
		int tEffectCount;
		int tEffectIndex;
		String tRoundID;
		
		tPreparedAction = aPreparedAction.getAction ();
		tRoundID = aGameManager.getOperatingRoundID ();
		tOperatingCorporation = aGameManager.getOperatingCompany ();
		tPreparedAction.setActor (tOperatingCorporation);
		tPreparedAction.setRoundID (tRoundID);
		aPreparedAction.setTriggeringActor (tOperatingCorporation);
		tOperatingCorpPresident = (Player) tOperatingCorporation.getPresident ();
		tEffectCount = tPreparedAction.getEffectCount ();
		for (tEffectIndex = 0; tEffectIndex < tEffectCount; tEffectIndex++) {
			tEffect = tPreparedAction.getEffect (tEffectIndex);
			tEffect.setActor (tOperatingCorpPresident);
		}
	}
	
	public String getTriggerClassName () {
		String tTriggerClass;
		PhaseInfo tPhaseInfo;
		
		tPhaseInfo = getCurrentPhaseInfo ();
		tTriggerClass = tPhaseInfo.getTriggerClass ();
		
		return tTriggerClass;
	}
	
	public boolean hasTriggerClass () {
		String tTriggerClass;
		
		tTriggerClass = getTriggerClassName ();

		return hasTriggerClass (tTriggerClass);
	}
	
	public boolean hasTriggerClass (String aTriggerClassName) {
		boolean tHasTriggerClass;
		
		if (aTriggerClassName != GUI.NULL_STRING) {
			tHasTriggerClass = true;
		} else {
			tHasTriggerClass = false;
		}
		
		return tHasTriggerClass;
	}
	
	@Override
	public boolean trainIsSelected () {
		boolean tTrainIsSelected;
		Coupon tTrain;
		TrainHolderI tTrainHolder;

		tTrainIsSelected = false;
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
		if (homeCity1 != MapCell.NO_MAP_CELL) {
			if (hasTwoBases ()) {
				if (homeCity2 != MapCell.NO_MAP_CELL) {
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
	public boolean baseTileHasTracks () {
		boolean tBaseTileHasTracks;
		
		tBaseTileHasTracks = false;
		if (homeCity1 != MapCell.NO_MAP_CELL) {
			if (hasTwoBases ()) {
				if (homeCity2 != MapCell.NO_MAP_CELL) {
					if (homeCity1.isTileOnCell () && homeCity2.isTileOnCell ()) {
						tBaseTileHasTracks = true;
					}
				}
			} else {
				tBaseTileHasTracks = homeCity1.isTileWithTrackOnCell ();		
			}
		}
		
		return tBaseTileHasTracks;
	}

	@Override
	public boolean canLayTile (int aTileLaysAllowed) {
		boolean tCanLayTile;

		tCanLayTile = false;
		if (aTileLaysAllowed > 1) {
			if ((status == ActorI.ActionStates.TileLaid) || 
				(status == ActorI.ActionStates.TileAndStationLaid) || 
				(status == ActorI.ActionStates.StartedOperations) || 
				(status == ActorI.ActionStates.StationLaid)) {
				tCanLayTile = true;
			}
		} else if ((status == ActorI.ActionStates.StartedOperations) || 
			(status == ActorI.ActionStates.StationLaid)) {
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
		if ((status == ActorI.ActionStates.StartedOperations) || 
			(status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.TilesLaid) || 
			(status == ActorI.ActionStates.TileUpgraded) ||
			(status == ActorI.ActionStates.TileAndStationLaid) ||
			(status == ActorI.ActionStates.TilesAndStationLaid) ||
			(status == ActorI.ActionStates.TileUpgradedStationLaid) ||
			(status == ActorI.ActionStates.StationLaid)) {
			if (trainPortfolio.getTrainCount () > 0) {
				tOperateTrains = true;
			} else if (canBorrowTrainNow ()) {
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
		if ((status == ActorI.ActionStates.HandledLoanInterest) || 
			dividendsHandled ())  {
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
		int tTrainLimit;
		int tTrainCount;
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
		if ((status == ActorI.ActionStates.Closed) || 
			(status == ActorI.ActionStates.Unformed) ||
			(status == ActorI.ActionStates.Unowned) || 
			(status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.HoldDividend) || 
			(status == ActorI.ActionStates.HalfDividend) ||
			(status == ActorI.ActionStates.FullDividend) || 
			(status == ActorI.ActionStates.BoughtTrain)) {
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
		if (previousRevenue > 0) {
			aXMLCorporationState.setAttribute (AN_PREVIOUS_REVENUE, previousRevenue);
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

		if (thisRevenue < 1) {
			tFormattedRevenue = trainRevenueFrame.formatRevenue (NO_REVENUE_GENERATED);

		} else {
			tFormattedRevenue = trainRevenueFrame.formatRevenue (thisRevenue);
		}

		return tFormattedRevenue;
	}

	@Override
	public String getFormattedPreviousRevenue () {
		String tFormattedRevenue;

		tFormattedRevenue = trainRevenueFrame.formatRevenue (previousRevenue);

		return tFormattedRevenue;
	}

	@Override
	public int getThisRevenue () {
		return thisRevenue;
	}

	@Override
	public int getPreviousRevenue () {
		return previousRevenue;
	}

	@Override
	public int getLocalSelectedTrainCount () {
		int tSelectedTrainCount;

		tSelectedTrainCount = trainPortfolio.getSelectedCount ();

		return tSelectedTrainCount;
	}

	@Override
	public TrainHolderI getLocalSelectedTrainHolder () {
		TrainHolderI tTrainHolder;
		Coupon tSelectedTrain;

		tTrainHolder = TrainHolderI.NO_TRAIN_HOLDER;
		tSelectedTrain = trainPortfolio.getSelectedTrain ();
		if (tSelectedTrain != Train.NO_TRAIN) {
			tTrainHolder = this;
		}

		return tTrainHolder;
	}

	public boolean isSelectedTrainHolderTheBank () {
		boolean tIsSelectedTrainHolder;
		TrainHolderI tTrainHolder;
		
		tTrainHolder = getSelectedTrainHolder ();
		if (tTrainHolder.isABank ()) {
			tIsSelectedTrainHolder = true;
		} else {
			tIsSelectedTrainHolder = false;
		}

		return tIsSelectedTrainHolder;
	}

	@Override
	public boolean isATrainCompany () {
		return true;
	}

	@Override
	public TrainHolderI getSelectedTrainHolder () {
		TrainHolderI tTrainHolder;
		GameManager tGameManager;
		
		tGameManager = getGameManager ();
		tTrainHolder = tGameManager.getSelectedTrainHolder ();
		
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
	 * Find the Train by the ID in the Train Portfolio, and return it
	 *
	 * @param aTrainID The ID of the Train to find
	 * @return The Train with the specified Train ID.
	 */
	public Train getTrainByID (int aTrainID) {
		return trainPortfolio.getTrainByID (aTrainID);
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

	public String getTrainList () {
		return trainPortfolio.getTrainList ();
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
	public int getTrainCount (String aName) {
		return trainPortfolio.getTrainCount (aName);
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
	public void loadStates (XMLNode aXMLNode) {
		int tThisRevenue;
		int tPreviousRevenue;
		boolean tMustBuyTrain;
		boolean tMustPayFullPrice;
		boolean tCanBorrowTrain;
		boolean tOnlyPermanentTrain;

		tPreviousRevenue = aXMLNode.getThisIntAttribute (AN_PREVIOUS_REVENUE);
		setPreviousRevenue (tPreviousRevenue);
		
		tThisRevenue = aXMLNode.getThisIntAttribute (AN_THIS_REVENUE);
		setThisRevenue (tThisRevenue);
		
		tMustBuyTrain = aXMLNode.getThisBooleanAttribute (AN_MUST_BUY_TRAIN);
		setMustBuyTrain (tMustBuyTrain);
		
		tMustPayFullPrice = aXMLNode.getThisBooleanAttribute (AN_MUST_PAY_FULL_PRICE);
		setMustPayFullPrice (tMustPayFullPrice);
		
		tCanBorrowTrain = aXMLNode.getThisBooleanAttribute (AN_CAN_BORROW_TRAIN);
		setCanBorrowTrain (tCanBorrowTrain);

		tOnlyPermanentTrain = aXMLNode.getThisBooleanAttribute (AN_ONLY_PERMANENT_TRAIN);
		setOnlyPermanentTrain (tOnlyPermanentTrain);
	}

	@Override
	public void loadStatus (XMLNode aXMLNode) {
		Bank tBank;
		int tCash;
		
		super.loadStatus (aXMLNode);
		
		addCash (-getCash ()); // Clear out any Cash here
		tCash = aXMLNode.getThisIntAttribute (AN_TREASURY);
		addCash (tCash);
		
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
		int tTrainCount;
		int tTrainIndex;

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
		trainRevenueFrame.hideFrame ();
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
			tOperatingRoundID = getOperatingRoundID ();
			tOperatingRound = corporationList.getOperatingRound ();
			tSkipBaseTokenAction = new SkipBaseTokenAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			tSkipBaseTokenAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatingRound.addAction (tSkipBaseTokenAction);
			setPreviousRevenue (thisRevenue);
			updateInfo ();
		} else {
			System.err.println ("Status has NOT been updated from " + status);
		}
	}

	@Override
	public void payNoDividend () {
		boolean tStatusUpdated;
		boolean tMovementStock;
		int tRevenueGenerated;
		int tOldPreviousRevenue;
		int tOldThisRevenue;
		int tTrainCount;
		String tOperatingRoundID;
		ShareCompany tShareCompany;
		Bank tBank;
		OperatingRound tOperatingRound;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		PayNoDividendAction tPayNoDividendAction;
		
		tRevenueGenerated = thisRevenue;
		tOldThisRevenue = thisRevenue;
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.HoldDividend);
		if (tStatusUpdated) {
			tNewStatus = status;
			tOperatingRoundID = getOperatingRoundID ();
			tOperatingRound = corporationList.getOperatingRound ();
			tPayNoDividendAction = new PayNoDividendAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			if (tRevenueGenerated > 0) {
				tBank = corporationList.getBank ();
				// Pay the Dividend to the TrainCompany (this) and not the players
				tBank.transferCashTo (this, tRevenueGenerated);
				tPayNoDividendAction.addCashTransferEffect (tBank, this, tRevenueGenerated);
			} else {
				if (tRevenueGenerated == NO_REVENUE_GENERATED) {
					tRevenueGenerated = 0;
				}
			}
			tOldPreviousRevenue = getPreviousRevenue ();
			tTrainCount = getTrainCount ();
			tPayNoDividendAction.addUpdatePreviousRevenueEffect (this, tOldPreviousRevenue, tRevenueGenerated);

			tPayNoDividendAction.addGeneratedThisRevenueEffect (this, tOldThisRevenue, tRevenueGenerated, tTrainCount);
			tMovementStock = true;
			if (isGovtRailway ()) {
				returnBorrowedTrain (tPayNoDividendAction);
				if (!hasPermanentTrain ()) {
					tMovementStock = false;
				}
			}

			/*
			 * If a Share Company -- Adjust the Market Cell regardless of how much dividend
			 * is paid
			 */
			if (isAShareCompany ()) {
				tShareCompany = (ShareCompany) this;
				if (tMovementStock) {
					tShareCompany.payNoDividendAdjustment (tPayNoDividendAction);
				}
			} 
			tPayNoDividendAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatingRound.addAction (tPayNoDividendAction);
			setPreviousRevenue (thisRevenue);
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
		PayFullDividendAction tPayFullDividendAction;
		String tOperatingRoundID;
		
		if (isGovtRailway ()) {
			tOperatingRoundID = getOperatingRoundID ();
			tPayFullDividendAction = new PayFullDividendAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			returnBorrowedTrain (tPayFullDividendAction);
		}
		// TODO -- Implement Half Pay Dividend for 1870 GITHUB Issue GE # 169
	}
	
	@Override
	public void payFullDividend () {
		int tRevenueGenerated;
		boolean tStatusUpdated;
		String tOperatingRoundID;
		ShareCompany tShareCompany;
		OperatingRound tOperatingRound;
		PayFullDividendAction tPayFullDividendAction;
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
			tOperatingRoundID = getOperatingRoundID ();
			tOperatingRound = corporationList.getOperatingRound ();
			tPayFullDividendAction = new PayFullDividendAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
					this);
			if (tRevenueGenerated > 0) {
				// Pay the Dividend to the Stock Holders not the TrainCompany (this)
				payShareHolders (tPayFullDividendAction, tOperatingRoundID);
			}
			if (isGovtRailway ()) {
				returnBorrowedTrain (tPayFullDividendAction);
			}
			// If a Share Company -- Adjust the Market Cell regardless of how much dividend
			// is paid
			if (isAShareCompany ()) {
				tShareCompany = (ShareCompany) this;
				tShareCompany.payFullDividendAdjustment (tPayFullDividendAction);
			}
			tPayFullDividendAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tPayFullDividendAction.addUpdatePreviousRevenueEffect (this, previousRevenue, thisRevenue);
			setPreviousRevenue (thisRevenue);
			tOperatingRound.addAction (tPayFullDividendAction);
			updateInfo ();
		} else {
			System.err.println ("Status has NOT been updated from " + status);
		}
	}

	public void payShareHolders (PayFullDividendAction aPayFullDividendAction, String tOperatingRoundID) {
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
		MapCell tSelectedMapCell;

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
					tPlayer.addCashToDividends (tDividendForShares, tOperatingRoundID);
					tPlayer.updatePlayerJPanel ();
					aPayFullDividendAction.addPayCashDividendEffect (tBank, tPlayer, 
										tDividendForShares, tOperatingRoundID);
				} else if (tPortfolioHolder.isABankPool ()) {
					tBank.transferCashTo (this, tDividendForShares);
					aPayFullDividendAction.addCashTransferEffect (tBank, this, tDividendForShares);
				} else if (tPortfolioHolder.isATrainCompany ()) {
					tTrainCompany  = (TrainCompany) tPortfolioHolder;
					tBank.transferCashTo (tTrainCompany, tDividendForShares);
					tTrainCompany.addCashToDividends (tDividendForShares, tOperatingRoundID);
					tSelectedMapCell = MapCell.NO_MAP_CELL;
					tTrainCompany.updateCorporationFrame (tSelectedMapCell);
					aPayFullDividendAction.addPayCashDividendEffect (tBank, tTrainCompany, 
										tDividendForShares, tOperatingRoundID);
				
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

	public int getSelectedTrainCountA () {
		GameManager tGameManager;
		int tSelectedCount;
		
		tGameManager = getGameManager ();
		tSelectedCount = tGameManager.getSelectedTrainCount ();
		
		return tSelectedCount;
	}	
	
	@Override
	public int getSelectedTrainCount () {
		Bank tBank;
		BankPool tBankPool;
		int tSelectedCount;

		tBank = getBank ();
		tBankPool = getBankPool ();
		if ((tBank != Bank.NO_BANK) && (tBankPool != BankPool.NO_BANK_POOL)) {
			tSelectedCount = tBank.getSelectedTrainCount () + tBankPool.getSelectedTrainCount ()
				+ super.getSelectedTrainCount ();
		} else {
			tSelectedCount = 0;
		}
		
		return tSelectedCount;
	}

	@Override
	public String reasonForNoBuyTrain () {
		String tReasonForNoBuyTrain;
		int tSelectedTrainCount;
		GameManager tGameManager;

		tReasonForNoBuyTrain = NO_REASON;
		tGameManager = getGameManager ();
		tSelectedTrainCount = tGameManager.getSelectedTrainCount ();
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
	public String reasonForNoDividendPayment () {
		String tReason;

		tReason = NO_REASON;
		if ((status == ActorI.ActionStates.NotOperated) || 
				(status == ActorI.ActionStates.StartedOperations) || 
				(status == ActorI.ActionStates.TileLaid) || 
				(status == ActorI.ActionStates.TilesLaid) || 
				(status == ActorI.ActionStates.TileUpgraded) || 
				(status == ActorI.ActionStates.TileAndStationLaid) || 
				(status == ActorI.ActionStates.TilesAndStationLaid) || 
				(status == ActorI.ActionStates.TileUpgradedStationLaid) || 
				(status == ActorI.ActionStates.StationLaid)) {
			tReason = REVENUES_NOT_GENERATED;
		} else if ((status == ActorI.ActionStates.HoldDividend) || 
				(status == ActorI.ActionStates.HalfDividend) || 
				(status == ActorI.ActionStates.FullDividend) || 
				(status == ActorI.ActionStates.BoughtTrain) || 
				(status == ActorI.ActionStates.Operated)) {
			tReason = DIVIDENDS_ALREADY_HANDLED;
		} else if ((status == ActorI.ActionStates.OperatedTrain) && 
				(thisRevenue <= 0)) {
			tReason = OPERATED_NO_REVENUE;
		}

		return tReason;
	}

	@Override
	public String reasonForNoTileLay () {
		String tReason;

		tReason = NO_REASON;
		if ((status == ActorI.ActionStates.TileLaid) || 
				(status == ActorI.ActionStates.TilesLaid) || 
				(status == ActorI.ActionStates.TileUpgraded) || 
				(status == ActorI.ActionStates.TileAndStationLaid) || 
				(status == ActorI.ActionStates.TilesAndStationLaid) || 
				(status == ActorI.ActionStates.TileUpgradedStationLaid)) {
			tReason = "Already laid Tile this Turn.";
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
			tReason = "There are no Trains in the Portfolio to Operate.";
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

	public void setPreviousRevenue (int aPreviousRevenue) {
		previousRevenue = aPreviousRevenue;
		// If we have any Revenue then a Train has run, so MUST Buy a Train from now on
		if (previousRevenue > 0) {
			setMustBuyTrain (true);
		}
	}

	public void setThisRevenue (int aRevenue) {
		thisRevenue = aRevenue;
		// If we have any Revenue then a Train has run, so MUST Buy a Train from now on
		// This only applies to Share/Government Corporations. Minors, Coals do not 
		// require trains
		
		if (isAShareCompany ()) {
			if (thisRevenue > 0) {
				setMustBuyTrain (true);
			}
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
		String tNewTileTokens;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		int tCostToLayTile;
		Bank tBank;
		GameManager tGameManager;

		tCurrentStatus = status;
		if (! benefitInUse.isRealBenefit ()) {
			updateStatusWithTile (aPreviousTile);
		} else if (benefitInUse.changeState ()) {
			updateStatusWithTile (aPreviousTile);
		}
		tNewStatus = status;
		tGameManager = getGameManager ();
		aMapCell.applyBases (aPreviousBases, tGameManager);
		tNewTileTokens = aTile.getPlacedTokens ();
		tOperatingRoundID = getOperatingRoundID ();
		tLayTileAction = new LayTileAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
		if (aPreviousTile != Tile.NO_TILE) {
			tRemoveTileAction = new RemoveTileAction (ActorI.ActionStates.OperatingRound, 
					tOperatingRoundID, this);
			tRemoveTileAction.addRemoveTileEffect (this, aMapCell, aPreviousTile, aPreviousOrientation,
					aPreviousTokens, aPreviousBases);
			tRemoveTileAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			addAction (tRemoveTileAction);
			tLayTileAction.setChainToPrevious (true);
		}
		tLayTileAction.addLayTileEffect (this, aMapCell, aTile, aOrientation, 
					aPreviousTokens, aPreviousBases, tNewTileTokens);
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
		tCostToLayTile = getCostForTile (aTile, aMapCell);
		if (tCostToLayTile > 0) {
			tBank = corporationList.getBank ();
			transferCashTo (tBank, tCostToLayTile);
			tLayTileAction.addCashTransferEffect (this, tBank, tCostToLayTile);
		}
		addAction (tLayTileAction);
		updateInfo ();
	}

	public void updateStatusWithTile (Tile aPreviousTile) {
		ActorI.ActionStates tTargetStatus;
		
		if (aPreviousTile == Tile.NO_TILE) {
			if (status == ActorI.ActionStates.TileLaid) {
				tTargetStatus = ActorI.ActionStates.TilesLaid;
			} else if (status == ActorI.ActionStates.StationLaid) {
				tTargetStatus = ActorI.ActionStates.TileAndStationLaid;
			} else if (status == ActorI.ActionStates.TileAndStationLaid) {
				tTargetStatus = ActorI.ActionStates.TilesAndStationLaid;
			} else if (status == ActorI.ActionStates.StartedOperations) {
				tTargetStatus = ActorI.ActionStates.TileLaid;
			} else {
				tTargetStatus = status;
			}
		} else {
			if (status == ActorI.ActionStates.StartedOperations) {
				tTargetStatus = ActorI.ActionStates.TileUpgraded;
			} else if (status == ActorI.ActionStates.StationLaid) {
				tTargetStatus = ActorI.ActionStates.TileUpgradedStationLaid;
			} else {
				tTargetStatus = status;
			}
		}
		updateStatus (tTargetStatus);
	}

	private int getCostForTile (Tile aTile, MapCell aMapCell) {
		int tCost;
		
		tCost = aMapCell.getCostToLayTile (aTile);
		if (benefitInUse.isRealBenefit ()) {
			tCost = benefitInUse.getCost ();
		}
		
		return tCost;
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

	public void trainsOperated (int aThisRevenue, int aOldThisRevenue) {
		OperatedTrainsAction tOperatedTrainsAction;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		String tOperatingRoundID;
		OperatingRound tOperatingRound;
		int tTrainCount;

		tCurrentStatus = getStatus ();
		if (updateStatus (ActorI.ActionStates.OperatedTrain)) {
			tNewStatus = getStatus ();
			tTrainCount = getTrainCount ();
			tOperatingRoundID = getOperatingRoundID ();
			tOperatedTrainsAction = new OperatedTrainsAction (ActorI.ActionStates.OperatingRound, 
					tOperatingRoundID, this);
			trainRevenueFrame.copyAllRoutesToPrevious (tOperatedTrainsAction);

			tOperatedTrainsAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatedTrainsAction.addGeneratedRevenueEffect (this, aOldThisRevenue, aThisRevenue, tTrainCount);
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
		String tDiscountAppliedTo;

		tDiscountAppliedTo = GUI.EMPTY_STRING;
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
		boolean tADiscountRemoved;

		tBank = getBank ();
		tADiscountRemoved = false;
		if (tBank != Bank.NO_BANK) {
			tAvailableTrains = tBank.getAvailableTrains ();
			for (Train tBankTrain : tAvailableTrains) {
				tADiscountRemoved |= tBankTrain.removeDiscount ();
			}
			if (tADiscountRemoved) {
				corporationFrame.updateBankJPanel ();
			}
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

	public void doFinalTrainBuySteps (TrainCompany aOwningTrainCompany, Train aTrain, 
									BuyTrainAction aBuyTrainAction) {
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
		boolean tRouteExtended;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;

		tRouteExtended = false;
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
		boolean tNewEndPointSet;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;

		tNewEndPointSet = false;
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
		trainRevenueFrame.hideFrame ();
	}

	@Override
	public void fillCorporationTrains (ButtonsInfoFrame aButtonsInfoFrame) {
		Corporation tCorporation;
		int tCorpCount;
		int tCorpIndex;
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
	
	@Override
	public boolean hasPermanentTrain () {
		return trainPortfolio.hasPermanentTrain  ();
	}

	@Override
	public void borrowTrain () {
		BorrowTrainAction tBorrowTrainAction;
		OperatingRound tOperatingRound;
		TrainPortfolio tBankTrainPortfolio;
		Train tTrain;
		Bank tBank;
		
		tBank = corporationList.getBank ();
		tBankTrainPortfolio = tBank.getTrainPortfolio ();
		tTrain = tBankTrainPortfolio.getLastTrain ();
		tOperatingRound = corporationList.getOperatingRound ();
		tBorrowTrainAction = new BorrowTrainAction (tOperatingRound.getRoundState (), tOperatingRound.getID (), this);
		tBorrowTrainAction.addBorrowTrainEffect (tBank, tTrain, this);
		if (tTrain == Train.NO_TRAIN) {
			System.err.println ("Selected Borrow Train Button, but could not get Last Train.");
		} else {
			System.out.println ("Selected Borrow Train Button, found the Last train is a " + tTrain.getName () + ".");
			tTrain.setBorrowed (true);
			trainPortfolio.addTrain (tTrain);
		}
		tOperatingRound.addAction (tBorrowTrainAction);
	}
	
	public void returnBorrowedTrain (ChangeMarketCellAction aChangeMarketCellAction) {
		Train tBorrowedTrain;
		Bank tBank;
		TrainPortfolio tBankTrainPortfolio;
		
		tBank = corporationList.getBank ();
		tBankTrainPortfolio = tBank.getTrainPortfolio ();
		tBorrowedTrain = trainPortfolio.getBorrowedTrain ();
		if (tBorrowedTrain != Train.NO_TRAIN) {
			System.out.println ("Ready to return BorrowedTrain " + tBorrowedTrain.getName ());
			tBorrowedTrain.setBorrowed (false);
			tBorrowedTrain.clearPreviousRoute ();
			tBorrowedTrain.clearRouteInformation ();
			tBankTrainPortfolio.addTrain (tBorrowedTrain);
			aChangeMarketCellAction.addReturnTrainEffect (this, tBorrowedTrain, tBank);
		}
	}

	public boolean hasBoughtTrain () {
		boolean tHasBoughtTrain;
		
		if (status == ActorI.ActionStates.BoughtTrain) {
			tHasBoughtTrain = true; 
		} else {
			tHasBoughtTrain = false;
		}
		
		return tHasBoughtTrain;
	}

	@Override
	public int getRangeCost (MapCell aMapCell) {
		return 0;
	}
}
