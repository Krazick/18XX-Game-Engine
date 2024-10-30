package ge18xx.company.formation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.RemoveDestinationsAction;
import ge18xx.round.action.ReplaceTokenAction;
import ge18xx.round.action.TokenExchangeFinishedAction;

import geUtilities.GUI;
import swingTweaks.KButton;

// TODO:
// 
// 1. Identify who is the President of the Forming Company, and adjust the payer to that Player
// 2. When the Player is DONE, find any Destination and Remove it from the Game Map
// 3. Confirm that after DONE, we shift to the Asset Collection Phase
// 4. Verify full Functionality from Loan Repayment through all of Token Exchange
// 5. Repeat the Verification for Network Based Game
// 6. Remove Debug Output
//

public class TokenExchange extends PlayerFormationPanel implements ItemListener {
	private static final long serialVersionUID = 1L;
	public static final String EXCHANGE_HOME_TOKEN = "ExchangeHomeTokens";
	public static final String EXCHANGE_NON_HOME_TOKEN = "ExchangeNonHomeTokens";
	JPanel nonHomeCheckboxesPanel;
	KButton homeTokensExchange;
	KButton nonHomeTokensExchange;
	JLabel homeTokensLabel;
	JLabel nonHomeTokensLabel;

	List<JCheckBox> nonHomeMapCellIDCheckbox;
	List<String> homeMapCellsInfo;
	List<String> nonHomeMapCellsInfo;
	ReplaceTokenAction replaceTokenAction;
	
	public TokenExchange (GameManager aGameManager, FormCGR aTokenExchange, Player aPlayer, 
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
	public JPanel buildCompanyJPanel (ShareCompany aFormingShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		Border tCorporateColorBorder;
		String tExchangeHomeTokens;
		String tTokenLocations;

		String tSimpleTokenInfo;
		String tTokenLocation;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		int tShareIndex;
		int tShareCount;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.Y_AXIS));	
		homeMapCellsInfo = new LinkedList<String> ();
		nonHomeMapCellsInfo = new LinkedList<String> ();
		nonHomeMapCellIDCheckbox = new LinkedList<JCheckBox> ();
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
							tTokenLocations += GUI.COMMA_SPACE;
						}
						tTokenLocation = collectHomeTokenInfo (tShareCompany);
						tSimpleTokenInfo = getSimpleTokenInfo (tTokenLocation);
						tTokenLocations += tSimpleTokenInfo;
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
		
		// Add Checkbox for each Non-Home Token
		
		buildNonHomeCheckboxes (aFormingShareCompany);
		tShareCompanyJPanel.add (nonHomeCheckboxesPanel);
		nonHomeCheckboxesPanel.setAlignmentX (Component.LEFT_ALIGNMENT);
		tShareCompanyJPanel.add (nonHomeTokensExchange);
		nonHomeTokensExchange.setAlignmentX (Component.LEFT_ALIGNMENT);
		
		return tShareCompanyJPanel;
	}
	
	public void buildNonHomeCheckboxes (ShareCompany aFormingShareCompany) {
		JCheckBox tMapCellIDCheckbox;
		String tSimpleTokenInfo;
		String tToolTip;
		MapCell tMapCell;
		HexMap tHexMap;
	
		nonHomeCheckboxesPanel = new JPanel ();
		nonHomeCheckboxesPanel.setLayout (new BoxLayout (nonHomeCheckboxesPanel, BoxLayout.X_AXIS));
		tHexMap = gameManager.getGameMap ();
		tToolTip = "Map Cell already has a token for " + aFormingShareCompany.getAbbrev () + ".";
		for (String tNonHomeMapCellInfo : nonHomeMapCellsInfo) {
			tSimpleTokenInfo = getSimpleTokenInfo (tNonHomeMapCellInfo);
			tMapCell = tHexMap.getMapCellByInfo (tNonHomeMapCellInfo);
			tMapCellIDCheckbox = new JCheckBox (tSimpleTokenInfo);
			tMapCellIDCheckbox.setSelected (true);
			tMapCellIDCheckbox.addItemListener (this);
			if (actingPlayer) {
				if (alreadyInHomeCells (tNonHomeMapCellInfo)) {
					tMapCellIDCheckbox.setEnabled (false);
					tMapCellIDCheckbox.setSelected (false);
					tMapCellIDCheckbox.setToolTipText (tToolTip);
				} else if (tMapCell.hasStation (aFormingShareCompany.getID ())) {
					tMapCellIDCheckbox.setEnabled (false);
					tMapCellIDCheckbox.setSelected (false);
					tMapCellIDCheckbox.setToolTipText (tToolTip);
				}
			} else {
				tMapCellIDCheckbox.setEnabled (false);
				tMapCellIDCheckbox.setSelected (false);
				tMapCellIDCheckbox.setToolTipText (NOT_ACTING_PRESIDENT);
			}
			nonHomeMapCellIDCheckbox.add (tMapCellIDCheckbox);
			nonHomeCheckboxesPanel.add (tMapCellIDCheckbox);
		}
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
	    Object tSource;
	    JCheckBox tCheckbox;
	    String tTitle;
	    String tNonHomeSimpleInfo;
	    String tNonHomeMapCellInfo;
	    int tLength;
	    int tInfoCount;
	    int tInfoIndex;
	    
	    tSource = aItemEvent.getItemSelectable ();
	    tTitle = GUI.EMPTY_STRING;
	    if (tSource instanceof JCheckBox) {
	    		tCheckbox = (JCheckBox) tSource;
	    		tTitle = tCheckbox.getText ();
	    		tInfoCount = nonHomeMapCellsInfo.size ();
	    		for (tInfoIndex = 0; tInfoIndex < tInfoCount; tInfoIndex++) {
	    			tNonHomeMapCellInfo = nonHomeMapCellsInfo.get (tInfoIndex);
		    		tNonHomeSimpleInfo = getSimpleTokenInfo (tNonHomeMapCellInfo);
		    		if (tNonHomeSimpleInfo.equals (tTitle)) {
		    			if (tCheckbox.isSelected ()) {
		    				tLength = tNonHomeMapCellInfo.length ();
		    				tNonHomeMapCellInfo = tNonHomeMapCellInfo.substring (0, tLength - 2);
		    			} else {
		    				tNonHomeMapCellInfo += ":X";
		    			}
		    			nonHomeMapCellsInfo.set (tInfoIndex, tNonHomeMapCellInfo);
		    		}
		    }
	    }
	}

	public boolean alreadyInHomeCells (String aMapCellInfo) {
		boolean tAlreadyInHomeCells;
		String [] tMapCellInfo;
		String [] tHomeMapCellInfo;
		
		tAlreadyInHomeCells = false;
		tMapCellInfo = aMapCellInfo.split (":");
		for (String tHomeMapCellID : homeMapCellsInfo) {
			tHomeMapCellInfo = tHomeMapCellID.split (":");
			if (tMapCellInfo [2].equals (tHomeMapCellInfo [2])) {
				tAlreadyInHomeCells = true;
			}
		}
		
		return tAlreadyInHomeCells;
	}
	
	public String getSimpleTokenInfo (String aMapCellInfo) {
		String [] tMapCellInfo;
		String tSimpleTokenInfo;
		
		tMapCellInfo = aMapCellInfo.split (":");
		tSimpleTokenInfo = tMapCellInfo [1] + " on " + tMapCellInfo [2];
		
		return tSimpleTokenInfo;
	}
	
	public boolean getHomeTokensExchanged () {
		return formCGR.getHomeTokensExchanged ();
	}
	
	public boolean getNonHomeTokensExchanged () {
		return formCGR.getNonHomeTokensExchanged ();
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
		homeMapCellsInfo.add (tTokenLocation);

		tHomeTokenInfo = "[" + tTokenLocation + "]";
		
		tHomeMapCell2 = aTokenCompany.getCorpHome2MapID ();
		if (! GUI.EMPTY_STRING.equals (tHomeMapCell2)) {
			tTokenLocation = tHexMap.getTokenLocation (tHomeMapCell2, tAbbrev, tCorpID);
			if (! homeMapCellsInfo.contains (tTokenLocation)) {
				tHomeTokenInfo += " & [" + tTokenLocation + "]";
				homeMapCellsInfo.add (tTokenLocation);
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
					tHexMap.collectNonHomeMapCellIDs (tCorpID, tAbbrev, homeMapCellsInfo, nonHomeMapCellsInfo);
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
						tHexMap.collectNonHomeMapCellIDs (tCorpID, tAbbrev, homeMapCellsInfo, nonHomeMapCellsInfo);
					}
				}
			}

		}
	}
	
	public void buildSpecialButtons (ShareCompany aFormingShareCompany, JPanel tShareCompanyPanel, 
									boolean aActingPlayer) {
		String tToolTip;
		Player tCurrentPlayer;
		Player tFormingPresident;
		ShareCompany tFormingCompany;
		
		tFormingCompany = formCGR.getFormingCompany ();
		tFormingPresident = (Player) tFormingCompany.getPresident ();
		tCurrentPlayer = formCGR.getCurrentPlayer ();
		if (tCurrentPlayer == tFormingPresident) {
			tToolTip = GUI.EMPTY_STRING;
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		homeTokensExchange = formCGR.buildSpecialButton ("Exchange all Home Tokens", EXCHANGE_HOME_TOKEN, 
								tToolTip, this);
	
		nonHomeTokensExchange = formCGR.buildSpecialButton ("Exchange all Non-Home Tokens", 
								EXCHANGE_NON_HOME_TOKEN, tToolTip, this);	
		updateSpecialButtons (aActingPlayer);
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		
		if (tActionCommand.equals (EXCHANGE_HOME_TOKEN)) {
			exchangeTokens (homeMapCellsInfo, true);
		} else if (tActionCommand.equals (EXCHANGE_NON_HOME_TOKEN)) {
			exchangeTokens (nonHomeMapCellsInfo, false);
		} else if (tActionCommand.equals (DONE)) {
			handlePlayerDone ();
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
			if (gameManager.notIsNetworkAndIsThisClient (player.getName ())) {	
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
			if (nonHomeMapCellsInfo.size () > 0) {
				if (gameManager.notIsNetworkAndIsThisClient (player.getName ())) {	
					if (getNonHomeTokensExchanged ()) {
						nonHomeTokensExchange.setEnabled (false);
						tToolTip = "President already completed all Non-Home Token exchanges.";
					} else if (! getHomeTokensExchanged ()) {
						nonHomeTokensExchange.setEnabled (false);
						tToolTip = "President has not completed Home Token exchanges.";
					} else {
						nonHomeTokensExchange.setEnabled (true);
						tToolTip = "President has completed Home Token exchanges.";
					}
				} else {
					nonHomeTokensExchange.setEnabled (false);
					tToolTip = NOT_ACTING_PRESIDENT;
				}
			} else {
				nonHomeTokensExchange.setEnabled (false);
				tToolTip = "There are no Non-Home Token to exchange.";
	
			}
			nonHomeTokensExchange.setToolTipText (tToolTip);
		}
	}

	@Override
	public void updateDoneButton () {
		String tToolTip;

		tToolTip = GUI.NO_TOOL_TIP;
		if (gameManager.notIsNetworkAndIsThisClient (player.getName ())) {
			if (getHomeTokensExchanged () && getNonHomeTokensExchanged ()) {
				doneButton.setEnabled (true);
				tToolTip = "All Tokens have been exchanged.";
			} else if (getHomeTokensExchanged ()) {
				if (nonHomeMapCellsInfo.size () > 0) {
					doneButton.setEnabled (false);
					tToolTip = "President already completed all Home Token exchanges.";
				} else {
					doneButton.setEnabled (true);
					tToolTip = "All Home Tokens have been exchanged, no Non-Home Tokens to Exchange.";
				}
			} else if (getNonHomeTokensExchanged ()) {
				doneButton.setEnabled (false);
				tToolTip = "President already completed all Non-Home Token exchanges.";
			} else {
				doneButton.setEnabled (false);
				tToolTip = "President has not completed all token exchanges.";
			}
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
			doneButton.setEnabled (false);
		}
		doneButton.setToolTipText (tToolTip);
	}
	
	@Override
	public void updateContinueButton (boolean aActingPlayer) {
		continueButton.setVisible (false);
	}

	public void exchangeTokens (List<String> aMapCellIDs, boolean tExchangeHomeTokens) {
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
		tFormingShareCompany = formCGR.getFormingCompany ();
		tCountReplacements = 0;
		for (String tHomeMapCellID : aMapCellIDs) {
			tNewMapToken = tFormingShareCompany.getLastMapToken ();
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
			if (tExchangeHomeTokens) {
				replaceTokenAction.addSetHomeTokensExchangedEffect (tFoldingCompany, true);
			} else {
				replaceTokenAction.addSetNonHomeTokensExchangedEffect (tFoldingCompany, true);
			}
			gameManager.addAction (replaceTokenAction);
		}
		tHexMap.redrawMap ();
		if (tExchangeHomeTokens) {
			formCGR.setHomeTokensExchanged (true);
		} else {
			formCGR.setNonHomeTokensExchanged (true);
		}
		formCGR.rebuildFormationPanel ();
	}

	public void prepareAction (TokenCompany aTokenCompany) {
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tRoundID = tRoundManager.getCurrentRoundOf ();
		replaceTokenAction = new ReplaceTokenAction (tRoundType, tRoundID, aTokenCompany);
	}
	
	public void removeDestinations () {
		MapCell tDestinationMapCell;
		Location tDestinationLocation;
		int tShareIndex;
		int tShareCount;
		String tOperatingRoundID;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		RemoveDestinationsAction tRemoveDestinationsAction;

		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tRemoveDestinationsAction = new RemoveDestinationsAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tShareCompanies = gameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany != Corporation.NO_CORPORATION) {
				if (tShareCompany.willFold ()) {
					tDestinationMapCell = tShareCompany.getDestinationMapCell ();
					tDestinationLocation = tShareCompany.getDestinationLocation ();
					tDestinationMapCell.removeDestination (tDestinationLocation, tShareCompany, tRemoveDestinationsAction);
				}
			}
		}
		gameManager.addAction (tRemoveDestinationsAction);
		gameManager.repaintMapFrame ();
	}
	
	@Override
	public void handlePlayerDone () {
		TokenExchangeFinishedAction tTokenExchangeFinishedAction;
		String tOperatingRoundID;
		PlayerManager tPlayerManager;
		List<Player> tPlayers;
		ActorI.ActionStates tOldState;
		
		tOperatingRoundID = gameManager.getOperatingRoundID ();

		removeDestinations ();

		tTokenExchangeFinishedAction = new TokenExchangeFinishedAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, player);
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		
		for (Player tPlayer : tPlayers) {
			tOldState = tPlayer.getPrimaryActionState ();
			tPlayer.setPrimaryActionState (formCGR.formationState);
			tTokenExchangeFinishedAction.addStateChangeEffect (tPlayer, tOldState, formCGR.formationState);
		}

		tTokenExchangeFinishedAction.setChainToPrevious (true);
		gameManager.addAction (tTokenExchangeFinishedAction);
		
		formCGR.allPlayersHandled  ();
		formCGR.applyCommand (FormCGR.ASSET_COLLECTION);
	}
}
