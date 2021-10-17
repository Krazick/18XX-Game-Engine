package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class NewActingCorpEffect extends Effect {
	public final static String NAME = "New Acting Corporation";
	
	public NewActingCorpEffect () {
		super (NAME);
	}

	public NewActingCorpEffect (String aName) {
		super (aName);
	}

	public NewActingCorpEffect (ActorI aActor) {
		super (NAME, aActor);
	}

	public NewActingCorpEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_ACTOR_NAME);
		
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + actor.getName () + " should Start Operations.");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		tEffectApplied = false;
		if (actor.isACorporation ()) {
			// TODO -- Properly apply the Effect (Change Round Frame Action Button and enable)
			aRoundManager.doneAction ((Corporation) actor);
		}
		
		tEffectApplied = true;
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		tEffectUndone = true;
		
		return tEffectUndone;
	}

}
