package ge18xx.utilities.xml;

import java.awt.Image;

import ge18xx.utilities.GameFrameConfig;

public abstract class GameEngineManager implements GameManagerI {

	public GameEngineManager () {
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract String getActiveGameName ();

	@Override
	public abstract String createFrameTitle (String aBaseTitle);

	@Override
	public abstract Image getIconImage ();

	@Override
	public abstract GameFrameConfig getGameFrameConfig ();

}
