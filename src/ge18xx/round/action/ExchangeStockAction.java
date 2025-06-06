package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.ExchangePrezShareEffect;
import ge18xx.round.action.effects.ExchangeShareEffect;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class ExchangeStockAction extends TransferOwnershipAction {
	public final static String NAME = "Exchange Stock";

	public ExchangeStockAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ExchangeStockAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addExchangePrezShareEffect (String aCorporationAbbrev) {
		ExchangePrezShareEffect tExchangePrezShareEffect;

		tExchangePrezShareEffect = new ExchangePrezShareEffect (getActor (), aCorporationAbbrev);
		addEffect (tExchangePrezShareEffect);
	}

	public void addExchangeShareEffect (String aCorporationAbbrev, String aNewCorporationAbbrev) {
		ExchangeShareEffect tExchangeShareEffect;

		tExchangeShareEffect = new ExchangeShareEffect (getActor (), aCorporationAbbrev, aNewCorporationAbbrev);
		addEffect (tExchangeShareEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " exchanged the Share of " + getOldCompanyAbbrev ()
				+ " for a Share of " + getNewCompanyAbbrev ();

		return tSimpleActionReport;
	}

	public String getOldCompanyAbbrev () {
		String tOldCompanyAbbrev = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (tOldCompanyAbbrev.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof ExchangeShareEffect) {
					tOldCompanyAbbrev = ((ExchangeShareEffect) tEffect).getCorporationAbbrev ();
				}
			}
		}

		return tOldCompanyAbbrev;
	}

	public String getNewCompanyAbbrev () {
		String tNewCompanyAbbrev = GUI.EMPTY_STRING;

		for (Effect tEffect : effects) {
			if (tNewCompanyAbbrev.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof ExchangeShareEffect) {
					tNewCompanyAbbrev = ((ExchangeShareEffect) tEffect).getNewCorporationAbbrev ();
				}
			}
		}

		return tNewCompanyAbbrev;
	}
}
