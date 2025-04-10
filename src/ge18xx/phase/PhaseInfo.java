package ge18xx.phase;

import java.util.Comparator;

import org.w3c.dom.NodeList;

import ge18xx.round.action.PreparedAction;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

//
//  PhaseInfo.java
//  Game_18XX
//
//  Created by Mark Smith on 12/25/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class PhaseInfo {
	public static final ElementName EN_PHASES = new ElementName ("Phases");
	public static final ElementName EN_PHASE = new ElementName ("Phase");
	public static final ElementName EN_PREPARED_ACTION = new ElementName ("PreparedAction");
	public static final AttributeName AN_PHASES = new AttributeName ("phases");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_SUB_NAME = new AttributeName ("subName");
	public static final AttributeName AN_ROUNDS = new AttributeName ("rounds");
	public static final AttributeName AN_TILES = new AttributeName ("tiles");
	public static final AttributeName AN_TRAIN_LIMIT = new AttributeName ("trainLimit");
	public static final AttributeName AN_MAJOR_TRAIN_LIMIT = new AttributeName ("majorTrainLimit");
	public static final AttributeName AN_MINOR_TRAIN_LIMIT = new AttributeName ("minorTrainLimit");
	public static final AttributeName AN_GOVT_TRAIN_LIMIT = new AttributeName ("govtTrainLimit");
	public static final AttributeName AN_OFF_BOARD = new AttributeName ("offBoard");
	public static final AttributeName AN_CAN_BUY_PRIVATE = new AttributeName ("canBuyPrivate");
	public static final AttributeName AN_CAN_BUY_TRAIN = new AttributeName ("canBuyTrain");
	public static final AttributeName AN_CLOSE_PRIVATES = new AttributeName ("closePrivate");
	public static final AttributeName AN_LOANS_ALLOWED = new AttributeName ("loansAllowed");
	public static final AttributeName AN_GOVERNMENT_CAN_FORM = new AttributeName ("governmentCanForm");
	public static final AttributeName AN_GOVERNMENT_MUST_FORM = new AttributeName ("governmentMustForm");
	public static final AttributeName AN_WILL_FLOAT = new AttributeName ("willFloat");
	public static final AttributeName AN_MIN_TO_FLOAT = new AttributeName ("minToFloat");
	public static final AttributeName AN_MIN_TO_FLOAT_LAST = new AttributeName ("minToFloatLast");
	public static final AttributeName AN_TILE_LAYS_ALLOWED = new AttributeName ("tileLaysAllowed");
	public static final AttributeName AN_TRIGGER_CLASS = new AttributeName ("triggerClass");
	public static final AttributeName AN_FORM_COMPANY_ID = new AttributeName ("formCompanyID");
	public static final PhaseInfo NO_PHASE_INFO = null;
	public static final int SORT_PHASE1_BEFORE_PHASE2 = -100;
	public static final int SORT_PHASE2_BEFORE_PHASE1 = 100;
	public static final int STANDARD_MIN_SHARES = 6;
	public static final int STANDARD_SHARE_SIZE = 10;
	public static final int DEFAULT_TILE_LAYS = 1;
	public static final int NO_LIMIT = 99;
	public static final int NO_NAME = 0;
	public static final int NO_ROUNDS = 0;
	public static final String [] NO_TILES = null;
	public static final String NO_OFF_BOARD = null;
	int name;
	int subName;
	int rounds;
	String tiles[];
	int tileLaysAllowed;
	int trainLimit;
	int minorTrainLimit;
	int govtTrainLimit;
	int willFloat;
	int minToFloat; // Minimum number of Shares sold to Float the Company at time of Preparing
					// Company
	int minToFloatLast; // Minimum number of Shares sold to Float the Company when last Train of Phase
						// has been Sold (ie when next train purchase triggers Phase Change)
	int formCompanyId;
	boolean canBuyPrivate;
	boolean canBuyTrain;
	boolean closePrivates;
	boolean loansAllowed;
	boolean governmentCanForm;
	boolean governmentMustForm;
	String offBoard;
	String triggerClass;
	String preparedActionXML;
	PreparedAction preparedAction;
	
	public PhaseInfo (XMLNode aCellNode) {
		int tName;
		int tSubName;
		int tRounds;
		int tTrainLimit;
		int tMinorTrainLimit;
		int tGovtTrainLimit;
		int tFormCompanyId;
		boolean tCanBuyPrivate;
		boolean tClosePrivate;
		boolean tGovernmentCanForm;
		boolean tGovernmentMustForm;
		boolean tCanBuyTrain;
		boolean tLoansAllowed;
		String tOffBoard;
		String tTileColors;
		String tTiles[];
		String tTriggerClass;

		tName = aCellNode.getThisIntAttribute (AN_NAME);
		tSubName = aCellNode.getThisIntAttribute (AN_SUB_NAME);
		tRounds = aCellNode.getThisIntAttribute (AN_ROUNDS);
		tTileColors = aCellNode.getThisAttribute (AN_TILES);
		tTiles = tTileColors.split (GUI.COMMA);
		tTrainLimit = aCellNode.getThisIntAttribute (AN_TRAIN_LIMIT, NO_LIMIT);
		if (tTrainLimit == NO_LIMIT) {
			tTrainLimit = aCellNode.getThisIntAttribute (AN_MAJOR_TRAIN_LIMIT, NO_LIMIT);
		}
		tMinorTrainLimit = aCellNode.getThisIntAttribute (AN_MINOR_TRAIN_LIMIT, NO_LIMIT);
		tGovtTrainLimit = aCellNode.getThisIntAttribute (AN_GOVT_TRAIN_LIMIT, NO_LIMIT);
		tOffBoard = aCellNode.getThisAttribute (AN_OFF_BOARD);
		
		tCanBuyPrivate = aCellNode.getThisBooleanAttribute (AN_CAN_BUY_PRIVATE);
		tCanBuyTrain = aCellNode.getThisBooleanAttribute (AN_CAN_BUY_TRAIN);
		tClosePrivate = aCellNode.getThisBooleanAttribute (AN_CLOSE_PRIVATES);
		tLoansAllowed = aCellNode.getThisBooleanAttribute (AN_LOANS_ALLOWED);
		tGovernmentCanForm = aCellNode.getThisBooleanAttribute (AN_GOVERNMENT_CAN_FORM);
		tGovernmentMustForm = aCellNode.getThisBooleanAttribute (AN_GOVERNMENT_MUST_FORM);
		tTriggerClass = aCellNode.getThisAttribute (AN_TRIGGER_CLASS);
		tFormCompanyId = aCellNode.getThisIntAttribute (AN_FORM_COMPANY_ID);
		
		setValues (tName, tSubName, tRounds, tTiles, tTrainLimit, tMinorTrainLimit, tGovtTrainLimit, tOffBoard,
				tCanBuyPrivate, tCanBuyTrain, tClosePrivate, tLoansAllowed, tGovernmentCanForm, tGovernmentMustForm,
				tTriggerClass, tFormCompanyId);
		parseMajorMinorValues (aCellNode);
		loadPreparedActionXML (aCellNode);
	}

	public XMLElement getElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		String tTileColors;

		tXMLElement = aXMLDocument.createElement (EN_PHASE);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_SUB_NAME, subName);
		tXMLElement.setAttribute (AN_ROUNDS, rounds);
		tTileColors = getTiles ();
		tXMLElement.setAttribute (AN_TILES, tTileColors);
		tXMLElement.setAttribute (AN_TRAIN_LIMIT, trainLimit);
		tXMLElement.setAttribute (AN_MINOR_TRAIN_LIMIT, minorTrainLimit);

		tXMLElement.setAttribute (AN_GOVT_TRAIN_LIMIT, govtTrainLimit);
		tXMLElement.setAttribute (AN_OFF_BOARD, offBoard);
		if (canBuyPrivate) {
			tXMLElement.setAttribute (AN_CAN_BUY_PRIVATE, canBuyPrivate);
		}
		if (canBuyTrain) {
			tXMLElement.setAttribute (AN_CAN_BUY_TRAIN, canBuyTrain);
		}
		if (closePrivates) {
			tXMLElement.setAttribute (AN_CLOSE_PRIVATES, closePrivates);
		}
		if (governmentCanForm) {
			tXMLElement.setAttribute (AN_GOVERNMENT_CAN_FORM, governmentCanForm);
		}
		if (governmentMustForm) {
			tXMLElement.setAttribute (AN_GOVERNMENT_MUST_FORM, governmentMustForm);
		}
		tXMLElement.setAttribute (AN_TRIGGER_CLASS, triggerClass);
		tXMLElement.setAttribute (AN_FORM_COMPANY_ID, formCompanyId);

		return tXMLElement;
	}

	private void parseMajorMinorValues (XMLNode aCellNode) {
		int tWillFloat;
		int tMinToFloat;
		int tMinToFloatLast;
		int tTileLaysAllowed;

		tWillFloat = aCellNode.getThisIntAttribute (AN_WILL_FLOAT, STANDARD_MIN_SHARES);
		setWillFloat (tWillFloat);
		tMinToFloat = aCellNode.getThisIntAttribute (AN_MIN_TO_FLOAT, tWillFloat);
		setMinToFloat (tMinToFloat);
		tMinToFloatLast = aCellNode.getThisIntAttribute (AN_MIN_TO_FLOAT_LAST, tWillFloat);
		setMinToFloatLast (tMinToFloatLast);
		tTileLaysAllowed = aCellNode.getThisIntAttribute (AN_TILE_LAYS_ALLOWED, DEFAULT_TILE_LAYS);
		setTileLaysAllowed (tTileLaysAllowed);
	}

	private void loadPreparedActionXML (XMLNode aCellNode) {
		String tChildName;
		XMLNode tChildNode;
		NodeList tChildren;
		int tIndex;
		int tChildrenCount;

		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (EN_PREPARED_ACTION.equals (tChildName)) {
				setPreparedActionXML (tChildNode.toString ());
			}
		}
	}
	
	private void setPreparedActionXML (String aPreparedActionXML) {
		preparedActionXML = aPreparedActionXML;
	}
	
	public String getPreparedActionXML () {
		return preparedActionXML;
	}
	
	public void setPreparedAction (PreparedAction aPreparedAction) {
		preparedAction = aPreparedAction;
	}
	
	private void setTileLaysAllowed (int aTileLaysAllowed) {
		tileLaysAllowed = aTileLaysAllowed;
	}

	public int getTileLaysAllowed () {
		return tileLaysAllowed;
	}
	
	private void setWillFloat (int aWillFloat) {
		willFloat = aWillFloat;
	}

	private void setMinToFloat (int aMinToFloat) {
		minToFloat = aMinToFloat;
	}

	private void setMinToFloatLast (int aMinToFloatLast) {
		minToFloatLast = aMinToFloatLast;
	}

	public int getMinToFloat () {
		return minToFloat;
	}

	public int getMinToFloatLast () {
		return minToFloatLast;
	}

	public int getWillFloat () {
		return willFloat;
	}

	public int getWillFloatPercent () {
		return willFloat * STANDARD_SHARE_SIZE;
	}

	public String getOffBoard () {
		return offBoard;
	}

	public int getOperatingRoundsCount () {
		return rounds;
	}

	public int getTrainLimit (boolean aGovtRailway) {
		int tTrainLimit;

		if (aGovtRailway) {
			tTrainLimit = getGovtTrainLimit ();
		} else {
			tTrainLimit = getTrainLimit ();
		}

		return tTrainLimit;
	}

	public int getGovtTrainLimit () {
		return govtTrainLimit;
	}

	public int getMinorTrainLimit () {
		return minorTrainLimit;
	}

	public int getTrainLimit () {
		return trainLimit;
	}

	public String getFullName () {
		return name + "." + subName;
	}

	public int getName () {
		return name;
	}

	public String getTiles () {
		return String.join (GUI.COMMA_SPACE, tiles);
	}

	public int getSubName () {
		return subName;
	}

	public boolean getCanBuyPrivate () {
		return canBuyPrivate;
	}

	public boolean getClosePrivates () {
		return closePrivates;
	}

	public boolean getGovernmentCanForm () {
		return governmentCanForm;
	}

	public boolean getGovernmentMustForm () {
		return governmentMustForm;
	}

	public void printPhaseInfo () {
		System.out.println ("Phase Name " + name);		// PRINTLOG
		System.out.println ("SubPhase Name " + subName);
		System.out.println ("Rounds " + rounds);
		System.out.println ("Tiles " + getTiles ());
		System.out.println ("Train Limit " + trainLimit);
		System.out.println ("Minor Train Limit  " + minorTrainLimit);
		System.out.println ("Government Train Limit " + govtTrainLimit);
		System.out.println ("Off Board Level " + offBoard);
		System.out.println ("Can Buy Private " + canBuyPrivate);
		System.out.println ("Can Buy Train from Other Corps " + canBuyTrain);
		System.out.println ("Close Privates " + closePrivates);
		System.out.println ("Government Can Form " + governmentCanForm);
		System.out.println ("Government Must Form " + governmentMustForm);
	}

	public void setValues (int aName, int aSubName, int aRounds, String [] aTiles, int aTrainLimit,
			int aMinorTrainLimit, int aGovtTrainLimit, String aOffBoard, boolean aCanBuyPrivate, boolean aCanBuyTrain,
			boolean aClosePrivates, boolean aLoansAllowed, boolean aGovernmentCanForm, 
			boolean aGovernmentMustForm, String aTriggerClass, int aFormCompanyId) {
		name = aName;
		subName = aSubName;
		rounds = aRounds;
		tiles = aTiles;
		trainLimit = aTrainLimit;
		minorTrainLimit = aMinorTrainLimit;
		govtTrainLimit = aGovtTrainLimit;
		offBoard = aOffBoard;
		canBuyPrivate = aCanBuyPrivate;
		canBuyTrain = aCanBuyTrain;
		closePrivates = aClosePrivates;
		loansAllowed = aLoansAllowed;
		governmentCanForm = aGovernmentCanForm;
		governmentMustForm = aGovernmentMustForm;
		triggerClass = aTriggerClass;
		formCompanyId = aFormCompanyId;
	}

	public int getFormingCompanyId () {
		return formCompanyId;
	}
	
	public boolean canBuyTrainInPhase () {
		return canBuyTrain;
	}

	public String getTriggerClass () {
		return triggerClass;
	}
	
	public boolean loansAllowed () {
		return loansAllowed;
	}
	
	public boolean isUpgradeAllowed (String aTileColor) {
		boolean tUpgradeAllowed;

		tUpgradeAllowed = false;
		for (String tTileColor : tiles) {
			if (tTileColor.equals (aTileColor)) {
				tUpgradeAllowed = true;
			}
		}

		return tUpgradeAllowed;
	}

	public static Comparator<PhaseInfo> PhaseInfoComparator = new Comparator<PhaseInfo> () {

		@Override
		public int compare (PhaseInfo aPhaseInfo1, PhaseInfo aPhaseInfo2) {
			int tSortOrder;
			int tPhaseName1;
			int tPhaseName2;
			int tPhaseSubName1;
			int tPhaseSubName2;

			tSortOrder = 0;
			tPhaseName1 = aPhaseInfo1.getName ();
			tPhaseName2 = aPhaseInfo2.getName ();
			if (tPhaseName1 == tPhaseName2) {
				tPhaseSubName1 = aPhaseInfo1.getSubName ();
				tPhaseSubName2 = aPhaseInfo2.getSubName ();
				if (tPhaseSubName1 < tPhaseSubName2) {
					tSortOrder = SORT_PHASE1_BEFORE_PHASE2;
				} else {
					tSortOrder = SORT_PHASE2_BEFORE_PHASE1;
				}
			} else if (tPhaseName1 < tPhaseName2) {
				tSortOrder = SORT_PHASE1_BEFORE_PHASE2;
			} else {
				tSortOrder = SORT_PHASE2_BEFORE_PHASE1;
			}

			return tSortOrder;
		}
	};
}
