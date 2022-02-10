package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class BidShareEffect extends Effect {
	public final static String NAME = "Bid On a Share";

	public BidShareEffect () {
		super ();
		setName (NAME);
	}

	public BidShareEffect (ActorI aActor) {
		super (NAME, aActor);
	}

	public BidShareEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
	
	public boolean getBidShare () {
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
		return (REPORT_PREFIX + actor.getName () + " " + name + " this StockRound.");
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
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setBidShare (true);
		}
		
		tEffectApplied = true;
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setBidShare (false);
		}
		
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}