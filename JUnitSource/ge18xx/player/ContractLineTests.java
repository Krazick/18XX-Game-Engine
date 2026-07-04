package ge18xx.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.center.CenterTestFactory;
import ge18xx.center.City;
import ge18xx.center.RevenueCenter;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.ShareCompany;
import geUtilities.GUI;

@DisplayName ("Contract Line Tests")

class ContractLineTests {
	CenterTestFactory centerTestFactory;
	CompanyTestFactory companyTestFactory;
	ShareCompany shareCompany;
	City city;
	RevenueCenter center;
	ContractLine contractLine;
	
	@BeforeEach
	void setUp () throws Exception {
		int tBond;
		
		centerTestFactory = new CenterTestFactory ();
		companyTestFactory = centerTestFactory.getCompanyTestFactory ();
		center = centerTestFactory.buildCity (3);
		if (center != RevenueCenter.NO_CENTER) {
			city = (City) center;
		}
		tBond = 50;
		shareCompany = companyTestFactory.buildAShareCompany (3);
		contractLine = new ContractLine (city, shareCompany, tBond);
	}

	@Test
	@DisplayName ("Basic Contract Line Tests")
	void basicContractLineTests () {
		City tCity;
		ShareCompany tShareCompany;
		
		assertNotNull (city);
		assertEquals ("Calcutta", city.getCityName ());
		tCity = contractLine.getCity ();
		
		assertEquals (city, tCity);
		assertEquals (50, contractLine.getBond ());
		tShareCompany = contractLine.getShareCompany ();
		assertEquals ("Buffalo, Brantford & Goderich Railway", tShareCompany.getName ());
		assertFalse (contractLine.isConnected ());
		assertEquals ("Calcutta", contractLine.getCityName ());
	}
	
	@Test
	@DisplayName ("IsValid Contract Line Tests")
	void isValidContractLineTests () {
		ContractLine tContractLine;
		
		tContractLine = new ContractLine (city, shareCompany, 0);
		assertFalse (tContractLine.isValidContractLine ());
		assertEquals ("Bond Value is <= zero (0)\n", tContractLine.reasonInvalidContractLine ());

		tContractLine = new ContractLine (city, ShareCompany.NO_SHARE_COMPANY, 20);
		assertFalse (tContractLine.isValidContractLine ());
		assertEquals ("No Share Company is specified\n", tContractLine.reasonInvalidContractLine ());
		
		tContractLine = new ContractLine (City.NO_CITY, shareCompany, 30);
		assertFalse (tContractLine.isValidContractLine ());
		assertEquals ("No City is specified\n", tContractLine.reasonInvalidContractLine ());
		
		assertEquals (GUI.EMPTY_STRING, tContractLine.getCityName ());
		
		tContractLine = new ContractLine (city, ShareCompany.NO_SHARE_COMPANY, 0);
		assertFalse (tContractLine.isValidContractLine ());
		assertEquals ("No Share Company is specified\nBond Value is <= zero (0)\n", tContractLine.reasonInvalidContractLine ());
		
		tContractLine = new ContractLine (City.NO_CITY, shareCompany, 0);
		assertFalse (tContractLine.isValidContractLine ());
		assertEquals ("No City is specified\nBond Value is <= zero (0)\n", tContractLine.reasonInvalidContractLine ());
		
		tContractLine = new ContractLine (City.NO_CITY, ShareCompany.NO_SHARE_COMPANY, 0);
		assertFalse (tContractLine.isValidContractLine ());
		assertEquals ("No City is specified\nNo Share Company is specified\nBond Value is <= zero (0)\n", tContractLine.reasonInvalidContractLine ());
	}
}
