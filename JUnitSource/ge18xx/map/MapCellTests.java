package ge18xx.map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MapCellTests {
	MapCell mapCell;
	MapTestFactory mapTestFactory;
	
	@BeforeEach
	void setUp () throws Exception {
		mapTestFactory = new MapTestFactory ();
		mapCell = mapTestFactory.buildMapCell ();
	}

	@Test
	@DisplayName ("Test Old Sides Connected with New Sides Test")
	void oldVSnewSidesTests () {
		String tOldSides;
		String tNewSides;
		
		tOldSides = "|0|4";
		tNewSides = "|0|1|4|5";
		
		assertTrue (mapCell.allOldSidesConnected (tOldSides, tNewSides));
		
		tNewSides = "|0|1|5";
		assertFalse (mapCell.allOldSidesConnected (tOldSides, tNewSides));
	}

}
