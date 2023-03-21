package ge18xx.player;

import java.util.Arrays;

import ge18xx.bank.Bank;

public class RoundDividends {

	int maxRoundID;
	int currentRoundIndex;
	int dividends [];
	
	public RoundDividends (int aMaxRoundID) {
		maxRoundID = aMaxRoundID;
		dividends = new int [maxRoundID];
		clearDividends ();
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
