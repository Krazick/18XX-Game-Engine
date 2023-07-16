package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class CertificateInfoFrame extends JFrame implements ActionListener {
	public static final String GET_INFO = "GET INFO";
	private static final long serialVersionUID = 1L;
	private Certificate certificate;

	public CertificateInfoFrame () {
		super ();
	}

	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
		fillFrame ();
	}
	
	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction;

		tTheAction = aActionEvent.getActionCommand ();
		System.out.println ("Certificate Info Dialog - Action " + tTheAction);
		if (GET_INFO.equals (tTheAction)) {
			setVisible (true);
		}
	}

	public void fillFrame () {
		JLabel tTitle;
		
		tTitle = new JLabel ("Certificate for " + certificate.getCompanyName () + " (" + 
							certificate.getCompanyAbbrev () + ")");
		add (tTitle);
		this.setSize (500, 500);
	}
}
