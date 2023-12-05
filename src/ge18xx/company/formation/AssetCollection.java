package ge18xx.company.formation;

import java.awt.Component;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.AssetCollectionFinishedAction;
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
		JPanel tShareCompanyJPanel;
		String tLabel;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));

		tShareCompanyJPanel = super.buildCompanyJPanel (aShareCompany, aActingPlayer, tShareCompanyJPanel);
		
		tLabel = "Time for Asset Collection from:";
		assetCollectionLabel = new JLabel (tLabel);
		tShareCompanyJPanel.add (assetCollectionLabel);
		assetCollectionLabel.setAlignmentX (Component.LEFT_ALIGNMENT);

		System.out.println (" Built the Company Panel for " + player.getName ());
//		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);

		return tShareCompanyJPanel;
	}

	
//	@Override
//	public void handlePlayerDone () {
//		AssetCollectionFinishedAction tAssetCollectionFinishedAction;
//		String tOperatingRoundID;
//		Player tNewPlayer;
//		
//		super.handlePlayerDone ();
////		if (formationPhase.getAllAssetsCollected ()) {
////			Generic functions to be finished
////		}
//		tOperatingRoundID = gameManager.getOperatingRoundID ();
//		tAssetCollectionFinishedAction = new AssetCollectionFinishedAction (ActorI.ActionStates.OperatingRound, 
//				tOperatingRoundID, player);
//		tNewPlayer = formationPhase.getCurrentPlayer ();
//
//		tAssetCollectionFinishedAction.addUpdateToNextPlayerEffect (player, tNewPlayer);
//		tAssetCollectionFinishedAction.setChainToPrevious (true);
//		gameManager.addAction (tAssetCollectionFinishedAction);
//	}
	
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
