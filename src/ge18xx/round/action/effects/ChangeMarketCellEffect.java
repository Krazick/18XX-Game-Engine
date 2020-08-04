package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TokenCompany;
import ge18xx.game.GameManager;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLDocument;

public class ChangeMarketCellEffect extends Effect {
	public final static String NAME = "Change Market Cell";
	final static AttributeName AN_START_LOCATION = new AttributeName ("startLocation");
	final static AttributeName AN_NEW_LOCATION = new AttributeName ("newLocation");
	final static AttributeName AN_START_COORDINATES = new AttributeName ("startCoordiantes");
	final static AttributeName AN_NEW_COORDINATES = new AttributeName ("newCoordinates");
	int startLocation;
	int newLocation;
	String startCellCoordinates;
	String newCellCoordinates;
	
	public ChangeMarketCellEffect () {
		super (NAME);
		setStartLocation (PlayerManager.NO_PLAYER_INDEX);
		setStartCellCoordinates (MarketCell.NO_COORDINATES);
		setNewCellCoordinates (MarketCell.NO_COORDINATES);
		setNewLocation (PlayerManager.NO_PLAYER_INDEX);
	}

	public ChangeMarketCellEffect (ActorI aActor, MarketCell aStartMarketCell, int aLocation, MarketCell aNewMarketCell, int aNewLocation) {
		super (NAME, aActor);
		
		String tStartCellCoordinates, tNewCellCoordinates;
		
		tStartCellCoordinates = aStartMarketCell.getCoordinates ();
		tNewCellCoordinates = aNewMarketCell.getCoordinates ();
		setStartLocation (aLocation);
		setStartCellCoordinates (tStartCellCoordinates);
		setNewCellCoordinates (tNewCellCoordinates);
	}

	public ChangeMarketCellEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		String tStartCellCoordinates, tNewCellCoordinates;
		int tStartLocation, tNewLocation;
		setName (NAME);
		
		tStartLocation = aEffectNode.getThisIntAttribute (AN_START_LOCATION);
		tNewLocation = aEffectNode.getThisIntAttribute (AN_NEW_LOCATION);
		tStartCellCoordinates = aEffectNode.getThisAttribute (AN_START_COORDINATES);
		tNewCellCoordinates = aEffectNode.getThisAttribute (AN_NEW_COORDINATES);
		setStartLocation (tStartLocation);
		setNewLocation (tNewLocation);
		setStartCellCoordinates (tStartCellCoordinates);
		setNewCellCoordinates (tNewCellCoordinates);		
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_START_LOCATION, startLocation);
		tEffectElement.setAttribute (AN_NEW_LOCATION, newLocation);
		tEffectElement.setAttribute (AN_START_COORDINATES, startCellCoordinates);
		tEffectElement.setAttribute (AN_NEW_COORDINATES, newCellCoordinates);
	
		return tEffectElement;
	}

	public int getStartLocation () {
		return startLocation;
	}
	
	public String getNewCellCoordinates () {
		return newCellCoordinates;
	}
	
	public String getStartCellCorodinates () {
		return startCellCoordinates;
	}
	
	public int getCellPrice (String aCellCoordinates, RoundManager aRoundManager) {
		MarketCell tCell;
		Market tMarket;
		int tPrice;
		
		tMarket = aRoundManager.getMarket ();
		tCell = tMarket.getMarketCellAtCoordinates (aCellCoordinates);
		tPrice = tCell.getValue ();
		
		return tPrice;
	}
	
	public int getNewCellPrice (RoundManager aRoundManager) {
		int tNewPrice;
		
		tNewPrice = getCellPrice (newCellCoordinates, aRoundManager);
		
		return tNewPrice;
	}
	
	public int getStartCellPrice (RoundManager aRoundManager) {
		int tStartPrice;
		
		tStartPrice = getCellPrice (startCellCoordinates, aRoundManager);
		
		return tStartPrice;
	}
	
	public String getEffectReport (RoundManager aRoundManager) {
		int tStartPrice;
		int tNewPrice;
		MarketCell tStartCell, tNewCell;
		Market tMarket;
		
		tMarket = aRoundManager.getMarket ();
		tStartCell = tMarket.getMarketCellAtCoordinates (startCellCoordinates);
		tNewCell = tMarket.getMarketCellAtCoordinates (newCellCoordinates);
		tStartPrice = tStartCell.getValue ();

		tNewPrice = tNewCell.getValue ();
		
		return (REPORT_PREFIX + name + " for " + actor.getName () + " from " + 
				startCellCoordinates + " (" + Bank.formatCash (tStartPrice) + ") location (" + startLocation + ") to " +
				newCellCoordinates + " (" + Bank.formatCash (tNewPrice) + ").");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setStartLocation (int aStartLocation) {
		startLocation = aStartLocation;
	}
	
	public void setNewLocation (int aNewLocation) {
		newLocation = aNewLocation;
	}
	
	public int getNewLocation () {
		return newLocation;
	}
	
	public void setNewCellCoordinates (String aNewCellCoordinates) {
		newCellCoordinates = aNewCellCoordinates;
	}
	
	public void setStartCellCoordinates (String aStartCellCoordinates) {
		startCellCoordinates = aStartCellCoordinates;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Market tMarket;
		MarketCell tStartCell, tNewCell;
		Token tToken;
		ShareCompany tShareCompany;
		String tCompanyAbbrev;
		
		tEffectApplied = true;
		tMarket = aRoundManager.getMarket ();
		tStartCell = tMarket.getMarketCellAtCoordinates (startCellCoordinates);
		tNewCell = tMarket.getMarketCellAtCoordinates (newCellCoordinates);
		if (tStartCell != tNewCell) {
			tShareCompany = (ShareCompany) actor;
			tCompanyAbbrev = tShareCompany.getAbbrev ();
			tShareCompany.setSharePrice (tNewCell);
			tToken = tStartCell.getToken (tCompanyAbbrev);
			if (tToken != TokenCompany.NO_TOKEN) {
				tNewCell.addTokenToLocation (newLocation, tToken);
			}
			tNewCell.redrawMarket ();
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Market tMarket;
		MarketCell tStartCell, tNewCell;
		Token tToken;
		ShareCompany tShareCompany;
		String tCompanyAbbrev;
		
		tEffectUndone = true;
		tMarket = aRoundManager.getMarket ();
		tStartCell = tMarket.getMarketCellAtCoordinates (startCellCoordinates);
		tNewCell = tMarket.getMarketCellAtCoordinates (newCellCoordinates);
		if (tStartCell != tNewCell) {
			tShareCompany = (ShareCompany) actor;
			tCompanyAbbrev = tShareCompany.getAbbrev ();
			tShareCompany.setSharePrice (tStartCell);
			tToken = tNewCell.getToken (tCompanyAbbrev);
			if (tToken != TokenCompany.NO_TOKEN) {
				tStartCell.addTokenToLocation (startLocation, tToken);
			}
			tStartCell.redrawMarket ();
		}

		return tEffectUndone;
	}
}
