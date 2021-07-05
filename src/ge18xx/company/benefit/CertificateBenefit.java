package ge18xx.company.benefit;

import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class CertificateBenefit extends Benefit {
	final static AttributeName AN_CERTIFICATE_PERCENTAGE = new AttributeName ("certificatePercentage");
	final static AttributeName AN_CORPORATION_ID = new AttributeName ("corporationID");
	final static AttributeName AN_CERTIFICATE_PRESIDENT = new AttributeName ("certificatePresident");
	public final static String NAME = "CERTIFICATE";
	int corporationID;
	int certificatePercentage;
	boolean certificatePresident;
	
	public CertificateBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		int tCorporationID;
		int tCertificatePercentage;
		boolean tCertificatePresident;
		
		tCorporationID = aXMLNode.getThisIntAttribute (AN_CORPORATION_ID);
		tCertificatePercentage = aXMLNode.getThisIntAttribute (AN_CERTIFICATE_PERCENTAGE);
		tCertificatePresident = aXMLNode.getThisBooleanAttribute (AN_CERTIFICATE_PRESIDENT);
		setCorporationID (tCorporationID);
		setCertificatePercentage (tCertificatePercentage);
		setCertificatePresident (tCertificatePresident);
		setName (NAME);
	}

	private void setCertificatePresident (boolean aCertificatePresident) {
		certificatePresident = aCertificatePresident;
	}

	private void setCertificatePercentage (int aCertificatePercentage) {
		certificatePercentage = aCertificatePercentage;
	}

	private void setCorporationID (int aCorporationID) {
		corporationID = aCorporationID;
	}

	public int getCertificatePercentage () {
		return certificatePercentage;
	}
	
	public int getCorporationID () {
		return corporationID;
	}
	
	public boolean getCertificatePresident () {
		return certificatePresident;
	}

	@Override
	public int getCost () {
		return 0;
	}
	

	public Certificate getShareCertificate () {
		Certificate tShareCertificate = Certificate.NO_CERTIFICATE;
		GameManager tGameManager;
		CorporationList tShareCompanyList;
		Corporation tShareCompany;
		
		tGameManager = privateCompany.getGameManager ();
		tShareCompanyList = tGameManager.getShareCompanies ();
		tShareCompany = tShareCompanyList.getCorporationByID (corporationID);
		tShareCertificate = tShareCompany.getCertificate (certificatePercentage, certificatePresident);
		
		return tShareCertificate;
	}

}
