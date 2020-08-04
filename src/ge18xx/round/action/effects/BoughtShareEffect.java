package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLDocument;

public class BoughtShareEffect extends Effect {
	public final static String NAME = "Bought Share";
	
	public BoughtShareEffect () {
		super ();
		setName (NAME);
	}

	public BoughtShareEffect (ActorI aActor) {
		super (NAME, aActor);
	}

	public BoughtShareEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	public boolean getBoughtShare () {
		return true;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_ACTOR_NAME);
	
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + actor.getName () + " " + name + " a share of stock this Stock Round.");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;
		
		tEffectApplied = false;
		if (actor instanceof Player) {
			tPlayer = (Player) actor;
			tPlayer.setBoughtShare (true);
		}
		
		tEffectApplied = true;
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;
		
		tEffectUndone = false;
		if (actor instanceof Player) {
			tPlayer = (Player) actor;
			tPlayer.setBoughtShare (false);
		}
		
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}
