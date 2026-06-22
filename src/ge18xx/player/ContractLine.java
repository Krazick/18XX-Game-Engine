package ge18xx.player;

import ge18xx.center.City;
import ge18xx.company.ShareCompany;

public class ContractLine {
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
	
	public ShareCompany getShareCompany () {
		return shareCompany;
	}
	
	public City getCity () {
		return city;
	}
	
	public boolean isConnected () {
		return connected;
	}
}
