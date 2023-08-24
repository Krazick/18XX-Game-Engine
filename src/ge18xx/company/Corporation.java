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
import java.util.List;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.apache.logging.log4j.Logger;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.License.LicenseTypes;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.Benefits;
import ge18xx.game.ButtonsInfoFrame;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.market.MarketCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.CashHolderI;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.round.OperatingRound;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.DeclareBankruptcyAction;
import ge18xx.round.action.DoneCorpAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.LayTokenAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.round.action.effects.Effect;
import ge18xx.tiles.Tile;
import ge18xx.toplevel.MapFrame;
import ge18xx.toplevel.XMLFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.MessageBean;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public abstract class Corporation extends Observable implements PortfolioHolderLoaderI, 
										ParsingRoutineI, Comparable<Corporation> {
	public static final String CORPORATION = "Corporation";
	public static final String COMPANIES = "Companies";
	public static final ElementName EN_CORPORATION = new ElementName (CORPORATION);
	public static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_ABBREV = new AttributeName ("abbrev");
	public static final AttributeName AN_HOMECELL1 = new AttributeName ("homeCell1");
	public static final AttributeName AN_HOMECELL2 = new AttributeName ("homeCell2");
	public static final AttributeName AN_HOME_TYPE= new AttributeName ("homeType");
	public static final AttributeName AN_CORP_STATUS = new AttributeName ("status");
	public static final AttributeName AN_GOVT_RAILWAY = new AttributeName ("govtRailway");
	public static final AttributeName AN_FORMATION_PHASE = new AttributeName ("formationPhase");
	public static final AttributeName AN_FORMATION_REQUIREMENT = new AttributeName ("formationRequirement");
	public static final AttributeName AN_FORMATION_MADATORY_PHASE = new AttributeName ("formationMandatoryPhase");
	public static final String CORPORATION_STATUS_CHANGE = "CORPORATION STATUS CHANGE";
	public static final String NO_NOTE = GUI.EMPTY_STRING;
	public static final String NO_REASON = ">>NO REASON<<";
	public static final String NO_PRESIDENT = GUI.EMPTY_STRING;
	public static final String NO_ABBREV = GUI.NULL_STRING;
	public static final String NO_NAME_STRING = "<NONE>";
	public static final String PRIVATE_COMPANY = "Private";
	public static final String MINOR_COMPANY = "Minor";
	public static final String SHARE_COMPANY = "Share";
	public static final String NO_NAME = ActorI.NO_NAME;
	public static final String FORMATION_PHASE1 = "1";
	public static final String HOME_TYPE_CHOICE = "choice";
	public static final String HOME_TYPE_BOTH = "both";
	public static final int NO_ID = 0;
	public static final Corporation NO_CORPORATION = null;
	static final String enum_closed = ActionStates.Closed.toString ();
	static final String enum_operated = ActionStates.Operated.toString ();
	static final String enum_not_operated = ActionStates.NotOperated.toString ();
	static final String NO_HOME_GRID = GUI.NULL_STRING;
	static final int NO_COST = -1;
	static final int NO_NAME_INT = -1;
	static final int SORT_CO1_BEFORE_CO2 = -100;
	static final int SORT_CO2_BEFORE_CO1 = 100;
	int loanAmount = 0;
	int loanInterest = 0;
	boolean gameTestFlag = false;
	boolean govtRailway;
	int id;
	String name;
	String abbrev;
	String homeCityGrid1;
	String homeCityGrid2;
	String formationPhase;
	String formationRequirement;
	String formationManadatoryPhase;
	MessageBean bean;
	MapCell homeCity1;
	Location homeLocation1;
	MapCell homeCity2;
	Location homeLocation2;
	String homeType;
	ActorI.ActionStates status;
	ActorI.ActorTypes actorType = ActorI.ActorTypes.Corporation;
	CorporationFrame corporationFrame;
	CorporationList corporationList;
	Benefit benefitInUse;
	Portfolio portfolio; 	// All Certificates Owned by the Corporation (Privates, Minors, it's own,
							// and others). Use this portfolio to find what this company can sell.
	
	Portfolio corporationCertificates; 	// A copy of all of this Corporation's Certificates -- 
										// regardless of who owns them.
										// Use this portfolio to pay-out dividends -- to the owners. 
										// It goes in here once, and never leaves.

	public Corporation () {
		this (NO_ID, NO_NAME);
	}

	public Corporation (int aID, String aName) {
		this (aID, aName, NO_ABBREV, MapCell.NO_MAP_CELL, Location.NO_LOC, MapCell.NO_MAP_CELL, Location.NO_LOC,
				ActorI.ActionStates.Unowned, false);
	}

	public Corporation (int aID, String aName, String aAbbrev, MapCell aHomeCity1, Location aHomeLocation1,
			MapCell aHomeCity2, Location aHomeLocation2, ActorI.ActionStates aStatus, boolean aGovtRailway) {
		String tActorType;
		
		tActorType = actorType.toString () + " " + aAbbrev;
		bean = new MessageBean (tActorType);
		setValues (aID, aName, aAbbrev, aHomeCity1, aHomeLocation1, aHomeCity2, aHomeLocation2, 
					aStatus, aGovtRailway);
	}

	/**
	 * Parse Corporation Node from XML Node - For Initial Load from Data file of Corporations
	 * 
	 * @param aXMLNode The Node to Parse
	 * @param aCorporationList 
	 */
	public Corporation (XMLNode aXMLNode, CorporationList aCorporationList) {
		XMLNodeList tXMLNodeList;
		String tName;
		String tAbbrev;
		String tHomeType;
		String tActorType;
		
		corporationCertificates = new Portfolio (this);
		portfolio = new Portfolio (this);
		id = aXMLNode.getThisIntAttribute (AN_ID);

		tName = aXMLNode.getThisAttribute (AN_NAME);
		setName (tName);

		tAbbrev = aXMLNode.getThisAttribute (AN_ABBREV);
		setAbbrev (tAbbrev);
		
		tActorType = actorType.toString () + " " + getAbbrev ();
		bean = new MessageBean (tActorType);

		parseHomeCities (aXMLNode);
		
		tHomeType = aXMLNode.getThisAttribute (AN_HOME_TYPE);
		setHomeType (tHomeType);
		
		loadFormationInfo (aXMLNode);

		setStatus (aXMLNode);

		tXMLNodeList = new XMLNodeList (this);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Certificate.EN_CERTIFICATE, Benefits.EN_BENEFITS);
		setCorporationList (aCorporationList);
		setBenefitInUse (Benefit.FAKE_BENEFIT);
	}

	public JPanel getButtonPanel () {
		return corporationFrame.getButtonPanel ();
	}
	
	public MessageBean getMessageBean () {
		return bean;
	}
	
	public void setLoanInfo (int aLoanAmount, int aLoanInterest) {
		loanAmount = aLoanAmount;
		loanInterest = aLoanInterest;
	}
	
	private void parseHomeCities (XMLNode aXMLNode) {
		int tLocation;
		String tHomeCityGrid1;
		String tHomeCityGrid2;
		
		tHomeCityGrid1 = aXMLNode.getThisAttribute (AN_HOMECELL1);
		setHomeCityGrid1 (tHomeCityGrid1);
		tLocation = aXMLNode.getThisIntAttribute (Location.AN_HOME_LOCATION1);
		homeLocation1 = new Location (tLocation);

		tHomeCityGrid2 = aXMLNode.getThisAttribute (AN_HOMECELL2);
		setHomeCityGrid2 (tHomeCityGrid2);
		tLocation = aXMLNode.getThisIntAttribute (Location.AN_HOME_LOCATION2, Location.NO_LOCATION);
		homeLocation2 = new Location (tLocation);
	}

	private void loadFormationInfo (XMLNode aXMLNode) {
		String tFormationPhase;
		String tFormationRequirement;
		String tFormationManadatoryPhase;

		tFormationPhase = aXMLNode.getThisAttribute (AN_FORMATION_PHASE, FORMATION_PHASE1);
		formationPhase = tFormationPhase;
		tFormationRequirement = aXMLNode.getThisAttribute (AN_FORMATION_REQUIREMENT, NO_NAME_STRING);
		formationRequirement = tFormationRequirement;
		tFormationManadatoryPhase = aXMLNode.getThisAttribute (AN_FORMATION_MADATORY_PHASE, NO_NAME_STRING);
		formationManadatoryPhase = tFormationManadatoryPhase;
	}

	public void removeHomeBases () {
		MapCell tMapCell;

		tMapCell = getHomeCity1 ();
		if (tMapCell != MapCell.NO_MAP_CELL) {
			tMapCell.clearCorporation (this);
			setHome1 (MapCell.NO_MAP_CELL, Location.NO_LOC);
		}
		setHomeCityGrid1 (NO_HOME_GRID);
		tMapCell = getHomeCity2 ();
		if (tMapCell != MapCell.NO_MAP_CELL) {
			tMapCell.clearCorporation (this);
			setHome2 (MapCell.NO_MAP_CELL, Location.NO_LOC);
		}
		setHomeCityGrid2 (NO_HOME_GRID);
	}

	public Logger getLogger () {
		Logger tLogger;

		tLogger = corporationList.getLogger ();

		return tLogger;
	}

	public void setBenefitInUse (Benefit aBenefitInUse) {
		benefitInUse = aBenefitInUse;
	}

	public Benefit getBenefitInUse () {
		return benefitInUse;
	}

	public String getFormationPhase () {
		return formationPhase;
	}

	public String getFormationRequirement () {
		return formationRequirement;
	}

	public String getFormationManadatoryPhase () {
		return formationManadatoryPhase;
	}

	private void setStatus (XMLNode aXMLNode) {
		String tStatus;
		String tUnowned;
		ActorI.ActionStates tActionStatus;
		GenericActor tGenericActor;

		tUnowned = ActorI.ActionStates.Unowned.toString ();
		tStatus = aXMLNode.getThisAttribute (AN_CORP_STATUS, tUnowned);
		if (tStatus.equals (tUnowned)) {
			if (!formationPhase.equals (FORMATION_PHASE1)) {
				tStatus = ActorI.ActionStates.Unformed.toString ();
			}
		}
		tGenericActor = new GenericActor ();
		tActionStatus = tGenericActor.getCorporationActionState (tStatus);
		setStatus (tActionStatus);
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
		Certificate tCertificate;
		int tCertificateQuantity;
		int tQtyIndex;

		tCertificate = new Certificate (aChildNode, this);
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
		aCorporationList.addDataElement (getHomeType (), aRowIndex, tCurrentColumn++);

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
		aCorporationList.addHeader ("Home Type", tCurrentColumn++);

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

		tPortfolioInfoJPanel = new JPanel ();
		if (portfolio == Portfolio.NO_PORTFOLIO) {
			tLabel = new JLabel (Portfolio.NO_PORTFOLIO_LABEL);
			tPortfolioInfoJPanel.add (tLabel);
		} else {
			if (portfolio.getCertificateTotalCount () == 0) {
				tLabel = new JLabel (Portfolio.NO_CERTIFICATES);
				tPortfolioInfoJPanel.add (tLabel);
			} else {
				if (isAMinorCompany ()) {
					tCertsPanel = portfolio.buildPortfolioJPanel (false, true, false, "", aItemListener,
							aGameManager);
					tPortfolioInfoJPanel.add (tCertsPanel);
				} else {
					tCertsPanel = portfolio.buildPortfolioJPanel (true, false, false, "", aItemListener,
							aGameManager);
					tPortfolioInfoJPanel.add (tCertsPanel);
				}
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
			updateListeners (CORPORATION_STATUS_CHANGE);
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
		TrainHolderI tGetOtherSelectedTrainHolder = TrainHolderI.NO_TRAIN_HOLDER;

		if (corpListValid ()) {
			tGetOtherSelectedTrainHolder = corporationList.getOtherSelectedTrainHolder (abbrev);
		}

		return tGetOtherSelectedTrainHolder;
	}

	public int getSelectedTrainCount () {
		int tGetSelectedTrainCount;

		tGetSelectedTrainCount = 0;
		if (corpListValid ()) {
			tGetSelectedTrainCount = corporationList.getSelectedTrainCount (abbrev);
		}

		return tGetSelectedTrainCount;
	}

	public boolean isSelectedTrainItem (Object aItem) {
		boolean tIsSelectedTrainItem;

		tIsSelectedTrainItem = false;
		if (corpListValid ()) {
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
		if (corpListValid ()) {
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

	// Train Company will override
	public boolean didOperateTrains () {
		return false;
	}

	public boolean canBuyPrivate () {
		boolean tCanBuyPrivate;

		tCanBuyPrivate = false;
		if (corpListValid ()) {
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
		if (corpListValid ()) {
			corporationList.clearBankSelections ();
		}
	}

	public boolean gameHasPrivates () {
		boolean tGameHasPrivates;

		tGameHasPrivates = false;
		if (corpListValid ()) {
			tGameHasPrivates = corporationList.gameHasPrivates ();
		}

		return tGameHasPrivates;
	}

	public String getOperatingOwnerName () {
		String tGetOperatingOwnerName;

		tGetOperatingOwnerName = ActorI.NO_NAME;
		if (corpListValid ()) {
			tGetOperatingOwnerName = corporationList.getOperatingOwnerName ();
		}

		return tGetOperatingOwnerName;
	}

	private boolean corpListValid () {
		return corporationList != CorporationList.NO_CORPORATION_LIST;
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

	// Share Company will override
	public boolean loanInterestHandled () {
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
	public void clearAllTrainsFromMap (boolean aAddAction) {
		System.err.println ("Trying to -CLEAR ALL TRAINS- Should be handled by Train Company");
	}

	public void repaintMapFrame () {
		corporationList.repaintMapFrame ();
	}

	public int getThisRevenue () {
		return TrainCompany.NO_REVENUE_GENERATED;
	}

	public void close () {
		if (!updateStatus (ActorI.ActionStates.Closed)) {
			System.err.println ("ZZZ--> Failure to update " + getName () + " to a Closed State <--");
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
		System.err.println ("The basic Corporation should not need to remove Benefit Buttons.");
	}

	/**
	 * Append Error Report String to Action Report Frame as an Error
	 *
	 * @param aErrorReport String Text to append as an Error to the end of the Action Report Frame
	 *
	 */
	public void appendErrorReport (String aErrorReport) {
		corporationList.appendErrorReport (aErrorReport);
	}

	public void close (LayTokenAction aTokenAction) {
		TransferOwnershipAction tTransferOwnershipAction;
		List<Effect> tEffects;
		
		tTransferOwnershipAction = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, "DUMMY", this);
		close (tTransferOwnershipAction);
		tEffects = tTransferOwnershipAction.getEffects ();
		for (Effect tEffect : tEffects) {
			aTokenAction.addEffect (tEffect);
		}
	}
	
	public void close (TransferOwnershipAction aTransferOwnershipAction) {
		Certificate tCertificate;
		CertificateHolderI tCertificateHolder;
		Portfolio tOwnerPortfolio;
		PortfolioHolderI tOwner;
		Bank tBank;
		int tCertificateCount;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		Portfolio tClosedPortfolio;

		tOldState = getActionStatus ();

		if (tOldState.equals (ActorI.ActionStates.Closed)) {
			appendErrorReport ("The Corporation " + name + " is already Closed... don't need to close again");
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
			updateListeners (CORPORATION_STATUS_CHANGE);
		} else {
			System.err.println ("XXX--> Failure to update Corp " + getName () + " State to Closed <--");
		}
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		return aXMLDocument.createElement (EN_CORPORATION);
	}

	public boolean didPartiallyOperate () {
		boolean tDidPartiallyOperate;

		tDidPartiallyOperate = false;
		if ((status == ActorI.ActionStates.StartedOperations) ||
			(status == ActorI.ActionStates.TileLaid) ||
			(status == ActorI.ActionStates.Tile2Laid) ||
			(status == ActorI.ActionStates.TileUpgraded) ||
			(status == ActorI.ActionStates.StationLaid) ||
			(status == ActorI.ActionStates.TileAndStationLaid) ||
			(status == ActorI.ActionStates.OperatedTrain) ||
			(status == ActorI.ActionStates.HandledLoanInterest) ||
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
		if ((status == ActorI.ActionStates.OperatedTrain) || 
			(status == ActorI.ActionStates.HandledLoanInterest)) {
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

	public boolean didPayLoanInterest () {
		return false;
	}

	public boolean wasLoanTaken () {
		return false;
	}

	public void prepareCorporation () {
	}

	public void declareBankruptcyAction () {
		DeclareBankruptcyAction tDeclareBankruptcyAction;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		OperatingRound tOperatingRound;
		String tOperatingRoundID;

		tCurrentStatus = status;
		resetStatus (ActorI.ActionStates.Bankrupt);
		tNewStatus = status;
		tOperatingRoundID = getOperatingRoundID ();
		tDeclareBankruptcyAction = new DeclareBankruptcyAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
				this);
		tDeclareBankruptcyAction.addChangeCorporationStatusEffect (this, tCurrentStatus, tNewStatus);
		tDeclareBankruptcyAction.addClearTrainsFromMapEffect (this);
		tOperatingRound = corporationList.getOperatingRound ();
		tOperatingRound.addAction (tDeclareBankruptcyAction);
		corporationList.declareBankuptcyAction (this);
		clearAllTrainsFromMap (true);
		hideFrame ();
	}

	public void doneAction () {
		boolean tStatusUpdated;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;
		DoneCorpAction tDoneAction;
		OperatingRound tOperatingRound;
		String tOperatingRoundID;

		tCurrentStatus = status;
		tStatusUpdated = updateStatus (ActorI.ActionStates.Operated);
		if (tStatusUpdated) {
			clearAllTrainsFromMap (false);
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

	public String getOperatingRoundID () {
		return corporationList.getOperatingRoundID ();
	}

	public int getMinSharesToFloat () {
		int tMinSharesToFloat;

		tMinSharesToFloat = corporationList.getMinSharesToFloat ();

		return tMinSharesToFloat;
	}

	// TODO: Push this method content up to CorporationList

	public int getWillFloatPercent () {
		PhaseInfo tPhaseInfo;
		int tWillFloatPercent;

		tPhaseInfo = corporationList.getCurrentPhaseInfo ();
		tWillFloatPercent = tPhaseInfo.getWillFloatPercent ();

		return tWillFloatPercent;
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
	public void placeTileOnMapCell (MapCell aMapCell, Tile aTile, int aOrientation, Tile aPreviousTile,
			int aPreviousTileOrientation, String aPreviousTokens, String aPreviousBases) {
		System.err.println ("Trying to Verify Place a Tile on a MapCell as Corporation - WRONG");
	}

	// Token Company will Override
	public void tokenWasPlaced (MapCell aMapCell, Tile aTile, int aRevenueCenterIndex, 
								MapToken aMapToken, int aTokenIndex, boolean aAddLayTokenAction) {
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
	public boolean hasTrainOfType (Coupon aTrain) {
		return false;
	}

	public void printCorporationInfo () {
		System.out.println ("Printing Corporation Info for " + getName ());
	}

	// For the Corporation List Columns
	public int fieldCount () {
		return 10;
	}

	public boolean gameHasLoans () {
		return corporationList.gameHasLoans ();
	}

	@Override
	public String getAbbrev () {
		return abbrev;
	}

	public ActionStates getActionStatus () {
		return status;
	}

	public int getAllowedTileLays () {
		int tAllowedTileLays;
		
		tAllowedTileLays = 1;
		
		return tAllowedTileLays;
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

	public abstract int calculateStartingTreasury ();

	public int getGameCapitalizationLevel (int aSharesSold) {
		int tCapitalizationLevel;

		tCapitalizationLevel = corporationList.getCapitalizationLevel (aSharesSold);

		return tCapitalizationLevel;
	}

	// TODO Reorder methods with single call to corporationCertificates together
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
	public void getCorporationStateElement (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		Location tHomeLocation1;
		Location tHomeLocation2;
		int tHomeLocation1Int;
		int tHomeLocation2Int;
		
		aXMLCorporationState.setAttribute (AN_ABBREV, getAbbrev ());
		aXMLCorporationState.setAttribute (AN_CORP_STATUS, getStatusName ());
		if (homeCity1 != MapCell.NO_MAP_CELL) {
			aXMLCorporationState.setAttribute (AN_HOMECELL1, homeCity1.getCellID ());
			tHomeLocation1 = getHomeLocation1 ();
			tHomeLocation1Int = tHomeLocation1.getLocation ();
			aXMLCorporationState.setAttribute (Location.AN_HOME_LOCATION1, tHomeLocation1Int);
		}
		if (homeCity2 != MapCell.NO_MAP_CELL) {
			aXMLCorporationState.setAttribute (AN_HOMECELL2, homeCity2.getCellID ());
			tHomeLocation2 = getHomeLocation2 ();
			tHomeLocation2Int = tHomeLocation2.getLocation ();
			aXMLCorporationState.setAttribute (Location.AN_HOME_LOCATION2, tHomeLocation2Int);
		}
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

	/**
	 * Create the "may operate", "will operate" label for the Action Button for this company
	 *
	 * @return String with the full specification to set to the Action Button Label
	 *
	 */
	public String getDoLabel () {
		String tLabel;
		String tActionVerb;

		if (status == ActorI.ActionStates.MayFloat) {
			tActionVerb = " may operate ";
		} else {
			tActionVerb = " will operate ";
		}
		tLabel = createButtonLabel (tActionVerb);

		return tLabel;
	}

	/**
	 * Create the "is operating" label for the Action Button for this company
	 *
	 * @return String with the full specification to set to the Action Button Label
	 *
	 */

	public String getOperatingLabel () {
		String tLabel;

		tLabel = createButtonLabel (" is operating ");

		return tLabel;
	}

	private String createButtonLabel (String aActionVerb) {
		String tLabel;
		String tCompanyName;
		
		tCompanyName = getName ();
		tLabel = getPresidentName () + aActionVerb + tCompanyName;

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
		return (Integer.valueOf (id).toString ());
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

	public String getHomeMapCellID (int aHomeID) {
		String tMapCellID;

		if (aHomeID == 1) {
			tMapCellID = getCorpHome1MapID ();
		} else if (aHomeID == 2) {
			tMapCellID = getCorpHome2MapID ();
		} else {
			tMapCellID = null;
		}
		return tMapCellID;
	}

	private String getMapCellID (MapCell aMapCell) {
		String tMapCellID;

		if (aMapCell != MapCell.NO_MAP_CELL) {
			tMapCellID = aMapCell.getID ();
		} else {
			tMapCellID = MapCell.NO_ID;
		}
		return tMapCellID;
	}

	public String getHomeLocations () {
		String tHomeLocations;
		String tHomeMapCell1;
		String tHomeMapCell2;
		
		tHomeMapCell1 = getCorpHome1MapID ();
		tHomeMapCell2 = getCorpHome2MapID ();
		
		tHomeLocations = "Home MapCell ID:  " + tHomeMapCell1;
		if (tHomeMapCell1 != tHomeMapCell2) {
			if (tHomeMapCell2 != MapCell.NO_ID) {
				tHomeLocations += "  ID2:  " + tHomeMapCell2;
			}
		}
		
		return tHomeLocations;
	}
	
	public String getCorpHome1MapID () {
		MapCell tMapCell;
		String tMapCellID;

		tMapCell = getHomeCity1 ();
		tMapCellID = getMapCellID (tMapCell);

		return tMapCellID;
	}

	public String getCorpHome2MapID () {
		MapCell tMapCell;
		String tMapCellID;

		tMapCell = getHomeCity2 ();
		tMapCellID = getMapCellID (tMapCell);

		return tMapCellID;
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

	public String getHomeType () {
		return homeType;
	}
	
	public boolean isHomeTypeChoice () {
		boolean tIsChoice;
		
		tIsChoice = false;
		if (homeType == null) {
			tIsChoice = false;
		} else if (homeType.equals (HOME_TYPE_CHOICE)) {
			tIsChoice = true;
		}
		
		return tIsChoice;
	}
	
	public boolean isHomeTypeBoth () {
		boolean tIsBoth;
		
		tIsBoth = false;
		if (homeType == null) {
			tIsBoth = false;
		} else if (homeType.equals (HOME_TYPE_BOTH)) {
			tIsBoth = true;
		}
		
		return tIsBoth;
	}
	
	public boolean allBasesHaveTiles () {
		boolean tAllBasesHaveTiles;
		
		tAllBasesHaveTiles = false;
		
		return tAllBasesHaveTiles;
	}
	
	public boolean hasTwoBases () {
		boolean tHasTwoBases;
		
		if ((getHomeCity1 () != MapCell.NO_MAP_CELL) &&
			(getHomeCity2 () != MapCell.NO_MAP_CELL)) {
			tHasTwoBases = true;
		} else {
			tHasTwoBases = false;
		}
		
		return tHasTwoBases;
	}
	
	// TokenCompany will Override
	public MapToken getMapToken () {
		return MapToken.NO_MAP_TOKEN;
	}
	
	// TokenCompany will Override
	public MapToken getMapToken (TokenType aTokenType) {
		return MapToken.NO_MAP_TOKEN;
	}

	/**
	 * Retrieve the Last available Token that is a MapToken
	 * 
	 * @return The Last available Map Token
	 * 
	 */
	public MapToken getLastMapToken () {
		return MapToken.NO_MAP_TOKEN;
	}

	@Override
	public String getName () {
		String tCompanyName;
		
		if (isAMinorCompany ()) {
			tCompanyName = "Minor " + getAbbrev () + " [" + name + "]";
		} else {
			tCompanyName = name;
		}
		
		return tCompanyName;
	}
	
	/**
	 * Percentage of Shares Sold from Bank
	 *
	 * @return int value of % shares sold (up to 100)
	 */

	public int getPercentOwned () {
		return corporationCertificates.getPercentOwned ();
	}

	public PhaseInfo getCurrentPhaseInfo () {
		return corporationList.getCurrentPhaseInfo ();
	}

	// Corporation by default have no Discount. Privates Can, if they are Must Sell
	// and all have passed
	// Private Company will Override
	public int getDiscount () {
		return 0;
	}

	// Basic Must Sell is false. Only with a Private that has this attribute is this
	// overridden
	// Private Company will Override
	public boolean getMustSell () {
		return false;
	}

	// Basic Increase Discount -- only needed on Private Override.
	// Private Company will Override
	public void increaseDiscount () {
	}

	// Basic Set Discount -- only needed on Private Override and XML Load
	// Private Company will Override
	public void setDiscount (int aDiscount) {
	}

	public void fillCertificateInfo (GameManager aGameManager) {
		corporationCertificates.fillCertificateInfo (aGameManager);
	}
	
	/**
	 * Build the generic Corporation Info Label to be added to the Company JPanel
	 *
	 * @return The constructed JLabel with the Corporation Info
	 *
	 */
	public JLabel buildCorpInfoJLabel () {
		JLabel tLabel;
		String tCorpInfoLabel;
		Border tCorpBorder;

		tCorpInfoLabel = buildCorpInfoLabel ();
		tCorpInfoLabel = "<html>" + tCorpInfoLabel + "</html>";
		tCorpBorder = setupBorder ();
		tLabel = new JLabel (tCorpInfoLabel);
		tLabel.setBorder (tCorpBorder);

		return tLabel;
	}

	// Train Company and Private Company will Override
	public String buildCorpInfoLabel () {
		return ">> OVERRIDE buildCorpInfoLabel <<";
	}
	
	public String buildCorpInfoLabel (String aLoanInfo) {
		return ">> OVERRIDE buildCorpInfoLabel <<";
	}

	public int getNextPresidentPercent () {
		return corporationCertificates.getNextPresidentPercent (this);
	}

	public String getNextPresidentName () {
		return corporationCertificates.getNextPresidentName (this);
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

	public int getSharesOwnedByPlayerOrCorp () {
		return corporationCertificates.getPlayerOrCorpOwnedPercentageFor (this)/PhaseInfo.STANDARD_SHARE_SIZE;
	}

	public String buildPercentOwnedLabel () {
		return "[" + getPlayerOrCorpOwnedPercentage () + "% Owned]";
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

	/**
	 * Base class to get the Share Price of the Company. Will be Overridden by the
	 * ShareCompany Class
	 *
	 * @return This will always return NO_COST (ie Zero -0-) since the ShareCompany
	 *         needs to provide this value
	 */

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
		if (isClosed () || !isFormed () || (getPercentOwned () == 0)) {
			tIsActive = false;
		}

		return tIsActive;
	}

	public boolean isClosed () {
		return (status == ActorI.ActionStates.Closed);
	}

	public boolean isStationLaid () {
		return (status == ActorI.ActionStates.StationLaid);
	}

	public boolean isInActive () {
		return (status == ActorI.ActionStates.Inactive);
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

	public boolean isSelectedForBuy () {
		return corporationCertificates.isSelectedForBuy ();
	}

	// Private Company will override
	@Override
	public boolean isAPrivateCompany () {
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
	@Override
	public boolean isAShareCompany () {
		return false;
	}

	@Override
	public boolean isABankPool () {
		return false;
	}

	public boolean isGovtRailway () {
		return govtRailway;
	}

	public boolean isSoldOut () {
		boolean isSoldOut;
		int tPercentOwned;

		isSoldOut = false;

		if (!isClosed ()) {
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
		boolean tDoesNotOwn = !portfolio.containsPresidentShareOf (aPrivateCompany);

		return tDoesNotOwn;
	}

	public boolean isUnowned () {
		boolean tIsUnowned;
		
		if ((status == ActorI.ActionStates.Unowned)  ||
			(status == ActorI.ActionStates.Unformed)) {
			tIsUnowned = true;
		} else {
			tIsUnowned = false;
		}
		
		return tIsUnowned;
	}

	public boolean isFormed () {
		return !(status == ActorI.ActionStates.Unformed);
	}

	// Train Company will override
	public void payFullDividend () {
		System.err.println ("Trying to -PAY FULL DIVIDEND- Should be handled by Train Company");
	}

	// Train Company will override
	public void payHalfDividend () {
		System.err.println ("Trying to -PAY HALF DIVIDEND- Should be handled by Train Company");
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

		if (!corporationFrame.isVisible ()) {
			updateFrameInfo ();
			tNewPoint = corporationList.getOffsetRoundFrame ();
			corporationFrame.setLocation (tNewPoint);
			corporationFrame.setVisible (true);
		}
		corporationFrame.toTheFront ();
	}

	public void showMap () {
		if (!mapVisible ()) {
			corporationList.showMap ();
		}
		corporationList.bringMapToFront ();
	}

	public void showTileTray () {
		if (!tileTrayVisible ()) {
			corporationList.showTileTray ();
		}
	}

	public void updateFrameInfo () {
		updateInfo ();
	}
	
	@Override
	public void updateInfo () {
		if (corporationFrame != XMLFrame.NO_XML_FRAME) {
			corporationFrame.updateInfo ();
		}
	}

	public void loadStatus (XMLNode aXMLNode) {
		String tLoadedStatus;
		String tCurrentStatus;
		ActorI.ActionStates tLoadedState;
		GenericActor tGenericActor;

		tLoadedStatus = aXMLNode.getThisAttribute (AN_CORP_STATUS);
		tCurrentStatus = getStatusName ();
		if (!tLoadedStatus.equals (tCurrentStatus)) {
			tGenericActor = new GenericActor ();
			tLoadedState = tGenericActor.getCorporationActionState (tLoadedStatus);
			resetStatus (tLoadedState);
		}
		removeHomeIfNull (aXMLNode);
		
		loadStates (aXMLNode);
	}

	private void removeHomeIfNull (XMLNode aXMLNode) {
		MapCell tDefaultHome1;
		MapCell tDefaultHome2;
		Location tDefaultLocation1;
		Location tDefaultLocation2;
		
		tDefaultHome1 = getHomeCity1 ();
		tDefaultHome2 = getHomeCity2 ();
		tDefaultLocation1 = getHomeLocation1 ();
		tDefaultLocation2 = getHomeLocation2 ();
		
		parseHomeCities (aXMLNode);
		if (homeCityGrid1 == GUI.NULL_STRING) {
			if (tDefaultHome1 != MapCell.NO_MAP_CELL) {
				tDefaultHome1.removeHome (this, tDefaultLocation1);
			}
		}
		if (homeCityGrid2 == GUI.NULL_STRING) {
			if (tDefaultHome2 != MapCell.NO_MAP_CELL) {
				tDefaultHome2.removeHome (this, tDefaultLocation2);
			}
		}
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

	public void printOwnershipReport () {
		System.out.println ("Who Owns this Corporation");		// PRINTLOG method
		corporationCertificates.printPortfolioInfo ();
		System.out.println ("What this Corporation Owns");
		portfolio.printPortfolioInfo ();
	}

	public void printReport () {
		System.out.println ("ID: " + id + " Name [" + name + "] Abbrev [" + abbrev + "]");	// PRINTLOG method
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
		CorporationFrame tCorporationFrame;

		if (isATrainCompany ()) {
			tGameManager = corporationList.getGameManager ();
			tIsNetworkGame = tGameManager.isNetworkGame ();
			tFullTitle = tGameManager.createFrameTitle ("Corporation");
			tCorporationFrame = new CorporationFrame (tFullTitle, this, tIsNetworkGame, tGameManager);
		} else {
			tCorporationFrame = (CorporationFrame) XMLFrame.NO_XML_FRAME;
		}
		setCorporationFrame (tCorporationFrame);
	}

	public void setCorporationFrame (CorporationFrame aCorporationFrame) {
		corporationFrame = aCorporationFrame;
	}
	
	/**
	 * Set the Company's HomeCity1 and HomeLocation1 to the provided parameters
	 *
	 * @param aHomeCity     the MapCell that is the First Home of the Company
	 * @param aHomeLocation the Location within the MapCell for the Home of the
	 *                      Company
	 */
	public void setHome1 (MapCell aHomeCity, Location aHomeLocation) {
		homeCity1 = aHomeCity;
		homeLocation1 = aHomeLocation;
	}

	/**
	 * Set the Company's HomeCity2 and HomeLocation1 to the provided parameters
	 *
	 * @param aHomeCity     the MapCell that is the Second Home of the Company
	 * @param aHomeLocation the Location within the MapCell for the Home of the
	 *                      Company
	 */
	public void setHome2 (MapCell aHomeCity, Location aHomeLocation) {
		homeCity2 = aHomeCity;
		homeLocation2 = aHomeLocation;
	}

	public void setHomeCityGrid1 (String aHomeCityGrid) {
		homeCityGrid1 = aHomeCityGrid;
	}

	public void setHomeCityGrid2 (String aHomeCityGrid) {
		homeCityGrid2 = aHomeCityGrid;
	}

	protected void setHomeType (String aHomeType) {
		homeType = aHomeType;
	}
	
	protected void setName (String aName) {
		name = aName;
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		resetStatus (aPrimaryActionState);
	}

	/**
	 * Force Reset the Corporation Status to the provided Status -- NO Checks to see
	 * if valid. Will also update The Frame Information
	 *
	 * @param aStatus The new status value to set.
	 */
	public void resetStatus (ActorI.ActionStates aStatus) {
		forceSetStatus (aStatus);
		updateFrameInfo ();
	}

	/**
	 * Force Set the Corporation Status to the provided Status -- NO Checks to see
	 * if valid. To allow for Unit Testing.
	 *
	 * @param aStatus The new status value to set.
	 */
	protected void forceSetStatus (ActorI.ActionStates aStatus) {
		status = aStatus;
		updateListeners (CORPORATION_STATUS_CHANGE + " Force [" + status.toString () + "]");
	}

	/**
	 * Set the Company Status to the Provided Status if the current Status is
	 * NO_STATE, or if it is Unowned Otherwise, call the updateStatus method to
	 * update if allowed.
	 *
	 * @param aStatus The New Status to set the Company Status to.
	 */
	public void setStatus (ActorI.ActionStates aStatus) {
		if (status == ActorI.NO_STATE) {
			forceSetStatus (aStatus);
		} else if (status == ActorI.ActionStates.Unowned) {
			forceSetStatus (aStatus);
		} else {
			updateStatus (aStatus);
		}
	}

	/**
	 * Update the Status of the Company to the provided Status, if it is allowed
	 * from the current state
	 *
	 * @param aStatus The desired new Status
	 * @return True if the status was updated, false if the update failed due to bad
	 *         target status
	 */
	public boolean updateStatus (ActorI.ActionStates aStatus) {
		boolean tStatusUpdated;

		tStatusUpdated = false;
		if (aStatus == ActorI.ActionStates.WaitingResponse) {
			forceSetStatus (aStatus);
			tStatusUpdated = true;
		} else if (status == ActorI.ActionStates.Owned) {
			if (isAShareCompany ()) {
				if ((aStatus == ActorI.ActionStates.MayFloat) ||
					(aStatus == ActorI.ActionStates.WillFloat) ||
					(aStatus == ActorI.ActionStates.Closed)) {
					status = aStatus;
					tStatusUpdated = true;
				}
			} else {
				if (isAMinorCompany ()) {
					status = aStatus;
					tStatusUpdated = true;
				} else if ((aStatus == ActorI.ActionStates.WillFloat) ||
					(aStatus == ActorI.ActionStates.Closed)) {
					status = aStatus;
					tStatusUpdated = true;
				}
			}
		} else if (status == ActorI.ActionStates.Unformed) {
			if ((aStatus == ActorI.ActionStates.Owned) ||
				(aStatus == ActorI.ActionStates.Closed)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.MayFloat) {
			if ((aStatus == ActorI.ActionStates.Owned) ||
				(aStatus == ActorI.ActionStates.WillFloat) ||
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
			if ((aStatus == ActorI.ActionStates.StartedOperations) ||
				(aStatus == ActorI.ActionStates.Closed)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.StartedOperations) {
			if ((aStatus == ActorI.ActionStates.TileLaid) ||
				(aStatus == ActorI.ActionStates.TileUpgraded) ||
				(aStatus == ActorI.ActionStates.StationLaid) ||
				(aStatus == ActorI.ActionStates.OperatedTrain) ||
				(aStatus == ActorI.ActionStates.HandledLoanInterest) ||
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
						(aStatus == ActorI.ActionStates.HandledLoanInterest) ||
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
				(aStatus == ActorI.ActionStates.HandledLoanInterest) ||
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
						(aStatus == ActorI.ActionStates.HandledLoanInterest) ||
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
				(aStatus == ActorI.ActionStates.HandledLoanInterest) ||
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
				(aStatus == ActorI.ActionStates.HandledLoanInterest) ||
				(aStatus == ActorI.ActionStates.HoldDividend) ||
				(aStatus == ActorI.ActionStates.HalfDividend) ||
				(aStatus == ActorI.ActionStates.FullDividend)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.OperatedTrain) {
			if (gameHasLoans () && needToHandleLoans ()) {

				if (aStatus == ActorI.ActionStates.HandledLoanInterest) {
					status = aStatus;
					tStatusUpdated = true;
				}
			} else if ((aStatus == ActorI.ActionStates.HoldDividend) ||
						(aStatus == ActorI.ActionStates.HalfDividend) ||
						(aStatus == ActorI.ActionStates.FullDividend)) {
				status = aStatus;
				tStatusUpdated = true;
			}
		} else if (status == ActorI.ActionStates.HandledLoanInterest) {
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
		} else if (status == ActorI.ActionStates.Operated) {
			if (aStatus == ActorI.ActionStates.Closed) {
				status = aStatus;
				tStatusUpdated = true;
			}
		}
		updateListeners (CORPORATION_STATUS_CHANGE + " [" + status.toString () + "]");
		
		return tStatusUpdated;
	}

	/**
	 * Default Corporation knows how big a loan can be.
	 *
	 * @return The amount of the Loan
	 */
	public int getLoanAmount () {
		return loanAmount;
	}

	/**
	 * Default Corporation will know how much interest must be paid per loan
	 *
	 * @return The amount of the Loan
	 */
	public int getLoanInterest () {
		return loanInterest;
	}

	/**
	 * Default Corporation will not have any outstanding loans. The Share Company must override this method.
	 *
	 * @return Always return 0
	 */
	public int getLoanCount () {
		return 0;
	}

	/**
	 * Default Corporation will not have any outstanding loans. The Share Company must override this method.
	 *
	 * @return Always return FALSE
	 */
	public boolean hasOutstandingLoans () {
		return false;
	}

	/**
	 * Will test if this corporation has loans that must be handled. The Share Company sub-class method
	 * hasOutstandingLoans will be called (hopefully without needing to cast it.
	 *
	 * @return TRUE if this is a ShareCompany that has at least one Outstanding Loan, otherwise FALSE
	 */
	public boolean needToHandleLoans () {
		boolean tNeedToHandleLoans;

		tNeedToHandleLoans = false;
		if (isAShareCompany ()) {
			tNeedToHandleLoans = hasOutstandingLoans ();
		}

		return tNeedToHandleLoans;
	}

	private void setValues (int aID, String aName, String aAbbrev, MapCell aHomeCity1, Location aHomeLocation1,
			MapCell aHomeCity2, Location aHomeLocation2, ActorI.ActionStates aStatus, boolean aGovtRailway) {
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
			(status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.Unformed)) {
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
			(status == ActorI.ActionStates.NotOperated) ||
			(status == ActorI.ActionStates.Unformed)) {
			isOperating = false;
		}

		return isOperating;
	}

	public boolean shouldFloat () {
		return false;
	}
	
	public boolean shouldOperate () {
		boolean tShouldOperate;

		tShouldOperate = true;
		if (isAMinorCompany ()) {
			if ((status == ActorI.ActionStates.Closed) ||
				(status == ActorI.ActionStates.Unowned) ||
				(status == ActorI.ActionStates.Operated) ||
				(status == ActorI.ActionStates.Unformed)) {
				tShouldOperate = false;
			}
		} else if ((status == ActorI.ActionStates.Closed) ||
			(status == ActorI.ActionStates.Unowned) ||
			(status == ActorI.ActionStates.Owned) ||
			(status == ActorI.ActionStates.Operated) ||
			(status == ActorI.ActionStates.Unformed)) {
			tShouldOperate = false;
		} else if (shouldFloat ()) {
			tShouldOperate = true;
		}

		return tShouldOperate;
	}

	public void sortCorporationCertificates () {
		corporationCertificates.sortByOwners ();
	}

	public void undoAction () {
		corporationList.undoAction ();
		/*
		 * Need to test if the state of the company should leave the window open or not
		 */
		if (status == ActorI.ActionStates.NotOperated) {
			hideFrame ();
		}
	}

	public void updateOwnerPortfolios () {
		corporationCertificates.updateCertificateOwnersInfo ();
		updateListeners (CORPORATION_STATUS_CHANGE);
	}

	/* Sort Methods for proper ordering */
	@Override
	public int compareTo (Corporation aCorporation) {
		return compareID (aCorporation);
	}

	public int compareID (Corporation aCorporation) {
		return id - aCorporation.getID ();
	}

	public int compareFormed (Corporation aCorporation) {
		boolean tIsFormed1, tIsFormed2;
		int tCompareFormed;

		tIsFormed1 = isFormed ();
		tIsFormed2 = aCorporation.isFormed ();
		tCompareFormed = 0;
		if (tIsFormed1) {
			if (tIsFormed2) {
				tCompareFormed = 0;
			} else {
				tCompareFormed = SORT_CO1_BEFORE_CO2;
			}
		} else if (tIsFormed2) {
			tCompareFormed = SORT_CO2_BEFORE_CO1;
		} else {
			tCompareFormed = 0;
		}

		return tCompareFormed;
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

		if (isAShareCompany ()) {
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

		if ((tMarketCell1 != MarketCell.NO_MARKET_CELL) && (tMarketCell2 != MarketCell.NO_MARKET_CELL)) {
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

		if ((tMarketCell1 != MarketCell.NO_MARKET_CELL) && (tMarketCell2 != MarketCell.NO_MARKET_CELL)) {
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

		if ((isAShareCompany ()) && (aCorporation.isAShareCompany ())) {
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

		if ((aCorporation.getSharePrice () == NO_COST) && (getSharePrice () == NO_COST)) {
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

	public static Comparator<Corporation> CorporationOperatingOrderComparator = new Comparator<Corporation> () {

		@Override
		public int compare (Corporation aCorporation1, Corporation aCorporation2) {
			int tOperatingOrderValue, tClosedCompare;

			tOperatingOrderValue = aCorporation1.compareFormed (aCorporation2);
			if (tOperatingOrderValue == 0) { // Both Companies are Active

				tOperatingOrderValue = aCorporation1.compareActive (aCorporation2);
				if (tOperatingOrderValue == 0) { // Both Companies are Active
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
			}

			if (tOperatingOrderValue == 0) {
				tOperatingOrderValue = aCorporation1.compareID (aCorporation2);
			}

			// ascending order
			return tOperatingOrderValue;
		}
	};

	public int getTrainCount () {
		return 0;
	}

	// Overriden in Train Company
	public JPanel buildPortfolioTrainsJPanel (CorporationFrame aCorporationFrame, GameManager aGameManager,
			boolean aFullTrainPortfolio, boolean aCanBuyTrain, String aDisableToolTipReason,
			Corporation aBuyingCorporation) {
		return GUI.NO_PANEL;
	}

	public TrainHolderI getLocalSelectedTrainHolder () {
		return TrainHolderI.NO_TRAIN_HOLDER;
	}

	// From here to end loading the Privates owned by a Corporation when Loading a
	// Saved Game
	public void loadPortfolio (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (portfolioParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Portfolio.EN_PORTFOLIO);
	}

	ParsingRoutineI portfolioParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			portfolio.loadPortfolio (aChildNode);
		}
	};

	// Routines needed by a Train Company to test for Emergency Train Buy
	// Train company will Override
	public void forceBuyTrain () {
	}

	public Coupon getCheapestBankTrain () {
		return Train.NO_TRAIN;
	}

	// Train Company will Override these
	public void setMustBuyTrain (boolean aMustBuyTrain) {
	}

	public boolean mustBuyTrain () {
		return false;
	}

	public void setMustBuyCoupon (boolean aMustBuyCoupon) {
	}
	
	public boolean mustBuyCoupon () {
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

	/**
	 * Base method to get a Loan for the Company. The Share company will Override
	 */
	// Share Company will Override
	public void getLoan () {

	}

	/**
	 * Base method to handle Redeeming (Paying back) a Loan for the Company. The Share company will Override
	 */
	// Share Company will Override
	public void redeemLoan () {

	}

	/**
	 * Base method to handle Interest Payment for a Loan for the Company. The Share company will Override
	 */
	// Share Company will Override
	public void payLoanInterest () {

	}

	/**
	 * Base method to the number of shares owned by Players. The Share company will Override
	 */
	// Share Company will Override
	public int getSharesOwned () {
		return 0;
	}

	public Border setupBorder () {
		Border tCorpBorder;

		tCorpBorder = BorderFactory.createLineBorder (Color.black, 1);

		return tCorpBorder;
	}

	@Override
	public boolean isACorporation () {
		return true;
	}

	public boolean hasDestination () {
		return false;
	}
	
	public boolean hasReachedDestination () {
		return false;
	}
	
	public void checkForDestinationReached (HexMap aHexMap) {
		
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
		} else if (homeCity2 != MapCell.NO_MAP_CELL) {
			tHomeMapCellHasTile = true;
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
	}

	protected boolean isPlaceTokenMode () {
		return corporationList.isPlaceTokenMode ();
	}

	public void setTestingFlag (boolean aGameTestFlag) {
		gameTestFlag = aGameTestFlag;
	}

	public boolean isATestGame () {
		return gameTestFlag;
	}

	public void placeHomeToken (MapCell aMapCell, Location aHomeLocation) {
		// Override in Token Company Class
	}

	public int getTokenCount () {
		return 0;
	}
	
	public int getTokenCost (MapToken aMapToken, TokenType aTokenType, MapCell tMapCell) {
		// Override in Token Company Class
		return 0;
	}

	public PrivateCompany getSelectedPrivateToBuy () {
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
		boolean tHasPlacedAnyStation;
		MapFrame tMapFrame;

		tHasPlacedAnyStation = false;
		tMapFrame = corporationList.getMapFrame ();
		tHasPlacedAnyStation = tMapFrame.hasStation (getID ());

		return tHasPlacedAnyStation;
	}

	protected void configurePrivateBenefitButtons (JPanel aButtonRow) {
		removeAllBenefitButtons (aButtonRow);
		portfolio.configurePrivateCompanyBenefitButtons (aButtonRow);
		addAllActorsBenefitButtons (aButtonRow);
		if (isAShareCompany ()) {
			addAllOwnerTypeBenefitButtons (aButtonRow);
		}
	}

	private void addAllActorsBenefitButtons (JPanel aButtonRow) {
		CorporationList tPrivates;
		PrivateCompany tPrivate;
		int tCount, tIndex;

		tPrivates = corporationList.getPrivates ();
		tCount = tPrivates.getCorporationCount ();
		if (tCount > 0) {
			for (tIndex = 0; tIndex < tCount; tIndex++) {
				tPrivate = (PrivateCompany) tPrivates.getCorporation (tIndex);
				if (tPrivate.isClosed () || ! tPrivate.isPlayerOwned ()) {
					if (tPrivate.hasActiveCompanyBenefits ()) {
						tPrivate.addAllActorsBenefitButtons (aButtonRow);
					}
				}
			}
		}
	}
	
	private void addAllOwnerTypeBenefitButtons (JPanel aButtonRow) {
		List<Benefit> tOwnerBenefitButtons;
		PortfolioHolderI tPresident;
		Player tPresidentPlayer;
		PrivateCompany tPrivateCompany;
		
		tPresident = getPresident ();
		if (tPresident != PortfolioHolderI.NO_HOLDER) {
			if (tPresident.isAPlayer ()) {
				tPresidentPlayer = (Player) tPresident;
				tOwnerBenefitButtons = tPresidentPlayer.getOwnerTypeBenefits ();
				for (Benefit tBenefit : tOwnerBenefitButtons) {
					if (tBenefit.isActiveCompanyBenefit ()) {
						tPrivateCompany = tBenefit.getPrivateCompany ();
						tBenefit.configure (tPrivateCompany, aButtonRow);
					}
				}
			}
		}
	}
	
	private void removeAllBenefitButtons (JPanel aButtonRow) {
		CorporationList tPrivates;
		PrivateCompany tPrivate;
		int tCount, tIndex;

		tPrivates = corporationList.getPrivates ();
		tCount = tPrivates.getCorporationCount ();
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

	public abstract void completeBenefitInUse (Corporation aCorporation);

	public License getPortLicense () {
		return License.NO_LICENSE;
	}
	
	protected abstract boolean choiceForBaseToken ();

	public int getSmallestSharePercentage () {
		return corporationCertificates.getSmallestSharePercentage ();
	}

	public boolean isLoading () {
		return corporationList.isLoading ();
	}

	public Action getLastAction () {
		Action tLastAction;

		tLastAction = corporationList.getLastAction ();

		return tLastAction;
	}

	@Override
	public void updateListeners (String aMessage) {
		bean.addMessage (aMessage);
	}

	@Override
	public boolean isWaitingForResponse () {
		boolean tIsWaitingForResponse;

		tIsWaitingForResponse = false;
		if (status.equals (ActorI.ActionStates.WaitingResponse)) {
			tIsWaitingForResponse = true;
		}

		return tIsWaitingForResponse;
	}

	/**
	 * When the Corporation has need to wait for a Response from a Network Player, State is ActorI.ActionStates.WaitingResponse
	 * Put this thread to sleep, in 2 second chunks
	 *
	 */
	public void waitForResponse () {
		int tWaitTime = 2000; // Wait for 2 Seconds before testing if a Response came back

		updateInfo ();
		while (isWaitingForResponse ()) {
			try {
				Thread.sleep (tWaitTime);
			} catch (InterruptedException e) {
				System.err.println ("Waiting for the Response to Clear - Exception");
				e.printStackTrace ();
			}
		}
	}

	public boolean haveLaidThisBaseToken (MapCell tMapCell) {
		return false;
	}
	
	public void updateCorporationFrame () {
		corporationFrame.updateCFButtons ();
	}

	public License getLicense (LicenseTypes aType) {
		return License.NO_LICENSE;
	}
	
	public String getCompanyInfo () {
		String tCompanyInfo;
		
		tCompanyInfo = getType () + " Company " + getName () + " (" + getAbbrev () + ")";
		
		return tCompanyInfo;
	}
		
	public Benefits getBenefits () {
		return Benefits.NO_BENEFITS;
	}
}
