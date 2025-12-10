package ge18xx.round.plan.condition;

import java.util.LinkedList;
import java.util.List;

import geUtilities.GUI;

public class Conditions {
	public static final Conditions NO_CONDITIONS = null;

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

	public String getFailsReasons () {
		String tFailsReasons;
		
		tFailsReasons = GUI.EMPTY_STRING;
		for (Condition tCondition : conditions) {
			tFailsReasons += tCondition.getFailsReason () + "\n";
		}

		return tFailsReasons;
	}
	
	public Condition getConditionAt (int aIndex) {
		return conditions.get (aIndex);
	}

	public String getConditionReport () {
		String tConditionReport;
		
		if (conditions.isEmpty ()) {
			tConditionReport = "No Conditions to report\n";
		} else if (conditions.size () == 1) {
			tConditionReport = "There is 1 Condition in this Plan\n";
		} else {
			tConditionReport = "There are " + conditions.size () + " Conditions in this Plan: \n";
		}

		if (! conditions.isEmpty ()) {
			for (Condition tCondition : conditions) {
				tConditionReport += tCondition.getReport () + "\n";
			}
		}
		
		return tConditionReport;
	}
}
