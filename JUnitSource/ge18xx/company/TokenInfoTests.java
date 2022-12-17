package ge18xx.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ge18xx.company.TokenInfo.TokenType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName ("Token Info Tests")
class TokenInfoTests {
	TokenInfo tokenInfo;
	CompanyTestFactory companyTestFactory;
	Token mToken;
	MapToken mMapToken;
	
	@BeforeEach
	void setUp () throws Exception {
		companyTestFactory = new CompanyTestFactory ();
		mToken = companyTestFactory.buildTokenMock ();
		mMapToken = companyTestFactory.buildMapTokenMock ();
		tokenInfo = new TokenInfo (mToken, TokenInfo.TokenType.MARKET, 0);
	}

	@Nested
	@DisplayName ("TokenInfo IS ... TOKEN Tests")
	class TestTokenInfoCreation {
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
		}
	
		@Test
		@DisplayName ("With Mocked Token and HOME1 TokenType")
		void basicConstructorHome1TokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mMapToken, TokenType.HOME1, 0);
	
			assertFalse (tTokenInfo.isMarketToken ());
			assertTrue (tTokenInfo.isHomeToken ());
			assertFalse (tTokenInfo.isFixedCostToken ());
			assertFalse (tTokenInfo.isRangeCostToken ());
			assertTrue (tTokenInfo.isMapToken ());
			assertEquals (0, tTokenInfo.getCost ());
			assertEquals (TokenType.HOME1, tTokenInfo.getTokenType ());
		}
	
		@Test
		@DisplayName ("With Mocked Token and HOME2 TokenType")
		void basicConstructorHome2TokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mMapToken, TokenType.HOME2, 0);
	
			assertFalse (tTokenInfo.isMarketToken ());
			assertTrue (tTokenInfo.isHomeToken ());
			assertFalse (tTokenInfo.isFixedCostToken ());
			assertFalse (tTokenInfo.isRangeCostToken ());
			assertTrue (tTokenInfo.isMapToken ());
			assertEquals (0, tTokenInfo.getCost ());
			assertEquals (TokenType.HOME2, tTokenInfo.getTokenType ());
		}
	
		@Test
		@DisplayName ("With Mocked Token and FIXED COST TokenType")
		void basicConstructorFixedCostdTokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mMapToken, TokenType.FIXED_COST, 40);
	
			assertFalse (tTokenInfo.isMarketToken ());
			assertFalse (tTokenInfo.isHomeToken ());
			assertTrue (tTokenInfo.isFixedCostToken ());
			assertFalse (tTokenInfo.isRangeCostToken ());
			assertTrue (tTokenInfo.isMapToken ());
			assertEquals (40, tTokenInfo.getCost ());
			assertEquals (TokenType.FIXED_COST, tTokenInfo.getTokenType ());
		}
	
		@Test
		@DisplayName ("With Mocked Token and RANGE COST TokenType")
		void basicConstructorRangeCostTokenTest () {
			TokenInfo tTokenInfo;
	
			tTokenInfo = new TokenInfo (mMapToken, TokenType.RANGE_COST, 0);
	
			assertFalse (tTokenInfo.isMarketToken ());
			assertFalse (tTokenInfo.isHomeToken ());
			assertFalse (tTokenInfo.isFixedCostToken ());
			assertTrue (tTokenInfo.isRangeCostToken ());
			assertTrue (tTokenInfo.isMapToken ());
			assertEquals (0, tTokenInfo.getCost ());
			assertEquals (TokenType.RANGE_COST, tTokenInfo.getTokenType ());
		}
	}
}
