package ge18xx.company.benefit;

import javax.swing.JPanel;

import ge18xx.company.PrivateCompany;
import ge18xx.utilities.XMLNode;

public class FreeCertificateBenefit extends CertificateBenefit {
	public final static String NAME = "FREE_CERTIFICATE";

	public FreeCertificateBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		setName (NAME);
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		super.configure (aPrivateCompany, aButtonRow);
	}
}
