package ge18xx.company.special;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.AssetCollectionFinishedAction;

public class AssetCollection extends PlayerFormationPhase {
	private static final long serialVersionUID = 1L;

	public AssetCollection (GameManager aGameManager, FormationPhase aTokenExchange, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aTokenExchange, aPlayer, aActingPresident);
	}

	@Override
	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));

		tShareCompanyJPanel = super.buildCompanyJPanel (aShareCompany, aActingPlayer, tShareCompanyJPanel);
		
//		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);

		return tShareCompanyJPanel;
	}

	
	@Override
	public void handlePlayerDone () {
		AssetCollectionFinishedAction tAssetCollectionFinishedAction;
		String tOperatingRoundID;
		Player tNewPlayer;
		
		super.handlePlayerDone ();
//		if (formationPhase.getAllAssetsCollected ()) {
//			Generic functions to be finished
//		}
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tAssetCollectionFinishedAction = new AssetCollectionFinishedAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tNewPlayer = formationPhase.getCurrentPlayer ();

		tAssetCollectionFinishedAction.addUpdateToNextPlayerEffect (player, tNewPlayer);
		tAssetCollectionFinishedAction.setChainToPrevious (true);
		gameManager.addAction (tAssetCollectionFinishedAction);
	}
}
