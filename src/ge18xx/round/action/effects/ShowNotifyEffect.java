package ge18xx.round.action.effects;

import javax.swing.JDialog;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ShowNotifyEffect extends ToEffect {
	public static final AttributeName AN_MESSAGE = new AttributeName ("message");
	public static final String NAME = "Show Notify";
	String message;
	JDialog notifyDialog;

	public ShowNotifyEffect (ActorI aFromActor, ActorI aToActor, String aMessage) {
		super (NAME, aFromActor, aToActor);
		setMessage (aMessage);
	}

	public void setMessage (String aMessage) {
		message = aMessage;
	}

	public ShowNotifyEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tMessage;

		setName (NAME);

		tMessage = aEffectNode.getThisAttribute (AN_MESSAGE);
		setMessage (tMessage);	// Always set the Title of the Frame
	}
	
	public String getMessage () {
		return message;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = true;
		printEffectReport (aRoundManager);
		
		return tEffectApplied;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_MESSAGE, message);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + actor.getName () + " will " + name + " with message [" + message + "].");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		tEffectUndone = false;

		return tEffectUndone;
	}

}
