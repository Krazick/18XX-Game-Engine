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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.map.GameMap;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;
import geUtilities.xml.GameEngineManager;
import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;

public class PlanFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TITLE = "Plan";
	public static final JScrollBar NO_JSCROLL_BAR = null;
	GameMap planningMap;
	JPanel mapPanel;
	JPanel tilePanel;
	JPanel buttonPanel;
	KButton discardPlan;
	KButton applyPlan;
	KButton savePlan;
	MapPlan mapPlan;
	
	public PlanFrame (String aFrameName, GameEngineManager aGameManager) {
		this (aFrameName, aGameManager, MapPlan.NO_MAP_PLAN);
	}
	
	public PlanFrame (String aFrameName, GameEngineManager aGameManager, MapPlan aMapPlan) {
		super (aFrameName, aGameManager);
		setMapPlan (aMapPlan);
		try {
			setSize (900, 500);
			buildMapPanel ();
			buildTilePanel ();
			buildButtonPanel ();
			
			add (mapPanel, BorderLayout.WEST);
			add (tilePanel, BorderLayout.CENTER);
			add (buttonPanel, BorderLayout.EAST);
			showFrame ();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void buildButtonPanel () {
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
		buttonPanel = new JPanel ();
		buttonPanel.setLayout (new BoxLayout (buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setBorder (tMargin);
		tButtonLabel = new JLabel ("This is a ButtonPanel");
		buttonPanel.add (tButtonLabel);
		buttonPanel.add (Box.createVerticalStrut (10));
		
		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
			tCorporation = tPlaceMapTilePlan.getCorporation ();
			if (tCorporation != Corporation.NO_CORPORATION) {
				tCompanyInfo = new JLabel ("Operating Company is " + tCorporation.getAbbrev ());
				buttonPanel.add (tCompanyInfo);
				buttonPanel.add (Box.createVerticalStrut (10));

			} else {
				tCompanyInfo = null;
			}
			tMapCell = tPlaceMapTilePlan.getMapCell ();
			if (tMapCell != MapCell.NO_MAP_CELL) {
				tMapCellInfo = new JLabel ("MapCell ID is " + tMapCell.getID ());
				buttonPanel.add (tMapCellInfo);
				buttonPanel.add (Box.createVerticalStrut (10));
				
				tBuildCost = Bank.formatCash (tMapCell.getCostToLayTile ());
				tBuildCostLabel = new JLabel ("Build Cost " + tBuildCost);
				buttonPanel.add (tBuildCostLabel);
				buttonPanel.add (Box.createVerticalStrut (10));

				if (tMapCell.isTileOnCell ()) {
					tTile = tMapCell.getTile ();
					tTileInfoLabel = new JLabel (tTile.getType ().getName () + " Tile # " + tTile.getNumber ());
				} else {
					tTileInfoLabel = new JLabel ("No Tile on the MapCell");
				}
				buttonPanel.add (tTileInfoLabel);
				buttonPanel.add (Box.createVerticalStrut (10));

			} else {
				tMapCellInfo = null;
			}
		}
		buttonPanel.setBackground (Color.green);
		tViewSize = new Dimension (300, 500);
		buttonPanel.setSize (tViewSize);
		buttonPanel.setPreferredSize (tViewSize);
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
		String tScrollBarInfo;
		Dimension tViewSize;
		
		mapPanel = new JPanel ();
		tGameManager = (GameManager) gameEngineManager;
		tGameMap = tGameManager.getGameMap ();
		planningMap = tGameMap.clone ();
		buildTheScrollPane (planningMap);
		tViewSize = new Dimension (300, 460);
		mapPanel.setSize (tViewSize);
		mapPanel.setPreferredSize (tViewSize);
		mapPanel.add (scrollPane);
		
		tScrollBarInfo = getScrollBarInfo (ScrollPaneConstants.HORIZONTAL_SCROLLBAR) + "\n" +
						getScrollBarInfo (ScrollPaneConstants.VERTICAL_SCROLLBAR);
		System.out.println (tScrollBarInfo);
		setScrollBarValue (ScrollPaneConstants.VERTICAL_SCROLLBAR, 100);
		setScrollBarValue (ScrollPaneConstants.HORIZONTAL_SCROLLBAR, 100);

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
	
	public void setScrollBarValue (String aOrientation, int aValue) {
		JScrollBar tJScrollBar;
		
		tJScrollBar = NO_JSCROLL_BAR;
		if (aOrientation == ScrollPaneConstants.HORIZONTAL_SCROLLBAR) {
			tJScrollBar = scrollPane.getHorizontalScrollBar ();
		} else if (aOrientation == ScrollPaneConstants.VERTICAL_SCROLLBAR) {
			tJScrollBar = scrollPane.getHorizontalScrollBar ();
		}
		if (tJScrollBar != NO_JSCROLL_BAR) {
			tJScrollBar.setValue (aValue);
		}
	}
	
	public String getScrollBarInfo (String aOrientation) {
		JScrollBar tJScrollBar;
		String tScrollBarInfo;
		
		tJScrollBar = NO_JSCROLL_BAR;
		if (aOrientation == ScrollPaneConstants.HORIZONTAL_SCROLLBAR) {
			tJScrollBar = scrollPane.getHorizontalScrollBar ();
		} else if (aOrientation == ScrollPaneConstants.VERTICAL_SCROLLBAR) {
			tJScrollBar = scrollPane.getHorizontalScrollBar ();
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
