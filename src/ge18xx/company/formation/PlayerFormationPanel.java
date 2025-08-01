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
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.CashTransferAction;
import ge18xx.round.action.FormationRoundAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.train.Train;
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
		
		buttonsPanel = new JPanel ();
		buttonsPanel.setLayout (new BoxLayout (buttonsPanel, BoxLayout.Y_AXIS));

		buttonsPanel.add (Box.createVerticalGlue ());
		buttonsPanel.add (Box.createVerticalStrut (10));
		
		continueButton = formCompany.buildSpecialButton (CONTINUE, aContinueCommand, tToolTip, this);
		buttonsPanel.add (continueButton);
		buttonsPanel.add (Box.createVerticalStrut (10));
		
		doneButton = formCompany.buildSpecialButton (DONE, DONE, tToolTip, this);
		buttonsPanel.add (doneButton);
		buttonsPanel.add (Box.createVerticalStrut (10));
		
		undoButton = formCompany.buildSpecialButton (UNDO, UNDO, tToolTip, this);
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

	protected Corporation getExchangeCorporation (MinorCompany aMinorCompany) {
		int tExchangeID;
		Corporation tCorporation;
		
		tExchangeID = aMinorCompany.getUpgradeTo ();
		tCorporation = gameManager.getCorporationByID (tExchangeID);
		
		return tCorporation;
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
	
	public PrivateCompany findPrivateCompany (KButton aActivatedButton) {
		PrivateCompany tFoundPrivateCompany;
		CorporationList tPrivateCompanies;
		
		tFoundPrivateCompany = PrivateCompany.NO_PRIVATE_COMPANY;
		tPrivateCompanies = gameManager.getPrivates ();
		if (tPrivateCompanies != CorporationList.NO_CORPORATION_LIST) {
			tFoundPrivateCompany = (PrivateCompany) tPrivateCompanies.findCompanyWithButton (aActivatedButton);
		}
		
		return tFoundPrivateCompany;
	}

	public MinorCompany findMinorCompany (KButton aActivatedButton) {
		MinorCompany tFoundMinorCompany;
		CorporationList tMinorCompanies;
		
		tFoundMinorCompany = MinorCompany.NO_MINOR_COMPANY;
		tMinorCompanies = gameManager.getMinors ();
		if (tMinorCompanies != CorporationList.NO_CORPORATION_LIST) {
			tFoundMinorCompany = (MinorCompany) tMinorCompanies.findCompanyWithButton (aActivatedButton);
		}
		
		return tFoundMinorCompany;
	}
	
	public ShareCompany findShareCompany (KButton aActivatedButton) {
		ShareCompany tFoundShareCompany;
		CorporationList tShareCompanies;
		
		tShareCompanies = gameManager.getShareCompanies ();
		tFoundShareCompany = (ShareCompany) tShareCompanies.findCompanyWithButton (aActivatedButton);
		
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

	public void transferShareToClosed (PortfolioHolderI aFromActor, String aFromName, Certificate aCertificate, 
						TransferOwnershipAction aTransferOwnershipAction) {
		Portfolio tPlayerPortfolio;
		Portfolio tClosedPortfolio;
		Bank tBank;
	
		tBank = gameManager.getBank ();
		tClosedPortfolio = tBank.getClosedPortfolio ();
		tPlayerPortfolio = aFromActor.getPortfolio ();
		tClosedPortfolio.transferOneCertificateOwnership (tPlayerPortfolio, aCertificate);
		aTransferOwnershipAction.addTransferOwnershipEffect (aFromActor, aFromName, aCertificate, 
						tBank, Bank.CLOSED);
	}

	public void transferShareToClosed (PortfolioHolderI aFromActor, Certificate aCertificate, 
						TransferOwnershipAction aTransferOwnershipAction) {
		transferShareToClosed (aFromActor, aFromActor.getName (), aCertificate, aTransferOwnershipAction);
	}
	
	public String getFormingAbbrev () {
		Corporation tFormingCompany;
		int tFormingCompanyID;
		String tFormingAbbrev;
	
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tFormingCompany = gameManager.getCorporationByID (tFormingCompanyID);
		tFormingAbbrev = tFormingCompany.getAbbrev ();

		return tFormingAbbrev;
	}

	public void transferShare (PortfolioHolderI aFromActor, String aFromNickName, ActorI aToActor, 
				String aToNickName, Certificate aCertificate, TransferOwnershipAction aTransferOwnershipAction) {
		Portfolio tFromActorPortfolio;
		Portfolio tToActorPortfolio;
		PortfolioHolderI tToHolder;
	
		tFromActorPortfolio = aFromActor.getPortfolio ();
		tToHolder = (PortfolioHolderI) aToActor;
		aToNickName = aToActor.getName ();
		tToActorPortfolio = tToHolder.getPortfolio ();
		tToActorPortfolio.transferOneCertificateOwnership (tFromActorPortfolio,  aCertificate);
		aTransferOwnershipAction.addTransferOwnershipEffect (aFromActor, aFromNickName, aCertificate, 
					aToActor, aToNickName);
	}

	public void transferShare (PortfolioHolderI aFromActor, String aFromName, ActorI aToActor, 
				Certificate aCertificate, TransferOwnershipAction aTransferOwnershipAction) {
		String tToName;
		
		tToName = ActorI.NO_NAME;
		transferShare (aFromActor, aFromName, aToActor, tToName, aCertificate, aTransferOwnershipAction);
	}

	public void updateCorporationOwnership (Certificate tFormedCertificate) {
		// If at least one FormedCertificate has been transfered to a Player,
		// Need to update the Corporation Ownership. But only need to do this once after all done
		// The TransferOwnershipEffect applies this to remote clients
		// Don't need to create the Effect
		if (tFormedCertificate != Certificate.NO_CERTIFICATE) {
			tFormedCertificate.updateCorporationOwnership ();
		}
	}

	protected void transferAllCash (TrainCompany aSourceCompany, ShareCompany aFormingShareCompany, 
						CashTransferAction aCashTransferAction) {
		int tCashAmount;
		
		tCashAmount = aSourceCompany.getCash ();
		aSourceCompany.addCash (-tCashAmount);
		aFormingShareCompany.addCash (tCashAmount);
		aCashTransferAction.addCashTransferEffect (aSourceCompany, aFormingShareCompany, tCashAmount);
	}

	protected void transferAllTrains (TrainCompany aTrainCompany, ShareCompany aFormingShareCompany,  
						TransferOwnershipAction aTransferOwnershipAction) {
		TrainPortfolio tTrainPortfolio;
		Train tTrain;
		int tTrainCount;
		
		tTrainPortfolio = aTrainCompany.getTrainPortfolio ();
		tTrainCount = tTrainPortfolio.getTrainCount ();
		if (tTrainCount > 0) {
			for (int tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
				tTrain = tTrainPortfolio.getTrainAt (tTrainIndex);
				transferATrain (aTrainCompany, aFormingShareCompany, tTrain, aTransferOwnershipAction);
			}
		}
	}

	protected MarketCell getClosestMarketCell (int aNewParPrice) {
		Market tMarket;
		MarketCell tParPriceMarketCell;
		
		tMarket = gameManager.getMarket ();
		tParPriceMarketCell = tMarket.getClosestMarketCell (aNewParPrice, 0);
		
		return tParPriceMarketCell;
	}

	protected void transferATrain (TrainCompany aTrainCompany, ShareCompany aFormingShareCompany, Train aTrain, 
			TransferOwnershipAction aTransferOwnershipAction) {
		aTrainCompany.removeTrain (aTrain.getName ());
		aFormingShareCompany.addTrain (aTrain);
		aTransferOwnershipAction.addTransferTrainEffect (aTrainCompany, aTrain, aFormingShareCompany);
	}

	protected boolean givePresidentCertificate (Portfolio aBankPortfolio, int aPercentage, TransferOwnershipAction aTransferOwnershipAction, Corporation aFormingCompany) {
		Certificate tPresidentCertificate;
		Certificate tExchangeCertificate;
		Certificate tPresidentZeroCertificate;
		Certificate tRemovedCertificate;
		Portfolio tOldPresidentPortfolio;
		Portfolio tPlayerPortfolio;
		PortfolioHolderI tCurrentPresident;
		Bank tBank;
		int tPercentageForExchange;
		String tFormingAbbrev;
		boolean tNewPresident;
		FormCGR tFormCGR;
		
		tFormCGR = (FormCGR) formCompany;
		tBank = gameManager.getBank ();
		tPlayerPortfolio = player.getPortfolio ();
		tCurrentPresident = aFormingCompany.getPresident ();
		tFormingAbbrev = aFormingCompany.getAbbrev ();
		tPercentageForExchange = tFormCGR.getPercentageForExchange ();
		tPresidentCertificate = aBankPortfolio.getCertificate (tFormingAbbrev, tPercentageForExchange * 2, true);
		tNewPresident = true;
		if (tPresidentCertificate != Certificate.NO_CERTIFICATE) {
			transferShare (tBank, Bank.IPO, player, tPresidentCertificate, aTransferOwnershipAction);
			tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
			transferShare (player, player.getName (), tBank, Bank.IPO, tExchangeCertificate, aTransferOwnershipAction);
			tExchangeCertificate = tPlayerPortfolio.getCertificate (tFormingAbbrev, aPercentage, false);
			if (tExchangeCertificate != Certificate.NO_CERTIFICATE) {
				transferShare (player, player.getName (), tBank, Bank.IPO, tExchangeCertificate, aTransferOwnershipAction);
			}
			if (tCurrentPresident != PortfolioHolderI.NO_PORTFOLIO_HOLDER) {
				tOldPresidentPortfolio = tCurrentPresident.getPortfolio ();
				tPresidentZeroCertificate = tOldPresidentPortfolio.getCertificate (tFormingAbbrev,
						Certificate.NO_PERCENTAGE, tNewPresident);
				if (tPresidentZeroCertificate != Certificate.NO_CERTIFICATE) {
					System.out.println ("Found Cert found to remove from " + tCurrentPresident.getName ());
					tRemovedCertificate = tOldPresidentPortfolio.getThisCertificate (tPresidentZeroCertificate);
					System.out.println ("Found 0% Cert in old President Portfolio - Removed");
					aTransferOwnershipAction.addDeleteCertificateEffet (tCurrentPresident, tRemovedCertificate,
									tCurrentPresident);
				}
			}
		}
		tFormCGR.setFormingPresidentAssigned  (tNewPresident);
		aTransferOwnershipAction.SetFormingPresidentAssignedEffect (aFormingCompany, tNewPresident);
		
		return tNewPresident;
	}

	public void confirmFormingPresident (TransferOwnershipAction aTransferOwnershipAction) {
		PlayerManager tPlayerManager;
		Player tCurrentPresident;
		PortfolioHolderI tCurrentHolder;
		Corporation tCorporation;
		ShareCompany tFormingCompany;
		int tFormingCompanyID;
		FormCompany tFormCGR;
		
		tFormCGR = (FormCompany) formCompany;
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tCorporation = gameManager.getCorporationByID (tFormingCompanyID);
		if (tCorporation.isAShareCompany ()) {
			tFormingCompany = (ShareCompany) tCorporation;
			tPlayerManager = gameManager.getPlayerManager ();
			tCurrentHolder = tFormingCompany.getPresident ();
			if (tCurrentHolder.isAPlayer ()) {
				tCurrentPresident = (Player) tCurrentHolder;
				tPlayerManager.handlePresidentialTransfer (aTransferOwnershipAction, tFormingCompany, tCurrentPresident);
				aTransferOwnershipAction.setChainToPrevious (true);
				tFormCGR.rebuildFormationPanel ();
			} else {
				System.err.println ("The Current President is not a Player");
			}
		} else {
			System.err.println ("The Forming Company ID found is NOT a Share Company");
		}
	}

	public boolean assignPresident (Portfolio aBankPortfolio, int aPercentage, ShareCompany aFormingCompany, TransferOwnershipAction aTransferOwnershipAction) {
		Certificate tPresidentCertificate;
		Certificate tPresidentZeroCertificate;
		Portfolio tPlayerPortfolio;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		Bank tBank;
		int tOwnsPercentage;
		int tPresidentOwnedPercentage;
		int tPresidentCertificatePercentage;
		int tFindPercentage;
		boolean tNewPresident;
		FormCGR tFormCGR;
		
		tFormCGR = (FormCGR) formCompany;
		tBank = gameManager.getBank ();
		tFindPercentage = tFormCGR.getPercentageForExchange () * 2;
		tPresidentOwnedPercentage = aFormingCompany.getPresidentOwnedPercent ();
		tPresidentCertificate = aFormingCompany.getPresidentCertificate (tFindPercentage);
		tPresidentCertificatePercentage = tPresidentCertificate.getPercentage ();
		tPlayerPortfolio = player.getPortfolio ();
		tOwnsPercentage = tPlayerPortfolio.getPercentageFor (aFormingCompany);
		tNewPresident = false;
		if (tOwnsPercentage >= tPresidentCertificatePercentage) {
			if (tOwnsPercentage > tPresidentOwnedPercentage) {
				tNewPresident = givePresidentCertificate (aBankPortfolio, aPercentage, aTransferOwnershipAction,
						aFormingCompany);
			}
		} else if (tPresidentOwnedPercentage == Certificate.NO_PERCENTAGE) {
			tPresidentZeroCertificate = new Certificate (aFormingCompany, true, Certificate.NO_PERCENTAGE,
							tPlayerPortfolio);
			tPlayerPortfolio.addCertificate (tPresidentZeroCertificate);
			aTransferOwnershipAction.addCreateNewCertificateEffet (tBank, tPresidentZeroCertificate, player);
			tNewPresident = true;
		}
		confirmFormingPresident (aTransferOwnershipAction);
		if (! aFormingCompany.willFloat ()) {
			tOldState = aFormingCompany.getStatus ();
			aFormingCompany.resetStatus (ActorI.ActionStates.WillFloat);
			tNewState = aFormingCompany.getStatus ();
			aTransferOwnershipAction.addChangeCorporationStatusEffect (aFormingCompany, tOldState, tNewState);
		}
		
		return tNewPresident;
	}
}
