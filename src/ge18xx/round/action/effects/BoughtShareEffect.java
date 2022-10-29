package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class BoughtShareEffect extends Effect {
	public final static String NAME = "Bought Share";
	final static AttributeName AN_BOUGHT_SHARE = new AttributeName ("boughtShare");
	String boughtShare;

	public BoughtShareEffect () {
		super ();
		setName (NAME);
	}

	public BoughtShareEffect (ActorI aActor, String aBoughtShare) {
		super (NAME, aActor);
		setBoughtShare (aBoughtShare);
	}

	public BoughtShareEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tBoughtShare;
		setName (NAME);

		tBoughtShare = aEffectNode.getThisAttribute (AN_BOUGHT_SHARE);
		setBoughtShare (tBoughtShare);
	}

	public String getBoughtShare () {
		return boughtShare;
	}

	public void setBoughtShare (String aBoughtShare) {
		boughtShare = aBoughtShare;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_ACTOR_NAME);
		tEffectElement.setAttribute (AN_BOUGHT_SHARE, boughtShare);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + actor.getName () + " " + name + " a share of stock (" + boughtShare + ") this Stock Round.");
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
			tPlayer.setBoughtShare (boughtShare);
			tEffectApplied = true;
		}
		// If the Actor is NOT A Player, this Effect should not have been Added, so Complain

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setBoughtShare (Player.NO_SHARE_BOUGHT);
			tEffectUndone = true;
		}
		// If the Actor is NOT A Player, this Effect should not have been Added, so Complain

		return tEffectUndone;
	}
}
