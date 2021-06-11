package ge18xx.company.benefit;

import javax.swing.JPanel;

import ge18xx.company.PrivateCompany;
import ge18xx.utilities.XMLNode;

public class ExchangeBenefit extends CertificateBenefit {
	public final static String NAME = "EXCHANGE";

	public ExchangeBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		setName (NAME);
	}
	
	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			System.out.println ("Should Configure for Private-Share Certificate Exchange");
		}
	}

}
