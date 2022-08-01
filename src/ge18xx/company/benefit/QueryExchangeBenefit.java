package ge18xx.company.benefit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.PrivateCompany;
import ge18xx.utilities.XMLNode;

public class QueryExchangeBenefit extends ExchangeBenefit {
	public final static String NAME = "QUERY EXCHANGE";

	public QueryExchangeBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		setName (NAME);
	}
	
	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		super.configure (aPrivateCompany, aButtonRow);
	}

	public void showQueryDialog (JFrame aParentFrame) {
		Certificate tShareCertificate;
		String tQueryText;
		String tOwnerName;
		int tAnswer;
		
		tShareCertificate = getShareCertificate ();
		tOwnerName = privateCompany.getPresidentName ();
		tQueryText = tOwnerName + ", do you want to Exchange " + privateCompany.getAbbrev () + " for " + 
				certificatePercentage + "% of " + tShareCertificate.getCompanyAbbrev () + "?";

		tAnswer = JOptionPane.showConfirmDialog (aParentFrame, 
				tQueryText, "Exchange Private Share Benefit", 
		        JOptionPane.YES_NO_OPTION);

		if (tAnswer == JOptionPane.YES_OPTION) {
		  	handleExchangeCertificate ();
		}
	}
}
