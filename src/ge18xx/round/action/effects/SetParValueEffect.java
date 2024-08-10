package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.game.GameManager;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.MarketFrame;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetParValueEffect extends Effect {
	public final static String NAME = "Set Par Value";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_PAR_VALUE = new AttributeName ("parValue");
	final static AttributeName AN_COORDINATES = new AttributeName ("coordinates");
	int parValue;
	String companyAbbrev;
	String coordinates;

	public SetParValueEffect () {
		super ();
		setName (NAME);
	}

	public SetParValueEffect (ActorI aActor, ShareCompany aShareCompany, int aParPrice, String aCoordinates) {
		super (NAME, aActor);
		
		String tCompanyAbbrev;
		
		tCompanyAbbrev = aShareCompany.getAbbrev ();
		setCompanyAbbrev (tCompanyAbbrev);
		setParValue (aParPrice);
		setCoordinates (aCoordinates);
	}

	public SetParValueEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tCompanyAbbrev;
		String tCoordinates;
		int tParValue;

		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tParValue = aEffectNode.getThisIntAttribute (AN_PAR_VALUE);
		tCoordinates = aEffectNode.getThisAttribute (AN_COORDINATES);
		setCompanyAbbrev (tCompanyAbbrev);
		setParValue (tParValue);
		setCoordinates (tCoordinates);
	}

	public String getCoordinates () {
		return coordinates;
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
		tEffectElement.setAttribute (AN_COORDINATES, getCoordinates ());

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		String tParValue;
		
		if (parValue > 0) {
			tParValue = Bank.formatCash (parValue);
		} else {
			tParValue = "NO PAR VALUE";
		}
		tReport = REPORT_PREFIX + name + " for " + companyAbbrev + " to " + tParValue;

		if (coordinates != GUI.NULL_STRING) {
			if (! coordinates.equals (GUI.EMPTY_STRING)) {
				tReport += " at coordinates " + coordinates;
			}
		}
		
		tReport += ".";
				
		return tReport;
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setCoordinates (String aCoordinates) {
		coordinates = aCoordinates;
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
		MarketCell tMarketCell;
		Market tMarket;
		MarketFrame tMarketFrame;
		GameManager tGameManager;

		tEffectApplied = false;
		tMarket = aRoundManager.getMarket ();
		if (actor.isAPlayer ()) {
			tShareCompany = aRoundManager.getShareCompany (companyAbbrev);
			aRoundManager.setParPrice (tShareCompany, parValue);
			tShareCompany.updateListeners (ShareCompany.SET_PAR_PRICE);
			tEffectApplied = true;
		} else if (actor.isAShareCompany ()) {
			tShareCompany = aRoundManager.getShareCompany (companyAbbrev);
			tGameManager = aRoundManager.getGameManager ();
			tMarketFrame = tGameManager.getMarketFrame ();
			tMarketCell = tMarket.getMarketCellAtCoordinates (coordinates);
			tShareCompany.setParPrice (parValue);
			tMarketFrame.setParPriceToMarketCell (tShareCompany, parValue, tMarketCell);
			if (parValue == 0) {
				tShareCompany.setNoPrice ();
			}
			tEffectApplied = true;
		} else {
			System.err.println ("Setting Par where Actor is not Player or Share Company");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		MarketCell tMarketCell;
		Market tMarket;

		tEffectUndone = false;
		tMarket = aRoundManager.getMarket ();
		if (actor.isAPlayer ()) {
			tMarketCell = tMarket.getMarketCellContainingToken (companyAbbrev);
			undoEffectAtMarketCell (tMarketCell);
			tEffectUndone = true;
		} else if (actor.isAShareCompany ()) {
			tMarketCell = tMarket.getMarketCellAtCoordinates (coordinates);
			undoEffectAtMarketCell (tMarketCell);
			tEffectUndone = true;
		} else {
			System.err.println ("Setting Par where Actor is not Player or Share Company");
		}

		return tEffectUndone;
	}

	public void undoEffectAtMarketCell (MarketCell aMarketCell) {
		ShareCompany tShareCompany;
		Token tToken;
		
		tToken = aMarketCell.getToken (companyAbbrev);
		tShareCompany = (ShareCompany) tToken.getWhichCompany ();
		tShareCompany.setNoPrice ();
		aMarketCell.redrawMarket ();
	}
}