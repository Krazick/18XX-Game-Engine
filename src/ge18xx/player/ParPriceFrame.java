package ge18xx.player;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.SetParValueAction;
import ge18xx.utilities.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class ParPriceFrame extends JDialog implements ActionListener {
	private static final String SET_PAR_PRICE_ACTION = "SetParPrice";
	private static final long serialVersionUID = 1L;
	public static final ParPriceFrame NO_PAR_PRICE_FRAME = null;
	public static final int NO_PAR_PRICE_VALUE = -1;
	
	Player player;
	StockRound stockRound;
	Certificate certificate;
	GameManager gameManager;
	JComboBox<String> parValuesCombo;
	JButton doActionButton;
	JPanel parValuesPanel;
	boolean parPriceFrameActive;
	
	public ParPriceFrame (JFrame aParentFrame, Player aPlayer, StockRound aStockRound, Certificate aCertificate) {
		super (aParentFrame, "Par Value Selection");
		
		// Need to have the Player that has bought the President Sale shown in Dialog 
		// This is to confirm the player who bought the share, possible during Auction, is notified THEY should be setting this,
		// Not the Player that bought the prior share that triggered the Auction
		Point tNewPoint;
		GameManager tGameManager;
		
		setPlayer (aPlayer);
		setStockRound (aStockRound);
		setCertificate (aCertificate);
		tGameManager = aPlayer.getGameManager ();
		setGameManager (tGameManager);
		
		buildParValuesPanel (aPlayer, aCertificate);
		tNewPoint = gameManager.getOffsetPlayerFrame ();
		setLocation (tNewPoint);
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
		pack ();
	}

	public void setPlayer (Player aPlayer) {
		player = aPlayer;
	}
	
	public void setStockRound (StockRound aStockRound) {
		stockRound = aStockRound;
	}
	
	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}
	
	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}
	
	private void buildParValuesPanel (Player aPlayer, Certificate aCertificate) {
		JPanel tVerticalBox;
		Border tCorporateColorBorder;
		
		parValuesPanel = new JPanel ();
		parValuesPanel.setLayout (new BoxLayout (parValuesPanel, BoxLayout.Y_AXIS));
		tCorporateColorBorder = aCertificate.getCorporateBorder ();

		parValuesPanel.setBorder (tCorporateColorBorder);
		
		tVerticalBox = buildVerticalBox (aPlayer, aCertificate);
		
		parValuesPanel.add (Box.createHorizontalStrut (10));
		parValuesPanel.add (tVerticalBox);
		parValuesPanel.add (Box.createHorizontalStrut (10));
		setParPriceFrameActive (true);
		add (parValuesPanel);
		if (gameManager.isNetworkGame ()) {
			parValuesPanel.setBackground (Color.ORANGE);
		}
	}

	public JPanel buildVerticalBox (Player aPlayer, Certificate aCertificate) {
		JLabel tFrameLabel1;
		int tTotalTreasury;
		int tEscrowCount;
		int tPadding;
		Escrow tEscrow;
		JPanel tVerticalBox;
		JPanel tMiddleBox;
		JPanel tCertificateInfoJPanel;
		
		tTotalTreasury = aPlayer.getCash ();
		tEscrowCount = aPlayer.getEscrowCount ();
		if (tEscrowCount == 1) {
			tEscrow = aPlayer.getEscrowAt (0);
			tTotalTreasury += tEscrow.getCash ();
		}
		tFrameLabel1 = new JLabel (aPlayer.getName () + " is buying the " + 
								aCertificate.getCompanyAbbrev () + " has in Treasury " + 
								Bank.formatCash (tTotalTreasury));
		tFrameLabel1.setAlignmentX (Component.CENTER_ALIGNMENT);
		
		tVerticalBox = new JPanel ();
		tVerticalBox.setLayout (new BoxLayout (tVerticalBox, BoxLayout.Y_AXIS));
		tPadding = 20;
		tVerticalBox.setBorder (BorderFactory.createEmptyBorder (tPadding, tPadding, tPadding, tPadding));

		tVerticalBox.add (Box.createVerticalStrut (10));
		
		tCertificateInfoJPanel = aCertificate.buildBasicCertInfoJPanel ();
		tVerticalBox.add (tCertificateInfoJPanel);
		tVerticalBox.add (Box.createVerticalStrut (10));
		tVerticalBox.add (tFrameLabel1);
		tVerticalBox.add (Box.createVerticalStrut (10));

		tMiddleBox = buildMiddleBox (aCertificate);
		
		tVerticalBox.add (tMiddleBox);
		setActionButton ("Set Par Price", SET_PAR_PRICE_ACTION);
		
		tVerticalBox.add (doActionButton);
		
		return tVerticalBox;
	}

	public JPanel buildMiddleBox (Certificate aCertificate) {
		Integer[] tParValues;
		JLabel tFrameLabel2;
		JPanel tMiddleBox;
		
		tFrameLabel2 = new JLabel ("MUST set the Par Price for " + aCertificate.getCompanyAbbrev ());
		tFrameLabel2.setAlignmentX (Component.CENTER_ALIGNMENT);
		
		tMiddleBox = new JPanel ();
		tMiddleBox.setLayout (new BoxLayout (tMiddleBox, BoxLayout.X_AXIS));
		tMiddleBox.add (tFrameLabel2);
		tMiddleBox.add (Box.createHorizontalStrut (10));

		tParValues = gameManager.getAllStartCells ();
		parValuesCombo = new JComboBox <String> ();
		// Update the Par Value Combo Box, and confirm or deny the Player has enough Cash to buy Cheapest.
		
		certificate.fillParValueComboBox (parValuesCombo, tParValues);
		
		
		if (parValuesCombo != null) {
			parValuesCombo.addActionListener (new ActionListener () {
			    @Override
				public void actionPerformed (ActionEvent e) {
			        updateActionButton ();
			    }
			});
			tMiddleBox.add (parValuesCombo);
			tMiddleBox.add (Box.createHorizontalStrut (10));
		}
		
		return tMiddleBox;
	}

	public void setParPriceFrameActive (boolean aParPriceFrameActive) {
		parPriceFrameActive = aParPriceFrameActive;
	}
	
	public boolean isParPriceFrameActive () {
		return parPriceFrameActive;
	}
	
	public void setActionButton (String aButtonLabel, String aActionCommand) {
		if (doActionButton == GUI.NO_BUTTON) {
			doActionButton = new JButton (aButtonLabel);
		} else {
			doActionButton.setText (aButtonLabel);
		}
		doActionButton.setAlignmentX (CENTER_ALIGNMENT);
		doActionButton.setActionCommand (aActionCommand);
		doActionButton.addActionListener (this);
		updateActionButton ();
	}

	public void updateActionButton () {
		if (getParPrice () > 0) {
			doActionButton.setEnabled (true);
			doActionButton.setToolTipText ("");
		} else {
			doActionButton.setEnabled (false);
			doActionButton.setToolTipText ("Par Price has not been selected yet");
		}
	}
	
	public int getParPrice () {
		int tParPrice = 0;
		String tParPriceString;
		
		tParPriceString = (String) parValuesCombo.getSelectedItem ();
		if (! Certificate.NO_PAR_PRICE.equals (tParPriceString)) {
			tParPrice = Integer.parseInt (tParPriceString);
		}
		
		return tParPrice;
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		int tSelectedParPrice;
		Corporation tCorporation;
		ShareCompany tShareCompany;
		
		if (SET_PAR_PRICE_ACTION.equals (aEvent.getActionCommand ())) {
			tSelectedParPrice = getParPrice ();
			if (tSelectedParPrice > 0) {
				tCorporation = certificate.getCorporation ();
				if (tCorporation.isAShareCompany ()) {
					tShareCompany = (ShareCompany) tCorporation;
				} else {
					tShareCompany = (ShareCompany) Corporation.NO_CORPORATION;
				}
				if ((tSelectedParPrice > 0) && (tShareCompany != Corporation.NO_CORPORATION)) {
					setParPriceFrameActive (false);
					gameManager.setParPrice (tShareCompany, tSelectedParPrice);
					setParValueAction (tSelectedParPrice, tShareCompany, true);
					gameManager.bringPlayerFrameToFront ();
				}
			}
		}
		setVisible (false);
	}

	public void setParValueAction (int aParPrice, ShareCompany aShareCompany, boolean aChainToPrevious) {
		SetParValueAction tSetParValueAction;
		
		tSetParValueAction = new SetParValueAction (stockRound.getRoundType (), stockRound.getID (), player);
		tSetParValueAction.addSetParValueEffect (player, aShareCompany, aParPrice);
		tSetParValueAction.setChainToPrevious (aChainToPrevious);
		
		stockRound.addAction (tSetParValueAction);
	}
}
