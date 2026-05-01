package ge18xx.round.action;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.AddPrivateToAuctionEffect;
import ge18xx.round.action.effects.AuctionStateChangeEffect;
import ge18xx.round.action.effects.SetInterruptedNameEffect;
import geUtilities.xml.XMLNode;

public class StartAuctionAction extends ChangeRoundAction {
	public final static String NAME = "Start Auction";

	public StartAuctionAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public StartAuctionAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " start Auction Round Action.";

		return tSimpleActionReport;
	}

	public void addAuctionStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState,
			ActorI.ActionStates aNewState) {
		AuctionStateChangeEffect tAuctionStateChangeEffect;

		tAuctionStateChangeEffect = new AuctionStateChangeEffect (aActor, aOldState, aNewState);
		addEffect (tAuctionStateChangeEffect);
	}

	public void addAddPrivateToAuctionEffect (ActorI aActor, Certificate aCertificate, 
					Certificate aFreeCertificate) {
		AddPrivateToAuctionEffect tAddPrivateToAuctionEffect;
		
		tAddPrivateToAuctionEffect = new AddPrivateToAuctionEffect (aActor, aCertificate, aFreeCertificate);
		addEffect (tAddPrivateToAuctionEffect);
	}
	
	public void addSetInterruptedRoundEffect (ActorI aActor, String aInterruptingRoundName, 
							String aInterruptedName) {
		SetInterruptedNameEffect tSetInterruptedNameEffect;
		
		tSetInterruptedNameEffect = new SetInterruptedNameEffect (aActor, aInterruptingRoundName, 
				aInterruptedName);
		addEffect (tSetInterruptedNameEffect);

	}
	
	// TODO: Consider options:
	// 1) Reverse order ALWAYS before undoing.
	// 2) Use flag on the Action that if set will reverse before undoing. This is applied when Action is created
	@Override
	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone;
		
		reverseEffects ();
		tActionUndone = super.undoAction (aRoundManager);
		reverseEffects ();
		
		return tActionUndone;
	}
}
