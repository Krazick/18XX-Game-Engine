package ge18xx.round.plan;

import java.util.LinkedList;
import java.util.List;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.Round;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.LayTileAction;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;

public class PlaceMapTilePlan extends MapPlan {
	Tile tile;
	int tileOrient;
	int buildCost;
	boolean tileOrientLocked;
	List<GameTile> gameTiles = new LinkedList<GameTile> ();
	private int previousOrientation;
	private String previousTokens;
	private String previousBases;
	private Tile previousTile;
	GameTile previousGameTile;
	GameTile selectedGameTile;
	
	public PlaceMapTilePlan (String aPlayerName, String aGameName, String aName) {
		this (aPlayerName, aGameName, aName, Corporation.NO_CORPORATION);
	}

	public PlaceMapTilePlan (String aPlayerName, String aGameName, String aName, 
			Corporation aCorporation) {
		this (aPlayerName, aGameName, aName, aCorporation, MapCell.NO_MAP_CELL);
	}

	public PlaceMapTilePlan (String aPlayerName, String aGameName, String aName, 
			Corporation aCorporation, MapCell aMapCell) {
		super (aPlayerName, aGameName, aName, aCorporation, aMapCell);
		setTileAndOrientation (Tile.NO_TILE, MapCell.NO_TILE_ORIENTATION);
	}

	public void lockTileOrientation () {
		tileOrientLocked = true;
	}
	
	public boolean isTileOrientationLocked () {
		return tileOrientLocked;
	}
	
	public void setGameTiles (List<GameTile> aGameTiles) {
		gameTiles = aGameTiles;
	}
	
	public void setSelectedGameTile (GameTile aSelectedGameTile) {
		selectedGameTile = aSelectedGameTile;
	}
	
	public GameTile getSelectedGameTile () {
		return selectedGameTile;
	}
	
	public void setPreviousGameTile (GameTile aPreviousGameTile) {
		previousGameTile = aPreviousGameTile;
	}
	
	public GameTile getPreviousGameTile () {
		return previousGameTile;
	}
	
	public void setTileAndOrientation (Tile aTile, int aTileOrient) {
		setTile (aTile);
		setTileOrient (aTileOrient);
		tileOrientLocked = false;
	}
	
	public void setTile (Tile aTile) {
		tile = aTile;
	}

	public Tile getTile () {
		return tile;
	}

	public void setTileOrient (int aTileOrient) {
		tileOrient = aTileOrient;
	}

	public int getTileOrient () {
		return tileOrient;
	}

	public void setBuildCost (int aBuildCost) {
		buildCost = aBuildCost;
	}

	public int getBuildCost () {
		return buildCost;
	}

	public void emptyGameTiles () {
		gameTiles.removeAll (gameTiles);
	}
	
	// Collect the set of Playable Tiles, without regards to the Current Phase
	public void setPlayableTiles () {
		
		planningMap.setPlayableTiles (mapCell, false);
		gameTiles = planningMap.getPlayableGameTiles ();
		planningMap.clearPlayableTiles ();
	}
	
	public int playableTilesCount () {
		return gameTiles.size ();
	}
	
	public GameTile getPlayableTileAt (int aIndex) {
		GameTile tGameTile;
			
		tGameTile = gameTiles.get (aIndex);
		
		return tGameTile;
	}
	
	public void putTileDownOnMap () {
		boolean tTilePlaced;
		PlanTileSet tPlanTileSet;
		GameTile tSelectedTile;
		GameTile tPreviousGameTile;
		Tile tNewTile;
		int tTileOrient;
		
		previousTile = planningMapCell.getTile ();
		if (previousTile != Tile.NO_TILE) {
			previousOrientation = planningMapCell.getTileOrient ();
			previousTokens = previousTile.getPlacedTokens ();
			previousBases = previousTile.getCorporationBases ();
		} else {
			previousOrientation = MapCell.NO_TILE_ORIENTATION;
			previousTokens = Tile.NO_TOKENS;
			previousBases = Tile.NO_BASES;
		}
		
		tPlanTileSet = planFrame.getPlanTileSet ();
		tSelectedTile = tPlanTileSet.getSelectedTile ();
		
		setSelectedGameTile (tSelectedTile);
		tPlanTileSet = planFrame.getPlanTileSet ();
		tPreviousGameTile = getPreviousGameTile (tPlanTileSet);
		setPreviousGameTile (tPreviousGameTile);
		
		tTilePlaced = planningMapCell.putThisTileDown (tPlanTileSet, tSelectedTile, MapCell.NO_ROTATION);
		tNewTile = planningMapCell.getTile ();
		setTile (tNewTile);
		tTileOrient = planningMapCell.getTileOrient ();
		setTileOrient (tTileOrient);
		planFrame.setTilePlaced (tTilePlaced);
		setPlanStatus (PlanStatus.TILE_PLACED);
//		tPlanTileSet.clearAllSelected ();
		planFrame.updateFrame ();
	}
	
	public void pickupTile () {
		PlanTileSet tPlanTileSet;
		GameTile tPreviousGameTile;
		GameManager tGameManager;
		Tile tFoundTile;
		
		tPlanTileSet = planFrame.getPlanTileSet ();
		tFoundTile = planningMapCell.removeTile ();

		if (previousTile != Tile.NO_TILE) {
			tPreviousGameTile = getPreviousGameTile (tPlanTileSet);
			planningMapCell.putThisTileDown (tPlanTileSet, tPreviousGameTile, previousOrientation);
			
		}
		if (tFoundTile != Tile.NO_TILE) {
			planningMapCell.restoreTile (tPlanTileSet, tile);
		}
		tGameManager = planFrame.getGameManager ();
		planningMapCell.applyTokens (previousTokens, tGameManager);
		planningMapCell.applyBases (previousBases, tGameManager);
		planFrame.setTilePlaced (false);
		setPlanStatus (PlanStatus.UNAPPROVED);
		tPlanTileSet.clearAllSelected ();
		planFrame.updateFrame ();
	}

	protected GameTile getPreviousGameTile (PlanTileSet aPlanTileSet) {
		int tPreviousTileNumber;
		GameTile tPreviousGameTile;
		
		if (previousTile == Tile.NO_TILE) {
			tPreviousGameTile = GameTile.NO_GAME_TILE;
		} else {
			tPreviousTileNumber = previousTile.getNumber ();
			tPreviousGameTile = aPlanTileSet.getGameTile (tPreviousTileNumber);
		}
		
		return tPreviousGameTile;
	}
	
	public void rotateTile () {
		int tPossible;
		int tTileOrient;
		
		tPossible = planningMapCell.getTileOrient ();
		planningMap.rotateTileInPlace (planningMapCell, tPossible, false, tile);
		tTileOrient = planningMapCell.getTileOrient ();
		setTileOrient (tTileOrient);

		planFrame.updateFrame ();
	}
	
	@Override
	public void createActions (GameManager aGameManager) {
		LayTileAction tLayTileAction;
		RoundManager tRoundManager;
		TrainCompany tTrainCompany;
		Bank tBank;
		Round tCurrentRound;
		String tNewTokens;
		String tRoundID;
		ActorI.ActionStates tCurrentStatus;
		ActorI.ActionStates tNewStatus;

		tRoundManager = aGameManager.getRoundManager ();
		tCurrentRound = tRoundManager.getCurrentRound ();
		tRoundID = tCurrentRound.getID ();
		tNewTokens = tile.getPlacedTokens ();
		tLayTileAction = new LayTileAction (ActorI.ActionStates.OperatingRound, tRoundID, corporation);
			
		tLayTileAction.addLayTileEffect (corporation, mapCell, tile, tileOrient, previousTokens, previousBases, 
				tNewTokens);
		if (corporation instanceof TrainCompany) {
			tTrainCompany = (TrainCompany) corporation;
			tTrainCompany.addRemoveHomeEffect (tLayTileAction, mapCell);
			
			if (previousTile != Tile.NO_TILE) {
				tTrainCompany.createAndAddRemoveTileAction (mapCell, previousTile, previousOrientation, 
						previousTokens, previousBases, tRoundID);
				tLayTileAction.setChainToPrevious (true);
			}
			if (buildCost > 0) {
				tBank = aGameManager.getBank ();
				tLayTileAction.addCashTransferEffect (tTrainCompany, tBank, buildCost);
			}
		}
		tCurrentStatus = corporation.getStatus ();
		tNewStatus = corporation.getNewStatusWithTile (previousTile);
		tLayTileAction.addChangeCorporationStatusEffect (corporation, tCurrentStatus, tNewStatus);
		tLayTileAction.addSetHasLaidTileEffect (corporation, true);
		setAction (tLayTileAction);
		planActions.add (tLayTileAction);
	}
}
