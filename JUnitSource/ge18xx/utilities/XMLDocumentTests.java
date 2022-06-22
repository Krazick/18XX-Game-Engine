package ge18xx.utilities;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName ("XMLDocument Tests")
class XMLDocumentTests {
	XMLDocument document;
	
	@BeforeEach
	void setUp () throws Exception {
		document = new XMLDocument ();
	}

	@Test
	@DisplayName ("Constructor Tests")
	void basicConstructorTests () {
		XMLDocument tNullDocument;
		XMLDocument tPrivateDocument;
		String tPrivateCompany1TestXML = "	<Private id=\"802\" name=\"TEST-Champlain &amp; St. Lawrence\" abbrev=\"TEST-C&amp;SL\" cost=\"40\" \n"
				+ "		revenue=\"10\" homeCell1=\"B20\" homeLocation1=\"7\" homeLocation2=\"12\" \n"
				+ "		note=\"A Corporation owning the C&amp;SL may lay a tile onC&amp;SL's hex even if this hex is not connected to the Corporation's Railhead. This free tile placement is in addition to the Corporation's tile placement — For this turn only the Corporation may play two tiles. The tile played on the C&amp;SL hex does not have to connect to any existing adjacent track.\"\n"
				+ "		special=\"Free Tile Placement\">\n" + "		<Benefits>\n"
				+ "			<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>\n"
				+ "		</Benefits>\n" + "		<Certificate director=\"YES\" percentage=\"100\"\n"
				+ "			allowedOwners=\"IPO,Player,Share\" />\n" + "	</Private>\n" + "";

		assertTrue (document.validDocument ());
		assertFalse (document.hasChildNodes ());
		
		tNullDocument = new XMLDocument (XMLDocument.NO_DOCUMENT);
		assertFalse (tNullDocument.validDocument ());
		assertFalse (tNullDocument.hasChildNodes ());
		
		tPrivateDocument = new XMLDocument ();
		tPrivateDocument.ParseXMLString (tPrivateCompany1TestXML);
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
	}
}
