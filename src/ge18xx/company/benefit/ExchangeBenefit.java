package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.PrivateCompany;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;
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
		JButton tExchangeButton;
		
		if (shouldConfigure ()) {
			if (! hasButton ()) {
				tExchangeButton = new JButton (getNewButtonLabel ());
				setButton (tExchangeButton);
				setButtonPanel (aButtonRow);
				tExchangeButton.setActionCommand (PlayerFrame.EXCHANGE_PRIVATE);
				tExchangeButton.addActionListener (this);
				aButtonRow.add (tExchangeButton);
			}
		}
		updateButton ();
	}
	
	@Override
	public String getNewButtonLabel () {
		String tNewButtonText;
		Certificate tShareCertificate;
		
		tShareCertificate = getShareCertificate ();
		tNewButtonText = "Exchange " + privateCompany.getAbbrev () + " for " + certificatePercentage + 
				"% of " + tShareCertificate.getCompanyAbbrev ();
		
		return tNewButtonText;
	}

	@Override
	public void updateButton () {
		Player tOwner;
		Benefit tBenefitInUse;
		String tBenefitInUseName;
		
		tOwner = (Player) privateCompany.getOwner ();
		tBenefitInUse = tOwner.getBenefitInUse ();
		tBenefitInUseName = tBenefitInUse.getName ();
		if ((tBenefitInUse.realBenefit ()) && (! NAME.equals (tBenefitInUseName))) {
			disableButton ();
			setToolTip ("Another Benefit is currently in Use");
		} else if (! hasShareInBank ()) {
			disableButton ();
			setToolTip ("Company has no Shares in Bank for Exchange.");
		}
	}

	private boolean hasShareInBank () {
		boolean tHasShareInBank = false;
		Certificate tCertificate;
		
		tCertificate = getShareCertificate ();
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tHasShareInBank = true;
		}
		
		return tHasShareInBank;
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (PlayerFrame.EXCHANGE_PRIVATE.equals (tActionCommand)) {
			handleExchangeCertificate  ();
		}
	}

	private void handleExchangeCertificate () {
		Player tOwner;
		Certificate tPrivateCertificate;
		
		tOwner = (Player) privateCompany.getOwner ();
		tPrivateCertificate = privateCompany.getPresidentCertificate ();
		System.out.println ("Handling Exchange of Certificate for " + tPrivateCertificate.getCompanyAbbrev ());
		tOwner.exchangeCertificate (tPrivateCertificate);
		removeButton ();
	}
}