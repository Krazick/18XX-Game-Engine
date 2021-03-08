package ge18xx.player;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.ActionManager;
import ge18xx.round.action.SetParValueAction;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ParPriceFrame extends JFrame implements ActionListener {
	private static final String SET_PAR_PRICE_ACTION = "SetParPrice";
	private static final long serialVersionUID = 1L;
	
	Player player;
	StockRound stockRound;
	Certificate certificate;
	GameManager gameManager;
	JComboBox<Integer> parValuesCombo;
	JButton doActionButton;
	JPanel parValuesPanel;
	boolean parPriceFrameActive;
	
	public ParPriceFrame (Player aPlayer, StockRound aStockRound, Certificate aCertificate) {
		super ("Par Value Selection");
		Integer [] tParValues;
		JLabel tFrameLabel;
		int tTotalTreasury, tEscrowCount;
		Escrow tEscrow;
		Container verticalBox = Box.createVerticalBox ();
		Container middleBox = Box.createHorizontalBox ();

		// Need to have the Player that has bought the President Sale shown in Dialog 
		// This is to confirm the player who bought the share, possible during Auction, is notified THEY should be setting this,
		// Not the Player that bought the prior share that triggered the Auction
		
		player = aPlayer;
		stockRound = aStockRound;
		certificate = aCertificate;
		parValuesPanel = new JPanel ();
		verticalBox.add (Box.createVerticalStrut (10));
		
		tTotalTreasury = aPlayer.getCash ();
		tEscrowCount = aPlayer.getEscrowCount ();
		if (tEscrowCount == 1) {
			tEscrow = aPlayer.getEscrowAt (0);
			tTotalTreasury += tEscrow.getCash ();
		}
		tFrameLabel = new JLabel (aPlayer.getName () + " is buying the " + 
								aCertificate.getCompanyAbbrev () + " has in Treasury " + 
								Bank.formatCash (tTotalTreasury));
		tFrameLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		verticalBox.add (tFrameLabel);
		verticalBox.add (Box.createVerticalStrut (10));

		tFrameLabel = new JLabel ("MUST set the Par Price for " + aCertificate.getCompanyAbbrev ());
		tFrameLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		middleBox.add (tFrameLabel);
		middleBox.add (Box.createHorizontalStrut (10));

		gameManager = aPlayer.getGameManager ();
		tParValues = gameManager.getAllStartCells ();
		parValuesCombo = new JComboBox <Integer> ();
		// Update the Par Value Combo Box, and confirm or deny the Player has enough Cash to buy Cheapest.
		certificate.fillParValueComboBox (parValuesCombo, tParValues);

		if (parValuesCombo != null) {
			middleBox.add (parValuesCombo);
			middleBox.add (Box.createHorizontalStrut (10));
		}
		verticalBox.add (middleBox);
		setActionButton ("Set Par Price", SET_PAR_PRICE_ACTION);
		verticalBox.add (doActionButton);
		parValuesPanel.add (Box.createHorizontalStrut (10));
		parValuesPanel.add (verticalBox);
		parValuesPanel.add (Box.createHorizontalStrut (10));
		setParPriceFrameActive (true);
		add (parValuesPanel);
		pack ();
	}
	
	public void setParPriceFrameActive (boolean aParPriceFrameActive) {
		parPriceFrameActive = aParPriceFrameActive;
	}
	
	public boolean isParPriceFrameActive () {
		return parPriceFrameActive;
	}
	
	public void setActionButton (String aButtonLabel, String aActionCommand) {
		if (doActionButton == null) {
			doActionButton = new JButton (aButtonLabel);
		} else {
			doActionButton.setText (aButtonLabel);
		}
		doActionButton.setAlignmentX (CENTER_ALIGNMENT);
		doActionButton.setActionCommand (aActionCommand);
		doActionButton.addActionListener (this);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		int tSelectedParPrice;
		Corporation tCorporation;
		ShareCompany tShareCompany;
		
		if (SET_PAR_PRICE_ACTION.equals (aEvent.getActionCommand ())) {
			tSelectedParPrice = (Integer) parValuesCombo.getSelectedItem ();
			
			tCorporation = certificate.getCorporation ();
			if (tCorporation instanceof ShareCompany) {
				tShareCompany = (ShareCompany) tCorporation;
			} else {
				tShareCompany = (ShareCompany) CorporationList.NO_CORPORATION;
			}
			if ((tSelectedParPrice > 0) && (tShareCompany != CorporationList.NO_CORPORATION)) {
				setParPriceFrameActive (false);
				gameManager.setParPrice (tShareCompany, tSelectedParPrice);
				setParValueAction (tSelectedParPrice, tShareCompany);
			}
		}
		if (gameManager.isNetworkGame ()) {
			setBackground (Color.ORANGE);
		}
		setVisible (false);
	}

	public void setParValueAction (int aParPrice, ShareCompany aShareCompany) {
		ActionManager tActionManager;
		SetParValueAction tSetParValueAction;
		
		tActionManager = stockRound.getActionManager ();
		tSetParValueAction = new SetParValueAction (stockRound.getRoundType (), stockRound.getID (), player);
		tSetParValueAction.addSetParValueEffect (player, aShareCompany, aParPrice);
		tActionManager.addAction (tSetParValueAction);
	}
}
