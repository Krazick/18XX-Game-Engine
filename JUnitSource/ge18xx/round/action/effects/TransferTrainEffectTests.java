package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

@DisplayName ("Transfer Train Effect Constructor Tests")
public class TransferTrainEffectTests {
	TransferTrainEffect effectBeta;
	TrainCompany actorBeta;
	TrainCompany actorGamma;
	TrainCompany mActorDelta;
	ShareCompany actorZeta;
	GameManager mGameManager;
	PlayerManager playerManager;
	GameTestFactory testFactory;
	CompanyTestFactory companyTestFactory;
	Train train200;
	Train train201;
	Train train300;
	Train train301;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;

		tClientName = "TFBuster";
		testFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (testFactory);
		mGameManager = testFactory.buildGameManagerMock (tClientName);
		playerManager = new PlayerManager (mGameManager);
		actorBeta = companyTestFactory.buildAShareCompany (1);
		actorGamma = companyTestFactory.buildAShareCompany (2);
		actorZeta = companyTestFactory.buildAShareCompany (companyTestFactory.NO_COMPANY_INDEX);
		mActorDelta = companyTestFactory.buildShareCompanyMock ();
		
		train200 = new Train ("Test200", 0, 1, 2, 80, 200);
		train201 = new Train ("Test201", 0, 1, 2, 80, 201);
		train300 = new Train ("Test300", 0, 1, 3, 180, 300);
		train301 = new Train ("Test301", 0, 1, 3, 180, 301);
		effectBeta = new TransferTrainEffect (actorBeta, train201, actorGamma);
	}

	@Test
	@DisplayName ("Simple Constructor Tests")
	void simpleConstructorTests () {
		String tReportResult = "--Effect: Transfer Train named Test201 (201) from Test Pennsylvania to Test Baltimore and Ohio.";
		TrainCompany tShareActor;
		TrainCompany tToShareActor;

		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals ("Transfer Train", effectBeta.getName ());
		assertEquals ("Test Pennsylvania", effectBeta.getActorName ());
		assertEquals ("Test Baltimore and Ohio", effectBeta.getToActorName ());

		tShareActor = (TrainCompany) effectBeta.getActor ();
		tToShareActor = (TrainCompany) effectBeta.getToActor ();
		assertEquals ("Test Pennsylvania", tShareActor.getName ());
		assertEquals (tReportResult, effectBeta.getEffectReport (null));
		assertNotNull (effectBeta.getToActorName ());
		assertEquals ("Test Baltimore and Ohio", effectBeta.getToActorName ());
		assertEquals ("Test Baltimore and Ohio", tToShareActor.getName ());

		assertNull (actorZeta);
	}
	
	@Test
	@DisplayName ("Test XMLDocument for Transfer Train Action")
	void transferTrainEffectXMLTest () {
		XMLDocument tXMLDocument;
		XMLElement tXMLElement;
		String tExpectedResult = "<Effect class=\"ge18xx.round.action.effects.TransferTrainEffect\" fromActor=\"TPRR\" fromName=\"Test Pennsylvania\" id=\"201\" isAPrivate=\"false\" name=\"\" order=\"1\" toActor=\"TBNO\" toNickName=\"\"/>\n";
		
		tXMLDocument = new XMLDocument ();
		tXMLElement = effectBeta.getEffectElement (tXMLDocument, ActorI.AN_ACTOR_NAME);
		System.out.println ("Transfer Train Effect Element: " + tXMLElement.toXMLString ());
		assertEquals (tExpectedResult, tXMLElement.toXMLString ());

	}
	
	@Test
	@DisplayName ("Test Get Train by ID from Portfolios")
	void getTrainByID () {
		Train tBankTrain;
		Train tBankPoolTrain;
		Train tCorporationTrain;
		Train tDeltaTrain;
		Train tFoundTrain;
		
		tBankTrain = train200;
		Mockito.when (mGameManager.getTrainByID (200)).thenReturn (tBankTrain);
		
		tBankPoolTrain = train201;
		Mockito.when (mGameManager.getBankPoolTrainByID (201)).thenReturn (tBankPoolTrain);
		
		tCorporationTrain = train300;
		Mockito.when (mGameManager.getTrainByID (300)).thenReturn (tCorporationTrain);
	
		tDeltaTrain = train301;
		Mockito.when (mActorDelta.getTrainByID (301)).thenReturn (tDeltaTrain);
		Mockito.when (mActorDelta.isATrainCompany ()).thenReturn (true);
	
		effectBeta.getTrainFromBank (mGameManager, "200", 200);
		tFoundTrain = (Train) effectBeta.getTrain ();
		assertEquals (tBankTrain, tFoundTrain);
		
		effectBeta.getTrainFromBankPool (mGameManager, "201", 201);
		tFoundTrain = (Train) effectBeta.getTrain ();
		assertEquals (tBankPoolTrain, tFoundTrain);
		
		effectBeta.getTrainFromCorporation (mGameManager, actorBeta, "300", 300);
		tFoundTrain = (Train) effectBeta.getTrain ();
		assertEquals (tCorporationTrain, tFoundTrain);
		
		effectBeta.getTrainFromCorporation (mGameManager, mActorDelta, "301", 301);
		tFoundTrain = (Train) effectBeta.getTrain ();
		assertEquals (tDeltaTrain, tFoundTrain);

	}
}
