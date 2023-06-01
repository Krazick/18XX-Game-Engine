package ge18xx.player;

public class PercentBought {
	String abbrev;
	int percent;
	
	public PercentBought (String aAbbrev, int aPercent) {
		setAbbrev (aAbbrev);
		setPercent (aPercent);
	}

	void setAbbrev (String aAbbrev) {
		abbrev = aAbbrev;
	}
	
	void setPercent (int aPercent) {
		percent = aPercent;
	}
	
	String getAbbrev () {
		return abbrev;
	}
	
	int getPercent () {
		return percent;
	}
	
	void addPercent (int aPercent) {
		percent += aPercent;
	}
	
	boolean isAbbrev (String aAbbrev) {
		boolean tIsAbbrev;
		
		if (abbrev.equals (aAbbrev)) {
			tIsAbbrev = true;
		} else {
			tIsAbbrev = false;
		}
		
		return tIsAbbrev;
	}
}
