package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.PrivateCompany;
import ge18xx.player.Player;
import ge18xx.player.PortfolioHolderI;
import ge18xx.utilities.XMLNode;

public class ExchangeBenefit extends CertificateBenefit {
	public final static String NAME = "EXCHANGE";
	public static final String EXCHANGE_PRIVATE = "Exchange Private Certificate for Share Certificate";

	public ExchangeBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		setName (NAME);
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		super.configure (aPrivateCompany, aButtonRow);
		JButton tExchangeButton;

		if (shouldConfigure ()) {
			if (!hasButton ()) {
				tExchangeButton = new JButton (getNewButtonLabel ());
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
			if ((tBenefitInUse.isRealBenefit ()) && (!NAME.equals (tBenefitInUseName))) {
				disableButton ();
				setToolTip ("Another Benefit is currently in Use");
			} else if (! hasShareInBank ()) {
				disableButton ();
				setToolTip ("Company has no Shares in Bank for Exchange.");
			} else if (playerAtShareLimit ()) {
				disableButton ();
				setToolTip ("Player cannot exceed Corp Share Limit.");
			} else {
				enableButton ();
				setToolTip ("");
			}
		}
	}

	protected boolean playerAtShareLimit () {
		boolean tPlayerAtShareLimit = false;
		Player tPlayer;
		Certificate tCertificate;
		String tShareAbbrev;

		tCertificate = getShareCertificate ();
		tShareAbbrev = tCertificate.getCompanyAbbrev ();
		tPlayer = (Player) privateCompany.getOwner ();
		tPlayerAtShareLimit = tPlayer.hasMaxShares (tShareAbbrev);

		return tPlayerAtShareLimit;
	}

	protected boolean hasShareInBank () {
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
}