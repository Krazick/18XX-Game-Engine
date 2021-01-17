package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.network.JGameClient;
import ge18xx.round.RoundManager;
import ge18xx.toplevel.AuditFrame;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class ActionManager {
	public final static Action NO_ACTION = null;
	public final static int STARTING_ACTION_NUMBER = 100;
	List<Action> actions;
	ActionReportFrame actionReportFrame;
	RoundManager roundManager;
	GameManager gameManager;
	int actionNumber;
	
	public ActionManager (RoundManager aRoundManager) {
		String tFullTitle;
		
		roundManager = aRoundManager;
		gameManager = roundManager.getGameManager ();
		tFullTitle = gameManager.createFrameTitle ("Action Report");
		actions = new LinkedList<Action> ();
		actionReportFrame = new ActionReportFrame (tFullTitle, aRoundManager.getGameName ());
		gameManager.addNewFrame (actionReportFrame);
		setActionNumber (0);
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
	
	public void incrementActionNumber () {
		String tReportActionNumber;
		
		tReportActionNumber = "Increment Action Number from " + actionNumber + " to " + (actionNumber + 1) + "\n";
		actionNumber++;
		actionReportFrame.append (tReportActionNumber);
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
		actionReportFrame.append ("\n\n----------------------\n");
		actionReportFrame.append (aReport);
		actionReportFrame.append ("\n----------------------\n");
	}
	
	private void appendToReportFrame (Action aAction) {
		actionReportFrame.append ("\n\n" + aAction.getActionReport (roundManager));
	}
	
	public void addAction (Action aAction) {
		setNewActionNumber (aAction);
		setAuditAttributes (aAction);
		justAddAction (aAction);
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
		
		incrementActionNumber ();
		tActionNumber = getActionNumber ();
		aAction.setNumber (tActionNumber);
	}
	
	public void briefActionReport () {
		int tActionCount;
		
		tActionCount = getActionCount ();
		if (tActionCount == 1) {
			System.out.println ("There is ONE Action.");
		} else {
			System.out.println ("There are " + getActionCount () + " Actions.");
		}
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
		int tActionNodeCount, tActionIndex;
		Action tAction;
		
		tActionChildren = aActionsNode.getChildNodes ();
		tActionNodeCount = tActionChildren.getLength ();
		try {
			for (tActionIndex = 0; tActionIndex < tActionNodeCount; tActionIndex++) {
				tActionNode = new XMLNode (tActionChildren.item (tActionIndex));
				tAction = getAction (aGameManager, tActionNode);
				if (tAction != NO_ACTION) {
					justAddAction (tAction);
				}
			}
			appendAllActions();
		} catch (Exception e) {
			System.err.println (e.getMessage ());
			e.printStackTrace();
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
	
	public boolean undoLastAction (RoundManager aRoundManager) {
		boolean tLastActionUndone;
		Action tLastAction;

		tLastAction = getLastAction ();
		actionReportFrame.append ("\n\nUNDOING: " + tLastAction.getBriefActionReport ());
		tLastAction.printBriefActionReport ();
		tLastActionUndone = tLastAction.undoAction (aRoundManager);
		if (tLastActionUndone) {
			removeLastAction ();
			if (tLastAction.getChainToPrevious ()) {
				tLastActionUndone = undoLastAction (aRoundManager);
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
	
	public void handleNetworkAction (XMLNode aXMLGameActivityNode) {
		Action tAction;
		XMLNode tActionNode;
		NodeList tActionChildren;
		int tActionNodeCount, tActionIndex;
		int tExpectedActionNumber, tThisActionNumber;
		String tANodeName;
		String tActionFailureMessage;
		
		// When handling incomming Network Actions, we DO NOT want to notify other clients
		// that they should apply these Actions (one of them is sending it to us)
		// We end up getting into an Infinite Loop between two separate clients 
		gameManager.setNotifyNetwork (false); 
		tANodeName = aXMLGameActivityNode.getNodeName ();
		if (JGameClient.EN_GAME_ACTIVITY.equals (tANodeName)) {
			tActionChildren = aXMLGameActivityNode.getChildNodes ();
			tActionNodeCount = tActionChildren.getLength ();
			try {
				for (tActionIndex = 0; tActionIndex < tActionNodeCount; tActionIndex++) {
					tExpectedActionNumber = actionNumber + 1;
					tActionNode = new XMLNode (tActionChildren.item (tActionIndex));
					tAction = getAction (gameManager, tActionNode);
					tThisActionNumber = tAction.getNumber ();
					System.out.println ("----------- Action Index " + tActionIndex + 
							" -- Number " + actionNumber + " Received " + tThisActionNumber + " -------------");
					tAction.printActionReport (roundManager);
					if (isSyncActionNumber (tAction)) {
						setActionNumber (tThisActionNumber);
						justAddAction (tAction);
					} else {
						if ((tThisActionNumber < STARTING_ACTION_NUMBER) ||
							(tThisActionNumber == tExpectedActionNumber)) {
							System.out.println ("\nReceived Action Number " + tThisActionNumber + 
									" the Expected Action Number is " + tExpectedActionNumber + " Processing\n");
							if (tThisActionNumber == tExpectedActionNumber) {
								setActionNumber (tExpectedActionNumber);
							}
							actions.add (tAction);
							applyAction (tAction);
											
							// Add the Report of the Action Applied to the Action Frame, and the JGameClient Game Activity Frame
							appendToReportFrame (tAction);
							appendToJGameClient (tAction);
						} else if (tThisActionNumber <= actionNumber) {
							tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber + 
									" is before the Expected Action Number of " + tExpectedActionNumber + " IGNORING\n";
							System.err.println (tActionFailureMessage);
							actionReportFrame.append (tActionFailureMessage);
						} else if (tThisActionNumber > tExpectedActionNumber) {
							tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber + 
									" is after the Expected Action Number of " + tExpectedActionNumber + " THERE IS A GAP\n";
							System.err.println (tActionFailureMessage);						
							actionReportFrame.append (tActionFailureMessage);
						} else {
							tActionFailureMessage = "\nReceived Action Number " + tThisActionNumber + 
									" is not the Expected Action Number of " + tExpectedActionNumber + " This should have Matched\n";
							System.err.println (tActionFailureMessage);
						}
					}
				}
			} catch (Exception tException) {
				System.err.println (tException.getMessage ());
				tException.printStackTrace ();
			}
		} else {
			System.err.println ("XML Document does not have <GA> Tag Set");
		}
		gameManager.setNotifyNetwork (true);
		// Once we are done applying these Actions, we then can reset this back to Notify

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
		
//		actionReportFrame.append ("\n\nApplying: " + aAction.getBriefActionReport ());
//		aAction.printBriefActionReport ();
		tActionApplied = aAction.applyAction (roundManager);
		tActionNumber = aAction.getNumber ();
		if (tActionNumber > actionNumber) {
			setActionNumber (tActionNumber);
		}
		roundManager.updateRoundFrame ();
		
		return tActionApplied;
	}

	public void showFrame () {
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
						if (tAction.effectsForActorAreCash (aActorName)) {
							tFoundActionCount++;
							tActionNumber = tAction.getNumber ();
							tActionName = tAction.getName ();
							
							tActionEventDescription = tActionName + ": " + tAction.getSimpleActionReport ();
							tDebit = tAction.getEffectDebit (aActorName);
							tCredit = tAction.getEffectCredit (aActorName);
							tRoundID = tAction.getRoundType ().toAbbrev () + " " + tAction.getRoundID ();
							aAuditFrame.addRow (tActionNumber, tRoundID, tActionEventDescription, tDebit, tCredit);
						}
					}
				}
				System.out.println ("Examined " + tActionIndex + " Actions found " + tFoundActionCount + " Actions for " + aActorName);
			}
		}
	}
}