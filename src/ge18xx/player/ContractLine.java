package ge18xx.player;

import ge18xx.center.City;
import ge18xx.company.ShareCompany;
import geUtilities.GUI;

public class ContractLine {
	public static final ContractLine NO_CONTRACT_LINE = null;

	ShareCompany shareCompany;
	City city;
	boolean connected;
	int bond;
	
	public ContractLine (City aCity, ShareCompany aShareCompany, int aBond) {
		setCity (aCity);
		setShareCompany (aShareCompany);
		setBond (aBond);
		setConnected (false);
	}

	public boolean isValidContractLine () {
		boolean tIsValidContractLine;
		
		tIsValidContractLine = true;
		if (city == City.NO_CITY) {
			tIsValidContractLine = false;
		}
		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tIsValidContractLine = false;
		}
		if (bond <= 0) {
			tIsValidContractLine = false;
		}
		
		return tIsValidContractLine;
	}

	public String reasonInvalidContractLine () {
		String tReasonInvalidContractLine;
		
		tReasonInvalidContractLine = GUI.EMPTY_STRING;
		if (city == City.NO_CITY) {
			tReasonInvalidContractLine += "No City is specified\n";
		}
		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tReasonInvalidContractLine += "No Share Company is specified\n";
		}
		if (bond <= 0) {
			tReasonInvalidContractLine += "Bond Value is <= zero (0)\n";
		}
		
		return tReasonInvalidContractLine;
	}

	private void setConnected (boolean aConnected) {
		connected = aConnected;
	}
	
	private void setBond (int aBond) {
		bond = aBond;
	}

	private void setShareCompany (ShareCompany aShareCompany) {
		shareCompany = aShareCompany;
	}

	private void setCity (City aCity) {
		city = aCity;
	}

	public int getBond () {
		return bond;
	}

	public String getCityName () {
		String tCityName;
		
		tCityName = GUI.EMPTY_STRING;
		
		if (city != City.NO_CITY) {
			tCityName = city.getCityName ();
		}
		
		return tCityName;
	}
	
	public ShareCompany getShareCompany () {
		return shareCompany;
	}
	
	public City getCity () {
		return city;
	}
	
	public boolean isConnected () {
		return connected;
	}

	public boolean isDeltaTerrain () {
		boolean tIsDeltaTerrain;
		
		tIsDeltaTerrain = city.isDeltaTerrain ();
		
		return tIsDeltaTerrain;
	}
	
	// New Methods to add
	// SaveContractBid -- Will generate XML to add to Save Game File
	// ParseContractBid -- Will parse the XML from the Save Game File
	// GenerateActionEffects -- Will generate the Action with Effects XML of the ContractBid
	// ParseActionEffects -- Will parse the Action with Effects XML of the ContractBid

}
