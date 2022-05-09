package ge18xx.phase;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLNode;

//
//  PhaseInfo.java
//  Game_18XX
//
//  Created by Mark Smith on 12/25/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class PhaseInfo {
	public static final PhaseInfo NO_PHASE_INFO = null;
	public static final ElementName EN_PHASES = new ElementName ("Phases");
	public static final ElementName EN_PHASE = new ElementName ("Phase");
	public static final AttributeName AN_PHASES = new AttributeName ("phases");
	static final AttributeName AN_NAME = new AttributeName ("name");
	static final AttributeName AN_SUB_NAME = new AttributeName ("subName");
	static final AttributeName AN_ROUNDS = new AttributeName ("rounds");
	static final AttributeName AN_TILES = new AttributeName ("tiles");
	static final AttributeName AN_TRAIN_LIMIT = new AttributeName ("trainLimit");
	static final AttributeName AN_MAJOR_TRAIN_LIMIT = new AttributeName ("majorTrainLimit");
	static final AttributeName AN_MINOR_TRAIN_LIMIT = new AttributeName ("minorTrainLimit");
	static final AttributeName AN_GOVT_TRAIN_LIMIT = new AttributeName ("govtTrainLimit");
	static final AttributeName AN_OFF_BOARD = new AttributeName ("offBoard");
	static final AttributeName AN_CAN_BUY_PRIVATE = new AttributeName ("canBuyPrivate");
	static final AttributeName AN_CAN_BUY_TRAIN = new AttributeName ("canBuyTrain");
	static final AttributeName AN_CLOSE_PRIVATE = new AttributeName ("closePrivate");
	static final AttributeName AN_GOVERNMENT_CAN_FORM = new AttributeName ("governmentCanForm");
	static final AttributeName AN_GOVERNMENT_MUST_FORM = new AttributeName ("governmentMustForm");
	static final AttributeName AN_MIN_TO_FLOAT = new AttributeName ("minToFloat");
	static final AttributeName AN_MIN_TO_FLOAT_LAST = new AttributeName ("minToFloatLast");
	static final AttributeName AN_CAPITALIZATION = new AttributeName ("capitalization");

	static final int STANDARD_MIN_SHARES = 6;
	static final int NO_LIMIT = 99;
	public static final int NO_NAME = 0;
	static final int NO_ROUNDS = 0;
	static final String [] NO_TILES = null;
	static final String NO_OFF_BOARD = null;
	int name;
	int subName;
	int rounds;
	String tiles[];
	int trainLimit;
	int minorTrainLimit;
	int govtTrainLimit;
	int willFloat;
	int minToFloat; // Minimum number of Shares sold to Float the Company at time of Preparing
					// Company
	int minToFloatLast; // Minimum number of Shares sold to Float the Company when last Train of Phase
						// has been Sold (ie when next train purchase triggers Phase Change)
	String capitalization;
	boolean canBuyPrivate;
	boolean canBuyTrain;
	boolean closePrivates;
	boolean governmentCanForm;
	boolean governmentMustForm;
	String offBoard;

	public PhaseInfo () {
		setValues (NO_NAME, NO_NAME, NO_ROUNDS, NO_TILES, NO_LIMIT, NO_LIMIT, NO_LIMIT, NO_OFF_BOARD, false, false,
				false, false, false);
	}

	public PhaseInfo (XMLNode aCellNode) {
		int tName, tSubName, tRounds, tTrainLimit, tMinorTrainLimit, tGovTrainLimit;
		boolean tCanBuyPrivate, tClosePrivate, tGovernmentCanForm, tGovernmentMustForm;
		boolean tCanBuyTrain;
		String tOffBoard, tTileColors;
		String tTiles[];

		tName = aCellNode.getThisIntAttribute (AN_NAME);
		tSubName = aCellNode.getThisIntAttribute (AN_SUB_NAME);
		tRounds = aCellNode.getThisIntAttribute (AN_ROUNDS);
		tTileColors = aCellNode.getThisAttribute (AN_TILES);
		tTiles = tTileColors.split (",");
		tTrainLimit = aCellNode.getThisIntAttribute (AN_TRAIN_LIMIT, NO_LIMIT);
		if (tTrainLimit == NO_LIMIT) {
			tTrainLimit = aCellNode.getThisIntAttribute (AN_MAJOR_TRAIN_LIMIT, NO_LIMIT);
		}
		tMinorTrainLimit = aCellNode.getThisIntAttribute (AN_MINOR_TRAIN_LIMIT, NO_LIMIT);
		tGovTrainLimit = aCellNode.getThisIntAttribute (AN_GOVT_TRAIN_LIMIT, NO_LIMIT);
		tOffBoard = aCellNode.getThisAttribute (AN_OFF_BOARD);
		tCanBuyPrivate = aCellNode.getThisBooleanAttribute (AN_CAN_BUY_PRIVATE);
		tCanBuyTrain = aCellNode.getThisBooleanAttribute (AN_CAN_BUY_TRAIN);
		tClosePrivate = aCellNode.getThisBooleanAttribute (AN_CLOSE_PRIVATE);
		tGovernmentCanForm = aCellNode.getThisBooleanAttribute (AN_GOVERNMENT_CAN_FORM);
		tGovernmentMustForm = aCellNode.getThisBooleanAttribute (AN_GOVERNMENT_MUST_FORM);
		setValues (tName, tSubName, tRounds, tTiles, tTrainLimit, tMinorTrainLimit, tGovTrainLimit, tOffBoard,
				tCanBuyPrivate, tCanBuyTrain, tClosePrivate, tGovernmentCanForm, tGovernmentMustForm);
		parseFloatMinValues (aCellNode);
	}

	// minToFloat="2" minToFloatLast="3" />
	private void parseFloatMinValues (XMLNode aCellNode) {
		int tValue;
		String tCapitalization;

		tValue = aCellNode.getThisIntAttribute (AN_MIN_TO_FLOAT, STANDARD_MIN_SHARES);
		setMinToFloat (tValue);
		tValue = aCellNode.getThisIntAttribute (AN_MIN_TO_FLOAT_LAST, STANDARD_MIN_SHARES);
		setMinToFloatLast (tValue);
		tCapitalization = aCellNode.getThisAttribute (AN_CAPITALIZATION, "FULL");
		setCapitalization (tCapitalization);
	}

	private void setCapitalization (String aCapitalization) {
		capitalization = aCapitalization;
	}

	private void setMinToFloat (int aValue) {
		minToFloat = aValue;
	}

	private void setMinToFloatLast (int aValue) {
		minToFloatLast = aValue;
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
		return willFloat * 10;
	}

	// TODO: 1856 - Capitalization level changes based upon Phase -- NEED to Expand
	public int getCapitalizationLevel (int aSharesSold) {
		int tCapitalizationLevel;

		if (capitalization.equals ("FULL")) {
			tCapitalizationLevel = 10;
		} else if (capitalization.equals ("min_Shares_Sold_5")) {
			tCapitalizationLevel = Math.min (5, aSharesSold);
		} else {
			tCapitalizationLevel = 1;
		}

		return tCapitalizationLevel;
	}

	public boolean doPartialCapitalization () {
		return false;
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
		return String.join (", ", tiles);
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
		System.out.println ("Phase Name " + name);
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
			boolean aClosePrivates, boolean aGovernmentCanForm, boolean aGovernmentMustForm) {
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
		governmentCanForm = aGovernmentCanForm;
		governmentMustForm = aGovernmentMustForm;
		willFloat = 6;
	}

	public boolean canBuyTrainInPhase () {
		return canBuyTrain;
	}

	public boolean isUpgradeAllowed (String aTileColor) {
		boolean tUpgradeAllowed = false;

		for (String tTileColor : tiles) {
			if (tTileColor.equals (aTileColor)) {
				tUpgradeAllowed = true;
			}
		}

		return tUpgradeAllowed;
	}
}
