package ge18xx.round.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.game.Game_18XX;
import ge18xx.network.JGameClient;
import ge18xx.network.ResendLastActionsFrame;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.toplevel.AuditFrame;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ActionManager {
	public final static ActionManager NO_ACTION_MANAGER = null;
	public final static int STARTING_ACTION_NUMBER = 100;
	public final static int DEFAULT_ACTION_NUMBER = 0;
	public final static int PREVIOUS_ACTION = 1;
	public final static ElementName EN_REMOVE_ACTION = new ElementName ("RemoveAction");
	private final static String ACTION_NUMBER_RESPONSE = "<GSResponse><ActionNumber newNumber=\"(\\d+)\"></GSResponse>";
	private final static Pattern ACTION_NUMBER_PATTERN = Pattern.compile (ACTION_NUMBER_RESPONSE);
	List<Action> actions;
	List<Action> actionsToRemove;
	ActionReportFrame actionReportFrame;
	RoundManager roundManager;
	GameManager gameManager;
	int actionNumber;
	Logger logger;

	public ActionManager (RoundManager aRoundManager) {
		String tFullTitle;

		roundManager = aRoundManager;
		gameManager = roundManager.getGameManager ();
		tFullTitle = gameManager.createFrameTitle ("Action Report");
		actions = new LinkedList<> ();
		actionsToRemove = new LinkedList<> ();
		actionReportFrame = new ActionReportFrame (tFullTitle, gameManager);
		gameManager.addNewFrame (actionReportFrame);
		setActionNumber (DEFAULT_ACTION_NUMBER);
		logger = Game_18XX.getLoggerX ();
	}

	public GameManager getGameManager () {
		return gameManager;
	}
	
	public void setActionNumber (int aNumber) {
		String tReportActionNumber;

		tReportActionNumber = "\nChange Action Number from " + actionNumber + " to " + aNumber + "\n";

		actionNumber = aNumber;
		appendReport (tReportActionNumber);
	}

	public int getActionNumber () {
		return actionNumber;
	}

	public int getLastActionNumber () {
		int tLastActionNumber;
		Action tLastAction;

		tLastAction = getLastAction ();
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

	public int generateNewActionNumber () {
		String tReportActionNumber;
		String tActionNumberString;
		int tNewActionNumber;

		if (gameManager.isNetworkGame ()) {
			tActionNumberString = gameManager.requestGameSupport (JGameClient.REQUEST_ACTION_NUMBER);
			tNewActionNumber = getActionNumberFrom (tActionNumberString);
			if (tNewActionNumber > 0) {
				actionNumber = tNewActionNumber;
				tReportActionNumber = "\nRetrieved New Action Number " + actionNumber + " from Game Server\n";
				appendReport (tReportActionNumber);
			} else {
				actionNumber++;
				tReportActionNumber = "FAILED to retrieve New Action Number from Game Server\n";
				appendErrorReport (tActionNumberString);
			}
		} else {
			tReportActionNumber = "Increment Action Number from " + actionNumber + " to " + (actionNumber + 1) + "\n";
			actionNumber++;
		}

		return actionNumber;
	}

	public void actionReport () {
		System.out.println ("<---------------------------\nAction Report");
		for (Action tAction : actions) {
			System.out.println (tAction.getActionReport (roundManager));
		}
		System.out.println ("<---------------------------");
	}

	public void appendAllActions () {
		for (Action tAction : actions) {
			appendActionReport (tAction);
		}
	}

	/**
	 * Append Report String to Action Report Frame with a Line Border on Top and Bottom
	 *
	 * @param aReport String Text to append to the end of the Action Report Frame
	 *
	 */
	public void appendBorderedReport (String aReport) {
		String tReport = "\n----------------------" + aReport + "\n----------------------";

		appendReport (tReport);
	}

	/**
	 * Append Report String to Action Report Frame
	 *
	 * @param aReport String Text to append to the end of the Action Report Frame
	 *
	 */
	public void appendReport (String aReport) {
		actionReportFrame.append (aReport);
	}

	/**
	 * Append Error Report String to Action Report Frame as an Error
	 *
	 * @param aErrorReport String Text to append as an Error to the end of the Action Report Frame
	 *
	 */
	public void appendErrorReport (String aErrorReport) {
		actionReportFrame.appendErrorReport (aErrorReport);
	}

	public String getFullActionReport () {
		return actionReportFrame.getText ();
	}

	private void appendActionReport (Action aAction) {
		String tActionReport;

		tActionReport = "\n" + aAction.getActionReport (roundManager);
		appendReport (tActionReport);
	}

	public void addAction (Action aAction) {
		boolean tAllNullEffects;

		tAllNullEffects = aAction.allNullEffects ();
		if (tAllNullEffects) {
			logger.debug (aAction.getBriefActionReport () + " All Null Effects " + tAllNullEffects
					+ " Last Action Number " + actionNumber);
		} else {
			setNewActionNumber (aAction);
			setAuditAttributes (aAction);
			logger.info ("Local Action # " + actionNumber + " Name " + aAction.getName () + " From "
					+ aAction.getActorName ());
			justAddAction (aAction);
		}
	}

	private void justAddAction (Action aAction) {
		boolean tAppendAction;
		
		actions.add (aAction);
		appendActionReport (aAction);
		tAppendAction = sendActionToNetwork (aAction);
		if (tAppendAction) {
			appendToJGameClient (aAction);
		}
	}

	public boolean sendActionToNetwork (Action aAction) {
		String tXMLFormat;
		boolean tAppendAction;
		
		// Note the 'getNotifyNetwork' in the Game Manager should be tested
		// To prevent Applying Actions from a remote client would also send
		// The action back out to the remote client causing an Infinite Loop
		tAppendAction = false;
		if (gameManager.isNetworkGame () && gameManager.getNotifyNetwork ()) {
			tXMLFormat = aAction.getXMLFormat (JGameClient.EN_GAME_ACTIVITY);
			
			System.out.println (tXMLFormat);
			
			tXMLFormat = tXMLFormat.replaceAll ("\n", "");
			sendGameActivity (tXMLFormat, false);
			tAppendAction = true;
		}
		
		return tAppendAction;
	}

	private void setAuditAttributes (Action aAction) {
		int tTotalCash;

		tTotalCash = gameManager.getTotalCash ();
		aAction.setTotalCash (tTotalCash);
	}

	private void setNewActionNumber (Action aAction) {
		int tActionNumber;

		tActionNumber = generateNewActionNumber ();
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
		for (Action tAction : actions) {
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

		tAction = Action.NO_ACTION;
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
		return getLastAction (PREVIOUS_ACTION);
	}

	public Action getLastAction (int aActionOffset) {
		Action tAction;
		int tLastActionIndex;

		tAction = Action.NO_ACTION;
		if (! actions.isEmpty ()) {
			tLastActionIndex = getLastActionIndex (aActionOffset);
			tAction = getActionAt (tLastActionIndex);
		}

		return tAction;
	}

	public int getLastActionIndex (int aActionOffset) {
		int tLastActionIndex;
		
		tLastActionIndex = getActionCount () - aActionOffset;
		
		return tLastActionIndex;
	}

	public void resendLastActions () {
		ResendLastActionsFrame tResendLastActionsFrame;
		
		if (gameManager.isNetworkGame ()) {
			if (actions.isEmpty ()) {
				System.err.println ("No Actions to resend");
			} else {
				tResendLastActionsFrame = new ResendLastActionsFrame ("Resend Last Actions", this, gameManager);
				tResendLastActionsFrame.resendLastActionsToNet ();
			}
		} else {
			System.err.println ("No need to resend actions unless this is a Network Game");
		}
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
				if (tAction != Action.NO_ACTION) {
					justAddAction (tAction);
					tActionNumber = tAction.getNumber ();
				}
			}
			setActionNumber (tActionNumber);
		} catch (ClassNotFoundException eException) {
			logger.warn ("Class not Found Exception Thrown when trying to get parse Action");
		} catch (Exception e) {
			logger.error ("Error Loading Actions", e);
		}
	}

	private Action getAction (GameManager aGameManager, XMLNode aActionNode) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		String tANodeName;
		String tClassName;
		Action tAction = Action.NO_ACTION;
		Class<?> tActionToLoad;
		Constructor<?> tActionConstructor;

		tANodeName = aActionNode.getNodeName ();
		if (Action.EN_ACTION.equals (tANodeName)) {
			// Use Reflections to identify the Action and call the constructor with the
			// XMLNode and the Game Manager
			tClassName = aActionNode.getThisAttribute (Action.AN_CLASS);
			tActionToLoad = Class.forName (tClassName);
			tActionConstructor = tActionToLoad.getConstructor (aActionNode.getClass (), aGameManager.getClass ());
			tAction = (Action) tActionConstructor.newInstance (aActionNode, aGameManager);
		}

		return tAction;
	}

	public void removeLastAction () {
		Action tLastAction;

		tLastAction = getLastAction ();
		removeActionFromNetwork (tLastAction);
		actions.remove (actions.size () - 1);

		resetLastActionNumber ();
	}

	private void resetLastActionNumber () {
		int tLastActionNumber;
		Action tLastAction;

		tLastAction = getLastAction ();
		if (tLastAction == Action.NO_ACTION) {
			tLastActionNumber = DEFAULT_ACTION_NUMBER;
		} else {
			tLastActionNumber = tLastAction.getNumber ();
		}
		setActionNumber (tLastActionNumber);
	}

	public void removeActionFromNetwork (Action aActionToRemove) {
		if (gameManager.isNetworkGame () && gameManager.getNotifyNetwork ()) {
			actionsToRemove.add (aActionToRemove);
			// Queue up the Action to remove from Network, after all are Undone.
		}
	}

//	public void printLastXActions (List<Action> aActions, int aCount) {
//		int tActionCount;
//		Action tActionToPrint;
//		int tPrintedCount = 0;
//
//		tActionCount = aActions.size ();
//		if (tActionCount > 0) {
//			for (int tIndex = tActionCount; (tIndex > 0) && (tPrintedCount < aCount); tIndex--, tPrintedCount++) {
//				tActionToPrint = aActions.get (tIndex - 1);
//				tActionToPrint.printBriefActionReport ();
//			}
//		} else {
//			System.out.println ("$$$ No Actions in list to print");
//		}
//	}

	public void removeUndoneActionsFromNetwork () {
		String tRemoveActionXML;
		int tActionNumberToRemove;

		if (gameManager.isNetworkGame () && gameManager.getNotifyNetwork ()) {
			for (Action tActionToRemove : actionsToRemove) {
				tActionNumberToRemove = tActionToRemove.getNumber ();
				tRemoveActionXML = "<" + EN_REMOVE_ACTION + " " + Action.AN_NUMBER + "=\"" + tActionNumberToRemove
						+ "\"/>";
				sendGameActivity (tRemoveActionXML, true);
			}
		}
		resetLastActionNumber ();
		actionsToRemove.clear ();
	}

	public boolean undoLastActionNetwork () {
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
			tXMLFormat = tUndoAction.getXMLFormat (JGameClient.EN_GAME_ACTIVITY);
			sendGameActivity (tXMLFormat, false);
			removeActionFromNetwork (tUndoAction); // Queue up Undo to be removed from Network
			appendToJGameClient (tUndoAction);
		}

		return tLastActionUndone;
	}

	public void sendGameActivity (String aXMLFormat, boolean aWrap) {
		JGameClient tNetworkJGameClient;
		String tXMLFormat;

		tXMLFormat = aXMLFormat.replaceAll ("\n", "");
		tNetworkJGameClient = gameManager.getNetworkJGameClient ();
		if (aWrap) {
			tXMLFormat = tNetworkJGameClient.wrapWithGA (tXMLFormat);
		}
		tNetworkJGameClient.sendGameActivity (tXMLFormat);
	}

	// This is called on Local Client Issuing the Undo Action
	public boolean undoLastAction (RoundManager aRoundManager) {
		boolean tLastActionUndone;

		tLastActionUndone = undoLastAction (aRoundManager, true);
		aRoundManager.updateAllFrames ();
		removeUndoneActionsFromNetwork ();
		gameManager.autoSaveGame ();
		
		return tLastActionUndone;
	}

	public boolean undoLastAction (RoundManager aRoundManager, boolean aNotifyNetwork) {
		boolean tLastActionUndone;
		Action tLastAction;

		tLastAction = getLastAction ();
		appendReport ("\nUNDOING: " + tLastAction.getBriefActionReport () + "\n");
//		tLastAction.printBriefActionReport ();			// DEBUGING CONSOLE OUTPUT
		tLastActionUndone = tLastAction.undoAction (aRoundManager);
		if (aNotifyNetwork) {
			undoLastActionNetwork ();
		}
		if (tLastActionUndone) {
			removeLastAction ();
			if (! actions.isEmpty ()) {
				if (tLastAction.getChainToPrevious ()) {
					tLastActionUndone = undoLastAction (aRoundManager, false);
				}
			}
		}

		return tLastActionUndone;
	}

	public boolean wasLastActionStartAuction () {
		Action tLastAction;
		boolean tWasLastActionStartAuction = false;

		tLastAction = getLastAction ();
		tWasLastActionStartAuction = tLastAction.wasLastActionStartAuction ();

		return tWasLastActionStartAuction;
	}

	public boolean isSyncActionNumber (Action aAction) {
		return (aAction instanceof SyncActionNumber);
	}

	public void handleNetworkAction (XMLNode aActionNode) {
		Action tAction;
		int tExpectedActionNumber, tThisActionNumber;
		String tActionFailureMessage;

		// When handling incomming Network Actions, we DO NOT want to notify other
		// clients
		// that they should apply these Actions (one of them is sending it to us)
		// We end up getting into an Infinite Loop between two separate clients
		gameManager.setNotifyNetwork (false);
		try {
			tAction = getAction (gameManager, aActionNode);
			if (tAction != Action.NO_ACTION) {
				tExpectedActionNumber = actionNumber + 1;
				tThisActionNumber = tAction.getNumber ();

//				System.out.println ("----------- Action Number " + actionNumber + " Received " + tThisActionNumber
//						+ " -------------");
//				tAction.printActionReport (roundManager);
				if (isSyncActionNumber (tAction)) {
					setActionNumber (tThisActionNumber);
					justAddAction (tAction);
				} else {
					if ((tThisActionNumber < STARTING_ACTION_NUMBER) || (tThisActionNumber > tExpectedActionNumber)
							|| (tThisActionNumber == tExpectedActionNumber)) {
//						System.out.println ("\nReceived Action Number " + tThisActionNumber
//								+ " the Expected Action Number is " + tExpectedActionNumber + " Processing\n");
						if (tThisActionNumber == tExpectedActionNumber) {
							setActionNumber (tExpectedActionNumber);
						}
						actions.add (tAction);
						logger.info ("Network Action # " + actionNumber + " Name " + tAction.getName () + " From "
								+ tAction.getActor ().getName ());
						applyAction (tAction);
						gameManager.autoSaveGame ();
						// Add the Report of the Action Applied to the Action Frame, and the JGameClient
						// Game Activity Frame
						appendActionReport (tAction);
						appendToJGameClient (tAction);
					} else if (tThisActionNumber <= actionNumber) {
						tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber
								+ " Current Action Number " + actionNumber + " is before the Expected Action Number of "
								+ tExpectedActionNumber + " IGNORING\n";
						logger.error (tActionFailureMessage);
						appendReport (tActionFailureMessage);
					} else {
						tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber
								+ " is not the Expected Action Number of " + tExpectedActionNumber
								+ " This should have Matched\n";
						logger.error (tActionFailureMessage);
						appendReport (tActionFailureMessage);
					}
				}
			} else {
				logger.error ("No Action Found to Process");
			}
		} catch (ClassNotFoundException eException) {
			logger.warn ("Class not Found Exception Thrown when trying to get parse Network Action.");
		} catch (Exception eException) {
			logger.error (eException.getMessage (), eException);
			System.err.println ("Exception Caught");
			eException.printStackTrace ();
		}
		gameManager.setNotifyNetwork (true);
//		Once we are done applying these Actions, we then can reset this back to Notify
//		System.out.println (
//				"----------- Finshed Handling Action, Latest Action Number is " + actionNumber + " -------------");
//		gameManager.setFrameBackgrounds ();
	}

	public void appendToJGameClient (Action aAction) {
		GameManager tGameManager;
		String tSimpleActionReport;

		tGameManager = roundManager.getGameManager ();
		tSimpleActionReport = aAction.getSimpleActionReport ();
		if (!tSimpleActionReport.equals ("")) {
			tGameManager.appendToGameActivity (tSimpleActionReport);
		}
	}

	public boolean applyAction (Action aAction) {
		boolean tActionApplied;
		int tActionNumber;

		tActionApplied = aAction.applyAction (roundManager);
		if (aAction instanceof UndoLastAction) {
			// If this is an UndoLastAction, we don't want to adjust the Action Number, this
			// is handled for undoing the chain of actions.
		} else {
			tActionNumber = aAction.getNumber ();
			if (tActionNumber > actionNumber) {
				setActionNumber (tActionNumber);
			}
		}
		roundManager.updateRoundFrame ();

		return tActionApplied;
	}

	public void showActionReportFrame () {
		actionReportFrame.setVisible (true);
	}

	public void fillAuditFrame (AuditFrame aAuditFrame, String aActorName) {
		Action tAction;
		int tTotalActionCount;
		int tActionIndex;
		int tDebit;
		int tCredit;
		int tActionNumber;
		int tFoundActionCount;
		String tActionEventDescription;
		String tActionName;
		String tRoundID;

		if (aActorName != ActorI.NO_NAME) {
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
							handleAuctionReporting (aAuditFrame, aActorName, tAction, tActionName, tActionNumber);
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
				logger.info ("Examined " + tActionIndex + " Actions found " + tFoundActionCount + " Actions for "
						+ aActorName);
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