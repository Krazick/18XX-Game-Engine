package ge18xx.player;

public class ContractBid {
	public static final ContractBid NO_CONTRACT_BID = null;
	Player player;
	boolean completed;
	boolean fullfilled;
	
	public ContractBid (Player aPlayer) {
		setPlayer (aPlayer);
		setContractBidCompleted (false);
		setFullfilled (false);
	}

	public void setContractBidCompleted (boolean aCompleted) {
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
}
