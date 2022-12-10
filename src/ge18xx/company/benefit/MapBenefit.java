package ge18xx.company.benefit;

import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.CloseCompanyAction;
import ge18xx.toplevel.MapFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class MapBenefit extends Benefit {
	final static AttributeName AN_MAPCELL = new AttributeName ("mapCell");
	final static AttributeName AN_COST = new AttributeName ("cost");
	final static AttributeName AN_SAME_TURN = new AttributeName ("sameTurn");
	public final static String PORT_TOKEN = "port";
	public final static String CATTLE_TOKEN = "cattle";
	public final static String NAME = "Map";
	String mapCellID;
	int cost;
	boolean sameTurn;

	public MapBenefit (XMLNode aXMLNode) {
		super (aXMLNode);

		String tMapCellID;
		int tCost;
		boolean tSameTurn;

		tMapCellID = aXMLNode.getThisAttribute (AN_MAPCELL);
		tCost = aXMLNode.getThisIntAttribute (AN_COST);
		tSameTurn = aXMLNode.getThisBooleanAttribute (AN_SAME_TURN);
		setMapCellID (tMapCellID);
		setCost (tCost);
		setSameTurn (tSameTurn);
		setName (NAME);
	}

	public void setSameTurn (boolean aSameTurn) {
		sameTurn = aSameTurn;
	}

	public void setMapCellID (String aMapCellID) {
		mapCellID = aMapCellID;
	}

	public void setMapCellID (MapCell aMapCell) {
		setMapCellID (aMapCell.getCellID ());
	}
	
	public void setCost (int aCost) {
		cost = aCost;
	}

	public String getMapCellID () {
		return mapCellID;
	}

	@Override
	public int getCost () {
		return cost;
	}

	public boolean getSameTurn () {
		return sameTurn;
	}

	@Override
	public String getNewButtonLabel () {
		return null;
	}

	protected HexMap getMap () {
		MapFrame tMapFrame;
		HexMap tMap;

		tMapFrame = getMapFrame ();
		tMap = tMapFrame.getMap ();

		return tMap;
	}

	protected MapFrame getMapFrame () {
		GameManager tGameManager;
		MapFrame tMapFrame;

		tGameManager = privateCompany.getGameManager ();
		tMapFrame = tGameManager.getMapFrame ();

		return tMapFrame;
	}
	
	protected void revalidateMap () {
		MapFrame tMapFrame;

		tMapFrame = getMapFrame ();
		tMapFrame.revalidate ();
	}
	
	protected void placeBenefitToken (MapCell aSelectedMapCell, String aTokenType) {
		MapFrame tMapFrame;
		
		tMapFrame = getMapFrame ();
		tMapFrame.placeBenefitToken (aSelectedMapCell, aTokenType);
		tMapFrame.revalidate ();
	}
	
	protected boolean isTileAvailable () {
		HexMap tMap;
		MapCell tMapCell;
		boolean tTileIsAvailable;

		tMap = getMap ();
		tMapCell = getMapCell (tMap);
		tTileIsAvailable = tMap.isTileAvailableForMapCell (tMapCell);

		return tTileIsAvailable;
	}

	protected boolean hasTile () {
		boolean tHasTile = false;
		MapCell tMapCell;

		tMapCell = getMapCell ();
		if (tMapCell.isTileOnCell ()) {
			tHasTile = true;
		}

		return tHasTile;
	}

	protected MapCell getMapCell () {
		HexMap tMap;

		tMap = getMap ();

		return getMapCell (tMap);
	}

	protected MapCell getSelectedMapCell () {
		HexMap tMap;
		MapCell tSelectedMapCell;
		
		tMap = getMap ();
		tSelectedMapCell = tMap.getSelectedMapCell ();
		
		return tSelectedMapCell;
	}
	
	protected MapCell getMapCell (HexMap aMap) {
		return aMap.getMapCellForID (mapCellID);
	}

	public void resetBenefitInUse () {
		Corporation tOwningCompany;

		if (privateCompany != PrivateCompany.NO_PRIVATE_COMPANY) {
			tOwningCompany = (Corporation) privateCompany.getOwner ();
			tOwningCompany.setBenefitInUse (previousBenefitInUse);
		}
	}

	@Override
	public void completeBenefitInUse () {
		super.completeBenefitInUse ();

		Corporation tOwningCompany;
		CloseCompanyAction tCloseCompanyAction;
		GameManager tGameManager;
		RoundManager tRoundManager;
		String tRoundID;

		resetBenefitInUse ();
		tOwningCompany = (Corporation) privateCompany.getOwner ();
		if (closeOnUse) {
			tGameManager = privateCompany.getGameManager ();
			tRoundManager = tGameManager.getRoundManager ();
			tRoundID = tRoundManager.getOperatingRoundID ();
			tCloseCompanyAction = new CloseCompanyAction (ActorI.ActionStates.OperatingRound, tRoundID, tOwningCompany);
			privateCompany.close (tCloseCompanyAction);
			tRoundManager.addAction (tCloseCompanyAction);
		}
		tOwningCompany.updateFrameInfo ();
	}

	@Override
	protected XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLBenefitElement;

		tXMLBenefitElement = super.getCorporationStateElement (aXMLDocument);
		tXMLBenefitElement.setAttribute (AN_MAPCELL, mapCellID);

		return tXMLBenefitElement;
	}

	@Override
	protected Benefit findMatchedBenefit (XMLNode aBenefitNode) {
		Benefit tMatchedBenefit = NO_BENEFIT;
		Benefit tMatchedNameBenefit;
		String tMapCellID;

		tMatchedNameBenefit = super.findMatchedBenefit (aBenefitNode);
		if (tMatchedNameBenefit != NO_BENEFIT) {
			tMapCellID = aBenefitNode.getThisAttribute (AN_MAPCELL);
			if (tMapCellID != null) {
				if (tMapCellID.equals (mapCellID)) {
					tMatchedBenefit = this;
				}
			}
		}

		return tMatchedBenefit;
	}
}
