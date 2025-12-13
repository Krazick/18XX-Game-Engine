package ge18xx.round.plan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import ge18xx.round.OperatingRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionManager;
import ge18xx.round.plan.condition.Condition;
import ge18xx.round.plan.condition.CorporationCanLayTile;
import ge18xx.round.plan.condition.CorporationExists;
import ge18xx.round.plan.condition.EnoughCash;
import ge18xx.round.plan.condition.NoTileOnMapCell;
import ge18xx.round.plan.condition.RoundIs;
import ge18xx.round.plan.condition.SpecifiedTileOnMapCell;
import ge18xx.round.plan.condition.TileAllowedInPhase;
import ge18xx.round.plan.condition.TileAvailableInTileSet;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.toplevel.MapFrame;
import geUtilities.GUI;
import geUtilities.xml.GameEngineManager;
import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;

public class PlanFrame extends XMLFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TITLE = "Map Plan";
	public static final String APPROVE_PLAN_LABEL = "Approve";
	public static final String APPROVE_PLAN = "ApprovePlan";
	public static final String DISCARD_PLAN_LABEL = "Discard";
	public static final String DISCARD_PLAN = "DiscardPlan";
	public static final String REVIEW_CONDITIONS_LABEL = "Review Conditions";
	public static final String REVIEW_CONDITIONS = "ReviewConditions";
	public static final String APPLY_PLAN_LABEL = "Apply";
	public static final String APPLY_PLAN = "ApplyPlan";
	public static final JScrollBar NO_JSCROLL_BAR = null;

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
	KButton approvePlanButton;
	KButton discardPlanButton;
	KButton applyPlanButton;
	KButton reviewConditionsButton;
	JLabel tileToPlayInfoLabel;
	List<Plan> allPlans = new LinkedList<Plan> ();
	MapPlan mapPlan;
	boolean tilePlaced;
	private JComboBox<String> companyList;
	private JLabel companyInfoLabel;
	
	public PlanFrame (String aFrameName, GameEngineManager aGameManager, MapPlan aMapPlan) {
		super (aFrameName, aGameManager);
		
		String tFullFrameTitle;
		TileSet tFullTileSet;
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		tFullTileSet = tGameManager.getTileSet ();
		setFullTileSet (tFullTileSet);

		setMapPlan (aMapPlan);
		allPlans.add (aMapPlan);
		
		try {
			setSize (900, 500);
			buildMapPanel ();
			buildTilePanel ();
			buildInfoAndActionPanel ();
			
			add (mapPanel, BorderLayout.WEST);
			add (infoAndActionPanel, BorderLayout.EAST);
			add (tilePanel, BorderLayout.CENTER);
			
			updateFrame ();

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
		JLabel tPanelLabel;
		PlaceMapTilePlan tPlaceMapTilePlan;
		Dimension tViewSize;
		Border tMargin;
		
		tMargin = new EmptyBorder (5, 5, 5, 5);
		infoAndActionPanel = new JPanel ();
		infoAndActionPanel.setLayout (new BoxLayout (infoAndActionPanel, BoxLayout.Y_AXIS));
		infoAndActionPanel.setBorder (tMargin);
		tPanelLabel = new JLabel ("This is the Info And Action Panel");
		infoAndActionPanel.add (tPanelLabel);
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
//		JLabel tCompanyChoice;
		Corporation tCorporation;
		GameManager tGameManager;
		String [] tPlayerCompanies;
		
		tCorporation = aPlaceMapTilePlan.getCorporation ();
		if (tCorporation != Corporation.NO_CORPORATION) {
			companyInfoLabel = new JLabel ("Operating Company is " + tCorporation.getAbbrev ());
			infoAndActionPanel.add (companyInfoLabel);
			infoAndActionPanel.add (Box.createVerticalStrut (5));
		} else {
			tGameManager = (GameManager) getGameManager ();
			tPlayerCompanies = tGameManager.getPlayerCompanies (mapPlan.getPlayerName ());
			companyInfoLabel = new JLabel ("Company to Plan for: ");
			if (tPlayerCompanies.length == 0) {
				tPlayerCompanies = tGameManager.getAllCompanyAbbrevs ();
			}
			companyList = new JComboBox<String> (tPlayerCompanies);
			infoAndActionPanel.add (companyInfoLabel);
			infoAndActionPanel.add (Box.createVerticalStrut (5));
			infoAndActionPanel.add (companyList);
			infoAndActionPanel.add (Box.createVerticalStrut (5));
		}
	}

	private void updateCorporationInfo () {
		Corporation tCorporation;
		
		tCorporation = mapPlan.getCorporation ();
		if (tCorporation != Corporation.NO_CORPORATION) {
			companyInfoLabel.setText ("Operating Company is " + tCorporation.getAbbrev ());
			companyList.setVisible (false);
		}
	}
	
	protected void addMapCellInfo (PlaceMapTilePlan aPlaceMapTilePlan) {
		JLabel tMapCellInfo;
		JLabel tBuildCostLabel;
		JLabel tTileInfoLabel;
		MapCell tMapCell;
		String tBuildCostText;
		Tile tTile;
		int tBuildCostValue;
		String tTileInfoText;
		String tSelectedTileInfoText;
		
		tMapCell = aPlaceMapTilePlan.getMapCell ();
		if (tMapCell != MapCell.NO_MAP_CELL) {
			tMapCellInfo = new JLabel ("MapCell ID is " + tMapCell.getID ());
			infoAndActionPanel.add (tMapCellInfo);
			infoAndActionPanel.add (Box.createVerticalStrut (10));
			
			tBuildCostValue = tMapCell.getCostToLayTile ();
			tBuildCostText = Bank.formatCash (tBuildCostValue);
			tBuildCostLabel = new JLabel ("Build Cost " + tBuildCostText);
			aPlaceMapTilePlan.setBuildCost (tBuildCostValue);
			infoAndActionPanel.add (tBuildCostLabel);
			infoAndActionPanel.add (Box.createVerticalStrut (10));

			if (tMapCell.isTileOnCell ()) {
				tTile = tMapCell.getTile ();
			} else {
				tTile = Tile.NO_TILE;
			}
			aPlaceMapTilePlan.setPlayableTiles ();
			
			fillPlanTileSet ();
			tTileInfoText = buildTileInfoText (tTile, true);
			tTileInfoLabel = new JLabel (tTileInfoText);
			infoAndActionPanel.add (tTileInfoLabel);
			infoAndActionPanel.add (Box.createVerticalStrut (10));

			tSelectedTileInfoText = buildTileInfoText (Tile.NO_TILE, false);

			tileToPlayInfoLabel = new JLabel (tSelectedTileInfoText);
			infoAndActionPanel.add (tileToPlayInfoLabel);
			infoAndActionPanel.add (Box.createHorizontalStrut (5));
			
			approvePlanButton = setupButton (APPROVE_PLAN_LABEL, APPROVE_PLAN, this, Component.CENTER_ALIGNMENT);
			infoAndActionPanel.add (approvePlanButton);
			infoAndActionPanel.add (Box.createHorizontalStrut (5));

			discardPlanButton = setupButton (DISCARD_PLAN_LABEL, DISCARD_PLAN, this, Component.CENTER_ALIGNMENT);
			infoAndActionPanel.add (discardPlanButton);
			infoAndActionPanel.add (Box.createHorizontalStrut (5));

			reviewConditionsButton = setupButton (REVIEW_CONDITIONS_LABEL, REVIEW_CONDITIONS, this, Component.CENTER_ALIGNMENT);
			infoAndActionPanel.add (reviewConditionsButton);
			infoAndActionPanel.add (Box.createHorizontalStrut (5));

			applyPlanButton = setupButton (APPLY_PLAN_LABEL, APPLY_PLAN, this, Component.CENTER_ALIGNMENT);
			infoAndActionPanel.add (applyPlanButton);
			infoAndActionPanel.add (Box.createHorizontalStrut (5));

		} else {
			tMapCellInfo = null;
		}
	}

	private String buildTileInfoText (Tile aTile, boolean aOriginalTile) {
		String tTileInfoText;
		
		if (aOriginalTile) {
			if (aTile == Tile.NO_TILE) {
				tTileInfoText = "No Tile on the Selected MapCell";
			} else {
				tTileInfoText = "Original " + aTile.getType ().getName () + " Tile # " + aTile.getNumber ();
			}
		} else {
			if (aTile == Tile.NO_TILE) {
				tTileInfoText = "No Tile Selected to place yet";
			} else {
				tTileInfoText = "Selected " + aTile.getType ().getName () + " Tile # " + aTile.getNumber ();
			}
		}
		
		return tTileInfoText;
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
		int tTileNumber;
		PlaceMapTilePlan tPlaceMapTilePlan;
		GameMap tPlanningMap;
		TileSet tFullTileSet;
		MapCell tMapCell;
		GameTile tGameTile;
		Tile tTile;
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
				tMapCell = tPlaceMapTilePlan.getMapCell ();
				if (tMapCell.isTileOnCell ()) {
					tTile = tMapCell.getTile ();
					tTileNumber = tTile.getNumber ();
					tFullTileSet = getFullTileSet ();
					tGameTile = tFullTileSet.getGameTile (tTileNumber);
					cloneAndAddGameTile (tGameTile, 1);
				}
				for (tIndex = 0; tIndex < tCount; tIndex++) {
					tGameTile = tPlaceMapTilePlan.getPlayableTileAt (tIndex);
					cloneAndAddGameTile (tGameTile, 0);
				}
				tPlanningMap = tPlaceMapTilePlan.getPlanningMap ();
				planTileSet.setTraySize (tPlanningMap, tPlaceMapTilePlan);
	
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

	protected void cloneAndAddGameTile (GameTile aGameTile, int aUsedCount) {
		GameTile tGameTileClone;
		Tile tTile;
		
		tGameTileClone = (GameTile) aGameTile.clone ();
		tTile = tGameTileClone.getTile ();
		tGameTileClone.pushTile (tTile);
		tGameTileClone.setUsedCount (aUsedCount);
		tGameTileClone.setTotalCount (1);
		planTileSet.addGameTile (tGameTileClone);
	}

	public PlanTileSet getPlanTileSet () {
		return planTileSet;
	}
	
	private void buildMapPanel () throws CloneNotSupportedException {
		GameManager tGameManager;
		GameMap tGameMap;
		GameMap tPlanningMap;
		Dimension tViewSize;
		MapCell tPlanningMapCell;
		float tHorizontalPercent;
		float tVerticalPercent;
		float tImageWidth;
		float tImageHeight;
		
		mapPanel = new JPanel ();
		tGameManager = (GameManager) gameEngineManager;
		tGameMap = tGameManager.getGameMap ();
		tPlanningMap = tGameMap.clone ();
		mapPlan.setPlanningMap (tPlanningMap);
		tPlanningMapCell = tPlanningMap.getSelectedMapCell ();
		mapPlan.setPlanningMapCell (tPlanningMapCell);
		tViewSize = new Dimension (300, 400);

		scrollPane = buildaScrollPane (tPlanningMap, tViewSize);
		mapPanel.setSize (tViewSize);
		mapPanel.setPreferredSize (tViewSize);
		mapPanel.add (scrollPane);
		
		tImageWidth = tPlanningMap.getMaxX ();
		tImageHeight = tPlanningMap.getMaxY ();
	
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
		
		setTilePlaced (false);
		mapPanel.add (mapButtonsPanel);
	}

	public void setTilePlaced (boolean aTilePlaced) {
		tilePlaced = aTilePlaced;
	}
	
	public boolean tileIsPlaced () {
		return tilePlaced;
	}
	
	public void updateFrame () {
		updatePutdownTileButton ();
		updatePickupTileButton ();
		updateRotateTileButton ();
		updateApprovePlanButton ();
		updateDiscardPlanButton ();
		updateApplyPlanButton ();
		updateReviewConditionsButton ();
		validate ();
		repaint ();
	}
	
	public void updatePutdownTileButton () {
		GameTile tSelectedGameTile;
		Tile tToPlayTile;
		String tToPlayTileInfoText;
		
		if (planTileSet != TileSet.NO_TILE_SET) {
			tSelectedGameTile = planTileSet.getSelectedTile ();
			if (tSelectedGameTile == GameTile.NO_GAME_TILE) {
				putdownTileButton.setEnabled (false);
				putdownTileButton.setToolTipText ("No Selected GameTile to place");
			} else {
				putdownTileButton.setEnabled (true);
				putdownTileButton.setToolTipText ("GameTile has been selected to be placed");
				tToPlayTile = tSelectedGameTile.getTile ();
				tToPlayTileInfoText = this.buildTileInfoText (tToPlayTile, false);
				tileToPlayInfoLabel.setText (tToPlayTileInfoText);
			}
		} else {
			putdownTileButton.setEnabled (false);
			putdownTileButton.setToolTipText ("No Selected GameTile to place");
		}
	}
	
	public void updatePickupTileButton () {
		if (planTileSet != TileSet.NO_TILE_SET) {
			if (mapPlan.isApproved ()) {
				pickupTileButton.setEnabled (false);
				pickupTileButton.setToolTipText ("The Plan has been approved, cannot pickup tile");
			} else if (tilePlaced) {
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
		PlaceMapTilePlan tPlaceMapTilePlan;
		boolean tTileOrientationLocked;

		tTileOrientationLocked = false;
		if (planTileSet != TileSet.NO_TILE_SET) {
			if (mapPlan instanceof PlaceMapTilePlan) {
				tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
				tTileOrientationLocked = tPlaceMapTilePlan.isTileOrientationLocked ();
			}
			if (mapPlan.isApproved ()) {
				rotateTileButton.setEnabled (false);
				rotateTileButton.setToolTipText ("The Plan has been approved, cannot rotate tile");
			} else if (tTileOrientationLocked) {
				rotateTileButton.setEnabled (false);
				rotateTileButton.setToolTipText ("Tile is Placed and Locked, cannot rotate the tile");					
			} else if (tilePlaced) {
				rotateTileButton.setEnabled (true);
				rotateTileButton.setToolTipText ("GameTile has been placed, can rotate the tile");
			} else {
				rotateTileButton.setEnabled (false);
				rotateTileButton.setToolTipText ("No placed GameTile to rotate");		
			}
		} else {
			rotateTileButton.setEnabled (false);
			rotateTileButton.setToolTipText ("No placed GameTile to rotate");
		}
	}
	
	public void updateApprovePlanButton () {
		GameTile tSelectedGameTile;
		
		if (planTileSet != TileSet.NO_TILE_SET) {
			tSelectedGameTile = planTileSet.getSelectedTile ();
			if (mapPlan.isApproved ()) {
				approvePlanButton.setEnabled (false);
				approvePlanButton.setToolTipText ("The Plan has already been approved");
			} else if (tilePlaced) {
				approvePlanButton.setEnabled (true);
				approvePlanButton.setToolTipText ("GameTile has been placed, plan can be approved");
			} else if (tSelectedGameTile == GameTile.NO_GAME_TILE) {
				approvePlanButton.setEnabled (false);
				approvePlanButton.setToolTipText ("No GameTile selected yet to be placed on MapCell, plan connot be approved");
			} else {
				approvePlanButton.setEnabled (false);
				approvePlanButton.setToolTipText ("The GameTile has not been placed, plan cannot be approved");				
			}
		} else {
			approvePlanButton.setEnabled (false);
			approvePlanButton.setToolTipText ("No Selected GameTile to place");
		}
	}
	
	public void updateDiscardPlanButton () {
		discardPlanButton.setEnabled (true);
		discardPlanButton.setToolTipText ("Plan can be discarded at any time");
	}
	
	public void updateReviewConditionsButton () {
		if (planTileSet != TileSet.NO_TILE_SET) {
			if (mapPlan.isApproved ()) {
				reviewConditionsButton.setEnabled (true);
				reviewConditionsButton.setToolTipText ("The Plan was approved, can review conditions");
			} else {
				reviewConditionsButton.setEnabled (false);
				reviewConditionsButton.setToolTipText ("The Plan has not been approved, the conditions cannot be reviewed");		
			}
		} else {
			reviewConditionsButton.setEnabled (false);
			reviewConditionsButton.setToolTipText ("The Plan Tile Set filled yet.");
		}
	}

	public void updateApplyPlanButton () {
		String tFailsReasons;
		
		tFailsReasons = GUI.EMPTY_STRING;
		if (planTileSet != TileSet.NO_TILE_SET) {
			if (mapPlan.isApproved ()) {
				if (mapPlan.allConditionsMet ()) {
					applyPlanButton.setEnabled (true);
					applyPlanButton.setToolTipText ("The Plan was approved, can apply");
				} else {
					tFailsReasons = mapPlan.getFailsReasons ();
					applyPlanButton.setEnabled (false);
					applyPlanButton.setToolTipText (tFailsReasons);
				}
			} else {
				applyPlanButton.setEnabled (false);
				applyPlanButton.setToolTipText ("The Plan has not been approved, the plan cannot be applied");		
			}
		} else {
			applyPlanButton.setEnabled (false);
			applyPlanButton.setToolTipText ("The Plan Tile Set filled yet.");
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
			} else if (APPROVE_PLAN.equals (tTheAction)) {
				approvePlan ();
			} else if (DISCARD_PLAN.equals (tTheAction)) {
				discardPlan ();
			} else if (REVIEW_CONDITIONS.equals (tTheAction)) {
				reviewConditions ();
			} else if (APPLY_PLAN.equals (tTheAction)) {
				applyPlan ();
			}
			updateFrame ();
		}
	}

	private void reviewConditions () {
		String tConditionReport;
		
		System.out.println ("Ready to Review Conditions");
		tConditionReport = mapPlan.getConditionReport ();
		JOptionPane.showMessageDialog (NO_JSCROLL_BAR, tConditionReport, 
					mapPlan.getName () + " Plan Conditions", JOptionPane.PLAIN_MESSAGE);
	}
	
	private void discardPlan () {
		System.out.println ("Ready to Discard Plan");
		// Delete Plan from List of Plans
		// Close Map Plan Frame
	}

	public void captureConditions (GameManager aGameManager) {
		Condition tCondition;
		Corporation tCorporation;
		PlaceMapTilePlan tPlaceMapTilePlan;
		TileSet tLiveTileSet;
		MapFrame tLiveMapFrame;
		MapCell tMapCell;
		GameTile tNewGameTile;
		GameTile tCurrentGameTile;
		RoundManager tRoundManager;
		int tNewTileNumber;
		int tTileNumber;
		int tTileOrient;

		tCorporation = mapPlan.getCorporation ();
		tCondition = new CorporationExists (tCorporation);
		mapPlan.addCondition (tCondition);
		
		tCondition = new CorporationCanLayTile (tCorporation);
		mapPlan.addCondition (tCondition);

		tRoundManager = aGameManager.getRoundManager ();
		tCondition = new RoundIs (OperatingRound.NAME, tRoundManager);
		mapPlan.addCondition (tCondition);

		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;

			tCondition = new EnoughCash (tPlaceMapTilePlan.getBuildCost (), tCorporation);
			tPlaceMapTilePlan.addCondition (tCondition);
			
			tMapCell = tPlaceMapTilePlan.getMapCell ();
			tNewGameTile = tPlaceMapTilePlan.getSelectedGameTile ();
			if (tMapCell.isTileOnCell ()) {
				tTileNumber = tMapCell.getTileNumber ();
				tTileOrient = tMapCell.getTileOrient ();
				tCondition = new SpecifiedTileOnMapCell (tMapCell, tTileNumber, tTileOrient);
				tPlaceMapTilePlan.addCondition (tCondition);
			
				tLiveMapFrame = aGameManager.getMapFrame ();
				tCurrentGameTile = tPlaceMapTilePlan.getPreviousGameTile ();
				if (tCurrentGameTile != GameTile.NO_GAME_TILE) {
					tCondition = new TileAllowedInPhase (tNewGameTile, tCurrentGameTile, tLiveMapFrame);
					tPlaceMapTilePlan.addCondition (tCondition);
				}
			} else {
				tCondition = new NoTileOnMapCell (tMapCell);
				tPlaceMapTilePlan.addCondition (tCondition);
			}
			
			tNewTileNumber = tNewGameTile.getTileNumber ();
			tLiveTileSet = aGameManager.getTileSet ();
			tCondition = new TileAvailableInTileSet (tNewTileNumber, tLiveTileSet);
			tPlaceMapTilePlan.addCondition (tCondition);

			// Condition - MapCell has No Private Company Restriction --- CREATE Later Maybe
		}
	}
	
	private void approvePlan () {
		PlaceMapTilePlan tPlaceMapTilePlan;
		String tConditionReport;
		String tCompanyAbbrev;
		Corporation tCorporation;
		GameManager tGameManager;
		
		System.out.println ("Ready to Approve Plan");
		tGameManager = getGameManager ();
		if (mapPlan instanceof PlaceMapTilePlan) {
			tPlaceMapTilePlan = (PlaceMapTilePlan) mapPlan;
			tPlaceMapTilePlan.lockTileOrientation ();
			tCorporation = mapPlan.getCorporation ();
			if (tCorporation == Corporation.NO_CORPORATION) {
				tCompanyAbbrev = ((String) companyList.getSelectedItem ()).substring (6);

				tCorporation = tGameManager.getCorporationByAbbrev (tCompanyAbbrev);
				tPlaceMapTilePlan.setCorporation (tCorporation);
				updateCorporationInfo ();
			}
		}

		// Add Plan to List of Plans
		captureConditions (tGameManager);
		tConditionReport = mapPlan.getConditionReport ();
		System.out.println (tConditionReport);
		
		mapPlan.setApproved (Plan.APPROVED);		
	}

	public void applyPlan () {
		GameManager tGameManager;
		RoundManager tRoundManager;
		ActionManager tActionManager;
		Action tActionToApply;
		
		tGameManager = getGameManager ();
		tRoundManager = tGameManager.getRoundManager ();
		tActionManager = tRoundManager.getActionManager ();

		mapPlan.createActions (tGameManager);
		
		tActionToApply = mapPlan.getAction ();
		if (tActionToApply == Action.NO_ACTION) {
			System.out.println ("No Action to apply");
		} else {
			tActionManager.addAction (tActionToApply);
			
			tActionManager.applyAction (tActionToApply);
			tGameManager.autoSaveGame (true);

		}
	}

	@Override
	public GameManager getGameManager () {
		return (GameManager) gameEngineManager;
	}
}
