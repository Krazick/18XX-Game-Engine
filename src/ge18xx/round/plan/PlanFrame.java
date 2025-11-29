package ge18xx.round.plan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

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
import geUtilities.xml.GameEngineManager;
import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;

public class PlanFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TITLE = "Map Plan Frame";
	public static final JScrollBar NO_JSCROLL_BAR = null;
	GameMap planningMap;
	JPanel mapPanel;
	JPanel tilePanel;
	JPanel infoAndActionPanel;
	PlanTileSet planTileSet;
	KButton discardPlan;
	KButton applyPlan;
	KButton savePlan;
	MapPlan mapPlan;
	
	public PlanFrame (String aFrameName, GameEngineManager aGameManager) {
		this (aFrameName, aGameManager, MapPlan.NO_MAP_PLAN);
	}
	
	public PlanFrame (String aFrameName, GameEngineManager aGameManager, MapPlan aMapPlan) {
		super (aFrameName, aGameManager);
		
		String tFullFrameTitle;

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
			tFullFrameTitle = BASE_TITLE + " (" + aMapPlan.getName () + ")";
			setTitle (tFullFrameTitle);
			showFrame ();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
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
			tCompanyList = new JComboBox<String> (tPlayerCompanies);
			infoAndActionPanel.add (tCompanyChoice);
			infoAndActionPanel.add (tCompanyList);
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
		JLabel tTilePanelLabel;
		Dimension tViewSize;
		
		tTilePanelLabel = new JLabel ("This is a TilePanel");
		tilePanel = new JPanel ();
		tilePanel.setLayout (new BoxLayout (tilePanel, BoxLayout.Y_AXIS));

		tilePanel.add (tTilePanelLabel);
		tilePanel.setBackground (Color.cyan);
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
		JScrollPane tTileScrollPane;
		Tile tTile;
		int tTileCountToShow;
		
		planTileSet = new PlanTileSet ("Plan Tile Set");
		
		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
			tCount = tPlaceMapTilePlan.playableTilesCount ();
			for (tIndex = 0; tIndex < tCount; tIndex++) {
				tGameTile = tPlaceMapTilePlan.getPlayableTileAt (tIndex);
				tTile = tGameTile.getTile ();
				planTileSet.addTile (tTile, 1);
				System.out.println ("Adding Tile # " + tTile.getNumber ());
			}
			planTileSet.setBounds (0, 0, 300, 500);
			tViewSize = new Dimension (250, 400);
			tTileScrollPane = buildaScrollPane (planTileSet, tViewSize);
			tilePanel.add (tTileScrollPane);
//			tilePanel.add (planTileSet);
			tTileCountToShow = planTileSet.getTileCountToShow ();
			tilePanel.add (new JLabel ("Tile Count To Show: " + tTileCountToShow));
			planTileSet.validate ();
			tilePanel.validate ();
			repaint ();
			revalidate ();
		}
	}

	private void buildMapPanel () throws CloneNotSupportedException {
		GameManager tGameManager;
		GameMap tGameMap;
		Dimension tViewSize;
		float tHorizontalPercent;
		float tVerticalPercent;
		float tImageWidth;
		float tImageHeight;
		
		mapPanel = new JPanel ();
		tGameManager = (GameManager) gameEngineManager;
		tGameMap = tGameManager.getGameMap ();
		planningMap = tGameMap.clone ();
		tViewSize = new Dimension (300, 460);

		scrollPane = buildaScrollPane (planningMap, tViewSize);
		mapPanel.setSize (tViewSize);
		mapPanel.setPreferredSize (tViewSize);
		mapPanel.add (scrollPane);
		
		tImageWidth = planningMap.getMaxX ();
		tImageHeight = planningMap.getMaxY ();
	
		if (mapPlan.getMapCell () != MapCell.NO_MAP_CELL) {
			tVerticalPercent = (mapPlan.getMapCellYc () - 250.0f)/tImageHeight;
			setScrollBarValue (ScrollPaneConstants.VERTICAL_SCROLLBAR, tVerticalPercent);
			tHorizontalPercent = (mapPlan.getMapCellXc () - 150.0f)/tImageWidth;
			setScrollBarValue (ScrollPaneConstants.HORIZONTAL_SCROLLBAR, tHorizontalPercent);
		}
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
	}
	
	public void setScrollBarValue (String aOrientation, float aPercentOfMax) {
		
		SwingUtilities.invokeLater ( () -> {
			JScrollBar tJScrollBar;
			int tTargetValue;
			float tScrollMax;

			tJScrollBar = NO_JSCROLL_BAR;
			if (aOrientation == ScrollPaneConstants.HORIZONTAL_SCROLLBAR) {
				tJScrollBar = scrollPane.getHorizontalScrollBar ();
			} else if (aOrientation == ScrollPaneConstants.VERTICAL_SCROLLBAR) {
				tJScrollBar = scrollPane.getVerticalScrollBar ();
			}
			if (tJScrollBar != NO_JSCROLL_BAR) {
				tScrollMax = tJScrollBar.getMaximum ();
				tTargetValue = (int) (tScrollMax * aPercentOfMax);
				tJScrollBar.setValue (tTargetValue);
			}
		});
		
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
}
