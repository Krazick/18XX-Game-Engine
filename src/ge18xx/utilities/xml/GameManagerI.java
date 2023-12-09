package ge18xx.utilities.xml;

import java.awt.Image;

import ge18xx.game.GameManager;
import ge18xx.utilities.GameFrameConfig;

public interface GameManagerI {
	public static final String NO_GAME_NAME = "<NONE>";
	public static final GameManager NO_GAME_MANAGER = null;
	public static final GameManager_XML NO_GAME_MANAGER_XML = null;

	public String getActiveGameName ();

	public String createFrameTitle (String aBaseTitle);

	public Image getIconImage ();

	public GameFrameConfig getGameFrameConfig ();
}
