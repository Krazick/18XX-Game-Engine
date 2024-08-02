package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.player.Player;
import ge18xx.player.PortfolioHolderI;
import geUtilities.GUI;
import geUtilities.XMLNode;
import swingTweaks.KButton;

public class ExchangeBenefit extends CertificateBenefit {
	public static final String NAME = "EXCHANGE";
	public static final String EXCHANGE_PRIVATE = "Exchange Private Certificate for Share Certificate";

	public ExchangeBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		setName (NAME);
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		super.configure (aPrivateCompany, aButtonRow);
		KButton tExchangeButton;

		if (shouldConfigure ()) {
			if (!hasButton ()) {
				tExchangeButton = new KButton (getNewButtonLabel ());
				setButton (tExchangeButton);
				setButtonPanel (aButtonRow);
				tExchangeButton.setActionCommand (EXCHANGE_PRIVATE);
				tExchangeButton.addActionListener (this);
				aButtonRow.add (tExchangeButton);
			}
			updateButton ();
		} else {
			removeButton ();
		}
	}

	@Override
	public String getNewButtonLabel () {
		String tNewButtonText;
		Certificate tShareCertificate;

		tShareCertificate = getShareCertificate ();
		tNewButtonText = "Exchange " + privateCompany.getAbbrev () + " for " + certificatePercentage + "% of "
				+ tShareCertificate.getCompanyAbbrev ();

		return tNewButtonText;
	}

	@Override
	public void updateButton () {
		Player tPlayer;
		Benefit tBenefitInUse;
		String tBenefitInUseName;
		PortfolioHolderI tHolder;

		tHolder = privateCompany.getOwner ();
		if (tHolder.isAPlayer ()) {
			tPlayer = (Player) tHolder;
			tBenefitInUse = tPlayer.getBenefitInUse ();
			tBenefitInUseName = tBenefitInUse.getName ();
			if ((tBenefitInUse.isRealBenefit ()) && 
				(!NAME.equals (tBenefitInUseName))) {
				disableButton ();
				setToolTip ("Another Benefit is currently in Use.");
			} else if (! companyIsFormed ()) {
				disableButton ();
				setToolTip ("Share Company must be Formed before Exchange.");		
			} else if (! hasShareInBank ()) {
				disableButton ();
				setToolTip ("Company has no Shares in Bank for Exchange.");
			} else if (playerAtShareLimit ()) {
				disableButton ();
				setToolTip ("Player cannot exceed Corp Share Limit.");
			} else {
				enableButton ();
				setToolTip (GUI.EMPTY_STRING);
			}
		}
	}

	protected boolean playerAtShareLimit () {
		boolean tPlayerAtShareLimit;
		Player tPlayer;
		Certificate tCertificate;
		String tShareAbbrev;

		tPlayerAtShareLimit = false;
		tCertificate = getShareCertificate ();
		tShareAbbrev = tCertificate.getCompanyAbbrev ();
		tPlayer = (Player) privateCompany.getOwner ();
		tPlayerAtShareLimit = tPlayer.hasMaxShares (tShareAbbrev);

		return tPlayerAtShareLimit;
	}

	protected boolean companyIsFormed () {
		boolean tCompanyIsFormed;
		ShareCompany tShareCompany;
		Certificate tCertificate;
		
		tCertificate = getShareCertificate ();
		tShareCompany = tCertificate.getShareCompany ();
		tCompanyIsFormed = tShareCompany.isFormed ();
		
		return tCompanyIsFormed;
	}
	
	protected boolean hasShareInBank () {
		boolean tHasShareInBank;
		Certificate tCertificate;

		tHasShareInBank = false;
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
		if (EXCHANGE_PRIVATE.equals (tActionCommand)) {
			handleExchangeCertificate ();
		}
	}

	protected void handleExchangeCertificate () {
		Player tOwner;
		Certificate tPrivateCertificate;

		tOwner = (Player) privateCompany.getOwner ();
		tPrivateCertificate = privateCompany.getPresidentCertificate ();
		tOwner.exchangeCertificate (tPrivateCertificate);
		removeButton ();
	}
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		String tLabelText;
		Certificate tShareCertificate;
		
		tShareCertificate = getShareCertificate ();
		tLabelText = "Exchange for " + tShareCertificate.getPercentage () + "% of " + 
						tShareCertificate.getCompanyAbbrev ();
		if (tShareCertificate.isPresidentShare ()) {
			tLabelText += " President";
		}
		tBenefitLabel = new JLabel (tLabelText);
		setBorder (tShareCertificate, tBenefitLabel);

		return tBenefitLabel;
	}
}