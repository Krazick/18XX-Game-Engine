package ge18xx.round.action.effects;

import javax.swing.JFrame;

import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.company.benefit.QueryExchangeBenefit;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ResponseOfferAction;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class QueryExchangeBenefitEffect extends ToEffect {
	public final static String NAME = "Query Exchange Benefit";
	public final static AttributeName AN_PRIVATE_ID = new AttributeName ("privateID");
	public final static AttributeName AN_QEBENEFIT_NAME = new AttributeName ("qeBenefitName");
	int privateID;
	String qeBenefitName;
	PrivateCompany privateCompany;
	QueryExchangeBenefit queryExchangeBenefit;
	
	public QueryExchangeBenefitEffect () {
		super (NAME);
	}

	public QueryExchangeBenefitEffect (String aName) {
		super (aName);
	}

	public QueryExchangeBenefitEffect (ActorI aFromActor, ActorI aToActor, PrivateCompany aPrivateCompany,
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

	public void setPrivateCompany (PrivateCompany aPrivateCompany) {
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
	
	public PrivateCompany getPrivateCompany () {
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
		boolean tApprovedExchange;
		JFrame tRoundFrame;
		String tToPlayerName;
		
		tEffectApplied = false;
		tToPlayerName = toActor.getName ();
		tApprovedExchange = false;
		if (aRoundManager.isNetworkAndIsThisClient (tToPlayerName)) {
			if (queryExchangeBenefit != Benefit.NO_BENEFIT) {
				tRoundFrame = aRoundManager.getRoundFrame ();
				tApprovedExchange = queryExchangeBenefit.showQueryDialog (tRoundFrame);
				System.out.println ("Queried Exchange Benefit -- Approved: " + tApprovedExchange);
				sendOfferResponseAction (tApprovedExchange, aRoundManager);
				tEffectApplied = true;
			}
		} else {
			tEffectApplied = true;			
		}
		
		return tEffectApplied;
	}
	
	public void sendOfferResponseAction (boolean aResponse, RoundManager aRoundManager) {
		ResponseOfferAction tResponseOfferAction;
		GameManager tGameManager;
		ActionStates tRoundType;
		String tRoundID = "";
		ActorI tToActor, tFromActor;

		tRoundType = aRoundManager.getCurrentRoundType ();
		if (tRoundType == ActionStates.OperatingRound) {
			tRoundID = aRoundManager.getOperatingRoundID ();
		} else if (tRoundType == ActionStates.StockRound) {
			tRoundID = "" + aRoundManager.getStockRoundID ();
		}
		tGameManager = aRoundManager.getGameManager ();
		
		// Need to find the original Actor who sent the Query Offer, to send back to

		tToActor = getActor ();

		// Need to find the current Actor (who was sent the Query offer) to state who
		// it comes from

		tFromActor = getToActor ();
		tResponseOfferAction = new ResponseOfferAction (tRoundType, tRoundID, tFromActor);
		tResponseOfferAction.setChainToPrevious (true);
		tResponseOfferAction.addResponseOfferEffect (tFromActor, tToActor, aResponse, NAME, getName ());
		// Because we are in the middle of applying an Effect, but we NEED to send the Action Back
		// Set ApplyingAction off, add the action (which will then send it), and set ApplyingAction back on
		//
		// NEED TO DEBUG Here since this Action does not seem to be sending out!!!!
		tGameManager.setApplyingAction (false);
		aRoundManager.addAction (tResponseOfferAction);
		tGameManager.setApplyingAction (true);
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
