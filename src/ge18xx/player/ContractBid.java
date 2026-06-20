package ge18xx.player;

public class ContractBid {
	public static final ContractBid NO_CONTRACT_BID = null;
	public static final int NO_EXTRA_BID = 0;
	Player player;
	boolean completed;
	boolean fullfilled;
	int extraForBid;
	
	public ContractBid (Player aPlayer) {
		setPlayer (aPlayer);
		setCompleted (false);
		setFullfilled (false);
		setExtraForBid (NO_EXTRA_BID);
	}

	public void setCompleted (boolean aCompleted) {
		completed = aCompleted;
	}

	public void setPlayer (Player aPlayer) {
		player = aPlayer;
	}
	
	public boolean isCompleted () {
		return completed;
	}
	
	public void setFullfilled (boolean aFullfilled) {
		fullfilled = aFullfilled;
	}
	
	public boolean isFullfilled () {
		return fullfilled;
	}
	
	public void setExtraForBid (int aExtraForBid) {
		extraForBid = aExtraForBid;
	}
	
	public int getExtraForBid () {
		return extraForBid;
	}
}
