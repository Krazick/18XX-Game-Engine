package ge18xx.round;

import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeRoundAction;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;

public class InterruptionRound extends Round {
	public final static AttributeName AN_INTERRUPTED_ROUND_NAME = new AttributeName ("interupttedName");
	public final static AttributeName AN_INTERRUPTION_STARTED = new AttributeName ("interuptionStarted");
	Round interruptedRound;
	boolean interruptionStarted;
	
	public InterruptionRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setInterruptionStarted (false);
	}
	
	@Override
	public void loadRound (XMLNode aRoundNode) {
		String tInterruptedRoundName;
		boolean tInterruptionStarted;
		Round tInterruptedRound;
		
		super.loadRound (aRoundNode);
		tInterruptedRoundName = aRoundNode.getThisAttribute (AN_INTERRUPTED_ROUND_NAME);
		tInterruptionStarted = aRoundNode.getThisBooleanAttribute (AN_INTERRUPTION_STARTED);
		
		tInterruptedRound = roundManager.getRoundByTypeName (tInterruptedRoundName);
		setInterruptedRound (tInterruptedRound);
		setInterruptionStarted (tInterruptionStarted);
	}
	
	public void setRoundTo (Round aNewRound, int aRoundID, String aOldRoundID, String aNewRoundID, 
							ChangeRoundAction aChangeRoundAction) {
		String tGameName;
		RoundFrame tRoundFrame;
		ActorI.ActionStates tNewRoundState;

		tNewRoundState = aNewRound.getRoundState ();
		roundManager.changeRound (interruptedRound, tNewRoundState, aNewRound, aOldRoundID, aNewRoundID, 
									aChangeRoundAction);
		tGameName = roundManager.getGameName ();
		tRoundFrame = roundManager.getRoundFrame ();
		tRoundFrame.setFrameLabel (tGameName, aRoundID);
	}

	public void setInterruptedRound (Round aInterruptedRound) {
		interruptedRound = aInterruptedRound;
	}

	public void setInterruptionStarted (boolean aInterruptionStarted) {
		interruptionStarted = aInterruptionStarted;
	}
	
	@Override
	public String getID () {
		String tID;

		tID = idPart1 + GUI.EMPTY_STRING;

		return tID;
	}

	@Override
	public void setRoundAttributes (XMLElement aXMLElement) {
		super.setRoundAttributes (aXMLElement);
		if (interruptedRound != InterruptionRound.NO_ROUND) {
			aXMLElement.setAttribute (AN_INTERRUPTED_ROUND_NAME, interruptedRound.getName ());
			aXMLElement.setAttribute (AN_INTERRUPTION_STARTED, interruptionStarted ());
		}
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
		
		ActorI.ActionStates tRoundState;
		ActorI.ActionStates tInterruptedRoundState;
		ChangeRoundAction tChangeRoundAction;
		String tNewRoundID;
		String tCurrentRoundID;
		
		tRoundState = getRoundState ();
		tInterruptedRoundState = interruptedRound.getRoundState ();
		tNewRoundID = interruptedRound.getID ();
		tCurrentRoundID = getID ();

		tChangeRoundAction = new ChangeRoundAction (tRoundState, tCurrentRoundID, this);
		tChangeRoundAction.setChainToPrevious (true);
		if (aInterruptionFrame != XMLFrame.NO_XML_FRAME) {
			tChangeRoundAction.addHideFrameEffect (this, aInterruptionFrame);
			aInterruptionFrame.hideFrame ();
		}
		roundManager.changeRound (this, tInterruptedRoundState, interruptedRound, tCurrentRoundID, 
								tNewRoundID, tChangeRoundAction);
		roundManager.addAction (tChangeRoundAction);
		roundManager.updateRoundFrame ();
	}

	@Override
	public void returnTo (Round aInterruptedRound) {
		setInterruptedRound (aInterruptedRound);
		setInterruptionStarted (true);
		roundManager.updateRoundFrame ();
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
