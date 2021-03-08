package ge18xx.player;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;

public interface EscrowHolderI extends CashHolderI {

	Bank getBank();
	void removeEscrow (Escrow escrow, boolean escrowCloseMatch);
	Escrow addEscrowInfo (Certificate tCertificate, int tCash);
	void printAllEscrows ();
}
