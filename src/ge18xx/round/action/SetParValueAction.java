package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.SetParValueEffect;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class SetParValueAction extends SetWaitStateAction {
	public final static String NAME = "Set Par Value";

	public SetParValueAction () {
		super ();
		setName (NAME);
	}

	public SetParValueAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public SetParValueAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addSetParValueEffect (ActorI aActor, ShareCompany aShareCompany, int aParPrice, String aCoordinates) {
		SetParValueEffect tSetParValueEffect;

		tSetParValueEffect = new SetParValueEffect (aActor, aShareCompany, aParPrice, aCoordinates);
		addEffect (tSetParValueEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " set Par Value for " + getCompanyAbbrev () + " at "
				+ Bank.formatCash (getParValue ()) + ".";

		return tSimpleActionReport;
	}

	public int getParValue () {
		int tParValue;

		tParValue = -1;
		for (Effect tEffect : effects) {
			if (tParValue == -1) {
				if (tEffect instanceof SetParValueEffect) {
					tParValue = ((SetParValueEffect) tEffect).getParValue ();
				}
			}
		}

		return tParValue;
	}

	public String getCompanyAbbrev () {
		String tCompanyAbbrev;

		tCompanyAbbrev = GUI.EMPTY_STRING;
		for (Effect tEffect : effects) {
			if (tCompanyAbbrev.equals (GUI.EMPTY_STRING)) {
				if (tEffect instanceof SetParValueEffect) {
					tCompanyAbbrev = ((SetParValueEffect) tEffect).getCompanyAbbrev ();
				}
			}
		}

		return tCompanyAbbrev;
	}
}
