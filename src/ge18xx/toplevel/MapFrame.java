package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.Logger;

import swingDelays.KButton;

//
//  MapFrame.java
//  rails_18xx
//
//  Created by Mark Smith on 8/24/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.center.City;
import ge18xx.center.CityInfo;
import ge18xx.center.CityList;
import ge18xx.center.RevenueCenter;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.MapToken;
import ge18xx.company.ShareCompany;
import ge18xx.company.TokenCompany;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.company.TrainCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.map.Terrain;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActionManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.ExtendRouteAction;
import ge18xx.round.action.RouteAction;
import ge18xx.round.action.StartRouteAction;
import ge18xx.round.action.effects.LayBenefitTokenEffect;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.TileType;
import ge18xx.train.RouteInformation;
import ge18xx.train.RouteSegment;

import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLFrame;
import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;
import geUtilities.XMLNodeList;

public class MapFrame extends XMLFrame implements ActionListener {
	private final String NO_TILE_PLACED = "No Tile placed yet";
	private final String NO_TILE_SELECTED = "No Tile selected yet";
	private final String NOT_PLACE_TILE_MODE = "Place Tile Mode not Active";
	private final String NO_SELECTED_MAP_CELL = "No Map Cell selected yet for tile placement";
	private final String TOKEN_ALREADY_PLACED = "Token already placed";
	private final String NOT_PLACE_TOKEN_MODE = "Place Token Mode not Active";
	private final String NOT_BASE_CORPORATION = "City Selected is a Base Corporation that is not the Operating Company";
	private final String NO_SELECTED_RC = "No Revenue Center Selected for Placing a Token";
	private final String NOT_ENOUGH_CASH = "%s does not have enough cash, needs %s has %s";
	private final String PRIVATE_NOT_OWNED = "%s does not own the Private Company %s";
	private final String NO_OPERATING_COMPANY = "There is no Operating Company to place a Tile";
	private final String NON_PLAYABLE_TILE_SELECTED = "The selected Tile is not Playable on the Selected MapCell.";
	private final String RESET_ALL_FLAGS_TIP = "Reset all Map Flags and Selections";
	private final String NO_VALID_ROTATION = "No Valid Rotation for the selected Upgrade Tile";
	public static final String NO_COMPANY = "NO_COMPANY";
	public static final String BASE_TITLE = "Map";
	private static final long serialVersionUID = 1L;

	KButton exitTileButton;
	KButton putTileButton;
	KButton pickupTileButton;
	KButton exitTokenButton;
	KButton putTokenButton;
	KButton buildGraphsButton;
	KButton resetAllFlagsButton;
	JPanel tokenButtonsJPanel;
	JPanel tileButtonsJPanel;
	JPanel allButtonsJPanel;
	JPanel otherButtonsJPanel;
	JSlider hexScaleSlider;
	
	HexMap map;
	TileSet tileSet;
	boolean placeTokenMode;
	boolean selectRouteMode;
	CorporationList privateCos;
	CorporationList minorCos;
	CorporationList shareCos;
	String companyAbbrev;
	RouteInformation routeInformation;
	GameManager gameManager;
	
	private String RESET_ALL_FLAGS = "Reset All Flags";
	private String BUILD_GRAPHS = "Build Graphs";
	private String CANCEL_TOKEN_MODE = "CancelToken";
	private String CANCEL_MODE_LABEL = "Cancel Mode";
	private String COMPLETE_TILE_LAY = "Complete Tile Lay";
	private String PUT_TILE = "PutTile";
	private String PICKUP_TILE = "PickupTile";
	private String PUT_TOKEN = "PutToken";
	Logger logger;

	public MapFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		gameManager = (GameManager) gameEngineManager;

		buildMapScrollPanel ();
		buildNorthPanel ();

		setPlaceTileMode (false);
		setPlaceTokenMode (false);
		setSelectRouteMode (false);
		setDefaultFrameInfo ();
		logger = gameManager.getLogger ();
	}

	public Logger getLogger () {
		return logger;
	}

	/**
	 * Update the Frame, and specifically updateFrameTitle (from super class XMLFrame) with the static BASE_TITLE provided
	 */
	public void updateFrame () {
		updateFrameTitle (BASE_TITLE);
	}
	
	private void buildMapScrollPanel () {
		map = new HexMap (this);
		buildScrollPane (map, BorderLayout.CENTER);
	}

	private void buildNorthPanel () {
		JPanel tNorthPanel;

		tNorthPanel = new JPanel ();
		hexScaleSlider = new JSlider (SwingConstants.HORIZONTAL, 4, 16, 8);
		hexScaleSlider.addChangeListener (map);

		// Turn on labels at major tick marks.
		hexScaleSlider.setMajorTickSpacing (4);
		hexScaleSlider.setMinorTickSpacing (1);
		hexScaleSlider.setPaintTicks (true);
		hexScaleSlider.setPaintLabels (true);
		tNorthPanel.add (hexScaleSlider);

		buildAllButtonsJPanel ();

		tNorthPanel.add (Box.createHorizontalGlue ());
		tNorthPanel.add (allButtonsJPanel);

		buildOtherButtonsJPanel ();
		tNorthPanel.add (Box.createHorizontalGlue ());
		tNorthPanel.add (otherButtonsJPanel);

		add (tNorthPanel, BorderLayout.NORTH);
	}

	private void buildOtherButtonsJPanel () {
		otherButtonsJPanel = new JPanel ();
		otherButtonsJPanel.setLayout (new BoxLayout (otherButtonsJPanel, BoxLayout.Y_AXIS));
				
		resetAllFlagsButton = setupButton (RESET_ALL_FLAGS, RESET_ALL_FLAGS, this, Component.CENTER_ALIGNMENT);
		resetAllFlagsButton.setToolTipText (RESET_ALL_FLAGS_TIP);

		otherButtonsJPanel.add (resetAllFlagsButton);	
		
		buildGraphsButton = setupButton (BUILD_GRAPHS, BUILD_GRAPHS, this, Component.CENTER_ALIGNMENT);
		buildGraphsButton.setToolTipText ("Build Graph of current Hex Map");
		otherButtonsJPanel.add (Box.createVerticalGlue ());
		otherButtonsJPanel.add (buildGraphsButton);
		otherButtonsJPanel.add (Box.createVerticalGlue ());
	}
	
	private void buildAllButtonsJPanel () {
		allButtonsJPanel = new JPanel ();
		allButtonsJPanel.setLayout (new BoxLayout (allButtonsJPanel, BoxLayout.Y_AXIS));

		buildTokenButtonsPanel ();
		allButtonsJPanel.add (tokenButtonsJPanel);
		allButtonsJPanel.add (Box.createVerticalStrut (10));

		buildTileButtonsPanel ();
		allButtonsJPanel.add (tileButtonsJPanel);
	}

	private void buildTileButtonsPanel () {
		JLabel tLabelTileMode;

		tLabelTileMode = new JLabel ("Tile Mode");
		tileButtonsJPanel = new JPanel ();
		tileButtonsJPanel.setLayout (new BoxLayout (tileButtonsJPanel, BoxLayout.X_AXIS));
		tileButtonsJPanel.setOpaque (true);
		tileButtonsJPanel.add (tLabelTileMode);
		tileButtonsJPanel.add (Box.createHorizontalStrut (10));

		putTileButton = setupButton ("Put Down", PUT_TILE, this, Component.CENTER_ALIGNMENT);
		tileButtonsJPanel.add (putTileButton);
		tileButtonsJPanel.add (Box.createHorizontalStrut (10));

		pickupTileButton = setupButton ("Pickup", PICKUP_TILE, this, Component.CENTER_ALIGNMENT);
		tileButtonsJPanel.add (pickupTileButton);
		tileButtonsJPanel.add (Box.createHorizontalStrut (10));

		exitTileButton = setupButton ("Exit Mode", COMPLETE_TILE_LAY, this, Component.CENTER_ALIGNMENT);

		tileButtonsJPanel.add (exitTileButton);
		tileButtonsJPanel.add (Box.createHorizontalStrut (10));
	}

	private void buildTokenButtonsPanel () {
		JLabel tLabelTokenMode;

		tLabelTokenMode = new JLabel ("Token Mode");
		tokenButtonsJPanel = new JPanel ();
		tokenButtonsJPanel.setLayout (new BoxLayout (tokenButtonsJPanel, BoxLayout.X_AXIS));
		tokenButtonsJPanel.setOpaque (true);
		tokenButtonsJPanel.add (tLabelTokenMode);
		tokenButtonsJPanel.add (Box.createHorizontalStrut (10));

		putTokenButton = setupButton ("Put Down", PUT_TOKEN, this, Component.CENTER_ALIGNMENT);
		tokenButtonsJPanel.add (putTokenButton);
		tokenButtonsJPanel.add (Box.createHorizontalStrut (10));

		exitTokenButton = setupButton (CANCEL_MODE_LABEL, CANCEL_TOKEN_MODE, this, Component.CENTER_ALIGNMENT);
		tokenButtonsJPanel.add (exitTokenButton);
		tokenButtonsJPanel.add (Box.createHorizontalStrut (10));
	}

	@Override
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

	/**
	 * Clear the Specified Train from the Map
	 *
	 * @param aTrainNumber The Train Number to clear from the Map
	 */
	public void clearTrainFromMap (int aTrainNumber) {
		map.clearTrain (aTrainNumber);
		repaint ();
	}

	/**
	 * Clear all Trains from the Map
	 *
	 */
	public void clearAllTrainsFromMap () {
		map.clearAllTrains ();
		repaint ();
	}

	/**
	 * Retrieve the current phase from the Game Manager and return it
	 * 
	 * @return The current Phase
	 */
	public int getCurrentPhase () {
		return gameManager.getCurrentPhase ();
	}

	public void togglePlaceTileMode () {
		boolean tIsPlaceTileMode;
		boolean tNewPlaceTileMode;

		tIsPlaceTileMode = map.isPlaceTileMode ();
		tNewPlaceTileMode = !tIsPlaceTileMode;
		setModes (tNewPlaceTileMode, false, false);
		map.setSingleMapCellSelect (tNewPlaceTileMode);
		tileSet.setSingleTileSelect (tNewPlaceTileMode);
		map.clearAllSelected ();
	}

	public void togglePlaceTokenMode () {
		setModes (false, !placeTokenMode, false);
		map.setSingleMapCellSelect (false);
		map.clearAllSelected ();
	}

	public void enterSelectRouteMode (RouteInformation aRouteInformation) {
		toggleSelectRouteMode ();
		routeInformation = aRouteInformation;
	}

	public void exitSelectRouteMode () {
		setSelectRouteMode (false);
		map.clearAllSelected ();
	}

	public void toggleSelectRouteMode () {
		setSelectRouteMode (!selectRouteMode);
		map.clearAllSelected ();
	}

	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction = aActionEvent.getActionCommand ();
		Corporation tCorporation;
		TokenCompany tTokenCompany;
		
		tCorporation = getOperatingCompany ();

		if (COMPLETE_TILE_LAY.equals (tTheAction)) {
			completeTileLay ();
		} else if (CANCEL_TOKEN_MODE.equals (tTheAction)) {
			togglePlaceTokenMode ();
			if (tCorporation.isATokenCompany ()) {
				resetBenefitInUse (tCorporation);
				resetAllModes ();
			}
		} else if (PICKUP_TILE.equals (tTheAction)) {
			pickupTile ();
		} else if (PUT_TILE.equals (tTheAction)) {
			putTileDownOnMap ();
		} else if (PUT_TOKEN.equals (tTheAction)) {
			if (tCorporation.isATokenCompany ()) {
				tTokenCompany = (TokenCompany) tCorporation;
				putATokenDown (tTokenCompany);
			}
		} else if (RESET_ALL_FLAGS.equals (tTheAction)) {
			resetAllModes ();
		} else if (BUILD_GRAPHS.equals (tTheAction)) {
			handleBuildGraphs ();
		}
		if (tCorporation != Corporation.NO_CORPORATION) {
			tCorporation.updateFrameInfo ();
		}
	}

	private void resetBenefitInUse (Corporation aCorporation) {
		Benefit tBenefitInUse;
		
		tBenefitInUse = aCorporation.getBenefitInUse ();
		if (tBenefitInUse.isRealBenefit ()) {
			tBenefitInUse.setUsed (false);
			aCorporation.setBenefitInUse (Benefit.FAKE_BENEFIT);
		}
	}
	
	public void resetAllModes () {
		setModes (false, false, false);

		map.clearAllTrains ();
		map.removeAllSMC ();
		map.setSingleMapCellSelect (true);
		
		tileSet.clearAllSelected ();
		tileSet.clearAllPlayable ();
	}
	
	private void handleBuildGraphs () {
		Corporation tCorporation;
		TokenCompany tTokenCompany;

		tCorporation = getOperatingCompany ();
		if (tCorporation != Corporation.NO_CORPORATION) {
			if (tCorporation.isATokenCompany ()) {
				tTokenCompany = (TokenCompany) tCorporation;
				map.buildMapGraph (tTokenCompany);
			}
		}
	}

	private void completeTileLay () {
		Corporation tOperatingCompany;
		
		if (map.wasTilePlaced ()) {
			tOperatingCompany = gameManager.getOperatingCompany ();
			completeBenefitInUse (tOperatingCompany);
			removeHomeIfChoice ();
		}
		togglePlaceTileMode ();
		map.setTilePlaced (false);
		map.removeAllSMC ();
		if (gameManager.hasDestinations ()) {
			gameManager.checkForDestinationsReached ();
		}
	}

	private void removeHomeIfChoice () {
		MapCell tSelectedMapCell;
		MapCell tHomeMapCell1;
		MapCell tHomeMapCell2;
		Location tHomeLocation1;
		Location tHomeLocation2;
		Corporation tCorporation;
		
		tCorporation = getOperatingCompany ();
		if (tCorporation.isHomeTypeChoice ()) {
			tSelectedMapCell = map.getSelectedMapCell ();
			tHomeMapCell1 = tCorporation.getHomeCity1 ();
			tHomeMapCell2 = tCorporation.getHomeCity2 ();
			tHomeLocation1 = tCorporation.getHomeLocation1 ();
			tHomeLocation2 = tCorporation.getHomeLocation2 ();
			if (tSelectedMapCell == tHomeMapCell1) {
				tHomeMapCell2.removeHome (tCorporation, tHomeLocation2);
				tCorporation.setHome2 (MapCell.NO_MAP_CELL, Location.NO_LOC);
			}
			if (tSelectedMapCell == tHomeMapCell2) {
				tHomeMapCell1.removeHome (tCorporation, tHomeLocation1);
				tCorporation.setHome1 (MapCell.NO_MAP_CELL, Location.NO_LOC);
			}
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
		updateGraphsButton ();
		map.setTilePlaced (false);
	}

	private void putTileDownOnMap () {
		Corporation tCorporation;
		MapCell tMapCell;
		Tile tTile;
		Tile tPreviousTile;
		int tOrientation;
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
		updatePickupTileButton (true, GUI.NO_TOOL_TIP);
		tCorporation = getOperatingCompany ();
		if (tCorporation != Corporation.NO_CORPORATION) {
			tTile = tMapCell.getTile ();
			tOrientation = tMapCell.getTileOrient ();
			tCorporation.placeTileOnMapCell (tMapCell, tTile, tOrientation, tPreviousTile, 
					tPreviousOrientation, tPreviousTokens, tPreviousBases);
		}
		tileSet.clearAllSelected ();
		updatePutTileButton ();
		updateGraphsButton ();
		toTheFront ();
	}

	private void completeBenefitInUse (Corporation aOperatingCompany) {
		aOperatingCompany.completeBenefitInUse (aOperatingCompany);
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
		TrainCompany tTrainCompany = (TrainCompany) Corporation.NO_CORPORATION;
		TrainCompany tCorporation;

		if (minorCos != CorporationList.NO_CORPORATION_LIST) {
			tCorporation = minorCos.getOperatingTrainCompany ();
			if (tCorporation != Corporation.NO_CORPORATION) {
				tTrainCompany = tCorporation;
			}
		}
		if (shareCos != CorporationList.NO_CORPORATION_LIST) {
			tCorporation = shareCos.getOperatingTrainCompany ();
			if (tCorporation != Corporation.NO_CORPORATION) {
				tTrainCompany = tCorporation;
			}
		}

		return tTrainCompany;
	}

	public TokenCompany getTokenCompany (String aAbbrev) {
		TokenCompany tTokenCompany;
		Corporation tCorporation;

		tTokenCompany = TokenCompany.NO_TOKEN_COMPANY;
		if (minorCos != CorporationList.NO_CORPORATION_LIST) {
			tCorporation = minorCos.getCorporation (aAbbrev);
			if (tCorporation != Corporation.NO_CORPORATION) {
				tTokenCompany = (TokenCompany) tCorporation;
			}
		}
		if (tTokenCompany == TokenCompany.NO_TOKEN_COMPANY) {
			if (shareCos != CorporationList.NO_CORPORATION_LIST) {
				tCorporation = shareCos.getCorporation (aAbbrev);
				if (tCorporation != Corporation.NO_CORPORATION) {
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
		return map.isPlaceTileMode ();
	}

	public boolean isSelectRouteMode () {
		return selectRouteMode;
	}

	public void loadMapStates (XMLNode aMapNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (mapStateParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aMapNode, MapCell.EN_MAP_CELL);
	}

	ParsingRoutineI mapStateParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aMapCellNode) {
			if (map != HexMap.NO_HEX_MAP) {
				map.loadMapCellState (aMapCellNode);
			}
		}
	};
	
	@Override
	public boolean loadXML (XMLDocument aXMLDocument, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;

		tXMLFileWasLoaded = super.loadXML (aXMLDocument, aLoadableObject);
		if (tXMLFileWasLoaded) {
			setFixedMapTiles ();
		}
		setScrollPanePreferredSize ();

		return tXMLFileWasLoaded;
	}

	@Override
	public boolean loadXML (String aXMLFileName, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;

		tXMLFileWasLoaded = super.loadXML (aXMLFileName, aLoadableObject);
		if (tXMLFileWasLoaded) {
			setFixedMapTiles ();
		}
		setScrollPanePreferredSize ();

		return tXMLFileWasLoaded;
	}

	public void setScrollPanePreferredSize () {
		int tMaxWidth;
		int tMaxHeight;
		
		tMaxWidth = map.getMaxWidth ();
		tMaxHeight = map.getMaxHeight ();

		scrollPane.setPreferredSize (new Dimension (tMaxWidth, tMaxHeight));
	}

	public boolean loadXMLColorScheme (XMLDocument aXMLDocument, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;

		tXMLFileWasLoaded = super.loadXML (aXMLDocument, aLoadableObject);

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
		if (tSelectedMapCell != MapCell.NO_MAP_CELL) {
			tSelectedRevenueCenter = tSelectedMapCell.getSelectedRevenueCenter ();
			if (tSelectedRevenueCenter != RevenueCenter.NO_CENTER) {
				tHasCityBeenSelected = true;
			}
		}

		return tHasCityBeenSelected;
	}

	public void putATokenDown (TokenCompany aTokenCompany) {
		putTokenDown (aTokenCompany);
		togglePlaceTokenMode ();
		map.removeAllSMC ();
	}
	
	public void putTokenDown (TokenCompany aTokenCompany) {
		MapCell tSelectedMapCell;
		RevenueCenter tSelectedRevenueCenter;
		TokenType tTokenType;
		String tHomeAbbrev;
		String tTokenAbbrev;

		tSelectedMapCell = map.getSelectedMapCell ();
		if (tSelectedMapCell != MapCell.NO_MAP_CELL) {
			tSelectedRevenueCenter = tSelectedMapCell.getSelectedRevenueCenter ();
			if (tSelectedRevenueCenter != RevenueCenter.NO_CENTER) {
				tHomeAbbrev = tSelectedRevenueCenter.getHomeCompanyAbbrev ();
				tTokenAbbrev = aTokenCompany.getAbbrev ();
				if (tHomeAbbrev == Corporation.NO_ABBREV) {
					tTokenType = TokenType.MAP;
				} else if (tHomeAbbrev.equals (tTokenAbbrev)) {
					tTokenType = TokenType.HOME1;
				} else {
					tTokenType = TokenType.MAP;
				}
				putTokenDownHere (aTokenCompany, tSelectedMapCell, tSelectedRevenueCenter, tTokenType);
			} else {
				System.err.println ("No Revenue Center Selected from Map Cell");
			}
		} else {
			System.err.println ("No Map Cell Selected from Frame");
		}
	}

	public void putTokenDownHere (TokenCompany aTokenCompany, MapToken aMapToken, TokenType aTokenType,
									MapCell aMapCell, RevenueCenter aRevenueCenter) {
		City tSelectedCity;
		boolean tCanPlaceToken;

		if (aTokenCompany != Corporation.NO_CORPORATION) {
			setCompanyAbbrev (aTokenCompany.getAbbrev ());
			if (aRevenueCenter != RevenueCenter.NO_CENTER) {
				tSelectedCity = (City) aRevenueCenter;
				if (tSelectedCity.canPlaceStation ()) {
					tCanPlaceToken = canPlaceToken (aTokenCompany, tSelectedCity, aMapCell);
					if (tCanPlaceToken) {
						putMapTokenDown (aTokenCompany, aMapToken, aTokenType, tSelectedCity, aMapCell, true);
					}
				} else {
					System.err.println ("***Cannot Place Station on this Revenue Center");
				}
			}
		} else {
			System.err.println ("No Operating Company Found ");
		}
	}

	public void putTokenDownHere (TokenCompany aTokenCompany, MapCell aMapCell, RevenueCenter aRevenueCenter, 
									TokenType aTokenType) {
		City tSelectedCity;
		boolean tCanPlaceToken;
		MapToken tMapToken;

		if (aTokenCompany != Corporation.NO_CORPORATION) {
			setCompanyAbbrev (aTokenCompany.getAbbrev ());
			if (aRevenueCenter != RevenueCenter.NO_CENTER) {
				if (aRevenueCenter.canPlaceStation ()) {
					tSelectedCity = (City) aRevenueCenter;
					tCanPlaceToken = canPlaceToken (aTokenCompany, tSelectedCity, aMapCell);
					if (tCanPlaceToken) {
						tMapToken = (MapToken) aTokenCompany.getToken (aTokenType);
						putMapTokenDown (aTokenCompany, tMapToken, aTokenType, tSelectedCity, aMapCell, true);
					}
				} else {
					System.err.println ("---Cannot Place Station on this Revenue Center");
				}
			}
		} else {
			System.err.println ("No Operating Company Found ");
		}
	}

	public void placeBenefitToken (MapCell aMapCell, String aTokenType, 
							Benefit aBenefitInUse, int aBenefitValue) {
		LayBenefitTokenEffect tLayBenefitTokenEffect;
		Corporation tOperatingCompany;
		
		aMapCell.layBenefitToken (aTokenType, aBenefitValue);
		
		tOperatingCompany = gameManager.getOperatingCompany ();
		tLayBenefitTokenEffect = new LayBenefitTokenEffect (tOperatingCompany, aMapCell, aTokenType, aBenefitValue);
		aBenefitInUse.addAdditionalEffect (tLayBenefitTokenEffect);
		completeBenefitInUse (tOperatingCompany);
	}
	
	public boolean putMapTokenDown (TokenCompany aTokenCompany, MapToken aMapToken, TokenType aTokenType, City aCity, 
								MapCell aMapCell, boolean aAddLayTokenAction) {
		Tile tTile;
		boolean tTokenPlaced;
		TokenCompany tBaseCompany;
		int tRevenueCenterIndex;
		int tCorporationID;
		int tTokenIndex;

		tBaseCompany = aCity.getBaseCorporation ();
		tTokenPlaced = false;
		if (aMapToken == MapToken.NO_MAP_TOKEN) {
			System.err.println ("Company has no tokens to place");
		} else {
			tTokenPlaced = aCity.setStation (aMapToken);
			if (tTokenPlaced) {
				tCorporationID = aTokenCompany.getID ();
				tTile = aMapCell.getTile ();
				tRevenueCenterIndex = tTile.getStationIndex (tCorporationID);
				tTokenIndex = aTokenCompany.getTokenIndex (aMapToken);
				aTokenCompany.tokenWasPlaced (aMapCell, tTile, tRevenueCenterIndex, aMapToken, tTokenIndex,
											aAddLayTokenAction);
				completeBenefitInUse (aTokenCompany);
				putTokenButton.setEnabled (false);
				putTokenButton.setToolTipText (TOKEN_ALREADY_PLACED);
				clearSecondaryBases (aTokenCompany, aMapCell, tBaseCompany);
			} else {
				System.err.println ("Token Placement Failed.***");
			}
			map.clearAllSelected ();
		}
		
		return tTokenPlaced;
	}

	// TODO: The fact that this is applying another Map Effect (to clear Base Companies), 
	// it should also add an additional Map Effect to the Action so that it can be undone
	// This may also need to be called when a choice of two bases, on different tiles that needed to be cleared
	public void clearSecondaryBases (TokenCompany aTokenCompany, MapCell aMapCell, TokenCompany aBaseCompany) {
		Tile tTile;
		int tCityCount;
		int tCityIndex;
		City tCity;
		int tBaseCount;
		
		// If we have placed the Token and there was a Base Corporation Tile, clear out
		// any other Bases for this Corporation from this Tile
		// Primarily for Companies (like EIRE, THB) that starts with a choice of two spots in the Tile.
		if (aBaseCompany == aTokenCompany) {
			tTile = aMapCell.getTile ();
			if (tTile != Tile.NO_TILE) {
				tCityCount = tTile.getCenterCount ();
				tBaseCount = 0;
				if (tCityCount > 1) {
					for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
						tCity = tTile.getCityAt (tCityIndex);
						if (tCity.withBaseForCorp (aBaseCompany)) {
							tBaseCount++;
						}
					}
				}
				// Simply having more than one City with bases, need to clear all bases.
				if (tBaseCount > 1) {
					tTile.clearCorporation (aTokenCompany);
				}
			}
		}
	}

	public boolean hasStation (int aCorpID) {
		return map.hasStation (aCorpID);
	}

	public String canPlaceTokenToolTip (Corporation aCorporation, City aSelectedCity, MapCell aMapCell) {
		String tCanPlaceTokenToolTip;
		Corporation tBaseCorporation;
		String tBaseAbbrev;
		String tCorporationAbbrev;
		int tCorporationID;

		tCanPlaceTokenToolTip = GUI.EMPTY_STRING;
		tCorporationID = aCorporation.getID ();
		if (aMapCell != MapCell.NO_MAP_CELL) {
			if (aMapCell.hasStation (tCorporationID)) {
				tCanPlaceTokenToolTip = "Map Cell already has this Company's Token";
			} else if (aSelectedCity != City.NO_CITY) {
				if (aSelectedCity.isDestination ()) {
					tCanPlaceTokenToolTip = "Selected City is a Destination, cannot Place Token Here";
				} else {
					tBaseCorporation = aSelectedCity.getBaseCorporation ();
					if (tBaseCorporation == Corporation.NO_CORPORATION) {
						if (!hasFreeStation (aSelectedCity)) {
							tCanPlaceTokenToolTip = "No Free Station on City";
						}
					} else {
						tBaseAbbrev = tBaseCorporation.getAbbrev ();
						tCorporationAbbrev = aCorporation.getAbbrev ();
						if (aSelectedCity.cityHasStation (tCorporationID)) {
							tCanPlaceTokenToolTip = "City already has this Company's Token";
						} else if (tBaseAbbrev.equals (tCorporationAbbrev)) {
							if (!hasFreeStation (aSelectedCity)) {
								tCanPlaceTokenToolTip = NOT_BASE_CORPORATION;
							}
						} else if (!baseHasFreeStation (aSelectedCity)) {
							tCanPlaceTokenToolTip = "No Free Station on City";
						}
					}
				}
			}
		}

		return tCanPlaceTokenToolTip;
	}

	public boolean canPlaceToken (Corporation aCorporation, City aSelectedCity, MapCell aMapCell) {
		boolean tCanPlaceToken;
		Corporation tBaseCorporation;
		int tCorporationID;

		tCanPlaceToken = false;
		tCorporationID = aCorporation.getID ();
		if (aMapCell != MapCell.NO_MAP_CELL) {
			if (aMapCell.hasStation (tCorporationID)) {
				tCanPlaceToken = false;
			} else if (aSelectedCity != City.NO_CITY) {
				if (aSelectedCity.isDestination ()) {
					tCanPlaceToken = false;
				} else {
					tBaseCorporation = aSelectedCity.getBaseCorporation ();
					if (tBaseCorporation == Corporation.NO_CORPORATION) {
						if (hasFreeStation (aSelectedCity)) {
							tCanPlaceToken = true;
						}
					} else {
						if (aSelectedCity.cityHasStation (tCorporationID)) {
							tCanPlaceToken = false;
						} else if (isHomeMapCell (aCorporation, aSelectedCity)) {
							if (hasFreeStation (aSelectedCity)) {
								tCanPlaceToken = true;
							}
						} else if (baseHasFreeStation (aSelectedCity)) {
							tCanPlaceToken = true;
						}
					}
				}
			}
		}

		return tCanPlaceToken;
	}

	private boolean isHomeMapCell (Corporation aCorporation, City aSelectedCity) {
		boolean tIsHomeMapCell;
		Corporation tBaseCorporation;
		String tBaseAbbrev;
		String tCorporationAbbrev;
		
		tBaseCorporation = aSelectedCity.getBaseCorporation ();
		tBaseAbbrev = tBaseCorporation.getAbbrev ();
		tCorporationAbbrev = aCorporation.getAbbrev ();
		if (tBaseAbbrev.equals (tCorporationAbbrev)) {
			tIsHomeMapCell = true;
		} else {
			tIsHomeMapCell = false;
		}

		return tIsHomeMapCell;
	}
	
	private boolean hasFreeStation (City aCity) {
		return aCity.getFreeStationCount () > 0;
	}

	private boolean baseHasFreeStation (City aCity) {
		return aCity.getFreeStationCount () > 1;
	}

	public void setCityInfo (CityList aCityList) {
		int rowIndex;
		int colIndex;
		int maxRow;
		int maxCol;
		int tRevenueCenterID;
		CityInfo tCityInfo;
		MapCell tMapCell;

		if (aCityList != CityList.NO_CITY_LIST) {
			maxRow = map.getRowCount ();
			for (rowIndex = 0; rowIndex < maxRow; rowIndex++) {
				maxCol = map.getColCount (rowIndex);
				for (colIndex = 0; colIndex < maxCol; colIndex++) {
					tRevenueCenterID = map.getRevenueCenterID (rowIndex, colIndex);
					if (tRevenueCenterID != RevenueCenter.NO_ID) {
						tCityInfo = aCityList.getCityInfo (tRevenueCenterID);
						tMapCell = map.getMapCell (rowIndex, colIndex);
						if (tCityInfo != CityInfo.NO_CITY_INFO) {
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

	public void addCorporationList (CorporationTableFrame aCorporationFrame, ElementName aCompanyType) {
		CorporationList tCorporationList;

		tCorporationList = aCorporationFrame.getCompanies ();
		setCorporationList (tCorporationList, aCompanyType);
		setHomeCities (tCorporationList);
	}
	
	public void setCorporationList (CorporationList aCorporationList, ElementName aType) {
		if (aType.equals (CorporationList.TYPE_NAMES [0])) {
			privateCos = aCorporationList;
		} else if (aType.equals (CorporationList.TYPE_NAMES [1])) {
			minorCos = aCorporationList;
		} else if (aType.equals (CorporationList.TYPE_NAMES [2])) {
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
					if (tTile != Tile.NO_TILE) {
						map.putStartingTile (rowIndex, colIndex, tTile);
					}
				}
			}
		}
	}

	public void setDestinationCorpIDs (CorporationTableFrame aCorporationFrame) {
		CorporationList tCorporationList;
		ShareCompany tShareCompany;
		int tCorporationIndex;
		int tMaxCorporations;
		
		tCorporationList = aCorporationFrame.getCompanies ();
		if (tCorporationList != CorporationList.NO_CORPORATION_LIST) {
			tMaxCorporations = tCorporationList.getRowCount ();
			for (tCorporationIndex = 0; tCorporationIndex < tMaxCorporations; tCorporationIndex++) {
				tShareCompany = (ShareCompany) tCorporationList.getCorporation (tCorporationIndex);
				setDestinationCorpID (tShareCompany);
			}
		}
	}

	public void setDestinationCorpID (ShareCompany aShareCompany) {
		MapCell tMapCell;
		String tDestinationMapCellID;
		
		tDestinationMapCellID = aShareCompany.getDestinationLabel ();
		if (tDestinationMapCellID != GUI.NULL_STRING) {
			tMapCell = map.getMapCellForID (tDestinationMapCellID);
			aShareCompany.setDestinationMapCell (tMapCell);
			setDestinationCorpID (aShareCompany, tMapCell);
		}
	}

	public void setDestinationCorpID (ShareCompany aShareCompany, MapCell aMapCell) {
		int tShareCompanyID;
		
		tShareCompanyID = aShareCompany.getID ();
		if (aMapCell != MapCell.NO_MAP_CELL) {
			aMapCell.setDestinationCorpID (tShareCompanyID);
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

		tMapCell = MapCell.NO_MAP_CELL;
		if (aCorporationList != CorporationList.NO_CORPORATION_LIST) {
			tMaxCorporations = aCorporationList.getRowCount ();
			if (tMaxCorporations > 0) {
				for (tCorporationIndex = 0; tCorporationIndex < tMaxCorporations; tCorporationIndex++) {
					tCorporation = aCorporationList.getCorporation (tCorporationIndex);
					tCellID = tCorporation.getHomeCityGrid1 ();
					tLocation = tCorporation.getHomeLocation1 ();
					tMapCell = map.getMapCellForID (tCellID);
					if (tMapCell != MapCell.NO_MAP_CELL) {
						tCorporation.setHome1 (tMapCell, tLocation);
						tMapCell.setCorporationHome (tCorporation, tLocation);
					}
					tCellID = tCorporation.getHomeCityGrid2 ();
					tLocation = tCorporation.getHomeLocation2 ();
					if (!tLocation.isNoLocation ()) {
						if (tCellID != Corporation.NO_NAME_STRING) {
							tMapCell = map.getMapCellForID (tCellID);
						}
						if (tMapCell != MapCell.NO_MAP_CELL) {
							tCorporation.setHome2 (tMapCell, tLocation);
							tMapCell.setCorporationHome (tCorporation, tLocation);
						}
					}
					if (tCorporation.isAShareCompany ()) {
						tShareCompany = (ShareCompany) tCorporation;
						tLocation = tShareCompany.getDestinationLocation ();
						if (tLocation != Location.NO_DESTINATION_LOCATION) {
							if (!tLocation.isNoLocation ()) {
								tCellID = tShareCompany.getDestinationLabel ();
								if (tCellID != Corporation.NO_NAME_STRING) {
									tMapCell = map.getMapCellForID (tCellID);
									if (tMapCell != MapCell.NO_MAP_CELL) {
										tShareCompany.setDestination (tMapCell, tLocation);
										tMapCell.setCorporationHome (tShareCompany, tLocation);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void setPlaceTileMode (boolean aPlaceTileMode) {
		MapCell tSelectedMapCell;

		map.setPlaceTileMode (aPlaceTileMode);
		exitTileButton.setEnabled (aPlaceTileMode);
		if (aPlaceTileMode) {
			exitTileButton.setToolTipText (GUI.NO_TOOL_TIP);
			tileButtonsJPanel.setBackground (Color.ORANGE);
		} else {
			exitTileButton.setToolTipText (NOT_PLACE_TILE_MODE);
			tileButtonsJPanel.setBackground (getBackground ());
		}
		putTileButton.setEnabled (false);
		if (map.isPlaceTileMode ()) {
			updatePickupTileButton (false, NO_TILE_PLACED);
			putTileButton.setToolTipText (NO_SELECTED_MAP_CELL);
		} else {
			updatePickupTileButton (false, NOT_PLACE_TILE_MODE);
			putTileButton.setToolTipText (NOT_PLACE_TILE_MODE);

			tSelectedMapCell = map.getSelectedMapCell ();
			if (tSelectedMapCell != MapCell.NO_MAP_CELL) {
				tSelectedMapCell.lockTileOrientation ();
			}
		}
	}

	public void setPlaceTokenMode (boolean aMode) {
		placeTokenMode = aMode;
		exitTokenButton.setEnabled (aMode);
		putTokenButton.setEnabled (false);
		if (aMode) {
			exitTokenButton.setToolTipText (GUI.NO_TOOL_TIP);
			putTokenButton.setToolTipText (NO_SELECTED_RC);
			tokenButtonsJPanel.setBackground (Color.ORANGE);
		} else {
			exitTokenButton.setToolTipText (NOT_PLACE_TOKEN_MODE);
			putTokenButton.setToolTipText (NOT_PLACE_TOKEN_MODE);
			tokenButtonsJPanel.setBackground (getBackground ());
		}
		map.setSelectTrackSegment (aMode);
		map.setSelectRevenueCenter (aMode);
	}

	public void setSelectRouteMode (boolean aMode) {
		selectRouteMode = aMode;
		map.setSelectTrackSegment (aMode);
		map.setSelectRevenueCenter (aMode);
		map.setSingleMapCellSelect (!aMode);
	}

	public void setTileSet (TileSet aTileSet) {
		tileSet = aTileSet;
		map.setTileSet (aTileSet);
	}

	public void updatePutTokenButton (City aSelectedCity, MapCell aMapCell) {
		boolean tCitySelected;
		boolean tCanPlaceToken;
		Corporation tCorporation;
		String tToolTip;

		tCitySelected = hasCityBeenSelected ();
		tCorporation = getOperatingTrainCompany ();
		tCanPlaceToken = canPlaceToken (tCorporation, aSelectedCity, aMapCell);
		if (tCanPlaceToken) {
			putTokenButton.setEnabled (tCitySelected);
			if (tCitySelected) {
				putTokenButton.setToolTipText (GUI.NO_TOOL_TIP);
			} else {
				putTokenButton.setToolTipText (NO_SELECTED_RC);
			}
		} else {
			putTokenButton.setEnabled (false);
			tToolTip = canPlaceTokenToolTip (tCorporation, aSelectedCity, aMapCell);
			putTokenButton.setToolTipText (tToolTip);
		}
	}

	public boolean validUpgradeType (MapCell aMapCell, GameTile aGameTile) {
		boolean tValidUpgradeType = false;
		TileType tSelectedTileType;

		tSelectedTileType = aGameTile.getTheTileType ();
		if (aMapCell.isTileOnCell ()) {
			if (aGameTile.canOverride ()) {
				tValidUpgradeType = true;
			} else {
				tSelectedTileType = aGameTile.getTheTileType ();
				if (aMapCell.canUpgradeTo (tSelectedTileType)) {
					tValidUpgradeType = true;
				}
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

	/**
	 * Determine if the specified GameTile is currently allowed to be placed on the Map, based upon the
	 * current Phase of the Game and the Tile Color
	 *
	 * @param aGameTile The Tile to test if allowed to be placed
	 * @param aCurrentGameTile the Tile currently on the MapCell that is proposed for Upgrading from
	 *
	 * @return TRUE if the current Game Phase allows this tile Type Color can be placed.
	 *
	 */
	public boolean isUpgradeAllowed (GameTile aGameTile, GameTile aCurrentGameTile) {
		boolean tUpgradeAllowed;
		String tTileColor;
		int tPhase;
		int tToTileNumber;
		
		tUpgradeAllowed = true;
		tTileColor = aGameTile.getTileColor ();
		tUpgradeAllowed = gameManager.isUpgradeAllowed (tTileColor);
		if (aCurrentGameTile != GameTile.NO_GAME_TILE) {
			if (tUpgradeAllowed) {
				tPhase = getCurrentPhase ();
				tToTileNumber = aGameTile.getTileNumber ();
				tUpgradeAllowed = aCurrentGameTile.isUpgradeAllowedInPhase (tToTileNumber, tPhase);
			}
		}
		
		return tUpgradeAllowed;
	}

	public void updateGraphsButton () {
		// TODO -- Find if the Operating Company has any Tokens placed on Map. If so, enable the testGraphsButton
//		testGraphsButton
	}

	public void setEnabledBuildGraphsButton (boolean aEnable, String aDisableToolTip) {
		buildGraphsButton.setEnabled (aEnable);
		if (aEnable) {
			buildGraphsButton.setToolTipText ("");
		} else {
			buildGraphsButton.setToolTipText (aDisableToolTip);
		}
	}
	
	public void updatePutTileButton () {
		MapCell tMapCell;
		GameTile tSelectedGameTile;
		GameTile tCurrentGameTile;
		Tile tNewTile;
		Tile tCurrentTile;
		int tTileLayCost;
		TrainCompany tOperatingTrainCompany;
		int tOperatingCompanyTreasury;
		boolean tAnyAllowedRotation;
		String tNotEnoughCash;
		String tPrivateNotOwned;
		
		tOperatingTrainCompany = getOperatingTrainCompany ();
		if (tOperatingTrainCompany != Corporation.NO_CORPORATION) {
			tOperatingCompanyTreasury = tOperatingTrainCompany.getCash ();
			putTileButton.setEnabled (false);
			tMapCell = map.getSelectedMapCell ();

			// If there is a Map Cell Selected we can do further tests
			if (tMapCell != MapCell.NO_MAP_CELL) {
				tCurrentTile = tMapCell.getTile ();
				if (tCurrentTile == Tile.NO_TILE) {
					tCurrentGameTile = GameTile.NO_GAME_TILE;
				} else {
					tCurrentGameTile = tileSet.getGameTile (tCurrentTile.getNumber ());
				}
				tSelectedGameTile = tileSet.getSelectedTile ();

				if (tSelectedGameTile != GameTile.NO_GAME_TILE) {
					if (tSelectedGameTile.isPlayable ()) {
						if (isUpgradeAllowed (tSelectedGameTile, tCurrentGameTile)) {
							tNewTile = tSelectedGameTile.getTile ();
							tAnyAllowedRotation = tMapCell.anyAllowedRotation (tileSet, tNewTile);
							if (tAnyAllowedRotation) {
								if (!tMapCell.privatePreventsTileLay (privateCos, tOperatingTrainCompany)) {
									tTileLayCost = tMapCell.getCostToLayTile (tNewTile);
									// If there is a Tile Lay Cost, and the Company Treasury has enough cash we can
									// move forward
									if (tTileLayCost <= tOperatingCompanyTreasury) {
										if (validUpgradeType (tMapCell, tSelectedGameTile)) {
											putTileButton.setEnabled (true);
											putTileButton.setToolTipText (GUI.NO_TOOL_TIP);
										} else {
											putTileButton.setEnabled (false);
											putTileButton.setToolTipText ("Not a Valid Upgrade choice");
										}
									} else {
										tNotEnoughCash = String.format (NOT_ENOUGH_CASH,
												tOperatingTrainCompany.getAbbrev (), Bank.formatCash (tTileLayCost),
												Bank.formatCash (tOperatingCompanyTreasury));
										putTileButton.setToolTipText (tNotEnoughCash);
									}
								} else {
									tPrivateNotOwned = String.format (PRIVATE_NOT_OWNED,
											tOperatingTrainCompany.getAbbrev (), tMapCell.getBasePrivateAbbrev (privateCos));
									putTileButton.setToolTipText (tPrivateNotOwned);
								}
							} else {
								putTileButton.setEnabled (false);
								putTileButton.setToolTipText (NO_VALID_ROTATION);
							}
						} else {
							putTileButton.setEnabled (false);
							putTileButton.setToolTipText ("The current phase does not allow the Selected Tile to be placed yet.");
						}
					} else {
						putTileButton.setEnabled (false);
						putTileButton.setToolTipText (NON_PLAYABLE_TILE_SELECTED);						
					}
				} else {
					putTileButton.setEnabled (false);
					putTileButton.setToolTipText (NO_TILE_SELECTED);
				}
			} else {
				putTileButton.setToolTipText (NO_SELECTED_MAP_CELL);
			}
		} else {
			putTileButton.setEnabled (false);
			putTileButton.setToolTipText (NO_OPERATING_COMPANY);
		}
	}

	public void handleRemoveRouteSegment (MapCell aSelectedMapCell) {
		if (routeInformation.getSegmentCount () > 1) {
			routeInformation.removeEndIfMapCell (aSelectedMapCell);
		}
	}
	
	public void handleSelectedRoute (MapCell aSelectedMapCell, RevenueCenter aSelectedRC) {
		RouteSegment tRouteSegment;
		Corporation tCorporation;
		int tCorpID;
		int tPhase;
		int tTrainIndex;
		RouteAction tRouteAction;
		StartRouteAction tStartRouteAction;
		ActionStates tRoundType;
		String tRoundID;
		RoundManager tRoundManager;
		ActionManager tActionManager;
		Location tStartLocation;
		Location tEndLocation;

		tCorporation = getOperatingCompany ();
		tRouteAction = RouteAction.NO_ROUTE_ACTION;
		tRouteSegment = new RouteSegment (aSelectedMapCell);
		tCorpID = tCorporation.getID ();
		tPhase = getCurrentPhase ();
		tRoundManager = gameManager.getRoundManager ();
		tActionManager = tRoundManager.getActionManager ();
		tRoundType = tRoundManager.getCurrentRoundType ();
		tRoundID = tRoundManager.getOperatingRoundID ();
		if (routeInformation.isEmpty ()) {
			if (aSelectedRC != RevenueCenter.NO_CENTER) {
				tStartRouteAction = new StartRouteAction (tRoundType, tRoundID, tCorporation);
				tStartLocation = aSelectedRC.getLocation ();
				tEndLocation = new Location ();
				tTrainIndex = routeInformation.getTrainIndex ();
				tStartRouteAction.addStartRouteEffect (tCorporation, tTrainIndex, aSelectedMapCell, tStartLocation,
						tEndLocation);
				tRouteAction = tStartRouteAction;
			} else {
				logger.error ("Need to Select a Revenue Center to start a new Route");
			}
		} else {
			tRouteAction = new ExtendRouteAction (tRoundType, tRoundID, tCorporation);
			tRouteAction.setChainToPrevious (true);
		}
		if (tRouteAction != RouteAction.NO_ROUTE_ACTION) {
			routeInformation.setStartSegment (tRouteSegment, aSelectedRC, tPhase, tCorpID);
			routeInformation.extendRouteInformation (tRouteSegment, tPhase, tCorpID, tRouteAction);

			tActionManager.addAction (tRouteAction);
		}
	}

	public MapCell getMapCellForID (String aMapCellID) {
		MapCell tMapCell;

		tMapCell = map.getMapCellForID (aMapCellID);

		return tMapCell;
	}

	public Corporation getCorporationByID (int aCorporationID) {
		return shareCos.getCorporationByID (aCorporationID);
	}

	public Corporation getCorporation (String aCorporationAbbrev) {
		return shareCos.getCorporation (aCorporationAbbrev);
	}
}
