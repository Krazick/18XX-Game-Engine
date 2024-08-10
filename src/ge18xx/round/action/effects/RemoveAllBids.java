package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class RemoveAllBids extends Effect {
	public final static String NAME = "Remove All Bids";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_PERCENTAGE = new AttributeName ("percentage");
	final static AttributeName AN_IS_PRESIDENT = new AttributeName ("isPresident");
	String companyAbbrev;
	int percentage;
	boolean isPresident;

	public RemoveAllBids () {
		this (NAME);
	}

	public RemoveAllBids (String aName) {
		super (aName);
	}

	public RemoveAllBids (ActorI aActor, Certificate aCertificate) {
		this (NAME, aActor, aCertificate);
	}

	public RemoveAllBids (String aName, ActorI aActor, Certificate aCertificate) {
		super (aName, aActor);

		String tCompanyAbbrev = aCertificate.getCompanyAbbrev ();
		int tPercentage = aCertificate.getPercentage ();
		boolean tIsPresident = aCertificate.isPresidentShare ();

		setCompanyAbbrev (tCompanyAbbrev);
		setPercentage (tPercentage);
		setIsPresident (tIsPresident);
	}

	public RemoveAllBids (XMLNode aEffectNode, GameManager aGameManager) {
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
		boolean tEffectApplied = false;

		tCertificate = aRoundManager.getCertificate (getCompanyAbbrev (), getPercentage (), isPresident ());
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tCertificate.removeAllBids ();
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
