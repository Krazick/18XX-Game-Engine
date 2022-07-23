package ge18xx.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Portfolio;
import ge18xx.train.Train;
import ge18xx.train.TrainPortfolio;

@DisplayName ("Game Bank Tests")
class GameBankTests {
	private BankTestFactory bankTestFactory;
	private GameTestFactory gameTestFactory;
	private GameManager mGameManager;
	private Portfolio mPortfolio;
	private TrainPortfolio mTrainPortfolio;
	private GameBank gameBank;
	
	@BeforeEach
	void setUp () throws Exception {
		
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		
		mGameManager = gameTestFactory.buildGameManagerMock ();
		gameBank = bankTestFactory.buildGameBank (mGameManager);
		
		mPortfolio = Mockito.mock (Portfolio.class);
		Mockito.when (mPortfolio.getName ()).thenReturn ("Portfolio Mock Name");
		mTrainPortfolio = Mockito.mock (TrainPortfolio.class);
		Mockito.when (mTrainPortfolio.getName ()).thenReturn ("Train Portfolio Mock Name");
		
		gameBank.setPortfolio (mPortfolio);
		gameBank.setTrainPortfolio (mTrainPortfolio);
	}

	@DisplayName ("Constructor Tests")
	@Test
	void gameBankConstructorTests () {
		GameBank tGameBank;
		Portfolio tPortfolio;
		TrainPortfolio tTrainPortfolio;
		
		tGameBank = bankTestFactory.buildGameBank (mGameManager);
		assertEquals ("Test Game Bank", tGameBank.getName ());
		tGameBank.setPortfolio (mPortfolio);
		tGameBank.setTrainPortfolio (mTrainPortfolio);
		
		tPortfolio = tGameBank.getPortfolio ();
		tTrainPortfolio = tGameBank.getTrainPortfolio ();
		
		assertEquals ("Portfolio Mock Name", tPortfolio.getName ());
		assertEquals ("Train Portfolio Mock Name", tTrainPortfolio.getName ());
		assertEquals ("Fixed", tGameBank.getStateName ());
	}

	@DisplayName ("Add Certificate Test")
	@Test
	void addCertificateTest () {
		Certificate mCertificate;
		
		mCertificate  = Mockito.mock (Certificate.class);
		Mockito.doNothing ().when (mPortfolio).addCertificate (any (Certificate.class));
		
		gameBank.addCertificate (mCertificate);
		Mockito.verify (mPortfolio, times (1)).addCertificate (any (Certificate.class));
	}
	
	@DisplayName ("Add Train Test")
	@Test
	void addTrainTest () {
		Train mTrain;
		
		mTrain  = Mockito.mock (Train.class);
		Mockito.doNothing ().when (mTrainPortfolio).addTrain (any (Train.class));
		
		gameBank.addTrain (mTrain);
		Mockito.verify (mTrainPortfolio, times (1)).addTrain (any (Train.class));
	}
	
	@DisplayName ("Clear Selections Test")
	@Test
	void clearSelectionsTest () {
		Mockito.doNothing ().when (mTrainPortfolio).clearSelections ();
		Mockito.doNothing ().when (mPortfolio).clearSelections ();
		
		gameBank.clearSelections ();
		Mockito.verify (mPortfolio, times (1)).clearSelections ();
		Mockito.verify (mTrainPortfolio, times (1)).clearSelections ();
	}
}
