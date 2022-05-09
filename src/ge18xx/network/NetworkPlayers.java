package ge18xx.network;

import javax.swing.DefaultListModel;

public class NetworkPlayers {
	private DefaultListModel<NetworkPlayer> playerList;
	private NetworkGameSupport gameManager;
	public static final boolean OK_TO_ADD = true;
	public static final boolean NOT_OK_TO_ADD = false;

	public NetworkPlayers (NetworkGameSupport aGameManager) {
		playerList = new DefaultListModel<NetworkPlayer> ();
		gameManager = aGameManager;
	}

	public DefaultListModel<NetworkPlayer> getPlayerList () {
		return playerList;
	}

	public void addPlayer (String aPlayerName) {
		NetworkPlayer tNetworkPlayer;

		if (NetworkPlayer.validPlayerName (aPlayerName)) {
			if (playerNotInList (aPlayerName)) {
				tNetworkPlayer = new NetworkPlayer (aPlayerName);
				addPlayer (tNetworkPlayer);
			}
		}
	}

	public void addPlayer (NetworkPlayer aNetworkPlayer) {
		playerList.addElement (aNetworkPlayer);
		gameManager.addNetworkPlayer (aNetworkPlayer.getName ());
	}

	public void removePlayer (String aPlayerName) {
		NetworkPlayer tNetworkPlayer;

		tNetworkPlayer = getNetworkPlayer (aPlayerName);
		if (NetworkPlayer.validPlayerName (aPlayerName)) {
			playerList.removeElement (tNetworkPlayer);
			gameManager.removeNetworkPlayer (aPlayerName);
		}
	}

	public void removeAllPlayers () {
		playerList.removeAllElements ();
	}

	public NetworkPlayer getNetworkPlayer (String aPlayerName) {
		int tIndex, tNetworkPlayerCount;
		NetworkPlayer tNetworkPlayer = NetworkPlayer.NO_NETWORK_PLAYER;
		NetworkPlayer tThisNetworkPlayer;

		if (NetworkPlayer.validPlayerName (aPlayerName)) {
			tNetworkPlayerCount = playerList.size ();
			if (tNetworkPlayerCount > 0) {
				for (tIndex = 0; tIndex < tNetworkPlayerCount; tIndex++) {
					tThisNetworkPlayer = playerList.get (tIndex);
					if (tThisNetworkPlayer.getName ().equals (aPlayerName)) {
						tNetworkPlayer = tThisNetworkPlayer;
					}
				}
			}
		}

		return tNetworkPlayer;
	}

	public boolean playerIsAFK (String aPlayerName) {
		NetworkPlayer tNetworkPlayer;
		boolean tPlayerIsAFK = false;

		if (NetworkPlayer.validPlayerName (aPlayerName)) {
			tNetworkPlayer = getNetworkPlayer (aPlayerName);
			tPlayerIsAFK = tNetworkPlayer.isAFK ();
		}

		return tPlayerIsAFK;
	}

	public void setPlayerAFK (String aPlayerName, boolean aAFKFlag) {
		NetworkPlayer tNetworkPlayer;

		if (NetworkPlayer.validPlayerName (aPlayerName)) {
			addPlayer (aPlayerName); // Add Player if not in the List
			tNetworkPlayer = getNetworkPlayer (aPlayerName);
			setPlayerAFK (tNetworkPlayer, aAFKFlag);
		}
	}

	public void setPlayerAFK (NetworkPlayer aNetworkPlayer, boolean aAFKFlag) {
		aNetworkPlayer.setAFK (aAFKFlag);
		repaintJGameClient ();
	}

	private void repaintJGameClient () {
		JGameClient tJGameClient;

		tJGameClient = gameManager.getNetworkJGameClient ();
		tJGameClient.repaint ();
	}

	public void setPlayerActive (String aPlayerName, boolean aActive) {
		NetworkPlayer tNetworkPlayer;

		if (NetworkPlayer.validPlayerName (aPlayerName)) {
			tNetworkPlayer = getNetworkPlayer (aPlayerName);
			setPlayerActive (tNetworkPlayer, aActive);
		}
	}

	public void setPlayerReady (String aPlayerName, boolean aReady) {
		NetworkPlayer tNetworkPlayer;

		if (NetworkPlayer.validPlayerName (aPlayerName)) {
			tNetworkPlayer = getNetworkPlayer (aPlayerName);
			setPlayerReady (tNetworkPlayer, aReady);
		}
	}

	public void setPlayerActive (NetworkPlayer aNetworkPlayer, boolean aActive) {
		aNetworkPlayer.setActive (aActive);
		updatePlayerInList (aNetworkPlayer);
		repaintJGameClient ();
	}

	private void updatePlayerInList (NetworkPlayer aNetworkPlayer) {
		int tPlayerIndex;

		tPlayerIndex = getPlayerIndex (aNetworkPlayer.getName ());
		if (tPlayerIndex >= 0) {
			playerList.set (tPlayerIndex, aNetworkPlayer);
		}
	}

	public void setPlayerReady (NetworkPlayer aNetworkPlayer, boolean aReady) {
		aNetworkPlayer.setReady (aReady);
		repaintJGameClient ();
	}

	public void setAllPlayerReady (boolean aReady) {
		int tIndex, tNetworkPlayerCount;
		NetworkPlayer tNetworkPlayer;

		tNetworkPlayerCount = playerList.size ();

		if (tNetworkPlayerCount > 0) {
			for (tIndex = 0; tIndex < tNetworkPlayerCount; tIndex++) {
				tNetworkPlayer = playerList.get (tIndex);
				setPlayerReady (tNetworkPlayer, aReady);
			}
		}
	}

	public int getPlayerIndex (String aPlayerName) {
		int tPlayerIndex, tPlayerCount, tFoundPlayerIndex;
		NetworkPlayer tNetworkPlayer;

		tFoundPlayerIndex = -1;
		tPlayerCount = playerList.size ();
		if (aPlayerName != null) {
			if (tPlayerCount > 0) {
				for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
					tNetworkPlayer = playerList.get (tPlayerIndex);
					if (aPlayerName.equals (tNetworkPlayer.getName ())) {
						tFoundPlayerIndex = tPlayerIndex;
					}
				}
			}
		}

		return tFoundPlayerIndex;
	}

	public int getPlayerCount () {
		return playerList.size ();
	}

	public boolean allPlayersAreReady () {
		int tIndex, tNetworkPlayerCount;
		NetworkPlayer tNetworkPlayer;
		boolean tAllPlayersAreReady = true;

		tNetworkPlayerCount = getPlayerCount ();

		if (tNetworkPlayerCount > 0) {
			for (tIndex = 0; tIndex < tNetworkPlayerCount; tIndex++) {
				tNetworkPlayer = playerList.get (tIndex);
				tAllPlayersAreReady = tAllPlayersAreReady && tNetworkPlayer.isReady ();
			}
		}

		return tAllPlayersAreReady;
	}

	public boolean playerNotInList (String aPlayerName) {
		return !playerInList (aPlayerName);
	}

	public boolean playerInList (String aPlayerName) {
		boolean tPlayerInList = false;
		int tIndex, tNetworkPlayerCount;
		NetworkPlayer tNetworkPlayer;

		if (NetworkPlayer.validPlayerName (aPlayerName)) {
			tNetworkPlayerCount = playerList.size ();

			if (tNetworkPlayerCount > 0) {
				for (tIndex = 0; tIndex < tNetworkPlayerCount; tIndex++) {
					tNetworkPlayer = playerList.get (tIndex);
					if (tNetworkPlayer.getName ().equals (aPlayerName)) {
						tPlayerInList = true;
					}
				}
			}
		}

		return tPlayerInList;
	}
}
