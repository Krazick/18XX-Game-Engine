package ge18xx.company;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import ge18xx.center.Revenue;
import ge18xx.company.benefit.Benefits;
import ge18xx.game.GameManager;
import geUtilities.xml.GameEngineManager;
import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;
import geUtilities.GUI;

public class CertificateInfoFrame extends XMLFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final CertificateInfoFrame NO_CERTIFICATE_INFO_FRAME = null;
	public static final String GET_INFO = "GET INFO";
	public static final String OK_STRING = "OK";
	JPanel certificateInfoJPanel;
	Certificate certificate;
	int padding1;

	public CertificateInfoFrame (Certificate aCertificate, GameEngineManager aGameManager) {
		super ("CERT INFO", aGameManager);
		String tInfoTitle;
		Corporation tCorporation;
		
		padding1 = 10;
		setCertificate (aCertificate);
		tCorporation = certificate.getCorporation ();
		tInfoTitle = "Info for " + tCorporation.getAbbrev () + " Certificate";
		setTitle (tInfoTitle);
		fillFrame ();
	}

	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}
	
	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction;
		Point tLocation;
		
		tTheAction = aActionEvent.getActionCommand ();
		if (GET_INFO.equals (tTheAction)) {
			tLocation = getOffset ();
			setLocation (tLocation);
			showFrame ();
		}
		if (OK_STRING.equals (tTheAction)) {
			hideFrame ();
		}
	}

	private Point getOffset () {
		Point tOffsetLocation;
		CertificateHolderI tOwner;
		GameManager tGameManager;
		
		tOwner = certificate.getOwner ();
		tGameManager = (GameManager) gameEngineManager;
		if (tOwner.isACorporation ()) {
			tOffsetLocation = tGameManager.getOffsetCorporationFrame ();
		} else {
			tOffsetLocation = tGameManager.getOffsetPlayerFrame ();
		}
		
		return tOffsetLocation;
	}
	
	public void fillFrame () {
		Corporation tCorporation;
		String tCompanyType;
		String tCompanyInfo;
		String tPercentPrezInfo;
		String tHomeLocations;
		String tNote;
		JLabel tTitle;
		JLabel tHomeLocationsJLabel;
		JLabel tNoteJLabel;
		JLabel tPrice;
		JLabel tRevenue;
		KButton tOKButton;
		Border tMargin;
		
		tMargin = BorderFactory.createEmptyBorder (padding1, padding1, padding1, padding1);
		certificateInfoJPanel = new JPanel ();
		certificateInfoJPanel.setLayout (new BoxLayout (certificateInfoJPanel, BoxLayout.Y_AXIS));
		certificateInfoJPanel.setBorder (tMargin);

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
		
		updateWithShareCompanyInfo (tCorporation);
		tPrice = new JLabel ("Price: " + Bank.formatCash (certificate.getValue ()));
		certificateInfoJPanel.add (tPrice);
		certificateInfoJPanel.add (Box.createVerticalStrut (10));
		
		tRevenue = new JLabel (Revenue.LABEL + tCorporation.getFormattedThisRevenue ());
		certificateInfoJPanel.add (tRevenue);
		certificateInfoJPanel.add (Box.createVerticalStrut (10));
		
		addBenefitLabels (tCorporation, tCompanyType);
		certificateInfoJPanel.add (Box.createVerticalStrut (10));
		
		tOKButton = new KButton (OK_STRING);
		tOKButton.setActionCommand (OK_STRING);
		tOKButton.addActionListener (this);
		certificateInfoJPanel.add (tOKButton);
		
		add (certificateInfoJPanel);
		pack ();
		setPreferredSize (getPreferredSize ());
	}

	public void addBenefitLabels (Corporation tCorporation, String tCompanyType) {
		Benefits tBenefits;
		JLabel tBenefitJLabel;
		
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
	}

	public void updateWithShareCompanyInfo (Corporation tCorporation) {
		ShareCompany tShareCompany;
		String tDestinationReached;
		JLabel tDestination;
		JLabel tDestinationReachedLabel;
		JLabel tParPriceJLabel;
		JLabel tEscrow;
		
		if (tCorporation.isAShareCompany ()) {
			tShareCompany = (ShareCompany) tCorporation;
			if (tShareCompany.hasDestination ()) {
				tDestination = new JLabel ("Destination MapCell ID:  " + 
						tShareCompany.getDestinationLabel ());
				certificateInfoJPanel.add (tDestination);
				certificateInfoJPanel.add (Box.createVerticalStrut (10));
				tDestinationReached = "Destination ";
				if (tShareCompany.hasReachedDestination ()) {
					tDestinationReached += "has been Reached.";
				} else {
					tDestinationReached += "has not been Reached";
				}
				tDestinationReachedLabel = new JLabel (tDestinationReached);
				certificateInfoJPanel.add (tDestinationReachedLabel);
				certificateInfoJPanel.add (Box.createVerticalStrut (10));
				if (! tShareCompany.hasReachedDestination ()) {
					tEscrow  = new JLabel ("Escrow held: " + 
							Bank.formatCash (tShareCompany.calculateEscrowWithheld ()));
					certificateInfoJPanel.add (tEscrow);
					certificateInfoJPanel.add (Box.createVerticalStrut (10));
				}
			}

			tParPriceJLabel = new JLabel ("Par Price: " + tShareCompany.getFormattedParPrice ());
			certificateInfoJPanel.add (tParPriceJLabel);
			certificateInfoJPanel.add (Box.createVerticalStrut (10));
		}
	}
}
