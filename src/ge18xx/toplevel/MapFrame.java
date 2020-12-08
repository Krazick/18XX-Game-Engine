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
import ge18xx.phase.PhaseInfo;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Gauge;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.Track;
import ge18xx.train.RouteInformation;
import ge18xx.train.RouteSegment;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

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
	Container tokenButtons;
	Container tileButtons;
	Container allButtonContainers;
	JScrollPane scrollPane;
	GameManager gameManager;
	RouteInformation routeInformation;
//	private String CANCEL_SELECT_MODE = "Cancel Select";
//	private String CANCEL_TILE_MODE = "Cancel Tile";
	private String CANCEL_TOKEN_MODE = "CancelToken";
	private String CANCEL_MODE_LABEL = "Cancel Mode";
	private String COMPLETE_TILE_LAY = "Complete Tile Lay";
	
	public MapFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getActiveGameName ());

		gameManager = aGameManager;
		JPanel tNorthComponents = new JPanel ();
		JLabel tLabelTokenMode = new JLabel ("Token Mode");
		JLabel tLabelTileMode = new JLabel ("Tile Mode");
		
		allButtonContainers = Box.createVerticalBox ();
		tokenButtons = Box.createHorizontalBox ();
		tileButtons = Box.createHorizontalBox ();
		allButtonContainers.add (tokenButtons);
		allButtonContainers.add (Box.createVerticalStrut (10));
		allButtonContainers.add (tileButtons);
		
		map = new HexMap (this);
        scrollPane = new JScrollPane ();
		scrollPane.setViewportView (map);
		add (scrollPane, BorderLayout.CENTER);

		JSlider hexScaleSlider = new JSlider (JSlider.HORIZONTAL, 4, 16, 8);
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
		putTokenButton.setActionCommand ("PutToken");
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
		putTileButton.setActionCommand ("PutTile");
		putTileButton.setEnabled (false);
		putTileButton.setToolTipText (NOT_PLACE_TILE_MODE);
		tileButtons.add (putTileButton);
		tileButtons.add (Box.createHorizontalStrut (10));
		
		pickupTileButton = new JButton ("Pickup");
		pickupTileButton.addActionListener (this);
		pickupTileButton.setActionCommand ("PickupTile");
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
		selectRouteButton.setActionCommand ("SelectRoute");
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
	
	public void toggleSelectRouteMode () {
		setSelectRouteMode (! selectRouteMode);
		if (selectRouteMode) {
			System.out.print ("*** Entered ");
		} else {
			System.out.print ("=== Exited ");
		}
		selectRouteButton.setEnabled (selectRouteMode);
		map.clearAllSelected ();
		System.out.println ("Select Route Mode. Revenue Center " + map.getSelectRevenueCenter() + " Track " + map.getSelectTrackSegment());
	}
	
	public void actionPerformed (ActionEvent e) {
		String tTheAction = e.getActionCommand ();
		Corporation tCorporation;
		MapCell tMapCell;
		Tile tTile;
		int tRevenueCenterIndex;
		int tCorporationID;
		
		if (COMPLETE_TILE_LAY.equals (tTheAction)) {
			togglePlaceTileMode ();
		} else if ("SelectRoute".equals (tTheAction)) {
			toggleSelectRouteMode ();
		} else if (CANCEL_TOKEN_MODE.equals (tTheAction)) {
			togglePlaceTokenMode ();
		} else if ("PickupTile".equals (tTheAction)) {
			pickupTile ();
		} else if ("PutTile".equals (tTheAction)) {
			putTileDownOnMap ();
		} else if ("PutToken".equals (tTheAction)) {
			tCorporation = getOperatingCompany ();
			if (tCorporation != null) {
				setCompanyAbbrev (tCorporation.getAbbrev ());
				tMapCell = map.getSelectedMapCell ();
				putTokenDown (tCorporation);
				tTile = tMapCell.getTile ();
				if (tCorporation != null) {
					tCorporationID = tCorporation.getID ();
					tRevenueCenterIndex = tTile.getStationIndex (tCorporationID);
					tCorporation.tokenWasPlaced (tMapCell, tTile, tRevenueCenterIndex);
				}				
			}
			togglePlaceTokenMode ();
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
	
	public void putTokenDown (Corporation aCorporation) {
		City tSelectedCity;
		MapCell tSelectedMapCell;
		RevenueCenter tSelectedRevenueCenter;
		boolean tCanPlaceToken;
		
		tSelectedMapCell = map.getSelectedMapCell ();
		if (tSelectedMapCell != null) {
			tSelectedRevenueCenter = tSelectedMapCell.getSelectedRevenueCenter ();
			if (tSelectedRevenueCenter != null) {
				if (tSelectedRevenueCenter.canPlaceStation ()) {
					tSelectedCity = (City) tSelectedRevenueCenter;
					if (aCorporation != null) {
						tCanPlaceToken = canPlaceToken (aCorporation, tSelectedCity);
						if (tCanPlaceToken) {
							putMapTokenDown (aCorporation, tSelectedCity, tSelectedMapCell);
						}
					} else {
						System.err.println ("No Operating Company Found ");
					}
				} else {
					System.err.println ("Cannot Place Station on this Revenue Center");
				}
			} else {
				System.err.println ("No Revenue Center Selected from Map Cell");
			}
		} else {
			System.err.println ("No Map Cell Selected from Frame");
		}		
	}

	public void putMapTokenDown (Corporation aCorporation, City aCity, MapCell aMapCell) {
		Tile tTile;
		MapToken tMapToken;
		boolean tTokenPlaced;
		Corporation tBaseCorporation;
		
		tBaseCorporation = (Corporation) aCity.getTokenCorporation ();
		tMapToken = aCorporation.getMapToken ();
		if (tMapToken == null) {
			System.err.println ("Company has no tokens to place");
		} else {
			tTokenPlaced = aCity.setStation (tMapToken);
			if (tTokenPlaced) {
				putTokenButton.setEnabled (false);
				putTokenButton.setToolTipText (TOKEN_ALREADY_PLACED);
				// If we have placed the Token and these was a Base Corporation Tile, clear out any other Bases for this Corporation from this Tile
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
		if (! aMode) {
			exitTileButton.setToolTipText (NOT_PLACE_TILE_MODE);
		} else {
			exitTileButton.setToolTipText (NO_TOOL_TIP);
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
		if (!aMode) {
			exitTokenButton.setToolTipText (NOT_PLACE_TOKEN_MODE);
			putTokenButton.setToolTipText (NOT_PLACE_TOKEN_MODE);
		} else {
			exitTokenButton.setToolTipText (NO_TOOL_TIP);
			putTokenButton.setToolTipText (NO_SELECTED_RC);
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
		}
		System.out.println ("Ready to set Track and Revenue Modes to " + aMode);
		
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
	
	public void updatePutTileButton () {
		MapCell tMapCell;
		GameTile tTile;
		int tTileLayCost;
		TrainCompany tOperatingTrainCompany;
		int tOperatingCompanyTreasury;
		
		tOperatingTrainCompany = getOperatingTrainCompany ();
		if (tOperatingTrainCompany != CorporationList.NO_CORPORATION) {
			tOperatingCompanyTreasury = tOperatingTrainCompany.getCash ();
			putTileButton.setEnabled (false);
			tMapCell = map.getSelectedMapCell ();
			tTile = tileSet.getSelectedTile ();
			// If there is a Map Cell Selected we can do further tests
			if (tMapCell != HexMap.NO_MAP_CELL) {
				if (! tMapCell.privatePreventsTileLay (privateCos, tOperatingTrainCompany)) {
					tTileLayCost = tMapCell.getCostToLayTile ();
					// If there is a Tile Lay Cost, and the Company Treasury has enough cash we can move forward
					if (tTileLayCost <= tOperatingCompanyTreasury) {
						// And there is a Game Tile Selected -- Enable the Put Tile Button 
						if (tTile != GameTile.NO_GAME_TILE) {
							putTileButton.setEnabled (true);
							putTileButton.setToolTipText (NO_TOOL_TIP);
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
		
		tRouteSegment = new RouteSegment (aSelectedMapCell);
		setStartSegment (tRouteSegment, aSelectedRevenueCenter);
		extendRouteInformation (tRouteSegment);
		System.out.println ("Route Segment Count " + routeInformation.getSegmentCount () + 
				" Center Count " + routeInformation.getCenterCount());
	}
	
	public void setStartSegment (RouteSegment aRouteSegment, RevenueCenter aSelectedRevenueCenter) {
		boolean tCorpStation, tOpenFlow, tIsCity, tIsDeadEnd, tHasRevenueCenter;
		int tRevenue, tBonus;
		Gauge tGauge;
		Corporation tCorporation = getOperatingCompany ();
		int tCorpID;
		int tPhase;
		Location tLocation;

		tCorpID = tCorporation.getID ();
		tPhase = gameManager.getCurrentPhase ();
		if (aSelectedRevenueCenter == RevenueCenter.NO_CENTER) {
			tCorpStation = false;
			tOpenFlow = true;
			tHasRevenueCenter = false;
			tRevenue = 0;
			tLocation = new Location ();
			tIsCity = false;
		} else {
			tCorpStation = aSelectedRevenueCenter.cityHasStation (tCorpID);
			tIsCity = aSelectedRevenueCenter.isCity ();
			tIsDeadEnd = aSelectedRevenueCenter.isDeadEnd ();
			tHasRevenueCenter = true;
			if (tIsDeadEnd) {			// if a Dead-End City, no Flow beyond this.
				tOpenFlow = false;
			} else if (tIsCity) {	
				if (tCorpStation) {		// If this is a City, and it has the Current Operating Company matches the Token
										// Then can flow beyond
					tOpenFlow = true;
				} else { 				// If this is a City, then if there is an Open Station, Flow can continue
					tOpenFlow = aSelectedRevenueCenter.isOpen ();
				}
			} else {					// If this is not a City, it is a Town, and Flow is allowed further
				tOpenFlow = true;
			}
			tRevenue = aSelectedRevenueCenter.getRevenue (tPhase);
			tLocation = aSelectedRevenueCenter.getLocation ();
		}
		
		tBonus = 0;		// TODO: If Selected City has Cattle, Port, etc that will add a Bonus, put that here
		tGauge = new Gauge (Gauge.NORMAL_GAUGE);	// TODO: For 1853, and others with different Gauges, 
													// find the Selected Gauge from the Tile.
				
		// setStartSegment (Location aStartLocation, boolean aCorpStation, boolean aOpenStation, int aRevenue, 
		//					int aBonus, Gauge aGauge
		
		aRouteSegment.setStartSegment (tLocation, tCorpStation, tOpenFlow, tHasRevenueCenter, tRevenue, tBonus, tGauge);
		System.out.println ("In Select Route Mode, - Add to Route. " + tIsCity + 
				", Corp Station " + tCorpStation + ", Open Flow " + tOpenFlow + ", Revenue " + tRevenue);
	}
	
	public void extendRouteInformation (RouteSegment aRouteSegment) {
		int tSegmentCount = routeInformation.getSegmentCount ();
		RouteSegment tPreviousSegment, tNewPreviousSegment;
		RevenueCenter tPreviousRevenueCenter;
		MapCell tCurrentMapCell, tPreviousMapCell;
		int tPreviousSide, tCurrentSide, tPreviousEnd;
		Track tPreviousTrack, tNewPreviousTrack, tCurrentTrack;
		Tile tPreviousTile;
		int tTrainNumber;
		
		if (tSegmentCount == 0) {
			routeInformation.addRouteSegment (aRouteSegment);			
			routeInformation.printDetail ();
		} else {
			tPreviousSegment = routeInformation.getRouteSegment (tSegmentCount - 1);
			tCurrentMapCell = aRouteSegment.getMapCell ();
			tPreviousMapCell = tPreviousSegment.getMapCell ();
			tTrainNumber = routeInformation.getTrainIndex () + 1;
			if (tCurrentMapCell.isNeighbor (tPreviousMapCell)) {
				if (tCurrentMapCell.hasConnectingTrackTo (tPreviousMapCell)) {
					tPreviousSide = tPreviousMapCell.getSideToNeighbor (tCurrentMapCell);
					tPreviousEnd = tPreviousSegment.getEndLocation ();
					if ((tPreviousEnd == Location.NO_LOCATION) ||
						(tPreviousMapCell.hasConnectingTrackBetween (tPreviousSide, tPreviousEnd))) {
						System.out.println ("MapCell " + tPreviousMapCell.getID () + " has Track connecting between " +
									tPreviousSide + " and " + tPreviousEnd);
						tCurrentSide = tCurrentMapCell.getSideToNeighbor (tPreviousMapCell);
						tPreviousTrack = tPreviousMapCell.getTrackFromSide (tPreviousSide);
						
						if (tPreviousSegment.getEndLocation () != Location.NO_LOCATION) {
							tNewPreviousSegment = new RouteSegment (tPreviousMapCell);
							tPreviousTile = tPreviousSegment.getTile ();
							tPreviousRevenueCenter = tPreviousTile.getCenterAtLocation (tPreviousEnd); 
							setStartSegment (tNewPreviousSegment, tPreviousRevenueCenter);
							tNewPreviousSegment.setEndSegment (tPreviousSide);
							
							tNewPreviousTrack = tPreviousMapCell.getTrackFromSide (tPreviousSide);
							tNewPreviousTrack.setTrainNumber (tTrainNumber);
							
							routeInformation.addRouteSegment (tNewPreviousSegment);
							System.out.println ("Added New Previous Segment from " + 
										tNewPreviousSegment.getStartLocation () + " to " + tNewPreviousSegment.getEndLocation ());
							routeInformation.printDetail ();
						} else {
							tPreviousSegment.setEndSegment (tPreviousSide);
							tPreviousTrack.setTrainNumber (tTrainNumber);
						}
						aRouteSegment.setEndSegment (tCurrentSide);
						tCurrentTrack = tCurrentMapCell.getTrackFromSide (tCurrentSide);
						tCurrentTrack.setTrainNumber (tTrainNumber);
	
						aRouteSegment.swapStartEndLocations ();
						routeInformation.addRouteSegment (aRouteSegment);
						System.out.println ("Added New Current Segment from " + 
										aRouteSegment.getStartLocation () + " to " + aRouteSegment.getEndLocation ());
						routeInformation.printDetail ();
					} else {
						System.out.println ("TRACK NOT FOUND between " + tPreviousSide + " and " + tPreviousEnd);
					}
				} else {
					System.out.println ("NO Connecting Track From Current " + tCurrentMapCell.getID() + " to Previous " + tPreviousMapCell.getID());
				}
			} else {
				System.out.println ("The Selected Map Cell is NOT a Neighbor of the Previous Map Cell");
			}
		}
		

	}
}
