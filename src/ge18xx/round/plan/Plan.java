package ge18xx.round.plan;

public class Plan {

	String name;
	
	public Plan (String aName) {
		setName (aName);
	}
	
	public void setName (String aName) {
		name = aName;
	}
	
	public String getName () {
		return name;
	}
}
