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
	
	public void addDividend (int aRoundID, int aAmount) {
		dividends [aRoundID - 1] += aAmount;
	}
	
	public int getDividends (int aRoundID) {
		return dividends [aRoundID];
	}
	
	public String getFormattedDividends (int aRoundID) {
		return Bank.formatCash (dividends [aRoundID]);
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
		
		tRoundDividends = "[ " + (aRoundID + 1) + ": " + getFormattedDividends (aRoundID) + " ]";
		
		return tRoundDividends;
	}
	
	public String getAllRoundsDividends () {
		String tAllRoundsDividends;
		int tRoundIndex;
		
		tAllRoundsDividends = "";
		for (tRoundIndex = 0; tRoundIndex < maxRoundID; tRoundIndex++) {
			tAllRoundsDividends += getRoundDividends (tRoundIndex) + " ";
		}
		return tAllRoundsDividends;
	}
}
