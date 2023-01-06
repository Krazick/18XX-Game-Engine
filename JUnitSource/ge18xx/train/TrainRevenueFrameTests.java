package ge18xx.train;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.TokenCompany;

class TrainRevenueFrameTests {
	TrainRevenueFrame trainRevenueFrame;
	TokenCompany mTokenCompany;
	CompanyTestFactory companyTestFactory;
	
	@BeforeEach
	void setUp () throws Exception {
		int tMockCoID1;

		tMockCoID1 = 5001;
		companyTestFactory = new CompanyTestFactory ();
		mTokenCompany = companyTestFactory.buildTokenCompanyMock (tMockCoID1, "MC1");
		trainRevenueFrame = new TrainRevenueFrame (mTokenCompany, "Mocked Train Company for Tests");
		trainRevenueFrame.setThisRevenue (40);
	}

	@Test
	@DisplayName ("Calculate Revenue Contributions")
	void calculateRevenueContributionTests () {
		int tTreasury;
		
		assertEquals (0, trainRevenueFrame.calculateRevenueContribution (40, 50));
		assertEquals (0, trainRevenueFrame.calculateRevenueContribution (40, 40));
		
		for (tTreasury = 30; tTreasury < 39; tTreasury++) {
			assertEquals (10, trainRevenueFrame.calculateRevenueContribution (40, tTreasury));
		}
		for (tTreasury = 20; tTreasury < 29; tTreasury++) {
			assertEquals (20, trainRevenueFrame.calculateRevenueContribution (40, tTreasury));
		}
		for (tTreasury = 10; tTreasury < 19; tTreasury++) {
			assertEquals (30, trainRevenueFrame.calculateRevenueContribution (40, tTreasury));
		}
		for (tTreasury = 0; tTreasury < 9; tTreasury++) {
			assertEquals (40, trainRevenueFrame.calculateRevenueContribution (40, tTreasury));
		}
	}

}
