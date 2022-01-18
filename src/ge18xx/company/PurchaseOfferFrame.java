package ge18xx.company;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.ResponseOfferAction;
import ge18xx.round.action.effects.PurchaseOfferEffect;

public class PurchaseOfferFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String ACCEPT_ACTION = "Accept";
	private static final String REJECT_ACTION = "Reject";
	JButton acceptButton;
	JButton rejectButton;
	PurchaseOfferEffect purchaseOfferEffect;
	RoundManager roundManager;
	JPanel offerPanel;
	JPanel offerTopPanel;
	JPanel offerButtonPanel;
	String itemType;
	String itemName;
	
	public PurchaseOfferFrame (PurchaseOfferEffect aPurchaseOfferEffect, RoundManager aRoundManager,
			String aItemType, String aItemName) {
		super ("Purchase Offer");
		
		String tPlayerName;
		Point tNewPoint;
		
		purchaseOfferEffect = aPurchaseOfferEffect;
		roundManager = aRoundManager;
		tPlayerName = aRoundManager.getClientUserName ();
		setTitle ("Purchase Offer for " + tPlayerName);
		
		setOfferTopPanel (aPurchaseOfferEffect);
		setOfferButtonPanel ();
		
		buildOfferPanel ();
		
		setItemType (aItemType);
		setItemName (aItemName);
		pack ();
		tNewPoint = roundManager.getOffsetRoundFrame ();
		setLocation (tNewPoint);
		setSize (500, 150);
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
		setVisible (false);
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

	public String getItemType () {
		return itemType;
	}
	
	public String getItemName () {
		return itemName;
	}
	
	public void setItemType (String aItemType) {
		itemType = aItemType;
	}
	
	public void setItemName (String aItemName) {
		itemName = aItemName;
	}

	private void setOfferButtonPanel () {
		offerButtonPanel = new JPanel ();
		offerButtonPanel.setLayout (new BoxLayout (offerButtonPanel, BoxLayout.X_AXIS));
		offerButtonPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		
		// TODO Rename to remove 'Action'
		
		acceptButton = setActionButton (ACCEPT_ACTION, ACCEPT_ACTION);
		rejectButton = setActionButton (REJECT_ACTION, REJECT_ACTION);
		offerButtonPanel.add (rejectButton);
		offerButtonPanel.add (Box.createHorizontalStrut (10));
		offerButtonPanel.add (acceptButton);
		offerButtonPanel.setBackground (Color.ORANGE);
	}

	private void setOfferTopPanel (PurchaseOfferEffect aPurchaseOfferEffect) {
		JLabel tOfferLabel1;
		JLabel tOfferLabel2;
		String tOffer1;
		String tOffer2;
		String tPresidentName;
		Corporation tOperatingCompany;
		
		tOperatingCompany = roundManager.getOperatingCompany ();
		tPresidentName = tOperatingCompany.getPresidentName ();
		tOffer1 = "The President of " + aPurchaseOfferEffect.getActorName () + 
				" (" + tPresidentName + ") offers to buy a ";
		tOffer2 =  aPurchaseOfferEffect.getItemName () + " " + aPurchaseOfferEffect.getItemType () +
				" for " + Bank.formatCash (aPurchaseOfferEffect.getCash ()) + " from " +
				aPurchaseOfferEffect.getToActor ().getName () + ".";
		tOfferLabel1 = new JLabel (tOffer1);
		tOfferLabel2 = new JLabel (tOffer2);
		tOfferLabel1.setAlignmentX (CENTER_ALIGNMENT);
		tOfferLabel2.setAlignmentX (CENTER_ALIGNMENT);
		offerTopPanel = new JPanel ();
		offerTopPanel.add (Box.createVerticalStrut (10));
		offerTopPanel.setLayout (new BoxLayout (offerTopPanel, BoxLayout.Y_AXIS));
		offerTopPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		offerTopPanel.add (tOfferLabel1);
		offerTopPanel.add (Box.createVerticalStrut (10));
		offerTopPanel.add (tOfferLabel2);
		offerTopPanel.add (Box.createVerticalStrut (10));
		offerTopPanel.setBackground (Color.ORANGE);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		String tActionCommand;
		
		tActionCommand = e.getActionCommand ();
		if (tActionCommand.equals (ACCEPT_ACTION)) {
			sendOfferResponseAction (true);
		}
		if (tActionCommand.equals (REJECT_ACTION)) {
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
		
		tToActor = purchaseOfferEffect.getActor ();
		
		// Need to find the current Actor (who was sent the Purchase offer) to state who it comes from
		
		tFromActor = purchaseOfferEffect.getToActor ();
		tResponseOfferAction = new ResponseOfferAction (tRoundType, tRoundID, tFromActor);
		tResponseOfferAction.addResponseOfferEffect (tFromActor, tToActor, aResponse, itemType, itemName);
		roundManager.addAction (tResponseOfferAction);

		setVisible (false);
	}
	
	public JButton setActionButton (String aButtonLabel, String aActionCommand) {
		JButton tActionButton;
		
		tActionButton = new JButton (aButtonLabel);
		tActionButton.setAlignmentX (CENTER_ALIGNMENT);
		tActionButton.setActionCommand (aActionCommand);
		tActionButton.addActionListener (this);
		
		return tActionButton;
	}
}
