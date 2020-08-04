package ge18xx.company;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author marksmith
 *
 */
@DisplayName ("Testing the Token Class with Mocking Token Company")
class TokenTests {

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
	@DisplayName ("Test Token Creation with No Argument")
	public void testToken() {
		Token tToken1;
		
		tToken1 = new Token ();
		assertEquals (Token.NO_COMPANY, tToken1.getWhichCompany ());
	}

	@Test
	@DisplayName ("Test Token Creation with a Mocked Company")
	public void testGetCorporationIDFromToken () {
		int tMockCoID;
		Token tToken;
		
		tMockCoID = 5001;
		TokenCompany mCompany = Mockito.mock (TokenCompany.class);
		Mockito.when (mCompany.getID ()).thenReturn (tMockCoID);
		
		tToken = new Token (mCompany);
		assertEquals (5001, tToken.getCorporationID ());
	}
	
	@Test
	@DisplayName ("Test Token Creation with a Token and Mocked Company")
	public void testToken2 () {	
		int tMockCoID;
		String tMockAbbrev, tMockStatus;
		
		tMockCoID = 5001;
		tMockAbbrev = "MCA";
		tMockStatus = "Operated";
		TokenCompany mCompany = Mockito.mock (TokenCompany.class);
		Mockito.when (mCompany.getID ()).thenReturn (tMockCoID);
		Mockito.when (mCompany.getAbbrev ()).thenReturn (tMockAbbrev);
		Mockito.when (mCompany.getStatusName ()).thenReturn (tMockStatus);
		
		Token tToken1 = new Token (mCompany);
		Token tToken2 = new Token (tToken1);
		
		assertEquals ("MCA", tToken2.getCorporationAbbrev ());
		assertEquals (5001, tToken2.getCorporationID ());
		assertEquals ("Operated", tToken2.getCorporationStatus ());
	}

	@Test
	@DisplayName ("Test Comparing Tokens based on (Not Same) Company")
	public void testToken3 () {	
		int tMockCoID;
		String tMockAbbrev, tMockStatus;
		
		tMockCoID = 5001;
		tMockAbbrev = "MCA";
		tMockStatus = "Operated";
		TokenCompany mCompany = Mockito.mock (TokenCompany.class);
		Mockito.when (mCompany.getID ()).thenReturn (tMockCoID);
		Mockito.when (mCompany.getAbbrev ()).thenReturn (tMockAbbrev);
		Mockito.when (mCompany.getStatusName ()).thenReturn (tMockStatus);
		tMockCoID = 5002;
		tMockAbbrev = "MCA2";
		tMockStatus = "Owned";
		TokenCompany mCompany2 = Mockito.mock (TokenCompany.class);
		Mockito.when (mCompany2.getID ()).thenReturn (tMockCoID);
		Mockito.when (mCompany2.getAbbrev ()).thenReturn (tMockAbbrev);
		Mockito.when (mCompany2.getStatusName ()).thenReturn (tMockStatus);
		
		Token tToken1 = new Token (mCompany);
		Token tToken2 = new Token (mCompany2);
		
		assertFalse (tToken1.isSameCompany (tToken2));
		assertFalse (tToken1.isCorporationAbbrev (tMockAbbrev));
	}

	@Test
	@DisplayName ("Test Comparing Tokens based on (Same) Company")
	public void testToken4 () {	
		int tMockCoID;
		String tMockAbbrev;
		
		tMockCoID = 5001;
		tMockAbbrev = "MCA";
		TokenCompany mCompany = Mockito.mock (TokenCompany.class);
		Mockito.when (mCompany.getID ()).thenReturn (tMockCoID);
		Mockito.when (mCompany.getAbbrev ()).thenReturn (tMockAbbrev);
		
		Token tToken1 = new Token (mCompany);
		Token tToken2 = new Token (mCompany);
		
		assertTrue (tToken1.isSameCompany (tToken2));
		assertTrue (tToken1.isCorporationAbbrev (tMockAbbrev));
	}
}
