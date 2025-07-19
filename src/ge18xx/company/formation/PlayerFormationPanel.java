package ge18xx.company.formation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
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
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.MinorCompany;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.RoundManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.FormationRoundAction;
import ge18xx.train.TrainPortfolio;

import geUtilities.GUI;
import swingTweaks.KButton;

public class PlayerFormationPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String CONTINUE = "Continue";
	public static final String DONE = "Done";
	public static final String UNDO = "Undo";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	public static final PlayerFormationPanel NO_PLAYER_FORMATION_PANEL = null;
	protected Player player;
	protected GameManager gameManager;
	protected FormCompany formCompany;
	protected List<String> shareCompaniesHandled;

	JPanel buttonsPanel;
	JLabel presidentNameLabel;
	KButton continueButton;
	KButton doneButton;
	KButton undoButton;
	boolean actingPlayer;

	public PlayerFormationPanel (GameManager aGameManager, FormCompany aFormCompany, Player aPlayer, 
							Player aActingPresident) {
		Color tBackgroundColor;
		Color tBorderColor;
		Border tActingBorder;
		
		setGameManager (aGameManager);
		setFormCompany (aFormCompany);
		setPlayer (aPlayer);
		if (aActingPresident == aPlayer) {
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
	
	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}
	
	public void setFormCompany (FormCompany aFormCompany) {
		formCompany = aFormCompany;
	}

	public void setPlayer (Player aPlayer) {
		player = aPlayer;
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
		String tCashLabel;
		int tPlayerCash;
		
		setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
		tMargin = new EmptyBorder (10,10,10,10);

		tPlayerInfo = new JPanel ();
		tPlayerInfo.setLayout (new BoxLayout (tPlayerInfo, BoxLayout.Y_AXIS));
		presidentNameLabel = new JLabel ("Name: " + player.getName () + " [" + player.getStateName () + "]");
		tPlayerInfo.add (presidentNameLabel);
		tPlayerInfo.add (Box.createVerticalStrut (10));
	
		tPlayerCash = player.getCash ();
		tCashLabel = Bank.formatCash (Bank.CASH_LABEL, tPlayerCash);
		tPresidentTreasury = new JLabel (tCashLabel);
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
		continueButton = formCompany.buildSpecialButton (CONTINUE, aContinueCommand, tToolTip, this);
		doneButton = formCompany.buildSpecialButton (DONE, DONE, tToolTip, this);
		undoButton = formCompany.buildSpecialButton (UNDO, UNDO, tToolTip, this);
		
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
		formCompany.setFormationState (aFormationState);
	}
	
	public void setFormationState (FormationRoundAction aFormationRoundAction, ActorI.ActionStates aNewFormationState) {
		formCompany.setFormationState (aFormationRoundAction, aNewFormationState);
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
		JPanel tMinorCompanyJPanel;
		JPanel tPrivateCompanyJPanel;
		JPanel tTitlePanel;
		JLabel tTitleLabel;
		ShareCompany tShareCompany;
		MinorCompany tMinorCompany;
		PrivateCompany tPrivateCompany;
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
				if (tShareCompanyJPanel != null) {
					tCompanyInfoPanel.add (tShareCompanyJPanel);
					tCompanyInfoPanel.add (Box.createVerticalStrut (5));
				}
			} else if (tCertificate.isAMinorCompany () && tCertificate.isPresidentShare ()) {
				tMinorCompany = tCertificate.getMinorCompany ();
				tMinorCompanyJPanel = buildCompanyJPanel (tMinorCompany, aActingPlayer);
				if (tMinorCompanyJPanel != null) {
					tCompanyInfoPanel.add (tMinorCompanyJPanel);
					tCompanyInfoPanel.add (Box.createVerticalStrut (5));
				}
			} else if (tCertificate.isAPrivateCompany ()) {
				tPrivateCompany = tCertificate.getPrivateCompany ();
				tPrivateCompanyJPanel = buildCompanyJPanel (tPrivateCompany, aActingPlayer);
				if (tPrivateCompanyJPanel != null) {
					tCompanyInfoPanel.add (tPrivateCompanyJPanel);
					tCompanyInfoPanel.add (Box.createVerticalStrut (5));
				}
			}

		}

		return tCompanyInfoPanel;
	}
	
	public JPanel buildCompanyJPanel (PrivateCompany aPrivateCompany, boolean aActingPlayer, 
							JPanel aPrivateCompanyJPanel) {
		JPanel tCompanyInfoJPanel;
		String tCompanyName;
		JLabel tUpgradeTo;
		Corporation tCorporation;
		
		tCompanyName = "Private " + aPrivateCompany.getName () + " [" + aPrivateCompany.getAbbrev () + "]";
		tCompanyInfoJPanel = setupCompanyInfoJPanel (aPrivateCompany, aPrivateCompanyJPanel, tCompanyName);
		tCorporation = getExchangeCorporation (aPrivateCompany);
		if (tCorporation != Corporation.NO_CORPORATION) {
			tUpgradeTo = new JLabel ("Upgrade To: " + aPrivateCompany.getExchangePercentage () + "% of " + 
									tCorporation.getAbbrev ());
			tCompanyInfoJPanel.add (tUpgradeTo);
		}

		aPrivateCompanyJPanel.add (tCompanyInfoJPanel);
		aPrivateCompanyJPanel.add (Box.createHorizontalStrut (10));

		return aPrivateCompanyJPanel;
	}

	protected Corporation getExchangeCorporation (PrivateCompany aPrivateCompany) {
		int tExchangeID;
		Corporation tCorporation;
		
		tExchangeID = aPrivateCompany.getExchangeID ();
		tCorporation = gameManager.getCorporationByID (tExchangeID);
		
		return tCorporation;
	}

	public JPanel buildCompanyJPanel (MinorCompany aMinorCompany, boolean aActingPlayer, JPanel aMinorCompanyJPanel) {
		JPanel tCompanyInfoJPanel;
		String tCompanyName;
		JLabel tUpgradeTo;
		
		tCompanyName = aMinorCompany.getName ();
		tCompanyInfoJPanel = setupCompanyInfoJPanel (aMinorCompany, aMinorCompanyJPanel, tCompanyName);
		
		tUpgradeTo = new JLabel ("Upgrade To: " + aMinorCompany.getUpgradePercentage () + "% of " + 
								aMinorCompany.getUpgradeToAbbrev ());
		tCompanyInfoJPanel.add (tUpgradeTo);

		addAssets (aMinorCompany, tCompanyInfoJPanel);

		aMinorCompanyJPanel.add (tCompanyInfoJPanel);
		aMinorCompanyJPanel.add (Box.createHorizontalStrut (10));

		return aMinorCompanyJPanel;
	}

	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer, JPanel aShareCompanyJPanel) {
		JLabel tLoans;
		JPanel tCompanyInfoJPanel;
		String tCompanyName;
		
		tCompanyName = aShareCompany.getAbbrev ();
		tCompanyInfoJPanel = setupCompanyInfoJPanel (aShareCompany, aShareCompanyJPanel, tCompanyName);
		
		if (gameManager.gameHasLoans ()) {
			if (! aShareCompany.isGovtRailway ()) {
				tLoans = new JLabel ("Loans: " + aShareCompany.getLoanCount ());
				tCompanyInfoJPanel.add (tLoans);
			}
		}

		addAssets (aShareCompany, tCompanyInfoJPanel);

		aShareCompanyJPanel.add (tCompanyInfoJPanel);
		aShareCompanyJPanel.add (Box.createHorizontalStrut (10));

		return aShareCompanyJPanel;
	}

	private JPanel setupCompanyInfoJPanel (Corporation aCorporation, JPanel aTrainCompanyJPanel, String aName) {
		JPanel tCompanyInfoJPanel;
		JLabel tCompanyAbbrev;
		Border tCorporateColorBorder;
		
		tCorporateColorBorder = aCorporation.setupBorder ();
		aTrainCompanyJPanel.setBorder (tCorporateColorBorder);
		
		tCompanyInfoJPanel = new JPanel ();
		tCompanyInfoJPanel.setLayout (new BoxLayout (tCompanyInfoJPanel, BoxLayout.Y_AXIS));
		tCompanyAbbrev = new JLabel (aName);
		tCompanyInfoJPanel.add (tCompanyAbbrev);
		
		return tCompanyInfoJPanel;
	}

	private void addAssets (TrainCompany aTrainCompany, JPanel tCompanyInfoJPanel) {
		JLabel tTreasury;
		JLabel tTrains;
		TrainPortfolio tTrainPortfolio;
		
		tTreasury = new JLabel ("Treasury: " + Bank.formatCash (aTrainCompany.getTreasury ()));
		tCompanyInfoJPanel.add (tTreasury);
		
		tTrainPortfolio = aTrainCompany.getTrainPortfolio ();
		tTrains = new JLabel (tTrainPortfolio.getTrainList ());
		tCompanyInfoJPanel.add (tTrains);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.equals (DONE)) {
			handlePlayerDone ();
			checkAndHandleRoundEnds ();
		}
		if (tActionCommand.equals (UNDO)) {
			handlePlayerUndo ();
		}
	}

	public boolean checkAndHandleRoundEnds () {
		RoundManager tRoundManager;
		boolean tRoundEnds;
		
		tRoundManager = gameManager.getRoundManager ();
		tRoundEnds = tRoundManager.checkAndHandleInteruptionRoundEnds ();
		
		return tRoundEnds;
	}

	public int getCurrentPlayerIndex () {
		int tCurrentPlayerIndex;

		tCurrentPlayerIndex = formCompany.getCurrentPlayerIndex ();
		
		return tCurrentPlayerIndex;
	}
	
	public void handlePlayerDone () {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		boolean tAddAction;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tAddAction = true;
		formCompany.updateToNextPlayer (tPlayers, tAddAction);
	}
	
	public void handlePlayerUndo () {
		int tCurrentPlayerIndex;
		int tLastActionNumber;
		
		tLastActionNumber = gameManager.getActionNumber ();
		System.out.println ("Player hit UNDO ------ Undoing Action #" + tLastActionNumber);
		player.undoAction ();
		if (formCompany.getFormationState () != ActorI.ActionStates.NoState) {
			tCurrentPlayerIndex = getCurrentPlayerIndex ();
			formCompany.rebuildFormationPanel (tCurrentPlayerIndex);
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
	
	public boolean ends () {
		return false;
	}
	
	public ShareCompany findShareCompany (KButton aActivatedButton) {
		ShareCompany tFoundShareCompany;
		CorporationList tShareCompanies;
		
		tShareCompanies = gameManager.getShareCompanies ();
		tFoundShareCompany = tShareCompanies.findCompanyWithButton (aActivatedButton);
		
		return tFoundShareCompany;
	}

	public JPanel buildCompanyJPanel (PrivateCompany aPrivateCompany, boolean aActingPlayer) {
		return null;
	}
	
	public JPanel buildCompanyJPanel (MinorCompany aMinorCompany, boolean aActingPlayer) {
		return null;
	}

	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		return null;
	}
	
	public Action constructFormationAction (String aFullClassName, ActorI aActor) {
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		Class<?> tFormationActionClass;
		Constructor<?> tActionConstructor;
		Action tFormationAction;
	
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tRoundID = tRoundManager.getCurrentRoundOf ();

		tFormationAction = Action.NO_ACTION;
		System.out.println ("Construct Formation Action with name [" + aFullClassName + "]");
		try {
			tFormationActionClass = Class.forName (aFullClassName);
			tActionConstructor = tFormationActionClass.getConstructor (tRoundType.getClass (), tRoundID.getClass (), aActor.getClass ());
			tFormationAction = (Action) tActionConstructor.newInstance (tRoundType, tRoundID, aActor);
		} catch (Exception tException) {
			System.err.println ("Caught Exception Trying to create Formation Action Constructor with message ");
			tException.printStackTrace ();
		}

		return tFormationAction;
	}
}
