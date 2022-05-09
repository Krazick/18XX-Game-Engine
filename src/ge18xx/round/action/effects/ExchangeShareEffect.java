package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLDocument;

public class ExchangeShareEffect extends ExchangePrezShareEffect {
	public final static String NAME = "Exchanged Share";
	final static AttributeName AN_EXCHANGE = new AttributeName ("exchanged");
	public final static String NO_EXCHANGE = null;
	String newCorporationAbbrev;

	public ExchangeShareEffect () {
		super ();
		setName (NAME);
		setNewCorporationAbbrev (NO_EXCHANGE_PREZ);
	}

	public ExchangeShareEffect (String aName, ActorI aToActor, String aCorporationAbbrev,
			String aNewCorporationAbbrev) {
		super (aName, aToActor, aCorporationAbbrev);
		setNewCorporationAbbrev (aNewCorporationAbbrev);
	}

	public ExchangeShareEffect (ActorI aToActor, String aCorporationAbbrev, String aNewCorporationAbbrev) {
		super (NAME, aToActor, aCorporationAbbrev);
		setNewCorporationAbbrev (aNewCorporationAbbrev);
	}

	public ExchangeShareEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tNewCorporationAbbrev;

		tNewCorporationAbbrev = aEffectNode.getThisAttribute (AN_EXCHANGE);
		setNewCorporationAbbrev (tNewCorporationAbbrev);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_EXCHANGE, getNewCorporationAbbrev ());

		return tEffectElement;
	}

	public String getNewCorporationAbbrev () {
		return newCorporationAbbrev;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " + corporationAbbrev + " into " + newCorporationAbbrev + " for "
				+ actor.getName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;

		tEffectApplied = false;
		tPlayer = (Player) actor;
		tPlayer.setExchangedPrezShare (newCorporationAbbrev);
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

	public void setNewCorporationAbbrev (String aCorporationAbbrev) {
		newCorporationAbbrev = aCorporationAbbrev;
	}
}