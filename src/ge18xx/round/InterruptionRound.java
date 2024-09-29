package ge18xx.round;

public class InterruptionRound extends Round {
	Round interruptedRound;
	boolean interruptionStarted;
	
	public InterruptionRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setInterruptionStarted (false);
	}
	
	public void setInterruptedRound (Round aInterruptedRound) {
		interruptedRound = aInterruptedRound;
	}

	public void setInterruptionStarted (boolean aInterruptionStarted) {
		interruptionStarted = aInterruptionStarted;
	}
	
	@Override
	public boolean interruptionStarted () {
		return interruptionStarted;
	}
	
	@Override
	public boolean isInterrupting () {
		return false;
	}
	
	public Round getInterruptedRound () {
		return interruptedRound;
	}
	
	/**
	 * This method will return this object which is extended to the actual round needed
	 * 
	 * @return this Round 
	 * 
	 */
	
	@Override
	public Round getInterruptionRound () {
		return this;
	}

	@Override
	public void finish () {
		setInterruptionStarted (false);		
	}

	@Override
	public void resume () {
	}

	@Override
	public void start () {
		Round tCurrentRound;
		
		tCurrentRound = roundManager.getCurrentRound ();
		setInterruptedRound (tCurrentRound);
		setInterruptionStarted (true);
	}
}
