package ge18xx.company;

import java.awt.Point;
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
		fillFrame ();
	}
	
	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction;
		Point tLocation;
		
		tTheAction = aActionEvent.getActionCommand ();
		if (GET_INFO.equals (tTheAction)) {
			tLocation = getOffset ();
			setLocation (tLocation);
			setVisible (true);
		}
	}

	private Point getOffset () {
		Point tOffsetLocation;
		CertificateHolderI tOwner;
		
		tOwner = certificate.getOwner ();
		if (tOwner.isACorporation ()) {
			tOffsetLocation = gameManager.getOffsetCorporationFrame ();
		} else {
			tOffsetLocation = gameManager.getOffsetPlayerFrame ();
		}
		
		return tOffsetLocation;
	}
	
	public void fillFrame () {
		Corporation tCorporation;
		String tCompanyInfo;
		JLabel tTitle;
		JLabel tNote;
		JLabel tPrice;
		JLabel tRevenue;
		
		certificateInfoJPanel = new JPanel ();
		certificateInfoJPanel.setLayout (new BoxLayout (certificateInfoJPanel, BoxLayout.Y_AXIS));
		certificateInfoJPanel.setBorder (BorderFactory.createEmptyBorder (padding1, padding1, padding1, padding1));

		tCorporation = certificate.getCorporation ();
		tCompanyInfo = tCorporation.getCompanyInfo ();
		tTitle = new JLabel ("Certificate for " + tCompanyInfo);
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
