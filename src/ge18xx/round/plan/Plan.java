package ge18xx.round.plan;

public class Plan {
	public final String [] GREEK_ALPHABET = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon",
			"Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron",
			"Pi", "Rho", "Sigma", "Tau", "Upsilon"};
	String gameName;
	String name;
	int id;
	
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

	public void setID (int aID) {
		id = aID;
	}
	
	public String getGameName () {
		return gameName;
	}
	
	public String getName () {
		return name;
	}
	
	public int getID () {
		return id;
	}
}
