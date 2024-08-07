package ge18xx.network;

import ge18xx.game.GameInfo;
import ge18xx.game.GameSet;
import ge18xx.toplevel.PlayerInputFrame;

public interface NetworkGameSupportTier2 extends NetworkGameSupport {
	public GameInfo getSelectedGame ();
	public JGameClient getNetworkJGameClient ();
	public PlayerInputFrame getPlayerInputFrame ();
	public GameSet getGameSet ();
}
