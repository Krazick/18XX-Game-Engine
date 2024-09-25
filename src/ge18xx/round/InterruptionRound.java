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
}
