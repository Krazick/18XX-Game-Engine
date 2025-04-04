package ge18xx.game;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.NodeList;

import ge18xx.company.CorporationList;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.game.variants.Variant;
import ge18xx.game.variants.VariantEffect;
import ge18xx.game.variants.VariantToggle;

//
//  GameInfo.java
//  Game_18XX
//
//  Created by Mark Smith on 12/19/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerInfo;
import ge18xx.round.AuctionRound;
import ge18xx.round.RoundType;
import ge18xx.train.TrainInfo;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.xml.XMLSaveGameI;

public class GameInfo implements XMLSaveGameI {
	public static final ElementName EN_GAME_INFO = new ElementName ("GameInfo");
	public static final AttributeName AN_GAME_ID = new AttributeName ("gameID");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_IS_A_TEST_GAME = new AttributeName ("isATestGame");
	final AttributeName AN_TEST_GRAPHS = new AttributeName ("testGraphs");
	final AttributeName AN_STATUS = new AttributeName ("status");
	final AttributeName AN_LICENSES = new AttributeName ("licenses");
	final AttributeName AN_LOANS = new AttributeName ("loans");
	final AttributeName AN_LOAN_AMOUNT = new AttributeName ("loanAmount");
	final AttributeName AN_LOAN_INTEREST = new AttributeName ("loanInterest");
	final AttributeName AN_PRIVATES = new AttributeName ("privates");
	final AttributeName AN_MINORS = new AttributeName ("minors");
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
	final AttributeName AN_OPERATE_BEFORE_SALE = new AttributeName ("operateBeforeSale");
	final AttributeName AN_RANDOMIZE_START_ORDER = new AttributeName ("randomizeStartOrder");
	final AttributeName AN_FIRST_TOKEN_COST = new AttributeName ("firstTokenCost");
	final AttributeName AN_LATER_TOKEN_COST = new AttributeName ("laterTokenCost");
	final AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
	final AttributeName AN_MAX_ROUNDS = new AttributeName ("maxRounds");
	final AttributeName AN_NO_TOUCH_PASS = new AttributeName ("noTouchPass");
	final AttributeName AN_OPTIONAL_OR = new AttributeName ("optionalOR");
	final AttributeName AN_BANK_POOL_DIVIDENDS = new AttributeName ("bankPoolDividends");
	final AttributeName AN_IPO_DIVIDENDS = new AttributeName ("ipoDividends");
	final AttributeName AN_BANK_POOL_NAME = new AttributeName ("bankPoolName");
	final AttributeName AN_INITIAL_ROUND_NAME = new AttributeName ("initialRoundName");

	public static final GameInfo NO_GAME_INFO = null;
	public static final GameInfo [] NO_GAMES = null;
	public static final String CORPORATION = "corporation";
	public static final String BANK = "bank";
	public static final String BANK_POOL_NAME = "Bank Pool";

	static final int NO_GAME_ID = 0;
	static final int NO_MIN_PLAYERS = 0;
	static final int NO_MAX_PLAYERS = 0;
	static final int NO_BANK_TOTAL = 0;
	static final int NO_SHARE_LIMIT = 100;
	static final int NO_BANK_SHARE_LIMIT = 9;
	static final String NO_NAME = "<NONE>";
	static final String NO_FORMAT = "<NONE>";
	static final TrainInfo [] NO_TRAINS = null;

	boolean gameTestFlag = false; // For marking this Game info as Test for JUNIT Purposes ONLY

	String gameID;
	String name;
	int id;
	int minPlayers;
	int maxPlayers;
	int bankTotal;
	int loanAmount;
	int loanInterest;
	int firstTokenCost;
	int laterTokenCost;
	int maxRounds;
	int roundIndex;
	String currencyFormat;
	String subTitle;
	String location;
	String designers;
	String producers;
	String releaseDate;
	String status;
	String bankPoolDividends;
	String ipoDividends;
	String bankPoolName;
	String initialRoundType;
	TokenType tokenType;
	RoundType [] roundTypes;
	boolean noTouchPass;
	boolean operateBeforeSale;
	boolean hasPrivates;
	boolean hasMinors;
	boolean hasShares;
	boolean hasLicenses;
	boolean canPayHalfDividend;
	boolean loans;
	boolean randomizeStartOrder;
	boolean optionalOR;
	boolean testGraphs;		// For DEBUGing Development testing of new Graphs
	int bankPoolShareLimit; // Limit on # of shares in Bank Pool
	int playerShareLimit; // Limit on # of shares a Player may Hold
	TrainInfo [] trains;
	PlayerInfo [] players;
	PhaseManager phaseManager;
	Variant [] variants;
	File18XX [] files;
	Capitalization capitalizations;
	private List<VariantEffect> activeVariantEffects;

	/* Used in Parsing Call back Functions only */
	int fileIndex;
	int variantIndex;
	int playerIndex;
	int trainIndex;

	public GameInfo () {
		trains = NO_TRAINS;
		setValues (NO_GAME_ID, NO_NAME, NO_MIN_PLAYERS, NO_MAX_PLAYERS, NO_BANK_TOTAL, NO_FORMAT);
		setOtherValues (NO_NAME, NO_NAME, NO_NAME, NO_NAME, NO_NAME);
		setBankPoolShareLimit (NO_SHARE_LIMIT);
		setPlayerShareLimit (NO_SHARE_LIMIT);
		setDividendPayments (CORPORATION, BANK, BANK_POOL_NAME);
		setHasCompanies (false, false, false);
	}

	public GameInfo (XMLNode aCellNode) {
		String tGameID;
		String tStatus;
		String tName;
		String tCurrencyFormat;
		String tSubTitle;
		String tLocation;
		String tDesigners;
		String tProducers;
		String tReleaseDate;
		String tBankPoolDividends;
		String tIpoDividends;
		String tBankPoolName;
		String tInitialRoundType;
		String tTokenType;
		int tID;
		int tMinPlayers;
		int tMaxPlayers;
		int tBankTotal;
		int tBankPoolShareLimit;
		int tPlayerShareLimit;
		int tLoanAmount;
		int tLoanInterest;
		int tFirstTokenCost;
		int tLaterTokenCost;
		int tMaxRounds;
		boolean tHasPrivates;
		boolean tHasMinors;
		boolean tHasShares;
		boolean tHasLicenses;
		boolean tLoans;
		boolean tTestGraphs;
		boolean tOperateBeforeSale;
		boolean tRandomizeStartOrder;
		boolean tCanPayHalfDividend;
		boolean tNoTouchPass;
		boolean tOptionalOR;
		boolean tIsATestGame;
		
		tGameID = aCellNode.getThisAttribute (AN_GAME_ID);
		tID = aCellNode.getThisIntAttribute (AN_ID);
		tName = aCellNode.getThisAttribute (AN_NAME);
		tStatus = aCellNode.getThisAttribute (AN_STATUS);
		tMinPlayers = aCellNode.getThisIntAttribute (AN_MIN_PLAYERS);
		tMaxPlayers = aCellNode.getThisIntAttribute (AN_MAX_PLAYERS);
		tBankTotal = aCellNode.getThisIntAttribute (AN_BANK_TOTAL);
		tCurrencyFormat = aCellNode.getThisAttribute (AN_CURRENCY_FORMAT);
		tSubTitle = aCellNode.getThisAttribute (AN_SUBTITLE, NO_NAME);
		tLocation = aCellNode.getThisAttribute (AN_LOCATION);
		tDesigners = aCellNode.getThisAttribute (AN_DESIGNERS);
		tProducers = aCellNode.getThisAttribute (AN_PRODUCERS);
		tReleaseDate = aCellNode.getThisAttribute (AN_RELEASE_DATE);
		tBankPoolDividends = aCellNode.getThisAttribute (AN_BANK_POOL_DIVIDENDS);
		tIpoDividends = aCellNode.getThisAttribute (AN_IPO_DIVIDENDS);
		tBankPoolName = aCellNode.getThisAttribute (AN_BANK_POOL_NAME, BANK_POOL_NAME);
		tInitialRoundType = aCellNode.getThisAttribute (AN_INITIAL_ROUND_NAME);
		tIsATestGame = aCellNode.getThisBooleanAttribute (AN_IS_A_TEST_GAME);

		tOptionalOR = aCellNode.getThisBooleanAttribute (AN_OPTIONAL_OR);
		tNoTouchPass = aCellNode.getThisBooleanAttribute (AN_NO_TOUCH_PASS);
		tTestGraphs = aCellNode.getThisBooleanAttribute (AN_TEST_GRAPHS);
		tHasPrivates = aCellNode.getThisBooleanAttribute (AN_PRIVATES);
		tHasLicenses = aCellNode.getThisBooleanAttribute (AN_LICENSES);
		tLoans = aCellNode.getThisBooleanAttribute (AN_LOANS);
		tLoanAmount = aCellNode.getThisIntAttribute (AN_LOAN_AMOUNT);
		tLoanInterest = aCellNode.getThisIntAttribute (AN_LOAN_INTEREST);
		tFirstTokenCost = aCellNode.getThisIntAttribute (AN_FIRST_TOKEN_COST);
		tLaterTokenCost = aCellNode.getThisIntAttribute (AN_LATER_TOKEN_COST);
		tTokenType = aCellNode.getThisAttribute (AN_TOKEN_TYPE, TokenType.FIXED_COST.toString ());
		tHasMinors = aCellNode.getThisBooleanAttribute (AN_MINORS);
		tHasShares = aCellNode.getThisBooleanAttribute (AN_SHARES);
		tOperateBeforeSale = aCellNode.getThisBooleanAttribute (AN_OPERATE_BEFORE_SALE);
		tRandomizeStartOrder = aCellNode.getThisBooleanAttribute (AN_RANDOMIZE_START_ORDER);
		tCanPayHalfDividend = aCellNode.getThisBooleanAttribute (AN_CAN_PAY_HALF);
		tMaxRounds = aCellNode.getThisIntAttribute (AN_MAX_ROUNDS);

		setGameID (tGameID);
		setValues (tID, tName, tMinPlayers, tMaxPlayers, tBankTotal, tCurrencyFormat);
		setOtherValues (tSubTitle, tLocation, tDesigners, tProducers, tReleaseDate);
		setHasCompanies (tHasPrivates, tHasMinors, tHasShares);
		setHasLicenses (tHasLicenses);
		setLoans (tLoans);
		setLoanAmount (tLoanAmount);
		setLoanInterest (tLoanInterest);
		setFirstTokenCost (tFirstTokenCost);
		setLaterTokenCost (tLaterTokenCost);
		setTokenType (tTokenType);
		setMaxRounds (tMaxRounds);
		setInitialRoundType (tInitialRoundType);
		setStatus (tStatus);
		setDividendPayments (tBankPoolDividends, tIpoDividends, tBankPoolName);
		setOperateBeforeSale (tOperateBeforeSale);
		setRandomizeStartOrder (tRandomizeStartOrder);
		setCanPayHalfDividend (tCanPayHalfDividend);
		setTestGraphs (tTestGraphs);
		setNoTouchPass (tNoTouchPass);
		setOptionalOR (tOptionalOR);

		tBankPoolShareLimit = aCellNode.getThisIntAttribute (AN_BANK_POOL_SHARE_LIMIT);
		tPlayerShareLimit = aCellNode.getThisIntAttribute (AN_PLAYER_SHARE_LIMIT);
		setBankPoolShareLimit (tBankPoolShareLimit);
		setPlayerShareLimit (tPlayerShareLimit);
		setTestingFlag (tIsATestGame);
		parseChildNodes (aCellNode);
	}

	private void parseChildNodes (XMLNode aCellNode) {
		NodeList tChildren;
		int tChildrenCount;
		XMLNodeList tXMLNodeList;
		XMLNode tChildNode;
		String tChildName;
		int tFileCount;
		int tIndex;
		int tVariantCount;
		int tTrainCount;
		int tPlayerCount;
		int tRoundCount;
		
		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();

		setVariants (new Variant [0]);
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
			} else if (Variant.EN_VARIANTS.equals (tChildName)) {
				tXMLNodeList = new XMLNodeList (variantsParsingRoutine);
				tVariantCount = tXMLNodeList.getChildCount (tChildNode, Variant.EN_VARIANT);
				setVariants (new Variant [tVariantCount]);
				variantIndex = 0;
				tXMLNodeList.parseXMLNodeList (tChildNode, Variant.EN_VARIANT);
			} else if (File18XX.EN_FILES.equals (tChildName)) {
				tXMLNodeList = new XMLNodeList (file18XXNameParsingRoutine);
				tFileCount = tXMLNodeList.getChildCount (tChildNode, File18XX.EN_FILE);
				files = new File18XX [tFileCount];
				fileIndex = 0;
				tXMLNodeList.parseXMLNodeList (tChildNode, File18XX.EN_FILE);
			} else if (Capitalization.EN_CAPITALIZATIONS.equals (tChildName)) {
				capitalizations = new Capitalization (tChildNode);
			} else if (RoundType.EN_ROUND_TYPES.equals (tChildName)) {
				tXMLNodeList = new XMLNodeList (roundTypesParsingRoutine);
				tRoundCount = tXMLNodeList.getChildCount (tChildNode, RoundType.EN_ROUND_TYPE);
				roundTypes = new RoundType [tRoundCount];
				roundIndex = 0;
				tXMLNodeList.parseXMLNodeList (tChildNode, RoundType.EN_ROUND_TYPE);
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

	ParsingRoutineI variantsParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode tVariantNode) {
			String tClassName;
			Variant tVariant;
			Class<?> tVariantToLoad;
			Constructor<?> tVariantConstructor;

			try {
				tClassName = tVariantNode.getThisAttribute (Variant.AN_VARIANT_CLASS);
				tVariantToLoad = Class.forName (tClassName);
				tVariantConstructor = tVariantToLoad.getConstructor (tVariantNode.getClass ());
				tVariant = (Variant) tVariantConstructor.newInstance (tVariantNode);
				variants [variantIndex++] = tVariant;
			} catch (Exception tException) {
				System.err.println ("Caught Exception with message ");
				tException.printStackTrace ();
			}
		}
	};

	ParsingRoutineI file18XXNameParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			files [fileIndex++] = new File18XX (aChildNode);
		}
	};

	ParsingRoutineI roundTypesParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			RoundType tRoundType;
			
			tRoundType = new RoundType (aChildNode);
			roundTypes [roundIndex++] = tRoundType;
		}
	};

	public boolean hasAuctionRound () {
		boolean tHasAuctionRound;
		
		tHasAuctionRound = false;
		for (RoundType tRoundType: roundTypes) {
			if (AuctionRound.NAME.equals (tRoundType.getName ())) {
				tHasAuctionRound = true;
			}
		}
		
		return tHasAuctionRound;
	}
	
	public void setGameManager (GameManager aGameManager) {
		phaseManager.setGameManager (aGameManager);
	}
	
	public void setVariants (Variant [] aVariants) {
		variants = aVariants;
	}

	public String generateNewGameID () {
		String tNewGameID;
		
		LocalDateTime datetime = LocalDateTime.now ();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("yyyy-MM-dd-HHmm");
		tNewGameID = datetime.format (formatter);

		return tNewGameID;
	}

	public void loadVariantEffects (XMLNode aGameInfoNode) {
		NodeList tChildren;
		XMLNode tChildNode;
		int tChildrenCount;
		int tIndex;
		String tChildName;

		tChildren = aGameInfoNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (VariantEffect.EN_VARIANT_EFFECTS.equals (tChildName)) {
				loadAllVariantEffects (tChildNode);
			}
		}
	}

	public void loadAllVariantEffects (XMLNode aVariantEffectsNode) {
		NodeList tChildren;
		XMLNode tChildNode;
		int tChildrenCount;
		int tIndex;
		String tChildName;
		VariantEffect tVariantEffect;
		Variant tDummyVariant;
		List<VariantEffect> tActiveVariantEffects;

		tChildren = aVariantEffectsNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tDummyVariant = new Variant ();
		tActiveVariantEffects = new LinkedList<> ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (VariantEffect.EN_VARIANT_EFFECT.equals (tChildName)) {
				tVariantEffect = tDummyVariant.loadVariantEffect (tChildNode);
				if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
					tActiveVariantEffects.add (tVariantEffect);
				}
			}
		}
		configureToggleVariants (tActiveVariantEffects);
		setActiveVariantEffects (tActiveVariantEffects);
	}

	private void configureToggleVariants (List<VariantEffect> aActiveVariantEffects) {
		int tVariantEffectCount;
		int tVariantEffectIndex;
		VariantEffect tVariantEffect;
		String tVariantEffectName;
		String tActiveVariantEffectName;
		boolean tActiveState;

		for (Variant tVariant: variants) {
			if (tVariant instanceof VariantToggle) {
				tVariantEffectCount = tVariant.getVariantEffectCount ();
				if (tVariantEffectCount > 0) {
					for (tVariantEffectIndex = 0; tVariantEffectIndex < tVariantEffectCount;
							tVariantEffectIndex++) {
						tVariantEffect = tVariant.getVariantEffectAt (tVariantEffectIndex);
						tVariantEffectName = tVariantEffect.getName ();
						for (VariantEffect tActiveVariantEffect : aActiveVariantEffects) {
							tActiveVariantEffectName = tActiveVariantEffect.getName ();
							if (tVariantEffectName.equals (tActiveVariantEffectName)) {
								tActiveState = tActiveVariantEffect.getState ();
								tVariant.setSelected (tActiveState);
							}
						}
					}
				}
			}
		}
	}

	public boolean selectActiveVariantEffects () {
		boolean tSuccess;
		boolean tEffectSelected;
		boolean tVariantHasEffect;
		int tVariantEffectID;

		if (hasActiveVariants ()) {
			tSuccess = true;
			for (VariantEffect tVariantEffect: activeVariantEffects) {
				tVariantEffectID = tVariantEffect.getID ();
				for (Variant tVariant: variants) {
					tVariantHasEffect = tVariant.hasVariantEffect (tVariantEffectID);
					if (tVariantHasEffect) {
						if (tVariantEffect.getState ()) {
							tEffectSelected = tVariant.selectActiveVariantEffects (tVariantEffect);
							tSuccess &= tEffectSelected;
							if (! tEffectSelected) {
								System.err.println ("Effect id " + tVariantEffectID +
										" action [" + tVariantEffect.getAction () + "] FAILED to be Selected.");
							}
						}
					}
				}
			}
		} else {
			System.err.println ("No Active VariantEffects to select");
			tSuccess = false;
		}

		return tSuccess;
	}

	public List<VariantEffect> getActiveVariantEffects () {
		List<VariantEffect> tActiveVariantEffects;

		tActiveVariantEffects = new LinkedList<> ();
		for (Variant tVariant: variants) {
			tVariant.addActiveVariantEffects (tActiveVariantEffects);
		}

		return tActiveVariantEffects;
	}

	public void setupVariants () {
		List<VariantEffect> tActiveVariantEffects;

		if (! hasActiveVariants ()) {
			tActiveVariantEffects = getActiveVariantEffects ();
			setActiveVariantEffects (tActiveVariantEffects);
		}
	}

	public void applyActiveVariantEffects (GameManager aGameManager) {
		if (hasActiveVariants ()) {
			for (VariantEffect tVariantEffect : activeVariantEffects) {
				tVariantEffect.applyVariantEffect (aGameManager);
			}
		}
	}

	public boolean hasActiveVariants () {
		boolean tHasActiveVariants;

		tHasActiveVariants = false;
		if (activeVariantEffects != VariantEffect.NO_VARIANT_EFFECTS) {
			if (activeVariantEffects.size () > 0) {
				tHasActiveVariants = true;
			}
		}

		return tHasActiveVariants;
	}

	public void setActiveVariantEffects (List<VariantEffect> aActiveVariantEffects) {
		activeVariantEffects = aActiveVariantEffects;
	}
	
	public XMLElement getGameVariantsXMLElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tVariantEffectXMLElement;

		tXMLElement = aXMLDocument.createElement (VariantEffect.EN_VARIANT_EFFECTS);
		for (VariantEffect tVariantEffect : activeVariantEffects) {
			tVariantEffectXMLElement = tVariantEffect.getEffectElement (aXMLDocument);
			tXMLElement.appendChild (tVariantEffectXMLElement);
		}
		
		return tXMLElement;
	}

	public void printActiveVariants () {
		if (activeVariantEffects != VariantEffect.NO_VARIANT_EFFECTS) {
			if (activeVariantEffects.isEmpty ()) {
				System.err.println ("No Variant Effects Found in List");
			} else {
				System.out.println ("Variant Effects to be Active:");		// PRINTLOG
				for (VariantEffect tVariantEffect : activeVariantEffects) {
					System.out.println ("Variant Effect " + tVariantEffect.getAction ());
				}
			}
		} else {
			System.err.println ("No Variant Effects Active");
		}
	}
	
	public int getCapitalizationLevel (int aSharesSold, String aNextTrainName) {
		int tCapitalizationLevel;
		
		tCapitalizationLevel = capitalizations.getCapitalizationLevel (aSharesSold, aNextTrainName);
		
		return tCapitalizationLevel;
	}
	
	public boolean gameHasRoundType (String aName) {
		boolean tGameHasRoundType;
		
		tGameHasRoundType = false;
		for (RoundType tRoundType : roundTypes) {
			if (tRoundType.matches (aName)) {
				tGameHasRoundType = true;
			}
		}
		
		return tGameHasRoundType;
	}
	
	public RoundType getRoundType (String aName) {
		RoundType tFoundRoundType;
		
		tFoundRoundType = RoundType.NO_ROUND_TYPE;
		if (aName != GUI.NULL_STRING) {
			for (RoundType tRoundType : roundTypes) {
				if (aName.equals (tRoundType.getName ())) {
					tFoundRoundType = tRoundType;
				}
			}
		}
		
		return tFoundRoundType;
	}

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

	public int getCertificateLimit (int aNumPlayers) {
		int tCertificateLimit;
		int tIndex;
		int tPlayerInfoCount;
		int tPhaseCount;
		PlayerInfo tPlayerInfo;

		tCertificateLimit = 0;
		if (canPlayWithXPlayers (aNumPlayers)) {
			tPlayerInfoCount = players.length;
			for (tIndex = 0; tIndex < tPlayerInfoCount; tIndex++) {
				tPlayerInfo = players [tIndex];
				if (aNumPlayers == tPlayerInfo.getNumPlayers ()) {
					tPhaseCount = tPlayerInfo.getPhaseCount ();
					if (tPhaseCount == 1) {
						tCertificateLimit = tPlayerInfo.getCertificateLimit ();
					} else {
						tCertificateLimit = getCertificateLimitCompanies (tPlayerInfo, tCertificateLimit);
					}
				}
			}
		}

		return tCertificateLimit;
	}

	public int getCertificateLimitCompanies (PlayerInfo aPlayerInfo, int aCurrentCertificateLimit) {
		PhaseInfo tCurrentPhaseInfo;
		String tCurrentPhaseName;
		String tPhaseName;
		int tCertificateLimit;
		int tCompanyCount;
		GameManager tGameManager;
		CorporationList tShareCompanies;
		
		tCertificateLimit = aCurrentCertificateLimit;
		tPhaseName = aPlayerInfo.getPhase (0);
		tCurrentPhaseInfo = phaseManager.getCurrentPhaseInfo ();
		tCurrentPhaseName = tCurrentPhaseInfo.getFullName ();
		if (tPhaseName.equals (tCurrentPhaseName)) {
			tGameManager = phaseManager.getGameManager ();
			tShareCompanies = tGameManager.getShareCompanies ();
			
			tCompanyCount = tShareCompanies.getCountOfOpen ();
			if (aPlayerInfo.getCompanyCount () == tCompanyCount) {
				tCertificateLimit = aPlayerInfo.getCertificateLimit ();
			}
		}
//		System.out.println ("Current Phase Name " + tCurrentPhaseName + " looking at " + tPhaseName + ", Limit " + tCertificateLimit);
		
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

	public String getGameName () {
		return name;
	}

	public String getStatus () {
		return status;
	}
	
	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		XMLElement tXMLElement;
		XMLElement tGameVariantEffects;

		tXMLElement = aXMLDocument.createElement (EN_GAME_INFO);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_GAME_ID, gameID);
		tGameVariantEffects = getGameVariantsXMLElement (aXMLDocument);
		if (tXMLElement != XMLElement.NO_XML_ELEMENT) {
			tXMLElement.appendChild (tGameVariantEffects);
		}
		
		return tXMLElement;
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
		tHTMLDescription += "<p>Status: " + status + "</p>";
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

	public int getVariantCount () {
		if (variants == Variant.NO_VARIANTS) {
			return 0;
		} else {
			return variants.length;
		}
	}

	public Variant getVariantIndex (int aIndex) {
		if (variants == Variant.NO_VARIANTS) {
			return Variant.NO_VARIANT;
		} else if (aIndex >= getVariantCount ()) {
			return Variant.NO_VARIANT;
		} else {
			return variants [aIndex];
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
			tPlayerInfoCount = players.length;
			for (tIndex = 0; (tIndex < tPlayerInfoCount) && (tStartingCash == 0); tIndex++) {
				if (aNumPlayers == players [tIndex].getNumPlayers ()) {
					tStartingCash = players [tIndex].getStartingCash ();
				}
			}
		}

		return tStartingCash;
	}

	public boolean hasTestGraphs () {
		return testGraphs;
	}

	public int getTrainCount () {
		return trains.length;
	}

	public int getLoanAmount () {
		return loanAmount;
	}
	
	public int getLoanInterest () {
		return loanInterest;
	}

	public int getFirstTokenCost () {
		return firstTokenCost;
	}
	
	public int getLaterTokenCost () {
		return laterTokenCost;
	}
	
	public TokenType getTokenType () {
		return tokenType;
	}
	
	public TrainInfo getTrainInfo (int aIndex) {
		return trains [aIndex];
	}

	public int getMaxRounds () {
		return maxRounds;
	}
	
	public String getInitialRoundType () {
		return initialRoundType;
	}
	
	public boolean hasMinors () {
		return hasMinors;
	}

	public boolean hasPrivates () {
		return hasPrivates;
	}

	public boolean gameHasLicenses () {
		return hasLicenses;
	}
	
	public boolean gameHasLoans () {
		return loans;
	}

	public boolean hasShares () {
		return hasShares;
	}

	public boolean operateBeforeSale () {
		return operateBeforeSale;
	}

	public boolean randomizeStartOrder () {
		return randomizeStartOrder;
	}

	public void printGameInfo () {
		int tIndex;

		System.out.println ("Title: " + name);		// PRINTLOG
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

	public void setLoans (boolean aLoans) {
		loans = aLoans;
	}

	public void setLoanAmount (int aLoanAmount) {
		loanAmount = aLoanAmount;
	}
	
	public void setLoanInterest (int aLoanInterest) {
		loanInterest = aLoanInterest;
	}
	
	public void setFirstTokenCost (int aFirstTokenCost) {
		firstTokenCost = aFirstTokenCost;
	}
	
	public void setLaterTokenCost (int aLaterTokenCost) {
		laterTokenCost = aLaterTokenCost;
	}

	public void setTokenType (String aTokenType) {
		if (aTokenType.equals (TokenType.FIXED_COST.toString ())) {
			tokenType = TokenType.FIXED_COST;
		} else 	if (aTokenType.equals (TokenType.RANGE_COST.toString ())) {
			tokenType = TokenType.RANGE_COST;
		} else {
			tokenType = TokenType.NO_TYPE;
		}
//		System.out.println ("Game Info Loaded Token Type as " + tokenType.toString ());
	}
	
	public void setMaxRounds (int aMaxRounds) {
		maxRounds = aMaxRounds;
	}
	
	public void setInitialRoundType (String aInitialRoundType) {
		initialRoundType = aInitialRoundType;
	}
	
	public void setOperateBeforeSale (boolean aOperateBeforeSale) {
		operateBeforeSale = aOperateBeforeSale;
	}

	public void setRandomizeStartOrder (boolean aRandomizeStartOrder) {
		randomizeStartOrder = aRandomizeStartOrder;
	}

	public void setCanPayHalfDividend (boolean aCanPayHalfDividend) {
		canPayHalfDividend = aCanPayHalfDividend;
	}

	public void setTestGraphs (boolean aTestGraphs) {
		testGraphs = aTestGraphs;
	}

	public void setNoTouchPass (boolean aNoTouchPass) {
		noTouchPass = aNoTouchPass;
	}

	public void setOptionalOR (boolean aOptionalOR) {
		optionalOR = aOptionalOR;
	}
	
	public void setStatus (String aStatus) {
		status = aStatus;
	}

	public void setDividendPayments (String aBankPoolDividends, String aIpoDividends, String aBankPoolName) {
		bankPoolDividends = aBankPoolDividends;
		ipoDividends = aIpoDividends;
		bankPoolName = aBankPoolName;
	}
	
	public void setHasCompanies (boolean aHasPrivates, boolean aHasMinors, boolean aHasShares) {
		hasPrivates = aHasPrivates;
		hasMinors = aHasMinors;
		hasShares = aHasShares;
	}

	public void setHasLicenses (boolean aHasLicenses) {
		hasLicenses = aHasLicenses;
	}
	
	public void setOtherValues (String aSubTitle, String aLocation, String aDesigners, String aProducers, String aReleaseDate) {
		subTitle = aSubTitle;
		location = aLocation;
		designers = aDesigners;
		producers = aProducers;
		releaseDate = aReleaseDate;
	}

	public void setPlayerShareLimit (int aPlayerShareLimit) {
		playerShareLimit = aPlayerShareLimit;
	}

	public void setValues (int aID, String aName, int aMinPlayers, int aMaxPlayers, int aBankTotal, String aFormat) {
		id = aID;
		name = aName;
		minPlayers = aMinPlayers;
		maxPlayers = aMaxPlayers;
		bankTotal = aBankTotal;
		currencyFormat = aFormat;
	}

	/**
	 * Returns the state of the NoTouchPass Game Attribute 
	 * 
	 * @return the NoTouchPass Attribute
	 * 
	 */
	public boolean noTouchPass () {
		return noTouchPass;
	}
	
	public String getBankPoolDividends () {
		return bankPoolDividends;
	}

	public String getIpoPoolDividends () {
		return ipoDividends;
	}

	public String getBankPoolName () {
		return bankPoolName;
	}

	public boolean optionalOR () {
		return optionalOR;
	}
	
	public boolean isATestGame () {
		return gameTestFlag;
	}

	public void setTestingFlag (boolean aGameTestFlag) {
		gameTestFlag = aGameTestFlag;
	}
}
