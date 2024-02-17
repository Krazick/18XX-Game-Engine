package ge18xx.round;

import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeStateAction;
import ge18xx.round.action.DonePlayerAction;

import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class StockRound extends Round {
	public static final ElementName EN_STOCK_ROUND = new ElementName ("StockRound");
	public static final AttributeName AN_CURRENT_PLAYER = new AttributeName ("currentPlayer");
	public static final AttributeName AN_PRIORITY_PLAYER = new AttributeName ("priorityPlayer");
	public static final StockRound NO_STOCK_ROUND = null;
	public static final String NAME = "Stock Round";
	public static final int FIRST_PLAYER = 0;
	PlayerManager playerManager;
	int currentPlayerIndex;
	int priorityPlayerIndex;
	int startRoundPriorityIndex;

	public StockRound () {
		super (null);
	}

	public StockRound (PlayerManager aPlayerManager, RoundManager aRoundManager) {
		super (aRoundManager);
		setPlayerManager (aPlayerManager);
	}

	public void setStartingPlayer () {
		setCurrentPlayer (FIRST_PLAYER, false);
		setPriorityPlayer (FIRST_PLAYER);
		setStartRoundPriorityIndex (FIRST_PLAYER);
	}

	public void clearAllPlayerPasses () {
		playerManager.clearAllPlayerPasses ();
	}

	@Override
	public void clearAllAuctionStates () {
		playerManager.clearAllAuctionStates ();
	}

	public void clearAllSoldCompanies () {
		playerManager.clearAllSoldCompanies ();
	}

	public Player getCurrentPlayer () {
		Player tPlayer;

		tPlayer = Player.NO_PLAYER;
		if (playerManager != PlayerManager.NO_PLAYER_MANAGER) {
			tPlayer = playerManager.getPlayer (currentPlayerIndex);
		}

		return tPlayer;
	}

	public int getCurrentPlayerIndex () {
		return currentPlayerIndex;
	}

	public String getCurrentPlayerName () {
		String tName;
		Player tPlayer;

		tName = Player.NO_PLAYER_NAME_LABEL;
		if (playerManager != PlayerManager.NO_PLAYER_MANAGER) {
			tPlayer = getCurrentPlayer ();
			if (tPlayer != Player.NO_PLAYER) {
				tName = tPlayer.getName ();
			}
		}

		return tName;
	}

	@Override
	public String getID () {
		return getIDPart1 () + "";
	}

	@Override
	public String getName () {
		return NAME;
	}

	public int getNextPlayerIndex () {
		int tNextPlayer;

		tNextPlayer = PlayerManager.NO_PLAYER_INDEX;
		if (playerManager != PlayerManager.NO_PLAYER_MANAGER) {
			tNextPlayer = (currentPlayerIndex + 1) % playerManager.getPlayerCount ();
		}

		return tNextPlayer;
	}

	public int getPlayerCount () {
		return playerManager.getPlayerCount ();
	}

	public Player getPlayerAtIndex (int aIndex) {
		return playerManager.getPlayer (aIndex);
	}

	public PlayerManager getPlayerManager () {
		return playerManager;
	}

	public int getPriorityIndex () {
		return priorityPlayerIndex;
	}

	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_STOCK_ROUND);
		setRoundAttributes (tXMLElement);
		tXMLElement.setAttribute (AN_CURRENT_PLAYER, currentPlayerIndex);
		tXMLElement.setAttribute (AN_PRIORITY_PLAYER, priorityPlayerIndex);

		return tXMLElement;
	}

	@Override
	public void loadRound (XMLNode aRoundNode) {
		super.loadRound (aRoundNode);
		currentPlayerIndex = aRoundNode.getThisIntAttribute (AN_CURRENT_PLAYER);
		priorityPlayerIndex = aRoundNode.getThisIntAttribute (AN_PRIORITY_PLAYER);
	}

	@Override
	public ActorI.ActionStates getRoundType () {
		return ActorI.ActionStates.StockRound;
	}

	@Override
	public String getStateName () {
		return getRoundType ().toString ();
	}

	@Override
	public String getType () {
		return NAME;
	}

	public void printRoundInfo () {
		System.out.println (" " + NAME + " " + idPart1);	// PRINTLOG
		System.out.println ("  Player Count " + playerManager.getPlayerCount ());
		System.out.println ("  Current Player Index " + currentPlayerIndex);
		System.out.println ("  Priority Player Index " + priorityPlayerIndex);
	}

	public void setCurrentPlayerIndex (int aPlayerIndex) {
		currentPlayerIndex = aPlayerIndex;
		roundManager.setCurrentPlayerLabel ();
	}

	public void prepareStockRound () {
		clearAllSoldCompanies ();
		// TODO -- for Complete Undo, this 'Clear' should be undoable, so that when a
		// Force Train Buy the player uses a Prez Stock Exchange that would require 
		// sale of stock of the company to not be required after undo backs it out.
		// Very rare situation that could be abused.
		// Could have this effect be applied on 'setCurrentPlayer' method, with the
		// ChangeStateAction
		playerManager.clearAllExchangedShares ();
		playerManager.clearAllPercentBought ();
		setCurrentPlayer (getPriorityIndex (), true);
		setStartRoundPriorityIndex (getPriorityIndex ());
	}

	public void setCurrentPlayer (int aPlayerIndex, boolean aChainToPrevious) {
		 ChangeStateAction tChangeStateAction;
		 Player tCurrentPlayer;
		 
		 tCurrentPlayer = getCurrentPlayer ();
		 tChangeStateAction = new DonePlayerAction (getRoundType (), getID (), tCurrentPlayer);
		 setCurrentPlayer (getPriorityIndex (), true, tChangeStateAction);
	}
	
	public void setCurrentPlayer (int aPlayerIndex, boolean aChainToPrevious, ChangeStateAction aChangeStateAction) {
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		Player tPlayer;

		tPlayer = playerManager.getPlayer (aPlayerIndex);
		if (tPlayer != Player.NO_PLAYER) {
			setCurrentPlayerIndex (aPlayerIndex);
			tOldState = tPlayer.getPrimaryActionState ();
			playerManager.clearPlayerPrimaryStateAt (currentPlayerIndex);
			tNewState = tPlayer.getPrimaryActionState ();
			
			aChangeStateAction.addStateChangeEffect (tPlayer, tOldState, tNewState);
		}
	}

	public void setPlayerManager (PlayerManager aPlayerManager) {
		playerManager = aPlayerManager;
		if (playerManager != PlayerManager.NO_PLAYER_MANAGER) {
			playerManager.setStockRound (this);
		}
	}

	public void setPriorityPlayer (int aPriorityIndex) {
		priorityPlayerIndex = aPriorityIndex;
	}

	public void setStartRoundPriorityIndex (int aPriorityIndex) {
		startRoundPriorityIndex = aPriorityIndex;
	}
	
	public int getStartRoundPriorityIndex () {
		return startRoundPriorityIndex;
	}
	
	public void showCurrentPlayerFrame () {
		playerManager.showPlayerFrame (currentPlayerIndex);
	}

	@Override
	public boolean roundIsDone () {
		boolean tRoundDone;

		tRoundDone = false;
		if (playerManager != PlayerManager.NO_PLAYER_MANAGER) {
			tRoundDone = playerManager.haveAllPassed ();
		}

		return tRoundDone;
	}

	public void endStockRound () {
		roundManager.fullOwnershipAdjustment ();
	}

	public boolean canStartOperatingRound () {
		return roundManager.canStartOperatingRound ();
	}

	@Override
	public boolean startOperatingRound () {
		boolean tStockRoundStarted;

		tStockRoundStarted = true;
		if (canStartOperatingRound ()) {
			endStockRound ();
			super.startOperatingRound ();
		} else {
			tStockRoundStarted = false;
		}

		return tStockRoundStarted;
	}

	public void updateRFPlayerLabel (Player aPlayer) {
		int tPlayerIndex;

		tPlayerIndex = playerManager.getPlayerIndex (aPlayer);
		roundManager.updateRFPlayerLabel (aPlayer, priorityPlayerIndex, tPlayerIndex);
	}

	public void updateStockRoundWindow () {
		updateRoundFrame ();
	}

	@Override
	public boolean isAStockRound () {
		return true;
	}

	public void passStockAction () {
		Player tPlayer;

		tPlayer = playerManager.getCurrentPlayer ();
		tPlayer.passAction ();
	}
}