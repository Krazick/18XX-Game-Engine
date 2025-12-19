package ge18xx.round.plan;

import java.util.LinkedList;
import java.util.List;

import ge18xx.game.GameManager;
import ge18xx.round.action.Action;
import ge18xx.round.plan.condition.Condition;
import ge18xx.round.plan.condition.Conditions;

public class Plan {
	public static final boolean DISAPPROVED = false;
	public static final boolean APPROVED = true;

	public enum PlanStatus {
		NO_STATUS ("No Status"), 
		UNAPPROVED ("Unapproved"), 
		APPROVED ("Approved"),
		TILE_SELECTED ("Tile Selected"), 
		TILE_PLACED ("Tile Placed"),
		APPLIED ("Applied"),
		DISCARDED ("Discarded");

		private String enumString;

		PlanStatus (String aEnumString) {
			enumString = aEnumString;
		}

		@Override
		public String toString () {
			return enumString;
		}
	}
	public static final String [] GREEK_ALPHABET = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon",
			"Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron",
			"Pi", "Rho", "Sigma", "Tau", "Upsilon"};
	Plan.PlanStatus status;
	String playerName;
	String gameName;
	String name;
	int id;
	PlanFrame planFrame;
	boolean approved;
	Conditions conditions;
	List<Action> planActions = new LinkedList<Action> ();
	Action action;
	
	public Plan (String aPlayerName, String aGameName, String aName) {
		Conditions tConditions;
		
		setGameName (aGameName);
		setName (aName);
		setPlayerName (aPlayerName);
		setPlanStatus (Plan.PlanStatus.UNAPPROVED);
		
		tConditions = new Conditions ();
		setConditions (tConditions);
	}

	public void setApplied () {
		setPlanStatus (PlanStatus.APPLIED);
	}
	
	public void setApproved () {
		setPlanStatus (PlanStatus.APPROVED);
	}
	
	public boolean isActive () {
		boolean isActive;
		
		isActive = false;
		if (status != PlanStatus.DISCARDED) {
			isActive = true;
		}
		
		return isActive;
	}
	
	public boolean isTileSelected () {
		boolean isTileSelected;
		
		isTileSelected = false;
		if ((status == PlanStatus.APPLIED) || 
			(status == PlanStatus.APPROVED) ||
			(status == PlanStatus.TILE_PLACED) ||
			(status == PlanStatus.TILE_SELECTED)){
			isTileSelected = true;
		}
		
		return isTileSelected;
	}
	
	public boolean isTilePlaced () {
		boolean isTilePlaced;
		
		isTilePlaced = false;
		if ((status == PlanStatus.APPLIED) || 
			(status == PlanStatus.APPROVED) ||
			(status == PlanStatus.TILE_PLACED)) {
			isTilePlaced = true;
		}
		
		return isTilePlaced;
	}
	
	public boolean isApproved () {
		return (status == PlanStatus.APPROVED);
	}
	
	public void setPlanStatus (Plan.PlanStatus aStatus) {
		status = aStatus;
	}
	
	public Plan.PlanStatus getPlanStatus () {
		return status;
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
	
	public void createActions (GameManager aGameManager) {
		System.out.println ("Sub-Classes should create the proper Plan");
	}
	
	public void setAction (Action aAction) {
		action = aAction;
	}
	
	public Action getAction () {
		return action;
	}

	public List<Action> getPlanActions () {
		return planActions;
	}
}
