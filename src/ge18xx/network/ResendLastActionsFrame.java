package ge18xx.network;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import ge18xx.game.GameManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionManager;
import ge18xx.toplevel.XMLFrame;

public class ResendLastActionsFrame extends XMLFrame {

	private static final long serialVersionUID = 1L;
	private ActionManager actionManager;
	JButton confirmResendButton;
	JButton cancelButton;
	JLabel summaryToResendLabel;
	JLabel networkLastActionLabel;
	JTextArea listToResendTextArea;
	String summary;
	String networkLastAction;

	public ResendLastActionsFrame (String aFrameName, ActionManager aActionManager) {
		super (aFrameName);
		actionManager = aActionManager;
	}

	public ResendLastActionsFrame (String aFrameName, String aGameName, ActionManager aActionManager) {
		super (aFrameName, aGameName);
		actionManager = aActionManager;
	}

	private void buildComponents () {
		summaryToResendLabel = new JLabel (summary);
		networkLastActionLabel = new JLabel (networkLastAction);
		confirmResendButton = new JButton ("Confirm Resend");
		
	}
	public void resendLastActionsToNet () {
		Action tAction;
		int tNetworkLastActionNumber;
		int tLastActionIndex;
		int tFromNumber;
		int tToNumber;
		String tFromName;
		String tToName;
		List<Action> tResendTheseActions;
		boolean tGetActionsToResend;
		
		tResendTheseActions = new LinkedList<Action> ();
		tLastActionIndex = actionManager.getLastActionIndex (ActionManager.PREVIOUS_ACTION);
		tGetActionsToResend = true;
		tAction = actionManager.getLastAction ();
		tToNumber = tAction.getNumber ();
		tFromNumber = tToNumber;
		tToName = tAction.getActorName ();
		tFromName = tToName;
		while (tGetActionsToResend) {
			tAction = actionManager.getActionAt (tLastActionIndex);
			if (tAction != Action.NO_ACTION) {
				tFromNumber = tAction.getNumber ();
				tFromName = tAction.getActorName ();
				tResendTheseActions.add (tAction);
				tGetActionsToResend = tAction.getChainToPrevious ();	
				tLastActionIndex--;
			} else {
				tGetActionsToResend = false;
			}
		}
		tNetworkLastActionNumber = getNetworkLastActionNumber ();
		summary = "Ready to resend " + tResendTheseActions.size () + " Actions from " + tFromNumber + 
				" (" + tFromName + ") " + " to " + tToNumber + " (" + tToName + ")";
		networkLastAction = "Network's Last Action for this game is " + tNetworkLastActionNumber;
	}

	public int getNetworkLastActionNumber () {
		String tLastAction;
		int tNetworkLastActionNumber;
		String LAST_ACTION_NUMBER = "<LastAction actionNumber=\"(\\d+)\"";
		Pattern REQUEST_ACTION_PATTERN = Pattern.compile (LAST_ACTION_NUMBER);
		Matcher tMatcher;
		GameManager tGameManager;

		tGameManager = actionManager.getGameManager ();
		tNetworkLastActionNumber = 0;
		if (tGameManager.isNetworkGame ()) {
			tLastAction = tGameManager.requestGameSupport (JGameClient.REQUEST_LAST_ACTION);
			System.out.println ("Action Number Request Response " + tLastAction);
			tMatcher = REQUEST_ACTION_PATTERN.matcher (tLastAction);
			if (tMatcher.find ()) {
				tNetworkLastActionNumber = Integer.parseInt (tMatcher.group (1));
			}
		}

		return tNetworkLastActionNumber;
	}

}
