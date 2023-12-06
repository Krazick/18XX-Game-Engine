package ge18xx.company.formation;

import java.awt.Component;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.AssetCollectionFinishedAction;
import geUtilities.GUI;
import swingDelays.KButton;

public class AssetCollection extends PlayerFormationPhase {
	private static final long serialVersionUID = 1L;
	KButton assetCollectionButton;
	JLabel assetCollectionLabel;

	public AssetCollection (GameManager aGameManager, FormationPhase aTokenExchange, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aTokenExchange, aPlayer, aActingPresident);
	}

	@Override
	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
//		JPanel tShareCompanyJPanel;
//		String tLabel;
//		
//		tShareCompanyJPanel = new JPanel ();
//		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));
//
//		tShareCompanyJPanel = super.buildCompanyJPanel (aShareCompany, aActingPlayer, tShareCompanyJPanel);
//		
//		tLabel = "Time for Asset Collection from:";
//		assetCollectionLabel = new JLabel (tLabel);
//		tShareCompanyJPanel.add (assetCollectionLabel);
//		assetCollectionLabel.setAlignmentX (Component.LEFT_ALIGNMENT);
//
		System.out.println (" Built the Company Panel for " + player.getName ());
		JPanel tCompanyInfoPanel;
		JPanel tFoldingCompaniesJPanel;
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
			if (player.isPresidentOf (tNewCompany)) {
				tIsPresident = true;
				
//				tShareCompanyJPanel = buildCompanyJPanel (tFormingShareCompany, aActingPlayer);
//				tCompanyInfoPanel.add (tShareCompanyJPanel);
//				tCompanyInfoPanel.add (Box.createVerticalStrut (5));
			}
		}

//		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);
		
		if (tIsPresident) {
			tFormingShareCompany = (ShareCompany) tNewCompany;
			tFoldingCompaniesJPanel = buildCompaniesJPanel (tFormingShareCompany, aActingPlayer);
			tCompanyInfoPanel.add (tFoldingCompaniesJPanel);
		} else {
			tNoteLabel = new JLabel (player.getName () + " is not the President of the " + tNewCompany.getAbbrev () + 
					". Nothing to do.");
			tCompanyInfoPanel.add (tNoteLabel);
		}

		return tCompanyInfoPanel;
	}

	public JPanel buildCompaniesJPanel (ShareCompany aFormingShareCompany, boolean aActingPlayer) {
		JPanel tCompaniesPanel;
		JPanel tAssetPanel;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		JLabel tAbbrev;
		JLabel tCash;
		JLabel tLicenses;
		

		tCompaniesPanel = new JPanel ();
		tCompaniesPanel.setLayout (new BoxLayout (tCompaniesPanel, BoxLayout.X_AXIS));
		tCompaniesPanel.add (Box.createHorizontalStrut (10));
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					tAssetPanel = new JPanel ();
					tAssetPanel.setLayout (new BoxLayout (tAssetPanel, BoxLayout.Y_AXIS));
					tAbbrev = new JLabel (tShareCompany.getAbbrev ());
					tAssetPanel.add (tAbbrev);
					tCash = new JLabel (Bank.formatCash (tShareCompany.getCash ()));
					tAssetPanel.add (tCash);
					tLicenses = new JLabel (tShareCompany.getLicenses ());
					tAssetPanel.add (tLicenses);
					tCompaniesPanel.add (tAssetPanel);
					tCompaniesPanel.add (Box.createHorizontalStrut (40));
				}
			}
		}
		
		return tCompaniesPanel;
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
