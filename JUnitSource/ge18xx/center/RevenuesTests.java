package ge18xx.center;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName ("Testing Revenues Class")
class RevenuesTests {
	Revenues revenueList;
	Revenues revenueList2;

	@BeforeEach
	void setUp () throws Exception {
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Revenue Test no Arguments")
	public void CreationTestNoArguments () {
		revenueList = new Revenues ();
		assertEquals (1, revenueList.getRevenueCount (), "New Revenue List - No Args");
	}
	
	@Test
	@DisplayName ("Revenue Test Null Argument")
	public void CreationTestNullArgument () {
		revenueList = new Revenues (null);
		assertEquals (1, revenueList.getRevenueCount (), "New Revenue List - Null Args");
	}
	
	@Test
	@DisplayName ("Revenue Test Three Arguments")
	public void CreationTestThreeArguments () {
//		Arguments: Value, Location Phase
		revenueList = new Revenues (10, 20, 0);
		assertEquals (1, revenueList.getRevenueCount (), "New Revenue List - Three Args");
	}
	
	@Test
	@DisplayName ("Revenue Test Four Arguments")
	public void CreationTestFourArguments () {
//		Arguments: Value, Location, Phase, Layout Style
		revenueList = new Revenues (20, 30, 0, 2);
		assertEquals (1, revenueList.getRevenueCount (), "New Revenue List - Four Args");
	}
	
	@Test
	@DisplayName ("Revenue Test Add 2nd Revenue")
	public void TestAdding2ndRevenue () {
//		Arguments: Value, Location, Phase, Layout Style
		revenueList = new Revenues (20, 30, 0, 2);
		
//		Arguments: Value, Phase
		revenueList.addRevenue (50, 2);
		assertEquals (2, revenueList.getRevenueCount (), "Add to Revenue List - 1 Item (50, 2)");
		assertEquals (20, revenueList.getValue (), "Find Revenue Value with No Argument - 20");
		assertNotEquals (50, revenueList.getValue (), "Not Find Revenue Value with No Argument - 50");
		
		assertEquals (2, revenueList.getPhase (), "Get Phase for 2nd Revenue");
		assertEquals (20, revenueList.getValue (1), "Find Revenue Value Phase 1 is 20");
		assertNotEquals (50, revenueList.getValue (1), "Find Revenue Value Phase 1 is not 50");
		assertEquals (50, revenueList.getValue (3), "Find Revenue Value Phase 3");
	}
	
	@Test
	@DisplayName ("Revenue Test Add 3rd Revenue")
	public void TestAdding3rdRevenue () {
//		Arguments: Value, Location, Phase, Layout Style
		revenueList = new Revenues (20, 30, 0, 2);
		
//		Arguments: Value, Phase
		revenueList.addRevenue (50, 2);
		revenueList.addRevenue (70, 3);
		assertEquals (3, revenueList.getRevenueCount (), "Add to Revenue List - 1 Item (70, 3)");
	}
	
	@Test
	@DisplayName ("RevenueList Creation Test from another RevenueList")
	public void TestCreateFromRevenue () {
//		Arguments: Value, Location, Phase, Layout Style
		revenueList = new Revenues (20, 30, 0, 2);
		
//		Arguments: Value, Phase
		revenueList.addRevenue (50, 2);
		revenueList.addRevenue (70, 3);
		revenueList2 = new Revenues (revenueList);
		assertEquals (3, revenueList.getRevenueCount ());
		assertEquals (3, revenueList2.getRevenueCount ());
	}
	
	@Test
	@DisplayName ("RevenueList Creation Test from another RevenueList")
	public void TestRevenueGets () {
//		Arguments: Value, Location, Phase, Layout Style
		revenueList = new Revenues (20, 30, 0, 2);
		
//		Arguments: Value, Phase
		revenueList.addRevenue (50, 2);
		revenueList.addRevenue (70, 3);
		revenueList2 = new Revenues (revenueList);
		assertEquals ("20", revenueList2.getValueIndexToString (0), "Revenue List - value to String (First)");
		assertEquals ("50", revenueList2.getValueIndexToString (1), "Revenue List - value to String (Second)");
		assertEquals ("70", revenueList2.getValueToString (), "Revenue List - value to String (Last)");
		assertEquals ("0", revenueList2.getPhaseIndexToString (0), "Revenue List - Phase to String (First)");
		assertEquals ("2", revenueList2.getPhaseIndexToString (1), "Revenue List - Phase to String (Second)");
		assertEquals ("3", revenueList2.getPhaseToString (), "Revenue List - Phase to String (Last)");
		assertEquals (0, revenueList.getLayoutFromName ("circle"), "Revenue List - Get Layout from Name - circle");
		assertEquals (1, revenueList.getLayoutFromName ("oval"), "Revenue List - Get Layout from Name - oval");
		assertEquals (2, revenueList.getLayoutFromName ("horizontal"), "Revenue List - Get Layout from Name - horizontal");
		assertEquals (3, revenueList.getLayoutFromName ("vertical"), "Revenue List - Get Layout from Name - vertical");
		assertEquals (4, revenueList.getLayoutFromName ("split"), "Revenue List - Get Layout from Name - split");
		assertEquals (0, revenueList.getLayoutFromName (null), "Revenue List - Get Layout from Name - NULL");
	}
}
