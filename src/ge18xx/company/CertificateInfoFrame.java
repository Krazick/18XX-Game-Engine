package ge18xx.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.toplevel.XMLFrame;

public class CertificateInfoFrame extends XMLFrame implements ActionListener {
	public static final String GET_INFO = "GET INFO";
	private static final long serialVersionUID = 1L;
	private Certificate certificate;
	private JPanel certificateInfoJPanel;
	int padding1;

	public CertificateInfoFrame () {
		super ();
		padding1 = 10;
	}

	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}
	
	@Override
	public void setGameManager (String aFrameTitle, GameManager aGameManager) {
		super.setGameManager (aFrameTitle, aGameManager);
		System.out.println ("Certificate Info Dialog - Setting Game Manager for  " + certificate.getCompanyName ());
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
		Corporation tCorporation;
		JLabel tTitle;
		JLabel tNote;
		JLabel tPrice;
		JLabel tRevenue;
		
		certificateInfoJPanel = new JPanel ();
		certificateInfoJPanel.setLayout (new BoxLayout (certificateInfoJPanel, BoxLayout.Y_AXIS));
		certificateInfoJPanel.setBorder (BorderFactory.createEmptyBorder (padding1, padding1, padding1, padding1));

		tCorporation = certificate.getCorporation ();
		tTitle = new JLabel ("Certificate for " + certificate.getCompanyName () + " (" + 
							certificate.getCompanyAbbrev () + ")");
		certificateInfoJPanel.add (tTitle);
		certificateInfoJPanel.add (Box.createVerticalGlue ());
		tNote = new JLabel (tCorporation.getNote ());
		certificateInfoJPanel.add (tNote);
		certificateInfoJPanel.add (Box.createVerticalGlue ());
		
		tPrice = new JLabel ("Price: " + Bank.formatCash (certificate.getValue ()));
		certificateInfoJPanel.add (tPrice);
		certificateInfoJPanel.add (Box.createVerticalGlue ());
		
		if (tCorporation.isAPrivateCompany ()) {
			tRevenue = new JLabel ("Revenue: " +  Bank.formatCash (tCorporation.getRevenue ()));
			certificateInfoJPanel.add (tRevenue);
			certificateInfoJPanel.add (Box.createVerticalGlue ());
		}
		add (certificateInfoJPanel);
		setSize (500, 500);
	}
}
