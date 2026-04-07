package ge18xx.round.action;

import org.junit.jupiter.api.BeforeAll;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.round.RoundTestFactory;
import geUtilities.utilites.xml.UtilitiesTestFactory;

public class ActionTester {
	protected UtilitiesTestFactory utilitiesTestFactory;
	protected RoundTestFactory roundTestFactory;
	protected GameTestFactory gameTestFactory;
	protected ActionEffectsFactory actionEffectsFactory;
	GameManager gameManager;
	
	@BeforeAll
	void factorySetUp () {
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager ();
		roundTestFactory = new RoundTestFactory ();
		utilitiesTestFactory = roundTestFactory.getUtilitiesTestFactory ();
		actionEffectsFactory = new ActionEffectsFactory (gameManager, utilitiesTestFactory);
	}
}
