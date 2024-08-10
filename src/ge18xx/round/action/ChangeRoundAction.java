package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.effects.ChangeRoundIDEffect;
import ge18xx.round.action.effects.ClearSoldCompanyEffect;
import ge18xx.round.action.effects.SetTriggeredAuctionEffect;
import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class ChangeRoundAction extends ChangeStateAction {
	public final static String NAME = "Change Round";

	public ChangeRoundAction () {
		super ();
		setName (NAME);
	}

	public ChangeRoundAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ChangeRoundAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addChangeRoundIDEffect (ActorI aActor, String aOldRoundID, String aNewRoundID) {
		ChangeRoundIDEffect tChangeRoundIDEffect;

		tChangeRoundIDEffect = new ChangeRoundIDEffect (aActor, aOldRoundID, aNewRoundID);
		addEffect (tChangeRoundIDEffect);
	}

	public void addClearSoldCompanyEffect (ActorI aActor, String aSoldCompanies, String aRoundID) {
		ClearSoldCompanyEffect tClearSoldCompanyEfect;

		tClearSoldCompanyEfect = new ClearSoldCompanyEffect (aActor, aSoldCompanies, aRoundID);
		addEffect (tClearSoldCompanyEfect);
	}

	public void addSetTriggeredAuctionEffect (ActorI aActor, boolean tTriggeredAuction) {
		SetTriggeredAuctionEffect tSetTriggeredAuctionEffect;

		tSetTriggeredAuctionEffect = new SetTriggeredAuctionEffect (aActor, tTriggeredAuction);
		addEffect (tSetTriggeredAuctionEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = GUI.EMPTY_STRING;
		String tOldState, tNewState;

		tOldState = getOldState ();
		tNewState = getNewState ();
		if (!tNewState.equals (tOldState)) {
			tSimpleActionReport = "Changed " + actor.getName () + " to " + getNewState () + ".";
		}

		return tSimpleActionReport;
	}
}