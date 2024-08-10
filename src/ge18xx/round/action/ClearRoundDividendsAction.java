package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ClearRoundDividendEffect;
import geUtilities.xml.XMLNode;

public class ClearRoundDividendsAction extends Action {
	public final static String NAME = "Clear Round Dividends";
	int aPreviousAount;
	
	public ClearRoundDividendsAction (String aName) {
		super (aName);
	}

	public ClearRoundDividendsAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public ClearRoundDividendsAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addClearRoundDividendEffect (ActorI aPlayer, int aPreviousAmount, int aOperatingRoundID) {
		ClearRoundDividendEffect tClearRoundDividendEffect;
		
		tClearRoundDividendEffect = new ClearRoundDividendEffect (aPlayer, aPreviousAmount, aOperatingRoundID);
		addEffect (tClearRoundDividendEffect);
	}
}
