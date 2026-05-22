package ge18xx.bank;

import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.round.action.ActorI;

public class CorporateBank extends Bank {
	int treasury;		// Corporate Treasury that overrides the Bank Treasury
	public static final String NAME = "Corporate Bank";
	ActorI.ActorTypes actorType = ActorI.ActorTypes.CorporateBank;

	public CorporateBank (int aTreasury, GameManager aGameManager) {
		super (aTreasury, aGameManager);
		
	}

	public CorporateBank (String aCorporateBankName, GameManager aGameManager) {
		super (aCorporateBankName, aGameManager);
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
}
