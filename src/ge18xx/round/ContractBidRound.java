package ge18xx.round;

public class ContractBidRound extends Round {
	public static final ContractBidRound NO_CONTRACT_BID_ROUND = null;
	public static final String NAME = "Contract Round";

	public ContractBidRound (RoundManager aRoundManager) {
		super (aRoundManager);
	}
	
	@Override
	public String getName () {
		return NAME;
	}
	
	
	@Override
	public boolean isAContractBidRound () {
		return true;
	}
}
