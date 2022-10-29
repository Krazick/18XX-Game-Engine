package ge18xx.train;

import org.mockito.Mockito;

public class TrainTestFactory {

	public TrainTestFactory () {
	}


	public Train buildTrain () {
		Train tTrain;

		tTrain = new Train ();

		return tTrain;
	}

	public Train buildTrainMock () {
		Train mTrain;

		mTrain = Mockito.mock (Train.class);
		Mockito.when (mTrain.getCityCount ()).thenReturn (3);

		return mTrain;
	}
}
