package ge18xx.round;

import ge18xx.round.action.ActorI;

public class ContractBidRound extends Round {
	public static final ContractBidRound NO_CONTRACT_BID_ROUND = null;
	public static final String NAME = "Contract Bid Round";

	public ContractBidRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setName (NAME);
		setRoundType ();
	}

	@Override
	public ActorI.ActionStates getRoundState () {
		return ActorI.ActionStates.ContractBidRound;
	}
	
	@Override
	public boolean isAContractBidRound () {
		return true;
	}

	@Override
	public void finish () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start () {
		// TODO Auto-generated method stub
		
	}
}
