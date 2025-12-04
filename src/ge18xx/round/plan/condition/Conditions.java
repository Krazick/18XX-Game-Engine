package ge18xx.round.plan.condition;

import java.util.LinkedList;
import java.util.List;

public class Conditions {
	List<Condition> conditions;

	public Conditions () {
		super ();
		conditions = new LinkedList<Condition> ();
	}

	public void addCondition (Condition aCondition) {
		conditions.add (aCondition);
	}
	
	public boolean meetsAll () {
		boolean tMeetsAll;
		
		tMeetsAll = Condition.MEETS;
		for (Condition tCondition : conditions) {
			tMeetsAll = tMeetsAll && tCondition.meets ();
		}
		
		return tMeetsAll;
	}
}
