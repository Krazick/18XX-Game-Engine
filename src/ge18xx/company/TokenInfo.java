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
	
	public TokenInfo (Token aToken, TokenType aTokenType) {
		this (aToken, aTokenType, Token.NO_COST);
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
	
	public void setUsed (boolean aUsed) {
		used = aUsed;
	}

	public Token getToken () {
		return token;
	}
	
	public boolean isMapToken () {
		boolean isMapToken;
		
		if (isRangeCostToken () || isFixedCostToken () || isHomeToken ())  {
			isMapToken = true;
		} else {
			isMapToken = false;
		}
		
		return isMapToken;
	}
	
	public boolean isRangeCostToken () {
		boolean tIsRangeCostToken;
		
		if (tokenType == TokenType.RANGE_COST)  {
			tIsRangeCostToken = true;
		} else {
			tIsRangeCostToken = false;
		}
		
		return tIsRangeCostToken;
	}
	
	public boolean isFixedCostToken () {
		boolean tIsFixedCostToken;
		
		if (tokenType == TokenType.FIXED_COST)  {
			tIsFixedCostToken = true;
		} else {
			tIsFixedCostToken = false;
		}
		
		return tIsFixedCostToken;
	}

	public boolean isMarketToken () {
		boolean tIsMarketToken;
		
		if (tokenType == TokenType.MARKET)  {
			tIsMarketToken = true;
		} else {
			tIsMarketToken = false;
		}
		
		return tIsMarketToken;
	}
	
	public boolean isHomeToken () {
		boolean tIsHomeToken;
		
		if ((tokenType == TokenType.HOME1) || (tokenType == TokenType.HOME2)) {
			tIsHomeToken = true;
		} else {
			tIsHomeToken = false;
		}
		
		return tIsHomeToken;
	}
	
	public MapToken getHomeToken () {
		MapToken tHomeToken;
		
		if (isHomeToken ()) {
			tHomeToken = (MapToken) token;
		} else {
			tHomeToken = MapToken.NO_MAP_TOKEN;
		}
		
		return tHomeToken;
	}
	
	public Token getMarketToken () {
		Token tMarketToken;
		
		if (isMarketToken ()) {
			tMarketToken = token;
		} else {
			tMarketToken = Token.NO_TOKEN;
		}
		
		return tMarketToken;
	}
	
	public MapToken getMapToken () {
		MapToken tMapToken;
		
		if (isMapToken ()) {
			if (token.isAMapToken ()) {
				tMapToken = (MapToken) token;
			} else {
				tMapToken = MapToken.NO_MAP_TOKEN;
			}
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
