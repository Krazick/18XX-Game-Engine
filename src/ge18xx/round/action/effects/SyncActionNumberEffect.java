package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;

public class SyncActionNumberEffect extends Effect {
	public static final AttributeName AN_NEW_ACTION_NUMBER = new AttributeName ("newActionNumber");
	public static final String NAME = "Sync Action Number";
	int newActionNumber;

	public SyncActionNumberEffect () {
		super (NAME);
	}

	public SyncActionNumberEffect (String aName) {
		super (aName);
		setName (NAME);
	}

	public SyncActionNumberEffect (ActorI aActor, int aNewActionNumber) {
		super (NAME, aActor);
		setNewActionNumber (aNewActionNumber);
	}

	public SyncActionNumberEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tNewActionNumber;

		tNewActionNumber = aEffectNode.getThisIntAttribute (AN_NEW_ACTION_NUMBER);
		setNewActionNumber (tNewActionNumber);
	}

	private void setNewActionNumber (int aNewActionNumber) {
		newActionNumber = aNewActionNumber;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " to " + newActionNumber + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		aRoundManager.setActionNumber (newActionNumber);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
