package ge18xx.company;

//
//  Corporation.java
//  Java_18XX
//
//  Created by Mark Smith on 8/7/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.Benefits;
import ge18xx.company.benefit.FakeBenefit;
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.market.MarketCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.CashHolderI;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.round.OperatingRound;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.DoneCorpAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import ge18xx.toplevel.XMLFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public abstract class Corporation implements PortfolioHolderLoaderI, ParsingRoutineI, Comparable<Corporation> {
	public static final String CORPORATION = "Corporation";
	public static final ElementName EN_CORPORATION = new ElementName (CORPORATION);
	public static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_ABBREV = new AttributeName ("abbrev");
	public static final AttributeName AN_HOMECELL1 = new AttributeName ("homeCell1");
	public static final AttributeName AN_HOMECELL2 = new AttributeName ("homeCell2");
	public static final AttributeName AN_CORP_STATUS = new AttributeName ("status");
	public static final AttributeName AN_GOVT_RAILWAY = new AttributeName ("govtRailway");
	public static final String NO_NOTE = "";
	public static final String NO_REASON = ">>NO REASON<<";
	public static final String NO_PRESIDENT = "";
	public static final String NO_ABBREV = null;
	public static final String NO_NAME_STRING = "<NONE>";
	public static final String PRIVATE_COMPANY = "Private";
	public static final String COAL_COMPANY = "Coal";
	public static final String MINOR_COMPANY = "Minor";
	public static final String SHARE_COMPANY = "Share";
	public static final String NO_NAME = ActorI.NO_NAME;
	public static final int NO_ID = 0;
	public static final Corporation NO_CORPORATION = null;
	static final String enum_closed = ActionStates.Closed.toString ();
	static final String enum_operated = ActionStates.Operated.toString ();
	static final String enum_not_operated = ActionStates.NotOperated.toString ();
	static final String NO_HOME_GRID = null;
	static final int NO_COST = -1;
	static final int NO_NAME_INT = -1;
	static final int SORT_CO1_BEFORE_CO2 = -100;
	static final int SORT_CO2_BEFORE_CO1 = 100;
	boolean gameTestFlag = false;
	boolean govtRailway;
	int id;
	String name;
	String abbrev;
	String homeCityGrid1;
	String homeCityGrid2;
	MapCell homeCity1;
	Location homeLocation1;
	MapCell homeCity2;
	Location homeLocation2;
	ActorI.ActionStates status;
	CorporationFrame corporationFrame;
	CorporationList corporationList;
	Benefit benefitInUse;
	Portfolio portfolio;   // All Certificates Owned by the Corporation (Privates, Minors, it's own, and others)
			// Use this portfolio to find what this company can sell.
	Portfolio corporationCertificates; // A copy of all of this Corporation's Certificates -- regardless of who owns them.
			// Use this portfolio to pay-out dividends -- to the owners. It goes in here once, and never leaves.
	
	public Corporation () {
		this (NO_ID, NO_NAME);
	}
	
	public Corporation (int aID, String aName) {
		this (aID, aName, NO_ABBREV, MapCell.NO_MAP_CELL, Location.NO_LOC, MapCell.NO_MAP_CELL, 
				Location.NO_LOC, ActorI.ActionStates.Unowned, false);
	}
	
	public Corporation (int aID, String aName, String aAbbrev, MapCell aHomeCity1, Location aHomeLocation1, 
			MapCell aHomeCity2, Location aHomeLocation2, ActorI.ActionStates aStatus, boolean aGovtRailway) {
		setValues (aID, aName, aAbbrev, aHomeCity1, aHomeLocation1, aHomeCity2, aHomeLocation2, aStatus, aGovtRailway);
	}
	
	/* Parse Corporation Node from XML Node - For Initial Load from Data file of Corporations*/
	public Corporation (XMLNode aXMLNode, CorporationList aCorporationList) {
		int tLocation;
		XMLNodeList tXMLNodeList;
		String tName, tAbbrev;
		Benefit tBenefitInUse;
		
		corporationCertificates = new Portfolio (this);
		portfolio = new Portfolio (this);
		id = aXMLNode.getThisIntAttribute (AN_ID);
		
		tName = aXMLNode.getThisAttribute (AN_NAME);
		setName (tName);
		
		tAbbrev = aXMLNode.getThisAttribute (AN_ABBREV);
		setAbbrev (tAbbrev);

		homeCityGrid1 = aXMLNode.getThisAttribute (AN_HOMECELL1);
		tLocation = aXMLNode.getThisIntAttribute (Location.AN_HOME_LOCATION1);
		homeLocation1 = new Location (tLocation);
		
		homeCityGrid2 = aXMLNode.getThisAttribute (AN_HOMECELL2);
		tLocation = aXMLNode.getThisIntAttribute (Location.AN_HOME_LOCATION2, Location.NO_LOCATION);
		homeLocation2 = new Location (tLocation);
		
		setStatus (aXMLNode);
				
		tXMLNodeList = new XMLNodeList (this);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Certificate.EN_CERTIFICATE, Benefits.EN_BENEFITS);
		setCorporationList (aCorporationList);
		tBenefitInUse = new FakeBenefit ();
		setBenefitInUse (tBenefitInUse);
	}

	public void setBenefitInUse (Benefit aBenefitInUse) {
		benefitInUse = aBenefitInUse;
	}
	
	public Benefit getBenefitInUse () {
		return benefitInUse;
	}
	
	private void setStatus (XMLNode aXMLNode) {
		String tStatus;
		ActorI.ActionStates tActionStatus;
		GenericActor tGenericActor;
		
		tStatus = aXMLNode.getThisAttribute (AN_CORP_STATUS, ActorI.ActionStates.Unowned.toString ());
		tGenericActor = new GenericActor ();
		tActionStatus = tGenericActor.getCorporationActionState (tStatus);
		setStatus (tActionStatus);
	}
		
	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
		Certificate tCertificate;
		int tCertificateQuantity;
		int tQtyIndex;
		
		tCertificate = new Certificate (aChildNode);
		tCertificate.setCorporation (this);
		tCertificateQuantity = aChildNode.getThisIntAttribute (AN_QUANTITY, 1);
		for (tQtyIndex = 0; tQtyIndex < tCertificateQuantity; tQtyIndex++) {
			if (tQtyIndex > 0) {
				tCertificate = new Certificate (tCertificate);
			}
			corporationCertificates.addCertificate (tCertificate);
		}
	}
		
	public void addAction (Action aAction) {
		corporationList.addAction (aAction);
	}

	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		aCorporationList.addDataElement (getID (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getName (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getAbbrev (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getStatusName (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (aCorporationList.getThisTypeName (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getHomeCityGrid1 (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getHomeLocation1Int (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getHomeCityGrid2 (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getHomeLocation2Int (), aRowIndex, tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		aCorporationList.addHeader ("ID", tCurrentColumn++);
		tCurrentColumn = addShortHeaders (aCorporationList, tCurrentColumn);
		aCorporationList.addHeader ("Type", tCurrentColumn++);
		aCorporationList.addHeader ("Home Cell 1", tCurrentColumn++);
		aCorporationList.addHeader ("Home Loc 1", tCurrentColumn++);
		aCorporationList.addHeader ("Home Cell 2", tCurrentColumn++);
		aCorporationList.addHeader ("Home Loc 2", tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
	@Override
	public void addCertificate (Certificate aCertificate) {
		portfolio.addCertificate (aCertificate);
	}
	
	public int addShortHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		aCorporationList.addHeader ("Corporation Name", tCurrentColumn++);
		aCorporationList.addHeader ("Abbreviation", tCurrentColumn++);
		aCorporationList.addHeader ("Status", tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
	public abstract JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash);

	public JPanel buildPortfolioJPanel (ItemListener aItemListener, GameManager aGameManager) {
		JLabel tLabel;
		JPanel tPortfolioInfoJPanel;
		JPanel tCertsPanel;
		String tTitle;
		
		tPortfolioInfoJPanel = new JPanel ();
		if (portfolio == Portfolio.NO_PORTFOLIO) {
			tLabel = new JLabel (Portfolio.NO_PORTFOLIO_LABEL);
			tPortfolioInfoJPanel.add (tLabel);
		} else {
			if (portfolio.getCertificateTotalCount () == 0) {
				tLabel = new JLabel (Portfolio.NO_CERTIFICATES);
				tPortfolioInfoJPanel.add (tLabel);				
			} else {
				tTitle = "Privates";
				tCertsPanel = portfolio.buildPortfolioJPanel (tTitle, true, false, false, false, "", 
						aItemListener, aGameManager);
				tPortfolioInfoJPanel.add (tCertsPanel);
			}
		}
		
		return tPortfolioInfoJPanel;
	}
	
	// Token Company will Override
	public JLabel buildTokenLabel () {
		return GUI.NO_LABEL;
	}
	
	// Share Company will Override
	public void buyPrivate (boolean tVisible) {
		System.err.println ("Trying to -BUY PRIVATE- Should be handled by Share Company");
	}
	
	// Train Company will Override
	public void buyTrain () {
		System.err.println ("Trying to -BUY TRAIN- Should be handled by Train Company");
	}
	
	public void clearOperatedStatus () {
		if (status == ActorI.ActionStates.Operated) {
			status = ActorI.ActionStates.NotOperated;
		}
	}
	
	// Corporation Types will Override
	public void clearCertificateSelections () {
		corporationCertificates.clearSelections ();
	}
	
	// Override in Train Company
	public Color getFgColor () {
		return Color.WHITE;
	}
	
	// Override in Train Company
	public Color getBgColor () {
		return Color.BLACK;
	}
	
	public TrainHolderI getOtherSelectedTrainHolder () {
		TrainHolderI tGetOtherSelectedTrainHolder = TrainPortfolio.NO_TRAIN_HOLDER;
		
		if (corporationList != CorporationList.NO_CORPORATION_LIST) {
			tGetOtherSelectedTrainHolder = corporationList.getOtherSelectedTrainHolder (abbrev);
		}
		
		return tGetOtherSelectedTrainHolder;
	}
	
	public int getSelectedTrainCount () {
		int tGetSelectedTrainCount;
		
		tGetSelectedTrainCount = 0;
		if (corporationList != CorporationList.NO_CORPORATION_LIST) {
			tGetSelectedTrainCount = corporationList.getSelectedTrainCount (abbrev);
		}
		
		return tGetSelectedTrainCount;
	}
	
	public boolean isSelectedTrainItem (Object aItem) {
		boolean tIsSelectedTrainItem;
		
		tIsSelectedTrainItem = false;
		if (corporationList != CorporationList.NO_CORPORATION_LIST) {
			tIsSelectedTrainItem = corporationList.isSelectedTrainItem (abbrev, aItem);
		}
		
		return tIsSelectedTrainItem;
	}

	// Token Company will Override
	public String getTokenLabel () {
		return null;
	}
	
	public boolean canPayHalfDividend () {
		boolean tCanPayHalfDividend;
		
		tCanPayHalfDividend = false;
		if (corporationList != CorporationList.NO_CORPORATION_LIST) {
			tCanPayHalfDividend = corporationList.canPayHalfDividend ();
		}
		
		return tCanPayHalfDividend;
	}
	
	public boolean canLayTile () {
		return false;
	}
	
	// Token Company will Override
	public boolean canLayToken () {
		return false;
	}

	// Train Company will override
	public boolean canOperateTrains () {
		return false;
	}

	// Train Company will override
	public boolean canPayDividend () {
		return false;
	}
	
	public boolean canBuyPrivate () {
		boolean tCanBuyPrivate;
		
		tCanBuyPrivate = false;
		if (corporationList != CorporationList.NO_CORPORATION_LIST) {
			tCanBuyPrivate = corporationList.canBuyPrivate ();
		}
		
		return tCanBuyPrivate;
	}
	
	// Train Company will override
	public boolean canBuyTrain () {
		return false;
	}
	
	// Train Company will override
	public boolean trainIsSelected () {
		return false;
	}
	
	public void clearBankSelections () {
		if (corporationList != CorporationList.NO_CORPORATION_LIST) {
			corporationList.clearBankSelections ();
		}
	}
	
	public boolean gameHasPrivates () {
		boolean tGameHasPrivates;
		
		tGameHasPrivates = false;
		if (corporationList != CorporationList.NO_CORPORATION_LIST) {
			tGameHasPrivates =  corporationList.gameHasPrivates ();
		}
		
		return tGameHasPrivates;
	}
	
	public String getOperatingOwnerName () {
		String tGetOperatingOwnerName = null;
		
		if (corporationList != CorporationList.NO_CORPORATION_LIST) {
			tGetOperatingOwnerName = corporationList.getOperatingOwnerName ();
		}
		
		return tGetOperatingOwnerName;
	}
	
	public String getFormattedLastRevenue () {
		return Bank.formatCash (getLastRevenue ());
	}
	
	public String getFormattedThisRevenue () {
		return Bank.formatCash (getThisRevenue ());
	}
	
	public int getLastRevenue () {
		return TrainCompany.NO_REVENUE_GENERATED; 
	}
	
	// Train Company will override
	public boolean dividendsHandled () {
		return false; 
	}
	
	public String commonReason () {
		String tReason;
		
		tReason = ">> NO REASON <<";
		if (status == ActorI.ActionStates.OperatedTrain) {
			tReason = "Already Operated Train";
		}
		if (status == ActorI.ActionStates.HoldDividend) {
			tReason = "Already Operated Train and Dividend Held";
		}
		if (status == ActorI.ActionStates.HalfDividend) {
			tReason = "Already Operated Train and Half Dividend Paid";
		}
		if (status == ActorI.ActionStates.FullDividend) {
			tReason = "Already Operated Train and Full Dividend Paid";
		}
		if (status == ActorI.ActionStates.BoughtTrain) {
			tReason = "Already Bought Train";
		}
		if (status == ActorI.ActionStates.Operated) {
			tReason = "Company completed Operations";
		}
		
		return tReason;
	}
	
	// Train Company will override
	public void operateTrains () { 
		System.err.println ("Trying to -OPERATE TRAINS- Should be handled by Train Company");
	}
	
	// Train Company will override
	public void handleResetAllRoutes () {
		System.err.println ("Trying to -RESET ALL ROUTES- Should be handled by Train Company");
	}

	// Train Company will override
	public void clearTrainsFromMap () {
		System.err.println ("Trying to -CLEAR ALL TRAINS- Should be handled by Train Company");
	}
	
	public void repaintMapFrame () {
		corporationList.repaintMapFrame ();
	}
	
	public int getThisRevenue () { 
		return TrainCompany.NO_REVENUE_GENERATED; 
	}
	
	public void close () {
		if (! updateStatus (ActorI.ActionStates.Closed)) {
			System.err.println ("--> Failure to update State to Closed <--");
		}
	}
	
	// TODO: Build Unit Tests for forceClose methods, 
	// Refactor out tBank Calls.
	
	public void forceClose () {
		Bank tBank;
		int tCertificateCount;
		
		tBank = corporationList.getBank ();
		tCertificateCount = corporationCertificates.getCertificateTotalCount ();
		forceClose (tCertificateCount, tBank);
	}
	
	public void forceClose (int aCertificateCount, Bank aBank) {
		Certificate tCertificate;
		CertificateHolderI tCertificateHolder;
		Portfolio tClosedPortfolio;
		Portfolio tOwnerPortfolio;

		aBank = corporationList.getBank ();
		tClosedPortfolio = aBank.getClosedPortfolio ();
		if (aCertificateCount > 0) {
			for (int tIndex = 0; tIndex < aCertificateCount; tIndex++) {
				tCertificate = corporationCertificates.getCertificate (tIndex);
				tCertificateHolder = tCertificate.getOwner ();
				tOwnerPortfolio = (Portfolio) tCertificateHolder;
				tClosedPortfolio.transferOneCertificateOwnership (tOwnerPortfolio, tCertificate);
			}
		}
	}
	
	public void removeBenefitButtons () {
		
	}
	
	public void close (TransferOwnershipAction aTransferOwnershipAction) {
		Certificate tCertificate;
		CertificateHolderI tCertificateHolder;
		Portfolio tOwnerPortfolio;
		PortfolioHolderI tOwner;
		Bank tBank;
		int tCertificateCount;
		ActorI.ActionStates tOldState, tNewState;
		Portfolio tClosedPortfolio;
		
		tOldState = getActionStatus ();
		
		if (tOldState.equals (ActorI.ActionStates.Closed)) {
			System.err.println ("The Corporation " + name + " is already Closed... don't need to close again");
		} else if (updateStatus (ActorI.ActionStates.Closed)) {
			tNewState = getActionStatus ();
			tBank = corporationList.getBank ();
			removeBenefitButtons ();
			tCertificateCount = corporationCertificates.getCertificateTotalCount ();
			tClosedPortfolio = tBank.getClosedPortfolio ();
			if (tCertificateCount > 0) {
				for (int tIndex = 0; tIndex < tCertificateCount; tIndex++) {
					tCertificate = corporationCertificates.getCertificate (tIndex);
					tCertificateHolder = tCertificate.getOwner ();
					tOwnerPortfolio = (Portfolio) tCertificateHolder;
					tOwner = tOwnerPortfolio.getPortfolioHolder ();
					tClosedPortfolio.transferOneCertificateOwnership (tOwnerPortfolio, tCertificate);
					aTransferOwnershipAction.addTransferOwnershipEffect (tOwner, tCertificate, tBank);
					aTransferOwnershipAction.addCloseCorporationEffect (this, tOldState, tNewState);
				}
			}
		} else {
			System.err.println ("--> Failure to update State to Closed <--");
		}
	}
	
	public XMLElement createElement (XMLDocument aXMLDocument) {
		return aXMLDocument.createElement (EN_CORPORATION);
	}
	
	public boolean didPartiallyOperate () {
		boolean tDidPartiallyOperate;
		
		tDidPartiallyOperate = false;
		if ((status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.Tile2Laid) ||
			(status == ActorI.ActionStates.TileUpgraded) ||
			(status == ActorI.ActionStates.StationLaid) ||
			(status == ActorI.ActionStates.TileAndStationLaid) ||
			(status == ActorI.ActionStates.OperatedTrain) ||
			(status == ActorI.ActionStates.HoldDividend) ||
			(status == ActorI.ActionStates.HalfDividend) ||
			(status == ActorI.ActionStates.FullDividend) ||
			(status == ActorI.ActionStates.WaitingResponse) ||
			(status == ActorI.ActionStates.BoughtTrain)) {
			tDidPartiallyOperate = true;
		}
			
		return tDidPartiallyOperate;
	}
	
	public boolean didOperateTrain () {
		boolean tDidOperateTrain;
		
		tDidOperateTrain = false;
		if ((status == ActorI.ActionStates.OperatedTrain))  {
//			(status == ActorI.ActionStates.HoldDividend) ||
//			(status == ActorI.ActionStates.HalfDividend) ||
//			(status == ActorI.ActionStates.FullDividend) ||
//			(status == ActorI.ActionStates.BoughtTrain)) {
			tDidOperateTrain = true;
		}

		return tDidOperateTrain;
	}
	
	public boolean didOperate () { 
		boolean tDidOperate;
		
		tDidOperate = false;
		if (status == ActorI.ActionStates.Operated) {
			tDidOperate = true;
		}
		
		return tDidOperate;
	}
	
	public void prepareCorporation () {
		System.out.println ("Ready to Prepare Corporation  for Operating -- OVERRIDDING SHOULD HANDLE");
	}
	
	public void doneAction () {
		boolean tStatusUpdated;
		ActorI.ActionStates tCurrentStatus, tNewStatus;
		DoneCorpAction tDoneAction;
		OperatingRound tOperatingRound;
		String tOperatingRoundID;
		
		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.Operated);
		if (tStatusUpdated) {
			tNewStatus = status;
			tOperatingRoundID = getOperatingRoundID ();
			tDoneAction = new DoneCorpAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, this);
			tDoneAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
			tDoneAction.addNewActingCorpEffect (this);
			tDoneAction.addClearTrainsFromMapEffect (this);
			tOperatingRound = corporationList.getOperatingRound ();
			tOperatingRound.addAction (tDoneAction);
			corporationList.doneAction (this);
		}
		clearTrainsFromMap ();
		hideFrame ();
	}
	
	// Train Company will override
	public int getCash () {
		return 0;
	}

	public int getRevenue () {
		return 0;
	}
	
	public CorporationFrame getCorporationFrame () {
		return corporationFrame;
	}
	
	public String getCurrentRoundOf () {
		return corporationList.getCurrentRoundOf ();
	}
	
//	public int getCurrentOR () {
//		return corporationList.getCurrentOR ();
//	}

	public String getOperatingRoundID () {
		return corporationList.getOperatingRoundID ();
	}
	
	// Train Company will Override
	public void enterPlaceTileMode () {
		System.err.println ("Trying to Enter Place Tile Mode as Corporation - WRONG");
	}
	
	// Token Company will Override
	public void enterPlaceTokenMode () {
		System.err.println ("Trying to Enter Place Token Mode as Corporation - WRONG");
	}
	
	// Train Company will Override
	public void tileWasPlaced (MapCell aMapCell, Tile aTile, int aOrientation, 
			Tile aPreviousTile, int aPreviousTileOrientation, 
			String aPreviousTokens, String aPreviousBases) {
		System.err.println ("Trying to Verify Placed Tile as Corporation - WRONG");
	}
	
	// Token Company will Override
	public void tokenWasPlaced (MapCell aMapCell, Tile aTile, int aRevenueCenterIndex,
			boolean aAddLayTokenAction) {
		System.err.println ("Trying to Verify Place Token as Corporation - WRONG");
	}
	
	// Token Company will Override
	public boolean haveLaidAllBaseTokens () {
		return false;
	}
	
	// Token Company will Override
	public boolean canLayBaseToken () {
		return false;
	}
	
	// Train Company will Override
	public boolean hasTrainOfType (Train aTrain) {
		return false;
	}
	
	public void printCorporationInfo () {
		System.out.println ("Printing Corporation Info for " + getName ());
	}
	
	// For the Corporation List Columns
	public int fieldCount () {
		return 9;
	}
	
	public boolean gameHasLoans () {
		return false; 
		// TODO: non-1830 Expand to check Game Info to see if Loans are in game
	}
	
	@Override
	public String getAbbrev () {
		return abbrev;
	}
	
	public ActionStates getActionStatus () {
		return status;
	}
	
	@Override
	public Bank getBank () {
		return corporationList.getBank ();
	}
	
	public BankPool getBankPool () {
		return corporationList.getBankPool ();
	}
	
	public String getBidderNames () {
		String tBidderNames;
		
		tBidderNames = corporationCertificates.getBidderNames ();
		
		return tBidderNames;
	}
	
	public int getBidderCount () {
		int tBidderCount;
		
		tBidderCount = corporationCertificates.getBidderCount ();
		
		return tBidderCount;
	}

	public int getCapitalizationAmount () {
		int tCapitalizationAmount;
		
		// TODO: non-1830 Need to adjust for 1856 and others (maybe)
		tCapitalizationAmount = 10;

		return tCapitalizationAmount;
	}

	public Certificate getIPOCertificate (int aPercentage, boolean aPresidentShare) {
		return corporationCertificates.getIPOCertificate (aPercentage, aPresidentShare);
	}
	
	public Certificate getCertificate (int aPercentage, boolean aPresidentShare) {
		return corporationCertificates.getCertificate (aPercentage, aPresidentShare);
	}

	public int getCorporationCertificateCount () {
		return corporationCertificates.getCertificateCountAgainstLimit ();
	}
	
	public Certificate getCorporationCertificate (int aIndex) {
		return corporationCertificates.getCertificate (aIndex);
	}
	
	/* Add to XML Element Corporation Specific Information -- For Save File */
	public void getCorporationStateElement (XMLElement aXMLCorporationState) {
		aXMLCorporationState.setAttribute (AN_ABBREV, getAbbrev ());
		aXMLCorporationState.setAttribute (AN_CORP_STATUS, getStatusName ());
	}
	
	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLCorporationState;
		
		tXMLCorporationState = aXMLDocument.createElement (EN_CORPORATION);
		tXMLCorporationState.setAttribute (AN_CORP_STATUS, getStatusName ());

		return tXMLCorporationState;
	}
	
	public void appendOtherElements (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		XMLElement tPortfolioElements;
		
		tPortfolioElements = portfolio.getElements (aXMLDocument);
		aXMLCorporationState.appendChild (tPortfolioElements);
	}

	public int getCountOfSelectedCertificates () {
		return corporationCertificates.getCountOfCertificatesForBuy ();
	}
	
	// Share Company will Override
	public int getCountOfSelectedPrivates () {
		return 0;
	}
	
	@Override
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		return portfolio.getCurrentHolder (aLoadedCertificate);
	}

	public int getCurrentValue () {
		return NO_COST;
	}
	
	public String getDoLabel () {
		String tLabel;
		
		tLabel = getPresidentName () + " will operate " + getName ();
		
		return tLabel;
	}
	
	public String getOperatingLabel () {
		String tLabel;
		
		tLabel = getPresidentName () + " is operating " + getName ();
		
		return tLabel;
	}

	public ElementName getElementName () {
		return EN_CORPORATION;
	}
	
	// Train Company will override
	public int getFullShareDividend () {
		return 0;
	}

	public int getID () {
		return id;
	}
	
	public String getIDToString () {
		return (new Integer (id).toString ());
	}
	
	public GameManager getGameManager () {
		return corporationList.getGameManager ();
	}
	
	// Train Company will override
	public int getHalfShareDividend () {
		return 0;
	}

	public String getHomeCityGrid1 () {
		if (homeCityGrid1 == NO_HOME_GRID) {
			return NO_NAME_STRING;
		} else {
			return homeCityGrid1; // Should get from homeCity1.getID ();
		}
	}
	
	public String getHomeCityGrid2 () {
		if (homeCityGrid2 == NO_HOME_GRID) {
			return NO_NAME_STRING;
		} else {
			return homeCityGrid2; // Should get from homeCity2.getID ();
		}
	}
	
	public MapCell getHomeCity1 () {
		return homeCity1;
	}
	
	public MapCell getHomeCity2 () {
		return homeCity2;
	}
	
	public Location getHomeLocation1 () {
		return homeLocation1;
	}
	
	public Location getHomeLocation2 () {
		return homeLocation2;
	}
	
	public int getHomeLocation1Int () {
		if (homeLocation1 == Location.NO_LOC) {
			return NO_NAME_INT;
		} else {
			return homeLocation1.getLocation ();
		}
	}
	
	public int getHomeLocation2Int () {
		if (homeLocation2 == Location.NO_LOC) {
			return NO_NAME_INT;
		} else {
			return homeLocation2.getLocation ();
		}
	}
	
	// TokenCompany will Override
	public MapToken getMapToken () {
		return null;
	}

	@Override
	public String getName () {
		return name;
	}

	public int getOwnedPercentage () {
		return corporationCertificates.getCertificatePercentageFor (abbrev);
	}
	
	public PhaseInfo getCurrentPhaseInfo () {
		return corporationList.getCurrentPhaseInfo ();
	}
	
	// Corporation by default have no Discount. Privates Can, if they are Must Sell and all have passed
	// Private Company will Override
	public int getDiscount () {
		return 0;
	}
	
	// Basic Must Sell is false. Only with a Private that has this attribute is this overridden
	// Private Company will Override
	public boolean getMustSell () {
		return false;
	}
	
	// Basic Increase Discount -- only needed on Private Override.
	// Private Company will Override
	public void increaseDiscount () { }

	// Basic Set Discount -- only needed on Private Override and XML Load
	// Private Company will Override
	public void setDiscount (int aDiscount) { }
	
	// Train Company and Private Company will Override
	public String buildCorpInfoLabel () {
		return ">> OVERRIDE buildCorpInfoLabel <<";
	}
	
	public int getNextPresidentPercent () {
		return corporationCertificates.getNextPresidentPercent (this);
	}

	public int getPresidentPercent () {
		return corporationCertificates.getPresidentPercent (this);
	}

	public int getBankPoolPercentage () {
		return corporationCertificates.getBankPoolPercentage (this);
	}

	public int getPlayerOrCorpOwnedPercentage () {
		return corporationCertificates.getPlayerOrCorpOwnedPercentageFor (this);
	}
	
	@Override
	public Portfolio getPortfolio () {
		return portfolio;
	}

	@Override
	public Corporation getPortfolioHolder () {
		return this;
	}

	public String getPresidentName () {
		return corporationCertificates.getPresidentName ();
	}
	
	public PortfolioHolderI getPresident () {
		return corporationCertificates.getPresident ();
	}

	public Certificate getPresidentCertificate () {
		return corporationCertificates.getPresidentCertificate ();
	}
	
	public int getPercentOwned () {
		return corporationCertificates.getPercentOwned ();
	}

	public int getSharePrice () {
		return NO_COST;
	}
	
	public ActorI.ActionStates getStatus () {
		return status;
	}
	
	public String getStatusName () {
		return getStateName ();
	}
	
	@Override
	public String getStateName () {
		return status.toString ();
	}
	
	public String getType () {
		return CORPORATION;
	}
	
	// Token Company will Override
	public boolean haveMoneyForToken () {
		return false;
	}
	
	public void hideFrame () {
		corporationFrame.setVisible (false);
	}
	
	public boolean isActive () {
		boolean tIsActive;
		
		tIsActive = true;
		if (isClosed () || (getPercentOwned () == 0)) {
			tIsActive = false;
		}
		
		return tIsActive;
	}
	
	@Override
	public boolean isBank () {
		return false;
	}
	
	@Override
	public boolean isBankPool () {
		return false;
	}
	
	public boolean isClosed () {
		return (status == ActorI.ActionStates.Closed);
	}
	
	public boolean isStationLaid () {
		return (status == ActorI.ActionStates.StationLaid);
	}
	
	public boolean isPlayerOwned () {
		boolean tPresidentIsAPlayer;
		
		if (isClosed ()) {
			tPresidentIsAPlayer = false;
		} else {
			tPresidentIsAPlayer = corporationCertificates.isPresidentAPlayer ();
		}
		
		return tPresidentIsAPlayer;
	}
	
	// Private Company will override	
	@Override
	public boolean isAPrivateCompany () {
		return false;
	}
	
	// Coal Company will override
	public boolean isACoalCompany () {
		return false;
	}
	
	// Train Company will override
	@Override
	public boolean isATrainCompany () {
		return false;
	}
	
	// Minor Company will override
	public boolean isAMinorCompany () {
		return false;
	}
	
	// Token Company will override
	public boolean isATokenCompany () {
		return false;
	}
	
	// Share Company will override
	public boolean isAShareCompany () {
		return false;
	}
	
	// Share Company will override
	@Override
	public boolean isABankPool () {
		return false;
	}
	
	@Override
	public boolean isCompany () {
		return true;
	}
	
	public boolean isGovtRailway () {
		return govtRailway;
	}
	
	// Minor Company will override
	public boolean isMinorCompany () {
		return false;
	}
	
	@Override
	public boolean isPlayer () {
		return false;
	}

	public boolean isSelectedForBuy () {
		return corporationCertificates.isSelectedForBuy ();
	}
	
	// Share Company will override
	public boolean isShareCompany () {
		return false;
	}
	
	public boolean isSoldOut () {
		boolean isSoldOut;
		int tPercentOwned;
		
		isSoldOut = false;
		
		if (! isClosed ()) {
			tPercentOwned = getPlayerOrCorpOwnedPercentage ();
			if (tPercentOwned == 100) {
				isSoldOut = true;
			}
		}
		
		return isSoldOut;
	}
	
	public void removeAllBids () {
		corporationCertificates.removeAllBids ();
	}

	public boolean doesOwn (PrivateCompany aPrivateCompany) {
		boolean tDoesOwn = portfolio.containsPresidentShareOf (aPrivateCompany);
		
		return tDoesOwn;
	}

	public boolean doesNotOwn (PrivateCompany aPrivateCompany) {
		boolean tDoesNotOwn = ! portfolio.containsPresidentShareOf (aPrivateCompany);
		
		return tDoesNotOwn;
	}

	public boolean isUnowned () {
		return (status == ActorI.ActionStates.Unowned);
	}

	// Train Company will override
	public void payFullDividend () {
		System.err.println ("Trying to -PAY FULL DIVIDEND- Should be handled by Train Company");
	}
	
	// Train Company will override
	public void payNoDividend () {
		System.err.println ("Trying to -PAY NO DIVIDEND- Should be handled by Train Company");
	}
	
	// Train Company will override
	public String reasonForNoTileLay () {
		return "Corporation cannot lay Tile - Only Train Companies";
	}
	
	// Token Company will override
	public String reasonForNoTokenLay () {
		return "Corporation cannot lay Token - Only Token Companies";
	}
	
	// Train Company will override
	public String reasonForNoTrainOperation () {
		return "Corporation cannot Operate Trains - Only Train Companies";
	}
	
	// Share Company will override
	public String reasonForNoBuyPrivate () {
		return "Corporation cannot Buy Privates - Only Share Companies";
	}
	
	// Train Company will override
	public String reasonForNoDividendPayment () {
		return "Corporation has made no Revenues - No money to pay Dividends";
	}
	
	// Train Company will override
	public String reasonForNoBuyTrain () {
		return "Corporation cannot buy Trains - Only Train Companies";
	}

	// Train Company will override
	public String reasonForNoDividendOptions () {
		return ">> NO REASON <<";
	}

	public void showFrame () {
		Point tNewPoint;

		if (! corporationFrame.isVisible ()) {
			updateFrameInfo ();
			tNewPoint = corporationList.getOffsetRoundFrame ();
			corporationFrame.setLocation (tNewPoint);
			corporationFrame.setVisible (true);
		}
		corporationFrame.toTheFront ();
	}
	
	public void showMap () {
		if (! mapVisible ()) {
			corporationList.showMap ();
		}
		corporationList.bringMapToFront ();
	}
	
	public void showTileTray () {
		if (! tileTrayVisible ()) {
			corporationList.showTileTray ();
		}
	}
	
	public void updateFrameInfo () {
		if (corporationFrame != XMLFrame.NO_XML_FRAME) {
			corporationFrame.updateInfo ();
		}
	}
	
	public void loadStatus (XMLNode aXMLNode) {
		String tLoadedStatus, tCurrentStatus;
		ActorI.ActionStates tLoadedState;
		GenericActor tGenericActor;
		
		tLoadedStatus = aXMLNode.getThisAttribute (AN_CORP_STATUS);
		tCurrentStatus = getStatusName ();
		if (! tLoadedStatus.equals (tCurrentStatus)) {
			tGenericActor = new GenericActor ();
			tLoadedState = tGenericActor.getCorporationActionState (tLoadedStatus);
			resetStatus (tLoadedState);
		}
		loadStates (aXMLNode);
	}
	
	public void loadStates (XMLNode aXMLNode) {
		// Override with Train Company and Private Company
	}
	
	public boolean mapVisible () {
		return corporationList.mapVisible ();
	}
	
	public boolean tileTrayVisible () {
		return corporationList.tileTrayVisible ();
	}
	
	@Override
	public void replacePortfolioInfo (JPanel aPortfolioInfoJPanel) {
		// Note -- Will need to activate when CorporationFrame is built
		//	corporationFrame.replacePortfolioInfo (aPortfolioInfoJPanel);
	}

	public void printOwnershipReport () {
		System.out.println ("Who Owns this Corporation");
		corporationCertificates.printPortfolioInfo ();
		System.out.println ("What this Corporation Owns");
		portfolio.printPortfolioInfo ();
	}
	
	public void printReport () {
		System.out.println ("ID: " + id + " Name [" + name + "] Abbrev [" + abbrev + "]");
		System.out.println ("President Share Holder Name is: " + getPresidentName ());
		printOwnershipReport ();
	}
	
	public void setAbbrev (String aAbbrev) {
		abbrev = aAbbrev;
	}
	
	public void setCorporationList (CorporationList aCorporationList) {
		corporationList = aCorporationList;
	}
	
	public void setCorporationFrame () {
		String tFullTitle;
		GameManager tGameManager;
		boolean tIsNetworkGame;
		
		if (isATrainCompany ()) {
			tGameManager = corporationList.getGameManager ();
			tIsNetworkGame = tGameManager.isNetworkGame ();
			tFullTitle = tGameManager.createFrameTitle ("Corporation");
			corporationFrame = new CorporationFrame (tFullTitle, this, tIsNetworkGame);
		} else {
			corporationFrame = (CorporationFrame) CorporationFrame.NO_XML_FRAME;
		}
	}

	public void setHome1 (MapCell aHomeCity, Location aHomeLocation) {
		homeCity1 = aHomeCity;
		homeLocation1 = aHomeLocation;
	}
	
	public void setHome2 (MapCell aHomeCity, Location aHomeLocation) {
		homeCity2 = aHomeCity;
		homeLocation2 = aHomeLocation;
	}
	
	public void setName (String aName) {
		name = aName;
	}
	
	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		setPrimaryActionState (aPrimaryActionState);
	}

	public void setPrimaryActionState (ActorI.ActionStates aStatus) {
		resetStatus (aStatus);
	}
	
	public void resetStatus (ActorI.ActionStates aStatus) {
		status = aStatus;
		updateFrameInfo ();
	}
	
	public void setStatus (ActorI.ActionStates aStatus) {
		if (status == ActorI.NO_STATE) {
			status = aStatus;
		} else if (status == ActorI.ActionStates.Unowned) {
			status = aStatus;
		} else {
			updateStatus (aStatus);
		}
	}
	
	public boolean updateStatus (ActorI.ActionStates aStatus) {
		boolean tStatusUpdated;
		
		tStatusUpdated = false;
		if (aStatus == ActorI.ActionStates.WaitingResponse) {
			status = aStatus;
			tStatusUpdated = true;
		} else if (status == ActorI.ActionStates.Owned) {
			if ((aStatus == ActorI.ActionStates.MayFloat) || 
				(aStatus == ActorI.ActionStates.WillFloat) ||
				(aStatus == ActorI.ActionStates.Closed)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.MayFloat) {
			if ((aStatus == ActorI.ActionStates.Owned) || 
				(aStatus == ActorI.ActionStates.NotOperated)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.WillFloat) {
			if (aStatus == ActorI.ActionStates.NotOperated) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.NotOperated) {
			if (aStatus == ActorI.ActionStates.StartedOperations) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.StartedOperations) {
			if ((aStatus == ActorI.ActionStates.TileLaid) || 
				(aStatus == ActorI.ActionStates.TileUpgraded) ||
				(aStatus == ActorI.ActionStates.StationLaid) ||
				(aStatus == ActorI.ActionStates.OperatedTrain) ||
				(aStatus == ActorI.ActionStates.HoldDividend) ||
				(aStatus == ActorI.ActionStates.HalfDividend) ||
				(aStatus == ActorI.ActionStates.FullDividend)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.Tile2Laid) {
			if (aStatus == ActorI.ActionStates.StationLaid) { 
				status = ActorI.ActionStates.TileAndStationLaid;
				tStatusUpdated = true;
			} else if ((aStatus == ActorI.ActionStates.OperatedTrain) ||
				(aStatus == ActorI.ActionStates.HoldDividend) ||
				(aStatus == ActorI.ActionStates.HalfDividend) ||
				(aStatus == ActorI.ActionStates.FullDividend)) { 
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.TileLaid) {
			if ((aStatus == ActorI.ActionStates.Tile2Laid) || 
				(aStatus == ActorI.ActionStates.StationLaid) ||
				(aStatus == ActorI.ActionStates.TileAndStationLaid) ||
				(aStatus == ActorI.ActionStates.OperatedTrain) ||
				(aStatus == ActorI.ActionStates.HoldDividend) ||
				(aStatus == ActorI.ActionStates.HalfDividend) ||
				(aStatus == ActorI.ActionStates.FullDividend)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.TileAndStationLaid) {
			if (aStatus == ActorI.ActionStates.StationLaid) {
				tStatusUpdated = true;
			} else if ((aStatus == ActorI.ActionStates.OperatedTrain) ||
				(aStatus == ActorI.ActionStates.HoldDividend) ||
				(aStatus == ActorI.ActionStates.HalfDividend) ||
				(aStatus == ActorI.ActionStates.FullDividend)) { 
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.StationLaid) {
			if ((aStatus == ActorI.ActionStates.TileAndStationLaid) ||
				(aStatus == ActorI.ActionStates.StationLaid) ||
				(aStatus == ActorI.ActionStates.OperatedTrain) ||
				(aStatus == ActorI.ActionStates.HoldDividend) ||
				(aStatus == ActorI.ActionStates.HalfDividend) ||
				(aStatus == ActorI.ActionStates.FullDividend)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.TileUpgraded) {
			if ((aStatus == ActorI.ActionStates.StationLaid) ||
				(aStatus == ActorI.ActionStates.TileAndStationLaid) ||
				(aStatus == ActorI.ActionStates.OperatedTrain) ||
				(aStatus == ActorI.ActionStates.HoldDividend) ||
				(aStatus == ActorI.ActionStates.HalfDividend) ||
				(aStatus == ActorI.ActionStates.FullDividend)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.OperatedTrain) {
			if ((aStatus == ActorI.ActionStates.HoldDividend) ||
				(aStatus == ActorI.ActionStates.HalfDividend) ||
				(aStatus == ActorI.ActionStates.FullDividend)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if ((status == ActorI.ActionStates.HoldDividend) ||
					(status == ActorI.ActionStates.HalfDividend) || 
					(status == ActorI.ActionStates.FullDividend)) {
			if ((aStatus == ActorI.ActionStates.BoughtTrain) ||
				(aStatus == ActorI.ActionStates.Operated)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if ((status == ActorI.ActionStates.BoughtTrain)) {
			if (aStatus == ActorI.ActionStates.Operated) {
				status = aStatus;
				tStatusUpdated = true;
			}
		}
		
//		ActorI.ActionStates -- CorporationStates
//
//		Unowned, Owned, Closed, MayFloat, WillFloat, NotOperated,
//		StartedOperations, TileLaid, Tile2Laid, TileUpgraded, 
//		StationLaid, TileAndStationLaid, OperatedTrain, HoldDividend, 
//		HalfDividend, FullDividend, BoughtTrain, Operated, WaitingResponse
		
		return tStatusUpdated;
	}
	
	private void setValues (int aID, String aName, String aAbbrev, MapCell aHomeCity1, 
			Location aHomeLocation1, MapCell aHomeCity2, Location aHomeLocation2, 
			ActorI.ActionStates aStatus, boolean aGovtRailway) {
		corporationCertificates = new Portfolio (this);
		id = aID;
		setName (aName);
		setAbbrev (aAbbrev);
		setHome1 (aHomeCity1, aHomeLocation1);
		setHome2 (aHomeCity2, aHomeLocation2);
		setStatus (aStatus);
		govtRailway = aGovtRailway;
	}
	
	public boolean canOperate () {
		boolean tCanOperate;
		
		tCanOperate = true;
		if ((status == ActorI.ActionStates.Closed) ||
			(status == ActorI.ActionStates.Unowned) || 
			(status == ActorI.ActionStates.Owned)) {
			tCanOperate = false;
		}
		
		return tCanOperate;
	}
	
	public boolean isOperating () {
		boolean isOperating;
		
		isOperating = true;
		if ((status == ActorI.ActionStates.Closed) ||
			(status == ActorI.ActionStates.Unowned) || 
			(status == ActorI.ActionStates.WillFloat) || 
			(status == ActorI.ActionStates.MayFloat) || 
			(status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.Operated) ||
			(status == ActorI.ActionStates.NotOperated)) {
			isOperating = false;
		}
		
		return isOperating;
	}
	
	public boolean shouldOperate () {
		boolean tShouldOperate;
		
		tShouldOperate = true;
		if ((status == ActorI.ActionStates.Closed) ||
			(status == ActorI.ActionStates.Unowned) || 
			(status == ActorI.ActionStates.Owned) || 
			(status == ActorI.ActionStates.Operated)) {
			tShouldOperate = false;
		}
		
		return tShouldOperate;
	}

	public void sortCorporationCertificates () {
		corporationCertificates.sortByOwners ();
	}
	
	public void undoAction () {
		corporationList.undoAction ();
		/* Need to test if the state of the company should leave the window open or not */
		if (status == ActorI.ActionStates.NotOperated) {
			hideFrame ();
		}
	}
	
	public void updateOwnerPortfolios () {
		corporationCertificates.updateCertificateOwnersInfo ();
	}

	/* Sort Methods for proper ordering */
	@Override
	public int compareTo (Corporation aCorporation) {
		return compareID (aCorporation);
	}
	
	public int compareID (Corporation aCorporation) {
		return id - aCorporation.getID ();
	}
	
	public int compareActive (Corporation aCorporation) {
		boolean tIsActive1, tIsActive2;
		int tCompareActive;
		
		tIsActive1 = isActive ();
		tIsActive2 = aCorporation.isActive ();
		tCompareActive = 0;
		if (tIsActive1) {
			if (tIsActive2) {
				tCompareActive = 0;
			} else {
				tCompareActive = SORT_CO1_BEFORE_CO2;
			}
		} else if (tIsActive2) {
			tCompareActive = SORT_CO2_BEFORE_CO1;
		} else {
			tCompareActive = 0;
		}
		
		return tCompareActive;
	}
	
	public int comparePartiallyOperated (Corporation aCorporation) {
		int tComparePartiallyOperated;
		
		if (didPartiallyOperate ()) {
			tComparePartiallyOperated = SORT_CO1_BEFORE_CO2;
		} else if (aCorporation.didPartiallyOperate ()) {
			tComparePartiallyOperated = SORT_CO2_BEFORE_CO1;
		} else {
			tComparePartiallyOperated = 0;
		}
		
		return tComparePartiallyOperated;
	}
	
	public int compareCanOperate (Corporation aCorporation) {
		boolean tCanOperate1, tCanOperate2;
		int tCompareCanOperate;
		TrainCompany tCompany1, tCompany2;
		
		if (this.isAShareCompany ()) {
			tCompany1 = (TrainCompany) this;
			tCompany2 = (TrainCompany) aCorporation;
			tCanOperate1 = tCompany1.canOperate ();
			tCanOperate2 = tCompany2.canOperate ();
			tCompareCanOperate = 0;
			if (tCanOperate1) {
				if (tCanOperate2) {
					tCompareCanOperate = 0;
				} else {
					tCompareCanOperate = SORT_CO1_BEFORE_CO2;
				}
			} else if (tCanOperate2) {
				tCompareCanOperate = SORT_CO2_BEFORE_CO1;
			} else {
				tCompareCanOperate = 0;
			}
		} else {
			tCompareCanOperate = 0;
		}
		
		return tCompareCanOperate;
	}

	public int compareClosed (Corporation aCorporation) {
		boolean tIsClosed1, tIsClosed2;
		int tCompareClosed;
		
		tIsClosed1 = isClosed ();
		tIsClosed2 = aCorporation.isClosed ();
		tCompareClosed = 0;
		if (tIsClosed1) {
			if (tIsClosed2) {
				tCompareClosed = 0;
			} else {
				tCompareClosed = SORT_CO2_BEFORE_CO1;
			}
		} else if (tIsClosed2) {
			tCompareClosed = SORT_CO1_BEFORE_CO2;
		} else {
			tCompareClosed = 0;
		}
		
		return tCompareClosed;
	}
	
	public int compareMarketCellLtoR (Corporation aCorporation) {
		ShareCompany tShare1, tShare2;
		MarketCell tMarketCell1, tMarketCell2;
		int tCompareMarketCellLtoR;
		
		tShare1 = (ShareCompany) this;
		tShare2 = (ShareCompany) aCorporation;
		tMarketCell1 = tShare1.getSharePriceMarketCell ();
		tMarketCell2 = tShare2.getSharePriceMarketCell ();
		tCompareMarketCellLtoR = 0;
		
		if ((tMarketCell1 != MarketCell.NO_MARKET_CELL) && 
			(tMarketCell2 != MarketCell.NO_MARKET_CELL)) {
			if (tMarketCell1 != tMarketCell2) {
				if (tMarketCell1.isRightOf (tMarketCell2)) {
					tCompareMarketCellLtoR = SORT_CO2_BEFORE_CO1;
				} else {
					if (tMarketCell2.isRightOf (tMarketCell1)) {
						tCompareMarketCellLtoR = SORT_CO1_BEFORE_CO2;
					}
				}
			}
		}

		return tCompareMarketCellLtoR;
	}
	
	public int compareMarketCellDtoU (Corporation aCorporation) {
		ShareCompany tShare1, tShare2;
		MarketCell tMarketCell1, tMarketCell2;
		int tCompareMarketCellDtoU;
		
		tShare1 = (ShareCompany) this;
		tShare2 = (ShareCompany) aCorporation;
		tMarketCell1 = tShare1.getSharePriceMarketCell ();
		tMarketCell2 = tShare2.getSharePriceMarketCell ();
		tCompareMarketCellDtoU = 0;
		
		if ((tMarketCell1 != MarketCell.NO_MARKET_CELL) && 
			(tMarketCell2 != MarketCell.NO_MARKET_CELL)) {
			if (tMarketCell1 != tMarketCell2) {
				if (tMarketCell1.isAbove (tMarketCell2)) {
					tCompareMarketCellDtoU = SORT_CO2_BEFORE_CO1;
				} else {
					if (tMarketCell2.isAbove (tMarketCell1)) {
						tCompareMarketCellDtoU = SORT_CO1_BEFORE_CO2;
					}
				}
			}
		}

		return tCompareMarketCellDtoU;
	}
	
	public int compareShare (Corporation aCorporation) {
		int tCompareShare;
		
		if ((this.isAShareCompany ()) && 
			(aCorporation.isAShareCompany ())) {
			tCompareShare = 0;
		} else {
			tCompareShare = compareID (aCorporation);
		}
		
		return tCompareShare;
	}
	
	public int compareStackLocation (Corporation aCorporation) {
		ShareCompany tShare;
		MarketCell tMarketCell;
		int tCompareStackLocation;
		
		tShare = (ShareCompany) this;
		tMarketCell = tShare.getSharePriceMarketCell ();
		if (tMarketCell != MarketCell.NO_SHARE_PRICE) {
			tCompareStackLocation = tMarketCell.compareStackLocation (this, aCorporation);
		} else {
			tCompareStackLocation = 0;
		}
		
		return tCompareStackLocation;
	}
	
	public int comparePrice (Corporation aCorporation) {
		int tPriceDiff;
		
		if ((aCorporation.getSharePrice () == NO_COST) && 
			(getSharePrice () == NO_COST)) {
			tPriceDiff = 0;
		} else if (aCorporation.getSharePrice () == NO_COST) {
			tPriceDiff = SORT_CO1_BEFORE_CO2;
		} else if (getSharePrice () == NO_COST) {
			tPriceDiff = SORT_CO2_BEFORE_CO1;
		} else {
			tPriceDiff = aCorporation.getSharePrice () - getSharePrice ();
		}
		
		return tPriceDiff;
	}
	
	public static Comparator<Corporation> CorporationOperatingOrderComparator 
    			= new Comparator<Corporation>() {
		
		@Override
		public int compare (Corporation aCorporation1, Corporation aCorporation2) {
			int tOperatingOrderValue, tClosedCompare;
			
			tOperatingOrderValue = aCorporation1.compareActive (aCorporation2);
			if (tOperatingOrderValue == 0) {	// Both Companies are Active
				tOperatingOrderValue = aCorporation1.comparePartiallyOperated (aCorporation2);
				if (tOperatingOrderValue == 0) { // Both Companies can Operate
					tOperatingOrderValue = aCorporation1.compareCanOperate (aCorporation2);
				}
				if (tOperatingOrderValue == 0) { // Neither Company is Partially Operated
					tOperatingOrderValue = aCorporation1.comparePrice (aCorporation2);
				}
				if (tOperatingOrderValue == 0) { // Both Companies have Same Price
					tOperatingOrderValue = aCorporation1.compareShare (aCorporation2);
				}
				if (tOperatingOrderValue == 0) { // Both Companies are Share Companies
					tOperatingOrderValue = aCorporation1.compareMarketCellLtoR (aCorporation2);
				}
				if (tOperatingOrderValue == 0) { // Both Companies are in Same Market Column
					tOperatingOrderValue = aCorporation1.compareMarketCellDtoU (aCorporation2);
				}
				if (tOperatingOrderValue == 0) { // Both Companies are in Same Market Cell
					tOperatingOrderValue = aCorporation1.compareStackLocation (aCorporation2);
				}
			} else {
				tClosedCompare = aCorporation1.compareClosed (aCorporation2);
				if (tClosedCompare != 0) {
					tOperatingOrderValue = tClosedCompare;
				}
			}
			
			if (tOperatingOrderValue == 0) {
				tOperatingOrderValue = aCorporation1.compareID (aCorporation2);
			}
			
			//ascending order
			return tOperatingOrderValue;
		}
	};

	public int getTrainCount () {
		return 0;
	}

	// Overriden in Train Company
	public JPanel buildPortfolioTrainsJPanel (CorporationFrame corporationFrame2, 
			GameManager aGameManager, boolean aFullTrainPortfolio, 
			boolean aCanBuyTrain, String aDisableToolTipReason, 
			Corporation aBuyingCorporation) {
		return null;
	}
	
	public TrainHolderI getLocalSelectedTrainHolder () {
		return TrainPortfolio.NO_TRAIN_HOLDER;
	}

	// From here to end loading the Privates owned by a Corporation when Loading a Saved Game
	public void loadPortfolio (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;
		
		tXMLNodeList = new XMLNodeList (portfolioParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Portfolio.EN_PORTFOLIO);
	}

	ParsingRoutineI portfolioParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			portfolio.loadPortfolio (aChildNode);
		}
	};

	// Routines needed by a Train Company to test for Emergency Train Buy
	// Train company will Override
	public void forceBuyTrain () {
	}

	public Train getCheapestBankTrain () {
		return Train.NO_TRAIN;
	}

	// Train Company will Override these
	public void setMustBuyTrain (boolean b) {
	}
	
	public boolean mustBuyTrain () {
		return false;
	}
	
	public boolean mustBuyTrainNow () {
		return false;
	}
	
	public boolean hasNoTrain () {
		return true;
	}

	public boolean canBuyTrainInPhase () {
		boolean tCanBuyTrainInPhase;
		
		tCanBuyTrainInPhase = corporationList.canBuyTrainInPhase ();
		
		return tCanBuyTrainInPhase;
	}

	// Train Company will Override
	public void getLoan () {
	}

	// Train Company will Override
	public void paybackLoan () {
	}

	public Border setupBorder () {
		Border tCorpBorder;
		
		tCorpBorder = BorderFactory.createLineBorder (Color.black, 1);

		return tCorpBorder;
	}

	@Override
	public boolean isAPlayer () {
		return false;
	}
	
	@Override
	public boolean isAStockRound () {
		return false;
	}

	@Override
	public boolean isAOperatingRound () {
		return false;
	}
	
	@Override
	public boolean isABank () {
		return false;
	}

	@Override
	public boolean isACorporation () {
		return true;
	}
	
	public String getNote () {
		return NO_NOTE;
	}

	public void repaintCorporationFrame () {
		corporationFrame.revalidate ();
		corporationFrame.repaint ();
	}
	
	public boolean isTileAvailableForMapCell (MapCell aMapCell) {
		boolean tIsTileAvailableForMapCell;
		
		tIsTileAvailableForMapCell = aMapCell.isTileAvailableForMapCell ();
		
		return tIsTileAvailableForMapCell;
	}
	
	public boolean homeMapCell1HasTile () {
		boolean tHomeMapCellHasTile;
		
		tHomeMapCellHasTile = false;
		if (homeCity1 != MapCell.NO_MAP_CELL) {
			tHomeMapCellHasTile = homeCity1.isTileOnCell ();
		} else {
			System.err.println ("Home City 1 Map Cell is NOT SET -- PROBLEM!");
		}
		
		return tHomeMapCellHasTile;
	}
	
	public boolean homeMapCell2HasTile () {
		boolean tHomeMapCellHasTile;
		
		tHomeMapCellHasTile = false;
		if (homeCity2 != MapCell.NO_MAP_CELL) {
			tHomeMapCellHasTile = homeCity2.isTileOnCell ();
		} else {
			tHomeMapCellHasTile = true;
		}
		
		return tHomeMapCellHasTile;
	}

	public void enterSelectRouteMode (RouteInformation aRouteInformation) {
//		Override in Train Company Class
		
	}
	
	public void exitSelectRouteMode () {
//		Override in Train Company Class
	}

	public abstract boolean atTrainLimit ();

	public void skipBaseToken () {
//		Override in Train Company Class		
	}

	protected boolean isPlaceTileMode () {
		return corporationList.isPlaceTileMode ();
	};

	protected boolean isPlaceTokenMode () {
		return corporationList.isPlaceTokenMode ();
	}

	public boolean isWaitingForResponse () {
		boolean tIsWaitingForResponse;
		
		tIsWaitingForResponse = false;
		if (status.equals (ActorI.ActionStates.WaitingResponse)) {
			tIsWaitingForResponse = true;
		}
		
		return tIsWaitingForResponse;
	}
	
	public void setTestingFlag (boolean aGameTestFlag) {
		gameTestFlag = aGameTestFlag;
	}

	public void placeBaseTokens () {
		// Override in Token Company Class
	}

	public int getCostToLayToken(MapCell tMapCell) {
		// Override in Token Company Class
		return 0;
	}

	public PrivateCompany getSelectedPrivateToBuy() {
		// Override in Share Company Class
		return null;
	}

	public CashHolderI getCashHolderByName (String aCashHolderName) {
		CashHolderI tCashHolder;
		
		tCashHolder = corporationList.getCashHolderByName (aCashHolderName);
		
		return tCashHolder;
	}

	public void handlePlaceTile () {
		corporationFrame.handlePlaceTile ();
	}
	
	public boolean hasPlacedAnyStation () {
		boolean tHasPlacedAnyStation = false;
		MapFrame tMapFrame;
		
		tMapFrame = corporationList.getMapFrame ();
		tHasPlacedAnyStation = tMapFrame.hasStation (getID ());
		
		return tHasPlacedAnyStation;
	}
	
	protected void configurePrivateBenefitButtons (JPanel aButtonRow) {
		removeAllBenefitButtons (aButtonRow);
		portfolio.configurePrivateCompanyBenefitButtons (aButtonRow);
	}

	private void removeAllBenefitButtons (JPanel aButtonRow) {
		CorporationList tPrivates;
		PrivateCompany tPrivate;
		int tCount, tIndex;
		
		tPrivates = corporationList.getPrivates ();
		tCount = tPrivates.getCountOfOpen ();
		if (tCount > 0) {
			for (tIndex = 0; tIndex < tCount; tIndex++) {
				tPrivate = (PrivateCompany) tPrivates.getCorporation (tIndex);
				if (tPrivate.hasActiveCompanyBenefits ()) {
					tPrivate.removeBenefitButtons (aButtonRow);
				}
			}
		}
	}
	
	public void fillCorporationTrains (ButtonsInfoFrame aButtonsInfoFrame) {
		
	}
	
	@Override
	public abstract void completeBenefitInUse ();

	protected abstract boolean choiceForBaseToken ();

	public int getSmallestSharePercentage () {
		return corporationCertificates.getSmallestSharePercentage ();
	}

	public boolean isLoading () {
		return corporationList.isLoading ();
	}
}
