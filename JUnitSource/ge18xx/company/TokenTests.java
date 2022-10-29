package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author marksmith
 *
 */
@DisplayName ("Testing the Token Class with Mocking Token Company")
class TokenTests {
	TokenCompany mCompany;
	TokenCompany mCompany2;
	String mockAbbrev, mockStatus;
	Token token1;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception {
		int tMockCoID;

		tMockCoID = 5001;
		mCompany = Mockito.mock (TokenCompany.class);
		Mockito.when (mCompany.getID ()).thenReturn (tMockCoID);

		tMockCoID = 5002;
		mCompany2 = Mockito.mock (TokenCompany.class);
		Mockito.when (mCompany2.getID ()).thenReturn (tMockCoID);

		token1 = new Token (mCompany);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown () throws Exception {
	}

	@Nested
	@DisplayName ("Test Token Creation")
	class TestTokenCreation {
		@Test
		@DisplayName ("With No Argument")
		public void testToken () {
			Token tToken;

			tToken = new Token ();
			assertEquals (TokenCompany.NO_TOKEN_COMPANY, tToken.getWhichCompany ());
		}

		@Test
		@DisplayName ("With a Mocked Company")
		public void testGetCorporationIDFromToken () {
			assertEquals (5001, token1.getCorporationID ());
			assertEquals (mCompany, token1.getWhichCompany ());
		}

		@Test
		@DisplayName ("With a Token and Mocked Company")
		public void testToken2 () {
			Token tToken2;

			mockAbbrev = "MCA";
			mockStatus = "Operated";
			Mockito.when (mCompany.getAbbrev ()).thenReturn (mockAbbrev);
			Mockito.when (mCompany.getStatusName ()).thenReturn (mockStatus);

			tToken2 = new Token (token1);
			assertEquals ("MCA", tToken2.getCorporationAbbrev ());
			assertEquals (5001, tToken2.getCorporationID ());
			assertEquals ("Operated", tToken2.getCorporationStatus ());
		}
	}

	@Nested
	@DisplayName ("Test Comparing Tokens")
	class TestComparingTokens {
		@Test
		@DisplayName ("Based on (Not Same) Company")
		public void testToken3 () {
			String tMockAbbrev2, tMockStatus2;
			Token tToken2;

			mockAbbrev = "MCA";
			mockStatus = "Operated";
			Mockito.when (mCompany.getAbbrev ()).thenReturn (mockAbbrev);
			Mockito.when (mCompany.getStatusName ()).thenReturn (mockStatus);
			tMockAbbrev2 = "MCA2";
			tMockStatus2 = "TileLaid";
			Mockito.when (mCompany2.getAbbrev ()).thenReturn (tMockAbbrev2);
			Mockito.when (mCompany2.getStatusName ()).thenReturn (tMockStatus2);

			tToken2 = new Token (mCompany2);

			assertFalse (token1.isSameCompany (tToken2));
			assertFalse (token1.isCorporationAbbrev (tMockAbbrev2));
		}

		@Test
		@DisplayName ("Based on (Same) Company")
		public void testToken4 () {
			String tMockAbbrev;
			Token tToken2;

			tMockAbbrev = "MCA";
			Mockito.when (mCompany.getAbbrev ()).thenReturn (tMockAbbrev);

			tToken2 = new Token (mCompany);

			assertTrue (token1.isSameCompany (tToken2));
			assertTrue (token1.isCorporationAbbrev (tMockAbbrev));
		}
	}
}
