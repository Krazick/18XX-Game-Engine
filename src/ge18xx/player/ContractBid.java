package ge18xx.player;

public class ContractBid {
	public static final ContractBid NO_CONTRACT_BID = null;
	public static final int NO_EXTRA_BID = 0;
	Player player;
	boolean signed;
	boolean fullfilled;
	int extraForBid;
	
	public ContractBid (Player aPlayer) {
		setPlayer (aPlayer);
		setSigned (false);
		setFullfilled (false);
		setExtraForBid (NO_EXTRA_BID);
	}

	public void setSigned (boolean aSigned) {
		signed = aSigned;
	}

	public void setPlayer (Player aPlayer) {
		player = aPlayer;
	}
	
	public boolean isSigned () {
		return signed;
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
