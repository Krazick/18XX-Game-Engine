package ge18xx.round.plan;

public class Plan {
	String gameName;
	String name;
	
	public Plan (String aGameName, String aName) {
		setGameName (aGameName);
		setName (aName);
	}

	private void setGameName (String aGameName) {
		gameName = aGameName;
	}

	private void setName (String aName) {
		name = aName;
	}

	public String getGameName () {
		return gameName;
	}
	
	public String getName () {
		return name;
	}
}
