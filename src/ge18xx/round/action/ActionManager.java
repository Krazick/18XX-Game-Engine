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
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLSaveGameI;

public class ActionManager implements XMLSaveGameI {
	public final static ActionManager NO_ACTION_MANAGER = null;
	public final static AttributeName AN_PREVIOUS_CHECKSUM = new AttributeName ("previousChecksum");
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
		actionNumber = aNumber;
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
		Matcher tMatcher;
		String tFoundNewNumber;
		int tNewNumber;

		tMatcher = ACTION_NUMBER_PATTERN.matcher (aResponse);
		tFoundNewNumber = "NOID";
		tNewNumber = 0;
		if (tMatcher.find ()) {
			tFoundNewNumber = tMatcher.group (1);
			tNewNumber = Integer.parseInt (tFoundNewNumber);
		}

		return tNewNumber;
	}
	
	public int generateNewActionNumber (boolean aUndoAction) {
		String tReportActionNumber;
		String tActionNumberString;
		int tNewActionNumber;

		if (gameManager.isNetworkGame ()) {
			tActionNumberString = gameManager.requestGameSupport (JGameClient.REQUEST_ACTION_NUMBER);
			tNewActionNumber = getActionNumberFrom (tActionNumberString);
			if (tNewActionNumber > 0) {
				actionNumber = tNewActionNumber;
				tReportActionNumber = "\nRetrieved New Action Number " + actionNumber + " from Game Server\n";
			} else {
				actionNumber++;
				tReportActionNumber = "FAILED to retrieve New Action Number from Game Server\n";
			}
			if (! aUndoAction) {
				appendReport (tReportActionNumber);
			}
		} else {
			tReportActionNumber = "Increment Action Number from " + actionNumber + " to " + (actionNumber + 1) + GUI.NEWLINE;
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

		tActionReport = GUI.NEWLINE + aAction.getActionReport (roundManager);
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
			tXMLFormat = tXMLFormat.replaceAll (GUI.NEWLINE, GUI.EMPTY_STRING);
			sendGameActivity (tXMLFormat, false);
			tAppendAction = true;
		}
		
		return tAppendAction;
	}

	public void setNewActionNumber (Action aAction) {
		setNewActionNumber (aAction, false);
	}

	private void setNewActionNumber (Action aAction, boolean aUndoAction) {
		int tActionNumber;

		tActionNumber = generateNewActionNumber (aUndoAction);
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

	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		XMLElement tActionElements;
		XMLElement tActionElement;
		String tPreviousChecksum;
		int tPreviousChecksumIndex;
		
		tActionElements = aXMLDocument.createElement (aEN_Type);
		tPreviousChecksumIndex = gameManager.getPreviousChecksumCount () - 1;
		tPreviousChecksum = gameManager.getPreviousChecksum (tPreviousChecksumIndex);
		tActionElements.setAttribute (AN_PREVIOUS_CHECKSUM, tPreviousChecksum);
		for (Action tAction : actions) {
			tActionElement = tAction.getActionElement (aXMLDocument);
			tActionElements.appendChild (tActionElement, ! XMLElement.ADD_CHECKSUM);
		}

		return tActionElements;
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

	public Action getActionWithNumber (int aActionNumber) {
		Action tAction;
		int tActionCount;
		int tActionIndex;
		
		tActionCount = getActionCount ();
		for (tActionIndex = 0; tActionIndex < tActionCount; tActionIndex++) {
			tAction = getActionAt (tActionIndex);
			if (tAction.getNumber () == aActionNumber) {
				return tAction;
			}
		}
		
		return Action.NO_ACTION;
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

	public int getLastActionIndex () {
		return getLastActionIndex (PREVIOUS_ACTION);
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
		int tActionNodeCount;
		int tActionIndex;
		int tActionNumber;
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
		Action tAction;
		Class<?> tActionToLoad;
		Class<?> tGameManagerClass;
		Class<?> tActionNodeClass;
		Constructor<?> tActionConstructor;

		tAction = Action.NO_ACTION;
		tANodeName = aActionNode.getNodeName ();
		if (Action.EN_ACTION.equals (tANodeName)) {
			// Use Reflections to identify the Action and call the constructor with the
			// XMLNode and the Game Manager
			tClassName = aActionNode.getThisAttribute (Action.AN_CLASS);
			tActionToLoad = Class.forName (tClassName);
			tActionNodeClass = aActionNode.getClass ();
			tGameManagerClass = aGameManager.getClass ();
			tActionConstructor = tActionToLoad.getConstructor (tActionNodeClass, tGameManagerClass);
			tAction = (Action) tActionConstructor.newInstance (aActionNode, aGameManager);
		}

		return tAction;
	}

	public void removeLastAction () {
		Action tLastAction;
		int tActionIndex;
		
		tLastAction = getLastAction ();
		removeActionFromNetwork (tLastAction);
		tActionIndex = actions.size () - 1;
		if (gameManager.isNetworkGame ()) {
			// Need to remove Last Checksum Action
			if (! (tLastAction instanceof UndoLastAction)) {
				gameManager.removeLastChecksum ();
			}
		}
		actions.remove (tActionIndex);
		
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
		}
	}

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
		ActorI tActor;
		Action tUndoAction;
		Action tLastAction;
		ActionStates tRoundType;
		String tRoundID;
		String tXMLFormat;
		boolean tLastActionUndone;

		tLastActionUndone = true;
		if (gameManager.isNetworkGame () && gameManager.getNotifyNetwork ()) {
			tLastAction = getLastAction ();
			tActor = tLastAction.getActor ();
			tRoundType = tLastAction.getRoundState ();
			tRoundID = tLastAction.getRoundID ();
			tUndoAction = new UndoLastAction (tRoundType, tRoundID, tActor);
			setNewActionNumber (tUndoAction, true);
			tXMLFormat = tUndoAction.getXMLFormat (JGameClient.EN_GAME_ACTIVITY);
			sendGameActivity (tXMLFormat, false);
			removeActionFromNetwork (tUndoAction); // Queue up Undo to be removed from Network
			appendToJGameClient (tUndoAction);
		}

		return tLastActionUndone;
	}

	// This is called on Local Client Issuing the Undo Action
	public boolean undoLastAction (RoundManager aRoundManager) {
		boolean tLastActionUndone;

		tLastActionUndone = undoLastAction (aRoundManager, true);
		aRoundManager.updateAllFrames ();
		removeUndoneActionsFromNetwork ();
		gameManager.autoSaveGame (! GameManager.ADD_CHECKSUM);
		
		return tLastActionUndone;
	}

	public boolean undoLastAction (RoundManager aRoundManager, boolean aNotifyNetwork) {
		boolean tLastActionUndone;
		Action tLastAction;

		tLastAction = getLastAction ();
		if (! (tLastAction instanceof UndoLastAction)) {
			appendReport ("\nUNDOING: " + tLastAction.getBriefActionReport () + GUI.NEWLINE);
		}
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

	public void sendGameActivity (String aXMLFormat, boolean aWrap) {
		JGameClient tNetworkJGameClient;
		String tXMLFormat;

		tXMLFormat = aXMLFormat.replaceAll (GUI.NEWLINE, "");
		tNetworkJGameClient = gameManager.getNetworkJGameClient ();
		if (aWrap) {
			tXMLFormat = tNetworkJGameClient.wrapWithGA (tXMLFormat);
		}
		tNetworkJGameClient.sendGameActivity (tXMLFormat);
	}

	public boolean wasLastActionStartAuction () {
		Action tLastAction;
		boolean tWasLastActionStartAuction;

		tWasLastActionStartAuction = false;
		tLastAction = getLastAction ();
		tWasLastActionStartAuction = tLastAction.wasLastActionStartAuction ();

		return tWasLastActionStartAuction;
	}

	public boolean isSyncActionNumber (Action aAction) {
		return (aAction instanceof SyncActionNumber);
	}

	public void handleNetworkAction (XMLNode aActionNode) {
		Action tAction;
		int tExpectedActionNumber;
		int tThisActionNumber;
		boolean tAddChecksum;
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

				if (isSyncActionNumber (tAction)) {
					setActionNumber (tThisActionNumber);
					justAddAction (tAction);
				} else {
					if ((tThisActionNumber < STARTING_ACTION_NUMBER) || 
						(tThisActionNumber > tExpectedActionNumber) || 
						(tThisActionNumber == tExpectedActionNumber)) {
						if (tThisActionNumber == tExpectedActionNumber) {
							setActionNumber (tExpectedActionNumber);
						}
						if (tAction instanceof UndoLastAction) {
							tAddChecksum = (! GameManager.ADD_CHECKSUM);
						} else {
							tAddChecksum = GameManager.ADD_CHECKSUM;
						}
						applyAction (tAction, tAddChecksum);
						// Add the Report of the Action Applied to the Action Frame, and the JGameClient
						// Game Activity Frame
						if (! (tAction instanceof UndoLastAction)) {
							appendActionReport (tAction);
						}
						appendToJGameClient (tAction);
					} else if (tThisActionNumber <= actionNumber) {
						tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber
								+ " Current Action Number " + actionNumber + " is before the Expected Action Number of "
								+ tExpectedActionNumber + " IGNORING\n";
						logger.error (tActionFailureMessage);
						logger.error ("\nRecieved: " + tAction.getActionReport (roundManager));
						appendReport (tActionFailureMessage);
					} else {
						tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber
								+ " is not the Expected Action Number of " + tExpectedActionNumber
								+ " This should have Matched\n";
						logger.error (tActionFailureMessage);
						logger.error ("\nRecieved: " + tAction.getActionReport (roundManager));
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
	}

	public void applyAction (Action aAction, boolean aAddChecksum) {
		actions.add (aAction);
		logger.info ("Network Action # " + actionNumber + " Name " + aAction.getName () + " From "
				+ aAction.getActor ().getName () + " Add Checksum " + aAddChecksum);
		applyAction (aAction);
		gameManager.autoSaveGame (aAddChecksum);
	}

	public void appendToJGameClient (Action aAction) {
		GameManager tGameManager;
		String tSimpleActionReport;

		tGameManager = roundManager.getGameManager ();
		tSimpleActionReport = aAction.getSimpleActionReport ();
		if (!tSimpleActionReport.equals (GUI.EMPTY_STRING)) {
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
							tRoundID = tAction.getRoundState ().toAbbrev () + " " + tAction.getRoundID ();
							aAuditFrame.addRow (tActionNumber, tRoundID, tActionEventDescription, tDebit, tCredit);
						}
					}
				}
				logger.info ("Examined " + tActionIndex + " Actions found " + tFoundActionCount + " Actions for "
						+ aActorName);
			}
		}
	}

	private void handleAuctionReporting (AuditFrame aAuditFrame, String aActorName, Action aAction, 
				String aActionName, int aActionNumber) {
		int tDebit;
		int tCredit;
		String tRoundID;
		String tAuctionWinner;
		String tActionEventDescription;

		tActionEventDescription = aActionName + ": " + aAction.getSimpleActionReport (aActorName);
		tDebit = 0;
		tCredit = aAction.getEffectCredit (aActorName);
		tRoundID = aAction.getRoundState ().toAbbrev () + " " + aAction.getRoundID ();
		aAuditFrame.addRow (aActionNumber, tRoundID, tActionEventDescription, tDebit, tCredit);
		tAuctionWinner = aAction.getAuctionWinner ();
		if (aActorName.equals (tAuctionWinner)) {
			tActionEventDescription = aActionName + ": " + aAction.getSimpleActionReport ();
			aAuditFrame.addRow (aActionNumber, tRoundID, tActionEventDescription, tCredit, tDebit);
		}
	}
	
	public boolean isLastActionComplete () {
		boolean tIsLastActionComplete;

		tIsLastActionComplete = true;
		if (gameManager.isNetworkGame ()) {
//			tLastActionComplete = gameManager.requestGameSupport (JGameClient.REQUEST_LAST_ACTION_COMPLETE);
		} else {
			tIsLastActionComplete = true;
		}

		return tIsLastActionComplete;
	}
}