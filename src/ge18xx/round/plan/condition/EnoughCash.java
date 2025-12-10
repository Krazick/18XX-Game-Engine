package ge18xx.round.plan.condition;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;

public class EnoughCash extends CorporationExists {
	public static final String NAME = "Enough Cash";
	int amountNeeded;
	
	public EnoughCash (int aAmountNeeded, Corporation aCorporation) {
		super (NAME, aCorporation);
		setAmountNeeded (aAmountNeeded);
	}

	public void setAmountNeeded (int aAmountNeeded) {
		amountNeeded = aAmountNeeded;
	}
	
	public int getAmountNeeded () {
		return amountNeeded;
	}
	
	@Override
	public boolean meets () {
		boolean tMeets;
		
		if (super.meets ()) {
			if (corporation.isATrainCompany ()) {
				if (corporation.getCash () >= amountNeeded) {
					tMeets = MEETS;
				} else {
					tMeets = FAILS;
				}
			} else {
				tMeets = FAILS;
			}
		} else {
			tMeets = FAILS;
		}
		
		return tMeets;
	}
	

	@Override
	public String getReport () {
		String tReport;
		
		tReport = super.getReport () + " (" + Bank.formatCash (corporation.getCash ()) + 
										" >= " + Bank.formatCash (amountNeeded) + ")";
		tReport = appendStatus (tReport);
		
		return tReport;
	}

}
