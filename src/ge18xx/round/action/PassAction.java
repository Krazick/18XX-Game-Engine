package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.effects.ApplyDiscountEffect;
import ge18xx.utilities.XMLNode;

public class PassAction extends ChangeStateAction {
	public final static String NAME = "Pass";

	public PassAction () {
		super ();
		setName (NAME);
	}

	public PassAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public PassAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addApplyDiscountEffect (ActorI aPlayer, String aCertificateName, int aOldDiscount, int aNewDiscount) {
		ApplyDiscountEffect tApplyDiscountEffect;

		tApplyDiscountEffect = new ApplyDiscountEffect (aPlayer, aCertificateName, aOldDiscount, aNewDiscount);
		addEffect (tApplyDiscountEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " Passed.";

		return tSimpleActionReport;
	}
	
	@Override
	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone;
		Player tCurrentPlayer;
		PlayerManager tPlayerManager;
		
		tPlayerManager = aRoundManager.getPlayerManager ();
		tCurrentPlayer = tPlayerManager.getCurrentPlayer ();
		tCurrentPlayer.hidePlayerFrame ();
		
		tActionUndone = super.undoAction (aRoundManager);

		tCurrentPlayer = tPlayerManager.getCurrentPlayer ();
		tCurrentPlayer.showPlayerFrame ();

		return tActionUndone;
	}
}