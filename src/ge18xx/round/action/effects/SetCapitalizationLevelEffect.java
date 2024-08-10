package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.Capitalization;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetCapitalizationLevelEffect extends Effect {
	public static final String NAME = "Set Capitalization Level";
	public static final AttributeName AN_OLD_CAPITALIZATION_LEVEL = new AttributeName ("oldCapitalizationLevel");
	public static final AttributeName AN_NEW_CAPITALIZATION_LEVEL = new AttributeName ("newCapitalizationLevel");
	int oldCapitalizationLevel;
	int newCapitalizationLevel;

	public SetCapitalizationLevelEffect () {
		super ();
		setName (NAME);
		setOldCapitalizationLevel (Capitalization.INCREMENTAL_0_MAX);
		setNewCapitalizationLevel (Capitalization.INCREMENTAL_10_MAX);
	}

	public SetCapitalizationLevelEffect (ActorI aActor, int aOldCapitalizationLevel, int aNewCapitalizationLevel) {
		super (NAME, aActor);
		setOldCapitalizationLevel (aOldCapitalizationLevel);
		setNewCapitalizationLevel (aNewCapitalizationLevel);
	}

	public SetCapitalizationLevelEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		int tOldCapitalizationLevel;
		int tNewCapitalizationLevel;

		tOldCapitalizationLevel = aEffectNode.getThisIntAttribute (AN_OLD_CAPITALIZATION_LEVEL);
		tNewCapitalizationLevel = aEffectNode.getThisIntAttribute (AN_NEW_CAPITALIZATION_LEVEL);
		setOldCapitalizationLevel (tOldCapitalizationLevel);
		setNewCapitalizationLevel (tNewCapitalizationLevel);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_OLD_CAPITALIZATION_LEVEL, oldCapitalizationLevel);
		tEffectElement.setAttribute (AN_NEW_CAPITALIZATION_LEVEL, newCapitalizationLevel);

		return tEffectElement;
	}

	public void setOldCapitalizationLevel (int aOldCapitalizationLevel) {
		oldCapitalizationLevel = aOldCapitalizationLevel;
	}

	public void setNewCapitalizationLevel (int aNewCapitalizationLevel) {
		newCapitalizationLevel = aNewCapitalizationLevel;
	}
	
	public int getOldCapitalizationLevel () {
		return oldCapitalizationLevel;
	}

	public int getNewCapitalizationLevel () {
		return newCapitalizationLevel;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tActorFullName;

		tActorFullName = actor.getName ();
		return (REPORT_PREFIX + name + " for " + tActorFullName + " from " + oldCapitalizationLevel + " to " + 
				newCapitalizationLevel + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		ShareCompany tShareCompany;

		tEffectApplied = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tShareCompany.setDestinationCapitalizationLevel (newCapitalizationLevel);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("Actor " + actor.getName () + " is not a Share Company.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		ShareCompany tShareCompany;

		tEffectUndone = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tShareCompany.setDestinationCapitalizationLevel (oldCapitalizationLevel);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("Actor " + actor.getName () + " is not a Share Company.");
		}

		return tEffectUndone;
	}
}
