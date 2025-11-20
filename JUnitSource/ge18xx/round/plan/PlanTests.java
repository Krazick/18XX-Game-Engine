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
		
		tPlan = new Plan ("Test Plan Alpha");
		
		assertEquals ("Test Plan Alpha", tPlan.getName ());
	}
	
	@Test 
	@DisplayName ("Basic Tests of Corporation Plan")
	void corporationPlanTests () {
		CorporationPlan tCorporationPlanAlpha;
		CorporationPlan tCorporationPlanBeta;
		Corporation tCorporation;
		
		tCorporationPlanAlpha = new CorporationPlan ("Test Corporation Plan Alpha");
		assertEquals ("Test Corporation Plan Alpha", tCorporationPlanAlpha.getName ());
		assertNull (tCorporationPlanAlpha.getCorporation ());
		
		tCorporation = companyTestFactory.buildAShareCompany (1);
		tCorporationPlanBeta = new CorporationPlan ("Test Corporation Plan Beta", tCorporation);
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
		
		tMapPlanAlpha = new MapPlan ("Test Map Plan Alpha");
		assertEquals ("Test Map Plan Alpha", tMapPlanAlpha.getName ());
		assertNull (tMapPlanAlpha.getCorporation ());
		
		tCorporationBeta = companyTestFactory.buildAShareCompany (2);
		tMapPlanBeta = new MapPlan ("Test Map Plan Beta", tCorporationBeta);
		assertEquals ("Test Map Plan Beta", tMapPlanBeta.getName ());
		assertEquals (tCorporationBeta, tMapPlanBeta.getCorporation ());
		
		tCorporationGamma = companyTestFactory.buildAShareCompany (3);
		tMapCellGamma = mapTestFactory.buildMapCell ();
		tMapPlanGamma = new MapPlan ("Test Map Plan Gamma", tCorporationGamma, tMapCellGamma);
		
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
		
		tPlaceMapTilePlanAlpha = new PlaceMapTilePlan ("Test PlaceMapTile Plan Alpha");
		assertEquals ("Test PlaceMapTile Plan Alpha", tPlaceMapTilePlanAlpha.getName ());
		assertNull (tPlaceMapTilePlanAlpha.getCorporation ());
		
		tCorporationBeta = companyTestFactory.buildAShareCompany (2);
		tPlaceMapTilePlanBeta = new PlaceMapTilePlan ("Test PlaceMapTile Plan Beta", tCorporationBeta);
		assertEquals ("Test PlaceMapTile Plan Beta", tPlaceMapTilePlanBeta.getName ());
		assertEquals (tCorporationBeta, tPlaceMapTilePlanBeta.getCorporation ());
		
		tCorporationGamma = companyTestFactory.buildAShareCompany (3);
		tMapCellGamma = mapTestFactory.buildMapCell ();
		tPlaceMapTilePlanGamma = new PlaceMapTilePlan ("Test PlaceMapTile Plan Gamma", 
					tCorporationGamma, tMapCellGamma);
		
		assertEquals ("Test PlaceMapTile Plan Gamma", tPlaceMapTilePlanGamma.getName ());
		assertEquals (tCorporationGamma, tPlaceMapTilePlanGamma.getCorporation ());
		assertEquals (tMapCellGamma, tPlaceMapTilePlanGamma.getMapCell ());

		tCorporationDelta = companyTestFactory.buildAShareCompany (4);
		tMapCellDelta = mapTestFactory.buildMapCell ("D5");
		tTileDelta = tilesTestFactory.buildTile (0);
		tTileEpsilon = tilesTestFactory.buildTile (1);
		tPlaceMapTilePlanDelta = new PlaceMapTilePlan ("Test PlaceMapTile Plan Delta", 
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
