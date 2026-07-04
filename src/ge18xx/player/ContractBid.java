package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import ge18xx.bank.Bank;
import ge18xx.center.City;

public class ContractBid {
	public static final ContractBid NO_CONTRACT_BID = null;
	public static final int NO_EXTRA_BOND = 0;
	public static final int DELTA_CITY_MAX_COUNT = 2;
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
	
	public int getCityCount () {
		return contractLines.size ();
	}
	
	public int getDeltaCityCount () {
		int tDeltaCityCount;
		
		tDeltaCityCount = 0;
		for (ContractLine tContractLine : contractLines) {
			if (tContractLine.isDeltaTerrain ()) {
				tDeltaCityCount++;
			}
		}

		return tDeltaCityCount;
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
			tText += "Signed " + getCityCount () + "/" + Bank.formatCash (getTotalValue ());
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
		if (getCityCount () > 0) {
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

	private boolean allContractLinesAreValid () {
		boolean tAllContractLinesAreValid;
		
		tAllContractLinesAreValid = true;
		for (ContractLine tContractLine : contractLines) {
			if (! tContractLine.isValidContractLine ()) {
				tAllContractLinesAreValid = false;
			}
		}

		return tAllContractLinesAreValid;
	}

	public boolean isValid () {
		boolean tIsValid;
		
		tIsValid = true;
		
		if (getCityCount () < player.getMinBidCities ()) {
			tIsValid = false;
		} else if (getCityCount () > player.getMaxBidCities ()) {
			tIsValid = false;
		} else if (getDeltaCityCount () > DELTA_CITY_MAX_COUNT) {
			tIsValid = false;
		} else if (player.getCash () < getTotalValue ()) {
			tIsValid = false;
		} else if (! allContractLinesAreValid ()) {
			tIsValid = false;
		}
		
		return tIsValid;
	}
	
	// New Methods to add:
	// isValid -- To verify if the entire contractBid is correct and can be signed:
	//		* ContractLines have at least the minimum required cities
	//		* ContractLines has less than or equal to the maximum number of cities
	//		* ContractLines has 0-2 Cities in the Delta (Calcutta is in the Delta), 
	//		* Player has sufficient cash to be added to the ContractBid Escrow
	//		* ContractLines are all Valid
	// SaveContractBid -- Will generate XML to add to Save Game File
	// ParseContractBid -- Will parse the XML from the Save Game File
	// GenerateActionEffects -- Will generate the Action with Effects XML of the ContractBid
	// ParseActionEffects -- Will parse the Action with Effects XML of the ContractBid
}
