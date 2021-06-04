package ge18xx.company.benefit;

import javax.swing.JPanel;

import ge18xx.company.PrivateCompany;
import ge18xx.utilities.XMLNode;

public class ExchangeBenefit extends CertificateBenefit {

	public ExchangeBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
	}
	
	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		if (shouldConfigure (aPrivateCompany)) {
			System.out.println ("Should Configure for Private-Share Certificate Exchange");
		}
	}

}
