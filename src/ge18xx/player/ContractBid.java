package ge18xx.player;

import javax.swing.JLabel;

import ge18xx.bank.Bank;

public class ContractBid {
	public static final ContractBid NO_CONTRACT_BID = null;
	public static final int NO_EXTRA_BOND = 0;
	Player player;
	boolean signed;
	boolean fullfilled;
	int extraForBond;
	int count;
	int totalValue;
	
	public ContractBid (Player aPlayer) {
		setPlayer (aPlayer);
		setSigned (false);
		setFullfilled (false);
		setExtraForBond (NO_EXTRA_BOND);
		setCount (0);
		setTotalValue (0);
	}

	public void setSigned (boolean aSigned) {
		signed = aSigned;
	}

	public void setPlayer (Player aPlayer) {
		player = aPlayer;
	}
	
	public boolean isSigned () {
		return signed;
	}
	
	public void setFullfilled (boolean aFullfilled) {
		fullfilled = aFullfilled;
	}
	
	public boolean isFullfilled () {
		return fullfilled;
	}
	
	public void setExtraForBond (int aExtraForBond) {
		extraForBond = aExtraForBond;
	}
	
	public int getExtraForBond () {
		return extraForBond;
	}
	
	public void setCount (int aCount) {
		count = aCount;
	}
	
	public void incrementCount () {
		count++;
	}
	
	public void decrementCount () {
		count--;
	}
	
	public int getCount () {
		return count;
	}
	
	public void setTotalValue (int aTotalValue) {
		totalValue = aTotalValue;
	}
	
	public int getTotalValue () {
		return totalValue;
	}
	
	public JLabel buildLabel () {
		JLabel tJLabel;
		String tText;
		
		tText = "Contract Bid: ";
		if (isFullfilled ()) {
			tText += "Fulfilled";
		} else if (isSigned ()) {
			tText += "Signed " + count + "/" + Bank.formatCash (totalValue);
		} else {
			tText += "Unsigned";
		}
		
		tJLabel = new JLabel (tText);

		return tJLabel;
	}
}
