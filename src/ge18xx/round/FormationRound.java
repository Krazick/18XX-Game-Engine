package ge18xx.round;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ge18xx.company.formation.FormCompany;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.PlayerManager;
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
	
	@Override
	public XMLFrame getXMLFrameNamed (String aXMLFrameTitle) {
		XMLFrame tXMLFrameFound;
		
		tXMLFrameFound = XMLFrame.NO_XML_FRAME;

		return tXMLFrameFound;
	}

	public FormCompany constructFormationClass (GameManager aGameManager, XMLNode aXMLNode) {
		String tFormationClassName;
		FormCompany tFormCompany;
		StartFormationAction tStartFormationAction;
		ActionStates tRoundType;
		String tRoundID;
		ActorI tActor;
		
		tFormationClassName = aXMLNode.getThisAttribute (FormCompany.AN_CLASS);
		tFormCompany = FormCompany.NO_FORM_COMPANY;
		tRoundType = ActionStates.FormationRound;
		tRoundID = getID ();
		tActor = (Round) this;
		tStartFormationAction = new StartFormationAction (tRoundType, tRoundID, tActor);
		try {
			tFormCompany = FormCompany.getConstructor (aGameManager, aXMLNode, tFormationClassName);
			setTriggerFormationClass (tFormCompany);
			tFormCompany.prepareFormation (tStartFormationAction);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		if (tFormCompany != FormCompany.NO_FORM_COMPANY) {
			tFormCompany.rebuildFormationPanel ();
		}
		
		return tFormCompany;
	}
	
	public TriggerClass constructFormationClass () {
		constructFormationClass (triggeringClassName);
		
		return triggerFormationClass;
	}
	
	public void constructFormationClass (String aFullClassName) {
		Class<?> tFormationToLoad;
		Constructor<?> tFormationConstructor;
		GameManager tGameManager;
		TriggerClass tTriggerFormationClass;

		tGameManager = roundManager.getGameManager ();
		try {
			tFormationToLoad = Class.forName (aFullClassName);
			tFormationConstructor = tFormationToLoad.getConstructor (tGameManager.getClass ());
			tTriggerFormationClass = (TriggerClass) tFormationConstructor.newInstance (tGameManager);
			setTriggerFormationClass (tTriggerFormationClass);
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
		String tRoundTypePhases;
		
		tIsInterrupting = false;
		tGameManager = roundManager.getGameManager ();
		if (tGameManager.gameHasRoundType (NAME)) {
			tRoundTypePhases = getPhases ();
			if (tRoundTypePhases != GUI.EMPTY_STRING) {
				tCurrentPhaseInfo = roundManager.getCurrentPhaseInfo ();
				tCurrentPhaseName = tCurrentPhaseInfo.getFullName ();
				if (tRoundTypePhases.contains (tCurrentPhaseName)) {
					tIsInterrupting = triggerFormationClass.isInterrupting ();
				}
			}
		}
		
		return tIsInterrupting;
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
		int tRoundID;

		tOldRoundID = getID ();
		if (aIncrementRoundID) {
			tRoundID = incrementRoundIDPart1 ();
			setIDPart1 (tRoundID);
			setIDPart2 (START_ID2);
			tNewRoundID = getID ();
		} else {
			tRoundID = getIDPart1 ();
			tNewRoundID = tOldRoundID;
		}
		setRoundTo (this, tRoundID, tOldRoundID, tNewRoundID, aChangeRoundAction);
	}
	
	@Override
	public void start () {
		StartFormationAction tStartFormationAction;
		GameManager tGameManager;
		TriggerClass tTriggerFormationClass;
		ActorI.ActionStates tRoundType;
		String tRoundID;

		super.start ();
		
		tGameManager = roundManager.getGameManager ();
		
		tRoundType = interruptedRound.getRoundState ();
		tRoundID = interruptedRound.getID ();

		tStartFormationAction = new StartFormationAction (tRoundType, tRoundID, interruptedRound);
		tStartFormationAction.setChainToPrevious (true);
		
		tStartFormationAction.addSetAtStartOfRoundEffect (this, atStartOfRound);
		
		tTriggerFormationClass = tGameManager.getTriggerFormation ();
		if (tTriggerFormationClass == TriggerClass.NO_TRIGGER_CLASS) {
			// This SHOULD NOT Happen, since the Form should have been created
			// when the Trigger Class has been triggered that should have
			// saved the operating company
			System.err.println ("The Trigger Formation Class was NOT setup properly when the Trigger Class was triggered!!!");
		}
		setTriggerFormationClass (tTriggerFormationClass);
		
		setRoundToThis (tStartFormationAction, true);
		triggerFormationClass.prepareFormation (tStartFormationAction);
		triggerFormationClass.showFormationFrame ();
		roundManager.addAction (tStartFormationAction);
	}
	
	/**
	 *  This method will test if the Formation Round will end. 
	 *  The call if the Round Manager then looks like:
	 *  
	 *      if (currentRound.ends ()) { move forward with finishing the Formation Round }
	 *      
	 */
	
	@Override
	public boolean ends () {
		boolean tEnds;
		
		tEnds = triggerFormationClass.ends ();
		
		return tEnds;
	}

	@Override
	public void finish () {
		XMLFrame tFormationFrame;
		
		tFormationFrame = triggerFormationClass.getFormationFrame ();
		super.finish (tFormationFrame);
		if (interruptedAtStartOfRound ()) {
			roundManager.startNextRound ();
		}
	}

	@Override
	public void addSpecificEffects (ChangeRoundAction aChangeRoundAction) {
		PlayerManager tPlayerManager;
		
		tPlayerManager = roundManager.getPlayerManager ();
		tPlayerManager.setAllPlayersToPass (aChangeRoundAction);
	}
	
	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_FORMATION_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}
}
