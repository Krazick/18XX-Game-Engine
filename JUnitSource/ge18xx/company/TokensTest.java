package ge18xx.company;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokensTest {
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

	@Test
	@DisplayName ("Adding Market Token Test") 
	void addMarketTokenTest () {
		Token tToken;
		Token tFoundToken;
		
		tToken = companyTestFactory.buildToken ();
		tokens.addNewToken (tToken, TokenInfo.TokenType.MARKET, 0);
		
		assertEquals (5, tokens.getTokenCount ());
		tFoundToken = tokens.getToken (TokenInfo.TokenType.MARKET);
		assertEquals (tToken, tFoundToken);
		tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME1);
		assertNotEquals (tToken, tFoundToken);
		tFoundToken = tokens.getToken (TokenInfo.TokenType.HOME2);
		assertNotEquals (tToken, tFoundToken);
		tFoundToken = tokens.getToken (TokenInfo.TokenType.FIXED_COST);
		assertNotEquals (tToken, tFoundToken);
		tFoundToken = tokens.getToken (TokenInfo.TokenType.RANGE_COST);
		assertNotEquals (tToken, tFoundToken);
	}
}
