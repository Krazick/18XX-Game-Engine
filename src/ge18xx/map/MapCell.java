package ge18xx.map;

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
import ge18xx.company.MapToken;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TokenCompany;
import ge18xx.company.TrainCompany;
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
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.geom.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.*;

public class MapCell implements Comparator<Object> {
	public static final AttributeName AN_REVENUE_CENTER_INDEX = new AttributeName ("revenueCenterIndex");
	public static final AttributeName AN_ORIENTATION = new AttributeName ("orientation");
	public static final AttributeName AN_MAP_CELL_ID = new AttributeName ("mapCellID");
	public static final AttributeName AN_STARTING = new AttributeName ("starting");
	public static final AttributeName AN_SIDE = new AttributeName ("side");
	public static final ElementName EN_BLOCKED = new ElementName ("Blocked");
	public static final ElementName EN_MAP_CELL = new ElementName ("MapCell");
	public static final String NO_ID = "";
	public static final MapCell NO_MAP_CELL = null;
	public static final int NO_ORIENTATION = 0;
	public static final int NO_ROTATION = -1;
	static final boolean NOT_ON_TILE = false;
	static final String NO_NAME = "";
	static final String NO_BLOCKED_SIDES = "";
	static final String NO_DIRECTION = null;
	static final Tile NO_TILE = null;
	static boolean mapDirection;
	Color terrainFillColor;
	boolean tileOrientLocked;
    boolean selected;
	boolean startingTile;	// If the board has a initial tile placed, need to have terrain features show through.
	boolean allowedRotations [] = new boolean [6];
	boolean blockedSides [] = new boolean [6];
	String id = "A1";
    int XCenter, YCenter;
	int tileNumber;
	int tileOrient;
	int startingTileNumber;
	MapCell neighbors [];
	Tile tile;
	TileName baseTileName;
	Rebate rebate;
	Centers centers;
	List<Feature2> selectedFeatures;
	Feature2 selectedFeature2;
	Terrain baseTerrain;
	Terrain terrain1;
	Terrain terrain2;
	HexMap hexMap;
	
	public MapCell (HexMap aHexMap) {
		this (aHexMap, NO_DIRECTION);
	}
	
	public MapCell (HexMap aHexMap, String aMapDirection) {
		this (0, 0, aHexMap);
		setMapDirection (aMapDirection);
	}
	
    public MapCell (int Xc, int Yc, HexMap aHexMap) {
		this (Xc, Yc, aHexMap, Terrain.NO_TERRAIN, NO_TILE, NO_ORIENTATION, NO_NAME, NO_BLOCKED_SIDES);
   }
	
    public MapCell (int Xc, int Yc, HexMap aHexMap, int aBaseTerrain, Tile aTile, int aTileOrient, 
					String aBaseName, String aBlockedSides) {
    	setAllValues (Xc, Yc, aHexMap, aBaseTerrain, aTile, aTileOrient, aBaseName, aBlockedSides);
    }
    
	public boolean addRevenueCenter (int aType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
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
	
	public boolean addRevenueCenter (RevenueCenter aRC) {
		return centers.add (aRC);
	}
	
	public void clearAllStation () {
		if (tile != null) {
			tile.clearAllStations ();
		}
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
	
	// Get the Side of the Neighbor that connects back to this MapCell
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
	
	// Does an Existing Tile on this MapCell have Track that is connected to the Neighboring MapCell
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
			System.out.println ("Failed to find matching Neighbors");
		}
		
		return tHasConnectingTrackTo;
	}
	
	public Track getTrackFromSide (int aSideLocation) {
		Track tTrack = Track.NO_TRACK;
		int tUnrotatedSideLocation;
		
		if (isTileOnCell ()) {
			tUnrotatedSideLocation = (aSideLocation - tileOrient) % 6;
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
		centers.clearCityInfoCorporation (aCorporation);
	}
	
	public void clearSelected () {
		selected = false;
		clearSelectedFeature2 ();
	}
	
	public void clearSelectedFeature2 () {
		setSelectedFeature2 (new Location (Location.NO_LOCATION));
	}
	
	public void clearStation (int aCorporationId) {
		if (tile != null) {
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
		int index;
		
		tXMLElement = aXMLDocument.createElement (EN_MAP_CELL);
		tTerrainElement = baseTerrain.createElement (aXMLDocument);
		if (tTerrainElement != null) {
			tTerrainElement.setAttribute (Terrain.AN_CATEGORY, "base");
			tXMLElement.appendChild (tTerrainElement);
		}
		if (terrain1 != null) {
			tTerrainElement = terrain1.createElement (aXMLDocument);
			if (tTerrainElement != null) {
				tTerrainElement.setAttribute (Terrain.AN_CATEGORY, "optional");
				tXMLElement.appendChild (tTerrainElement);
			}
		}
		if (terrain2 != null) {
			tTerrainElement = terrain2.createElement (aXMLDocument);
			if (tTerrainElement != null) {
				tTerrainElement.setAttribute (Terrain.AN_CATEGORY, "optional");
				tXMLElement.appendChild (tTerrainElement);
			}
		}
		if (baseTileName != null) {
			tNameElement = new XMLElement (baseTileName.createElement (aXMLDocument).getElement ());
			if (tNameElement != null) {
				tXMLElement.appendChild (tNameElement);
			}
		}
		
		centers.appendCenters (tXMLElement, aXMLDocument);
		
		for (index = 0; index < 6; index++) {
			if (blockedSides [index]) {
				tBlockedElement = aXMLDocument.createElement (EN_BLOCKED);
				tBlockedElement.setAttribute (AN_SIDE, index);
				tXMLElement.appendChild (tBlockedElement);
			}
		}
		if (tile != null) {
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
		
		if (aTerrain != null) {
			tLocation = aTerrain.getLocation ();
			
			if (tLocation == null) {
				Xol = XCenter + Xoffset;
				Yol = YCenter + Yoffset;
			} else {
				tLocationPoint = tLocation.calcCenter (aHex);
				Xol = tLocationPoint.x + XCenter + Xoffset;
				Yol = tLocationPoint.y + YCenter + Yoffset;
			}
			aTerrain.draw (g, Xol, Yol, aHex, terrainFillColor);
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
	
	public boolean getAllowedRotation (int aIndex) {
		if ((aIndex < 0) || (aIndex > 5)) {
			return false;
		} else {
			return allowedRotations [aIndex];
		}
	}
	
	public Terrain getBaseTerrain () {
		return baseTerrain;
	}
		
	public Color getBaseTerrainFillColor () {
		return baseTerrain.getColor ();
	}
	
	public RevenueCenter getCenterAtLocation (Location aLocation) {
		return centers.getCenterAtLocation (aLocation);
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
	
	/* Generate XMLElement of current State of Cell, for all tiles, and Tokens for Saving */
	public XMLElement getMapCellState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		
		tXMLElement = null;
		if (isTileOnCell ()) {
			tXMLElement = aXMLDocument.createElement (EN_MAP_CELL);
			tXMLElement.setAttribute (Tile.AN_TILE_NUMBER, tile.getNumber ());
			tXMLElement.setAttribute (AN_ORIENTATION, tileOrient);
			if (tile.hasAnyStation ()) {
				tile.appendTokensState (aXMLDocument, tXMLElement);
			}
		}
		
		return tXMLElement;
	}
	
	public boolean getMapDirection () {
		return mapDirection;
	}
	
	public String getName () {
		return baseTileName.getName ();
	}
	
	public MapCell getNeighbor (int aSide) {
		if ((aSide < 0) || (aSide > 5)) {
			return null;
		} else {
			return neighbors [aSide];
		}
	}
	
	public RevenueCenter getRCContainingPoint (Point aPoint, Hex aHex) {
		RevenueCenter tRevenueCenter;
		
		tRevenueCenter = tile.getRCContainingPoint (aPoint, aHex, XCenter, YCenter, tileOrient);
		
		return tRevenueCenter;
	}
	
	public RevenueCenter getRevenueCenter (int aCenterIndex) {
		return centers.get (aCenterIndex);
	}
	
	public int getRevenueCenterCount () {
		return centers.size ();
	}
	
	public int getRevenueCenterID () {
		return centers.getRevenueCenterID ();
	}
	
	public int getRevenueCenterIndex (int aCorporationID) {
		return centers.getStationIndex (aCorporationID);
	}
	
	public Location getSelectedFeature2Location () {
		if (selectedFeature2 != null) {
			return selectedFeature2.getLocation ();
		} else {
			return new Location (Location.NO_LOCATION);
		}
	}
	
	public Location getSelectedFeature2Location2 () {
		if (selectedFeature2 != null) {
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
			tRevenueCenter = null;
		}
		
		return tRevenueCenter;
	}
	
	public Terrain getTerrain1 () {
		return terrain1;
	}
	
	public Terrain getTerrain2 () {
		return terrain2;
	}
	
	public Color getTerrainFillColor () {
		return terrainFillColor;
	}
	
	public Tile getTile () {
		return tile;
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
		int tTerrainCost;
		String tTileName;
		
		if (baseTerrain.isSelectable ()) {
			tTip = "<html>Cell <b>" + getCellID () + "</b><br>";
			if (baseTileName != null) {
				tTileName = baseTileName.getName ();
				if (tTileName.length () > 0) {
					tTip += "Tile Name: " + baseTileName.getName () + "<br>";
				}
			}
			if (tile != Tile.NO_TILE) {
				tTip += tile.getToolTip ();
				tTip += "Tile Orientation: " + tileOrient + "<br>";
			} else {
				if (centers.size () > 0) {
					tTip += centers.getToolTip ();
				}			
			}
			tTip = tTip + "Base: " + baseTerrain.getName () + "<br>";
			if (terrain1 != null) {
				tTip += "Terain: " + terrain1.getName ();
				tTerrainCost = terrain1.getCost ();
				if (terrain2 != null) {
					tTip += " & " + terrain2.getName ();
					tTerrainCost += terrain2.getCost ();
				}
				tTip += "<br>";
				if (tTerrainCost > 0) {
					tTip += "Build Cost: " + tTerrainCost + "<br>";
				}
			}
			if (rebate != null) {
				tTip += "Rebate: " + rebate.getAmount () + "<br>";
			}
			tTip += "</html>";
		} else {
			tTip = null;
		}
		
		return tTip;
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
		
		if (! isSelected ()) {
			hexMap.toggleSelectedMapCell (this);
		}
		if (isTileOnCell ()) {
			tSelectedRevenueCenter = getRCContainingPoint (aPoint, hexMap.hex);
			if (tSelectedRevenueCenter != null) {
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
	
	public boolean hasStation (int aCorpID) {
		if (tile != Tile.NO_TILE) {
			return tile.hasStation (aCorpID);
		} else {
			return false;
		}
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
	
	public void loadStationsStates (XMLNode aMapCellNode) {
		if (isTileOnCell ()) {
			tile.loadStationsStates (aMapCellNode);
		}
	}
	
	public void loadXMLCell (XMLNode aCellNode, int aTerrainCost [], int aTerrainType [], String aID) {
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		String tCategory;
		String tRCType;
		int tChildrenCount;
		int tChildrenIndex;
		Terrain tTerrain;
		RevenueCenter tRevenueCenter;
		int tTerrainIndex;
		int tTerrainType;
		int tTileNumber;
		int tOrientation;
		int tSide;
		boolean tStarting;
		
		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tile = null;
		rebate = null;
		id = aID;
		for (tChildrenIndex = 0; tChildrenIndex < tChildrenCount; tChildrenIndex++) {
			tChildNode = new XMLNode (tChildren.item (tChildrenIndex));
			tChildName = tChildNode.getNodeName ();
			if (Terrain.EN_TERRAIN.equals (tChildName)) {
				tTerrain = new Terrain (tChildNode);
				if (tTerrain != null) {
					tTerrainType = tTerrain.getTerrain ();
					for (tTerrainIndex = 0; tTerrainIndex < 15; tTerrainIndex++) {
						if (aTerrainType [tTerrainIndex] == tTerrainType) {
							tTerrain.setCost (aTerrainCost [tTerrainIndex]);
						}
					}
				}
				tCategory = tTerrain.getCategory (tChildNode);
				if (Terrain.AN_BASE.equals (tCategory)) {
					baseTerrain = tTerrain;
				} else if (Terrain.AN_OPTIONAL.equals (tCategory)) {
					if (terrain1 == null) {
						terrain1 = tTerrain;
					} else if (terrain2 == null) {
						terrain2 = tTerrain;
					}
				}
			} else if (RevenueCenter.EN_REVENUE_CENTER.equals (tChildName)) {
				tRCType = tChildNode.getThisAttribute (RevenueCenter.AN_TYPE);
				if (RevenueCenterType.isTown (tRCType)) {
					tRevenueCenter = new Town (tChildNode);
				} else if (RevenueCenterType.isCity(tRCType)){
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
	
	// To Place a Tile, determine if there is a tile already on the Map Cell, if so, and it is there
	// Do a Upgrade Tile, Otherwise, 
	// Copy Corporation Bases Corporation Destinations and Tokens from prior placement to new Tile 
	// Set the Tile down, and set orientation lock as false so it can be rotated
	
	public void placeTile (TileSet aTileSet, Tile aTile) {
		Tile tNewTile;
		
		if ((isTileOnCell ()) && (getTile () != Tile.NO_TILE)) {
			System.out.println ("in Map Cell, Ready to do Tile Upgrade");
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
		System.out.println ("Map Cell " + id);
	}
	
	public void setTile (Tile aTile) {
		tile = aTile;
	}
	
	public void setTileOrientationLocked (boolean aTileOrientLocked) {
		tileOrientLocked = aTileOrientLocked;
	}
	
	public void putTile (Tile aTile, int aTileOrient, boolean aStarting) {
		putTile (aTile, aTileOrient);
		setStartingTile (aStarting);
	}

	public void putTile (Tile aTile, int aTileOrient) {
		int tNewTileNumber;
		
		if (centers != null) {
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
	
	public void pickupTile (TileSet aTileSet) {
		Tile tTileOnMapCell;
		
		if (isTileOnCell ()) {
			// Tile on MapCell -- Need to return it to the Tile Set
			tTileOnMapCell = getTile ();
			System.out.println ("Found Tile # " + tTileOnMapCell.getNumber () + " on the Selected Map Cell");
			System.out.println ("Will try and restore it to the Tile Set and remove from the map.");
			removeTile ();
			restoreTile (aTileSet, tTileOnMapCell);
			hexMap.redrawMap ();
			aTileSet.redrawTileTray ();
		} else {
			System.err.println ("No Tile on Hex to return");
		}
	}
	
	public void putTileDown (TileSet aTileSet) {
		GameTile tSelectedTile;
		
		tSelectedTile = aTileSet.getSelectedTile ();
		if (tSelectedTile != null) {
			putThisTileDown (aTileSet, tSelectedTile, NO_ROTATION);
		} else {
			System.err.println ("Put Tile Down Button Selected, no Tile Selected From Tray");
		}
	}

	public void putThisTileDown (TileSet aTileSet, GameTile aThisTile, int aThisRotation) {
		GameTile tGameTileOnMapCell;
		Tile tTile;
		Tile tTileOnMapCell;
		int tUpgradeCount;
		int tTileNumber;
		int tPossibleOrientation;
		if (isTileOnCell ()) {
			// Tile on MapCell -- Upgrade Required
			tTileOnMapCell = getTile ();
			tTileNumber = tTileOnMapCell.getNumber ();
			tGameTileOnMapCell = aTileSet.getGameTile (tTileNumber);
			tUpgradeCount = tGameTileOnMapCell.getUpgradeCount ();
			if (tUpgradeCount > 0) {
				tTile = aThisTile.popTile ();
				upgradeTile (aTileSet, tTile);
				hexMap.redrawMap();
			} else {
				System.err.println ("No Upgrades Available");
			}
		} else {
			// No Tile on Map Cell Straight Forward placement
			if (sameTypeCount (aThisTile)) {
				// Have a Tile, and a MapCell Selected with same Revenue Center Types
				tTile = aThisTile.popTile ();
				if (aThisRotation != NO_ROTATION)  {
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
		
		tSameTypeCount = (aGameTile.getTypeCount () == getTypeCount ());

		return tSameTypeCount;
	}
	
	public void rotateTileRight (int aSteps) {
		if (isTileOnCell ()) {
			if (! tileOrientLocked) {
				if (aSteps > 0) {
					tileOrient = (tileOrient + aSteps) % 6;
				}
			} else {
				System.err.println ("The Tile Orientation is Locked on MapCell " + getID ());
			}
		} else {
			System.err.println ("No Tile found on this Map Cell " + getID ());
		}
	}
	
	public void rotateTileRight () {
		rotateTileRight (1);
	}
	
	public void removeTile () {
		setTileOrientation (NO_ORIENTATION);
		setTileNumber (0);
		setTile (null);
	}
	
    public void paintComponent (Graphics g, Hex aHex) {
		RevenueCenter tRC1;
		Color thickFrame;
		String tTileName = TileName.NO_NAME2;
		String tBaseTileName;
		String tCityInfoName;
		int Xoffset, Yoffset;
		int tNIndex;
		Xoffset = 0;
		Yoffset = 0;
		
		tRC1 = getRevenueCenter (0);
        if (isTileOnCell ()) {
			tTileName = tile.getName ();
			tile.paintComponent (g, XCenter, YCenter, tileOrient, aHex, selectedFeature2);
			if (blockedSides != null) {
				aHex.drawBorders (g, XCenter, YCenter, baseTerrain.drawBorder (), blockedSides);
			}
			if (isStartingTile ()) {
				if (tRC1 != null) {
					if (terrain1 != null) {
						if (terrain1.isRiver ()) {
							if (tRC1.isCenterLocation ()) {
								Yoffset = aHex.getTrackWidth () * 4;
							}
						}
						drawTerrain (g, terrain1, aHex, Xoffset, Yoffset);
					}
				}
				if (terrain2 != null) {
					if (terrain2.isRiver ()) {
						if (terrain1.getLocation () == terrain2.getLocation ()) {
							Yoffset = aHex.getTrackWidth () * 2;
						}
					}
					drawTerrain (g, terrain2, aHex, Xoffset, Yoffset);
				}
			} else {
				if (terrain1 != null) {
					if (terrain1.bleedThroughAll ()) {
						drawTerrain (g, terrain1, aHex, Xoffset,Yoffset);
					}
				}
				if (terrain2 != null) {
					if (terrain2.bleedThroughAll ()) {
						drawTerrain (g, terrain2, aHex, Xoffset,Yoffset);
					}
				}
			}
		} else {
			if ("OO".equals (baseTileName.getName ())) {
				thickFrame = new TileType (TileType.YELLOW, false).getColor ();
			} else {
				thickFrame = null;
			}
			aHex.paintHex (g, XCenter, YCenter, baseTerrain.getColor (), baseTerrain.drawBorder (), 
						   thickFrame, blockedSides);
			if (tRC1 != null) {
				if (terrain1 != null) {
					if (terrain1.isRiver ()) {
						if (tRC1.isCenterLocation ()) {
							Yoffset = aHex.getTrackWidth () * 4;
						}
					}
				}
			}
			drawTerrain (g, terrain1, aHex, Xoffset, Yoffset);
			if (terrain2 != null) {
				if (terrain2.isRiver ()) {
					if (terrain1.getLocation () == terrain2.getLocation ()) {
						Yoffset = aHex.getTrackWidth () * 2;
					}
				}
			}
			drawTerrain (g, terrain2, aHex, Xoffset, Yoffset);
			centers.draw (g, XCenter, YCenter, aHex, NOT_ON_TILE, selectedFeature2);
			if (rebate != null) {
				rebate.draw (g, XCenter, YCenter, aHex);
			}
		}
		if (baseTileName != null) {
			tBaseTileName = baseTileName.getName ();
			if (tRC1 != null) {
				tCityInfoName = tRC1.getCIName ();
				if (! tBaseTileName.equalsIgnoreCase (tCityInfoName)) {
					if (! tTileName.equalsIgnoreCase (tBaseTileName)) {
						baseTileName.draw (g, XCenter, YCenter, aHex);
					}
				}
				if (! tTileName.equalsIgnoreCase (tCityInfoName)) {
					tRC1.drawName (g, XCenter, YCenter, aHex);
				}
			}
		}
				
		if (selected) {
			aHex.paintSelected (g, XCenter, YCenter);
		}
		
		for (tNIndex = 0; tNIndex < 6; tNIndex++) {
			if (neighbors [tNIndex] != null) {
				if (neighbors [tNIndex].isSelected ()) {
					paintAsNeighbor (g, aHex, tNIndex);
				}
			}
		}
    }
	
	public void paintAsNeighbor (Graphics g, Hex aHex, int aSide) {
		aHex.drawNeighbor (g, aSide, XCenter, YCenter);
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
	
	private void setAllValues (int Xc, int Yc, HexMap aHexMap, int aBaseTerrain, Tile aTile, 
								int aTileOrient, String aBaseName, String aBlockedSides) {
		int tIndex;
		
		setXY (Xc, Yc);
		hexMap = aHexMap;
        setOtherValues (aBaseTerrain, aTile, aTileOrient, aBaseName, aBlockedSides);
		allowedRotations = new boolean [6];
		for (tIndex = 0; tIndex < 6; tIndex++) {
			allowedRotations [tIndex] = false;
			neighbors [tIndex] = null;
		}
		tileOrientLocked = false;
		selectedFeature2 = new Feature2 ();
    }
	
	public void setCityInfo (CityInfo aCityInfo) {
		centers.setCityInfo (aCityInfo);
		
		if (tile != null) {
			tile.setCityInfo (aCityInfo);
		}
	}
	
	public void setCorporation (Corporation aCorporation, Location aLocation) {
		Location tNewLocation;
		
		centers.setCorporationBase (aCorporation, aLocation);
		centers.setMapCell (this);
		if (isTileOnCell ()) {
			if (tile != null) {
				tNewLocation = aLocation.rotateLocation (-tileOrient);
				tile.setCorporationBase (aCorporation, tNewLocation);
				tile.setMapCell (this);
			}
		}
	}
	
	public void setEmptyMapCell (int aBaseTerrain) {
		setOtherValues (aBaseTerrain, NO_TILE, NO_ORIENTATION, NO_NAME, NO_BLOCKED_SIDES);
	}
	
	public void setMapDirection (String aMapDirection) {
		if (aMapDirection == null) {
			mapDirection = false;
		} else {
			if (aMapDirection.equals ("NS")) {
				mapDirection = false;
			} else {
				if (aMapDirection.equals ("EW")) {
					mapDirection = true;
				} else {
					mapDirection = false;
				}
			}
		}
	}
	
	public void setNeighbor (int aSide, MapCell aNeighbor) {
		if (neighbors [aSide] == null) {
			neighbors [aSide] = aNeighbor;
			aNeighbor.setNeighbor ((aSide + 3) % 6, this);
		}
	}
	
	public void setOtherValues (int aBaseTerrain, Tile aTile, int aTileOrient,  
								String aBaseName, String aBlockedSides) {
		int tBlockedIndex;
		String tSideNames [] = {"A", "B", "C", "D", "E", "F"};
		int tIndex;
		
		centers = new Centers ();
		selectedFeatures = new LinkedList<Feature2> ();
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
					tIndex = aBlockedSides.indexOf (tSideNames[tBlockedIndex]);
					if (tIndex >= 0) {
						blockedSides [tBlockedIndex] = true;
					} else {
						blockedSides [tBlockedIndex] = false;
					}
				}
			}
		}
		terrain1 = null;
		terrain2 = null;
		baseTerrain = new Terrain (aBaseTerrain);
        putTile (aTile, aTileOrient);
		clearSelected ();
		startingTileNumber = Tile.NOT_A_TILE;
		startingTile = false;
		rebate = null;
    }
	
	public void setScale (int hexScale, Hex aHex) {
		aHex.setScale (hexScale);
	}
	
	public void setSelectedFeature2 (Location aLocation) {
		if (selectedFeature2 != null) {
			selectedFeature2.setLocation (aLocation);
		}
	}
	
	public void setSelectedFeature2 (Location aLocation, Location aLocation2) {
		selectedFeature2.setLocation (aLocation);
		selectedFeature2.setLocation2 (aLocation2);
	}
	
	// Used to Undo Tile Upgrade that had Tokens
	public void setStationAt (ShareCompany aShareCompany, int aStationIndex, int aCityIndex) {
		City tCity;
		MapToken tMapToken;
		
		tMapToken = aShareCompany.popToken ();
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
		Color tTerrainFillColor = null;
		
		if (!(aTerrainFillColor == null)) {
			if (aTerrainFillColor.equals ("white")) {
				tTerrainFillColor = Color.white;
			} else if (aTerrainFillColor.equals ("black")) {
				tTerrainFillColor = Color.black;
			} else if (aTerrainFillColor.equals ("hollow")) {
				tTerrainFillColor = null;
			} else if (aTerrainFillColor.equals ("orange")) {
				tTerrainFillColor = Color.orange;
			}
		}
		setTerrainFillColor (tTerrainFillColor);
	}
	
	public void setTerrainFillColor (Color aTerrainFillColor) {
		terrainFillColor = aTerrainFillColor;
	}
	
	public RevenueCenter setupRevenueCenter (int aType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
		RevenueCenterType tRCType = new RevenueCenterType (aType);
		
		return setupRevenueCenter (tRCType, aID, aLocation, aName, aValue, aTileType);
	}
	
	public RevenueCenter setupRevenueCenter (RevenueCenterType tRevenueCenterType, int aID, int aLocation, 
											 String aName, int aValue, TileType aTileType) {
		RevenueCenter tRevenueCenter;
		int tStationCount = tRevenueCenterType.getStationCount ();
		
		if (tRevenueCenterType.isDotTown()) {
			tRevenueCenter = new Town (tRevenueCenterType, aID, aLocation, aName, aValue, aTileType);
		} else if (tRevenueCenterType.isTown()) {
			tRevenueCenter = new Town (tRevenueCenterType, aID, aLocation, aName, aValue, aTileType);
		} else if (tRevenueCenterType.isCity()) {
			tRevenueCenter = new City (tRevenueCenterType, tStationCount, aID, aLocation, aName, aValue, aTileType);
		} else {
			tRevenueCenter = null;
		}
		
		return (tRevenueCenter);
	}
	
	public void setXY (int Xc, int Yc) {
        XCenter = Xc;
        YCenter = Yc;
	}
	
	public void swapTokens () {
		if (isTileOnCell ()) {
			tile.swapTokens (this);
		}
	}
	
	public void toggleSelected () {
		selected = !selected;
	}
	
	public void upgradeTile (TileSet aTileSet, Tile aNewTile) {
		int tCurrentTileOrient;
		int tCurrentTileNumber;
		int tUpgradeToTileNumber;
		int tRotation;
		int tRotationCount;
		int tRotationIndex;
		int tCityCenterCount;
		int tCityCenterIndex;
		int tUpgradeRotation;
		int tFirstPossibleRotation;
		int tStationIndex;
		Location tOldCityLocation, tNewCityLocation;
		RevenueCenter tRevenueCenter;
		City tCity;
		City tTileCity;
		Corporation tBaseCorporation;
		GameTile tCurrentGameTile;
		Tile tCurrentTile;
		Upgrade tUpgrade;
		MapToken tMapToken;
		boolean tAllowedRotations [] = new boolean [6];
		
		tCurrentTile = getTile ();
		tCurrentTileOrient = getTileOrient ();	// Identify current rotation of Tile on Map Cell
		tCurrentTileNumber = getTileNumber ();	// Find Current Tile Number and the Current Game Tile
		tCurrentGameTile = aTileSet.getGameTile (tCurrentTileNumber);
		
		for (tRotationIndex = 0; tRotationIndex < 6; tRotationIndex++) {
			tAllowedRotations [tRotationIndex] = false;
		}
		tFirstPossibleRotation = MapCell.NO_ROTATION;
		tUpgradeToTileNumber = aNewTile.getNumber (); // get New Tile's Number
		tRotation = NO_ROTATION;
		tUpgrade = GameTile.NO_UPGRADE;
		if (tCurrentGameTile != GameTile.NO_GAME_TILE) {
			// Determine possible rotations of new Tile that replaces all Existing Track on previous Tile
			// Add an Allowed Rotations if all Tracks can Exit based on 'canAllTracksExit' Method
			// Once the first Rotation that is allowed is found, Save it.
			tUpgrade = tCurrentGameTile.getUpgradeTo (tUpgradeToTileNumber);
			tRotationCount = tUpgrade.getRotationCount ();
			for (tRotationIndex = 0; tRotationIndex < tRotationCount; tRotationIndex++) {
				tRotation = tUpgrade.getRotation (tRotationIndex);
				tUpgradeRotation = (tRotation + tCurrentTileOrient) % 6;
				tAllowedRotations [tUpgradeRotation] = canAllTracksExit (aNewTile, tUpgradeRotation);
				if (tAllowedRotations [tUpgradeRotation]) {
					if (tFirstPossibleRotation == NO_ROTATION) {
						tFirstPossibleRotation = tUpgradeRotation;
					}
				}
			}
		}
		
		if (tUpgrade == GameTile.NO_UPGRADE) {
			restoreTile (aTileSet, aNewTile);
			System.err.println ("No Upgrade available -- Aborting Upgrade");
			return;
		}
		
		if (tFirstPossibleRotation == NO_ROTATION) {
			restoreTile (aTileSet, aNewTile);
			System.err.println ("No Rotation allows all Tracks to Exit -- Aborting Upgrade");
			return;
		}
		
		// For the Tile on Map, find Revenue Centers, and Tokens on them. Place them onto the Tile to be Placed.
		// If the Revenue Center has a Base Corporation without the Base Token, Transfer Base Corporation
		// To the Tile to be Placed.
		tCityCenterCount = tCurrentTile.getCityCenterCount ();
		if (tCityCenterCount > 0) {
			for (tCityCenterIndex = 0; tCityCenterIndex < tCityCenterCount; tCityCenterIndex++) {
				tRevenueCenter = tCurrentTile.getRevenueCenter (tCityCenterIndex);
				if (tRevenueCenter.isCity ()) {
					tCity = (City) tCurrentTile.getRevenueCenter (tCityCenterIndex);
					tOldCityLocation = tCity.getLocation ();
					tNewCityLocation = tUpgrade.getToFromLocation (tOldCityLocation, tRotation);
					if (tCity.hasToken ()) {
						for (tStationIndex = 0; tStationIndex < tCity.getStationCount (); tStationIndex++) {
							tMapToken = tCity.getToken (tStationIndex);
							tTileCity = (City) aNewTile.getCenterAtLocation (tNewCityLocation);
							if ((tTileCity != null)  && (tMapToken != null)) {
								tTileCity.setStation (tMapToken);
							} else {
								if (tTileCity == null) {
									System.err.println ("===Do not have Tile City");
								} else {
									System.err.println ("===Do not have Map Token");
								}
							}
						}
					} else {
						if (tCity.isCorporationBase ()) {
							tBaseCorporation = tCity.getCorporation ();
							aNewTile.setCorporationBase (tBaseCorporation, tNewCityLocation);
							aNewTile.setMapCell (this);
						}
					}
				}
			}
		}
		restoreTile (aTileSet, tCurrentTile);
		
		// Add Tile in first Possible Orientation
		for (tRotationIndex = 0; tRotationIndex < 6; tRotationIndex++) {
			setAllowedRotation (tRotationIndex, tAllowedRotations [tRotationIndex]);
		}
		setTile (aNewTile);
		setTileOrientationLocked (false);
		setTileInfo (aNewTile.getNumber (), tFirstPossibleRotation, false);
	}

	public void restoreTile (TileSet aTileSet, Tile aCurrentTile) {
		GameTile tCurrentGameTile;
		int tCurrentTileNumber = aCurrentTile.getNumber ();
		
		// Remove Tile from Map Cell, Clear all City Info and Stations, and place it back on TileSet
		tCurrentGameTile = aTileSet.getGameTile (tCurrentTileNumber);
		aCurrentTile.clearAllCityInfoCorporations ();
		aCurrentTile.clearAllCityInfoMapCells ();
		aCurrentTile.clearAllCityInfoRevenueCenters ();
		aCurrentTile.clearAllStations ();
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
			iXC1 = iMap1.getXCenter();
			iYC1 = iMap1.getYCenter();
			if ((iXC0 == iXC1) && (iYC0 == iYC1)) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return -1;
		}
	}
	
	public boolean equals (Object arg) {
		if (compare (this, arg) == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		assert false : "hashCode not designed";
		return 42; // any arbitrary constant will do
	}
	
	public int getCostToLayTile () {
		int tCostToLayTile = 0;
		
		if (baseTerrain != Terrain.NO_TERRAIN_FEATURE) {
			tCostToLayTile += baseTerrain.getCost ();
		}
		if (terrain1 != Terrain.NO_TERRAIN_FEATURE) {
			tCostToLayTile += terrain1.getCost ();
		}
		if (terrain2 != Terrain.NO_TERRAIN_FEATURE) {
			tCostToLayTile += terrain2.getCost ();
		}
		
		return tCostToLayTile;
	}

	public String getBasePrivateAbbrev (CorporationList privateCos) {
		String tPrivateAbbrev = "";
		PrivateCompany tPrivateCompany;
		
		if (privateCos != CorporationList.NO_CORPORATION_LIST) {
			tPrivateCompany = privateCos.getPrivateCompanyAtMapCell (this);
			if (tPrivateCompany != CorporationList.NO_CORPORATION) {
				tPrivateAbbrev = tPrivateCompany.getAbbrev ();
			}
		}
		
		return tPrivateAbbrev;
	}

	public boolean privatePreventsTileLay (CorporationList privateCos, TrainCompany tOperatingTrainCompany) {
		boolean tPrivatePrevents = false;
		PrivateCompany tPrivateCompany;
		
		if (privateCos != CorporationList.NO_CORPORATION_LIST) {
			tPrivateCompany = privateCos.getPrivateCompanyAtMapCell (this);
			if (tPrivateCompany != CorporationList.NO_CORPORATION) {
				// Given this Map Cell is home to a Private Company
				// Then Prevent this Tile Lay if the Private is not Owned by the Operating Train Company
				if (tOperatingTrainCompany.doesNotOwn (tPrivateCompany)) {
					tPrivatePrevents = true;
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
		
		if (aTerrain != null) {
			tTerrainCost = aTerrain.getCost ();
		} else {
			tTerrainCost = 0;
		}
		
		return tTerrainCost;
	}

	public int getCostToLayTile (Tile aTile) {
		int tCostToLay;
		TileType tTileType;
		TileName tTileName;
		int tTileTypeInt;
		int tTotalTerrainCost = getTotalTerrainCost ();
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
}

