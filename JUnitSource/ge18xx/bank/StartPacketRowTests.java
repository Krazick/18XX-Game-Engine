package ge18xx.bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.Certificate;
import ge18xx.company.CertificateTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

class StartPacketRowTests {
	private CompanyTestFactory companyTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;
	private CertificateTestFactory certificateTestFactory;
	private StartPacketRow packetRow1;
	private StartPacketRow packetRow2;
	private Certificate mCertificateAlpha;
	private Certificate mCertificateGamma;
	private Certificate mCertificateDelta;
	private Certificate mCertificateChi;

	@BeforeEach
	void setUp () throws Exception {
		String tPacketRow1 = "<Item row=\"1\">\n"
				+ "			<Certificate corporationId=\"1601\" discountAmount=\"0\" canBeBidOn=\"NO\">\n"
				+ "				<FreeCertificate corporationId=\"1801\" percentage=\"10\"/>\n"
				+ "			</Certificate>\n"
				+ "		</Item>";
		String tPacketRow2 = "<Item row=\"2\">\n"
				+ "			<Certificate corporationId=\"1701\" discountAmount=\"0\" canBeBidOn=\"NO\"></Certificate>\n"
				+ "			<Certificate corporationId=\"1602\" discountAmount=\"0\" canBeBidOn=\"NO\">\n"
				+ "				<FreeCertificate corporationId=\"1802\" percentage=\"20\"/>\n"
				+ "			</Certificate>\n"
				+ "			<Certificate corporationId=\"1702\" discountAmount=\"0\" canBeBidOn=\"NO\"></Certificate>\n"
				+ "		</Item>";

		companyTestFactory = new CompanyTestFactory ();
		utilitiesTestFactory = companyTestFactory.getUtilitiesTestFactory ();
		certificateTestFactory = new CertificateTestFactory ();
		packetRow1 = constructStartPacketRow (tPacketRow1);
		packetRow2 = constructStartPacketRow (tPacketRow2);
		mCertificateAlpha = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificateAlpha.getCompanyAbbrev ()).thenReturn ("ASC");
		mCertificateGamma = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificateGamma.getCompanyAbbrev ()).thenReturn ("GPC");
		mCertificateDelta = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificateGamma.getCompanyAbbrev ()).thenReturn ("DPC");
		mCertificateChi = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificateGamma.getCompanyAbbrev ()).thenReturn ("CPC");
	}

	private StartPacketRow constructStartPacketRow (String aStartPacketRowTextXML) {
		XMLNode tStartPacketRowNode;
		StartPacketRow tStartPacketRow;
		
		tStartPacketRowNode = utilitiesTestFactory.buildXMLNode (aStartPacketRowTextXML);
		tStartPacketRow = StartPacketRow.NO_START_PACKET_ROW;
		
		if (tStartPacketRowNode != XMLNode.NO_NODE) {
			tStartPacketRow = new StartPacketRow (tStartPacketRowNode);
		}

		return tStartPacketRow;
	}

	@Test
	@DisplayName ("Test the basic Parsing of the Start Packet Row")
	void basicPacketItemParsingTest () {
		assertNotNull (packetRow1);
		assertTrue (packetRow1.isOneLeftInRow ());
		assertFalse (packetRow1.isRowEmpty ());
		assertEquals (1, packetRow1.getItemCount ());
		assertFalse (packetRow2.isOneLeftInRow ());
//		assertEquals (packetRow1.getCertificateInRow (1));
	}

	@Test
	@DisplayName ("Get Certificate in Row - Invalid Index Test")
	void CertInRowBadIndexTest () {
		assertEquals (packetRow1.getCertificateInRow (-1), Certificate.NO_CERTIFICATE);
		assertEquals (packetRow1.getCertificateInRow (5), Certificate.NO_CERTIFICATE);
	}
}
