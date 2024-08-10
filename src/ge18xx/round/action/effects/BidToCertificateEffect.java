package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class BidToCertificateEffect extends Effect {
	public final static String NAME = "Add Bid to Certificate";
	final static AttributeName AN_CERTIFICATE_NAME = new AttributeName ("certificateName");
	final static AttributeName AN_BID_AMOUNT = new AttributeName ("bidAmount");
	String certificateName;
	int bidAmount;

	public BidToCertificateEffect () {
		super ();
		setName (NAME);
		setActor (ActorI.NO_ACTOR);
		setCertificateName (Certificate.NO_CERTIFICATE_NAME);
	}

	public BidToCertificateEffect (ActorI aActor, Certificate aCertificate, int aBidAmount) {
		super (NAME, aActor);
		setCertificateName (aCertificate.getCompanyAbbrev ());
		bidAmount = aBidAmount;
	}

	public BidToCertificateEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		certificateName = aEffectNode.getThisAttribute (AN_CERTIFICATE_NAME);
		bidAmount = aEffectNode.getThisIntAttribute (AN_BID_AMOUNT);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_CERTIFICATE_NAME, certificateName);
		tEffectElement.setAttribute (AN_BID_AMOUNT, bidAmount);

		return tEffectElement;
	}

	public String getCertificateName () {
		return certificateName;
	}

	public void setCertificateName (String aCertificateName) {
		certificateName = aCertificateName;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + certificateName + " made by " + actor.getName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		// This Effect does not need to be applied, since the creation of the Action
		// adds the Bidder to the Certificate
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		boolean tPresidentShare;
		int tPercentage;
		Certificate tCertificate;
		String tFailureReason;

		tEffectUndone = false;
		tPercentage = 100;
		tPresidentShare = true;
		tCertificate = aRoundManager.getCertificate (certificateName, tPercentage, tPresidentShare);
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tCertificate.removeBidder ((CashHolderI) actor);
			tEffectUndone = true;
		} else {
			tFailureReason = "Certificate " + certificateName + " " + tPercentage + "% that is ";
			if (! tPresidentShare) {
				tFailureReason += "NOT ";
			}
			tFailureReason += "the President Share";
			setUndoFailureReason (tFailureReason);
		}

		return tEffectUndone;
	}
}
