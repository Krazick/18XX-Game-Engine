package ge18xx.round.plan.condition;

public class Condition {
	public static final boolean FAILS = false;
	public static final boolean MEETS = true;
	public static final String NAME = "Condition";
	String name;
	
	public Condition (String aName) {
		setName (aName + " " + NAME);
	}

	public void setName (String aName) {
		name = aName;
	}
	
	public String getName () {
		return name;
	}
	
	public boolean meets () {
		return FAILS;
	}
}
