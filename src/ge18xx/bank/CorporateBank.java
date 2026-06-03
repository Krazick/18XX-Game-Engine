package ge18xx.bank;

import javax.swing.JLabel;

import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.action.ActorI;
import geUtilities.GUI;

public class CorporateBank extends Bank {
	public static final String NAME = "Corporate Bank";
	public static final String BANK_LABEL_PREFIX = "Remaining Corpoate Bank Cash ";
	public static final CorporateBank NO_CORPORATE_BANK = null;
	ActorI.ActorTypes actorType = ActorI.ActorTypes.CorporateBank;
	int treasury;		// Corporate Treasury that overrides the Bank Treasury

	public CorporateBank (int aTreasury, GameManager aGameManager) {
		super (NAME, aTreasury, aGameManager);
	}

	public CorporateBank (String aCorporateBankName, GameManager aGameManager) {
		super (aCorporateBankName, NO_BANK_CASH, aGameManager);
	}

	@Override
	public void setTreasury (int aTreasury) {
		treasury = aTreasury;
	}

	@Override
	public void addCash (int aAmount) {
		treasury += aAmount;
		updateBankCashLabel ();
		if (aAmount < 0) {
			if (treasury < 0) {
				gameManager.updateRoundFrame ();
			}
		}
		updateListeners (BANK_CASH_CHANGED + " by " + aAmount);
	}

	@Override
	public void transferCashTo (CashHolderI aToHolder, int aAmount) {
		aToHolder.addCash (aAmount);
		addCash (-aAmount);
		updateBankCashLabel ();
	}
	
	@Override
	public int getCash () {
		return treasury;
	}
	
	@Override
	public boolean isCorporateBank () {
		return true;
	}

	@Override
	public JLabel getBankCashLabel () {
		return bankCashLabel;
	}

	@Override
	public void updateBankCashLabel () {
		String tBankLabel;

		tBankLabel = BANK_LABEL_PREFIX + Bank.formatCash (getCash ());
		if (bankCashLabel == GUI.NO_LABEL) {
			bankCashLabel = new JLabel (tBankLabel);
		} else {
			bankCashLabel.setText (tBankLabel);
		}
	}
}
