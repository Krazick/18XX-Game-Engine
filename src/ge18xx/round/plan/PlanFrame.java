package ge18xx.round.plan;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	}

	private void buildTilePanel () {
		JLabel tTilePanelLabel;
		
		tTilePanelLabel = new JLabel ("This is a TilePanel");
		tilePanel = new JPanel ();
		tilePanel.add (tTilePanelLabel);
	}

	private void buildMapPanel () throws CloneNotSupportedException {
		JLabel tMapPanelLabel;
		GameManager tGameManager;
		GameMap tGameMap;
		
		tMapPanelLabel = new JLabel ("This is a MapPanel");
		mapPanel = new JPanel ();
		mapPanel.setLayout (new BorderLayout ());
		mapPanel.add (tMapPanelLabel, BorderLayout.NORTH);
		tGameManager = (GameManager) gameEngineManager;
		tGameMap = tGameManager.getGameMap ();
		planningMap = tGameMap.clone ();
		buildScrollPane (planningMap, BorderLayout.SOUTH);
		scrollPane.setSize (300, 300);
	}

	public void setMapPlan (MapPlan aMapPlan) {
		mapPlan = aMapPlan;
	}
}
