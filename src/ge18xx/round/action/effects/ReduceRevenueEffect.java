package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class ReduceRevenueEffect extends Effect {
	public final static String NAME = "Reduced Revenue";
	final static AttributeName AN_REVENUE_REDUCED_BY = new AttributeName ("reduceRevenueBy");
	public final static int NO_REVENUE_REDUCTION = 0;
	int reduceRevenueBy;
	
	public ReduceRevenueEffect () {
		super ();
		setName (NAME);
		setReduceRevenueBy (NO_REVENUE_REDUCTION);
	}

	public ReduceRevenueEffect (ActorI aFromActor, int aReduceRevenueBy) {
		super (NAME, aFromActor);
		setReduceRevenueBy (aReduceRevenueBy);
	}

	public ReduceRevenueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		int tReduceRevenueBy;

		tReduceRevenueBy = aEffectNode.getThisIntAttribute (AN_REVENUE_REDUCED_BY);
		setReduceRevenueBy (tReduceRevenueBy);
	}

	public void setReduceRevenueBy (int aReduceRevenueBy) {
		reduceRevenueBy = aReduceRevenueBy;
	}
	
	public int getReduceRevenueBy () {
		return reduceRevenueBy;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_REVENUE_REDUCED_BY, reduceRevenueBy);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " by " + Bank.formatCash (reduceRevenueBy) + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		int tRevenueReducedTo;
		ShareCompany tShareCompany;
		
		tEffectApplied = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tRevenueReducedTo = tShareCompany.getThisRevenue () - reduceRevenueBy;
			tShareCompany.setThisRevenue (tRevenueReducedTo);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		ShareCompany tShareCompany;
		int tRevenueResetTo;

		tEffectUndone = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tRevenueResetTo = tShareCompany.getThisRevenue () + reduceRevenueBy;
			tShareCompany.setThisRevenue (tRevenueResetTo);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
