package ge18xx.company;

import ge18xx.company.benefit.QueryExchangeBenefit;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ResponseToOfferAction;
import ge18xx.round.action.effects.QueryExchangeBenefitEffect;
import ge18xx.round.action.effects.ToEffect;

public class QueryExchangeFrame extends QueryFrame {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Query Exchange";
	public static final String BASE_TITLE = "Query Exchange";
	String actionText;

	public QueryExchangeFrame (RoundManager aRoundManager, ToEffect aToEffect) {
		super (aRoundManager, aToEffect);
		String tFrameTitle;
		GameManager tGameManager;

		tGameManager = aRoundManager.getGameManager ();
		tFrameTitle = tGameManager.createFrameTitle (BASE_TITLE);
		setTitle (tFrameTitle);
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
	protected void addResponseToOfferEffect (ResponseToOfferAction aResponseToOfferAction, ActorI aFromActor,
			ActorI aToActor, boolean aResponse) {
		aResponseToOfferAction.addResponseToOfferEffect (aFromActor, aToActor, aResponse, NAME, actionText);
	}
}
