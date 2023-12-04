package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class AuctionBidChangeEffect extends Effect {
	public final static String NAME = "Auction Bid Change";
	public final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	public final static AttributeName AN_OLD_BID = new AttributeName ("oldBid");
	public final static AttributeName AN_NEW_BID = new AttributeName ("newBid");
	public static AttributeName AN_PERCENTAGE = new AttributeName ("percentage");
	public static AttributeName AN_IS_PRESIDENT = new AttributeName ("isPresident");
	private int oldBid;
	private int newBid;
	private Certificate certificate;

	public AuctionBidChangeEffect () {
		super ();
		setName (NAME);
	}

	public AuctionBidChangeEffect (ActorI aActor, int aOldBid, int aNewBid, Certificate aCertificate) {
		super (NAME, aActor);
		oldBid = aOldBid;
		newBid = aNewBid;
		certificate = aCertificate;
	}

	public AuctionBidChangeEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tCompanyAbbrev;
		boolean tIsPresident;
		int tPercentage;

		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tIsPresident = aEffectNode.getThisBooleanAttribute (AN_IS_PRESIDENT);
		tPercentage = aEffectNode.getThisIntAttribute (AN_PERCENTAGE);
		oldBid = aEffectNode.getThisIntAttribute (AN_OLD_BID);
		newBid = aEffectNode.getThisIntAttribute (AN_NEW_BID);
		certificate = aGameManager.getCertificate (tCompanyAbbrev, tPercentage, tIsPresident);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, certificate.getCompanyAbbrev ());
		tEffectElement.setAttribute (AN_IS_PRESIDENT, certificate.isPresidentShare ());
		tEffectElement.setAttribute (AN_PERCENTAGE, certificate.getPercentage ());
		tEffectElement.setAttribute (AN_OLD_BID, oldBid);
		tEffectElement.setAttribute (AN_NEW_BID, newBid);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		Player tPlayer;

		tPlayer = (Player) getActor ();

		return (REPORT_PREFIX + name + " for " + tPlayer.getName () + " Company Abbrev "
				+ certificate.getCompanyAbbrev () + " Old Bid " + Bank.formatCash (oldBid) + " New Bid "
				+ Bank.formatCash (newBid) + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			int tBidderCount;
			Player tBidder;
			Player tPlayer = (Player) actor;

			tBidderCount = certificate.getNumberOfBidders ();
			if (tBidderCount > 0) {
				for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
					tBidder = (Player) certificate.getCashHolderAt (tBidderIndex);
					if (tBidder.getName ().equals (tPlayer.getName ())) {
						certificate.setBidAt (tBidderIndex, newBid);
						tEffectApplied = true;
					}
				}
				aRoundManager.updateAuctionFrame ();
			}
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			int tBidderCount;
			Player tBidder;
			Player tPlayer = (Player) actor;

			tBidderCount = certificate.getNumberOfBidders ();
			if (tBidderCount > 0) {
				for (int tBidderIndex = 0; tBidderIndex < tBidderCount; tBidderIndex++) {
					tBidder = (Player) certificate.getCashHolderAt (tBidderIndex);
					if (tBidder.getName ().equals (tPlayer.getName ())) {
						certificate.setBidAt (tBidderIndex, oldBid);
						tEffectUndone = true;
						System.out.println ("Reset Bid for " + tPlayer.getName () + " for "
								+ certificate.getCompanyAbbrev () + " to " + oldBid + " from " + newBid);
					}
				}

			}
		}

		return tEffectUndone;
	}

	public int getOldBid () {
		return oldBid;
	}

	public int getNewBid () {
		return newBid;
	}

	public Certificate getCertificate () {
		return certificate;
	}
}
