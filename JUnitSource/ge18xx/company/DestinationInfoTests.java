package ge18xx.company;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.game.GameTestFactory;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;

class DestinationInfoTests {
	GameTestFactory gameTestFactory;
	BankTestFactory bankTestFactory;
	CompanyTestFactory companyTestFactory;
	MapTestFactory mapTestFactory;
	ShareCompany noDestinationShareCompany;
	ShareCompany destinationShareCompany;
	DestinationInfo noDestinationInfo;
	DestinationInfo destinationInfo;
	Bank bank;
	MapCell mMapCell;
	
	@BeforeEach
	void setUp () throws Exception {
		String tMapCellID;
		String tCityName;
		
		gameTestFactory = new GameTestFactory ();
		bankTestFactory = new BankTestFactory ();
		mapTestFactory = new MapTestFactory ();
		bank = bankTestFactory.buildBank ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		noDestinationShareCompany = companyTestFactory.buildAShareCompany (1);
		noDestinationInfo = noDestinationShareCompany.getDestinationInfo ();
		destinationShareCompany = companyTestFactory.buildAShareCompany (3);
		destinationInfo = destinationShareCompany.getDestinationInfo ();
		
		tMapCellID = "N17";
		tCityName = "Welland";
		mMapCell = mapTestFactory.buildMapCellMock (tMapCellID);
		Mockito.when (mMapCell.getCellID ()).thenReturn (tMapCellID);
		Mockito.when (mMapCell.getCityName ()).thenReturn (tCityName);
		destinationInfo.setMapCell (mMapCell);
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
