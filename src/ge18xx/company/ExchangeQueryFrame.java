package ge18xx.company;

import ge18xx.company.benefit.QueryExchangeBenefit;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ResponseOfferAction;
import ge18xx.round.action.effects.QueryExchangeBenefitEffect;
import ge18xx.round.action.effects.ToEffect;

public class ExchangeQueryFrame extends QueryFrame {
	private static final long serialVersionUID = 1L;
	public final static String NAME = "QUERY EXCHANGE";

	String actionText;

	public ExchangeQueryFrame (RoundManager aRoundManager, ToEffect aToEffect) {
		super (aRoundManager, aToEffect);
		String tPlayerName;

		tPlayerName = aRoundManager.getClientUserName ();
		setTitle ("Exchange Query for " + tPlayerName);
		setAcceptButtonLabel ("YES");
		setRejectButtonLabel ("NO");
	}

	@Override
	protected void setOfferTopPanel () {
		String tOffer;
		QueryExchangeBenefitEffect tQueryExchangeBenefitEffect;
		QueryExchangeBenefit tQueryExchangeBenefit;

		if (toEffect instanceof QueryExchangeBenefitEffect) {
			tQueryExchangeBenefitEffect = (QueryExchangeBenefitEffect) toEffect;
			tQueryExchangeBenefit = tQueryExchangeBenefitEffect.getQueryExchangeBenefit ();

			tOffer = tQueryExchangeBenefit.buildQueryText ();
			actionText = tQueryExchangeBenefit.buildActionText ();
			buildOfferTopPanel (tOffer);
		}
	}

	@Override
	protected void addResponseOfferEffect (ResponseOfferAction aResponseOfferAction, ActorI aFromActor,
			ActorI aToActor, boolean aResponse) {
		aResponseOfferAction.addResponseOfferEffect (aFromActor, aToActor, aResponse, NAME, actionText);
	}
}
