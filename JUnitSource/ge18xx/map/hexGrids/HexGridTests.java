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

	private void testDistance (int aEvenOdd, int aColIndex1, int aRowIndex1, int aColIndex2, int aRowIndex2, int aExpectedDistance) {
		Hex tHex1;
		Hex tHex2;
		OffsetCoord tOffsetCoord1;
		OffsetCoord tOffsetCoord2;
		int tDistance;
		
		tOffsetCoord1 = new OffsetCoord (aColIndex1, aRowIndex1);
		tHex1 = OffsetCoord.roffsetToCube (aEvenOdd, tOffsetCoord1);
		tOffsetCoord2 = new OffsetCoord (aColIndex2, aRowIndex2);
		tHex2 = OffsetCoord.roffsetToCube (aEvenOdd, tOffsetCoord2);
		
		tDistance = tHex1.distance (tHex2);
		assertEquals (aExpectedDistance, tDistance);
//		printInfo (tHex1, tHex2);
	}

//	private void printInfo (Hex aHex1, Hex aHex2) {
//		OffsetCoord tOffsetCoord1;
//		OffsetCoord tOffsetCoord2;
//		
//		tOffsetCoord1 = OffsetCoord.roffsetFromCube (evenOdd, aHex1);
//		tOffsetCoord2 = OffsetCoord.roffsetFromCube (evenOdd, aHex2);
//	
//		System.out.println ("Hex 1: " + tOffsetCoord1.getCoordinates () + 
//				" | Hex 2: " + tOffsetCoord2.getCoordinates () + 
//				" Distance: " + aHex1.distance (aHex2));
//	}
}
