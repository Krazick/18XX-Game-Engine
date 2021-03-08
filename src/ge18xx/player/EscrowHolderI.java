package ge18xx.player;

import ge18xx.bank.Bank;

public interface EscrowHolderI extends CashHolderI {

	Bank getBank();
	void removeEscrow (Escrow escrow, boolean escrowCloseMatch);
}
