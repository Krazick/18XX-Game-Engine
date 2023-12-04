package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.game.GameManager;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class SetParValueEffect extends Effect {
	public final static String NAME = "Set Par Value";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_PAR_VALUE = new AttributeName ("parValue");
	int parValue;
	String companyAbbrev;

	public SetParValueEffect () {
		super ();
		setName (NAME);
	}

	public SetParValueEffect (ActorI aActor, ShareCompany aShareCompany, int aParPrice) {
		super (NAME, aActor);
		setCompanyAbbrev (aShareCompany.getAbbrev ());
		setParValue (aParPrice);
	}

	public SetParValueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tCompanyAbbrev;
		int tParValue;

		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tParValue = aEffectNode.getThisIntAttribute (AN_PAR_VALUE);
		setCompanyAbbrev (tCompanyAbbrev);
		setParValue (tParValue);
	}

	public String getCompanyAbbrev () {
		return companyAbbrev;
	}

	public int getParValue () {
		return parValue;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, companyAbbrev);
		tEffectElement.setAttribute (AN_PAR_VALUE, getParValue ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + actor.getName () + " sets " + name + " for " + companyAbbrev + " to $ " + parValue
				+ ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}

	public void setParValue (int aParValue) {
		parValue = aParValue;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		ShareCompany tShareCompany;

		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tShareCompany = aRoundManager.getShareCompany (companyAbbrev);
			aRoundManager.setParPrice (tShareCompany, parValue);
			tShareCompany.updateListeners (ShareCompany.SET_PAR_PRICE);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		MarketCell tMarketCell;
		ShareCompany tShareCompany;
		Token tToken;
		Market tMarket;

		tEffectUndone = false;
		tMarket = aRoundManager.getMarket ();
		if (actor.isAPlayer ()) {
			tMarketCell = tMarket.getMarketCellContainingToken (companyAbbrev);
			tMarketCell.printMarketCellInfo ();
			tToken = tMarketCell.getToken (companyAbbrev);
			tShareCompany = (ShareCompany) tToken.getWhichCompany ();
			tShareCompany.setNoPrice ();
			tMarketCell.redrawMarket ();
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}