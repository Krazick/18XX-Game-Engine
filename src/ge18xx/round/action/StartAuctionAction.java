package ge18xx.round.action;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.AddPrivateToAuctionEffect;
import ge18xx.round.action.effects.AuctionStateChangeEffect;
import ge18xx.round.action.effects.SetInterruptionStartedEffect;
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
	
	public void addSetInterruptionRoundStartedEffect (ActorI aActor, boolean aSetInterruptionStarted) {
		SetInterruptionStartedEffect tSetInterruptionStartedEffect;

		tSetInterruptionStartedEffect = new SetInterruptionStartedEffect (aActor, aSetInterruptionStarted);
		addEffect (tSetInterruptionStartedEffect);
	}
}
