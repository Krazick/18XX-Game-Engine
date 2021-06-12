package ge18xx.company;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.CashHolderI;
import ge18xx.player.Player;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.ShareHolders;
import ge18xx.round.OperatingRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.LayTileAction;
import ge18xx.round.action.OperatedTrainsAction;
import ge18xx.round.action.PayNoDividendAction;
import ge18xx.round.action.RemoveTileAction;
import ge18xx.round.action.SkipBaseTokenAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.round.action.PayFullDividendAction;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.train.TrainRevenueFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.*;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public abstract class TrainCompany extends Corporation implements CashHolderI, TrainHolderI {
	public static final ElementName EN_TRAIN_COMPANY = new ElementName ("TrainCompany");
	public static final AttributeName AN_TREASURY = new AttributeName ("treasury");
	public static final AttributeName AN_VALUE = new AttributeName ("value");
	public static final AttributeName AN_LAST_REVENUE = new AttributeName ("lastRevenue");
	public static final AttributeName AN_THIS_REVENUE = new AttributeName ("thisRevenue");
	static final AttributeName AN_CLOSE_ON_TRAIN_PURCHASE = new AttributeName ("closeOnTrainPurchase");
	static final AttributeName AN_BG_COLOR = new AttributeName ("bgColor");
	static final AttributeName AN_FG_COLOR = new AttributeName ("fgColor");
	static final AttributeName AN_COST = new AttributeName ("cost");
	static final AttributeName AN_MUST_BUY_TRAIN = new AttributeName ("mustBuyTrain");
	public static final String BUY_LABEL = "Buy";
	public final static String NO_MONEY = "No money in the Treasury.";
	public final static String REVENUES_NOT_GENERATED = "Train Revenues have not been generated yet.";
	public final static String DIVIDENDS_ALREADY_HANDLED = "Dividend Payment already completed for the turn.";
	public final static String DIVIDENDS_NOT_HANDLED = "Dividends have not been Handled.";
	public final static String NO_TRAIN_SELECTED = "No train has been selected to be bought.";
	public final static String SELECT_SINGLE_TRAIN = "Must select a single Train to be bought.";
	public final static String OPERATED_NO_REVENUE = "Train Operated but no Revenue has been generated.";
	static final int NO_COST = 0;
	public static final TrainCompany NO_TRAIN_COMPANY = null;
	static final int NO_REVENUE = -1;
	static final float LEFT_ALIGNMENT = 0.0f;
	String bgColorName;
	String fgColorName;
	Color bgColor;
	Color fgColor;
	int treasury;
	int thisRevenue;
	int lastRevenue;
	int closeOnTrainPurchase;
	TrainPortfolio trainPortfolio;
	TrainRevenueFrame trainRevenueFrame;
	ForceBuyTrainFrame forceBuyTrainFrame;
	int value;
	boolean mustBuyTrain;
	PurchaseOffer purchaseOffer;
	
	public TrainCompany () {
		this (Corporation.NO_ID, Corporation.NO_NAME);
	}
	
	public TrainCompany (int aID, String aName) {
		this (aID, aName, Corporation.NO_ABBREV, Color.white, 
				Color.black, Corporation.NO_HOME_MAPCELL, Corporation.NO_HOME_LOCATION, 
				NO_COST, ActorI.ActionStates.Unowned, false);
	}

	public TrainCompany (int aID, String aName, String aAbbrev, Color aBgColor, Color aFgColor, 
			MapCell aHomeCity1, Location aHomeLocation1, int aCost, ActorI.ActionStates aState, 
			boolean aGovtRailway) {
		this (aID, aName, aAbbrev, aBgColor, aFgColor, aHomeCity1, aHomeLocation1, 
				Corporation.NO_HOME_MAPCELL, Corporation.NO_HOME_LOCATION, aCost, aState, aGovtRailway);
	}
	
	public TrainCompany (int aID, String aName, String aAbbrev, Color aBgColor, Color aFgColor, 
			MapCell aHomeCity1, Location aHomeLocation1, MapCell aHomeCity2, 
			Location aHomeLocation2, int aCost, ActorI.ActionStates aState, boolean aGovtRailway) {
		super (aID, aName, aAbbrev, aHomeCity1, aHomeLocation1, aHomeCity2, aHomeLocation2, aState, aGovtRailway);
		
		trainPortfolio = new TrainPortfolio (this);
		bgColor = aBgColor;
		fgColor = aFgColor;
		treasury = 0;
		value = aCost;
		setThisRevenue (NO_REVENUE);
		setLastRevenue (NO_REVENUE);
		if (aID != Corporation.NO_ID) {
			setupTrainRevenueFrame ();
			setCorporationFrame ();
		}
		setMustBuyTrain (false);
	}
	
	public TrainCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		
		String tColorName;
		boolean tMustBuyTrain;
		
		trainPortfolio = new TrainPortfolio (this);
		tColorName = aChildNode.getThisAttribute (AN_BG_COLOR);
		bgColorName = tColorName;
		bgColor = translateColor (bgColorName);
		fgColorName = aChildNode.getThisAttribute (AN_FG_COLOR);
		fgColor = translateColor (fgColorName);
		value = aChildNode.getThisIntAttribute (AN_COST);
		tMustBuyTrain = aChildNode.getThisBooleanAttribute (AN_MUST_BUY_TRAIN);
		setMustBuyTrain (tMustBuyTrain);
		closeOnTrainPurchase = aChildNode.getThisIntAttribute (AN_CLOSE_ON_TRAIN_PURCHASE, NO_ID);
		setupTrainRevenueFrame ();
		setCorporationFrame ();
	}
	
	public void setupTrainRevenueFrame () {
		String tRevenueFrameTitle;
		String tClientName;
		GameManager tGameManager;
		
		tGameManager = corporationList.getGameManager ();
		tClientName = tGameManager.getClientUserName ();
		tRevenueFrameTitle = "Train Revenue for " + abbrev + " (" + tClientName + ")";
		trainRevenueFrame = new TrainRevenueFrame (this, tRevenueFrameTitle);
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
	
	public void addCash (int aAmount) {
		treasury += aAmount;
	}
	
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		aCorporationList.addDataElement (getTreasury (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getBgColorName (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getBgColor (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getFgColorName (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getFgColor (), aRowIndex, tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
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

	public void addTrain (Train aTrain) {
		trainPortfolio.addTrain (aTrain);
	}
	
	public void appendOtherElements (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		XMLElement tTrainPortfolioElements;

		aXMLCorporationState.setAttribute (AN_LAST_REVENUE, getLastRevenue ());
		aXMLCorporationState.setAttribute (AN_THIS_REVENUE, getThisRevenue ());
		aXMLCorporationState.setAttribute (AN_MUST_BUY_TRAIN, mustBuyTrain ());
		if (trainPortfolio.getTrainCount () > 0) {
			tTrainPortfolioElements = trainPortfolio.getElements (aXMLDocument);
			aXMLCorporationState.appendChild (tTrainPortfolioElements);
		}
		super.appendOtherElements (aXMLCorporationState, aXMLDocument);
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
	public String buildCorpInfoLabel () {
		String tCorpLabel = "";
		String tThisRevenue;
		
		tCorpLabel = getAbbrev () + "&nbsp;";
		if (isActive ()) {
			tCorpLabel += "[" + getPlayerOrCorpOwnedPercentage () + "%&nbsp; Owned]";
			tCorpLabel += "<br>[" + getBankPoolPercentage () + "%&nbsp; in Bank Pool]";
			tCorpLabel += "<br>[" + getStatusName () + "]";
			tCorpLabel += "<br>Prez: " + getPresidentName ();
			tCorpLabel += "<br>Treasury: " + Bank.formatCash (getCash ());
			if (canOperate ()) {
				tCorpLabel += "<br>" + trainPortfolio.getTrainList ();
				tThisRevenue = getFormattedThisRevenue ();
				tCorpLabel += "<br>This Revenue: " + tThisRevenue;
			}
		} else {
			tCorpLabel += "<br>[" + getStatusName () + "]";
		}
		
		return tCorpLabel;
	}
	
	public Border setupBorder (boolean aSamePresident) {
		Border tPanelBorder, tBackgroundBorder, tOuterBorder, tRaisedBevel;
		Border tLoweredBevel, tBevelBorder1, tBevelBorder2;

		tBackgroundBorder = setupBackgroundBorder (5);
		if (aSamePresident) {
			tRaisedBevel = BorderFactory.createBevelBorder (BevelBorder.RAISED, fgColor, bgColor);
			tLoweredBevel = BorderFactory.createBevelBorder (BevelBorder.LOWERED, fgColor, bgColor);
			tBevelBorder1 = BorderFactory.createCompoundBorder (tRaisedBevel, tLoweredBevel);
			tBevelBorder2 = BorderFactory.createCompoundBorder (tBevelBorder1, tBackgroundBorder);
			tPanelBorder = BorderFactory.createCompoundBorder (tBackgroundBorder, tBevelBorder2);
		} else {
			tOuterBorder = setupOuterBorder ();
			tPanelBorder = BorderFactory.createCompoundBorder (tOuterBorder, tBackgroundBorder);
		}
		
		return tPanelBorder;
	}

	private Border setupOuterBorder () {
		Border tOuterBorder;
		tOuterBorder = BorderFactory.createLineBorder (bgColor, 2);
		return tOuterBorder;
	}

	private Border setupBackgroundBorder (int aWidth) {
		Border tBackgroundBorder;
		Color tBackgroundColor;
		
		tBackgroundColor = new Color (237, 237, 237);
		tBackgroundBorder = BorderFactory.createLineBorder (tBackgroundColor, aWidth);
		
		return tBackgroundBorder;
	}
	
	public Border setupBorder () {
		Border tCorpBorder, tOuterBorder, tInnerBorder;

		tOuterBorder = setupOuterBorder ();
		tInnerBorder = setupBackgroundBorder (2);
		tCorpBorder = BorderFactory.createCompoundBorder (tOuterBorder, tInnerBorder);
		
		return tCorpBorder;
	}

//	@Override
	public Container buildPortfolioTrainsJPanel (CorporationFrame aItemListener, 
			GameManager aGameManager, boolean aFullTrainPortfolio, 
			boolean aCanBuyTrain, String aDisableToolTipReason, 
			Corporation aBuyingCorporation, int aTokenCount) {
		Container tTrainPortfolioInfoContainer;
		Container tTrainInfoContainer;
		Container tCorpTrainContainer;
		Container tCorpInfoContainer;
		JPanel tCorpJPanel;
		JLabel tLabel, tPresidentLabel, tStateLabel, tTreasuryLabel, tTokensLabel;
		Border tBorder;
		String tPresident, tBuyingPresident;
		String tActionLabel;

		tActionLabel = BUY_LABEL;
		tPresident = getPresidentName (); 
		tBuyingPresident = aBuyingCorporation.getPresidentName ();		
		tBorder = setupBorder (tPresident.equals (tBuyingPresident));

		tCorpJPanel = new JPanel ();
		tCorpJPanel.setBorder (tBorder);
		
		tTrainPortfolioInfoContainer = Box.createVerticalBox ();
		tCorpTrainContainer = Box.createVerticalBox ();
		tCorpInfoContainer = Box.createVerticalBox ();
		tLabel = new JLabel (getAbbrev ());
		tCorpInfoContainer.add (tLabel);
		if (isPlayerOwned ()) {
			tStateLabel = new JLabel ("State: " + getStatusName ());
			tPresidentLabel = new JLabel ("Prez: " + tPresident);
			tTreasuryLabel = new JLabel ("Treasury: " + Bank.formatCash (treasury));
			tTokensLabel = new JLabel ("Tokens: " + aTokenCount);
			tCorpInfoContainer.add (tStateLabel);
			tCorpInfoContainer.add (tPresidentLabel);
			tCorpInfoContainer.add (tTreasuryLabel);
			tCorpInfoContainer.add (tTokensLabel);
			tCorpTrainContainer.add (tCorpInfoContainer);
			if (trainPortfolio != null) {
				tTrainInfoContainer = trainPortfolio.buildPortfolioJPanel (aItemListener, this, 
						aGameManager, tActionLabel, aFullTrainPortfolio, aCanBuyTrain, aDisableToolTipReason);
				
				tCorpTrainContainer.add (tTrainInfoContainer);
			} else {
				tLabel = new JLabel (">> NO TRAINS <<");
				tCorpTrainContainer.add (tLabel);
			}
		} else {
			tCorpTrainContainer.add (new JLabel (getAbbrev ()));
			tCorpTrainContainer.add (new JLabel ("State: " + getStatusName ()));
		}
		tCorpTrainContainer.add (Box.createVerticalGlue ());
		
		tCorpJPanel.add (tCorpTrainContainer);			
		tTrainPortfolioInfoContainer.add (tCorpJPanel);
		
		return tTrainPortfolioInfoContainer;
	}

	public JPanel buildCertPortfolioInfoContainer (ItemListener aItemListener) {
		JPanel tCertPortfolioInfoContainer;
		JPanel tTrainPortfolioInfoContainer;
		JPanel tPortfolioInfoJPanel;
		GameManager tGameManager;
		JLabel tLabel;
		
		tGameManager = corporationList.getGameManager ();
		if (trainPortfolio != null) {
			tTrainPortfolioInfoContainer = trainPortfolio.buildPortfolioJPanel (aItemListener, this, 
					tGameManager, null, TrainPortfolio.FULL_TRAIN_PORTFOLIO, true, "");
		} else {
			tTrainPortfolioInfoContainer = new JPanel ();
			tLabel = new JLabel (">>NO TRAINS<<");
			tTrainPortfolioInfoContainer.add (tLabel);
		}
		
		tPortfolioInfoJPanel = buildPortfolioJPanel (aItemListener, tGameManager);
		tCertPortfolioInfoContainer = new JPanel ();
		tCertPortfolioInfoContainer.add (tTrainPortfolioInfoContainer);
		tCertPortfolioInfoContainer.add (tPortfolioInfoJPanel);
		
		return tCertPortfolioInfoContainer;
	}
	
	public void forceBuyTrain () {
		Train tCheapestTrain;
		tCheapestTrain = getCheapestBankTrain ();
		forceBuyTrainFrame = new ForceBuyTrainFrame (this, tCheapestTrain);
	}
	
	public boolean mustBuyTrain () {
		return mustBuyTrain;
	}

	public boolean mustBuyTrainNow () {
		boolean tMustBuyTrainNow = false;
		
		if (mustBuyTrain && (trainPortfolio.getTrainCount () == 0)) {
			tMustBuyTrainNow = true;
		}
		
		return tMustBuyTrainNow;
	}
	
	@Override
	public void setMustBuyTrain (boolean aMustBuyTrain) {
		mustBuyTrain = aMustBuyTrain;
	}
	
	public Train getCheapestBankTrain () {
		Train tCheapestTrain = null;
		Train tBankPoolTrain, tBankTrain;
		int tBankPoolTrainCost, tBankTrainCost;
		BankPool tBankPool;
		Bank tBank;
		
		tBankPool = corporationList.getBankPool ();
		tBankPoolTrain = tBankPool.getCheapestTrain ();
		tBank = corporationList.getBank ();
		tBankTrain = tBank.getCheapestTrain ();
		if (tBankPoolTrain != null) {
			tBankPoolTrainCost = tBankPoolTrain.getPrice ();
		} else {
			tBankPoolTrainCost = 99999;
		}
		if (tBankTrain!= null) {
			tBankTrainCost = tBankTrain.getPrice ();
		} else {
			tBankTrainCost = 99999;
		}
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
		tBuyTrainFrame.updateInfo (tTrainToBuy);
		tBuyTrainFrame.setVisible (true);
		tBuyTrainFrame.requestFocus ();
	}

	public void buyTrain () {
		Train tUpgradingTrain;
		BankPool tBankPool;
		BuyTrainAction tBuyTrainAction;
		String tOperatingRoundID;

		if (isSelectedTrainInBank ()) {
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			tUpgradingTrain = getSelectedTrain ();
			if (tUpgradingTrain != TrainPortfolio.NO_TRAIN) {
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
	
	public Train getSelectedBankTrain () {
		Train tTrain;
		TrainHolderI tTrainHolder;
		
		tTrainHolder = getSelectedTrainHolder ();
		tTrain = tTrainHolder.getSelectedTrain ();
		
		return tTrain;
	}
	
	public void buyBankTrain (BuyTrainAction aBuyTrainAction) {
		Train tTrain;
		TrainHolderI tTrainHolder;
		CashHolderI tCashHolder;
		ActorI.ActionStates tCurrentStatus, tNewStatus;
		boolean tStatusUpdated;
		Bank tBank;
		GameManager tGameManager;
		boolean tFirstTrainOfType;
		
		tTrain = getSelectedBankTrain ();
		tTrainHolder = getSelectedTrainHolder ();
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.BoughtTrain);
		tNewStatus = status;
		tCashHolder = tTrainHolder.getCashHolder ();
		aBuyTrainAction.addTransferTrainEffect (tCashHolder, tTrain, this);
		aBuyTrainAction.addCashTransferEffect (this, tCashHolder, tTrain.getPrice ());
		if (tStatusUpdated) {
			aBuyTrainAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);			
		}
		if (closeOnTrainPurchase != NO_ID) {
			tGameManager = getGameManager ();
			tGameManager.closeCompany (closeOnTrainPurchase, (TransferOwnershipAction) aBuyTrainAction);
		}
		tFirstTrainOfType = false;
		if (tTrainHolder instanceof Bank) {
			tBank = (Bank) tTrainHolder;
			tFirstTrainOfType = corporationList.isFirstTrainOfType (tTrain);
			tBank.makeTrainsAvailable (tTrain, aBuyTrainAction);
		}
		addTrain (tTrain);
		this.transferCashTo (tCashHolder, tTrain.getPrice ());
		tTrainHolder.removeSelectedTrain ();
		if (tFirstTrainOfType) {
			corporationList.performPhaseChange (this, tTrain, aBuyTrainAction);
		}
		addAction (aBuyTrainAction);
		corporationFrame.updateInfo ();
		corporationFrame.revalidate ();
	}
	
	public boolean trainIsSelected () {
		boolean tTrainIsSelected = false;
		Train tTrain;
		TrainHolderI tTrainHolder;
		
		tTrainHolder = getSelectedTrainHolder ();
		if (tTrainHolder != TrainPortfolio.NO_TRAIN_HOLDER) {
			tTrain = tTrainHolder.getSelectedTrain ();
			if (tTrain != TrainPortfolio.NO_TRAIN) {
				tTrainIsSelected = true;
			}			
		}
		
		return tTrainIsSelected;
	}
	
	public boolean canLayTile () {
		boolean tCanLayTile;
		
		tCanLayTile = false;
		if ((status == ActorI.ActionStates.StartedOperations) ||
			(status == ActorI.ActionStates.StationLaid)) {
			tCanLayTile = true;
		}
		
		return tCanLayTile;
	}

	@Override
	public boolean canOperate () {
		boolean tCanOperate;
		
		if (didOperate () || isClosed ()) {
			tCanOperate = false;			
		} else {
			if ((status == ActorI.ActionStates.WillFloat) || 
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
			(status == ActorI.ActionStates.Tile2Laid) ||
			(status == ActorI.ActionStates.TileUpgraded) ||
			(status == ActorI.ActionStates.TileAndStationLaid) ||
			(status == ActorI.ActionStates.StationLaid)) {
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
		if (status == ActorI.ActionStates.OperatedTrain) {
			if (thisRevenue > 0) {
				tCanPayDividend = true;
			}
		}
		
		return tCanPayDividend;
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
		if ((status == ActorI.ActionStates.Closed) ||
			(status == ActorI.ActionStates.Unowned) ||
			(status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.MayFloat) ||
			(status == ActorI.ActionStates.WillFloat) ||
			(status == ActorI.ActionStates.HoldDividend) ||
			(status == ActorI.ActionStates.HalfDividend) ||
			(status == ActorI.ActionStates.FullDividend) ||
			(status == ActorI.ActionStates.BoughtTrain) ||
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
	public int getCash () {
		return treasury;
	}

	public CashHolderI getCashHolder () {
		return (CashHolderI) this;
	}

	@Override
	public Color getBgColor () {
		return bgColor;
	}
	
	public String getBgColorName () {
		return bgColorName;
	}
	
	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLCorporationState;
		
		tXMLCorporationState = aXMLDocument.createElement (EN_TRAIN_COMPANY);
		getCorporationStateElement (tXMLCorporationState);

		return tXMLCorporationState;
	}
	
	public void getCorporationStateElement (XMLElement aXMLCorporationState) {
		
		aXMLCorporationState.setAttribute (AN_VALUE, getValue ());
		if (lastRevenue > 0) {
			aXMLCorporationState.setAttribute (AN_LAST_REVENUE, lastRevenue);
		}
		aXMLCorporationState.setAttribute (AN_TREASURY, getCash ());
		super.getCorporationStateElement (aXMLCorporationState);
	}
	
	public double getDividendFor1Percent () {
		return thisRevenue/100.0;
	}

	@Override
	public int getFullShareDividend () {
		return thisRevenue/10;
	}
	
	@Override
	public int getHalfShareDividend () {
		int tHalfShare;
		
		/* Add 0.5 to round double up to next integer */
		tHalfShare = (int) (thisRevenue/20.0 + 0.5);
		
		return tHalfShare;
	}

	@Override
	public Color getFgColor () {
		return fgColor;
	}
	
	public String getFgColorName () {
		return fgColorName;
	}
	
	public String getFormattedThisRevenue () {
		String tFormattedThisRevenue;
		int tThisRevenue = getThisRevenue ();
		
		if (tThisRevenue == NO_REVENUE) {
			tFormattedThisRevenue = "NONE";
		} else {
			tFormattedThisRevenue = Bank.formatCash (tThisRevenue);
		}

		return tFormattedThisRevenue;
	}
	
	public String getFormattedLastRevenue () {
		String tFormattedLastRevenue;
		int tLastRevenue = getLastRevenue ();
		
		if (tLastRevenue == NO_REVENUE) {
			tFormattedLastRevenue = "NONE";
		} else {
			tFormattedLastRevenue = Bank.formatCash (tLastRevenue);
		}

		return tFormattedLastRevenue;
	}

	public int getLastRevenue () {
		return lastRevenue;
	}
	
	public int getLocalSelectedTrainCount () {
		int tSelectedTrainCount;
		
		tSelectedTrainCount = trainPortfolio.getSelectedCount ();
		
		return tSelectedTrainCount;
	}
	
	public TrainHolderI getLocalSelectedTrainHolder () {
		TrainHolderI tTrainHolder = TrainPortfolio.NO_TRAIN_HOLDER;
		Train tSelectedTrain;
		
		tSelectedTrain = trainPortfolio.getSelectedTrain ();
		if (tSelectedTrain != TrainPortfolio.NO_TRAIN) {
			tTrainHolder = this;
		}
		
		return tTrainHolder;
	}
	
	public boolean isSelectedTrainInBank () {
		boolean tIsSelectedTrainHolder = true;
		TrainHolderI tTrainHolder;
		
		tTrainHolder = getOtherSelectedTrainHolder ();
		if (tTrainHolder != TrainPortfolio.NO_TRAIN_HOLDER) {
			tIsSelectedTrainHolder = false;
		}
		
		return tIsSelectedTrainHolder;
	}
	
	public boolean isSelectedTrainHolderTheBank () {
		boolean tIsSelectedTrainHolderTheBank;
		TrainHolderI tSelectedTrainHolder;
		Bank tBank;
		
		tBank = this.getBank ();
		
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
		Train tTrain;
		
		tBank = this.getBank ();
		tBankPool = this.getBankPool ();
		tTrain = TrainPortfolio.NO_TRAIN;
		tTrainHolder = TrainPortfolio.NO_TRAIN_HOLDER;
		if (tTrain == TrainPortfolio.NO_TRAIN) {
			tTrain = tBank.getSelectedTrain ();
			if (tTrain != TrainPortfolio.NO_TRAIN) {
				tTrainHolder = tBank;
			}
		}
		if (tTrain == TrainPortfolio.NO_TRAIN) {
			tTrain = tBankPool.getSelectedTrain ();
			if (tTrain != TrainPortfolio.NO_TRAIN) {
				tTrainHolder = tBankPool;
			}
		}
		
		if (tTrain == TrainPortfolio.NO_TRAIN) {
			tTrainHolder = getOtherSelectedTrainHolder ();
		}
		
		if (tTrain == TrainPortfolio.NO_TRAIN) {
			tTrain = trainPortfolio.getSelectedTrain ();
			if (tTrain != TrainPortfolio.NO_TRAIN) {
				tTrainHolder = this;
			}
		}
		
		return tTrainHolder;
	}
	
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

	public int getThisRevenue () {
		return thisRevenue;
	}

	public Train getTrain (int aIndex) {
		return trainPortfolio.getTrainAt (aIndex);
	}
	
	public Train getTrain (String aName) {
		return trainPortfolio.getTrain (aName);
	}

	public int getTrainCount () {
		int tTrainCount;
		
		tTrainCount = 0;
		if (trainPortfolio != null) {
			tTrainCount = trainPortfolio.getTrainCount ();
		}
		
		return tTrainCount;
	}
	
	public int getTrainLimit () {
		int tTrainLimit;
		
		if (isMinorCompany ()) {
			tTrainLimit = corporationList.getMinorTrainLimit ();
		} else {
			tTrainLimit = corporationList.getTrainLimit (govtRailway);
		}
		
		return tTrainLimit;
	}

	public String getTrainNameAndQty (String aStatus) {
		return trainPortfolio.getTrainNameAndQty (aStatus);
	}

	public TrainPortfolio getTrainPortfolio () {
		return trainPortfolio;
	}
	
	public int getTrainQuantity (String aName) {
		return trainPortfolio.getTrainQuantity (aName);
	}
	
	public int getTreasury () {
		return treasury;
	}
	
	public int getValue () {
		return value;
	}

	public boolean hasTrainNamed (String aName) {
		return trainPortfolio.hasTrainNamed (aName);
	}
	
	@Override
	public boolean hasTrainOfType (Train aTrain) {
		return trainPortfolio.hasTrainNamed (aTrain.getName ());
	}

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
	}

	public void operateTrains () {
		GameManager tGameManager;
		Point tFrameOffset;
		
		tGameManager = corporationList.getGameManager ();
		tFrameOffset = tGameManager.getOffsetCorporationFrame ();
		trainRevenueFrame.operateTrains (tFrameOffset);
	}
//	
//	public void handleResetAllRoutes () {
//		trainRevenueFrame.handleResetAllRoutes ();
//	}
	
	public void clearTrainsFromMap () {
		trainRevenueFrame.clearTrainsFromMap();
	}
	
	public void hideTrainRevenueFrame () {
		trainRevenueFrame.setVisible (false);
	}
	
	public void skipBaseToken () {
		ActorI.ActionStates tCurrentStatus, tNewStatus;
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
			tSkipBaseTokenAction = new SkipBaseTokenAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			tSkipBaseTokenAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatingRound.addAction (tSkipBaseTokenAction);
			setLastRevenue (thisRevenue);
			corporationFrame.updateInfo ();
		} else {
			System.err.println ("Status has NOT been updated from " + status);
		}
	}
	
	public void payNoDividend () {
		int tRevenueGenerated;
		PayNoDividendAction tPayNoDividendAction;
		String tOperatingRoundID;
		ShareCompany tShareCompany;
		Bank tBank;
		OperatingRound tOperatingRound;
		boolean tStatusUpdated;
		ActorI.ActionStates tCurrentStatus, tNewStatus;
		
		tRevenueGenerated = 0;
		if (thisRevenue != NO_REVENUE) {
			tRevenueGenerated = thisRevenue;
		}
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.HoldDividend);
		if (tStatusUpdated) {
			tNewStatus = status;
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tOperatingRound = corporationList.getOperatingRound ();
			tPayNoDividendAction = new PayNoDividendAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			if (tRevenueGenerated > 0) {
				tBank = corporationList.getBank ();
				// Pay the Dividend to the TrainCompany (this) and not the players
				tBank.transferCashTo (this, tRevenueGenerated);
				tPayNoDividendAction.addCashTransferEffect (tBank, this, tRevenueGenerated);
			}
			/* If a Share Company -- Adjust the Market Cell regardless of how much dividend is paid */
			if (isShareCompany ()) {
				tShareCompany = (ShareCompany) this;
				tShareCompany.payNoDividendAdjustment (tPayNoDividendAction);
			}
			tPayNoDividendAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatingRound.addAction (tPayNoDividendAction);
			setLastRevenue (thisRevenue);
			corporationFrame.updateInfo ();
		} else {
			System.err.println ("Status has NOT been updated from " + status);
		}
	}
	
	public void payFullDividend () {
		int tRevenueGenerated;
		PayFullDividendAction tPayFullDividendAction;
		String tOperatingRoundID;
		ShareCompany tShareCompany;
		OperatingRound tOperatingRound;
		boolean tStatusUpdated;
		ActorI.ActionStates tCurrentStatus, tNewStatus;
		
		tRevenueGenerated = 0;
		if (thisRevenue != NO_REVENUE) {
			tRevenueGenerated = thisRevenue;
		}
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.FullDividend);
		if (tStatusUpdated) {
			tNewStatus = status;
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tOperatingRound = corporationList.getOperatingRound ();
			tPayFullDividendAction = new PayFullDividendAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			if (tRevenueGenerated > 0) {
				// Pay the Dividend to the Stock Holders not the TrainCompany (this) 
				payShareHolders (tPayFullDividendAction);
			}
			// If a Share Company -- Adjust the Market Cell regardless of how much dividend is paid
			if (isShareCompany ()) {
				tShareCompany = (ShareCompany) this;
				tShareCompany.payFullDividendAdjustment (tPayFullDividendAction);
			}
			tPayFullDividendAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatingRound.addAction (tPayFullDividendAction);
			setLastRevenue (thisRevenue);
			corporationFrame.updateInfo ();
		} else {
			System.err.println ("Status has NOT been updated from " + status);
		}
	}

	public void payShareHolders (PayFullDividendAction aPayFullDividendAction) {
		ShareHolders tShareHolders;
		int tCertificateCount, tCertificateIndex, tShareHolderCount, tShareHolderIndex;
		int tPercentage, tDividendForShares;
		double tDividendFor1Percent;
		Certificate tCertificate;
		PortfolioHolderI tPortfolioHolder;
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
				tPercentage = tShareHolders.getShareCount (tShareHolderIndex);
				tDividendForShares = (int) (tDividendFor1Percent * tPercentage + 0.5);

				if (tPortfolioHolder instanceof Player) {
					tPlayer = (Player) tPortfolioHolder;
					tBank.transferCashTo (tPlayer, tDividendForShares);
					tPlayer.updatePlayerContainer ();
					aPayFullDividendAction.addCashTransferEffect (tBank, tPlayer, tDividendForShares);
				} else if (tPortfolioHolder instanceof BankPool) {
					tBank.transferCashTo (this, tDividendForShares);
					aPayFullDividendAction.addCashTransferEffect (tBank, this, tDividendForShares);
				}
				// TODO: non-1830 Games Test if Portfolio Holder is Bank or Bank Pool -- and if game states 
				// if these pay Corporation, pay those share there
			}
		}
	}
	
	public boolean canBuyTrain () {
		boolean tCanBuyTrain = true;
		
		if (atTrainLimit ()) {
			tCanBuyTrain = false;
		} else if (treasury == 0) {
			tCanBuyTrain = false;
		} else if ((status != ActorI.ActionStates.BoughtTrain) &&
				(status != ActorI.ActionStates.HoldDividend) &&
				(status != ActorI.ActionStates.HalfDividend) &&
				(status != ActorI.ActionStates.FullDividend)) {
			tCanBuyTrain = false;
		}
		
		return tCanBuyTrain;
	}
	
	public int getSelectedTrainCount () {
		Bank tBank;
		BankPool tBankPool;
		int tSelectedCount;

		tBank = getBank ();
		tBankPool = getBankPool ();
		tSelectedCount = tBank.getSelectedTrainCount () 
				+ tBankPool.getSelectedTrainCount ()
				+ super.getSelectedTrainCount ();
		
		return tSelectedCount;
	}

	public String reasonForNoBuyTrain () {
		String tReasonForNoBuyTrain;
		int tSelectedTrainCount;
		
		tReasonForNoBuyTrain = NO_REASON;
		tSelectedTrainCount = getSelectedTrainCount ();
		if ((status == ActorI.ActionStates.HoldDividend) ||
			(status == ActorI.ActionStates.HalfDividend) ||
			(status == ActorI.ActionStates.FullDividend)) {
			// If Dividend has been held, half paid, or full paid it is time to buy train
			if (treasury == 0) {
				tReasonForNoBuyTrain = NO_MONEY;
			} else if (atTrainLimit ()) {
				tReasonForNoBuyTrain = "At Train Limit of " + getTrainLimit () + " for this Corporation/Phase.";
			}
			if (tSelectedTrainCount == 0) {
				tReasonForNoBuyTrain = NO_TRAIN_SELECTED;
			} else if (tSelectedTrainCount > 1) {
				tReasonForNoBuyTrain = SELECT_SINGLE_TRAIN;
			} else {
				tReasonForNoBuyTrain = CorporationFrame.NO_TOOL_TIP;
			}
		} else {
			tReasonForNoBuyTrain = DIVIDENDS_NOT_HANDLED;
		}
		
		return tReasonForNoBuyTrain;
	}
	
	public String reasonForNoDividendOptions () {
		String tReason;
		
		tReason = commonReason ();
		if ((status == ActorI.ActionStates.NotOperated) ||
			(status == ActorI.ActionStates.StartedOperations) ||
			(status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.Tile2Laid) ||
			(status == ActorI.ActionStates.TileUpgraded) ||
			(status == ActorI.ActionStates.TileAndStationLaid)) {
			tReason = REVENUES_NOT_GENERATED;
		} else if ((status == ActorI.ActionStates.HoldDividend) ||
			(status == ActorI.ActionStates.HalfDividend) ||
			(status == ActorI.ActionStates.FullDividend) ||
			(status == ActorI.ActionStates.BoughtTrain) ||
			(status == ActorI.ActionStates.Operated)) {
			tReason = DIVIDENDS_ALREADY_HANDLED;
		}
		
		return tReason;
	}
	
	public String reasonForNoDividendPayment () {
		String tReason;
		
		tReason = NO_REASON;
		if ((status == ActorI.ActionStates.NotOperated) ||
			(status == ActorI.ActionStates.StartedOperations) ||
			(status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.Tile2Laid) ||
			(status == ActorI.ActionStates.TileUpgraded) ||
			(status == ActorI.ActionStates.TileAndStationLaid)) {
			tReason = REVENUES_NOT_GENERATED;
		} else if ((status == ActorI.ActionStates.HoldDividend) ||
			(status == ActorI.ActionStates.HalfDividend) ||
			(status == ActorI.ActionStates.FullDividend) ||
			(status == ActorI.ActionStates.BoughtTrain) ||
			(status == ActorI.ActionStates.Operated)) {
			tReason = DIVIDENDS_ALREADY_HANDLED;
		} else if ((status == ActorI.ActionStates.OperatedTrain) && (thisRevenue <= 0)) {
			tReason = OPERATED_NO_REVENUE;
		}
				
		return tReason;
	}
	
	public String reasonForNoTileLay () {
		String tReason;
		
		tReason = NO_REASON;
		if ((status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.Tile2Laid) ||
			(status == ActorI.ActionStates.TileUpgraded) ||
			(status == ActorI.ActionStates.TileAndStationLaid)) {
			tReason = "Already laid Tile this Turn";
		}
		if (NO_REASON.equals (tReason)) {
			tReason = commonReason ();
		}
		
		return tReason;
	}

	public String reasonForNoTrainOperation () {
		String tReason;
		if (trainPortfolio.getTrainCount () == 0) {
			tReason = "There are no Trains in the Portfolio to Operate";
		} else {
			tReason = commonReason ();
		}
		
		return tReason;
	}
	
	public boolean removeSelectedTrain () {
		return trainPortfolio.removeSelectedTrain ();
	}

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
		if (! updateStatus (ActorI.ActionStates.Operated)) {
			System.err.println ("--> Failure to update State to Operated <--");
		}
	}
	
	@Override
	public void tileWasPlaced (MapCell aMapCell, Tile aTile, int aOrientation, 
			Tile aPreviousTile, int aPreviousOrientation, 
			String aPreviousTokens, String aPreviousBases) {
		boolean tStatusUpdated;
		RemoveTileAction tRemoveTileAction;
		LayTileAction tLayTileAction;
		String tOperatingRoundID;
		ActorI.ActionStates tCurrentStatus, tNewStatus;
		int tCostToLayTile;
		Bank tBank;
		String tNewTokens, tNewBases;
		
		tCurrentStatus = status;
		if (benefitInUse.changeState ()) {
			if (status == ActorI.ActionStates.TileLaid) {
				tStatusUpdated = updateStatus (ActorI.ActionStates.Tile2Laid);
			} else if (status == ActorI.ActionStates.StationLaid) {
				tStatusUpdated = updateStatus (ActorI.ActionStates.TileAndStationLaid);
			} else {
				tStatusUpdated = updateStatus (ActorI.ActionStates.TileLaid);
			}
		} else {
			tStatusUpdated = true;
		}
		if (tStatusUpdated) {
			tNewStatus = status;
			tCostToLayTile = aMapCell.getCostToLayTile (aTile);
			tOperatingRoundID = corporationList.getOperatingRoundID ();
			tLayTileAction = new LayTileAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			
			if (aPreviousTile != Tile.NO_TILE) {
				tRemoveTileAction = new RemoveTileAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
				tRemoveTileAction.addTileRemoveEffect (this, aMapCell, aPreviousTile, 
						aPreviousOrientation, aPreviousTokens, aPreviousBases);
				tRemoveTileAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
				addAction (tRemoveTileAction);
				tLayTileAction.setChainToPrevious (true);
			}
			tNewTokens = aTile.getPlacedTokens ();
			tNewBases = aTile.getCorporationBases ();
			tLayTileAction.addLayTileEffect (this, aMapCell, aTile, aOrientation, tNewTokens, tNewBases, benefitInUse);
			if (tCurrentStatus != tNewStatus) {
				tLayTileAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			}
			if (tCostToLayTile > 0) {
				tBank = corporationList.getBank ();
				this.transferCashTo (tBank, tCostToLayTile);
				tLayTileAction.addCashTransferEffect (this, tBank, tCostToLayTile);
			}
			addAction (tLayTileAction);
			corporationFrame.updateInfo ();
		}
	}
	
	public void completeBenefitUse () {
		benefitInUse.completeBenefitUse ();		
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
			tOperatedTrainsAction = new OperatedTrainsAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			tOperatedTrainsAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tOperatedTrainsAction.addGeneratedRevenueEffect (this, aRevenue, tTrainCount);
			tOperatingRound = corporationList.getOperatingRound ();
			tOperatingRound.addAction (tOperatedTrainsAction);
			tOperatingRound.updateRoundFrame ();
		} else {
			System.err.println ("--> Failure to update State to Operated Trains");
		}
		corporationFrame.updateInfo ();	
	}
	
	public void transferCashTo (CashHolderI aToHolder, int aAmount) {
		aToHolder.addCash (aAmount);
		addCash (-aAmount);
	}
	
	public Color translateColor (String aColorName) {
		int tRed, tGreen, tBlue;
		String rgbValues [] = new String [3];
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
		} else {
			rgbValues = aColorName.split (",");
			tRed = new Integer (rgbValues [0]).intValue ();
			tGreen = new Integer (rgbValues [1]).intValue ();
			tBlue = new Integer (rgbValues [2]).intValue ();
			tColor = new Color (tRed, tGreen, tBlue);
		}
		
		return tColor;
	}

	public void applyDiscount () {
		Train tSelectedTrainToUpgrade;
		Bank tBank;
		Train [] tAvailableTrains;
		String tDiscountAppliedTo = "";

		tBank = getBank ();
		tAvailableTrains = tBank.getAvailableTrains ();
		
		tSelectedTrainToUpgrade = getSelectedTrain ();
		for (Train tBankTrain : tAvailableTrains) {
			if (tBankTrain.canBeUpgradedFrom (tSelectedTrainToUpgrade.getName ())) {
				if (! (tDiscountAppliedTo.equals (tBankTrain.getName ()))) {
					tDiscountAppliedTo = tBankTrain.getName ();
					tBankTrain.applyDiscount (tSelectedTrainToUpgrade);
					corporationFrame.updateBankBox ();
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
			corporationFrame.updateBankBox ();
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
	
	@Override
	public void getLoan () {
		// TODO: 1856 - Handle Getting Loans from the Bank
		System.out.println ("Get Loan Action");
	}

	@Override
	public void paybackLoan () {
		// TODO: 1856 - Handle Paying Back Loans to the Bank
		System.out.println ("Payback Loan Action");
	}
	
	public void setPurchaseOffer (PurchaseOffer aPurchaseOffer) {
		purchaseOffer = aPurchaseOffer;
	}
	
	public PurchaseOffer getPurchaseOffer () {
		return purchaseOffer;
	}
	
	public void handleRejectOffer (RoundManager aRoundManager) {
		CorporationFrame tCorporationFrame;
		
		corporationList.clearTrainSelections ();
		tCorporationFrame = getCorporationFrame ();
		tCorporationFrame.updateInfo ();
	}
	
	public void handleAcceptOffer (RoundManager aRoundManager) {
		BuyTrainAction tBuyTrainAction;
		TrainCompany tOwningTrainCompany;
		Corporation tOwningCompany;
		ActorI tActorOfferSentTo;
		int tCashValue;
		Train tTrain;
		CorporationFrame tCorporationFrame;
		String tOperatingRoundID;
		String tActorToName;
		String tItemName, tItemType;
		GameManager tGameManager;
		boolean tCurrentNotify;
		
		tGameManager = aRoundManager.getGameManager ();
		tOperatingRoundID = aRoundManager.getOperatingRoundID ();
		tCashValue = purchaseOffer.getAmount ();
		tActorToName = purchaseOffer.getToName ();
		tActorOfferSentTo = tGameManager.getActor (tActorToName);
		if (tActorOfferSentTo.isACorporation ()) {
			tOwningCompany = (Corporation) tActorOfferSentTo;
			if (tOwningCompany.isATrainCompany ()) {
				tCorporationFrame = getCorporationFrame ();
				tOwningTrainCompany = (TrainCompany) tOwningCompany;
				tItemType = purchaseOffer.getItemType ();
				tItemName = purchaseOffer.getItemName ();
				tTrain = purchaseOffer.getTrain ();
				if (tTrain != null) {
					if (tTrain.getType ().equals (tItemType)) {
						if (tTrain.getName ().equals (tItemName)) {
							tBuyTrainAction = new BuyTrainAction (ActorI.ActionStates.OperatingRound, 
								tOperatingRoundID, this);
							transferCashTo (tOwningTrainCompany, tCashValue);
							tBuyTrainAction.addCashTransferEffect (this, tOwningTrainCompany, tCashValue);
							// We must toggle NotifyNetwork on, for this, and reset to prior state to allow for handling Response
							// to when doing a purchase between players
							tCurrentNotify = tGameManager.getNotifyNetwork ();
							tGameManager.setNotifyNetwork (true); 
							doFinalTrainBuySteps (tOwningTrainCompany, tTrain, tBuyTrainAction);
							tGameManager.setNotifyNetwork (tCurrentNotify); 
						} else {
							System.err.println ("Purchase Offer's Item Name " + tItemName +
									" does not match Selected Item Name " + tTrain.getName ());
						}
					} else {
						System.err.println ("Purchase Offer's Item Type " + tItemType +
								" does not match Selected Item Type " + tTrain.getType ());
					}
					tCorporationFrame.updateInfo ();
				} else {
					System.err.println ("Train Selected to buy not found (NULL)");
				}
			} else {
				System.err.println ("Company " + tActorToName + " is not a Train Company");
			}
		} else {
			System.err.println ("Actor " + tActorToName + " is not a Corporation - Likely Player");
		}
	}
	
	public void doFinalTrainBuySteps (TrainCompany aOwningTrainCompany, 
			Train aTrain, BuyTrainAction aBuyTrainAction) {
		ActorI.ActionStates tCurrentCorporationStatus, tNewCorporationStatus;
		TrainPortfolio tCompanyPortfolio, tOwningPortfolio;
		
		tCompanyPortfolio = getTrainPortfolio ();
		tOwningPortfolio = aOwningTrainCompany.getTrainPortfolio ();
		tCompanyPortfolio.addTrain (aTrain);
		tOwningPortfolio.removeSelectedTrain ();
		tCompanyPortfolio.clearSelections ();
		tOwningPortfolio.clearSelections ();
		tCurrentCorporationStatus = getStatus ();
		updateStatus (ActorI.ActionStates.BoughtTrain);
		tNewCorporationStatus = getStatus ();
		aBuyTrainAction.addTransferTrainEffect (aOwningTrainCompany, aTrain, this);
		if (tCurrentCorporationStatus != tNewCorporationStatus) {
			aBuyTrainAction.addChangeCorporationStatusEffect (this, 
					tCurrentCorporationStatus, tNewCorporationStatus);
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

	public boolean startRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation, Location aEndLocation) {
		boolean tRouteStarted = false;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;
		
		showTrainRevenueFrameForOthers (aTrainIndex);
		tRoundID = corporationList.getOperatingRoundID ();
		tPhaseInfo = corporationList.getCurrentPhaseInfo () ;
		tPhase = tPhaseInfo.getName ();
		tRouteStarted = trainPortfolio.startRouteInformation (aTrainIndex, aMapCell, aStartLocation, aEndLocation,
				tRoundID, tPhase, this, trainRevenueFrame);
		corporationList.repaintMapFrame ();
	
		return tRouteStarted;
	}

	public boolean extendRouteInformation (int aTrainIndex, MapCell aMapCell, Location aStartLocation, Location aEndLocation) {
		boolean tRouteExtended = false;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;
		
		showTrainRevenueFrameForOthers (aTrainIndex);
		tRoundID = corporationList.getOperatingRoundID ();
		tPhaseInfo = corporationList.getCurrentPhaseInfo () ;
		tPhase = tPhaseInfo.getName ();
		tRouteExtended = trainPortfolio.extendRouteInformation (aTrainIndex, aMapCell, aStartLocation, aEndLocation,
				tRoundID, tPhase, this, trainRevenueFrame);
		corporationList.repaintMapFrame ();

		return tRouteExtended;
	}
	
	public void showTrainRevenueFrameForOthers (int aTrainIndex) {
		Point tFrameOffset;
		GameManager tGameManager;
		
		if (! trainRevenueFrame.isVisible ()) {
			tGameManager = corporationList.getGameManager ();
			tFrameOffset = tGameManager.getOffsetRoundFrame ();
			trainRevenueFrame.setLocation (tFrameOffset);
			trainRevenueFrame.updateInfo ();
		}
		trainRevenueFrame.setVisible (true);
		trainRevenueFrame.disableAll (aTrainIndex);
	}
	
	public boolean setNewEndPoint (int aTrainIndex, MapCell aMapCell, Location aStartLocation, Location aEndLocation) {
		boolean tNewEndPointSet = false;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;
		
		showTrainRevenueFrameForOthers (aTrainIndex);
		tRoundID = corporationList.getOperatingRoundID ();
		tPhaseInfo = corporationList.getCurrentPhaseInfo( ) ;
		tPhase = tPhaseInfo.getName ();
		tNewEndPointSet = trainPortfolio.setNewEndPoint (aTrainIndex, aMapCell, aStartLocation, aEndLocation,
				tRoundID, tPhase, this, trainRevenueFrame);
		corporationList.repaintMapFrame ();
		
		return tNewEndPointSet;
	}

	public void closeTrainRevenueFrame() {
		// Need to clear the Frame Setup Flag for the Next Company Operating to be able to update.
		trainRevenueFrame.setFrameSetup (false);
		trainRevenueFrame.setVisible (false);
	}
}
