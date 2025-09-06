package ge18xx.company.formation;

import java.util.List;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.MinorCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeStateAction;
import ge18xx.round.action.StartFormationAction;
import geUtilities.xml.XMLNode;

public class FormPrussian extends FormCompany {
	int currentPlayerIndex;

	public FormPrussian (GameManager aGameManager) {
		super (aGameManager);
		
		String tFullFrameTitle;
		
		tFullFrameTitle = setFormationState (ActorI.ActionStates.Nationalization);
		System.out.println ("Initiate Form Prussian, Frame Title: " + tFullFrameTitle +
				" Game: " + gameManager.getActiveGameName ());
		setTriggeringCompany ();
		buildAllPlayers (tFullFrameTitle);
	
		gameManager.setTriggerClass (this);
		gameManager.setTriggerFormation (this);
	}
	
	public FormPrussian (XMLNode aXMLNode, GameManager aGameManager) {
		this (aGameManager);

		parseXML (aXMLNode);
	}

	@Override
	public void prepareFormation (StartFormationAction aStartFormationAction) {
		Player tActingPresident;
		int tCurrentPlayerIndex;
		PlayerManager tPlayerManager;
		
		tActingPresident = findActingPresident ();
		tPlayerManager = gameManager.getPlayerManager ();
		tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (tActingPresident);
		setCurrentPlayerIndex (tCurrentPlayerIndex);
		rebuildFormationPanel (tCurrentPlayerIndex);
		// TODO -- New Effect to add "AddSetFormationPlayerIndexEffect (FromPlayer, toPlayer)
		aStartFormationAction.addUpdateToNextPlayerEffect (tActingPresident, tActingPresident, tActingPresident);

		showFormationFrame (aStartFormationAction);
	}
	
	@Override
	public void rebuildFormationPanel () {
		int tCurrentPlayerIndex;
		
		tCurrentPlayerIndex = currentPlayerIndex;
		if (tCurrentPlayerIndex >= 0) {
			rebuildFormationPanel (tCurrentPlayerIndex);
		}
	}
	
	public void setTriggeringCompany () {
		Corporation tTriggeringCompany;
		CorporationList tMinorCompanies;
		MinorCompany tMinorCompany;
		int tMinorCompanyCount;
		int tMinorCompanyIndex;
		
		tMinorCompanies = gameManager.getMinorsCompanies ();
		tTriggeringCompany = MinorCompany.NO_MINOR_COMPANY;
		tMinorCompanyCount = tMinorCompanies.getCorporationCount ();
		for (tMinorCompanyIndex = 0; tMinorCompanyIndex < tMinorCompanyCount; tMinorCompanyIndex++) {
			tMinorCompany = (MinorCompany) tMinorCompanies.getCorporation (tMinorCompanyIndex);
			if (tMinorCompany.canFormUpgrade ()) {
				tTriggeringCompany = tMinorCompany;
			}
		}
		setTriggeringCompany (tTriggeringCompany);
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
	public Player findActingPresident () {
		Corporation tTriggeringCorporation;
		Player tActingPlayer;
		PortfolioHolderI tPresident;
		
		tTriggeringCorporation = getTriggeringCompany ();
		if (actingPresident == Player.NO_PLAYER) {
			if (tTriggeringCorporation != Corporation.NO_CORPORATION) {
				tPresident = tTriggeringCorporation.getPresident ();
				if (tPresident.isAPlayer ()) {
					tActingPlayer = (Player) tPresident;
					setActingPresident (tActingPlayer);
				} else {
					setActingPresident (Player.NO_PLAYER);
				}
			}
		} else {
			
		}
		tActingPlayer = actingPresident;
	
		return tActingPlayer;
	}

	@Override
	public void allPlayersHandled (ChangeStateAction aChangeStateAction) {
		setAllPlayerHandled (true);
		rebuildFormationPanel (currentPlayerIndex);
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
	
	@Override
	public boolean ends () {
		boolean tEnds;
		
		tEnds = false;
		if (! triggeringCompany.isClosed ()) {
			tEnds = true;
		} else if (allPlayersHandled) {
			tEnds = true;
		}
		
		return tEnds;
	}
}
