package ge18xx.phase;

import java.util.LinkedList;
import java.util.List;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.train.Train;
import ge18xx.train.TrainInfo;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PhaseManager {
	public static final PhaseManager NO_PHASE_MANAGER = null;
	public static final List<PhaseInfo> NO_PHASES = null;
	public static final int NO_PHASE = -1;
	public static final int FIRST_PHASE = 0;
	public final static ElementName EN_PHASE = new ElementName ("Phase");
	final static AttributeName AN_CURRENT_PHASE = new AttributeName ("currentPhase");
	List<PhaseInfo> phases;
	int currentPhase;

	public PhaseManager () {
		phases = new LinkedList<PhaseInfo> ();
		setCurrentPhase (NO_PHASE);
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
		System.out.println ("----");
		for (PhaseInfo tPhaseInfo : phases) {
			System.out.println ("Phase Name " + tPhaseInfo.getFullName ());
		}
	}
	
	public boolean doIncrementalCapitalization () {
		return getCurrentPhaseInfo ().doIncrementalCapitalization ();
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

	public XMLElement getPhaseElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_PHASE);
		tXMLElement.setAttribute (AN_CURRENT_PHASE, currentPhase);

		return tXMLElement;
	}

	public void loadPhase (XMLNode aXMLNode) {
		currentPhase = aXMLNode.getThisIntAttribute (AN_CURRENT_PHASE);
	}

	public String getCurrentOffBoard () {
		String tCurrentOffBoard = "";
		PhaseInfo tCurrentPhaseInfo = getCurrentPhaseInfo ();

		if (tCurrentPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			tCurrentOffBoard = tCurrentPhaseInfo.getOffBoard ();
		}

		return tCurrentOffBoard;
	}

	public int getPhaseIndex (String aFullPhaseName) {
		int tMatchingPhaseIndex, tPhaseIndex;
		PhaseInfo tPhaseInfo;

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

		tPhaseInfo = getCurrentPhaseInfo ();

		return tPhaseInfo.getMinorTrainLimit ();
	}

	public int getTrainLimit (boolean aGovtRailway) {
		PhaseInfo tPhaseInfo;
		int tTrainLimit;

		tPhaseInfo = getCurrentPhaseInfo ();

		if (aGovtRailway) {
			tTrainLimit = tPhaseInfo.getGovtTrainLimit ();
		} else {
			tTrainLimit = tPhaseInfo.getTrainLimit ();
		}

		return tTrainLimit;
	}

	public void performPhaseChange (TrainCompany aTrainCompany, Train aTrain, BuyTrainAction aBuyTrainAction,
			Bank aBank) {
		TrainInfo tTrainInfo;
		PhaseInfo tCurrentPhase;
		int tPhaseIndex;
		int tOldPhaseIndex;
		String tRustTrainName;

		tTrainInfo = aTrain.getTrainInfo ();
		tCurrentPhase = getCurrentPhaseInfo ();
		if (!(tCurrentPhase.getFullName ().equals (tTrainInfo.getTriggerPhase ()))) {
			tOldPhaseIndex = currentPhase;
			tPhaseIndex = getPhaseIndex (tTrainInfo.getTriggerPhase ());
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

	public void printAllPhaseInfos () {
		int tPhaseIndex;
		PhaseInfo tPhaseInfo;

		for (tPhaseIndex = 0; tPhaseIndex < phases.size (); tPhaseIndex++) {
			System.out.println ("----> Phase Index " + tPhaseIndex);
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
		boolean tCanBuyTrainInPhase = false;
		PhaseInfo tPhaseInfo;

		tPhaseInfo = getCurrentPhaseInfo ();
		if (tPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
			tCanBuyTrainInPhase = tPhaseInfo.canBuyTrainInPhase ();
		}

		return tCanBuyTrainInPhase;
	}

	public boolean isUpgradeAllowed (String aTileColor) {
		PhaseInfo tCurrentPhaseInfo;
		boolean tUpgradeAllowed = true;

		tCurrentPhaseInfo = getCurrentPhaseInfo ();
		tUpgradeAllowed = tCurrentPhaseInfo.isUpgradeAllowed (aTileColor);

		return tUpgradeAllowed;
	}

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
