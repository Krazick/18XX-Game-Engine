package ge18xx.network;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionManager;
import ge18xx.toplevel.XMLFrame;

public class ResendLastActionsFrame extends XMLFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String CONFIRM_RESEND = "Confirm Resend Actions";
	private static final String CANCEL = "Cancel";
	private final static String NEWLINE = "\n";
	private ActionManager actionManager;
	JButton confirmResendButton;
	JButton cancelButton;
	JLabel summaryToResendLabel;
	JLabel networkLastActionLabel;
	JTextArea listToResendTextArea;
	JScrollPane resendActionsScrollPane;
	JPanel resendConfirmPanel;
	String summary;
	String networkLastAction;
	List<Action> resendTheseActions;

	public ResendLastActionsFrame (String aFrameName, ActionManager aActionManager, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		actionManager = aActionManager;
	}

	public ResendLastActionsFrame (String aFrameName, String aGameName, ActionManager aActionManager, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		actionManager = aActionManager;
	}

	private void buildResendPanel () {
		int padding1;
		Point tRoundFrameOffset;
		GameManager tGameManager;
		
		padding1 = 10;
		buildComponents ();
		resendConfirmPanel.setBorder (BorderFactory.createEmptyBorder (padding1, padding1, padding1, padding1));
		resendConfirmPanel.setLayout (new BoxLayout (resendConfirmPanel, BoxLayout.Y_AXIS));
		resendConfirmPanel.add (Box.createVerticalGlue ());
		resendConfirmPanel.add (summaryToResendLabel);
		resendConfirmPanel.add (Box.createVerticalGlue ());
		resendConfirmPanel.add (networkLastActionLabel);
		resendConfirmPanel.add (Box.createVerticalGlue ());
		resendConfirmPanel.add (resendActionsScrollPane);
		resendConfirmPanel.add (Box.createVerticalGlue ());
		resendConfirmPanel.add (confirmResendButton);
		resendConfirmPanel.add (Box.createVerticalGlue ());
		resendConfirmPanel.add (cancelButton);
		resendConfirmPanel.add (Box.createVerticalStrut (10));
		add (resendConfirmPanel);
		setPreferredSize (new Dimension (700, 250));
		tGameManager = actionManager.getGameManager ();
		tRoundFrameOffset = tGameManager.getOffsetRoundFrame ();
		setLocation (tRoundFrameOffset);
		pack ();
		setVisible (true);
	}

	private void buildComponents () {
		resendConfirmPanel = new JPanel ();
		summaryToResendLabel = new JLabel (summary);
		summaryToResendLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		networkLastActionLabel = new JLabel (networkLastAction);
		networkLastActionLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		confirmResendButton = setupButton (CONFIRM_RESEND, CONFIRM_RESEND, this, Component.CENTER_ALIGNMENT);
		cancelButton = setupButton (CANCEL, CANCEL, this, Component.CENTER_ALIGNMENT);
		
		listToResendTextArea = new JTextArea ();
		resendActionsScrollPane = new JScrollPane ();
		resendActionsScrollPane.setAutoscrolls (true);
		resendActionsScrollPane.setViewportBorder (null);
		resendActionsScrollPane.setViewportView (listToResendTextArea);
		resendActionsScrollPane.setPreferredSize (new Dimension (680, 120));
	}
	
	public void resendLastActionsToNet () {
		Action tAction;
		int tNetworkLastActionNumber;
		int tLastActionIndex;
		int tFromNumber;
		int tToNumber;
		String tActorName;
		String tFullList;
		String tLastActorName;
		boolean tGetActionsToResend;

		resendTheseActions = new LinkedList<Action> ();
		tLastActionIndex = actionManager.getLastActionIndex (ActionManager.PREVIOUS_ACTION);
		tGetActionsToResend = true;
		tAction = actionManager.getLastAction ();
		tToNumber = tAction.getNumber ();
		tFromNumber = tToNumber;
		tActorName = tAction.getActorName ();
		tLastActorName = tActorName;
		tFullList = "";
		while (tGetActionsToResend) {
			tAction = actionManager.getActionAt (tLastActionIndex);
			if (tAction != Action.NO_ACTION) {
				tFromNumber = tAction.getNumber ();
				tActorName = tAction.getActorName ();
				resendTheseActions.add (0, tAction);

				tFullList = tAction.getBriefActionReport () + NEWLINE + tFullList;
				tGetActionsToResend = tAction.getChainToPrevious ();	
				tLastActionIndex--;
			} else {
				tGetActionsToResend = false;
			}
		}
		tNetworkLastActionNumber = getNetworkLastActionNumber ();
		summary = "Ready to resend " + resendTheseActions.size () + " Actions from " + tFromNumber + 
				" (" + tActorName + ") " + " to " + tToNumber + " (" + tLastActorName + ")";
		networkLastAction = "Network's Last Action for this game is " + tNetworkLastActionNumber;
		buildResendPanel ();
		updateResendConfirmButton (tActorName);
		listToResendTextArea.setText (tFullList);
	}

	public void updateResendConfirmButton (String aActorName) {
		String tClientName;
		String tPlayerName;
		String tActingParty;
		Corporation tCorporation;
		GameManager tGameManager;
		
		tClientName = gameManager.getClientUserName ();
		tGameManager = actionManager.getGameManager ();
		tCorporation = tGameManager.getActingCorporationByName (aActorName);
		if (tCorporation == Corporation.NO_CORPORATION) {
			tPlayerName = aActorName;
			tActingParty = tPlayerName;
		} else {
			tPlayerName = tCorporation.getPresidentName ();
			tActingParty = tPlayerName + " (President of " + aActorName + ")";
		}
		if (tPlayerName.equals (tClientName)) {
			confirmResendButton.setEnabled (true);
			confirmResendButton.setToolTipText ("Actor of Action is " + tActingParty + " can now resend the Actions.");
		} else {
			confirmResendButton.setEnabled (false);
			confirmResendButton.setToolTipText ("Actor of Action is " + tActingParty + " who is not you " + tClientName + ".");
		}
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
			tMatcher = REQUEST_ACTION_PATTERN.matcher (tLastAction);
			if (tMatcher.find ()) {
				tNetworkLastActionNumber = Integer.parseInt (tMatcher.group (1));
			}
		}

		return tNetworkLastActionNumber;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tEventAction;

		tEventAction = aEvent.getActionCommand ();
		if (CONFIRM_RESEND.equals (tEventAction)) {
			for (Action tAction : resendTheseActions ) {
				actionManager.sendActionToNetwork (tAction);
			}
			setVisible (false);
		}
		if (CANCEL.equals (tEventAction)) {
			setVisible (false);
		}
	}
}
