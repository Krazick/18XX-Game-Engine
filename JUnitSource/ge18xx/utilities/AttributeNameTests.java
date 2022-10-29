package ge18xx.utilities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author marksmith
 *
 */
@DisplayName ("Attribute Name Test")
class AttributeNameTests {
	AttributeName attributeName1;
	AttributeName attributeName2;
	AttributeName attributeName3;

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
		attributeName1 = new AttributeName ("alpha");
		attributeName2 = new AttributeName ("Alpha");
		attributeName3 = new AttributeName ("alpha Beta");

		assertTrue (attributeName1.hasValue (), "Attribute Name of [alpha] with Lower Case first letter is valid");
		assertFalse (attributeName2.hasValue (), "Attribute Name of [Alpha] with Upper Case first letter is invalid");
		assertFalse (attributeName3.hasValue (), "Attribute Name of [alpha Beta] with a space is Invalid");
	}
}
