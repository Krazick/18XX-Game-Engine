package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ChangeMaxORCountEffect extends Effect {
	public final static String NAME = "Change Max OR Count";
	final static AttributeName AN_OLD_MAX_OR_COUNT = new AttributeName ("oldMaxOR");
	final static AttributeName AN_NEW_MAX_OR_COUNT = new AttributeName ("newMaxOR");
	public final static int NO_MAX_OR_COUNT = -1;
	int oldMaxORCount;
	int newMaxORCount;

	public ChangeMaxORCountEffect (ActorI aActor, int aOldMaxORCount, int aNewMaxORCount) {
		super (NAME, aActor);
		setOldMaxORCount (aOldMaxORCount);
		setNewMaxORCount (aNewMaxORCount);
	}
	
	public void setOldMaxORCount (int aOldMaxORCount) {
		oldMaxORCount = aOldMaxORCount;
	}
	
	public void setNewMaxORCount (int aNewMaxORCount) {
		newMaxORCount = aNewMaxORCount;
	}
	public ChangeMaxORCountEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		int tOldMaxORCount;
		int tNewMaxORCount;

		tOldMaxORCount = aEffectNode.getThisIntAttribute (AN_OLD_MAX_OR_COUNT);
		tNewMaxORCount = aEffectNode.getThisIntAttribute (AN_NEW_MAX_OR_COUNT);
		setOldMaxORCount (tOldMaxORCount);
		setNewMaxORCount (tNewMaxORCount);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_OLD_MAX_OR_COUNT, oldMaxORCount);
		tEffectElement.setAttribute (AN_NEW_MAX_OR_COUNT, newMaxORCount);

		return tEffectElement;
	}

	public int getOldMaxORCount () {
		return oldMaxORCount;
	}

	public int getNewMaxORCount () {
		return newMaxORCount;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;

		tReport = REPORT_PREFIX + name + " from " + oldMaxORCount + " to " +  newMaxORCount + ".";
		
		return tReport;
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = false;
		if (actor.isAOperatingRound ()) {
			aRoundManager.setOperatingRoundCount (newMaxORCount);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("Actor " + actor.getName () + " is not a Operating Round.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = false;
		if (actor.isAOperatingRound ()) {
			aRoundManager.setOperatingRoundCount (oldMaxORCount);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("Actor " + actor.getName () + " is not a Operating Round.");
		}

		return tEffectUndone;
	}

}
