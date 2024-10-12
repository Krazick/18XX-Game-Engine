package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.effects.ApplyDiscountEffect;
import ge18xx.round.action.effects.ChangeRoundIDEffect;
import ge18xx.round.action.effects.ClearSoldCompanyEffect;
import ge18xx.round.action.effects.HideFrameEffect;
import geUtilities.xml.XMLFrame;
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

	public void addApplyDiscountEffect (ActorI aActor, String aCertificateName, int aOldDiscount, int aNewDiscount) {
		ApplyDiscountEffect tApplyDiscountEffect;

		tApplyDiscountEffect = new ApplyDiscountEffect (aActor, aCertificateName, aOldDiscount, aNewDiscount);
		addEffect (tApplyDiscountEffect);
	}

	public void addClearSoldCompanyEffect (ActorI aActor, String aSoldCompanies, String aRoundID) {
		ClearSoldCompanyEffect tClearSoldCompanyEfect;

		tClearSoldCompanyEfect = new ClearSoldCompanyEffect (aActor, aSoldCompanies, aRoundID);
		addEffect (tClearSoldCompanyEfect);
	}

	public void addHideFrameEffect (ActorI aActor, XMLFrame aXMLAuctionFrame) {
		HideFrameEffect tHideFrameEffect;
		
		tHideFrameEffect = new HideFrameEffect (aActor, aXMLAuctionFrame);
		addEffect (tHideFrameEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;
		String tOldState;
		String tNewState;

		tSimpleActionReport = super.getSimpleActionReport ();
		tOldState = getOldState ();
		tNewState = getNewState ();
		if (!tNewState.equals (tOldState)) {
			tSimpleActionReport = "Changed " + actor.getName () + " to " + getNewState () + ".";
		} else {
			tSimpleActionReport = getName () + " remains as " + tNewState;
		}

		return tSimpleActionReport;
	}
}