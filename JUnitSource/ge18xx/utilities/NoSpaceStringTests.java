package ge18xx.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author marksmith
 *
 */
@DisplayName ("No Space String Test")
class NoSpaceStringTests {
	NoSpaceString noSpaceString1;
	NoSpaceString noSpaceString2;
	NoSpaceString noSpaceString3;
	NoSpaceString noSpaceString4;
	NoSpaceString noSpaceString5;
	NoSpaceString noSpaceString6;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	public void setup () {
		noSpaceString1 = new NoSpaceString ();
		noSpaceString2 = new NoSpaceString ("AlphaBeta");
		noSpaceString3 = new NoSpaceString (null);
		noSpaceString4 = new NoSpaceString ("Alpha");
		noSpaceString5 = new NoSpaceString ("");
		noSpaceString6 = new NoSpaceString ("Test with Space");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Test No Space String Constructors")
	public void testNoSpaceConstructors () {
		assertFalse (noSpaceString1.hasValue ());
		assertTrue (noSpaceString2.hasValue ());
		assertFalse (noSpaceString3.hasValue ());
		assertTrue (noSpaceString4.hasValue ());
		assertFalse (noSpaceString5.hasValue ());
		assertFalse (noSpaceString6.hasValue ());
	}

	@Test
	@DisplayName ("Test No Space Get Methods")
	public void testNoSpaceGetMethods () {
		assertNotNull (noSpaceString2.toString ());
		assertNotNull (noSpaceString2.toString ());
		assertNull (noSpaceString1.getString ());
		assertNull (noSpaceString1.toString ());

		assertEquals ("AlphaBeta", noSpaceString2.getString ());
		assertEquals ("AlphaBeta", noSpaceString2.toString ());
		assertNotEquals ("NotThisText", noSpaceString2.getString ());
		assertNotEquals ("NotThisText", noSpaceString2.toString ());

	}

	@Test
	@DisplayName ("Test No Space Equals Methods")
	public void testNoSpaceEquals () {
		assertFalse (noSpaceString1.equals ("AlphaBeta"));
		assertFalse (noSpaceString2.equals ("Alpha"));
		assertTrue (noSpaceString2.equals ("AlphaBeta"));
		assertFalse (noSpaceString2.equals (null));
	}

}
