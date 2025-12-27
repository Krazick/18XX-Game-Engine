package ge18xx.company;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ge18xx.bank.Bank;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;

@DisplayName ("Destination Info Tests")
@TestInstance (Lifecycle.PER_CLASS)
class DestinationInfoTests extends CorporationTester {
	MapTestFactory mapTestFactory;
	ShareCompany noDestinationShareCompany;
	ShareCompany destinationShareCompany;
	DestinationInfo noDestinationInfo;
	DestinationInfo destinationInfo;
	Bank bank;
	MapCell mMapCell;
	
	@Override
	@BeforeAll
	void factorySetup () {
		super.factorySetup ();
	}

	@BeforeEach
	void setUp () throws Exception {
		mapTestFactory = new MapTestFactory ();
		bank = bankTestFactory.buildBank ();
		noDestinationShareCompany = companyTestFactory.buildAShareCompany (1);
		noDestinationInfo = noDestinationShareCompany.getDestinationInfo ();
		destinationShareCompany = companyTestFactory.buildAShareCompany (3);
		destinationInfo = companyTestFactory.setupDestinationInfo (mapTestFactory, 
									destinationShareCompany);
		mMapCell = destinationInfo.getMapCell ();
	}

	@Test
	@DisplayName ("Get Location as Int Test")
	void getLocationIntTest () {
		int tLocationInt;
		
		tLocationInt = destinationInfo.getLocationInt ();
		assertEquals (tLocationInt, 12);
		
		tLocationInt = noDestinationInfo.getLocationInt ();
		assertEquals (tLocationInt, -1);
	}

	@Test
	@DisplayName ("Get Info Objects Tests")
	void getInfoObjectsTests () {
		assertEquals (noDestinationInfo.getCityName (), null);
		assertEquals (destinationInfo.getMapCell (),mMapCell);
		assertEquals (destinationInfo.getMapCellID (), "N17");
		assertEquals (destinationInfo.getCityName (), "Welland");
		assertEquals (destinationInfo.getLabel (), "N17");
		assertEquals (destinationInfo.getLocation ().getLocation (), 12);
	}
}
