package ge18xx.game;

import ge18xx.toplevel.XMLFrame;

public class UserPreferencesFrame extends XMLFrame {

	private static final long serialVersionUID = 1L;

	public UserPreferencesFrame (String aFrameName) {
		super (aFrameName);
		
		setSize (500, 500);
	}

	public UserPreferencesFrame (String aFrameName, String aGameName) {
		super (aFrameName, aGameName);
		
		setSize (500, 500);
	}
}
