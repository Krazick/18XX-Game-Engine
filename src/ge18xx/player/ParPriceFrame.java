package ge18xx.player;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.SetParValueAction;
import ge18xx.round.action.WinAuctionAction;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.TransferOwnershipEffect;

import geUtilities.GUI;
import swingDelays.KButton;

public class ParPriceFrame extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String SET_PAR_PRICE_ACTION = "SetParPrice";
	public static final ParPriceFrame NO_PAR_PRICE_FRAME = null;
	public static final int NO_PAR_PRICE_VALUE = ShareCompany.NO_PAR_PRICE;

	Player player;
	StockRound stockRound;
	Certificate certificate;
	GameManager gameManager;
	JComboBox<String> parValuesCombo;
	KButton doButton;
	JPanel parValuesPanel;
	boolean parPriceFrameActive;

	public ParPriceFrame (JFrame aParentFrame, Player aPlayer, StockRound aStockRound, Certificate aCertificate) {
		super (aParentFrame, "Par Value Selection");

		// Need to have the Player that has bought the President Sale shown in Dialog
		// This is to confirm the player who bought the share, possible during Auction,
		// is notified THEY should be setting this,
		// Not the Player that bought the prior share that triggered the Auction
		Point tNewPoint;
		GameManager tGameManager;

		setPlayer (aPlayer);
		setStockRound (aStockRound);
		setCertificate (aCertificate);
		tGameManager = aPlayer.getGameManager ();
		setGameManager (tGameManager);

		parValuesPanel = buildParValuesPanel (aPlayer, aCertificate);
		activateParValuesPanel ();
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

	public JPanel buildParValuesPanel (Player aPlayer, Certificate aCertificate) {
		JPanel tVerticalBox;
		Border tCorporateColorBorder;
		JPanel tParValuesPanel;

		tParValuesPanel = new JPanel ();
		tParValuesPanel.setLayout (new BoxLayout (tParValuesPanel, BoxLayout.Y_AXIS));
		tCorporateColorBorder = aCertificate.getCorporateBorder ();

		tParValuesPanel.setBorder (tCorporateColorBorder);

		tVerticalBox = buildVerticalBox (aPlayer, aCertificate);

		tParValuesPanel.add (Box.createHorizontalStrut (10));
		tParValuesPanel.add (tVerticalBox);
		tParValuesPanel.add (Box.createHorizontalStrut (10));

		return tParValuesPanel;
	}

	private void activateParValuesPanel () {
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
		tFrameLabel1 = new JLabel (aPlayer.getName () + " is buying the " + aCertificate.getCompanyAbbrev ()
				+ " has in Treasury " + Bank.formatCash (tTotalTreasury));
		tFrameLabel1.setAlignmentX (Component.CENTER_ALIGNMENT);

		tVerticalBox = new JPanel ();
		tVerticalBox.setLayout (new BoxLayout (tVerticalBox, BoxLayout.Y_AXIS));
		tPadding = 20;
		tVerticalBox.setBorder (BorderFactory.createEmptyBorder (tPadding, tPadding, tPadding, tPadding));

		tVerticalBox.add (Box.createVerticalStrut (10));

		tCertificateInfoJPanel = aCertificate.buildBasicCertInfoJPanel ();
		tCertificateInfoJPanel.setAlignmentY (CENTER_ALIGNMENT);
		tVerticalBox.add (tCertificateInfoJPanel);
		tVerticalBox.add (Box.createVerticalStrut (10));
		tVerticalBox.add (tFrameLabel1);
		tVerticalBox.add (Box.createVerticalStrut (10));

		tMiddleBox = buildMiddleBox (aCertificate);

		tVerticalBox.add (tMiddleBox);
		updateButton ("Set Par Price", SET_PAR_PRICE_ACTION);

		tVerticalBox.add (doButton);

		return tVerticalBox;
	}

	public JPanel buildMiddleBox (Certificate aCertificate) {
		JLabel tFrameLabel2;
		JPanel tMiddleBox;

		tFrameLabel2 = new JLabel ("MUST set the Par Price for " + aCertificate.getCompanyAbbrev ());
		tFrameLabel2.setAlignmentX (Component.CENTER_ALIGNMENT);

		tMiddleBox = new JPanel ();
		tMiddleBox.setLayout (new BoxLayout (tMiddleBox, BoxLayout.X_AXIS));
		tMiddleBox.add (tFrameLabel2);
		tMiddleBox.add (Box.createHorizontalStrut (10));

		parValuesCombo = buildParValuesCombo ();
		if (parValuesCombo != null) {
			tMiddleBox.add (parValuesCombo);
			tMiddleBox.add (Box.createHorizontalStrut (10));
		}

		return tMiddleBox;
	}

	public JComboBox<String> buildParValuesCombo () {
		Integer [] tParValues;
		JComboBox<String> tParValuesCombo;

		tParValues = gameManager.getAllStartCells ();
		tParValuesCombo = new JComboBox<> ();
		// Update the Par Value Combo Box, and confirm or deny the Player has enough
		// Cash to buy Cheapest.

		certificate.fillParValueComboBox (tParValuesCombo, tParValues);

		if (tParValuesCombo != null) {
			tParValuesCombo.addActionListener (new ActionListener () {
				@Override
				public void actionPerformed (ActionEvent e) {
					updateButton ();
				}
			});
		}

		return tParValuesCombo;
	}

	public void setParPriceFrameActive (boolean aParPriceFrameActive) {
		parPriceFrameActive = aParPriceFrameActive;
	}

	public boolean isParPriceFrameActive () {
		return parPriceFrameActive;
	}

	public void updateButton (String aButtonLabel, String aActionCommand) {
		if (doButton == GUI.NO_BUTTON) {
			doButton = new KButton (aButtonLabel);
		} else {
			doButton.setText (aButtonLabel);
		}
		doButton.setAlignmentX (CENTER_ALIGNMENT);
		doButton.setActionCommand (aActionCommand);
		doButton.addActionListener (this);
		updateButton ();
	}

	public void updateButton () {
		if (getParPrice () > 0) {
			doButton.setEnabled (true);
			doButton.setToolTipText ("");
		} else {
			doButton.setEnabled (false);
			doButton.setToolTipText ("Par Price has not been selected yet");
		}
	}

	public int getParPrice () {
		int tParPrice = 0;
		String tParPriceString;

		tParPriceString = (String) parValuesCombo.getSelectedItem ();
		if (!Certificate.NO_PAR_PRICE.equals (tParPriceString)) {
			tParPrice = Integer.parseInt (tParPriceString);
		}

		return tParPrice;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		boolean tParPriceSet;

		tParPriceSet = false;
		if (SET_PAR_PRICE_ACTION.equals (aEvent.getActionCommand ())) {
			tParPriceSet = handleSetParPrice ();
		}

		if (tParPriceSet) {
			setVisible (false);
		}
	}

	public boolean handleSetParPrice () {
		int tSelectedParPrice;
		boolean tParPriceSet;

		tParPriceSet = false;
		tSelectedParPrice = getParPrice ();
		if (tSelectedParPrice > 0) {
			tParPriceSet = handleSetParPrice (tSelectedParPrice);
		}

		return tParPriceSet;
	}

	public boolean handleSetParPrice (int aSelectedParPrice) {
		boolean tParPriceSet;

		tParPriceSet = gameManager.handleSetParPrice (certificate, aSelectedParPrice, this);
		if (tParPriceSet) {
			setParPriceFrameActive (false);
			gameManager.bringPlayerFrameToFront ();
		}

		return tParPriceSet;
	}

	public void setParValueAction (int aParPrice, ShareCompany aShareCompany, boolean aChainToPrevious) {
		SetParValueAction tSetParValueAction;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		String tCoordinates;
		
		tRoundType = stockRound.getRoundType ();
		tRoundID = stockRound.getID ();
		tCoordinates = GUI.EMPTY_STRING;
		tSetParValueAction = new SetParValueAction (tRoundType, tRoundID, player);
		tSetParValueAction.addSetParValueEffect (player, aShareCompany, aParPrice, tCoordinates);
		if (gameManager.isNetworkGame ()) {
			handleResetPlayerStates (tSetParValueAction);
		}
		tSetParValueAction.setChainToPrevious (aChainToPrevious);

		stockRound.addAction (tSetParValueAction);
	}

	private void handleResetPlayerStates (SetParValueAction aSetParValueAction) {
		WinAuctionAction tWinAuctionAction;

		tWinAuctionAction = getLastWinAuctionAction ();
		if (tWinAuctionAction != Action.NO_ACTION) {
			if (IsCorrectAction (tWinAuctionAction)) {
				aSetParValueAction.resetPlayerStatesAfterWait (tWinAuctionAction);
			}
		}
	}

	private boolean IsCorrectAction (Action aAction) {
		boolean tIsCorrectAction;
		TransferOwnershipEffect tTransferOwnershipEffect;
		Certificate tCertificate;
		Effect tEffect;
		String tEffectName;
		String tFoundEffectName;
		int tEffectCount;
		int tEffectIndex;

		tIsCorrectAction = false;
		tEffectName = TransferOwnershipEffect.NAME;
		tEffectCount = aAction.getEffectCount ();
		for (tEffectIndex = 0; tEffectIndex < tEffectCount; tEffectIndex++) {
			tEffect = aAction.getEffect (tEffectIndex);
			tFoundEffectName = tEffect.getName ();
			if (tFoundEffectName.equals (tEffectName)) {
				tTransferOwnershipEffect = (TransferOwnershipEffect) tEffect;
				tCertificate = tTransferOwnershipEffect.getCertificate ();
				if (tCertificate == certificate) {
					tIsCorrectAction = true;
				}
			}
		}

		return tIsCorrectAction;
	}

	private WinAuctionAction getLastWinAuctionAction () {
		WinAuctionAction tWinAuctionAction;
		Action tLastAction;
		boolean tLookingForLastAction;
		int tActionOffset;

		tLookingForLastAction = true;
		tActionOffset = ActionManager.PREVIOUS_ACTION;
		tWinAuctionAction = (WinAuctionAction) Action.NO_ACTION;
		while (tLookingForLastAction) {
			tLastAction = gameManager.getLastAction (tActionOffset);
			if (tLastAction != Action.NO_ACTION) {
				if (tLastAction instanceof WinAuctionAction) {
					tWinAuctionAction = (WinAuctionAction) tLastAction;
					tLookingForLastAction = false;
				} else {
					tActionOffset++;
				}
			} else {
				tLookingForLastAction = false;
			}
		}

		return tWinAuctionAction;
	}
}
