package ge18xx.phase;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.train.Train;
import ge18xx.train.TrainInfo;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLSaveGameI;

public class PhaseManager implements XMLSaveGameI {
	public static final PhaseManager NO_PHASE_MANAGER = null;
	public static final List<PhaseInfo> NO_PHASES = null;
	public static final int NO_PHASE = -1;
	public static final int FIRST_PHASE = 0;
	public static final int MINIMUM_TRAIN_LIMIT = 1;
	public static final ElementName EN_PHASE = new ElementName ("Phase");
	public final static AttributeName AN_CURRENT_PHASE = new AttributeName ("currentPhase");
	List<PhaseInfo> phases;
	int currentPhase;
	GameManager gameManager;
	TriggerClass [] triggerClasses;

	public PhaseManager () {
		phases = new LinkedList<> ();
		setCurrentPhase (NO_PHASE);
	}

	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}
	
	public GameManager getGameManager () {
		return gameManager;
	}
	
	public void addPhase (PhaseInfo aPhase) {
		if (phases == NO_PHASES) {
			System.err.println ("Phases Linked List not Initialized");
		} else {
			phases.add (aPhase);
			phases.sort (PhaseInfo.PhaseInfoComparator);
		}
	}

	public void printAllPhaseNames () {
		System.out.println ("----");		// PRINTLOG
		for (PhaseInfo tPhaseInfo : phases) {
			System.out.println ("Phase Name " + tPhaseInfo.getFullName ());
		}
	}

	public boolean canBuyPrivate () {
		PhaseInfo tPhaseInfo;
		boolean tCanBuyPrivate;

		tCanBuyPrivate = false;
		tPhaseInfo = getCurrentPhaseInfo ();
		if (tPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			tCanBuyPrivate = tPhaseInfo.getCanBuyPrivate ();
		}

		return tCanBuyPrivate;
	}

	public boolean loansAllowed () {
		PhaseInfo tPhaseInfo;
		boolean tLoansAllowed;

		tLoansAllowed = false;
		tPhaseInfo = getCurrentPhaseInfo ();
		if (tPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			tLoansAllowed = tPhaseInfo.loansAllowed ();
		}

		return tLoansAllowed;
	}

	public int getTileLaysAllowed () {
		PhaseInfo tPhaseInfo;
		int tTileLaysAllowed;
		
		tTileLaysAllowed = PhaseInfo.DEFAULT_TILE_LAYS;
		tPhaseInfo = getCurrentPhaseInfo ();
		if (tPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			tTileLaysAllowed = tPhaseInfo.getTileLaysAllowed ();
		}
		
		return tTileLaysAllowed;
	}
	
	public PhaseInfo getCurrentPhaseInfo () {
		PhaseInfo tPhaseInfo;

		if (currentPhase == NO_PHASE) {
			tPhaseInfo = PhaseInfo.NO_PHASE_INFO;
		} else {
			tPhaseInfo = phases.get (currentPhase);
		}

		return tPhaseInfo;
	}

	public PhaseInfo getPhaseInfo (int aIndex) {
		PhaseInfo tPhaseInfo;

		tPhaseInfo = PhaseInfo.NO_PHASE_INFO;
		if (phases != NO_PHASES) {
			if ((aIndex >= 0) && (aIndex <= phases.size ())) {
				tPhaseInfo = phases.get (aIndex);
			}
		}

		return tPhaseInfo;
	}

	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_PHASE);
		tXMLElement.setAttribute (AN_CURRENT_PHASE, currentPhase);

		return tXMLElement;
	}

	public void loadPhase (XMLNode aXMLNode) {
		currentPhase = aXMLNode.getThisIntAttribute (AN_CURRENT_PHASE);
	}

	public int getFormingCompanyId () {
		PhaseInfo tCurrentPhaseInfo;
		int tFormingCompanyId;
		
		tCurrentPhaseInfo = getCurrentPhaseInfo ();
		tFormingCompanyId = tCurrentPhaseInfo.getFormingCompanyId ();
		
		return tFormingCompanyId;
	}
	
	public String getCurrentOffBoard () {
		PhaseInfo tCurrentPhaseInfo;
		String tCurrentOffBoard;

		tCurrentPhaseInfo = getCurrentPhaseInfo ();
		tCurrentOffBoard = GUI.EMPTY_STRING;
		if (tCurrentPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			tCurrentOffBoard = tCurrentPhaseInfo.getOffBoard ();
		}

		return tCurrentOffBoard;
	}

	public int getPhaseIndex (String aFullPhaseName) {
		PhaseInfo tPhaseInfo;
		int tMatchingPhaseIndex;
		int tPhaseIndex;

		tMatchingPhaseIndex = NO_PHASE;
		for (tPhaseIndex = 0; tPhaseIndex < phases.size (); tPhaseIndex++) {
			tPhaseInfo = phases.get (tPhaseIndex);
			if (tPhaseInfo.getFullName ().equals (aFullPhaseName)) {
				tMatchingPhaseIndex = tPhaseIndex;
			}
		}

		return tMatchingPhaseIndex;
	}

	public int getMinorTrainLimit () {
		PhaseInfo tPhaseInfo;
		int tTrainLimit;

		tPhaseInfo = getCurrentPhaseInfo ();
		tTrainLimit = MINIMUM_TRAIN_LIMIT;
		if (tPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			tTrainLimit = tPhaseInfo.getMinorTrainLimit ();
		}
		
		return tTrainLimit;
	}

	public int getTrainLimit (boolean aGovtRailway) {
		PhaseInfo tPhaseInfo;
		int tTrainLimit;

		tPhaseInfo = getCurrentPhaseInfo ();
		tTrainLimit = MINIMUM_TRAIN_LIMIT;
		if (tPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			if (aGovtRailway) {
				tTrainLimit = tPhaseInfo.getGovtTrainLimit ();
			} else {
				tTrainLimit = tPhaseInfo.getTrainLimit ();
			}
		}
		
		return tTrainLimit;
	}
	
	public void performPhaseChange (TrainCompany aTrainCompany, Train aTrain, BuyTrainAction aBuyTrainAction,
			Bank aBank) {
		TrainInfo tTrainInfo;
		PhaseInfo tCurrentPhase;
		String tRustTrainName;
		String tTriggerPhaseName;
		String tCurrentPhaseName;
		int tPhaseIndex;
		int tOldPhaseIndex;

		tTrainInfo = aTrain.getTrainInfo ();
		tCurrentPhase = getCurrentPhaseInfo ();
		tCurrentPhaseName = tCurrentPhase.getFullName ();
		tTriggerPhaseName = tTrainInfo.getTriggerPhase ();
		if (!(tCurrentPhaseName.equals (tTriggerPhaseName))) {
			tOldPhaseIndex = currentPhase;
			tPhaseIndex = getPhaseIndex (tTriggerPhaseName);
			setCurrentPhase (tPhaseIndex);
			tCurrentPhase = getCurrentPhaseInfo ();
			aBuyTrainAction.addPhaseChangeEffect (aTrainCompany, tOldPhaseIndex, tPhaseIndex);

			tRustTrainName = aTrain.getRust ();
			if (!tRustTrainName.equals (TrainInfo.NO_RUST)) {
				aBank.rustAllTrainsNamed (tRustTrainName, aBuyTrainAction);
			}
			aBank.discardExcessTrains (aBuyTrainAction);
			if (tCurrentPhase.getClosePrivates ()) {
				aBank.closeAllPrivates (aBuyTrainAction);
			}
		}
	}
	
	public void handleTriggerClass () {
		String tTriggerClass;
		
		tTriggerClass = getTriggerClass ();
		if (hasTriggerClass (tTriggerClass)) {
			callTriggerClass (tTriggerClass);
		}
	}

	public String getTriggerClass () {
		String tTriggerClass;
		PhaseInfo tPhaseInfo;
		
		tPhaseInfo = getCurrentPhaseInfo ();
		tTriggerClass = tPhaseInfo.getTriggerClass ();
		
		return tTriggerClass;
	}
	
	public boolean hasTriggerClass () {
		String tTriggerClass;
		
		tTriggerClass = getTriggerClass ();

		return hasTriggerClass (tTriggerClass);
	}
	
	public boolean hasTriggerClass (String aTriggerClass) {
		boolean tHasTriggerClass;
		
		if (aTriggerClass != GUI.NULL_STRING) {
			if (gameManager.triggerPanelExists ()) {
				tHasTriggerClass = false;
			} else {
				tHasTriggerClass = true;
			}
		} else {
			tHasTriggerClass = false;
		}
		
		return tHasTriggerClass;
	}

	public void handleTriggerClass (BuyTrainAction aBuyTrainAction) {
		String tTriggerClass;
		
		tTriggerClass = getTriggerClass ();
		if (hasTriggerClass (tTriggerClass)) {
			callTriggerClass (tTriggerClass, aBuyTrainAction);
		}
	}

	public void callTriggerClass (String aTriggerClass) {
		Class<?> tTriggerClass;
		Class<?> tGameManagerClass;
		Constructor<?> tTriggerConstructor;
		@SuppressWarnings ("unused")
		TriggerClass tTriggerClassActual;
		
		try {
			tTriggerClass = Class.forName (aTriggerClass);
			tGameManagerClass = gameManager.getClass ();
			tTriggerConstructor = tTriggerClass.getConstructor (tGameManagerClass);
			tTriggerClassActual = (TriggerClass) tTriggerConstructor.newInstance (gameManager);
		} catch (NoSuchMethodException tException) {
			System.err.println ("Could not find Method for Class " + aTriggerClass);
					tException.printStackTrace ();
		} catch (ClassNotFoundException tException) {
			System.err.println (
					"Could not find Class for Effect " + aTriggerClass + " due to Rename and using old Save Game");
		} catch (Exception tException) {
			System.err.println ("Caught Exception with message ");
			System.err.println ("Class name " + aTriggerClass);
			tException.printStackTrace ();
		}
	}

	public void callTriggerClass (String aTriggerClass, BuyTrainAction aBuyTrainAction) {
		Class<?> tTriggerClass;
		Class<?> tGameManagerClass;
		Class<?> tBTActionClass;
		Constructor<?> tTriggerConstructor;
		@SuppressWarnings ("unused")
		TriggerClass tTriggerClassActual;
		
		try {
			tTriggerClass = Class.forName (aTriggerClass);
			tGameManagerClass = gameManager.getClass ();
			tBTActionClass = aBuyTrainAction.getClass ();
			tTriggerConstructor = tTriggerClass.getConstructor (tGameManagerClass, tBTActionClass);
			tTriggerClassActual = (TriggerClass) tTriggerConstructor.newInstance (gameManager, aBuyTrainAction);
		} catch (NoSuchMethodException tException) {
			System.err.println ("Could not find Method for Class " + aTriggerClass);
					tException.printStackTrace ();
		} catch (ClassNotFoundException tException) {
			System.err.println (
					"Could not find Class for Effect " + aTriggerClass + " due to Rename and using old Save Game");
		} catch (Exception tException) {
			System.err.println ("Caught Exception with message ");
			System.err.println ("Class name " + aTriggerClass);
			tException.printStackTrace ();
		}
	}
	
	public void printAllPhaseInfos () {
		PhaseInfo tPhaseInfo;
		int tPhaseIndex;

		for (tPhaseIndex = 0; tPhaseIndex < phases.size (); tPhaseIndex++) {
			System.out.println ("----> Phase Index " + tPhaseIndex);		// PRINTLOG
			tPhaseInfo = phases.get (tPhaseIndex);
			tPhaseInfo.printPhaseInfo ();
		}
	}

	public void setCurrentPhase (int aIndex) {
		if ((aIndex < NO_PHASE) || (aIndex > phases.size ())) {
			System.err.println ("Trying to set Current Phase Index out of range " + aIndex);
		} else {
			currentPhase = aIndex;
		}
	}

	public boolean canBuyTrainInPhase () {
		PhaseInfo tPhaseInfo;
		boolean tCanBuyTrainInPhase;

		tCanBuyTrainInPhase = false;
		tPhaseInfo = getCurrentPhaseInfo ();
		if (tPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			tCanBuyTrainInPhase = tPhaseInfo.canBuyTrainInPhase ();
		}

		return tCanBuyTrainInPhase;
	}

	public boolean isUpgradeAllowed (String aTileColor) {
		PhaseInfo tCurrentPhaseInfo;
		boolean tUpgradeAllowed;

		tUpgradeAllowed = true;
		tCurrentPhaseInfo = getCurrentPhaseInfo ();
		tUpgradeAllowed = tCurrentPhaseInfo.isUpgradeAllowed (aTileColor);

		return tUpgradeAllowed;
	}

	/**
	 * Retrieve the current phase from the Game Manager and return it
	 * 
	 * @return The current Phase as int value
	 */

	public int getCurrentPhase () {
		return currentPhase;
	}

	public int getMinSharesToFloat (String aNextTrainName) {
		PhaseInfo tPhaseInfo;
		char tFirstChar;
		int tMinToFloat;
		int tMinToFloatLast;
		int tMinSharesToFloat;
		int tNextTrainSize;

		tPhaseInfo = getCurrentPhaseInfo ();
		tMinToFloat = tPhaseInfo.getMinToFloat ();
		tMinToFloatLast = tPhaseInfo.getMinToFloatLast ();
		tMinSharesToFloat = tMinToFloat;

		if (aNextTrainName.length () > 0) {
			tFirstChar = aNextTrainName.charAt (0);
			if ((tFirstChar >= '0') && (tFirstChar <= '9')) {
				tNextTrainSize = Integer.parseInt (String.valueOf (tFirstChar));
				if (tMinToFloatLast == tNextTrainSize) {
					tMinSharesToFloat = tMinToFloatLast;
				}
			}
		}

		return tMinSharesToFloat;
	}
}
