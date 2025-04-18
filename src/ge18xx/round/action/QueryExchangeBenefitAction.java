package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.company.benefit.QueryExchangeBenefit;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.QueryExchangeBenefitEffect;
import ge18xx.round.action.effects.StateChangeEffect;
import geUtilities.xml.XMLNode;

public class QueryExchangeBenefitAction extends Action {
	public final static String NAME = "Query Exchange Benefit";

	public QueryExchangeBenefitAction () {
		super ();
		setName (NAME);
	}

	public QueryExchangeBenefitAction (String aName) {
		super (aName);
	}

	public QueryExchangeBenefitAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public QueryExchangeBenefitAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addQueryExchangeBenefitEffect (ActorI aFromActor, ActorI aToActor,
						Corporation aPrivateCompany, QueryExchangeBenefit aQueryExchangeBenefit) {
		QueryExchangeBenefitEffect tQueryExchangeBenefitEffect;

		tQueryExchangeBenefitEffect = new QueryExchangeBenefitEffect (aFromActor, aToActor,
						aPrivateCompany, aQueryExchangeBenefit);
		addEffect (tQueryExchangeBenefitEffect);
	}

	public void addStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState, ActorI.ActionStates aNewState) {
		StateChangeEffect tStateChangeEffect;

		tStateChangeEffect = new StateChangeEffect (aActor, aOldState, aNewState);
		addEffect (tStateChangeEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " must answer Query Exchange Benefit Question.";

		return tSimpleActionReport;
	}
}
