package ge18xx.toplevel;

import ge18xx.bank.Bank;

//
//  MapFrame.java
//  rails_18xx
//
//  Created by Mark Smith on 8/24/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.center.City;
import ge18xx.center.CityInfo;
import ge18xx.center.CityList;
import ge18xx.center.RevenueCenter;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.company.MapToken;
import ge18xx.company.TokenCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.map.Terrain;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActionManager;
import ge18xx.round.action.StartRouteAction;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.ExtendRouteAction;
import ge18xx.round.action.RouteAction;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.TileType;
import ge18xx.train.RouteInformation;
import ge18xx.train.RouteSegment;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

//import javax.swing.*;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

public class MapFrame extends XMLFrame implements ActionListener {
	private final String NO_TILE_PLACED = "No Tile placed yet";
	private final String NO_TILE_SELECTED = "No Tile selected yet";
	private final String NOT_PLACE_TILE_MODE = "Place Tile Mode not Active";
	private final String NO_SELECTED_MAP_CELL = "No Map Cell selected yet for tile placement";
	private final String TOKEN_ALREADY_PLACED = "Token already placed";
	private final String NOT_PLACE_TOKEN_MODE = "Place Token Mode not Active";
	private final String NOT_BASE_CORPORATION = "City Selected is a Base Corporation that is not the Operating Company";
	private final String NO_SELECTED_RC = "No Revenue Center Selected for Placing a Token";
	private final String NOT_ENOUGH_CASH = "%s does not have enough Cash, needs %s has %s";
	private final String PRIVATE_NOT_OWNED = "%s does not own the Private Company %s";
	private final String NO_OPERATING_COMPANY = "There is no Operating Company to place a Tile";
	private final String NO_TOOL_TIP = "";
	public static final String NO_COMPANY = "NO_COMPANY";
	private static final long serialVersionUID = 1L;
	HexMap map;
	TileSet tileSet;
	CorporationList privateCos;
	CorporationList coalCos;
	CorporationList minorCos;
	CorporationList shareCos;
	
	boolean placeTileMode;
	boolean placeTokenMode;
	boolean selectRouteMode;
	JButton exitTileButton;
	JButton selectRouteButton;
	JButton putTileButton;
	JButton pickupTileButton;
	JButton exitTokenButton;
	JButton putTokenButton;
	String companyAbbrev;
	Box tokenButtons;
	Box tileButtons;
	Container allButtonContainers;
	JScrollPane scrollPane;
	GameManager gameManager;
	RouteInformation routeInformation;
	private String SELECT_ROUTE_MODE = "Select Route Mode";
	private String CANCEL_TOKEN_MODE = "CancelToken";
	private String CANCEL_MODE_LABEL = "Cancel Mode";
	private String COMPLETE_TILE_LAY = "Complete Tile Lay";
	private String PUT_TILE = "PutTile";
	private String PICKUP_TILE = "PickupTile";
	private String PUT_TOKEN = "PutToken";
	JSlider hexScaleSlider;
	
	public MapFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getActiveGameName ());

		gameManager = aGameManager;
		JPanel tNorthComponents = new JPanel ();
		JLabel tLabelTokenMode = new JLabel ("Token Mode");
		JLabel tLabelTileMode = new JLabel ("Tile Mode");
		
		allButtonContainers = Box.createVerticalBox ();
		tokenButtons = Box.createHorizontalBox ();
		tokenButtons.setOpaque (true);
		tileButtons = Box.createHorizontalBox ();
		tileButtons.setOpaque (true);
		allButtonContainers.add (tokenButtons);
		allButtonContainers.add (Box.createVerticalStrut (10));
		allButtonContainers.add (tileButtons);
		
		map = new HexMap (this);
        scrollPane = new JScrollPane ();
		scrollPane.setViewportView (map);
		add (scrollPane, BorderLayout.CENTER);

		hexScaleSlider = new JSlider (JSlider.HORIZONTAL, 4, 16, 8);
		hexScaleSlider.addChangeListener (map);
		
		//Turn on labels at major tick marks.
		hexScaleSlider.setMajorTickSpacing (4);
		hexScaleSlider.setMinorTickSpacing (1);
		hexScaleSlider.setPaintTicks (true);
		hexScaleSlider.setPaintLabels (true);
		tNorthComponents.add (hexScaleSlider);
		
		tokenButtons.add (tLabelTokenMode);
		tokenButtons.add (Box.createHorizontalStrut (10));
		
		putTokenButton = new JButton ("Put Down");
		putTokenButton.addActionListener (this);
		putTokenButton.setActionCommand (PUT_TOKEN);
		putTokenButton.setEnabled (false);
		putTokenButton.setToolTipText (NOT_PLACE_TOKEN_MODE);
		tokenButtons.add (putTokenButton);
		tokenButtons.add (Box.createHorizontalStrut (10));
		
		exitTokenButton = new JButton (CANCEL_MODE_LABEL);
		exitTokenButton.addActionListener (this);
		exitTokenButton.setActionCommand (CANCEL_TOKEN_MODE);
		exitTokenButton.setToolTipText (NOT_PLACE_TOKEN_MODE);
		tokenButtons.add (exitTokenButton);
		tokenButtons.add (Box.createHorizontalStrut (10));

		tileButtons.add (tLabelTileMode);
		tileButtons.add (Box.createHorizontalStrut (10));

		putTileButton = new JButton ("Put Down");
		putTileButton.addActionListener (this);
		putTileButton.setActionCommand (PUT_TILE);
		putTileButton.setEnabled (false);
		putTileButton.setToolTipText (NOT_PLACE_TILE_MODE);
		tileButtons.add (putTileButton);
		tileButtons.add (Box.createHorizontalStrut (10));
		
		pickupTileButton = new JButton ("Pickup");
		pickupTileButton.addActionListener (this);
		pickupTileButton.setActionCommand (PICKUP_TILE);
		updatePickupTileButton (false, NOT_PLACE_TILE_MODE);
		tileButtons.add (pickupTileButton);
		tileButtons.add (Box.createHorizontalStrut (10));
		
		exitTileButton = new JButton ("Exit Mode");
		exitTileButton.addActionListener (this);
		exitTileButton.setActionCommand (COMPLETE_TILE_LAY);
		exitTileButton.setEnabled (false);
		exitTileButton.setToolTipText (NOT_PLACE_TILE_MODE);
		
		tileButtons.add (exitTileButton);
		tileButtons.add (Box.createHorizontalStrut (10));
		
		tNorthComponents.add (Box.createHorizontalGlue ());
		tNorthComponents.add (allButtonContainers);
		
		selectRouteButton = new JButton ("Enter Select Route Mode");
		selectRouteButton.addActionListener (this);
		selectRouteButton.setActionCommand (SELECT_ROUTE_MODE);
		selectRouteButton.setEnabled (false);
		tNorthComponents.add (Box.createHorizontalGlue ());
		tNorthComponents.add (selectRouteButton);
		
		add (tNorthComponents, BorderLayout.NORTH);
		setPlaceTileMode (false);
		setPlaceTokenMode (false);
		setSelectRouteMode (false);
		setDefaultFrameInfo ();
	}
	
	public GameManager getGameManager () {
		return gameManager;
	}
	
	public int getHexScale () {
		return map.getHexScale ();
	}
	
	public void setHexScaleSlider (int aScale) {
		hexScaleSlider.setValue (aScale);
	}
	
	@Override
	public void setHexScale (int aScale) {
		map.setHexScale (aScale);
		setHexScaleSlider (aScale);
	}
	
	private void setModes (boolean aTileMode, boolean aTokenMode, boolean aRouteMode) {
		setSelectRouteMode (aRouteMode);
		setPlaceTokenMode (aTokenMode);
		setPlaceTileMode (aTileMode);
	}

	public void togglePlaceTileMode () {
		setModes (! placeTileMode, false, false);
		map.setSingleMapCellSelect (placeTileMode);
		tileSet.setSingleTileSelect (placeTileMode);
		map.clearAllSelected ();
	}
	
	public void togglePlaceTokenMode () {
		setModes (false, ! placeTokenMode, false);
		map.setSingleMapCellSelect (false);
		map.clearAllSelected ();
	}

	public void enterSelectRouteMode (RouteInformation aRouteInformation) {
		toggleSelectRouteMode ();
		routeInformation = aRouteInformation;
	}
	
	public void exitSelectRouteMode () {
		setSelectRouteMode (false);
		selectRouteButton.setEnabled (selectRouteMode);
		map.clearAllSelected ();
	}
	
	public void toggleSelectRouteMode () {
		setSelectRouteMode (! selectRouteMode);
		selectRouteButton.setEnabled (selectRouteMode);
		map.clearAllSelected ();
	}
	
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction = aActionEvent.getActionCommand ();
		Corporation tCorporation;

		tCorporation = getOperatingCompany ();

		if (COMPLETE_TILE_LAY.equals (tTheAction)) {
			completeBenefitUse ();
			togglePlaceTileMode ();
		} else if (SELECT_ROUTE_MODE.equals (tTheAction)) {
			toggleSelectRouteMode ();
		} else if (CANCEL_TOKEN_MODE.equals (tTheAction)) {
			togglePlaceTokenMode ();
		} else if (PICKUP_TILE.equals (tTheAction)) {
			pickupTile ();
		} else if (PUT_TILE.equals (tTheAction)) {
			putTileDownOnMap ();
		} else if (PUT_TOKEN.equals (tTheAction)) {
			putATokenDown (tCorporation);
		}
		if (tCorporation != CorporationList.NO_CORPORATION) {
			tCorporation.updateFrameInfo ();
		}
	}

	private void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}
	
	private void pickupTile () {
		Corporation tCorporation;
		
		tCorporation = getOperatingCompany ();
		tCorporation.clearBankSelections ();
		tCorporation.undoAction ();
		updatePickupTileButton (false, NO_TILE_PLACED);
		updatePutTileButton ();
	}

	private void putTileDownOnMap () {
		Corporation tCorporation;
		MapCell tMapCell;
		Tile tTile;
		int tOrientation;
		Tile tPreviousTile;
		int tPreviousOrientation;
		String tPreviousTokens;
		String tPreviousBases;
		
		tMapCell = map.getSelectedMapCell ();
		tPreviousTile = tMapCell.getTile ();
		if (tPreviousTile != Tile.NO_TILE) {
			tPreviousOrientation = tMapCell.getTileOrient ();
			tPreviousTokens = tPreviousTile.getPlacedTokens ();
			tPreviousBases = tPreviousTile.getCorporationBases ();
		} else {
			tPreviousOrientation = 0;
			tPreviousTokens = "";
			tPreviousBases = "";
		}
		// Save Tokens from Previous Tile placement
		map.putTileDown ();
		updatePickupTileButton (true, NO_TOOL_TIP);
		tTile = tMapCell.getTile ();
		tOrientation = tMapCell.getTileOrient ();
		tCorporation = getOperatingCompany ();
		if (tCorporation != null) {
			tCorporation.tileWasPlaced (tMapCell, tTile, tOrientation, 
					tPreviousTile, tPreviousOrientation, 
					tPreviousTokens, tPreviousBases);
		}
		tileSet.clearAllSelected ();
		updatePutTileButton ();
		toFront ();
	}
	
	private void completeBenefitUse () {
		Corporation tCorporation;
		
		tCorporation = getOperatingCompany ();
		tCorporation.completeBenefitUse ();
	}
	
	private void updatePickupTileButton (boolean aEnabled, String aToolTip) {
		pickupTileButton.setEnabled (aEnabled);
		pickupTileButton.setToolTipText (aToolTip);
	}
	
	public XMLElement createMapDefinitions (XMLDocument aXMLDocument) {
		return (map.createElement (aXMLDocument));
	}
	
	public HexMap getMap () {
		return map;
	}
	
	public XMLElement getMapStateElements (XMLDocument aXMLDocument) {
		return (map.getMapStateElements (aXMLDocument));
	}
	
	public int getMaxRowCount () {
		return map.getMaxRowCount ();
	}
	
	public int getMaxColCount () {
		return map.getMaxColCount ();
	}
	
	public Terrain getTerrain () {
		return map.getTerrain ();
	}
	
	public TrainCompany getOperatingTrainCompany () {
		TrainCompany tTrainCompany = (TrainCompany) CorporationList.NO_CORPORATION;
		TrainCompany tCorporation;
		
		if (coalCos != CorporationList.NO_CORPORATION_LIST) {
			tCorporation = coalCos.getOperatingTrainCompany ();
			if (tCorporation != CorporationList.NO_CORPORATION) {
				tTrainCompany = tCorporation;
			}
		}
		if (minorCos != CorporationList.NO_CORPORATION_LIST) {
			tCorporation = minorCos.getOperatingTrainCompany ();
			if (tCorporation != CorporationList.NO_CORPORATION) {
				tTrainCompany = tCorporation;
			}
		}
		if (shareCos != CorporationList.NO_CORPORATION_LIST) {
			tCorporation = shareCos.getOperatingTrainCompany ();
			if (tCorporation != CorporationList.NO_CORPORATION) {
				tTrainCompany = tCorporation;
			}
		}
		
		return tTrainCompany;
	}
	
	public TokenCompany getTokenCompany (String aAbbrev) {
		TokenCompany tTokenCompany;
		Corporation tCorporation;
		
		tTokenCompany = TokenCompany.NO_TOKEN_COMPANY;
		if (coalCos != CorporationList.NO_CORPORATION_LIST) {
			tCorporation = coalCos.getCorporation (aAbbrev);
			if (tCorporation != CorporationList.NO_CORPORATION) {
				tTokenCompany = (TokenCompany) tCorporation;
			}
		}
		if (tTokenCompany == TokenCompany.NO_TOKEN_COMPANY) {
			if (minorCos != CorporationList.NO_CORPORATION_LIST) {
				tCorporation = minorCos.getCorporation (aAbbrev);
				if (tCorporation != CorporationList.NO_CORPORATION) {
					tTokenCompany = (TokenCompany) tCorporation;
				}
			}
		}
		if (tTokenCompany == TokenCompany.NO_TOKEN_COMPANY) {
			if (shareCos != CorporationList.NO_CORPORATION_LIST) {
				tCorporation = shareCos.getCorporation (aAbbrev);
				if (tCorporation != CorporationList.NO_CORPORATION) {
					tTokenCompany = (TokenCompany) tCorporation;
				}
			}
		}

		return tTokenCompany;
	}

	public boolean isPlaceTokenMode () {
		return placeTokenMode;
	}
	
	public boolean isPlaceTileMode () {
		return placeTileMode;
	}
	
	public  boolean isSelectRouteMode () {
		return selectRouteMode;
	}
	
	public void loadMapStates (XMLNode aMapNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (mapStateParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aMapNode, MapCell.EN_MAP_CELL);
	}
	
	ParsingRoutineI mapStateParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aMapCellNode) {
			map.loadMapCellState (aMapCellNode);
		}
	};


	public boolean loadXML (String aXMLFileName, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;
		int tMaxWidth, tMaxHeight;
		
		tXMLFileWasLoaded = super.loadXML (aXMLFileName, aLoadableObject);
		if (tXMLFileWasLoaded) {
			setFixedMapTiles ();
		}
		tMaxWidth = map.getMaxWidth ();
		tMaxHeight = map.getMaxHeight ();
        scrollPane.setPreferredSize (new Dimension (tMaxWidth, tMaxHeight));

		return tXMLFileWasLoaded;
	}
	
	public boolean loadXMLColorScheme (String aXMLFileName, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;
		
		tXMLFileWasLoaded = super.loadXML (aXMLFileName, aLoadableObject);
		
		return tXMLFileWasLoaded;
	}

	public boolean hasCityBeenSelected () {
		boolean tHasCityBeenSelected = false;
		MapCell tSelectedMapCell;
		RevenueCenter tSelectedRevenueCenter;
		
		tSelectedMapCell = map.getSelectedMapCell ();
		if (tSelectedMapCell != null) {
			tSelectedRevenueCenter = tSelectedMapCell.getSelectedRevenueCenter ();
			if (tSelectedRevenueCenter != null) {
				tHasCityBeenSelected = true;
			}
		}

		return tHasCityBeenSelected;
	}

	public void putATokenDown (Corporation aCorporation) {
		putTokenDown (aCorporation);
		togglePlaceTokenMode ();
	}
	
	public void putTokenDown (Corporation aCorporation) {
		MapCell tSelectedMapCell;
		RevenueCenter tSelectedRevenueCenter;
		
		tSelectedMapCell = map.getSelectedMapCell ();
		if (tSelectedMapCell != MapCell.NO_MAP_CELL) {
			tSelectedRevenueCenter = tSelectedMapCell.getSelectedRevenueCenter ();
			if (tSelectedRevenueCenter != RevenueCenter.NO_CENTER) {
				putTokenDownHere (aCorporation, tSelectedMapCell, tSelectedRevenueCenter);
			} else {
				System.err.println ("No Revenue Center Selected from Map Cell");
			}
		} else {
			System.err.println ("No Map Cell Selected from Frame");
		}		
	}

	public void putTokenDownHere (Corporation aCorporation, MapCell aMapCell, 
								RevenueCenter aRevenueCenter) {
		City tSelectedCity;
		boolean tCanPlaceToken;
		
		if (aCorporation != CorporationList.NO_CORPORATION) {
			setCompanyAbbrev (aCorporation.getAbbrev ());
			if (aRevenueCenter != RevenueCenter.NO_CENTER) {
				if (aRevenueCenter.canPlaceStation ()) {
					tSelectedCity = (City) aRevenueCenter;
					tCanPlaceToken = canPlaceToken (aCorporation, tSelectedCity);
					if (tCanPlaceToken) {
						putMapTokenDown (aCorporation, tSelectedCity, aMapCell, true);
					}
				} else {
					System.err.println ("Cannot Place Station on this Revenue Center");
				}
			}
		} else {
			System.err.println ("No Operating Company Found ");
		}
	}

	public void putMapTokenDown (Corporation aCorporation, City aCity, MapCell aMapCell, 
								boolean aAddLayTokenAction) {
		Tile tTile;
		MapToken tMapToken;
		boolean tTokenPlaced;
		Corporation tBaseCorporation;
		int tRevenueCenterIndex;
		int tCorporationID;
		
		tBaseCorporation = (Corporation) aCity.getTokenCorporation ();
		tMapToken = aCorporation.getMapToken ();
		if (tMapToken == null) {
			System.err.println ("Company has no tokens to place");
		} else {
			tTokenPlaced = aCity.setStation (tMapToken);
			if (tTokenPlaced) {
				tCorporationID = aCorporation.getID ();
				tTile = aMapCell.getTile ();
				tRevenueCenterIndex = tTile.getStationIndex (tCorporationID);
				aCorporation.tokenWasPlaced (aMapCell, tTile, tRevenueCenterIndex, aAddLayTokenAction);
				completeBenefitUse ();
				putTokenButton.setEnabled (false);
				putTokenButton.setToolTipText (TOKEN_ALREADY_PLACED);
				// If we have placed the Token and there was a Base Corporation Tile, clear out any other Bases for this Corporation from this Tile
				// Primarily for EIRE that starts with a choice of two spots in the Tile.
				if (tBaseCorporation == aCorporation) {
					tTile = aMapCell.getTile ();
					if (tTile != null) {
						tTile.clearCorporation (aCorporation);
					}
				}
			} else {
				System.err.println ("Token Placement Failed.");
			}
			map.clearAllSelected ();
		}
	}
	
	public boolean canPlaceToken (Corporation aCorporation, City aSelectedCity) {
		boolean tCanPlaceToken = false;
		Corporation tBaseCorporation;
		String tBaseAbbrev, tCorporationAbbrev;
		
		if (aSelectedCity != City.NO_CITY) {
			tBaseCorporation = (Corporation) aSelectedCity.getTokenCorporation ();
			if (tBaseCorporation == null) {
				tCanPlaceToken = true;
			} else {
				tBaseAbbrev = tBaseCorporation.getAbbrev ();
				tCorporationAbbrev = aCorporation.getAbbrev ();
				if (tBaseAbbrev.equals (tCorporationAbbrev)) {
					if (aSelectedCity.getFreeStationCount () > 0) {
						tCanPlaceToken = true;
					}
				} else {
					if ((aSelectedCity.getMaxStations () - aSelectedCity.getFreeStationCount ()) > 0) {
						tCanPlaceToken = true;
					}
				}
			}
		}
		
		return tCanPlaceToken;
	}
	
	public void setCityInfo (CityList aCityList) {
		int rowIndex;
		int colIndex;
		int maxRow;
		int maxCol;
		int tRevenueCenterID;
		CityInfo tCityInfo;
		MapCell tMapCell;
		
		if (aCityList != null) {
			maxRow = map.getRowCount ();
			for (rowIndex = 0; rowIndex < maxRow; rowIndex++) {
				maxCol = map.getColCount (rowIndex);
				for (colIndex = 0; colIndex < maxCol; colIndex++) {
					tRevenueCenterID = map.getRevenueCenterID (rowIndex, colIndex);
					if (tRevenueCenterID != RevenueCenter.NO_ID) {
						tCityInfo = aCityList.getCityInfo (tRevenueCenterID);
						tMapCell = map.getMapCell (rowIndex, colIndex);
						if (tCityInfo != null) {
							tCityInfo.setMapCell (tMapCell);
						}
						tMapCell.setCityInfo (tCityInfo);
					}
				}
			}
		}
	}
	
	public Corporation getOperatingCompany () {
		Corporation tCorporation;
		
		tCorporation = gameManager.getOperatingCompany ();

		return tCorporation;
	}
	
	public void setCorporationList (CorporationList aCorporationList, ElementName aType) {
		if (aType.equals (CorporationList.TYPE_NAMES [0])) {
			privateCos = aCorporationList;
		} else if (aType.equals (CorporationList.TYPE_NAMES [1])) {
			coalCos = aCorporationList;
		} else if (aType.equals (CorporationList.TYPE_NAMES [2])) {
			minorCos = aCorporationList;
		} else if (aType.equals (CorporationList.TYPE_NAMES [3])) {
			shareCos = aCorporationList;
		}
	}
	
	public void setFixedMapTiles () {
		int rowIndex;
		int colIndex;
		int maxRow;
		int maxCol;
		int tTileNumber;
		Tile tTile;
		
		maxRow = map.getRowCount ();
		for (rowIndex = 0; rowIndex < maxRow; rowIndex++) {
			maxCol = map.getColCount (rowIndex);
			for (colIndex = 0; colIndex < maxCol; colIndex++) {
				if (map.isTileOnCell (rowIndex, colIndex)) {
					tTileNumber = map.getTileNumber (rowIndex, colIndex);
					tTile = tileSet.popTile (tTileNumber);
					if (tTile != null) {
						map.putStartingTile (rowIndex, colIndex, tTile);
					}
				}
			}
		}
	}
	
	public void setHomeCities (CorporationList aCorporationList) {
		int tCorporationIndex;
		int tMaxCorporations;
		MapCell tMapCell;
		Corporation tCorporation;
		ShareCompany tShareCompany;
		String tCellID;
		Location tLocation;
		
		if (aCorporationList != null) {
			tMaxCorporations = aCorporationList.getRowCount ();
			if (tMaxCorporations > 0) {
				for (tCorporationIndex = 0; tCorporationIndex < tMaxCorporations; tCorporationIndex++) {
					tCorporation = aCorporationList.getCorporation (tCorporationIndex);
					tCellID = tCorporation.getHomeCityGrid1 ();
					tLocation = tCorporation.getHomeLocation1 ();
					tMapCell = map.getMapCellForID (tCellID);
					if (tMapCell != null) {
						tCorporation.setHome1 (tMapCell, tLocation);
						tMapCell.setCorporation (tCorporation, tLocation);
					}
					tCellID = tCorporation.getHomeCityGrid2 ();
					tLocation = tCorporation.getHomeLocation2 ();
					if (! tLocation.isNoLocation ()) {
						if (tCellID != Corporation.NO_NAME_STRING) {
							tMapCell = map.getMapCellForID (tCellID);
						}
						if (tMapCell != null) {
							tCorporation.setHome2 (tMapCell, tLocation);
							tMapCell.setCorporation (tCorporation, tLocation);
						}
					}
					if (tCorporation instanceof ShareCompany) {
						tShareCompany = (ShareCompany) tCorporation;
						tLocation = tShareCompany.getDestinationLocation ();
						if (tLocation != ShareCompany.NO_DESTINATION_LOCATION) {
							if (! tLocation.isNoLocation ()) {
								tCellID = tShareCompany.getDestination ();
								if (tCellID != Corporation.NO_NAME_STRING) {
									tMapCell = map.getMapCellForID (tCellID);
									if (tMapCell != null) {
										tShareCompany.setDestination (tMapCell, tLocation);
										tMapCell.setCorporation (tShareCompany, tLocation);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void setPlaceTileMode (boolean aMode) {
		placeTileMode = aMode;
		map.setPlaceTileMode (placeTileMode);
		exitTileButton.setEnabled (aMode);
		if (aMode) {
			exitTileButton.setToolTipText (NO_TOOL_TIP);
			tileButtons.setBackground (Color.ORANGE);
		} else {
			exitTileButton.setToolTipText (NOT_PLACE_TILE_MODE);
			tileButtons.setBackground (getBackground ());
		}
		putTileButton.setEnabled (false);
		if (placeTileMode) {
			updatePickupTileButton (false, NO_TILE_PLACED);
			putTileButton.setToolTipText (NO_SELECTED_MAP_CELL);
		} else {
			MapCell tSelectedMapCell;
			updatePickupTileButton (false, NOT_PLACE_TILE_MODE);
			putTileButton.setToolTipText (NOT_PLACE_TILE_MODE);
			
			tSelectedMapCell = map.getSelectedMapCell ();
			if (tSelectedMapCell != null) {
				tSelectedMapCell.lockTileOrientation ();
			}
		}
	}
	
	public void setPlaceTokenMode (boolean aMode) {
		placeTokenMode = aMode;
		exitTokenButton.setEnabled (aMode);
		putTokenButton.setEnabled (false);
		if (aMode) {
			exitTokenButton.setToolTipText (NO_TOOL_TIP);
			putTokenButton.setToolTipText (NO_SELECTED_RC);
			tokenButtons.setBackground (Color.ORANGE);
		} else {
			exitTokenButton.setToolTipText (NOT_PLACE_TOKEN_MODE);
			putTokenButton.setToolTipText (NOT_PLACE_TOKEN_MODE);
			tokenButtons.setBackground (getBackground ());
		}
		map.setSelectTrackSegment (aMode);
		map.setSelectRevenueCenter (aMode);
	}

	public void setSelectRouteMode (boolean aMode) {
		selectRouteMode = aMode;
		if (selectRouteMode) {
			selectRouteButton.setText ("Exit Select Route Mode");
		} else {
			selectRouteButton.setText ("Enter Select Route Mode");
//			if (routeInformation != RouteInformation.NO_ROUTE_INFORMATION) {
////				routeInformation.enableAllSelectRoutes ();
//				routeInformation.setTrainCurrentRouteInformation ();
//			}
		}
		map.setSelectTrackSegment (aMode);
		map.setSelectRevenueCenter (aMode);
	}

	public void setTileSet (TileSet aTileSet) {
		tileSet = aTileSet;
		map.setTileSet (aTileSet);
	}
	
	public void updatePutTokenButton (City aSelectedCity) {
		boolean tCitySelected = hasCityBeenSelected ();
		boolean tCanPlaceToken;
		Corporation tCorporation;
		
		tCorporation = getOperatingTrainCompany ();
		tCanPlaceToken = canPlaceToken (tCorporation, aSelectedCity);
		if (tCanPlaceToken) {
			putTokenButton.setEnabled (tCitySelected);
			if (tCitySelected) {
				putTokenButton.setToolTipText (NO_TOOL_TIP);
			} else {
				putTokenButton.setToolTipText (NO_SELECTED_RC);
			}
		} else {
			putTokenButton.setEnabled (false);
			putTokenButton.setToolTipText (NOT_BASE_CORPORATION);
		}
	}
	
	public boolean validUpgradeType (MapCell aMapCell, GameTile aGameTile) {
		boolean tValidUpgradeType = false;
		TileType tSelectedTileType;
		
		tSelectedTileType = aGameTile.getTheTileType ();
		if (aMapCell.isTileOnCell ()) {
			tSelectedTileType = aGameTile.getTheTileType ();
			if (aMapCell.canUpgradeTo (tSelectedTileType)) {
				tValidUpgradeType = true;
			}
		} else {
			if (aMapCell.pseudoYellowTile ()) {
				if (tSelectedTileType.isSameType (TileType.GREEN)) {
					tValidUpgradeType = true;							
				}
			} else if (tSelectedTileType.isSameType (TileType.YELLOW)) {
				tValidUpgradeType = true;		
			}
		}
		
		return tValidUpgradeType;	
	}
	
	public void updatePutTileButton () {
		MapCell tMapCell;
		GameTile tTile;
		Tile tNewTile;
		int tTileLayCost;
		TrainCompany tOperatingTrainCompany;
		int tOperatingCompanyTreasury;
		boolean tAnyAllowedRotation;
		
		tOperatingTrainCompany = getOperatingTrainCompany ();
		if (tOperatingTrainCompany != CorporationList.NO_CORPORATION) {
			tOperatingCompanyTreasury = tOperatingTrainCompany.getCash ();
			putTileButton.setEnabled (false);
			tMapCell = map.getSelectedMapCell ();
			tTile = tileSet.getSelectedTile ();
			// If there is a Map Cell Selected we can do further tests
			if (tMapCell != MapCell.NO_MAP_CELL) {
				if (tTile != GameTile.NO_GAME_TILE) {
					tNewTile = tTile.getTile ();
					tAnyAllowedRotation = tMapCell.anyAllowedRotation (tileSet, tNewTile);
					if (tAnyAllowedRotation) {
						if (! tMapCell.privatePreventsTileLay (privateCos, tOperatingTrainCompany)) {
							tTileLayCost = tMapCell.getCostToLayTile ();
							// If there is a Tile Lay Cost, and the Company Treasury has enough cash we can move forward
							if (tTileLayCost <= tOperatingCompanyTreasury) {
								// And there is a Game Tile Selected -- Enable the Put Tile Button 
								if (tTile != GameTile.NO_GAME_TILE) {
									if (validUpgradeType (tMapCell, tTile)) {
										putTileButton.setEnabled (true);
										putTileButton.setToolTipText (NO_TOOL_TIP);
									} else {
										putTileButton.setEnabled (false);
										putTileButton.setToolTipText ("Not a Valid Upgrade choice");
									}
								} else {
									putTileButton.setToolTipText (NO_TILE_SELECTED);
								}
							} else {
								String tNotEnoughCash = String.format (NOT_ENOUGH_CASH, 
										tOperatingTrainCompany.getAbbrev (), 
										Bank.formatCash (tTileLayCost), 
										Bank.formatCash (tOperatingCompanyTreasury));
								putTileButton.setToolTipText (tNotEnoughCash);
							}
						} else {
							String tPrivateNotOwned = String.format (PRIVATE_NOT_OWNED, 
									tOperatingTrainCompany.getAbbrev (),
									tMapCell.getBasePrivateAbbrev (privateCos));
							putTileButton.setToolTipText (tPrivateNotOwned);
						}

					} else {
						putTileButton.setEnabled (false);
						putTileButton.setToolTipText ("No Valid Rotation for the selected Upgrade Tile");
					}
				} else {
					putTileButton.setEnabled (false);
					putTileButton.setToolTipText ("No selected Tile Yet");
				}
			} else {
				putTileButton.setToolTipText (NO_SELECTED_MAP_CELL);
			}
		} else {
			putTileButton.setEnabled (false);
			putTileButton.setToolTipText (NO_OPERATING_COMPANY);
		}
	}
	
	public void sendToReportFrame (String aReport) {
		shareCos.sendToReportFrame (aReport);
	}

	public void handleSelectedRouteRC (MapCell aSelectedMapCell, RevenueCenter aSelectedRevenueCenter) {
		RouteSegment tRouteSegment;
		Corporation tCorporation = getOperatingCompany ();
		int tCorpID, tPhase, tTrainIndex;
		RouteAction tRouteAction;
		StartRouteAction tStartRouteAction;
		ActionStates tRoundType;
		String tRoundID;
		RoundManager tRoundManager;
		ActionManager tActionManager;
		Location tStartLocation, tEndLocation;
		
		tRouteSegment = new RouteSegment (aSelectedMapCell);
		tCorpID = tCorporation.getID ();
		tPhase = gameManager.getCurrentPhase ();
		tRoundManager = gameManager.getRoundManager ();
		tActionManager = tRoundManager.getActionManager ();
		tRoundType = tRoundManager.getCurrentRoundType ();
		tRoundID = tRoundManager.getOperatingRoundID ();
		if (routeInformation.getSegmentCount () == 0) {
			if (aSelectedRevenueCenter != RevenueCenter.NO_CENTER) {
				tStartRouteAction = new StartRouteAction (tRoundType, tRoundID, tCorporation);
				tStartLocation = aSelectedRevenueCenter.getLocation ();
				tEndLocation = new Location ();
				tTrainIndex = routeInformation.getTrainIndex ();
				tStartRouteAction.addStartRouteEffect (tCorporation, tTrainIndex, aSelectedMapCell, tStartLocation, tEndLocation);
				tRouteAction = tStartRouteAction;
			} else {
				System.err.println ("Need to Select a Revenue Center to start a new Route");
				tRouteAction = new RouteAction (tRoundType, tRoundID, tCorporation);
			}
		} else {
			tRouteAction = new ExtendRouteAction (tRoundType, tRoundID, tCorporation);
		}
		routeInformation.setStartSegment (tRouteSegment, aSelectedRevenueCenter, tPhase, tCorpID);
		routeInformation.extendRouteInformation (tRouteSegment, tPhase, tCorpID, tRouteAction);
		
		tActionManager.addAction (tRouteAction);

	}

	public MapCell getMapCellForID (String aMapCellID) {
		MapCell tMapCell;
		
		tMapCell = map.getMapCellForID (aMapCellID);
		
		return tMapCell;
	}

	public Corporation getCorporation(String aCorporationAbbrev) {
		return shareCos.getCorporation (aCorporationAbbrev);
	}
}
