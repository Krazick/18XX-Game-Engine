package ge18xx.company;

import java.util.ArrayList;

import ge18xx.company.TokenInfo.TokenType;

public class Tokens {
	private static int MARKET_INDEX = 0;
	private static int HOME1_INDEX = 1;
	private static int HOME2_INDEX = 2;
	
	ArrayList<TokenInfo> tokens;
	int startIndex;
	int nextIndex;
	
	// Methods of ArrayList of Tokens:
	// 1) GetToken (TokenType) -- Market, Home
	// 2) GetMapToken () -- No Arg, gets the first FixedCost or RangeCost that is not used
	// 3) GetLastMapToken () -- No Arg, gets the last FixedCost or RangeCost that is not used
	// 4) GetTokenCount () -- No Arg, gets the count of available MapTokens (never counts the MarketToken Type)
	// 5) AddNewToken (Token, TokenType, Cost) -- Set ArrayList Token to the provided Token
	
	public Tokens (int aTokenCount) {
		int tIndex;
		int tTokenCount;
		TokenInfo tEmptyTokenInfo;
		
		tTokenCount = aTokenCount + 1;
		tokens = new ArrayList<TokenInfo> (tTokenCount);
		tEmptyTokenInfo = new TokenInfo ();
		nextIndex = 0;
		for (tIndex = 0; tIndex < tTokenCount; tIndex++) {
			tokens.add (tEmptyTokenInfo);
		}
	}

	public void addNewToken (Token aToken, TokenType aTokenType, int aCost) {
		TokenInfo tTokenInfo;
		
		tTokenInfo = new TokenInfo (aToken, aTokenType, aCost);
		if (aTokenType == TokenType.MARKET) {
			tokens.set (MARKET_INDEX, tTokenInfo);
		} else if (aTokenType == TokenType.HOME1) {
			tokens.set (HOME1_INDEX, tTokenInfo);
			startIndex = HOME1_INDEX + 1;
		} else if (aTokenType == TokenType.HOME2) {
			tokens.set (HOME2_INDEX, tTokenInfo);
			startIndex = HOME2_INDEX + 1;
		} else {
			if (nextIndex == 0) {
				nextIndex = startIndex;
			} else {
				nextIndex++;
			}
			tokens.set (nextIndex, tTokenInfo);
		}
	}
	
	public Token getToken (TokenType aTokenType) {
		Token tToken;
		TokenInfo tTokenInfo;
		
		tToken = Token.NO_TOKEN;
		tTokenInfo = TokenInfo.NO_TOKEN_INFO;
		if (aTokenType == TokenType.MARKET) {
			tTokenInfo = tokens.get (MARKET_INDEX);
		} else if (aTokenType == TokenType.HOME1) {
			tTokenInfo = tokens.get (HOME1_INDEX);
		} else if (aTokenType == TokenType.HOME2) {
			tTokenInfo = tokens.get (HOME2_INDEX);
		} else {
			tToken = getMapToken ();
		}
		if (tTokenInfo != TokenInfo.NO_TOKEN_INFO) {
			tToken = tTokenInfo.getToken ();
		}			
		
		return tToken;
	}
	
	public int getTokenCount () {
		return tokens.size ();
	}
	
	public Token getMapToken () {
		MapToken tMapToken;
		TokenInfo tTokenInfo;
		int tIndex;
		int tTokenCount;
		
		tTokenCount = getTokenCount ();
		tMapToken = MapToken.NO_MAP_TOKEN;
		for (tIndex = startIndex; tIndex < tTokenCount; tIndex++) {
			tTokenInfo = tokens.get (tIndex);
			if (! tTokenInfo.isUsed ()) {
				tMapToken = tTokenInfo.getMapToken ();
			}
		}
		
		return tMapToken;
	}
}
