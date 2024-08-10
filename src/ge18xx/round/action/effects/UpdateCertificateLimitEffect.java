package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class UpdateCertificateLimitEffect extends Effect {
	public static final AttributeName AN_OLD_CERTIFICATE_LIMIT = new AttributeName ("oldCertificateLimit");
	public static final AttributeName AN_NEW_CERTIFICATE_LIMIT = new AttributeName ("newCertificateLimit");
	public static final String NAME = "Update Certificate Limit";
	int oldCertificateLimit;
	int newCertificateLimit;

	public UpdateCertificateLimitEffect () {
		this (NAME);
	}

	public UpdateCertificateLimitEffect (String aName) {
		super (aName);
	}

	public UpdateCertificateLimitEffect (ActorI aActor, int aOldCertificateLimit, int aNewCertificateLimit) {
		super (NAME, aActor);
		setOldCertificateLimit (aOldCertificateLimit);
		setNewCertificateLimit (aNewCertificateLimit);
	}

	public UpdateCertificateLimitEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tOldCertificateLimit;
		int tNewCertificateLimit;
		
		tOldCertificateLimit = aEffectNode.getThisIntAttribute (AN_OLD_CERTIFICATE_LIMIT);
		tNewCertificateLimit = aEffectNode.getThisIntAttribute (AN_NEW_CERTIFICATE_LIMIT);
		setOldCertificateLimit (tOldCertificateLimit);
		setNewCertificateLimit (tNewCertificateLimit);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_OLD_CERTIFICATE_LIMIT, oldCertificateLimit);
		tEffectElement.setAttribute (AN_NEW_CERTIFICATE_LIMIT, newCertificateLimit);

		return tEffectElement;
	}

	public void setOldCertificateLimit (int aOldCertificateLimit) {
		oldCertificateLimit = aOldCertificateLimit;
	}

	public void setNewCertificateLimit (int aNewCertificateLimit) {
		newCertificateLimit = aNewCertificateLimit;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;

		tReport = REPORT_PREFIX + name + " for " + actor.getName () + " from "+ oldCertificateLimit + " to " + 
				newCertificateLimit + ".";

		return tReport;
	}

	public int getOldCertificateLimit () {
		return oldCertificateLimit;
	}

	public int getNewCertificateLimit () {
		return newCertificateLimit;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;

		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setCertificateLimit (newCertificateLimit);
			aRoundManager.updateAllRFPlayers ();
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tPlayer = (Player) actor;
			tPlayer.setCertificateLimit (oldCertificateLimit);
			aRoundManager.updateAllRFPlayers ();
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
