package ge18xx.company.formation;

import java.util.List;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.StartFormationAction;

public class FormPrussian extends FormCompany {
	int currentPlayerIndex;

	public FormPrussian (GameManager aGameManager) {
		super (aGameManager);
		
		String tFullFrameTitle;
		
		tFullFrameTitle = setFormationState (ActorI.ActionStates.Nationalization);
		System.out.println ("Initiate Form Prussian, Frame Title: " + tFullFrameTitle +
				" Game: " + gameManager.getActiveGameName ());
		buildAllPlayers (tFullFrameTitle);
		
		gameManager.setTriggerClass (this);
		gameManager.setTriggerFormation (this);
	}
	
	@Override
	public void prepareFormation (StartFormationAction aStartFormationAction) {
		showFormationFrame ();
	}
	
	@Override
	public void rebuildFormationPanel () {
		int tCurrentPlayerIndex;
		
		tCurrentPlayerIndex = currentPlayerIndex;
		if (tCurrentPlayerIndex >= 0) {
			rebuildFormationPanel (tCurrentPlayerIndex);
		}
	}
		
	@Override
	public void setupPlayers (PlayerManager aPlayerManager, List<Player> aPlayers) {
		int tCurrentPlayerIndex;
		
		findActingPresident ();
		tCurrentPlayerIndex = aPlayerManager.getPlayerIndex (actingPresident);
		setCurrentPlayerIndex (tCurrentPlayerIndex);
		updatePlayers (aPlayers, actingPresident);
	}

	@Override
	public void updatePlayers (List<Player> aPlayers, Player aActingPresident) {
		super.updatePlayers (aPlayers, aActingPresident);

	}
	
	@Override
	public boolean isInterrupting () {
		return true;
	}
	
	public int getPercentageForExchange () {
		int tPercentage;
		
		tPercentage = 10;
		
		return tPercentage;
	}
}
