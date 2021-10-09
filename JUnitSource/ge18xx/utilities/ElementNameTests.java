/**
 * 
 */
package ge18xx.utilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marksmith
 *
 */
@DisplayName ("Element Name Test")
class ElementNameTests {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Test Constructor")
	public void testContructor () {
		ElementName elementName1 = new ElementName ("Alpha");
		ElementName elementName2 = new ElementName ("alpha");
		ElementName elementName3 = new ElementName ("Alpha Beta");

		assertTrue (elementName1.hasValue (), "Element Name of [Alpha] with Upper Case first letter is valid");
		assertFalse (elementName2.hasValue (), "Element Name of [alpha] with Lower Case first letter is invalid");
		assertFalse (elementName3.hasValue (), "Element Name of [Alpha Beta] with a space is Invalid");
	}

}
