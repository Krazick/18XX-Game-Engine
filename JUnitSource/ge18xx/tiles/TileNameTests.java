package ge18xx.tiles;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TileNameTests {

	@BeforeEach
	void setUp () throws Exception {
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Test Tile Name Constructors")
	void test () {
		TileName tTileName1;
		TileName tTileName2;
		
		tTileName1 = new TileName ();
		assertEquals (null, tTileName1.getName (), "Constructor with no Args ");
		tTileName1 = new TileName ("San Diego");
		assertEquals ("San Diego", tTileName1.getName (), "Constructor with Name arg only");
		assertEquals (-1, tTileName1.getLocation ().getLocation (), "Constructor with Name arg only - Location");
		tTileName2 = tTileName1.clone ();
		tTileName1 = new TileName ("Seattle", 20);
		assertEquals ("San Diego", tTileName2.getName (), "Clone Tile Name 2");
		assertEquals ("Seattle", tTileName1.getName (), "Constructor 2 Args");
		
		tTileName2.printlog ();
	}

}
