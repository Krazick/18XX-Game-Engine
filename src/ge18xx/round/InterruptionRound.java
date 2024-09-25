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
	
	public boolean interruptionStarted () {
		return interruptionStarted;
	}
	
	public boolean interruptRound () {
		return false;
	}
	
	public Round getInterruptedRound () {
		return interruptedRound;
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
