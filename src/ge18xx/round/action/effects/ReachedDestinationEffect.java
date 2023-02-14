package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ReachedDestinationEffect extends Effect {
	public final static String NAME = "Reached Destination";
	final static AttributeName AN_REACHED_DESTINATION = new AttributeName ("reached");
	final static AttributeName AN_OLD_CAPITALIZATION= new AttributeName ("oldCapitalization");
	final static AttributeName AN_NEW_CAPITALIZATION = new AttributeName ("newCapitalization");
	boolean reached;
	int oldCapitalizationLevel;
	int newCapitalizationLevel;
	

	public ReachedDestinationEffect () {
		super ();
		setName (NAME);
	}

	public ReachedDestinationEffect (String aName) {
		super (aName);
	}

	public ReachedDestinationEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public ReachedDestinationEffect (ActorI aActor, boolean aReached, int aOldCapitalizationLevel, 
			int aNewCapitalizationLevel) {
		super (NAME, aActor);
		setReached (aReached);
		setOldCapitalization (aOldCapitalizationLevel);
		setNewCapitalization (aNewCapitalizationLevel);
	}

	public ReachedDestinationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		boolean tReached;
		int tOldCapitalization;
		int tNewCapitalization;
		
		tReached = aEffectNode.getThisBooleanAttribute (AN_REACHED_DESTINATION);
		tOldCapitalization = aEffectNode.getThisIntAttribute (AN_OLD_CAPITALIZATION);
		tNewCapitalization = aEffectNode.getThisIntAttribute (AN_NEW_CAPITALIZATION);
		setReached (tReached);
		setOldCapitalization (tOldCapitalization);
		setNewCapitalization (tNewCapitalization);
	}
	
	public void setReached (boolean aReached) {
		reached = aReached;
	}
	
	public void setOldCapitalization (int aOldCapitalization) {
		oldCapitalizationLevel = aOldCapitalization;
	}
	
	public void setNewCapitalization (int aNewCapitalization) {
		newCapitalizationLevel = aNewCapitalization;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_REACHED_DESTINATION, reached);
		tEffectElement.setAttribute (AN_OLD_CAPITALIZATION, oldCapitalizationLevel);
		tEffectElement.setAttribute (AN_NEW_CAPITALIZATION, newCapitalizationLevel);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getAbbrev () + " "
				+ " has reached it's Destination. Old Capitalization Level "+ oldCapitalizationLevel +
				" New Capitalization Level " + newCapitalizationLevel);
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		ShareCompany tShareCompany;
		
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tShareCompany.setReachedDestination (reached);
			tShareCompany.setDestinationCapitalizationLevel (newCapitalizationLevel);
			tEffectApplied = true;
		} else {
			tEffectApplied = false;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		ShareCompany tShareCompany;
		
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tShareCompany.setReachedDestination (reached);
			tShareCompany.setDestinationCapitalizationLevel (oldCapitalizationLevel);
			tEffectUndone = true;
		} else {
			tEffectUndone = false;
		}

		return tEffectUndone;
	}
}
