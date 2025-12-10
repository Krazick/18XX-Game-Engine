package ge18xx.round.plan;

import ge18xx.round.plan.condition.Condition;
import ge18xx.round.plan.condition.Conditions;

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
	Conditions conditions;
	
	public Plan (String aPlayerName, String aGameName, String aName) {
		Conditions tConditions;
		
		setGameName (aGameName);
		setName (aName);
		setPlayerName (aPlayerName);
		setApproved (DISAPPROVED);
		
		tConditions = new Conditions ();
		setConditions (tConditions);
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
	
	public void setConditions (Conditions aConditions) {
		conditions = aConditions;
	}
	
	public Conditions getConditions () {
		return conditions;
	}
	
	public void addCondition (Condition aCondition) {
		conditions.addCondition (aCondition);
	}
	
	public Condition getConditionAt (int aIndex) {
		return conditions.getConditionAt (aIndex);
	}
	
	public String getConditionReport () {
		return conditions.getConditionReport ();
	}
	
	public boolean allConditionsMet () {
		return conditions.meetsAll ();
	}
	
	public String getFailsReasons () {
		return conditions.getFailsReasons ();
	}
}
