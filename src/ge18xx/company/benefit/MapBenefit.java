package ge18xx.company.benefit;

import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import ge18xx.game.GameManager;
import ge18xx.map.GameMap;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.CloseCompanyAction;
import ge18xx.round.action.LayTokenAction;
import ge18xx.toplevel.MapFrame;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class MapBenefit extends Benefit {
	public static final AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
	public static final AttributeName AN_MAPCELL = new AttributeName ("mapCell");
	public static final AttributeName AN_COST = new AttributeName ("cost");
	public static final AttributeName AN_SAME_TURN = new AttributeName ("sameTurn");
	public static final String PORT_TOKEN = "Port";
	public static final String CATTLE_TOKEN = "Cattle";
	public static final String BRIDGE_TOKEN = "Bridge";
	public static final String TUNNEL_TOKEN = "Tunnel";
	public static final String NAME = "Map";
	public static final MapBenefit NO_MAP_BENEFIT = (MapBenefit) NO_BENEFIT;
	String mapCellID;
	int cost;
	boolean sameTurn;
	String tokenType;

	public MapBenefit (XMLNode aXMLNode) {
		super (aXMLNode);

		String tMapCellID;
		int tCost;
		boolean tSameTurn;
		String tTokenType;
		
		tTokenType = aXMLNode.getThisAttribute (AN_TOKEN_TYPE);
		tMapCellID = aXMLNode.getThisAttribute (AN_MAPCELL);
		tCost = aXMLNode.getThisIntAttribute (AN_COST);
		tSameTurn = aXMLNode.getThisBooleanAttribute (AN_SAME_TURN);
		setTokenType (tTokenType);
		setMapCellID (tMapCellID);
		setCost (tCost);
		setSameTurn (tSameTurn);
		setName (NAME);
	}
	
	public void setTokenType (String aTokenType) {
		tokenType = aTokenType;
	}

	public String getTokenType () {
		return tokenType;
	}

	public void setSameTurn (boolean aSameTurn) {
		sameTurn = aSameTurn;
	}

	public void setMapCellID (String aMapCellID) {
		mapCellID = aMapCellID;
	}

	public void setMapCellID (MapCell aMapCell) {
		String tMapCellID;
		
		tMapCellID = aMapCell.getCellID ();
		setMapCellID (tMapCellID);
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
		return GUI.NULL_STRING;
	}

	protected HexMap getMap () {
		MapFrame tMapFrame;
		HexMap tHexMap;

		tMapFrame = getMapFrame ();
		tHexMap = tMapFrame.getMap ();

		return tHexMap;
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
	
	protected void placeBenefitToken (MapCell aSelectedMapCell, String aTokenType, 
				MapBenefit aMapBenefit, int aBenefitValue) {
		MapFrame tMapFrame;
		
		tMapFrame = getMapFrame ();
		tMapFrame.placeBenefitToken (aSelectedMapCell, aTokenType, aMapBenefit, aBenefitValue);
		tMapFrame.resetAllModes ();
		tMapFrame.revalidate ();
		tMapFrame.repaint ();
	}
	
	protected boolean isTileAvailable () {
		HexMap tHexMap;
		MapCell tMapCell;
		boolean tTileIsAvailable;

		tHexMap = getMap ();
		tMapCell = getMapCell (tHexMap);
		tTileIsAvailable = tHexMap.isTileAvailableForMapCell (tMapCell);

		return tTileIsAvailable;
	}

	protected boolean hasTileWithTrack () {
		boolean tHasTileWithTrack;
		MapCell tMapCell;

		tHasTileWithTrack = false;
		tMapCell = getMapCell ();
		if (tMapCell.isTileOnCell ()) {
			if (tMapCell.isTileWithTrackOnCell ()) {
				tHasTileWithTrack = true;
			} else {
				tHasTileWithTrack = false;
			}
		} else {
			tHasTileWithTrack = false;
		}

		
		return tHasTileWithTrack;
	}
	
	protected boolean hasNonStartingTile () {
		boolean tHasNonStartingTile;
		MapCell tMapCell;

		tHasNonStartingTile = false;
		tMapCell = getMapCell ();
		if (tMapCell.isTileOnCell ()) {
			if (tMapCell.isStartingTile ()) {
				tHasNonStartingTile = false;
			} else {
				tHasNonStartingTile = true;
			}
		} else {
			tHasNonStartingTile = false;
		}

		return tHasNonStartingTile;
	}

	protected boolean hasTile () {
		boolean tHasTile;
		MapCell tMapCell;

		tHasTile = false;
		tMapCell = getMapCell ();
		if (tMapCell.isTileOnCell ()) {
			tHasTile = true;
		}

		return tHasTile;
	}

	protected MapCell getMapCell () {
		HexMap tHexMap;
		MapCell tMapCell;
		
		tHexMap = getMap ();
		tMapCell = getMapCell (tHexMap);
		
		return tMapCell;
	}
	
	protected MapCell getMapCell (HexMap aHexMap) {
		MapCell tMapCell;
		
		tMapCell = aHexMap.getMapCellForID (mapCellID);
		
		return tMapCell;
	}

	protected MapCell getSelectedMapCell () {
		GameMap tMap;
		MapCell tSelectedMapCell;
		
		tMap = getMap ();
		tSelectedMapCell = tMap.getSelectedMapCell ();
		
		return tSelectedMapCell;
	}

	public void resetBenefitInUse (Corporation aOwningCompany) {
		if (privateCompany != PrivateCompany.NO_PRIVATE_COMPANY) {
			aOwningCompany.setBenefitInUse (previousBenefitInUse);
		}
	}

	@Override
	public void completeBenefitInUse (Corporation aOwningCompany) {
		super.completeBenefitInUse (aOwningCompany);

		if (closeOnUse) {
			closePrivateCompany (aOwningCompany);
		} else if (closeOnAllUsed ()) {
			if (privateCompany.allMapBenefitsUsed ()) {
				closePrivateCompany (aOwningCompany);
			}
		}
		resetBenefitInUse (aOwningCompany);
		aOwningCompany.updateFrameInfo ();
	}

	private void closePrivateCompany (Corporation aOwningCompany) {
		CloseCompanyAction tCloseCompanyAction;
		GameManager tGameManager;

		tGameManager = privateCompany.getGameManager ();
		tCloseCompanyAction = createCloseCompanyAction (tGameManager, aOwningCompany);
		privateCompany.close (tCloseCompanyAction);
		addAdditionalEffects (tCloseCompanyAction);
		tGameManager.addAction (tCloseCompanyAction);
	}
	
	public void completeBenefitInUse (Corporation aOwningCompany, LayTokenAction aLayTokenAction) {
		super.completeBenefitInUse (aOwningCompany);
		
		if (closeOnUse) {
			privateCompany.close (aLayTokenAction);
			addAdditionalEffects (aLayTokenAction);
		}
		resetBenefitInUse (aOwningCompany);
		aOwningCompany.updateFrameInfo ();		
	}
	
	private CloseCompanyAction createCloseCompanyAction (GameManager aGameManager, Corporation aOwningCompany) {
		String tRoundID;
		CloseCompanyAction tCloseCompanyAction;
		
		tRoundID = aGameManager.getOperatingRoundID ();
		tCloseCompanyAction = new CloseCompanyAction (ActorI.ActionStates.OperatingRound, tRoundID, aOwningCompany);

		return tCloseCompanyAction;
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
		Benefit tMatchedBenefit;
		Benefit tMatchedNameBenefit;
		String tMapCellID;

		tMatchedBenefit = NO_BENEFIT;
		tMatchedNameBenefit = super.findMatchedBenefit (aBenefitNode);
		if (tMatchedNameBenefit != NO_BENEFIT) {
			tMapCellID = aBenefitNode.getThisAttribute (AN_MAPCELL);
			if (tMapCellID != GUI.NULL_STRING) {
				if (tMapCellID.equals (mapCellID)) {
					tMatchedBenefit = this;
				}
			}
		}

		return tMatchedBenefit;
	}
}
