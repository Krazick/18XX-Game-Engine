package ge18xx.round;

import java.lang.reflect.Constructor;

import ge18xx.company.CorporationList;
import ge18xx.company.formation.FormCGR;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeRoundAction;
import ge18xx.round.action.StartFormationAction;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;

public class FormationRound extends InterruptionRound {
	public final static AttributeName AN_TRIGGERING_CLASS = new AttributeName ("triggeringClass");
	public static final FormationRound NO_FORMATION_ROUND = null;
	public static final String NAME = "Formation Round";
	FormCGR formCGR;
	String triggeringClassName;
	TriggerClass triggerFormationClass;

	public FormationRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setName (NAME);
		setRoundType ();
	}
	
	@Override
	public void loadRound (XMLNode aRoundNode) {
		super.loadRound (aRoundNode);
	}
	
	public void constructFormationClass (String aFullClassName) {
		Class<?> tFormationToLoad;
		Constructor<?> tFormationConstructor;
		GameManager tGameManager;

		tGameManager = roundManager.getGameManager ();
		try {
			tFormationToLoad = Class.forName (aFullClassName);
			tFormationConstructor = tFormationToLoad.getConstructor (tGameManager.getClass ());
			triggerFormationClass = (TriggerClass) tFormationConstructor.newInstance (tGameManager);
		} catch (Exception tException) {
			System.err.println ("Caught Exception Trying to create Formation Constructor with message ");
			tException.printStackTrace ();
		}
	}
	
	@Override
	public ActorI.ActionStates getRoundState () {
		return ActorI.ActionStates.FormationRound;
	}
	
	@Override
	public boolean isAFormationRound () {
		return true;
	}
	
	@Override
	public boolean isInterrupting () {
		boolean tIsInterrupting;
		GameManager tGameManager;
		String tCurrentPhaseName;
		PhaseInfo tCurrentPhaseInfo;
		String tRoundTypePhase;
		
		tIsInterrupting = false;
		// TODO: Need to add test if Interruption is required:
		// for 1856 Purchase of 
		//		We have Entered Phase 5 (in Phase Info)
		//		AND at least one Share Company has at least one Loan Outstanding
		tGameManager = roundManager.getGameManager ();
		if (tGameManager.gameHasRoundType (NAME)) {
			tRoundTypePhase = getPhase ();
			if (tRoundTypePhase != GUI.EMPTY_STRING) {
				tCurrentPhaseInfo = roundManager.getCurrentPhaseInfo ();
				tCurrentPhaseName = tCurrentPhaseInfo.getFullName ();
				if (tRoundTypePhase.equals (tCurrentPhaseName)) {
					tIsInterrupting = hasOutstandingLoans ();
				}
			}
		}
		
		// for 1835 Purchase of 
		//		a X Train and Formation is Optional
		//		OR Start of OR if PR formation started
		//		OR Purchase of Z Train and Formation REQUIRED
		
		return tIsInterrupting;
	}
	
	public boolean hasOutstandingLoans () {
		CorporationList tShareCompanies;
		boolean tCanStart;
		
		tCanStart = false;
		if (roundManager.gameHasLoans ()) {
			tShareCompanies = roundManager.getShareCompanies ();
			tCanStart = tShareCompanies.anyHaveLoans ();
		}
		
		return tCanStart;
	}
	
	public void setTriggerFormationClass (TriggerClass aTriggerFormationClass) {
		triggerFormationClass = aTriggerFormationClass;
	}
	
	public TriggerClass getTriggerFormationClass () {
		return triggerFormationClass;
	}
	
	public void setRoundToThis (ChangeRoundAction aChangeRoundAction, boolean aIncrementRoundID) {
		String tOldRoundID;
		String tNewRoundID;
		String tGameName;
		int tRoundID;
		RoundFrame tRoundFrame;

		tOldRoundID = getID ();
		if (aIncrementRoundID) {
			tRoundID = incrementRoundIDPart1 ();
			setIDPart1 (tRoundID);
			tNewRoundID = tRoundID + "";
		} else {
			tRoundID = getIDPart1 ();
			tNewRoundID = tOldRoundID;
		}

		roundManager.changeRound (interruptedRound, ActorI.ActionStates.FormationRound, this, tOldRoundID, tNewRoundID,
				aChangeRoundAction);
		tGameName = roundManager.getGameName ();
		tRoundFrame = roundManager.getRoundFrame ();
		tRoundFrame.setFormationRound (tGameName, tRoundID);
	}

	@Override
	public void start () {
		StartFormationAction tStartFormationAction;
		GameManager tGameManager;
		TriggerClass tTriggerFormationClass;
		ActorI.ActionStates tRoundType;
		String tRoundID;

		System.out.println ("Ready to START Formation Round");
		super.start ();
		
		tGameManager = roundManager.getGameManager ();
		
		tRoundType = interruptedRound.getRoundState ();
		tRoundID = interruptedRound.getID ();

		tStartFormationAction = new StartFormationAction (tRoundType, tRoundID, interruptedRound);
		tStartFormationAction.setChainToPrevious (true);
		
		tTriggerFormationClass = tGameManager.getTriggerFormation ();
		if (tTriggerFormationClass == TriggerClass.NO_TRIGGER_CLASS) {
			// This SHOULD NOT Happen, since the Form should have been created
			// when the Trigger Class has been triggered that should have
			// saved the operating company
			System.err.println ("The Trigger Formation Class was NOT setup properly when the Trigger Class was triggered!!!");
		}
		setTriggerFormationClass (tTriggerFormationClass);

		triggerFormationClass.prepareFormation (tStartFormationAction);
		
		setRoundToThis (tStartFormationAction, true);
		roundManager.addAction (tStartFormationAction);
	}
	
	@Override
	public void finish () {
		XMLFrame tFormationFrame;

		tFormationFrame = formCGR.getFormationFrame ();
		super.finish (tFormationFrame);
	}

	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_FORMATION_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}
}
