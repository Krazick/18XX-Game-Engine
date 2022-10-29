package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ExchangePrezShareEffect extends Effect {
	public final static String NAME = "Exchanged President Share";
	final static AttributeName AN_EXCHANGE_PREZ = new AttributeName ("exchangedPrez");
	public final static String NO_EXCHANGE_PREZ = null;
	String corporationAbbrev;

	public ExchangePrezShareEffect () {
		super ();
		setName (NAME);
		setExchangedPrez (NO_EXCHANGE_PREZ);
	}

	public ExchangePrezShareEffect (String aName, ActorI aToActor) {
		super (aName, aToActor);
		setExchangedPrez (NO_EXCHANGE_PREZ);
	}

	public ExchangePrezShareEffect (String aName, ActorI aToActor, String aCorporationAbbrev) {
		super (aName, aToActor);
		setExchangedPrez (aCorporationAbbrev);
	}

	public ExchangePrezShareEffect (ActorI aToActor, String aCorporationAbbrev) {
		super (NAME, aToActor);
		setExchangedPrez (aCorporationAbbrev);
	}

	public ExchangePrezShareEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tCorporationAbbrev;

		tCorporationAbbrev = aEffectNode.getThisAttribute (AN_EXCHANGE_PREZ);
		setExchangedPrez (tCorporationAbbrev);
	}

	public String getCorporationAbbrev () {
		return corporationAbbrev;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_EXCHANGE_PREZ, getCorporationAbbrev ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " + corporationAbbrev + " for " + actor.getName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setExchangedPrez (String aCorporationAbbrev) {
		corporationAbbrev = aCorporationAbbrev;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;

		tEffectApplied = false;
		tPlayer = (Player) actor;
		tPlayer.setExchangedPrezShare (corporationAbbrev);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tEffectUndone = false;
		tPlayer = (Player) actor;
		tPlayer.setExchangedPrezShare (null);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
