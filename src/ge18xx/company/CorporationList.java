package ge18xx.company;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.apache.logging.log4j.Logger;

//
//  CorporationList.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.CashHolderI;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.OperatingRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.ChangeStateAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.PayRevenueAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.toplevel.InformationTable;
import ge18xx.toplevel.LoadableXMLI;
import ge18xx.toplevel.MapFrame;
import ge18xx.train.RouteInformation;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.ParsingRoutineIO;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

//  Should be able to specify this as a Generic Class, where the TYPE extends Corporation

public class CorporationList extends InformationTable implements LoadableXMLI, ParsingRoutineI {
	private static final long serialVersionUID = 1L;
	public final static ElementName EN_CORPORATIONS = new ElementName ("Corporations");
	public static final int NO_CORPORATION_INDEX = -1;
	public static final CorporationList NO_CORPORATION_LIST = null;
	public static final ElementName TYPE_NAMES[] = {
			new ElementName (Corporation.PRIVATE_COMPANY),
			new ElementName (Corporation.MINOR_COMPANY),
			new ElementName (Corporation.SHARE_COMPANY) };
	List<Corporation> corporations;
	ElementName typeName;
	RoundManager roundManager;
	Border EMPTY_BORDER = BorderFactory.createEmptyBorder ();

	public CorporationList (ElementName aTypeName, RoundManager aRoundManager) {
		super ();
		corporations = new LinkedList<> ();
		setTypeName (aTypeName);
		roundManager = aRoundManager;
	}

	public boolean anyCanOperate () {
		boolean tAnyCanOperate = false;

		for (Corporation tCorporation : corporations) {
			tAnyCanOperate = tAnyCanOperate || tCorporation.canOperate ();
		}

		return tAnyCanOperate;
	}

	/**
	 * Search the corporations for one with this ID. Don't use Abbrev which can be duplicated, for example B&O.
	 * When found force set the status to unowned.
	 * This is to be used when Activating a Company
	 *
	 * @param aCompanyID The ID value of the Company.
	 *
	 * @return TRUE if the company was activated, FALSE otherwise
	 *
	 */
	public boolean activateCorporation (int aCompanyID) {
		boolean tActivated;

		tActivated = false;
		for (Corporation tCorporation : corporations) {
			if (aCompanyID == tCorporation.getID ()) {
				tCorporation.forceSetStatus (ActorI.ActionStates.Unowned);
				tActivated = true;
			}
		}

		return tActivated;
	}

	/**
	 * Build the entire Company JPanel in the CorporationList
	 *
	 * @param aAllCompanies TRUE to set Title "All TYPE Companies", FALSE to set
	 *                      Title "TYPE Companies in Operating Order"
	 * @return the JPanel to add to the JFrame
	 *
	 */
	public JPanel buildCompanyJPanel (boolean aAllCompanies) {
		JPanel tCompanyJPanel;
		String tBoxLabel;
		JLabel tCorpInfoLabel;

		tBoxLabel = getThisTypeName () + " Companies";
		if (aAllCompanies) {
			tBoxLabel = "All " + tBoxLabel;
		} else {
			tBoxLabel += " in Operating Order";
		}
		tCompanyJPanel = new JPanel ();
		tCompanyJPanel.setBorder (BorderFactory.createTitledBorder (tBoxLabel));
		tCompanyJPanel.setLayout (new BoxLayout (tCompanyJPanel, BoxLayout.X_AXIS));

		sortByOperatingOrder ();
		for (Corporation tCorporation : corporations) {
			tCorpInfoLabel = tCorporation.buildCorpInfoJLabel ();
			tCompanyJPanel.add (Box.createHorizontalStrut (10));
			tCompanyJPanel.add (tCorpInfoLabel);
		}

		return tCompanyJPanel;
	}

	public void sortByOperatingOrder () {
		Collections.sort (corporations, Corporation.CorporationOperatingOrderComparator);
	}

	public JPanel buildCompaniesForPurchaseJPanel (ItemListener aItemListener, String aCorpType, int aAvailableCash) {
		JPanel tPrivatesJPanel;
		BoxLayout tLayout;
		JPanel tPrivateCertJPanel;
		Dimension tMinSize = new Dimension (20, 70);
		int tCount;
		String tTitle;

		tPrivatesJPanel = new JPanel ();
		tLayout = new BoxLayout (tPrivatesJPanel, BoxLayout.X_AXIS);
		tPrivatesJPanel.setLayout (tLayout);
		tPrivatesJPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		tPrivatesJPanel.add (Box.createRigidArea (tMinSize));
		tCount = 0;
		for (Corporation tCorporation : corporations) {
			if (!tCorporation.isClosed ()) {
				if (tCorporation.isPlayerOwned ()) {
					tPrivateCertJPanel = tCorporation.buildPrivateCertJPanel (aItemListener, aAvailableCash);
					tPrivatesJPanel.add (tPrivateCertJPanel);
					tPrivatesJPanel.add (Box.createRigidArea (tMinSize));
					tCount++;
				}
			}
		}
		tTitle = "Player Owned ";
		if (tCount > 3) {
			tTitle = tTitle + aCorpType + " Companies";
		} else if (tCount == 2) {
			tTitle = tTitle + aCorpType + "s";
		} else {
			tTitle = tTitle + aCorpType;
		}
		tPrivatesJPanel.setBorder (BorderFactory.createTitledBorder (tTitle));

		return tPrivatesJPanel;
	}

	public String getOperatingOwnerName () {
		String tOwnerName = ActorI.NO_NAME;

		if (typeName.equals (TYPE_NAMES [0])) {
			tOwnerName = roundManager.getOperatingOwnerName ();
		} else {
			for (Corporation tCorporation : corporations) {
				if (tCorporation.isOperating () && (tOwnerName == ActorI.NO_NAME)) {
					tOwnerName = tCorporation.getPresidentName ();
				}
			}
		}

		return tOwnerName;
	}

	public String getOwnerWhoWillOperate () {
		String tOwnerName = ActorI.NO_NAME;
		int tNextToOperate;
		Corporation tNextCorpToOperate;

		if (typeName.equals (TYPE_NAMES [0])) {
			tOwnerName = roundManager.getOperatingOwnerName ();
		} else {
			tNextToOperate = getNextToOperate ();
			if (tNextToOperate != NO_CORPORATION_INDEX) {
				tNextCorpToOperate = getCorporation (tNextToOperate);
				tOwnerName = tNextCorpToOperate.getPresidentName ();
			}
		}

		return tOwnerName;
	}

	public void clearSelections () {
		for (Corporation tCorporation : corporations) {
			tCorporation.clearCertificateSelections ();
		}
	}

	public void clearOperatedStatus () {
		ChangeStateAction tChangeCorporationStatesAction;
		boolean tCorporationStateChanged;
		String tRoundID;
		OperatingRound tOperatingRound;
		ActorI.ActionStates tRoundType;
		ActorI.ActionStates tCurrentCorporationState, tNewCorporationState;

		tCorporationStateChanged = false;
		tOperatingRound = getOperatingRound ();
		tRoundID = tOperatingRound.getID ();
		tRoundType = tOperatingRound.getRoundType ();
		tChangeCorporationStatesAction = new ChangeStateAction (tRoundType, tRoundID, tOperatingRound);
		for (Corporation tCorporation : corporations) {
			tCurrentCorporationState = tCorporation.getStatus ();
			tCorporation.clearOperatedStatus ();
			tNewCorporationState = tCorporation.getStatus ();
			if (!tCurrentCorporationState.equals (tNewCorporationState)) {
				tChangeCorporationStatesAction.addChangeCorporationStatusEffect (tCorporation, tCurrentCorporationState,
						tNewCorporationState);
				tCorporationStateChanged = true;
			}
		}
		if (tCorporationStateChanged) {
			tChangeCorporationStatesAction.setChainToPrevious (true);
			addAction (tChangeCorporationStatesAction);
		}
	}

	public void closeCompany (int aCompanyID, TransferOwnershipAction aTransferOwnershipAction) {
		for (Corporation tCorporation : corporations) {
			if (tCorporation.getID () == aCompanyID) {
				if (!tCorporation.isClosed ()) {
					tCorporation.close (aTransferOwnershipAction);
				}
			}
		}
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tCorporationElement;

		tXMLElement = aXMLDocument.createElement (EN_CORPORATIONS);
		for (Corporation tCorporation : corporations) {
			tCorporationElement = tCorporation.createElement (aXMLDocument);
			tXMLElement.appendChild (tCorporationElement);
		}
		aXMLDocument.appendChild (tXMLElement);

		return tXMLElement;
	}

	public void discardExcessTrains (BankPool aBankPool, BuyTrainAction aBuyTrainAction) {
		TrainCompany tTrainCompany;
		for (Corporation tCorporation : corporations) {
			if (tCorporation.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCorporation;
				tTrainCompany.discardExcessTrains (aBankPool, aBuyTrainAction);
			}
		}
	}

	public boolean anyPrivatesUnowned () {
		boolean tAnyPrivatesUnowned = false;

		if (gameHasPrivates ()) {
			if (typeName != ElementName.NO_ELEMENT_NAME) {
				if (typeName.equals (TYPE_NAMES [0])) {
					for (Corporation tCorporation : corporations) {
						if (tCorporation.isUnowned ()) {
							tAnyPrivatesUnowned = true;
						}
					}
				}
			}
		}

		return tAnyPrivatesUnowned;
	}

	public void applyClose () {
		for (Corporation tCorporation : corporations) {
			if (tCorporation.isClosed ()) {
				tCorporation.forceClose ();
			}
		}
	}

	public void removeAllBids () {
		for (Corporation tCorporation : corporations) {
			tCorporation.removeAllBids ();
		}
	}

	public ActorI getActor (String aActorName) {
		ActorI tActor;

		tActor = ActorI.NO_ACTOR;
		for (Corporation tCorporation : corporations) {
			// The Actor can be the Company Abbreviation or the Company Name, check both.
			if (aActorName.equals (tCorporation.getAbbrev ())) {
				tActor = tCorporation;
			} else if (aActorName.equals (tCorporation.getName ())) {
				tActor = tCorporation;
			}
		}

		return tActor;
	}

	public int getCountOfSelectedCertificates () {
		int tCountOfSelectedCertificates;

		tCountOfSelectedCertificates = 0;
		for (Corporation tCorporation : corporations) {
			tCountOfSelectedCertificates += tCorporation.getCountOfSelectedCertificates ();
		}

		return tCountOfSelectedCertificates;
	}

	public Certificate getCertificate (String aCompanyAbbrev, int aPercentage, boolean aPresidentShare) {
		Certificate tCertificate;

		tCertificate = Certificate.NO_CERTIFICATE;
		for (Corporation tCorporation : corporations) {
			if (tCertificate == Certificate.NO_CERTIFICATE) {
				if (aCompanyAbbrev.equals (tCorporation.getAbbrev ())) {
					tCertificate = tCorporation.getCertificate (aPercentage, aPresidentShare);
				}
			}
		}

		return tCertificate;
	}

	@Override
	public int getColCount () {
		Corporation tCorporation;
		Iterator<Corporation> tCorporationIter = corporations.iterator ();
		int tColCount;

		if (tCorporationIter.hasNext ()) {
			tCorporation = tCorporationIter.next ();
			tColCount = tCorporation.fieldCount ();
		} else {
			tColCount = 0;
		}

		return tColCount;
	}

	public Corporation getCorporation (int aCorporationIndex) {
		Corporation tCorporation;

		tCorporation = Corporation.NO_CORPORATION;

		if (aCorporationIndex < getRowCount ()) {
			tCorporation = corporations.get (aCorporationIndex);
		}

		return tCorporation;
	}

	public Corporation getCorporation (String aCompanyAbbrev) {
		Corporation tCorporation;
		String tAbbrev;

		tCorporation = Corporation.NO_CORPORATION;
		for (Corporation tCorporationI : corporations) {
			tAbbrev = tCorporationI.getAbbrev ();
			if (tAbbrev.equals (aCompanyAbbrev)) {
				tCorporation = tCorporationI;
			}
		}

		return tCorporation;
	}

	public Corporation getCorporationByID (int aCorporationID) {
		Corporation tCorporation;

		tCorporation = Corporation.NO_CORPORATION;
		for (Corporation tCorporationI : corporations) {
			if (aCorporationID == tCorporationI.getID ()) {
				tCorporation = tCorporationI;
			}
		}

		return tCorporation;
	}

	public int getCorporationCount () {
		return corporations.size ();
	}

	public ActorI.ActionStates getCorporationState (String aCorpStateName) {
		ActorI.ActionStates tCorporationState;
		GenericActor tGenericActor;

		tGenericActor = new GenericActor ();
		tCorporationState = tGenericActor.getCorporationActionState (aCorpStateName);

		return tCorporationState;
	}

	public void getCorporationStateElements (XMLDocument aXMLDocument, XMLElement aXMLParent) {
		XMLElement tXMLCorporationState;

		for (Corporation tCorporation : corporations) {
			tXMLCorporationState = tCorporation.getCorporationStateElement (aXMLDocument);
			aXMLParent.appendChild (tXMLCorporationState);
		}
	}

	public int getCountOfOpen () {
		int tCountOfOpen;

		tCountOfOpen = 0;
		for (Corporation tCorporation : corporations) {
			if (!tCorporation.isClosed ()) {
				tCountOfOpen++;
			}
		}

		return tCountOfOpen;
	}

	public int getCountOfPlayerOwned () {
		int tCountOfPlayerOwned;

		tCountOfPlayerOwned = 0;
		for (Corporation tCorporation : corporations) {
			if (tCorporation.isPlayerOwned ()) {
				tCountOfPlayerOwned++;
			}
		}

		return tCountOfPlayerOwned;
	}

	public ElementName getElementName () {
		ElementName tElementName;
		Corporation tCorporation;

		tElementName = ElementName.NO_ELEMENT_NAME;
		if (corporations != null) {
			tCorporation = corporations.get (0);
			tElementName = tCorporation.getElementName ();
		}

		return tElementName;
	}

	public int getCurrentlyOperating () {
		int tCurrentlyOperating = NO_CORPORATION_INDEX;
		int tCount = getCorporationCount ();
		Corporation tCorporation;

		for (int tIndex = 0; tIndex < tCount; tIndex++) {
			tCorporation = corporations.get (tIndex);
			if (tCorporation.isOperating () && (tCurrentlyOperating == NO_CORPORATION_INDEX)) {
				tCurrentlyOperating = tIndex;
			}
		}

		return tCurrentlyOperating;
	}

	public int getNextToOperate () {
		int tNextToOperate = NO_CORPORATION_INDEX;
		int tCount = getCorporationCount ();
		Corporation tCorporation;

		for (int tIndex = 0; tIndex < tCount; tIndex++) {
			tCorporation = corporations.get (tIndex);
			if (tCorporation.shouldOperate () && (tNextToOperate == NO_CORPORATION_INDEX)) {
				tNextToOperate = tIndex;
			}
		}

		return tNextToOperate;
	}


	public int getRowIndex (Corporation aCorporation) {
		int tRowIndex, tRowIndexFound;

		tRowIndex = 0;
		tRowIndexFound = 0;
		for (Corporation tCorporation : corporations) {
			if (tCorporation == aCorporation) {
				tRowIndexFound = tRowIndex;
			}
			tRowIndex++;
		}

		return tRowIndexFound;
	}

	@Override
	public int getRowCount () {
		return corporations.size ();
	}

	public String getThisTypeName () {
		return typeName.getString ();
	}

	public RoundManager getRoundManager () {
		return roundManager;
	}

	public Corporation getSelectedCorporation () {
		Corporation tSelectedCorporation;

		tSelectedCorporation = Corporation.NO_CORPORATION;
		for (Corporation tCorporation : corporations) {
			if (tCorporation.isSelectedForBuy ()) {
				tSelectedCorporation = tCorporation;
			}
		}

		return tSelectedCorporation;
	}

	@Override
	public String getTypeName () {
		return "Corporation List";
	}

	/**
	 * Test if ALL of the Train Companies in this Corporation List have operated or not.
	 *
	 * @return True if all Train Companies in this list have operated
	 *
	 */
	public boolean haveAllCompaniesOperated () {
		boolean tAllCompaniesOperated;

		tAllCompaniesOperated = true;
		for (Corporation tCorporation : corporations) {
			if (tCorporation.isATrainCompany ()) {
				if (tCorporation.shouldOperate ()) {
					tAllCompaniesOperated &= tCorporation.didOperate ();
				}
			}
		}

		return tAllCompaniesOperated;
	}

	public boolean isFirstTrainOfType (Train aTrain) {
		boolean tIsFirstTrainOfType;

		tIsFirstTrainOfType = true;
		for (Corporation tCorporation : corporations) {
			if (tCorporation.isATrainCompany ()) {
				if (tCorporation.hasTrainOfType (aTrain)) {
					tIsFirstTrainOfType = false;
				}
			}
		}

		return tIsFirstTrainOfType;
	}

	public void loadJTable () {
		int tColCount, tRowCount;
		int tRowIndex;
		PrivateCompany tPrivate;
		ShareCompany tShare;
		MinorCompany tMinor;

		tColCount = getColCount ();
		tRowCount = getRowCount ();
		initiateArrays (tRowCount, tColCount);

		tRowIndex = 0;
		for (Corporation tCorporationInfo : corporations) {
			if (typeName.equals (TYPE_NAMES [0])) {
				tPrivate = (PrivateCompany) tCorporationInfo;
				if (tRowIndex == 0) {
					tPrivate.addAllHeaders (this, 0);
				}
				tPrivate.addAllDataElements (this, tRowIndex, 0);
			} else if (typeName.equals (TYPE_NAMES [1])) {
				tMinor = (MinorCompany) tCorporationInfo;
				if (tRowIndex == 0) {
					tMinor.addAllHeaders (this, 0);
				}
				tMinor.addAllDataElements (this, tRowIndex, 0);
			} else if (typeName.equals (TYPE_NAMES [2])) {
				tShare = (ShareCompany) tCorporationInfo;
				if (tRowIndex == 0) {
					tShare.addAllHeaders (this, 0);
				}
				tShare.addAllDataElements (this, tRowIndex, 0);
			}
			tRowIndex++;
		}
		if (tRowCount > 0) {
			setModel ();
		}
	}

	public void loadStates (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;
		ElementName tElementName = getElementName ();

		tXMLNodeList = new XMLNodeList (corporationParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, tElementName);
	}

	ParsingRoutineI corporationParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			String tAbbrev;
			Corporation tCorporation;

			tAbbrev = aChildNode.getThisAttribute (Corporation.AN_ABBREV);
			tCorporation = getCorporation (tAbbrev);
			if (tCorporation == Corporation.NO_CORPORATION) {
				System.err.println ("Did not find a " + aChildNode.getNodeName () + " with name " + tAbbrev);
			} else {
				tCorporation.loadStatus (aChildNode);
			}
		}
	};

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNodeList tXMLNodeList;
		XMLNode XMLCorporationListRoot;

		if (typeName == ElementName.NO_ELEMENT_NAME) {
			System.err.println ("Before loading Corporations, need to know which Type to load.");
		} else {
			XMLCorporationListRoot = aXMLDocument.getDocumentNode ();
			tXMLNodeList = new XMLNodeList (corporationListParsingRoutine, this);
			tXMLNodeList.parseXMLNodeList (XMLCorporationListRoot, typeName);
		}
		for (Corporation tCorporation : corporations) {
			tCorporation.setCorporationList (this);
		}
		loadJTable ();
	}

	public void closeAll (BuyTrainAction aBuyTrainAction) {
		for (Corporation tCorporation : corporations) {
			tCorporation.close (aBuyTrainAction);
		}
	}

	// Payment to Privates -- Single Owner (Player or Corporation)
	public void payPrivateRevenues (Bank aBank, OperatingRound aOperatingRound) {
		PrivateCompany tPrivate;
		int tRevenue;
		PortfolioHolderI tOwner;
		CashHolderI tOwnerCashHolder;
		PayRevenueAction tPayRevenueAction;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isActive ()) {
				if (tCorporation.isAPrivateCompany ()) {
					tPrivate = (PrivateCompany) tCorporation;
					tRevenue = tPrivate.getRevenue ();
					tOwner = tPrivate.getOwner ();
					tOwnerCashHolder = (CashHolderI) tOwner;
					aBank.transferCashTo (tOwnerCashHolder, tRevenue);
					tPayRevenueAction = new PayRevenueAction (aOperatingRound.getRoundType (), aOperatingRound.getID (),
							tPrivate);
					tPayRevenueAction.addCashTransferEffect (aBank, tOwnerCashHolder, tRevenue);
					aOperatingRound.addAction (tPayRevenueAction);
				}
			}
		}
	}

	public void handleQueryBenefits (JFrame aRoundFrame) {
		PrivateCompany tPrivate;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isActive ()) {
				if (tCorporation.isAPrivateCompany ()) {
					tPrivate = (PrivateCompany) tCorporation;
					tPrivate.handleQueryBenefits (aRoundFrame);
				}
			}
		}
	}

	public void printReport () {
		System.out.println ("Corporation Report");
		for (Corporation tCorporation : corporations) {
			tCorporation.printReport ();
		}
	}

	public void rustAllTrainsNamed (String aTrainName, TrainPortfolio aRustedTrainsPortfolio, Bank aBank,
			BuyTrainAction aBuyTrainAction) {
		TrainCompany tTrainCompany;
		TrainPortfolio tTrainPortfolio;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCorporation;
				tTrainPortfolio = tTrainCompany.getTrainPortfolio ();
				tTrainPortfolio.rustAllTrainsNamed (aTrainName, aRustedTrainsPortfolio, tTrainCompany, aBank,
						aBuyTrainAction);
			}
		}
	}

	public void setTypeName (ElementName aTypeName) {
		int tIndex;
		boolean tFoundType = false;

		for (tIndex = 0; (tIndex < 3) && !tFoundType; tIndex++) {
			if (aTypeName.equals (TYPE_NAMES [tIndex])) {
				tFoundType = true;
			}
		}
		if (tFoundType) {
			typeName = aTypeName;
		} else {
			typeName = ElementName.NO_ELEMENT_NAME;
		}
	}

	public boolean companyStartedOperating (int aIndex) {
		Corporation tCorporation;
		boolean tCompanyStartedOperating;

		tCorporation = corporations.get (aIndex);
		tCompanyStartedOperating = tCorporation.isOperating ();

		return tCompanyStartedOperating;
	}

	public void prepareCorporation (int aIndex) {
		Corporation tCorporation;

		tCorporation = corporations.get (aIndex);
		tCorporation.prepareCorporation ();
	}

	public void showCompanyFrame (int aIndex) {
		Corporation tCorporation;

		tCorporation = corporations.get (aIndex);
		tCorporation.showFrame ();
	}

	ParsingRoutineI corporationListParsingRoutine = new ParsingRoutineIO () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode, Object aMetaObject) {
			PrivateCompany tPrivateInfo;
			MinorCompany tMinorCompanyInfo;
			ShareCompany tShareCompanyInfo;
			CorporationList tCorporationList;

			tCorporationList = (CorporationList) aMetaObject;
			if (typeName.equals (TYPE_NAMES [0])) {
				tPrivateInfo = new PrivateCompany (aChildNode, tCorporationList);
				corporations.add (tPrivateInfo);
			} else if (typeName.equals (TYPE_NAMES [1])) {
				tMinorCompanyInfo = new MinorCompany (aChildNode, tCorporationList);
				corporations.add (tMinorCompanyInfo);
			} else if (typeName.equals (TYPE_NAMES [2])) {
				tShareCompanyInfo = new ShareCompany (aChildNode, tCorporationList);
				corporations.add (tShareCompanyInfo);
			}
		}
	};

	public JPanel buildFullCorpsJPanel (CorporationFrame aCorporationFrame, Corporation aBuyingCorporation,
			GameManager aGameManager, boolean aFullTrainPortfolio, boolean aCanBuyTrain, String aDisableToolTipReason) {
		JPanel tFullCorpsJPanel;
		JPanel tOtherCorpsInfoJPanel;
		JPanel tOtherCorpsJPanel1;

		tFullCorpsJPanel = new JPanel ();
		tFullCorpsJPanel.setLayout (new BoxLayout (tFullCorpsJPanel, BoxLayout.X_AXIS));
		tOtherCorpsJPanel1 = new JPanel ();
		tOtherCorpsJPanel1.setLayout (new BoxLayout (tOtherCorpsJPanel1, BoxLayout.Y_AXIS));
		tOtherCorpsJPanel1
				.setBorder (BorderFactory.createTitledBorder (" Other Train Corporations - In Operating Order "));

		tOtherCorpsInfoJPanel = buildOtherCorpsInfoJPanel (aCorporationFrame, aBuyingCorporation, aGameManager,
				aFullTrainPortfolio, aCanBuyTrain, aDisableToolTipReason);
		tOtherCorpsJPanel1.add (Box.createVerticalStrut (5));
		tOtherCorpsJPanel1.add (tOtherCorpsInfoJPanel);
		tOtherCorpsJPanel1.add (Box.createVerticalStrut (5));
		tFullCorpsJPanel.add (tOtherCorpsJPanel1);

		return tFullCorpsJPanel;
	}

	public JPanel buildOtherCorpsInfoJPanel (CorporationFrame aCorporationFrame, Corporation aBuyingCorporation,
			GameManager aGameManager, boolean aFullTrainPortfolio, boolean aCanBuyTrain, String aDisableToolTipReason) {
		JPanel tOtherCorpsInfoJPanel;
		JPanel tOtherCorpInfoJPanel;
		JPanel tOperatingCorpJPanel;
		JPanel tScrollableCorpJPanel;

		JScrollPane tScrollCorpPane;
		Color tFgColor, tBgColor;
		TrainCompany tTrainCompany;
		Border tBorder;

		tOtherCorpsInfoJPanel = new JPanel ();
		tOtherCorpsInfoJPanel.setLayout (new BoxLayout (tOtherCorpsInfoJPanel, BoxLayout.X_AXIS));
		tOtherCorpsInfoJPanel.add (Box.createHorizontalStrut (10));

		for (Corporation tCorporation : corporations) {
			if (aBuyingCorporation != tCorporation) {
				tOtherCorpInfoJPanel = tCorporation.buildPortfolioTrainsJPanel (aCorporationFrame, aGameManager,
						aFullTrainPortfolio, aCanBuyTrain, aDisableToolTipReason, aBuyingCorporation);
				tOtherCorpsInfoJPanel.add (tOtherCorpInfoJPanel);
			} else {
				if (aBuyingCorporation.isATrainCompany ()) {
					tTrainCompany = (TrainCompany) aBuyingCorporation;
					tBgColor = tTrainCompany.getBgColor ();
					tFgColor = tTrainCompany.getFgColor ();
				} else {
					tBgColor = Color.BLACK;
					tFgColor = Color.white;
				}
				tBorder = setupBorder (true, tFgColor, tBgColor);
//				SET BORDER HERE

				tOperatingCorpJPanel = new JPanel ();
				tOperatingCorpJPanel.setLayout (new BoxLayout (tOperatingCorpJPanel, BoxLayout.Y_AXIS));
				tOperatingCorpJPanel.setBorder (tBorder);
				tOperatingCorpJPanel.add (new JLabel (tCorporation.getAbbrev ()));
				tOperatingCorpJPanel.add (new JLabel ("State: Operating"));
				tOtherCorpsInfoJPanel.add (tOperatingCorpJPanel);
			}
			tOtherCorpsInfoJPanel.add (Box.createHorizontalStrut (10));
		}
		tScrollableCorpJPanel = new JPanel ();
		tScrollableCorpJPanel.setLayout (new BoxLayout (tScrollableCorpJPanel, BoxLayout.X_AXIS));
		tScrollCorpPane = new JScrollPane (tOtherCorpsInfoJPanel);
		tScrollCorpPane.setLayout (new ScrollPaneLayout ());
		tScrollCorpPane.setBorder (EMPTY_BORDER);
		tScrollableCorpJPanel.add (tScrollCorpPane);

		return tScrollableCorpJPanel;
	}

	public Border setupBorder (boolean aSamePresident, Color aFgColor, Color aBgColor) {
		Border tPanelBorder, tBackgroundBorder, tOuterBorder, tRaisedBevel;
		Border tLoweredBevel, tBevelBorder1, tBevelBorder2;

		tBackgroundBorder = setupBackgroundBorder (5);
		if (aSamePresident) {
			tRaisedBevel = BorderFactory.createBevelBorder (BevelBorder.RAISED, aFgColor, aBgColor);
			tLoweredBevel = BorderFactory.createBevelBorder (BevelBorder.LOWERED, aFgColor, aBgColor);
			tBevelBorder1 = BorderFactory.createCompoundBorder (tRaisedBevel, tLoweredBevel);
			tBevelBorder2 = BorderFactory.createCompoundBorder (tBevelBorder1, tBackgroundBorder);
			tPanelBorder = BorderFactory.createCompoundBorder (tBackgroundBorder, tBevelBorder2);
		} else {
			tOuterBorder = setupOuterBorder (aFgColor, aBgColor);
			tPanelBorder = BorderFactory.createCompoundBorder (tOuterBorder, tBackgroundBorder);
		}

		return tPanelBorder;
	}

	private Border setupOuterBorder (Color aFgColor, Color aBgColor) {
		Border tOuterBorder;

		tOuterBorder = BorderFactory.createLineBorder (aBgColor, 2);

		return tOuterBorder;
	}

	private Border setupBackgroundBorder (int aWidth) {
		Border tBackgroundBorder;
		Color tBackgroundColor;

		tBackgroundColor = new Color (237, 237, 237);
		tBackgroundBorder = BorderFactory.createLineBorder (tBackgroundColor, aWidth);

		return tBackgroundBorder;
	}

	public Border setupBorder (Color aFgColor, Color aBgColor) {
		Border tCorpBorder, tOuterBorder, tInnerBorder;

		tOuterBorder = setupOuterBorder (aFgColor, aBgColor);
		tInnerBorder = setupBackgroundBorder (2);
		tCorpBorder = BorderFactory.createCompoundBorder (tOuterBorder, tInnerBorder);

		return tCorpBorder;
	}

	public boolean isSelectedTrainItem (String aCurrentAbbrev, Object aItem) {
		boolean tSelectedTrainItem = false;
		int tSelectedTrainCount;

		tSelectedTrainCount = getSelectedTrainCount (aCurrentAbbrev);
		if (tSelectedTrainCount > 0) {
			tSelectedTrainItem = true;
		}

		return tSelectedTrainItem;
	}

	public int getSelectedTrainCount (String aCurrentAbbrev) {
		int tSelectedCount = 0;
		TrainHolderI tTrainHolder;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isActive ()) {
				if (!aCurrentAbbrev.equals (tCorporation.getAbbrev ())) {
					tTrainHolder = tCorporation.getLocalSelectedTrainHolder ();
					if (tTrainHolder != TrainHolderI.NO_TRAIN_HOLDER) {
						tSelectedCount += tTrainHolder.getLocalSelectedTrainCount ();
					}
				}
			}
		}

		return tSelectedCount;
	}

	public TrainHolderI getOtherSelectedTrainHolder (String aCurrentAbbrev) {
		TrainHolderI tTrainHolder = TrainHolderI.NO_TRAIN_HOLDER;

		for (Corporation tCorporation : corporations) {
			if (tTrainHolder == TrainHolderI.NO_TRAIN_HOLDER) {
				if (!aCurrentAbbrev.equals (tCorporation.getAbbrev ())) {
					tTrainHolder = tCorporation.getLocalSelectedTrainHolder ();
				}
			}
		}

		return tTrainHolder;
	}

	public TrainCompany getOperatingTrainCompany () {
		TrainCompany tTrainCompany = (TrainCompany) Corporation.NO_CORPORATION;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isOperating () && (tTrainCompany == Corporation.NO_CORPORATION)) {
				tTrainCompany = (TrainCompany) tCorporation;
			}
		}

		return tTrainCompany;
	}

	public ShareCompany getOperatingCompany () {
		ShareCompany tOperatingCompany = (ShareCompany) Corporation.NO_CORPORATION;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isOperating () && (tOperatingCompany == (ShareCompany) Corporation.NO_CORPORATION)) {
				tOperatingCompany = (ShareCompany) tCorporation;
			}
		}

		return tOperatingCompany;
	}

	public void setAllMustBuyTrain () {
		for (Corporation tCorporation : corporations) {
			tCorporation.setMustBuyTrain (true);
		}
	}

	public PrivateCompany getPrivateCompanyAtMapCell (MapCell aMapCell) {
		PrivateCompany tPrivateCompany = (PrivateCompany) Corporation.NO_CORPORATION;
		MapCell tHomeCity1, tHomeCity2;

		for (Corporation tCorporation : corporations) {
			if (!tCorporation.isClosed ()) {
				tHomeCity1 = tCorporation.getHomeCity1 ();
				tHomeCity2 = tCorporation.getHomeCity2 ();
				if (tHomeCity1 != MapCell.NO_MAP_CELL) {
					if (tHomeCity1 == aMapCell) {
						tPrivateCompany = (PrivateCompany) tCorporation;
					}
				}
				if ((tHomeCity2 != MapCell.NO_MAP_CELL) &&
					(tPrivateCompany == (PrivateCompany) Corporation.NO_CORPORATION)) {
					if (tHomeCity2 == aMapCell) {
						tPrivateCompany = (PrivateCompany) tCorporation;
					}
				}
			}
		}

		return tPrivateCompany;
	}

	public void fixLoadedRoutes (MapFrame aMapFrame) {
		TrainCompany tTrainCompany;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCorporation;
				tTrainCompany.fixLoadedRoutes (aMapFrame);
			}
		}
	}

	public int getTotalCorpCash () {
		int tTotalCorpCash = 0;
		TrainCompany tTrainCompany;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCorporation;
				tTotalCorpCash += tTrainCompany.getCash ();
			}
		}

		return tTotalCorpCash;
	}

	public int getTotalEscrow () {
		int tTotalEscrow = 0;
		PrivateCompany tPrivateCompany;
		for (Corporation tCorporation : corporations) {
			if (tCorporation.isAPrivateCompany ()) {
				tPrivateCompany = (PrivateCompany) tCorporation;
				tTotalEscrow += tPrivateCompany.getTotalEscrows ();
			}
		}

		return tTotalEscrow;
	}

	public void clearTrainSelections () {
		TrainCompany tTrainCompany;

		for (Corporation tCorporation : corporations) {
			if (tCorporation.isATrainCompany ()) {
				tTrainCompany = (TrainCompany) tCorporation;
				tTrainCompany.clearAllTrainSelections ();
			}
		}
	}

	public void removeInactiveCompanies () {
		List<Corporation> tCorporations;

		tCorporations = new LinkedList<> ();
		for (Corporation tCorporation : corporations) {
			if (tCorporation.isInActive ()) {
				tCorporation.removeHomeBases ();
			} else {
				tCorporations.add (tCorporation);
			}
		}
		corporations = tCorporations;
	}

	public void clearPrivateSelections () {
		// TODO Need to walk through the Privates, and clear all Selections

	}

	public Benefit findBenefit (String aBenefitName) {
		Benefit tFoundBenefit = Benefit.NO_BENEFIT;
		PrivateCompany tPrivateCompany;

		for (Corporation tCorporation : corporations) {
			if (tFoundBenefit == Benefit.NO_BENEFIT) {
				if (tCorporation.isAPrivateCompany ()) {
					tPrivateCompany = (PrivateCompany) tCorporation;
					tFoundBenefit = tPrivateCompany.findBenefit (aBenefitName);
				}
			}
		}

		return tFoundBenefit;
	}

	// Pass method calls over to Round Manager

	public void addAction (Action aAction) {
		roundManager.addAction (aAction);
	}

	/**
	 * Append Error Report String to Action Report Frame as an Error
	 *
	 * @param aErrorReport String Text to append as an Error to the end of the Action Report Frame
	 *
	 */
	public void appendErrorReport (String aReport) {
		roundManager.appendErrorReport (aReport);
	}

	public void bringMapToFront () {
		roundManager.bringMapToFront ();
	}

	public void bringTileTrayToFront () {
		roundManager.bringTileTrayToFront ();
	}

	public JPanel buildPrivatesForPurchaseJPanel (ItemListener aItemListener, int aAvailableCash) {
		return roundManager.buildPrivatesForPurchaseJPanel (aItemListener, aAvailableCash);
	}

	public boolean canBuyPrivate () {
		return roundManager.canBuyPrivate ();
	}

	public boolean canBuyTrainInPhase () {
		return roundManager.canBuyTrainInPhase ();
	}

	public boolean canPayHalfDividend () {
		return roundManager.canPayHalfDividend ();
	}

	public void clearBankSelections () {
		roundManager.clearBankSelections ();
	}

	public void declareBankuptcyAction (Corporation aCorporation) {
		roundManager.declareBankuptcyAction (aCorporation);
	}

	public void doneAction (Corporation aCorporation) {
		roundManager.doneAction (aCorporation);
	}

	public boolean doIncrementalCapitalization () {
		return roundManager.doIncrementalCapitalization ();
	}

	public void enterPlaceTileMode () {
		roundManager.enterPlaceTileMode ();
	}

	public void enterPlaceTokenMode () {
		roundManager.enterPlaceTokenMode ();
	}

	public void enterSelectRouteMode (RouteInformation aRouteInformation) {
		roundManager.enterSelectRouteMode (aRouteInformation);
	}

	public void exitSelectRouteMode () {
		roundManager.exitSelectRouteMode ();
	}

	public boolean gameHasPrivates () {
		return roundManager.gameHasPrivates ();
	}

	public boolean gameHasLoans () {
		return roundManager.gameHasLoans ();
	}

	public Bank getBank () {
		return roundManager.getBank ();
	}

	public BankPool getBankPool () {
		return roundManager.getBankPool ();
	}

	public int getCapitalizationLevel (int aSharesSold) {
		return roundManager.getCapitalizationLevel (aSharesSold);
	}

	public CashHolderI getCashHolderByName (String aCashHolderName) {
		return roundManager.getCashHolderByName (aCashHolderName);
	}

	public int getCountOfSelectedPrivates () {
		return roundManager.getCountOfSelectedPrivates ();
	}

	public PhaseInfo getCurrentPhaseInfo () {
		return roundManager.getCurrentPhaseInfo ();
	}

	public String getCurrentRoundOf () {
		return roundManager.getCurrentRoundOf ();
	}

	public GameManager getGameManager () {
		return roundManager.getGameManager ();
	}

	public Action getLastAction () {
		return roundManager.getLastAction ();
	}

	public Logger getLogger () {
		return roundManager.getLogger ();
	}

	public MapFrame getMapFrame () {
		return roundManager.getMapFrame ();
	}

	public int getMinSharesToFloat () {
		return roundManager.getMinSharesToFloat ();
	}

	public int getMinorTrainLimit () {
		return roundManager.getMinorTrainLimit ();
	}

	public Point getOffsetRoundFrame () {
		return roundManager.getOffsetRoundFrame ();
	}

	public OperatingRound getOperatingRound () {
		return roundManager.getOperatingRound ();
	}

	public String getOperatingRoundID () {
		return roundManager.getOperatingRoundID ();
	}

	public CorporationList getPrivates () {
		return roundManager.getPrivates ();
	}

	public PrivateCompany getSelectedPrivateCompanyToBuy () {
		return roundManager.getSelectedPrivateCompanyToBuy ();
	}

	public int getTrainLimit (boolean aGovtRailway) {
		return roundManager.getTrainLimit (aGovtRailway);
	}

	public boolean isLoading () {
		return roundManager.isLoading ();
	}

	public boolean isPlaceTileMode () {
		return roundManager.isPlaceTileMode ();
	}

	public boolean isPlaceTokenMode () {
		return roundManager.isPlaceTokenMode ();
	}

	public boolean mapVisible () {
		return roundManager.mapVisible ();
	}

	public void performPhaseChange (TrainCompany aTrainCompany, Train aTrain, BuyTrainAction aBuyTrainAction) {
		roundManager.performPhaseChange (aTrainCompany, aTrain, aBuyTrainAction);
	}

	public void repaintMapFrame () {
		roundManager.repaintMapFrame ();
	}

	public void showMap () {
		roundManager.showMap ();
	}

	public void showTileTray () {
		roundManager.showTileTray ();
	}

	public boolean tileTrayVisible () {
		return roundManager.tileTrayVisible ();
	}

	public void undoAction () {
		roundManager.undoLastAction ();
	}

	public void updateRoundFrame () {
		roundManager.updateRoundFrame ();
	}
}
