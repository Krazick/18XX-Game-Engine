package ge18xx.company.special;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.TokenExchangeFinishedAction;

// Actions:
// 1. Collect a list of Revenue Centers that are home Centers for companies folding in
// 2. Collect a list of Revenue Centers (scan whole map) that have Tokens for Companies foleded in, without Home
// 3. Display a list of all Home Centers that MUST be upgraded to CGR with "DO IT" button
// 4. Display a list of non-home Center Revenue Centers
//     A. With Checkbox for each one (if selected - highlight on Map - change Revenue Center to have a SELECTED State
//     B. with one "DO IT" BUtton
// 5. When both DO IT buttons applied, then have "DONE" Button enabled
// 6. Each DO IT Button will replaced the appropriate Token with CGR Tokens
// 7. When DONE button is selected, remove Tokens not replaced from no-Home Center list
// 8. Clear all Revenue Center States and redraw map

// New Action: "Remove Token from Map"

public class TokenExchange extends PlayerFormationPhase {
	private static final long serialVersionUID = 1L;

	public TokenExchange (GameManager aGameManager, FormationPhase aTokenExchange, Player aPlayer,
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
		TokenExchangeFinishedAction tTokenExchangeFinishedAction;
		String tOperatingRoundID;
		Player tNewPlayer;
		
		super.handlePlayerDone ();

//		if (formationPhase.getAllPlayerTokensExchanged ()) {
//
//		}
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tTokenExchangeFinishedAction = new TokenExchangeFinishedAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tNewPlayer = formationPhase.getCurrentPlayer ();

		tTokenExchangeFinishedAction.addUpdateToNextPlayerEffect (player, tNewPlayer);
		tTokenExchangeFinishedAction.setChainToPrevious (true);
		gameManager.addAction (tTokenExchangeFinishedAction);
	}
}
