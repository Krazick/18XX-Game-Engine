package ge18xx.company.special;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.GUI;

public class PlayerFormationPhase extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String DONE = "Done";
	public static final String UNDO = "Undo";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	public static final PlayerFormationPhase NO_PLAYER_FORMATION_PHASE = null;
	protected Player player;
	protected GameManager gameManager;
	protected FormationPhase formationPhase;
	JButton done;
	JButton undo;
	protected List<String> shareCompaniesHandled;

	public PlayerFormationPhase (GameManager aGameManager, FormationPhase aLoanRepayment, Player aPlayer, 
							Player aActingPresident) {
		String tActingPresidentName;
		Color tBackgroundColor;
		Color tBorderColor;
		Border tActingBorder;
		boolean tActingPlayer;
		
		gameManager = aGameManager;
		formationPhase = aLoanRepayment;
		player = aPlayer;
		
		if (aActingPresident == aPlayer) {
			tActingPresidentName = aActingPresident.getName ();
			if (gameManager.isNetworkAndIsThisClient (tActingPresidentName)) {
				tBackgroundColor = gameManager.getAlertColor ();
				tBorderColor = gameManager.getAlertColor ();
				tActingPlayer = true;
			} else {
				tBackgroundColor = GUI.defaultColor;
				tBorderColor = gameManager.getAlertColor ();
				tActingPlayer = false;
			}
		} else {
			tBackgroundColor = GUI.defaultColor;
			tBorderColor = Color.BLACK;
			tActingPlayer = false;
		}
		tActingBorder = BorderFactory.createLineBorder (tBorderColor, 3);
		buildPlayerJPanel (tActingPlayer, tActingBorder);
		setBackground (tBackgroundColor);
	}
	
	public void buildPlayerJPanel (boolean aActingPlayer, Border aActingBorder) {
		JLabel tPresidentName;
		JLabel tPresidentTreasury;
		JPanel tPlayerInfo;
		JPanel tPortfolio;
		JPanel tCompanies;
		JPanel tDoneUndo;
		Portfolio tPlayerPortfolio;
		Border tMargin;
		Border tCombinedBorder;
		String tToolTip;
		
		setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
		tMargin = new EmptyBorder (10,10,10,10);

		tPlayerInfo = new JPanel ();
		tPlayerInfo.setLayout (new BoxLayout (tPlayerInfo, BoxLayout.Y_AXIS));
		tPresidentName = new JLabel ("Name: " + player.getName ());
		tPlayerInfo.add (tPresidentName);
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
		
		tToolTip = GUI.EMPTY_STRING;
		done = formationPhase.buildSpecialButton (DONE, DONE, tToolTip, this);
		undo = formationPhase.buildSpecialButton (UNDO, UNDO, tToolTip, this);
		updateDoneButton (aActingPlayer);
		updateUndoButton (aActingPlayer);
		tDoneUndo = new JPanel ();
		tDoneUndo.setLayout (new BoxLayout (tDoneUndo, BoxLayout.Y_AXIS));

		tDoneUndo.add (Box.createVerticalGlue ());
		tDoneUndo.add (Box.createVerticalStrut (10));
		tDoneUndo.add (done);
		tDoneUndo.add (Box.createVerticalGlue ());
		tDoneUndo.add (undo);
		tDoneUndo.add (Box.createVerticalStrut (10));
		tDoneUndo.add (Box.createVerticalGlue ());
		add (tDoneUndo);
		
		tMargin = new EmptyBorder (10,10,10,10);
		tCombinedBorder = BorderFactory.createCompoundBorder (aActingBorder, tMargin);
		setBorder (tCombinedBorder);
	}
	
	public void updateDoneButton () {
		
	}
	
	public void updateDoneButton (boolean aActingPlayer) {
		String tToolTip;
		
		if (aActingPlayer) {
			updateDoneButton ();
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
			done.setToolTipText (tToolTip);
			done.setEnabled (false);
		}
	}
	
	public void updateUndoButton (boolean aActingPlayer) {
		if (aActingPlayer) {
			undo.setEnabled (true);
		} else {
			undo.setToolTipText (NOT_ACTING_PRESIDENT);
			undo.setEnabled (false);
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
		JPanel tPlayerCompaniesJPanel;
		JPanel tShareCompanyJPanel;
		JPanel tTitlePanel;
		JLabel tTitleLabel;
		ShareCompany tShareCompany;
		Certificate tCertificate;
		String tTitle;
		int tCertificateCount;
		int tCertificateIndex;
		
		tPlayerCompaniesJPanel = new JPanel ();
		tPlayerCompaniesJPanel.setLayout (new BoxLayout (tPlayerCompaniesJPanel, BoxLayout.Y_AXIS));
		
		tTitlePanel = new JPanel ();
		tTitlePanel.setLayout (new BoxLayout (tTitlePanel, BoxLayout.X_AXIS));
		tTitle = "Companies where " + player.getName () + " is President";
		tTitleLabel = new JLabel (tTitle);
		tTitlePanel.add (tTitleLabel);
		tPlayerCompaniesJPanel.add (tTitlePanel);
		
		tCertificateCount = aPlayerPortfolio.getCertificateTotalCount ();
		for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
			tCertificate = aPlayerPortfolio.getCertificate (tCertificateIndex);
			if (tCertificate.isAShareCompany () && tCertificate.isPresidentShare ()) {
				tShareCompany = tCertificate.getShareCompany ();
				tShareCompanyJPanel = buildCompanyJPanel (tShareCompany, aActingPlayer);
				tPlayerCompaniesJPanel.add (tShareCompanyJPanel);
				tPlayerCompaniesJPanel.add (Box.createVerticalStrut (5));
			}
		}

		return tPlayerCompaniesJPanel;
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
		
		tLoans = new JLabel ("Loans: " + aShareCompany.getLoanCount ());
		tCompanyInfoJPanel.add (tLoans);

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
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		formationPhase.updateToNextPlayer (tPlayers);
		
			// Action to move to Next player
	}
	
	public void handlePlayerUndo () {
		int tCurrentPlayerIndex;
		
		player.undoAction ();
		if (formationPhase.getFormationState () != ActorI.ActionStates.NoState) {
			tCurrentPlayerIndex = getCurrentPlayerIndex ();
			formationPhase.rebuildSpecialPanel (tCurrentPlayerIndex);
		}
	}
	
	public JButton getActivatedButton (ActionEvent aEvent) {
		JButton tActivatedButton;
		Object tSource;
		
		tSource = aEvent.getSource ();
		if (tSource instanceof JButton) {
			tActivatedButton = (JButton) tSource;
		} else {
			tActivatedButton = GUI.NO_BUTTON;
		}
		
		return tActivatedButton;
	}
	
	public ShareCompany findShareCompany (JButton aActivatedButton) {
		ShareCompany tFoundShareCompany;
		CorporationList tShareCompanies;
		
		tShareCompanies = gameManager.getShareCompanies ();
		tFoundShareCompany = tShareCompanies.findCompanyWithSpecial (aActivatedButton);
		
		return tFoundShareCompany;
	}

	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
				return null;
	}
}
