package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLDocument;

//TODO: Refactor TransferTrainEffect, TransferOwnershipEffect, ResponseToOfferEffect, and CashTransferEffect
//to extend a new SuperClass "ToEffect" to hold the "toActor" and methods setToActor, getToActor, getToActorName

public class TransferOwnershipEffect extends Effect {
	public final static String NAME = "Transfer Ownership";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_PRESIDENT_SHARE = new AttributeName ("president");
	final static AttributeName AN_SHARE_PERCENT = new AttributeName ("percentage");
	ActorI toActor;
	Certificate certificate;

	public TransferOwnershipEffect () {
		super ();
		setName (NAME);
		setCertificate (Certificate.NO_CERTIFICATE);
		setToActor (ActorI.NO_ACTOR);
	}

	public TransferOwnershipEffect (ActorI aFromActor, Certificate aCertificate, ActorI aToActor) {
		super (NAME, aFromActor);
		setCertificate (aCertificate);
		setToActor (aToActor);
	}

	public TransferOwnershipEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		String tCompanyAbbrev;
		String tFromActorName;
		String tToActorName;
		ActorI tFromActor, tToActor;
		Certificate tCertificate;
		int tPercentage;
		boolean tPresidentShare;
		
		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tFromActorName = aEffectNode.getThisAttribute (ActorI.AN_FROM_ACTOR_NAME);
		tToActorName = aEffectNode.getThisAttribute (ActorI.AN_TO_ACTOR_NAME);
		tFromActor = aGameManager.getActor (tFromActorName);
		tToActor = aGameManager.getActor (tToActorName);
		tPercentage = aEffectNode.getThisIntAttribute (AN_SHARE_PERCENT);
		tPresidentShare = aEffectNode.getThisBooleanAttribute (AN_PRESIDENT_SHARE);
		tCertificate = aGameManager.getCertificate (tCompanyAbbrev, tPercentage, tPresidentShare);
		setCertificate (tCertificate);
		setActor (tFromActor);
		setToActor (tToActor);
	}

	public Certificate getCertificate () {
		return certificate;
	}
	
	public String getCompanyAbbrev () {
		return certificate.getCompanyAbbrev ();
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, certificate.getCompanyAbbrev ());
		tEffectElement.setAttribute (AN_PRESIDENT_SHARE, certificate.isPresidentShare ());
		tEffectElement.setAttribute (AN_SHARE_PERCENT, certificate.getPercentage ());
		tEffectElement.setAttribute (ActorI.AN_TO_ACTOR_NAME, toActor.getName ());
	
		return tEffectElement;
	}

	public ActorI getToActor () {
		return toActor;
	}
	
	@Override
	public String getToActorName () {
		String tToActorName = ActorI.NO_NAME;
		
		if (toActor != ActorI.NO_ACTOR) {
			tToActorName = toActor.getName ();
		}
		
		return tToActorName;
	}
	
	@Override 
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport = "";
		
		tEffectReport += REPORT_PREFIX + name + " of ";
		tEffectReport += certificate.getPercentage () + "% of " +  certificate.getCompanyAbbrev ();
		tEffectReport += " from " +  actor.getName ();
		tEffectReport += " to " + toActor.getName () + ".";
		
		return tEffectReport;
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}
	
	public void setToActor (ActorI aActor) {
		toActor = aActor;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		PortfolioHolderI tToHolder, tFromHolder;
		Portfolio tToPortfolio, tFromPortfolio;
		Corporation tCorporation;
		Certificate tThisCertificate;
		Bank tBank;
		
		tEffectApplied = false;
		tToHolder = (PortfolioHolderI) toActor;
		tToPortfolio = tToHolder.getPortfolio ();
		
		// TODO: When the new "ToEffect" is created, also create a new "isActor" Method that receives an Actor's Name and does
		// the comparison of the toActor's Name
		
		// Test if the ToPortfolio has the Certificate. If this was a Close Corp Action, and the ToHolder 
		// is the Bank, need to get the Closed Portfolio from the Bank instead.
		if (tToHolder.getName ().equals (Bank.NAME)){
			tCorporation = certificate.getCorporation ();
			tThisCertificate = tToPortfolio.getCertificate (tCorporation, certificate.getPercentage ());
			if (tThisCertificate == Certificate.NO_CERTIFICATE) {
				tBank = (Bank) tToHolder;
				tToPortfolio = tBank.getClosedPortfolio ();
			}
		}
		tFromHolder = (PortfolioHolderI) actor;
		tFromPortfolio = tFromHolder.getPortfolio ();

		tEffectApplied = tToPortfolio.transferOneCertificateOwnership (tFromPortfolio, certificate);
		certificate.updateCorporationOwnership ();
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		PortfolioHolderI tToHolder, tFromHolder;
		Portfolio tToPortfolio, tFromPortfolio;
		Corporation tCorporation;
		Certificate tThisCertificate;
		Bank tBank;
//		String tToHolderName, tFromHolderName, tCorpAbbrev;
		
		tEffectUndone = false;
		tToHolder = (PortfolioHolderI) toActor;
		tToPortfolio = tToHolder.getPortfolio ();
//		System.out.println ("Undoing " + getEffectReport (aRoundManager));
		
		// TODO: When the new "ToEffect" is created, also create a new "isActor" Method that receives an Actor's Name and does
		// the comparison of the toActor's Name

		// Test if the ToPortfolio has the Certificate. If this was a Close Corp Action,  and the ToHolder 
		// is the Bank, need to get the Closed Portfolio from the Bank instead.
		if (tToHolder.getName ().equals (Bank.NAME)){
			tCorporation = certificate.getCorporation ();
			tThisCertificate = tToPortfolio.getCertificate (tCorporation, certificate.getPercentage ());
			if (tThisCertificate == Certificate.NO_CERTIFICATE) {
				tBank = (Bank) tToHolder;
				tToPortfolio = tBank.getClosedPortfolio ();
			}
		}
		tFromHolder = (PortfolioHolderI) actor;
		tFromPortfolio = tFromHolder.getPortfolio ();
//		tToHolderName = tToPortfolio.getHolderName ();
//		tFromHolderName = tFromPortfolio.getHolderName ();
//		tCorpAbbrev = certificate.getCompanyAbbrev ();
//		System.out.println ("Ready to Transfer " + tCorpAbbrev + " Cert " + certificate.getPercentage () + "% From " + 
//				tToHolderName + " back to " + tFromHolderName);
		
		tEffectUndone = tFromPortfolio.transferOneCertificateOwnership (tToPortfolio, certificate);
		
//		System.out.println ("After Transfer Back");
//		tFromPortfolio.printPortfolioInfo ();
		
		return tEffectUndone;
	}
}
