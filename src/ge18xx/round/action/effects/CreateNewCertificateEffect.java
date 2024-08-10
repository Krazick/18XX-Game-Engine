/**
 * 
 */
package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

/**
 * @author Mark J Smith
 *
 */
public class CreateNewCertificateEffect extends TransferOwnershipEffect {
	public static final String NAME = "Create New Certificate";
	public static final AttributeName AN_CORPORATION_ID = new AttributeName ("corporationID");
	String companyAbbrev;
	boolean isPresident;
	int percentage;
	int corporationID;
	
	/**
	 * 
	 */
	public CreateNewCertificateEffect () {
		super ();
		setName (NAME);
	}

	/**
	 * @param aFromActor
	 * @param aCertificate
	 * @param aToActor
	 */
	public CreateNewCertificateEffect (ActorI aFromActor, Certificate aCertificate, ActorI aToActor) {
		super (aFromActor, aCertificate, aToActor);
		
		setName (NAME);
		
		Corporation tCorporation;
		
		setCompanyAbbrev (aCertificate.getCompanyAbbrev ());
		setIsPresident (aCertificate.isPresidentShare ());
		setPercentage (aCertificate.getPercentage ());
		
		tCorporation = aCertificate.getCorporation ();
		setCorporationID (tCorporation.getID ());
	}

	/**
	 * @param aFromActor
	 * @param aFromNickName
	 * @param aCertificate
	 * @param aToActor
	 * @param aToNickName
	 */
	public CreateNewCertificateEffect (ActorI aFromActor, String aFromNickName, Certificate aCertificate,
			ActorI aToActor, String aToNickName) {
		super (aFromActor, aFromNickName, aCertificate, aToActor, aToNickName);

		setName (NAME);
		
		Player tPlayer;
		Corporation tCorporation;
		Portfolio tPlayerPortfolio;
		Certificate tZeroCertificate;
		int tCorporationID;

		setCompanyAbbrev (aCertificate.getCompanyAbbrev ());
		setIsPresident (aCertificate.isPresidentShare ());
		setPercentage (aCertificate.getPercentage ());

		tCorporation = aCertificate.getCorporation ();
		tCorporationID = tCorporation.getID ();
		setCorporationID (tCorporationID);
		if (certificate == Certificate.NO_CERTIFICATE) {
			if (toActor.isAPlayer ()) {
				tPlayer = (Player) toActor;
				tPlayerPortfolio = tPlayer.getPortfolio ();
				tZeroCertificate = new Certificate (tCorporation, true, 
						Certificate.NO_PERCENTAGE, tPlayerPortfolio);
				tPlayerPortfolio.addCertificate (tZeroCertificate);
				setCertificate (tZeroCertificate);
			}
		}
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		// TODO -- ZERO Percent Cert doesn't have Corporation assigned, because the Cert is not
		// in the corporationCertificates List. And need to create that Certificate. 
		// Verify via Breakpoint the attributes below are set. 
		tEffectElement.setAttribute (AN_CORPORATION_ID, corporationID);

		return tEffectElement;
	}

	/**
	 * @param aEffectNode
	 * @param aGameManager
	 */
	public CreateNewCertificateEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		Certificate tNewCertificate;
		Portfolio tPlayerPortfolio;
		Player tPlayer;
		Corporation tCorporation;
		String tCompanyAbbrev;
		boolean tIsPresident;
		int tPercentage;
		int tCorporationID;
		
		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tPercentage = aEffectNode.getThisIntAttribute (AN_SHARE_PERCENT);
		tCorporationID = aEffectNode.getThisIntAttribute (AN_CORPORATION_ID);
		tIsPresident = aEffectNode.getThisBooleanAttribute (AN_PRESIDENT_SHARE);

		setCompanyAbbrev (tCompanyAbbrev);
		setIsPresident (tIsPresident);
		setPercentage (tPercentage);
		setCorporationID (tCorporationID);
		
		tCorporation = aGameManager.getCorporationByID (tCorporationID);
		if (toActor.isAPlayer ()) {
			tPlayer = (Player) toActor;
			tPlayerPortfolio = tPlayer.getPortfolio ();
			tNewCertificate = new Certificate (tCorporation, isPresident, percentage, tPlayerPortfolio);
			setCertificate (tNewCertificate);
		}
	}
	
	private void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}
	
	private void setIsPresident (boolean aIsPresident) {
		isPresident = aIsPresident;
	}
	
	private void setPercentage (int aPercentage) {
		percentage = aPercentage;
	}
	
	private void setCorporationID (int aCorporationID) {
		corporationID = aCorporationID;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;
		String tToActorName;
		String tFromActorName;
		
		tEffectReport = "";
		if (certificate == Certificate.NO_CERTIFICATE) {
			tEffectReport = "No Certificate created";
		} else {
			tEffectReport += REPORT_PREFIX + name + " of ";
			tEffectReport += certificate.getPercentage () + "% of " + certificate.getCompanyAbbrev ();
			if (certificate.isPresidentShare ()) {
				tEffectReport += " (President Share)";
			}
			tFromActorName = getFromDisplayName ();
			tEffectReport += " from " + tFromActorName;
			tToActorName = getToDisplayName ();
			tEffectReport += " for " + tToActorName + ".";
		}
		
		return tEffectReport;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Portfolio tPlayerPortfolio;
		Player tPlayer;

		tEffectApplied = false;

		if (toActor.isAPlayer ()) {
			tPlayer = (Player) toActor;
			tPlayerPortfolio = tPlayer.getPortfolio ();
			tPlayerPortfolio.addCertificate (certificate);
			certificate.updateCorporationOwnership ();
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("The ToActor [" + toActor.getName () + "] is not a Player");
		}
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Certificate tCertificate;
		Certificate tRemovedCertificate;
		Portfolio tPlayerPortfolio;
		Player tPlayer;

		tEffectUndone = false;
		
		if (toActor.isAPlayer ()) {
			tPlayer = (Player) toActor;
			tPlayerPortfolio = tPlayer.getPortfolio ();
			if (tPlayerPortfolio.hasPresidentCertificateFor (companyAbbrev)) {
				tCertificate = tPlayerPortfolio.getCertificate (companyAbbrev, percentage, isPresident);
				tRemovedCertificate = tPlayerPortfolio.getThisCertificate (tCertificate);
				if (tRemovedCertificate == Certificate.NO_CERTIFICATE) {
					setUndoFailureReason ("The Portfolio did not find the proper certificate to Remove.");
				} else {
					tEffectUndone = true;
				}
			} else {
				setUndoFailureReason ("The Portfolio for [" + tPlayer.getName () + 
						"] does not have a Certificate for " + companyAbbrev + " with Percentage " + 
						percentage + " and President Flag as " + isPresident);
			}
		} else {
			setUndoFailureReason ("The ToActor [" + toActor.getName () + "] is not a Player");
		}

		return tEffectUndone;
	}
}
