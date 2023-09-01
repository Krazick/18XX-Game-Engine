package ge18xx.company.special;

import ge18xx.game.GameManager;

public class LoanRepayment extends TriggerClass {

	public LoanRepayment (GameManager aGameManager) {
		System.out.println ("Initiate Loan Repayment Game: " + aGameManager.getActiveGameName ());
	}

}
