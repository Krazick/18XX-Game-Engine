package ge18xx.company;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TokensTests {
	CompanyTestFactory companyTestFactory;
	Tokens tokens;
	
	@BeforeEach
	void setUp () throws Exception {
		int tTokenCount;
		
		tTokenCount = 4;
		tokens = new Tokens (tTokenCount);
		companyTestFactory = new CompanyTestFactory ();
	}

	@Test
	@DisplayName ("Basic construction of Tokens Test")
	void basicConstructionTest () {
		assertEquals (5, tokens.getTokenCount ());
	}

	@Nested
	@DisplayName ("Adding Different Token Types, and validating the Retrieval")
	class addDifferentTokenTypes {
		@Test
		@DisplayName ("Market Token Test") 
		void addMarketTokenTest () {
			Token mToken;
			Token tFoundToken;
			
			mToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mToken, TokenInfo.TokenType.MARKET, 0);
			
			assertEquals (5, tokens.getTokenCount ());
			tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
			assertEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME1);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME2);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.FIXED_COST);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.RANGE_COST);
			assertNotEquals (mToken, tFoundToken);
		}
		
		@Test
		@DisplayName ("Home1 Token Test") 
		void addHome1TokenTest () {
			Token mToken;
			Token tFoundToken;
			
			mToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mToken, TokenInfo.TokenType.HOME1, 0);
			
			assertEquals (5, tokens.getTokenCount ());
			tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME1);
			assertEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME2);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.FIXED_COST);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.RANGE_COST);
			assertNotEquals (mToken, tFoundToken);
		}
		
		@Test
		@DisplayName ("Home2 Token Test") 
		void addHome2TokenTest () {
			Token mToken;
			Token tFoundToken;
	
			mToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mToken, TokenInfo.TokenType.HOME2, 0);
			
			assertEquals (5, tokens.getTokenCount ());
			tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME1);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME2);
			assertEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.FIXED_COST);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.RANGE_COST);
			assertNotEquals (mToken, tFoundToken);
		}
		
		@Test
		@DisplayName ("Fixed Cost Token Test") 
		void addFixedCostTokenTest () {
			Token mToken;
			Token mHomeToken;
			Token tFoundToken;
	
			mHomeToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mHomeToken, TokenInfo.TokenType.HOME1, 0);
			
			mToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mToken, TokenInfo.TokenType.FIXED_COST, 40);
			
			assertEquals (5, tokens.getTokenCount ());
			tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME1);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME2);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.FIXED_COST);
			assertEquals (mToken, tFoundToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.RANGE_COST);
			assertNotEquals (mToken, tFoundToken);
		}
		
		@Test
		@DisplayName ("Range Cost Token Test") 
		void addRangeCostTokenTest () {
			MapToken mMapToken;
			MapToken mHomeToken;
			MapToken tFoundToken;
			
			mHomeToken = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mHomeToken, TokenInfo.TokenType.HOME1, 0);
			
			mMapToken = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMapToken, TokenInfo.TokenType.RANGE_COST, 0);
			
			assertEquals (5, tokens.getTokenCount ());
			tFoundToken = tokens.getMapToken (TokenInfo.TokenType.MARKET);
			assertNotEquals (mMapToken, tFoundToken);
			tFoundToken = tokens.getMapToken (TokenInfo.TokenType.HOME1);
			assertNotEquals (mMapToken, tFoundToken);
			tFoundToken = tokens.getMapToken (TokenInfo.TokenType.HOME2);
			assertNotEquals (mMapToken, tFoundToken);
			tFoundToken = tokens.getMapToken (TokenInfo.TokenType.FIXED_COST);
			assertNotEquals (mMapToken, tFoundToken);
			tFoundToken = tokens.getMapToken (TokenInfo.TokenType.RANGE_COST);
			assertEquals (mMapToken, tFoundToken);
		}
	}
	
	@Nested
	@DisplayName ("Add Max # of Tokens (5) and confirm retrieval of all")
	class fullTokenInfoSetTests {
		@Test
		@DisplayName ("Market, Home1, Home2 and two Fixed Costs")
		void addAllTokensMHHFF_FixedCost_Test () {
			Token mMarketToken;
			MapToken mHome1Token;
			MapToken mHome2Token;
			MapToken mMap1Token;
			MapToken mMap2Token;
			Token tFoundToken;
			MapToken tFoundMapToken;
			
			mMarketToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
			mHome1Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
			mHome2Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mHome2Token, TokenInfo.TokenType.HOME2, 0);
			mMap1Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap1Token, TokenInfo.TokenType.FIXED_COST, 40);
			mMap2Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap2Token, TokenInfo.TokenType.FIXED_COST, 100);
			assertEquals (5, tokens.getTokenCount ());
		
			tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
			tokens.setTokenUsed (tFoundToken, true);
			assertEquals (mMarketToken, tFoundToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.HOME1);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome1Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.HOME2);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome2Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.FIXED_COST);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap1Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.FIXED_COST);
			assertEquals (mMap2Token, tFoundMapToken);
		}
		
		@Test
		@DisplayName ("Market, Home1, and three Fixed Costs")
		void addAllTokensMHFFF_FixedCost_Test () {
			Token mMarketToken;
			MapToken mHome1Token;
			MapToken mMap1Token;
			MapToken mMap2Token;
			MapToken mMap3Token;
			Token tFoundToken;
			MapToken tFoundMapToken;
			
			mMarketToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
			mHome1Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
			mMap1Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap1Token, TokenInfo.TokenType.FIXED_COST, 40);
			mMap2Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap2Token, TokenInfo.TokenType.FIXED_COST, 100);
			mMap3Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap3Token, TokenInfo.TokenType.FIXED_COST, 100);
			assertEquals (5, tokens.getTokenCount ());
		
			tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
			tokens.setTokenUsed (tFoundToken, true);
			assertEquals (mMarketToken, tFoundToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.HOME1);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome1Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.FIXED_COST);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap1Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.FIXED_COST);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap2Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.FIXED_COST);
			assertEquals (mMap3Token, tFoundMapToken);
			
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.HOME2);
			assertNull (tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.NO_TYPE);
			assertNull (tFoundMapToken);
			tFoundToken = tokens.getToken (TokenInfo.TokenType.NO_TYPE);
			assertNull (tFoundToken);
		}
		
		@Test
		@DisplayName ("Market, Home1, Home2 and two Range Costs")
		void addAllTokensMHHFF_RangeCost_Test () {
			Token mMarketToken;
			MapToken mHome1Token;
			MapToken mHome2Token;
			MapToken mMap1Token;
			MapToken mMap2Token;
			Token tFoundToken;
			MapToken tFoundMapToken;
			
			mMarketToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
			mHome1Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
			mHome2Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mHome2Token, TokenInfo.TokenType.HOME2, 0);
			mMap1Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap1Token, TokenInfo.TokenType.RANGE_COST, 0);
			mMap2Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap2Token, TokenInfo.TokenType.RANGE_COST, 0);
			assertEquals (5, tokens.getTokenCount ());
		
			tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
			tokens.setTokenUsed (tFoundToken, true);
			assertEquals (mMarketToken, tFoundToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.HOME1);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome1Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.HOME2);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome2Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.RANGE_COST);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap1Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.RANGE_COST);
			assertEquals (mMap2Token, tFoundMapToken);
		}
		
		@Test
		@DisplayName ("Market, Home1, and three Range Costs")
		void addAllTokensMHFFF_RangeCost_Test () {
			Token mMarketToken;
			MapToken mHome1Token;
			MapToken mMap1Token;
			MapToken mMap2Token;
			MapToken mMap3Token;
			Token tFoundToken;
			MapToken tFoundMapToken;
			
			mMarketToken = companyTestFactory.buildTokenMock ();
			tokens.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
			mHome1Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
			mMap1Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap1Token, TokenInfo.TokenType.RANGE_COST, 0);
			mMap2Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap2Token, TokenInfo.TokenType.RANGE_COST, 0);
			mMap3Token = companyTestFactory.buildMapTokenMock ();
			tokens.addNewToken (mMap3Token, TokenInfo.TokenType.RANGE_COST, 0);
			assertEquals (5, tokens.getTokenCount ());
		
			tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
			tokens.setTokenUsed (tFoundToken, true);
			assertEquals (mMarketToken, tFoundToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.HOME1);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome1Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.RANGE_COST);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap1Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.RANGE_COST);
			tokens.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap2Token, tFoundMapToken);
			tFoundMapToken = tokens.getMapToken (TokenInfo.TokenType.RANGE_COST);
			assertEquals (mMap3Token, tFoundMapToken);
		}
	}
}
