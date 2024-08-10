package ge18xx.bank;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

public class StartPacketItem implements ParsingRoutineI {
	public static final ElementName EN_FREE_CERTIFICATE = new ElementName ("FreeCertificate");
	public static final AttributeName AN_CORPORATION_ID = new AttributeName ("corporationId");
	public static final AttributeName AN_PERCENTAGE = new AttributeName ("percentage");
	public static final AttributeName AN_AVAILABLE = new AttributeName ("available");
	public static final AttributeName AN_CAN_BE_BID_ON = new AttributeName ("canBeBidOn");
	public static final AttributeName AN_DISCOUNT_AMOUNT = new AttributeName ("discountAmount");
	public static final StartPacketItem NO_START_PACKET_ITEM = null;
	StartPacketRow startPacketRow;
	Certificate certificate;
	Certificate freeCertificate;
	boolean canBeBidOn;
	boolean available;
	int corporationId;
	int discountAmount;
	int freeCertificateCorporationId;
	int freeCertificateCorporationPercentage;

	public StartPacketItem (XMLNode aNode) {
		XMLNodeList tXMLNodeList;
		int tCorporationID;
		boolean tAvailable;
		
		tCorporationID = aNode.getThisIntAttribute (AN_CORPORATION_ID, Corporation.NO_ID);
		setCorporationID (tCorporationID);
		discountAmount = aNode.getThisIntAttribute (AN_DISCOUNT_AMOUNT, 0);
		canBeBidOn = aNode.getThisBooleanAttribute (AN_CAN_BE_BID_ON);
		tAvailable = aNode.getThisBooleanAttribute (AN_AVAILABLE);
		setFreeCertificateCorporationId (Corporation.NO_ID);
		setFreeCertificateCorporationPercentage (0);
		tXMLNodeList = new XMLNodeList (this);
		tXMLNodeList.parseXMLNodeList (aNode, EN_FREE_CERTIFICATE);
		setAvailable (tAvailable);
	}

	public JPanel buildStartPacketItemJPanel (String aSelectedButtonLabel, ItemListener aItemListener, Player aPlayer,
			GameManager aGameManager) {
		JPanel tCertificateInfoPanel;

		if (available) {
			tCertificateInfoPanel = certificate.buildCertificateInfoJPanel (aSelectedButtonLabel, aItemListener, true,
					aPlayer, aGameManager);
			certificate.addBenefitLabels (tCertificateInfoPanel, false);
		} else {
			tCertificateInfoPanel = GUI.NO_PANEL;
		}
		
		return tCertificateInfoPanel;
	}

	public void setAvailable (boolean aAvailable) {
		available = aAvailable;
	}
	
	public boolean available () {
		return available;
	}
	
	public void disableCheckedButton (String aToolTip) {
		certificate.setStateCheckedButton (false, aToolTip);
	}

	public void enableCheckedButton (String aToolTip) {
		certificate.setStateCheckedButton (true, aToolTip);
	}
	
	public Certificate getMustBuyCertificate () {
		Certificate tMustBuyCertificate = Certificate.NO_CERTIFICATE;

		if (certificate.valueEqualsDiscount ()) {
			tMustBuyCertificate = certificate;
		}
		
		return tMustBuyCertificate;
	}

	public boolean enableMustBuyPrivateButton () {
		boolean tPrivateEnabled = false;

		if (certificate.valueEqualsDiscount ()) {
			certificate.setStateCheckedButton (true, "Must Buy this Private");
			tPrivateEnabled = true;
		}

		return tPrivateEnabled;
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
		setFreeCertificateCorporationId (aChildNode.getThisIntAttribute (AN_CORPORATION_ID, Corporation.NO_ID));
		setFreeCertificateCorporationPercentage (aChildNode.getThisIntAttribute (AN_PERCENTAGE, 0));
	}

	public boolean getCanBeBidOn () {
		return canBeBidOn;
	}

	public Certificate getCertificate () {
		return certificate;
	}

	public int getCorporationId () {
		return corporationId;
	}

	public String getCorporationAbbrev () {
		return certificate.getCompanyAbbrev ();
	}
	
	public int getDiscountAmount () {
		return discountAmount;
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

	public Certificate getMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		Certificate tCertificate = Certificate.NO_CERTIFICATE;

		if (certificate.isMatchingCertificate (aAbbrev, aPercentage, aIsPresident)) {
			tCertificate = certificate;
		}

		return tCertificate;
	}

	public boolean hasBidOnThisCert (Player aPlayer) {
		return certificate.hasBidOnThisCert (aPlayer);
	}

	public boolean isSelected () {
		return certificate.isSelected ();
	}

	public boolean loadWithCertificates (Portfolio aBankPortfolio, Portfolio aStartPacketPortfolio) {
		boolean tAllCertsLoaded = true;
		Corporation tCorporation, tFreeCorporation;

		// Find the Certificate for the Private/Minor Corp and transfer from the
		// Bank to the Start Packet
		// if there is a Free Certificate for a Share Corp, transfer that from the Bank
		// to the Start Packet
		tCorporation = aBankPortfolio.getCorporationForID (corporationId);
		setCertificate (aBankPortfolio.getPresidentCertificate (tCorporation));
		aStartPacketPortfolio.transferOneCertificateOwnership (aBankPortfolio, certificate);
		if (freeCertificateCorporationId != Corporation.NO_ID) {
			tFreeCorporation = aBankPortfolio.getCorporationForID (freeCertificateCorporationId);
			if (freeCertificateCorporationPercentage > 10) {
				setFreeCertificate (aBankPortfolio.getPresidentCertificate (tFreeCorporation));
			} else {
				setFreeCertificate (aBankPortfolio.getNonPresidentCertificate (tFreeCorporation));
			}
			aStartPacketPortfolio.transferOneCertificateOwnership (aBankPortfolio, freeCertificate);
		}

		return tAllCertsLoaded;
	}

	public void getEffectElements (XMLDocument aXMLDocument, XMLElement aXMLElement) {
		
		aXMLElement.setAttribute (AN_CORPORATION_ID, corporationId);
		aXMLElement.setAttribute (AN_DISCOUNT_AMOUNT, discountAmount);
		aXMLElement.setAttribute (AN_CAN_BE_BID_ON, canBeBidOn);
	}
	
	void printStartPacketItemInfo () {
		System.out.println ("Start Packet Item Information");	// PRINTLOG method
		System.out.println ("  Corporation ID [" + corporationId + "] Discount Amount [" + discountAmount + "]");	
		if (certificate != Certificate.NO_CERTIFICATE) {
			System.out.print ("  ");
			certificate.printCertificateInfo ();
		}
		System.out.println ("    Can be Bid On [" + canBeBidOn + "]");

		if (freeCertificateCorporationId > 0) {
			System.out.println ("      Free Certificate of [" + freeCertificateCorporationId + "] Percentage of ["
					+ freeCertificateCorporationPercentage + "]");
			if (freeCertificate != Certificate.NO_CERTIFICATE) {
				System.out.print ("    ");
				freeCertificate.printCertificateInfo ();
			}
		}
	}

	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}

	void setCorporationID (int aCorporationID) {
		corporationId = aCorporationID;
		// Will load Proper Certificate with the Load Certificate Routine
		setCertificate (Certificate.NO_CERTIFICATE);
	}

	public void setFreeCertificate (Certificate aFreeCertificate) {
		freeCertificate = aFreeCertificate;
	}

	void setFreeCertificateCorporationId (int aFreeCorpId) {
		freeCertificateCorporationId = aFreeCorpId;
	}

	void setFreeCertificateCorporationPercentage (int aFreeCorpPercentange) {
		freeCertificateCorporationPercentage = aFreeCorpPercentange;
		// Will load Proper Certificate with the Load Certificate Routine
		setFreeCertificate (Certificate.NO_CERTIFICATE);
	}

	void setStartPacketRow (StartPacketRow aStartPacketRow) {
		startPacketRow = aStartPacketRow;
	}

	public boolean containsCertificate (Certificate aCertificate) {
		boolean tContainsCertificate;
		
		if (certificate == aCertificate) {
			tContainsCertificate = true;
		} else {
			tContainsCertificate = false;
		}
		
		return tContainsCertificate;
	}
}
