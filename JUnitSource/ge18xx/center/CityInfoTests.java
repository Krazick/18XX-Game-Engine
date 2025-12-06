package ge18xx.center;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.Corporation;
import ge18xx.map.MapCell;
import ge18xx.map.MapTestFactory;

@DisplayName ("City Info Tests")
class CityInfoTests {
	CenterTestFactory centerTestFactory;
	CompanyTestFactory companyTestFactory;
	MapTestFactory mapTestFactory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception {
		centerTestFactory = new CenterTestFactory ();
		companyTestFactory = centerTestFactory.getCompanyTestFactory ();
		mapTestFactory = centerTestFactory.getMapTestFactory ();
	}

	@Test
	@DisplayName ("City Info Construction and Clone Tests")
	void cityInfoConstructionTests () {
		CityInfo tCityInfo1;
		CityInfo tCityInfo2;
		MapCell mMapCell;
		Corporation mCorporation;
		
		tCityInfo1 = centerTestFactory.buildCityInfo (1);
		
		assertEquals ("City ID 7, Type 3, Name [Baltimore] on Map Cell NOT YET\n"
				+ "No Corporation Base\n", tCityInfo1.getFullCityInfo ());
		
		mCorporation = companyTestFactory.buildShareCompanyMock ();
		mMapCell = mapTestFactory.buildMapCellMock ("B12");
		Mockito.when (mMapCell.getCorporation ("MSC")).thenReturn (mCorporation);
		
		tCityInfo2 = centerTestFactory.buildCityInfo (2);
		tCityInfo2.setCorporation (mCorporation);
		tCityInfo2.setMapCell (mMapCell);
		assertEquals ("City ID 3, Type 3, Name [Chicago] on Map Cell B12\n"
				+ "Base for MSC Corporation", tCityInfo2.getFullCityInfo ());
	}

}
