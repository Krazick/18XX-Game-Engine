package ge18xx.network;

public class NetworkPlayer {
	public static final NetworkPlayer NO_NETWORK_PLAYER = null;
	public static final String INVALID_NAME = "INVALID NAME";
	String name;
	boolean ready;
	boolean afk;
	boolean active;

	public NetworkPlayer (String aName) {
		setName (aName);
		setReady (false);
		setAFK (false);
		setActive (false);
	}

	@Override
	public String toString () {
		String tFullDisplay;

		tFullDisplay = getName ();
		if (isActive ()) {
			tFullDisplay += " [ACTIVE]";
		} else if (isReady ()) {
			tFullDisplay += " [READY]";
		}
		if (afk) {
			tFullDisplay += " [AFK]";
		}

		return tFullDisplay;
	}

	public void setName (String aName) {
		if (validPlayerName (aName)) {
			name = aName;
		} else {
			name = INVALID_NAME;
		}
	}

	public String getName () {
		return name;
	}

	public void setActive (boolean aActive) {
		active = aActive;
	}

	public void setReady (boolean aReady) {
		ready = aReady;
	}

	public boolean isReady () {
		return ready;
	}

	public boolean isActive () {
		return active;
	}

	public void setAFK (boolean aAFK) {
		afk = aAFK;
	}

	public boolean isAFK () {
		return afk;
	}

	public static boolean validPlayerName (String aPlayerName) {
		boolean tValidName;

		tValidName = true;
		if ((aPlayerName != null) && (aPlayerName.length () > 0)) {
			if (!aPlayerName.matches ("[A-Za-z][A-Za-z0-9_]*")) {
				tValidName = false;
			}
		} else {
			tValidName = false;
		}

		return tValidName;
	}
}