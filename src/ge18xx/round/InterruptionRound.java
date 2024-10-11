package ge18xx.round;

import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeRoundAction;
import geUtilities.xml.XMLFrame;

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
		
	}

	@Override
	public void finish (XMLFrame aInterruptionFrame) {
		setInterruptionStarted (false);		
		
		ActorI.ActionStates tRoundType;
		ActorI.ActionStates tInterruptedRoundType;
		ChangeRoundAction tChangeRoundAction;
		String tOldRoundID;
		String tNewRoundID;
		String tCurrentRoundID;
		
		tRoundType = getRoundState ();
		tInterruptedRoundType = interruptedRound.getRoundState ();
		tOldRoundID = interruptedRound.getID ();
		tNewRoundID = tOldRoundID;
		tCurrentRoundID = getID ();

		tChangeRoundAction = new ChangeRoundAction (tRoundType, tCurrentRoundID, this);
		tChangeRoundAction.addStateChangeEffect (this, tRoundType, tInterruptedRoundType);
		tChangeRoundAction.setChainToPrevious (true);
		roundManager.updateRoundFrame ();
		if (aInterruptionFrame != XMLFrame.NO_XML_FRAME) {
			tChangeRoundAction.addHideFrameEffect (this, aInterruptionFrame);
			aInterruptionFrame.hideFrame ();
		}
		roundManager.changeRound (this, tInterruptedRoundType, interruptedRound, tOldRoundID, tNewRoundID, tChangeRoundAction);
		roundManager.addAction (tChangeRoundAction);
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
