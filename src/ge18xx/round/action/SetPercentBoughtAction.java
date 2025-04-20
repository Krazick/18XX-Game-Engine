package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.SetPercentBoughtEffect;
import geUtilities.xml.XMLNode;

public class SetPercentBoughtAction extends Action {
	public final static String NAME = "Set Percent Bought";
	
	public SetPercentBoughtAction (String aName) {
		super (aName);
	}

	public SetPercentBoughtAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public SetPercentBoughtAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addSetPercentBoughtEffect (ActorI aPlayer, String aAbbrev, int aPreviousPercent, int aNewPercent) {
		SetPercentBoughtEffect tSetPercentBoughtEffect;
		
		tSetPercentBoughtEffect = new SetPercentBoughtEffect (aPlayer, aAbbrev, aPreviousPercent, aNewPercent);
		addEffect (tSetPercentBoughtEffect);
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " " + NAME;

		return tSimpleActionReport;
	}
}
