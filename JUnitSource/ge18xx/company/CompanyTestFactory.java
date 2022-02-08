package ge18xx.company;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

public class CompanyTestFactory {
	GameTestFactory gameTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;
	
	public CompanyTestFactory () {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
	}
	
	public CompanyTestFactory (GameTestFactory aGameTestFactory) {
		gameTestFactory = aGameTestFactory;
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
	}
	
	public UtilitiesTestFactory getUtilitiesTestFactory () {
		return utilitiesTestFactory;
	}
	
	public PrivateCompany buildAPrivateCompany (int tCompanyIndex) {
		String tPrivateCompany1TestXML =
				"	<Private id=\"802\" name=\"TEST-Champlain &amp; St. Lawrence\" abbrev=\"TEST-C&amp;SL\" cost=\"40\" \n"
				+ "		revenue=\"10\" homeCell1=\"B20\" homeLocation1=\"7\" homeLocation2=\"12\" \n"
				+ "		note=\"A Corporation owning the C&amp;SL may lay a tile onC&amp;SL's hex even if this hex is not connected to the Corporation's Railhead. This free tile placement is in addition to the Corporation's tile placement — For this turn only the Corporation may play two tiles. The tile played on the C&amp;SL hex does not have to connect to any existing adjacent track.\"\n"
				+ "		special=\"Free Tile Placement\">\n"
				+ "		<Benefits>\n"
				+ "			<Benefit actorType=\"Share Company\" class=\"ge18xx.company.benefit.TilePlacementBenefit\" extra=\"true\" mapCell=\"B20\" cost=\"0\" passive=\"false\"/>\n"
				+ "		</Benefits>\n"
				+ "		<Certificate director=\"YES\" percentage=\"100\"\n"
				+ "			allowedOwners=\"IPO,Player,Share\" />\n"
				+ "	</Private>\n"
				+ "";
		PrivateCompany tPrivateCompany = PrivateCompany.NO_PRIVATE_COMPANY;
		CorporationList mCorporationList = Mockito.mock (CorporationList.class);
		GameManager mGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);

		if (tCompanyIndex == 1) {
			tPrivateCompany = constructPrivateCompany (tPrivateCompany1TestXML, tPrivateCompany, mCorporationList);
		}
		
		return tPrivateCompany;
	}
	
	private PrivateCompany constructPrivateCompany (String aPrivateCompanyTextXML, PrivateCompany aPrivateCompany, 
			CorporationList mCorporationList) {
		XMLNode tPrivateCompanyNode;
		
		tPrivateCompanyNode = utilitiesTestFactory.constructXMLNode (aPrivateCompanyTextXML);
		if (tPrivateCompanyNode != XMLNode.NO_NODE) {
			aPrivateCompany = new PrivateCompany (tPrivateCompanyNode, mCorporationList);
			aPrivateCompany.setTestingFlag (true);			
		}
		
		return aPrivateCompany;
	}

	
	public ShareCompany buildAShareCompany (int tCompanyIndex) {
		String tShareCompany1TestXML =
				"<Share id=\"901\" name=\"TestPennsylvania\" abbrev=\"TPRR\" homeCell1=\"H12\" \n" +
				"	homeLocation1=\"14\" bgColor=\"Dark Green\" fgColor=\"White\" tokens=\"4\"> \n" +
				"	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n" +
				"	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n" +
				"		allowedOwners=\"IPO,Player,BankPool\" /> \n" +
				"</Share>";
		String tShareCompany2TestXML =
				"<Share id=\"902\" name=\"Test Baltimore and Ohio\" abbrev=\"TBNO\" homeCell1=\"I15\" \n" +
				"	homeLocation1=\"21\" bgColor=\"Deep Blue\" fgColor=\"White\" tokens=\"3\"> \n" +
				"	<Certificate director=\"YES\" percentage=\"20\" allowedOwners=\"IPO,Player\" /> \n" +
				"	<Certificate director=\"NO\" percentage=\"10\" quantity=\"8\" \n" +
				"		allowedOwners=\"IPO,Player,BankPool\" /> \n" +
				"</Share>";

		ShareCompany tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		CorporationList mCorporationList = Mockito.mock (CorporationList.class);
		GameManager mGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (mCorporationList.getGameManager ()).thenReturn (mGameManager);

		if (tCompanyIndex == 1) {
			tShareCompany = constructShareCompany (tShareCompany1TestXML, tShareCompany, mCorporationList);
		} else if (tCompanyIndex == 2) {
			tShareCompany = constructShareCompany (tShareCompany2TestXML, tShareCompany, mCorporationList);
		} else {
			tShareCompany = ShareCompany.NO_SHARE_COMPANY;
		}
		
		return tShareCompany;
	}

	private ShareCompany constructShareCompany (String aShareCompanyTestXML, ShareCompany aShareCompany,
			CorporationList mCorporationList) {
		XMLNode tShareCompanyNode;
		
		tShareCompanyNode = utilitiesTestFactory.constructXMLNode (aShareCompanyTestXML);
		if (tShareCompanyNode != XMLNode.NO_NODE) {
			aShareCompany = new ShareCompany (tShareCompanyNode, mCorporationList);
			aShareCompany.setTestingFlag (true);			
		}
		
		return aShareCompany;
	}

}
