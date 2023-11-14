package ge18xx.company.formation;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.MapToken;
import ge18xx.company.ShareCompany;
import ge18xx.company.TokenCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ReplaceTokenAction;
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
	boolean homeTokensExchanges;
	boolean nonHomeTokenExchanges;
	JButton homeTokensExchange;
	JButton nonHomeTokensExchange;
	List<String> homeMapCellIDs;
	List<String> nonHomeMapCellIDs;
	ReplaceTokenAction replaceTokenAction;
	
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
		String tTokenLocations;
		String tNonHomeStations;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.Y_AXIS));

		homeMapCellIDs = new LinkedList<String> ();
		nonHomeMapCellIDs = new LinkedList<String> ();
		// NOTE:   
		// Exchange Home Tokens of (ABC, DEF, GHI...) for XXX Tokens   
		tCorporateColorBorder = aFormingShareCompany.setupBorder ();
		tShareCompanyJPanel.setBorder (tCorporateColorBorder);
		tExchangeHomeTokens = "Exchange Home Tokens of (";
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		tTokenLocations = GUI.EMPTY_STRING;
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					if (! GUI.EMPTY_STRING.equals (tTokenLocations)) {
						tTokenLocations += ", ";
					}
					tTokenLocations += collectHomeTokenInfo (tShareCompany);
				}
			}
		}
		tExchangeHomeTokens += tTokenLocations + ")";
		System.out.println (tExchangeHomeTokens);
		tHomeTokensLabel = new JLabel (tExchangeHomeTokens);
		tShareCompanyJPanel.add (tHomeTokensLabel);
		
		buildSpecialButtons (aFormingShareCompany, tShareCompanyJPanel, aActingPlayer);
		tShareCompanyJPanel.add (homeTokensExchange);
		
		collectNonHomeMapTokens ();
		tNonHomeTokensLabel = new JLabel ("Non-Home Tokens Exchange Choices");
		tShareCompanyJPanel.add (tNonHomeTokensLabel);
		tNonHomeStations = nonHomeMapCellIDs.toString ();
		System.out.println (tNonHomeStations);
		
		// Add Checkbox for each Non-Home Token
		
		tShareCompanyJPanel.add (nonHomeTokensExchange);
		
		return tShareCompanyJPanel;
	}

	public String collectHomeTokenInfo (TokenCompany aTokenCompany) {
		String tHomeTokenInfo;
		String tAbbrev;
		String tHomeMapCell1;
		String tHomeMapCell2;
		String tTokenLocation;
		int tCorpID;
		HexMap tHexMap;
		
		tHexMap = gameManager.getGameMap ();
		tAbbrev = aTokenCompany.getAbbrev ();
		tHomeMapCell1 = aTokenCompany.getCorpHome1MapID ();
		tCorpID = aTokenCompany.getID ();
		tTokenLocation = tHexMap.getTokenLocation (tHomeMapCell1, tAbbrev, tCorpID);
		homeMapCellIDs.add (tTokenLocation);
		tHomeTokenInfo = "[" + tTokenLocation + "]";
		
		tHomeMapCell2 = aTokenCompany.getCorpHome2MapID ();
		if (! GUI.EMPTY_STRING.equals (tHomeMapCell2)) {
			tTokenLocation = tHexMap.getTokenLocation (tHomeMapCell2, tAbbrev, tCorpID);
			if (! homeMapCellIDs.contains (tTokenLocation)) {
				tHomeTokenInfo += " & [" + tTokenLocation + "]";
				homeMapCellIDs.add (tTokenLocation);
			}
		}
		
		return tHomeTokenInfo;
	}
	
	
	public void collectNonHomeMapTokens () {
		HexMap tHexMap;
		int tCorpID;
		int tShareIndex;
		int tMinorIndex;
		int tMinorCount;
		int tShareCount;
		Corporation tTokenCompany;
		CorporationList tShareCompanies;
		CorporationList tMinorCompanies;
		String tAbbrev;

		tHexMap = gameManager.getGameMap ();
		tTokenCompany = Corporation.NO_CORPORATION;
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tTokenCompany = (TokenCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tTokenCompany != Corporation.NO_CORPORATION) {
				if (tTokenCompany.willFold ()) {
					tCorpID = tTokenCompany.getID ();
					tAbbrev = tTokenCompany.getAbbrev ();
					tHexMap.collectNonHomeMapCellIDs (tCorpID, tAbbrev, homeMapCellIDs, nonHomeMapCellIDs);
				}
			}
		}
		if (tTokenCompany == Corporation.NO_CORPORATION) {
			tMinorCompanies = gameManager.getMinorCompanies ();
			tMinorCount = tMinorCompanies.getCorporationCount ();
			for (tMinorIndex = 0; tMinorIndex < tMinorCount; tMinorIndex++) {
				tTokenCompany = (TokenCompany) tMinorCompanies.getCorporation (tMinorIndex);
				if (tTokenCompany != Corporation.NO_CORPORATION) {
					if (tTokenCompany.willFold ()) {
						tCorpID = tTokenCompany.getID ();
						tAbbrev = tTokenCompany.getAbbrev ();
						tHexMap.collectNonHomeMapCellIDs (tCorpID, tAbbrev, homeMapCellIDs, nonHomeMapCellIDs);
					}
				}
			}

		}
	}
	
	public void buildSpecialButtons (ShareCompany aFormingShareCompany, JPanel tShareCompanyPanel, boolean aActingPlayer) {
		String tToolTip;
		Player tCurrentPlayer;
		Player tFormingPresident;
		ShareCompany tFormingCompany;
		
		tFormingCompany = formationPhase.getFormingCompany ();
		tFormingPresident = (Player) tFormingCompany.getPresident ();
		tCurrentPlayer = formationPhase.getCurrentPlayer ();
		if (tCurrentPlayer == tFormingPresident) {
			tToolTip = GUI.EMPTY_STRING;
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		homeTokensExchange = formationPhase.buildSpecialButton ("Exchange all Home Tokens", EXCHANGE_HOME_TOKEN, 
				tToolTip, this);
		nonHomeTokensExchange = formationPhase.buildSpecialButton ("Exchange all Non-Home Tokens", EXCHANGE_NON_HOME_TOKEN, 
				tToolTip, this);	
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		
		if (tActionCommand.equals (EXCHANGE_HOME_TOKEN)) {
			exchangeHomeTokens ();
		} else if (tActionCommand.equals (EXCHANGE_NON_HOME_TOKEN)) {
			exchangeNonHomeTokens ();
		} else if (tActionCommand.equals (DONE)) {
			handlePlayerDone ();
		} else if (tActionCommand.equals (FormationPhase.ASSET_COLLECTION)) {
			formationPhase.allPlayersHandled  ();
		}
	}
	
	@Override
	public void updateDoneButton () {
		String tToolTip;

		tToolTip = GUI.NO_TOOL_TIP;
		if (homeTokensExchanges) {
			doneButton.setEnabled (false);
			tToolTip = "President already completed all Home Token exchanges";
		} else if (nonHomeTokenExchanges) {
			doneButton.setEnabled (false);
			tToolTip = "President already completed all Non-Home Token exchanges";
		} else {
			doneButton.setEnabled (false);
			tToolTip = "President has not completed all token exchanges";
		}
		doneButton.setToolTipText (tToolTip);
	}

	public void exchangeHomeTokens () {
		HexMap tHexMap;
		MapToken tNewMapToken;
		ShareCompany tFormingShareCompany;
		TokenCompany tFoldingCompany;
		String tCompanyAbbrev;
		CorporationList tShareCompanies;
		CorporationList tMinorCompanies;
		
		tHexMap = gameManager.getGameMap ();
		tShareCompanies = gameManager.getShareCompanies ();
		tMinorCompanies = gameManager.getMinorCompanies ();
		tFormingShareCompany = formationPhase.getFormingCompany ();
		tNewMapToken = tFormingShareCompany.getLastMapToken ();
				
		System.out.println ("Ready to Exchange all Homes Tokens");
		for (String tHomeMapCellID : homeMapCellIDs) {
			System.out.println ("Swap out Token on MapCellID " + tHomeMapCellID);
			tCompanyAbbrev = tHexMap.getCompanyAbbrev (tHomeMapCellID);
			tFoldingCompany = (TokenCompany) tShareCompanies.getCorporation (tCompanyAbbrev);
			if (tFoldingCompany == Corporation.NO_CORPORATION) {
				tFoldingCompany = (TokenCompany) tMinorCompanies.getCorporation (tCompanyAbbrev);
			}
			tHexMap.replaceMapToken (tHomeMapCellID, tNewMapToken, tFoldingCompany, replaceTokenAction);
		}
	}

	public void prepareAction (TokenCompany aTokenCompany) {
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundType ();
		tRoundID = tRoundManager.getCurrentRoundOf ();
		replaceTokenAction = new ReplaceTokenAction (tRoundType, tRoundID, aTokenCompany);
	}
	
	public void exchangeNonHomeTokens () {
		System.out.println ("Ready to Exchange all Non-Homes Tokens");
		for (String tNonHomeMapCellID : nonHomeMapCellIDs) {
			System.out.println ("Swap out Token on MapCellID " + tNonHomeMapCellID);
		}
	}
	
	@Override
	public void handlePlayerDone () {
		TokenExchangeFinishedAction tTokenExchangeFinishedAction;
		String tOperatingRoundID;
		Player tNewPlayer;
		
		super.handlePlayerDone ();

		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tTokenExchangeFinishedAction = new TokenExchangeFinishedAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tNewPlayer = formationPhase.getCurrentPlayer ();

		tTokenExchangeFinishedAction.addUpdateToNextPlayerEffect (player, tNewPlayer);
		tTokenExchangeFinishedAction.setChainToPrevious (true);
		gameManager.addAction (tTokenExchangeFinishedAction);
	}
}
