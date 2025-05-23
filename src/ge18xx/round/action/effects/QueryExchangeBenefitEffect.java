package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.ExchangeQueryFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.QueryExchangeBenefit;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class QueryExchangeBenefitEffect extends ToEffect {
	public final static String NAME = "Query Exchange Benefit";
	public final static AttributeName AN_PRIVATE_ID = new AttributeName ("privateID");
	public final static AttributeName AN_QEBENEFIT_NAME = new AttributeName ("qeBenefitName");
	int privateID;
	String qeBenefitName;
	Corporation privateCompany;
	QueryExchangeBenefit queryExchangeBenefit;

	public QueryExchangeBenefitEffect () {
		super (NAME);
	}

	public QueryExchangeBenefitEffect (String aName) {
		super (aName);
	}

	public QueryExchangeBenefitEffect (ActorI aFromActor, ActorI aToActor, Corporation aPrivateCompany,
			QueryExchangeBenefit aQueryExchangeBenefit) {
		super (NAME, aFromActor, aToActor);
		setPrivateCompany (aPrivateCompany);
		setQueryExchangeBenefit (aQueryExchangeBenefit);
		setPrivateCompanyID (aPrivateCompany.getID ());
		setQEBenefitName (aQueryExchangeBenefit.getName ());
	}

	public QueryExchangeBenefitEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		int tPrivateID;
		String tQEBenefitName;
		Corporation tCorporation;
		PrivateCompany tPrivateCompany;
		Benefit tBenefit;
		QueryExchangeBenefit tQueryExchangeBenefit;

		tPrivateID = aEffectNode.getThisIntAttribute (AN_PRIVATE_ID);
		setPrivateCompanyID (tPrivateID);
		tQEBenefitName = aEffectNode.getThisAttribute (AN_BENEFIT_NAME);
		setQEBenefitName (tQEBenefitName);
		tCorporation = aGameManager.getCorporationByID (tPrivateID);
		if (tCorporation.isAPrivateCompany ()) {
			tPrivateCompany = (PrivateCompany) tCorporation;
			setPrivateCompany (tPrivateCompany);
			tBenefit = tPrivateCompany.getBenefitNamed (tQEBenefitName);
			if (tBenefit != Benefit.NO_BENEFIT) {
				tQueryExchangeBenefit = (QueryExchangeBenefit) tBenefit;
				setQueryExchangeBenefit (tQueryExchangeBenefit);
			}
		}
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_PRIVATE_ID, getPrivateCompanyID ());
		tEffectElement.setAttribute (AN_BENEFIT_NAME, getQEBenefitName ());

		return tEffectElement;
	}

	public void setPrivateCompany (Corporation aPrivateCompany) {
		privateCompany = aPrivateCompany;
	}

	public void setQueryExchangeBenefit (QueryExchangeBenefit aQueryExchangeBenefit) {
		queryExchangeBenefit = aQueryExchangeBenefit;
	}

	public void setPrivateCompanyID (int aPrivateID) {
		privateID = aPrivateID;
	}

	public void setQEBenefitName (String aQEBenefitName) {
		qeBenefitName = aQEBenefitName;
	}

	public Corporation getPrivateCompany () {
		return privateCompany;
	}

	public QueryExchangeBenefit getQueryExchangeBenefit  () {
		return queryExchangeBenefit;
	}

	public int getPrivateCompanyID () {
		return privateID;
	}

	public String getQEBenefitName  () {
		return qeBenefitName;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		String tToPlayerName;
		ExchangeQueryFrame tExchangeQueryFrame;

		tEffectApplied = false;
		tToPlayerName = toActor.getName ();
		if (aRoundManager.isNetworkAndIsThisClient (tToPlayerName)) {
			if (queryExchangeBenefit != Benefit.NO_BENEFIT) {
				tExchangeQueryFrame = new ExchangeQueryFrame (aRoundManager, this);
				tExchangeQueryFrame.setVisible (true);
				tEffectApplied = true;
			}
		} else {
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		// For a Query Exchange Benefit Effect no actual change to the state of the game was Applied
		// Therefore there is nothing to undo.

		tEffectUndone = true;

		return tEffectUndone;
	}
}
