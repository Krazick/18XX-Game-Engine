package ge18xx.round.plan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.GameMap;
import ge18xx.map.MapCell;
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
		TrainCompany tTrainCompany;
		
		tTrainCompany = (TrainCompany) aMapPlan.getCorporation ();
		System.out.println ("Ready to build a Map Plan for " + tTrainCompany.getName () +
				" Player is " + aMapPlan.getPlayerName ());

		setMapPlan (aMapPlan);
		try {
			setSize (900, 500);
			buildMapPanel ();
			buildTilePanel ();
			buildInfoAndActionPanel ();
			
			add (mapPanel, BorderLayout.WEST);
			add (tilePanel, BorderLayout.CENTER);
			add (infoAndActionPanel, BorderLayout.EAST);
			tFullFrameTitle = BASE_TITLE + " (" + aMapPlan.getName () + ")";
			setTitle (tFullFrameTitle);
			showFrame ();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private void buildInfoAndActionPanel () {
		JLabel tButtonLabel;
		JLabel tCompanyInfo;
		JLabel tMapCellInfo;
		JLabel tBuildCostLabel;
		JLabel tTileInfoLabel;
		Corporation tCorporation;
		MapCell tMapCell;
		PlaceMapTilePlan tPlaceMapTilePlan;
		Dimension tViewSize;
		Border tMargin;
		String tBuildCost;
		Tile tTile;
		
		tMargin = new EmptyBorder (10,10,10,10);
		infoAndActionPanel = new JPanel ();
		infoAndActionPanel.setLayout (new BoxLayout (infoAndActionPanel, BoxLayout.Y_AXIS));
		infoAndActionPanel.setBorder (tMargin);
		tButtonLabel = new JLabel ("This is the Info And Action Panel");
		infoAndActionPanel.add (tButtonLabel);
		infoAndActionPanel.add (Box.createVerticalStrut (10));
		
		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
			tCorporation = tPlaceMapTilePlan.getCorporation ();
			if (tCorporation != Corporation.NO_CORPORATION) {
				tCompanyInfo = new JLabel ("Operating Company is " + tCorporation.getAbbrev ());
				infoAndActionPanel.add (tCompanyInfo);
				infoAndActionPanel.add (Box.createVerticalStrut (10));

			} else {
				tCompanyInfo = null;
			}
			tMapCell = tPlaceMapTilePlan.getMapCell ();
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
					tPlaceMapTilePlan.setPlayableTiles (planningMap);
					
				}
				infoAndActionPanel.add (tTileInfoLabel);
				infoAndActionPanel.add (Box.createVerticalStrut (10));

			} else {
				tMapCellInfo = null;
			}
		}
		infoAndActionPanel.setBackground (Color.green);
		tViewSize = new Dimension (300, 500);
		infoAndActionPanel.setSize (tViewSize);
		infoAndActionPanel.setPreferredSize (tViewSize);
	}

	private void buildTilePanel () {
		JLabel tTilePanelLabel;
		Dimension tViewSize;
		
		tTilePanelLabel = new JLabel ("This is a TilePanel");
		tilePanel = new JPanel ();
		tilePanel.add (tTilePanelLabel);
		tilePanel.setBackground (Color.cyan);
		tViewSize = new Dimension (300, 500);
		tilePanel.setSize (tViewSize);
		tilePanel.setPreferredSize (tViewSize);
	}

	private void buildMapPanel () throws CloneNotSupportedException {
		GameManager tGameManager;
		GameMap tGameMap;
//		String tScrollBarInfo;
		Dimension tViewSize;
		float tHorizontalPercent;
		float tVerticalPercent;
		float tImageWidth;
		float tImageHeight;
		
		mapPanel = new JPanel ();
		tGameManager = (GameManager) gameEngineManager;
		tGameMap = tGameManager.getGameMap ();
		planningMap = tGameMap.clone ();
		buildTheScrollPane (planningMap);
		tViewSize = new Dimension (300, 460);
		mapPanel.setSize (tViewSize);
		mapPanel.setPreferredSize (tViewSize);
		mapPanel.add (scrollPane);
		
//		tScrollBarInfo = getScrollBarInfo (ScrollPaneConstants.HORIZONTAL_SCROLLBAR) + "\n" +
//						getScrollBarInfo (ScrollPaneConstants.VERTICAL_SCROLLBAR);
		
		tImageWidth = planningMap.getMaxX ();
		tImageHeight = planningMap.getMaxY ();
	
		if (mapPlan.getMapCell () != MapCell.NO_MAP_CELL) {
			tVerticalPercent = (mapPlan.getMapCellYc () - 250.0f)/tImageHeight;
			setScrollBarValue (ScrollPaneConstants.VERTICAL_SCROLLBAR, tVerticalPercent);
			tHorizontalPercent = (mapPlan.getMapCellXc () - 150.0f)/tImageWidth;
			setScrollBarValue (ScrollPaneConstants.HORIZONTAL_SCROLLBAR, tHorizontalPercent);
//		
//			tScrollBarInfo = getScrollBarInfo (ScrollPaneConstants.HORIZONTAL_SCROLLBAR) + "\n" +
//				getScrollBarInfo (ScrollPaneConstants.VERTICAL_SCROLLBAR);
		}
	}

	public void buildTheScrollPane (JComponent aImage) {
		Dimension tViewSize;
		
		tViewSize = new Dimension (300, 460);
		scrollPane = new JScrollPane (aImage);
		scrollPane.setPreferredSize (tViewSize);
		scrollPane.setSize (tViewSize);
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
				System.out.println ("Percent of Max " + aPercentOfMax + " Target Value " + tTargetValue);
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
