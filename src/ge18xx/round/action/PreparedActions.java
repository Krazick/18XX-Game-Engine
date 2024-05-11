package ge18xx.round.action;

import java.util.LinkedList;
import java.util.List;

public class PreparedActions {

	List<PreparedAction> preparedActions;

	public PreparedActions () {
		preparedActions = new LinkedList<PreparedAction> ();
	}

	public void addPreparedAction (PreparedAction aPreparedAction) {
		preparedActions.add (aPreparedAction);
	}
	
	public int getCount () {
		return preparedActions.size ();
	}
	
	public PreparedAction getAction (int aIndex) {
		return preparedActions.get (aIndex);
	}
}
