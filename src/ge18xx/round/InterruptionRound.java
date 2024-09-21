package ge18xx.round;

public class InterruptionRound extends Round {
	Round interruptedRound;
	
	public InterruptionRound (RoundManager aRoundManager) {
		super (aRoundManager);
	}

	public void setInterruptedRound (Round aInterruptedRound) {
		interruptedRound = aInterruptedRound;
	}
	
	public Round getInterruptedRound () {
		return interruptedRound;
	}
}
