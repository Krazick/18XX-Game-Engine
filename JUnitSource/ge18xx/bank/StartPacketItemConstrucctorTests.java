package ge18xx.bank;

//import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.Certificate;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import ge18xx.company.CompanyTestFactory;
//import ge18xx.company.CorporationList;
//import ge18xx.company.PrivateCompany;
//import ge18xx.company.ShareCompany;
//import ge18xx.utilities.UtilitiesTestFactory;
//import ge18xx.utilities.XMLNode;

class StartPacketItemConstrucctorTests {
	private ShareCompany alphaShareCompany;
	private PrivateCompany gammaPrivateCompany;
	private CompanyTestFactory companyTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;
	private StartPacketItem packetItem1;
	private StartPacketItem packetItem2;
	
	@BeforeEach
	void setUp () throws Exception {
		String tPacketItem1 = "<Certificate corporationId=\"802\" discountAmount=\"5\" canBeBidOn=\"NO\"></Certificate>";
		String tPacketItem2 = "<Certificate corporationId=\"803\" discountAmount=\"0\" canBeBidOn=\"YES\">" +
				"<FreeCertificate corporationId=\"1101\" percentage=\"10\"/>" + 
				"</Certificate>";
		
		companyTestFactory = new CompanyTestFactory ();
		utilitiesTestFactory = companyTestFactory.getUtilitiesTestFactory ();
		packetItem1 = constructStartPacketItem (tPacketItem1);
		packetItem2 = constructStartPacketItem (tPacketItem2);
		alphaShareCompany = companyTestFactory.buildAShareCompany (1);
		gammaPrivateCompany = companyTestFactory.buildAPrivateCompany (1);
	}
	
	private StartPacketItem constructStartPacketItem (String aStartPacketTextXML) {
		XMLNode tStartPacketItemNode;
		StartPacketItem tStartPacketItem;
		
		tStartPacketItemNode = utilitiesTestFactory.constructXMLNode (aStartPacketTextXML);
		tStartPacketItem = StartPacketItem.NO_START_PACKET_ITEM;
		
		if (tStartPacketItemNode != XMLNode.NO_NODE) {
			tStartPacketItem = new StartPacketItem (tStartPacketItemNode);
		}

		return tStartPacketItem;
	}

	@Test
	@DisplayName ("Test the basic Parsing of the Start Packet Item")
	void basicPacketItemParsingTest () {
		assertNotNull (packetItem1);
		assertEquals (802, packetItem1.getCorporationId ());
		assertEquals (5, packetItem1.getDiscountAmount ());
		assertFalse (packetItem1.getCanBeBidOn ());
		
		assertEquals (803, packetItem2.getCorporationId ());
		assertEquals (0, packetItem2.getDiscountAmount ());
		assertTrue (packetItem2.getCanBeBidOn ());
		assertEquals (1101, packetItem2.getFreeCertificateCorporationId ());
		assertEquals (10, packetItem2.getFreeCertificateCorporationPercentage ());
	}

	@Test
	@DisplayName ("Test fetching Certificates")
	void fetchingCertificatesFromPacketTests () {
		assertEquals (packetItem1.getCertificate (), Certificate.NO_CERTIFICATE);
		assertEquals (packetItem2.getFreeCertificate (), Certificate.NO_CERTIFICATE);
	}
}
