package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.StartAuctionAction;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class AddPrivateToAuctionEffect extends Effect {
	public final static String NAME = "Add Private to Auction";
	final static AttributeName AN_AUCTION_CERTIFICATE = new AttributeName ("auctionCertificate");
	String certificateAbbrev;
	
	public AddPrivateToAuctionEffect (ActorI aActor, Certificate aAuctionCertificate, Certificate aFreeCertificate) {
		super (NAME, aActor);
		setCertificateAbbrev (aAuctionCertificate.getCompanyAbbrev ());
	}

	public AddPrivateToAuctionEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tAuctionCertificate;
		
		setName (NAME);
		tAuctionCertificate = aEffectNode.getThisAttribute (AN_AUCTION_CERTIFICATE);
		setCertificateAbbrev (tAuctionCertificate);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_AUCTION_CERTIFICATE, certificateAbbrev);

		return tEffectElement;
	}

	private void setCertificateAbbrev (String aCertificateAbbrev) {
		certificateAbbrev = aCertificateAbbrev;
	}


	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + certificateAbbrev + " from " + actor.getName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		boolean tPresidentShare;
		int tPercentage;
		Certificate tCertificate;
		String tFailureReason;
		GameManager tGameManager;

		tEffectApplied = false;
		tPercentage = 100;
		tPresidentShare = true;
		tCertificate = aRoundManager.getCertificate (certificateAbbrev, tPercentage, tPresidentShare);
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tGameManager = aRoundManager.getGameManager ();
			tGameManager.addPrivateToAuction ((StartAuctionAction) StartAuctionAction.NO_ACTION);
			tEffectApplied = true;
		} else {
			tFailureReason = "Certificate for " + certificateAbbrev + " with " + tPercentage + "% that is ";
			if (! tPresidentShare) {
				tFailureReason += "NOT ";
			}
			tFailureReason += "the President Share was NOT Found";
			setApplyFailureReason (tFailureReason);
		}

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
		tCertificate = aRoundManager.getCertificate (certificateAbbrev, tPercentage, tPresidentShare);
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tCertificate.removeBidder ((CashHolderI) actor);
			tEffectUndone = true;
		} else {
			tFailureReason = "Certificate for " + certificateAbbrev + " for " + tPercentage + "% that is ";
			if (! tPresidentShare) {
				tFailureReason += "NOT ";
			}
			tFailureReason += "the President Share was NOT Found";
			setUndoFailureReason (tFailureReason);
		}

		return tEffectUndone;
	}

}
