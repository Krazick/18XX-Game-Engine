package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class TransferOwnershipEffect extends ToEffect {
	public final static String NAME = "Transfer Ownership";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_PRESIDENT_SHARE = new AttributeName ("president");
	final static AttributeName AN_SHARE_PERCENT = new AttributeName ("percentage");
	Certificate certificate;

	public TransferOwnershipEffect () {
		super ();
		setName (NAME);
		setCertificate (Certificate.NO_CERTIFICATE);
	}

	public TransferOwnershipEffect (ActorI aFromActor, Certificate aCertificate, ActorI aToActor) {
		super (NAME, aFromActor, aToActor);
		setCertificate (aCertificate);
	}
	
	public TransferOwnershipEffect (ActorI aFromActor, String aFromName, Certificate aCertificate, ActorI aToActor, 
			String aToName) {
		super (NAME, aFromActor, aFromName, aToActor, aToName);
		setCertificate (aCertificate);
	}

	public TransferOwnershipEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tCompanyAbbrev;
		Certificate tCertificate;
		int tPercentage;
		boolean tPresidentShare;

		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tPercentage = aEffectNode.getThisIntAttribute (AN_SHARE_PERCENT);
		tPresidentShare = aEffectNode.getThisBooleanAttribute (AN_PRESIDENT_SHARE);
		tCertificate = aGameManager.getCertificate (tCompanyAbbrev, tPercentage, tPresidentShare);
		setCertificate (tCertificate);
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

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;
		String tToActorName;
		
		tEffectReport = "";
		tEffectReport += REPORT_PREFIX + name + " of ";
		tEffectReport += certificate.getPercentage () + "% of " + certificate.getCompanyAbbrev ();
		if (certificate.isPresidentShare ()) {
			tEffectReport += " (President Share)";
		}
		tEffectReport += " from " + getActorName ();
		tToActorName = getToActorName ();
		tEffectReport += " to " + tToActorName + ".";

		return tEffectReport;
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		PortfolioHolderI tFromHolder;
		Portfolio tToPortfolio;
		Portfolio tFromPortfolio;

		tEffectApplied = false;
		tToPortfolio = getToPortfolio ();
		tFromHolder = (PortfolioHolderI) getActor ();
		tFromPortfolio = tFromHolder.getPortfolio ();

		tEffectApplied = tToPortfolio.transferOneCertificateOwnership (tFromPortfolio, certificate);
		certificate.updateCorporationOwnership ();

		return tEffectApplied;
	}

	private Portfolio getToPortfolio () {
		PortfolioHolderI tToHolder;
		Portfolio tToPortfolio;
		Corporation tCorporation;
		Certificate tThisCertificate;
		Bank tBank;

		tToHolder = (PortfolioHolderI) getToActor ();
		tToPortfolio = tToHolder.getPortfolio ();

		// Test if the ToPortfolio has the Certificate. If this was a Close Corp Action,
		// and the ToHolder
		// is the Bank, need to get the Closed Portfolio from the Bank instead.
		if (isToActor (Bank.NAME)) {
			tCorporation = certificate.getCorporation ();
			tThisCertificate = tToPortfolio.getCertificate (tCorporation, certificate.getPercentage ());
			if (tThisCertificate == Certificate.NO_CERTIFICATE) {
				tBank = (Bank) tToHolder;
				tToPortfolio = tBank.getClosedPortfolio ();
			}
		}

		return tToPortfolio;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		PortfolioHolderI tFromHolder;
		PrivateCompany tPrivateCompany;
		Portfolio tToPortfolio;
		Portfolio tFromPortfolio;

		tEffectUndone = false;
		tToPortfolio = getToPortfolio ();
		if (certificate.isAPrivateCompany ()) {
			tPrivateCompany = (PrivateCompany) certificate.getCorporation ();
			tPrivateCompany.removeBenefitButtons ();
		}
		certificate.resetFrameButton ();
		tFromHolder = (PortfolioHolderI) getActor ();
		tFromPortfolio = tFromHolder.getPortfolio ();

		tEffectUndone = tFromPortfolio.transferOneCertificateOwnership (tToPortfolio, certificate);

		return tEffectUndone;
	}
}
