package ge18xx.round.plan;

public class Plan {

	String name;
	String gameName;
	
	public Plan (String aGameName, String aName) {
		setGameName (aGameName);
		setName (aName);
	}
	
	public void setName (String aName) {
		name = aName;
	}
	
	public String getName () {
		return name;
	}
	
	public void setGameName (String aGameName) {
		gameName = aGameName;
	}
	
	public String getGameName () {
		return gameName;
	}
}
