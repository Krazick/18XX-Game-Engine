package ge18xx.train;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.TokenCompany;
import ge18xx.company.TrainCompany;

class TrainPortfolioTests {
	TrainPortfolio trainPortfolio;
	TrainPortfolio emptyTrainPortfolio;
	TrainCompany trainCompany1;
	TrainCompany trainCompany2;
	CompanyTestFactory companyTestFactory;
	TrainTestFactory trainTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		Train tTrain;
		TokenCompany tTokenCompany1;
		TokenCompany tTokenCompany2;
		
		companyTestFactory = new CompanyTestFactory ();
		trainTestFactory = new TrainTestFactory ();
		
		tTokenCompany1 = companyTestFactory.buildATokenCompany (1);
		trainCompany1 = (TrainCompany) tTokenCompany1;
		trainPortfolio = trainTestFactory.buildTrainPortfolio (trainCompany1);

		tTokenCompany2 = companyTestFactory.buildATokenCompany (2);
		trainCompany2 = (TrainCompany) tTokenCompany2;
		emptyTrainPortfolio = trainTestFactory.buildTrainPortfolio (trainCompany2);
		
		tTrain = trainTestFactory.buildTrain (1);
		trainPortfolio.addTrain (tTrain);
		tTrain = trainTestFactory.buildTrain (2);
		trainPortfolio.addTrain (tTrain);
		
	}

	@Test
	@DisplayName ("Initial Setup for TrainCompany with TrainPortfolio")
	void trainPortfolioTrainCountTest () {
		assertEquals (2, trainPortfolio.getTrainCount ());
		assertEquals (0, emptyTrainPortfolio.getTrainCount ());
		
		assertTrue (emptyTrainPortfolio.hasNoTrain ());
		assertFalse (trainPortfolio.hasNoTrain ());
		
		assertTrue (trainPortfolio.hasTrains ());
		assertFalse (emptyTrainPortfolio.hasTrains ());

	}

	@Test
	@DisplayName ("Verify Holder Abbrev TrainPortfolio")
	void trainPortfolioHolderAbbrevTest () {
		assertEquals ("TTPRR", trainPortfolio.getPortfolioHolderAbbrev ());
		assertEquals ("Test Token Pennsylvania", trainPortfolio.getPortfolioHolderName ());
		
		assertEquals ("TTBNO", emptyTrainPortfolio.getPortfolioHolderAbbrev ());
		assertEquals ("Test Token Baltimore and Ohio", emptyTrainPortfolio.getPortfolioHolderName ());
	}
	
}
