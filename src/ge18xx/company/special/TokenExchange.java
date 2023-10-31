package ge18xx.company.special;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.map.Location;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.TokenExchangeFinishedAction;
import ge18xx.utilities.GUI;

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

	int formingCompanyPresidentIndex;
	
	public TokenExchange (GameManager aGameManager, FormationPhase aTokenExchange, Player aPlayer,
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
		int tFormingCompanyID;
		boolean tIsPresident;
		
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
	public JPanel buildCompanyJPanel (ShareCompany aFormingShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		JLabel tHomeTokensLabel;
		Border tCorporateColorBorder;
		String tExchangeHomeTokens;
		String tAbbrevs;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));

		// NOTE:   
		// Exchange Home Tokens of (ABC, DEF, GHI...) for XXX Tokens   
		tCorporateColorBorder = aFormingShareCompany.setupBorder ();
		tShareCompanyJPanel.setBorder (tCorporateColorBorder);
		tExchangeHomeTokens = "Exchange Home Tokens of (";
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		tAbbrevs = GUI.EMPTY_STRING;
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					if (! GUI.EMPTY_STRING.equals (tAbbrevs)) {
						tAbbrevs += ", ";
					}
					tAbbrevs += collectHomeTokenInfo (tShareCompany);
				}
			}
		}
		tExchangeHomeTokens += tAbbrevs + ")";
		System.out.println ("Abbrevs: " + tAbbrevs + " Full Label: " + tExchangeHomeTokens);
		tHomeTokensLabel = new JLabel (tExchangeHomeTokens);
		tShareCompanyJPanel.add (tHomeTokensLabel);

//		tShareCompanyJPanel = super.buildCompanyJPanel (aShareCompany, aActingPlayer, tShareCompanyJPanel);
		
//		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);
		
		return tShareCompanyJPanel;
	}

	public String collectHomeTokenInfo (ShareCompany aShareCompany) {
		String tHomeTokenInfo;
		String tAbbrev;
		String tHomeMapCell1;
		String tHomeMapCell2;
		Location tHomeLocation1;
		Location tHomeLocation2;
		
		tAbbrev = aShareCompany.getAbbrev ();
		tHomeMapCell1 = aShareCompany.getCorpHome1MapID ();
		tHomeLocation1 = aShareCompany.getHomeLocation1 ();
		tHomeMapCell2 = aShareCompany.getCorpHome2MapID ();
		tHomeLocation2 = aShareCompany.getHomeLocation2 ();
		tHomeTokenInfo = tAbbrev + " [" + tHomeMapCell1 + ", " + tHomeLocation1 + "]";
		if (! GUI.EMPTY_STRING.equals (tHomeMapCell2)) {
			tHomeTokenInfo += " & [" + tHomeMapCell2 + ", " + tHomeLocation2 + "]";
		}
		
		return tHomeTokenInfo;
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
