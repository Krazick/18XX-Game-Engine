package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.network.JGameClient;
import ge18xx.round.RoundManager;
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
	List<Action> actions;
	ActionReportFrame actionReportFrame;
	RoundManager roundManager;
	GameManager gameManager;
	
	public ActionManager (RoundManager aRoundManager) {
		String tFullTitle;
		
		roundManager = aRoundManager;
		gameManager = roundManager.getGameManager ();
		tFullTitle = gameManager.createFrameTitle ("Action Report");
		actions = new LinkedList<Action> ();
		actionReportFrame = new ActionReportFrame (tFullTitle, aRoundManager.getGameName ());
		gameManager.addNewFrame (actionReportFrame);
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
					addAction (tAction);
				}
			}
			appendAllActions();
		} catch (Exception e) {
			System.out.println (e.getMessage ());
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

	public void handleNetworkAction (XMLNode aXMLGameActivityNode) {
		Action tAction;
		XMLNode tActionNode;
		NodeList tActionChildren;
		int tActionNodeCount, tActionIndex;
		String tANodeName;
		
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
					tActionNode = new XMLNode (tActionChildren.item (tActionIndex));
					tAction = getAction (gameManager, tActionNode);
					actions.add (tAction);
					applyAction (tAction);
										
					// Add the Report of the Action Applied to the Action Frame, and the JGameClient Game Activity Frame
					appendToReportFrame (tAction);
					appendToJGameClient (tAction);
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
		
		actionReportFrame.append ("\n\nApplying: " + aAction.getBriefActionReport ());
		aAction.printBriefActionReport ();
		tActionApplied = aAction.applyAction (roundManager);
		roundManager.updateRoundFrame ();
		
		return tActionApplied;
	}

	public void showFrame () {
		actionReportFrame.setVisible (true);
	}
}