package ge18xx.company;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.game.GameManager;

class PrivateCompanyTests {
	private PrivateCompany privateCompany1;
	private PrivateCompany privateCompany2;
	private CompanyTestFactory companyTestFactory;
	private CertificateTestFactory certificateTestFactory;
	private ShareCompany mShareCompany2;
	private Certificate mShareCertificate2;

	@BeforeEach
	void setUp () throws Exception {
		GameManager mGameManager;
		CorporationList mCorporationList;
		
		companyTestFactory = new CompanyTestFactory ();
		certificateTestFactory = new CertificateTestFactory ();
		
		mShareCompany2 = companyTestFactory.buildShareCompanyMock ();
		
		mShareCertificate2 = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mShareCertificate2.getPercentage ()).thenReturn (100);
		Mockito.when (mShareCertificate2.getCompanyAbbrev ()).thenReturn ("TPRR");
		Mockito.when (mShareCertificate2.getCorporation ()).thenReturn (mShareCompany2);
		
		Mockito.when (mShareCompany2.getIPOCertificate (anyInt (), anyBoolean ())).thenReturn (mShareCertificate2);
		Mockito.when (mShareCompany2.getBgColor ()).thenReturn (Color.BLUE);
		
		mGameManager = companyTestFactory.getGameManagerMock ();
		mCorporationList = companyTestFactory.getCorporationListMock ();
		Mockito.when (mCorporationList.getCorporationByID (anyInt ())).thenReturn (mShareCompany2);
		Mockito.when (mGameManager.getShareCompanies ()).thenReturn (mCorporationList);
		
		privateCompany1 = companyTestFactory.buildAPrivateCompany (1);
		privateCompany2 = companyTestFactory.buildAPrivateCompany (2);
	}

	@Test
	@DisplayName ("Basic Private Construction Test")
	void basicPrivateConstructionTest () {
		Certificate tCertificate;
		
		assertEquals ("TEST-C&SL", privateCompany1.getAbbrev ());
		assertEquals ("TEST-C&A", privateCompany2.getAbbrev ());
		tCertificate = privateCompany1.getCorporationCertificate (0);
		
		assertEquals ("TEST-C&SL", tCertificate.getCompanyAbbrev ());
	}

}
