package ge18xx.company.benefit;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.PrivateCompany;
import geUtilities.XMLNode;

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
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		String tLabelText;
		Certificate tShareCertificate;

		tShareCertificate = getShareCertificate ();
		tLabelText = "Free " + tShareCertificate.getPercentage () + "% of " + 
				tShareCertificate.getCompanyAbbrev ();
		if (tShareCertificate.isPresidentShare ()) {
			tLabelText += " President";
		}
		tBenefitLabel = new JLabel (tLabelText);
		setBorder (tShareCertificate, tBenefitLabel);

		return tBenefitLabel;
	}
}
