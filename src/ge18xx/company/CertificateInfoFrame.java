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
import ge18xx.company.benefit.Benefits;
import ge18xx.game.GameManager;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

public class CertificateInfoFrame extends XMLFrame implements ActionListener {
	public static CertificateInfoFrame NO_CERTIFICATE_INFO_FRAME = null;
	public static final String GET_INFO = "GET INFO";
	private static final long serialVersionUID = 1L;
	private Certificate certificate;
	private JPanel certificateInfoJPanel;
	int padding1;

	public CertificateInfoFrame (Certificate aCertificate, GameManager aGameManager) {
		super ();
		String tInfoTitle;
		Corporation tCorporation;
		
		padding1 = 10;
		setCertificate (aCertificate);
		tCorporation = certificate.getCorporation ();
		tInfoTitle = "Info for " + tCorporation.getAbbrev () + " Certificate";
		setGameManager (tInfoTitle, aGameManager);
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
		Benefits tBenefits;

		Corporation tCorporation;
		ShareCompany tShareCompany;
		String tCompanyType;
		String tCompanyInfo;
		String tPercentPrezInfo;
		String tHomeLocations;
		String tNote;
		JLabel tTitle;
		JLabel tHomeLocationsJLabel;
		JLabel tDestination;
		JLabel tNoteJLabel;
		JLabel tPrice;
		JLabel tParPriceJLabel;
		JLabel tRevenue;
		JLabel tBenefitJLabel;
		
		certificateInfoJPanel = new JPanel ();
		certificateInfoJPanel.setLayout (new BoxLayout (certificateInfoJPanel, BoxLayout.Y_AXIS));
		certificateInfoJPanel.setBorder (BorderFactory.createEmptyBorder (padding1, padding1, padding1, padding1));

		tCorporation = certificate.getCorporation ();
		tCompanyType = tCorporation.getType ();
		tCompanyInfo = tCorporation.getCompanyInfo ();
		tPercentPrezInfo = certificate.getPercentPrezInfo ();
		tTitle = new JLabel (tPercentPrezInfo + " Certificate for " + tCompanyInfo);
		certificateInfoJPanel.add (tTitle);
		certificateInfoJPanel.add (Box.createVerticalStrut (10));
		
		tHomeLocations = tCorporation.getHomeLocations ();
		tHomeLocationsJLabel = new JLabel (tHomeLocations);
		certificateInfoJPanel.add (tHomeLocationsJLabel);
		
		certificateInfoJPanel.add (Box.createVerticalStrut (10));
		tNote = tCorporation.getNote ();
		if (tNote != GUI.EMPTY_STRING) {
			tNoteJLabel = new JLabel (tNote);
			certificateInfoJPanel.add (tNoteJLabel);
			certificateInfoJPanel.add (Box.createVerticalStrut (10));
		}
		
		if (tCorporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) tCorporation;
			if (tShareCompany.hasDestination ()) {
				tDestination = new JLabel ("Destination MapCell ID:  " + tShareCompany.getDestinationLabel ());
				certificateInfoJPanel.add (tDestination);
				certificateInfoJPanel.add (Box.createVerticalStrut (10));		
			}
			tParPriceJLabel = new JLabel ("Par Price: " + tShareCompany.getFormattedParPrice ());
			certificateInfoJPanel.add (tParPriceJLabel);
			certificateInfoJPanel.add (Box.createVerticalStrut (10));		
		}
		tPrice = new JLabel ("Price: " + Bank.formatCash (certificate.getValue ()));
		certificateInfoJPanel.add (tPrice);
		certificateInfoJPanel.add (Box.createVerticalStrut (10));
		
		tRevenue = new JLabel ("Revenue: " +  Bank.formatCash (tCorporation.getRevenue ()));
		certificateInfoJPanel.add (tRevenue);
		certificateInfoJPanel.add (Box.createVerticalStrut (10));
		
		
		if (! tCorporation.isAShareCompany ()) {
			tBenefits = tCorporation.getBenefits ();
			
			if ((tCorporation.isAPrivateCompany ())  && (tBenefits == Benefits.NO_BENEFITS)) {
				tBenefitJLabel = new JLabel ("Benefits: NO BENEFITS");
				certificateInfoJPanel.add (tBenefitJLabel);
			} else if ((tCorporation.isAPrivateCompany ()) && (tBenefits != Benefits.NO_BENEFITS)) {
				tBenefitJLabel = new JLabel ("Benefits for this "+ tCompanyType + " Corporation:");
				certificateInfoJPanel.add (tBenefitJLabel);
				certificate.addBenefitLabels (certificateInfoJPanel, true);
			} else {
				tBenefitJLabel = new JLabel ("Benefits for this "+ tCompanyType + " Corporation:");
				certificateInfoJPanel.add (tBenefitJLabel);
				certificate.addBenefitLabels (certificateInfoJPanel, true);
			}
		}
		add (certificateInfoJPanel);
		pack ();
		setPreferredSize (getPreferredSize ());
	}
}
