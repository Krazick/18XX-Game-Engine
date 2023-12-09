package ge18xx.company.formation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.AssetCollectionFinishedAction;
import ge18xx.train.Train;
import ge18xx.train.TrainInfo;
import geUtilities.GUI;
import swingDelays.KButton;

public class AssetCollection extends PlayerFormationPhase {
	public static final String CLAIM = "Claim";
	public static final String DECLINE = "Decline";
	public static final String COLLECT_CASH = "Collect Cash";
	public static final String COLLECT_LICENSES = "Collect Licenses";
	public static final List<KButton> NO_TRAIN_BUTTONS = null;
	private static final long serialVersionUID = 1L;
	List<KButton> trainButtons;
	KButton collectCashButton;
	KButton collectLicensesButton;
	JLabel assetCollectionLabel;
	protected JPanel companiesPanel;

	public AssetCollection (GameManager aGameManager, FormationPhase aTokenExchange, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aTokenExchange, aPlayer, aActingPresident);
	}

	@Override
	public JPanel buildPlayerCompaniesJPanel (Portfolio aPlayerPortfolio, boolean aActingPlayer) {
		JPanel tCompanyInfoPanel;
		JPanel tShareCompanyJPanel;
		JLabel tNoteLabel;
		Corporation tNewCompany;
		ShareCompany tFormingShareCompany;
		boolean tIsPresident;
		int tFormingCompanyID;

		tCompanyInfoPanel = new JPanel ();		
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tNewCompany = gameManager.getCorporationByID (tFormingCompanyID);
		tIsPresident = false;
		if (tNewCompany.isAShareCompany ()) {
			tFormingShareCompany = (ShareCompany) tNewCompany;
			if (player.isPresidentOf (tNewCompany)) {
				tIsPresident = true;
				tShareCompanyJPanel = buildCompanyJPanel (tFormingShareCompany, aActingPlayer);
				tCompanyInfoPanel.add (tShareCompanyJPanel);
				tCompanyInfoPanel.add (Box.createVerticalStrut (5));
			}
		}
		if (!tIsPresident) {
			tNoteLabel = new JLabel (player.getName () + " is not the President of the " + tNewCompany.getAbbrev () + 
					". Nothing to do.");
			tCompanyInfoPanel.add (tNoteLabel);
		}

		return tCompanyInfoPanel;
	}
	
	@Override
	public JPanel buildCompanyJPanel (ShareCompany aFormingCompany, boolean aActingPlayer) {
		JPanel tCompanyInfoPanel;
		JPanel tFoldingCompaniesJPanel;
		JLabel tPanelTitle;
		Border tEmptyBorder;
		
		tCompanyInfoPanel = new JPanel ();
		tCompanyInfoPanel.setLayout (new BoxLayout (tCompanyInfoPanel, BoxLayout.Y_AXIS));
		tCompanyInfoPanel.add (Box.createVerticalStrut (10));
		tEmptyBorder = BorderFactory.createEmptyBorder (10, 10, 10, 10);
		tCompanyInfoPanel.setBorder (tEmptyBorder);
		tPanelTitle = new JLabel ("Assets of Companies folding into " + aFormingCompany.getAbbrev ());
		tPanelTitle.setAlignmentX (CENTER_ALIGNMENT);
		tCompanyInfoPanel.add (Box.createVerticalStrut (10));
		tCompanyInfoPanel.add (tPanelTitle);
		
		tFoldingCompaniesJPanel = buildCompaniesJPanel (aFormingCompany, aActingPlayer);
		tFoldingCompaniesJPanel.setAlignmentX (CENTER_ALIGNMENT);
		tCompanyInfoPanel.add (tFoldingCompaniesJPanel);
		tCompanyInfoPanel.add (Box.createVerticalStrut (10));

		buildSpecialButtons (aFormingCompany, companiesPanel, aActingPlayer);

		return tCompanyInfoPanel;
	}

	public JPanel buildCompaniesJPanel (ShareCompany aFormingShareCompany, boolean aActingPlayer) {
		JPanel tAssetPanel;
		JPanel tLabelPanel;
		JPanel tButtonsPanel;
		JPanel tTrainButtonsPanel;
		JPanel tFullCompaniesPanel;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		JLabel tAbbrev;
		JLabel tCash;
		JLabel tLicenses;
		JLabel tTrains;
		
		JLabel tAbbrevLabel;
		JLabel tCashLabel;
		JLabel tLicensesLabel;
		JLabel tTrainsLabel;
		Border tBorder;

		tBorder = BorderFactory.createLineBorder (Color.black, 1);
		
		tFullCompaniesPanel = new JPanel ();
		tFullCompaniesPanel.setLayout (new BoxLayout (tFullCompaniesPanel, BoxLayout.Y_AXIS));

		companiesPanel = new JPanel ();
		companiesPanel.setLayout (new BoxLayout (companiesPanel, BoxLayout.X_AXIS));
		companiesPanel.setBorder (tBorder);
		
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		
		tLabelPanel = new JPanel ();
		tLabelPanel.setLayout (new BoxLayout (tLabelPanel, BoxLayout.Y_AXIS));
		tAbbrevLabel = new JLabel ("Abbrev:");
		tCashLabel = new JLabel ("Cash:");
		tLicensesLabel = new JLabel ("Licenses:");
		tTrainsLabel = new JLabel ("Trains:");
				
		tLabelPanel.add (Box.createVerticalGlue ());
		tLabelPanel.add (tAbbrevLabel);
		tLabelPanel.add (Box.createVerticalGlue ());
		tLabelPanel.add (tCashLabel);
		tLabelPanel.add (Box.createVerticalGlue ());
		if (gameManager.hasLicenses ()) {
			tLabelPanel.add (tLicensesLabel);
			tLabelPanel.add (Box.createVerticalGlue ());
		}
		tLabelPanel.add (tTrainsLabel);
		tLabelPanel.add (Box.createVerticalGlue ());

		companiesPanel.add (tLabelPanel);
		companiesPanel.add (Box.createHorizontalGlue ());

		trainButtons = new LinkedList<KButton> ();
		
		tButtonsPanel = new JPanel ();
		tButtonsPanel.setLayout (new BoxLayout (tButtonsPanel, BoxLayout.X_AXIS));
		tButtonsPanel.add (Box.createHorizontalGlue ());
		
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					tAssetPanel = new JPanel ();
					tAssetPanel.setLayout (new BoxLayout (tAssetPanel, BoxLayout.Y_AXIS));
					tAbbrev = new JLabel (tShareCompany.getAbbrev ());
					tAssetPanel.add (Box.createVerticalGlue ());
					tAssetPanel.add (tAbbrev);
					tCash = new JLabel (Bank.formatCash (tShareCompany.getCash ()));
					tAssetPanel.add (Box.createVerticalGlue ());
					tAssetPanel.add (tCash);
					tAssetPanel.add (Box.createVerticalGlue ());
					if (gameManager.hasLicenses ()) {
						tLicenses = new JLabel (tShareCompany.getLicenses ());
						tAssetPanel.add (tLicenses);
						tAssetPanel.add (Box.createVerticalGlue ());
					}
					
					tTrains = new JLabel (tShareCompany.getTrainList ());
					tAssetPanel.add (tTrains);
					tAssetPanel.add (Box.createVerticalGlue ());
					companiesPanel.add (Box.createHorizontalStrut (10));
					companiesPanel.add (tAssetPanel);
					companiesPanel.add (Box.createHorizontalGlue ());
					companiesPanel.add (Box.createHorizontalStrut (10));
					
					tTrainButtonsPanel = buildTrainButtons (tShareCompany);
					tButtonsPanel.add (tTrainButtonsPanel);
					tButtonsPanel.add (Box.createHorizontalGlue ());
					}
			}
		}
		
		tFullCompaniesPanel.add (companiesPanel);
		tFullCompaniesPanel.add (tButtonsPanel);
		
		return tFullCompaniesPanel;
	}
	
	public int sumAllFoldingCash () {
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		int tAllFoldingCash;
		
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		tAllFoldingCash = 0;
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					tAllFoldingCash += tShareCompany.getCash ();
				}
			}
		}
		
		return tAllFoldingCash;
	}
	
	public void buildSpecialButtons (ShareCompany aFormingShareCompany, JPanel aShareCompanyPanel, 
									boolean aActingPlayer) {
		String tToolTip;
		Player tCurrentPlayer;
		Player tFormingPresident;
		ShareCompany tFormingCompany;
		String tCollectCashText;
		String tCollectLicensesText;
		int tTotalCash;
		JLabel tSpacingLabel;
		JPanel tSpecialButtonsPanel;
		
		tFormingCompany = formationPhase.getFormingCompany ();
		tFormingPresident = (Player) tFormingCompany.getPresident ();
		tCurrentPlayer = formationPhase.getCurrentPlayer ();
		if (tCurrentPlayer == tFormingPresident) {
			tToolTip = GUI.EMPTY_STRING;
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tTotalCash = sumAllFoldingCash ();
		
		tCollectCashText = "Collect " + Bank.formatCash (tTotalCash);
		collectCashButton = formationPhase.buildSpecialButton (tCollectCashText, COLLECT_CASH, 
								tToolTip, this);
	
		tCollectLicensesText = "Collect Licenses";
		collectLicensesButton = formationPhase.buildSpecialButton (tCollectLicensesText, COLLECT_LICENSES, 
								tToolTip, this);	
		tSpacingLabel = new JLabel (" ");
		tSpecialButtonsPanel = new JPanel ();
		tSpecialButtonsPanel.setLayout (new BoxLayout (tSpecialButtonsPanel, BoxLayout.Y_AXIS));
		tSpecialButtonsPanel.add (tSpacingLabel);
		tSpecialButtonsPanel.add (collectCashButton);
		tSpecialButtonsPanel.add (tSpacingLabel);
		tSpecialButtonsPanel.add (collectLicensesButton);
	
		aShareCompanyPanel.add (tSpecialButtonsPanel);
		
		updateSpecialButtons (aActingPlayer);
	}

	public JPanel buildTrainButtons (ShareCompany aShareCompany) {
		KButton tButton;
		JLabel tNoTrainsLabel;
		JPanel tButtonsPanel;
		int tTrainCount;
		int tTrainIndex;
		Train tTrain;
		
		tButtonsPanel = new JPanel ();
		tButtonsPanel.setLayout (new BoxLayout (tButtonsPanel, BoxLayout.Y_AXIS));
		tTrainCount = aShareCompany.getTrainCount ();
		if (tTrainCount == 0) {
			tNoTrainsLabel = new JLabel ("NO TRAINS");
			tButtonsPanel.add (tNoTrainsLabel);
		} else {
			for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
				tTrain = aShareCompany.getTrain (tTrainIndex);
				if (tTrain.isPermanent ()) {
					tButton = buildTrainActionButton (CLAIM, aShareCompany, tButtonsPanel, tTrain);
					tButtonsPanel.add (tButton);
					tButton = buildTrainActionButton (DECLINE, aShareCompany, tButtonsPanel, tTrain);
					tButtonsPanel.add (tButton);
				} else {
					tButton = buildTrainActionButton (CLAIM, aShareCompany, tButtonsPanel, tTrain);
					tButtonsPanel.add (tButton);
					
					tButton = buildTrainActionButton (DECLINE, aShareCompany, tButtonsPanel, tTrain);
					tButtonsPanel.add (tButton);
				}
			}
		}
		
		return tButtonsPanel;
	}

	public KButton buildTrainActionButton (String aAction, ShareCompany aShareCompany, JPanel aButtonsPanel, 
						Train aTrain) {
		KButton tButton;
		String tName;
		String tActionCommand;
		
		tName = aAction + " " + aShareCompany.getAbbrev () + " " + aTrain.getName ();
		tActionCommand = aAction + " " + aShareCompany.getAbbrev () + " " + aTrain.getName ();
		if (aTrain.isPermanent ()) {
			tActionCommand += " " + TrainInfo.PERMANENT;
		}
		tButton = new KButton (tName);
		tButton.setActionCommand (tActionCommand);
		tButton.addActionListener (this);
		trainButtons.add (tButton);
		
		return tButton;
	}
	
	public void updateTrainButtons (ShareCompany aShareCompany, boolean aActingPlayer) {
		boolean tTrainTight;
		String tActionCommand;
		String tCommandParts [];
		String tTrainName;
		String tCompanyAbbrev;
		String tToolTipText;
		ShareCompany tFoldingCompany;
		Train tTrain;
		boolean tStillPermanentAvailable;
		
		if (aActingPlayer) {
			tTrainTight = aShareCompany.atTrainLimit ();
			if (trainButtons != NO_TRAIN_BUTTONS) {
				tStillPermanentAvailable = stillPermanentAvailable ();
				for (KButton tTrainButton : trainButtons) {
					tActionCommand = tTrainButton.getActionCommand ();
					tCommandParts = tActionCommand.split (" ");
					tToolTipText = GUI.EMPTY_STRING;
					tCompanyAbbrev = tCommandParts [1];
					tFoldingCompany = gameManager.getShareCompany (tCompanyAbbrev);
					tTrainName = tCommandParts [2];
					tTrain = tFoldingCompany.getTrain (tTrainName);
					if (tCommandParts [0].equals (CLAIM)) {
						if (tTrainTight) {
							tTrainButton.setEnabled (false);
							tToolTipText = "Company " + aShareCompany.getAbbrev () + " is at the Train Limit, cannot be Claimed.";
						} else {
							if (tTrain.isPermanent ()) {
								tTrainButton.setEnabled (true);
								tToolTipText = "Company " + aShareCompany.getAbbrev () + " can add a train, and this is a Permanent Train that can be Claimed.";
							} else if (tStillPermanentAvailable) {
								tTrainButton.setEnabled (false);
								tToolTipText = "Company " + aShareCompany.getAbbrev () + 
											" other Permanent Trains must be claimed first.";
							} else {
								tTrainButton.setEnabled (true);
								tToolTipText = "Company " + aShareCompany.getAbbrev () + " can add a train and this train can be Claimed.";
							}
						}
					} else {
						if (tTrainTight) {
							tTrainButton.setEnabled (true);
							tToolTipText = "Company " + aShareCompany.getAbbrev () + " is at the Train Limit, must be Declined";
						} else if (tTrain.isPermanent ()) {
							tTrainButton.setEnabled (false);
							tToolTipText = "Company " + aShareCompany.getAbbrev () + 
									" is not at the Train Limit, and this is a Permanent Train, cannot be Declined.";
						} else {
							tTrainButton.setEnabled (true);
							tToolTipText = "Company " + aShareCompany.getAbbrev () + " can add a Train, this train can be Declined.";	
						}
					}
					tTrainButton.setToolTipText (tToolTipText);
				}
			}
		} else {
			if (trainButtons != NO_TRAIN_BUTTONS) {
				for (KButton tTrainButton : trainButtons) {
					tTrainButton.setEnabled (false);
					tTrainButton.setToolTipText (NOT_ACTING_PRESIDENT);
				}
			}
		}
	}
	
	public boolean stillPermanentAvailable () {
		boolean tStillPermanentAvailable;
		String tActionCommand;
		String tCommandParts [];

		tStillPermanentAvailable = false;
		if (trainButtons != NO_TRAIN_BUTTONS) {
			for (KButton tTrainButton : trainButtons) {
				tActionCommand = tTrainButton.getActionCommand ();
				tCommandParts = tActionCommand.split (" ");
				if (tCommandParts.length > 3) {
					if (tCommandParts [3].equals (TrainInfo.PERMANENT)) {
						tStillPermanentAvailable = true;
					}
				}
			}
		}
		
		return tStillPermanentAvailable;
	}
		
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		
		if (tActionCommand.startsWith (CLAIM)) {
			System.out.println ("Company wants to " + tActionCommand);
		} else if (tActionCommand.startsWith (DECLINE)) {
			System.out.println ("Company wants to " + tActionCommand);			
		} else if (tActionCommand.equals (COLLECT_CASH)) {
			System.out.println ("Company wants to " + tActionCommand);			
		} else if (tActionCommand.equals (COLLECT_LICENSES)) {
			System.out.println ("Company wants to " + tActionCommand);			
		}
	}
	
	@Override
	public void updateSpecialButtons (boolean aActingPlayer) {
		int tFormingCompanyID;
		ShareCompany tFormingCompany;
		Corporation tCompany;
		
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tCompany = gameManager.getCorporationByID (tFormingCompanyID);
		if (tCompany.isAShareCompany ()) {
			tFormingCompany = (ShareCompany) tCompany;
			updateTrainButtons (tFormingCompany, aActingPlayer);
			updateCollectCash (aActingPlayer);
			updateCollectLicenses (aActingPlayer);
		}
	}

	public boolean anyCashToCollect () {
		boolean tAnyCashToCollect;
		int tTotalCash;
		
		tTotalCash = sumAllFoldingCash ();
		if (tTotalCash > 0) {
			tAnyCashToCollect = true;
		} else {
			tAnyCashToCollect = false;
		}
		
		return tAnyCashToCollect;
	}

	public boolean anyLicensesToCollect () {
		boolean tAnyLicensesToCollect;
		boolean tHasLicense;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		int tAllFoldingCash;

		tAnyLicensesToCollect = false;
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		tAllFoldingCash = 0;
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					tHasLicense = tShareCompany.hasAnyLicense ();
					if (tHasLicense) {
						tAnyLicensesToCollect = true;
					}
				}
			}
		}

		
		return tAnyLicensesToCollect;
	}
	
	public void updateCollectCash (boolean aActingPlayer) {
		if (collectCashButton != GUI.NO_BUTTON) { 
			if (aActingPlayer) {
				if (anyCashToCollect ()) {
					collectCashButton.setEnabled (true);
					collectCashButton.setToolTipText (GUI.EMPTY_STRING);
				} else {
					collectCashButton.setEnabled (false);
					collectCashButton.setToolTipText ("No Cash to collect");
				}
			} else {
				collectCashButton.setEnabled (false);
				collectCashButton.setToolTipText (NOT_ACTING_PRESIDENT);
			}
		}
	}
	
	public void updateCollectLicenses (boolean aActingPlayer) {
		if (collectLicensesButton != GUI.NO_BUTTON) { 
			if (aActingPlayer) {
				if (anyLicensesToCollect ()) {
					collectLicensesButton.setEnabled (true);
					collectLicensesButton.setToolTipText (GUI.EMPTY_STRING);
				} else {
					collectLicensesButton.setEnabled (false);
					collectLicensesButton.setToolTipText ("No Licenses to collect");
					
				}
			} else {
				collectLicensesButton.setEnabled (false);
				collectLicensesButton.setToolTipText (NOT_ACTING_PRESIDENT);
			}
		}
	}
	
	@Override
	public void updateContinueButton (boolean aActingPlayer) {
		continueButton.setVisible (false);
	}

	@Override
	public void handlePlayerDone () {
		AssetCollectionFinishedAction tAssetCollectionFinishedAction;
		String tOperatingRoundID;
		PlayerManager tPlayerManager;
		List<Player> tPlayers;
		ActorI.ActionStates tOldState;
		
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tAssetCollectionFinishedAction = new AssetCollectionFinishedAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		
		for (Player tPlayer : tPlayers) {
			tOldState = tPlayer.getPrimaryActionState ();
			tPlayer.setPrimaryActionState (formationPhase.formationState);
			tAssetCollectionFinishedAction.addStateChangeEffect (tPlayer, tOldState, formationPhase.formationState);
		}

		tAssetCollectionFinishedAction.setChainToPrevious (true);
		gameManager.addAction (tAssetCollectionFinishedAction);
	}
}
