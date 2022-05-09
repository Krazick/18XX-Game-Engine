package ge18xx.bank;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class StartPacketItem implements ParsingRoutineI {
	private static final AttributeName AN_CORPORATION_ID = new AttributeName ("corporationId");
	private static final AttributeName AN_PERCENTAGE = new AttributeName ("percentage");
	private static final AttributeName AN_DISCOUNT_AMOUNT = new AttributeName ("discountAmount");
	private static final AttributeName AN_CAN_BE_BID_ON = new AttributeName ("canBeBidOn");
	private static final ElementName EN_FREE_CERTIFICATE = new ElementName ("FreeCertificate");

	StartPacketRow startPacketRow;
	int corporationId;
	int discountAmount;
	boolean canBeBidOn;
	Certificate certificate;
	Certificate freeCertificate;
	int freeCertificateCorporationId;
	int freeCertificateCorporationPercentage;

	StartPacketItem () {
		startPacketRow = StartPacketRow.NO_START_PACKET_ROW;
		setCorporationID (Corporation.NO_ID);
		discountAmount = 0;
		canBeBidOn = false;
		setFreeCertificateCorporationId (Corporation.NO_ID);
		setFreeCertificateCorporationPercentage (0);
	}

	StartPacketItem (XMLNode aNode) {
		XMLNodeList tXMLNodeList;

		setCorporationID (aNode.getThisIntAttribute (AN_CORPORATION_ID, Corporation.NO_ID));
		discountAmount = aNode.getThisIntAttribute (AN_DISCOUNT_AMOUNT, 0);
		canBeBidOn = aNode.getThisBooleanAttribute (AN_CAN_BE_BID_ON);
		setFreeCertificateCorporationId (Corporation.NO_ID);
		setFreeCertificateCorporationPercentage (0);
		tXMLNodeList = new XMLNodeList (this);
		tXMLNodeList.parseXMLNodeList (aNode, EN_FREE_CERTIFICATE);
	}

	public JPanel buildStartPacketItemJPanel (String aSelectedButtonLabel, ItemListener aItemListener, Player aPlayer,
			GameManager aGameManager) {
		JPanel tCertificateInfoPanel;

		tCertificateInfoPanel = certificate.buildCertificateInfoJPanel (aSelectedButtonLabel, aItemListener, true,
				aPlayer, aGameManager);

		return tCertificateInfoPanel;
	}

	public boolean hasBidOnThisCert (Player aPlayer) {
		return certificate.hasBidOnThisCert (aPlayer);
	}

	public int getCorporationId () {
		return corporationId;
	}

	public int getDiscountAmount () {
		return discountAmount;
	}

	public boolean getCanBeBidOn () {
		return canBeBidOn;
	}

	public Certificate getCertificate () {
		return certificate;
	}

	public Certificate getFreeCertificate () {
		return freeCertificate;
	}

	public int getFreeCertificateCorporationId () {
		return freeCertificateCorporationId;
	}

	public int getFreeCertificateCorporationPercentage () {
		return freeCertificateCorporationPercentage;
	}

	public boolean isSelected () {
		return certificate.isSelected ();
	}

	public boolean loadWithCertificates (Portfolio aBankPortfolio, Portfolio aStartPacketPortfolio) {
		boolean tAllCertsLoaded = true;
		Corporation tCorporation, tFreeCorporation;

		// Find the Certificate for the Private/Coal/Minor Corp and transfer from the
		// Bank to the Start Packet
		// if there is a Free Certificate for a Share Corp, transfer that from the Bank
		// to the Start Packet
		tCorporation = aBankPortfolio.getCorporationForID (corporationId);
		certificate = aBankPortfolio.getPresidentCertificate (tCorporation);
		aStartPacketPortfolio.transferOneCertificateOwnership (aBankPortfolio, certificate);
		if (freeCertificateCorporationId != Corporation.NO_ID) {
			tFreeCorporation = aBankPortfolio.getCorporationForID (freeCertificateCorporationId);
			if (freeCertificateCorporationPercentage > 10) {
				freeCertificate = aBankPortfolio.getPresidentCertificate (tFreeCorporation);
			} else {
				freeCertificate = aBankPortfolio.getNonPresidentCertificate (tFreeCorporation);
			}
			aStartPacketPortfolio.transferOneCertificateOwnership (aBankPortfolio, freeCertificate);
		}

		return tAllCertsLoaded;
	}

	void printStartPacketItemInfo () {
		System.out.println ("Start Packet Item Information");
		System.out.println ("  Corporation ID [" + corporationId + "] Discount Amount [" + discountAmount + "]");
		if (certificate != Certificate.NO_CERTIFICATE) {
			System.out.print ("  ");
			certificate.printCertificateInfo ();
		}
		System.out.println ("    Can be Bid On [" + canBeBidOn + "]");
		;
		if (freeCertificateCorporationId > 0) {
			System.out.println ("      Free Certificate of [" + freeCertificateCorporationId + "] Percentage of ["
					+ freeCertificateCorporationPercentage + "]");
			if (freeCertificate != Certificate.NO_CERTIFICATE) {
				System.out.print ("    ");
				freeCertificate.printCertificateInfo ();
			}
		}

	}

	void setCorporationID (int aCorporationID) {
		corporationId = aCorporationID;
		// Will load Proper Certificate with the Load Certificate Routine
		certificate = Certificate.NO_CERTIFICATE;
	}

	void setFreeCertificateCorporationId (int aFreeCorpId) {
		freeCertificateCorporationId = aFreeCorpId;
	}

	void setFreeCertificateCorporationPercentage (int aFreeCorpPercentange) {
		freeCertificateCorporationPercentage = aFreeCorpPercentange;
		// Will load Proper Certificate with the Load Certificate Routine
		freeCertificate = Certificate.NO_CERTIFICATE;
	}

	void setStartPacketRow (StartPacketRow aStartPacketRow) {
		startPacketRow = aStartPacketRow;
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
		setFreeCertificateCorporationId (aChildNode.getThisIntAttribute (AN_CORPORATION_ID, Corporation.NO_ID));
		setFreeCertificateCorporationPercentage (aChildNode.getThisIntAttribute (AN_PERCENTAGE, 0));
	}

	public boolean enableMustBuyPrivateButton () {
		boolean tPrivateEnabled = false;

		if (certificate.getValue () == certificate.getDiscount ()) {
			certificate.setStateCheckedButton (true, "Must Buy this Private");
			tPrivateEnabled = true;
		}

		return tPrivateEnabled;
	}

	public void disableCheckedButton (String aToolTip) {
		certificate.setStateCheckedButton (false, aToolTip);
	}

	public void enableCheckedButton (String aToolTip) {
		certificate.setStateCheckedButton (true, aToolTip);
	}

	public Certificate getMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		Certificate tCertificate = Certificate.NO_CERTIFICATE;

		if (certificate.isMatchingCertificate (aAbbrev, aPercentage, aIsPresident)) {
			tCertificate = certificate;
		}

		return tCertificate;
	}
}
