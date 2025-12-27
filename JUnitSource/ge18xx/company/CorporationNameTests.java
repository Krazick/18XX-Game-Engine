package ge18xx.company;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JLabel;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ge18xx.bank.Bank;
import ge18xx.map.MapTestFactory;

@DisplayName ("Building All Corporation Name Labels")
@TestInstance (Lifecycle.PER_CLASS)
class CorporationNameTests extends CorporationTester {
	ShareCompany alphaShareCompany;
	ShareCompany betaShareCompany;
	ShareCompany limaShareCompany;
	ShareCompany phiShareCompany;
	Corporation gammaPrivateCompany;
	MinorCompany deltaMinorCompany;
	DestinationInfo destinationInfo;
	Bank bank;
	MapTestFactory mapTestFactory;

	@Override
	@BeforeAll
	void factorySetup () {
		super.factorySetup ();
	}

	@BeforeEach
	void setUp () throws Exception {
		mapTestFactory = new MapTestFactory ();

		bank = bankTestFactory.buildBank ();
		alphaShareCompany = companyTestFactory.buildAShareCompany (1);
		betaShareCompany = companyTestFactory.buildAShareCompany (2);
		limaShareCompany = companyTestFactory.buildAShareCompany (3);
		destinationInfo = companyTestFactory.setupDestinationInfo (mapTestFactory, 
				limaShareCompany);

		gammaPrivateCompany = companyTestFactory.buildAPrivateCompany (1);
		deltaMinorCompany = companyTestFactory.buildAMinorCompany (1);
		phiShareCompany = companyTestFactory.buildAShareCompany (4);
	}

	@Test
	@DisplayName ("Building the Corporation Name Label Test")
	void CorporationNameTest () {
		JLabel tAlphaNameLabel;
		String tAlphaName;
		String tAlphaName2;
		JLabel tPhiNameLabel;
		String tPhiName;
		String tPhiName2;
		JLabel tLimaNameLabel;
		String tLimaName;
		String tLimaName2;
		
		tAlphaNameLabel = alphaShareCompany.buildCorpNameLabel ();
		tAlphaName = tAlphaNameLabel.getText ();
		assertEquals ("TPRR", tAlphaName);

		tAlphaName2 = alphaShareCompany.buildCorpNameText ();
		assertEquals ("TPRR", tAlphaName2);

		tPhiNameLabel = phiShareCompany.buildCorpNameLabel ();
		tPhiName = tPhiNameLabel.getText ();
		assertEquals ("CGR [Gov't]", tPhiName);
		
		tPhiName2 = phiShareCompany.buildCorpNameText ();
		assertEquals ("CGR [Gov't]", tPhiName2);

		
		tLimaNameLabel = limaShareCompany.buildCorpNameLabel ();
		tLimaName = tLimaNameLabel.getText ();
		assertEquals ("BBG (N17)", tLimaName);
		
		tLimaName2 = limaShareCompany.buildCorpNameText ();
		assertEquals ("BBG (N17)", tLimaName2);
		
		limaShareCompany.setReachedDestination (true);
		tLimaName2 = limaShareCompany.buildCorpNameText ();
		assertEquals ("BBG*", tLimaName2);
	}

}
