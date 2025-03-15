package ge18xx.map.hexGrids;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HexGridTests {
	int evenOdd = OffsetCoord.ODD;

	@BeforeEach
	void setUp () throws Exception {
	}

	@Test
	@DisplayName ("HexGrid Map Tests")
	void hexGridMapTests () {
		testDistance (evenOdd, 7, 1, 6, 2, 2);
		testDistance (evenOdd, 7, 1, 6, 1, 1);
		testDistance (evenOdd, 7, 1, 7, 4, 3);
		testDistance (evenOdd, 7, 1, 4, 6, 6);
		testDistance (evenOdd, 7, 1, 9, 11, 10);
		testDistance (evenOdd, 7, 1, 5, 15, 14);
		testDistance (evenOdd, 6, 10, 7, 1, 9);
	}

	private void testDistance (int aEvenOdd, int aColIndex1, int aRowIndex1, int aColIndex2, int aRowIndex2, 
								int aExpectedDistance) {
		Hex tHex1;
		Hex tHex2;
		OffsetCoord tOffsetCoord1;
		OffsetCoord tOffsetCoord2;
		int tDistance1;
		int tDistance2;
		
		tOffsetCoord1 = new OffsetCoord (aColIndex1, aRowIndex1);
		tHex1 = OffsetCoord.roffsetToCube (aEvenOdd, tOffsetCoord1);
		tOffsetCoord2 = new OffsetCoord (aColIndex2, aRowIndex2);
		tHex2 = OffsetCoord.roffsetToCube (aEvenOdd, tOffsetCoord2);
		
		tDistance1 = tHex1.distance (tHex2);
		assertEquals (aExpectedDistance, tDistance1);
		
		tDistance2 = tOffsetCoord1.getDistanceTo (aEvenOdd, tOffsetCoord2);
		assertEquals (aExpectedDistance, tDistance2);
	}
}
