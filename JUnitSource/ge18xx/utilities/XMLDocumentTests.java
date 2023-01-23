package ge18xx.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

@DisplayName ("XMLDocument Tests")
class XMLDocumentTests {
	XMLDocument document;
	XMLDocument nullDocument;
	UtilitiesTestFactory utilityTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		utilityTestFactory = new UtilitiesTestFactory ();
		document = new XMLDocument ();
		nullDocument = new XMLDocument (XMLDocument.NO_DOCUMENT);
	}

	private XMLDocument buildPrivateCompanyTestDocument () {
		XMLDocument tPrivateDocument;
		String tPrivateCompany1TestXML = "	<Private id=\"802\" name=\"TEST-Champlain &amp; St. Lawrence\" abbrev=\"TEST-C&amp;SL\" cost=\"40\" \n"
				+ "		revenue=\"10\" homeCell1=\"B20\" homeLocation1=\"7\" homeLocation2=\"12\" \n"
				+ "		note=\"A Corporation owning the C&amp;SL may lay a tile onC&amp;SL's hex even if this hex is not connected to the Corporation's Railhead. This free tile placement is in addition to the Corporation's tile placement — For this turn only the Corporation may play two tiles. The tile played on the C&amp;SL hex does not have to connect to any existing adjacent track.\"\n"
				+ "		special=\"Free Tile Placement\">\n" + "		<Benefits>\n"
				+ "			<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>\n"
				+ "		</Benefits>\n" + "		<Certificate director=\"YES\" percentage=\"100\"\n"
				+ "			allowedOwners=\"IPO,Player,Share\" />\n" + "	</Private>\n" + "";

		tPrivateDocument = utilityTestFactory.buildXMLDocument (tPrivateCompany1TestXML);

		return tPrivateDocument;
	}

	@Test
	@DisplayName ("Constructor Tests")
	void basicConstructorTests () {
		XMLDocument tPrivateDocument;

		assertTrue (document.validDocument ());
		assertFalse (document.hasChildNodes ());

		assertFalse (nullDocument.validDocument ());
		assertFalse (nullDocument.hasChildNodes ());

		tPrivateDocument = buildPrivateCompanyTestDocument ();
		assertTrue (tPrivateDocument.validDocument ());
		assertTrue (tPrivateDocument.hasChildNodes ());
	}

	@Test
	@DisplayName ("Create Element Tests")
	void createElementTests () {
		XMLElement tXMLElement;
		XMLElement tNoXMLElement;
		XMLElement tBadXMLElement;
		ElementName tElementName;
		ElementName tNoElementName;
		ElementName tBadElementName;

		tElementName = new ElementName ("TestElementName");
		tBadElementName = new ElementName ("testElementName");
		tXMLElement = document.createElement (tElementName);
		assertNotNull (tXMLElement);
		tNoElementName = ElementName.NO_ELEMENT_NAME;
		tNoXMLElement = document.createElement (tNoElementName);
		assertNull (tNoXMLElement);

		tBadXMLElement = document.createElement (tBadElementName);
		assertNull (tBadXMLElement);

		assertFalse (document.hasChildNodes ());
		document.appendChild (tNoXMLElement);
		assertFalse (document.hasChildNodes ());

		document.appendChild (tBadXMLElement);
		assertFalse (document.hasChildNodes ());
	}

	@Test
	@DisplayName ("Clear Element Tests")
	void clearElementTests () {
		XMLElement tXMLElement;
		ElementName tElementName;

		assertFalse (document.hasChildNodes ());
		assertFalse (nullDocument.hasChildNodes ());

		tElementName = new ElementName ("TestElementName");
		tXMLElement = document.createElement (tElementName);
		document.appendChild (tXMLElement);
		assertTrue (document.hasChildNodes ());

		document.clearDocumentChildren ();
		assertFalse (document.hasChildNodes ());

		assertFalse (nullDocument.validDocument ());
		assertFalse (nullDocument.hasChildNodes ());
		nullDocument.appendChild (tXMLElement);
		assertFalse (nullDocument.validDocument ());
		assertFalse (nullDocument.hasChildNodes ());

		nullDocument.clearDocumentChildren ();
		assertFalse (nullDocument.hasChildNodes ());
	}

	@Test
	@DisplayName ("Get Document Tests")
	void getDocumentTests () {
		XMLDocument tPrivateDocument;
		Document tDocument1;
		Document tDocument2;
		Document tDocument3;

		tPrivateDocument = buildPrivateCompanyTestDocument ();
		tDocument1 = tPrivateDocument.getDocument ();
		assertNotNull (tDocument1);

		tDocument2 = nullDocument.getDocument ();
		assertNull (tDocument2);

		tDocument3 = document.getDocument ();
		assertNotNull (tDocument3);
	}

	@Test
	@DisplayName ("Get Document Element Tests")
	void getDocumentElementTests () {
		XMLDocument tPrivateDocument;
		XMLNode tPrivateNode;
		XMLNode tNode1;
		XMLNode tNode2;

		tPrivateDocument = buildPrivateCompanyTestDocument ();
		tPrivateNode = tPrivateDocument.getDocumentNode ();
		assertNotNull (tPrivateNode);

		tNode1 = document.getDocumentNode ();
		assertNotNull (tNode1);

		tNode2 = nullDocument.getDocumentNode ();
		assertNull (tNode2);
	}

	@Test
	@DisplayName ("Get Document ToString Tests")
	void documentToStringTests () {
		XMLDocument tPrivateDocument;
		String tResult;
		String tExpected;
		
		tExpected = "<Private abbrev=\"TEST-C&amp;SL\" cost=\"40\" homeCell1=\"B20\" homeLocation1=\"7\" homeLocation2=\"12\" id=\"802\" name=\"TEST-Champlain &amp; St. Lawrence\" note=\"A Corporation owning the C&amp;SL may lay a tile onC&amp;SL's hex even if this hex is not connected to the Corporation's Railhead. This free tile placement is in addition to the Corporation's tile placement — For this turn only the Corporation may play two tiles. The tile played on the C&amp;SL hex does not have to connect to any existing adjacent track.\" revenue=\"10\" special=\"Free Tile Placement\">\n"
				+ "		<Benefits>\n"
				+ "			<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" cost=\"0\" extra=\"true\" mapCell=\"B20\" passive=\"false\"/>\n"
				+ "		</Benefits>\n"
				+ "		<Certificate allowedOwners=\"IPO,Player,Share\" director=\"YES\" percentage=\"100\"/>\n"
				+ "	</Private>\n";
		tPrivateDocument = buildPrivateCompanyTestDocument ();
		tResult = tPrivateDocument.toString ();
		assertEquals (tExpected, tResult);
	}
}
