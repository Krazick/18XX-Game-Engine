package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

public class AllPercentBought {
	List<PercentBought> allPercentBought;
	
	public AllPercentBought () {
		allPercentBought = new LinkedList<PercentBought> ();
	}

	public void addPercentBought (String aAbbrev, int aPercentBought) {
		if (allPercentBought.isEmpty ()) {
			addNewPercentBought (aAbbrev, aPercentBought);
		} else {
			if (containsAbbrev (aAbbrev)) {
				addPercent (aAbbrev, aPercentBought);
			} else {
				addNewPercentBought (aAbbrev, aPercentBought);
			}
		}
	}
	
	public void addPercent (String aAbbrev, int aPercentBought) {
		for (PercentBought tPercentBought : allPercentBought) {
			if (tPercentBought.isAbbrev (aAbbrev)) {
				tPercentBought.addPercent (aPercentBought);
			}
		}
	}
	
	public void addNewPercentBought (String aAbbrev, int aPercentBought) {
		PercentBought tPercentBought;

		tPercentBought = new PercentBought (aAbbrev, aPercentBought);
		allPercentBought.add (tPercentBought);
	}
	
	public boolean containsAbbrev (String aAbbrev) {
		boolean tContainsAbbrev;
		
		tContainsAbbrev = false;
		for (PercentBought tPercentBought : allPercentBought) {
			if (tPercentBought.isAbbrev (aAbbrev)) {
				tContainsAbbrev = true;
			}
		}
		
		return tContainsAbbrev;
	}
	
	public int getPercentFor (String aAbbrev) {
		int tFoundPercent;
		String tAbbrev;
		
		tFoundPercent = 0;
		tAbbrev = aAbbrev.trim ();
		for (PercentBought tPercentBought : allPercentBought) {
			if (tPercentBought.isAbbrev (tAbbrev)) {
				tFoundPercent = tPercentBought.getPercent ();
			}
		}
		
		return tFoundPercent;
	}
	
	public void clear () {
		allPercentBought.clear ();
	}
}
