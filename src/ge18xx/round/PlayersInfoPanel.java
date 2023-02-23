package ge18xx.round;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ge18xx.player.Player;

public class PlayersInfoPanel extends ObserverPanel{

	private static final long serialVersionUID = 1L;
	private static final String PLAYER_JPANEL_LABEL = "Player Information";
	RoundFrame roundFrame;
	
	public PlayersInfoPanel (RoundManager aRoundManager) {
		super (aRoundManager);
		buildPlayersJPanel ();
	}

	public PlayersInfoPanel (LayoutManager layout, RoundManager aRoundManager) {
		super (layout, aRoundManager);
		buildPlayersJPanel ();
	}

	public PlayersInfoPanel (boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (isDoubleBuffered, aRoundManager);
		buildPlayersJPanel ();
	}

	public PlayersInfoPanel (LayoutManager layout, boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (layout, isDoubleBuffered, aRoundManager);
		buildPlayersJPanel ();
	}

	public void setRoundFrame (RoundFrame aRoundFrame) {
		roundFrame = aRoundFrame;
	}

	@Override
	protected void updatePanel () {
		updateAllPlayerJPanels ();
	}
	
	private void buildPlayersJPanel () {
		BoxLayout tLayout;
		RoundFrame tRoundFrame;
	
		tRoundFrame = roundManager.getRoundFrame ();
		setRoundFrame (tRoundFrame);
		tLayout = new BoxLayout (this, BoxLayout.X_AXIS);
		setLayout (tLayout);
		buildBorder (PLAYER_JPANEL_LABEL, TitledBorder.LEADING, Color.DARK_GRAY);
		add (Box.createHorizontalStrut (10));
		updateAllPlayerJPanels ();
		updateCurrentPlayerText ();
		observePlayers ();
	}

	private void observePlayers () {
		StockRound tStockRound;
		Player tPlayer;
		int tPlayerCount;
		int tIndex;

		addMessage (Player.PLAYER_CHANGED);
		tStockRound = roundManager.getStockRound ();
		tPlayerCount = tStockRound.getPlayerCount ();
		for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
			tPlayer = tStockRound.getPlayerAtIndex (tIndex);
			tPlayer.addObserver (this);
		}
	}

	public void updateAllPlayerJPanels () {
		Player tPlayer;
		StockRound tStockRound;
		JPanel tPlayerJPanel;
		int tPlayerCount;
		int tPriorityPlayer;
		int tIndex;
		int tPlayerOffset;
		int tPlayerIndex;

		tStockRound = roundManager.getStockRound ();
		tPlayerCount = tStockRound.getPlayerCount ();
		tPriorityPlayer = tStockRound.getPriorityIndex ();
		removeAll ();
		tPlayerOffset = getPlayerOffset (tPlayerCount, tStockRound);
		add (Box.createHorizontalGlue ());
		for (tIndex = 0; tIndex < tPlayerCount; tIndex++) {
			tPlayerIndex = getAdjustedPlayerIndex (tPlayerCount, tIndex, tPlayerOffset);
			tPlayer = tStockRound.getPlayerAtIndex (tPlayerIndex);
			if (tPlayer != Player.NO_PLAYER) {
				tPlayerJPanel = tPlayer.buildAPlayerJPanel (tPriorityPlayer, tPlayerIndex);
				add (tPlayerJPanel);
				add (Box.createHorizontalStrut (10));
				add (Box.createHorizontalGlue ());
			}
		}
	}

	private int getAdjustedPlayerIndex (int aPlayerCount, int aIndex, int aIndexOffset) {
		int tPlayerIndex;

		tPlayerIndex = (aIndex + aIndexOffset) % aPlayerCount;

		return tPlayerIndex;
	}

	private int getPlayerOffset (int aPlayerCount, StockRound aStockRound) {
		Player tPlayer;
		String tPlayerName;
		String tFirstPlayerName;
		int tPlayerOffset;
		int tPlayerIndex;

		tFirstPlayerName = roundManager.getFirstPlayerName ();
		
		tPlayerOffset = 0;
		for (tPlayerIndex = 0; tPlayerIndex < aPlayerCount; tPlayerIndex++) {
			tPlayer = aStockRound.getPlayerAtIndex (tPlayerIndex);
			tPlayerName = tPlayer.getName ();
			if (tPlayerName.equals (tFirstPlayerName)) {
				tPlayerOffset = tPlayerIndex;
			}
		}

		return tPlayerOffset;
	}

	private void updateCurrentPlayerText () {
		Player tPlayer;
		StockRound tStockRound;
		int tCurrentPlayer;
		int tPlayerCount;
		int tPlayerIndex;
		String tPlayerName;

		tStockRound = roundManager.getStockRound ();
		tCurrentPlayer = tStockRound.getCurrentPlayerIndex ();
		tPlayerCount = tStockRound.getPlayerCount ();
		if (roundFrame != RoundFrame.NO_ROUND_FRAME) {
			for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
				tPlayer = tStockRound.getPlayerAtIndex (tPlayerIndex);
				if (tCurrentPlayer == tPlayerIndex) {
					tPlayerName = tPlayer.getName ();
					roundFrame.setCurrentPlayerText (tPlayerName);
				}
			}
		}
	}
	
	public void setCurrentPlayerText () {
		String tPlayerName;

		if (roundFrame != RoundFrame.NO_ROUND_FRAME) {
			tPlayerName = getCurrentPlayerName ();
			roundFrame.setCurrentPlayerText (tPlayerName);
		}
	}

	public String getCurrentPlayerName () {
		StockRound tStockRound;
		String tPlayerName;

		tStockRound = roundManager.getStockRound ();
		tPlayerName = tStockRound.getCurrentPlayerName ();

		return tPlayerName;
	}
}
