package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.InterruptionRound;
import ge18xx.round.Round;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetInterruptedNameEffect extends Effect {
	public final static String NAME = "Set Interrupted Name";
	final static AttributeName AN_INTERRUPTED_NAME = new AttributeName ("interruptedName");
	String interruptedName;
	String interruptingRoundName;

	public SetInterruptedNameEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetInterruptedNameEffect (ActorI aActor, String aInterruptingRoundName, String aInterruptedName) {
		super (NAME, aActor);
		setInterruptingRoundName (aInterruptingRoundName);
		setInterruptedName (aInterruptedName);
	}

	public SetInterruptedNameEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tInterruptedName;

		tInterruptedName = aEffectNode.getThisAttribute (AN_INTERRUPTED_NAME);
		setInterruptedName (tInterruptedName);
	}

	public void setInterruptingRoundName (String aInterruptingRoundName) {	
		interruptingRoundName = aInterruptingRoundName;
	}

	public String tetInterruptingRoundName () {
		return interruptingRoundName;
	}

	public void setInterruptedName (String aInterruptedName) {	
		interruptedName = aInterruptedName;
	}

	public String getInterruptedName () {
		return interruptedName;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_INTERRUPTED_NAME, interruptedName);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + NAME + " to " + interruptedName + 
				" by interrupting " + interruptingRoundName +  ".");
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		InterruptionRound tInterruptingRound;
		Round tInterruptedRound;
		
		tEffectApplied = false;
		
		tInterruptingRound = (InterruptionRound) aRoundManager.getRoundByTypeName (interruptingRoundName);
		tInterruptedRound = aRoundManager.getRoundByTypeName (interruptedName);
		tInterruptingRound.setInterruptedRound (tInterruptedRound);
		
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
//		InterruptionRound tInterruptingRound;
//
//		tInterruptingRound = (InterruptionRound) aRoundManager.getRoundByTypeName (interruptingRoundName);
//		tInterruptingRound.setInterruptedRound (Round.NO_ROUND);
		
		tEffectUndone = true;

		return tEffectUndone;
	}
}
