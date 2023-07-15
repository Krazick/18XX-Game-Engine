package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

public class CertificateInfoDialog extends JDialog implements ActionListener {
	public static final String GET_INFO = "GET INFO";
	private static final long serialVersionUID = 1L;
	private Certificate certificate;

	public CertificateInfoDialog () {
		super ();
	}

	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}
	
	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction;

		tTheAction = aActionEvent.getActionCommand ();
		System.out.println ("Certificate Info Dialog - Action " + tTheAction);
		if (GET_INFO.equals (tTheAction)) {
			System.out.println ("Get Info for Certificate of " + certificate.getCompanyAbbrev ());
		}

		
	}

}
