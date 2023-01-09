package ge18xx.train;

import org.mockito.Mockito;

import ge18xx.company.Coupon;

public class TrainTestFactory {

	public TrainTestFactory () {
	}

	public Coupon buildTrain () {
		Coupon tTrain;

		tTrain = new Train ();

		return tTrain;
	}

	public Train buildTrainMock () {
		Train mTrain;

		mTrain = Mockito.mock (Train.class);
		Mockito.when (mTrain.getCityCount ()).thenReturn (3);

		return mTrain;
	}

	public TrainPortfolio buildTrainPortfolio () {
		TrainPortfolio tTrainPortfolio;

		tTrainPortfolio = new TrainPortfolio ();

		return tTrainPortfolio;
	}

	public TrainPortfolio buildTrainPortfolioMock () {
		TrainPortfolio tTrainPortfolio;

		tTrainPortfolio = Mockito.mock (TrainPortfolio.class);
		Mockito.when (tTrainPortfolio.getPortfolioHolderAbbrev ()).thenReturn ("MTPH");

		return tTrainPortfolio;
	}
}
