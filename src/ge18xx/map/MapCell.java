package ge18xx.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.NodeList;

import ge18xx.bank.Bank;

//
//  MapCell.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.center.Centers;
import ge18xx.center.City;
import ge18xx.center.CityInfo;
import ge18xx.center.PrivateRailwayCenter;
import ge18xx.center.RevenueCenter;
import ge18xx.center.RevenueCenterType;
import ge18xx.center.Town;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.License;
import ge18xx.company.License.LicenseTypes;
import ge18xx.company.LicenseToken;
import ge18xx.company.MapToken;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TokenCompany;
import ge18xx.company.TrainCompany;
import ge18xx.company.benefit.MapBenefit;
import ge18xx.round.action.CloseCompanyAction;
import ge18xx.tiles.Feature2;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileName;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.TileType;
import ge18xx.tiles.Track;
import ge18xx.tiles.Upgrade;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class MapCell implements Comparator<Object> {
	public static final AttributeName AN_REVENUE_CENTER_INDEX = new AttributeName ("revenueCenterIndex");
	public static final AttributeName AN_ORIENTATION = new AttributeName ("orientation");
	public static final AttributeName AN_MAP_CELL_ID = new AttributeName ("mapCellID");
	public static final AttributeName AN_STARTING = new AttributeName ("starting");
	public static final AttributeName AN_SIDE = new AttributeName ("side");
	public static final AttributeName AN_PORT_TOKEN = new AttributeName ("port");
	public static final AttributeName AN_CATTLE_TOKEN = new AttributeName ("cattle");
	public static final AttributeName AN_BRIDGE_TOKEN = new AttributeName ("bridge");
	public static final AttributeName AN_TUNNEL_TOKEN = new AttributeName ("tunnel");
	public static final AttributeName AN_BENEFIT_VALUE = new AttributeName ("benefitValue");
	public static final ElementName EN_BLOCKED = new ElementName ("Blocked");
	public static final ElementName EN_MAP_CELL = new ElementName ("MapCell");
	public static final String NO_ID = GUI.EMPTY_STRING;
	public static final MapCell NO_MAP_CELL = null;
	public static final MapCell [] [] NO_MAP_CELLS = null;
	public static final MapCell NO_DESTINATION = null;
	public static final int NO_ORIENTATION = 0;
	public static final int NO_ROTATION = -1;
	static final boolean NOT_ON_TILE = false;
	static final String NO_NAME = GUI.EMPTY_STRING;
	static final String NO_BLOCKED_SIDES = GUI.EMPTY_STRING;
	static final String NO_DIRECTION = null;
	static boolean mapDirection;
	boolean tileOrientLocked;
	boolean selected;
	boolean startingTile; // If the board has a initial tile placed, need to have terrain features show through.
	boolean allowedRotations [] = new boolean [6];
	boolean blockedSides [] = new boolean [6];
	int trainUsingSide [] = new int [6]; // Train Number using the side;
	MapCell neighbors [];
	int XCenter;
	int YCenter;
	int tileNumber;
	int tileOrient;
	int startingTileNumber;
	String id = "A1";
	Tile tile;
	TileName baseTileName;
	Rebate rebate;
	Centers centers;
	List<Terrain> endRoutes;
	Feature2 selectedFeature2;
	Terrain baseTerrain;
	Terrain terrain1;
	Terrain terrain2;
	Paint terrainFillPaint;
	int destinationCorpID;
	int benefitValue;
	boolean hasPortToken;
	boolean hasCattleToken;
	boolean hasBridgeToken;
	boolean hasTunnelToken;
	LicenseToken licenseToken;
	HexMap hexMap;

	public MapCell (HexMap aHexMap) {
		this (aHexMap, NO_DIRECTION);
	}

	public MapCell (HexMap aHexMap, String aMapDirection) {
		this (0, 0, aHexMap);
		setMapDirection (aMapDirection);
	}

	public MapCell (int Xc, int Yc, HexMap aHexMap) {
		this (Xc, Yc, aHexMap, Terrain.NO_TERRAIN, Tile.NO_TILE, NO_ORIENTATION, NO_NAME, NO_BLOCKED_SIDES);
	}

	public MapCell (int Xc, int Yc, HexMap aHexMap, int aBaseTerrain, Tile aTile, int aTileOrient, String aBaseName,
			String aBlockedSides) {
		setAllValues (Xc, Yc, aHexMap, aBaseTerrain, aTile, aTileOrient, aBaseName, aBlockedSides);
		setupLicenseToken ();
	}


	public boolean addRevenueCenter (int aType, int aID, int aLocation, String aName, int aValue, 
									TileType aTileType) {
		RevenueCenterType tRCType = new RevenueCenterType (aType);
		RevenueCenter tRC;
		Location tLocation2;
		boolean addOK;

		addOK = addRevenueCenter (setupRevenueCenter (tRCType, aID, aLocation, aName, aValue, aTileType));
		if (addOK) {
			if (tRCType.isTwoTowns ()) {
				tLocation2 = new Location (aLocation);
				tLocation2.rotateLocation180 ();
				tRC = setupRevenueCenter (tRCType, aID, tLocation2.getLocation (), aName, aValue, aTileType);
				addOK = addRevenueCenter (tRC);
			}
		}

		return addOK;
	}

	public boolean sameID (MapCell aMapCell) {
		boolean tSameID;

		tSameID = false;
		if (aMapCell != NO_MAP_CELL) {
			if (id.equals (aMapCell.getID ())) {
				tSameID = true;
			}
		}

		return tSameID;
	}

	public void setTrainUsingSide (int aSide, int aTrainIndex) {
		trainUsingSide [aSide] = aTrainIndex;
	}

	public void clearTrainUsingASide (int aSide) {
		setTrainUsingSide (aSide, 0);
	}

	/**
	 * Clear the Specified Train from all Sides of the Map Cell, if it matches
	 *
	 * @param aTrainNumber The Train Number to Clear
	 */
	public void clearTrainUsingSides (int aTrainNumber) {
		for (int tSideIndex = 0; tSideIndex < 6; tSideIndex++) {
			if (trainUsingSide [tSideIndex] == aTrainNumber) {
				clearTrainUsingASide (tSideIndex);
			}
		}
	}

	/**
	 * Clear ALL Trains from every side of the Map Cell
	 */
	public void clearAllTrainsUsingSides () {
		for (int tSideIndex = 0; tSideIndex < 6; tSideIndex++) {
			clearTrainUsingASide (tSideIndex);
		}
	}

	public void addEndRoute (Location aLocation) {
//		Terrain tNewEndRoute;

//		tNewEndRoute = new Terrain (Terrain.END_ROUTE, Terrain.NO_COST, aLocation);
//		endRoutes.add (tNewEndRoute);
	}

	public void clearAllEndRoutes () {
		endRoutes.clear ();
	}

	public void removeEndRoute (Location aLocation) {
		int tCount, tIndex;
		int tThisLocation, tFoundLocation;
		Terrain tEndRoute;

		tCount = endRoutes.size ();
		if (tCount > 0) {
			tThisLocation = aLocation.getLocation ();
			for (tIndex = 0; tIndex < tCount; tIndex++) {
				tEndRoute = endRoutes.get (tIndex);
				tFoundLocation = tEndRoute.getLocationToInt ();

				if (tThisLocation == tFoundLocation) {
					endRoutes.remove (tIndex);
					tIndex = tCount;
				}
			}
		}
	}

	public boolean isTrainUsingSide (int aSide) {
		boolean tIsTrainUsingSide = false;

		tIsTrainUsingSide = (trainUsingSide [aSide] > 0);

		return tIsTrainUsingSide;
	}

	public boolean addRevenueCenter (int aIndex, RevenueCenter aRC) {
		return centers.add (aIndex, aRC);
	}

	public boolean addRevenueCenter (RevenueCenter aRC) {
		return centers.add (aRC);
	}

	public void clearAllStation () {
		if (tile != Tile.NO_TILE) {
			tile.clearAllStations ();
		}
	}
	
	public int calculateSteps (int aPossible, Tile aTile, boolean aShiftDown) {
		boolean tCanAllTracksExit;
		int tTileOrient;
		int tSteps;
		int tIncrement;
		
		tSteps = 0;
		tCanAllTracksExit = false;
		if (aShiftDown) {
			tIncrement = 5;
		} else {
			tIncrement = 1;
		}
		for (tTileOrient = 0; ((!tCanAllTracksExit) && (tTileOrient < 6)); tTileOrient++) {
			aPossible = (aPossible + tIncrement) % 6;
			tSteps++;
			if (getAllowedRotation (aPossible)) {
				tCanAllTracksExit = canAllTracksExit (aTile, aPossible);
			}
		}
		
		return tSteps;
	}

	public boolean canAllTracksExit (Tile aTile, int aTileOrient) {
		return (aTile.canAllTracksExit (this, aTileOrient));
	}

	public boolean isNeighbor (MapCell tPossibleNeighbor) {
		boolean tIsNeighbor = false;

		if (tPossibleNeighbor != NO_MAP_CELL) {
			for (MapCell tNeighbor : neighbors) {
				if (tNeighbor == tPossibleNeighbor) {
					tIsNeighbor = true;
				}
			}
		}

		return tIsNeighbor;
	}

	// Get the Side of the this MapCell to this Neighbor
	public int getSideToNeighbor (MapCell aNeighborMapCell) {
		int tSideToNeighbor;

		tSideToNeighbor = Location.NO_LOCATION;
		for (int tSideIndex = 0; tSideIndex < 6; tSideIndex++) {
			if (neighbors [tSideIndex] == aNeighborMapCell) {
				tSideToNeighbor = tSideIndex;
			}
		}

		return tSideToNeighbor;
	}

	/**
	 * Find the side on the Neighbor MapCell that connects to this MapCell.
	 *
	 * @param aNeighborMapCell Find this Neighbor, if valid and return the
	 * Location of the side. Otherwise return NO_LOCATION;
	 *
	 * @return NO_LOCATION if the Neighbor is not Found for this MapCell
	 */
	public int getSideFromNeighbor (MapCell aNeighborMapCell) {
		int tSideFromNeighbor;

		tSideFromNeighbor = Location.NO_LOCATION;
		for (int tSideIndex = 0; tSideIndex < 6; tSideIndex++) {
			if (neighbors [tSideIndex] == aNeighborMapCell) {
				tSideFromNeighbor = (tSideIndex + 3) % 6;
			}
		}

		return tSideFromNeighbor;
	}

	// Does an Existing Tile on this MapCell have Track that is connected to the
	// Neighboring MapCell
	public boolean hasConnectingTrackTo (MapCell aNeighborMapCell) {
		boolean tHasConnectingTrackTo = false;
		boolean tMatchedNeighbors = false;
		boolean tMatchedTracksNeighbor, tMatchedTracks;
		int tSideToNeighbor, tSideFromNeighbor;

		tSideToNeighbor = getSideToNeighbor (aNeighborMapCell);
		tSideFromNeighbor = getSideFromNeighbor (aNeighborMapCell);
		if ((tSideToNeighbor != Location.NO_LOCATION) && (tSideFromNeighbor != Location.NO_LOCATION)) {
			tMatchedNeighbors = true;
		}
		if (tMatchedNeighbors) {
			tMatchedTracksNeighbor = false;
			tMatchedTracks = false;
			if (aNeighborMapCell.isTrackOnSide (tSideFromNeighbor)) {
				tMatchedTracksNeighbor = true;
			}
			if (isTrackOnSide (tSideToNeighbor)) {
				tMatchedTracks = true;
			}
			tHasConnectingTrackTo = tMatchedTracksNeighbor && tMatchedTracks;

		} else {
			System.err.println ("Failed to find matching Neighbors");
		}

		return tHasConnectingTrackTo;
	}

	public Track getTrackFromSide (int aSideLocation) {
		Track tTrack = Track.NO_TRACK;
		int tUnrotatedSideLocation;

		if (isTileOnCell ()) {
			tUnrotatedSideLocation = (aSideLocation + 6 - tileOrient) % 6;
			tTrack = tile.getTrackFromSide (tUnrotatedSideLocation);
		}

		return tTrack;
	}

	// Can a Tile added to this MapCell have Track to this side?
	public boolean canTrackToSide (int aSide) {
		boolean tCanTrackToSide;
		MapCell tNeighborMapCell;
		int tOtherSide;

		tCanTrackToSide = true;
		if (isBlockedSide (aSide)) {
			tCanTrackToSide = false;
		} else {
			tNeighborMapCell = getNeighbor (aSide);
			if (tNeighborMapCell != NO_MAP_CELL) {
				if (tNeighborMapCell.isTileOnCell ()) {
					tOtherSide = (aSide + 3) % 6;
					tCanTrackToSide = tNeighborMapCell.isTrackToSide (tOtherSide);
				} else {
					tCanTrackToSide = tNeighborMapCell.isSelectable ();
				}
			} else {
				tCanTrackToSide = false;
			}
		}

		return tCanTrackToSide;
	}

	public void clearCorporation () {
		centers.clearCorporation ();
	}

	public void clearCorporation (Corporation aCorporation) {
		Tile tTile;

		centers.clearCityInfoCorporation (aCorporation);
		if (isTileOnCell ()) {
			tTile = getTile ();
			tTile.clearCorporation (aCorporation);
		}
	}

	public void clearSelected () {
		selected = false;
		clearSelectedFeature2 ();
	}

	/**
	 * Clear the Specified Train from the Map Cell, if it has a Tile on it.
	 *
	 * @param aTrainNumber The Train Number to Clear
	 */
	public void clearTrain (int aTrainNumber) {
		if (isTileOnCell ()) {
			tile.clearTrain (aTrainNumber);
		}
	}

	/**
	 * Clear all Trains from the Map Cell, if it has a Tile on it.
	 *
	 */
	public void clearAllTrains () {
		if (isTileOnCell ()) {
			tile.clearAllTrains ();
		}
	}

	public int getTrackCountFromSide (Location aSideLocation) {
		Location tNewSideLocation;
		int tTrackCount;
		
		tNewSideLocation = aSideLocation.unrotateLocation (tileOrient);
		tTrackCount = tile.getTrackCountFromSide (tNewSideLocation);
		
		return tTrackCount;
	}
	
	public void clearSelectedFeature2 () {
		setSelectedFeature2 (new Location (Location.NO_LOCATION));
	}

	public void returnStation (TokenCompany aTokenCompany) {
		if (tile != Tile.NO_TILE) {
			tile.returnStation (aTokenCompany);
		}
	}

	public void clearStation (int aCorporationId) {
		if (tile != Tile.NO_TILE) {
			tile.clearStation (aCorporationId);
		}
	}

	public boolean containingPoint (Point2D.Double point, Hex hex) {
		return hex.contains (point, XCenter, YCenter);
	}

	public boolean containingPoint (Point point, Hex hex) {
		return hex.contains (point, XCenter, YCenter);
	}

	/* Generate XMLElement of all the Map Cell Information for saving to File */
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tTerrainElement;
		XMLElement tNameElement;
		XMLElement tTileElement;
		XMLElement tBlockedElement;
		int tIndex;

		tXMLElement = aXMLDocument.createElement (EN_MAP_CELL);
		tTerrainElement = baseTerrain.createElement (aXMLDocument);
		if (tTerrainElement != XMLElement.NO_XML_ELEMENT) {
			tTerrainElement.setAttribute (Terrain.AN_CATEGORY, "base");
			tXMLElement.appendChild (tTerrainElement);
		}
		if (terrain1 != Terrain.NO_TERRAINX) {
			tTerrainElement = terrain1.createElement (aXMLDocument);
			if (tTerrainElement != XMLElement.NO_XML_ELEMENT) {
				tTerrainElement.setAttribute (Terrain.AN_CATEGORY, "optional");
				tXMLElement.appendChild (tTerrainElement);
			}
		}
		if (terrain2 != Terrain.NO_TERRAINX) {
			tTerrainElement = terrain2.createElement (aXMLDocument);
			if (tTerrainElement != XMLElement.NO_XML_ELEMENT) {
				tTerrainElement.setAttribute (Terrain.AN_CATEGORY, "optional");
				tXMLElement.appendChild (tTerrainElement);
			}
		}
		if (baseTileName != TileName.NO_TILE_NAME) {
			tNameElement = new XMLElement (baseTileName.createElement (aXMLDocument).getElement ());
			if (tNameElement != XMLElement.NO_XML_ELEMENT) {
				tXMLElement.appendChild (tNameElement);
			}
		}

		centers.appendCenters (tXMLElement, aXMLDocument);

		for (tIndex = 0; tIndex < 6; tIndex++) {
			if (blockedSides [tIndex]) {
				tBlockedElement = aXMLDocument.createElement (EN_BLOCKED);
				tBlockedElement.setAttribute (AN_SIDE, tIndex);
				tXMLElement.appendChild (tBlockedElement);
			}
		}
		if (tile != Tile.NO_TILE) {
			tTileElement = aXMLDocument.createElement (Tile.EN_TILE);
			tTileElement.setAttribute (Tile.AN_NUMBER, tile.getNumber ());
			if (tileOrient > 0) {
				tTileElement.setAttribute (AN_ORIENTATION, tileOrient);
			}
			if (isStartingTile ()) {
				tTileElement.setAttribute (AN_STARTING, "TRUE");
			}
			tXMLElement.appendChild (tTileElement);
		}
		
		return tXMLElement;
	}

	public void drawTerrain (Graphics g, Terrain aTerrain, Hex aHex, int Xoffset, int Yoffset) {
		int Xol, Yol;
		Point tLocationPoint;
		Location tLocation;
		Font tCurrentFont;
		Font tNewFont;

		if (aTerrain != Terrain.NO_TERRAINX) {
			tLocation = aTerrain.getLocation ();

			if (tLocation == Location.NO_LOC) {
				Xol = XCenter + Xoffset;
				Yol = YCenter + Yoffset;
			} else {
				tLocationPoint = tLocation.calcCenter (aHex);
				Xol = tLocationPoint.x + XCenter + Xoffset;
				Yol = tLocationPoint.y + YCenter + Yoffset;
			}

			aTerrain.draw (g, Xol, Yol, aHex, terrainFillPaint, hasPortToken, hasCattleToken, 
							hasBridgeToken, hasTunnelToken, benefitValue);
			Yol += 15;
			if (aTerrain.isMountainous ()) {
				Yol += 5;
			}
			Xol -= 10;
			if (aTerrain.getCost () > 0) {
				tCurrentFont = g.getFont ();
				tNewFont = new Font ("Dialog", Font.PLAIN, 10);
				g.setFont (tNewFont);
				g.drawString (aTerrain.getCostToString (), Xol, Yol);
				g.setFont (tCurrentFont);
			}
		}
	}

	public boolean forID (String aID) {
		return (aID.equals (id));
	}

	public Terrain getBaseTerrain () {
		return baseTerrain;
	}

	public Paint getBaseTerrainFillPaint () {
		return baseTerrain.getPaint ();
	}

	public boolean canHoldCattleToken () {
		boolean tCanHoldCattleToken;
		
		// TODO Fix how to determine if a MapCell can hold a Cattle Token for 1870
		tCanHoldCattleToken = false;
		if (terrain1 != null) {
			if (terrain1.isCattle ()) {
				tCanHoldCattleToken = true;
			}
		} 
		if (terrain2 != null) {
			if (terrain2.isCattle ()) {
				tCanHoldCattleToken = true;
			}
		}
		
		return tCanHoldCattleToken;
	}

	public boolean canHoldPortToken () {
		boolean tCanHoldPortToken;
		
		tCanHoldPortToken = false;
		if (terrain1 != null) {
			if (terrain1.isPort ()) {
				tCanHoldPortToken = true;
			}
		} 
		if (terrain2 != null) {
			if (terrain2.isPort ()) {
				tCanHoldPortToken = true;
			}
		}
		
		return tCanHoldPortToken;
	}
	
	public void layBenefitToken (String aTokenType, int aBenefitValue) {
		setBenefitValue (aBenefitValue);
		if (aTokenType.equals (MapBenefit.PORT_TOKEN)) {
			layPortToken ();
		} else if (aTokenType.equals (MapBenefit.CATTLE_TOKEN)) {
			layCattleToken ();
		} else if (aTokenType.equals (MapBenefit.BRIDGE_TOKEN)) {
			layBridgeToken ();
		} else if (aTokenType.equals (MapBenefit.TUNNEL_TOKEN)) {
			layTunnelToken ();
		}
	}
	
	public void removeBenefitToken (String aTokenType) {
		if (aTokenType.equals (MapBenefit.PORT_TOKEN)) {
			removePortToken ();
		} else if (aTokenType.equals (MapBenefit.CATTLE_TOKEN)) {
			removeCattleToken ();
		} else if (aTokenType.equals (MapBenefit.BRIDGE_TOKEN)) {
			removeBridgeToken ();
		} else if (aTokenType.equals (MapBenefit.TUNNEL_TOKEN)) {
			removeTunnelToken ();
		}
	}
	
	public void setupLicenseToken () {
		License tLicense;
		
		tLicense = new License ();
		licenseToken = new LicenseToken (tLicense);
	}
	
	public void activateLicenseToken (License.LicenseTypes aLicenseType, int aPrice, int aBenefitValue) {
		License tLicense;
		
		tLicense = licenseToken.getLicense ();
		tLicense.setType (aLicenseType);
		tLicense.setPrice (aPrice);
		tLicense.setBenefitValue (aBenefitValue);
		licenseToken.setActive (true);
	}
	
	public void deactiveLicenseToken () {
		licenseToken.setActive (false);
	}
	
	public void setPortToken (boolean aPortToken) {
		hasPortToken = aPortToken;
	}
	
	public void setCattleToken (boolean aCattleToken) {
		hasCattleToken = aCattleToken;
	}
	
	public void setBridgeToken (boolean aBridgeToken) {
		hasBridgeToken = aBridgeToken;
	}
	
	public void setTunnelToken (boolean aTunnelToken) {
		hasTunnelToken = aTunnelToken;
	}
	
	public void layBridgeToken () {
		setBridgeToken (true);
	}
	
	public void removeBridgeToken () {
		setBridgeToken (false);
	}
	
	public void layTunnelToken () {
		setTunnelToken (true);
	}
	
	public void removeTunnelToken () {
		setTunnelToken (false);
	}

	public void layPortToken () {
		setPortToken (true);
	}
	
	public void removePortToken () {
		setPortToken (false);
	}
	
	public boolean hasPortToken () {
		return hasPortToken;
	}
	
	public void layCattleToken () {
		setCattleToken (true);
	}
	
	public void removeCattleToken () {
		setCattleToken (false);
	}
	
	public boolean hasBridgeToken () {
		return hasBridgeToken;
	}
	
	public boolean hasTunnelToken () {
		return hasTunnelToken;
	}
	
	public boolean hasCattleToken () {
		return hasCattleToken;
	}
	
	public RevenueCenter getCenterAtLocation (Location aLocation) {
		RevenueCenter tRevenueCenter;

		if (isTileOnCell ()) {
			tRevenueCenter = tile.getCenterAtLocation (aLocation);
		} else {
			tRevenueCenter = centers.getRevenueCenterAtLocation (aLocation);
		}

		return tRevenueCenter;
	}

	@Override
	public String toString () {
		return id;
	}
	
	public String getCellID () {
		return id;
	}

	public String getCityName () {
		return centers.getCityName ();
	}

	public String getID () {
		return id;
	}

	/*
	 * Generate XMLElement of current State of Cell, for all tiles, and Tokens for
	 * Saving
	 */
	public XMLElement getMapCellState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = null;
		if (isTileOnCell ()) {
			tXMLElement = aXMLDocument.createElement (EN_MAP_CELL);
			tXMLElement.setAttribute (Tile.AN_TILE_NUMBER, tile.getNumber ());
			tXMLElement.setAttribute (AN_ORIENTATION, tileOrient);
			if (hasPortToken) {
				tXMLElement.setAttribute (AN_PORT_TOKEN, true);
			}
			if (hasCattleToken) {
				tXMLElement.setAttribute (AN_CATTLE_TOKEN, true);
			}
			if (hasBridgeToken) {
				tXMLElement.setAttribute (AN_BRIDGE_TOKEN, true);
			}
			if (hasTunnelToken) {
				tXMLElement.setAttribute (AN_TUNNEL_TOKEN, true);
			}
			if (benefitValue > 0) {
				tXMLElement.setAttribute (AN_BENEFIT_VALUE, benefitValue);
			}
			if (tile.hasAnyStation ()) {
				tile.appendTokensState (aXMLDocument, tXMLElement);
			}
			if (tile.hasAnyCorporationBase ()) {
				tile.appendCorporationBases (aXMLDocument, tXMLElement);
			}
		}

		return tXMLElement;
	}

	public void loadBenefitStates (XMLNode aXMLNode) {
		boolean tHasPortToken;
		boolean tHasCattleToken;
		boolean tHasBridgeToken;
		boolean tHasTunnelToken;
		int tBenefitValue;
		
		tHasPortToken = aXMLNode.getThisBooleanAttribute (AN_PORT_TOKEN);
		tHasCattleToken = aXMLNode.getThisBooleanAttribute (AN_CATTLE_TOKEN);
		tHasBridgeToken = aXMLNode.getThisBooleanAttribute (AN_BRIDGE_TOKEN);
		tHasTunnelToken = aXMLNode.getThisBooleanAttribute (AN_TUNNEL_TOKEN);
		tBenefitValue = aXMLNode.getThisIntAttribute (AN_BENEFIT_VALUE);
		
		setPortToken (tHasPortToken);
		setCattleToken (tHasCattleToken);
		setBridgeToken (tHasBridgeToken);
		setTunnelToken (tHasTunnelToken);
		setBenefitValue (tBenefitValue);
	}
	
	public boolean getMapDirection () {
		return mapDirection;
	}

	public String getName () {
		return baseTileName.getName ();
	}

	/**
	 * Get the MapCell that is the Neighbor to for this MapCell on the specified side.
	 *
	 * @param aSide int value of the Location (must be between 0 and 5
	 *
	 * @return NO_MAP_CELL if not a valid side, otherwise the neighbor MapCell.
	 * Also note, if the MapCell has no neighbor on that side, it also returns NO_MAP_CELL
	 *
	 */
	public MapCell getNeighbor (int aSide) {
		MapCell tNeighbor;

		if ((aSide < 0) || (aSide > 5)) {
			tNeighbor = NO_MAP_CELL;
		} else {
			tNeighbor = neighbors [aSide];
		}

		return tNeighbor;
	}

	/**
	 * Get the MapCell that is the Neighbor to for this MapCell on the specified Location
	 *
	 * @param aLocation the Location which must be a Side, fetch the Neighbor
	 *
	 * @return NO_MAP_CELL if not a valid side, otherwise the neighbor MapCell.
	 * Also note, if the MapCell has no neighbor on that side, it also returns NO_MAP_CELL
	 *
	 */
	public MapCell getNeighbor (Location aLocation) {
		MapCell tNeighbor;

		tNeighbor = NO_MAP_CELL;
		if (aLocation.isSide ()) {
			tNeighbor = getNeighbor (aLocation.getLocation ());
		}

		return tNeighbor;
	}

	public RevenueCenter getRCContainingPoint (Point aPoint, Hex aHex) {
		RevenueCenter tRevenueCenter;

		tRevenueCenter = tile.getRCContainingPoint (aPoint, aHex, XCenter, YCenter, tileOrient);

		return tRevenueCenter;
	}

	public RevenueCenter getRevenueCenter (int aCenterIndex) {
		RevenueCenter tFoundRevenueCenter;
		
		tFoundRevenueCenter = RevenueCenter.NO_CENTER;
		if (isTileOnCell ()) {
			tFoundRevenueCenter = tile.getCenterAt (aCenterIndex);
		} else {
			tFoundRevenueCenter = centers.get (aCenterIndex);
		}

		return tFoundRevenueCenter;
	}

	public RevenueCenter getRevenueCenterAt (Location aLocation) {
		RevenueCenter tFoundRevenueCenter;

		tFoundRevenueCenter = RevenueCenter.NO_CENTER;
		if (isTileOnCell ()) {
			tFoundRevenueCenter = tile.getCenterAtLocation (aLocation);
		} else {
			tFoundRevenueCenter = centers.getRevenueCenterAtLocation (aLocation);
		}

		return tFoundRevenueCenter;
	}

	public int getRevenueCenterCount () {
		int tRevenueCenterCount;
		
		if (isTileOnCell ()) {
			tRevenueCenterCount = tile.getCenterCount ();
		} else {
			tRevenueCenterCount = centers.size ();
		}
		
		return tRevenueCenterCount;
	}

	public int getRevenueCenterID () {
		return centers.getRevenueCenterID ();
	}

	public int getRevenueCenterIndex (int aCorporationID) {
		return centers.getStationIndex (aCorporationID);
	}

	public Location getSelectedFeature2Location () {
		if (selectedFeature2 != Feature2.NO_FEATURE2) {
			return selectedFeature2.getLocation ();
		} else {
			return new Location (Location.NO_LOCATION);
		}
	}

	public Location getSelectedFeature2Location2 () {
		if (selectedFeature2 != Feature2.NO_FEATURE2) {
			return selectedFeature2.getLocation2 ();
		} else {
			return new Location (Location.NO_LOCATION);
		}
	}

	public RevenueCenter getSelectedRevenueCenter () {
		RevenueCenter tRevenueCenter;

		if (isTileOnCell ()) {
			tRevenueCenter = tile.getSelectedRevenueCenter (selectedFeature2, tileOrient);
		} else {
			tRevenueCenter = RevenueCenter.NO_CENTER;
		}

		return tRevenueCenter;
	}

	public Terrain getTerrain1 () {
		return terrain1;
	}

	public Terrain getTerrain2 () {
		return terrain2;
	}

	public Tile getTile () {
		return tile;
	}

	public String getTileInfo () {
		String tTileInfo;

		if (isTileOnCell ()) {
			tTileInfo = " Tile #" + getTileNumber ();
		} else {
			tTileInfo = " NO TILE";
		}

		return tTileInfo;
	}

	public int getTileNumber () {
		if (tile == Tile.NO_TILE) {
			return tileNumber;
		} else {
			return tile.getNumber ();
		}
	}

	public int getTileOrient () {
		return tileOrient;
	}

	public String getToolTip () {
		String tTip;
		String tTileName;
		String tBenefitValue;
		String tCost;
		int tTerrainCost;
		int tCurrentPhase;
		Corporation tDestinationCorporation;

		if (baseTerrain.isSelectable ()) {
			tTip = "<html>Cell <b>" + getCellID () + "</b><br>";
			if (tile != Tile.NO_TILE) {
				tCurrentPhase = hexMap.getCurrentPhase () + 1;
				tTip += "Tile Orientation: " + tileOrient + "<br>";
				tTip += tile.getToolTip (tCurrentPhase);
			} else {
				if (baseTileName != TileName.NO_TILE_NAME) {
					tTileName = baseTileName.getName ();
					if (tTileName.length () > 0) {
						tTip += "Tile Name: " + baseTileName.getName () + "<br>";
					}
				}
 				if (centers.size () > 0) {
					tTip += centers.getToolTip ();
				}
			}
			tTip = tTip + "Base: " + baseTerrain.getName () + "<br>";
			if (terrain1 != Terrain.NO_TERRAINX) {
				tTip += "Terain: " + terrain1.getName ();
				tTerrainCost = terrain1.getCost ();
				if (terrain2 != Terrain.NO_TERRAINX) {
					tTip += " & " + terrain2.getName ();
					tTerrainCost += terrain2.getCost ();
				}
				tTip += "<br>";
				if (tTerrainCost > 0) {
					tCost = Bank.formatCash (tTerrainCost);
					tTip += "Build Cost: " + tCost + "<br>";
				}
			}
			tBenefitValue = Bank.formatCash (benefitValue);
			if (hasPortToken) {
				tTip += "Port: Open " + tBenefitValue + "<br>";
			}
			if (hasCattleToken) {
				tTip += "Cattle: Active " + tBenefitValue +"<br>";
			}
			if (hasBridgeToken) {
				tTip += "Bridge: Active " + tBenefitValue + "<br>";
			}
			if (hasTunnelToken) {
				tTip += "Tunnel: Active " + tBenefitValue + "<br>";
			}
			if (rebate != Rebate.NO_REBATE) {
				tTip += "Rebate: " + rebate.getFormattedAmount () + "<br>";
			}
			if (destinationCorpID != Corporation.NO_ID) {
				tDestinationCorporation = getCorporationByID (destinationCorpID);
				if (tDestinationCorporation != Corporation.NO_CORPORATION) {
					tTip += "Destination: " + tDestinationCorporation.getAbbrev ();
				}
			}
			tTip += "</html>";
		} else {
			tTip = null;
		}

		return tTip;
	}

	public TokenCompany getTokenCompanyByID (int aCorpID) {
		Corporation tCorporation;
		TokenCompany tTokenCompany;
		
		tCorporation = hexMap.getCorporationByID (aCorpID);
		if (tCorporation.isATokenCompany ()) {
			tTokenCompany = (TokenCompany) tCorporation;
		} else {
			tTokenCompany = TokenCompany.NO_TOKEN_COMPANY;
		}
		
		return tTokenCompany;
	}

	public TokenCompany getTokenCompany (String aAbbrev) {
		return hexMap.getTokenCompany (aAbbrev);
	}

	public int getTypeCount () {
		return centers.getTypeCount ();
	}

	public int getXCenter () {
		return XCenter;
	}

	public int getYCenter () {
		return YCenter;
	}

	public void handleSelectRevenueCenter (Point aPoint) {
		Location tSelectedLocation;
		RevenueCenter tSelectedRevenueCenter;
		int tTileOrient;

		if (!isSelected ()) {
			hexMap.toggleSelectedMapCell (this);
		}
		if (isTileOnCell ()) {
			tSelectedRevenueCenter = getRCContainingPoint (aPoint, hexMap.hex);
			if (tSelectedRevenueCenter != RevenueCenter.NO_CENTER) {
				tSelectedLocation = tSelectedRevenueCenter.getLocation ();
				tTileOrient = getTileOrient ();
				tSelectedLocation = tSelectedLocation.rotateLocation (tTileOrient);
				setSelectedFeature2 (tSelectedLocation);
			}
		} else {
			hexMap.toggleSelectedMapCell (this);
		}
	}

	public boolean hasStation (Token aToken) {
		if (tile != Tile.NO_TILE) {
			return tile.hasStation (aToken);
		} else {
			return false;
		}
	}

	/**
	 * Determine if this MapCell contains a Station for the Specified Corp ID, return True if so
	 *
	 * @param aCorpID The Corporation ID to find.
	 *
	 * @return TRUE if there is a Station that has the Corporation ID on the MapCell OR FALSE
	 *
	 */
	public boolean hasStation (int aCorpID) {
		if (tile != Tile.NO_TILE) {
			return tile.hasStation (aCorpID);
		} else {
			return false;
		}
	}

	public Location getLocationWithStation (int aCorpID) {
		Location tLocationWithStation;

		tLocationWithStation = Location.NO_LOC;
		if (tile != Tile.NO_TILE) {
			tLocationWithStation = tile.getLocationWithStation (aCorpID, tileOrient);
		}

		return tLocationWithStation;
	}

	public boolean haveLaidBaseTokenFor (Corporation aCorporation) {
		return hasStation (aCorporation.getID ());
	}

	public boolean isBlockedSide (int aSide) {
		if ((aSide < 0) || (aSide > 5)) {
			return true;
		} else {
			return blockedSides [aSide];
		}
	}

	public boolean isSelected () {
		return selected;
	}

	public boolean isSelectable () {
		return baseTerrain.isSelectable ();
	}

	public boolean isStartingTile () {
		return (tileNumber == startingTileNumber);
	}

	public boolean isTileOrientationLocked () {
		return tileOrientLocked;
	}

	public boolean isTileOnCell () {
		if (tile == Tile.NO_TILE) {
			if (tileNumber == 0) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean isTrackOnSide (int aSide) {
		boolean tIsTrackOnSide = false;
		int tUnrotatedSide;

		if (isTileOnCell ()) {
			if (tile != Tile.NO_TILE) {
				tUnrotatedSide = (aSide - tileOrient + 6) % 6;
				tIsTrackOnSide = tile.isTrackOnSide (tUnrotatedSide);
			}
		}

		return tIsTrackOnSide;
	}

	public boolean isTrackToSide (int aSide) {
		boolean tIsTrackToSide = false;
		int tUnrotatedSide;

		if (isTileOnCell ()) {
			if (tile != Tile.NO_TILE) {
				if (tile.canDeadEndTrack ()) {
					tIsTrackToSide = true;
				} else {
					tUnrotatedSide = (aSide - tileOrient + 6) % 6;
					tIsTrackToSide = tile.isTrackToSide (tUnrotatedSide);
				}
			}
		}

		return tIsTrackToSide;
	}

	public void loadBaseStates (XMLNode aMapCellNode) {
		if (isTileOnCell ()) {
			tile.loadBaseStates (aMapCellNode);
		}
	}

	public void loadStationsStates (XMLNode aMapCellNode) {
		if (isTileOnCell ()) {
			tile.loadStationsStates (aMapCellNode);
		}
		loadBenefitStates (aMapCellNode);
	}

	public void setID (String aID) {
		id = aID;
	}

	public void loadXMLCell (XMLNode aCellNode, int aTerrainCost[], int aTerrainType[], String aID) {
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		String tCategory;
		String tRCType;
		Terrain tTerrain;
		RevenueCenter tRevenueCenter;
		int tChildrenCount;
		int tChildrenIndex;
		int tTerrainIndex;
		int tTerrainType;
		int tTileNumber;
		int tOrientation;
		int tSide;
		boolean tStarting;

		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tile = Tile.NO_TILE;
		rebate = Rebate.NO_REBATE;
		setID (aID);
		for (tChildrenIndex = 0; tChildrenIndex < tChildrenCount; tChildrenIndex++) {
			tChildNode = new XMLNode (tChildren.item (tChildrenIndex));
			tChildName = tChildNode.getNodeName ();
			if (Terrain.EN_TERRAIN.equals (tChildName)) {
				tTerrain = new Terrain (tChildNode);
				if (tTerrain != Terrain.NO_TERRAINX) {
					tTerrainType = tTerrain.getTerrain ();
					for (tTerrainIndex = 0; tTerrainIndex < 15; tTerrainIndex++) {
						if (aTerrainType [tTerrainIndex] == tTerrainType) {
							tTerrain.setCost (aTerrainCost [tTerrainIndex]);
						}
					}
				}
				tCategory = tTerrain.getCategory (tChildNode);
				if (Terrain.AN_BASE.equals (tCategory)) {
					setBaseTerrain (tTerrain);
				} else if (Terrain.AN_OPTIONAL.equals (tCategory)) {
					if (terrain1 == Terrain.NO_TERRAINX) {
						terrain1 = tTerrain;
					} else if (terrain2 == Terrain.NO_TERRAINX) {
						terrain2 = tTerrain;
					}
				}
			} else if (RevenueCenter.EN_REVENUE_CENTER.equals (tChildName)) {
				tRCType = tChildNode.getThisAttribute (RevenueCenter.AN_TYPE);
				if (RevenueCenterType.isTown (tRCType)) {
					tRevenueCenter = new Town (tChildNode);
				} else if (RevenueCenterType.isCity (tRCType)) {
					tRevenueCenter = new City (tChildNode);
				} else {
					tRevenueCenter = new PrivateRailwayCenter (tChildNode);
				}
				tRevenueCenter.setMapCell (this);
				centers.add (tRevenueCenter);
			} else if (TileName.EN_TILE_NAME.equals (tChildName)) {
				baseTileName = new TileName (tChildNode);
			} else if (Tile.EN_TILE.equals (tChildName)) {
				tTileNumber = tChildNode.getThisIntAttribute (Tile.AN_NUMBER);
				tOrientation = tChildNode.getThisIntAttribute (AN_ORIENTATION);
				tStarting = tChildNode.getThisBooleanAttribute (AN_STARTING);
				setTileInfo (tTileNumber, tOrientation, tStarting);
			} else if (EN_BLOCKED.equals (tChildName)) {
				tSide = tChildNode.getThisIntAttribute (AN_SIDE);
				blockedSides [tSide] = true;
			} else if (Rebate.EN_REBATE.equals (tChildName)) {
				rebate = new Rebate (tChildNode);
			}
		}
	}

	public void lockTileOrientation () {
		tileOrientLocked = true;
	}

	// To Place a Tile, determine if there is a tile already on the Map Cell, if so,
	// and it is there
	// Do a Upgrade Tile, Otherwise,
	// Copy Corporation Bases Corporation Destinations and Tokens from prior
	// placement to new Tile
	// Set the Tile down, and set orientation lock as false so it can be rotated

	public void placeTile (TileSet aTileSet, Tile aTile) {
		Tile tNewTile;

		if ((isTileOnCell ()) && (getTile () != Tile.NO_TILE)) {
			upgradeTile (aTileSet, aTile);
		} else {
			tNewTile = new Tile (aTile);
			// Copy over Corporation Bases, and Corporation Destinations
			tNewTile.setRevenueCenters (centers);
			tNewTile.setMapCell (this);
			setTile (tNewTile);
			setTileOrientationLocked (false);
		}
	}

	public void printfulllog () {
		printlog ();
		tile.printlog ();
		System.out.print ("MapCell ");
		centers.printlog ();
	}

	public void printlog () {
		System.out.println ("Map Cell " + id);		// PRINTLOG
	}

	public void setTile (Tile aTile) {
		tile = aTile;
	}

	public void setTileOrientationLocked (boolean aTileOrientLocked) {
		tileOrientLocked = aTileOrientLocked;
	}

//	public void putTile (Tile aTile, int aTileOrient, boolean aStarting) {
//		putTile (aTile, aTileOrient);
//		setStartingTile (aStarting);
//	}

	public void putTile (Tile aTile, int aTileOrient) {
		int tNewTileNumber;

		if (centers != Centers.NO_CENTERS) {
			if (aTile != Tile.NO_TILE) {
				centers.copyCityInfo (aTile);
				aTile.setMapCell (this);
			}
		}

		if (aTile == Tile.NO_TILE) {
			tNewTileNumber = tileNumber;
		} else {
			tNewTileNumber = aTile.getNumber ();
		}
		setTile (aTile);
		setTileOrientationLocked (false);
		setTileInfo (tNewTileNumber, aTileOrient, false);
	}

	public void setTileInfo (int aTileNumber, int aTileOrient, boolean aStarting) {
		setTileNumber (aTileNumber);
		setTileOrientation (aTileOrient);
		setStartingTile (aStarting);
	}

	public void setTileNumber (int aTileNumber) {
		tileNumber = aTileNumber;
	}

	public void setTileOrientation (int aTileOrient) {
		tileOrient = aTileOrient;
	}

	public boolean canPlaceTile (TileSet aTileSet) {
		boolean tCanPlaceTile = true;
		GameTile tSelectedTile;
		Tile tNewTile;

		tSelectedTile = aTileSet.getSelectedTile ();
		if (tSelectedTile != GameTile.NO_GAME_TILE) {
			if (isTileOnCell ()) {
				tNewTile = tSelectedTile.getTile ();
				tCanPlaceTile = anyAllowedRotation (aTileSet, tNewTile);
			}
		}

		return tCanPlaceTile;
	}

	public boolean putTileDown (TileSet aTileSet) {
		GameTile tSelectedTile;
		boolean tTilePlaced = false;

		tSelectedTile = aTileSet.getSelectedTile ();
		if (tSelectedTile != GameTile.NO_GAME_TILE) {
			tTilePlaced = putThisTileDown (aTileSet, tSelectedTile, NO_ROTATION);
		} else {
			System.err.println ("Put Tile Down Button Selected, no Tile Selected From Tray");
		}

		return tTilePlaced;
	}

	public boolean putThisTileDown (TileSet aTileSet, GameTile aThisTile, int aThisRotation) {
		GameTile tGameTileOnMapCell;
		Tile tTile;
		Tile tClonedTile;
		Tile tTileOnMapCell;
		int tUpgradeCount;
		int tTileNumber;
		int tPossibleOrientation;
		boolean tTilePlaced;

		tTilePlaced = false;
		if (isTileOnCell ()) {
			// Tile on MapCell -- Upgrade Required
			tTileOnMapCell = getTile ();
			tTileNumber = tTileOnMapCell.getNumber ();
			tGameTileOnMapCell = aTileSet.getGameTile (tTileNumber);
			tUpgradeCount = tGameTileOnMapCell.getUpgradeCount ();
			if (tUpgradeCount > 0) {
				tTile = aThisTile.popTile ();
				tClonedTile = tTile.clone ();
				tTilePlaced = upgradeTile (aTileSet, tClonedTile);
				hexMap.redrawMap ();
			} else {
				System.err.println ("No Upgrades Available");
			}
		} else {
			// No Tile on Map Cell Straight Forward placement
			
			if (sameTypeCount (aThisTile) || aThisTile.canOverride ()) {
				// Have a Tile, and a MapCell Selected with same Revenue Center Types
				tTile = aThisTile.popTile ();
				if (aThisRotation != NO_ROTATION) {
					tPossibleOrientation = aThisRotation;
				} else {
					tPossibleOrientation = getAllAllowedRotations (tTile);
				}
				if (tPossibleOrientation == NO_ROTATION) {
					System.err.println ("Tile has no orientation possible");
					aThisTile.pushTile (tTile);
				} else {
					// Found at least one orientation that works - Put it on the Map Cell
					putTile (tTile, tPossibleOrientation);
					tTilePlaced = true;
					hexMap.redrawMap ();
				}
				aThisTile.toggleSelected ();
				aTileSet.revalidate ();
				aTileSet.repaint ();
			} else {
				// Count of Revenue Types don't match - Can't place
				System.err.println ("Different Type Counts between Tiles");
			}
		}

		return tTilePlaced;
	}

	public boolean areLocationsConnected (Location aLocation, int aRemoteLocationIndex) {
		boolean tIsConnectedToLocation = false;

		if (isTileOnCell ()) {
			tIsConnectedToLocation = tile.areLocationsConnected (aLocation, aRemoteLocationIndex);
		}

		return tIsConnectedToLocation;
	}

	public int getAllAllowedRotations (Tile aTile) {
		int tPossibleOrientation;
		int tTileOrient;
		boolean tCanAllTracksExit;

		setAllRotations (true);
		tPossibleOrientation = NO_ROTATION;
		// Get Allowed Orientations for placement.
		for (tTileOrient = 0; tTileOrient < 6; tTileOrient++) {
			if (getAllowedRotation (tTileOrient)) {
				tCanAllTracksExit = canAllTracksExit (aTile, tTileOrient);
				setAllowedRotation (tTileOrient, tCanAllTracksExit);
				if (tCanAllTracksExit) {
					if (tPossibleOrientation == NO_ROTATION) {
						tPossibleOrientation = tTileOrient;
					}
				}
			}
		}

		return tPossibleOrientation;
	}

	public boolean sameTypeCount (GameTile aGameTile) {
		boolean tSameTypeCount;
		int tGameTypeCount;
		int tMapCellTypeCount;
		
		tGameTypeCount = aGameTile.getTypeCount ();
		tMapCellTypeCount = getTypeCount ();

//		tSameTypeCount = (aGameTile.getTypeCount () == getTypeCount ());
		tSameTypeCount = (tGameTypeCount == tMapCellTypeCount);
		
		return tSameTypeCount;
	}

	public void rotateTileLeft (int aSteps) {
		if (isTileOnCell ()) {
			if (!tileOrientLocked) {
				if (aSteps > 0) {
					setTileOrient ((tileOrient - aSteps + 6) % 6);
				}
			} else {
				System.err.println ("The Tile Orientation is Locked on MapCell " + getID ());
			}
		} else {
			System.err.println ("No Tile found on this Map Cell " + getID ());
		}
	}

	public void rotateTileRight (int aSteps) {
		if (isTileOnCell ()) {
			if (!tileOrientLocked) {
				if (aSteps > 0) {
					setTileOrient ((tileOrient + aSteps) % 6);
				}
			} else {
				System.err.println ("The Tile Orientation is Locked on MapCell " + getID ());
			}
		} else {
			System.err.println ("No Tile found on this Map Cell " + getID ());
		}
	}

	public void setTileOrient (int aNewTileOrientation) {
		tileOrient = aNewTileOrientation;
	}

	public void rotateTileLeft () {
		rotateTileLeft (1);
	}

	public void rotateTileRight () {
		rotateTileRight (1);
	}

	public void removeTile () {
		setTileOrientation (NO_ORIENTATION);
		setTileNumber (0);
		setTile (Tile.NO_TILE);
	}

	public boolean pseudoYellowTile () {
		boolean tPseudoYellowTile = false;

		if ("OO".equals (baseTileName.getName ())) {
			tPseudoYellowTile = true;
		}

		return tPseudoYellowTile;
	}

	public void paintComponent (Graphics aGraphics, Hex aHex) {
		boolean tIsInSelectable;
		RevenueCenter tRevenueCenter;
		Paint tThickFrame;
		String tTileName;
		String tBaseTileName;
		String tCityInfoName;
		int tXoffset;
		int tYoffset;
		
		tXoffset = 0;
		tYoffset = 0;
		Paint tFillPaint;
		tTileName = TileName.NO_NAME2;
		
		tRevenueCenter = getRevenueCenter (0);
		tIsInSelectable = hexMap.mapCellIsInSelectableSMC (this) || selected;
		if (isTileOnCell ()) {
			tTileName = tile.getName ();
			tile.paintComponent (aGraphics, XCenter, YCenter, tileOrient, aHex, selectedFeature2, tIsInSelectable);
			if (blockedSides != null) {
				aHex.drawBorders (aGraphics, XCenter, YCenter, baseTerrain.drawBorder (), blockedSides);
			}
			if (isStartingTile ()) {
				tYoffset = drawTerrain1 (aGraphics, aHex, tRevenueCenter, tXoffset, tYoffset);
				drawTerrain2 (aGraphics, aHex, tXoffset, tYoffset);
			} else {
				drawTerrainBleedThrough (aGraphics, terrain1, aHex, tXoffset, tYoffset);
				drawTerrainBleedThrough (aGraphics, terrain2, aHex, tXoffset, tYoffset);
			}
			if (endRoutes.size () > 0) {
				for (Terrain tEndRoute : endRoutes) {
					drawTerrain (aGraphics, tEndRoute, aHex, tXoffset, tYoffset);
				}
			}
		} else {
			if (pseudoYellowTile ()) {
				tThickFrame = new TileType (TileType.YELLOW, false).getPaint ();
			} else {
				tThickFrame = null;
			}
			if (tIsInSelectable) {
				tFillPaint = baseTerrain.getPaint (true);
			} else {
				tFillPaint = baseTerrain.getPaint ();
			}
			aHex.paintHex (aGraphics, XCenter, YCenter, tFillPaint, baseTerrain.drawBorder (), tThickFrame,
					blockedSides);
			tYoffset = drawTerrain1 (aGraphics, aHex, tRevenueCenter, tXoffset, tYoffset);
			drawTerrain2 (aGraphics, aHex, tXoffset, tYoffset);
			centers.draw (aGraphics, XCenter, YCenter, aHex, NOT_ON_TILE, selectedFeature2);
			if (rebate != Rebate.NO_REBATE) {
				rebate.draw (aGraphics, XCenter, YCenter, aHex);
			}
		}
		if (baseTileName != TileName.NO_TILE_NAME) {
			tBaseTileName = baseTileName.getName ();
			if (tRevenueCenter != RevenueCenter.NO_CENTER) {
				tCityInfoName = tRevenueCenter.getCIName ();
				if (!tBaseTileName.equalsIgnoreCase (tCityInfoName)) {
					if (!tTileName.equalsIgnoreCase (tBaseTileName)) {
						baseTileName.draw (aGraphics, XCenter, YCenter, aHex);
					}
				}
				if (!tTileName.equalsIgnoreCase (tCityInfoName)) {
					tRevenueCenter.drawName (aGraphics, XCenter, YCenter, aHex);
				}
			}
		}

		if (selected) {
			aHex.paintSelected (aGraphics, XCenter, YCenter);
		}

		paintNeighbors (aGraphics, aHex);
	}

	private void paintNeighbors (Graphics aGraphics, Hex aHex) {
		int tNIndex;
		for (tNIndex = 0; tNIndex < 6; tNIndex++) {
			if (neighbors [tNIndex] != null) {
				if (neighbors [tNIndex].isSelected ()) {
					paintAsNeighbor (aGraphics, aHex, tNIndex);
				}
			}
		}
	}
	
	public void paintAsNeighbor (Graphics aGraphics, Hex aHex, int aSide) {
		aHex.drawNeighbor (aGraphics, aSide, XCenter, YCenter);
	}

	private int drawTerrain1 (Graphics aGraphics, Hex aHex, RevenueCenter aRevenueCenter, int aXoffset, int aYoffset) {
		if (aRevenueCenter != RevenueCenter.NO_CENTER) {
			if (terrain1 != Terrain.NO_TERRAINX) {
				if (terrain1.isRiver ()) {
					if (aRevenueCenter.isCenterLocation ()) {
						aYoffset = aHex.getTrackWidth () * 4;
					}
				}
			}
		}
		drawTerrain (aGraphics, terrain1, aHex, aXoffset, aYoffset);
		
		return aYoffset;
	}

	private void drawTerrain2 (Graphics aGraphics, Hex aHex, int aXoffset, int aYoffset) {
		if (terrain2 != Terrain.NO_TERRAINX) {
			if (terrain2.isRiver ()) {
				if (terrain1.getLocation () == terrain2.getLocation ()) {
					aYoffset = aHex.getTrackWidth () * 2;
				}
			}
		}
		drawTerrain (aGraphics, terrain2, aHex, aXoffset, aYoffset);
	}
	 
	private void drawTerrainBleedThrough (Graphics aGraphics, Terrain aTerrain, Hex aHex, int aXoffset, int aYoffset) {
		if (aTerrain != Terrain.NO_TERRAINX) {
			if (aTerrain.bleedThroughAll ()) {
				drawTerrain (aGraphics, aTerrain, aHex, aXoffset, aYoffset);
			}
		}
	}

	public boolean setAllowedRotation (int aIndex, boolean aAllowed) {
		boolean goodSet;

		if ((aIndex < 0) || (aIndex > 5)) {
			goodSet = false;
		} else {
			allowedRotations [aIndex] = aAllowed;
			goodSet = true;
		}

		return goodSet;
	}

	public void setAllRotations (boolean aAllowed) {
		int aIndex;

		for (aIndex = 0; aIndex < 6; aIndex++) {
			setAllowedRotation (aIndex, aAllowed);
		}
	}

	public boolean getAllowedRotation (int aIndex) {
		if ((aIndex < 0) || (aIndex > 5)) {
			return false;
		} else {
			return allowedRotations [aIndex];
		}
	}

	public int getCountofAllowedRotations () {
		int tCount;
		int tIndex;

		tCount = 0;
		for (tIndex = 0; tIndex < 6; tIndex++) {
			if (allowedRotations [tIndex]) {
				tCount++;
			}
		}

		return tCount;
	}

	private void setAllValues (int Xc, int Yc, HexMap aHexMap, int aBaseTerrain, Tile aTile, int aTileOrient,
			String aBaseName, String aBlockedSides) {
		int tIndex;

		setXY (Xc, Yc);
		hexMap = aHexMap;
		setOtherValues (aBaseTerrain, aTile, aTileOrient, aBaseName, aBlockedSides);
		allowedRotations = new boolean [6];
		for (tIndex = 0; tIndex < 6; tIndex++) {
			allowedRotations [tIndex] = false;
			neighbors [tIndex] = NO_MAP_CELL;
		}
		tileOrientLocked = false;
		selectedFeature2 = new Feature2 ();
		removePortToken ();
		removeCattleToken ();
		setDestinationCorpID (Corporation.NO_ID);
		setBenefitValue (0);
	}

	public int getBenefitValue () {
		int tBenefitValue;
		Corporation tCorporation;
		License tPortLicense;
		License tLicense;
		
		tBenefitValue = 0;
		if (hasPortToken ()) {
			tCorporation = hexMap.getOperatingCompany ();
			tPortLicense = tCorporation.getPortLicense ();
			if (tPortLicense != License.NO_LICENSE) {
				tBenefitValue = tPortLicense.getPortValue ();
			}
		} else if (hasCattleToken ()) {
			tCorporation = hexMap.getOperatingCompany ();
			tLicense = tCorporation.getLicense (LicenseTypes.CATTLE);
			if (tLicense != License.NO_LICENSE) {
				tBenefitValue = tLicense.getBenefitValue ();
			}
		} else if (hasBridgeToken ()) {
			tCorporation = hexMap.getOperatingCompany ();
			tLicense = tCorporation.getLicense (LicenseTypes.BRIDGE);
			if (tLicense != License.NO_LICENSE) {
				tBenefitValue = tLicense.getBenefitValue ();
			}
		} else if (hasTunnelToken ()) {
			tCorporation = hexMap.getOperatingCompany ();
			tLicense = tCorporation.getLicense (LicenseTypes.TUNNEL);
			if (tLicense != License.NO_LICENSE) {
				tBenefitValue = tLicense.getBenefitValue ();
			}

		}
		
		return tBenefitValue;
	}
	
	public void setBenefitValue (int aBenefitValue) {
		benefitValue = aBenefitValue;
	}
	
	public void setDestinationCorpID (int aDestionationCorpID) {
		destinationCorpID = aDestionationCorpID;
	}
	
	public int getDestinationCorpID () {
		return destinationCorpID;
	}
	
	public void setCityInfo (CityInfo aCityInfo) {
		centers.setCityInfo (aCityInfo);

		if (tile != Tile.NO_TILE) {
			tile.setCityInfo (aCityInfo);
		}
	}

	public boolean removeHome (Corporation aCorporation, Location aLocation) {
		boolean tHomeRemoved;
		Location tNewLocation;
		
		tHomeRemoved = false;
		if (isTileOnCell ()) {
			if (tile != Tile.NO_TILE) {
				tNewLocation = aLocation.rotateLocation (-tileOrient);
				tHomeRemoved = tile.removeHome (aCorporation, tNewLocation);
				tile.setMapCell (this);
			}
		} else {
			tHomeRemoved = centers.removeHome (aCorporation, aLocation);
		}
		
		return tHomeRemoved;
	}
	
	public void setCorporationHome (Corporation aCorporation, Location aLocation) {
		Location tNewLocation;

		centers.setCorporationHome (aCorporation, aLocation);
		centers.setMapCell (this);
		if (isTileOnCell ()) {
			if (tile != Tile.NO_TILE) {
				tNewLocation = aLocation.rotateLocation (-tileOrient);
				tile.setCorporationHome (aCorporation, tNewLocation);
				tile.setMapCell (this);
			}
		}
	}

	public void setEmptyMapCell (int aBaseTerrain) {
		setOtherValues (aBaseTerrain, Tile.NO_TILE, NO_ORIENTATION, NO_NAME, NO_BLOCKED_SIDES);
	}

	public static void setMapDirection (boolean aMapDirection) {
		mapDirection = aMapDirection;
	}

	public void setMapDirection (String aMapDirection) {
		if (aMapDirection == null) {
			setMapDirection (false);
		} else {
			if (aMapDirection.equals (Hex.DIRECTION_NS)) {
				setMapDirection (false);
			} else {
				if (aMapDirection.equals (Hex.DIRECTION_EW)) {
					setMapDirection (true);
				} else {
					setMapDirection (false);
				}
			}
		}
	}

	public void setNeighbor (int aSide, MapCell aNeighbor) {
		if (neighbors [aSide] == NO_MAP_CELL) {
			neighbors [aSide] = aNeighbor;
			aNeighbor.setNeighbor ((aSide + 3) % 6, this);
		}
	}

	public void setOtherValues (int aBaseTerrain, Tile aTile, int aTileOrient, String aBaseName, String aBlockedSides) {
		String tSideNames[] = { "A", "B", "C", "D", "E", "F" };
		int tBlockedIndex;
		int tIndex;

		centers = new Centers ();
		endRoutes = new LinkedList<> ();
		neighbors = new MapCell [6];
		baseTileName = new TileName (aBaseName);
		if (aBlockedSides == null) {
			for (tBlockedIndex = 0; tBlockedIndex < 6; tBlockedIndex++) {
				blockedSides [tBlockedIndex] = false;
			}
		} else {
			if (aBlockedSides.equals ("")) {
				for (tBlockedIndex = 0; tBlockedIndex < 6; tBlockedIndex++) {
					blockedSides [tBlockedIndex] = false;
				}
			} else {
				for (tBlockedIndex = 0; tBlockedIndex < 6; tBlockedIndex++) {
					tIndex = aBlockedSides.indexOf (tSideNames [tBlockedIndex]);
					if (tIndex >= 0) {
						blockedSides [tBlockedIndex] = true;
					} else {
						blockedSides [tBlockedIndex] = false;
					}
				}
			}
		}
		terrain1 = Terrain.NO_TERRAINX;
		terrain2 = Terrain.NO_TERRAINX;
		setBaseTerrain (aBaseTerrain);
		putTile (aTile, aTileOrient);
		clearSelected ();
		startingTileNumber = Tile.NOT_A_TILE;
		startingTile = false;
		rebate = Rebate.NO_REBATE;
		clearAllTrainsUsingSides ();
	}

	public void setBaseTerrain (Terrain aBaseTerrain) {
		baseTerrain = aBaseTerrain;
	}
	
	public void setBaseTerrain (int aBaseTerrain) {
		setBaseTerrain (new Terrain (aBaseTerrain));
	}
	
	public void setScale (int hexScale, Hex aHex) {
		aHex.setScale (hexScale);
	}

	public void setSelectedFeature2 (Location aLocation) {
		if (selectedFeature2 != Feature2.NO_FEATURE2) {
			selectedFeature2.setLocation (aLocation);
		}
	}

	public void setSelectedFeature2 (Location aLocation, Location aLocation2) {
		selectedFeature2.setLocation (aLocation);
		selectedFeature2.setLocation2 (aLocation2);
	}

	// Used to Undo Tile Upgrade that had Tokens
	public void setStationAt (ShareCompany aShareCompany, int aStationIndex, int aCityIndex, int aTokenIndex) {
		City tCity;
		MapToken tMapToken;

		tMapToken = (MapToken) aShareCompany.getTokenAt (aTokenIndex);
		tCity = tile.getCityAt (aCityIndex);
		tCity.setStation (aStationIndex, tMapToken);
	}

	public void setStartingTile (boolean aStarting) {
		if (aStarting) {
			startingTileNumber = tileNumber;
		}
		startingTile = aStarting;
		tileOrientLocked = startingTile;
	}

	public void setStartingTile () {
		setStartingTile (true);
	}

	public void setTerrain1 (int aTerrain, int aCost, Location aLocation) {
		terrain1 = new Terrain (aTerrain, aCost, aLocation);
	}

	public void setTerrain2 (int aTerrain, int aCost, Location aLocation) {
		terrain2 = new Terrain (aTerrain, aCost, aLocation);
	}

	public void setTerrainFillColor (String aTerrainFillColor) {
		Color tTerrainFillColor;

		tTerrainFillColor = GUI.NO_COLOR;
		if (!(aTerrainFillColor == GUI.NULL_STRING)) {
			if (aTerrainFillColor.equals ("white")) {
				tTerrainFillColor = Color.white;
			} else if (aTerrainFillColor.equals ("black")) {
				tTerrainFillColor = Color.black;
			} else if (aTerrainFillColor.equals ("hollow")) {
				tTerrainFillColor = GUI.NO_COLOR;
			} else if (aTerrainFillColor.equals ("orange")) {
				tTerrainFillColor = Color.orange;
			}
		}
		setTerrainFillColor (tTerrainFillColor);
	}

	public void setTerrainFillColor (Color aTerrainFillColor) {
		terrainFillPaint = aTerrainFillColor;
	}

	public RevenueCenter setupRevenueCenter (int aType, int aID, int aLocation, String aName, int aValue,
			TileType aTileType) {
		RevenueCenterType tRCType;
		RevenueCenter tRevenueCenter;
		
		tRCType = new RevenueCenterType (aType);
		tRevenueCenter = setupRevenueCenter (tRCType, aID, aLocation, aName, aValue, aTileType);
		
		return tRevenueCenter;
	}

	public RevenueCenter setupRevenueCenter (RevenueCenterType tRevenueCenterType, int aID, int aLocation, String aName,
			int aValue, TileType aTileType) {
		RevenueCenter tRevenueCenter;
		int tStationCount;

		tStationCount = tRevenueCenterType.getStationCount ();
		if (tRevenueCenterType.isDotTown ()) {
			tRevenueCenter = new Town (tRevenueCenterType, aID, aLocation, aName, aValue, aTileType);
		} else if (tRevenueCenterType.isTown ()) {
			tRevenueCenter = new Town (tRevenueCenterType, aID, aLocation, aName, aValue, aTileType);
		} else if (tRevenueCenterType.isCity ()) {
			tRevenueCenter = new City (tRevenueCenterType, tStationCount, aID, aLocation, aName, aValue, aTileType);
		} else {
			tRevenueCenter = null;
		}

		return tRevenueCenter;
	}

	public void setXY (int aXCenter, int aYCenter) {
		XCenter = aXCenter;
		YCenter = aYCenter;
	}

	public void swapTokens () {
		if (isTileOnCell ()) {
			tile.swapTokens (this);
		}
	}

	public void toggleSelected () {
		selected = !selected;
	}

	public boolean upgradeTile (TileSet aTileSet, Tile aNewTile) {
		int tCurrentTileNumber;
		int tUpgradeToTileNumber;
		int tCityCenterCount;
		int tCityCenterIndex;
		int tFirstPossibleRotation;
		Location tOldCityLocation;
		Location tNewCityLocation;
		RevenueCenter tRevenueCenter;
		City tDestinationCity;
		City tCity;
		Corporation tBaseCorporation;
		GameTile tCurrentGameTile;
		Tile tCurrentTile;
		Upgrade tUpgrade;
		boolean tAllowedRotations [] = new boolean [6];
		boolean tMustSwap = false;
		boolean tTilePlaced = false;

		tCurrentTile = getTile ();
		tCurrentTileNumber = getTileNumber (); // Find Current Tile Number and the Current Game Tile
		tCurrentGameTile = aTileSet.getGameTile (tCurrentTileNumber);

		for (int tRotationIndex = 0; tRotationIndex < 6; tRotationIndex++) {
			tAllowedRotations [tRotationIndex] = false;
		}
		tFirstPossibleRotation = MapCell.NO_ROTATION;
		tUpgradeToTileNumber = aNewTile.getNumber (); // get New Tile's Number
		tUpgrade = Upgrade.NO_UPGRADE;
		if (tCurrentGameTile != GameTile.NO_GAME_TILE) {
			// Determine possible rotations of new Tile that replaces all Existing Track on
			// previous Tile
			// Add an Allowed Rotations if all Tracks can Exit based on 'canAllTracksExit'
			// Method
			// Once the first Rotation that is allowed is found, Save it.
			tUpgrade = tCurrentGameTile.getUpgradeTo (tUpgradeToTileNumber);
			tAllowedRotations = getAllowedRotations (tUpgrade, aNewTile);
		}

		tFirstPossibleRotation = getFirstPossibleRotation (tAllowedRotations);

		if (tUpgrade == Upgrade.NO_UPGRADE) {
			restoreTile (aTileSet, aNewTile);
			System.err.println ("No Upgrade available -- Aborting Upgrade");
			return tTilePlaced;
		}

		if (tFirstPossibleRotation == NO_ROTATION) {
			restoreTile (aTileSet, aNewTile);
			System.err.println ("No Rotation allows all Tracks to Exit -- Aborting Upgrade");
			return tTilePlaced;
		}

		// Set the new Tile onto the MapCell so that when MoveMapToken is called, it can
		// properly set the Connected Sides
		// using the new Tile.

		setTile (aNewTile);
		setTileOrientationLocked (false);
		setTileInfo (aNewTile.getNumber (), tFirstPossibleRotation, false);
		aNewTile.setMapCell (this);

		// For the Tile on Map, find Revenue Centers, and Tokens on them. Place them
		// onto the Tile to be Placed.
		// If the Revenue Center has a Base Corporation without the Base Token, Transfer
		// Base Corporation
		// To the Tile to be Placed.
		tCityCenterCount = tCurrentTile.getCityCenterCount ();
		if (tCityCenterCount > 0) {
			for (tCityCenterIndex = 0; tCityCenterIndex < tCityCenterCount; tCityCenterIndex++) {
				tRevenueCenter = tCurrentTile.getRevenueCenter (tCityCenterIndex);
				if (tRevenueCenter.isCity ()) {
					tCity = (City) tRevenueCenter;
					tOldCityLocation = tCity.getLocation ();
					tNewCityLocation = tUpgrade.getToFromLocation (tOldCityLocation, tFirstPossibleRotation);
					if (tCity.cityHasAnyStation ()) {
						tMustSwap = moveAllMapTokens (aNewTile, tNewCityLocation, tCity);
					} else {
						if (tCity.isDestination ()) {
							tDestinationCity = tCity.clone ();
							tDestinationCity.setTemporary (true);
							aNewTile.addCenter (0, tDestinationCity);
						} else if (tCity.isCorporationBase ()) {
							tBaseCorporation = tCity.getCorporation ();
							aNewTile.setCorporationHome (tBaseCorporation, tNewCityLocation);
							aNewTile.setMapCell (this);
						}
					}
				}
			}
		}
		if (tMustSwap) {
			swapTokens ();
		}
		tTilePlaced = true;
		restoreTile (aTileSet, tCurrentTile);

		// Add Tile in first Possible Orientation
		for (int tRotationIndex = 0; tRotationIndex < 6; tRotationIndex++) {
			setAllowedRotation (tRotationIndex, tAllowedRotations [tRotationIndex]);
		}

		return tTilePlaced;
	}

	public boolean moveAllMapTokens (Tile aNewTile, Location aNewCityLocation, City aOldCity) {
		int tStationIndex;
		MapToken tMapToken;
		City tNewTileCity;
		Location tNewLocation;
		boolean tMustSwap;
		String tOldSides;
		String tNewSides;

		tNewLocation = aNewCityLocation;
		tMustSwap = false;		// For handling O-O Tiles where the Tracks must replace existing Track
								// The existing Track is from a Side to a City. It either works as 
								// coded in the upgrade data or it has a missing Connection.
								// a if missing it must swap with the other RevenueCenter for Token Upgrades
								//
		for (tStationIndex = 0; tStationIndex < aOldCity.getStationCount (); tStationIndex++) {
			tMapToken = aOldCity.getToken (tStationIndex);
			if (tMapToken != MapToken.NO_MAP_TOKEN) {
				tNewTileCity = (City) aNewTile.getCenterAtLocation (tNewLocation);
				if (tNewTileCity != City.NO_CITY) {
					tOldSides = tMapToken.getSides ();
					moveAMapToken (tMapToken, tNewTileCity);
					tNewSides = tMapToken.getSides ();
					if (! allOldSidesConnected (tOldSides,tNewSides)) {
						tMustSwap = true;
					}
				}
			}
		}

		return tMustSwap;
	}

	public boolean allOldSidesConnected (String aOldSides, String aNewSides) {
		boolean tAllOldSidesConnected;
		String tAllOldSides [];
		
		tAllOldSidesConnected = true;
		tAllOldSides = aOldSides.split ("\\|");
		
		for (String tOldSide : tAllOldSides) {
			if (! aNewSides.contains (tOldSide)) {
				tAllOldSidesConnected = false;
			}
		}
	
		return tAllOldSidesConnected;
	}
	
	public void moveAMapToken (MapToken aMapToken, City aNewCity) {
		aNewCity.setMapCell (this);
		aNewCity.setStation (aMapToken);
	}

	public boolean anyAllowedRotation (TileSet aTileSet, Tile aNewTile) {
		boolean tAnyAllowedRotation = false;
		Upgrade tUpgrade;
		GameTile tCurrentGameTile;
		int tCurrentTileNumber;
		int tUpgradeToTileNumber;
		int tFirstPossibleRotation;
		boolean tAllowedRotations [];

		if (this.isTileOnCell ()) {
			tCurrentTileNumber = getTileNumber (); // Find Current Tile Number and the Current Game Tile
			tCurrentGameTile = aTileSet.getGameTile (tCurrentTileNumber);
			tUpgradeToTileNumber = aNewTile.getNumber (); // get New Tile's Number
			tUpgrade = tCurrentGameTile.getUpgradeTo (tUpgradeToTileNumber);
			tAllowedRotations = getAllowedRotations (tUpgrade, aNewTile);
		} else {
			tAllowedRotations = getAllowedRotations (aNewTile);
		}
		tFirstPossibleRotation = getFirstPossibleRotation (tAllowedRotations);
		if (tFirstPossibleRotation != NO_ROTATION) {
			tAnyAllowedRotation = true;
		}

		return tAnyAllowedRotation;
	}

	public boolean [] getAllowedRotations (Upgrade aUpgrade, Tile aNewTile) {
		int tRotationCount;
		int tRotation;
		int tCurrentTileOrient;
		int tUpgradeRotation;
		boolean tAllowedRotations[] = new boolean [6];

		for (int tRotationIndex = 0; tRotationIndex < 6; tRotationIndex++) {
			tAllowedRotations [tRotationIndex] = false;
		}
		tCurrentTileOrient = getTileOrient (); // Identify current rotation of Tile on Map Cell
		if (aUpgrade != Upgrade.NO_UPGRADE) {
			tRotationCount = aUpgrade.getRotationCount ();
			for (int tRotationIndex = 0; tRotationIndex < tRotationCount; tRotationIndex++) {
				tRotation = aUpgrade.getRotation (tRotationIndex);
				tUpgradeRotation = (tRotation + tCurrentTileOrient) % 6;
				tAllowedRotations [tUpgradeRotation] = canAllTracksExit (aNewTile, tUpgradeRotation);
			}
		}

		return tAllowedRotations;
	}

	public boolean [] getAllowedRotations (Tile aTile) {
		boolean tAllowedRotations[] = new boolean [6];
		int tRotationCount;

		for (int tRotation = 0; tRotation < 6; tRotation++) {
			tAllowedRotations [tRotation] = false;
		}
		tRotationCount = 6;
		for (int tRotation = 0; tRotation < tRotationCount; tRotation++) {
			tAllowedRotations [tRotation] = canAllTracksExit (aTile, tRotation);
		}

		return tAllowedRotations;
	}

	public int getFirstPossibleRotation (boolean aAllowedRotations[]) {
		int tFirstPossibleRotation = NO_ROTATION;

		for (int tUpgradeRotation = 0; tUpgradeRotation < aAllowedRotations.length; tUpgradeRotation++) {
			if (aAllowedRotations [tUpgradeRotation]) {
				if (tFirstPossibleRotation == NO_ROTATION) {
					tFirstPossibleRotation = tUpgradeRotation;
				}
			}
		}

		return tFirstPossibleRotation;
	}

	public boolean upgradeAllowed (boolean aAllowedRotations []) {
		boolean tUpgradeAllowed = false;
		int tFirstPossibleRotation;

		tFirstPossibleRotation = getFirstPossibleRotation (aAllowedRotations);
		if (tFirstPossibleRotation != NO_ROTATION) {
			tUpgradeAllowed = true;
		}

		return tUpgradeAllowed;
	}

	public void restoreTile (TileSet aTileSet, Tile aCurrentTile) {
		GameTile tCurrentGameTile;
		int tCurrentTileNumber = aCurrentTile.getNumber ();

		// Remove Tile from Map Cell, Clear all City Info and Stations, and place it
		// back on TileSet
		aCurrentTile.removeTemporaryCenters ();
		tCurrentGameTile = aTileSet.getGameTile (tCurrentTileNumber);
		aCurrentTile.clearAll ();
		tCurrentGameTile.pushTile (aCurrentTile);
	}

	@Override
	public int compare (Object arg0, Object arg1) {
		if (arg0 instanceof MapCell && arg1 instanceof MapCell) {
			MapCell iMap0 = (MapCell) arg0;
			MapCell iMap1 = (MapCell) arg1;
			int iXC0, iYC0, iXC1, iYC1;

			iXC0 = iMap0.getXCenter ();
			iYC0 = iMap0.getYCenter ();
			iXC1 = iMap1.getXCenter ();
			iYC1 = iMap1.getYCenter ();
			if ((iXC0 == iXC1) && (iYC0 == iYC1)) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return -1;
		}
	}

	@Override
	public boolean equals (Object arg) {
		if (compare (this, arg) == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode () {
		assert false : "hashCode not designed";
		return 42; // any arbitrary constant will do
	}

	public boolean isTileLayCostFree () {
		boolean tIsTileLayCostFree = true;

		// A Tile on the Cell, unless it is Fixed, is Free to lay
		if (isTileOnCell ()) {
			if (tile.isFixedTile ()) {
				tIsTileLayCostFree = false;
			}
		} else {
			tIsTileLayCostFree = false;
		}

		return tIsTileLayCostFree;
	}

	public int getCostToLayTile () {
		int tCostToLayTile = 0;

		// Test if the Tile Lay Cost if Free.
		if (!isTileLayCostFree ()) {

			if (baseTerrain != Terrain.NO_TERRAIN_FEATURE) {
				tCostToLayTile += baseTerrain.getCost ();
			}

			if (terrain1 != Terrain.NO_TERRAIN_FEATURE) {
				tCostToLayTile += terrain1.getCost ();
			}
			if (terrain2 != Terrain.NO_TERRAIN_FEATURE) {
				tCostToLayTile += terrain2.getCost ();
			}
		}

		return tCostToLayTile;
	}

	public String getBasePrivateAbbrev (CorporationList privateCos) {
		String tPrivateAbbrev = "";
		PrivateCompany tPrivateCompany;

		if (privateCos != CorporationList.NO_CORPORATION_LIST) {
			tPrivateCompany = privateCos.getPrivateCompanyAtMapCell (this);
			if (tPrivateCompany != Corporation.NO_CORPORATION) {
				tPrivateAbbrev = tPrivateCompany.getAbbrev ();
			}
		}

		return tPrivateAbbrev;
	}

	public boolean privatePreventsTileLay (CorporationList privateCos, TrainCompany tOperatingTrainCompany) {
		boolean tPrivatePrevents = false;
		PrivateCompany tPrivateCompany;

		// If a Tile is on the Cell any Company can do a placement/upgrade
		if (!isTileOnCell ()) {
			if (privateCos != CorporationList.NO_CORPORATION_LIST) {
				tPrivateCompany = privateCos.getPrivateCompanyAtMapCell (this);
				if (tPrivateCompany != Corporation.NO_CORPORATION) {
					// Given this Map Cell is home to a Private Company
					// Then Prevent this Tile Lay if the Private is not Owned by the Operating Train
					// Company
					if (tOperatingTrainCompany.doesNotOwn (tPrivateCompany)) {
						tPrivatePrevents = true;
					}
				}
			}
		}

		return tPrivatePrevents;
	}

	public int getTotalTerrainCost () {
		int tTotalTerrainCost;
		int tCostBaseTerrain, tCostTerrain1, tCostTerrain2;
		Terrain tBaseTerrain, tTerrain1, tTerrain2;

		tBaseTerrain = getBaseTerrain ();
		tCostBaseTerrain = getTerrainCost (tBaseTerrain);
		tTerrain1 = getTerrain1 ();
		tCostTerrain1 = getTerrainCost (tTerrain1);
		tTerrain2 = getTerrain2 ();
		tCostTerrain2 = getTerrainCost (tTerrain2);
		tTotalTerrainCost = tCostBaseTerrain + tCostTerrain1 + tCostTerrain2;

		return tTotalTerrainCost;
	}

	public int getTerrainCost (Terrain aTerrain) {
		int tTerrainCost;

		if (aTerrain != Terrain.NO_TERRAINX) {
			tTerrainCost = aTerrain.getCost ();
		} else {
			tTerrainCost = 0;
		}

		return tTerrainCost;
	}

	public int getCostToLayTile (Tile aTile) {
		TileType tTileType;
		TileName tTileName;
		int tCostToLay;
		int tTileTypeInt;
		int tTotalTerrainCost;
		
		tTotalTerrainCost = getTotalTerrainCost ();
		tCostToLay = 0;

		if (aTile == Tile.NO_TILE) {
			tCostToLay = tTotalTerrainCost;
		} else {
			tTileType = aTile.getType ();
			tTileTypeInt = tTileType.getType ();
			if (tTileTypeInt == TileType.YELLOW) {
				tCostToLay = tTotalTerrainCost;
			} else if (tTileTypeInt == TileType.GREEN) {
				tTileName = aTile.getTileName ();
				if (tTileName != null) {
					if (tTileName.isOOTile () || tTileName.isNYTile ()) {
						tCostToLay = tTotalTerrainCost;
					}
				}
			}
		}

		return tCostToLay;
	}

	public Track getTrackFromStartToEnd (int aStartLocation, int aEndLocation) {
		Track tTrack;
		Location tRawThisLocation;
		Location tRawThatLocation;
		int tRawStartLocation;
		int tRawEndLocation;

		tTrack = Track.NO_TRACK;
		tRawStartLocation = aStartLocation;
		tRawEndLocation = aEndLocation;
		if (isTileOnCell ()) {
			if ((aStartLocation == Location.DEAD_END_LOC) && 
				((aEndLocation >= Location.MIN_SIDE) && (aEndLocation <= Location.MAX_SIDE))) {
				tRawStartLocation = tRawEndLocation + Location.DEAD_END0_LOC;
			} else if ((aEndLocation == Location.DEAD_END_LOC) &&
						((aStartLocation >= Location.MIN_SIDE) && (aStartLocation <= Location.MAX_SIDE))) {
				tRawEndLocation = tRawStartLocation + Location.DEAD_END0_LOC;
			}

			tRawThisLocation = new Location (tRawStartLocation);
			tRawThatLocation = new Location (tRawEndLocation);
			tRawThisLocation = unrotateIfSide (tRawThisLocation);
			tRawThisLocation = unrotateIfDeadEndSide (tRawThisLocation);
			tRawThatLocation = unrotateIfSide (tRawThatLocation);
			tRawThatLocation = unrotateIfDeadEndSide (tRawThatLocation);

			tTrack = tile.getTrackFromStartToEnd (tRawThisLocation.getLocation (), tRawThatLocation.getLocation ());
		}

		return tTrack;
	}

	public Location unrotateIfDeadEndSide (Location aLocation) {
		if (aLocation.isDeadEndSide ()) {
			aLocation = aLocation.unrotateLocation (tileOrient);
		}

		return aLocation;
	}

	public Location unrotateIfSide (Location aLocation) {
		if (aLocation.isSide ()) {
			aLocation = aLocation.unrotateLocation (tileOrient);
		}

		return aLocation;
	}

	public boolean hasConnectingTrackBetween (int aThisLocation, int aThatLocation) {
//		Location tRawThisLocation, tRawThatLocation;
		Track tTrack;
		boolean tHasTrack;
		
		tTrack = getTrackFromStartToEnd (aThisLocation, aThatLocation);
		if (tTrack == Track.NO_TRACK) {
			tHasTrack = false;
		} else {
			tHasTrack = true;
		}
//		if ((aThisLocation == Location.DEAD_END_LOC) && 
//			((aThatLocation >= Location.MIN_SIDE) && (aThatLocation <= Location.MAX_SIDE))) {
//			aThisLocation = aThatLocation + Location.DEAD_END0_LOC;
//		} else if ((aThatLocation == Location.DEAD_END_LOC) &&
//				((aThisLocation >= Location.MIN_SIDE) && (aThisLocation <= Location.MAX_SIDE))) {
//			aThatLocation = aThatLocation + Location.DEAD_END0_LOC;
//		}
//		tRawThisLocation = new Location (aThisLocation);
//		tRawThatLocation = new Location (aThatLocation);
//		tRawThisLocation = unrotateIfSide (tRawThisLocation);
//		tRawThatLocation = unrotateIfSide (tRawThatLocation);
//
//		return tile.hasConnectingTrackBetween (tRawThisLocation, tRawThatLocation);
		return tHasTrack;
	}

	public int getSideInUseCount () {
		int tSideInUseCount = 0;

		for (int tSideIndex = 0; tSideIndex < 6; tSideIndex++) {
			if (trainUsingSide [tSideIndex] > 0) {
				tSideInUseCount++;
			}
		}

		return tSideInUseCount;
	}

	public String getSidesInUse () {
		String tSidesInUse = "";

		for (int tSideIndex = 0; tSideIndex < 6; tSideIndex++) {
			if (trainUsingSide [tSideIndex] > 0) {
				if (tSidesInUse != "") {
					tSidesInUse += ", ";
				}
				tSidesInUse += tSideIndex + ": " + trainUsingSide [tSideIndex];
			}
		}

		return tSidesInUse;
	}

	public String getDetail () {
		String tMapCellDetail;

		tMapCellDetail = getCellID ();
		if (isTileOnCell ()) {
			tMapCellDetail += " Tile # " + tile.getNumber () + " Orientation " + tileOrient;
		}
		if (getSideInUseCount () == 0) {
			tMapCellDetail += " [No Sides in Use]";
		} else {
			tMapCellDetail += " [Sides in use {" + getSidesInUse () + "} ]";
		}

		return tMapCellDetail;
	}

	public boolean isTileAvailableForMapCell () {
		boolean tIsTileAvailableForMapCell = true;

		tIsTileAvailableForMapCell = hexMap.isTileAvailableForMapCell (this);

		return tIsTileAvailableForMapCell;
	}

	public boolean canUpgradeTo (TileType tSelectedTileType) {
		boolean tCanUpgradeTo = false;
		TileType tTileTypeOnCell;

		if (isTileOnCell ()) {
			tTileTypeOnCell = tile.getTheTileType ();
			tCanUpgradeTo = tTileTypeOnCell.canUpgradeTo (tSelectedTileType);
		}

		return tCanUpgradeTo;
	}

	public Corporation getCorporationByID (int aCorporationID) {
		return hexMap.getCorporationByID (aCorporationID);
	}

	public Corporation getCorporation (String aCorporationAbbrev) {
		return hexMap.getCorporation (aCorporationAbbrev);
	}

	public void fillMapGraph (MapGraph aMapGraph) {
		if (isTileOnCell ()) {
			tile.fillMapGraph (aMapGraph, tileOrient, this);
		}
	}
	
	public int getBonusRevenue () {
		int tBonusRevenue;
		int tPortRevenue;
		int tCattleRevenue;
		int tBridgeRevenue;
		int tTunnelRevenue;
		int tLicenseRevenue;
		
		tBonusRevenue = 0;
		tPortRevenue = 0;
		tCattleRevenue = 0;
		tBridgeRevenue = 0;
		tTunnelRevenue = 0;
		tLicenseRevenue = 0;
		if (hasPortToken ()) {
			tPortRevenue = getBenefitValue ();
		}
		if (hasCattleToken ()) {
			tCattleRevenue = getBenefitValue ();
		}
		if (hasBridgeToken ()) {
			tBridgeRevenue = getBenefitValue ();
		}
		if (hasTunnelToken ()) {
			tTunnelRevenue = getBenefitValue ();
		}
		
		tLicenseRevenue = tBridgeRevenue + tTunnelRevenue;
		
		tBonusRevenue = tPortRevenue + tCattleRevenue + tLicenseRevenue;
		
		return tBonusRevenue;
	}
	
	public void removeMapTokens (TokenCompany aTokenCompany, CloseCompanyAction aCloseCompanyAction) {
		if (isTileOnCell ()) {
			tile.removeMapTokens (aTokenCompany, id, aCloseCompanyAction);
		}
	}
}
