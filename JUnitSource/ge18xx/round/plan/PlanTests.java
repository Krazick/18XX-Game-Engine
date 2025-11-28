package ge18xx.round.plan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ge18xx.company.Corporation;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;

@DisplayName ("Plan Constructor Tests")
@TestInstance (Lifecycle.PER_CLASS)
class PlanTests extends PlanTester {

	@Test
	@DisplayName ("Basic Tests of Plan")
	void planTests () {
		Plan tPlan;
		String tGameName;
		String tPlanName;
		String tPlayerName;
		
		tGameName = "1830";
		tPlanName = "Test Plan Alpha";
		tPlayerName = "Buster";
		tPlan = new Plan (tPlayerName, tGameName, tPlanName);
		
		assertEquals ("Test Plan Alpha", tPlan.getName ());
	}
	
	@Test 
	@DisplayName ("Basic Tests of Corporation Plan")
	void corporationPlanTests () {
		CorporationPlan tCorporationPlanAlpha;
		CorporationPlan tCorporationPlanBeta;
		Corporation tCorporation;
		String tGameName;
		String tPlanName;
		String tPlayerName;
		
		tGameName = "1830";
		tPlayerName = "Buster";
		tPlanName = "Test Corporation Plan Alpha";
		
		tCorporationPlanAlpha = new CorporationPlan (tPlayerName, tGameName, tPlanName);
		assertEquals ("Test Corporation Plan Alpha", tCorporationPlanAlpha.getName ());
		assertNull (tCorporationPlanAlpha.getCorporation ());
		
		tPlanName = "Test Corporation Plan Beta";
		tCorporation = companyTestFactory.buildAShareCompany (1);
		tCorporationPlanBeta = new CorporationPlan (tPlayerName, tGameName, tPlanName, tCorporation);
		assertEquals ("Test Corporation Plan Beta", tCorporationPlanBeta.getName ());
		assertEquals (tCorporation, tCorporationPlanBeta.getCorporation ());
	}
	
	@Test 
	@DisplayName ("Basic Tests of Map Plan")
	void MapPlanTests () {
		MapPlan tMapPlanAlpha;
		MapPlan tMapPlanBeta;
		MapPlan tMapPlanGamma;
		Corporation tCorporationBeta;
		Corporation tCorporationGamma;
		MapCell tMapCellGamma;
		String tGameName;
		String tPlanName;
		String tPlayerName;
		
		tGameName = "1830";
		tPlanName = "Test Map Plan Alpha";
		tPlayerName = "Buster";
		
		tMapPlanAlpha = new MapPlan (tPlayerName, tGameName, tPlanName);
		assertEquals ("Test Map Plan Alpha", tMapPlanAlpha.getName ());
		assertNull (tMapPlanAlpha.getCorporation ());
		
		tPlanName = "Test Map Plan Beta";
		tCorporationBeta = companyTestFactory.buildAShareCompany (2);
		tMapPlanBeta = new MapPlan (tPlayerName, tGameName, tPlanName, tCorporationBeta);
		assertEquals ("Test Map Plan Beta", tMapPlanBeta.getName ());
		assertEquals (tCorporationBeta, tMapPlanBeta.getCorporation ());
		
		tPlanName = "Test Map Plan Gamma";
		tCorporationGamma = companyTestFactory.buildAShareCompany (3);
		tMapCellGamma = mapTestFactory.buildMapCell ();
		tMapPlanGamma = new MapPlan (tPlayerName, tGameName, tPlanName, tCorporationGamma, 
								tMapCellGamma);
		
		assertEquals ("Test Map Plan Gamma", tMapPlanGamma.getName ());
		assertEquals (tCorporationGamma, tMapPlanGamma.getCorporation ());
		assertEquals (tMapCellGamma, tMapPlanGamma.getMapCell ());
	}
	
	@Test 
	@DisplayName ("Basic Tests of PlaceMapTile Plan")
	void PlaceMapTilePlanTests () {
		PlaceMapTilePlan tPlaceMapTilePlanAlpha;
		PlaceMapTilePlan tPlaceMapTilePlanBeta;
		PlaceMapTilePlan tPlaceMapTilePlanGamma;
		PlaceMapTilePlan tPlaceMapTilePlanDelta;
		Corporation tCorporationBeta;
		Corporation tCorporationGamma;
		Corporation tCorporationDelta;
		MapCell tMapCellGamma;
		MapCell tMapCellDelta;
		Tile tTileDelta;
		Tile tTileEpsilon;
		String tGameName;
		String tPlanName;
		String tPlayerName;
		
		tGameName = "1830";
		tPlanName = "Test PlaceMapTile Plan Alpha";
		tPlayerName = "Buster";

		tPlaceMapTilePlanAlpha = new PlaceMapTilePlan (tPlayerName, tGameName, tPlanName);
		assertEquals ("Test PlaceMapTile Plan Alpha", tPlaceMapTilePlanAlpha.getName ());
		assertNull (tPlaceMapTilePlanAlpha.getCorporation ());
		
		tPlanName = "Test PlaceMapTile Plan Beta";
		tCorporationBeta = companyTestFactory.buildAShareCompany (2);
		tPlaceMapTilePlanBeta = new PlaceMapTilePlan (tPlayerName, tGameName, tPlanName, tCorporationBeta);
		assertEquals ("Test PlaceMapTile Plan Beta", tPlaceMapTilePlanBeta.getName ());
		assertEquals (tCorporationBeta, tPlaceMapTilePlanBeta.getCorporation ());
		
		tPlanName = "Test PlaceMapTile Plan Gamma";
		tCorporationGamma = companyTestFactory.buildAShareCompany (3);
		tMapCellGamma = mapTestFactory.buildMapCell ();
		tPlaceMapTilePlanGamma = new PlaceMapTilePlan (tPlayerName, tGameName, tPlanName, 
					tCorporationGamma, tMapCellGamma);
		
		assertEquals ("Test PlaceMapTile Plan Gamma", tPlaceMapTilePlanGamma.getName ());
		assertEquals (tCorporationGamma, tPlaceMapTilePlanGamma.getCorporation ());
		assertEquals (tMapCellGamma, tPlaceMapTilePlanGamma.getMapCell ());

		tPlanName = "Test PlaceMapTile Plan Delta";
		tCorporationDelta = companyTestFactory.buildAShareCompany (4);
		tMapCellDelta = mapTestFactory.buildMapCell ("D5");
		tTileDelta = tilesTestFactory.buildTile (0);
		tTileEpsilon = tilesTestFactory.buildTile (1);
		tPlaceMapTilePlanDelta = new PlaceMapTilePlan (tPlayerName, tGameName, tPlanName, 
					tCorporationDelta, tMapCellDelta);
		tPlaceMapTilePlanDelta.setTileAndOrientation (tTileDelta, 0);
		
		assertEquals ("Test PlaceMapTile Plan Delta", tPlaceMapTilePlanDelta.getName ());
		assertEquals (tCorporationDelta, tPlaceMapTilePlanDelta.getCorporation ());
		assertEquals (tMapCellDelta, tPlaceMapTilePlanDelta.getMapCell ());
		assertEquals (tTileDelta, tPlaceMapTilePlanDelta.getTile ());
		assertEquals (0, tPlaceMapTilePlanDelta.getTileOrient ());
		assertNotEquals (tTileEpsilon, tPlaceMapTilePlanDelta.getTile ());
	}
}
