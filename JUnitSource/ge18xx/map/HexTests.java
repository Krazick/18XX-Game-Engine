package ge18xx.map;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName ("Hex Tests")
class HexTests {
	Hex hex10f;
	Hex hex10t;

	@BeforeEach
	void setUp () throws Exception {
		hex10t = new Hex (10, 10, true, 10);
		hex10f = new Hex (10, 10, false, 10);
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Test default static values")
	public void TestDefaultStaticValues () {
		Hex tHex;
		
		tHex = new Hex ();
		assertFalse (Hex.getDirection ());
		assertEquals (8, Hex.getScale (), "Default Scale should be 8");
		assertEquals (40, Hex.getWidth (), "Default Width should be 40:");
		assertEquals (20, tHex.getXd ());
	}

	@Test
	@DisplayName ("Test Constructor with 'false', same as no Args")
	public void TestConstructorFalseArg () {
		Hex tHex;
		
		tHex = new Hex (false);
		assertFalse (Hex.getDirection ());
		assertEquals (8, Hex.getScale (), "Default Scale should be 8");
		assertEquals (40, Hex.getWidth (), "Default Width should be 40:");
		assertEquals (20, tHex.getXd ());
	}
	
	@Test
	@DisplayName ("Test Constructor with 'true', same as no Args")
	public void TestConstructorTrueArg () {
		Hex tHex;
		
		tHex = new Hex (true);
		assertTrue (Hex.getDirection ());
		assertEquals (8, Hex.getScale (), "Default Scale should be 8");
		assertEquals (40, Hex.getWidth (), "Default Width should be 40:");
		assertEquals (20, tHex.getXd ());
	}
	
	@Test
	@DisplayName ("Test Constructor with offset 10,10, and 'false'")
	public void TestConstructorWithOffsetDirection () {
		assertFalse (Hex.getDirection ());
		assertEquals (10, Hex.getScale (), "Default Scale should be 10");
		assertEquals (50, Hex.getWidth (), "Default Width should be 50:");
		assertEquals (25, hex10t.getXd ());
	}
	
	@Test
	@DisplayName ("Test Constructor with offset 10,10, 'false', 12")
	public void TestConstructorWithOffsetDirectionAndScale () {
		assertFalse (Hex.getDirection ());
		assertEquals (10, Hex.getScale (), "Scale should be 10");
		assertEquals (50, Hex.getWidth (), "Width should be 50:");
		assertEquals (25, hex10t.getXd ());
	}
	
	@Test
	@DisplayName ("Test Constructor with offset 10,10, 'false', 12")
	public void TestConstructorChangingScale () {
		assertEquals (10, Hex.getScale (), "Scale should be 10");
		assertEquals (50, Hex.getWidth (), "Width should be 50:");
		assertEquals (25, hex10t.getXd ());
	}
	
	@Test
	@DisplayName ("Test getting XArray")
	public void TestGetXArray () {
		int [] tXArray;
		
		tXArray = hex10f.getXArray ();
		assertEquals ("[-15, 35, 60, 35, -15, -40, -15]", Arrays.toString (tXArray));
	}
	
	@Test
	@DisplayName ("Test getting YArray")
	public void TestGetYArray () {
		int [] tYArray;

		tYArray = hex10f.getYArray ();
		assertEquals ("[-33, -33, 10, 53, 53, 10, -33]", Arrays.toString (tYArray));
	}
	
	@Test
	@DisplayName ("Test getMinX, getMaxX, getMinY, getMaxY methods")
	public void testGetMinMaxValues () {
		assertEquals (-40, hex10f.getMinX ());
		assertEquals (60, hex10f.getMaxX ());
		assertEquals (-33, hex10f.getMinY ());
		assertEquals (53, hex10f.getMaxY ());
	}
	
	@Test
	@DisplayName ("Test getDisplace methods methods")
	public void testGetDisplaceMethods () {
		assertEquals (25, hex10f.getDisplaceUpDown ());
		assertEquals (43, hex10f.getDisplaceLeftRight ());
		assertEquals (-40, hex10f.leftEdgeDisplacment ());
		assertEquals (60, hex10f.rightEdgeDisplacement ());
		assertEquals (53, hex10f.bottomEdgeDisplacement ());
		assertEquals (-33, hex10f.topEdgeDisplacement ());
	}
	
	@Test
	@DisplayName ("Test getXt, getYt, getXd, getYd methods")
	public void testGetXYMethods () {
		assertEquals (40, hex10f.getXt ());
		assertEquals (22, hex10f.getYt ());
		assertEquals (25, hex10f.getXd ());
		assertEquals (43, hex10f.getYd ());
	}
	
	@Test
	@DisplayName ("Test getCityWidth, getTrackWidth methods")
	public void testGetWidthMethods () {
		assertEquals (14, hex10f.getCityWidth ());
		assertEquals (6, hex10f.getTrackWidth ());
		assertEquals (50, hex10f.getIntDWidth ());
	}
	
	@Test
	@DisplayName ("Test resetting with SetScale, One Arg")
	public void testResetScale () {
		int [] tYArray;
		int [] tXArray;
		
		hex10f.setScale (14);

		tXArray = hex10f.getXArray ();
		tYArray = hex10f.getYArray ();
		assertEquals ("[-35, 35, 70, 35, -35, -70, -35]", Arrays.toString (tXArray));
		assertEquals ("[-60, -60, 0, 60, 60, 0, -60]", Arrays.toString (tYArray));
	}
	
	@ParameterizedTest (name = "{index} ==> Slice {0}, Point {1}, Expected X {2}")
	@CsvFileSource (resources = "HexTestSlicePointX.csv")
	@DisplayName ("Test getHexSlicePointX method")
	public void testTetHexSlicePointX (int aSliceNumber, int aPointNum, int aExpectedX) {
		assertEquals (aExpectedX, hex10f.getHexSlicePointX (aSliceNumber, aPointNum));
	}
	
	@ParameterizedTest (name = "{index} ==> Slice {0}, Point {1}, Expected Y {2}")
	@CsvFileSource (resources = "HexTestSlicePointY.csv")
	@DisplayName ("Test getHexSlicePointY method")
	public void testTestHexSlicePointY (int aSliceNumber, int aPointNum, int aExpectedY) {
		assertEquals (aExpectedY, hex10f.getHexSlicePointY (aSliceNumber, aPointNum));
	}

	@ParameterizedTest (name = "{index} ==> Point {0}, Expected X {1}")
	@CsvFileSource (resources = "HexTestMidpointXf.csv")
	@DisplayName ("Test getMidpointX method with False Direction ")
	public void testMidpointXf (int aPoint, int aExpectedMidpointX) {
		assertEquals (aExpectedMidpointX, hex10f.midpointX (aPoint));
	}
	
	@ParameterizedTest (name = "{index} ==> Point {0}, Expected Y {1}")
	@CsvFileSource (resources = "HexTestMidpointYf.csv")
	@DisplayName ("Test getMidpointY method with False Direction ")
	public void testMidpointYf (int aPoint, int aExpectedMidpointY) {
		assertEquals (aExpectedMidpointY, hex10f.midpointY (aPoint));
	}
	
	@ParameterizedTest (name = "{index} ==> Point {0}, Expected X {1}")
	@CsvFileSource (resources = "HexTestMidpointXt.csv")
	@DisplayName ("Test getMidpointX method with True Direction ")
	public void testMidpointXt (int aPoint, int aExpectedMidpointX) {
		Hex tHex10t = new Hex (10, 10, true, 10); 
		assertEquals (aExpectedMidpointX, tHex10t.midpointX (aPoint));
	}
	
	@DisplayName ("Test getMidpointY method with True Direction ")
	@ParameterizedTest (name = "{index} ==> Point {0}, Expected Y {1}")
	@CsvFileSource (resources = "HexTestMidpointYt.csv")
	public void testMidpointYt (int aPoint, int aExpectedMidpointY) {
		Hex tHex10t = new Hex (10, 10, true, 10); 
		assertEquals (aExpectedMidpointY, tHex10t.midpointY (aPoint));
	}

}
