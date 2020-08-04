package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
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
	
	public PurchaseOfferFrame (PurchaseOfferEffect aPurchaseOfferEffect, RoundManager aRoundManager) {
		super ("Purchase Offer");
		
		JPanel tOfferPanel;
		JLabel tOfferLabel;
		String tOffer;
		String tPlayerName;
		
		purchaseOfferEffect = aPurchaseOfferEffect;
		roundManager = aRoundManager;
		tPlayerName = aRoundManager.getClientUserName ();
		setTitle ("Purchase Offer for " + tPlayerName);
		tOfferPanel = new JPanel ();
		tOfferPanel.add (Box.createVerticalStrut (10));
		tOffer = "The President of " + aPurchaseOfferEffect.getActorName () + " offers to buy a " + 
				aPurchaseOfferEffect.getItemName () + " " + aPurchaseOfferEffect.getItemType () +
				" for " + Bank.formatCash (aPurchaseOfferEffect.getCash ()) + " from " +
				aPurchaseOfferEffect.getToActor ().getName () + ".";
		tOfferLabel = new JLabel (tOffer);
		tOfferPanel.add (tOfferLabel);
		tOfferPanel.add (Box.createVerticalStrut (10));
		acceptButton = setActionButton (ACCEPT_ACTION, ACCEPT_ACTION);
		rejectButton = setActionButton (REJECT_ACTION, REJECT_ACTION);
		tOfferPanel.add (rejectButton);
		tOfferPanel.add (Box.createVerticalStrut (10));
		tOfferPanel.add (acceptButton);
		add (tOfferPanel);
		
		pack ();
		setSize (500, 150);
		setVisible (false);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		String tActionCommand;
		
		tActionCommand = e.getActionCommand ();
		if (tActionCommand.equals (ACCEPT_ACTION)) {
			System.out.println ("Accepted Offer");
			sendOfferResponseAction (true);
		}
		if (tActionCommand.equals (REJECT_ACTION)) {
			System.out.println ("Offer Rejected");
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
		tResponseOfferAction.addResponseOfferEffect (tFromActor, tToActor, aResponse);
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
