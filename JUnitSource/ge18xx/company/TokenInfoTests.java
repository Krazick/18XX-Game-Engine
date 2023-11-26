package ge18xx.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ge18xx.company.TokenInfo.TokenType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName ("Token Info Tests")
class TokenInfoTests {
	TokenInfo tokenInfo;
	Token mapToken;
	CompanyTestFactory companyTestFactory;
	Token mToken;
	MapToken mMapToken1;
	MapToken mMapToken2;
	
	@BeforeEach
	void setUp () throws Exception {
		companyTestFactory = new CompanyTestFactory ();
		mToken = companyTestFactory.buildTokenMock ();
		mMapToken1 = companyTestFactory.buildMapTokenMock ();
		mMapToken2 = companyTestFactory.buildMapTokenMock ();
		mapToken = companyTestFactory.buildToken ();
	}

	@DisplayName ("TokenType To String Tests")
	@Test
	void tokenTypeToStringTest () {
		TokenInfo.TokenType tTokenInfo;
		
		tTokenInfo  = mapToken.getTokenType ();
		System.out.println ("Map Token Info: " + mapToken.getCorporationAbbrev () +
				" Type: " + tTokenInfo.toString ());
	}
	
	@Nested
	@DisplayName ("TokenInfo IS ... TOKEN Tests")
	class TokenInfoCreationTests {
		@Test
		@DisplayName ("With Mocked Token and MARKET TokenType")
		void basicConstructorMarketTokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mToken, TokenType.MARKET, 0);
	
			assertTrue (tTokenInfo.isMarketToken ());
			assertFalse (tTokenInfo.isHomeToken ());
			assertFalse (tTokenInfo.isFixedCostToken ());
			assertFalse (tTokenInfo.isRangeCostToken ());
			assertFalse (tTokenInfo.isMapToken ());
			assertEquals (0, tTokenInfo.getCost ());
			assertEquals (TokenType.MARKET, tTokenInfo.getTokenType ());
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.MARKET));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.HOME1));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.MAP));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.FIXED_COST));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.RANGE_COST));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.HOME2));
		}
	
		@Test
		@DisplayName ("With Mocked Token and HOME1 TokenType")
		void basicConstructorHome1TokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mMapToken1, TokenType.HOME1, 0);
	
			assertFalse (tTokenInfo.isMarketToken ());
			assertTrue (tTokenInfo.isHomeToken ());
			assertFalse (tTokenInfo.isFixedCostToken ());
			assertFalse (tTokenInfo.isRangeCostToken ());
			assertTrue (tTokenInfo.isMapToken ());
			assertEquals (0, tTokenInfo.getCost ());
			assertEquals (TokenType.HOME1, tTokenInfo.getTokenType ());
			
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.MARKET));
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.HOME1));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.HOME2));
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.MAP));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.FIXED_COST));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.RANGE_COST));
		}
	
		@Test
		@DisplayName ("With Mocked Token and HOME2 TokenType")
		void basicConstructorHome2TokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mMapToken1, TokenType.HOME2, 0);
	
			assertFalse (tTokenInfo.isMarketToken ());
			assertTrue (tTokenInfo.isHomeToken ());
			assertFalse (tTokenInfo.isFixedCostToken ());
			assertFalse (tTokenInfo.isRangeCostToken ());
			assertTrue (tTokenInfo.isMapToken ());
			assertEquals (0, tTokenInfo.getCost ());
			assertEquals (TokenType.HOME2, tTokenInfo.getTokenType ());
			
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.MARKET));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.HOME1));
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.HOME2));
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.MAP));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.FIXED_COST));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.RANGE_COST));
		}
	
		@Test
		@DisplayName ("With Mocked Token and FIXED COST TokenType")
		void basicConstructorFixedCostdTokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mMapToken1, TokenType.FIXED_COST, 40);
	
			assertFalse (tTokenInfo.isMarketToken ());
			assertFalse (tTokenInfo.isHomeToken ());
			assertTrue (tTokenInfo.isFixedCostToken ());
			assertFalse (tTokenInfo.isRangeCostToken ());
			assertTrue (tTokenInfo.isMapToken ());
			assertEquals (40, tTokenInfo.getCost ());
			assertEquals (TokenType.FIXED_COST, tTokenInfo.getTokenType ());
			
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.MARKET));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.HOME1));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.HOME2));
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.MAP));
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.FIXED_COST));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.RANGE_COST));
		}
	
		@Test
		@DisplayName ("With Mocked Token and RANGE COST TokenType")
		void basicConstructorRangeCostTokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mMapToken1, TokenType.RANGE_COST, 0);
	
			assertFalse (tTokenInfo.isMarketToken ());
			assertFalse (tTokenInfo.isHomeToken ());
			assertFalse (tTokenInfo.isFixedCostToken ());
			assertTrue (tTokenInfo.isRangeCostToken ());
			assertTrue (tTokenInfo.isMapToken ());
			assertEquals (Token.RANGE_COST, tTokenInfo.getCost ());
			assertEquals (TokenType.RANGE_COST, tTokenInfo.getTokenType ());
			
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.MARKET));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.HOME1));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.HOME2));
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.MAP));
			assertFalse (tTokenInfo.isMatchingTokenType (TokenType.FIXED_COST));
			assertTrue (tTokenInfo.isMatchingTokenType (TokenType.RANGE_COST));
		}
	}
	
	@Nested
	@DisplayName ("TokenInfo to test GetToken Methods")
	class TokenInfoGetTokenTests {
		@Test
		@DisplayName ("GetHomeToken Test")
		void getHomeTokenTest () {
			TokenInfo tHome1TokenInfo;
			TokenInfo tHome2TokenInfo;
			TokenInfo tMarketTokenInfo;
			MapToken tMapToken1;
			MapToken tMapToken2;
			Token tMarketToken;
			
			tHome1TokenInfo = new TokenInfo (mMapToken1, TokenType.HOME1, 0);
			tMapToken1 = tHome1TokenInfo.getHomeToken ();
			assertEquals (mMapToken1, tMapToken1);
			assertNotEquals (mMapToken2, tMapToken1);
			
			tMarketToken = tHome1TokenInfo.getMarketToken ();
			assertNull (tMarketToken);

			tHome2TokenInfo = new TokenInfo (mMapToken2, TokenType.HOME2, 0);
			tMapToken2 = tHome2TokenInfo.getHomeToken ();
			assertNotEquals (mMapToken1, tMapToken2);
			assertEquals (mMapToken2, tMapToken2);
			
			tMarketToken = tHome2TokenInfo.getMarketToken ();
			assertNull (tMarketToken);
			
			tMarketTokenInfo = new TokenInfo (mToken, TokenType.MARKET);
			tMapToken1 = tMarketTokenInfo.getHomeToken ();
			assertNull (tMapToken1);
		}
		
		@Test
		@DisplayName ("Get Map Token with a Fixed Cost Token Test")
		void getFixedCostTokenTest () {
			TokenInfo tFixedCostMapTokenInfo;
			TokenInfo tFixedCostTokenInfo;
			TokenInfo tMarketTokenInfo;
			MapToken tMapToken;
			Token tMarketToken;
			
			tFixedCostMapTokenInfo = new TokenInfo (mMapToken1, TokenType.FIXED_COST, 0);
			tMapToken = tFixedCostMapTokenInfo.getMapToken ();
			assertEquals (mMapToken1, tMapToken);
			assertNotEquals (mMapToken2, tMapToken);
			
			tMarketToken = tFixedCostMapTokenInfo.getMarketToken ();
			assertNull (tMarketToken);

			tFixedCostTokenInfo = new TokenInfo (mToken, TokenType.FIXED_COST, 0);
			tMapToken = tFixedCostTokenInfo.getMapToken ();
			assertNull (tMapToken);
			
			tMarketToken = tFixedCostMapTokenInfo.getMarketToken ();
			assertNull (tMarketToken);

			tMarketTokenInfo = new TokenInfo (mToken, TokenType.MARKET);
			tMapToken = tMarketTokenInfo.getMapToken ();
			assertNull (tMapToken);
		}

		@Test
		@DisplayName ("Get Token with a Market Token Test")
		void getMarketTokenTest () {
			TokenInfo tFixedCostMapTokenInfo;
			TokenInfo tMarketTokenInfo;
			Token tMarketToken;
			
			tFixedCostMapTokenInfo = new TokenInfo (mMapToken1, TokenType.FIXED_COST, 0);
			tMarketToken = tFixedCostMapTokenInfo.getMarketToken ();
			assertNull (tMarketToken);
			
			tMarketTokenInfo = new TokenInfo (mToken, TokenType.MARKET);
			tMarketToken = tMarketTokenInfo.getMarketToken ();
			assertEquals (mToken, tMarketToken);
			assertNotEquals (mMapToken2, tMarketToken);
			
			assertFalse (tMarketTokenInfo.isUsed ());
			assertFalse (tMarketTokenInfo.getUsed ());
			tMarketTokenInfo.setUsed (true);
			assertTrue (tMarketTokenInfo.isUsed ());
			assertTrue (tMarketTokenInfo.getUsed ());
		}
		
		@Test
		@DisplayName ("Get Token Test")
		void getTokenTest () {
			TokenInfo tMarketTokenInfo;
			Token tMarketToken;

			tMarketTokenInfo = new TokenInfo (mToken, TokenType.MARKET);
			tMarketToken = tMarketTokenInfo.getToken ();
			assertEquals (mToken, tMarketToken);
		}
	}

}
