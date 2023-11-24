package ge18xx.company.formation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	JPanel nonHomeCheckboxesPanel;
	JButton homeTokensExchange;
	JButton nonHomeTokensExchange;
	JLabel homeTokensLabel;
	JLabel nonHomeTokensLabel;

	List<String> homeMapCellIDs;
	List<String> nonHomeMapCellIDs;
	ReplaceTokenAction replaceTokenAction;
	
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
		Border tCorporateColorBorder;
		String tExchangeHomeTokens;
		String tTokenLocations;
		String tNonHomeStations;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		
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
		if (! getHomeTokensExchanged ()) {
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
		} else {
			tExchangeHomeTokens = "All Home Tokens have been exchanged";
		}
		homeTokensLabel = new JLabel (tExchangeHomeTokens);
		tShareCompanyJPanel.add (homeTokensLabel);
		homeTokensLabel.setAlignmentX (Component.LEFT_ALIGNMENT);
		
		buildSpecialButtons (aFormingShareCompany, tShareCompanyJPanel, aActingPlayer);
		tShareCompanyJPanel.add (homeTokensExchange);
		
		if (! getNonHomeTokensExchanged ()) {
			collectNonHomeMapTokens ();
			nonHomeTokensLabel = new JLabel ("Non-Home Tokens Exchange Choices");
		} else {
			nonHomeTokensLabel = new JLabel ("All Non-Home Tokens have been Exchanged");
		}
		tShareCompanyJPanel.add (nonHomeTokensLabel);
		nonHomeTokensLabel.setAlignmentX (Component.LEFT_ALIGNMENT);
		tNonHomeStations = nonHomeMapCellIDs.toString ();
		System.out.println (tNonHomeStations);
		
		// Add Checkbox for each Non-Home Token
		
		buildNonHomeCheckboxes ();
		tShareCompanyJPanel.add (nonHomeCheckboxesPanel);
		nonHomeCheckboxesPanel.setAlignmentX (Component.LEFT_ALIGNMENT);
		tShareCompanyJPanel.add (nonHomeTokensExchange);
		nonHomeTokensExchange.setAlignmentX (Component.LEFT_ALIGNMENT);
		
		return tShareCompanyJPanel;
	}
	
	public void buildNonHomeCheckboxes () {
		JCheckBox tMapCellIDCheckbox;
		String tMapCellInfo [];
		String tMapCellID;
	
		nonHomeCheckboxesPanel = new JPanel ();
		nonHomeCheckboxesPanel.setLayout (new BoxLayout (nonHomeCheckboxesPanel, BoxLayout.X_AXIS));
		for (String tNonHomeMapCellID : nonHomeMapCellIDs) {
			tMapCellInfo = tNonHomeMapCellID.split (":");
			tMapCellID = tMapCellInfo [1] + " on " + tMapCellInfo [2];
			tMapCellIDCheckbox = new JCheckBox (tMapCellID);
			
			nonHomeCheckboxesPanel.add (tMapCellIDCheckbox);
		}

	}
	
	public boolean getHomeTokensExchanged () {
		return formationPhase.getHomeTokensExchanged ();
	}
	
	public boolean getNonHomeTokensExchanged () {
		return formationPhase.getNonHomeTokensExchanged ();
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
		updateSpecialButtons (aActingPlayer);
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
		} else {
			super.actionPerformed (aEvent);
		}
	}
	
	@Override
	public void updateSpecialButtons (boolean aActingPlayer) {
		updateHomeTokenExchangeButton (aActingPlayer);
		updateNonHomeTokenExchangeButton (aActingPlayer);
	}

	public void updateHomeTokenExchangeButton (boolean aActingPlayer) {
		String tToolTip;
		
		if (homeTokensExchange != GUI.NO_BUTTON) {
			tToolTip = GUI.EMPTY_STRING;
			if (gameManager.isNetworkAndIsThisClient (player.getName ())) {	
				if (getHomeTokensExchanged ()) {
					homeTokensExchange.setEnabled (false);
					tToolTip = "President already completed all Home Token exchanges";
				}
			} else {
				homeTokensExchange.setEnabled (false);
				tToolTip = NOT_ACTING_PRESIDENT;
			}
	
			homeTokensExchange.setToolTipText (tToolTip);
		}
	}
	
	public void updateNonHomeTokenExchangeButton (boolean aActingPlayer) {
		String tToolTip;
		
		if (nonHomeTokensExchange != GUI.NO_BUTTON) {
			tToolTip = GUI.EMPTY_STRING;
			if (gameManager.isNetworkAndIsThisClient (player.getName ())) {	
				if (getNonHomeTokensExchanged ()) {
					nonHomeTokensExchange.setEnabled (false);
					tToolTip = "President already completed all Non-Home Token exchanges";
				} else if (! getHomeTokensExchanged ()) {
					nonHomeTokensExchange.setEnabled (false);
					tToolTip = "President has not completed Home Token exchanges";
				}
			} else {
				nonHomeTokensExchange.setEnabled (false);
				tToolTip = NOT_ACTING_PRESIDENT;
			}
	
			nonHomeTokensExchange.setToolTipText (tToolTip);
		}
	}

	@Override
	public void updateDoneButton () {
		String tToolTip;

		tToolTip = GUI.NO_TOOL_TIP;
		if (gameManager.isNetworkAndIsThisClient (player.getName ())) {
			if (getHomeTokensExchanged ()) {
				doneButton.setEnabled (false);
				tToolTip = "President already completed all Home Token exchanges";
			} else if (getNonHomeTokensExchanged ()) {
				doneButton.setEnabled (false);
				tToolTip = "President already completed all Non-Home Token exchanges";
			} else {
				doneButton.setEnabled (false);
				tToolTip = "President has not completed all token exchanges";
			}
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
			doneButton.setEnabled (false);
		}
		doneButton.setToolTipText (tToolTip);
	}
	
	@Override
	public void updateContinueButton () {
		String tToolTip;
		
		if (getHomeTokensExchanged () && getNonHomeTokensExchanged () && actingPlayer) {
			if (formationPhase.getFormationState ().equals ((ActorI.ActionStates.TokenExchange))) {
				continueButton.setEnabled (true);
				tToolTip = "All Tokens have been exchanged, proceed to Asset Collection";			
				continueButton.setToolTipText (tToolTip);
				continueButton.setVisible (true);
			} else {
				continueButton.setEnabled (false);
				tToolTip = "Not Ready Yet";
				continueButton.setToolTipText (tToolTip);
				continueButton.setVisible (false);
			}	
			continueButton.setActionCommand (FormationPhase.ASSET_COLLECTION);
		} else {
			continueButton.setVisible (false);
		}
	}

	public void exchangeHomeTokens () {
		HexMap tHexMap;
		MapToken tNewMapToken;
		ShareCompany tFormingShareCompany;
		TokenCompany tFoldingCompany;
		String tCompanyAbbrev;
		CorporationList tShareCompanies;
		CorporationList tMinorCompanies;
		int tCountReplacements;
		
		tHexMap = gameManager.getGameMap ();
		tShareCompanies = gameManager.getShareCompanies ();
		tMinorCompanies = gameManager.getMinorCompanies ();
		tFormingShareCompany = formationPhase.getFormingCompany ();
		tNewMapToken = tFormingShareCompany.getLastMapToken ();
		tCountReplacements = 0;
		for (String tHomeMapCellID : homeMapCellIDs) {
			tCompanyAbbrev = tHexMap.getCompanyAbbrev (tHomeMapCellID);
			tFoldingCompany = (TokenCompany) tShareCompanies.getCorporation (tCompanyAbbrev);
			if (tFoldingCompany == Corporation.NO_CORPORATION) {
				tFoldingCompany = (TokenCompany) tMinorCompanies.getCorporation (tCompanyAbbrev);
			}
			prepareAction (tFoldingCompany);
			
			tHexMap.replaceMapToken (tHomeMapCellID, tNewMapToken, tFoldingCompany, replaceTokenAction);
			tCountReplacements++;
			if (tCountReplacements > 1) {
				replaceTokenAction.setChainToPrevious (true);
			}
			replaceTokenAction.addSetHomeTokensExchangedEffect (tFoldingCompany, true);
			
			gameManager.addAction (replaceTokenAction);
		}
		tHexMap.redrawMap ();
		formationPhase.setHomeTokensExchanged (true);
		formationPhase.rebuildFormationPanel ();
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
