package ge18xx.utilities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class XMLNodeTests {
	UtilitiesTestFactory utilityTestFactory;
	XMLNode xmlNode;
	
	@BeforeEach
	void setUp () throws Exception {
		String tPrivateCompany1TestXML = "	<Private id=\"802\" name=\"TEST-Champlain &amp; St. Lawrence\" abbrev=\"TEST-C&amp;SL\" cost=\"40\" \n"
				+ "		revenue=\"10\" homeCell1=\"B20\" homeLocation1=\"7\" homeLocation2=\"12\" \n"
				+ "		note=\"A Corporation owning the C&amp;SL may lay a tile onC&amp;SL's hex even if this hex is not connected to the Corporation's Railhead. This free tile placement is in addition to the Corporation's tile placement — For this turn only the Corporation may play two tiles. The tile played on the C&amp;SL hex does not have to connect to any existing adjacent track.\"\n"
				+ "		special=\"Free Tile Placement\">\n" + "		<Benefits>\n"
				+ "			<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>\n"
				+ "		</Benefits>\n" + "		<Certificate director=\"YES\" percentage=\"100\"\n"
				+ "			allowedOwners=\"IPO,Player,Share\" />\n" + "	</Private>\n" + "";
		
		utilityTestFactory = new UtilitiesTestFactory ();
		xmlNode = utilityTestFactory.buildXMLNode (tPrivateCompany1TestXML);
	}

	@Test
	@DisplayName ("Test finding a Node")
	void findChildNodeTest () {
		XMLNode tChildNode;
		ElementName tElementName;
		
		tElementName = new ElementName ("Benefits");
		tChildNode = xmlNode.getNode (tElementName);
		assertNotNull (tChildNode);
	}

}
