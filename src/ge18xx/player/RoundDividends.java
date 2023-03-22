package ge18xx.player;

import java.util.Arrays;

import ge18xx.bank.Bank;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class RoundDividends {
	public final static AttributeName AN_DIVIDENDS = new AttributeName ("dividends");
	public final static AttributeName AN_DIVIDEND_COUNT = new AttributeName ("dividendCount");

	int maxRoundID;
	int currentRoundIndex;
	int dividends [];
	
	public RoundDividends (int aMaxRoundID) {
		maxRoundID = aMaxRoundID;
		dividends = new int [maxRoundID];
		clearDividends ();
	}
	
	public void parseDividendAtribute (XMLNode aPlayerNode) {
		String tDividends;
		String tAllDividends [];
		int tDividendCount;
		int tIndex;
		int tValue;
		
		tDividends = aPlayerNode.getThisAttribute (AN_DIVIDENDS);
		if (tDividends != GUI.NULL_STRING) {
			tDividendCount = aPlayerNode.getThisIntAttribute (AN_DIVIDEND_COUNT);
			tAllDividends = tDividends.split (GUI.SPLIT);
			clearDividends ();
			if (tAllDividends.length > 0) {
				for (tIndex = 0; tIndex < tDividendCount; tIndex++) {
					tValue = Integer.parseInt (tAllDividends [tIndex]);
					addDividend ((tIndex + 1), tValue);
				}
			}
		}
	}
	
	public void addDividendAttribute (XMLElement aXMLElement, int aOperatingRoundCount) {
		String tDividendAttribute;
		int tIndex;
		
		tDividendAttribute = GUI.EMPTY_STRING;
		for (tIndex = 1; tIndex <= aOperatingRoundCount; tIndex++) {
			if (tDividendAttribute != GUI.EMPTY_STRING) {
				tDividendAttribute += GUI.SPLIT;
			}
			tDividendAttribute += getDividends (tIndex);
		}
		aXMLElement.setAttribute (AN_DIVIDEND_COUNT, aOperatingRoundCount);
		aXMLElement.setAttribute (AN_DIVIDENDS, tDividendAttribute);
	}

	public void clearDividends () {
		Arrays.fill (dividends, 0);
	}
	
	private int realIndex (int aRoundID) {
		return (aRoundID - 1);
	}
	
	public void clearDividends (int aRoundID) {
		dividends [realIndex (aRoundID)] = 0;
	}
	
	public void addDividend (int aRoundID, int aAmount) {
		dividends [realIndex (aRoundID)] += aAmount;
	}
	
	public int getDividends (int aRoundID) {
		return dividends [realIndex (aRoundID)];
	}
	
	public String getFormattedDividends (int aRoundID) {
		return Bank.formatCash (dividends [realIndex (aRoundID)]);
	}
	
	public int sumAllDividends () {
		int tTotal;
		
		tTotal = 0;
		for (int tAmount : dividends) {
			tTotal += tAmount;
		}
		
		return tTotal;
	}
	
	public String getRoundDividends (int aRoundID) {
		String tRoundDividends;
		
		tRoundDividends = "[ " + aRoundID + ": " + getFormattedDividends (aRoundID) + " ]";
		
		return tRoundDividends;
	}
	
	public String getAllRoundsDividends () {
		return getAllRoundsDividends (maxRoundID);
	}
	
	public String getAllRoundsDividends (int aOperatingRoundCount) {
		String tAllRoundsDividends;
		int tRoundIndex;
		
		tAllRoundsDividends = "";
		for (tRoundIndex = 1; tRoundIndex <= aOperatingRoundCount; tRoundIndex++) {
			tAllRoundsDividends += getRoundDividends (tRoundIndex) + " ";
		}
		return tAllRoundsDividends;
	}
}
