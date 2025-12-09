package ge18xx.round.plan;

public class Plan {
	public static final boolean DISAPPROVED = false;
	public static final boolean APPROVED = true;
	public final String [] GREEK_ALPHABET = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon",
			"Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron",
			"Pi", "Rho", "Sigma", "Tau", "Upsilon"};
	String playerName;
	String gameName;
	String name;
	int id;
	PlanFrame planFrame;
	boolean approved;
	
	public Plan (String aPlayerName, String aGameName, String aName) {
		setGameName (aGameName);
		setName (aName);
		setPlayerName (aPlayerName);
		setApproved (DISAPPROVED);
	}

	public void setApproved (boolean aApproved) {
		approved = aApproved;
	}
	
	public boolean isApproved () {
		return approved;
	}
	
	public void setPlanFrame (PlanFrame aPlanFrame) {
		planFrame = aPlanFrame;
	}
	
	public PlanFrame getPlanFrame () {
		return planFrame;
	}
	
	private void setGameName (String aGameName) {
		gameName = aGameName;
	}

	public void setID (int aID) {
		id = aID;
	}

	private void setName (String aName) {
		name = aName;
	}
	
	private void setPlayerName (String aPlayerName) {
		playerName = aPlayerName;
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
	
	public String getPlayerName () {
		return playerName;
	}
}
