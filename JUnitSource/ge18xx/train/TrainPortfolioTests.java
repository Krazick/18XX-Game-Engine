package ge18xx.train;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.TokenCompany;
import ge18xx.company.TrainCompany;
import ge18xx.player.CashHolderI;
import geUtilities.GUI;

class TrainPortfolioTests {
	TrainPortfolio trainPortfolio;
	TrainPortfolio emptyTrainPortfolio;
	TrainPortfolio noTrainsPortfolio;
	TrainPortfolio trainPortfolioWithMockedTrains;
	TrainCompany trainCompany1;
	TrainCompany trainCompany2;
	TrainCompany trainCompany3;
	CompanyTestFactory companyTestFactory;
	TrainTestFactory trainTestFactory;
	Train train1;
	Train train2;
	Train mTrain1;
	Train mTrain2;
	
	@BeforeEach
	void setUp () throws Exception {
		TokenCompany tTokenCompany1;
		TokenCompany tTokenCompany2;
		TokenCompany tTokenCompany3;
		
		companyTestFactory = new CompanyTestFactory ();
		trainTestFactory = new TrainTestFactory ();
		
		tTokenCompany1 = companyTestFactory.buildATokenCompany (1);
		trainCompany1 = (TrainCompany) tTokenCompany1;
		trainPortfolio = trainTestFactory.buildTrainPortfolio (trainCompany1);

		tTokenCompany2 = companyTestFactory.buildATokenCompany (2);
		trainCompany2 = (TrainCompany) tTokenCompany2;
		emptyTrainPortfolio = trainTestFactory.buildTrainPortfolio (trainCompany2);
		
		noTrainsPortfolio = new TrainPortfolio ();
		noTrainsPortfolio.setTrains (TrainPortfolio.NO_TRAINS);
		
		train1 = trainTestFactory.buildTrain (1);
		trainPortfolio.addTrain (train1);
		train2 = trainTestFactory.buildTrain (2);
		trainPortfolio.addTrain (train2); 
		
		tTokenCompany3 = companyTestFactory.buildATokenCompany (2);
		trainPortfolioWithMockedTrains  = trainTestFactory.buildTrainPortfolio (tTokenCompany3);
		trainCompany3 = (TrainCompany) tTokenCompany3;
		mTrain1 = trainTestFactory.buildTrainMock ();
		trainPortfolioWithMockedTrains.addTrain (mTrain1);
		mTrain2 = trainTestFactory.buildTrainMock ();
		trainPortfolioWithMockedTrains.addTrain (mTrain2);
	}
	
	@Test
	@DisplayName ("No Trains Portfolio Count tests")
	void noTrainPortfolioTrainCountTests () {
		assertTrue (noTrainsPortfolio.isEmpty ());
		assertTrue (noTrainsPortfolio.hasNoTrain ());
		assertFalse (noTrainsPortfolio.hasTrains ());
		assertEquals (0, noTrainsPortfolio.getTrainCount ());
	}
	
	@Test
	@DisplayName ("Portfolio Count tests")
	void trainPortfolioTrainCountTests () {
		assertEquals (2, trainPortfolio.getTrainCount ());
		assertEquals (0, emptyTrainPortfolio.getTrainCount ());
		
		assertTrue (emptyTrainPortfolio.hasNoTrain ());
		assertFalse (trainPortfolio.hasNoTrain ());
		
		assertTrue (emptyTrainPortfolio.isEmpty ());
		assertFalse (trainPortfolio.isEmpty ());
		
		assertTrue (trainPortfolio.hasTrains ());
		assertFalse (emptyTrainPortfolio.hasTrains ());

		assertEquals ("Train Portfolio", trainPortfolio.getName ());
		
		assertEquals (0, trainPortfolio.getSelectedCount ());
		assertEquals (0, emptyTrainPortfolio.getSelectedCount ());
		
		assertEquals (0, trainPortfolio.countTrainsOfThisOrder (2));
		assertEquals (1, trainPortfolio.countTrainsOfThisOrder (3));
		assertEquals (0, emptyTrainPortfolio.countTrainsOfThisOrder (2));
		
		assertEquals (1, trainPortfolio.getTrainCount ("4"));
		assertEquals (0, trainPortfolio.getTrainCount ("Diesel"));
		assertEquals (0, emptyTrainPortfolio.getTrainCount ("4"));
	}
	
	@Test
	@DisplayName ("Train Portfolio support method tests")
	void trainPortfolioSupportMethodTests () {
		String tResult;
		String tTrainName;
		int tTrainCount;
		
		tTrainName = train1.getName ();
		tTrainCount = trainPortfolio.getTrainCount ();
		
		tResult = trainPortfolio.getTrainAndCount (tTrainName, tTrainCount);
		assertEquals ("4 (2)", tResult);
		
		tTrainName = "Diesel";
		tTrainCount = TrainInfo.UNLIMITED_TRAINS;
		tResult = trainPortfolio.getTrainAndCount (tTrainName, tTrainCount);
		assertEquals ("Diesel (unlimited)", tResult);
	
	}

	@Test
	@DisplayName ("Get PortfolioHolder Abbrev from TrainPortfolio test")
	void trainPortfolioHolderAbbrevTest () {
		assertEquals ("TTPRR", trainPortfolio.getPortfolioHolderAbbrev ());
		assertEquals ("Test Token Pennsylvania", trainPortfolio.getPortfolioHolderName ());
		
		assertEquals ("TTBNO", emptyTrainPortfolio.getPortfolioHolderAbbrev ());
		assertEquals ("Test Token Baltimore and Ohio", emptyTrainPortfolio.getPortfolioHolderName ());
		
		assertEquals ("NONE", noTrainsPortfolio.getPortfolioHolderAbbrev ());
		assertEquals ("NONE", noTrainsPortfolio.getPortfolioHolderName ());
		
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
		Train tPermaTrain;
		
		assertEquals (train1, trainPortfolio.getCheapestTrain ());
		assertEquals (train2, trainPortfolio.getCheapestPermanentTrain ());
		tPermaTrain = trainTestFactory.buildTrain (3);
		trainPortfolio.addTrain (tPermaTrain);
		assertEquals (tPermaTrain, trainPortfolio.getCheapestPermanentTrain ());
	}
	
	@Test
	@DisplayName ("Get Train by Name Tests")
	void trainPortfolioGetTrainbyName () {
		Train tDieselTrain;
		
		assertEquals (train2, trainPortfolio.getTrain ("5"));
		assertEquals (train1, trainPortfolio.getTrain ("4"));
		assertNull (trainPortfolio.getTrain ("Diesel"));
		tDieselTrain = trainTestFactory.buildTrain (4);
		trainPortfolio.addTrain (tDieselTrain);
		assertEquals (tDieselTrain, trainPortfolio.getTrain ("Diesel"));
		assertNull (emptyTrainPortfolio.getTrain ("4"));
	}

	@Test
	@DisplayName ("Get Train of Order Tests")
	void trainPortfolioGetTrainOfOrder () {
		Train tDieselTrain;
		
		assertEquals (train2, trainPortfolio.getTrainOfOrder (4));
		assertEquals (train1, trainPortfolio.getTrainOfOrder (3));
		assertNull (trainPortfolio.getTrainOfOrder (6));
		tDieselTrain = trainTestFactory.buildTrain (4);
		trainPortfolio.addTrain (tDieselTrain);
		assertEquals (tDieselTrain, trainPortfolio.getTrainOfOrder (6));
		assertNull (emptyTrainPortfolio.getTrainOfOrder (4));
	}
	
	@Test
	@DisplayName ("Clear Current Route count Tests")
	void clearCurrentRouteTests () {
		verify (mTrain1, times (0)).clearCurrentRoute ();
		verify (mTrain2, times (0)).clearCurrentRoute ();
		
		trainPortfolioWithMockedTrains.clearCurrentRoutes ();
		verify (mTrain1, times (1)).clearCurrentRoute ();
		verify (mTrain2, times (1)).clearCurrentRoute ();
		trainPortfolioWithMockedTrains.clearCurrentRoutes ();
		verify (mTrain1, times (2)).clearCurrentRoute ();
		verify (mTrain2, times (2)).clearCurrentRoute ();
	}
	
	@Test
	@DisplayName ("Clear Selections count Tests")
	void clearSelectionsTests () {
		verify (mTrain1, times (0)).clearSelection ();
		verify (mTrain2, times (0)).clearSelection ();
		
		trainPortfolioWithMockedTrains.clearSelections ();
		verify (mTrain1, times (1)).clearSelection ();
		verify (mTrain2, times (1)).clearSelection ();
	}
	
	@Test
	@DisplayName ("Update Compact Portfolio Test 1")
	void updateCompactPortfolioTests () {
		JPanel tTrainCertJPanel;
		JComponent tPanelComponent;
		JLabel tLabel;
		int tTrainQuantity;
		int tTrainIndex;
		int tNewTrainIndex;
		String tTrainName;
		String tLabelText;
		Train tTrain;
		
		tTrainCertJPanel = new JPanel ();
		tTrainIndex = 1;
		tTrainQuantity = trainPortfolio.getTrainCount ();
		tTrain = trainPortfolio.getTrain ("4");
		tTrainName = tTrain.getName ();
		
		trainPortfolio.updateForCompactPortfolio (tTrainCertJPanel, tTrainQuantity, tTrainName);
		
		quantityLabelTest (tTrainCertJPanel, 0, "Quantity: 2");
		
		trainPortfolio.addTrain (tTrain);
		tTrainQuantity = trainPortfolio.getTrainCount ();
		tNewTrainIndex = trainPortfolio.updateTrainIndex (tTrainIndex, tTrainQuantity);
		assertEquals (3, tNewTrainIndex);
		
		trainPortfolio.updateForCompactPortfolio (tTrainCertJPanel, tTrainQuantity, tTrainName);
		quantityLabelTest (tTrainCertJPanel, 1, "Quantity: 3");

		tNewTrainIndex = trainPortfolio.updateTrainIndex (tNewTrainIndex, tTrainQuantity);
		
		assertEquals (5, tNewTrainIndex);
		
		trainPortfolio.removeTrain ("5");
		tTrainQuantity = trainPortfolio.getTrainCount ();
		tNewTrainIndex = trainPortfolio.updateTrainIndex (tTrainIndex, tTrainQuantity);
		assertEquals (2, tNewTrainIndex);
		
		trainPortfolio.updateForCompactPortfolio (tTrainCertJPanel, tTrainQuantity, tTrainName);
		quantityLabelTest (tTrainCertJPanel, 2, "Quantity: 2");

		tNewTrainIndex = trainPortfolio.updateTrainIndex (tNewTrainIndex, tTrainQuantity);
		
		assertEquals (3, tNewTrainIndex);

		trainPortfolio.removeTrain ("4");
		tTrainQuantity = trainPortfolio.getTrainCount ();
		tNewTrainIndex = trainPortfolio.updateTrainIndex (tTrainIndex, tTrainQuantity);
		assertEquals (1, tNewTrainIndex);
		
		trainPortfolio.updateForCompactPortfolio (tTrainCertJPanel, tTrainQuantity, tTrainName);
		quantityLabelTest (tTrainCertJPanel, 3, "LAST 4 Train");

		tNewTrainIndex = trainPortfolio.updateTrainIndex (tNewTrainIndex, tTrainQuantity);
		
		assertEquals (1, tNewTrainIndex);

		trainPortfolio.removeTrain ("4");
		tTrainQuantity = trainPortfolio.getTrainCount ();
		tNewTrainIndex = trainPortfolio.updateTrainIndex (tTrainIndex, tTrainQuantity);
		assertEquals (0, tNewTrainIndex);
		
		trainPortfolio.updateForCompactPortfolio (tTrainCertJPanel, tTrainQuantity, tTrainName);
		quantityLabelTest (tTrainCertJPanel, 4, GUI.EMPTY_STRING);

		tNewTrainIndex = trainPortfolio.updateTrainIndex (tNewTrainIndex, tTrainQuantity);
		
		assertEquals (-1, tNewTrainIndex);
	}

	private void quantityLabelTest (JPanel aTrainCertJPanel, int aCompoentIndex, String aExpectedText) {
		JComponent tPanelComponent;
		JLabel tLabel;
		String tLabelText;
		
		tLabelText = GUI.EMPTY_STRING;
		try {
			tPanelComponent = (JComponent) aTrainCertJPanel.getComponent (aCompoentIndex);
			if (tPanelComponent instanceof JLabel) {
				tLabel = (JLabel) tPanelComponent;
				tLabelText = tLabel.getText ();
			}
		} catch (ArrayIndexOutOfBoundsException eOutOfBounds) {
		}
		assertEquals (aExpectedText, tLabelText);
	}
}
