package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class RemoveAllBidsEffect extends Effect {
	public final static String NAME = "Remove All Bids";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_PERCENTAGE = new AttributeName ("percentage");
	final static AttributeName AN_IS_PRESIDENT = new AttributeName ("isPresident");
	String companyAbbrev;
	int percentage;
	boolean isPresident;

	public RemoveAllBidsEffect () {
		this (NAME);
	}

	public RemoveAllBidsEffect (String aName) {
		super (aName);
	}

	public RemoveAllBidsEffect (ActorI aActor, Certificate aCertificate) {
		this (NAME, aActor, aCertificate);
	}

	public RemoveAllBidsEffect (String aName, ActorI aActor, Certificate aCertificate) {
		super (aName, aActor);

		String tCompanyAbbrev = aCertificate.getCompanyAbbrev ();
		int tPercentage = aCertificate.getPercentage ();
		boolean tIsPresident = aCertificate.isPresidentShare ();

		setCompanyAbbrev (tCompanyAbbrev);
		setPercentage (tPercentage);
		setIsPresident (tIsPresident);
	}

	public RemoveAllBidsEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		String tCompanyAbbrev;
		int tPercentage;
		boolean tIsPresident;

		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tPercentage = aEffectNode.getThisIntAttribute (AN_PERCENTAGE);
		tIsPresident = aEffectNode.getThisBooleanAttribute (AN_IS_PRESIDENT);
		setCompanyAbbrev (tCompanyAbbrev);
		setPercentage (tPercentage);
		setIsPresident (tIsPresident);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, getCompanyAbbrev ());
		tEffectElement.setAttribute (AN_PERCENTAGE, getPercentage ());
		tEffectElement.setAttribute (AN_IS_PRESIDENT, isPresident ());

		return tEffectElement;
	}

	private String getCompanyAbbrev () {
		return companyAbbrev;
	}

	private boolean isPresident () {
		return isPresident;
	}

	private int getPercentage () {
		return percentage;
	}

	private void setIsPresident (boolean aIsPresident) {
		isPresident = aIsPresident;
	}

	private void setPercentage (int tPercentage) {
		percentage = tPercentage;
	}

	private void setCompanyAbbrev (String aCorporationAbbrev) {
		companyAbbrev = aCorporationAbbrev;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		Certificate tCertificate;
		boolean tEffectApplied;

		tEffectApplied = false;
		tCertificate = aRoundManager.getCertificate (getCompanyAbbrev (), getPercentage (), isPresident ());
		// TODO: Need to capture all Bidders and save to allow for Undo to restore them.
		// Save for each Bidder, the Actor Name, the Bidder Index, and the Bid Amount
		
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tCertificate.removeAllBids ();
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		// TODO:  Need to restore all Bidders
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}
