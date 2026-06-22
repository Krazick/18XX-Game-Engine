package ge18xx.player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;

public class ContractBid {
	public static final ContractBid NO_CONTRACT_BID = null;
	public static final int NO_EXTRA_BOND = 0;
	Player player;
	List<ContractLine> contractLines;
	boolean signed;
	boolean fullfilled;
	int extraForBond;
	
	public ContractBid (Player aPlayer) {
		setPlayer (aPlayer);
		setSigned (false);
		setFullfilled (false);
		setExtraForBond (NO_EXTRA_BOND);
		contractLines = new LinkedList<> ();
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
	
	public int getCount () {
		return contractLines.size ();
	}
	
	public int getTotalValue () {
		int tTotalValue;
		
		tTotalValue = extraForBond;
		for (ContractLine tContractLine : contractLines) {
			tTotalValue += tContractLine.getBond ();
		}
		return tTotalValue;
	}
	
	public JLabel buildLabel () {
		JLabel tJLabel;
		String tText;
		
		tText = "Contract Bid: ";
		if (isFullfilled ()) {
			tText += "Fulfilled";
		} else if (isSigned ()) {
			tText += "Signed " + getCount () + "/" + Bank.formatCash (getTotalValue ());
		} else {
			tText += "Unsigned";
		}
		
		tJLabel = new JLabel (tText);

		return tJLabel;
	}
	
	public void addContractLine (ContractLine aContractLine) {
//		aContractLine.setOwner (this);
//		holder.updateListeners (CERTIFICATE_ADDED + " to " + holder.getName ());
		contractLines.add (aContractLine);
//		Collections.sort (contractLines);
	} 
}
