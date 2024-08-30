package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Bidders;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.ParsingRoutineI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

public class RemoveBiddersEffect extends Effect {
	public final static String NAME = "Remove All Bids";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_PERCENTAGE = new AttributeName ("percentage");
	final static AttributeName AN_IS_PRESIDENT = new AttributeName ("isPresident");
	String companyAbbrev;
	int percentage;
	boolean isPresident;
	Bidders bidders;
	
	public RemoveBiddersEffect (ActorI aActor, Certificate aCertificate, Bidders aBidders) {
		this (NAME, aActor, aCertificate, aBidders);
	}

	public RemoveBiddersEffect (String aName, ActorI aActor, Certificate aCertificate, Bidders aBidders) {
		super (aName, aActor);

		String tCompanyAbbrev = aCertificate.getCompanyAbbrev ();
		int tPercentage = aCertificate.getPercentage ();
		boolean tIsPresident = aCertificate.isPresidentShare ();

		setCompanyAbbrev (tCompanyAbbrev);
		setPercentage (tPercentage);
		setIsPresident (tIsPresident);
		setBidders (aBidders);
	}

	public RemoveBiddersEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		String tCompanyAbbrev;
		int tPercentage;
		boolean tIsPresident;
		Certificate tCertificate;

		// TODO To Build the Bidders, needs to get the Certificate to which this will be attached to,
		// So that it can be properly parsed. The Bidders object needs to be created with this Certificate.
		// The Certificate itself will have the bidders attached initially when this is Parsed, but then 
		// It needs to be removed from the Certificate, since this effect removes it.
		// This knotted up code is only used for loading from a Save Game to allow for this effect to UNDO,
		// and have the Bidders object that is thus re-created properly so it is re-attached to the Certificate
		// That will then allow further Raising/Passing 
		
		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tPercentage = aEffectNode.getThisIntAttribute (AN_PERCENTAGE);
		tIsPresident = aEffectNode.getThisBooleanAttribute (AN_IS_PRESIDENT);
		setCompanyAbbrev (tCompanyAbbrev);
		setPercentage (tPercentage);
		setIsPresident (tIsPresident);
		tCertificate = aGameManager.getCertificate (tCompanyAbbrev, tPercentage, tIsPresident);
		bidders = new Bidders (tCertificate);
		addBiddersInfo (aEffectNode);
		System.out.println ("Bidder Count " + bidders.getCount () + " Bidders " + bidders.getBidderNames ());
		tCertificate.removeBidders ();
	}
	
	public void addBiddersInfo (XMLNode aBiddersNode) {
		XMLNodeList tXMLBiddersNodeList;
		
		tXMLBiddersNodeList = new XMLNodeList (biddersParsingRoutine);
		tXMLBiddersNodeList.parseXMLNodeList (aBiddersNode, Bidders.EN_BIDDERS);
	}

	public ParsingRoutineI biddersParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aBiddersNode) {
			bidders.addBidderInfo (aBiddersNode);
		}
	};

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		XMLElement tBiddersElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, getCompanyAbbrev ());
		tEffectElement.setAttribute (AN_PERCENTAGE, getPercentage ());
		tEffectElement.setAttribute (AN_IS_PRESIDENT, isPresident ());
		tBiddersElement = bidders.getOnlyBiddersElement (aXMLDocument);
		tEffectElement.appendChild (tBiddersElement);
		
		return tEffectElement;
	}
	
	private void setBidders (Bidders aBidders) {
		bidders = aBidders;
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
		Bidders tBidders;

		tEffectApplied = false;
		tCertificate = aRoundManager.getCertificate (getCompanyAbbrev (), getPercentage (), isPresident ());
		// TODO: Need to capture all Bidders and save to allow for Undo to restore them.
		// Save for each Bidder, the Actor Name, the Bidder Index, and the Bid Amount
		
		tBidders = tCertificate.getBidders ();
		setBidders (tBidders);
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tCertificate.removeBidders ();
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Certificate tCertificate;
		
		tCertificate = aRoundManager.getCertificate (companyAbbrev, percentage, isPresident);
		tCertificate.setBidders (bidders);
		tEffectUndone = true;
		
		return tEffectUndone;
	}

}
