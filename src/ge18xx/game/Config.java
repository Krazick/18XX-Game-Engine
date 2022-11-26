package ge18xx.game;

import java.util.ArrayList;

import org.w3c.dom.NodeList;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class Config {
	public static final AttributeName AN_GAME_NAME = new AttributeName ("gameName");
	public static final ElementName EN_CONFIG = new ElementName ("Config");
	public static final ElementName EN_FRAMES = new ElementName ("Frames");
	public static final GameFrameConfig NO_GAME_FRAME = null;

	ArrayList<GameFrameConfig> gameFrames;
	String saveGameDirectory;
	GameManager gameManager;

	public Config (GameManager aGameManager) {
		gameManager = aGameManager;
		gameFrames = new ArrayList<GameFrameConfig> ();
		setJustSaveGameDirectory ("");
	}

	public Config (XMLNode aConfigNode, GameManager aGameManager) {
		NodeList tChildren;
		XMLNode tChildNode;
		int tNodeCount;
		int tNodeIndex;
		GameFrameConfig tGameFrameConfig;
		UserPreferencesFrame tUserPreferencesFrame;
		String tSaveGameDirName;
		String tNodeName;

		gameManager = aGameManager;
		gameFrames = new ArrayList<GameFrameConfig> ();
		tChildren = aConfigNode.getChildNodes ();
		tNodeCount = tChildren.getLength ();
		try {
			for (tNodeIndex = 0; tNodeIndex < tNodeCount; tNodeIndex++) {
				tChildNode = new XMLNode (tChildren.item (tNodeIndex));
				tNodeName = tChildNode.getNodeName ();
				if (EN_FRAMES.equals (tNodeName)) {
					tGameFrameConfig = new GameFrameConfig (tChildNode);
					addNewGameFrame (tGameFrameConfig);
				} else if (GameManager.EN_SAVEGAMEDIR.equals (tNodeName)) {
					tSaveGameDirName = tChildNode.getThisAttribute (GameManager.AN_NAME);
					setJustSaveGameDirectory (tSaveGameDirName);
				} else if (UserPreferencesFrame.EN_USER_PREFERENCES.equals (tNodeName)) {
					tUserPreferencesFrame = gameManager.getUserPreferencesFrame ();
					tUserPreferencesFrame.parsePreferences (tChildNode);
				}
			}
		} catch (Exception tException) {
			System.err.println ("Caught Exception with message ");
			tException.printStackTrace ();
		}
	}

	public String getSaveGameDirectory () {
		return saveGameDirectory;
	}

	private void setJustSaveGameDirectory (String aSaveGameDirectory) {
		saveGameDirectory = aSaveGameDirectory;
	}

	public void setSaveGameDirectory (String aSaveGameDirectory) {
		setJustSaveGameDirectory (aSaveGameDirectory);
		gameManager.saveConfig (true);
	}

	public int getGameFramesCount () {
		return gameFrames.size ();
	}

	public XMLElement getXMLFramesElement (XMLDocument aXMLDocument, int aGameIndex) {
		int tGamesCount, tFrameCount, tFrameIndex;
		XMLElement tXMLFramesElement = XMLElement.NO_XML_ELEMENT;
		XMLElement tXMLFrameElement;
		GameFrameConfig tGameFrameConfig;
		String tGameName, tFrameName;

		tGamesCount = getGameFramesCount ();
		if ((tGamesCount > 0) && (aGameIndex < tGamesCount)) {
			tXMLFramesElement = aXMLDocument.createElement (EN_FRAMES);
			tGameFrameConfig = gameFrames.get (aGameIndex);
			tGameName = tGameFrameConfig.getGameName ();
			tXMLFramesElement.setAttribute (AN_GAME_NAME, tGameName);
			tFrameCount = tGameFrameConfig.getFrameCount ();
			for (tFrameIndex = 0; tFrameIndex < tFrameCount; tFrameIndex++) {
				tFrameName = tGameFrameConfig.getFrameName (tFrameIndex);
				if (tFrameName != FrameInfo.NO_FRAME_NAME) {
					tXMLFrameElement = tGameFrameConfig.getXMLFrameElement (tFrameName, aXMLDocument);
					tXMLFramesElement.appendChild (tXMLFrameElement);
				}
			}
		}

		return tXMLFramesElement;
	}

	public GameFrameConfig getGameFrameConfigFor (int aGameIndex) {
		GameFrameConfig tGameFrameConfig = NO_GAME_FRAME;

		if ((getGameFramesCount () > 0) && (aGameIndex < getGameFramesCount ())) {
			tGameFrameConfig = gameFrames.get (aGameIndex);
		}

		return tGameFrameConfig;
	}

	public GameFrameConfig getGameFrameConfigFor (String aGameName) {
		GameFrameConfig tFoundGameFrameConfig = NO_GAME_FRAME;
		String tGameName;
		
		if (getGameFramesCount () > 0) {
			for (GameFrameConfig tGameFrameConfig : gameFrames) {
				if (tGameFrameConfig != NO_GAME_FRAME) {
					tGameName = tGameFrameConfig.getGameName ();
					if (tGameName != GameFrameConfig.NO_GAME_NAME) {
						if (tGameName.equals (aGameName)) {
							tFoundGameFrameConfig = tGameFrameConfig;
						}
					}
				}
			}
		}

		return tFoundGameFrameConfig;
	}
	
	private void addNewGameFrame (GameFrameConfig aGameFrameConfig) {
		GameFrameConfig tFoundGameFrameConfig;
		String tGameName;
		
		tGameName = aGameFrameConfig.getGameName ();
		tFoundGameFrameConfig = getGameFrameConfigFor (tGameName);
		if (tFoundGameFrameConfig == NO_GAME_FRAME) {
			gameFrames.add (aGameFrameConfig);
		} else {
			System.err.println ("Already have GameFrame for " + tGameName);
		}
	}
}
