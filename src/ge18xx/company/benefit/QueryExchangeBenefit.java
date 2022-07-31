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
	}

	public void showQueryDialog (JFrame aParentFrame) {
		Certificate tShareCertificate;
		String tQueryText;
		int tAnswer;
		
		tShareCertificate = getShareCertificate ();
		tQueryText = "Exchange " + privateCompany.getAbbrev () + " for " + certificatePercentage + "% of "
				+ tShareCertificate.getCompanyAbbrev () + "?";

		tAnswer = JOptionPane.showConfirmDialog (aParentFrame, 
				tQueryText, "Exchange Private Share Benefit", 
		        JOptionPane.YES_NO_OPTION);

		if (tAnswer == JOptionPane.NO_OPTION) {
			System.out.println ("DO NOT Exchange Share");
		} else if(tAnswer == JOptionPane.YES_OPTION) {
		  	System.out.println ("Exchange Share YES ANSWER");
		  	handleExchangeCertificate ();
		}
	}
}
