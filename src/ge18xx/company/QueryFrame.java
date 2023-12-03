package ge18xx.company;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.ResponseOfferAction;
import ge18xx.round.action.effects.ToEffect;
import swingDelays.KButton;

public class QueryFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String ACCEPT_OFFER = "Accept";
	private static final String REJECT_OFFER = "Reject";
	KButton acceptButton;
	KButton rejectButton;
	RoundManager roundManager;
	ToEffect toEffect;
	JPanel offerPanel;
	JPanel offerTopPanel;
	JPanel offerButtonPanel;

	public QueryFrame (RoundManager aRoundManager, ToEffect aToEffect) {
		super ();

		Point tNewPoint;

		roundManager = aRoundManager;
		setToEffect (aToEffect);
		setOfferTopPanel ();
		if (offerTopPanel != null) {
			buildOfferButtonPanel ();

			buildOfferPanel ();

			pack ();
			tNewPoint = roundManager.getOffsetRoundFrame ();
			setLocation (tNewPoint);
			setSize (500, 150);
			setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
			setAlwaysOnTop (true);
			setVisible (false);
		} else {
			System.err.println ("Offer Top Panel not properly built");
		}
	}

	public void setToEffect (ToEffect aToEffect) {
		toEffect = aToEffect;
	}

	protected void setOfferTopPanel () {

	}

	public void setAcceptButtonLabel (String aAcceptLabel) {
		acceptButton.setText (aAcceptLabel);
	}

	public void setRejectButtonLabel (String aRejectLabel) {
		rejectButton.setText (aRejectLabel);
	}

	private void buildOfferPanel () {
		offerPanel = new JPanel ();
		offerPanel.add (offerTopPanel);
		offerPanel.setLayout (new BoxLayout (offerPanel, BoxLayout.Y_AXIS));
		offerPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		offerPanel.add (offerButtonPanel);
		offerPanel.setBackground (Color.ORANGE);
		add (offerPanel);
	}

	private void buildOfferButtonPanel () {
		offerButtonPanel = new JPanel ();
		offerButtonPanel.setLayout (new BoxLayout (offerButtonPanel, BoxLayout.X_AXIS));
		offerButtonPanel.setAlignmentY (Component.CENTER_ALIGNMENT);

		acceptButton = buildButton (ACCEPT_OFFER, ACCEPT_OFFER);
		rejectButton = buildButton (REJECT_OFFER, REJECT_OFFER);
		offerButtonPanel.add (rejectButton);
		offerButtonPanel.add (Box.createHorizontalStrut (10));
		offerButtonPanel.add (acceptButton);
		offerButtonPanel.setBackground (Color.ORANGE);
	}

	protected void buildOfferTopPanel (String aOfferLine) {
		buildOfferTopPanel (aOfferLine, null);
	}

	protected void buildOfferTopPanel (String aOfferLine1, String aOfferLine2) {
		JLabel tOfferLabel1;
		JLabel tOfferLabel2;

		tOfferLabel1 = new JLabel (aOfferLine1);
		tOfferLabel1.setAlignmentX (CENTER_ALIGNMENT);

		if (aOfferLine2 != null) {
			tOfferLabel2 = new JLabel (aOfferLine2);
			tOfferLabel2.setAlignmentX (CENTER_ALIGNMENT);
		} else {
			tOfferLabel2 = null;
		}

		offerTopPanel = new JPanel ();
		offerTopPanel.add (Box.createVerticalStrut (10));
		offerTopPanel.setLayout (new BoxLayout (offerTopPanel, BoxLayout.Y_AXIS));
		offerTopPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		offerTopPanel.add (tOfferLabel1);
		offerTopPanel.add (Box.createVerticalStrut (10));
		if (tOfferLabel2 != null) {
			offerTopPanel.add (tOfferLabel2);
			offerTopPanel.add (Box.createVerticalStrut (10));
		}
		offerTopPanel.setBackground (Color.ORANGE);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		String tActionCommand;

		tActionCommand = e.getActionCommand ();
		if (tActionCommand.equals (ACCEPT_OFFER)) {
			sendOfferResponseAction (true);
		}
		if (tActionCommand.equals (REJECT_OFFER)) {
			sendOfferResponseAction (false);
		}
	}

	public void sendOfferResponseAction (boolean aResponse) {
		ResponseOfferAction tResponseOfferAction;
		ActionStates tRoundType;
		String tRoundID = "";
		ActorI tToActor, tFromActor;

		tRoundType = roundManager.getCurrentRoundType ();
		if (tRoundType == ActionStates.OperatingRound) {
			tRoundID = roundManager.getOperatingRoundID ();
		} else if (tRoundType == ActionStates.StockRound) {
			tRoundID = "" + roundManager.getStockRoundID ();
		}

		// Need to find the original Actor who sent the Purchase Offer, to send back to

		tToActor = toEffect.getActor ();

		// Need to find the current Actor (who was sent the Purchase offer) to state who
		// it comes from

		tFromActor = toEffect.getToActor ();
		tResponseOfferAction = new ResponseOfferAction (tRoundType, tRoundID, tFromActor);
		tResponseOfferAction.setChainToPrevious (true);
		addResponseOfferEffect (tResponseOfferAction, tFromActor, tToActor, aResponse);
		roundManager.addAction (tResponseOfferAction);

		setVisible (false);
	}

	protected void addResponseOfferEffect (ResponseOfferAction aResponseOfferAction, ActorI aFromActor,
			ActorI aToActor, boolean aResponse) {

	}

	public KButton buildButton (String aButtonLabel, String aActionCommand) {
		KButton tActionButton;

		tActionButton = new KButton (aButtonLabel);
		tActionButton.setAlignmentX (CENTER_ALIGNMENT);
		tActionButton.setActionCommand (aActionCommand);
		tActionButton.addActionListener (this);

		return tActionButton;
	}
}
