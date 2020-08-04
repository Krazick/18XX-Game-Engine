package ge18xx.network;

public class NetworkPlayer {
	public final static NetworkPlayer NO_NETWORK_PLAYER = null;
	public final static String INVALID_NAME = "INVALID NAME";
	String name;
	boolean ready;
	boolean afk;
	
	public NetworkPlayer (String aName) {
		setName (aName);
	}

	public String toString () {
		String tFullDisplay;
		
		tFullDisplay = getNameReady ();
		if (afk) {
			tFullDisplay += " [AFK]";
		}
		
		return tFullDisplay;
	}
	
	public String getNameReady () {
		String tDisplay;
		
		tDisplay = getName ();
		if (isReady ()) {
			tDisplay += " [READY]";
		}
		
		return tDisplay;
	}
	
	public void setName (String aName) {
		if (validPlayerName (aName)) {
			name = aName;
		} else {
			name= INVALID_NAME;
		}
	}
	
	public String getName () {
		return name;
	}
	
	public void setReady (boolean aReady) {
		ready = aReady;
	}
	
	public boolean isReady () {
		return ready;
	}
	
	public void setAFK (boolean aAFK) {
		afk = aAFK;
	}
	
	public boolean isAFK () {
		return afk;
	} 
	
	public static boolean validPlayerName (String aPlayerName) {
		boolean tValidName = true;
		
		if ((aPlayerName != null) && (aPlayerName.length () > 0)) {
			if (! aPlayerName.matches ("[A-Za-z][A-Za-z0-9_]*")) {
				tValidName = false;
			} 
		} else {
			tValidName = false;
		}

		return tValidName;
	}
}