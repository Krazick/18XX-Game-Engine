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
import ge18xx.player.CashHolderI;

class TrainPortfolioTests {
	TrainPortfolio trainPortfolio;
	TrainPortfolio emptyTrainPortfolio;
	TrainCompany trainCompany1;
	TrainCompany trainCompany2;
	CompanyTestFactory companyTestFactory;
	TrainTestFactory trainTestFactory;
	Train train1;
	Train train2;
	
	@BeforeEach
	void setUp () throws Exception {
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
		
		train1 = trainTestFactory.buildTrain (1);
		trainPortfolio.addTrain (train1);
		train2 = trainTestFactory.buildTrain (2);
		trainPortfolio.addTrain (train2); 
	}

	@Test
	@DisplayName ("Initial Setup for TrainCompany with TrainPortfolio")
	void trainPortfolioTrainCountTests () {
		assertEquals (2, trainPortfolio.getTrainCount ());
		assertEquals (0, emptyTrainPortfolio.getTrainCount ());
		
		assertTrue (emptyTrainPortfolio.hasNoTrain ());
		assertFalse (trainPortfolio.hasNoTrain ());
		
		assertTrue (trainPortfolio.hasTrains ());
		assertFalse (emptyTrainPortfolio.hasTrains ());

		assertEquals ("Train Portfolio", trainPortfolio.getName ());
		
		assertEquals (0, trainPortfolio.getSelectedCount ());
		assertEquals (0, emptyTrainPortfolio.getSelectedCount ());
		
		assertEquals (0, trainPortfolio.countTrainsOfThisOrder (2));
		assertEquals (1, trainPortfolio.countTrainsOfThisOrder (3));
		assertEquals (0, emptyTrainPortfolio.countTrainsOfThisOrder (2));
	}

	@Test
	@DisplayName ("Get PortfolioHolder Abbrev from TrainPortfolio test")
	void trainPortfolioHolderAbbrevTest () {
		assertEquals ("TTPRR", trainPortfolio.getPortfolioHolderAbbrev ());
		assertEquals ("Test Token Pennsylvania", trainPortfolio.getPortfolioHolderName ());
		
		assertEquals ("TTBNO", emptyTrainPortfolio.getPortfolioHolderAbbrev ());
		assertEquals ("Test Token Baltimore and Ohio", emptyTrainPortfolio.getPortfolioHolderName ());
	}
	
	@Test
	@DisplayName ("Get CashHolder from TrainPortfolio test") 
	void trainPortfolioCashCholderTest () {
		assertEquals (CashHolderI.NO_CASH_HOLDER, trainPortfolio.getCashHolder ());
	}
	
	@Test
	@DisplayName ("Porfolio contains types of Trains tests")
	void trainTypesTests () {
		Train tTrain;
		
		assertFalse (trainPortfolio.hasBorrowedTrain ());
		assertTrue (trainPortfolio.hasPermanentTrain ());
		
		assertFalse (emptyTrainPortfolio.hasBorrowedTrain ());
		assertFalse (emptyTrainPortfolio.hasPermanentTrain ());
		
		tTrain = trainTestFactory.buildTrain (2);
		tTrain.setBorrowed (true);
		trainPortfolio.addTrain (tTrain);
		assertTrue (trainPortfolio.hasBorrowedTrain ());
	}
	
	@Test
	@DisplayName ("TrainPortfolio has Operating Train tests") 
	void trainPortfolioHasOperatingTrainTest () {
		assertFalse (trainPortfolio.anyTrainIsOperating ());
		assertFalse (emptyTrainPortfolio.anyTrainIsOperating ());
		
		train1.setOperating (true);
		assertTrue (trainPortfolio.anyTrainIsOperating ());
	}
	
	@Test
	@DisplayName ("TrainPortfolio get Train Limit tests") 
	void trainPortfolioGetTrainLimitTest () {
		assertEquals (3, trainPortfolio.getTrainLimit ());

	}

	@Test
	@DisplayName ("TrainPortfolio Train List tests") 
	void trainPortfolioTrainListTest () {
		String tTrainList1 = "Trains (4, 5, X)";
		String tNoTrainsList = "NO TRAINS";
		String tTrainList2 = "Trains (4, 4, 5)";;
		
		assertEquals (tTrainList1, trainPortfolio.getTrainList ());
		assertEquals (tNoTrainsList, emptyTrainPortfolio.getTrainList ());
		
		trainPortfolio.addTrain (train1);
		assertEquals (tTrainList2, trainPortfolio.getTrainList ());
	}
	
	@Test
	@DisplayName ("TrainPortfolio get Cheapest Train tests") 
	void trainPortfolioCheapestTrainTest () {
		assertEquals (train1, trainPortfolio.getCheapestTrain ());
		assertEquals (train2, trainPortfolio.getCheapestPermanentTrain ());
	}

}
