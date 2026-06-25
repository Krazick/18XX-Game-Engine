package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import ge18xx.bank.Bank;
import ge18xx.center.City;

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
	
	public boolean cityAlreadyInContractLines (String aCityName) {
		boolean tCityAlreadyInContractLines;
		String tContractCityName;
		
		tCityAlreadyInContractLines = false;
		if (getCount () > 0) {
			for (ContractLine tContractLine : contractLines) {
				tContractCityName = tContractLine.getCityName ();
				if (tContractCityName.equals (aCityName)) {
					tCityAlreadyInContractLines = true;
				}
			}
		}
		
		return tCityAlreadyInContractLines;
	}
	
	public void addContractLine (ContractLine aContractLine) {
		String tNewCityName;
		
		tNewCityName = aContractLine.getCityName ();
		if (! cityAlreadyInContractLines (tNewCityName)) {
			contractLines.add (aContractLine);
			System.out.println ("Adding City named " + tNewCityName);
		}
	} 
	
	public void deleteContractLine (City aCity) {
		String tCityNameToDelete;
		String tContractCityName;
		ContractLine tContractLineToDelete;
		
		if (aCity != City.NO_CITY) {
			tCityNameToDelete = aCity.getCityName ();
			System.out.println ("Want to Remove City named " + tCityNameToDelete);
		
			if (cityAlreadyInContractLines (tCityNameToDelete)) {
				tContractLineToDelete = ContractLine.NO_CONTRACT_LINE;
				for (ContractLine tContractLine : contractLines) {
					tContractCityName = tContractLine.getCityName ();
					if (tContractCityName.equals (tCityNameToDelete)) {
						tContractLineToDelete = tContractLine;
					}
				}
				contractLines.remove (tContractLineToDelete);
				System.out.println ("Removing City named " + tCityNameToDelete);
			}	
		}
	}
}
