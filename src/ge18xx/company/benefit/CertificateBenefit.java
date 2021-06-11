package ge18xx.company.benefit;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class CertificateBenefit extends Benefit {
	final static AttributeName AN_CERTIFICATE_PERCENTAGE = new AttributeName ("certificatePercentage");
	final static AttributeName AN_CERTIFICATE_ID = new AttributeName ("certificateID");
	final static AttributeName AN_CERTIFICATE_PRESIDENT = new AttributeName ("certificatePresident");
	public final static String NAME = "CERTIFICATE";
	String certificateID;
	int certificatePercentage;
	boolean certificatePresident;
	
	public CertificateBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		String tCertificateID;
		int tCertificatePercentage;
		boolean tCertificatePresident;
		
		tCertificateID = aXMLNode.getThisAttribute (AN_CERTIFICATE_ID);
		tCertificatePercentage = aXMLNode.getThisIntAttribute (AN_CERTIFICATE_PERCENTAGE);
		tCertificatePresident = aXMLNode.getThisBooleanAttribute (AN_CERTIFICATE_PRESIDENT);
		setCertificateID (tCertificateID);
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

	private void setCertificateID (String aCertificateID) {
		certificateID = aCertificateID;
	}

	public int getCertificatePercentage () {
		return certificatePercentage;
	}
	
	public String getCertificateID () {
		return certificateID;
	}
	
	public boolean getCcertificatePresident () {
		return certificatePresident;
	}

	@Override
	public int getCost () {
		return 0;
	}
}
