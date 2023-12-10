package ge18xx.utilities.xml;

import java.awt.Component;
import java.awt.Image;

import ge18xx.utilities.GameFrameConfig;

public abstract class GameEngineManager extends Component implements GameManagerI {

	private static final long serialVersionUID = 1L;

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
