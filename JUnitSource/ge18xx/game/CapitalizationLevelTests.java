package ge18xx.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import geUtilities.XMLNode;
import geUtilities.utilites.UtilitiesTestFactory;

class CapitalizationLevelTests {
	Capitalization capitalization1830;
	Capitalization capitalization1856;
	private GameTestFactory gameTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		String tCapitalization1830XML = "<Capitalizations>\n"
				+ "			<Capitalization availableTrain=\"2\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"3\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"4\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"5\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"6\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"Diesel\" level=\"FULL\" />\n"
				+ "		</Capitalizations>\n";
		String tCapitalization1856XML = "		<Capitalizations>\n"
				+ "			<Capitalization availableTrain=\"2\" level=\"Incremental_5\" />\n"
				+ "			<Capitalization availableTrain=\"3\" level=\"Incremental_5\" />\n"
				+ "			<Capitalization availableTrain=\"4\" level=\"Incremental_5\" />\n"
				+ "			<Capitalization availableTrain=\"5\" level=\"Incremental_10\" />\n"
				+ "			<Capitalization availableTrain=\"6\" level=\"FULL\" />\n"
				+ "			<Capitalization availableTrain=\"Diesel\" level=\"FULL\" />\n"
				+ "		</Capitalizations>";
				

		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
		capitalization1830 = buildCapitalization (tCapitalization1830XML);
		capitalization1856 = buildCapitalization (tCapitalization1856XML);
	}
	
	private Capitalization buildCapitalization (String aCapitalizationXML) {
		XMLNode tCapitalizationNode;
		Capitalization tCapitalization;
		
		tCapitalizationNode = utilitiesTestFactory.buildXMLNode (aCapitalizationXML);
		if (tCapitalizationNode != XMLNode.NO_NODE) {
			tCapitalization = new Capitalization (tCapitalizationNode);
		} else {
			tCapitalization = Capitalization.NO_CAPITALIZATION;
		}

		return tCapitalization;
	}

	@Test
	@DisplayName ("Capitalization Level Test")
	
	void capitalizationLevelTests () {
		String tResult1830 = "Capitalization Info for 1830:\n"
				+ "{2=FULL, 3=FULL, 4=FULL, 5=FULL, 6=FULL, Diesel=FULL}";
				
		String tResult1856 = "Capitalization Info for 1856:\n"
				+ "{2=Incremental_5, 3=Incremental_5, 4=Incremental_5, 5=Incremental_10, 6=FULL, Diesel=FULL}";
		
		assertEquals (tResult1830, capitalization1830.getInfo ("1830"));
		assertEquals (tResult1856, capitalization1856.getInfo ("1856"));
	}

	@Test
	@DisplayName ("Certificate Capitalization 1830 Tests")
	
	void CertificateCapitalization1830Tests () {
		int tSharesSold;
		String tNextTrainName;
		int tCapLevel;
		
		tSharesSold = 2;
		tNextTrainName = "2";
		tCapLevel = capitalization1830.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);

		tSharesSold = 4;
		tNextTrainName = "2";
		tCapLevel = capitalization1830.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);
		
		tSharesSold = 4;
		tNextTrainName = "3";
		tCapLevel = capitalization1830.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);
		
		tSharesSold = 4;
		tNextTrainName = "4";
		tCapLevel = capitalization1830.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);
		
		tSharesSold = 4;
		tNextTrainName = "5";
		tCapLevel = capitalization1830.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);
		
		tSharesSold = 4;
		tNextTrainName = "6";
		tCapLevel = capitalization1830.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);

		tSharesSold = 8;
		tNextTrainName = "Diesel";
		tCapLevel = capitalization1830.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);
	}
	
	@Test
	@DisplayName ("Certificate Capitalization 1856 Tests")
	
	void CertificateCapitalization1856Tests () {
		int tSharesSold;
		String tNextTrainName;
		int tCapLevel;
		
		tSharesSold = 2;
		tNextTrainName = "2";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (2, tCapLevel);

		tSharesSold = 4;
		tNextTrainName = "2";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (4, tCapLevel);

		tSharesSold = 6;
		tNextTrainName = "2";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (5, tCapLevel);
		
		tSharesSold = 4;
		tNextTrainName = "3";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (4, tCapLevel);
		
		tSharesSold = 4;
		tNextTrainName = "4";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (4, tCapLevel);
		
		tSharesSold = 4;
		tNextTrainName = "5";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (4, tCapLevel);
		
		tSharesSold = 6;
		tNextTrainName = "5";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (6, tCapLevel);
	
		tSharesSold = 4;
		tNextTrainName = "6";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);

		tSharesSold = 8;
		tNextTrainName = "Diesel";
		tCapLevel = capitalization1856.getCapitalizationLevel (tSharesSold, tNextTrainName);
		assertEquals (10, tCapLevel);
	}

}
