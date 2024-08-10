package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class BidShareEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Bid On a Share";
	final static AttributeName AN_HAS_BID_SHARE = new AttributeName ("hasBidShare");

	public BidShareEffect () {
		super ();
		setName (NAME);
	}

	public BidShareEffect (ActorI aActor, boolean aBidOnShare) {
		super (NAME, aActor, aBidOnShare);
	}
	
	public BidShareEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_HAS_BID_SHARE);
		setName (NAME);
	}

	public boolean getBidShare () {
		return true;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_ACTOR_NAME, AN_HAS_BID_SHARE);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		
		if (booleanFlag) {
			tReport = REPORT_PREFIX + actor.getName () + " has " + name + " this StockRound";
		} else {
			tReport = REPORT_PREFIX + actor.getName () + " is clearing the " + name + " flag.";
		}
		
		return tReport;
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
			tPlayer.setBidShare (booleanFlag);
		} else {
			setApplyFailureReason ("This is not a Player");
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
			tPlayer.setBidShare (! booleanFlag);
		} else {
			setUndoFailureReason ("This is not a Player");
		}

		tEffectUndone = true;

		return tEffectUndone;
	}
}