package ge18xx.game;

//
//  GameInfo.java
//  Game_18XX
//
//  Created by Mark Smith on 12/19/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.company.CorporationList;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerInfo;
import ge18xx.train.Train;
import ge18xx.train.TrainInfo;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import org.w3c.dom.NodeList;

public class GameInfo {
	public static final GameInfo NO_GAME_INFO = null;
	public static final GameInfo [] NO_GAMES = null;
	public static final AttributeName AN_GAME_ID = new AttributeName ("gameID");
	static final AttributeName AN_ID = new AttributeName ("id");
	static final AttributeName AN_NAME = new AttributeName ("name");
	public static final ElementName EN_GAME_INFO = new ElementName ("GameInfo");
	final AttributeName AN_PRIVATES = new AttributeName ("privates");
	final AttributeName AN_MINORS = new AttributeName ("minors");
	final AttributeName AN_COALS = new AttributeName ("coals");
	final AttributeName AN_SHARES = new AttributeName ("shares");
	final AttributeName AN_SUBTITLE = new AttributeName ("subtitle");
	final AttributeName AN_MIN_PLAYERS = new AttributeName ("minPlayers");
	final AttributeName AN_MAX_PLAYERS = new AttributeName ("maxPlayers");
	final AttributeName AN_BANK_TOTAL = new AttributeName ("bankTotal");
	final AttributeName AN_BANK_POOL_SHARE_LIMIT = new AttributeName ("bankPoolShareLimit");
	final AttributeName AN_PLAYER_SHARE_LIMIT = new AttributeName ("playerShareLimit");
	final AttributeName AN_CURRENCY_FORMAT = new AttributeName ("currencyFormat");
	final AttributeName AN_LOCATION = new AttributeName ("location");
	final AttributeName AN_DESIGNERS = new AttributeName ("designers");
	final AttributeName AN_PRODUCERS = new AttributeName ("producers");
	final AttributeName AN_RELEASE_DATE = new AttributeName ("releaseDate");
	final AttributeName AN_CAN_PAY_HALF = new AttributeName ("canPayHalfDividend");
	final AttributeName AN_PARITAL_CAPITAL = new AttributeName ("partialCapitalization");

	static final int NO_GAME_ID = 0;
	static final String NO_NAME = "<NONE>";
	static final int NO_MIN_PLAYERS = 0;
	static final int NO_MAX_PLAYERS = 0;
	static final int NO_BANK_TOTAL = 0;
	static final int NO_SHARE_LIMIT = 100;
	static final int NO_BANK_SHARE_LIMIT = 9;
	static final String NO_FORMAT = "<NONE>";
	static final TrainInfo [] NO_TRAINS = null;

	boolean gameTestFlag = false; // For marking this Game info as Test for JUNIT Purposes ONLY

	int id;
	String gameID;
	String name;
	int minPlayers;
	int maxPlayers;
	int bankTotal;
	String currencyFormat;
	String subTitle;
	String location;
	String designers;
	String producers;
	String releaseDate;
	boolean hasPrivates;
	boolean hasMinors;
	boolean hasCoals;
	boolean hasShares;
	boolean canPayHalfDividend;
	boolean partialCapitalization;
	int bankPoolShareLimit; // Limit on # of shares in Bank Pool
	int playerShareLimit; // Limit on # of shares a Player may Hold
	TrainInfo trains[];
	PlayerInfo players[];
	PhaseManager phaseManager;
	Option options[];
	File18XX files[];

	/* Used in Parsing Call back Functions only */
	int fileIndex;
	int optionIndex;
	int playerIndex;
	int trainIndex;

	public GameInfo () {
		trains = NO_TRAINS;
		setValues (NO_GAME_ID, NO_NAME, NO_MIN_PLAYERS, NO_MAX_PLAYERS, NO_BANK_TOTAL, NO_FORMAT);
		setOtherValues (NO_NAME, NO_NAME, NO_NAME, NO_NAME, NO_NAME);
		setBankPoolShareLimit (NO_SHARE_LIMIT);
		setPlayerShareLimit (NO_SHARE_LIMIT);
		setHasCompanies (false, false, false, false);
	}

	public GameInfo (XMLNode aCellNode) {
		XMLNodeList tXMLNodeList;
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		String tGameID;
		String tName, tCurrencyFormat, tSubTitle, tLocation, tDesigners, tProducers, tReleaseDate;
		int tID, tMinPlayers, tMaxPlayers, tBankTotal, tFileCount;
		int tChildrenCount, tIndex, tOptionCount;
		int tTrainCount, tPlayerCount;
		int tBankPoolShareLimit, tPlayerShareLimit;
		boolean tHasPrivates, tHasMinors, tHasCoals, tHasShares;

		tGameID = aCellNode.getThisAttribute (AN_GAME_ID);
		tID = aCellNode.getThisIntAttribute (AN_ID);
		tName = aCellNode.getThisAttribute (AN_NAME);
		tMinPlayers = aCellNode.getThisIntAttribute (AN_MIN_PLAYERS);
		tMaxPlayers = aCellNode.getThisIntAttribute (AN_MAX_PLAYERS);
		tBankTotal = aCellNode.getThisIntAttribute (AN_BANK_TOTAL);
		tCurrencyFormat = aCellNode.getThisAttribute (AN_CURRENCY_FORMAT);
		tSubTitle = aCellNode.getThisAttribute (AN_SUBTITLE, NO_NAME);
		tLocation = aCellNode.getThisAttribute (AN_LOCATION);
		tDesigners = aCellNode.getThisAttribute (AN_DESIGNERS);
		tProducers = aCellNode.getThisAttribute (AN_PRODUCERS);
		tReleaseDate = aCellNode.getThisAttribute (AN_RELEASE_DATE);

		tHasPrivates = aCellNode.getThisBooleanAttribute (AN_PRIVATES);
		tHasMinors = aCellNode.getThisBooleanAttribute (AN_MINORS);
		tHasCoals = aCellNode.getThisBooleanAttribute (AN_COALS);
		tHasShares = aCellNode.getThisBooleanAttribute (AN_SHARES);
		canPayHalfDividend = aCellNode.getThisBooleanAttribute (AN_CAN_PAY_HALF);
		partialCapitalization = aCellNode.getThisBooleanAttribute (AN_PARITAL_CAPITAL);

		setGameID (tGameID);
		setValues (tID, tName, tMinPlayers, tMaxPlayers, tBankTotal, tCurrencyFormat);
		setOtherValues (tSubTitle, tLocation, tDesigners, tProducers, tReleaseDate);
		setHasCompanies (tHasPrivates, tHasMinors, tHasCoals, tHasShares);

		tBankPoolShareLimit = aCellNode.getThisIntAttribute (AN_BANK_POOL_SHARE_LIMIT);
		tPlayerShareLimit = aCellNode.getThisIntAttribute (AN_PLAYER_SHARE_LIMIT);
		setBankPoolShareLimit (tBankPoolShareLimit);
		setPlayerShareLimit (tPlayerShareLimit);

		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (PlayerInfo.EN_PLAYER_INFO.equals (tChildName)) {
				tXMLNodeList = new XMLNodeList (playersParsingRoutine);
				tPlayerCount = tXMLNodeList.getChildCount (tChildNode, Player.EN_PLAYER);
				players = new PlayerInfo [tPlayerCount];
				playerIndex = 0;
				tXMLNodeList.parseXMLNodeList (tChildNode, Player.EN_PLAYER);
			} else if (PhaseInfo.EN_PHASES.equals (tChildName)) {
				tXMLNodeList = new XMLNodeList (phasesParsingRoutine);
				phaseManager = new PhaseManager ();
				tXMLNodeList.parseXMLNodeList (tChildNode, PhaseInfo.EN_PHASE);
			} else if (TrainInfo.EN_TRAINS_INFO.equals (tChildName)) {
				tXMLNodeList = new XMLNodeList (trainsParsingRoutine);
				tTrainCount = tXMLNodeList.getChildCount (tChildNode, TrainInfo.EN_TRAIN_INFO);
				trains = new TrainInfo [tTrainCount];
				trainIndex = 0;
				tXMLNodeList.parseXMLNodeList (tChildNode, TrainInfo.EN_TRAIN_INFO);
			} else if (Option.EN_OPTIONS.equals (tChildName)) {
				tXMLNodeList = new XMLNodeList (optionsParsingRoutine);
				tOptionCount = tXMLNodeList.getChildCount (tChildNode, Option.EN_OPTION);
				options = new Option [tOptionCount];
				optionIndex = 0;
				tXMLNodeList.parseXMLNodeList (tChildNode, Option.EN_OPTION);
			} else if (File18XX.EN_FILES.equals (tChildName)) {
				tXMLNodeList = new XMLNodeList (file18XXNameParsingRoutine);
				tFileCount = tXMLNodeList.getChildCount (tChildNode, File18XX.EN_FILE);
				files = new File18XX [tFileCount];
				fileIndex = 0;
				tXMLNodeList.parseXMLNodeList (tChildNode, File18XX.EN_FILE);
			}
		}
	}

	ParsingRoutineI playersParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			players [playerIndex++] = new PlayerInfo (aChildNode);
		}
	};

	ParsingRoutineI phasesParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			PhaseInfo tPhase;

			tPhase = new PhaseInfo (aChildNode);
			phaseManager.addPhase (tPhase);
		}
	};

	ParsingRoutineI trainsParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			trains [trainIndex++] = new TrainInfo (aChildNode);
		}
	};

	ParsingRoutineI optionsParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			options [optionIndex++] = new Option (aChildNode);
		}
	};

	ParsingRoutineI file18XXNameParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			files [fileIndex++] = new File18XX (aChildNode);
		}
	};

	public boolean canPayHalfDividend () {
		return canPayHalfDividend;
	}

	public boolean canPlayWithXPlayers (int aNumPlayers) {
		boolean tCanPlay;

		tCanPlay = false;
		if ((aNumPlayers >= minPlayers) && (aNumPlayers <= maxPlayers)) {
			tCanPlay = true;
		}

		return tCanPlay;
	}

	public boolean canSellPresidentShare () {
		boolean tCanSellPresidentShare;

		tCanSellPresidentShare = false;
		if (bankPoolShareLimit == NO_BANK_SHARE_LIMIT) {
			tCanSellPresidentShare = true;
		}

		return tCanSellPresidentShare;
	}

	public int getBankPoolShareLimit () {
		return bankPoolShareLimit;
	}

	public int getBankTotal () {
		return bankTotal;
	}

	public boolean fullCapitalization () {
		return !partialCapitalization;
	}

	public int getCertificateLimit (int aNumPlayers) {
		int tCertificateLimit;
		int tIndex, tPlayerInfoCount;

		tCertificateLimit = 0;
		if (canPlayWithXPlayers (aNumPlayers)) {
//		if ((aNumPlayers >= minPlayers) && (aNumPlayers <= maxPlayers)) {
			tPlayerInfoCount = players.length;
			for (tIndex = 0; (tIndex < tPlayerInfoCount) && (tCertificateLimit == 0); tIndex++) {
				if (aNumPlayers == players [tIndex].getNumPlayers ()) {
					tCertificateLimit = players [tIndex].getCertificateLimit ();
				}
			}
		}

		return tCertificateLimit;
	}

	public String getCurrencyFormat () {
		return currencyFormat;
	}

	public String getFileNameFor (String aType) {
		String tFileName;
		int tIndex, tFileCount;

		tFileName = NO_NAME;
		tFileCount = files.length;
		if (tFileCount > 0) {
			for (tIndex = 0; (tIndex < tFileCount) && (tFileName.equals (NO_NAME)); tIndex++) {
				if (aType.equals (files [tIndex].getType ())) {
					tFileName = files [tIndex].getName ();
				}
			}
		}
		return tFileName;
	}

	public String getGameID () {
		return gameID;
	}

	public XMLElement getGameInfoElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tGameOptions, tGameOption;

		tXMLElement = aXMLDocument.createElement (EN_GAME_INFO);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_GAME_ID, gameID);
		if (options != Option.NO_OPTIONS) {
			tGameOptions = aXMLDocument.createElement (Option.EN_OPTIONS);
			for (Option tOption : options) {
				if (tOption.isEnabled ()) {
					tGameOption = tOption.getOptionElement (aXMLDocument);
					tGameOptions.appendChild (tGameOption);
				}
			}
			tXMLElement.appendChild (tGameOptions);
		}

		return tXMLElement;
	}

	public String getGameName () {
		return name;
	}

	public String getHTMLDescription () {
		String tHTMLDescription;

		tHTMLDescription = "<html><body><h3>" + name;
		if (subTitle != null) {
			if (!(subTitle.equals (NO_NAME))) {
				tHTMLDescription += ": <i>" + subTitle + "</i>";
			}
		}
		tHTMLDescription += "</h3>";
		tHTMLDescription += "<p>Setting is " + location + "</p>";
		tHTMLDescription += "<p>&copy;" + releaseDate + " by " + producers + "</p>";
		tHTMLDescription += "<p>Designed by: " + designers + "</p><br/></body></html>";

		return tHTMLDescription;
	}

	public int getID () {
		return id;
	}

	public int getMaxPlayers () {
		return maxPlayers;
	}

	public int getMinPlayers () {
		return minPlayers;
	}

	public String getName () {
		return name;
	}

	public int getOptionCount () {
		if (options == Option.NO_OPTIONS) {
			return 0;
		} else {
			return options.length;
		}
	}

	public Option getOptionIndex (int aIndex) {
		if (options == Option.NO_OPTIONS) {
			return Option.NO_OPTION;
		} else if (aIndex >= getOptionCount ()) {
			return Option.NO_OPTION;
		} else {
			return options [aIndex];
		}
	}

	public PhaseManager getPhaseManager () {
		return phaseManager;
	}

	public int getPlayerShareLimit () {
		return playerShareLimit;
	}

	public int getStartingCash (int aNumPlayers) {
		int tStartingCash;
		int tIndex, tPlayerInfoCount;

		tStartingCash = 0;
		if (canPlayWithXPlayers (aNumPlayers)) {
//		if ((aNumPlayers >= minPlayers) && (aNumPlayers <= maxPlayers)) {
			tPlayerInfoCount = players.length;
			for (tIndex = 0; (tIndex < tPlayerInfoCount) && (tStartingCash == 0); tIndex++) {
				if (aNumPlayers == players [tIndex].getNumPlayers ()) {
					tStartingCash = players [tIndex].getStartingCash ();
				}
			}
		}

		return tStartingCash;
	}

	public int getTrainCount () {
		return trains.length;
	}

	public TrainInfo getTrainInfo (int aIndex) {
		return trains [aIndex];
	}

	public boolean hasCoals () {
		return hasCoals;
	}

	public boolean hasMinors () {
		return hasMinors;
	}

	public boolean hasPrivates () {
		return hasPrivates;
	}

	public boolean hasShares () {
		return hasShares;
	}

	public void printGameInfo () {
		int tIndex;

		System.out.println ("Title: " + name);
		System.out.println ("SubTitle: " + subTitle);
		System.out.println ("Location: " + location);
		System.out.println ("Min Players: " + minPlayers);
		System.out.println ("Max Players: " + maxPlayers);
		System.out.println ("Bank Total: " + bankTotal);
		System.out.println ("Currency Format: " + currencyFormat);
		for (tIndex = 0; tIndex < players.length; tIndex++) {
			System.out.println ("For " + players [tIndex].getNumPlayers () + " Players Starting Cash is "
					+ players [tIndex].getStartingCash ());
		}
	}

	public void setBankPoolShareLimit (int aBankPoolShareLimit) {
		bankPoolShareLimit = aBankPoolShareLimit;
	}

	public void setGameID (String aGameID) {
		gameID = aGameID;
	}

	public void setHasCompanies (boolean aHasPrivates, boolean aHasMinors, boolean aHasCoals, boolean aHasShares) {
		hasPrivates = aHasPrivates;
		hasMinors = aHasMinors;
		hasCoals = aHasCoals;
		hasShares = aHasShares;
	}

	public void setOtherValues (String aSubTitle, String aLocation, String aDesigners, String aProducers,
			String aReleaseDate) {
		subTitle = aSubTitle;
		location = aLocation;
		designers = aDesigners;
		producers = aProducers;
		releaseDate = aReleaseDate;
	}

	public void setPlayerShareLimit (int aPlayerShareLimit) {
		playerShareLimit = aPlayerShareLimit;
	}

	public void setupOptions (GameManager aGameManager) {
		int tEffectCount, tEffectIndex;
		OptionEffect tEffect;
		Train tTrain, tNewTrain;
		String tTrainName, tEffectName;
		int tQuantity, tFoundQuantity, tAddThisMany, tRemoveThisMany;
		int tTrainIndex;
		Bank tBank;
		CorporationList tCorporationList;

		if (options != Option.NO_OPTIONS) {
			if (options.length > 0) {
				tBank = aGameManager.getBank ();
				for (Option tOption : options) {
					if (tOption.isEnabled ()) {
						tEffectCount = tOption.getEffectCount ();
						for (tEffectIndex = 0; tEffectIndex < tEffectCount; tEffectIndex++) {
							tEffect = tOption.getEffectIndex (tEffectIndex);
							if (tEffect != OptionEffect.NO_OPTION_EFFECT) {
								tEffectName = tEffect.getName ();
								if (OptionEffect.MUST_BUY_TRAIN.equals (tEffectName)) {
									tCorporationList = aGameManager.getShareCompanies ();
									System.out.println (" Setting all Companies to MUST BUY TRAIN");
									tCorporationList.setAllMustBuyTrain ();
								} else if (OptionEffect.ADD_TO_BANK.equals (tEffectName)) {
									tBank.addCash (tEffect.getQuantity ());
								} else if (OptionEffect.SET_TRAIN_QUANTITY.equals (tEffectName)) {
									tTrainName = tEffect.getTrainName ();
									tQuantity = tEffect.getQuantity ();
									tTrain = tBank.getTrain (tTrainName);
									if (tQuantity == TrainInfo.UNLIMITED_TRAINS) {
										tTrain.setUnlimitedQuantity ();
									} else {
										tFoundQuantity = tBank.getTrainQuantity (tTrainName);
										if (tQuantity > tFoundQuantity) {
											tAddThisMany = tQuantity - tFoundQuantity;
											for (tTrainIndex = 0; tTrainIndex < tAddThisMany; tTrainIndex++) {
												System.out.println (
														"Train " + tTrain.getName () + " adding " + tAddThisMany);
												tNewTrain = new Train (tTrain);
												tBank.addTrain (tNewTrain);
											}
										} else if (tQuantity < tFoundQuantity) {
											tRemoveThisMany = tFoundQuantity - tQuantity;
											for (tTrainIndex = 0; tTrainIndex < tRemoveThisMany; tTrainIndex++) {
												tBank.removeTrain (tTrainName);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void setValues (int aID, String aName, int aMinPlayers, int aMaxPlayers, int aBankTotal, String aFormat) {
		id = aID;
		name = aName;
		minPlayers = aMinPlayers;
		maxPlayers = aMaxPlayers;
		bankTotal = aBankTotal;
		currencyFormat = aFormat;
	}

	public boolean isATestGame () {
		return gameTestFlag;
	}

	public void setTestingFlag (boolean aGameTestFlag) {
		gameTestFlag = aGameTestFlag;
	}
}
