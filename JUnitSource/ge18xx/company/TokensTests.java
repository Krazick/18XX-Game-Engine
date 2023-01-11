package ge18xx.company;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TokensTests {
	CompanyTestFactory companyTestFactory;
	Tokens tokens2;
	Tokens tokens3;
	Tokens tokens4;
	Tokens tokens5;
	
	@BeforeEach
	void setUp () throws Exception {
		int tTokenCount;
		
		companyTestFactory = new CompanyTestFactory ();
		
		tTokenCount = 2;
		tokens2 = new Tokens (tTokenCount);
		tTokenCount = 3;
		tokens3 = new Tokens (tTokenCount);
		tTokenCount = 4;
		tokens4 = new Tokens (tTokenCount);
		tTokenCount = 5;
		tokens5 = new Tokens (tTokenCount);
	}

	@Test
	@DisplayName ("Basic construction of Tokens Test")
	void basicConstructionTest () {
		assertEquals (5, tokens4.getTokenCount ());
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
			tokens4.addNewToken (mToken, TokenInfo.TokenType.MARKET, 0);
			
			assertEquals (5, tokens4.getTokenCount ());
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
			assertEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.HOME1);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.HOME2);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.FIXED_COST);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.RANGE_COST);
			assertNotEquals (mToken, tFoundToken);
			
			tFoundToken = tokens4.getMarketToken ();
			assertEquals (mToken, tFoundToken);
	}
		
		@Test
		@DisplayName ("Home1 Token Test") 
		void addHome1TokenTest () {
			Token mToken;
			Token tFoundToken;
			
			mToken = companyTestFactory.buildTokenMock ();
			tokens4.addNewToken (mToken, TokenInfo.TokenType.HOME1, 0);
			
			assertEquals (5, tokens4.getTokenCount ());
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.HOME1);
			assertEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.HOME2);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.FIXED_COST);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.RANGE_COST);
			assertNotEquals (mToken, tFoundToken);
			
			tFoundToken = tokens4.getHome1Token ();
			assertEquals (mToken, tFoundToken);
		}
		
		@Test
		@DisplayName ("Home2 Token Test") 
		void addHome2TokenTest () {
			Token mToken;
			Token tFoundToken;
	
			mToken = companyTestFactory.buildTokenMock ();
			tokens4.addNewToken (mToken, TokenInfo.TokenType.HOME2, 0);
			
			assertEquals (5, tokens4.getTokenCount ());
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.HOME1);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.HOME2);
			assertEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.FIXED_COST);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.RANGE_COST);
			assertNotEquals (mToken, tFoundToken);
			
			tFoundToken = tokens4.getHome2Token ();
			assertEquals (mToken, tFoundToken);
		}
		
		@Test
		@DisplayName ("Fixed Cost Token Test") 
		void addFixedCostTokenTest () {
			Token mToken;
			Token mHomeToken;
			Token tFoundToken;
	
			mHomeToken = companyTestFactory.buildTokenMock ();
			tokens4.addNewToken (mHomeToken, TokenInfo.TokenType.HOME1, 0);
			
			mToken = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mToken, TokenInfo.TokenType.FIXED_COST, 40);
			
			assertEquals (5, tokens4.getTokenCount ());
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.HOME1);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.HOME2);
			assertNotEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.FIXED_COST);
			assertEquals (mToken, tFoundToken);
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.RANGE_COST);
			assertNotEquals (mToken, tFoundToken);

			tFoundToken = tokens4.getMapToken ();
			assertEquals (mToken, tFoundToken);
		}
		
		@Test
		@DisplayName ("Range Cost Token Test") 
		void addRangeCostTokenTest () {
			MapToken mMapToken;
			MapToken mHomeToken;
			MapToken tFoundToken;
			
			mHomeToken = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mHomeToken, TokenInfo.TokenType.HOME1, 0);
			
			mMapToken = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mMapToken, TokenInfo.TokenType.RANGE_COST, 0);
			
			assertEquals (5, tokens4.getTokenCount ());
			tFoundToken = tokens4.getMapToken (TokenInfo.TokenType.MARKET);
			assertNotEquals (mMapToken, tFoundToken);
			tFoundToken = tokens4.getMapToken (TokenInfo.TokenType.HOME1);
			assertNotEquals (mMapToken, tFoundToken);
			tFoundToken = tokens4.getMapToken (TokenInfo.TokenType.HOME2);
			assertNotEquals (mMapToken, tFoundToken);
			tFoundToken = tokens4.getMapToken (TokenInfo.TokenType.FIXED_COST);
			assertNotEquals (mMapToken, tFoundToken);
			tFoundToken = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
			assertEquals (mMapToken, tFoundToken);

			tFoundToken = tokens4.getMapToken ();
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
			tokens4.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
			mHome1Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
			mHome2Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mHome2Token, TokenInfo.TokenType.HOME2, 0);
			mMap1Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mMap1Token, TokenInfo.TokenType.FIXED_COST, 40);
			mMap2Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mMap2Token, TokenInfo.TokenType.FIXED_COST, 100);
			assertEquals (5, tokens4.getTokenCount ());
		
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
			tokens4.setTokenUsed (tFoundToken, true);
			assertEquals (mMarketToken, tFoundToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.HOME1);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome1Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.HOME2);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome2Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.FIXED_COST);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap1Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.FIXED_COST);
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
			tokens5.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
			mHome1Token = companyTestFactory.buildMapTokenMock ();
			tokens5.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
			mMap1Token = companyTestFactory.buildMapTokenMock ();
			tokens5.addNewToken (mMap1Token, TokenInfo.TokenType.FIXED_COST, 40);
			mMap2Token = companyTestFactory.buildMapTokenMock ();
			tokens5.addNewToken (mMap2Token, TokenInfo.TokenType.FIXED_COST, 100);
			mMap3Token = companyTestFactory.buildMapTokenMock ();
			tokens5.addNewToken (mMap3Token, TokenInfo.TokenType.FIXED_COST, 100);
			assertEquals (5, tokens4.getTokenCount ());
		
			tFoundToken = tokens5.getToken (TokenInfo.TokenType.MARKET);
			tokens5.setTokenUsed (tFoundToken, true);
			assertEquals (mMarketToken, tFoundToken);
			tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.HOME1);
			tokens5.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome1Token, tFoundMapToken);
			tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
			tokens5.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap1Token, tFoundMapToken);
			tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
			tokens5.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap2Token, tFoundMapToken);
			tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
			assertEquals (mMap3Token, tFoundMapToken);
			
			tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.HOME2);
			assertNull (tFoundMapToken);
			tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.NO_TYPE);
			assertNull (tFoundMapToken);
			tFoundToken = tokens5.getToken (TokenInfo.TokenType.NO_TYPE);
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
			tokens4.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
			mHome1Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
			mHome2Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mHome2Token, TokenInfo.TokenType.HOME2, 0);
			mMap1Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mMap1Token, TokenInfo.TokenType.RANGE_COST, 0);
			mMap2Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mMap2Token, TokenInfo.TokenType.RANGE_COST, 0);
			assertEquals (5, tokens4.getTokenCount ());
		
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
			tokens4.setTokenUsed (tFoundToken, true);
			assertEquals (mMarketToken, tFoundToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.HOME1);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome1Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.HOME2);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome2Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap1Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
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
			tokens4.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
			mHome1Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
			mMap1Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mMap1Token, TokenInfo.TokenType.RANGE_COST, 0);
			mMap2Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mMap2Token, TokenInfo.TokenType.RANGE_COST, 0);
			mMap3Token = companyTestFactory.buildMapTokenMock ();
			tokens4.addNewToken (mMap3Token, TokenInfo.TokenType.RANGE_COST, 0);
			assertEquals (5, tokens4.getTokenCount ());
		
			tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
			tokens4.setTokenUsed (tFoundToken, true);
			assertEquals (mMarketToken, tFoundToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.HOME1);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mHome1Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap1Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
			tokens4.setTokenUsed (tFoundMapToken, true);
			assertEquals (mMap2Token, tFoundMapToken);
			tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
			assertEquals (mMap3Token, tFoundMapToken);
		}
	}
	
	@Test
	@DisplayName ("Get Token Costs with Fixed Cost Tests")
	void getToken_FixedCost_Test () {
		Token mMarketToken;
		MapToken mHome1Token;
		MapToken mMap1Token;
		MapToken mMap2Token;
		MapToken mMap3Token;
		Token tFoundToken;
		MapToken tFoundMapToken;
		
		mMarketToken = companyTestFactory.buildTokenMock ();
		tokens5.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
		mHome1Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
		mMap1Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mMap1Token, TokenInfo.TokenType.FIXED_COST, 40);
		mMap2Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mMap2Token, TokenInfo.TokenType.FIXED_COST, 100);
		mMap3Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mMap3Token, TokenInfo.TokenType.FIXED_COST, 100);

		tFoundToken = tokens5.getToken (TokenInfo.TokenType.MARKET);
		assertEquals (0, tokens5.getTokenCost (tFoundToken));
		tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.HOME1);
		assertEquals (0, tokens5.getTokenCost (tFoundMapToken));
		tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
		assertEquals (40, tokens5.getTokenCost (tFoundMapToken));
		tokens5.setTokenUsed (tFoundMapToken, true);
		tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
		assertEquals (100, tokens5.getTokenCost (tFoundMapToken));
		tokens5.setTokenUsed (tFoundMapToken, true);
		tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
		assertEquals (100, tokens5.getTokenCost (tFoundMapToken));
	}
	
	@Test
	@DisplayName ("Get Token Costs with Fixed Cost Tests")
	void getToken_FixedCost_Test2 () {
		Token mMarketToken;
		MapToken mHome1Token;
		MapToken mMap1Token;
		MapToken mMap2Token;
		MapToken mMap3Token;
		Token tFoundToken;
		Token tFoundToken2;
		MapToken tFoundMapToken;
		
		mMarketToken = companyTestFactory.buildTokenMock ();
		tokens5.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
		mHome1Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
		mMap1Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mMap1Token, TokenInfo.TokenType.FIXED_COST, 40);
		mMap2Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mMap2Token, TokenInfo.TokenType.FIXED_COST, 100);
		mMap3Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mMap3Token, TokenInfo.TokenType.FIXED_COST, 100);
		mMap3Token = companyTestFactory.buildMapTokenMock ();
		tokens5.addNewToken (mMap3Token, TokenInfo.TokenType.FIXED_COST, 120);

		tFoundToken = tokens5.getToken (TokenInfo.TokenType.MARKET);
		assertEquals (0, tokens5.getTokenCost (tFoundToken));
		tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.HOME1);
		assertEquals (0, tokens5.getTokenCost (tFoundMapToken));
		tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
		assertEquals (40, tokens5.getTokenCost (tFoundMapToken));
		tokens5.setTokenUsed (tFoundMapToken, true);
		tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
		assertEquals (100, tokens5.getTokenCost (tFoundMapToken));
		tokens5.setTokenUsed (tFoundMapToken, true);
		tFoundMapToken = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
		assertEquals (100, tokens5.getTokenCost (tFoundMapToken));
		tokens5.setTokenUsed (tFoundMapToken, true);
		tFoundToken2 = tokens5.getMapToken (TokenInfo.TokenType.FIXED_COST);
		assertEquals (120, tokens5.getTokenCost (tFoundToken2));
	}
	
	@Test
	@DisplayName ("Get Token Costs with Range Cost Tests")
	void getToken_RangeCost_Test () {
		Token mMarketToken;
		MapToken mHome1Token;
		MapToken mMap1Token;
		MapToken mMap2Token;
		MapToken mMap3Token;
		Token tFoundToken;
		MapToken tFoundMapToken;
		
		mMarketToken = companyTestFactory.buildTokenMock ();
		tokens4.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
		mHome1Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
		mMap1Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap1Token, TokenInfo.TokenType.RANGE_COST, 0);
		mMap2Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap2Token, TokenInfo.TokenType.RANGE_COST, 0);
		mMap3Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap3Token, TokenInfo.TokenType.RANGE_COST, 0);

		tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
		assertEquals (0, tokens4.getTokenCost (tFoundToken));
		tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.HOME1);
		assertEquals (0, tokens4.getTokenCost (tFoundMapToken));
		tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
		assertEquals (-1, tokens4.getTokenCost (tFoundMapToken));
		tokens4.setTokenUsed (tFoundMapToken, true);
		tFoundMapToken = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
		assertEquals (-1, tokens4.getTokenCost (tFoundMapToken));
	}

	@Test
	@DisplayName ("Test getting Last Map Token in list with Range Costs Tests")
	void getLastMapToken_RangeCost_Test () {
		Token mMarketToken;
		MapToken mHome1Token;
		MapToken mMap1Token;
		MapToken mMap2Token;
		MapToken mMap3Token;
		MapToken tFoundLastMapToken;
		MapToken tFoundLastMapToken2;
		
		mMarketToken = companyTestFactory.buildTokenMock ();
		tokens4.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
		mHome1Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
		mMap1Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap1Token, TokenInfo.TokenType.RANGE_COST, 0);
		mMap2Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap2Token, TokenInfo.TokenType.RANGE_COST, 0);
		mMap3Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap3Token, TokenInfo.TokenType.RANGE_COST, 0);

		tFoundLastMapToken = tokens4.getLastMapToken (TokenInfo.TokenType.RANGE_COST);
		assertEquals (mMap3Token, tFoundLastMapToken);

		tokens4.setTokenUsed (tFoundLastMapToken, true);
		tFoundLastMapToken2 = tokens4.getLastMapToken (TokenInfo.TokenType.RANGE_COST);
		assertEquals (MapToken.NO_MAP_TOKEN, tFoundLastMapToken2);

		tFoundLastMapToken = tokens4.getLastMapToken (TokenInfo.TokenType.FIXED_COST);
		assertEquals (MapToken.NO_MAP_TOKEN, tFoundLastMapToken);
	}
	
	@Test
	@DisplayName ("Get Token Used with Range Costs Tests")
	void getTokenUsed_RangeCost_Test () {
		Token mMarketToken;
		MapToken mHome1Token;
		MapToken mMap1Token;
		MapToken mMap2Token;
		MapToken mMap3Token;
		Token tFoundToken;
		MapToken tFoundMapToken1;
		MapToken tFoundMapToken2;
		MapToken tFoundMapToken3;
		
		mMarketToken = companyTestFactory.buildTokenMock ();
		tokens4.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
		mHome1Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
		mMap1Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap1Token, TokenInfo.TokenType.RANGE_COST, 0);
		mMap2Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap2Token, TokenInfo.TokenType.RANGE_COST, 0);
		mMap3Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap3Token, TokenInfo.TokenType.RANGE_COST, 0);

		tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
		tokens4.setTokenUsed (tFoundToken, true);
		tFoundMapToken1 = tokens4.getMapToken (TokenInfo.TokenType.HOME1);
		tokens4.setTokenUsed (tFoundMapToken1, true);
		tFoundMapToken2 = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
		tokens4.setTokenUsed (tFoundMapToken2, true);
		tFoundMapToken3 = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);

		assertTrue (tokens4.getTokenUsed (tFoundToken));
		assertTrue (tokens4.getTokenUsed (tFoundMapToken1));
		assertTrue (tokens4.getTokenUsed (tFoundMapToken2));
		assertFalse (tokens4.getTokenUsed (tFoundMapToken3));
	}

	@Test
	@DisplayName ("Get Token Available Count with Range Costs Tests")
	void getTokenAvailableCount_RangeCost_Test () {
		Token mMarketToken;
		MapToken mHome1Token;
		MapToken mMap1Token;
		MapToken mMap2Token;
		MapToken mMap3Token;
		Token tFoundToken;
		MapToken tFoundMapToken1;
		MapToken tFoundMapToken2;
		MapToken tFoundMapToken3;
		MapToken tFoundMapToken4;

		
		mMarketToken = companyTestFactory.buildTokenMock ();
		tokens4.addNewToken (mMarketToken, TokenInfo.TokenType.MARKET, 0);
		mHome1Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mHome1Token, TokenInfo.TokenType.HOME1, 0);
		mMap1Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap1Token, TokenInfo.TokenType.RANGE_COST, 0);
		mMap2Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap2Token, TokenInfo.TokenType.RANGE_COST, 0);
		mMap3Token = companyTestFactory.buildMapTokenMock ();
		tokens4.addNewToken (mMap3Token, TokenInfo.TokenType.RANGE_COST, 0);

		assertEquals (4, tokens4.getAvailableTokenCount ());
		tFoundToken = tokens4.getToken (TokenInfo.TokenType.MARKET);
		tokens4.setTokenUsed (tFoundToken, true);
		assertEquals (4, tokens4.getAvailableTokenCount ());
		
		tFoundMapToken1 = tokens4.getMapToken (TokenInfo.TokenType.HOME1);
		tokens4.setTokenUsed (tFoundMapToken1, true);
		assertEquals (3, tokens4.getAvailableTokenCount ());
		
		tFoundMapToken2 = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
		tokens4.setTokenUsed (tFoundMapToken2, true);
		assertEquals (2, tokens4.getAvailableTokenCount ());
		
		tFoundMapToken3 = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
		tokens4.setTokenUsed (tFoundMapToken3, true);
		assertEquals (1, tokens4.getAvailableTokenCount ());
		
		tFoundMapToken4 = tokens4.getMapToken (TokenInfo.TokenType.RANGE_COST);
		tokens4.setTokenUsed (tFoundMapToken4, true);
		assertEquals (0, tokens4.getAvailableTokenCount ());

	}

}
