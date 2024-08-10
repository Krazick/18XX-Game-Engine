package ge18xx.train;

import org.mockito.Mockito;

import ge18xx.company.Coupon;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameTestFactory;
import geUtilities.utilites.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class TrainTestFactory {
	private GameTestFactory gameTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;
	TrainInfo tTrainInfo1;
	TrainInfo tTrainInfo2;
	TrainInfo tTrainInfo3;
	TrainInfo tTrainInfo4;

	public TrainTestFactory () {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
	}

	public Coupon buildTrain () {
		Coupon tTrain;

		tTrain = new Train ();

		return tTrain;
	}

	public Train buildTrain (int aTrainInfoIndex) {
		Train tTrain;
		
		if (aTrainInfoIndex == 1) {
			tTrainInfo1 = buildTrainInfo (aTrainInfoIndex);
			tTrain = tTrainInfo1.getTrain ();
		} else if (aTrainInfoIndex == 2) {
			tTrainInfo2 = buildTrainInfo (aTrainInfoIndex);
			tTrain = tTrainInfo2.getTrain ();
		} else if (aTrainInfoIndex == 3) {
			tTrainInfo3 = buildTrainInfo (aTrainInfoIndex);
			tTrain = tTrainInfo3.getTrain ();
		} else if (aTrainInfoIndex == 4) {
			tTrainInfo4 = buildTrainInfo (aTrainInfoIndex);
			tTrain = tTrainInfo4.getTrain ();
		} else {
			tTrain = Train.NO_TRAIN;
		}
		
		return tTrain;
	}
	
	public Train buildTrainMock () {
		Train mTrain;

		mTrain = Mockito.mock (Train.class);
		Mockito.when (mTrain.getCityCount ()).thenReturn (3);

		return mTrain;
	}

	public TrainInfo buildTrainInfo (int aInfoIndex) {
		TrainInfo tTrainInfo;
		String tTrainInfo1XML = "<Train name=\"4\" order=\"3\" revenueCenters=\"4\" quantity=\"4\" price=\"350\"\n"
				+ "				onLast=\"4\" triggerPhase=\"3.1\" rust=\"2\" />";
		String tTrainInfo2XML = "<Train name=\"5\" order=\"4\" revenueCenters=\"5\" quantity=\"3\" price=\"550\"\n"
				+ "				onLast=\"5\" triggerPhase=\"4.1\" tileInfo=\"Brown\" isPermanent=\"true\" />";
		String tTrainInfo3XML = "<Train name=\"5A\" order=\"5\" revenueCenters=\"5\" quantity=\"3\" price=\"500\"\n"
				+ "				onLast=\"6\" triggerPhase=\"4.2\" tileInfo=\"Brown\" isPermanent=\"true\" />";
		String tTrainInfo4XML = "<Train name=\"Diesel\" order=\"6\" revenueCenters=\"99\" quantity=\"6\" price=\"1000\"\n"
				+ "				triggerPhase=\"6.1\" tileInfo=\"Brown\" isPermanent=\"true\" />";
		
		if (aInfoIndex == 1) {
			tTrainInfo = buildTrainInfo (tTrainInfo1XML);
		} else if (aInfoIndex == 2) {
			tTrainInfo = buildTrainInfo (tTrainInfo2XML);
		} else if (aInfoIndex == 3) {
			tTrainInfo = buildTrainInfo (tTrainInfo3XML);
		} else if (aInfoIndex == 4) {
			tTrainInfo = buildTrainInfo (tTrainInfo4XML);
		} else {
			tTrainInfo = TrainInfo.NO_TRAIN_INFO;
		}

		return tTrainInfo;
	}
	
	private TrainInfo buildTrainInfo (String aTrainInfoXML) {
		XMLNode tTrainInfoNode;
		TrainInfo tTrainInfo;

		tTrainInfoNode = utilitiesTestFactory.buildXMLNode (aTrainInfoXML);
		if (tTrainInfoNode != XMLNode.NO_NODE) {
			tTrainInfo = new TrainInfo (tTrainInfoNode);
		} else {
			tTrainInfo = TrainInfo.NO_TRAIN_INFO;
		}

		return tTrainInfo;
	}
	
	public TrainPortfolio buildTrainPortfolio (TrainCompany aTrainCompany) {
		TrainPortfolio tTrainPortfolio;

		tTrainPortfolio = new TrainPortfolio (aTrainCompany);

		return tTrainPortfolio;
	}

	public TrainPortfolio buildTrainPortfolioMock () {
		TrainPortfolio tTrainPortfolio;

		tTrainPortfolio = Mockito.mock (TrainPortfolio.class);
		Mockito.when (tTrainPortfolio.getPortfolioHolderAbbrev ()).thenReturn ("MTP");

		return tTrainPortfolio;
	}
}
