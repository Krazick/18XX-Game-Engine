package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.game.Game_18XX;
import ge18xx.network.JGameClient;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.toplevel.AuditFrame;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionManager {
	public final static Action NO_ACTION = null;
	public final static int STARTING_ACTION_NUMBER = 100;
	List<Action> actions;
	ActionReportFrame actionReportFrame;
	RoundManager roundManager;
	GameManager gameManager;
	int actionNumber;
	private final static String ACTION_NUMBER_RESPONSE = "<GSResponse><ActionNumber newNumber=\"(\\d+)\"></GSResponse>";
	private final static Pattern ACTION_NUMBER_PATTERN = Pattern.compile (ACTION_NUMBER_RESPONSE);
	Logger logger;
	
	public ActionManager (RoundManager aRoundManager) {
		String tFullTitle;
		
		roundManager = aRoundManager;
		gameManager = roundManager.getGameManager ();
		tFullTitle = gameManager.createFrameTitle ("Action Report");
		actions = new LinkedList<Action> ();
		actionReportFrame = new ActionReportFrame (tFullTitle, aRoundManager.getGameName ());
		gameManager.addNewFrame (actionReportFrame);
		setActionNumber (0);
		logger = Game_18XX.getLogger ();
	}
	
	public void setActionNumber (int aNumber) {
		String tReportActionNumber;
		
		tReportActionNumber = "Change Action Number from " + actionNumber + " to " + aNumber + "\n";
		
		actionNumber = aNumber;
		actionReportFrame.append (tReportActionNumber);
	}
	
	public int getActionNumber () {
		return actionNumber;
	}
	
	public int getLastActionNumber () {
		int tLastActionNumber;
		Action tLastAction;
		
		tLastAction = this.getLastAction ();
		tLastActionNumber = tLastAction.getNumber ();
		
		return tLastActionNumber;
	}
	
	public int getActionNumberFrom (String aResponse) {
		Matcher tMatcher = ACTION_NUMBER_PATTERN.matcher (aResponse);
		String tFoundNewNumber = "NOID";
		int tNewNumber = 0;
		
		if (tMatcher.find ()) {
			tFoundNewNumber = tMatcher.group (1);
			tNewNumber = Integer.parseInt (tFoundNewNumber);
		}
		
		return tNewNumber;
	}

	public void generateNewActionNumber () {
		String tReportActionNumber;
		String tActionNumberString;
		int tNewActionNumber;
		
		if (gameManager.isNetworkGame ()) {
			tActionNumberString = gameManager.requestGameSupport (JGameClient.REQUEST_ACTION_NUMBER);
			tNewActionNumber = getActionNumberFrom (tActionNumberString);
			if (tNewActionNumber > 0) {
				actionNumber = tNewActionNumber;
				tReportActionNumber = "Retrieved New Action Number " + actionNumber + " from Game Server\n";
			} else {
				actionNumber++;
				tReportActionNumber = "FAILED to retrieve New Action Number from Game Server\n";
			}
			
			actionReportFrame.append (tReportActionNumber);
		} else {
			tReportActionNumber = "Increment Action Number from " + actionNumber + " to " + (actionNumber + 1) + "\n";
			actionNumber++;
			actionReportFrame.append (tReportActionNumber);
		}
	}
	
	public void actionReport () {
		System.out.println ("<---------------------------\nAction Report");
		for (Action tAction: actions) {
			System.out.println (tAction.getActionReport (roundManager));
		}
		System.out.println ("<---------------------------");
	}
	
	public void appendAllActions () {
		for (Action tAction: actions) {
			appendToReportFrame (tAction);
		}
	}

	public void sendToReportFrame (String aReport) {
		String tReport = "\n----------------------" + aReport + "\n----------------------";
		
		actionReportFrame.append (tReport);
	}
	
	private void appendToReportFrame (Action aAction) {
		String tActionReport;
		
		tActionReport = aAction.getActionReport (roundManager);
		actionReportFrame.append (tActionReport);
	}
	
	public void addAction (Action aAction) {
		boolean tAllNullEffects;
		
		tAllNullEffects = aAction.allNullEffects ();
		if (tAllNullEffects) {
			logger.debug (aAction.getBriefActionReport () + " All Null Effects " + 
					tAllNullEffects + " Last Action Number " + actionNumber);
		} else {
			setNewActionNumber (aAction);
			setAuditAttributes (aAction);
			logger.info ("Local Action # " + actionNumber + " Name " + aAction.getName () + 
					" From " + aAction.getActor ().getName ());
			justAddAction (aAction);
		}
	}

	private void justAddAction (Action aAction) {
		JGameClient tNetworkJGameClient;
		String tXMLFormat;

		actions.add (aAction);
		appendToReportFrame (aAction);
		// Note the 'getNotifyNetwork' in the Game Manager should be tested
		// To prevent Applying Actions from a remote client would also send
		// The action back out to the remote client causing an Infinite Loop
		if (gameManager.isNetworkGame () && gameManager.getNotifyNetwork ()) {
			tNetworkJGameClient = gameManager.getNetworkJGameClient ();
			tXMLFormat = aAction.getXMLFormat (JGameClient.EN_GAME_ACTIVITY);
			tXMLFormat = tXMLFormat.replaceAll ("\n","");
			tNetworkJGameClient.sendGameActivity (tXMLFormat);
			appendToJGameClient (aAction);
		}	
	}
	
	private void setAuditAttributes (Action aAction) {
		int tTotalCash;
		
		tTotalCash = gameManager.getTotalCash ();
		aAction.setTotalCash (tTotalCash);
	}
	
	private void setNewActionNumber (Action aAction) {
		int tActionNumber;
		
		generateNewActionNumber ();
		tActionNumber = getActionNumber ();
		aAction.setNumber (tActionNumber);
	}
	
	public void briefActionReport () {
		int tActionCount;
		String tBriefReport;
		
		tActionCount = getActionCount ();
		if (tActionCount == 1) {
			tBriefReport = "There is ONE Action.";
		} else {
			tBriefReport = "There are " + getActionCount () + " Actions.";
		}
		System.out.println (tBriefReport);
		for (Action tAction: actions) {
			System.out.println (tAction.getBriefActionReport ());
		}
	}
	
	public XMLElement getActionElements (XMLDocument aXMLDocument) {
		XMLElement tElements, tActionElement;
		
		tElements = aXMLDocument.createElement (Action.EN_ACTIONS);
		for (Action tAction : actions) {
			tActionElement = tAction.getActionElement (aXMLDocument);
			tElements.appendChild (tActionElement);
		}

		return tElements;
	}
	
	public Action getActionAt (int aIndex) {
		Action tAction;
		int tActionCount;
		
		tAction = NO_ACTION;
		tActionCount = getActionCount ();
		if (tActionCount > 0) {
			if ((aIndex >= 0) && (aIndex < tActionCount)) {
				tAction = actions.get (aIndex);
			}
		}
		
		return tAction;
	}
	
	public int getActionCount () {
		return actions.size ();
	}
	
	public Action getLastAction () {
		Action tAction;
		int tLastActionID;
		
		tAction = NO_ACTION;
		if (!actions.isEmpty()) {
			tLastActionID = getActionCount () - 1;
			tAction = actions.get (tLastActionID);
		}
		
		return tAction;
	}
	
	public boolean hasActionsToUndo () {
		return !actions.isEmpty ();
	}
	
	public void loadActions (XMLNode aActionsNode, GameManager aGameManager) {
		XMLNode tActionNode;
		NodeList tActionChildren;
		int tActionNodeCount, tActionIndex, tActionNumber;
		Action tAction;
		
		tActionChildren = aActionsNode.getChildNodes ();
		tActionNodeCount = tActionChildren.getLength ();
		tActionNumber = 0;
		try {
			for (tActionIndex = 0; tActionIndex < tActionNodeCount; tActionIndex++) {
				tActionNode = new XMLNode (tActionChildren.item (tActionIndex));
				tAction = getAction (aGameManager, tActionNode);
				if (tAction != NO_ACTION) {
					justAddAction (tAction);
					tActionNumber = tAction.getNumber ();
				}
			}
			setActionNumber (tActionNumber);
		} catch (Exception e) {
			logger.error ("Error Loading Actions", e);
		}
	}

	private Action getAction (GameManager aGameManager, XMLNode tActionNode) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		String tANodeName;
		String tClassName;
		Action tAction = NO_ACTION;
		Class<?> tActionToLoad;
		Constructor<?> tActionConstructor;
		
		tANodeName = tActionNode.getNodeName ();
		if (Action.EN_ACTION.equals (tANodeName)) {
			// Use Reflections to identify the Action and call the constructor with the XMLNode and the Game Manager
			tClassName = tActionNode.getThisAttribute (Action.AN_CLASS);
			tActionToLoad = Class.forName (tClassName);
			tActionConstructor = tActionToLoad.getConstructor (tActionNode.getClass (), aGameManager.getClass ());
			tAction = (Action) tActionConstructor.newInstance (tActionNode, aGameManager);
		}
		
		return tAction;
	}
	
	public void removeLastAction () {
		int tLastActionID;
		
		tLastActionID = getActionCount () - 1;
		actions.remove (tLastActionID);
	}
	
	public boolean undoLastActionNetwork () {
		JGameClient tNetworkJGameClient;
		String tXMLFormat;
		Action tUndoAction, tLastAction;
		ActorI tActor;
		ActionStates tRoundType;
		String tRoundID;
		boolean tLastActionUndone = true;
		
		if (gameManager.isNetworkGame () && gameManager.getNotifyNetwork ()) {
			tLastAction = getLastAction ();
			tActor = tLastAction.getActor ();
			tRoundType = tLastAction.getRoundType ();
			tRoundID = tLastAction.getRoundID ();
			tUndoAction = new UndoLastAction (tRoundType, tRoundID, tActor);
			setNewActionNumber (tUndoAction);
			tNetworkJGameClient = gameManager.getNetworkJGameClient ();
			tXMLFormat = tUndoAction.getXMLFormat (JGameClient.EN_GAME_ACTIVITY);
			tXMLFormat = tXMLFormat.replaceAll ("\n","");
			tNetworkJGameClient.sendGameActivity (tXMLFormat);
			appendToJGameClient (tUndoAction);
		}	

		return tLastActionUndone;
	}
	public boolean undoLastAction (RoundManager aRoundManager) {
		boolean tLastActionUndone;
		
		tLastActionUndone = undoLastAction (aRoundManager, true);
		
		return tLastActionUndone;
	}
	
	public boolean undoLastAction (RoundManager aRoundManager, boolean aNotifyNetwork) {
		boolean tLastActionUndone;
		Action tLastAction;

		tLastAction = getLastAction ();
		actionReportFrame.append ("\nUNDOING: " + tLastAction.getBriefActionReport ());
		tLastAction.printBriefActionReport ();
		tLastActionUndone = tLastAction.undoAction (aRoundManager);
		if (aNotifyNetwork) {
			undoLastActionNetwork ();
		}
		if (tLastActionUndone) {
			removeLastAction ();
			if (tLastAction.getChainToPrevious ()) {
				tLastActionUndone = undoLastAction (aRoundManager, false);
			}
		}
		aRoundManager.updateAllFrames ();
		
		return tLastActionUndone;
	}
	
	public boolean wasLastActionStartAuction () {
		Action tLastAction;
		boolean tWasLastActionStartAuction = false;

		tLastAction = getLastAction ();
		tWasLastActionStartAuction = tLastAction.wasLastActionStartAuction ();
		
		return tWasLastActionStartAuction;
	}

	public void appendAction (String aGameActivity) {
		actionReportFrame.append (aGameActivity);
	}

	public boolean isSyncActionNumber (Action aAction) {
		return (aAction instanceof SyncActionNumber);
	}
	
	public void handleSyncActionNumber (Action aSyncAction) {
		
	}
	
	public void handleNetworkAction (XMLNode aActionNode) {
		Action tAction;
		int tExpectedActionNumber, tThisActionNumber;
		String tActionFailureMessage;
		
		// When handling incomming Network Actions, we DO NOT want to notify other clients
		// that they should apply these Actions (one of them is sending it to us)
		// We end up getting into an Infinite Loop between two separate clients 
		gameManager.setNotifyNetwork (false); 
		try {
			tAction = getAction (gameManager, aActionNode);
			if (tAction != ActionManager.NO_ACTION) {
				tExpectedActionNumber = actionNumber + 1;
				tThisActionNumber = tAction.getNumber ();
				
				System.out.println ("----------- Action Number " + actionNumber + 
						" Received " + tThisActionNumber + " -------------");
				tAction.printActionReport (roundManager);
				if (isSyncActionNumber (tAction)) {
					setActionNumber (tThisActionNumber);
					justAddAction (tAction);
				} else {
					if ((tThisActionNumber < STARTING_ACTION_NUMBER) ||
							(tThisActionNumber > tExpectedActionNumber) ||
						(tThisActionNumber == tExpectedActionNumber)) {
						System.out.println ("\nReceived Action Number " + tThisActionNumber + 
								" the Expected Action Number is " + tExpectedActionNumber + " Processing\n");
						if (tThisActionNumber == tExpectedActionNumber) {
							setActionNumber (tExpectedActionNumber);
						}
						actions.add (tAction);
						logger.info ("Network Action # " + actionNumber + " Name " + tAction.getName () + " From " + tAction.getActor ().getName ());
						applyAction (tAction);
						gameManager.autoSaveGame ();
						// Add the Report of the Action Applied to the Action Frame, and the JGameClient Game Activity Frame
						appendToReportFrame (tAction);
						appendToJGameClient (tAction);
					} else if (tThisActionNumber <= actionNumber) {
						tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber + 
								" Current Action Number " + actionNumber +
								" is before the Expected Action Number of " + tExpectedActionNumber + " IGNORING\n";
						logger.error (tActionFailureMessage);
//						System.err.println (tActionFailureMessage);
						actionReportFrame.append (tActionFailureMessage);
//					} else if (tThisActionNumber > tExpectedActionNumber) {
//						tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber + 
//								" is after the Expected Action Number of " + tExpectedActionNumber + " THERE IS A GAP\n";
//						logger.error (tActionFailureMessage);
//						System.err.println (tActionFailureMessage);						
//						actionReportFrame.append (tActionFailureMessage);
					} else {
						tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber + 
								" is not the Expected Action Number of " + tExpectedActionNumber + " This should have Matched\n";
						logger.error (tActionFailureMessage);
//						System.err.println (tActionFailureMessage);
					}
				}
			} else {
				logger.error ("No Action Found to Process");
			}
		} catch (Exception tException) {
			logger.error (tException.getMessage (), tException);
//			System.err.println (tException.getMessage ());
//			tException.printStackTrace ();
		}
		gameManager.setNotifyNetwork (true);
//		Once we are done applying these Actions, we then can reset this back to Notify

	}
	
	public void appendToJGameClient (Action aAction) {
		GameManager tGameManager;
		String tSimpleActionReport;
		
		tGameManager = roundManager.getGameManager ();
		tSimpleActionReport = aAction.getSimpleActionReport ();
		if (! tSimpleActionReport.equals ("")) {
			tGameManager.appendToGameActivity (tSimpleActionReport);
		}
	}
	
	public boolean applyAction (Action aAction) {
		boolean tActionApplied;
		int tActionNumber;
		
		tActionApplied = aAction.applyAction (roundManager);
		tActionNumber = aAction.getNumber ();
		if (tActionNumber > actionNumber) {
			setActionNumber (tActionNumber);
		}
		roundManager.updateRoundFrame ();
		
		return tActionApplied;
	}

	public void showActionReportFrame () {
		actionReportFrame.setVisible (true);
	}
	
	public void fillAuditFrame (AuditFrame aAuditFrame, String aActorName) {
		int tTotalActionCount, tActionIndex;
		Action tAction;
		String tActionEventDescription, tActionName;
		int tDebit, tCredit, tActionNumber, tFoundActionCount;
		String tRoundID;
		
		if (aActorName != null) { 
			tTotalActionCount = actions.size ();
			if (tTotalActionCount > 0) {
				tFoundActionCount = 0;
				for (tActionIndex = 0; tActionIndex < tTotalActionCount; tActionIndex++) {
					tAction = actions.get (tActionIndex);
					if (tAction.effectsThisActor (aActorName)) {
						tActionNumber = tAction.getNumber ();
						tActionName = tAction.getName ();
						if (tAction.hasRefundEscrowEffect (aActorName)) {
							tFoundActionCount++;
							handleAuctionReporting(aAuditFrame, aActorName, tAction, tActionName, tActionNumber);		
						} else if (tAction.effectsForActorAreCash (aActorName)) {
							tFoundActionCount++;
							
							tActionEventDescription = tActionName + ": " + tAction.getSimpleActionReport ();
							tDebit = tAction.getEffectDebit (aActorName);
							tCredit = tAction.getEffectCredit (aActorName);
							tRoundID = tAction.getRoundType ().toAbbrev () + " " + tAction.getRoundID ();
							aAuditFrame.addRow (tActionNumber, tRoundID, tActionEventDescription, tDebit, tCredit);
						}
					}
				}
				logger.info ("Examined " + tActionIndex + " Actions found " + tFoundActionCount + " Actions for " + aActorName);

//				System.out.println ("Examined " + tActionIndex + " Actions found " + tFoundActionCount + " Actions for " + aActorName);
			}
		}
	}

	private void handleAuctionReporting (AuditFrame aAuditFrame, String aActorName, Action aAction, String aActionName,
			int aActionNumber) {
		String tActionEventDescription;
		int tDebit;
		int tCredit;
		String tRoundID;
		String tAuctionWinner;
		
		tActionEventDescription = aActionName + ": " + aAction.getSimpleActionReport (aActorName);
		tDebit = 0;
		tCredit = aAction.getEffectCredit (aActorName);
		tRoundID = aAction.getRoundType ().toAbbrev () + " " + aAction.getRoundID ();
		aAuditFrame.addRow (aActionNumber, tRoundID, tActionEventDescription, tDebit, tCredit);
//		tClientUserName = gameManager.getClientUserName ();
		tAuctionWinner = aAction.getAuctionWinner ();
		if (aActorName.equals (tAuctionWinner)) {
			tActionEventDescription = aActionName + ": " + aAction.getSimpleActionReport ();
			aAuditFrame.addRow (aActionNumber, tRoundID, tActionEventDescription, tCredit, tDebit);
		}
	}

	public boolean isLastActionComplete () {
		boolean tIsLastActionComplete = true;
//		String tLastActionComplete;
		
		if (gameManager.isNetworkGame ()) {
//			tLastActionComplete = gameManager.requestGameSupport (JGameClient.REQUEST_LAST_ACTION_COMPLETE);
		} else {
			tIsLastActionComplete = true;
		}
		
		return tIsLastActionComplete;
	}
}