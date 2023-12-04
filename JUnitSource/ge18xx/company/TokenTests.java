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

import geUtilities.XMLDocument;
import geUtilities.XMLElement;

/**
 * @author marksmith
 *
 */
@DisplayName ("Testing the Token Class with Mocking Token Company")
class TokenTests {
	TokenCompany mTokenCompany;
	TokenCompany mTokenCompany2;
	CompanyTestFactory companyTestFactory;
	String mockAbbrev;
	String mockStatus;
	Token token;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception {
		int tMockCoID1;
		int tMockCoID2;

		tMockCoID1 = 5001;
		companyTestFactory = new CompanyTestFactory ();
		mTokenCompany = companyTestFactory.buildTokenCompanyMock (tMockCoID1, "MC1");

		tMockCoID2 = 5002;
		mTokenCompany2 = companyTestFactory.buildTokenCompanyMock (tMockCoID2);

		token = companyTestFactory.buildToken (mTokenCompany, TokenInfo.TokenType.MAP);
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
		@DisplayName ("Using CompanyTestFactory")
		public void testCompanyTestFactoryToken () {
			Token tToken;

			tToken = companyTestFactory.buildToken ();
			assertEquals ("MC1", tToken.getCorporationAbbrev ());
			assertEquals (5001, tToken.getCorporationID ());
		}

		@Test
		@DisplayName ("That Token is NOT a Map Token")
		public void testIsAMapTokenA () {
			assertFalse (token.isAMapToken ());
		}

		@Test
		@DisplayName ("That Default Token is NOT a Map Token")
		public void testIsAMapTokenB () {
			Token tToken;

			tToken = new Token ();
			assertFalse (tToken.isAMapToken ());
		}
		
		@Test
		@DisplayName ("With a Mocked Company")
		public void testGetCorporationIDFromToken () {
			assertEquals (5001, token.getCorporationID ());
			assertEquals (mTokenCompany, token.getWhichCompany ());
		}

		@Test
		@DisplayName ("With a Token and Mocked Company")
		public void testToken2 () {
			Token tToken2;

			mockAbbrev = "MCA";
			mockStatus = "Operated";
			Mockito.when (mTokenCompany.getAbbrev ()).thenReturn (mockAbbrev);
			Mockito.when (mTokenCompany.getStatusName ()).thenReturn (mockStatus);

			tToken2 = new Token (token, TokenInfo.TokenType.FIXED_COST);
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
			Mockito.when (mTokenCompany.getAbbrev ()).thenReturn (mockAbbrev);
			Mockito.when (mTokenCompany.getStatusName ()).thenReturn (mockStatus);
			tMockAbbrev2 = "MCA2";
			tMockStatus2 = "TileLaid";
			Mockito.when (mTokenCompany2.getAbbrev ()).thenReturn (tMockAbbrev2);
			Mockito.when (mTokenCompany2.getStatusName ()).thenReturn (tMockStatus2);

			tToken2 = new Token (mTokenCompany2, TokenInfo.TokenType.MAP);

			assertFalse (token.isSameCompany (tToken2));
			assertFalse (token.isCorporationAbbrev (tMockAbbrev2));
		}

		@Test
		@DisplayName ("Based on (Same) Company")
		public void testToken4 () {
			String tMockAbbrev;
			Token tToken2;

			tMockAbbrev = "MCA";
			Mockito.when (mTokenCompany.getAbbrev ()).thenReturn (tMockAbbrev);

			tToken2 = new Token (mTokenCompany, TokenInfo.TokenType.MAP);

			assertTrue (token.isSameCompany (tToken2));
			assertTrue (token.isCorporationAbbrev (tMockAbbrev));
		}
	}
	
	@Test
	@DisplayName ("Test Creating XML Element for Token")
	public void testXMLElementCreation () {
		XMLDocument tXMLDocument;
		XMLElement tXMLElement;
		
		tXMLDocument = new XMLDocument ();
		tXMLElement = token.getTokenElement (tXMLDocument);
		tXMLDocument.appendChild (tXMLElement);
		assertEquals ("<Token abbrev=\"MC1\"/>\n", tXMLDocument.toString ());
	}
}
