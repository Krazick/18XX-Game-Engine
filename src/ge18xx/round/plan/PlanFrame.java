package ge18xx.round.plan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.map.GameMap;
import ge18xx.map.MapCell;
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
			setSize (500, 500);
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
		Corporation tCorporation;
		MapCell tMapCell;
		PlaceMapTilePlan tPlaceMapTilePlan;
		
		buttonPanel = new JPanel ();
		mapPanel.setLayout (new BoxLayout (mapPanel, BoxLayout.Y_AXIS));
		tButtonLabel = new JLabel ("This is a ButtonPanel");
		buttonPanel.add (tButtonLabel);
		
		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
			tCorporation = tPlaceMapTilePlan.getCorporation ();
			if (tCorporation != Corporation.NO_CORPORATION) {
				tCompanyInfo = new JLabel ("Operating Company is " + tCorporation.getName ());
				buttonPanel.add (tCompanyInfo);
			} else {
				tCompanyInfo = null;
			}
			tMapCell = tPlaceMapTilePlan.getMapCell ();
			if (tMapCell != MapCell.NO_MAP_CELL) {
				tMapCellInfo = new JLabel ("MapCell ID is " + tMapCell.getID ());
				buttonPanel.add (tMapCellInfo);
			} else {
				tMapCellInfo = null;
			}
		}
		buttonPanel.setBackground (Color.green);
	}

	private void buildTilePanel () {
		JLabel tTilePanelLabel;
		
		tTilePanelLabel = new JLabel ("This is a TilePanel");
		tilePanel = new JPanel ();
		tilePanel.add (tTilePanelLabel);
		tilePanel.setBackground (Color.blue);
	}

	private void buildMapPanel () throws CloneNotSupportedException {
//		JLabel tMapPanelLabel;
		GameManager tGameManager;
		GameMap tGameMap;
		Rectangle tViewArea;
		String tScrollBarInfo;
		
//		tMapPanelLabel = new JLabel ("This is a MapPanel");
		mapPanel = new JPanel ();
//		mapPanel.setLayout (new BorderLayout ());
//		mapPanel.add (tMapPanelLabel, BorderLayout.NORTH);
		tGameManager = (GameManager) gameEngineManager;
		tGameMap = tGameManager.getGameMap ();
		planningMap = tGameMap.clone ();
		buildTheScrollPane (planningMap);
		mapPanel.add (scrollPane);
		tViewArea = mapPlan.buildSelectedViewArea ();
		scrollPane.scrollRectToVisible (tViewArea);
		
		tScrollBarInfo = getScrollBarInfo (ScrollPaneConstants.HORIZONTAL_SCROLLBAR) + "\n" +
						getScrollBarInfo (ScrollPaneConstants.VERTICAL_SCROLLBAR);
		System.out.println (tScrollBarInfo);
		setScrollBarValue (ScrollPaneConstants.VERTICAL_SCROLLBAR, 100);
		setScrollBarValue (ScrollPaneConstants.HORIZONTAL_SCROLLBAR, 90);
	}

	public void buildTheScrollPane (JComponent aImage) {
		Dimension tViewSize;
		
		tViewSize = new Dimension (300, 500);
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
