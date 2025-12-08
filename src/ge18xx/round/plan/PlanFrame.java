package ge18xx.round.plan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.map.GameMap;
import ge18xx.map.MapCell;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.toplevel.MapFrame;
import geUtilities.xml.GameEngineManager;
import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;

public class PlanFrame extends XMLFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TITLE = "Map Plan";
	public static final JScrollBar NO_JSCROLL_BAR = null;
	GameMap planningMap;
	JPanel mapPanel;
	JPanel tilePanel;
	JPanel infoAndActionPanel;
	JPanel mapButtonsPanel;
	PlanTileSet planTileSet;
	TileSet fullTileSet;
	JScrollPane tileScrollPane;
	KButton putdownTileButton;
	KButton pickupTileButton;
	KButton rotateTileButton;
	KButton discardPlan;
	KButton applyPlan;
	KButton savePlan;
	MapPlan mapPlan;
	boolean tilePlaced;
	
	public PlanFrame (String aFrameName, GameEngineManager aGameManager) {
		this (aFrameName, aGameManager, MapPlan.NO_MAP_PLAN);
	}
	
	public PlanFrame (String aFrameName, GameEngineManager aGameManager, MapPlan aMapPlan) {
		super (aFrameName, aGameManager);
		
		String tFullFrameTitle;
		TileSet tFullTileSet;
		GameManager tGameManager;
		
		System.out.println ("Ready to build a Map Plan for Player is " + aMapPlan.getPlayerName ());

		setMapPlan (aMapPlan);
		try {
			setSize (900, 500);
			buildMapPanel ();
			buildTilePanel ();
			buildInfoAndActionPanel ();
			
			add (mapPanel, BorderLayout.WEST);
			add (infoAndActionPanel, BorderLayout.EAST);
			add (tilePanel, BorderLayout.CENTER);
			
			tGameManager = (GameManager) gameEngineManager;
			tFullTileSet = tGameManager.getTileSet ();
			setFullTileSet (tFullTileSet);
			
			tFullFrameTitle = BASE_TITLE + " (" + aMapPlan.getName () + ")";
			tFullFrameTitle = aGameManager.createFrameTitle (tFullFrameTitle);
			setTitle (tFullFrameTitle);
			showFrame ();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public void setFullTileSet (TileSet aFullTileSet) {
		fullTileSet = aFullTileSet;
	}
	
	public TileSet getFullTileSet () {
		return fullTileSet;
	}
	
	private void buildInfoAndActionPanel () {
		JLabel tButtonLabel;
		PlaceMapTilePlan tPlaceMapTilePlan;
		Dimension tViewSize;
		Border tMargin;
		
		tMargin = new EmptyBorder (10,10,10,10);
		infoAndActionPanel = new JPanel ();
		infoAndActionPanel.setLayout (new BoxLayout (infoAndActionPanel, BoxLayout.Y_AXIS));
		infoAndActionPanel.setBorder (tMargin);
		tButtonLabel = new JLabel ("This is the Info And Action Panel");
		infoAndActionPanel.add (tButtonLabel);
		infoAndActionPanel.add (Box.createVerticalStrut (10));
		
		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
			addCorporationInfo (tPlaceMapTilePlan);
			addMapCellInfo (tPlaceMapTilePlan);
		}
		infoAndActionPanel.setBackground (Color.green);
		tViewSize = new Dimension (300, 500);
		infoAndActionPanel.setSize (tViewSize);
		infoAndActionPanel.setPreferredSize (tViewSize);
	}

	protected void addCorporationInfo (PlaceMapTilePlan aPlaceMapTilePlan) {
		JLabel tCompanyInfo;
		JLabel tCompanyChoice;
		JComboBox<String> tCompanyList;
		Corporation tCorporation;
		GameManager tGameManager;
		String [] tPlayerCompanies;
		
		tCorporation = aPlaceMapTilePlan.getCorporation ();
		if (tCorporation != Corporation.NO_CORPORATION) {
			tCompanyInfo = new JLabel ("Operating Company is " + tCorporation.getAbbrev ());
			infoAndActionPanel.add (tCompanyInfo);
			infoAndActionPanel.add (Box.createVerticalStrut (10));
		} else {
			tGameManager = (GameManager) getGameManager ();
			tPlayerCompanies = tGameManager.getPlayerCompanies (mapPlan.getPlayerName ());
			tCompanyChoice = new JLabel ("Company to Plan for: ");
			if (tPlayerCompanies.length == 0) {
				tPlayerCompanies = tGameManager.getAllCompanyAbbrevs ();
			}
			tCompanyList = new JComboBox<String> (tPlayerCompanies);
			infoAndActionPanel.add (tCompanyChoice);
			infoAndActionPanel.add (Box.createVerticalStrut (10));
			infoAndActionPanel.add (tCompanyList);
			infoAndActionPanel.add (Box.createVerticalStrut (10));
			tCompanyInfo = null;
		}
	}

	protected void addMapCellInfo (PlaceMapTilePlan aPlaceMapTilePlan) {
		JLabel tMapCellInfo;
		JLabel tBuildCostLabel;
		JLabel tTileInfoLabel;
		MapCell tMapCell;
		String tBuildCost;
		Tile tTile;
		
		tMapCell = aPlaceMapTilePlan.getMapCell ();
		if (tMapCell != MapCell.NO_MAP_CELL) {
			tMapCellInfo = new JLabel ("MapCell ID is " + tMapCell.getID ());
			infoAndActionPanel.add (tMapCellInfo);
			infoAndActionPanel.add (Box.createVerticalStrut (10));
			
			tBuildCost = Bank.formatCash (tMapCell.getCostToLayTile ());
			tBuildCostLabel = new JLabel ("Build Cost " + tBuildCost);
			infoAndActionPanel.add (tBuildCostLabel);
			infoAndActionPanel.add (Box.createVerticalStrut (10));

			if (tMapCell.isTileOnCell ()) {
				tTile = tMapCell.getTile ();
				tTileInfoLabel = new JLabel (tTile.getType ().getName () + " Tile # " + tTile.getNumber ());
				aPlaceMapTilePlan.setPlayableTiles (planningMap);
				fillPlanTileSet ();
			} else {
				tTileInfoLabel = new JLabel ("No Tile on the MapCell");
				// Build a set of Tiles that can be placed on this MapCell
				// show these in the Tile Panel. Need to Clone the Tiles, regardless if there are none available
				// in the game's inventory. This will allow it to be placed on the Planning Map 
				aPlaceMapTilePlan.setPlayableTiles (planningMap);
				fillPlanTileSet ();
			}
			infoAndActionPanel.add (tTileInfoLabel);
			infoAndActionPanel.add (Box.createVerticalStrut (10));

		} else {
			tMapCellInfo = null;
		}
	}

	private void buildTilePanel () {
		Dimension tViewSize;
		
		tilePanel = new JPanel ();
		tilePanel.setLayout (new BoxLayout (tilePanel, BoxLayout.Y_AXIS));

		tViewSize = new Dimension (300, 500);
		tilePanel.setSize (tViewSize);
		tilePanel.setPreferredSize (tViewSize);
	}

	protected void fillPlanTileSet () {
		int tIndex;
		int tCount;
		PlaceMapTilePlan tPlaceMapTilePlan;
		GameTile tGameTile;
		Dimension tViewSize;
		JLabel tNoUpgrades;
		Border tMargin;

		planTileSet = new PlanTileSet ("Plan Tile Set", this);
		
		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
			tCount = tPlaceMapTilePlan.playableTilesCount ();
			if (tCount == 0) {
				tMargin = new EmptyBorder (30,30,30,30);
				tilePanel.setLayout (new BoxLayout (tilePanel, BoxLayout.Y_AXIS));
				tilePanel.setBorder (tMargin);
				tNoUpgrades = new JLabel ("No Upgrades available");
				tilePanel.add (Box.createVerticalStrut (30));
				tilePanel.add (tNoUpgrades);
			} else {
				for (tIndex = 0; tIndex < tCount; tIndex++) {
					tGameTile = tPlaceMapTilePlan.getPlayableTileAt (tIndex);
					planTileSet.addGameTile (tGameTile);
				}
				planTileSet.setTraySize (planningMap, tPlaceMapTilePlan);
	
				tViewSize = new Dimension (300, 460);
				tileScrollPane = buildaScrollPane (planTileSet, tViewSize);
				
				tilePanel.add (tileScrollPane);
			}
			planTileSet.validate ();
			tilePanel.validate ();
			repaint ();
			revalidate ();
		}
	}

	public PlanTileSet getPlanTileSet () {
		return planTileSet;
	}
	
	public GameMap getPlanningMap () {
		return planningMap;
	}
	
	private void buildMapPanel () throws CloneNotSupportedException {
		GameManager tGameManager;
		GameMap tGameMap;
		Dimension tViewSize;
		MapCell tPlanningMapCell;
		float tHorizontalPercent;
		float tVerticalPercent;
		float tImageWidth;
		float tImageHeight;
		
		mapPanel = new JPanel ();
		tGameManager = (GameManager) gameEngineManager;
		tGameMap = tGameManager.getGameMap ();
		planningMap = tGameMap.clone ();
		tPlanningMapCell = planningMap.getSelectedMapCell ();
		mapPlan.setPlanningMapCell (tPlanningMapCell);
		tViewSize = new Dimension (300, 400);

		scrollPane = buildaScrollPane (planningMap, tViewSize);
		mapPanel.setSize (tViewSize);
		mapPanel.setPreferredSize (tViewSize);
		mapPanel.add (scrollPane);
		
		tImageWidth = planningMap.getMaxX ();
		tImageHeight = planningMap.getMaxY ();
	
		if (mapPlan.getMapCell () != MapCell.NO_MAP_CELL) {
			tVerticalPercent = (mapPlan.getMapCellYc () - 200.0f)/tImageHeight;
			setScrollBarValue (scrollPane, ScrollPaneConstants.VERTICAL_SCROLLBAR, tVerticalPercent);
			tHorizontalPercent = (mapPlan.getMapCellXc () - 150.0f)/tImageWidth;
			setScrollBarValue (scrollPane, ScrollPaneConstants.HORIZONTAL_SCROLLBAR, tHorizontalPercent);
		}
		
		buildMapButtonsPanel ();
	}

	public JScrollPane buildaScrollPane (JComponent aImage, Dimension aViewSize) {
		JScrollPane tScrollPane;
		
		tScrollPane = new JScrollPane (aImage);
		tScrollPane.setPreferredSize (aViewSize);
		tScrollPane.setSize (aViewSize);
		tScrollPane.setMaximumSize (aViewSize);
//		tScrollPane.setHorizontalScrollBarPolicy (ScrolPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		return tScrollPane;
	}

	public void setMapPlan (MapPlan aMapPlan) {
		mapPlan = aMapPlan;
		if (mapPlan != MapPlan.NO_MAP_PLAN) {
			mapPlan.setPlanFrame (this);
		}
	}
	
	public void setScrollBarValue (JScrollPane aScrollPane, String aOrientation, float aPercentOfMax) {
		SwingUtilities.invokeLater ( () -> {
			JScrollBar tJScrollBar;
			int tTargetValue;
			float tScrollMax;

			tJScrollBar = NO_JSCROLL_BAR;
			if (aOrientation == ScrollPaneConstants.HORIZONTAL_SCROLLBAR) {
				tJScrollBar = aScrollPane.getHorizontalScrollBar ();
			} else if (aOrientation == ScrollPaneConstants.VERTICAL_SCROLLBAR) {
				tJScrollBar = aScrollPane.getVerticalScrollBar ();
			}
			if (tJScrollBar != NO_JSCROLL_BAR) {
				tScrollMax = tJScrollBar.getMaximum ();
				tTargetValue = (int) (tScrollMax * aPercentOfMax);
				tJScrollBar.setValue (tTargetValue);
			}
		});
	}
	
	protected void buildMapButtonsPanel () {
		mapButtonsPanel = new JPanel ();
		mapButtonsPanel.setLayout (new BoxLayout (mapButtonsPanel, BoxLayout.X_AXIS));
		
		mapButtonsPanel.add (Box.createHorizontalStrut (5));
		
		putdownTileButton = setupButton (MapFrame.PUT_TILE_LABEL, MapFrame.PUT_TILE, this, Component.CENTER_ALIGNMENT);
		mapButtonsPanel.add (putdownTileButton);
		mapButtonsPanel.add (Box.createHorizontalStrut (5));

		pickupTileButton = setupButton (MapFrame.PICKUP_TILE_LABEL, MapFrame.PICKUP_TILE, this, Component.CENTER_ALIGNMENT);
		mapButtonsPanel.add (pickupTileButton);
		mapButtonsPanel.add (Box.createHorizontalStrut (5));
		
		rotateTileButton = setupButton (MapFrame.ROTATE_TILE_LABEL, MapFrame.ROTATE_TILE, this, Component.CENTER_ALIGNMENT);
		mapButtonsPanel.add (rotateTileButton);
		mapButtonsPanel.add (Box.createHorizontalStrut (5));
		
		update ();
		setTilePlaced (false);
		mapPanel.add (mapButtonsPanel);
	}

	public void setTilePlaced (boolean aTilePlaced) {
		tilePlaced = aTilePlaced;
	}
	
	public void update () {
		updatePutdownTileButton ();
		updatePickupTileButton ();
		updateRotateTileButton ();
		validate ();
		repaint ();
	}
	
	public void updatePutdownTileButton () {
		GameTile tSelectedGameTile;
		
		if (planTileSet != TileSet.NO_TILE_SET) {
			tSelectedGameTile = planTileSet.getSelectedTile ();
			if (tSelectedGameTile == GameTile.NO_GAME_TILE) {
				putdownTileButton.setEnabled (false);
				putdownTileButton.setToolTipText ("No Selected GameTile to place");
			} else {
				putdownTileButton.setEnabled (true);
				putdownTileButton.setToolTipText ("GameTile has been selected to place");
			}
		} else {
			putdownTileButton.setEnabled (false);
			putdownTileButton.setToolTipText ("No Selected GameTile to place");
		}
	}
	
	public void updatePickupTileButton () {
		if (planTileSet != TileSet.NO_TILE_SET) {
			if (tilePlaced) {
				pickupTileButton.setEnabled (true);
				pickupTileButton.setToolTipText ("GameTile has been placed, can pickup");
			} else {
				pickupTileButton.setEnabled (false);
				pickupTileButton.setToolTipText ("No placed GameTile to pickup");		
			}
		} else {
			pickupTileButton.setEnabled (false);
			pickupTileButton.setToolTipText ("No placed GameTile to pickup");
		}
	}
	
	public void updateRotateTileButton () {
		if (planTileSet != TileSet.NO_TILE_SET) {
			if (tilePlaced) {
				rotateTileButton.setEnabled (true);
				rotateTileButton.setToolTipText ("GameTile has been placed, can rotate");
			} else {
				rotateTileButton.setEnabled (false);
				rotateTileButton.setToolTipText ("No placed GameTile to rotate");		
			}
		} else {
			rotateTileButton.setEnabled (false);
			rotateTileButton.setToolTipText ("No placed GameTile to rotate");
		}
	}
	
	public String getScrollBarInfo (String aOrientation) {
		JScrollBar tJScrollBar;
		String tScrollBarInfo;
		
		tJScrollBar = NO_JSCROLL_BAR;
		if (aOrientation == ScrollPaneConstants.HORIZONTAL_SCROLLBAR) {
			tJScrollBar = scrollPane.getHorizontalScrollBar ();
		} else if (aOrientation == ScrollPaneConstants.VERTICAL_SCROLLBAR) {
			tJScrollBar = scrollPane.getVerticalScrollBar ();
		}
		if (tJScrollBar != NO_JSCROLL_BAR) {
			tScrollBarInfo = aOrientation + " Min Value " + tJScrollBar.getMinimum () +
						" Max Value " + tJScrollBar.getMaximum () +
						" Current Value " + tJScrollBar.getValue ();
		} else {
			tScrollBarInfo = "No ScrollBar found ;";
		}
		
		return tScrollBarInfo;
	}
	
	/**
	 * Create a new KButton with the specified parameters.
	 * 
	 * @param aLabel Label for the Button
	 * @param aAction Action Command for the Button
	 * @param aListener The Action Listener Class
	 * @param aAlignment The Alignment
	 * @return the newly create KButton
	 */
	@Override
	public KButton setupButton (String aLabel, String aAction, ActionListener aListener, float aAlignment) {
		KButton tButton;
	
		tButton = new KButton (aLabel);
		setupButton (aAction, aListener, aAlignment, tButton);
	
		return tButton;
	}

	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction;
		PlaceMapTilePlan tPlaceMapTilePlan;
		
		tTheAction = aActionEvent.getActionCommand ();
		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
			
			if (MapFrame.PUT_TILE.equals (tTheAction)) {
				tPlaceMapTilePlan.putTileDownOnMap ();
			} else if (MapFrame.PICKUP_TILE.equals (tTheAction)) {
				tPlaceMapTilePlan.pickupTile ();
			} else if (MapFrame.ROTATE_TILE.equals (tTheAction)) {
				tPlaceMapTilePlan.rotateTile ();
			}
		}
	}

	@Override
	public GameManager getGameManager () {
		return (GameManager) gameEngineManager;
	}
}
