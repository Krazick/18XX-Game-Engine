package ge18xx.map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName ("Hex Map Tests")
class HexMapTests {
	MapTestFactory mapTestFactory;
	HexMap hexMap;
	
	@BeforeEach
	void setUp () throws Exception {
		mapTestFactory = new MapTestFactory ();
		hexMap = mapTestFactory.buildHexMap ();
	}

	@Test
	@DisplayName ("Test initial flags")
	void HexMapInitialFlagsTests () {
		assertTrue (hexMap.isSMCEmpty ());
		assertFalse (hexMap.wasTilePlaced ());
	}

}
