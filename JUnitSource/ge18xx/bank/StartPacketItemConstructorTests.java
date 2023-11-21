package ge18xx.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

//import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.Certificate;
import ge18xx.company.CertificateTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerTestFactory;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

@DisplayName ("Start Packet Item Constructor Tests")
class StartPacketItemConstructorTests {
	private CompanyTestFactory companyTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;
	private CertificateTestFactory certificateTestFactory;
	private StartPacketItem packetItem1;
	private StartPacketItem packetItem2;
	private Certificate mCertificateGamma;
	private Certificate mCertificateAlpha;

	@BeforeEach
	void setUp () throws Exception {
		String tPacketItem1 = "<Certificate corporationId=\"802\" discountAmount=\"5\" canBeBidOn=\"NO\"></Certificate>";
		String tPacketItem2 = "<Certificate corporationId=\"803\" discountAmount=\"0\" canBeBidOn=\"YES\">" +
				"<FreeCertificate corporationId=\"1101\" percentage=\"10\"/>" +
				"</Certificate>";

		companyTestFactory = new CompanyTestFactory ();
		utilitiesTestFactory = companyTestFactory.getUtilitiesTestFactory ();
		certificateTestFactory = new CertificateTestFactory ();
		packetItem1 = constructStartPacketItem (tPacketItem1);
		packetItem2 = constructStartPacketItem (tPacketItem2);
		mCertificateGamma = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificateGamma.getCompanyAbbrev ()).thenReturn ("GPC");
		mCertificateAlpha = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificateAlpha.getCompanyAbbrev ()).thenReturn ("ASC");
	}

	private StartPacketItem constructStartPacketItem (String aStartPacketTextXML) {
		XMLNode tStartPacketItemNode;
		StartPacketItem tStartPacketItem;

		tStartPacketItemNode = utilitiesTestFactory.buildXMLNode (aStartPacketTextXML);
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
		Certificate tCertificate;
		Certificate tFreeCertificate;

		assertEquals (packetItem1.getCertificate (), Certificate.NO_CERTIFICATE);
		assertEquals (packetItem2.getFreeCertificate (), Certificate.NO_CERTIFICATE);

		packetItem1.setCertificate (mCertificateGamma);
		packetItem2.setFreeCertificate (mCertificateAlpha);
		tCertificate = packetItem1.getCertificate ();
		tFreeCertificate = packetItem2.getFreeCertificate ();

		assertEquals ("GPC", tCertificate.getCompanyAbbrev ());
		assertEquals ("ASC", tFreeCertificate.getCompanyAbbrev ());

	}

	@Nested
	@DisplayName ("Using Mocked Certificates")
	class UseMockedCertificates {

		@Test
		@DisplayName ("Test isSelected Method")
		void isSelectedWithMockedCertificateTests () {
			Mockito.when (mCertificateGamma.isSelected ()).thenReturn (true);
			packetItem1.setCertificate (mCertificateGamma);

			assertTrue (packetItem1.isSelected ());

			Mockito.when (mCertificateAlpha.isSelected ()).thenReturn (false);
			packetItem2.setCertificate (mCertificateAlpha);
			assertFalse (packetItem2.isSelected ());
		}

		@Test
		@DisplayName ("Test hasBidOnThisCert Method - TRUE")
		void hasBidWithMockedCertificateTrueTest () {
			Player mPlayer;

			mPlayer = prepareMockedPlayer ();

			Mockito.when (mCertificateGamma.hasBidOnThisCert (any (Player.class))).thenReturn (true);
			packetItem1.setCertificate (mCertificateGamma);

			assertTrue (packetItem1.hasBidOnThisCert (mPlayer));
		}

		public Player prepareMockedPlayer () {
			GameTestFactory tGameTestFactory;
			GameManager tGameManager;
			PlayerTestFactory tPlayerTestFactory;
			Player mPlayer;
			
			tGameTestFactory = new GameTestFactory ();
			tGameManager = tGameTestFactory.buildGameManager ();
			tPlayerTestFactory = new PlayerTestFactory (tGameManager);
			mPlayer = tPlayerTestFactory.buildPlayerMock ("SPIPlayer");
			
			return mPlayer;
		}
		
		@Test
		@DisplayName ("Test hasBidOnThisCert Method - FALSE")
		void hasBidWithMockedCertificateFalseTest () {
			Player mPlayer;

			mPlayer = prepareMockedPlayer ();

			Mockito.when (mCertificateAlpha.hasBidOnThisCert (any (Player.class))).thenReturn (false);
			packetItem2.setCertificate (mCertificateAlpha);

			assertFalse (packetItem2.hasBidOnThisCert (mPlayer));
		}

		@Test
		@DisplayName ("Test getMatchingCertificate Method")
		void getMatchingCertWithMockedCertificateTest () {

			Mockito.when (mCertificateGamma.isMatchingCertificate (anyString (), anyInt (), anyBoolean ())).thenReturn (true);
			packetItem1.setCertificate (mCertificateGamma);
			assertEquals (mCertificateGamma, packetItem1.getMatchingCertificate ("GPC", 100, true));

			Mockito.when (mCertificateAlpha.isMatchingCertificate (anyString (), anyInt (), anyBoolean ())).thenReturn (false);
			packetItem1.setCertificate (mCertificateAlpha);
			assertEquals (Certificate.NO_CERTIFICATE, packetItem1.getMatchingCertificate ("APC", 10, false));
		}
	}
}
