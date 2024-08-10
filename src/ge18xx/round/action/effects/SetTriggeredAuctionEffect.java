package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetTriggeredAuctionEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set Triggered Auction";
	final static AttributeName AN_TRIGGERED_AUCTION = new AttributeName ("triggeredAuction");

	public SetTriggeredAuctionEffect (String aName) {
		super (aName);
		setName (NAME);
	}

	public SetTriggeredAuctionEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetTriggeredAuctionEffect (ActorI aActor, boolean aTriggeredAuction) {
		super (NAME, aActor, aTriggeredAuction);
	}

	public SetTriggeredAuctionEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_TRIGGERED_AUCTION);
		setName (NAME);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_TRIGGERED_AUCTION);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + ".");
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
			tPlayer.setTriggeredAuction (getBooleanFlag ());
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("The provided Actor " + actor.getName () + " is not a Player");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tEffectUndone = false;
		if (actor.isATrainCompany ()) {
			tPlayer = (Player) actor;
			tPlayer.setTriggeredAuction (! getBooleanFlag ());
		} else {
			setUndoFailureReason ("The provided Actor " + actor.getName () + " is not a Player");
		}
		tEffectUndone = true;

		return tEffectUndone;
	}
}
