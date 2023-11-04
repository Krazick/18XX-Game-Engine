package ge18xx.company.formation;

import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
	public static String EXCHANGE_HOME_TOKEN = "ExchangeHomeTokens";
	public static String EXCHANGE_NON_HOME_TOKEN = "ExchangeNonHomeTokens";

//	int formingCompanyPresidentIndex;
	boolean homeTokensExchanges;
	boolean nonHomeTokenExchanges;
	JButton homeTokensExchange;
	JButton nonHomeTokensExchange;
	
	public TokenExchange (GameManager aGameManager, FormationPhase aTokenExchange, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aTokenExchange, aPlayer, aActingPresident);
		setHomeTokenExchanges (false);
		setNonHomeTokenExchanges (false);
	}

	public void setHomeTokenExchanges (boolean aHomeTokenExchanges) {
		homeTokensExchanges = aHomeTokenExchanges;
	}

	public void setNonHomeTokenExchanges (boolean aNonHomeTokenExchanges) {
		nonHomeTokenExchanges = aNonHomeTokenExchanges;
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
		JLabel tNonHomeTokensLabel;
		Border tCorporateColorBorder;
		String tExchangeHomeTokens;
		String tAbbrevs;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.Y_AXIS));

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
		
		buildSpecialButtons (aFormingShareCompany, tShareCompanyJPanel, aActingPlayer);
		tShareCompanyJPanel.add (homeTokensExchange);
		tNonHomeTokensLabel = new JLabel ("Non-Home Tokens Exchange Choices");
		tShareCompanyJPanel.add (tNonHomeTokensLabel);
		// Add Checkbox for each Non-Home Token
		
		tShareCompanyJPanel.add (nonHomeTokensExchange);
		
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
	
	public void buildSpecialButtons (ShareCompany aFormingShareCompany, JPanel tShareCompanyPanel, boolean aActingPlayer) {
		
		homeTokensExchange = formationPhase.buildSpecialButton ("Exchange all Home Tokens", EXCHANGE_HOME_TOKEN, 
					GUI.EMPTY_STRING, this);
		nonHomeTokensExchange = formationPhase.buildSpecialButton ("Exchange all Non-Home Tokens", EXCHANGE_NON_HOME_TOKEN, 
				GUI.EMPTY_STRING, this);	
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.equals (EXCHANGE_HOME_TOKEN)) {
			exchangeHomeTokens ();
		} else if (tActionCommand.equals (EXCHANGE_NON_HOME_TOKEN)) {
			exchangeNonHomeTokens ();
		}
	}
	
	@Override
	public void updateDoneButton () {
		String tToolTip;

		tToolTip = GUI.NO_TOOL_TIP;
		if (homeTokensExchanges) {
			done.setEnabled (false);
			tToolTip = "President already completed all Home Token exchanges";
		} else if (nonHomeTokenExchanges) {
			done.setEnabled (false);
			tToolTip = "President already completed all Non-Home Token exchanges";
		} else {
			done.setEnabled (false);
			tToolTip = "President has not completed all token exchanges";
		}
		done.setToolTipText (tToolTip);
	}

	public void exchangeHomeTokens () {
		System.out.println ("Ready to Exchange all Homes Tokens");
	}

	public void exchangeNonHomeTokens () {
		System.out.println ("Ready to Exchange all Non-Homes Tokens");
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
