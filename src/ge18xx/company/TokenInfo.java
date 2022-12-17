package ge18xx.company;

public class TokenInfo {
	// Create new Class 'TokenInfo' that has:
	// 1) Token (can be sub-Class MapToken)
	// 2) Type [an ENUM] (Market, Home1, Home2, FixedCost, RangeCost
	// 3) Cost (if RangeCost Type, value -1, otherwise the cost)
	// 4) Used (Boolean to mark if used or not)
	// Store in an ArrayList, with a fixed number of entries, based on how many tokens the Company has

	public static TokenInfo NO_TOKEN_INFO = null;
	public enum TokenType {
		MARKET, HOME1, HOME2, FIXED_COST, RANGE_COST, NO_TYPE
	}

	Token token;
	TokenType tokenType;
	int cost;
	boolean used;
	
	public TokenInfo () {
		this (Token.NO_TOKEN, TokenType.NO_TYPE, 0);
	}
	
	public TokenInfo (Token aToken, TokenType aTokenType, int aCost) {
		setToken (aToken);
		setTokenType (aTokenType);
		setCost (aCost);
		setUsed (false);
	}

	private void setToken (Token aToken) {
		token = aToken;
	}
	
	private void setTokenType (TokenType aTokenType) {
		tokenType = aTokenType;
	}
	
	private void setCost (int aCost) {
		cost = aCost;
	}
	
	private void setUsed (boolean aUsed) {
		used = aUsed;
	}


	public Token getToken () {
		return token;
	}
	
	public MapToken getMapToken () {
		MapToken tMapToken;
		
		if (token.isAMapToken ()) {
			tMapToken = (MapToken) token;
		} else {
			tMapToken = MapToken.NO_MAP_TOKEN;
		}
		
		return tMapToken;
	}
	
	public TokenType getTokenType () {
		return tokenType;
	}
	
	public int getCost () {
		return cost;
	}
	
	public boolean getUsed () {
		return used;
	}

	public boolean isUsed () {
		return used;
	}
}
