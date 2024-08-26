package ge18xx.company.formation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.FormationPhaseAction;
import ge18xx.train.TrainPortfolio;

import geUtilities.GUI;
import swingTweaks.KButton;

public class PlayerFormationPhase extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String CONTINUE = "Continue";
	public static final String DONE = "Done";
	public static final String UNDO = "Undo";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	public static final PlayerFormationPhase NO_PLAYER_FORMATION_PHASE = null;
	protected Player player;
	protected GameManager gameManager;
	protected FormationPhase formationPhase;
	protected List<String> shareCompaniesHandled;

	JPanel buttonsPanel;
	JLabel presidentNameLabel;
	KButton continueButton;
	KButton doneButton;
	KButton undoButton;
	boolean actingPlayer;

	public PlayerFormationPhase (GameManager aGameManager, FormationPhase aFormationPhase, Player aPlayer, 
							Player aActingPresident) {
//		String tActingPresidentName;
		Color tBackgroundColor;
		Color tBorderColor;
		Border tActingBorder;
		
		gameManager = aGameManager;
		formationPhase = aFormationPhase;
		player = aPlayer;
		if (aActingPresident == aPlayer) {
//			tActingPresidentName = aActingPresident.getName ();
//			if (gameManager.notIsNetworkAndIsThisClient (tActingPresidentName)) {
			if (isActingPlayer (aActingPresident)) {
				tBackgroundColor = gameManager.getAlertColor ();
				tBorderColor = gameManager.getAlertColor ();
				actingPlayer = true;
			} else {
				tBackgroundColor = GUI.defaultColor;
				tBorderColor = gameManager.getAlertColor ();
				actingPlayer = false;
			}
		} else {
			tBackgroundColor = GUI.defaultColor;
			tBorderColor = Color.BLACK;
			actingPlayer = false;
		}
		tActingBorder = BorderFactory.createLineBorder (tBorderColor, 3);
		buildPlayerJPanel (actingPlayer, tActingBorder);
		setBackground (tBackgroundColor);
	}
	
	public boolean isActingPlayer (Player aActingPresident) {
		boolean tIsActingPlayer;
		String tActingPresidentName;
		
		tActingPresidentName = aActingPresident.getName ();
		tIsActingPlayer = gameManager.notIsNetworkAndIsThisClient (tActingPresidentName);

		return tIsActingPlayer;
	}
	
	public void buildPlayerJPanel (boolean aActingPlayer, Border aActingBorder) {
		JLabel tPresidentTreasury;
		JPanel tPlayerInfo;
		JPanel tPortfolio;
		JPanel tCompanies;
		Portfolio tPlayerPortfolio;
		Border tMargin;
		Border tCombinedBorder;
		
		setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
		tMargin = new EmptyBorder (10,10,10,10);

		tPlayerInfo = new JPanel ();
		tPlayerInfo.setLayout (new BoxLayout (tPlayerInfo, BoxLayout.Y_AXIS));
		presidentNameLabel = new JLabel ("Name: " + player.getName () + " [" + player.getStateName () + "]");
		tPlayerInfo.add (presidentNameLabel);
		tPlayerInfo.add (Box.createVerticalStrut (10));
	
		tPresidentTreasury = new JLabel ("Cash: " + Bank.formatCash (player.getCash ()));
		tPlayerInfo.add (tPresidentTreasury);
		tPlayerInfo.add (Box.createVerticalStrut (10));
		
		add (tPlayerInfo);
		add (Box.createHorizontalStrut (10));
		
		tPlayerPortfolio = player.getPortfolio ();
		tPortfolio = buildPlayerPortfolioJPanel (tPlayerPortfolio);
		add (tPortfolio);
		add (Box.createHorizontalStrut (10));
	
		tCompanies = buildPlayerCompaniesJPanel (tPlayerPortfolio, aActingPlayer);
		add (tCompanies);
		
		buildButtonsPanel (aActingPlayer, CONTINUE);
		
		tMargin = new EmptyBorder (10,10,10,10);
		tCombinedBorder = BorderFactory.createCompoundBorder (aActingBorder, tMargin);
		setBorder (tCombinedBorder);
	}
	
	public void buildButtonsPanel (boolean aActingPlayer, String aContinueCommand) {
		String tToolTip;
		
		tToolTip = GUI.EMPTY_STRING;
		continueButton = formationPhase.buildSpecialButton (CONTINUE, aContinueCommand, tToolTip, this);
		doneButton = formationPhase.buildSpecialButton (DONE, DONE, tToolTip, this);
		undoButton = formationPhase.buildSpecialButton (UNDO, UNDO, tToolTip, this);
		
		buttonsPanel = new JPanel ();
		buttonsPanel.setLayout (new BoxLayout (buttonsPanel, BoxLayout.Y_AXIS));

		buttonsPanel.add (Box.createVerticalGlue ());
		buttonsPanel.add (Box.createVerticalStrut (10));
		buttonsPanel.add (continueButton);
		buttonsPanel.add (Box.createVerticalStrut (10));
		buttonsPanel.add (doneButton);
		buttonsPanel.add (Box.createVerticalStrut (10));
		buttonsPanel.add (undoButton);
		buttonsPanel.add (Box.createVerticalStrut (10));
		buttonsPanel.add (Box.createVerticalGlue ());
		updateAllButtons (aActingPlayer);
		
		add (buttonsPanel);
	}
	
	public void setFormationState (ActorI.ActionStates aFormationState) {
		formationPhase.setFormationState (aFormationState);
	}
	
	public void setFormationState (FormationPhaseAction aFormationPhaseAction, ActorI.ActionStates aNewFormationState) {
		formationPhase.setFormationState (aFormationPhaseAction, aNewFormationState);
	}
	
	public void updateSpecialButtons (boolean aActingPlayer) {
		
	}
	
	public void updateAllButtons (boolean aActingPlayer) {
		updateContinueButton (aActingPlayer);
		updateDoneButton (aActingPlayer);
		updateUndoButton (aActingPlayer);
		updateSpecialButtons (aActingPlayer);
	}
	
	public void updateContinueButton () {
		
	}
	
	public void updateContinueButton (boolean aActingPlayer) {
		if (aActingPlayer) {
			continueButton.setVisible (true);
		} else {
			continueButton.setVisible (false);
		}
		updateContinueButton ();
	}
	
	public void updateDoneButton (boolean aActingPlayer) {
		String tToolTip;
		
		if (aActingPlayer) {
			updateDoneButton ();
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
			doneButton.setToolTipText (tToolTip);
			doneButton.setEnabled (false);
		}
	}
	
	public void updateDoneButton () {
		
	}
	
	public void updateUndoButton (boolean aActingPlayer) {
		if (aActingPlayer) {
			undoButton.setEnabled (true);
		} else {
			undoButton.setToolTipText (NOT_ACTING_PRESIDENT);
			undoButton.setEnabled (false);
		}
	}
	
	public JPanel buildPlayerPortfolioJPanel (Portfolio aPlayerPortfolio) {
		JPanel tPlayerPortfolioJPanel;
		JPanel tOwnershipPanel;
		JLabel tTitle;
		
		tPlayerPortfolioJPanel = new JPanel ();
		tPlayerPortfolioJPanel.setLayout (new BoxLayout (tPlayerPortfolioJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel ("Portfolio");
		tPlayerPortfolioJPanel.add (tTitle);
		tOwnershipPanel = aPlayerPortfolio.buildOwnershipPanel (gameManager);
		tPlayerPortfolioJPanel.add (tOwnershipPanel);
		
		return tPlayerPortfolioJPanel;
	}
	
	public JPanel buildPlayerCompaniesJPanel (Portfolio aPlayerPortfolio, boolean aActingPlayer) {
		JPanel tCompanyInfoPanel;
		JPanel tShareCompanyJPanel;
		JPanel tTitlePanel;
		JLabel tTitleLabel;
		ShareCompany tShareCompany;
		Certificate tCertificate;
		String tTitle;
		int tCertificateCount;
		int tCertificateIndex;
		
		tCompanyInfoPanel = new JPanel ();
		tCompanyInfoPanel.setLayout (new BoxLayout (tCompanyInfoPanel, BoxLayout.Y_AXIS));
		
		tTitlePanel = new JPanel ();
		tTitlePanel.setLayout (new BoxLayout (tTitlePanel, BoxLayout.X_AXIS));
		tTitle = "Companies where " + player.getName () + " is President";
		tTitleLabel = new JLabel (tTitle);
		tTitlePanel.add (tTitleLabel);
		tCompanyInfoPanel.add (tTitlePanel);
		
		tCertificateCount = aPlayerPortfolio.getCertificateTotalCount ();
		for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
			tCertificate = aPlayerPortfolio.getCertificate (tCertificateIndex);
			if (tCertificate.isAShareCompany () && tCertificate.isPresidentShare ()) {
				tShareCompany = tCertificate.getShareCompany ();
				tShareCompanyJPanel = buildCompanyJPanel (tShareCompany, aActingPlayer);
				tCompanyInfoPanel.add (tShareCompanyJPanel);
				tCompanyInfoPanel.add (Box.createVerticalStrut (5));
			}
		}

		return tCompanyInfoPanel;
	}

	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer, JPanel aShareCompanyJPanel) {
		JPanel tCompanyInfoJPanel;
		JLabel tCompanyAbbrev;
		JLabel tLoans;
		JLabel tTreasury;
		JLabel tTrains;
		Border tCorporateColorBorder;
		TrainPortfolio tTrainPortfolio;
		
		tCorporateColorBorder = aShareCompany.setupBorder ();
		aShareCompanyJPanel.setBorder (tCorporateColorBorder);
		
		tCompanyInfoJPanel = new JPanel ();
		tCompanyInfoJPanel.setLayout (new BoxLayout (tCompanyInfoJPanel, BoxLayout.Y_AXIS));
		tCompanyAbbrev = new JLabel (aShareCompany.getAbbrev ());
		tCompanyInfoJPanel.add (tCompanyAbbrev);
		
		if (! aShareCompany.isGovtRailway ()) {
			tLoans = new JLabel ("Loans: " + aShareCompany.getLoanCount ());
			tCompanyInfoJPanel.add (tLoans);
		}

		tTreasury = new JLabel ("Treasury: " + Bank.formatCash (aShareCompany.getTreasury ()));
		tCompanyInfoJPanel.add (tTreasury);
		
		tTrainPortfolio = aShareCompany.getTrainPortfolio ();
		tTrains = new JLabel (tTrainPortfolio.getTrainList ());
		tCompanyInfoJPanel.add (tTrains);

		aShareCompanyJPanel.add (tCompanyInfoJPanel);
		aShareCompanyJPanel.add (Box.createHorizontalStrut (10));

		return aShareCompanyJPanel;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.equals (DONE)) {
			handlePlayerDone ();
		}
		if (tActionCommand.equals (UNDO)) {
			handlePlayerUndo ();
		}
	}

	public int getCurrentPlayerIndex () {
		int tCurrentPlayerIndex;

		tCurrentPlayerIndex = formationPhase.getCurrentPlayerIndex ();
		
		return tCurrentPlayerIndex;
	}
	
	public void handlePlayerDone () {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		boolean tAddAction;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tAddAction = true;
		formationPhase.updateToNextPlayer (tPlayers, tAddAction);
	}
	
	public void handlePlayerUndo () {
		int tCurrentPlayerIndex;
		int tLastActionNumber;
		
		tLastActionNumber = gameManager.getActionNumber ();
		System.out.println ("Player hit UNDO ------ Undoing Action #" + tLastActionNumber);
		player.undoAction ();
		if (formationPhase.getFormationState () != ActorI.ActionStates.NoState) {
			tCurrentPlayerIndex = getCurrentPlayerIndex ();
			formationPhase.rebuildFormationPanel (tCurrentPlayerIndex);
		}
	}
	
	public KButton getActivatedButton (ActionEvent aEvent) {
		KButton tActivatedButton;
		Object tSource;
		
		tSource = aEvent.getSource ();
		if (tSource instanceof KButton) {
			tActivatedButton = (KButton) tSource;
		} else {
			tActivatedButton = GUI.NO_BUTTON;
		}
		
		return tActivatedButton;
	}
	
	public ShareCompany findShareCompany (KButton aActivatedButton) {
		ShareCompany tFoundShareCompany;
		CorporationList tShareCompanies;
		
		tShareCompanies = gameManager.getShareCompanies ();
		tFoundShareCompany = tShareCompanies.findCompanyWithButton (aActivatedButton);
		
		return tFoundShareCompany;
	}

	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		return null;
	}
}
