package ge18xx.company;

import ge18xx.bank.Bank;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;

public class TokenInfo {
	public static final ElementName EN_TOKEN_INFO = new ElementName ("TokenInfo");
	public static final AttributeName AN_AVAILABLE_TOKEN_TYPE = new AttributeName ("tokenType");
	public static final AttributeName AN_AVAILABLE_TOKEN_COST = new AttributeName ("cost");
	public static final AttributeName AN_AVAILABLE_TOKEN_USED = new AttributeName ("used");
	public static final TokenInfo NO_TOKEN_INFO = null;
	public static final TokenType NO_TOKEN_TYPE = null;

	// Create new Class 'TokenInfo' that has:
	// 1) Token (can be sub-Class MapToken)
	// 2) Type [an ENUM] (Market, Home1, Home2, FixedCost, RangeCost
	// 3) Cost (if RangeCost Type, value -1, otherwise the cost)
	// 4) Used (Boolean to mark if used or not)
	// Store in an ArrayList, with a fixed number of entries, based on how many tokens the Company has

	public enum TokenType {
		MARKET ("Market"), 
		HOME1 ("Home1"), 
		HOME2 ("Home2"), 
		FIXED_COST ("FixedCost"),
		RANGE_COST ("RangeCost"), 
		NO_TYPE ("NoType"), 
		MAP ("Map");
		
		private String enumString;
		
		TokenType (String aEnumString) {
			enumString = aEnumString;
		}
		
		@Override
		public String toString () {
			return enumString;
		}
	}

	Token token;
	TokenType tokenType;
	int cost;
	boolean used;
	
	public TokenInfo () {
		this (Token.NO_TOKEN, TokenType.NO_TYPE);
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
	
	public void printInfo (int aIndex) {
		String tTokenInfo;
		String tTokenTypeInfo;
		
		tTokenInfo = GUI.EMPTY_STRING;
		if (token == Token.NO_TOKEN) {
			tTokenInfo = "No Token";
		} else {
			tTokenInfo = token.getInfo ();
		}
		tTokenTypeInfo = GUI.EMPTY_STRING;
		if (tokenType == TokenType.NO_TYPE) {
			tTokenTypeInfo = "No TokenInfo";
		} else {
			tTokenTypeInfo = tokenType.toString ();
		}
		System.out.println (aIndex + ": " + tTokenInfo + " " + tTokenTypeInfo + " Cost: " + 
							Bank.formatCash (cost) + " Used: " + used);
	}

	private void setToken (Token aToken) {
		token = aToken;
	}
	
	private void setTokenType (TokenType aTokenType) {
		tokenType = aTokenType;
	}
	
	public void setCost (int aCost) {
		cost = aCost;
	}
	
	public void setUsed (boolean aUsed) {
		used = aUsed;
	}

	public Token getToken () {
		return token;
	}
	
	public boolean isMatchingTokenType (String aTokenType) {
		boolean tIsMatchingTokenType;
		String tThisType;
		
		tThisType = tokenType.toString ();
		if (tThisType.equals (aTokenType)){
			tIsMatchingTokenType = true;
		} else {
			tIsMatchingTokenType = false;
		}
		
		return tIsMatchingTokenType;		
	}
	
	public boolean isMatchingTokenType (TokenType aRequestedTokenType) {
		boolean tIsMatchingTokenType;
		
		if (tokenType == aRequestedTokenType) {
			tIsMatchingTokenType = true;
		} else if (aRequestedTokenType == TokenType.MAP){
			tIsMatchingTokenType = isMapToken ();
		} else {
			tIsMatchingTokenType = false;
		}
		
		return tIsMatchingTokenType;
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
		int tCost;
		
		if (isRangeCostToken ()) {
			tCost = Token.RANGE_COST;
		} else {
			tCost = cost;
		}
		
		return tCost;
	}
	
	public boolean getUsed () {
		return used;
	}

	public boolean isUsed () {
		return used;
	}

	public XMLElement getTokenInfoElement (XMLDocument aXMLDocument) {
		XMLElement tTokenInfoElement;
		
		tTokenInfoElement = aXMLDocument.createElement (EN_TOKEN_INFO);
		tTokenInfoElement.setAttribute (AN_AVAILABLE_TOKEN_TYPE, tokenType.toString ());
		tTokenInfoElement.setAttribute (AN_AVAILABLE_TOKEN_COST, getCost ());
		tTokenInfoElement.setAttribute (AN_AVAILABLE_TOKEN_USED, getUsed ());
		
		return tTokenInfoElement;
	}
	
}
