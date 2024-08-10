package ge18xx.company;

import java.util.ArrayList;

import ge18xx.company.TokenInfo.TokenType;
import geUtilities.ParsingRoutineI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

public class Tokens {
	public static final ElementName EN_TOKENS = new ElementName ("Tokens");
	public static final AttributeName AN_AVAILABLE_TOKEN_COUNT = new AttributeName ("availableTokenCount");
	public static final AttributeName AN_TOKEN_INDEX = new AttributeName ("tokenIndex");
	public static final Tokens NO_TOKENS = null;
	public static final int MARKET_INDEX = 0;
	public static final int HOME1_INDEX = 1;
	public static final int HOME2_INDEX = 2;
	public static final int NO_TOKEN_INDEX = -1;
	
	ArrayList<TokenInfo> tokens;
	int startIndex;
	int nextIndex;
	
	// Methods of ArrayList of Tokens:
	// 1) GetToken (TokenType) -- Market, Home
	// 2) GetMapToken () -- No Arg, gets the first FixedCost or RangeCost that is not used
	// 3) GetLastMapToken () -- No Arg, gets the last FixedCost or RangeCost that is not used
	// 4) GetTokenCount () -- No Arg, gets the count of available MapTokens (never counts the MarketToken Type)
	// 5) AddNewToken (Token, TokenType, Cost) -- Set ArrayList Token to the provided Token
	
	/**
	 * Constructor for the Tokens ArrayList that keeps the Market Token, the Home Tokens, and the additional
	 * Tokens to be placed on RevenueCenters. These tokens with the Cost are stored in the 'TokenInfo' ArrayList
	 * The other two indexes are the StartIndex (that is set to the Token AFTER the Home Tokens) and the NextIndex 
	 * is used as new tokens are "add" by resetting those created during the construction below.
	 * This constructor just create "Empty Token Info" elements to add to the array list.
	 * 
	 * @param aTokenCount The Number of Tokens to be used on the Map (Home Tokens, one or two, and either the 
	 * 						FixedCost or RangeCost Token Types. The later method 'getTokenCount' will be one more
	 * 						since the Market Token takes Slot Zero (0) and is never placed on the Map.
	 */
	
	public Tokens (int aTokenCount) {
		int tIndex;
		int tTokenCount;
		TokenInfo tEmptyTokenInfo;
		
		tTokenCount = aTokenCount;
		tokens = new ArrayList<TokenInfo> (tTokenCount);
		tEmptyTokenInfo = new TokenInfo ();
		nextIndex = 0;
		for (tIndex = 0; tIndex < tTokenCount; tIndex++) {
			tokens.add (tEmptyTokenInfo);
		}
	}

	public void loadStatus (XMLNode aTokensNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (TokensParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aTokensNode, EN_TOKENS);
	}

	ParsingRoutineI TokensParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aTokensNode) {
			loadTokenInfo (aTokensNode);
		}
	};

	public void loadTokenInfo (XMLNode aXMLTokenInfoNode) {
		XMLNodeList tXMLTokenInfoNodeList;

		tXMLTokenInfoNodeList = new XMLNodeList (TokenInfoParsingRoutine);
		tXMLTokenInfoNodeList.parseXMLNodeList (aXMLTokenInfoNode, TokenInfo.EN_TOKEN_INFO);
	}

	ParsingRoutineI TokenInfoParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aTokenInfoNode) {
			int tTokenIndex;
			int tTokenCost;
			String tTokenType;
			boolean tTokenUsed;
			TokenInfo tTokenInfo;
			
			tTokenIndex = aTokenInfoNode.getThisIntAttribute (AN_TOKEN_INDEX);
			tTokenCost = aTokenInfoNode.getThisIntAttribute (TokenInfo.AN_AVAILABLE_TOKEN_COST);
			tTokenType = aTokenInfoNode.getThisAttribute (TokenInfo.AN_AVAILABLE_TOKEN_TYPE);
			tTokenUsed = aTokenInfoNode.getThisBooleanAttribute (TokenInfo.AN_AVAILABLE_TOKEN_USED);
			tTokenInfo = tokens.get (tTokenIndex);
			if (tTokenInfo.isMatchingTokenType (tTokenType)) {
				tTokenInfo.setUsed (tTokenUsed);
				tTokenInfo.setCost (tTokenCost);
			}
		}
	};

	public void setStartIndex (int aStartIndex) {
		startIndex = aStartIndex;
	}
	
	public void addNewToken (Token aToken, TokenType aTokenType, int aCost) {
		TokenInfo tTokenInfo;
		
		tTokenInfo = new TokenInfo (aToken, aTokenType, aCost);
		if (aTokenType == TokenType.MARKET) {
			tokens.set (MARKET_INDEX, tTokenInfo);
			setStartIndex (HOME1_INDEX);
		} else if (aTokenType == TokenType.HOME1) {
			tokens.set (HOME1_INDEX, tTokenInfo);
			setStartIndex (HOME1_INDEX + 1);
		} else if (aTokenType == TokenType.HOME2) {
			tokens.set (HOME2_INDEX, tTokenInfo);
			setStartIndex (HOME2_INDEX + 1);
		} else {
			if (nextIndex == 0) {
				nextIndex = startIndex;
			} else {
				nextIndex++;
			}
			tokens.set (nextIndex, tTokenInfo);
		}
	}
	
	public MapToken getHome1Token () {
		return getMapToken (TokenType.HOME1);
	}
	
	public MapToken getHome2Token () {
		return getMapToken (TokenType.HOME2);
	}
	
	public MapToken getMapToken () {
		return getMapToken (TokenType.MAP);
	}
	
	public Token getMarketToken () {
		return getToken (TokenType.MARKET);
	}
	
	public Token getToken (TokenType aTokenType) {
		Token tToken;
		TokenInfo tTokenInfo;
		
		tTokenInfo = TokenInfo.NO_TOKEN_INFO;
		if (aTokenType == TokenType.MARKET) {
			tTokenInfo = tokens.get (MARKET_INDEX);
		} else if (aTokenType == TokenType.HOME1) {
			tTokenInfo = tokens.get (HOME1_INDEX);
		} else if (aTokenType == TokenType.HOME2) {
			tTokenInfo = tokens.get (HOME2_INDEX);
		} else {
			tTokenInfo = getMapTokenInfo ();
		}
		if (tTokenInfo != TokenInfo.NO_TOKEN_INFO) {
			// Have to double-check the TokenInfo retrieved is the type requested
			// ie. just because HOME2 was requested, this could be a fixed or range type if corp has only one Home
			if (tTokenInfo.isMatchingTokenType (aTokenType)) {
				tToken = tTokenInfo.getToken ();
			} else { 
				tToken = Token.NO_TOKEN;
			}
		} else {
			tToken = Token.NO_TOKEN;
		}
		
		return tToken;
	}
	
	public MapToken getMapToken (TokenType aTokenType) {
		MapToken tMapToken;
		TokenInfo tTokenInfo;
		
		tTokenInfo = TokenInfo.NO_TOKEN_INFO;
		if (aTokenType == TokenType.MARKET) {
			tTokenInfo = TokenInfo.NO_TOKEN_INFO;
		} else if (aTokenType == TokenType.HOME1) {
			tTokenInfo = tokens.get (HOME1_INDEX);
		} else if (aTokenType == TokenType.HOME2) {
			tTokenInfo = tokens.get (HOME2_INDEX);
		} else {
			tTokenInfo = getMapTokenInfo ();
		}
		if (tTokenInfo != TokenInfo.NO_TOKEN_INFO) {
			// Have to double-check the TokenInfo retrieved is the type requested
			// ie. just because HOME2 was requested, this could be a fixed or range type if corp has only one Home
			if (tTokenInfo.isMatchingTokenType (aTokenType)) {
				tMapToken = tTokenInfo.getMapToken ();
			} else { 
				tMapToken = MapToken.NO_MAP_TOKEN;
			}
		} else {
			tMapToken = MapToken.NO_MAP_TOKEN;
		}
		
		return tMapToken;
	}
	
	public MapToken getLastMapToken () {
		return getLastMapToken (TokenType.MAP);
	}
	
	public MapToken getLastMapToken (TokenType aTokenType) {
		MapToken tMapToken;
		TokenInfo tLastMapTokenInfo;
		int tTokenCount;
		int tTokenIndex;
		boolean tFound;
		
		tTokenCount = getTokenCount ();
		tFound = false;
		tTokenIndex = tTokenCount - 1;
		tLastMapTokenInfo = TokenInfo.NO_TOKEN_INFO;
		while (! tFound && (tTokenIndex > 0)) {
			tLastMapTokenInfo = tokens.get (tTokenIndex);
			if (tLastMapTokenInfo.isUsed ()) {
				tTokenIndex--;
			} else {
				tFound = true;
			}
		}
		if (tLastMapTokenInfo.isMatchingTokenType (aTokenType)) {
			tMapToken = tLastMapTokenInfo.getMapToken ();
		} else {
			tMapToken = MapToken.NO_MAP_TOKEN;
		}
		
		return tMapToken;
	}
	
	public int getTokenCount () {
		return tokens.size ();
	}
	
	public int getAvailableTokenCount () {
		int tAvailableTokenCount;
		int tTokenCount;
		int tTokenIndex;
		TokenInfo tTokenInfo;
		
		tAvailableTokenCount = 0;
		tTokenCount = getTokenCount ();
		for (tTokenIndex = 0; tTokenIndex < tTokenCount; tTokenIndex++) {
			tTokenInfo = tokens.get (tTokenIndex);
			if (! tTokenInfo.isUsed ()) {
				if (tTokenInfo.isMapToken ()) {
//				if (! tTokenInfo.isHomeToken ()) {
					tAvailableTokenCount++;
				}
			}
		}
		
		return tAvailableTokenCount;
	}

	public Token getTokenAt (int aIndex) {
		TokenInfo tTokenInfo;
		Token tToken;
		
		tTokenInfo = tokens.get (aIndex);
		if (tTokenInfo != TokenInfo.NO_TOKEN_INFO) {
			tToken = tTokenInfo.getToken ();
		} else {
			tToken = Token.NO_TOKEN;
		}
		
		return tToken;
	}
	
	public int getTokenIndex (Token aToken) {
		Token tFoundToken;
		int tTokenCount;
		int tTokenIndex;
		int tFoundTokenIndex;
		
		tTokenCount = tokens.size ();
		tFoundTokenIndex = NO_TOKEN_INDEX;
		for (tTokenIndex = 0; tTokenIndex < tTokenCount; tTokenIndex++) {
			tFoundToken = getTokenAt (tTokenIndex);
			if (tFoundToken == aToken) {
				tFoundTokenIndex = tTokenIndex;
			}
		}
		
		return tFoundTokenIndex;
	}

	public TokenType getTokenType (Token aToken) {
		TokenInfo tTokenInfo;
		TokenType tTokenType;
		
		tTokenInfo = getTokenInfo (aToken);
		tTokenType = TokenInfo.NO_TOKEN_TYPE;
		if (tTokenInfo != TokenInfo.NO_TOKEN_INFO) {
			tTokenType = tTokenInfo.getTokenType ();
		}
		
		return tTokenType;
	}
	
	public TokenInfo getTokenInfo (Token aToken) {
		TokenInfo tTokenInfo;
		TokenInfo tFoundTokenInfo;
		int tIndex;
		int tTokenCount;

		tFoundTokenInfo = TokenInfo.NO_TOKEN_INFO;
		if (aToken != Token.NO_TOKEN) {
			tTokenCount = getTokenCount ();
			for (tIndex = 0; tIndex < tTokenCount; tIndex++) {
				tTokenInfo = tokens.get (tIndex);
				if (tTokenInfo.getToken () == aToken) {
					tFoundTokenInfo = tTokenInfo;
				}
			}
		}
		
		return tFoundTokenInfo;
	}
	
	public TokenInfo getMapTokenInfo () {
		TokenInfo tTokenInfo;
		TokenInfo tFoundMapTokenInfo;
		int tIndex;
		int tTokenCount;
		
		tTokenCount = getTokenCount ();
		tFoundMapTokenInfo = TokenInfo.NO_TOKEN_INFO;
		for (tIndex = startIndex; 
				((tIndex < tTokenCount) && 
				(tFoundMapTokenInfo == TokenInfo.NO_TOKEN_INFO)); tIndex++) {
			tTokenInfo = tokens.get (tIndex);
			if (! tTokenInfo.isUsed ()) {
				tFoundMapTokenInfo = tTokenInfo;
			}
		}
		
		return tFoundMapTokenInfo;
	}

	public boolean getTokenUsed (Token aToken) {
		TokenInfo tTokenInfo;
		int tIndex;
		int tTokenCount;
		boolean tTokenUsed;

		tTokenUsed = false;
		if (aToken != Token.NO_TOKEN) {
			tTokenCount = getTokenCount ();
			for (tIndex = 0; tIndex < tTokenCount; tIndex++) {
				tTokenInfo = tokens.get (tIndex);
				if (tTokenInfo.getToken () == aToken) {
					tTokenUsed = tTokenInfo.getUsed ();
				}
			}
		}
		
		return tTokenUsed;
	}
	
	public int getTokenCost (Token aToken) {
		TokenInfo tTokenInfo;
		int tIndex;
		int tTokenCount;
		int tTokenCost;

		tTokenCost = Token.NO_COST;
		if (aToken != Token.NO_TOKEN) {
			tTokenCount = getTokenCount ();
			for (tIndex = 0; tIndex < tTokenCount; tIndex++) {
				tTokenInfo = tokens.get (tIndex);
				if (tTokenInfo.getToken () == aToken) {
					tTokenCost = tTokenInfo.getCost ();
				}
			}
		}
		
		return tTokenCost;
	}

	public void printInfo () {
		int tIndex;
		int tTokenCount;
		TokenInfo tTokenInfo;
		
		tTokenCount = getTokenCount ();
		for (tIndex = 0; tIndex < tTokenCount; tIndex++) {
			tTokenInfo = tokens.get (tIndex);
			tTokenInfo.printInfo (tIndex);
		}
	}
		
	public void setTokenUsed (Token aUsedToken, boolean aUsed) {
		int tIndex;
		int tTokenCount;
		TokenInfo tTokenInfo;
		
		if (aUsedToken != Token.NO_TOKEN) {
			tTokenCount = getTokenCount ();
			for (tIndex = 0; tIndex < tTokenCount; tIndex++) {
				tTokenInfo = tokens.get (tIndex);
				if (tTokenInfo.getToken () == aUsedToken) {
					tTokenInfo.setUsed (aUsed);
				}
			}
		}
	}

	public void getTokensElement (XMLElement aXMLCorporationState, XMLDocument aXMLDocument) {
		XMLElement tTokensElement;
		XMLElement tTokenInfoElement;
		int tTokenCount;
		int tTokenIndex;
		
		tTokenCount = getTokenCount ();
		aXMLCorporationState.setAttribute (AN_AVAILABLE_TOKEN_COUNT, getTokenCount ());
		if (tTokenCount > 0) {
			tTokensElement = aXMLDocument.createElement (EN_TOKENS);
			tTokenIndex = MARKET_INDEX;
			for (TokenInfo tTokenInfo : tokens) {
				tTokenInfoElement = tTokenInfo.getTokenInfoElement (aXMLDocument);
				tTokenInfoElement.setAttribute (AN_TOKEN_INDEX, tTokenIndex);
				tTokenIndex++;
				tTokensElement.appendChild (tTokenInfoElement);
			}
			aXMLCorporationState.appendChild (tTokensElement);
		}
	}
}
